#ifndef STRATAIRLIFTUTILS_HPP
#define STRATAIRLIFTUTILS_HPP

#include <iostream>
#include <fstream>
#include <string>
#include <boost/rational.hpp>
#include <vector>

#include <sys/types.h>
#include <sys/stat.h>
#include <string>
#include <unordered_map>
#include <iterator>
#include <map>

#include "../messages/StratAirLiftSimMessage.hpp"

using namespace std;

const int DAYS_IN_MIN=1440;				// 1440 - the number of minutes in a day
const int HOURS_IN_MIN=60;				// 60 - the number of minutes in an hour

const int METERS_PER_KTS=31;			// meters per knot

const double PI = 3.14159265358979323846;	// Value of PI
const double R_EARTH_KM = 6371.0;			// Radius of Earth in KM

int ConvertToDays(EIRational tTime);	// Converts from EIRational time to number of days for output
int ConvertToHours(EIRational tTime);	// Converts from EIRational time to number of hours for output
int ConvertToInt(EIRational tTime);		// Converts from EIRational time to integer for output

vector<oPallet> ParsePallets(string sPallets);

double toRads(double dDegrees);
double toDegrees(double dRad);
double calcDist(double dSourceLat, double dSourceLong, double dDestLat, double dDestLong);
double calcBearing(double dSourceLat, double dSourceLong, double dDestLat, double dDestLong);
void calcPoint(double dSourceLat, double dSourceLong, double dBearing, double dDistKM, double* dEndLat, double* dEndLong);

#endif // STRATAIRLIFTUTILS_HPP