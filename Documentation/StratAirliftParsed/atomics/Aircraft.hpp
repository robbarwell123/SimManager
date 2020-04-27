#ifndef AIRCRAFT_HPP
#define AIRCRAFT_HPP

#include <cadmium/modeling/ports.hpp>
#include <cadmium/modeling/message_bag.hpp>

#include <limits>
#include <assert.h>
#include <string>
#include <random>

#include <stdlib.h>

#include "../messages/StratAirLiftSimMessage.hpp"
#include "../usrlib/StratAirliftUtils.hpp"

using namespace cadmium;
using namespace std;

const int I_UPDATE_PERIOD=30;			// the update period for the plane in minutes
const int I_AVG_UNLOAD_TIME=30;			// the average unload time

//Port definition
struct Aircraft_defs{
	struct inLoads : public in_port<oLoad> { };
	struct outLoads : public out_port<oLoad> { };
	struct outACStatus : public out_port<oAircraftStatus> { };
};

template<typename TIME> class Aircraft{
	
	enum ePhase {INIT, WAITING, FLYING, UNLOADING};
	
    public:
		Aircraft() = default;
		
        // default constructor
        Aircraft(int tiAircraftID, int tiType, int tiHomeLocation, int tiSpeed, int tiMaxLoad) noexcept{
			state.currPhase=INIT;
			state.sigma=TIME(0);
			state.iAircraftID=tiAircraftID;
			state.iType=tiType;
			state.iAvgSpeed=(tiSpeed*METERS_PER_KTS)/1000;
			state.iHomeLocation=tiHomeLocation;
			state.iMaxLoad=tiMaxLoad;
        }
        
        // state definition
        struct state_type{
			ePhase currPhase;			
            TIME sigma;
			int iAircraftID;
			int iType;
			int iAvgSpeed;
			int iHomeLocation;
			int iMaxLoad;
			double dDistanceRemaining;
			double dTotalDist;
			double dCurrHeading;
			double dCurrLat;
			double dCurrLong;
			oLoad myCurrLoad;
        }; 
        state_type state;
		
        // ports definition
        using input_ports=std::tuple<typename Aircraft_defs::inLoads>;
        using output_ports=std::tuple<typename Aircraft_defs::outLoads, typename Aircraft_defs::outACStatus>;

        // internal transition
        void internal_transition()
		{
			switch(state.currPhase)
			{
				case INIT:
					state.currPhase=WAITING;
					state.sigma=std::numeric_limits<TIME>::infinity();
					break;
				case FLYING:
					if(state.dDistanceRemaining <= 0)
					{
						state.currPhase=UNLOADING;
						state.sigma=TIME(I_AVG_UNLOAD_TIME);
					}else if(state.dDistanceRemaining <= (state.iAvgSpeed*I_UPDATE_PERIOD))
					{
						state.sigma=TIME((state.dDistanceRemaining / state.iAvgSpeed)+1);
						state.dDistanceRemaining=0;
					}else
					{
						state.dDistanceRemaining-=state.iAvgSpeed*I_UPDATE_PERIOD;
						state.sigma=TIME(I_UPDATE_PERIOD);
					}
					calcPoint(state.myCurrLoad.dSourceLat,state.myCurrLoad.dSourceLong,state.dCurrHeading,(state.dTotalDist-state.dDistanceRemaining),&state.dCurrLat, &state.dCurrLong);
					break;
				case UNLOADING:
					state.currPhase=WAITING;
					state.sigma=std::numeric_limits<TIME>::infinity();
					break;
			}

        }

        // external transition
        void external_transition(TIME e, typename make_message_bags<input_ports>::type mbs)
		{ 
            for(const auto &tLoad : get_messages<typename Aircraft_defs::inLoads>(mbs))
			{
				if(tLoad.iAircraftID == state.iAircraftID)
				{
					state.myCurrLoad=tLoad;
					state.dTotalDist=calcDist(tLoad.dSourceLat,tLoad.dSourceLong,tLoad.dDestLat,tLoad.dDestLong);
					state.dCurrHeading=calcBearing(tLoad.dSourceLat,tLoad.dSourceLong,tLoad.dDestLat,tLoad.dDestLong);
					state.dDistanceRemaining=state.dTotalDist;
					state.dCurrLat=tLoad.dSourceLat;
					state.dCurrLong=tLoad.dSourceLong;
				}
            }
			
			if(state.currPhase == WAITING && state.dDistanceRemaining>0)
			{
				state.currPhase=FLYING;
				state.sigma=TIME(0);
			}else if(state.currPhase == FLYING)
			{
				state.sigma=TIME(I_UPDATE_PERIOD)-e;
			}else if(state.currPhase == WAITING)
			{
				state.sigma=std::numeric_limits<TIME>::infinity();
			}
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
			oAircraftStatus tStatus;
			
			switch(state.currPhase)
			{
				case UNLOADING:
					get_messages<typename Aircraft_defs::outLoads>(bags).push_back(state.myCurrLoad);
					tStatus=oAircraftStatus(state.iAircraftID,state.myCurrLoad.iDestination,state.iMaxLoad,state.iType,state.iHomeLocation);
					get_messages<typename Aircraft_defs::outACStatus>(bags).push_back(tStatus);
					break;
				case INIT:
					tStatus=oAircraftStatus(state.iAircraftID,state.iHomeLocation,state.iMaxLoad,state.iType,state.iHomeLocation);
					get_messages<typename Aircraft_defs::outACStatus>(bags).push_back(tStatus);
					break;
			}
			
			return bags;			
        }

        // time_advance function
        TIME time_advance() const {  
             return state.sigma;
        }

        friend std::ostringstream& operator<<(std::ostringstream& os, const typename Aircraft<TIME>::state_type& currState) 
		{
			string myState;
			switch(currState.currPhase)
			{
				case INIT:
					myState="Init";
					break;
				case FLYING:			
					myState="Flying";
					break;
				case UNLOADING:
					myState="Unloading";
					break;
				case WAITING:
					myState="Waiting";
					break;
			}
			
			os << "\"data\":{\"class\":\"Aircraft\",\"text\":\"Aircraft" << currState.iAircraftID;
			os << "\",\"state\":\"" << myState;
			os << "\",\"location\":{\"lat\":" << currState.dCurrLat;
			os << ",\"long\":" << currState.dCurrLong;
			os << "},\"debug\":\"" << currState.dDistanceRemaining << " of " << currState.dTotalDist;
			os << "\",\"message\":\"" << "Aircraft" << currState.iAircraftID << " currently " << myState << " distance remaining " << currState.dDistanceRemaining << " of " << currState.dTotalDist;
			os << "\"}";
			
			return os;
        }
};     
#endif // AIRCRAFT_HPP