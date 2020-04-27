package ca.rooper.RunExperiment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoggerDef
{
	private String name;
	private String type;
	private String format;
	private String loggerFile;
}
