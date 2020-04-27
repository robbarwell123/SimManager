package ca.rooper.GenExperiment.model;

public class Plane
{
	private int id;
	private int type;
	private int home;
	
	public Plane(int p_id,int p_type,int p_home)
	{
		id=p_id;
		type=p_type;
		home=p_home;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getHome()
	{
		return home;
	}
}
