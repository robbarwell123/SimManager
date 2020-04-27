package ca.rooper.GenExperiment.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.rooper.GenExperiment.model.Airport;
import ca.rooper.GenExperiment.model.PathLinks;

public class ShortestPath
{
	private static final Logger logger = LoggerFactory.getLogger(ShortestPath.class);

	public static void calc() throws GenException
	{
		for(int build_paths=0;build_paths<DataInfo.scenario_list.size();build_paths++)
		{
			dijsktra(build_paths);
		}
	}
	
	private static void dijsktra(int source)
	{
	    int dist[]=new int[DataInfo.scenario_list.size()];
	    int visited[]=new int[DataInfo.scenario_list.size()];
	    int next[]=new int[DataInfo.scenario_list.size()];
	    int curr=source;
	    int smallest_value=0;
	    PathLinks new_path;

		ArrayList<Airport> scenario_values=new ArrayList<Airport>(DataInfo.scenario_list.values());

	    for(int init=0;init<DataInfo.scenario_list.size();init++)
	    {
	        dist[init]=Integer.MAX_VALUE;
	        visited[init]=0;
	    }
	    
	    dist[curr]=0;
	    next[curr]=0;
	    
	    while(curr!=Integer.MAX_VALUE)
	    {
	        for(int check=0;check<DataInfo.scenario_list.size();check++)
	        {
	            if(visited[check]!=1 && DataInfo.distance_matrix[curr][check]!=0 && (dist[curr]+DataInfo.distance_matrix[curr][check])<dist[check])
	            {
	                dist[check]=(dist[curr]+DataInfo.distance_matrix[curr][check]);
	                if(curr==source)
	                {
	                    next[check]=check;
	                }else
	                {
	                    next[check]=next[curr];
	                }
	            }
	        }

	        visited[curr]=1;
	        
	        smallest_value=Integer.MAX_VALUE;
	        curr=Integer.MAX_VALUE;
	        for(int check=0;check<DataInfo.scenario_list.size();check++)
	        {
	            if(visited[check]!=1 && dist[check]<smallest_value)
	            {
	                smallest_value=dist[check];
	                curr=check;
	            }
	        }
	    }
	    
	    for(int convert=0;convert<DataInfo.scenario_list.size();convert++)
	    {
	        if(convert!=source)
	        {
	        	new_path=new PathLinks(source,convert,next[convert],scenario_values.get(next[convert]).getLat(),scenario_values.get(next[convert]).getLon(),(source<DataInfo.main_base_list.size() && next[convert]<DataInfo.main_base_list.size()) ? 1: 2);
	        	DataInfo.path_list.add(new_path);
	        }
	    }
	}
}
