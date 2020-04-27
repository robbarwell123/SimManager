package ca.rooper.GenExperiment.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.rooper.GenExperiment.model.Airport;
import ca.rooper.GenExperiment.model.Plane;

public class ParseConfig
{				
	private static final Logger logger = LoggerFactory.getLogger(ParseConfig.class);
	
	public static void parse_config() throws GenException
	{
		check_config();
		parse_main_bases();
		parse_airplanes();
	}

	private static void parse_airplanes() throws GenException
	{
		String[] curr_pair;
		ArrayList<String> airport_list=new ArrayList<String>(DataInfo.main_base_list.keySet());
		int airport_index;
		Plane new_plane;
		int curr_plane=1;
		int make_plane;
		
		for(String curr_base: DataInfo.config_airplanes.split(";"))
		{
			curr_pair=curr_base.split(":");
			airport_index=airport_list.indexOf(curr_pair[0]);
			if(airport_index!=-1)
			{
				make_plane=0;
				for(make_plane=0;make_plane<Integer.parseInt(curr_pair[2]);make_plane++)
				{
					new_plane=new Plane(curr_plane,Integer.parseInt(curr_pair[1]),airport_index);
					DataInfo.airplanes.add(new_plane);
					curr_plane++;					
				}
			}else
			{				
				throw new GenException("CONFIG ERROR: invalid airplane pairs, "+curr_base);
			}
		}		
	}
	
	private static void parse_main_bases() throws GenException
	{
		String[] curr_pair;
		Airport source;
		Airport destination;
		List<String> airport_keys;
		
		for(String curr_base: DataInfo.config_main_bases.split(";"))
		{
			curr_pair=curr_base.split(":");
			source=SupportFunctions.find_airport(curr_pair[0]);
			destination=SupportFunctions.find_airport(curr_pair[1]);
			if(source!=null && destination!=null)
			{
				DataInfo.main_base_list.put(curr_pair[0], source);
				DataInfo.main_base_list.put(curr_pair[1], destination);
				DataInfo.distance_matrix=SupportFunctions.resize_matrix(DataInfo.distance_matrix,DataInfo.main_base_list.size());
				airport_keys=new ArrayList<String>(DataInfo.main_base_list.keySet());
				DataInfo.distance_matrix[airport_keys.indexOf(curr_pair[0])][airport_keys.indexOf(curr_pair[1])]=SupportFunctions.calc_distance(source.getLat(), source.getLon(), destination.getLat(), destination.getLon());
				DataInfo.distance_matrix[airport_keys.indexOf(curr_pair[1])][airport_keys.indexOf(curr_pair[0])]=SupportFunctions.calc_distance(destination.getLat(), destination.getLon(), source.getLat(), source.getLon());;
			}else
			{
				throw new GenException("CONFIG ERROR: invalid base pairs, "+curr_base);
			}
		}
	}
	
	private static void check_config() throws GenException
	{
		double dist_sum=0.0;
		if(DataInfo.config_dist.length!=7)
		{
			throw new GenException("CONFIG ERROR: dist needs to have 7 elements, "+DataInfo.config_dist.length);
		}		
		for(double curr_dist:DataInfo.config_dist)
		{
			dist_sum+=curr_dist;
		}
		if(dist_sum<0.92 || dist_sum>1.0)
		{
			throw new GenException("CONFIG ERROR: dist needs add up to between 0.93 and 1.0, "+dist_sum);
		}
		
		if(DataInfo.config_num_scenarios<1 || DataInfo.config_num_scenarios>100)
		{
			throw new GenException("CONFIG ERROR: num_scenarios outside range, "+DataInfo.config_num_scenarios);
		}
		
		if(DataInfo.config_num_locations<1 || DataInfo.config_num_locations>100)
		{
			throw new GenException("CONFIG ERROR: num_locations outside range, "+DataInfo.config_num_locations);
		}
		
	}
}
