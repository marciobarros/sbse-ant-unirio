package unirio.ant.calc.metrics;

import java.io.FileOutputStream;
import java.io.PrintStream;

import unirio.ant.calc.loader.ProjectLoader;
import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;

/**
 * Publishes metrics and characteristics for a set of projects
 * 
 * @author Marcio
 */
public class MainPackages
{
	/**
	 * Loads and publishes data for all projects
	 */
	public static final void main(String[] args) throws Exception
	{
		FileOutputStream out = new FileOutputStream("package_metrics.data"); 
		PrintStream ps = new PrintStream(out);
		ps.println("version\tpackage\tcbo\taff\teff\tlcom\tmf\tcs");
		
		ProjectLoader loader = new ProjectLoader();
		
		for (String versao : loader.getRealVersions())
		{
			Project project = loader.loadRealVersion(versao);
			ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
			
			for (int i = 0; i < project.getPackageCount(); i++)
				ps.println(versao.substring(11) + "\t" + project.getPackageIndex(i).getName() + 
							"\t" + cc.calculateCBO(i) + 
							"\t" + cc.calculateAfferentCoupling(i) + 
							"\t" + cc.calculateEfferentCoupling(i) + 
							"\t" + cc.calculateLCOM5(i) +
							"\t" + cc.calculateModularizationFactor(i) + 
							"\t" + cc.calculateClusterScore(i));
		}
		
		ps.close();
		System.out.println("Finished!");
	}
}