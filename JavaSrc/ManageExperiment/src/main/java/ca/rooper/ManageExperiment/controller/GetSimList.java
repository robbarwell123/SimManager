package ca.rooper.ManageExperiment.controller;

import java.util.ArrayList;
import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetSimList
{
//	private static final Logger logger = LoggerFactory.getLogger(GetSimList.class);

	@CrossOrigin
	@GetMapping("/GetSimList")
    public List<String> GetExperimentList()
    {
		List<String> toRtn = new ArrayList<String>();
		toRtn.add("Test");
		return toRtn;
    }
}
