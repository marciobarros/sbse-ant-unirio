package unirio.ant.controller;

/**
 * Class that calculates simple statistics
 * 
 * @author Marcio
 */
public class StatisticsCalculator
{
	private double sum;
	private double sumSquare;
	private int count;

	/**
	 * Adds a number to the calculator
	 */
	public void add(int number)
	{
		sum += number;
		sumSquare += number * number;
		count++;
	}
	
	/**
	 * Returns the standard deviation of previously added numbers
	 */
	public double standardDeviation() 
	{
		return (count > 2) ? Math.sqrt(sumSquare / count - sum / count * sum / count) : 0.0;
	}

	/**
	 * Returns the average value of previously added numbers
	 */
	public double average() 
	{
		return (count > 0) ? sum / count : 0.0;
	}
}