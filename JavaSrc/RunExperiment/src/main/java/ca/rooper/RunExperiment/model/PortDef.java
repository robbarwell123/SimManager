package ca.rooper.RunExperiment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortDef
{
	private String type;
	private String name;
	private String messageType;
}
