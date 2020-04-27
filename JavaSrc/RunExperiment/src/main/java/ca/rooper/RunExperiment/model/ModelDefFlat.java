package ca.rooper.RunExperiment.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelDefFlat
{
	private String inPorts;
	private String outPorts;
	private String subModels;
	
	private List<ConnectionDef> EIC;
	private List<ConnectionDef> EOC;
	private List<ConnectionDef> IC;
	
	public ModelDefFlat()
	{
		inPorts="";
		outPorts="";
		subModels="";
		EIC = new ArrayList<ConnectionDef>();
		EOC = new ArrayList<ConnectionDef>();
		IC = new ArrayList<ConnectionDef>();
	}
	
	public void addEIC(ConnectionDef myConnection)
	{
		EIC.add(myConnection);
	}
	
	public void addIC(ConnectionDef myConnection)
	{
		IC.add(myConnection);
	}

	public void addEOC(ConnectionDef myConnection)
	{
		EOC.add(myConnection);
	}
}
