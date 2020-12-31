package Constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import workflowschedule.Policy;

public class PolicyContainer {
	//虚拟机计算性能
	public static long highStorageValue = 0;
	public static int highRamValue = 0;
	public static int highCoreNumberValue = 0;
	//网络带宽
	public static long highProviderBwValue = 0;
	public static long highNetworkBwValue = 0; 
	//延迟
	public static double highNetworkLatencyValue = 0;
	
	private double weightSum = 0;
	private double weightNumber = 0;
	
	private final static Logger log = Logger.getLogger(PolicyContainer.class.getSimpleName());
	
	List<Policy> list = null;
	
	public List<Policy> getList() {
		return list;
	}

	public void setList(List<Policy> list) {
		this.list = list;
	}
	
	public PolicyContainer(double[] weightVector){
		weightNumber = weightVector.length;
		list = new ArrayList<Policy>(weightVector.length);
		this.calculateWeightSum(weightVector);
		log.setLevel(Level.WARNING);
	}
	
	private void calculateWeightSum(double[] weightVector){
		for (int i=0; i<weightVector.length; i++)
			weightSum+=weightVector[i];
	}
	
	public boolean add(Policy p){
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
	
	public Policy costssPolicy(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new WorkflowParameterConstraints.costConstraint(weight);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy makespanPolicy(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new WorkflowParameterConstraints.makespanConstraint(weight);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
//	public Policy providerIdsPloPolicy(double weight) {
////		double normWeight = calculateNormWeight(weight);
//		Policy p = new WorkflowParameterConstraints.providerIdConstraint(weight);
//		log.info("Norm weight into " + p.getName() + " " + weight);
//		return p;
//	}
	
	public Policy CoreNumberConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new CoreNumberConstraint(weight,highCoreNumberValue);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy RamConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new RamConstraint(weight,highRamValue);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy StorageConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new StorageConstraint(weight,highStorageValue);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy providerBwConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new providerBwConstraint(weight,highProviderBwValue);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy IntercloudBwConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new IntercloudBwConstraint(weight,highNetworkBwValue);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy latencyConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new LatencyConstraint(weight,highNetworkLatencyValue);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy costConstraints(double weight) {
//		double normWeight = calculateNormWeight(weight);
		Policy p = new costConstraint(weight);
		log.info("Norm weight into " + p.getName() + " " + weight);
		return p;
	}
	
	public Policy providerIDConstraints(double weight) {
		double normWeight = calculateNormWeight(weight);
		Policy p = new providerIDConstraint(normWeight);
		log.info("Norm weight into " + p.getName() + " " + normWeight);
		return p;
	}
	
	@Override
	public String toString() {
		String s = "[MakePolicy] HiStorage HiRam HiCpuNum HiNetLatency HiProviBw HiNetBw " + "\n";
		s += "[MakePolicy] " + highStorageValue/1024/1024 + "TB" + " " + highRamValue/1024 + "GB" + " " + highCoreNumberValue  +  " " 
							+ highNetworkLatencyValue + "s" +" " + highProviderBwValue/1024/1024 + "MB/s " + highNetworkBwValue/1024/1024 + "MB/s";
		return s;
	}
}
