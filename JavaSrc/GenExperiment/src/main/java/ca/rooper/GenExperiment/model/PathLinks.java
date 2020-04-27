package ca.rooper.GenExperiment.model;

public class PathLinks
{
	private int source;
	private int dest;
	private int next;
	private double next_lat;
	private double next_lon;
	private int aircraft_type;
	
	public PathLinks(int p_source, int p_dest, int p_next, double p_next_lat, double p_next_lon, int p_aircraft_type)
	{
		source=p_source;
		dest=p_dest;
		next=p_next;
		next_lat=p_next_lat;
		next_lon=p_next_lon;
		aircraft_type=p_aircraft_type;
	}
	
	public String toString()
	{
		String rtn_val="0/1 ";

		rtn_val+=source+" ";
		rtn_val+=dest+" ";
		rtn_val+=next+" ";
		rtn_val+=next_lat+" ";
		rtn_val+=next_lon+" ";
		rtn_val+=aircraft_type+"\n";
		
		return rtn_val;
	}
}
