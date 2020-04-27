package ca.rooper.GenExperiment.controller;

public class GenException extends Exception
{
	private static final long serialVersionUID = 6818158883221108078L;

	public GenException(String error_message)
	{
		super(error_message);
	}
}
