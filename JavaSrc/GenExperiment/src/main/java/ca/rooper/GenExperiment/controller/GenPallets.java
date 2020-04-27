package ca.rooper.GenExperiment.controller;

import java.util.Random;

public class GenPallets
{
	public static void gen_pallets() throws GenException
	{
		Random rand_gen=new Random();
		
		DataInfo.pallets=new int[DataInfo.scenario_list.size()];
		
		for(int location=DataInfo.main_base_list.size();location<DataInfo.scenario_list.size();location++)
		{
			DataInfo.pallets[location]=rand_gen.nextDouble()<=0.2 ? 1000 : 200;
		}
	}
}
