/*Dynamic OBL based WOA.*/
  
package org.fog.test.perfeval;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.fog.entities.FogDevice;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.Math;

public class DOWOA {
	
	private static String category = "Random";
	private static String subCategory;
	private static int noOfWhales = 5; 	// 50 according to page no. 15 last paragraph
	private static int maxItr = 5;		// 200 according to page no. 15 last paragraph
	private static int wait = 10;

	private static double FS = 0;
	private static double makespan = 0;
	private static double costs = 0;
	private static double energy = 0;
	private static double delay = 0;
	private static double loadVar = 0;
	private static double loadBal = 0;
	private static double responseT = 0;
	
	private static double minTime = 0;
	private static double minCost = 0;
	private static double minEnergy = 0;
	private static double minDelay = 0;
	private static double maxTime = 0;
	private static double maxCost = 0;
	private static double maxEnergy = 0;

	private static HashMap<Integer, Double> bestWhalesMap = new HashMap<>();
	private static HashMap<Integer, Double> bestFitnessMap = new HashMap<>();				// stores the best fitness for each agent
	private static HashMap<Integer, HashMap <Cloudlet, FogDevice>> gBWhaleMap = new HashMap<>();
	private static HashMap<Integer, HashMap<Integer, Double>> VMRTMap = new HashMap<>();
	private static HashMap<Integer, Double> fitnessMap = new HashMap<>();					// stores the latest fitness for each agent
	private static HashMap<Integer, HashMap<Cloudlet, FogDevice>> whalesPopulationMap = new HashMap<>();
	private static HashMap<Integer, Integer> waitForWhalePopulationMap = new HashMap<>();
	
	/** The cloudlet list. */
	private static List<? extends Cloudlet> cloudletList;
	private static List<FogDevice> fogDevices;

	public static void setCloudletList(List<? extends Cloudlet> cldLst) 
	{
		cloudletList = cldLst;
	}
	public static void setFogDevices(List<FogDevice> cldLst) 
	{
		fogDevices = cldLst;
	}

