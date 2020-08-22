package workflowschedule;

import java.util.ArrayList;
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

import it.cnr.isti.smartfed.metascheduler.JGAPMapping;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;


public class JGAPMapping {
	public static int POP_SIZE = 100;
	public static int EVOLUTION_STEP = 200;
	
	public static final int INTERNAL_SOLUTION_NUMBER = 10;
	public static final int SOLUTION_NUMBER = 5;
	public static int MUTATION = 0;
	public static double CROSSOVER = 0;
	public static Genotype population = null;
	static Configuration conf = null;
	
	public static Solution[] execute(MSExternalState state, List<MSPolicy> policy, long randomSeed) {
		List<IMSProvider> providerList = state.getProviders();
		Solution sol[] = new Solution[SOLUTION_NUMBER];
		try {
			Configuration conf = new InternalDefaultConfiguration();
			// making gene
			int providerNumber = providerList.size();
			List<MSApplicationNode> nodes = state.getApplication().getNodes();
			List<MSApplicationNode> appnode = new ArrayList<MSApplicationNode>();
			for (MSApplicationNode node : nodes) {
				if(node.getID()!=0 && node.getID()!= nodes.size()-1) {
					appnode.add(node);
				}
			}
			Gene[] genes = new Gene[appnode.size()];
			//provider有序
			for (int i = 0; i < appnode.size(); i++){
				int firstInteger = providerList.get(0).getID();
				int lastInteger =  providerList.get(providerList.size()-1).getID();
				genes[i] = new CIntegerGene(conf, firstInteger, lastInteger);
			}
			
			IChromosome sampleCh = new Chromosome(conf, genes);
			conf.setSampleChromosome(sampleCh);
			conf.setPopulationSize(JGAPMapping.POP_SIZE);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
}

class InternalDefaultConfiguration extends Configuration implements ICloneable {

	private static final long serialVersionUID = 1L;

	public InternalDefaultConfiguration() {
		super();
		if (JGAPMapping.MUTATION == 0 || JGAPMapping.CROSSOVER == 0){
			// throw new RuntimeException();
			JGAPMapping.MUTATION = 10;
			JGAPMapping.CROSSOVER = 0.35;
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
