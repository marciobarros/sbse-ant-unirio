package unirio.ant.calc.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import unirio.ant.controller.CDAReader;
import unirio.ant.model.Project;
import unirio.ant.model.ProjectClass;
import unirio.ant.model.ProjectPackage;

/**
 * Class that loads real and optimized versions of Apache Ant
 * 
 * @author Marcio
 */
public class ProjectLoader
{
	private static String OPTIMIZED_DIRECTORY = "\\Users\\Marcio\\Desktop\\Resultados Pesquisa\\Resultados Clustering Apache ANT\\multi run\\v1.9.0\\";
	
	/**
	 * Input directory for real versions
	 */
	private static String INPUT_DIRECTORY = "data\\odem\\";

	/**
	 * Datas das versões do Apache Ant
	 */
	private static String[] VERSION_DATES = 
	{
		"2000-07-19",
		"2000-10-24",
		"2001-03-03",
		"2001-09-03",
		"2001-10-11",
		"2002-07-10",
		"2002-10-03",
		"2003-03-03",
		"2003-04-09",
		"2003-08-12",
		"2003-12-18",
		"2004-02-12",
		"2004-07-16",
		"2005-04-28",
		"2005-05-19",
		"2005-06-02",
		"2006-12-13",
		"2008-07-09",
		"2010-02-02",
		"2010-04-30",
		"2010-12-20",
		"2012-03-13",
		"2012-05-23",
		"2013-03-10"
	};
	
	/**
	 * Names of the real versions
	 */
	private static String[] REAL_VERSIONS = 
	{
		"apache-ant-1.1.0",
		"apache-ant-1.2.0",
		"apache-ant-1.3.0",
		"apache-ant-1.4.0",
		"apache-ant-1.4.1",
		"apache-ant-1.5.0",
		"apache-ant-1.5.1",
		"apache-ant-1.5.2",
		"apache-ant-1.5.3",
		"apache-ant-1.5.4",
		"apache-ant-1.6.0",
		"apache-ant-1.6.1",
		"apache-ant-1.6.2",
		"apache-ant-1.6.3",
		"apache-ant-1.6.4",
		"apache-ant-1.6.5",
		"apache-ant-1.7.0",
		"apache-ant-1.7.1",
		"apache-ant-1.8.0",
		"apache-ant-1.8.1",
		"apache-ant-1.8.2",
		"apache-ant-1.8.3",
		"apache-ant-1.8.4",
		"apache-ant-1.9.0"
	};
	
	/**
	 * Known external dependencies for real versions
	 */
	private static String[] REAL_VERSION_EXTERNAL_DEPENDENCIES = 
	{
		// Versão 1.1.0
		"org.apache.tools.ant.taskdefs.optional.XalanLiaison",
		"org.apache.tools.ant.taskdefs.optional.XslpLiaison",
		// Versão 1.3.0
		"org.apache.tools.ant.taskdefs.optional.TraXLiaison",
		// Versão 1.4.0
		"at.dms.kjc.Main",
		// Versão 1.5.0
		"org.apache.tools.ant.taskdefs.optional.Test",
		"org.apache.tools.ant.taskdefs.Get",
		"org.apache.tools.ant.taskdefs.email.UUMailer",
		"org.apache.tools.ant.taskdefs.email.MimeMailer",
		// Versão 1.6.0
		"org.apache.tools.ant.launch.AntMain",
		"org.apache.tools.ant.launch.Locator",
		"org.apache.tools.ant.util.optional.WeakishReference12",
		// Versão 1.7.0
		"org.apache.tools.ant.launch.Launcher",
		"org.apache.tools.ant.util.java15.ProxyDiagnostics",
		"org.apache.tools.ant.filters.util.JavaClassHelper",
		// Versão 1.8.0
		"org.apache.tools.ant.loader.AntClassLoader5",
		"org.apache.tools.ant.taskdefs.optional.EchoProperties"
	};

	/**
	 * Returns all real versions
	 */
	public String[] getRealVersions()
	{
		return REAL_VERSIONS;
	}
	
	/**
	 * Returns the dates for all real versions
	 */
	public String[] getRealVersionsDate()
	{
		return VERSION_DATES;
	}
	
	/**
	 * Loads a real version into a project
	 */
	public Project loadRealVersion(String versao) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		return reader.execute(INPUT_DIRECTORY + versao + ".odem");
	}
	
	/**
	 * Loads an optimized version into a project
	 */
	private Project loadOptimizedVersion(String solution) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		Project project = reader.execute("data\\odem\\apache-ant-1.9.0.odem");
		project.clearPackages();

		String[] tokens = solution.split(" ");
		
		for (int i = 0; i < tokens.length; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
			int packageNumber = Integer.parseInt(tokens[i]);
			
			while (project.getPackageCount() <= packageNumber)
				project.addPackage("" + project.getPackageCount());
			
			ProjectPackage _package = project.getPackageIndex(packageNumber);
			_class.setPackage(_package);
		}
		
		return project;
	}
	
	/**
	 * Loads a set of optimized versions of a project
	 */
	public List<Project> loadOptimizedVersions(String filename) throws XMLParseException
	{
		List<Project> ciclos = new ArrayList<Project>();
		 
		try 
		{
 			BufferedReader br = new BufferedReader(new FileReader(OPTIMIZED_DIRECTORY + filename));
			String line;
 
			while ((line = br.readLine()) != null) 
			{
				int posOpen = line.indexOf('[');
				int posClose = line.indexOf(']');
				
				if (posOpen != -1 && posClose != -1)
					ciclos.add(loadOptimizedVersion(line.substring(posOpen+1, posClose)));
			}
			
			br.close();
 
		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		return ciclos;
	}
	
	/**
	 * Loads the version optimized for EVM
	 */
	public List<Project> loadOptimizedVersionsEVM() throws XMLParseException
	{
		return loadOptimizedVersions("saida evm pseudo.txt");
	}

	/**
	 * Loads the version optimized for MQ
	 */
	public List<Project> loadOptimizedVersionsMQ() throws XMLParseException
	{
		return loadOptimizedVersions("saida mq pseudo.txt");
	}
}