	public static void main(String[] args) {

		Log.printLine("Starting DOWOA...");

		try {

			if (args.length > 0) 
			{
				category = args[0];
				subCategory = args[1];
				noOfWhales = Integer.parseInt(args[2]);
				maxItr = Integer.parseInt(args[3]);
				wait = Integer.parseInt(args[4]);
			}
			
			double whaleStartTime = System.currentTimeMillis();

			minTime = calcMinTime(fogDevices, cloudletList);
			minCost = calcMinCost(fogDevices, cloudletList);
			minEnergy = calcMinEnergy(fogDevices, cloudletList);
			minDelay = calcMinDelay(fogDevices, cloudletList);

			maxTime = calcMaxTime(fogDevices, cloudletList);
			maxCost = calcMaxCost(fogDevices, cloudletList);
			maxEnergy = calcMaxEnergy(fogDevices, cloudletList);

			System.out.println("Lower bounds:\n" + "makespan: " + minTime + ", Cost: " + minCost + ", Energy: " + minEnergy + ", delay: " + minDelay);
			
			int itr = 0;
			double bestWhaleMakespan = 0.0;						
			double gBestWhaleFitness = 0.0; 				
			double a = 0.0;																																			// 2 (exploration) linearly decreases to 0 (exploitation). Coefficient for the encircling prey mechanism. It affects the size of the search space and the intensity of exploitation.
			double A = 0.0;																																			// -1 (clockwise spiral) to 1 (counter clockwise). Maximum distance of a whale's position update during the encircling prey mechanism. It controls the size of the search space and influences the movement of the whales in a circular or spiral pattern around a potential solution. This parameter determines the radius of the circular movement or spiral pattern.
			double C = 0.0;																																			// Linear or exponential decreasing value. Problem dependent. Coefficient for the bubble-net prey encircling mechanism. How fast the algorithm transitions from exploration to exploitation.
			double p = 0.7;		// Sinusoidal map yields the best result for "p". reference (page 16, column 2, second-third paragraphs)																	// 0(not used) to 1(always used). Probability of a whale using the bubble-net prey encircling mechanism. 
			double b = 0.0;																																			// Position vector of a whale (candidate solution). Coefficient for the spiral updating mechanism. 
			double l = 0.0;																																			// [-1, 1] Negative or Positive Step size 
			double r1 = 0.0;
			double r2 = 0.0;
			double a2 = 0.0; 																																		// a2 linearly decreases from -1 to -2 - Additional variable by Mirjallili which is not listed in the paper itself.
			double jr = 0.0;
		    
			for (int w = 0; w < noOfWhales; w++)
			{	waitForWhalePopulationMap.put(w, 0); }

			for (int w = 0; w < noOfWhales; w++)
			{	bestWhalesMap.put(w, 0.0);	}

			for (int w = 0; w < noOfWhales; w++)
			{	fitnessMap.put(w, 0.0);	}

			for (int w = 0; w < noOfWhales; w++)
			{ 	bestFitnessMap.put(w, 0.0); }

			HashMap<Cloudlet, FogDevice> innergbFMap = new HashMap<>();
			for (Cloudlet cld:cloudletList)
    		{
				Random r = new Random();
				int ranVmId = r.nextInt(fogDevices.size());
				FogDevice randVm = fogDevices.get(ranVmId); 		
				innergbFMap.put(cld, randVm);
    		}
			gBWhaleMap.put(0, innergbFMap);

			initializeWhales(cloudletList, fogDevices, bestWhalesMap, whalesPopulationMap, gBWhaleMap, noOfWhales);

			clearAndInitializeVMRTMap();
			compVMRTMap();
			
			double []fitMk =  firstCompFitnessEx();

			gBestWhaleFitness = fitMk[0];
			bestWhaleMakespan = fitMk[1];
			
			System.out.println("After intialization:\n"
							  + "----------------------------------------------------------\n"
							  + "gBestFitness: "+gBestWhaleFitness + "\n"
							  + "bestWhaleMakespan: "+bestWhaleMakespan + "\n"
			  					+ "----------------------------------------------------------\n");
			
			// add temporarily
			for (HashMap.Entry<Cloudlet, FogDevice> entry : gBWhaleMap.get(0).entrySet()) 
			{
				long cldLength = entry.getKey().getCloudletLength();
				double vmLength = entry.getValue().getCharacteristics().getMips();
		
				double exec = cldLength/vmLength;
				System.out.println("executionTime_cld:" + entry.getKey().getCloudletId() + "_on_fog:" + entry.getValue().getId() + " is: " + exec);
			}
			System.out.println("\n");
			 //end

			
			// Main OppoCWOA 
			while(itr < maxItr)		
			{
				Random rand = new Random();

				for (int w = 0; w < noOfWhales; w++)
				{
					HashMap<Cloudlet, FogDevice> tmpWhaleMap = new HashMap<>(whalesPopulationMap.get(w));		// Taking out the first whale

					if (w >= noOfWhales/wait)		// to preserve eliteWhales from undergoing any change
					{
						for (int c = 0; c < cloudletList.size(); c++)
						{
							Cloudlet cld = cloudletList.get(c);
							FogDevice VM = whalesPopulationMap.get(w).get(cld);	
							
							double dToRandVm = 0.0;
							double dToGB = 0.0;
							double dToAGB = 0.0;
							double newWhalePosition = 0.0;
							double randVm = 0.0;
							
							if (p < 0.5)		// Encircling Prey
							{
								if (Math.abs(A) >= 1)	// |A|>1 facilitates Exploration
								{
									randVm = rand.nextInt(fogDevices.size());
									dToRandVm = Math.abs(C*randVm - VM.getId());														// Equation 2.7
									newWhalePosition = randVm - A*dToRandVm;															// Equation 2.8
								}
								else if (Math.abs(A) < 1) // |A|<1 facilitates Exploitation
								{
									dToGB = Math.abs(C*gBWhaleMap.get(0).get(cld).getId() - tmpWhaleMap.get(cld).getId());  			//Equation 2.1
									newWhalePosition = gBWhaleMap.get(0).get(cld).getId() - A*dToGB;									// Equation 2.2
								}
							}
							else if (p >= 0.5)	// Spiral Bubble Net Attacking- facilitates Exploitation
							{
								dToAGB = Math.abs(gBWhaleMap.get(0).get(cld).getId() - tmpWhaleMap.get(cld).getId());					 // Calculates D'	
								newWhalePosition = dToAGB * Math.exp(b*l) * Math.cos(2*Math.PI*l) + gBWhaleMap.get(0).get(cld).getId(); // Equation 2.5
								//b = b+2; the even values of b have been tested but not good.
							}	
	
							if (newWhalePosition >= fogDevices.size() || newWhalePosition < 0)
							{
								newWhalePosition = rand.nextInt(fogDevices.size());
							}
							
							int intnewWhalePosition = (int) newWhalePosition; 
							FogDevice newRandVm = fogDevices.get(intnewWhalePosition);
							tmpWhaleMap.put(cld, newRandVm);
						} // End of Cloudlets loop
					}
					whalesPopulationMap.put(w, tmpWhaleMap);			// Update the position of the whale.

					if (waitForWhalePopulationMap.get(w) == wait && w >= noOfWhales/wait) 
					{
						HashMap<Cloudlet, FogDevice> singleOppWhaleMap = new HashMap<>(whalesPopulationMap.get(w));
				        for (int c = 0; c < cloudletList.size(); c++)
				        {
				            Cloudlet cloudlet = cloudletList.get(c);
				            FogDevice vm = singleOppWhaleMap.get(cloudlet);
				            
				            int ovmIndex = fogDevices.getFirst().getId() + fogDevices.getLast().getId() - vm.getId();
				            
				            FogDevice ovm = fogDevices.get(mapper(ovmIndex));
				            singleOppWhaleMap.put(cloudlet, ovm);
				        }

				        HashMap<Cloudlet, FogDevice> singleWhaleMap = new HashMap<>();
							
						for (HashMap.Entry<Cloudlet, FogDevice> entry1 : whalesPopulationMap.get(w).entrySet())
						{
							Cloudlet cld = entry1.getKey();
							FogDevice fd = entry1.getValue();
							
							singleWhaleMap.put(cld, fd);
						}
						
						/* New routine for dynamic opposition solution generation. It adjusts the slide each time to copy 1/6 partial opposition based solutions */
						
						int crossOverPoint1 = (cloudletList.size()/3)-1;
						int crossOverPoint2 = cloudletList.size() - (cloudletList.size()/3);
						int range = crossOverPoint2 - crossOverPoint1;
						
						int firstSlidingEdge = rand.nextInt(cloudletList.size()-range);
				
						for (int j = 0; j<cloudletList.size(); j++)
						{
								if (j >= firstSlidingEdge && range != 0)		// starting from firstSlidingEdge and keep copying until the range is expired.
								{
									Cloudlet cld = cloudletList.get(j);
									FogDevice fd = singleOppWhaleMap.get(cld);
									singleWhaleMap.put(cld, fd);
									range = range-1;
								}
						}
						whalesPopulationMap.put(w, singleWhaleMap);

				        waitForWhalePopulationMap.put(w, waitForWhalePopulationMap.get(w)-wait);
					}

					clearAndInitializeVMRTMap();		// Decisive step, otherwise the makespan will be calculated incorrectly.
					compVMRTMap();

					double tmpBestWhaleMakespan = getpbMap(VMRTMap.get(w));
					
					double [] result = calcFitness(w, tmpBestWhaleMakespan);
					double fitness = result[0];
					double cost = result[1];
					double totalEnergy = result[2];
					double totalDelay = result[3];
					double totalLoadVar = result[4];
					double totalLoadBal = result[5];
					double totalRespTime = result[6];
					
					fitnessMap.put(w, fitness);
				
					waitForWhalePopulationMap.put(w, waitForWhalePopulationMap.get(w)+1);

					if (fitness > bestFitnessMap.get(w))		// bestFitnessMap and fitnessMap are still holding the old values before sortAndSelect();
					{											// However, they will be replaced as the older values are inferior to the new computed values
						bestFitnessMap.put(w, fitness);			// based on updated VMRTMapEx in the two if clauses.
				
						waitForWhalePopulationMap.put(w, waitForWhalePopulationMap.get(w)-1);
						
						if (fitness > gBestWhaleFitness)
						{
							gBestWhaleFitness = fitness;
							System.out.println("The g-Best is: " + fitness+ "\n");
							gBWhaleMap.put(0, whalesPopulationMap.get(w));
							
							System.out.println("fitness: " + fitness + " Makespan: "+bestWhaleMakespan+", Cost: "+cost+", "+ ", totalEnergy: "+totalEnergy);
							
							bestWhaleMakespan = tmpBestWhaleMakespan;
							makespan = bestWhaleMakespan;
							costs = cost;
							energy = totalEnergy;
							FS = fitness;
							delay = totalDelay;
							loadVar = totalLoadVar;
							loadBal = totalLoadBal;
							responseT = totalRespTime;
						}
					}

					r1 = rand.nextDouble();
					a = 2.0 - (double) itr * ((2.0) / maxItr);
					A = (2.0 * a * r1) - a; 		// Eq. 2.3 - Facilitating exploitation (shrinking encircling mechanism) by decreasing the value of a.
					C = 2.0 * r2;
					p = rand.nextDouble();
					l = (rand.nextDouble() * 2.0 ) - 1.0;
					b = 1;
					
					sortAndSelect();		// only whalesPopulationMap is population with best noOfWhales items;

				}// End of Whales loop
				itr++;
			}// End of Iterations loop
			double whaleEndTime = System.currentTimeMillis();
			double executionTime = whaleEndTime-whaleStartTime;

			System.out.println("\n	The last gBestWhale is: " + FS + " Makespan: "+makespan+", Cost: "+costs+ ", Energy: " + energy+ ", Execution Time: " 
			+ executionTime + " Delay: " + delay + " LoadVar: " + loadVar + " LoadBal: " + loadBal + " ResponseT:" + responseT + "\n");

			// Executing Final Mapping of cloudlets to VMs on console			
			for (HashMap.Entry<Cloudlet, FogDevice> entry : gBWhaleMap.get(0).entrySet()) 
			{
				long cldLength = entry.getKey().getCloudletTotalLength();
				double vmLength = entry.getValue().getCharacteristics().getMips();

				double exec = cldLength/vmLength;
//				System.out.println("executionTime_" + entry.getKey().getCloudletId() + "_on_" + entry.getValue().getId() + " is: " + exec);
				System.out.println("executionTime_" + entry.getKey().getCloudletId() + "_on_" + entry.getValue().getName() + " is: " + exec);

			}

	    	try
	    	{
//	    		String fileToWrite = Utility.fileToWrite("DOWOA", category, subCategory, noOfWhales, maxItr);
	    		String fileToWrite = Utility.fileToWrite("DOWOA", category, subCategory, noOfWhales, wait); // Changed for analyzing the different values of wait threshold.

	    		FileWriter fWriter = new FileWriter(fileToWrite,true);
	    		BufferedWriter bWriter = new BufferedWriter(fWriter);
	    		bWriter.write(FS + ", "+ makespan + ", "+ costs+ ", "+ energy+", "+ executionTime +", "+ cloudletList.size()/makespan +
						", " + delay + ", " + loadVar + ", " + loadBal + ", " + responseT + "\n");
	    		bWriter.close();
	    		fWriter.close();
	    	}
	    	catch(IOException e)
	    	{
	    		System.out.println("An error occurred while writing to the file: " + e.getMessage());
	    	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static HashMap<Integer, Double> initializeWhales(	// Initializing every three agents as random, OBL, and Quasi-OBL based whale. 
			List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices,
	HashMap<Integer, Double> bestWhalesMap, 
	HashMap<Integer, HashMap<Cloudlet, FogDevice>> whalesPopulationMap, 
	HashMap<Integer, HashMap <Cloudlet, FogDevice>> gBWhaleMap, 
	int noOfWhales)
	{	
		Random random = new Random();
		
		for (int w = 0; w < noOfWhales; w=w+3)
		{
			HashMap<Cloudlet, FogDevice> singleWhaleMap = new HashMap<>();
			HashMap<Cloudlet, FogDevice> singleOppWhaleMap = new HashMap<>();
			HashMap<Cloudlet, FogDevice> singleQuasiOppWhaleMap = new HashMap<>();

	        for (int c = 0; c < cloudletList.size(); c++) 		// For each particle, there are "cloudletList.size" no of entries in the particleMap.
	        {
	            int vmIndex = random.nextInt(fogDevices.size());
	            int ovmIndex = Math.abs((0+(fogDevices.size()-1))-vmIndex);
	            
	            FogDevice vm = fogDevices.get(vmIndex);
	            FogDevice ovm = fogDevices.get(ovmIndex);
	            
	            int qOVmIndex = fogDevices.getFirst().getId() + fogDevices.getLast().getId() - vm.getId();
	            int center = (fogDevices.getFirst().getId() + fogDevices.getLast().getId())/2;
	            int aPlusB = fogDevices.getFirst().getId() + fogDevices.getLast().getId();
	            
	            if (vm.getId() > center)						// x > center
	            {
	            	int min = aPlusB-vm.getId();	// a+b-x
	            	int max = center;

	            	qOVmIndex = random.nextInt(max-min+1)+min;
	            }
	            else if (vm.getId() < center)					// x < center
	            {
	            	int min = center;
	            	int max = aPlusB-vm.getId();	// a+b-x

	            	qOVmIndex = random.nextInt(max-min+1)+min;
	            }
	            else 											// x = center
	            {
	            	qOVmIndex = center; 			// Do nothing
	            }
	            
	            Cloudlet cloudlet = cloudletList.get(c);
	            
	            FogDevice qOVm = fogDevices.get(mapper(qOVmIndex));
	            
	            singleWhaleMap.put(cloudlet, vm);
	            singleOppWhaleMap.put(cloudlet, ovm);
	            singleQuasiOppWhaleMap.put(cloudlet, qOVm);
	        }
	        whalesPopulationMap.put(w, singleWhaleMap);
	        whalesPopulationMap.put(w+1, singleWhaleMap);
	        whalesPopulationMap.put(w+2, singleQuasiOppWhaleMap);
	   }

		return bestWhalesMap;
	}

	public static int mapper(int originalValue) 	// returns in terms of 0 to max
	{
		int originalMin = fogDevices.get(0).getId();						// previously stored 3
		int originalMax = fogDevices.get(fogDevices.size()-1).getId();		// previously stored 14

		int newMin = 0;
		int newMax = fogDevices.size()-1;									// previously stored 11
		
		int mappedValue = ((originalValue-originalMin) * (newMax - newMin)/(originalMax-originalMin)) + newMin;

		return mappedValue;
	}

	public static int deMapper(int originalValue) 
	{
		return originalValue+3;
	}

	private static void sortAndSelect() 
	{
//				new Id	  oldId, fitness
		HashMap<Integer, Double[]> sortedBestFitnessMap = sortByValue(bestFitnessMap);			// sorted fitness map having both new and old keys
		
		for (int i = 0; i < sortedBestFitnessMap.size(); i++)
		{
			Double [] v = sortedBestFitnessMap.get(i);
//			System.out.println("New Key: "+ i + ", Old Key: " + v[0] + " Value: " + v[1]);
		}
		
		HashMap<Integer, HashMap<Cloudlet, FogDevice>> backupWhalesPopulationMap = new HashMap<>();	// to take backup of whalesPopulationMap
		HashMap<Integer, Double> backupBestFitnessMap = new HashMap<>();								// to take backup of bestFitnessMap
		
		for (int i = 0; i < noOfWhales; i++)															// selecting the best "noOfWhales" agents
		{
			Double [] v = sortedBestFitnessMap.get(i);
			int oldP = (int) Math.round(v[0]);
			backupWhalesPopulationMap.put(i, whalesPopulationMap.get(oldP));
			backupBestFitnessMap.put(i, v[1]);
		}

		whalesPopulationMap.clear();
		
		for (int i = 0; i < noOfWhales; i++)													// Updating whalesPopulationMap 
		{
			whalesPopulationMap.put(i, backupWhalesPopulationMap.get(i));
		}
	}

	private static double[] firstCompFitnessEx() 
	{
		for (int w = 0; w < bestWhalesMap.size(); w++) 
		{
			double tmpBestWhaleMakespan = bestWhalesMap.get(w);
			double [] result = calcFitness(w, tmpBestWhaleMakespan);
			double fitness = result[0];
	//		double cost = result[1];			// Not needed at this stage
	//		double totalEnergy = result[2];		// Not needed at this stage
	//		double totalDelay = result[3];		// Not needed at this stage
	//		double totalLoadVar = result[4];	// Not needed at this stage
	//		double totalLoadBal = result[5];	// Not needed at this stage
	//		double totalRespTime = result[6];	// Not needed at this stage
			bestFitnessMap.put(w, fitness);
		}

		double fit = 0;
		int index = 0;
		
		for (HashMap.Entry<Integer, Double> entry : bestFitnessMap.entrySet()) 
		{
			int whale = entry.getKey();
			double fitness = entry.getValue();
	
			if (fitness > fit)
			{
				fit = fitness;
				index = whale;
			}
		}		
	
		gBWhaleMap.put(0, whalesPopulationMap.get(index));
		
		makespan = bestWhalesMap.get(index);
		
		double [] result = calcFitness(index, makespan);
		FS = result[0];
		costs = result[1];	
		energy = result[2];		
		delay = result[3];		
		loadVar = result[4];	
		loadBal = result[5];	
		responseT = result[6];	

		double[] fitMk = {0,0,0};
		
		fitMk[0] = FS;
		fitMk[1] = makespan;

		return fitMk;
	}

	public static HashMap<Integer, Double[]> sortByValue(HashMap<Integer, Double> probab)
	{
		List<Map.Entry<Integer, Double> > list = new LinkedList<Map.Entry<Integer, Double> >(probab.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() 
		{
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Collections.reverse(list);		// list is sorted in descending order

		int i = 0;
		// put data from sorted list to hashmap 
		HashMap<Integer, Double[]> temp = new LinkedHashMap<Integer, Double[]>();
		for (Map.Entry<Integer, Double> entry : list) 
		{
			Double[] v = {(double)entry.getKey(), entry.getValue()};
			temp.put(i, v);
			i++;
		}
		
		return temp;
	}

	private static double[] calcFitness(int w, double tmpBestWhaleMakespan)
	{
			for (int i = 0; i < fogDevices.size(); i++) // clearing the assigned cloudlets on all fogDevices
			{
				FogDevice fd = fogDevices.get(i);
				fd.getCloudletListAssignment().clear();
			}

			/***************************** Cost **************************/

			double cost = 0;
			HashMap<Cloudlet, FogDevice> tmpWhaleMap = new HashMap<>(whalesPopulationMap.get(w));
			
			for (HashMap.Entry<Cloudlet, FogDevice> entry : tmpWhaleMap.entrySet()) 
			{
				Cloudlet cld = entry.getKey(); 		// unique
				FogDevice fd = entry.getValue(); 	// can repeat
				
				// cost includes the processing cost = cost/sec * processing time
				cost = cost + fd.getCharacteristics().getCostPerSecond() * cld.getCloudletLength()/ fd.getHost().getTotalMips();
				// cost includes the memory cost
				cost = cost + fd.getCharacteristics().getCostPerMem() * cld.getMemRequired();
				// cost includes the bandwidth cost
				cost = cost + fd.getCharacteristics().getCostPerBw() * (cld.getCloudletFileSize() + cld.getCloudletOutputSize());
				
				fd.getCloudletListAssignment().add(cld);	// assigning cloudlets to each fog node
				tmpWhaleMap.put(cld, fd);
			}
			whalesPopulationMap.put(w, tmpWhaleMap);

			/***************************** Delay and Load Variance **************************/
			
			double QD = 0;
			double ET = 0;
			double TD = 0;
			double PD = 0;		// Assumed negligible in objective-3

			double sumOfLoads = 0;
			HashMap<Integer, Double> loads = new HashMap<>();
			
			for (FogDevice fogDevice : fogDevices) 
			{
				double sumOfData = 0;
				double previousLoad = 0;
				double avgQD = 0;
				double avgET = 0;
				double totalLength = 0;
				double singleLoad = 0;
				
				for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment())
				{
					sumOfData = sumOfData + (cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize());
					totalLength = totalLength + cloudlet.getCloudletLength();
				}
				
				int NoOfTasks = 0;
				for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment())
				{
					double singleTaskDelay = cloudlet.getCloudletLength()/fogDevice.getCharacteristics().getMips();
					avgQD = avgQD + previousLoad;
					avgET = avgET+singleTaskDelay;
					
					previousLoad = previousLoad + singleTaskDelay;
					NoOfTasks++;
				}
				
				if (NoOfTasks == 0) // if any fogDevice has zero tasks allocated to it
				{
					QD = QD + 0;
					ET = ET + 0;
					TD = TD + 0; 	// bW = 1024 MB/s
				}
				else 
				{
					QD = QD + (avgQD/NoOfTasks);
					ET = ET + (avgET/NoOfTasks);
					TD = TD + (sumOfData/1024); 	// bW = 1024 MB/s
				}
				
				singleLoad = totalLength/(fogDevice.getCharacteristics().getMips()+1024);
				sumOfLoads = sumOfLoads + singleLoad;
				
				loads.put(fogDevice.getCharacteristics().getId(), singleLoad);
			}
			
			QD = QD/fogDevices.size();
			ET = ET/fogDevices.size();
			TD = TD/fogDevices.size();
			
			double delay = QD + ET + TD + PD;		// average delay for each task on 32 vms.
			
			double sumOfDif = 0;
			for (HashMap.Entry<Integer, Double> entry : loads.entrySet()) 
			{
				double load = entry.getValue();
				double dif = sumOfLoads - load;		// if we swap the values, then we need to use the Math.Abs() function
					
				sumOfDif = sumOfDif + dif;
			}
			
			double loadVar = Math.sqrt(sumOfDif/fogDevices.size());
			
			double avgLB = sumOfLoads/fogDevices.size();
			
			/***************************** Response Time *****************************/
			double avgRT = QD;
			
			/***************************** Energy **************************/

			double totalEnergy = 0;
			for (FogDevice fogDevice : fogDevices) 
			{
				double totalLength = 0;
				
				for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment()) // Only the assigned cloudlets to a fogDevice are considered 
				{
					totalLength = totalLength + cloudlet.getCloudletLength();
				}
				
				//			   				Active EC in watts/sec   *	Active Time Duration
				totalEnergy = totalEnergy + fogDevice.getBusyPower() * (totalLength/fogDevice.getCharacteristics().getMips()) 
										  + fogDevice.getIdlePower() * (tmpBestWhaleMakespan - (totalLength/fogDevice.getCharacteristics().getMips())); 
				//			 				 Idle EC in watts/sec	 * 		Idle Time Duration 
		
				double SingleNodeCommEn = 0;
				
				for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment()) // Only the assigned cloudlets to a fogDevice are considered 
				{										// During communication, comEC is consumed per second by a node having bW = 1024 Mbps
														// The sum of file size and output size is divided by the bW
					SingleNodeCommEn = SingleNodeCommEn + (((cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize()) /1024) * fogDevice.getComEC());
				}
				
				totalEnergy = totalEnergy + SingleNodeCommEn;
			}
			totalEnergy = totalEnergy*0.001;	// 1 Watt = 0.001 kj per second

		double [] fitness = {0,0,0,0,0,0,0};		// 0 index storing fitness while the 1 index stores energy

		fitness[0] = Utility.fitAgent(minTime, tmpBestWhaleMakespan, minCost, cost, minEnergy, totalEnergy, minDelay, delay);

		fitness[1] = cost;
		fitness[2] = totalEnergy;
		fitness[3] = delay;
		fitness[4] = loadVar;
		fitness[5] = avgLB;
		fitness[6] = avgRT;
		
		return fitness;
	}

	private static double calcMinTime(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) 
	{
		double minTime = 0;
		double totalLength = 0;
		double totalMips = 0;
		
		for (Cloudlet cloudlet : cloudletList) 
		{
			totalLength = totalLength + cloudlet.getCloudletLength();
		}
		
		for (FogDevice fogDevice : fogDevices) 
		{
			totalMips = totalMips + fogDevice.getCharacteristics().getMips();
		}
		
		minTime = totalLength / totalMips;
		
		return minTime;
	}

	// the function calculate the LOWER BOUND of the solution about cost
	private static double calcMinCost(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList)
	{
		double minCost = 0;																			
	
		for (Cloudlet cloudlet : cloudletList) 
		{
			double minCloudletCost = Double.MAX_VALUE;
		
			for (FogDevice fogDevice : fogDevices) 
			{
				double cost = calcCost(cloudlet, fogDevice);
				if (cost < minCloudletCost)
				{
					minCloudletCost = cost;
				}
			}
			// the minCost is defined as the sum of all minCloudletCost
			minCost = minCost + minCloudletCost;
		}
		return minCost;
	}

// the method calculates the cost (G$) of single cloudlet on a fogDevice
	private static double calcCost(Cloudlet cloudlet, FogDevice fogDevice) 
	{
		double cost = 0;
		// cost includes the processing cost = cost/sec * processing time
		cost = cost + fogDevice.getCharacteristics().getCostPerSecond() * (cloudlet.getCloudletLength()/ (double) fogDevice.getCharacteristics().getMips());
		// cost includes the memory cost
		cost = cost + fogDevice.getCharacteristics().getCostPerMem() * cloudlet.getMemRequired();
		// cost includes the bandwidth cost
		cost = cost + fogDevice.getCharacteristics().getCostPerBw() * (cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize());

		return cost;
	}


	// the function calculate the LOWER BOUND of the solution about cost
	private static double calcMinEnergy(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList)
	{
		double minEnergy = 0;																			
	
		for (Cloudlet cloudlet : cloudletList) 
		{
			double minCloudletEnergy = Double.MAX_VALUE;
		
			for (FogDevice fogDevice : fogDevices) 
			{
				double energy = calcEnergy(cloudlet, fogDevice);
				if (energy < minCloudletEnergy)
				{
					minCloudletEnergy = energy;
					System.out.println("Fog Device: " +
					fogDevice.getName() + ", MIPS: " + fogDevice.getCharacteristics().getMips() + ", MEC: "+ energy+ 
					" for cloudlet: "+ cloudlet.getCloudletId() + ", Length: " + cloudlet.getCloudletLength() );
				}
			}
			System.out.println("\n");

			// the minEnergy stores the sum of all cloudlets minimum energy consumption
			minEnergy = minEnergy + minCloudletEnergy;
		}
		return minEnergy;	
	}
	
	private static double calcEnergy(Cloudlet cloudlet, FogDevice fogDevice) 
	{
		double energy =  fogDevice.getBusyPower() * (cloudlet.getCloudletLength()/ fogDevice.getCharacteristics().getMips());

		energy = energy + (((cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize()) /  1024)   *fogDevice.getComEC());

		energy = energy*0.001;
		
		return energy;
	}

	private static double calcMinDelay(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) 
	{	// Assuming queuing delay = 0
		
		double minDelay = 0;
		double totalLength = 0;
		double totalMips = 0;
		double totalFileSizeOutput = 0;
		
		for (Cloudlet cloudlet : cloudletList) 
		{
			totalLength = totalLength + cloudlet.getCloudletLength();
			totalFileSizeOutput = totalFileSizeOutput + (cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize());
		}
		
		for (FogDevice fogDevice : fogDevices) 
		{
			totalMips = totalMips + fogDevice.getCharacteristics().getMips();
		}
		
		minDelay = (totalLength/totalMips) + (totalFileSizeOutput/(1024*31));		// bW = 1024 and number of vms besides gateway = 31
		minDelay = minDelay/fogDevices.size();										// to treat minDelay as an average delay for all nodes.
		
		return minDelay;
	}
	
	// the function calculate the UPPER BOUND of the solution about makespan
	private static double calcMaxTime(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList) 
	{
		double maxTime = 0;
		double totalLength = 0;
		
		for (Cloudlet cloudlet : cloudletList) 
		{
			totalLength = totalLength + cloudlet.getCloudletLength();
		}
		
		FogDevice weakestFogDevice = fogDevices.get(0);		// fog00 is the weakest fog node among all
		maxTime = totalLength / weakestFogDevice.getCharacteristics().getMips();	// running all tasks on the weakest fog node
		
		return maxTime;
	}

	// the function calculate the UPPER BOUND of the solution about cost
	private static double calcMaxCost(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList)
	{
		double maxCost = 0;																			
	
		for (Cloudlet cloudlet : cloudletList) 
		{
			double maxCloudletCost = Double.MIN_VALUE;
		
			for (FogDevice fogDevice : fogDevices) 
			{
				double cost = calcCost(cloudlet, fogDevice);
				if (cost > maxCloudletCost)
				{
					maxCloudletCost = cost;
				}
			}
			// the maxCost is defined as the sum of all maxCloudletCost
			maxCost = maxCost + maxCloudletCost;
		}
		return maxCost;
	}

	private static double calcMaxEnergy2(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList)
	{
		/* This function is assigning all tasks to all fog nodes and then the energy consumption is calculated as upper bound. The other function is more 
		 * logical as compared to this one.
		 */
		double totalLength = 0;
		double totalEnergyInActiveTime = 0;
		double totalEnergy = 0;
		
		for (Cloudlet cloudlet : cloudletList) 
		{
			totalLength = totalLength + cloudlet.getCloudletLength();
		}

		for (int i = 0; i < fogDevices.size(); i++)
		{
			FogDevice fNode = fogDevices.get(i);
			
			totalEnergyInActiveTime = fNode.getBusyPower() * (totalLength/ fNode.getCharacteristics().getMips());

			totalEnergy = totalEnergy + totalEnergyInActiveTime;
		
			double SingleCloudCommEn = 0;
			
			for (Cloudlet cloudlet : cloudletList)  // All cloudlets will run on every device  
			{										/*During communication, 100 watts is consumed per second by a cloud node having bW = 500 Mbps
														The sum of file size and output size is divided by the bW */
				SingleCloudCommEn = SingleCloudCommEn + (((cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize()) /  1024) * fNode.getComEC());
			}
			
			totalEnergy = totalEnergy + SingleCloudCommEn;
		}

		double idleEnergyAllFogNodes = 0;
		
		for (FogDevice fd : fogDevices)			// calculating the idle energy of all nodes in addition to active time
		{
			double activeTime = totalLength/ fd.getCharacteristics().getMips();
		
			double idleTimeforAllFogDevices = activeTime; // suppose both times are same to get the upper bound
			
			idleEnergyAllFogNodes = idleEnergyAllFogNodes + fd.getIdlePower() * idleTimeforAllFogDevices;
		}

		totalEnergy = totalEnergy + idleEnergyAllFogNodes;
		
		totalEnergy = totalEnergy*0.001;
		
		return totalEnergy;	
	}

	private static double calcMaxEnergy(List<FogDevice> fogDevices, List<? extends Cloudlet> cloudletList)
	{
		/*This function is more logical as all the tasks are run on fog00 to get the energy consumption of the entire system. While in the second function, we 
		are assigning all the tasks to all the fog nodes and then the energy consumption is calculated which is illogical*/

		double allCloudletsLength = 0;
		
		for (Cloudlet cloudlet : cloudletList) 
		{
			allCloudletsLength = allCloudletsLength + cloudlet.getCloudletLength();
		}

		double mk = allCloudletsLength /fogDevices.get(0).getCharacteristics().getMips();	 	// running all tasks on the slowest fog node to get the upper bound for 
																								// energy consumption
		for (int i = 0; i < 1; i++) 
		{
			FogDevice fd = fogDevices.get(i);
			
			for (Cloudlet cloudlet : cloudletList) 
			{
				fd.getCloudletListAssignment().add(cloudlet);
			}
		}		

		double totalEnergy = 0;
		for (FogDevice fogDevice : fogDevices) 
		{
			double totalLength = 0;
			
			for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment()) // Only the assigned cloudlets to a fogDevice are considered 
			{
				totalLength = totalLength + cloudlet.getCloudletLength();
			}
			
			//			   				Active EC in watts/sec   *	Active Time Duration
			totalEnergy = totalEnergy + fogDevice.getBusyPower() * (totalLength/fogDevice.getCharacteristics().getMips()) 
									  + fogDevice.getIdlePower() * (mk - (totalLength/fogDevice.getCharacteristics().getMips())); 
			//			 				 Idle EC in watts/sec	 * 		Idle Time Duration 
	
			double SingleNodeCommEn = 0;
			
			for (Cloudlet cloudlet : fogDevice.getCloudletListAssignment()) // Only the assigned cloudlets to a fogDevice are considered 
			{								/* During communication, comEC is consumed per second by a node having bW = 1024 Mbps */
																			/* The sum of file size and output size is divided by the bW */
				SingleNodeCommEn = SingleNodeCommEn + (((cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize()) /  1024)   * fogDevice.getComEC());
			}
			totalEnergy = totalEnergy + SingleNodeCommEn;
		}
		totalEnergy = totalEnergy*0.001;	// 1 Watt = 0.001 kj per second
		
		return totalEnergy;	
	}
	
