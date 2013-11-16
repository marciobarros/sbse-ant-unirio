package unirio.ant.calc.revision;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import unirio.ant.calc.loader.ProjectLoader;

public class MainVersionLog
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
	
	private boolean hasPackage(List<String> packages, String packageName)
	{
		for (String name : packages)
			if (name.compareToIgnoreCase(packageName) == 0)
				return true;
		
		return false;
	}
	
	private void saveRevisionsByVersion(String filename, PrintStream ps) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		
		String lastVersion = "";
		String lastRevision = "";
		String lastAuthor = "";
		int lastClasses = 0;
		List<String> lastPackages = new ArrayList<String>();
		ps.println("version\tauthor\tclasses\tpackages");
		
		while ((line = br.readLine()) != null) 
		{
			if (line.length() > 0)
			{
				String tokens[] = line.split(" ");
				
				String sdata = tokens[0];
				String revision = tokens[1];
				String author = tokens[2];
				String clazz = tokens[3];
				
				clazz = clazz.substring(0, clazz.lastIndexOf('.'));
				//String className = clazz.substring(clazz.lastIndexOf('.') + 1);
				String packageName = clazz.substring(0, clazz.lastIndexOf('.'));

				if (revision.compareToIgnoreCase(lastRevision) != 0)
				{
					if (lastVersion.length() > 0)
						ps.println(lastVersion + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());
					
					lastVersion = findVersion(sdata);
					lastRevision = revision;
					lastAuthor = author;
					lastClasses = 0;
					lastPackages.clear();
				}
				
				if (!hasPackage(lastPackages, packageName))
					lastPackages.add(packageName);
				
				lastClasses++;
			}
		}

		if (lastVersion.length() > 0)
			ps.println(lastVersion + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());

		br.close();		
	}
	
	public static void main(String[] args) throws IOException
	{		
		FileOutputStream out = new FileOutputStream("results\\versioncontrol\\log_versions.data"); 
		PrintStream ps = new PrintStream(out);
		
		MainVersionLog me = new MainVersionLog();
		me.saveRevisionsByVersion("data\\log\\log_summary.data", ps);

		ps.close();
		System.out.println("Finished!");
	}
}