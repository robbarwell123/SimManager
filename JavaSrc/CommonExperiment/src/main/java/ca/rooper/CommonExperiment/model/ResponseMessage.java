package ca.rooper.CommonExperiment.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseMessage
{
	private String status;
	private String data;
	
	public ResponseMessage()
	{
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public String getData()
	{
		return data;
	}
	
	public void setStatus(String tStatus)
	{
		status=tStatus;
	}
	
	public void setData(String tData)
	{
		data=tData;
	}
}