	private static void clearAndInitializeVMRTMap()
	{
		VMRTMap.clear();
		for (int w = 0; w < noOfWhales; w++)
		{
			HashMap<Integer, Double> innerVMRTMap = new HashMap<>();
			for (FogDevice vm:fogDevices)
			{
				innerVMRTMap.put(vm.getId(), 0.0);
			}
			VMRTMap.put(w, innerVMRTMap);
		}
	}	

	/* In the first 1/3 iterations, OBL is performed. In the second half, partial OBL is computed while in the last half, quasi OBL is conducted */

	private static void addOBLToWhalesPopulationMap(HashMap<Integer, HashMap<Cloudlet, FogDevice>> OBLMap)
	{
		int j = noOfWhales;
		for (int i = 0; i < noOfWhales; i++) 
		{
			HashMap<Cloudlet, FogDevice> singleWhaleMap = OBLMap.get(i);	
			whalesPopulationMap.put(j, singleWhaleMap); 
			j++;
		}

	}	
	private static void compVMRTMap()
	{
		// Populating VMRTMapEx to store vm busy times for all whales.
		for (int w = 0; w < noOfWhales; w++)
		{
			HashMap<Cloudlet, FogDevice> singleWhaleMap = whalesPopulationMap.get(w);
	        
			for (HashMap.Entry<Cloudlet, FogDevice> entry : singleWhaleMap.entrySet())
	        {
	        	Cloudlet cloudlet = entry.getKey();
	            FogDevice vm = entry.getValue();
	            
				double eTime =  cloudlet.getCloudletLength() / (double) vm.getHostList().get(0).getTotalMips();
				
				int vmId = vm.getId();
				
				if (VMRTMap.get(w).containsKey(vmId))
				{
					double prevLoad = VMRTMap.get(w).get(vmId);
					eTime = eTime + prevLoad;
					VMRTMap.get(w).put(vmId, eTime);
				}
				else
				{	VMRTMap.get(w).put(vmId, eTime);	}
	        }
	        bestWhalesMap.put(w, getpbMap(VMRTMap.get(w)));
		}
	}
	

