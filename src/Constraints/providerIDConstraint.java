package Constraints;

import java.util.List;

import org.jgap.IChromosome;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.Policy;

public class providerIDConstraint extends Policy{

	public providerIDConstraint(double weight) {
		super(weight, Policy.EQUAL_TYPE);
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
//		System.out.println("云服务供应商Id:"+nodeProviderId);
		if(nodeProviderId.equals("-1")) {
			distance = 0;
		}else if(nodeProviderId == provProviderId) {
			distance = -1;
		}else {
			distance = 1;
		}
//		if(nodeProviderId.equals("-1")) {
//			distance = 0;
//		} else if(nodeProviderId == provProviderId) {
//			distance = calculateDistance(nodeProviderId, provProviderId);
//		}
		
		if (DEBUG)
			System.out.println("\tEvaluation on providerId: " + nodeProviderId + " vs " + provProviderId + "=" + distance);
		
		return distance * getWeight();	
	}
	
	private double calculateDistance(String nodeProviderId,String provProviderId) {
		double distance;
		try {
			distance = evaluateDistance(nodeProviderId, provProviderId);
		} catch (Exception e) {
			distance = Policy.RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return distance;
	}
}
