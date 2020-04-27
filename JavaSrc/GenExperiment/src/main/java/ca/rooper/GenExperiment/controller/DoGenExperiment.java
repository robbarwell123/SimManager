package ca.rooper.GenExperiment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import ca.rooper.CommonExperiment.model.ResponseMessage;
import ca.rooper.GenExperiment.model.Airport;
import ca.rooper.GenExperiment.model.PathLinks;
import ca.rooper.GenExperiment.model.Plane;
import ca.rooper.GenExperiment.controller.SupportFunctions;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class DoGenExperiment
{
	private static final Logger logger = LoggerFactory.getLogger(DoGenExperiment.class);

	@Value("${config.airport_file}")
	private String airport_file_name;
	@Value("${config.output_dir}")
	private String output_dir;
	@Value("${config.num_scenarios}")
	private int num_scenarios;
	@Value("${config.num_locations}")
	private int num_locations;
	@Value("${config.dist}")
	private double[] dist;
	@Value("${config.main_bases}")
	private String main_bases;
	@Value("${config.planes}")
	private String planes;

    @Autowired
    private SpringTemplateEngine coupled_xml;
	
	private FileWriter log_writer=null;

	@CrossOrigin
	@GetMapping("/DoGenExperiment")
    public ResponseMessage UploadXMLs()
    {
		ClassLoader curr_class = this.getClass().getClassLoader();

		String curr_dir;
		String output_xml;
		File scenario_dir;
		File scenario_file;
		FileWriter scenario_writer=null;
		Context curr_context = new Context();
		
		DataInfo.config_airport_file=airport_file_name;
		DataInfo.config_num_scenarios=num_scenarios;
		DataInfo.config_num_locations=num_locations;
		DataInfo.config_dist=dist;
		DataInfo.config_main_bases=main_bases;
		DataInfo.config_airplanes=planes;
		
		ResponseMessage toRtn=new ResponseMessage();
		try
		{
			curr_dir=find_dir();
			log_writer=new FileWriter(curr_dir+"log.txt");
			DataInfo.airport_list=new ArrayList<ArrayList<Airport>>();
			DataInfo.main_base_list=new LinkedHashMap <String,Airport>();
			DataInfo.airplanes=new ArrayList<Plane>();
			DataInfo.distance_matrix=new int[0][0];
			ParseAirports.read_airport_file();
			ParseConfig.parse_config();
			log_status();
			for(int gen_scenario=0;gen_scenario<DataInfo.config_num_scenarios;gen_scenario++)
			{
				log_writer.write("Generating scenario "+gen_scenario+"\n");
				scenario_dir=new File(curr_dir+gen_scenario+"/");
				scenario_dir.mkdir();
				DataInfo.scenario_list=(LinkedHashMap <String, Airport>)DataInfo.main_base_list.clone();			
				GenLocations.gen_locations();
				log_writer.write("Locations:\n");
				for(Airport curr_loc : DataInfo.scenario_list.values())
				{
					log_writer.write(curr_loc.toString()+"\n");
				}
				DataInfo.distance_matrix=SupportFunctions.resize_matrix(DataInfo.distance_matrix, DataInfo.main_base_list.size());
				DataInfo.distance_matrix=SupportFunctions.resize_matrix(DataInfo.distance_matrix, DataInfo.scenario_list.size());
				FindClosest.find_closest();
				DataInfo.path_list=new ArrayList<PathLinks>();
				ShortestPath.calc();
				GenPallets.gen_pallets();
//				SupportFunctions.print_matrix(DataInfo.distance_matrix);
//				SupportFunctions.print_paths();

				curr_context.setVariable("locations", DataInfo.scenario_list.values());
				output_xml=coupled_xml.process("Locations", curr_context);
				scenario_file=new File(scenario_dir+"/Locations.xml");
				scenario_writer=new FileWriter(scenario_file);
				scenario_writer.write(output_xml);
				scenario_writer.close();

				curr_context.setVariable("pallets", DataInfo.pallets);
				output_xml=coupled_xml.process("Pallets", curr_context);
				output_xml=output_xml.replaceAll("\r\n", "\n");
				output_xml=output_xml.substring(0,output_xml.length()-1);
				scenario_file=new File(scenario_dir+"/Pallets.txt");
				scenario_writer=new FileWriter(scenario_file);
				scenario_writer.write(output_xml);
				scenario_writer.close();
				
				curr_context.setVariable("paths", DataInfo.path_list);
				output_xml=coupled_xml.process("LocInfo", curr_context);
				output_xml=output_xml.replaceAll("\r\n", "\n");
				output_xml=output_xml.substring(0,output_xml.length()-1);
				scenario_file=new File(scenario_dir+"/LocInfo.txt");
				scenario_writer=new FileWriter(scenario_file);
				scenario_writer.write(output_xml);
				scenario_writer.close();

				curr_context.setVariable("planes", DataInfo.airplanes);
				output_xml=coupled_xml.process("Aircraft", curr_context);
				scenario_file=new File(scenario_dir+"/Aircraft.xml");
				scenario_writer=new FileWriter(scenario_file);
				scenario_writer.write(output_xml);
				scenario_writer.close();

				Files.copy(Paths.get(curr_class.getResource("basefiles/Config.xml").toURI()), Paths.get(scenario_dir+"/Config.xml"),StandardCopyOption.REPLACE_EXISTING);
				Files.copy(Paths.get(curr_class.getResource("basefiles/TOP.xml").toURI()), Paths.get(scenario_dir+"/TOP.xml"),StandardCopyOption.REPLACE_EXISTING);
				
				logger.info("Completed: "+gen_scenario);
				log_writer.write("Completed scenario generation.\n");
			}
			
			
	        toRtn.setStatus("Success");
	        toRtn.setData("Complete");
		}catch(Exception ex)
		{
			toRtn.setStatus("Error");
			toRtn.setData(ex.toString());
		}finally
		{
			try
			{
				log_writer.close();
				scenario_writer.close();
			}catch(Exception ex)
			{
			}
		}

    	return toRtn;
    }
	
	private void log_status() throws Exception
	{
		log_writer.write("Used common airport file: "+DataInfo.config_airport_file+"\n");
		log_writer.write("Generating "+DataInfo.config_num_scenarios+" scenarios.\n");
		log_writer.write("Each scenario contains "+DataInfo.config_num_locations+" locations.\n");
		log_writer.write("Distribution as follows:"+"\n");
		for(int write_dist=0;write_dist<DataInfo.config_dist.length;write_dist++)
		{
			log_writer.write(DataInfo.CONT_NAMES[write_dist]+": "+DataInfo.config_dist[write_dist]+"\n");
		}
		log_writer.write("With main bases: "+DataInfo.config_main_bases+"\n");

	
	}
	private String find_dir()
	{
		File new_dir;
		for(int dir_check=1;dir_check<500;dir_check++)
		{
			new_dir=new File(output_dir+dir_check);
			if(!new_dir.isDirectory())
			{
				new_dir.mkdir();
				return output_dir+dir_check+"/";
			}
		}
		return output_dir+"500/";
	}
}