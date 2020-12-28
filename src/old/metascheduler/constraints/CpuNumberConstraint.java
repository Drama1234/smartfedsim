package old.metascheduler.constraints;

import org.jgap.Gene;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import old.metaschedulers.MSPolicy;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;

public class CpuNumberConstraint extends MSPolicy{
	private static double highCpuValue;


	public CpuNumberConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE);
		this.constraintName = "CpuNumber";
		highCpuValue = highestValue;
	}

	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		double nodeCPU = (Integer) node.getComputing().getCharacteristic().get(Constant.CPU_NUMBER); //what I want
		double provCPU = (Integer) prov.getComputing().getCharacteristic().get(Constant.CPU_NUMBER); //what I have
		
		double distance = calculateDistance_ErrHandling(provCPU, nodeCPU, highCpuValue);
		return distance * getWeight();
	}
}
