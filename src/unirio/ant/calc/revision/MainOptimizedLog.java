package unirio.ant.calc.revision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import unirio.ant.calc.loader.ProjectLoader;
import unirio.ant.model.Project;
import unirio.ant.model.ProjectClass;
import unirio.ant.model.ProjectPackage;

public class MainOptimizedLog
{
	private String findVersion(String sdata)
	{
		ProjectLoader pl = new ProjectLoader();
		String versions[] = pl.getRealVersions();
		String versionDates[] = pl.getRealVersionsDate();
		
		for (int i = 0; i < versionDates.length; i++)
		{
			if (sdata.compareToIgnoreCase(versionDates[i]) < 0)
				return versions[i].substring(11);
		}

		return "";
	}
	
	private void loadRevisionsForVersion(String filename, String version, List<Revision> revisions) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		
		String lastVersion = "";
		String lastRevision = "";
		List<String> lastClasses = new ArrayList<String>();
		
		while ((line = br.readLine()) != null) 
		{
			if (line.length() > 0)
			{
				String tokens[] = line.split(" ");
				
				String sdata = tokens[0];
				String revision = tokens[1];
				String clazz = tokens[3];
				
				clazz = clazz.substring(0, clazz.lastIndexOf('.'));

				if (revision.compareToIgnoreCase(lastRevision) != 0)
				{
					if (lastVersion.length() > 0 && lastVersion.compareToIgnoreCase(version) == 0)
						revisions.add(new Revision(lastClasses));
					
					lastVersion = findVersion(sdata);
					lastRevision = revision;
					lastClasses.clear();
				}
				
				lastClasses.add(clazz);
			}
		}

		if (lastVersion.length() > 0 && lastVersion.compareToIgnoreCase(version) == 0)
			revisions.add(new Revision(lastClasses));

		br.close();
	}
	
	public void publishVersionControlInformation(String name, Project project, List<Revision> revisions)
	{
		List<Integer> packagesAffected = new ArrayList<Integer>();
		int singlePackageRevisions = 0;
		int sumPackages = 0;
		double sumSquarePackages = 0;
		
		for (Revision revision : revisions)
		{
			packagesAffected.clear();
			int classCount = 0;
			
			for (String className : revision.getClasses())
			{
				ProjectClass _class = project.getClassName(className);
				
				if (_class != null)
				{
					ProjectPackage _package = _class.getPackage();
					
					int index = project.getIndexForPackage(_package);
					boolean found = false;
					
					for (int i = 0; i < packagesAffected.size(); i++)
						if (packagesAffected.get(i) == index)
							found = true;
					
					if (!found)
						packagesAffected.add(index);
				}
				
				classCount++;
			}
			
			int countAffected = packagesAffected.size();
			
			if (countAffected == 0 && classCount == 1)
				countAffected = 1;
			
			if (countAffected == 1)
				singlePackageRevisions++;
			
			sumPackages += countAffected;
			sumSquarePackages += countAffected * countAffected;
		}
		
		double avgPacksCommit = sumPackages / 64.0;
		double stdPacksCommit = Math.sqrt(sumSquarePackages / 64.0 - avgPacksCommit * avgPacksCommit);		
		System.out.println(name + "\t" + singlePackageRevisions + "\t" + avgPacksCommit + "\t" + stdPacksCommit);
	}
	
	public static void main(String[] args) throws IOException, XMLParseException
	{
		ProjectLoader loader = new ProjectLoader();
		MainOptimizedLog me = new MainOptimizedLog();
		
		List<Revision> revisions = new ArrayList<Revision>();
		me.loadRevisionsForVersion("data\\log\\log_summary.data", "1.9.0", revisions);
		
		List<Project> projectsEVM = loader.loadOptimizedVersionsEVM();
		
		for (Project project : projectsEVM)
			me.publishVersionControlInformation("EMVopt", project, revisions);

		List<Project> projectsMQ = loader.loadOptimizedVersionsMQ();

		for (Project project : projectsMQ)
			me.publishVersionControlInformation("MQopt", project, revisions);

		System.out.println("Finished!");
	}
}

class Revision
{
	private List<String> classes;
	
	Revision(List<String> revClasses)
	{
		classes = new ArrayList<String>();
		
		for (String className : revClasses)
			classes.add(className);
	}
	
	Iterable<String> getClasses()
	{
		return classes;
	}
}