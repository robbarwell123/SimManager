#include <math.h> 
#include <assert.h>
#include <iostream>
#include <fstream>
#include <string>

#include "EIRational.hpp"

#include "StratAirLiftSimMessage.hpp"
#include "../usrlib/StratAirliftUtils.hpp"

// oPallet input and output streams
ostream& operator<<(ostream& os, const oPallet& msg)
{
	os << "Pallet: " << msg.iPalletID << " Next Location: " << msg.iNextLoc << " Destination: " << msg.iDest;

	return os;
}

istream& operator>> (istream& is, oPallet& msg)
{
	string sRouting;
	
	is >> msg.iPalletID;
	is >> msg.iNextLoc;
	is >> msg.iDest;
	
	return is;
}

// oLoad input and output streams
ostream& operator<<(ostream& os, const oLoad& msg)
{
	os << "Load: " << msg.sLoadID << " Destination: " << msg.iDestination;
	
	return os;
}

istream& operator>> (istream& is, oLoad& msg)
{
	string sPallets;
	
	is >> msg.sLoadID;
	is >> msg.iDestination;
	is >> msg.dSourceLat;
	is >> msg.dSourceLong;
	is >> msg.dDestLat;
	is >> msg.dDestLong;
	is >> msg.iAircraftID;
	is >> sPallets;

	msg.vPallets=ParsePallets(sPallets);
	
	return is;
}

// oAircraftStatus output stream
ostream& operator<<(ostream& os, const oAircraftStatus& msg)
{
	os << "Aircraft ID: " << msg.iAircraftID << " at Location ID: " << msg.iLocation;
	
	return os;
}

istream& operator>> (istream& is, oAircraftStatus& msg)
{
	is >> msg.iAircraftID;
	is >> msg.iLocation;
	is >> msg.iCapacity;
	is >> msg.iType;
	is >> msg.iHome;
	
	return is;
}

// oLocInfo output stream
ostream& operator<<(ostream& os, const oLocInfo& msg)
{
	os << "Location: " << msg.iLocID << " Going to: " << msg.dLat << "," << msg.dLong << " via: " << msg.iNextDest;
	
	return os;
}

istream& operator>> (istream& is, oLocInfo& msg)
{
	is >> msg.iLocID;
	is >> msg.iDestID;
	is >> msg.iNextDest;
	is >> msg.dLat;
	is >> msg.dLong;
	is >> msg.iACType;
	
	return is;
}