	private static double getGBest(HashMap<Integer, Double> bestWhalesMap) {
	    double minParticleMakespan = bestWhalesMap.get(0);			// Selecting the particle having the minimum makespan.
		
	    for(int i=0; i < bestWhalesMap.size(); i++)
		{
			if (bestWhalesMap.get(i) < minParticleMakespan)
			{
			    minParticleMakespan = bestWhalesMap.get(i);
			}
		}
		return minParticleMakespan;
	}

	private static int getGBestIndex(HashMap<Integer, Double> bestWhalesMap) {
    double minParticleMakespan = Double.MAX_VALUE;			// Selecting the particle' index having the minimum makespan.
	int index = 0;
	
    for(int i=0; i < bestWhalesMap.size(); i++)
	{
		if (bestWhalesMap.get(i) < minParticleMakespan)
		{
		    minParticleMakespan = bestWhalesMap.get(i);
		    index = i;
		}
	}
    System.out.println("The gBest index is: "+index + " with makespan: "+minParticleMakespan);
	return index;
}
	
	private static double getVMReadyTime(HashMap<Integer, Double> vmRemMap, FogDevice VM) 
	{
		int vmid = VM.getId();
		double vmReadyT = 0.0;
		
		if (vmRemMap.get(vmid) != null)
		{
			vmReadyT = vmRemMap.get(vmid);
		}
		return vmReadyT;
	}	
	
