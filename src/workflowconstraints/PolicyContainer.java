package workflowconstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import workflowschedule.MSPolicy;
import workflowschedule.MSPolicy.ConstraintScope;


public class PolicyContainer {
	public static long highStorageValue = 0;
	public static int highRamValue = 0;
	public static int highCpuNumberValue = 0;
	public static long highProviderBwValue = 0;
	public static long highNetworkBwValue = 0; 
	public static double highNetworkLatencyValue = 0;
	//public static double highCostValueVm = 0; 

	
	private double weightSum = 0;
	private double weightNumber = 0;
	
	private final static Logger log = Logger.getLogger(PolicyContainer.class.getSimpleName());
	
	List<MSPolicy> list = null;
	
	public List<MSPolicy> getList() {
		return list;
	}

	public void setList(List<MSPolicy> list) {
		this.list = list;
	}

	public PolicyContainer(double[] weightVector){
		weightNumber = weightVector.length;
		list = new ArrayList<MSPolicy>(weightVector.length);
		this.calculateWeightSum(weightVector);
		log.setLevel(Level.WARNING);
	}
	
	private void calculateWeightSum(double[] weightVector){
		for (int i=0; i<weightVector.length; i++)
			weightSum+=weightVector[i];
	}
	
	public boolean add(MSPolicy p){
		if (list.size() < weightNumber){
			return list.add(p);
		}
		else {
			System.out.println("Impossible to add this policy");
			return false;
		}
	}
	
	public double calculateNormWeight(double weight) {
		double normWeight = (weightSum==0) ? weight : (weight / weightSum);
		return normWeight;
	}
	
	public MSPolicy cpuNumberConstraint(double weight){
		double normWeight = calculateNormWeight(weight);
		MSPolicy p = new CpuNumberConstraint(normWeight, highCpuNumberValue);
		log.info("Norm weight into " + p.getName() + " " + normWeight);
		return p;
	}
	
	public MSPolicy ramConstraint(double weight){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into ramConstraint " + normWeight);
		return new RamConstraint(normWeight, highRamValue);
	}
	
	public MSPolicy storageConstraint(double weight){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into storageConstraint " + normWeight);
		return new StorageConstraint(normWeight, highStorageValue);
	}
	
//	public MSPolicy locationConstraint(double weight){
//		double normWeight = calculateNormWeight(weight);
//		log.info("Norm weight into locationConstraint " + normWeight);
//		return new CityConstraint(normWeight);
//	}
	
	public MSPolicy providerId(double weight) {
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into providerConstraint " + normWeight);
		return new ProviderIdConstraints(weight);
	}
	
	public MSPolicy costPerResourceConstraint(double weight,ConstraintScope c){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into costPerResourceConstraint " + normWeight);
		//double [] highCostValue = {highCostValueCpu, highCostValueRam, highCostValueStorage};
		return new BudgetConstraint(normWeight,c);
	}
	
	public MSPolicy networkConstraint(double weightP,double weightN, ConstraintScope c){
		double normWeightP = calculateNormWeight(weightP);
		double normWeightN = calculateNormWeight(weightP);
		
		log.info("Norm weight into ProviderBwConstraint " + normWeightP);
		log.info("Norm weight into ProviderBwConstraint " + normWeightN);
		
		return new NetworkBWConstraint(normWeightP, normWeightN, highProviderBwValue, highNetworkBwValue, c);
	}
	
	public MSPolicy LatencyConstraint(double weight,ConstraintScope c) {
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into networkLatencyConstraint" + normWeight);
		return new LatencyConstraint(normWeight, highNetworkLatencyValue, c);
	}
	
}
