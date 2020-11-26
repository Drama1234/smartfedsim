package Constraints;

import java.util.List;

import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.Policy;

public class providerBwConstraint extends Policy{
	private static double highProviderBwValue;
	
	public providerBwConstraint(double normWeight, double highestValue) {
		super(normWeight, Policy.ASCENDENT_TYPE);
		highProviderBwValue = highestValue;
		this.constraintName = "ProviderBwConstraint";
	}

	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		
		long nodeBW = (long)node.getNetwork().getCharacteristic().get(Constant.BW);//what I want
		long provBW = (long)prov.getNetwork().getCharacteristic().get(Constant.BW);//what I have
		
		double distance = super.calculateDistance_ErrHandling(provBW, nodeBW, highProviderBwValue);
		
		return distance * getWeight();
	}
}
