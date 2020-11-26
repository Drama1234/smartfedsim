package workflowtest;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import application.Application;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import federation.resources.ResourceCounter;
import it.cnr.isti.smartfed.test.TestResult;
import workflowDatacenter.WorkflowGenerator;
import workflowfederation.Allocation;
import workflowfederation.CostComputer;
import workflowfederation.Federation;
import workflowfederation.FederationQueue;
import workflowfederation.FederationQueueProfile;
import workflowfederation.FederationQueueProvider;
import workflowfederation.MonitoringHub;
import workflowfederation.UtilityPrint;
import workflowfederation.WorkflowComputer;
import workflowmapping.AbstractAllocator;
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
	
	public void run() 
	{
		// init the cloudsim simulator
		Log.enable();
		int num_user = 1;   // users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = true;  // trace events
		CloudSim.init(num_user, calendar, trace_flag);
		
		//创建Federation
		Federation federation = new Federation(allocator,randomSeed);
		CloudSim.addEntity(federation);
				
		// 重新设置资源统计
		ResourceCounter.reset();
		
		// 创建数据中心
		List<FederationDatacenter> datacenters = dataset.createDatacenters();
		federation.setDatacenters(datacenters);
		
		allocator.setRandomSeed(randomSeed);
		
		// 创建网络
		InternetEstimator internetEstimator = dataset.createInternetEstimator(datacenters);
		allocator.setNetEstimator(internetEstimator);
		//创建监控
		int schedulingInterval = 1; // probably simulation time
		MonitoringHub monitor = new MonitoringHub(datacenters, schedulingInterval);
		CloudSim.addEntity(monitor);
		allocator.setMonitoring(monitor);
		
		//创建应用程序
		List<Application> applications = dataset.createApplications(federation.getId(),datacenters);
	
		// create the queue (is that still needed)?
		FederationQueueProfile queueProfile = FederationQueueProfile.getDefault();
		FederationQueue queue = FederationQueueProvider.getFederationQueue(queueProfile, federation, applications);
		CloudSim.addEntity(queue);
		
		// manually setup the end of the simulation
		CloudSim.terminateSimulation(1000000); // in milliseconds
		
		// actually start the simulation
		CloudSim.startSimulation();
		
		// print the cloudlet
		List<Cloudlet> newList = federation.getReceivedCloudlet();
		UtilityPrint.printCloudletList(newList);
		
		
		int i = 0;
		for (Allocation allocation: federation.getAllocations()) {
			if (allocation.isCompleted())
			{
				i++;
				System.out.println("分配成功：第"+i+"个分配方案");
				double budget = 0;
				for (ApplicationVertex av : allocation.getApplication().vertexSet())
					budget += av.getBudget();
	
				double completion = WorkflowComputer.getFlowCompletionTime((WorkflowGenerator) applications.get(0), datacenters, internetEstimator);
				double cost = WorkflowComputer.getFlowCost( datacenters, allocation, internetEstimator);
	
				System.out.println("BUDGET：------------------->" + budget);
				System.out.println("COST：-------------------> " + cost);
				System.out.println("makespan：----------------> " + completion);
	
			}else
				System.out.println("Not completed");
		}	
	}
}
