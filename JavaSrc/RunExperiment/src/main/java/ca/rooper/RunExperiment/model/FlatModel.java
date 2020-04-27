package ca.rooper.RunExperiment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlatModel
{
	ModelDef myTopModel;
	TreeMap<String,PortDef> myPorts;
	String myLoggers;
	List<SubModel> myAtomicModels;
	TreeMap<String,SubModel> myAtomicModelTypes;
	List<ModelDef> myCoupledModels;
	HashMap<String,String> myInputReaders;
	
	public FlatModel()
	{
		myPorts = new TreeMap<String,PortDef>();
		myAtomicModels = new ArrayList<SubModel>();
		myCoupledModels = new ArrayList<ModelDef>();
		myAtomicModelTypes = new TreeMap<String,SubModel>();
		myInputReaders = new HashMap<String,String>();
	}
	
	public void addPort(String sPort, PortDef myPort)
	{
		myPorts.put(sPort, myPort);
	}
	
	public void addAtomicModel(SubModel myAtomic)
	{
		myAtomicModels.add(myAtomic);
		myAtomicModelTypes.put(myAtomic.getClassName(),myAtomic);
		myAtomic.Flatten();
	}
	
	public void addCoupledModel(ModelDef myCoupled)
	{
		myCoupledModels.add(myCoupled);
	}
	
	public void addInputReader(SubModel myAtomic)
	{
		for(ModelParam myParam : myAtomic.getModelParams())
		{
			if(myParam.getParamType().equalsIgnoreCase("message_type"))
			{
				myInputReaders.put(myParam.getName(), myParam.getValue());
			}
		}
	}
}
