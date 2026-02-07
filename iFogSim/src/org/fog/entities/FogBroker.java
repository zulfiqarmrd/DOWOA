package org.fog.entities;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.fog.test.perfeval.SchedulingAlgorithm;
import org.fog.test.perfeval.fogScheduling;
import org.fog.test.perfeval.Individual;
import org.cloudbus.cloudsim.lists.CloudletList;

public class FogBroker extends PowerDatacenterBroker{

	private List<FogDevice> fogDevices;
	
	/****start******/
	protected List<? extends Cloudlet> listCloudlet;
	/*****end*****/
	
	public FogBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startEntity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvent(SimEvent ev) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		
	}
	
    public List<FogDevice> getFogDevices() {
        return fogDevices;
}

	public void setFogDevices(List<FogDevice> fogDevices) {
		this.fogDevices = fogDevices;
		
	}
	
    public void assignCloudlet(String category, String subCategory, List<Cloudlet> listCloudlet, List<FogDevice> fogDevices, String schedulingStrategy, int noOfAgents, int maxItr, int wait) 
    {
    	switch (schedulingStrategy)
    	{
    	case "WOA":
    		SchedulingAlgorithm.runWOAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "oppoCWOA":
    		SchedulingAlgorithm.runOppoCWOAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "OWOA":
    		SchedulingAlgorithm.runOWOAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "DOWOA":
    		SchedulingAlgorithm.runDOWOAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr, wait);
    		break;

     	case "DOWOA2":
    		SchedulingAlgorithm.runDOWOA2Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;
    		
     	case "DOWOA3":
    		SchedulingAlgorithm.runDOWOA3Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;
    		
     	case "DOWOA4":
    		SchedulingAlgorithm.runDOWOA4Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;
    		
     	case "DOWOA5":
    		SchedulingAlgorithm.runDOWOA5Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "DOWOA6":
    		SchedulingAlgorithm.runDOWOA6Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "DOWOA7":
    		SchedulingAlgorithm.runDOWOA7Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "DOWOA8":
    		SchedulingAlgorithm.runDOWOA8Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "DOWOA9":
    		SchedulingAlgorithm.runDOWOA9Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "DOWOAB":
    		SchedulingAlgorithm.runDOWOABAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO":
    		SchedulingAlgorithm.runSACOAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO2":
    		SchedulingAlgorithm.runSACO2Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO3":
    		SchedulingAlgorithm.runSACO3Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO4":
    		SchedulingAlgorithm.runSACO4Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO5":
    		SchedulingAlgorithm.runSACO5Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO6":
    		SchedulingAlgorithm.runSACO6Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO7":
    		SchedulingAlgorithm.runSACO7Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO8":
    		SchedulingAlgorithm.runSACO8Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO9":
    		SchedulingAlgorithm.runSACO9Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

     	case "SACO10":
    		SchedulingAlgorithm.runSACO10Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "ACOExecTime":
    		SchedulingAlgorithm.runACOExecTimeAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "ACOSimple":
    		SchedulingAlgorithm.runACOSimpleAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO":
    		SchedulingAlgorithm.runRACOAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO2":
    		SchedulingAlgorithm.runRACO2Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO3":
    		SchedulingAlgorithm.runRACO3Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO4":
    		SchedulingAlgorithm.runRACO4Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO5":
    		SchedulingAlgorithm.runRACO5Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO6":
    		SchedulingAlgorithm.runRACO6Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO7":
    		SchedulingAlgorithm.runRACO7Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO8":
    		SchedulingAlgorithm.runRACO8Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO9":
    		SchedulingAlgorithm.runRACO9Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RACO10":
    		SchedulingAlgorithm.runRACO10Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "PSOGA":
    		SchedulingAlgorithm.runPSOGAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "IPSO":
    		SchedulingAlgorithm.runIPSOAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

      	case "EWOA1":
    		SchedulingAlgorithm.runEWOA1Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

      	case "EWOA2":
    		SchedulingAlgorithm.runEWOA2Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "EWOA3":
    		SchedulingAlgorithm.runEWOA3Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "EWOA4":
    		SchedulingAlgorithm.runEWOA4Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "EWOA5":
    		SchedulingAlgorithm.runEWOA5Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "EWOA6":
    		SchedulingAlgorithm.runEWOA6Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "EWOA7":
    		SchedulingAlgorithm.runEWOA7Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "EWOA8":
    		SchedulingAlgorithm.runEWOA8Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "SMWOA":
    		SchedulingAlgorithm.runSMWOAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

        case "SMWOA3":
    		SchedulingAlgorithm.runSMWOA3Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "WOAmM":
    		SchedulingAlgorithm.runWOAmMAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

       	case "RWOA":
    		SchedulingAlgorithm.runRWOAAlgorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

      	case "WOAmM3":
    		SchedulingAlgorithm.runWOAmM3Algorithm(category, subCategory, cloudletList, fogDevices, noOfAgents, maxItr);
    		break;

}
        return ;
}
	

}
