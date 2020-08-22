package workflowconstraints;

import org.jgap.Gene;
import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class CpuNumberConstraint extends MSPolicy{
	private static double highCpuValue;

	public static double getHighCpuValue() {
		return highCpuValue;
	}

	public static void setHighCpuValue(double highCpuValue) {
		CpuNumberConstraint.highCpuValue = highCpuValue;
	}
	
	public CpuNumberConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE);
		this.constraintName = "CpuNumber";
		highCpuValue = highestValue;
	}
	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		long nodeCPU = (Integer) node.getComputing().getCharacteristic().get(Constant.CPU_NUMBER); //what I want
		long provCPU = (Integer) prov.getComputing().getCharacteristic().get(Constant.CPU_NUMBER); //what I have
		
		double distance = calculateDistance_ErrHandling(provCPU, nodeCPU, highCpuValue);
		return distance * getWeight();
	}

	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,
			InternetEstimator internet) {
		return 0.0;
	}
	
}
