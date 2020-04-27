package ca.rooper.ViewExperiment.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.rooper.CommonExperiment.model.ResponseMessage;

@RestController
public class GetJSONLog
{
//	private static final Logger logger = LoggerFactory.getLogger(GetJSONLog.class);

	@Value("${config.basedir}")
	private String base_dir;

	private static final int ITEM_TIME=0;
	private static final int ITEM_MODEL=1;
	private static final int ITEM_TYPE=2;	
	private static final int ITEM_DATA=3;
	
	@CrossOrigin
	@GetMapping("/GetJSONLog")
    public ResponseMessage GetExperimentList(@RequestParam("source") String source)
    {		
		
		ResponseMessage toRtn=new ResponseMessage();
		List<String> myLogFiles=Collections.emptyList();
		List<String> myLogEntries=Collections.emptyList();
		String[] sEntityElements;
		
		LinkedHashMap<String,HashMap<String,String>> myJSONLog;
		String tJSONEntry;
		
		try
		{
			myJSONLog=new LinkedHashMap<String,HashMap<String,String>>();
			myLogFiles=Files.readAllLines(Paths.get(source+"/logs.list"));
			
			for(String myLogFile : myLogFiles)
			{
				myLogEntries=Files.readAllLines(Paths.get(source+"/"+myLogFile));
				for(String sLogEntry : myLogEntries)
				{
					sEntityElements=sLogEntry.split("\t");
					tJSONEntry="{\"time\":\""+sEntityElements[ITEM_TIME]+"\",\"name\":\""+sEntityElements[ITEM_MODEL]+"\",\"type\":\""+sEntityElements[ITEM_TYPE]+"\",";
					tJSONEntry+=sEntityElements[ITEM_DATA].matches("\"data\":.*") ? sEntityElements[ITEM_DATA] : "\"data\":{\"Debug\":\""+sEntityElements[ITEM_DATA]+"\"}";
					tJSONEntry+="}";
					if(!myJSONLog.containsKey(sEntityElements[ITEM_TIME]))
					{
						myJSONLog.put(sEntityElements[ITEM_TIME], new HashMap<String,String>());
					}
					myJSONLog.get(sEntityElements[ITEM_TIME]).put(sEntityElements[ITEM_MODEL]+sEntityElements[ITEM_TYPE], tJSONEntry);						
				}
			}
			toRtn.setStatus("Success");
			toRtn.setData(ConstructJSON(myJSONLog));
		}catch(Exception errDelete)
		{
			toRtn.setStatus("Error");
			toRtn.setData(errDelete.toString());
		}
		
		return toRtn;
    }
	
	public String ConstructJSON(LinkedHashMap<String,HashMap<String,String>> myJSONLog)
	{
		String myCompleteLog="{";
		
		for(Map.Entry<String, HashMap<String,String>> myTimePeriod : myJSONLog.entrySet())
		{
			myCompleteLog+="\""+myTimePeriod.getKey()+"\":[";
			for(Map.Entry<String,String> myLogEntry : myTimePeriod.getValue().entrySet())
			{
				myCompleteLog+=myLogEntry.getValue()+",";
			}
			myCompleteLog=myCompleteLog.replaceAll(",$", "");
			myCompleteLog+="],";
		}
		
		myCompleteLog=myCompleteLog.replaceAll(",$", "");
		myCompleteLog+="}";
		return myCompleteLog;
	}
}
