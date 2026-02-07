/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
// testing 123

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

public class Run 
{
	
	public static void main(String[] args) 
	{
		String category = "HCSP";
		int subCategory = 100;		// For HCSP 100, 200 are the instances, i-lohi, i-hilo, s-lohi, s-hilo, c-lohi, c-hilo.
		int agents = 30;
		int iterations = 250;

		while (subCategory < 101) 
		{
			for (int i = 0; i < 1; i++) 
			{
				String[] args16 = { "DOWOA", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
				fogScheduling.main(args16);

			}	
			subCategory = subCategory+100;
		}
			
		category = "NASA";
		while (subCategory < 2501) 
		{
			for (int i = 0; i < 20; i++) 
			{
				String[] args5 = { "DOWOA", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
				fogScheduling.main(args5);
			}	
			subCategory = subCategory+100;
		}

		category = "HPC2N";
		while (subCategory < 2501) 
		{
			for (int i = 0; i < 20; i++) 
			{
				String[] args5 = { "DOWOA", category, Integer.toString(subCategory), Integer.toString(agents), Integer.toString(iterations) };
				fogScheduling.main(args5);
			}	
			subCategory = subCategory+100;
		}

	}

}