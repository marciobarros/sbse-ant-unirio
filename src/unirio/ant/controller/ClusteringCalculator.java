package unirio.ant.controller;

import unirio.ant.model.Project;
import unirio.ant.model.ProjectClass;

/**
 * DEFINIC�ES:
 * 
 * - acoplamento = n�mero de depend�ncias que as classes de um pacote possuem com classes de fora
 *   do pacote. Deve ser minimizado.
 *   
 * - coes�o = n�mero de depend�ncias que as classes de um pacote possuem com outras classes do
 *   mesmo pacote. Deve ser maximizado (ou seja, minimizamos seu valor com sinal invertido)
 *   
 * - spread = partindo de zero e percorrendo cada pacote, acumula o quadrado da diferen�a entre
 *   o n�mero de classes do pacote e o n�mero de classes do menor pacote
 *   
 * - diferenca = diferen�a entre o n�mero m�ximo de classes em um pacote e o n�mero m�nimo de
 *   classes em um pacote
 * 
 * @author Marcio Barros
 */
public class ClusteringCalculator
{
	private int classCount;
	private int packageCount;

	private int[][] dependencies;
	private int[] originalPackage;
	private int[] newPackage;
	private int[] originalClasses;
	private int[] newClasses;

	private int minClasses;
	private int maxClasses;
	private double[] classProbability;

	/**
	 * Inicializa o calculador de acoplamento
	 */
	public ClusteringCalculator(Project project, int packageCount) throws Exception
	{
		this.classCount = project.getClassCount();
		this.packageCount = packageCount;
		this.minClasses = this.maxClasses = 0;
		this.classProbability = null;
		prepareClasses(project);
	}
	
