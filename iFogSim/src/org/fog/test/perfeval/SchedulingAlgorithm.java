package org.fog.test.perfeval;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.fog.entities.FogDevice;
import org.fog.test.perfeval.EWOA.EWOA4;
import org.fog.test.perfeval.EWOA.EWOA5;
import org.fog.test.perfeval.EWOA.EWOA6;
import org.fog.test.perfeval.EWOA.EWOA7;
import org.fog.test.perfeval.GA.GeneticAlgorithm;
import org.fog.test.perfeval.GA.Population;


public class SchedulingAlgorithm {

	public static final double TIME_WEIGHT = 0.5;

	public static int NUMBER_INDIVIDUAL;	// Agents- 100 according to table page 14.
	public static int NUMBER_ITERATION;	// Generations-500 according to table page 14.
	public static final double MUTATION_RATE = 0.1;
	public static final double CROSSOVER_RATE = 0.9;
	// GA
	public static final int NUMBER_ELITISM_INDIVIDUAL = 1;
	// BEE
	public static final int NUMBER_DRONE = (int) (NUMBER_INDIVIDUAL * 0.4);


	public static void writeToFile(List<? extends Cloudlet> cloudletList, Population population, String algoName, double eTime, String category, int maxItr,
																																				int noOfAgents)
	{
    	try
    	{
//    		String fileToWrite = "D:/iFogSim/iFogSim_master/reports/"+algoName+"/" + category + "/output_"+ cloudletList.size()  + ".txt";
//    		String fileToWrite = "D:/iFogSim/iFogSim_master/reports/"+algoName+"/" + category + "/output_"+ maxItr  + ".txt";
    		String fileToWrite = "D:/iFogSim/iFogSim_master/reports/"+algoName+"/" + category + "/output_"+ noOfAgents  + ".txt";
    		
    		FileWriter fWriter = new FileWriter(fileToWrite,true);
    		BufferedWriter bWriter = new BufferedWriter(fWriter);
    		
    		bWriter.write(population.getFittest(0).getFitness() + ", "+population.getFittest(0).getTime() + ", "+ population.getFittest(0).getCost()+ ", "+ 
    		population.getFittest(0).getEnergy()+", " + eTime+", "+ cloudletList.size()/population.getFittest(0).getTime()+ "\n");
    		
    		bWriter.close();
    		fWriter.close();
    	}
    	catch(IOException e)
    	{
    		System.out.println("An error occurred while writing to the file: " + e.getMessage());
    	}
	}

	public static void runWOAAlgorithm(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr) 
	{
		WOA.setFogDevices(fogDevices);
		WOA.setCloudletList(cloudletList);
		String[] args = {category, subCategory, Integer.toString(noOfAgents), Integer.toString(maxItr)};
		WOA.main(args);
	}

