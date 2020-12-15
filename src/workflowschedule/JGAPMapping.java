 package workflowschedule;

import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.DefaultFitnessEvaluator;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.EventManager;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.MutationOperator;
import org.jgap.util.ICloneable;

import Constraints.Makespan;
import Constraints.costConstraint;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflowfederation.FederationLog;
import workflownetworking.InternetEstimator;
import workflowschedule.iface.MSProviderAdapter;


public class JGAPMapping {
	public static int POP_SIZE = 50;
	public static int EVOLUTION_STEP = 150;
	
	public static final int INTERNAL_SOLUTION_NUMBER = 2;
//	public static final int SOLUTION_NUMBER = 10;
	public static int MUTATION = 0;
	public static double CROSSOVER = 0;
	public static Genotype population = null;
	static Configuration conf = null;
	
	public static Solution[] execute(MSExternalState state, List<Policy> policy, long randomSeed) {
		List<IMSProvider> providerList = state.getProviders();
		InternetEstimator internet = state.getInternet();
		IMSApplication application = state.getApplication();
		Solution sol[] = new Solution[INTERNAL_SOLUTION_NUMBER];
		try {
			Configuration conf = new InternalDefaultConfiguration();
			// making gene
			int providerNumber = providerList.size();
			List<MSApplicationNode> nodes = state.getApplication().getNodes();
			
			Gene[] genes = new Gene[nodes.size()];
//			System.out.println("基因的长度"+nodes.size());
//			genes[0] = new CIntegerGene(conf,0,0);
//			genes[nodes.size() - 1] = new CIntegerGene(conf,0,0);
			for (int i = 0; i < nodes.size(); i++) {
				//precondition: providerList is ordered
				//int firstInteger = providerList.get(0).getID();
				//int lastInteger =  providerList.get(providerList.size()-1).getID();
				genes[i] = new CIntegerGene(conf, 3, providerNumber+2);
				//genes[i] = new CIntegerGene(conf, firstInteger, lastInteger);
				//genes[i] = new CIntegerGene(conf, i, i);
			}
			
			IChromosome sampleCh = new Chromosome(conf, genes);
			conf.setSampleChromosome(sampleCh);
			conf.setPopulationSize(JGAPMapping.POP_SIZE);
			
			conf.setRandomGenerator(new CRandGenerator(providerNumber));
			
			MSFitnessFunction fitness = new MSFitnessFunction(state, policy);
			conf.setFitnessFunction(fitness);
			
			Genotype.setStaticConfiguration(conf);
			
			population = Genotype.randomInitialGenotype(conf);
			System.out.println("*** 开始调度迭代优化 ***");
			List<String> message = population.evolve(new Monitor(JGAPMapping.EVOLUTION_STEP));
			System.out.println("*** 结束调度迭代优化 ***");
			for(String s : message){
//				System.out.println("message size:"+message.size());
				System.out.println(s);
//				FederationLog.print(s);
			}
			//IChromosome bestSolutionSoFar = population.getPopulation().determineFittestChromosome();
			@SuppressWarnings("unchecked")
			List<IChromosome> list = population.getFittestChromosomes(JGAPMapping.INTERNAL_SOLUTION_NUMBER);
			IChromosome[] array = new IChromosome[list.size()];
			int k=0;
			for (IChromosome ic: list){ // converting list to array - not using list.toArray(array); because it will call wrong constructor for genes
				array[k] = ic;
				k++;
			}
			k = 0;
			
			boolean[] acceptable = selectingSatisfactorySolutions(array);
			
			for (int i=0; i<acceptable.length && k < JGAPMapping.INTERNAL_SOLUTION_NUMBER; i++) {
				if (acceptable[i]){
					Gene[] mygenes = array[i].getGenes();
					sol[k] = new Solution(array[i], nodes);
					sol[k].chromosome.setGenes(mygenes);
					sol[k].setCostAmount(calculateCostSolution(application,providerList,array[i],internet));
					sol[k].setMakespan(calculateMakespanSolution(application,providerList,array[i],internet));
					k++;
				}
			}
			if (k != JGAPMapping.INTERNAL_SOLUTION_NUMBER)
				System.out.println("\n\nAlert!!!! Not all solution were satisfactory\n");
			
			if (k == 0){
				System.out.println("并不是每个任务都成功执行。");
				for (int i=0; i<JGAPMapping.INTERNAL_SOLUTION_NUMBER && i<array.length; i++){
					Gene[] mygenes = array[i].getGenes();
					sol[i] = new Solution(array[i], nodes);
					sol[i].chromosome.setGenes(mygenes);
					sol[i].setCostAmount(calculateCostSolution(application,providerList,array[i],internet));
					sol[i].setMakespan(calculateMakespanSolution(application,providerList,array[i],internet));
				}
			}
			Configuration.reset();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}	
		return sol;
	}
	private static double calculateMakespanSolution(IMSApplication application, List<IMSProvider> providerList,IChromosome c,InternetEstimator internet) {
		Gene[] genes = c.getGenes();
		double tmp = 0;
		//tmp = WorkflowComputer.getFlowCompletionTime(application, providerList, internet);
		for(int j=0; j < genes.length; j++) {
			IMSProvider provider = MSProviderAdapter.findProviderById(providerList, (int) genes[j].getAllele());
			tmp += Makespan.calculateMakespan_Network(j, c, application, provider, internet);
		}
		return tmp;
	}
	
