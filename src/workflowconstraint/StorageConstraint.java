package workflowconstraint;

import org.jgap.Gene;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class StorageConstraint extends MSPolicy{
	private static double highStorageValue;

	public static double getHighStorageValue() {
		return highStorageValue;
	}

	public static void setHighStorageValue(double highStorageValue) {
		StorageConstraint.highStorageValue = highStorageValue;
	}
	
	public StorageConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE);
		this.constraintName = "storage";
		highStorageValue = highestValue;
	}
	
	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		long nodeStore =  (Long) node.getStorage().getCharacteristic().get(Constant.STORE); // what I want
		long provStore =  (Long) prov.getStorage().getCharacteristic().get(Constant.STORE); // what I have
		
		double distance = super.calculateDistance_ErrHandling(provStore, nodeStore, highStorageValue);
		return distance * getWeight();
		
//		try {
//			distance = evaluateDistance(provStore, nodeStore, highStorageValue);
//		} catch (Exception e) {
//			distance = RUNTIME_ERROR; // a positive value in order to not consider this constraint
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
//		if (DEBUG)
//			System.out.println("\t  Eval on storage " + nodeStore + "-" + provStore + "/" + highStorageValue + "=" + distance);
//		return distance * getWeight();
	}
}
