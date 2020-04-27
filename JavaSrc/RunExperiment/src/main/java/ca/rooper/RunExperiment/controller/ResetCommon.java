package ca.rooper.RunExperiment.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.rooper.CommonExperiment.model.ResponseMessage;

@RestController
public class ResetCommon
{
	@Value("${cadmium.resetcommon}")
	private String sResetCmd;
	
	@CrossOrigin
	@GetMapping("/ResetCommon")
    public ResponseMessage RunResetCommon()
    {
		ResponseMessage toRtn=new ResponseMessage();
		toRtn.setStatus("Success");
		
		String sErrMsg;
		
		try
		{
			Process myRun = Runtime.getRuntime().exec(sResetCmd);
			if(myRun.waitFor()!=0)
			{
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(myRun.getErrorStream()));				
	            while ((sErrMsg = stdError.readLine()) != null)
	            {
	            	toRtn.setData(sErrMsg);
	            }
	            toRtn.setStatus("Error");
			}
		}catch(Exception errReset)
		{
			toRtn.setStatus("Error");
			toRtn.setData("Reset Common Error: "+errReset.toString());
		}
		
		return toRtn;
    }
}
