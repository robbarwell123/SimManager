package ca.rooper.ManageExperiment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ca.rooper.CommonExperiment.model.ResponseMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class Scenarios
{
	private static final Logger logger = LoggerFactory.getLogger(CreateExperiment.class);

	@Value("${config.basedir}")
	private String base_dir;
	@Value("${config.url.parsexml}")
	private String url_parsexml;
	@Value("${config.url.runsim}")
	private String url_runsim;
	@Value("${config.url.convertlog}")
	private String url_convertlog;
	
	@CrossOrigin
	@GetMapping("/CompileScenarios")
    public ResponseMessage CompileScenarios(@RequestParam("scenario") String scenario)
    {
		ResponseMessage resp_message=new ResponseMessage();
		Stream<Path> dir_list_stream=null;
		Path curr_scenario_dir=Paths.get(base_dir+scenario);
		RestTemplate rest_client = new RestTemplate();
		
		try
		{
			dir_list_stream = Files.walk(curr_scenario_dir);

			List<Path> dir_list = dir_list_stream.filter(Files::isDirectory)
					.map(curr_dir -> curr_dir.toAbsolutePath()).collect(Collectors.toList());

			dir_list.forEach(curr_dir -> {
				if(curr_dir.compareTo(curr_scenario_dir)!=0)
				{
					ResponseMessage curr_resp=rest_client.getForObject(url_parsexml+"?dir="+curr_dir.toString().replaceAll("\\\\", "/")+"/&top=TOP.xml&config=Config.xml",ResponseMessage.class);
					logger.info(curr_resp.getStatus()+" - "+curr_resp.getData()+" "+curr_dir.toString());					
				}
			});
			resp_message.setStatus("Success");
			resp_message.setData("Compiled all scenarios.");
		} catch (Exception ex)
		{
			resp_message.setStatus("Error");
			resp_message.setData(ex.toString());
		}finally
		{
			dir_list_stream.close();
		}
		
		return resp_message;
    }

	@CrossOrigin
	@GetMapping("/RunScenarios")
    public ResponseMessage RunScenarios(@RequestParam("scenario") String scenario)
    {
		ResponseMessage resp_message=new ResponseMessage();
		Stream<Path> dir_list_stream=null;
		Path curr_scenario_dir=Paths.get(base_dir+scenario);
		RestTemplate rest_client = new RestTemplate();
		
		try
		{
			dir_list_stream = Files.walk(curr_scenario_dir);

			List<Path> dir_list = dir_list_stream.filter(Files::isDirectory)
					.map(curr_dir -> curr_dir.toAbsolutePath()).collect(Collectors.toList());

			dir_list.forEach(curr_dir -> {
				if(curr_dir.compareTo(curr_scenario_dir)!=0)
				{
					ResponseMessage curr_resp=rest_client.getForObject(url_runsim+"?dir="+curr_dir.toString().replaceAll("\\\\", "/"),ResponseMessage.class);
					logger.info(curr_resp.getStatus()+" - "+curr_resp.getData());					
				}
			});
			resp_message.setStatus("Success");
			resp_message.setData("All scenarios run.");
		} catch (Exception ex)
		{
			resp_message.setStatus("Error");
			resp_message.setData(ex.toString());
		}finally
		{
			dir_list_stream.close();
		}
		
		return resp_message;
    }	

	@CrossOrigin
	@GetMapping("/ConvertLogs")
    public ResponseMessage convert_logs(@RequestParam("scenario") String scenario)
    {
		ResponseMessage resp_message=new ResponseMessage();
		Stream<Path> dir_list_stream=null;
		Path curr_scenario_dir=Paths.get(base_dir+scenario);
		RestTemplate rest_client = new RestTemplate();
		
		try
		{
			dir_list_stream = Files.walk(curr_scenario_dir);

			List<Path> dir_list = dir_list_stream.filter(Files::isDirectory)
					.map(curr_dir -> curr_dir.toAbsolutePath()).collect(Collectors.toList());

			dir_list.forEach(curr_dir -> {
				if(curr_dir.compareTo(curr_scenario_dir)!=0)
				{
					ResponseMessage curr_resp=rest_client.getForObject(url_convertlog+"?dir="+curr_dir.toString().replaceAll("\\\\", "/")+"/",ResponseMessage.class);
					logger.info(curr_resp.getStatus()+" - "+curr_resp.getData()+" "+curr_dir.toString());					
				}
			});
			resp_message.setStatus("Success");
			resp_message.setData("Converted all scenarios.");
		} catch (Exception ex)
		{
			resp_message.setStatus("Error");
			resp_message.setData(ex.toString());
		}finally
		{
			dir_list_stream.close();
		}
		
		return resp_message;
    }		

	@CrossOrigin
	@GetMapping("/MergeLogs")
    public ResponseMessage merge_logs(@RequestParam("scenario") String scenario)
    {
		ResponseMessage resp_message=new ResponseMessage();
		Stream<Path> dir_list_stream=null;
		Path curr_scenario_dir=Paths.get(base_dir+scenario);
		RestTemplate rest_client = new RestTemplate();
		FileWriter merged_logs=null;
		FileInputStream curr_log_file=null;
		Scanner curr_file=null;
		String curr_line;
		File curr_log;
		Path curr_path;
		String curr_scenario;
		
		try
		{
			dir_list_stream = Files.walk(curr_scenario_dir);

			List<Path> dir_list = dir_list_stream.filter(Files::isDirectory)
					.map(curr_dir -> curr_dir.toAbsolutePath()).collect(Collectors.toList());
			
			merged_logs=new FileWriter(curr_scenario_dir.toString()+"/aircraft_merged.txt");
			merged_logs.write("id\ttime\tstate\taircraft\tlat\tlon\n");
			for(Path curr_dir : dir_list)
			{
				curr_log=new File(curr_dir+"/aircraft_parsed.txt");
				curr_path=Paths.get(curr_log.getParent());
				curr_scenario=curr_path.getFileName().toString();
				if(curr_log.exists())
				{
					curr_log_file=new FileInputStream(curr_log);
					curr_file=new Scanner(curr_log_file);
					while(curr_file.hasNextLine())
					{
						curr_line=curr_file.nextLine();
						merged_logs.write(curr_scenario+"\t"+curr_line+"\n");
					}
					merged_logs.flush();
					curr_file.close();
					curr_log_file.close();
					logger.info("Merged: "+curr_log.toString());
				}
			}
			merged_logs.close();
			merged_logs=new FileWriter(curr_scenario_dir.toString()+"/locations_merged.txt");
			merged_logs.write("scenario_id\ttime\tlocation\tloc_id\tstate\tpallets\tstate_num\tlat\tlon\n");
			for(Path curr_dir : dir_list)
			{
				curr_log=new File(curr_dir+"/location_parsed.txt");
				curr_path=Paths.get(curr_log.getParent());
				curr_scenario=curr_path.getFileName().toString();
				if(curr_log.exists())
				{
					curr_log_file=new FileInputStream(curr_log);
					curr_file=new Scanner(curr_log_file);
					while(curr_file.hasNextLine())
					{
						curr_line=curr_file.nextLine();
						merged_logs.write(curr_scenario+"\t"+curr_line+"\n");
					}
					merged_logs.flush();
					curr_file.close();
					curr_log_file.close();
					logger.info("Merged: "+curr_log.toString());
				}
			}
			merged_logs.close();
			merged_logs=new FileWriter(curr_scenario_dir.toString()+"/summary_merged.txt");
			for(Path curr_dir : dir_list)
			{
				curr_log=new File(curr_dir+"/summary.txt");
				curr_path=Paths.get(curr_log.getParent());
				curr_scenario=curr_path.getFileName().toString();
				if(curr_log.exists())
				{
					curr_log_file=new FileInputStream(curr_log);
					curr_file=new Scanner(curr_log_file);
					while(curr_file.hasNextLine())
					{
						curr_line=curr_file.nextLine();
						merged_logs.write(curr_line+"\n");
					}
					merged_logs.flush();
					curr_file.close();
					curr_log_file.close();
					logger.info("Merged: "+curr_log.toString());
				}
			}
			merged_logs.close();
			resp_message.setStatus("Success");
			resp_message.setData("Merged all scenarios.");
		} catch (Exception ex)
		{
			resp_message.setStatus("Error");
			resp_message.setData(ex.toString());
		}finally
		{
			dir_list_stream.close();
			try
			{
				if(curr_log_file!=null)
				{
					curr_log_file.close();
				}
				if(curr_file!=null)
				{
					curr_file.close();
				}
				if(merged_logs!=null)
				{
					merged_logs.close();
				}
			}catch(Exception ex)
			{
			}
		}
		
		return resp_message;
    }			

	@CrossOrigin
	@GetMapping("/MergeSummaries")
    public ResponseMessage merge_summaries()
    {
		ResponseMessage resp_message=new ResponseMessage();
		Stream<Path> dir_list_stream=null;
		FileWriter merged_logs=null;
		FileInputStream curr_log_file=null;
		Scanner curr_file=null;
		String curr_line;
		File curr_log;
		Path curr_path;
		String curr_scenario;
		
		try
		{
			dir_list_stream = Files.walk(Paths.get(base_dir));

			List<Path> dir_list = dir_list_stream.filter(Files::isDirectory)
					.map(curr_dir -> curr_dir.toAbsolutePath()).collect(Collectors.toList());
			
			merged_logs=new FileWriter(base_dir+"all_summaries.txt");
			merged_logs.write("scenario\trun\ttime\tC17\tC130\n");
			for(Path curr_dir : dir_list)
			{
				curr_log=new File(curr_dir+"/summary_merged.txt");
				curr_path=Paths.get(curr_log.getParent());
				curr_scenario=curr_path.getFileName().toString();
				if(curr_log.exists())
				{
					curr_log_file=new FileInputStream(curr_log);
					curr_file=new Scanner(curr_log_file);
					while(curr_file.hasNextLine())
					{
						curr_line=curr_file.nextLine();
						merged_logs.write(curr_scenario+"\t"+curr_line+"\n");
					}
					merged_logs.flush();
					curr_file.close();
					curr_log_file.close();
					logger.info("Merged: "+curr_log.toString());
				}
			}
			merged_logs.close();
			resp_message.setStatus("Success");
			resp_message.setData("Merged all summaries.");
		} catch (Exception ex)
		{
			resp_message.setStatus("Error");
			resp_message.setData(ex.toString());
		}finally
		{
			dir_list_stream.close();
			try
			{
				if(curr_log_file!=null)
				{
					curr_log_file.close();
				}
				if(curr_file!=null)
				{
					curr_file.close();
				}
				if(merged_logs!=null)
				{
					merged_logs.close();
				}
			}catch(Exception ex)
			{
			}
		}
		
		return resp_message;
    }			

}
