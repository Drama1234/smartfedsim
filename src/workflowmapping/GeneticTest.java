package workflowmapping;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;

import application.Application;
import federation.resources.FederationDatacenter;
import workflowconstraints.PolicyContainer;
import workflowfederation.MonitoringHub;
import workflownetworking.InternetEstimator;
import workflowschedule.Solution;

public class GeneticTest extends AbstractAllocator{
	
	PolicyContainer constraint = null;
	List<FederationDatacenter> dcs = null;
	Solution[] solutions = null;
	
	public Solution[] getSolutions() {
		return solutions;
	}

	public GeneticTest()
	{
		super();
	}
	
	public GeneticTest(MonitoringHub monitoring, InternetEstimator netEstimator) {
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application) {
		startSimTime = CloudSim.clock();
		startRealTime = System.currentTimeMillis();
		
		if (monitoring != null){
			dcs = monitoring.getView(); // maybe we can avoid to perform algorithm if the view has not changed
		}
		
		
		return null;
	}

}