	/**
	 * Prepara as classes para serem processadas pelo programa
	 */
	public void prepareClasses(Project project) throws Exception
	{
		this.dependencies = new int[classCount][classCount];
		
		this.originalPackage = new int[classCount];
		this.newPackage = new int[classCount];
		
		this.originalClasses = new int[packageCount];
		this.newClasses = new int[packageCount];

		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
			int sourcePackageIndex = project.getIndexForPackage(_class.getPackage());
			
			this.originalPackage[i] = sourcePackageIndex;
			this.newPackage[i] = sourcePackageIndex;
			this.originalClasses[sourcePackageIndex]++;
			this.newClasses[sourcePackageIndex]++; 

			for (int j = 0; j < _class.getDependencyCount(); j++)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
				if (classIndex == -1)
					throw new Exception ("Class not registered in project: " + targetName);
				
				dependencies[i][classIndex]++;
			}
		}
	}

	/**
	 * Estima as probabilidades de distribui��o de classes 
	 */
	public void setClassDistributionProbabilities(int min, int expected, int max)
	{
		// guarda os par�metros de distribui��o de classes
		this.minClasses = min;
		this.maxClasses = max;
		this.classProbability = new double[max-min];
		
		// calcula a altura da distribui��o triangular
		double height = 2.0 / (max - min); 
		
		// calcula a equa��o da reta da esquerda
		double al = height / (expected - min);
		double bl = -min * al;
		
		// calcula a equa��o da reta da direita
		double ar = -height / (max - expected);
		double br = -max * ar;
		
		// estima as probabilidades
		for (int i = min; i < expected; i++)
			this.classProbability[i-min] = al / 2 * (i + 1) * (i + 1) + bl * (i + 1) - al / 2 * i * i - bl * i;
		
		for (int i = expected; i < max; i++)
			this.classProbability[i-min] = ar / 2 * (i + 1) * (i + 1) + br * (i + 1) - ar / 2 * i * i - br * i;
	}

	/**
	 * Estima as probabilidades de distribui��o de classes 
	 */
	private double calculateClassCountProbabilities(int count)
	{
		if (count < this.minClasses || count >= this.maxClasses)
			return 0.0;
		else
			return classProbability[count - this.minClasses]; 
	}

	/**
	 * Inicializa o processo de c�lculo
	 */
	public void reset()
	{
		for (int i = 0; i < classCount; i++)
			newPackage[i] = originalPackage[i];

		for (int i = 0; i < packageCount; i++)
			newClasses[i] = originalClasses[i];
	}

	/**
	 * Move uma classe para um pacote
	 */
	public void moveClass(int classIndex, int packageIndex)
	{
		int actualPackage = newPackage[classIndex];
		
		if (actualPackage != packageIndex)
		{
			newClasses[actualPackage]--;
			newPackage[classIndex] = packageIndex;
			newClasses[packageIndex]++;
		}
	}
	
	/**
	 * Returns the package of a given class
	 */
	public int getPackage(int classIndex)
	{
		return newPackage[classIndex];
	}

	/**
	 * Retorna o n�mero de classes de um pacote
	 */
	public int getClassCount(int packageIndex)
	{
		return newClasses[packageIndex];
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	private int getMinimumClassCount()
	{
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];

			if (count < min)
				min = count;
		}

		return min;
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	private int getMaximumClassCount()
	{
		int max = Integer.MIN_VALUE;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];

			if (count > max)
				max = count;
		}

		return max;
	}

	/**
	 * Retorna o n�mero de pacotes com mais de uma classe
	 */
	public int getPackageCount()
	{
		int packages = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (newClasses[i] > 0)
				packages++;

		return packages;
	}

	/**
	 * Calcula o numero de depend�ncias com origem em um dado pacote e t�rmino em outro de um pacote
	 */
	protected int countOutboundEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					edges++;
		}

		return edges;
	}

	/**
	 * Calcula o numero de depend�ncias com um pacote e t�rmino em um dado pacote
	 */
	protected int countInboundEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == packageIndex)
					edges++;
		}

		return edges;
	}
	
	/**
	 * Calcula o n�mero de depend�ncias internas de um pacote
	 */
	protected int countIntraEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == currentPackage)
					edges++;
		}

		return edges;
	}

	/**
	 * Retorna a dispers�o da distribui��o de classes em pacotes
	 */
	public int calculateDifference()
	{
		int min = getMinimumClassCount();
		int max = getMaximumClassCount();
		return max - min;
	}

	/**
	 * Retorna a dispers�o da distribui��o de classes em pacotes
	 */
	public double calculateSpread()
	{
		int min = getMinimumClassCount();
		double spread = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];
			spread += Math.pow(count - min, 2);
		}

		return spread;
	}

	/**
	 * Calcula o acoplamento do projeto
	 */
	public int calculateCoupling()
	{
		int coupling = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					coupling += 2;
		}

		return coupling;
	}

	/**
	 * Calcula a coes�o do projeto
	 */
	public int calculateCohesion()
	{
		int cohesion = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == currentPackage)
					cohesion++;
		}

		return cohesion;
	}

	/**
	 * Calcula o coeficiente de modularidade do projeto
	 */
	public double calculateModularizationQuality()
	{
		int[] inboundEdges = new int[packageCount];
		int[] outboundEdges = new int[packageCount];
		int[] intraEdges = new int[packageCount];

		for (int i = 0; i < classCount; i++)
		{
			int sourcePackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
			{
				if (dependencies[i][j] > 0)
				{
					int targetPackage = newPackage[j];
					
					if (targetPackage != sourcePackage)
					{
						outboundEdges[sourcePackage]++;
						inboundEdges[targetPackage]++;
					}
					else
						intraEdges[sourcePackage]++;
				}
			}
		}
		
		double mq = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int inter = inboundEdges[i] + outboundEdges[i];
			int intra = intraEdges[i];
			
			if (intra != 0 && inter != 0)
			{
				double mf = intra / (intra + 0.5 * inter);
				mq += mf;
			}
		}

		return mq;
	}

	/**
	 * Calcula o EVM do projeto
	 */
	public int calculateEVM()
	{
		int evm = 0;

		for (int i = 0; i < classCount-1; i++)
		{
			int sourcePackage = newPackage[i];
			
			for (int j = i+1; j < classCount; j++)
			{
				int targetPackage = newPackage[j];

				if (targetPackage == sourcePackage)
					if (dependencies[i][j] > 0 || dependencies[j][i] > 0) 
						evm++;
					else
						evm--; 
			}
		}

		return evm;
	}

	/**
	 * Calcula o fator de distribui��o de classes 
	 */
	public double calculateClassDistributionFactor()
	{
		double cdf = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int count = getClassCount(i);
			cdf += calculateClassCountProbabilities(count);
		}

		return cdf;
	}
	
	/**
	 * Calcula o desvio padr�o do n�mero de dependencias internas de um pacote
	 */
	public double calculateCohesionElegance()
	{
		StatisticsCalculator statistics = new StatisticsCalculator();

		for (int i = 0; i < packageCount; i++)
			statistics.add(countInboundEdges(i));		

		return statistics.standardDeviation();
	}
	
	/**
	 * calcula o desvio padr�o do n�mero de depend�ncias externas de cada pacote 
	 */
	public double calculateCouplingElegance()
	{
		StatisticsCalculator statistics = new StatisticsCalculator();

		for (int i = 0; i < packageCount; i++)
			statistics.add(countOutboundEdges(i));

		return statistics.standardDeviation();
	}
		
	/**
	 * Calcula o desvio padr�o do n�mero de classes por pacote
	 */
	public double calculateClassElegance()
	{
		StatisticsCalculator statistics = new StatisticsCalculator();

		for (int i = 0; i < packageCount; i++)
			statistics.add(getClassCount(i));

		return statistics.standardDeviation();
	}

	/**
	 * Retorna as classes que pertencem a um pacote, dado seu �ndice
	 */
	private int[] getPackageClasses(int packageIndex)
	{
		int classesOnPackage = getClassCount(packageIndex);
		int[] _classes = new int[classesOnPackage];
		int walker = 0;
		
		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
				_classes[walker++] = i;
		}

		return _classes;
	}
	
	/**
	 * Calcula o LCOM5 de um pacote, dado seu �ndice
	 */
	private double calculateLCOM5(int packageIndex)
	{
		int classesOnPackage = getClassCount(packageIndex);
		int[] _classes = getPackageClasses(packageIndex);
		int[] usage = new int[classesOnPackage];
		
		for (int i = 0; i < classesOnPackage; i++)
		{
			int classI = _classes[i];
			
			for (int j = 0; j < classesOnPackage; j++)
			{
				int classJ = _classes[j];
				
				if (dependencies[classI][classJ] > 0)
					usage[i]++;
			}
		}
		
		double sum = 0.0;
		
		for (int i = 0; i < classesOnPackage; i++)
			sum += usage[i] + 1;
		
		double classSquare = classesOnPackage * classesOnPackage;
		return (sum - classSquare) / (classesOnPackage - classSquare);
	}
	
	/**
	 * Calcula o LCOM5 do projeto
	 */
	public double calculateLCOM5()
	{
		double sum = 0.0;
		int count = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (getClassCount(i) > 1)
			{
				sum += calculateLCOM5(i);
				count++;
			}
		
		return sum / count;
	}

	/**
	 * Calcula o CBO de um pacote, dado seu �ndice
	 */
	public int calculateCBO(int packageIndex)
	{
		int cbo = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
			{
				for (int j = 0; j < classCount; j++)
					if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
						cbo++;
			}
		}

		return cbo;
	}

	/**
	 * Calcula o CBO de um projeto
	 */
	public double calculateCBO()
	{
		double sum = 0.0;
		int count = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (getClassCount(i) > 0)
			{
				sum += calculateCBO(i);
				count++;
			}
		
		return sum / count;
	}
}