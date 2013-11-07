package unirio.ant.architecture;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MainRevisionParser
{
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
	
	private String findVersion(String sdata)
	{
		ProjectLoader pl = new ProjectLoader();
		String versions[] = pl.getRealVersions();
		
		for (int i = 0; i < VERSION_DATES.length; i++)
		{
			if (sdata.compareToIgnoreCase(VERSION_DATES[i]) < 0)
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
	
	private void saveRevisionsByYear(String filename, PrintStream ps) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		
		int lastYear = 0;
		String lastRevision = "";
		String lastAuthor = "";
		int lastClasses = 0;
		List<String> lastPackages = new ArrayList<String>();
		ps.println("year\tauthor\tclasses\tpackages");
		
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
					if (lastYear > 0)
						ps.println(lastYear + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());
					
					lastYear = Integer.parseInt(sdata.substring(0, 4));
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

		if (lastYear > 0)
			ps.println(lastYear + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());

		br.close();		
	}

	public static void main(String[] args) throws IOException
	{
		MainRevisionParser mrp = new MainRevisionParser();
		
		FileOutputStream out1 = new FileOutputStream("log_years.data"); 
		PrintStream ps1 = new PrintStream(out1);
		mrp.saveRevisionsByYear("log_summary.data", ps1);
		ps1.close();

		FileOutputStream out2 = new FileOutputStream("log_versions.data"); 
		PrintStream ps2 = new PrintStream(out2);
		mrp.saveRevisionsByVersion("log_summary.data", ps2);
		ps2.close();
		
		System.out.println("Finished!");
	}
}