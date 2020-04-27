package ca.rooper.GenExperiment.model;

public class Airport
{
	private String icao_id;
	private String name;
	private double lat;
	private double lon;

	public Airport(String p_id, String p_name, double p_lat, double p_lon)
	{
		icao_id=p_id;
		name=p_name.replaceAll("[^a-zA-Z0-9 ]", "");
		lat=p_lat;
		lon=p_lon;
	}
	
	public String toString()
	{
		return name +" ("+icao_id+") "+lat+","+lon;
	}
	
	public String getId()
	{
		return icao_id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLon()
	{
		return lon;
	}
}
