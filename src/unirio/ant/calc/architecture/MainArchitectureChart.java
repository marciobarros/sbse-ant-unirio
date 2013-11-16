package unirio.ant.calc.architecture;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import unirio.ant.calc.loader.ProjectLoader;
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
			if (project.getClassCount(sourcePackage.getName()) > 0)
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
		}
		
		bw.close();
	}
	
	/**
	 * Saves the architecture charts for all versions
	 */
	public static final void main(String[] args) throws Exception
	{
		ProjectLoader loader = new ProjectLoader();
		
		for (String versao : loader.getRealVersions())
		{
			Project project = loader.loadRealVersion(versao);
			saveDependencies(project, DIRETORIO_SAIDA + versao + ".txt");
		}

		Project projectEVM = loader.loadOptimizedVersionsEVM().get(0);
		saveDependencies(projectEVM, DIRETORIO_SAIDA + "evm_optimized.txt");

		Project projectMQ = loader.loadOptimizedVersionsMQ().get(0);
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