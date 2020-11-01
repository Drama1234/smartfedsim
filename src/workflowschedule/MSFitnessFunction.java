package workflowschedule;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflowschedule.iface.MSProviderAdapter;


public class MSFitnessFunction extends FitnessFunction{
	
	public final static int AWARD = 100;
	private final double EQUALITY = 0.0001;
	private final static Logger log = Logger.getLogger(Logger.class.getName());
	
	private static final long serialVersionUID = 1L;

	static private MSExternalState _state;
	static private List<MSPolicy> policy;

	private HashMap<Integer, Integer> association;
	
	public MSFitnessFunction(MSExternalState state, List<MSPolicy> policyList){
		_state = state;
		policy = policyList;
	}
	
	private double evaluateGene(int gene_index, IChromosome chromos){
		double fitness = -1;
		double [] weightedDistance = new double[policy.size()];
		Gene[] genes = chromos.getGenes();
		
		Integer providerID = (Integer) genes[gene_index].getAllele();
		IMSProvider provider = MSProviderAdapter.findProviderById(_state.getProviders(), providerID);
		for (int i = 0; i < policy.size(); i++) {
			weightedDistance[i] = policy.get(i).evaluatePolicy(gene_index, chromos, _state.getApplication(), provider, _state.getInternet());
		}
		for (int i=0; i<weightedDistance.length; i++){
			if (weightedDistance[i] > 0){
				weightedDistance[i] = 0;
				fitness = 0; // for construction, distances in the positive space are not good, not satisfying constraints as inequality
			}
			else if (weightedDistance[i] == 0){
				weightedDistance[i] = EQUALITY;
			}
			else {
				weightedDistance[i] *= -1; // absolute value of negative numbers
			}	
		}
		
		if (fitness != 0){
			fitness = 0;
			for (int i=0; i<weightedDistance.length; i++)
				fitness += weightedDistance[i]; 
		}
		return fitness;
	}
	
	
	@Override
	protected double evaluate(IChromosome chromos) {
		log.setLevel(Level.INFO);
		double fitness = 0;
		Gene[] genes = chromos.getGenes();
		double g_fit = 0;
		
		for (int i = 0; i < genes.length; i++) {
			g_fit = evaluateGene(i, chromos) * AWARD;
			((CIntegerGene) genes[i]).setFitness(g_fit);
			fitness += g_fit;
		}
		
		if (MSPolicy.DEBUG)
			printGenes(chromos, fitness);
		
		return fitness;
	}
	
	void printGenes(IChromosome chromos, double fitness){
		Gene[] genes = chromos.getGenes();
		System.out.print("基因的适应度值: ");
		for (int i = 0; i < genes.length; i++) {
			System.out.print(((CIntegerGene) genes[i]).getFitness() + " | ");
		}
		System.out.println();
		System.out.println("\t染色体 " + Monitor.chromosomeToString(chromos) + " 的适应度值: " + fitness);
		System.out.println();
	}
}
