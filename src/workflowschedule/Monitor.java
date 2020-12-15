package workflowschedule;

import java.util.List;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.audit.IEvolutionMonitor;
import org.jgap.eval.PopulationHistoryIndexed;

import workflowfederation.FederationLog;

public class Monitor implements IEvolutionMonitor{

	private static final long serialVersionUID = 1L;
	private int maxIteration = 0;
	private int iterationCount = 0;
	
	private long startMillisec;
	
	public Monitor(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	@Override
	public void event(String arg0, int arg1, Object[] arg2) {		
	}

	@Override
	public PopulationHistoryIndexed getPopulations() {
		return null;
	}
	
	public void printPopulation(Population p) {
		System.out.println("Iteration: " + iterationCount);
		List<IChromosome> chlist = p.getChromosomes();
		for(IChromosome ch : chlist) {
			System.out.println(chromosomeToString(ch));
		}
	}
	
	public static String chromosomeToString(IChromosome ch) {
		String s = new String();
		Gene[] genes = ch.getGenes();
		s += "|";
		for (int i=0; i<genes.length; i++) {
			s+=genes[i].getAllele();
			s+="|";
		}
		s+= "  age:";
		s+= ch.getAge();
		return s;
	}
	
	@Override
	public boolean nextCycle(Population arg0, List<String> arg1) {
		iterationCount++;
		FederationLog.print("[MONITOR] (nextCycle) Iteration: " + iterationCount);
//		System.out.println("[MONITOR] (nextCycle) Iteration: " + iterationCount);
		List<IChromosome> chlist = arg0.getChromosomes();
		if (Policy.DEBUG) { 
//		if(true){
			for(IChromosome ch : chlist) {
				System.out.println(chromosomeToString(ch));
			}
		}
		
		double maxFitPopul = arg0.determineFittestChromosome().getFitnessValue();
		double numGenes = arg0.getChromosome(0).getGenes().length;
		double acceptableFitness = (numGenes * MSFitnessFunction.AWARD);
//		System.out.println("iterationCount："+iterationCount);
//		System.out.println("maxIteration："+maxIteration);
		if (iterationCount >= maxIteration) {
			arg1.add("\n[MONITOR] \nIterations: " + iterationCount + "/" + maxIteration);
			arg1.add("BestFitness " + maxFitPopul + " vs Expected " + acceptableFitness);
			arg1.add("ExecutionTime: " + (System.currentTimeMillis() - startMillisec)/ new Double(1000) + "\n");
			return false;
		}
		return true;
	}

	@Override
	public void start(Configuration arg0) {
		startMillisec = System.currentTimeMillis();
	}
}
