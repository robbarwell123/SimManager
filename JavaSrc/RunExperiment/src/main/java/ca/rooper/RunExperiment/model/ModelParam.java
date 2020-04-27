package ca.rooper.RunExperiment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelParam
{
	private String paramType;
	private String name;
	private String value;
}
