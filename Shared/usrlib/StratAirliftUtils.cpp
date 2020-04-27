#include <iostream>
#include <fstream>
#include <string>
#include <boost/rational.hpp>
#include <boost/algorithm/string.hpp>
#include <vector>
#include <cmath>
#include <math.h>

#include "EIRational.hpp"

//Messages structures
#include "../messages/StratAirLiftSimMessage.hpp"

#include <sys/types.h>
#include <sys/stat.h>
#include <string>
#include <map>

using namespace std;
using namespace boost;

const int DAYS_IN_MIN=1440;				// 1440 - the number of minutes in a day
const int HOURS_IN_MIN=60;				// 60 - the number of minutes in an hour

const double PI = 3.14159265358979323846;	// Value of PI
const double R_EARTH_KM = 6371.0;			// Radius of Earth in KM

// Converts from EIRational time to number of days for output
int ConvertToDays(EIRational tTime)
{
	return rational_cast<int>(tTime._value)/DAYS_IN_MIN;
}

// Converts from EIRational time to number of hours for output
int ConvertToHours(EIRational tTime)
{
	return rational_cast<int>(tTime._value)/HOURS_IN_MIN;
}

// Converts from EIRational time to integer for output
int ConvertToInt(EIRational tTime)
{
	return rational_cast<int>(tTime._value);
}

vector<oPallet> ParsePallets(string sPallets)
{
	vector<oPallet> vReturn;
	
	vector<string> vPallets;
	vector<string> vPallet;
	oPallet tPallet;
	
	split(vPallets,sPallets,is_any_of(";"));
	
	for (vector<string>::iterator iConvert = vPallets.begin(); iConvert != vPallets.end(); ++iConvert)
	{
		split(vPallet,*iConvert,is_any_of(":"));
		tPallet = oPallet();
		tPallet.iPalletID=stoi(vPallet.at(0));
		tPallet.iNextLoc=stoi(vPallet.at(1));
		tPallet.iDest=stoi(vPallet.at(2));
		vReturn.push_back(tPallet);
	}
	
	return vReturn;
}

double toRads(double dDegrees)
{
    return dDegrees/180 * PI;
}

double toDegrees(double dRad)
{
	return dRad*180/PI;
}

double calcDist(double dSourceLat, double dSourceLong, double dDestLat, double dDestLong)
{
	double dDist;
    dDist = sin(toRads(dSourceLat)) * sin(toRads(dDestLat)) + cos(toRads(dSourceLat)) * cos(toRads(dDestLat)) * cos(toRads(dSourceLong - dDestLong));
    dDist = acos(dDist);
    dDist = R_EARTH_KM * dDist;

    return dDist;
}

double calcBearing(double dSourceLat, double dSourceLong, double dDestLat, double dDestLong)
{
	double y = sin(toRads(dDestLong)-toRads(dSourceLong))*cos(toRads(dDestLat));
	double x = cos(toRads(dSourceLat))*sin(toRads(dDestLat)) - sin(toRads(dSourceLat))*cos(toRads(dDestLat))*cos(toRads(dDestLong)-toRads(dSourceLong));

    return toDegrees(atan2(y,x));
}


void calcPoint(double dSourceLat, double dSourceLong, double dBearing, double dDistKM, double* dEndLat, double* dEndLong)
{
	double dInit = toRads(dBearing);
    double dRatio = dDistKM / R_EARTH_KM;
    double dRatioSine = sin(dRatio);
    double dRatioCosine = cos(dRatio);

    double dLat = toRads(dSourceLat);
    double dLong = toRads(dSourceLong);

    double dLatCos = cos(dLat);
    double dLatSin = sin(dLat);

	double dEndLatRad=asin((dLatSin * dRatioCosine) + (dLatCos * dRatioSine * cos(dInit)));
	double dEndLongRad=dLong + atan2(sin(dInit) * dRatioSine * dLatCos,dRatioCosine - dLatSin * sin(dEndLatRad));
    *dEndLat = toDegrees(dEndLatRad);
    *dEndLong = toDegrees(dEndLongRad);
}
