package workflowtest;

import java.util.List;

import application.Application;
import federation.resources.FederationDatacenter;
import workflownetworking.InternetEstimator;

public interface InterfaceDataSet {
	public List<FederationDatacenter> createDatacenters();

	public List<Application> createApplications(int userId,List<FederationDatacenter> datacenters);

	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters);

}
