package Constraints;

import java.util.List;

import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.Policy;

public class CoreNumberConstraint extends Policy{
	private static double highCoreNumberValue;
	
	public CoreNumberConstraint(double normWeight,double highestValue) {
		super(normWeight, Policy.ASCENDENT_TYPE);
		highCoreNumberValue = highestValue;
		this.constraintName = "CpuConstraint";
	}

	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		long nodeCPU = (Integer) node.getComputing().getCharacteristic().get(Constant.CPU_NUMBER); //what I want
		long provCPU = (Integer) prov.getComputing().getCharacteristic().get(Constant.CPU_NUMBER); //what I have
		
		double distance = super.calculateDistance_ErrHandling(provCPU, nodeCPU, highCoreNumberValue);
		
		System.out.println("基因内核值："+distance * getWeight());
		
		return distance * getWeight();
	}
}
