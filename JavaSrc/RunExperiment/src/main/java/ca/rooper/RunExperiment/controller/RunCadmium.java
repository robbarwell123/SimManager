package ca.rooper.RunExperiment.controller;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import ca.rooper.CommonExperiment.model.ExperimentError;
import ca.rooper.RunExperiment.model.FlatModel;
import ca.rooper.RunExperiment.model.LoggerDef;
import ca.rooper.RunExperiment.model.ModelDef;
import ca.rooper.RunExperiment.model.PortDef;
import ca.rooper.RunExperiment.model.SubModel;
import ca.rooper.CommonExperiment.model.ResponseMessage;

@RestController
public class RunCadmium
{
	private static final Logger logger = LoggerFactory.getLogger(RunCadmium.class);

	@Value("${experiments.run}")
	private String sRunCommand;
	@Value("${cadmium.maxtimeout}")
	private int iMaxTimeout;

	private ResponseMessage toRtn;
	
	@CrossOrigin
	@GetMapping("/RunExperiment")
    public ResponseMessage RunExperimentList(@RequestParam("dir") String base_dir)
    {
		boolean bStatus=true;
		toRtn=new ResponseMessage();
				
		try
		{
			RunSim(base_dir);
			toRtn.setData("Sim Executed Successfully.");
		}catch(Exception errConvert)
		{
			bStatus=false;
			toRtn.setData(errConvert.toString());
		}		
		
		toRtn.setStatus(bStatus ? "Success" : "Error");
		return toRtn;
    }
	
	public void RunSim(String base_dir) throws ExperimentError
	{
		String sErrMsg;
		
		try
		{
logger.info(sRunCommand+" "+base_dir.replaceAll(":", ""));
			Process myRun = Runtime.getRuntime().exec(sRunCommand+" "+base_dir.replaceAll(":", ""));
			if(!myRun.waitFor(iMaxTimeout,TimeUnit.SECONDS))
			{
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(myRun.getErrorStream()));				
	            while ((sErrMsg = stdError.readLine()) != null)
	            {
	            	toRtn.setData(sErrMsg);
	            }
            	throw new ExperimentError("Run Error");
			}
		}catch(Exception errCompile)
		{
			throw new ExperimentError("Run Error");
		}
	}	
}