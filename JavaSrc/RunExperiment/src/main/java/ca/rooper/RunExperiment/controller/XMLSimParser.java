package ca.rooper.RunExperiment.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.env.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ca.rooper.CommonExperiment.model.ExperimentError;
import ca.rooper.RunExperiment.model.ModelDef;
import ca.rooper.RunExperiment.model.ModelParam;

public class XMLSimParser
{
	private static XMLSimParser instance = null;

    private File myConfigXSD;
    private File myCoupledXSD;
    
	private DocumentBuilderFactory myXMLFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder myXMLParser;
	
	private XMLSimParser()
	{
	}

	public void SetXSD(Environment myEnvVars) throws ExperimentError
	{
		try
		{
			myConfigXSD=new File(myEnvVars.getProperty("xsd.config"));
			myCoupledXSD=new File(myEnvVars.getProperty("xsd.coupled"));
			myXMLParser = myXMLFactory.newDocumentBuilder();
		}catch(Exception errCreate)
		{
			throw new ExperimentError("Unabled to Start Parser: "+errCreate.toString());
		}
	}
	
	public static XMLSimParser getInstance() throws ExperimentError
	{
		if (instance == null)
		{
			instance=new XMLSimParser();			
		}
  
        return instance; 		
	}

	public ModelDef ParseConfigXML(ModelDef myTopModel, File myConfigFile, String sBaseExpDir, PrintWriter myLogFiles) throws ExperimentError
	{
		ModelDef toRtn=myTopModel;
		NodeList myNodes;

		try
		{
			if(!ValidateXML(myConfigFile,"configfile"))
			{
				throw new ExperimentError("Invalid XML File: "+myConfigFile.toString());
			}
			
			Document myCurrXML=myXMLParser.parse(myConfigFile);
			myCurrXML.getDocumentElement().normalize();
			Element myRoot=myCurrXML.getDocumentElement();
			toRtn.setSimulator(myRoot.getAttribute("name"));
			
			toRtn.setTimeType(myRoot.getElementsByTagName("timetype").item(0).getTextContent());
			toRtn.setRunUntil(myRoot.getElementsByTagName("rununtil").item(0).getTextContent());
			
			myNodes=myRoot.getElementsByTagName("header");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addHeaders(myNodes.item(iNode).getTextContent());
			}

			myNodes=myRoot.getElementsByTagName("object");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addObjects(myNodes.item(iNode).getTextContent());
			}
			
			myNodes=myRoot.getElementsByTagName("loggerfile");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				myLogFiles.println(myNodes.item(iNode).getAttributes().getNamedItem("name").getNodeValue()+".txt");
				toRtn.addLoggerFiles(myNodes.item(iNode).getAttributes().getNamedItem("name").getNodeValue());
			}
			
			myNodes=myRoot.getElementsByTagName("logger");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addLogger(myNodes.item(iNode).getAttributes());
			}
		}catch(Exception errParse)
		{
			throw new ExperimentError("Invalid XML File: "+myConfigFile.toString()+","+errParse.toString());
		}
		
		return toRtn;
	}
	
	public ModelDef ParseXML(File myXMLFile) throws ExperimentError
	{
		ModelDef toRtn = new ModelDef();
		NodeList myNodes;
		NodeList myParams;
		List<ModelParam> mySubModelParams;
		File tModelFile;
		
		try
		{
			if(!ValidateXML(myXMLFile,"coupledmodel"))
			{
				throw new ExperimentError("Invalid XML File: "+myXMLFile.toString());
			}
			
			Document myCurrXML=myXMLParser.parse(myXMLFile);
			myCurrXML.getDocumentElement().normalize();
			Element myRoot=myCurrXML.getDocumentElement();
			toRtn.setName(myRoot.getAttribute("name"));
									
			myNodes=myRoot.getElementsByTagName("port");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addPort(myNodes.item(iNode).getAttributes());
			}
			
			myNodes=myRoot.getElementsByTagName("submodel");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				if(myNodes.item(iNode).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("atomic"))
				{
					mySubModelParams=new ArrayList<ModelParam>();
					myParams=myNodes.item(iNode).getChildNodes();
					for(int iParam=0; iParam < myParams.getLength(); iParam++)
					{
						if(myParams.item(iParam).getNodeName().equalsIgnoreCase("param"))
						{
							mySubModelParams.add(new ModelParam(myParams.item(iParam).getAttributes().getNamedItem("type").getNodeValue(), myParams.item(iParam).getAttributes().getNamedItem("name").getNodeValue(), myParams.item(iParam).getAttributes().getNamedItem("value").getNodeValue()));
						}
					}
					toRtn.addSubModel(myNodes.item(iNode).getAttributes(),mySubModelParams);					
				}else if(myNodes.item(iNode).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("coupled"))
				{
					tModelFile=new File(FilenameUtils.getFullPath(myXMLFile.toString())+myNodes.item(iNode).getAttributes().getNamedItem("class_name").getNodeValue()+".xml");
					toRtn.addSubModel(myNodes.item(iNode).getAttributes(), ParseXML(tModelFile));
				}else
				{
					throw new ExperimentError("Unknown Submodel Type: "+myNodes.item(iNode).getAttributes().getNamedItem("type").getNodeValue());
				}
			}

			myNodes=myRoot.getElementsByTagName("eic");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addConnection(myNodes.item(iNode).getAttributes(),"EIC");
			}
			myNodes=myRoot.getElementsByTagName("eoc");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addConnection(myNodes.item(iNode).getAttributes(),"EOC");
			}
			myNodes=myRoot.getElementsByTagName("ic");
			for(int iNode=0; iNode < myNodes.getLength();iNode++)
			{
				toRtn.addConnection(myNodes.item(iNode).getAttributes(),"IC");
			}			
		}catch(Exception errParse)
		{
			throw new ExperimentError("Invalid XML File: "+myXMLFile.toString()+","+errParse.toString());
		}
		
		return toRtn;
	}
	
	private boolean ValidateXML(File myExperimentXML, String sType) throws ExperimentError
	{
		File myXSDFile=myConfigXSD;
		switch(sType)
		{
			case "configfile":
				myXSDFile=myConfigXSD;
				break;
			case "coupledmodel":
				myXSDFile=myCoupledXSD;
				break;
		}
		boolean bXML=true;
		
		try
		{
            SchemaFactory myXSDRepository = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema myXSDCompare = myXSDRepository.newSchema(myXSDFile);
            Validator myCompare = myXSDCompare.newValidator();
            myCompare.validate(new StreamSource(myExperimentXML));
        }catch (Exception exXMLValidErr)
		{
        	throw new ExperimentError("XML Parse Error on: "+myExperimentXML.getName()+" because of "+exXMLValidErr.toString());
        }
		
        return bXML;		
	}	
}
