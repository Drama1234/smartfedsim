package workflowconstraint;

import org.jgap.Gene;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class ProviderIdConstraints extends MSPolicy{
	
	public ProviderIdConstraints(double weight) {
		super(weight, MSPolicy.EQUAL_TYPE);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		String nodeProviderId = node.getCharacteristic().get(Constant.providerID).toString(); //what I want
		String provProviderId = prov.getCharacteristic().get(Constant.providerID).toString();
		double distance = 0;
		if(nodeProviderId!="-1") {
			distance = calculateDistance(nodeProviderId, provProviderId);
		}else {
			distance = 0;
		}
		if (DEBUG)
			System.out.println("\tEvaluation on providerId: " + nodeProviderId + " vs " + provProviderId + "=" + distance);
		return distance * getWeight();	
	}
	
	private double calculateDistance(String nodeProviderId,String provProviderId) {
		double distance;
		try {
			distance = evaluateDistance(nodeProviderId, provProviderId);
		} catch (Exception e) {
			distance = MSPolicy.RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return distance;
	}
}
