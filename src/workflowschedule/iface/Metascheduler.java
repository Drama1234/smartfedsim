package workflowschedule.iface;

import java.util.List;

import application.Application;
import federation.resources.FederationDatacenter;
import workflownetworking.InternetEstimator;
import workflowschedule.JGAPMapping;
import workflowschedule.MSExternalState;
import workflowschedule.Policy;
import workflowschedule.Solution;

public class Metascheduler {
	public static Solution[] getMapping(Application application, List<Policy> policy, List<FederationDatacenter> dclist, InternetEstimator internet, long randomSeed)
	{
		MSExternalState state = new MSExternalState(application, dclist, internet); //TODO
		return JGAPMapping.execute(state, policy, randomSeed);
	}
}
