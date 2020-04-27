package ca.rooper.GenExperiment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;

import ca.rooper.GenExperiment.model.Airport;
import ca.rooper.GenExperiment.model.PathLinks;
import ca.rooper.GenExperiment.model.Plane;

public class DataInfo
{
	public static ArrayList<ArrayList<Airport>> airport_list;
	public static LinkedHashMap <String,Airport> main_base_list;
	public static LinkedHashMap <String, Airport> scenario_list;
	public static ArrayList<PathLinks> path_list;
	public static ArrayList<Plane> airplanes;
	public static int[] pallets;
			
	public static int[][] distance_matrix;

	public static final int FIELD_ID=1;
	public static final int FIELD_TYPE=2;
	public static final int FIELD_NAME=3;
	public static final int FIELD_LAT=4;
	public static final int FIELD_LON=5;
	public static final int FIELD_CONT=7;

	public static final HashMap<String, Integer> CONT_VALUES = new HashMap<String,Integer>();
	public static final String[] CONT_NAMES = {"Africa","Antarctica","Asia","Europe","North America","Australia","South America"};
	
	static {
		CONT_VALUES.put("AF", 0);
		CONT_VALUES.put("AN", 1);
		CONT_VALUES.put("AS", 2);
		CONT_VALUES.put("EU", 3);
		CONT_VALUES.put("NA", 4);
		CONT_VALUES.put("OC", 5);
		CONT_VALUES.put("SA", 6);
	}
	
	public static String config_airport_file;
	public static int config_num_scenarios;
	public static int config_num_locations;
	public static double[] config_dist;
	public static String config_main_bases;
	public static String config_airplanes;
	
}