	private static void updateUpVMRTMap(HashMap<Integer, Double> vmRemMap, FogDevice VM, double execTime) 
	{
//		System.out.println("Adding " + execTime + " to "+ VM.getId());
		int vmid = VM.getId();
		double prevLoad = 0.0;
		double newLoad = execTime;
		
		if (vmRemMap.get(vmid) == null)
		{
			vmRemMap.put(vmid, newLoad);
		}
		else
		{
			prevLoad = vmRemMap.get(vmid);
			double totalLoad = prevLoad + newLoad;
			vmRemMap.put(vmid, totalLoad);
		}
	}
	
	private static void updateDownVMRTMap(HashMap<Integer, Double> vmRemMap, FogDevice VM, double execTime) 
	{
//		System.out.println("Subtracting " + execTime + " from "+ VM.getId()+"\n");
		int vmid = VM.getId();
		double prevLoad = 0.0;
		
		
		if (vmRemMap.get(vmid) == null)
		{
			vmRemMap.put(vmid, prevLoad);
		}
		else
		{
			prevLoad = vmRemMap.get(vmid);
			double totalLoad = prevLoad - execTime;
			vmRemMap.put(vmid, totalLoad);
		}
	}
	
	private static void updatepMap(HashMap<Integer, Double> vmRemMap,
	  HashMap<Cloudlet, FogDevice> VmMap, double pos, int c) 
	  { 
		  int ranPos = (int) pos; 
		  FogDevice newRandVm = fogDevices.get(ranPos); 
		  Cloudlet cloudlet = cloudletList.get(c);
		  VmMap.put(cloudlet, newRandVm); 
	  }
	 	
