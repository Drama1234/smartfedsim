package WorkflowParameterConstraints;

import java.util.List;

import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.Policy;

public class providerIdConstraint extends Policy{
	public providerIdConstraint(double weight) {
		super(weight, Policy.DESCENDENT_TYPE);
		this.constraintName = "providerIdConstraint";
	}
	
	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		
		String nodeProviderId = node.getCharacteristic().get(Constant.providerID).toString(); //what I want
		String provProviderId = prov.getCharacteristic().get(Constant.providerID).toString();
		double distance = 0;
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		
		if(nodeProviderId.equals("-1")) {
			distance = 0;
		}else if(nodeProviderId == provProviderId) {
			distance = -1;
		}else {
			distance = 1;
		}
		
		if (DEBUG)
			System.out.println("\tEvaluation on providerId: " + nodeProviderId + " vs " + provProviderId + "=" + distance);
		
		return distance * getWeight();	
	}
}
