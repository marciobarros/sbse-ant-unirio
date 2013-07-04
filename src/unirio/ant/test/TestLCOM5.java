package unirio.ant.test;

import unirio.ant.controller.ClusteringCalculator;
import unirio.ant.model.Project;
import unirio.ant.model.ProjectClass;
import unirio.ant.model.ProjectPackage;

public class TestLCOM5
{
	private static void testaPrimeiroProjeto() throws Exception
	{
		Project project = new Project("test");
		ProjectPackage p1 = project.addPackage("p1");
		
		ProjectClass a1 = new ProjectClass("a1", p1);
		a1.addDependency("a2");
		project.addClass(a1);

		ProjectClass a2 = new ProjectClass("a2", p1);
		a2.addDependency("a1");
		project.addClass(a2);

		ProjectClass a3 = new ProjectClass("a3", p1);
		project.addClass(a3);

		ProjectClass a4 = new ProjectClass("a4", p1);
		project.addClass(a4);

		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		if (Math.abs(cc.calculateLCOM5() - 0.8333) > 0.0001) throw new Exception("Erro no cálculo do LCOM5");
	}

	private static void testaSegundoProjeto() throws Exception
	{
		Project project = new Project("test");
		ProjectPackage p1 = project.addPackage("p1");
		
		ProjectClass a1 = new ProjectClass("a1", p1);
		a1.addDependency("a2");
		project.addClass(a1);

		ProjectClass a2 = new ProjectClass("a2", p1);
		a2.addDependency("a1");
		project.addClass(a2);

		ProjectClass a3 = new ProjectClass("a3", p1);
		a2.addDependency("a4");
		project.addClass(a3);

		ProjectClass a4 = new ProjectClass("a4", p1);
		project.addClass(a4);

		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		if (Math.abs(cc.calculateLCOM5() - 0.75) > 0.0001) throw new Exception("Erro no cálculo do LCOM5");
	}

	private static void testaTerceiroProjeto() throws Exception
	{
		Project project = new Project("test");
		ProjectPackage p1 = project.addPackage("p1");
		
		ProjectClass a1 = new ProjectClass("a1", p1);
		a1.addDependency("a2");
		project.addClass(a1);

		ProjectClass a2 = new ProjectClass("a2", p1);
		a2.addDependency("a1");
		a2.addDependency("a3");
		project.addClass(a2);

		ProjectClass a3 = new ProjectClass("a3", p1);
		a2.addDependency("a4");
		project.addClass(a3);

		ProjectClass a4 = new ProjectClass("a4", p1);
		project.addClass(a4);

		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		if (Math.abs(cc.calculateLCOM5() - 0.6666) > 0.0001) throw new Exception("Erro no cálculo do LCOM5");
	}

	private static void testaQuartoProjeto() throws Exception
	{
		Project project = new Project("test");
		ProjectPackage p1 = project.addPackage("p1");
		
		ProjectClass a1 = new ProjectClass("a1", p1);
		a1.addDependency("a2");
		a1.addDependency("a3");
		a1.addDependency("a4");
		project.addClass(a1);

		ProjectClass a2 = new ProjectClass("a2", p1);
		a2.addDependency("a1");
		a2.addDependency("a3");
		a2.addDependency("a4");
		project.addClass(a2);

		ProjectClass a3 = new ProjectClass("a3", p1);
		a3.addDependency("a1");
		a3.addDependency("a2");
		a3.addDependency("a4");
		project.addClass(a3);

		ProjectClass a4 = new ProjectClass("a4", p1);
		a4.addDependency("a1");
		a4.addDependency("a2");
		a4.addDependency("a3");
		project.addClass(a4);

		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		if (Math.abs(cc.calculateLCOM5() - 0.0) > 0.0001) throw new Exception("Erro no cálculo do LCOM5");
	}

	public static final void main(String[] args) throws Exception
	{
		testaPrimeiroProjeto();
		testaSegundoProjeto();
		testaTerceiroProjeto();
		testaQuartoProjeto();
		System.out.print("Sucesso");
	}
}