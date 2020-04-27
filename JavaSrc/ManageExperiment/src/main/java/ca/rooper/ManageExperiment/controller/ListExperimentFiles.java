package ca.rooper.ManageExperiment.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListExperimentFiles
{
//	private static final Logger logger = LoggerFactory.getLogger(GetSimList.class);

	@Value("${config.basedir}")
	private String sBaseDir;
	
	@CrossOrigin
	@GetMapping("/ListExperimentFiles")
    public List<String> GetFileList(@RequestParam("iId") int tId)
    {
		List<String> toRtn=new ArrayList<String>();

		try
		{
			Stream<Path> myDir = Files.walk(Paths.get(sBaseDir+tId));
			toRtn = myDir.filter(Files::isRegularFile).map(myFile -> myFile.getFileName().toString()).collect(Collectors.toList());
			myDir.close();
		}catch(Exception errDelete)
		{
		}
		
		return toRtn;
    }

}