	private static double calculateCostSolution(IMSApplication application, List<IMSProvider> providerList, IChromosome c,InternetEstimator internet){
	//private static double calculateCostSolution(IChromosome c) {
		Gene[] genes = c.getGenes();
		double tmp = 0;
		for (int j=0; j<genes.length; j++){
			IMSProvider provider = MSProviderAdapter.findProviderById(providerList, (int) genes[j].getAllele());
			//tmp += ((CIntegerGene) genes[j]).getAllocationCost();
			tmp += costConstraint.calculateCost_Network(j, c, application, provider, internet);
//			tmp += BudgetConstraint.vmCost(nodes.get(j), provider, c);
		}
		return tmp;
	}
	
	
	private static boolean[] selectingSatisfactorySolutions(IChromosome[] solarray) {
		boolean[] accept = new boolean[solarray.length];
		for (int i=0; i<accept.length; i++){
			Gene[] mygenes = solarray[i].getGenes();
			boolean scarta = false;
			for (int j=0; j<mygenes.length && !scarta; j++){
				if (((CIntegerGene) mygenes[j]).getFitness() < 0.0){
					scarta = true;
				}
			}
			if (scarta == false){
				accept[i] = true;
			}
			else {
				accept[i] = false;
			}
		}
		return accept;
	}
}

class InternalDefaultConfiguration extends Configuration implements ICloneable {

	private static final long serialVersionUID = 1L;

	public InternalDefaultConfiguration() {
		super();
		if (JGAPMapping.MUTATION == 0 || JGAPMapping.CROSSOVER == 0){
			// throw new RuntimeException();
			JGAPMapping.MUTATION = 20;
			JGAPMapping.CROSSOVER = 0.4;
		}
		
		BestChromosomesSelector bestSelector;
		try {
			// setBreeder(new GABreeder());
			bestSelector = new BestChromosomesSelector(this, 0.90d);
			bestSelector.setDoubletteChromosomesAllowed(true);
			this.addNaturalSelector(bestSelector, false);
			this.setPreservFittestIndividual(true);
			// setMinimumPopSizePercent(0);
			// setSelectFromPrevGen(1.0d);
			setKeepPopulationSizeConstant(true);
			
			this.setEventManager(new EventManager());
			addGeneticOperator(new CrossoverOperator(this, JGAPMapping.CROSSOVER));
			addGeneticOperator(new MutationOperator(this, JGAPMapping.MUTATION)); // 0 disable the mutation
			this.setFitnessEvaluator(new DefaultFitnessEvaluator());
			this.setChromosomePool(new ChromosomePool());
			
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
