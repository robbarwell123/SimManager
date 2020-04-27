package ca.rooper.GenExperiment.controller;

import java.util.Random;

public class GenLocations
{
	public static void gen_locations() throws GenException
	{
		Random rand_gen=new Random();
		int num_to_gen;
		int curr_opt;
		
		for(int curr_continent=0;curr_continent<7;curr_continent++)
		{
			num_to_gen=(int)(DataInfo.config_dist[curr_continent]*DataInfo.config_num_locations);
			
			if(num_to_gen>DataInfo.airport_list.get(curr_continent).size())
			{
				throw new GenException("GEN ERROR: Not enough locations: "+DataInfo.CONT_NAMES[curr_continent]+", "+num_to_gen);
			}
			
			for(int gen_loc=0;gen_loc<num_to_gen;gen_loc++)
			{
				do
				{
					curr_opt=rand_gen.nextInt(DataInfo.airport_list.get(curr_continent).size());					
				}while(DataInfo.scenario_list.containsKey(DataInfo.airport_list.get(curr_continent).get(curr_opt).getId()));
				DataInfo.scenario_list.put(DataInfo.airport_list.get(curr_continent).get(curr_opt).getId(), DataInfo.airport_list.get(curr_continent).get(curr_opt));
			}
		}
	}
}
