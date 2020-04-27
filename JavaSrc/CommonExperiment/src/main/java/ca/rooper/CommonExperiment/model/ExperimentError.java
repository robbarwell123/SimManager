package ca.rooper.CommonExperiment.model;

public class ExperimentError extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2632541950735867390L;

	public ExperimentError(String sErrMsg)
	{
        super(sErrMsg);
    }
}
