package org.fog.test.perfeval;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.Application;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.placement.Controller;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class fogScheduling{

		private static String category = "HCSP";
		private static String subCategory = "i-lohi";
		private static int noOfAgents = 5;
		private static int maxItr = 5;
		private static int wait = 10;
		

		static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
        static FogDevice smartGateway;
        static List<Cloudlet> listCloudlet = new ArrayList<Cloudlet>();
        private static final String COMMA_DELIMITER = ",";
        public static String fileName = "infrastructure/i-lohi";
        public static int number_cloudlet= 100;
        public static String algorithm = null;
        public static String filename_cloudlet;
    	static List<Sensor> sensors = new ArrayList<Sensor>();
    	static List<Actuator> actuators = new ArrayList<Actuator>();

        public static void main(String[] args) {
 
                Log.printLine("Starting scheduling simulation...");

                try {
                	
	        			if (args.length > 0) 
	        			{
	        				if(args[0] == "oppoCWOA")
	        				{
		        				algorithm = "oppoCWOA"; 
	        				}
	        				else if(args[0] == "SACO")
	        				{
		        				algorithm = "SACO"; 
	        				}
	        				else if(args[0] == "PSOGA")
	        				{
		        				algorithm = "PSOGA"; 
	        				}
	        				else if(args[0] == "IPSO")
	        				{
		        				algorithm = "IPSO"; 
	        				}
	        			}

	        			category = args[1];
	        			
	        			if (category == "HCSP")
	        			{
	        		    	switch (args[2])		// args[2] is a subcategory
	        		    	{
	        		    	case "100": 
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "100"; break;
	        		    	case "200":
	        		    		fileName = "infrastructure/i-hilo"; subCategory = "200"; break;
	        		    	case "300":
	        		    		fileName = "infrastructure/s-lohi"; subCategory = "300"; break;
	        		    	case "400":
	        		    		fileName = "infrastructure/s-hilo"; subCategory = "400"; break;
	        		    	case "500":
	        		    		fileName = "infrastructure/c-lohi"; subCategory = "500"; break;
	        		    	case "600":
	        		    		fileName = "infrastructure/c-hilo"; subCategory = "600"; break;
	        		    	case "3":
	        		    		fileName = "infrastructure/i-3"; subCategory = "3"; break;
	        		    	}
	        		    }
	        			else if (category == "HPC2N" || category == "Random" || category == "NASA")
	        			{
	        		    	switch (args[2])		// args[2] is a subcategory
	        		    	{
	        		    	case "100": 
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "100"; break;
	        		    	case "200":
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "200"; break;
	        		    	case "300":
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "300"; break;
	        		    	case "400":
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "400"; break;
	        		    	case "500":
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "500"; break;
	        		    	case "600":
	        		    		fileName = "infrastructure/i-lohi"; subCategory = "600"; break;
	        		    	case "3":
	        		    		fileName = "infrastructure/i-3"; subCategory = "3"; break;
	        		    	}
	        			}

	        			number_cloudlet = Integer.parseInt(args[2]); 		// it is a sub-category
    					noOfAgents = Integer.parseInt(args[3]);
    					maxItr = Integer.parseInt(args[4]);
    					wait = Integer.parseInt(args[5]);
        				
	        			filename_cloudlet = "workloads/" + category + "/data" + number_cloudlet;

                        Log.disable();
                        int num_user = 1; // number of cloud users
                        Calendar calendar = Calendar.getInstance();
                        boolean trace_flag = false; // mean trace events

                        CloudSim.init(num_user, calendar, trace_flag);

                        String appId = "scheduler"; // identifier of the application 2413793103448276

                        FogBroker broker = new FogBroker("broker");
                        
                        Application application = createApplication(appId, broker.getId());
                        application.setUserId(broker.getId());

                        // initiate the fog-cloud devices list from json file
                        fogDevices = createFogDevices(broker.getId(), appId);
                        
                        broker.setFogDevices(fogDevices);

                        // initiate the cloudlet list - bag of tasks from file
                        listCloudlet = createCloudlet(filename_cloudlet);
                        broker.setCloudletList(listCloudlet);

                        // set up the scheduling algorithm to run cloudlet in fog-cloud infrucstructure
                        broker.assignCloudlet(category, subCategory, listCloudlet, fogDevices, algorithm, noOfAgents, maxItr, wait);
//                      broker.assignCloudletloop(algorithm, filename_ouput);

                } catch (Exception e) {
                        e.printStackTrace();
                        Log.printLine("Unwanted errors happen");
                }
        }

		/**
         * Creates the fog devices in the physical topology of the simulation.
         *
         * @param userId
         * @param appId
         */
        private static List<FogDevice> createFogDevices(int userId, String appId) 
        {
                return jsonToInfrastructure(fileName);
        }

        // read file to convert JSON object to Fog device object
        public static List<FogDevice> jsonToInfrastructure(String fileName) {

                List<FogDevice> fogDevices = new ArrayList<FogDevice>();

                try {
                        JSONObject doc = (JSONObject) JSONValue.parse(new FileReader(fileName));
                        JSONArray nodes = (JSONArray) doc.get("nodes");
                        @SuppressWarnings("unchecked")
                        Iterator<JSONObject> iter = nodes.iterator();
                        while (iter.hasNext()) {
                                JSONObject node = iter.next();
                                String nodeType = (String) node.get("type");
                                String nodeName = (String) node.get("name");

                                if (nodeType.equals("FOG_DEVICE")) 
                                {
                                        long mips = (Long) node.get("mips");
                                        int ram = new BigDecimal((Long) node.get("ram")).intValueExact();
                                        long upBw = new BigDecimal((Long) node.get("upBw")).intValueExact();
                                        long downBw = new BigDecimal((Long) node.get("downBw")).intValueExact();
                                        int level = new BigDecimal((Long) node.get("level")).intValue();
                                        double rate = new BigDecimal((Double) node.get("ratePerMips")).doubleValue();
                                        double costPerSec = (Double) node.get("costPerSec");
                                        double costPerMem = new BigDecimal((Double) node.get("costPerMem")).doubleValue();
                                        double costPerBw = new BigDecimal((Double) node.get("costPerBw")).doubleValue();

                                        /***** Added ******/
                                        double activeEC = new BigDecimal((Double) node.get("activeEC")).doubleValue();
                                        double idleEC = new BigDecimal((Double) node.get("idleEC")).doubleValue();
                                        double comEC = new BigDecimal((Double) node.get("comEC")).doubleValue();
                                        FogDevice fogDevice = createFogDevice(nodeName, mips, ram, upBw, downBw, level, rate, costPerSec, costPerMem, 
        										costPerBw, activeEC, idleEC, comEC);
                                        /***** end *********/
                                        
                                        fogDevices.add(fogDevice);
                                }
                        }
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }

                System.out.println("* Infrastructure Completed *");
                System.out.println("--------------------------------");
                System.out.println("No.of fog devices are: "+fogDevices.size());
                return fogDevices;
        }

        /**
         * Creates a vanilla fog device
         *
         * @param nodeName
         *            name of the device to be used in simulation
         * @param mips
         *            MIPS
         * @param ram
         *            RAM
         * @param upBw
         *            uplink bandwidth
         * @param downBw
         *            downlink bandwidth
         * @param level
         *            hierarchy level of the device
         * @param ratePerMips
         *            cost rate per MIPS used
         * @param costPerSec
         *                        the cost of using processing in this resource
         * @param costPerMem
         *                        the cost of using memory in this resource
         * @param costPerBw
         *                        the cost of using bw in this resource
         * @param busyPower
         * @param idlePower
         * @return
         */
        private static FogDevice createFogDevice(String nodeName, long mips, int ram, long upBw, long downBw, int level,
                        double ratePerMips,double costPerSec, double costPerMem, double costPerBw, double busyPower, double idlePower, double comCost) {

                List<Pe> peList = new ArrayList<Pe>();

                // 3. Create PEs and add these into a list.
                peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

                int hostId = FogUtils.generateEntityId();
                long storage = 1000000; // host storage
                int bw = 10000;

                PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram), new BwProvisionerOverbooking(bw), storage,
                                peList, new StreamOperatorScheduler(peList), new FogLinearPowerModel(busyPower, idlePower));

                List<Host> hostList = new ArrayList<Host>();
                hostList.add(host);

                String arch = "x86"; // system architecture
                String os = "Linux"; // operating system
                String vmm = "Xen";
                double time_zone = 10.0; // time zone this resource located
                double costPerStorage = 0.001; // the cost of using storage in this resource
                LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices by now

                FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(arch, os, vmm, host, time_zone, costPerSec,
                                costPerMem, costPerStorage, costPerBw);

                FogDevice fogdevice = null;
                try {
                        fogdevice = new FogDevice(nodeName, characteristics, new AppModuleAllocationPolicy(hostList), storageList,
                                        10, upBw, downBw, 0, ratePerMips, busyPower, idlePower, comCost);
                } catch (Exception e) {
                        e.printStackTrace();
                }

                fogdevice.setLevel(level);
                return fogdevice;
        }

        // initiate the task list (cloudlet list)
        public static List<Cloudlet> createCloudlet(String filename) 
        {
	         LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
	
	        // cloudlet parameters
	         int cloudletId;
	         long length;
	         long fileSize;
	         long outputSize;
	         long memRequired;
	         int pesNumber = 1;
	         UtilizationModel utilizationModel = new UtilizationModelFull();
	
	         BufferedReader br = null;
	         try {
		            String line;
		            br = new BufferedReader(new FileReader(filename));

		            // How to read file in java line by line?
		            while ((line = br.readLine()) != null) 
		            {
		                if (line != null) 
		                {
		                    String[] splitData = line.split(COMMA_DELIMITER);
		                    cloudletId = Integer.parseInt(splitData[0]);
		                    
		                    length = Long.parseLong(splitData[1]);
		                    fileSize = Long.parseLong(splitData[2]);
		                    outputSize = Long.parseLong(splitData[3]);
		                    memRequired = Long.parseLong(splitData[4]);
		                    
		                    Cloudlet cloudlet = new Cloudlet(cloudletId , length, pesNumber, fileSize, outputSize, memRequired, utilizationModel,
		                                                utilizationModel, utilizationModel);
		                    list.add(cloudlet);
		                }
		            }
	        } catch (IOException e) 
	         {
	            e.printStackTrace();
	         } finally {
	            try {
	                if (br != null)
	                    br.close();
	            } catch (IOException crunchifyException) {
	                crunchifyException.printStackTrace();
	            }
	        }
	                return list;
	    }

        /**
         * Function to create the model.
         *
         * @param appId
         *            unique identifier of the application
         * @param userId
         *            identifier of the user of the application
         * @return
         */
        @SuppressWarnings({ })
        private static Application createApplication(String appId, int userId) {

                Application application = Application.createApplication(appId, userId);
                return application;
        }
}
