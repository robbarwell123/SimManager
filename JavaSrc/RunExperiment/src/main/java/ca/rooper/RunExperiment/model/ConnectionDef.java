package ca.rooper.RunExperiment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionDef
{
	private String portType;
	private String fromName;
	private String toName;
	private String fromType;
	private String toType;
}
