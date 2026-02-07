package org.fog.test.perfeval;
import java.util.*;
import java.awt.Point;
import static java.lang.Math.pow;
import static java.lang.Math.random;

public class Utility
{
    public static double fitAgent(double minTime, double makespan, double minCost, double cost, double minEnergy, double energy, double minDelay, double delay)
    {															// Fitness for a complete agent
    	double fitness = 0;

//    	fitness = (0.33 * (minTime/makespan)) + (0.33 * (minCost/cost)) + (0.33 * (minEnergy/energy));
//    	fitness = (0.33 * (minTime/makespan)) + (0.33 * (minCost/cost)) + (0.33 * (minDelay/delay));
//    	fitness = (0.33 * (minCost/cost)) + (0.33 * (minEnergy/energy)) + (0.33 * (minDelay/delay));
//    	fitness = (0.33 * (minTime/makespan)) + (0.33 * (minEnergy/energy)) + (0.33 * (minDelay/delay));
//    	fitness = (0.5 * (minCost/cost)) + (0.5 * (minEnergy/energy));
//    	fitness = (0.5 * (minTime/makespan)) + (0.5 * (minEnergy/energy));
//    	fitness = (0.5 * (minEnergy/energy)) + (0.5 * (minDelay/delay));
//    	fitness = (0.5 * (minTime/makespan)) + (0.5 * (minDelay/delay));
    	fitness = (0.5 * (minDelay/delay)) + (0.5 * (minCost/cost));
//    	fitness = minTime/makespan;
//    	fitness = minEnergy/energy;
//    	fitness = minDelay/delay;
    	
    	return fitness;
    }

    public static double fitTask(double t, double c, double e)		// Fitness for a single task to fogDevice
    {
    	double fitness = 0;

//    	fitness = (0.33 * 1/t) + (0.33 * 1/c) + (0.33 * 1/e);
//    	fitness = (0.33 * 1/t) + (0.33 * 1/c) + (0.33 * 1/e);
//    	fitness = (0.5 * 1/t) + (0.5 * 1/e);
//    	fitness = (0.5 * 1/c) + (0.5 * 1/e);
    	fitness = (0.5 * 1/t) + (0.5 * 1/c);
//    	fitness = 1/t;
//    	fitness = 1/c;
//    	fitness = 1/e;

    	return fitness;
    }

    public static double pher(double t, double c, double e)			// Initial values for pheromones
    {
    	double pher = 0;

//    	pher = (0.33 * 1/t) + (0.33 * 1/c) + (0.33 * 1/e);
//    	pher = (0.33 * 1/t) + (0.33 * 1/c) + (0.33 * 1/e);
//    	pher = (0.5 * 1/t) + (0.5 * 1/e);
//    	pher = (0.5 * 1/c) + (0.5 * 1/e);
    	pher = (0.5 * 1/t) + (0.5 * 1/c);
//    	pher = 1/t;
//    	pher = 1/c;
//    	pher = 1/e;

    	return pher;
    }

    public static String fileToWrite(String algorithm, String category, String subCategory, int noOfAgents, int maxItr)
    {
//		String file = "D:/iFogSim_Obj3/iFogSim/reports/" + algorithm + "/" + category + "/output_"+ subCategory + ".txt";
//		String file = "D:/iFogSim_Obj3/iFogSim/reports/" + algorithm + "/" + category + "/output_"+ noOfAgents + ".txt";
		String file = "D:/iFogSim_Obj3/iFogSim/reports/" + algorithm + "/" + category + "/output_"+ maxItr + ".txt";

    	return file;
    }

    public static String fileToWrite2(String algorithm, String category, String subCategory, int noOfAgents, int wait)
    {	// added for providing results of DOWOA for various values of waitThreshold
    	
		String file = "D:/iFogSim_Obj3/iFogSim/reports/" + algorithm + "/" + category + "/output_"+ wait + ".txt";

    	return file;
    }


}
