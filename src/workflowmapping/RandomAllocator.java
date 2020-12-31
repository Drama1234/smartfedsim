package workflowmapping;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;

import application.Application;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowfederation.MonitoringHub;
import workflownetworking.InternetEstimator;


public class RandomAllocator extends AbstractAllocator{
	public RandomAllocator()
	{
		super();
	}
	
	public RandomAllocator(MonitoringHub monitoring, InternetEstimator netEstimator)
	{
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application)
	{
		Random rand = new Random(randomSeed);
		
		List<FederationDatacenter> dcs = getMonitoringHub().getView();
		this.solution = new MappingSolution(application);
			
		// for all the vertex of the graph
		Set<ApplicationVertex> vertexes =  application.vertexSet();
		for (ApplicationVertex vertex : vertexes)
		{
			List<Cloudlet> cloudlets = vertex.getCloudlets();
				
			for (Cloudlet c: cloudlets)
			{
				// choose a random datacenter
				FederationDatacenter fd = dcs.get(rand.nextInt(dcs.size()));
				solution.set(c, fd);
			}
		}
		solution.setAllocatorName(this.getClass().getSimpleName());
		return new MappingSolution[]{solution};
	}
}
