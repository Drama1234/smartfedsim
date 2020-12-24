package workflowschedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import Constraints.PolicyContainer;
import WorkflowParameterConstraints.costConstraint;
import federation.resources.FederationDatacenter;
import workflownetworking.InternetEstimator;


public class PolicyFactory {
	private final static Logger log = Logger.getLogger(PolicyContainer.class.getSimpleName());
	public static PolicyContainer createPoliciesDefaultNetBw(List<FederationDatacenter> dcList, double[] weights,InternetEstimator internet){
		System.out.println("*** Creating global policy with net and bw ***");
		findCommonMaxValues(dcList);
		findCommonMaxNetworkValues(internet);
		
		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.CoreNumberConstraints(weights[0]));
		constraint.add(constraint.RamConstraints(weights[1]));
		constraint.add(constraint.StorageConstraints(weights[2]));
		constraint.add(constraint.providerBwConstraints(weights[3]));
		constraint.add(constraint.latencyConstraints(weights[4]));
		constraint.add(constraint.IntercloudBwConstraints(weights[5]));
//		constraint.add(constraint.providerIDConstraints(weights[6]));
		constraint.add(constraint.costConstraints(weights[6]));
		return constraint;
	}
	
	public static PolicyContainer createPolicies(List<FederationDatacenter> dcList, double[] weights,InternetEstimator internet) {
		System.out.println("*** Creating global policy with net and bw ***");
		PolicyContainer constraint = new PolicyContainer(weights);
		constraint.add(constraint.costssPolicy(weights[0]));
		constraint.add(constraint.makespanPolicy(weights[1]));
//		constraint.add(constraint.providerIdsPloPolicy(weights[2]));
		return constraint;
	}
	
	private static void findCommonMaxValues(List<FederationDatacenter> dcList) {
		Collections.sort(dcList);
		long highBw = findMaxBwAllDatacenters(dcList);
		PolicyContainer.highProviderBwValue = highBw;
		
		double highRam_dc = findMaxRamAllDatacenters(dcList);
		PolicyContainer.highRamValue = (int) highRam_dc;

		double highStorage_dc = findMaxStorageAllDatacenters(dcList);
		PolicyContainer.highStorageValue = (long) highStorage_dc;
		
		int highCore_dc = findMaxCoreAllDatacenters(dcList);
		PolicyContainer.highCoreNumberValue = highCore_dc;
	}
	
	public static void findCommonMaxNetworkValues(InternetEstimator internet) {
		PolicyContainer.highNetworkLatencyValue = Double.valueOf(String.format("%.2f", internet.getHighestLatency()));
		PolicyContainer.highNetworkBwValue = internet.getHighestBw();
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
	
	public static long findMaxBwAllDatacenters(List<FederationDatacenter> dcList) {
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
	
	public static long findMaxStorageAllDatacenters(List<FederationDatacenter> dcList) {
		FederationDatacenter max = Collections.max(dcList, new Comparator<FederationDatacenter>() {
			@Override
			public int compare(FederationDatacenter first, FederationDatacenter second) {
				if (first.getMSCharacteristics().getHighestStorage() > second.getMSCharacteristics().getHighestStorage())
					return 1;
				else if(first.getMSCharacteristics().getHighestStorage() < second.getMSCharacteristics().getHighestStorage())
					return -1;
				return 0;
			}
		});
		long highStorage_dc = max.getMSCharacteristics().getHighestStorage();
		return highStorage_dc;
	}
	
	
	
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
}
