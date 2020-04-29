package ca.rooper.ViewExperiment.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.rooper.CommonExperiment.model.ExperimentError;
import ca.rooper.CommonExperiment.model.ResponseMessage;

@RestController
public class ConvertLogTxt
{
	private static final Logger logger = LoggerFactory.getLogger(ConvertLogTxt.class);

	@Value("${config.basedir}")
	private String base_dir;

	private DocumentBuilderFactory myXMLFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder myXMLParser;

	private static final int ITEM_TIME=0;
	private static final int ITEM_MODEL=1;
	private static final int ITEM_TYPE=2;	
	private static final int ITEM_DATA=3;
	
	@CrossOrigin
	@GetMapping("/ConvertLogTxt")
    public ResponseMessage GetExperimentList(@RequestParam("dir") String base_dir)
    {		
		ResponseMessage toRtn=new ResponseMessage();
		ObjectMapper json_read=new ObjectMapper();
		List<String> myLogFiles=Collections.emptyList();
		FileInputStream curr_log_file=null;
		Scanner curr_file=null;
		String curr_line;
		String[] curr_elements;
		JsonNode curr_json;
		FileWriter aircraft_file=null;
		FileWriter location_file=null;
		FileWriter summary_file=null;
		int state_as_int=0;
		int max_time=0;
		int curr_time=0;
		Path curr_path;
		String curr_scenario;
		HashMap<String,Integer> planes;
		
		try
		{			
			myLogFiles=Files.readAllLines(Paths.get(base_dir+"logs.list"));
			
			for(String myLogFile : myLogFiles)
			{
				if(myLogFile.indexOf("stratairliftstates.txt")==0)
				{
					curr_path=Paths.get((new File(base_dir+"/"+myLogFile)).getParent());
					curr_scenario=curr_path.getFileName().toString();
					planes=parse_planes_xml(base_dir+"/Aircraft.xml");

					curr_log_file=new FileInputStream(base_dir+myLogFile);
					curr_file=new Scanner(curr_log_file);
					aircraft_file=new FileWriter(base_dir+"aircraft_parsed.txt");
					location_file=new FileWriter(base_dir+"location_parsed.txt");
					summary_file=new FileWriter(base_dir+"summary.txt");
					while(curr_file.hasNextLine())
					{
						curr_line=curr_file.nextLine();
						curr_elements=curr_line.split("\t");
						curr_time=Integer.parseInt(curr_elements[ITEM_TIME].replaceAll("/1", ""));
						if(curr_time>max_time)
						{
							max_time=curr_time;
						}
						if(curr_elements[ITEM_DATA].indexOf("data")!=-1)
						{
							curr_json=json_read.readTree("{"+curr_elements[ITEM_DATA]+"}");
							if(curr_json.get("data").get("class").asText().indexOf("Aircraft")!=-1)
							{
								aircraft_file.write(curr_elements[ITEM_TIME].replaceAll("/1", "")+"\t"+curr_json.get("data").get("state")+"\t"+curr_json.get("data").get("text")+"\t"+curr_json.get("data").get("location").get("lat")+"\t"+curr_json.get("data").get("location").get("long")+"\n");								
							}
							if(curr_json.get("data").get("class").asText().indexOf("Location")!=-1)
							{
								switch(curr_json.get("data").get("state").asText())
								{
									case "Waiting":
										state_as_int=1;
										break;
									case "Processing":
										state_as_int=2;
										break;
									case "Delivering":
										state_as_int=3;
										break;
									case "Sending Load":
										state_as_int=4;
										break;
									default:
										state_as_int=0;
										break;
										
								}
								location_file.write(curr_elements[ITEM_TIME].replaceAll("/1", "")+"\t"+curr_elements[ITEM_MODEL]+"\t"+curr_json.get("data").get("loc_id")+"\t"+curr_json.get("data").get("state")+"\t"+curr_json.get("data").get("pallets")+"\t"+state_as_int+"\t"+curr_json.get("data").get("location").get("lat")+"\t"+curr_json.get("data").get("location").get("long")+"\n");								
							}
						}
					}
					summary_file.write(curr_scenario+"\t"+max_time+"\t"+planes.get("1")+"\t"+planes.get("2")+"\n");					
					curr_file.close();
					curr_log_file.close();
					aircraft_file.close();
					location_file.close();
					summary_file.close();
				}
			}
			toRtn.setStatus("Success");
			toRtn.setData("Completed: "+base_dir);
		}catch(Exception errDelete)
		{
			toRtn.setStatus("Error");
			toRtn.setData(errDelete.toString());
		}finally
		{
			try
			{
				if(curr_log_file!=null)
				{
					curr_log_file.close();
				}
				if(curr_file!=null)
				{
					curr_file.close();
				}
				if(aircraft_file!=null)
				{
					aircraft_file.close();
				}
				if(location_file!=null)
				{
					location_file.close();
				}
				if(summary_file!=null)
				{
					location_file.close();
				}
			}catch(Exception ex)
			{
			}
		}
		
		return toRtn;
    }	
	
	HashMap<String,Integer> parse_planes_xml(String plane_xml_file) throws Exception
	{
		HashMap<String,Integer> list_of_planes=new HashMap<String,Integer>();
		NodeList myNodes;
		NodeList myParams;

		myXMLParser = myXMLFactory.newDocumentBuilder();

		Document myCurrXML=myXMLParser.parse(plane_xml_file);
		myCurrXML.getDocumentElement().normalize();
		Element myRoot=myCurrXML.getDocumentElement();
		
		myNodes=myRoot.getElementsByTagName("submodel");
		for(int iNode=0; iNode < myNodes.getLength();iNode++)
		{
			if(myNodes.item(iNode).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("atomic"))
			{
				myParams=myNodes.item(iNode).getChildNodes();
				for(int iParam=0; iParam < myParams.getLength(); iParam++)
				{
					if(myParams.item(iParam).getNodeName().equalsIgnoreCase("param") && myParams.item(iParam).getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("iType"))
					{
						list_of_planes.merge(myParams.item(iParam).getAttributes().getNamedItem("value").getNodeValue(),1,Integer::sum);
					}
				}
			}
		}
		
		return list_of_planes;
	}
}
