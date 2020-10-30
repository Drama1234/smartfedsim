package workflowtest;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import application.Application;
import application.ApplicationVertex;
import application.WorkflowApplication;
import federation.resources.FederationDatacenter;
import federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.federation.FederationQueue;
import it.cnr.isti.smartfed.federation.FederationQueueProfile;
import it.cnr.isti.smartfed.federation.FederationQueueProvider;
import it.cnr.isti.smartfed.test.PreciseDataset;
import it.cnr.isti.smartfed.test.TestResult;
import workflowfederation.Allocation;
import workflowfederation.CostComputer;
import workflowfederation.Federation;
import workflowfederation.MonitoringHub;
import workflowfederation.UtilityPrint;
import workflowfederation.WorkflowComputer;
import workflowmapping.AbstractAllocator;
import workflowmapping.MappingSolution;
import workflownetworking.InternetEstimator;

public class Experiment {
	protected AbstractAllocator allocator;
	protected InterfaceDataSet dataset;
	protected long randomSeed;
	
	public Experiment(AbstractAllocator allocator, InterfaceDataSet d)
	{
		this.allocator = allocator;
		this.dataset = d;
	}
	
	public Experiment(AbstractAllocator allocator, InterfaceDataSet d, long seed)
	{
		this.allocator = allocator;
		this.dataset = d;
		this.randomSeed = seed;
	}
	
	public void run() {
		// init the cloudsim simulator
		Log.enable();
		int num_user = 1;   // users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = true;  // trace events
		CloudSim.init(num_user, calendar, trace_flag);
		
		//创建Federation
		Federation federation = new Federation(allocator,randomSeed);
		CloudSim.addEntity(federation);
		
		//初始化联盟云
		if (dataset instanceof PreciseDataset)
			((PreciseDataset)dataset).init(federation.getId());
		
		// 重新设置资源统计
		ResourceCounter.reset();
		
		// 创建数据中心
		List<FederationDatacenter> datacenters = dataset.createDatacenters();
		federation.setDatacenters(datacenters);
		
		// 创建网络
		InternetEstimator internetEstimator = dataset.createInternetEstimator(datacenters);
		
		//创建监控
		int schedulingInterval = 1; // probably simulation time
		MonitoringHub monitor = new MonitoringHub(datacenters, schedulingInterval);
		CloudSim.addEntity(monitor);
		
		//创建应用程序
		List<Application> applications = dataset.createApplications(federation.getId(),datacenters);
		
		//设置分配
		allocator.setMonitoring(monitor);
		allocator.setNetEstimator(internetEstimator);
		allocator.setRandomSeed(randomSeed);
		
//		// create the queue (is that still needed)?
//		FederationQueueProfile queueProfile = FederationQueueProfile.getDefault();
//		FederationQueue queue = FederationQueueProvider.getFederationQueue(queueProfile, federation, applications);
//		CloudSim.addEntity(queue);
		
		// manually setup the end of the simulation
		CloudSim.terminateSimulation(1000000); // in milliseconds
		
		// actually start the simulation
		CloudSim.startSimulation();
		
		// print the cloudlet
		List<Cloudlet> newList = federation.getReceivedCloudlet();
		UtilityPrint.printCloudletList(newList);
		
//		// calculates the vendor lock-in metric on the mapping plan
//		MappingSolution sol = allocator.getSolution();
//		System.out.println(sol);
//		Set<FederationDatacenter> myset = new HashSet<FederationDatacenter>();
//		for (FederationDatacenter fd: sol.getMapping().values()){
//			myset.add(fd);
//		}
//
//		int usedDc = myset.size();// datacenters.size();
//		TestResult.getLockDegree().addValue(usedDc);
		
		// add the values to the TestResult class
		for (Allocation a: federation.getAllocations())
		{
			if (a.isCompleted())
			{
				double budget = 0;
				for (ApplicationVertex av : a.getApplication().vertexSet())
					budget += av.getBudget();
			

				if (applications.get(0) instanceof WorkflowApplication)
				{			
					double completion = WorkflowComputer.getFlowCompletionTime((WorkflowApplication) applications.get(0), datacenters, internetEstimator);
					double cost = WorkflowComputer.getFlowCostPerHour(a, completion,internetEstimator);
					TestResult.getCompletion().addValue(completion);
					TestResult.getCost().addValue(cost);
					System.out.println("COMPLETION -----------> " + completion);
				}
				else
				{
					double total = CostComputer.actualCost(a,internetEstimator);
					double netcost = CostComputer.actualNetCost(a,internetEstimator);
					System.out.println("TOTAL --------> "+total);
					
					TestResult.getCost().addValue(total);
					TestResult.getNetCost().addValue(netcost);
					TestResult.getBerger().addValue(Math.log(total / budget));
				}	
			}
			else
				System.out.println("Not completed");
		}
	}
}
