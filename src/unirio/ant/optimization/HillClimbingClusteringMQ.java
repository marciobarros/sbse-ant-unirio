package unirio.ant.optimization;

import jmetal.util.PseudoRandom;
import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;

/**
 * Hill Climbing searcher for the clustering problem
 * 
 * @author Marcio Barros
 */
public class HillClimbingClusteringMQ
{
	/**
	 * Calculator used in the search process
	 */
	private ClusteringCalculator calculator;

	/**
	 * Number of classes in the project under evaluation
	 */
	private int classCount;

	/**
	 * Number of packages in the project under evaluation
	 */
	private int packageCount;

	/**
	 * Number of movements evaluations available in the budget
	 */
	private int maxMovements;

	/**
	 * Number of fitness evaluations available in the budget
	 */
	private long maxEvaluations;

	/**
	 * Number of fitness evaluations executed
	 */
	private long evaluations;

	/**
	 * Fitness of the best solution found
	 */
	private double fitness;

	/**
	 * Initializes the Hill Climbing search process
	 * 
	 * @param detailsFile File where details of the search process will be saved
	 * @param project Project whose classes will be distributed into clusters
	 * @param maxEvaluations Budget of fitness evaluations
	 * @param maxMovements Maximum number of acceptable movements
	 */
	public HillClimbingClusteringMQ(Project project, long maxEvaluations, int maxMovements) throws Exception
	{
		this.classCount = project.getClassCount();
		this.packageCount = classCount;
		this.calculator = new ClusteringCalculator(project, packageCount);
		this.maxEvaluations = maxEvaluations;
		this.maxMovements = maxMovements;
	}
	
	/**
	 * Retorna o número de avaliações da função de fitness
	 */
	public long getEvaluations()
	{
		return evaluations;
	}

	/**
	 * Prints a solution into a string
	 */
	public String printSolution(int[] solution)
	{
		String s = "[" + solution[0];

		for (int i = 1; i < solution.length; i++)
			s += " " + solution[i];

		return s + "]";
	}

	/**
	 * Copies a source solution to a target one
	 */
	private void copySolution(int[] source, int[] target)
	{
		for (int i = 0; i < source.length; i++)
			target[i] = source[i];
	}

	/**
	 * Applies a solution to the clustering calculator
	 */
	private void applySolution(int[] solution)
	{
		for (int i = 0; i < solution.length; i++)
			calculator.moveClass(i, solution[i]);
	}

	/**
	 * Evaluates the fitness of a solution, saving detail information
	 */
	private double evaluate()
	{
		double fit = calculator.calculateModularizationQuality();

		if (++evaluations % (classCount * classCount) == 0)
			System.out.print(evaluations / (classCount * classCount) + " ");

		return fit;
	}

	/**
	 * Creates a starting solution based on initial class settings
	 */
	public int[] createStartingSolution()
	{
		int[] solution = new int[classCount];
		
		for (int i = 0; i < classCount; i++)
			solution[i] = calculator.getPackage(i);
		
		return solution;
	}

	/**
	 * Counts the number of movements in a solution
	 */
	private int countMovements(int[] original, int[] current)
	{
		int movements = 0;
		
		for (int i = 0; i < original.length; i++)
			if (original[i] != current[i])
				movements++;
		
		return movements;
	}
	
	/**
	 * Executes the Hill Climbing search
	 */
	public int[] execute(int[] startSolution, int[] classOrder) throws Exception
	{
		int[] bestSolution = new int[classCount];
		copySolution(startSolution, bestSolution);

		int[] solution = new int[classCount];
		copySolution(bestSolution, solution);

		applySolution(solution);
		this.fitness = evaluate();
		this.evaluations = 0;
		boolean foundBetterSolution = true;
		
		while (foundBetterSolution && evaluations < maxEvaluations && countMovements(startSolution, solution) < maxMovements)
		{
			foundBetterSolution = false;
			
			for (int i = 0; i < classCount && !foundBetterSolution; i++)
			{
				int classNumber = classOrder[i];
				
				for (int j = 0; j < packageCount && !foundBetterSolution; j++)
				{
					if (solution[classNumber] != j)
					{
						calculator.moveClass(classNumber, j);
						double neighborFitness = evaluate();

						if (neighborFitness > fitness)
						{
							solution[classNumber] = j;
							copySolution(solution, bestSolution);
							this.fitness = neighborFitness;
							foundBetterSolution = true;
						}
						else
							calculator.moveClass(classNumber, solution[classNumber]);
					}
				}
			}
		}
		
		return bestSolution;
	}

	/**
	 * Generates a class order having one class per package
	 */
	public int[] generateBasicClassOrder(int classCount)
	{
		int[] classes = new int[classCount];
		
		for (int i = 0; i < classCount; i++)
			classes[i] = i;
		
		return classes;
	}

	/**
	 * Generates a class order resembling the source-code
	 */
	public int[] generateCodeClassOrder(Project project)
	{
		int classCount = project.getClassCount();
		int[] classes = new int[classCount];
		
		for (int i = 0; i < classCount; i++)
			classes[i] = project.getIndexForPackage(project.getClassIndex(i).getPackage());
		
		return classes;
	}

	/**
	 * Generates a random class order
	 */
	public int[] generateRandomClassOrder(int classCount)
	{
		int[] classes = new int[classCount];
		
		for (int i = 0; i < classCount; i++)
			classes[i] = i;
		
		int[] result = new int[classCount];
		
		for (int i = 0; i < classCount - 1; i++)
		{
			int index = PseudoRandom.randInt(0, classCount - i - 1);
			result[i] = classes[index];
			
			for (int j = index; j < classCount-1; j++)
				classes[j] = classes[j+1];
			
			classes[classCount-1] = 0;
		}

		result[classCount-1] = classes[0];
		return result;
	}
}