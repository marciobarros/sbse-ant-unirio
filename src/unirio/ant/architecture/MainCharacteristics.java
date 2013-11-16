package unirio.ant.architecture;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;

/**
 * Publishes metrics and characteristics for a set of projects
 * 
 * @author Marcio
 */
public class MainCharacteristics
{
	private static String JAR_DIRECTORY = "\\Users\\Marcio\\Desktop\\Resultados Pesquisa\\Resultados Clustering Apache ANT\\versoes";

	/**
	 * Publishes information about a JAva class
	 */
    private void processClass(InputStream is, String version, String filename, PrintStream ps) throws ClassNotFoundException, IOException
	{
		ClassParser cp = new ClassParser(is, filename);
		JavaClass clazz = cp.parse();
		
		int countAttributes = clazz.getAttributes().length;
		int countMethods = clazz.getMethods().length;
		int countPublicMethods = 0;
		
		for (Method method : clazz.getMethods())
			if ((method.getAccessFlags() & Constants.ACC_PUBLIC) != 0)
				countPublicMethods++;
		
		String packageName = filename.substring(0, filename.lastIndexOf("/")).replace('/', '.');
		String className = filename.substring(filename.lastIndexOf("/")+1, filename.lastIndexOf("."));
		ps.println(version + "\t" + packageName + "\t" + className + "\t" + countAttributes + "\t" + countMethods + "\t" + countPublicMethods);
	}
	
    /**
     * Transverses a JAR file to publish information about its contents
     */
	private void transverseJarFile(String jarFile, String version, PrintStream ps) throws IOException, FileNotFoundException, ClassNotFoundException
	{
		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> e = jar.entries();

		while (e.hasMoreElements())
		{
			JarEntry file = e.nextElement();
			String fileName = file.getName();
			
			if (fileName.endsWith(".class"))
			{
				InputStream is = jar.getInputStream(file);
				processClass(is, version, fileName, ps);
				is.close();
			}
		}

		jar.close();
	}
	
	/**
	 * Publishes the characteristics and metrics for a given project
	 */
	private static void publishProjectInformation(String versao, Project project) throws Exception
	{
		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		int dependencyCount = project.getDependencyCount();
		double mq = cc.calculateModularizationQuality();
		int evm = cc.calculateEVM();
		double aff = cc.calculateAfferentCoupling();
		double eff = cc.calculateEfferentCoupling();
		//int interEdges = cc.calculateCohesion();
		//int extraEdges = cc.calculateCoupling();
		double lcom5 = cc.calculateLCOM5();
		double cbo = cc.calculateCBO();
		
		//DecimalFormat df4 = new DecimalFormat("0.0000");
		System.out.println(versao + "; D: " + dependencyCount + "; CBO: " + cbo + "; AFF: " + aff + "; EFF: " + eff + "; MQ: " + mq + "; EVM: " + evm + "; LCOM: " + lcom5);
	}

	/**
	 * Loads and publishes data for all projects
	 */
	public static final void main(String[] args) throws Exception
	{
		FileOutputStream out = new FileOutputStream("size_metrics.data"); 
		PrintStream ps = new PrintStream(out);
		ps.println("version\tpackage\tclasse\tattr\tmeth\tpmeth");
		
		MainCharacteristics mc = new MainCharacteristics();
		ProjectLoader loader = new ProjectLoader();
		
		for (String versao : loader.getRealVersions())
		{
			mc.transverseJarFile(JAR_DIRECTORY + "\\" + versao + "\\ant.jar", versao.substring(11), ps);
			Project project = loader.loadRealVersion(versao);
			publishProjectInformation(versao, project);
		}		
		
		/*Project projectEVM = loader.loadOptimizedVersionEVM();
		int evm = new ClusteringCalculator(projectEVM, projectEVM.getPackageCount()).calculateEVM();
		if (evm != -8170) throw new Exception("Erro no cálculo do EVM");
		publishProjectInformation("EMVopt", projectEVM);

		Project projectMQ = loader.loadOptimizedVersionMQ();
		double mq = new ClusteringCalculator(projectMQ, projectMQ.getPackageCount()).calculateModularizationQuality();
		if (Math.abs(mq - 43.0896) > 0.0001) throw new Exception("Erro no cálculo do MQ");
		publishProjectInformation("MQopt", projectMQ);*/
		
		ps.close();
		System.out.println("Finished!");
	}
}