package unirio.ant.architecture;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;
import unirio.ant.model.ProjectPackage;

/**
 * Class that publishes architectural charts for all versions of Apache Ant
 * 
 * @author Marcio
 */
public class MainArchitectureChart
{
	/**
	 * Output directory for architecture charts
	 */
	private static String DIRETORIO_SAIDA = "results\\packdeps\\";
	
	/**
	 * Returns an ordered list of packages
	 */
	private static List<ProjectPackage> getOrderedPackages(Iterable<ProjectPackage> packages)
	{
		List<ProjectPackage> listPackages = new ArrayList<ProjectPackage>();
		
		for (ProjectPackage p : packages)
			listPackages.add(p);
		
		Collections.sort(listPackages, new PackageComparator());		
		return listPackages;
	}

	/**
	 * Saves a file conveying dependencies among packages
	 */
	private static void saveDependencies(Project project, String outputFilename) throws IOException
	{
		FileWriter fw = new FileWriter(new File(outputFilename));
		BufferedWriter bw = new BufferedWriter(fw);
		
		List<ProjectPackage> allPackages = getOrderedPackages(project.getPackages());

		for (ProjectPackage sourcePackage : allPackages)
		{
			List<ProjectPackage> dependencies = getOrderedPackages(project.getPackageDependencies(sourcePackage));

			bw.write(sourcePackage.getName());

			for (ProjectPackage targetPackage : dependencies)
			{
				int targetIndex = allPackages.indexOf(targetPackage);
				bw.write("; " + targetIndex);
			}
			
			bw.write("\n");
		}
		
		bw.close();
	}
	
	/**
	 * Saves the architecture charts for all versions
	 */
	public static final void main(String[] args) throws Exception
	{
		ProjectLoader loader = new ProjectLoader();
		
		/*for (String versao : loader.getRealVersions())
		{
			Project project = loader.loadRealVersion(versao);
			saveDependencies(project, DIRETORIO_SAIDA + versao + ".txt");
		}*/

		Project projectEVM = loader.loadOptimizedVersionEVM();
		int evm = new ClusteringCalculator(projectEVM, projectEVM.getPackageCount()).calculateEVM();
		if (evm != -8170) throw new Exception("Erro no cálculo do EVM");
		saveDependencies(projectEVM, DIRETORIO_SAIDA + "evm_optimized.txt");

		Project projectMQ = loader.loadOptimizedVersionMQ();
		double mq = new ClusteringCalculator(projectMQ, projectMQ.getPackageCount()).calculateModularizationQuality();
		if (Math.abs(mq - 43.0896) > 0.0001) throw new Exception("Erro no cálculo do MQ");
		saveDependencies(projectMQ, DIRETORIO_SAIDA + "mq_optimized.txt");
		
		System.out.println("Finished!");
	}
}

/**
 * Compare the name of different packages
 */
class PackageComparator implements Comparator<ProjectPackage>
{
	@Override
	public int compare(ProjectPackage p1, ProjectPackage p2)
	{
		return p1.getName().compareToIgnoreCase(p2.getName());
	}
}