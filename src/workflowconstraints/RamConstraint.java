package workflowconstraints;

import org.jgap.Gene;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class RamConstraint extends MSPolicy{
	private static double highRamValue;

	public static double getHighRamValue() {
		return highRamValue;
	}

	public static void setHighRamValue(double highestValue) {
		highRamValue = highestValue;
	}

	public RamConstraint(double weight, double highestValue) {
		super(weight, MSPolicy.ASCENDENT_TYPE);
		highRamValue = highestValue;
		this.constraintName = "Ram";
	}
	
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov,InternetEstimator internet) {
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		Integer nodeRam = (Integer) node.getComputing().getCharacteristic().get(Constant.RAM); //what I want
		Integer provRam = (Integer) prov.getComputing().getCharacteristic().get(Constant.RAM); //what I have
		double distance = super.calculateDistance_ErrHandling(provRam, nodeRam, highRamValue);
		return distance * getWeight();
	}
}
