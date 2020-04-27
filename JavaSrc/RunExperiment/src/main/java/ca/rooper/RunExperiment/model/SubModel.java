package ca.rooper.RunExperiment.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubModel
{
	private String modelType;
	private String name;
	private String className;
	private String xmlImplementation;
	private List<ModelParam> modelParams;
	private ModelDef submodel;
	private String paramTypes;
	private String paramList;
	
	public SubModel()
	{
		modelParams=new ArrayList<ModelParam>();
	}
	
	public void Flatten()
	{
		for(ModelParam myParam : modelParams)
		{
			paramTypes+=myParam.getParamType()+",";
			paramList+=myParam.getValue()+",";			
		}
		paramTypes=paramTypes.replaceAll(",$", "");
		paramList=paramList.replaceAll(",$", "");
	}	
}
