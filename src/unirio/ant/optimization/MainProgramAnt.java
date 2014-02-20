package unirio.ant.optimization;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import unirio.ant.controller.CDAReader;
import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;

public class MainProgramAnt
{
	protected static double calculateMQ(Project project, int[] solution) throws Exception
	{
		ClusteringCalculator calculator = new ClusteringCalculator(project, project.getClassCount());

		for (int i = 0; i < solution.length; i++)
			calculator.moveClass(i, solution[i]);

		return calculator.calculateModularizationQuality();
	}

	protected static int calculateEVM(Project project, int[] solution) throws Exception
	{
		ClusteringCalculator calculator = new ClusteringCalculator(project, project.getClassCount());

		for (int i = 0; i < solution.length; i++)
			calculator.moveClass(i, solution[i]);

		return calculator.calculateEVM();
	}

	private static void write(PrintWriter pw, String text)
	{
		System.out.println(text);
		pw.println(text);
		pw.flush();
	}
	
	protected static String format(double mq)
	{
		DecimalFormat df4 = new DecimalFormat("0.0000");
		String sMQ = df4.format(mq);
		
		while (sMQ.length() < 7)
			sMQ = ' ' + sMQ;
		
		return sMQ;
	}
	
	private static String format(int n)
	{
		DecimalFormat df0 = new DecimalFormat("0");
		String s = df0.format(n);
		
		while (s.length() < 4)
			s = ' ' + s;
		
		return s;
	}

	public static final void main(String[] args) throws Exception
	{
		Project project = new CDAReader().execute("data\\odem\\apache-ant-1.9.0.odem");
		PrintWriter pw = new PrintWriter(new File("saida.txt"));

		int classCount = project.getClassCount();
		long maxEvaluations = 1000 * classCount * classCount;
		write(pw, project.getName());

		HillClimbingClusteringMQ hccMQ = new HillClimbingClusteringMQ(project, maxEvaluations, project.getClassCount());
		int[] classOrderMQ = hccMQ.generateBasicClassOrder(classCount);
		int[] startSolutionMQ = hccMQ.createStartingSolution();
		write(pw, "S; MQ: " + format(calculateMQ(project, startSolutionMQ)) + "; SOL: " + hccMQ.printSolution(startSolutionMQ));
		int[] completeSolutionMQ = hccMQ.execute(startSolutionMQ, classOrderMQ);
		write(pw, "C; MQ: " + format(calculateMQ(project, completeSolutionMQ)) + "; SOL: " + hccMQ.printSolution(completeSolutionMQ));

		HillClimbingClusteringEVM hccEVM = new HillClimbingClusteringEVM(project, maxEvaluations, project.getClassCount());
		int[] classOrderEVM = hccEVM.generateBasicClassOrder(classCount);
		int[] startSolutionEVM = hccEVM.createStartingSolution();
		write(pw, "S; FIT: " + format(calculateEVM(project, startSolutionEVM)) + "; SOL: " + hccEVM.printSolution(startSolutionEVM));
		int[] completeSolutionEVM = hccEVM.execute(startSolutionEVM, classOrderEVM);
		write(pw, "C; FIT: " + format(calculateEVM(project, completeSolutionEVM)) + "; SOL: " + hccEVM.printSolution(completeSolutionEVM));

		pw.close();
	}
}