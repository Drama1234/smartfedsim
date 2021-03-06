package old.metascheduler.constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import old.metaschedulers.MSPolicy;


public class PolicyContainer {
	public static long highStorageValue = 0;
	public static long highBwValue = 0; 
	public static int highRamValue = 0;
	public static double highCostValueRam = 0; 
	public static double highCostValueStorage = 0;
	public static double highCostValueVm = 0; 
	public static double highCpuNumberValue = 0;
	
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
	
	private double calculateNormWeight(double weight){
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
	
	public MSPolicy locationConstraint(double weight){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into locationConstraint " + normWeight);
		return new CountryConstraint(normWeight);
	}
	
	
	public MSPolicy costVmConstraint(double weight){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into costVmConstraint " + normWeight);
		return new CostPerVmConstraint(normWeight, highCostValueVm);
	}
	
	
	public MSPolicy costPerResourceConstraint(double weight, ConstraintScope scope){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into costPerResourceConstraint " + normWeight);
		double [] highCostValue = {highCostValueRam, highCostValueStorage};
		return new BudgetConstraint(normWeight, highCostValue, scope);
	}
	
	public MSPolicy storageConstraint(double weight){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into storageConstraint " + normWeight);
		return new StorageConstraint(normWeight, highStorageValue);
	}
	
	public MSPolicy networkConstraint(double weight, ConstraintScope c){
		double normWeight = calculateNormWeight(weight);
		log.info("Norm weight into networkConstraint " + normWeight);
		return new NetworkConstraint(normWeight, highBwValue, c);
	}
	
	public String toString(){
		String s = "[MakePolicy] HiStorage HiRam HiCost" + "\n";
		s += "[MakePolicy] " + highStorageValue + " " + highRamValue + " " +  highCostValueRam + "+" + highCostValueStorage;
		return s;
	}
}
