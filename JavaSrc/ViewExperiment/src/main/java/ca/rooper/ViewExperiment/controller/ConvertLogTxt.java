package ca.rooper.ViewExperiment.controller;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.rooper.CommonExperiment.model.ResponseMessage;

@RestController
public class ConvertLogTxt
{
	private static final Logger logger = LoggerFactory.getLogger(ConvertLogTxt.class);

	@Value("${config.basedir}")
	private String base_dir;

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
		int state_as_int=0;
		
		try
		{
			myLogFiles=Files.readAllLines(Paths.get(base_dir+"logs.list"));
			
			for(String myLogFile : myLogFiles)
			{
				if(myLogFile.indexOf("stratairliftstates.txt")==0)
				{
					curr_log_file=new FileInputStream(base_dir+myLogFile);
					curr_file=new Scanner(curr_log_file);
					aircraft_file=new FileWriter(base_dir+"aircraft_parsed.txt");
					location_file=new FileWriter(base_dir+"location_parsed.txt");
					while(curr_file.hasNextLine())
					{
						curr_line=curr_file.nextLine();
						curr_elements=curr_line.split("\t");
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
					curr_file.close();
					curr_log_file.close();
					aircraft_file.close();
					location_file.close();
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
			}catch(Exception ex)
			{
			}
		}
		
		return toRtn;
    }	
}
