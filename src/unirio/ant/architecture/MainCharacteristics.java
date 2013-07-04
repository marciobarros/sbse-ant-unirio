package unirio.ant.architecture;

import java.text.DecimalFormat;

import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;

/**
 * Publishes metrics and characteristics for a set of projects
 * 
 * @author Marcio
 */
public class MainCharacteristics
{
	/**
	 * Publishes the characteristics and metrics for a given project
	 */
	private static void publishProjectInformation(String versao, Project project) throws Exception
	{
		DecimalFormat df4 = new DecimalFormat("0.0000");
		DecimalFormat df1 = new DecimalFormat("0.0");

		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		
		int classCount = project.getClassCount();
		int packageCount = project.getPackageCount();
		int dependencyCount = project.getDependencyCount();
		
		double mq = cc.calculateModularizationQuality();
		int evm = cc.calculateEVM();
		int interEdges = cc.calculateCohesion();
		int extraEdges = cc.calculateCoupling();
		
		double classElegance = cc.calculateClassElegance();
		double couplingElegance = cc.calculateCouplingElegance();
		double cohesionElegance = cc.calculateCohesionElegance();
		double lcom5 = cc.calculateLCOM5();
		double cbo = cc.calculateCBO();
		
		System.out.println(versao + "; C: " + classCount + 
									"; P: " + packageCount + 
									"; D: " + dependencyCount + 
									"; C/P: " + df1.format((double)classCount / packageCount) +
									"; IE: " + interEdges + 
									"; XE: " + extraEdges + 
									"; MQ: " + df4.format(mq) + 
									"; EVM: " + evm +
									"; ClE: " + df1.format(classElegance) +
									"; ChE: " + df1.format(cohesionElegance) +
									"; CpE: " + df1.format(couplingElegance) +
									"; LCOM: " + df4.format(lcom5) + 
									"; CBO: " + df4.format(cbo));
	}

	/**
	 * Loads and publishes data for all projects
	 */
	public static final void main(String[] args) throws Exception
	{
		ProjectLoader loader = new ProjectLoader();
		
		for (String versao : loader.getRealVersions())
		{
			Project project = loader.loadRealVersion(versao);
			publishProjectInformation(versao, project);
		}

		Project projectEVM = loader.loadOptimizedVersionEVM();
		int evm = new ClusteringCalculator(projectEVM, projectEVM.getPackageCount()).calculateEVM();
		if (evm != -8170) throw new Exception("Erro no cálculo do EVM");
		publishProjectInformation("EMVopt", projectEVM);

		Project projectMQ = loader.loadOptimizedVersionMQ();
		double mq = new ClusteringCalculator(projectMQ, projectMQ.getPackageCount()).calculateModularizationQuality();
		if (Math.abs(mq - 43.0896) > 0.0001) throw new Exception("Erro no cálculo do MQ");
		publishProjectInformation("MQopt", projectMQ);
		
		System.out.println("Finished!");
	}
}