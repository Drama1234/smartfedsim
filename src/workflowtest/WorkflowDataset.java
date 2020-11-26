package workflowtest;

import java.util.ArrayList;
import java.util.List;

import application.Application;
import federation.resources.FederationDatacenter;
import workflowDatacenter.DatacenterGenerator;
import workflowDatacenter.WorkflowGenerator;
import workflownetworking.InternetEstimator;


public class WorkflowDataset implements InterfaceDataSet{
	protected long seed = 77;
	
	private String filename;
	private int numOfDatacenters;
	
	public WorkflowDataset(int numOfDatacenters, String filename)
	{
		this.filename = filename;
		this.numOfDatacenters = numOfDatacenters;
	}
	
	public void setSeed(long seed)
	{
		this.seed = seed;
	}
	
	@Override
	public List<FederationDatacenter> createDatacenters(){
		DatacenterGenerator dg = new DatacenterGenerator(this.seed*15);
		int numHost = 50  * numOfDatacenters; // it will assign more or less 1000 host to each datacenter
		List<FederationDatacenter> dcs = dg.getDatacenters(numOfDatacenters, numHost);
		return dcs;
	}

	@Override
	public List<Application> createApplications(int userId,List<FederationDatacenter> datacenters) {
		List<Application> apps = new ArrayList<Application>();
		apps.add(new WorkflowGenerator(filename, userId, datacenters));
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters) {
		InternetEstimator inetEst = new InternetEstimator(datacenters, seed);
		return inetEst;
	}
}
