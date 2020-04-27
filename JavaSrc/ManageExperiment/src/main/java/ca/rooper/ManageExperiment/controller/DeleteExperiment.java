package ca.rooper.ManageExperiment.controller;

import java.io.File;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUtils;
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
public class DeleteExperiment
{
//	private static final Logger logger = LoggerFactory.getLogger(DeleteExperiment.class);

	@Value("${config.basedir}")
	private String sBaseDir;
	
	@CrossOrigin
	@GetMapping("/DeleteExperiment")
    public ResponseMessage GetExperimentList(@RequestParam("iId") int tId)
    {
		ResponseMessage toRtn=new ResponseMessage();
		File myExperimentDir;
		
		try
		{
			myExperimentDir=new File(sBaseDir+tId+"/");
			FileUtils.deleteDirectory(myExperimentDir);
			toRtn.setStatus("Success");
			toRtn.setData("Experiment "+tId+" Deleted");
		}catch(Exception errDelete)
		{
			toRtn.setStatus("Error");
			toRtn.setData(errDelete.toString());
		}
		
		return toRtn;
    }

}
