/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */


package org.fog.test.perfeval;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class RunIterations 
{
	
	public static void main(String[] args) 
	{
		String category = "NASA";
		int subCategory = 300;			// For HCSP 100, 200 are the instances, i-lohi, i-hilo, s-lohi, s-hilo, c-lohi, c-hilo.
		int agents = 30;
		int iterations = 1;

		while (iterations < 251) 
		{
			for (int i = 0; i < 25; i++) 
			{
//				String[] args1 = { "PSOGA", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) }; 
//				fogScheduling.main(args1);
//				String[] args2 = { "IPSO", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
//				fogScheduling.main(args2);
//				String[] args3 = { "oppoCWOA", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
//				fogScheduling.main(args3);
				String[] args4 = { "DOWOA", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
				fogScheduling.main(args4);
//				String[] args5 = { "DOWOAB", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
//				fogScheduling.main(args5);
//				String[] args5 = { "SACO", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
//				fogScheduling.main(args5);
			}
			
			if (iterations == 1)
			{ iterations = iterations+24; }
			else
			{ iterations = iterations +25; }
		}
			
//		category = "NASA";
//		while (subCategory < 2501) 
//		{
//			for (int i = 0; i < 20; i++) 
//			{
////				String[] args1 = { "TCaS2", category, Integer.toString(subCategory), Integer.toString(100), Integer.toString(500) };
////				fogScheduling.main(args1);
////
////				String[] args2 = { "SMWOA2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args2);
////
////				String[] args3 = { "WOAmM2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args3);
////
////				String[] args4 = { "HFSGA2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args4);
////
////				String[] args5 = { "EWOA_2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args5);
//			}	
//			subCategory = subCategory+100;
//		}
//
//		category = "HPC2N";
//		while (subCategory < 2501) 
//		{
//			for (int i = 0; i < 20; i++) 
//			{
////				String[] args1 = { "TCaS2", category, Integer.toString(subCategory), Integer.toString(100), Integer.toString(500) };
////				fogScheduling.main(args1);
////
////				String[] args2 = { "SMWOA2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args2);
////
////				String[] args3 = { "WOAmM2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args3);
////
////				String[] args4 = { "HFSGA2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args4);
////
////				String[] args5 = { "EWOA_2", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
////				fogScheduling.main(args5);
//			}	
//			subCategory = subCategory+100;
//		}

//		Final_Report_GoCJ.main(args);
//		Final_Report_HCSP.main(args);

//		String[] argNasa = { "NASA" };
//		Final_Report_NASA_HPC2N.main(argNasa);

//		String[] argHpc2n = { "HPC2N" };
//		Final_Report_NASA_HPC2N.main(argHpc2n);
	}

}