	public static Individual runGeneticAlgorithm(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr) 
	{
		NUMBER_INDIVIDUAL = noOfAgents;	// Agents
		NUMBER_ITERATION = maxItr;	// Generations
		
		double gaStartTime = System.currentTimeMillis();
		
		// Create GA object
		GeneticAlgorithm ga = new GeneticAlgorithm(NUMBER_INDIVIDUAL, MUTATION_RATE, CROSSOVER_RATE, NUMBER_ELITISM_INDIVIDUAL);

		// Calculate the boundary of time and cost
		ga.calcMinTimeCost(fogDevices, cloudletList);

		// Initialize population
		Population population = ga.initPopulation(cloudletList.size(), fogDevices.size() - 1);

		// Evaluate population
		ga.evalPopulation(population, fogDevices, cloudletList);

		population.printPopulation();

		// Keep track of current generation
		int generation = 0;

		while (generation < NUMBER_ITERATION) 
		{
			System.out.println("\n------------- Generation " + generation + " --------------");
//                                      population.printPopulation();
			// Apply crossover
			population = ga.crossoverPopulation(population, fogDevices, cloudletList);

			// Apply mutation
			population = ga.mutatePopulation(population, fogDevices, cloudletList);

			// Evaluate population
			ga.evalPopulation(population, fogDevices, cloudletList);

			population.getFittest(0).printGene();

			// Print fittest individual from population
			System.out.println(
					"\nBest solution of generation " + generation + ": " + population.getFittest(0).getFitness());
			System.out.println("Makespan: (" + ga.getMinTime() + ")--" + population.getFittest(0).getTime());
			System.out.println("TotalCost: (" + ga.getMinCost() + ")--" + population.getFittest(0).getCost());
			System.out.println("TotalEnergy: (" + ga.getMinEnergy() + ")--" + population.getFittest(0).getEnergy());
			
			// Increment the current generation
			generation++;
//            population.printPopulation();
		}

		double gaEndTime = System.currentTimeMillis();
		double eTime = gaEndTime-gaStartTime;

		System.out.println("\n>>>>>>>>>>>>>>>>>>>RESULTS<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("Found solution in " + generation + " generations");
		population.getFittest(0).printGene();
		System.out.println("\nBest solution: " + population.getFittest(0).getFitness());

		System.out.println("No. of cloudlets: " + population.getFittest(0).getChromosomeLength());
		System.out.println("GA execution time: "+eTime);
		writeToFile(cloudletList, population, "GA", eTime, category, maxItr, noOfAgents);
		
		return population.getFittest(0);
	}

	public static Individual runGeneticAlgorithm2(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr) 
	{

	NUMBER_INDIVIDUAL = noOfAgents;	// Agents
	NUMBER_ITERATION = maxItr;	// Generations

	double gaStartTime = System.currentTimeMillis();

		// Create GA object
	GeneticAlgorithm ga = new GeneticAlgorithm(NUMBER_INDIVIDUAL, MUTATION_RATE, CROSSOVER_RATE, NUMBER_ELITISM_INDIVIDUAL);

	// Calculate the boundary of time and cost
	ga.calcMinTimeCost(fogDevices, cloudletList);

	// Initialize population
	Population population = ga.initPopulation(cloudletList.size(), fogDevices.size() - 1);

	ga.evalPopulation(population, fogDevices, cloudletList);

	// Keep track of current generation
	int generation = 0;

	while (generation < NUMBER_ITERATION) 
	{
		System.out.println("\n------------- Generation " + generation + " --------------");
		Population newPopulation = new Population();

		// Apply crossover
		newPopulation = ga.crossoverPopulation2(population, fogDevices, cloudletList);

		// Apply mutation
		population = ga.mutatePopulation2(newPopulation, fogDevices, cloudletList);

		population = ga.evalPopulation(population, fogDevices, cloudletList);

//    population = ga.selectPopulation2(population, newPopulation, fogDevices, cloudletList);

		population.getFittest(0).printGene();

		
		// Print fittest individual from population
		System.out.println(
				"\nBest solution of generation " + generation + ": " + population.getFittest(0).getFitness());
		System.out.println("Makespan: (" + ga.getMinTime() + ")--" + population.getFittest(0).getTime());
		System.out.println("TotalCost: (" + ga.getMinCost() + ")--" + population.getFittest(0).getCost());
		System.out.println("TotalEnergy: (" + ga.getMinEnergy() + ")--" + population.getFittest(0).getEnergy());
		// Increment the current generation
		generation++;
//                                  population.printPopulation();
	}

	double gaEndTime = System.currentTimeMillis();
	double eTime = gaEndTime-gaStartTime;

	System.out.println(">>>>>>>>>>>>>>>>>>>RESULTS<<<<<<<<<<<<<<<<<<<<<");
	System.out.println("Found solution in " + generation + " generations");
	population.getFittest(0).printGene();
	System.out.println("\nBest solution: " + population.getFittest(0).getFitness());

	writeToFile(cloudletList, population, "GA2", eTime, category, maxItr, noOfAgents);
	return population.getFittest(0);
}

	public static void runPSOGAAlgorithm(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr) 
	{
		PSOGA.setFogDevices(fogDevices);
		PSOGA.setCloudletList(cloudletList);
		String[] args = {category, subCategory, Integer.toString(noOfAgents), Integer.toString(maxItr)};
		PSOGA.main(args);
	}

	public static void runIPSOAlgorithm(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr) 
	{
		IPSO.setFogDevices(fogDevices);
		IPSO.setCloudletList(cloudletList);
		String[] args = {category, subCategory, Integer.toString(noOfAgents), Integer.toString(maxItr)};
		IPSO.main(args);
	}

	public static void runOppoCWOAAlgorithm(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr) 
	{
		oppoCWOA.setFogDevices(fogDevices);
		oppoCWOA.setCloudletList(cloudletList);
		String[] args = {category, subCategory, Integer.toString(noOfAgents), Integer.toString(maxItr)};
		oppoCWOA.main(args);
	}
		

	public static void runDOWOAAlgorithm(String category, String subCategory, List<? extends Cloudlet> cloudletList, List<FogDevice> fogDevices, int noOfAgents, int maxItr, int wait) 
	{
		DOWOA.setFogDevices(fogDevices);
		DOWOA.setCloudletList(cloudletList);
		String[] args = {category, subCategory, Integer.toString(noOfAgents), Integer.toString(maxItr), Integer.toString(wait)};
		DOWOA.main(args);
	}
}
