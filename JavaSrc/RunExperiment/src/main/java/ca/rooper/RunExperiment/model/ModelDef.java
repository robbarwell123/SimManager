package ca.rooper.RunExperiment.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;

import lombok.Data;

@Data
public class ModelDef
{
	private String simulator;
	private String name;
	private List<String> headers;
	private List<String> objects;
	private String timeType;
	private String runUntil;
	
	private List<LoggerDef> loggers;
	private List<PortDef> ports;
	private List<ConnectionDef> connections;
	private List<String> loggerFiles;
	private List<SubModel> models;
	
	private ModelDefFlat myFlatModel;
	
	public ModelDef()
	{
		headers=new ArrayList<String>();
		objects=new ArrayList<String>();
		loggers=new ArrayList<LoggerDef>();
		ports=new ArrayList<PortDef>();
		connections=new ArrayList<ConnectionDef>();
		loggerFiles=new ArrayList<String>();
		models=new ArrayList<SubModel>();
	}
	
	public void Flatten()
	{
		myFlatModel = new ModelDefFlat();
		for(PortDef myPort : ports)
		{
			if(myPort.getType().equalsIgnoreCase("IN"))
			{
				myFlatModel.setInPorts(myFlatModel.getInPorts()+"typeid("+myPort.getName()+"),");
			}else if(myPort.getType().equalsIgnoreCase("OUT"))
			{
				myFlatModel.setOutPorts(myFlatModel.getOutPorts()+"typeid("+myPort.getName()+"),");				
			}
		}
		myFlatModel.setInPorts(myFlatModel.getInPorts().replaceAll(",$", ""));
		myFlatModel.setOutPorts(myFlatModel.getOutPorts().replaceAll(",$", ""));
		
		for(SubModel myModel : models)
		{
			myFlatModel.setSubModels(myFlatModel.getSubModels()+myModel.getName()+",");
		}
		myFlatModel.setSubModels(myFlatModel.getSubModels().replaceAll(",$", ""));
		
		for(ConnectionDef myConnection : connections)
		{
			if(myConnection.getPortType().equalsIgnoreCase("EIC"))
			{
				myFlatModel.addEIC(myConnection);
			}else if(myConnection.getPortType().equalsIgnoreCase("EOC"))
			{
				myFlatModel.addEOC(myConnection);
			}else if(myConnection.getPortType().equalsIgnoreCase("IC"))
			{
				myFlatModel.addIC(myConnection);
			}
		}
	}
	
	public void addHeaders(String tHeader)
	{
		headers.add(tHeader);
	}
	
	public void addObjects(String tObject)
	{
		objects.add(tObject);
	}
	
	public void addLogger(NamedNodeMap myLogger)
	{
		loggers.add(new LoggerDef(myLogger.getNamedItem("name").getNodeValue(),myLogger.getNamedItem("type").getNodeValue(),myLogger.getNamedItem("format").getNodeValue(),myLogger.getNamedItem("loggerfile").getNodeValue()));
	}
	
	public void addLoggerFiles(String tFile)
	{
		loggerFiles.add(tFile);
	}
	
	public void addPort(NamedNodeMap myPort)
	{
		ports.add(new PortDef(myPort.getNamedItem("type").getNodeValue(),myPort.getNamedItem("name").getNodeValue(),myPort.getNamedItem("message_type").getNodeValue()));
	}
	
	public void addSubModel(NamedNodeMap myModel,List<ModelParam> myParams)
	{
		models.add(new SubModel(myModel.getNamedItem("type").getNodeValue(),myModel.getNamedItem("name").getNodeValue(),myModel.getNamedItem("class_name").getNodeValue(),myModel.getNamedItem("xml_implementation").getNodeValue(),myParams,null,",",","));
	}
	
	public void addSubModel(NamedNodeMap myModel, ModelDef myCoupledModel)
	{
		models.add(new SubModel(myModel.getNamedItem("type").getNodeValue(),myModel.getNamedItem("name").getNodeValue(),myModel.getNamedItem("class_name").getNodeValue(),myModel.getNamedItem("xml_implementation").getNodeValue(),null,myCoupledModel,",",","));		
	}

	public void addConnection(NamedNodeMap myConnection,String conType)
	{
		switch(conType)
		{
			case "EOC":
				connections.add(new ConnectionDef("eoc",myConnection.getNamedItem("submodel").getNodeValue(),"",myConnection.getNamedItem("out_port_submodel").getNodeValue(),myConnection.getNamedItem("out_port_coupled").getNodeValue()));
				break;
			case "EIC":
				connections.add(new ConnectionDef("eic","",myConnection.getNamedItem("submodel").getNodeValue(),myConnection.getNamedItem("in_port_coupled").getNodeValue(),myConnection.getNamedItem("in_port_submodel").getNodeValue()));
				break;
			case "IC":
				connections.add(new ConnectionDef("ic",myConnection.getNamedItem("from_submodel").getNodeValue(),myConnection.getNamedItem("to_submodel").getNodeValue(),myConnection.getNamedItem("out_port_from").getNodeValue(),myConnection.getNamedItem("in_port_to").getNodeValue()));
				break;
		}
	}	
}