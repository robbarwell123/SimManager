package ca.rooper.GenExperiment.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.rooper.GenExperiment.model.Airport;
import ca.rooper.GenExperiment.model.PathLinks;

public class SupportFunctions
{
	private static final int EARTH_RADIUS=6371;
	private static final Logger logger = LoggerFactory.getLogger(SupportFunctions.class);

	public static int calc_distance(double lat1, double lon1, double lat2, double lon2 )
	{		
	    double delta_lat = deg_to_rad(lat2 - lat1);
	    double delta_lon = deg_to_rad(lon2 - lon1);

	    lat1 =  deg_to_rad(lat1);
	    lat2 =  deg_to_rad(lat2);

	    double radius_1 = Math.pow ( Math.sin(delta_lat/2), 2 ) + Math.cos(lat1) * Math.cos(lat2) * Math.pow ( Math.sin(delta_lon/2), 2 );

	    double radius_2 = 2 * Math.atan2( Math.sqrt(radius_1), Math.sqrt( 1 - radius_1 ));
	    double distance = EARTH_RADIUS * radius_2;

	    return (int)distance;
	}

	private static double deg_to_rad(double deg)
	{
	    return (deg * Math.PI / 180);
	}
		
	public static Airport find_airport(String icao_id)
	{
		int curr_airport=0;
		
		Airport rtn_val=null;
		for(int find_cont=0;find_cont<7;find_cont++)
		{
			for(curr_airport=0;curr_airport<DataInfo.airport_list.get(find_cont).size();curr_airport++)
			{
				if(DataInfo.airport_list.get(find_cont).get(curr_airport).getId().equals(icao_id))
				{
					return DataInfo.airport_list.get(find_cont).get(curr_airport);
				}
			}
		}
		return rtn_val;
	}	
	
	public static void print_paths()
	{
		String to_output="\n";
		for(PathLinks curr_path : DataInfo.path_list)
		{
			to_output+=curr_path.toString()+"\n";
		}
		logger.info(to_output);
	}
	
	public static void print_matrix(int[][] curr_matrix)
	{
		String to_output="\n\t";
		for(int header=0;header<curr_matrix.length;header++)
		{
			to_output+=header+"\t";
		}
		to_output+="\n";
		
		for(int row=0;row<curr_matrix.length;row++)
		{
			to_output+=row+"\t";
			for(int col=0;col<curr_matrix[row].length;col++)
			{
				to_output+=curr_matrix[row][col]+"\t";
			}
			to_output+="\n";
		}
		logger.info(to_output);
	}
	
	public static int[][] resize_matrix(int[][] curr_matrix, int new_size)
	{
		int[][] new_matrix=new int[new_size][new_size];
		int copy_size=curr_matrix.length<new_size ? curr_matrix.length : new_size;
		
		for(int row=0;row<copy_size;row++)
		{
			for(int col=0;col<copy_size;col++)
			{
				new_matrix[row][col]=curr_matrix[row][col];
			}
		}
		return new_matrix;
		
	}
}
