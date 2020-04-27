#ifndef STRAT_AIR_SIM_MESSAGE_HPP
#define STRAT_AIR_SIM_MESSAGE_HPP

#include <assert.h>
#include <iostream>
#include <string>
#include <vector>

using namespace std;
using TIME = EIRational;

// Pallets
struct oPallet
{
	oPallet()
		:iPalletID(-1){}
	oPallet(int tiPalletID, int tiNextLoc, int tiDest)
		:iPalletID(tiPalletID), iNextLoc(tiNextLoc), iDest(tiDest){}
		
	int iPalletID;
	int iNextLoc;
	int iDest;
	
	bool operator< (const oPallet &rhs) const { return iPalletID < rhs.iPalletID; }	
};

ostream& operator<<(ostream& os, const oPallet& msg);
istream& operator>> (istream& is, oPallet& msg);

// Loads
struct oLoad
{
	oLoad()=default;
	oLoad(string tsLoadID, int tiDestination, double tdSourceLat, double tdSourceLong, double tdDestLat, double tdDestLong, int tiAircraftID, vector<oPallet> tvPallets)
		:sLoadID(tsLoadID), iDestination(tiDestination), dSourceLat(tdSourceLat), dSourceLong(tdSourceLong), dDestLat(tdDestLat), dDestLong(tdDestLong), iAircraftID(tiAircraftID), vPallets(tvPallets){}
		
	string sLoadID;
	int iDestination;
	double dSourceLat;
	double dSourceLong;
	double dDestLat;
	double dDestLong;
	int iAircraftID;
	vector<oPallet> vPallets;
};

ostream& operator<<(ostream& os, const oLoad& msg);
istream& operator>> (istream& is, oLoad& msg);

// Aircraft Status
struct oAircraftStatus
{
	oAircraftStatus()
		:iAircraftID(-1){}
	oAircraftStatus(int tiAircraftID, int tiLocation, int tiCapacity, int tiType, int tiHome)
		:iAircraftID(tiAircraftID), iLocation(tiLocation), iCapacity(tiCapacity), iType(tiType), iHome(tiHome){}
		
	int iAircraftID;
	int iLocation;
	int iCapacity;
	int iType;
	int iHome;
	TIME iWaitingTime;
};

ostream& operator<<(ostream& os, const oAircraftStatus& msg);
istream& operator>> (istream& is, oAircraftStatus& msg);

// Aircraft Status
struct oLocInfo
{
	oLocInfo()
		:iLocID(-1){}
	oLocInfo(int tiLocID, int tiDestID, int tiNextDest, double tdLat, double tdLong, int tiACType)
		:iLocID(tiLocID), iDestID(tiDestID), iNextDest(tiNextDest) ,dLat(tdLat), dLong(tdLong), iACType(tiACType){}
		
	int iLocID;
	int iDestID;
	int iNextDest;
	double dLat;
	double dLong;
	int iACType;
};

ostream& operator<<(ostream& os, const oLocInfo& msg);
istream& operator>> (istream& is, oLocInfo& msg);

#endif // STRAT_AIR_SIM_MESSAGE_HPP