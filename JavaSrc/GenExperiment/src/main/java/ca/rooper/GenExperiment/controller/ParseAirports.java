package ca.rooper.GenExperiment.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.rooper.GenExperiment.model.Airport;

public class ParseAirports
{
	private static final Logger logger = LoggerFactory.getLogger(ParseAirports.class);
		
	public static void read_airport_file() throws GenException
	{
		BufferedReader airport_file=null;
		String curr_line;
		String[] curr_fields;
		Airport temp_airport;
		
		if(DataInfo.airport_list.size()==0)
		{
			for(int init=0;init<7;init++)
			{
				DataInfo.airport_list.add(new ArrayList<Airport>());
			}
		}

		try
		{
			airport_file=new BufferedReader(new FileReader(DataInfo.config_airport_file));
			while((curr_line=airport_file.readLine())!=null)
			{
				curr_fields=curr_line.split("\t");
				if(curr_fields[DataInfo.FIELD_TYPE].matches("medium_airport|large_airport"))
				{
					temp_airport=new Airport(curr_fields[DataInfo.FIELD_ID],curr_fields[DataInfo.FIELD_NAME], Double.parseDouble(curr_fields[DataInfo.FIELD_LAT]),Double.parseDouble(curr_fields[DataInfo.FIELD_LON]));
					DataInfo.airport_list.get(DataInfo.CONT_VALUES.get(curr_fields[DataInfo.FIELD_CONT])).add(temp_airport);
				}
			}
		}catch(Exception e)
		{
			throw new GenException("System Error: "+e.toString());
		}finally
		{
			try
			{
				airport_file.close();
			}catch(Exception e)
			{
			}
		}
	}

}
