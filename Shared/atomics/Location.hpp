#ifndef LOCATION_HPP
#define LOCATION_HPP

#include <cadmium/modeling/ports.hpp>
#include <cadmium/modeling/message_bag.hpp>

#include <limits>
#include <assert.h>
#include <string>
#include <random>

#include <stdlib.h>

#include <map>
#include <iterator>

#include "../messages/StratAirLiftSimMessage.hpp"
#include "../usrlib/StratAirliftUtils.hpp"

using namespace cadmium;
using namespace std;

const int I_DELIVERY_TIME=10;
const int I_MAX_WAIT_TIME=1*DAYS_IN_MIN;

const int DEST_DIST=0;
const int DEST_ACTYPE=1;

//Port definition
struct Locations_defs{
	struct inPallets : public in_port<oPallet> { };
	struct inACStatus : public in_port<oAircraftStatus> { };
	struct inLoads : public in_port<oLoad> { };
	struct inLocInfo : public in_port<oLocInfo> { };
	struct outLoads : public out_port<oLoad> { };
	struct outPallets : public out_port<oPallet> { };
};

template<typename TIME> class Location{
	
	enum ePhase {WAITING, PROCESSING, DELIVER, SENDLOAD};
	
    public:
		Location() = default;
	
        // default constructor
		Location(int tiLocation, string tsLocation,double dLat, double dLong) noexcept{
			state.currPhase=WAITING;
			state.sigma=std::numeric_limits<TIME>::infinity();
			state.iLocation=tiLocation;
			state.sLocation=tsLocation;
			state.iCurrLoad=0;
			state.iCurrTime=TIME(0);
			state.iNextAdd=TIME(0);
			state.myLat=dLat;
			state.myLong=dLong;
			cout << "Created Location: " << state.sLocation << " [" << state.iLocation << "]\n";
        }
        
        // state definition
        struct state_type{
			ePhase currPhase;			
            TIME sigma;
			string sLocation;
			int iLocation;
			map<int,oLocInfo> mapConnections;
			vector<oPallet> myWaitingPallets;
			vector<oAircraftStatus> myWaitingAircraft;
			vector<oLoad> myWaitingLoads;
			vector<oPallet> myDestPallets;
			TIME iCurrTime;
			TIME iNextAdd;
			int iCurrLoad;
			double myLat;
			double myLong;
			vector<oLoad> mySendLoads;
        }; 
        state_type state;
		
        // ports definition
        using input_ports=std::tuple<typename Locations_defs::inPallets, typename Locations_defs::inACStatus, typename Locations_defs::inLoads, typename Locations_defs::inLocInfo>;
        using output_ports=std::tuple<typename Locations_defs::outLoads, typename Locations_defs::outPallets>;

        // internal transition
        void internal_transition()
		{
			state.iCurrTime+=state.iNextAdd;
			int iAddTime=0;
			int iNextExpireAircraftTime=0;
			state.mySendLoads.clear();
			
			switch(state.currPhase)
			{
				case PROCESSING:
					for (vector<oLoad>::iterator oProcessLoad = state.myWaitingLoads.begin(); oProcessLoad != state.myWaitingLoads.end(); ++oProcessLoad)
					{
						
						for (vector<oPallet>::iterator oProcessPallet = oProcessLoad->vPallets.begin(); oProcessPallet != oProcessLoad->vPallets.end(); ++oProcessPallet)
						{
							if(oProcessPallet->iDest==state.iLocation)
							{
								state.myDestPallets.push_back(*oProcessPallet);
							}else
							{
								oProcessPallet->iNextLoc=state.mapConnections.find(oProcessPallet->iDest) == state.mapConnections.end() ? -1 : state.mapConnections[oProcessPallet->iDest].iNextDest;
								if(oProcessPallet->iNextLoc!=-1)
								{
									state.myWaitingPallets.push_back(*oProcessPallet);
								}else
								{
									state.myDestPallets.push_back(*oProcessPallet);									
								}
							}
						}
					}
					state.myWaitingLoads.clear();
					sort(state.myWaitingPallets.begin(),state.myWaitingPallets.end());

					ProcessPallets();
					iNextExpireAircraftTime=CheckAircraft();
					iNextExpireAircraftTime=iNextExpireAircraftTime-ConvertToInt(state.iCurrTime);

					if(state.mySendLoads.size()>0)
					{
						state.currPhase=SENDLOAD;
						state.sigma=TIME(10);
					}else if(state.myDestPallets.size()>0)
					{
						state.currPhase=DELIVER;
						state.iNextAdd=TIME(I_DELIVERY_TIME);
						state.sigma=TIME(I_DELIVERY_TIME);
						iAddTime=I_DELIVERY_TIME;
					}else
					{
						if(GuestAircraft())
						{
							state.currPhase=PROCESSING;
							state.iNextAdd=TIME(iNextExpireAircraftTime);
							state.sigma=TIME(iNextExpireAircraftTime);
						}else
						{
							state.currPhase=WAITING;
							state.sigma=std::numeric_limits<TIME>::infinity();
						}
					}
					break;
				case SENDLOAD:
					state.currPhase=PROCESSING;
					state.sigma=TIME(10);
					break;
				case DELIVER:
					state.myDestPallets.clear();
					state.currPhase=PROCESSING;
					state.sigma=TIME(10);
					break;
			}
        }

		bool GuestAircraft()
		{
			bool toRtn=false;
			for(vector<oAircraftStatus>::iterator oAircraft=state.myWaitingAircraft.begin(); oAircraft!=state.myWaitingAircraft.end();++oAircraft)
			{
				if(oAircraft->iHome!=state.iLocation)
				{
					toRtn=true;
				}
			}
			return toRtn;
		}

		int CheckAircraft()
		{
			int iMinTime=ConvertToInt(state.iCurrTime)+I_MAX_WAIT_TIME;
			for(vector<oAircraftStatus>::iterator oAircraft=state.myWaitingAircraft.begin(); oAircraft!=state.myWaitingAircraft.end();)
			{
				if(oAircraft->iHome!=state.iLocation)
				{
					iMinTime=min(ConvertToInt(oAircraft->iWaitingTime),iMinTime);
				}
				if(oAircraft->iWaitingTime <= state.iCurrTime && oAircraft->iHome!=state.iLocation)
				{
					oLoad myLoad;
					state.iCurrLoad++;
					myLoad.sLoadID=to_string(state.iLocation) + "_" + to_string(state.iCurrLoad);
					myLoad.iDestination=state.mapConnections.find(oAircraft->iHome) == state.mapConnections.end() ? -1 : state.mapConnections[oAircraft->iHome].iNextDest;
					myLoad.dSourceLat=state.myLat;
					myLoad.dSourceLong=state.myLong;
					myLoad.dDestLat=state.mapConnections.find(oAircraft->iHome) == state.mapConnections.end() ? -1 : state.mapConnections[oAircraft->iHome].dLat;
					myLoad.dDestLong=state.mapConnections.find(oAircraft->iHome) == state.mapConnections.end() ? -1 : state.mapConnections[oAircraft->iHome].dLong;
					myLoad.iAircraftID=oAircraft->iAircraftID;
					if(myLoad.iDestination!=-1)
					{
						state.myWaitingAircraft.erase(oAircraft);
						state.mySendLoads.push_back(myLoad);
					}					
				}else
				{
					++oAircraft;
				}
			}
			return iMinTime;
		}

		void ProcessPallets()
		{
			const int MIN_POS=0;
			const int NUM_PALLETS=1;
			const int AC_TYPE=2; 

			unordered_map<int,array<int,3>> myStatus;
			
			for (vector<oPallet>::iterator oPalletStatus = state.myWaitingPallets.begin(); oPalletStatus != state.myWaitingPallets.end(); ++oPalletStatus)
			{
				if(myStatus.find(oPalletStatus->iNextLoc) == myStatus.end())
				{
					myStatus[oPalletStatus->iNextLoc][MIN_POS]=distance(state.myWaitingPallets.begin(), oPalletStatus);
					myStatus[oPalletStatus->iNextLoc][AC_TYPE]=state.mapConnections[oPalletStatus->iNextLoc].iACType;
				}
				myStatus[oPalletStatus->iNextLoc][NUM_PALLETS]++;
			}
			
			vector<int> vFindMinPos;
			for (auto const& [iNextDest, oStatus] : myStatus)
			{
				vFindMinPos.push_back(oStatus[MIN_POS]);
			}
			sort(vFindMinPos.begin(),vFindMinPos.end());

			unordered_map<int,array<int,3>>::iterator itFindPos;
			bool bFoundPos;
			
			vector<oAircraftStatus>::iterator itFindAC;
			bool bFoundAC;
			
			vector<oPallet>::iterator itLoadPallet;
			
			int iCurrLoadSize;
			
			for (auto iMinPos : vFindMinPos)
			{
				itFindPos=myStatus.begin();
				bFoundPos=false;
				while(!bFoundPos && itFindPos!=myStatus.end())
				{
					if(iMinPos == itFindPos->second[MIN_POS])
					{
						itFindAC=state.myWaitingAircraft.begin();
						bFoundAC=false;
						while(!bFoundAC && itFindAC!=state.myWaitingAircraft.end())
						{
							if(itFindPos->second[NUM_PALLETS] >= itFindAC->iCapacity/2 && itFindPos->second[AC_TYPE]==itFindAC->iType)
							{
								oLoad myLoad;
								state.iCurrLoad++;
								myLoad.sLoadID=to_string(state.iLocation) + "_" + to_string(state.iCurrLoad);
								myLoad.iDestination=itFindPos->first;
								myLoad.dSourceLat=state.myLat;
								myLoad.dSourceLong=state.myLong;
								myLoad.dDestLat=state.mapConnections[myLoad.iDestination].dLat;
								myLoad.dDestLong=state.mapConnections[myLoad.iDestination].dLong;
								myLoad.iAircraftID=itFindAC->iAircraftID;
								
								itLoadPallet=state.myWaitingPallets.begin();
								iCurrLoadSize=0;
								while(itLoadPallet!=state.myWaitingPallets.end() && iCurrLoadSize<itFindAC->iCapacity)
								{
									if(itLoadPallet->iNextLoc==itFindPos->first)
									{
										myLoad.vPallets.push_back(*itLoadPallet);
										state.myWaitingPallets.erase(itLoadPallet);
										iCurrLoadSize++;
									}else
									{
										itLoadPallet++;
									}
								}
								state.myWaitingAircraft.erase(itFindAC);
								state.mySendLoads.push_back(myLoad);
								bFoundAC=true;
							}else
							{
								itFindAC++;
							}
						}
						bFoundPos=true;
					}
					itFindPos++;
				}
			}
		}

        // external transition
        void external_transition(TIME e, typename make_message_bags<input_ports>::type mbs)
		{ 
			state.iCurrTime+=e;
			state.iNextAdd=TIME(0);
			
            for(auto &tPallet : get_messages<typename Locations_defs::inPallets>(mbs))
			{
				if(tPallet.iNextLoc == state.iLocation)
				{
					tPallet.iNextLoc=state.mapConnections.find(tPallet.iDest) == state.mapConnections.end() ? -1 : state.mapConnections[tPallet.iDest].iNextDest;
					if(tPallet.iNextLoc!=-1)
					{
						state.myWaitingPallets.push_back(tPallet);
					}else
					{
						state.myDestPallets.push_back(tPallet);									
					}
				}
            }

            for(auto &tACStatus : get_messages<typename Locations_defs::inACStatus>(mbs))
			{
				if(tACStatus.iLocation == state.iLocation)
				{
					tACStatus.iWaitingTime=state.iCurrTime+TIME(I_MAX_WAIT_TIME);
					state.myWaitingAircraft.push_back(tACStatus);
				}
            }

            for(const auto &tLoad : get_messages<typename Locations_defs::inLoads>(mbs))
			{
				if(tLoad.iDestination == state.iLocation)
				{
					state.myWaitingLoads.push_back(tLoad);
				}
            }
			
            for(const auto &tLocInfo : get_messages<typename Locations_defs::inLocInfo>(mbs))
			{
				if(tLocInfo.iLocID == state.iLocation)
				{
					state.mapConnections[tLocInfo.iDestID]=tLocInfo;
				}
            }
			
			state.currPhase=state.currPhase==WAITING ? PROCESSING : state.currPhase;
			state.sigma=TIME(0);
        }
		
        // confluence transition
        void confluence_transition(TIME e, typename make_message_bags<input_ports>::type mbs) {
            internal_transition();
            external_transition(TIME(), std::move(mbs));
        }

        // output function
        typename make_message_bags<output_ports>::type output() const
		{
            typename make_message_bags<output_ports>::type bags;
			
			switch(state.currPhase)
			{
				case DELIVER:
					for (vector<oPallet>::const_iterator oProcessPallet = state.myDestPallets.begin(); oProcessPallet != state.myDestPallets.end(); ++oProcessPallet)
					{
						get_messages<typename Locations_defs::outPallets>(bags).push_back(*oProcessPallet);
					}
					break;
				case SENDLOAD:
					for (vector<oLoad>::const_iterator oProcessLoad = state.mySendLoads.begin(); oProcessLoad != state.mySendLoads.end(); ++oProcessLoad)
					{
						get_messages<typename Locations_defs::outLoads>(bags).push_back(*oProcessLoad);
					}
					break;
			}
			
			return bags;			
        }

        // time_advance function
        TIME time_advance() const {  
             return state.sigma;
        }

        friend std::ostringstream& operator<<(std::ostringstream& os, const typename Location<TIME>::state_type& currState) 
		{
			string myState;
			switch(currState.currPhase)
			{
				case WAITING:
					myState="Waiting";
					break;
				case PROCESSING:			
					myState="Processing";
					break;
				case DELIVER:
					myState="Delivering";
					break;
				case SENDLOAD:
					myState="Sending Load";
					break;
			}
			
			os << "\"data\":{\"class\":\"Location\",\"text\":\"" << currState.sLocation;
			os << "\",\"state\":\"" << myState;
			os << "\",\"loc_id\":\"" << currState.iLocation;
			os << "\",\"location\":{\"lat\":" << currState.myLat;
			os << ",\"long\":" << currState.myLong;
			os << "},\"pallets\":" << currState.myWaitingPallets.size();
			os << ",\"debug\":\"" << currState.myWaitingAircraft.size() << ":" << currState.myWaitingPallets.size() << ":" << currState.mySendLoads.size();
			os << "\",\"message\":\"" << "Location " << currState.sLocation << " has " << currState.myWaitingAircraft.size() << " aircraft waiting and " << currState.myWaitingPallets.size() << " pallets waiting.";
			os << "\"}";
						
			return os;
        }
};     
#endif // LOCATION_HPP