	private static double getpbMap(HashMap<Integer, Double> VMRTMap) 	// Returns the busiest VM -- It is also a fitness function
	{
		double busyVm = 0.0;
		
		for (HashMap.Entry<Integer, Double> entry : VMRTMap.entrySet()) 
		{
			double vmRt = entry.getValue();
			
			if (vmRt > busyVm)
	        {
				busyVm = vmRt; 
	        }
	    }
//		System.out.println("The bestValue of whale is: "+ busyVm);
		return busyVm;
	}	

	/* WOA Functions Definitions End*/

	private static double avgRU(double mk) 	// Average Resource Utilization according to OG-RADL eq.(10)
	{
		double ARU = 0.0, avgMakespan = 0.0, sum = 0.0;
		for(FogDevice vm:fogDevices)
		  {
			double singleVmMakespan = 0.0;
			for (int i = 0; i<cloudletList.size(); i++)
			  {
				  if (cloudletList.get(i).getVmId() == vm.getId())
				  {
					  if(cloudletList.get(i).getFinishTime() > singleVmMakespan)
					  {
						  singleVmMakespan = cloudletList.get(i).getFinishTime(); // returns the lastly executed cloudlet by the vm
					  }
				  }				  
			  }
			  sum = sum + singleVmMakespan; // summing up the finish-times of last cloudlet's execution by all vms
		  }
		avgMakespan = sum/fogDevices.size();
		ARU = avgMakespan/mk;
		return ARU;
	}	

	private static double avgRespT() 	// Average Response Time of All Tasks according to OG-RADL eq.(13)
	{
		double avgRTAllVms = 0.0, sumAvgRTSingleVms = 0;
		for(FogDevice vm:fogDevices)
		{
			double avgRTSingleVm = 0;
			int count = 0;
			for(int i=0; i<cloudletList.size(); i++)
			{
				if (cloudletList.get(i).getVmId() == vm.getId())
				{
					avgRTSingleVm = avgRTSingleVm + cloudletList.get(i).getExecStartTime();    
					count++;
				}
			}
			if (avgRTSingleVm!=0 || count!=0)
			{
				avgRTSingleVm = avgRTSingleVm/count;
			}
			sumAvgRTSingleVms = sumAvgRTSingleVms + avgRTSingleVm;
		}
		avgRTAllVms = sumAvgRTSingleVms/fogDevices.size();
		return avgRTAllVms;
	}	

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}
