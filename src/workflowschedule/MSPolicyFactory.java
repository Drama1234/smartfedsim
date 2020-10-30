package workflowschedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import federation.resources.FederationDatacenter;
import workflowconstraints.PolicyContainer;
import workflownetworking.InternetEstimator;
import workflowschedule.MSPolicy.ConstraintScope;

public class MSPolicyFactory {
	private final static Logger log = Logger.getLogger(PolicyContainer.class.getSimpleName());
	
	public static PolicyContainer createPoliciesDefaultNetBw(List<FederationDatacenter> dcList, double[] weights){
		System.out.println("*** Creating global policy with net and bw ***");
		findCommonMaxValues(dcList);

		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.ramConstraint(weights[0]));
		constraint.add(constraint.storageConstraint(weights[1]));
		constraint.add(constraint.cpuNumberConstraint(weights[2]));
//		constraint.add(constraint.locationConstraint(weights[3]));
		constraint.add(constraint.costPerResourceConstraint(weights[3],ConstraintScope.Global));
		constraint.add(constraint.networkConstraint(weights[4],weights[5],ConstraintScope.Global));
		constraint.add(constraint.LatencyConstraint(weights[6], ConstraintScope.Global));
		
		System.out.println(constraint);
		return constraint;
	}
	
	private static void findCommonMaxValues(List<FederationDatacenter> dcList){
		// finding the datacenter with the highest cost per ram (default criteria in the compare method)
		Collections.sort(dcList);
		double highCostRam_dc = dcList.get(dcList.size()-1).getMSCharacteristics().getCostPerMem();
		//PolicyContainer.highCostValueRam = highCostRam_dc;
//		double highCostCpu_dc = dcList.get(dcList.size()-1).getMSCharacteristics().getCostPerCpu();
//		PolicyContainer.highCostValueCpu = highCostCpu_dc;
//		double highCostStorage_dc = dcList.get(dcList.size()-1).getMSCharacteristics().getCostPerMem();
//		PolicyContainer.highCostValueStorage = highCostStorage_dc;
		
		long highBw = findMaxBwAllDatacenters(dcList);
		PolicyContainer.highProviderBwValue = highBw;
		
		double highRam_dc = findMaxRamAllDatacenters(dcList);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double highStorage_dc = findMaxStorageAllDatacenters(dcList);
		PolicyContainer.highStorageValue = (long) highStorage_dc;
		
		int highCore_dc = findMaxCoreAllDatacenters(dcList);
		PolicyContainer.highCpuNumberValue = highCore_dc;
		
	}
	
	public static void findCommonMaxNetworkValues(InternetEstimator internet) {
		PolicyContainer.highNetworkLatencyValue = internet.getHighestLatency();
		PolicyContainer.highNetworkBwValue = internet.getHighestBw();
	}
	
	// finding the datacenter with the highest storage quantity 
	public static FederationDatacenter findDatacenterMaxCore(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestCore() > second.getMSCharacteristics().getHighestStorage())
					return 1;
				else if (first.getMSCharacteristics().getHighestStorage() < second.getMSCharacteristics().getHighestStorage())
					return -1;
				return 0;
			}
		});
		return max;
	}
	 
	
	public static int findMaxCoreAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList,new Comparator<FederationDatacenter>() {
			public int compare(FederationDatacenter first,FederationDatacenter second) {
				if(first.getMSCharacteristics().getHighestCore() > second.getMSCharacteristics().getHighestCore())
					return 1;
				else if(first.getMSCharacteristics().getHighestCore() < second.getMSCharacteristics().getHighestCore())
					return -1;
				return 0;
			}
		});
		int highCore_dc = max.getMSCharacteristics().getHighestCore();
		return highCore_dc;
	}
	
	public static long findMaxBwAllDatacenters(List<FederationDatacenter> dcList){
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestAllocatedBwAmongHosts() > second.getMSCharacteristics().getHighestAllocatedBwAmongHosts())
					return 1;
				else if(first.getMSCharacteristics().getHighestAllocatedBwAmongHosts() < second.getMSCharacteristics().getHighestAllocatedBwAmongHosts())
					return -1;
				return 0;
			}
		});
		long highBw_dc = max.getMSCharacteristics().getHighestAllocatedBwAmongHosts();
		return highBw_dc;
	}
	
	// finding the datacenter with the highest ram quantity 
	public static double findMaxRamAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestRam() > second.getMSCharacteristics().getHighestRam())
					return 1;
				else if (first.getMSCharacteristics().getHighestRam() < second.getMSCharacteristics().getHighestRam())
					return -1;
				return 0;
			}
		});
		double highRam_dc = max.getMSCharacteristics().getHighestRam();
		return highRam_dc;
	}
	
	// finding the highest storage quantity among all datacenters
	public static double findMaxStorageAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = findDatacenterMaxStorage(dcList);
		double high = max.getMSCharacteristics().getHighestStorage();
		return high;
	}
	
	// finding the datacenter with the highest storage quantity 
	public static FederationDatacenter findDatacenterMaxStorage(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestStorage() > second.getMSCharacteristics().getHighestStorage())
					return 1;
				else if (first.getMSCharacteristics().getHighestStorage() < second.getMSCharacteristics().getHighestStorage())
					return -1;
				return 0;
			}
		});
		return max;
	}
}
