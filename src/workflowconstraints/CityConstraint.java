package workflowconstraints;

import org.jgap.Gene;

import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class CityConstraint extends MSPolicy{

	public CityConstraint(double weight) {
		super(weight, MSPolicy.EQUAL_TYPE);
	}

	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		String nodeCity = node.getCharacteristic().get(Constant.CITY).toString(); //what I want
		String provCity = prov.getCharacteristic().get(Constant.CITY).toString(); //what I have
		
		nodeCity = nodeCity.toLowerCase().trim();
		provCity = provCity.toLowerCase().trim();
		
		double distance = 0;
		String[] cities = provCity.split(",");
		if (cities.length > 1){
			distance = calculateDistance(nodeCity, cities[0]);
			for (int i=1; i<cities.length; i++){
				double tmp = calculateDistance(nodeCity, cities[i]);
				distance = tmp < distance ? tmp : distance;
			}
		}
		else {
			distance = calculateDistance(nodeCity, provCity);
		}
		
		if (DEBUG)
			System.out.println("\tEvaluation on city: " + nodeCity + " vs " + provCity + "=" + distance);
		return distance * getWeight();
	}
	
	private double calculateDistance(String nodeCity,String provCity) {
		double distance;
		try {
			distance = evaluateDistance(nodeCity, provCity);
		} catch (Exception e) {
			distance = MSPolicy.RUNTIME_ERROR; // a positive value in order to not consider this constraint
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return distance;
	}
}
