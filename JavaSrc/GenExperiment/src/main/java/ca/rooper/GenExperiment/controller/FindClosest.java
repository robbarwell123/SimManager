package ca.rooper.GenExperiment.controller;

import java.util.ArrayList;

import ca.rooper.GenExperiment.model.Airport;

public class FindClosest
{
	public static void find_closest() throws GenException
	{
		int closest_distance;
		int closest_index;
		int main_base;
		int distance;

		ArrayList<Airport> scenario_values=new ArrayList<Airport>(DataInfo.scenario_list.values());

		for(int gen_loc=DataInfo.main_base_list.size();gen_loc<scenario_values.size();gen_loc++)
		{
			closest_distance=Integer.MAX_VALUE;
			closest_index=-1;
			for(main_base=0;main_base<DataInfo.main_base_list.size();main_base++)
			{
				distance=SupportFunctions.calc_distance(scenario_values.get(gen_loc).getLat(), scenario_values.get(gen_loc).getLon(), scenario_values.get(main_base).getLat(), scenario_values.get(main_base).getLon());
				if(distance<closest_distance)
				{
					closest_distance=distance;
					closest_index=main_base;
				}
			}
			if(closest_index!=-1)
			{
				distance=SupportFunctions.calc_distance(scenario_values.get(gen_loc).getLat(), scenario_values.get(gen_loc).getLon(), scenario_values.get(closest_index).getLat(), scenario_values.get(closest_index).getLon());
				DataInfo.distance_matrix[gen_loc][closest_index]=distance;
				DataInfo.distance_matrix[closest_index][gen_loc]=distance;
			}else
			{
				throw new GenException("FIND_CLOSEST ERROR: Unable to find closest location for: "+scenario_values.get(gen_loc).toString());
			}
		}
	}
}
