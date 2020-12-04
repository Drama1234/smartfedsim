package workflownetworking;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.core.CloudSim;
import org.jgrapht.graph.Multigraph;
import org.junit.Test.None;

import federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.federation.generation.Range;
import it.cnr.isti.smartfed.networking.SecuritySupport;
import workflowDatacenter.DatacenterGenerator;
import workflowtest.WorkflowDataset;

/**
 * This class encloses methods for the generation of a "provider networks" that
 * would model the state and the nature of the links among different federation
 * providers. 
 * The information contained by this class are static, in the sense that the
 * class is not supposed to track what applications are currently running on 
 * providers. For example, if the bandwidth available among two provides is
 * 100KB and then an application requiring 10KB is deployed, this class would 
 * still return 100KB as link capacity.
 * 
 * @author carlini
 *
 */

public class InternetEstimator {
	protected AbstractRealDistribution distribution;
	private Multigraph<FederationDatacenter, InternetLink> graph;
	protected Range interBwAmount;
	protected Range interLatencyAmount;
	protected Range costInterBw;
	public InternetEstimator(InternetEstimator internet) {
		internet.toString();
		this.costInterBw = internet.costInterBw;
		this.interBwAmount = internet.interBwAmount;
		this.interLatencyAmount = internet.interLatencyAmount;
		this.graph = null;
	}
	
	
	public InternetEstimator(List<FederationDatacenter> list,long seed) {
		distribution = new UniformRealDistribution();
		distribution.reseedRandomGenerator(seed);
		interBwAmount = new Range(5*1024*1024,50*1024*1024);//5MB-50MB/s
		interLatencyAmount = new Range(0.09, 0.15);//延迟 0.09-0.15s
		costInterBw = new Range(0.05, 0.12);//USD GB
		
		graph = new Multigraph<FederationDatacenter,InternetLink>(InternetLink.class);
		// populate the vertexes
		for (FederationDatacenter d: list)
		{
			graph.addVertex(d);
		}
		// populate the edges
		for (FederationDatacenter outer: list) {
			for (FederationDatacenter inner: list) {
				// a self edges will exits, even if probably will be never used
				if (outer.getId() == inner.getId())
				{
//					//内部延迟设0,不进行统计
//					InternetLink il = new InternetLink(outer.getMSCharacteristics().getHighestAllocatedBwAmongHosts(),
//							0.0,outer.getMSCharacteristics().getCostPerBw(),SecuritySupport.NO);
//					graph.addEdge(outer, inner,il);
					// DO NOTHING!
					// InternetLink il = new InternetLink(Long.MAX_VALUE, 0, SecuritySupport.ADVANCED);
					// graph.addEdge(outer, inner, il);
				}
				// regular edge,云供应商a到云供应商b之间方向不同参数不同
				else{					
					long interbw;
					double interlatency,costinterbw;
					
					interbw = (long)interBwAmount.denormalize(distribution.sample());
					interlatency = interLatencyAmount.denormalize(distribution.sample());
					costinterbw = costInterBw.denormalize(distribution.sample());
					interlatency = Double.valueOf(String.format("%.2f", interlatency));
					costinterbw = Double.valueOf(String.format("%.2f", costinterbw));
					
					InternetLink il = new InternetLink(interbw, interlatency, costinterbw);
					graph.addEdge(outer, inner, il);
				}
			}	
		}
//		this.networkRepresation(list);
	}	
	
	/**
	 * Return the InternetLink between two datacenters. 
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public InternetLink getInternetLink(FederationDatacenter a, FederationDatacenter b) throws Exception
	{
		if ((a == null) || (graph.containsVertex(a) == false))
			throw new Exception("Vertex not found or null: "+a);
		
		if ((b == null) || (graph.containsVertex(b) == false))
			throw new Exception("Vertex not found or null: "+b);
		
		return graph.getEdge(a, b);
	}
	
	/**
	 * Return the InternetLink between the two datacenters
	 * with the parameter ids.
	 * @param id_a
	 * @param id_b
	 * @return
	 * @throws Exception 
	 */
	public InternetLink getInternetLink(Integer id_a, Integer id_b) throws Exception
	{
		FederationDatacenter a = (FederationDatacenter) CloudSim.getEntity(id_a);
		FederationDatacenter b = (FederationDatacenter) CloudSim.getEntity(id_b);
//		for(int i =3;i<22;i++) {
//			System.out.println("云服务供应商实体"+CloudSim.getEntity(i));
//		}
		return this.getInternetLink(a, b);
	}
	
	/**
	 * Return the highest value for latency among all the links 
	 * @return
	 */
	public double getHighestLatency()
	{
		double max = 0;
		for (InternetLink link: graph.edgeSet())
		{
			if (link.getLatency() > max)
				max = link.getLatency();
		}
		return max;
	}
	
	/*
	 * Return the highest value for bw among all the links 
	 */
	public long getHighestBw() {
		long max = 0;
		for (InternetLink link : graph.edgeSet()) {
			if(link.getBandwidth() > max)
				max = link.getBandwidth();
		}
		return max;
	}
	
	/*
	 * Return the highest value for bw among all the links 
	 */
	public double getHighestCostBw() {
		double max = 0;
		for (InternetLink link : graph.edgeSet()) {
			if(link.getBwcost() > max)
				max = link.getBwcost();
		}
		return max;
	}

	@Override
	public String toString() {
		return "HighestLatency:"+getHighestLatency()+"HighestBw:"+getHighestBw()+"HighestCostBw:"+getHighestCostBw();
	}
	
	public void networkRepresation(List<FederationDatacenter> list) {
		for (FederationDatacenter outer: list) {
			for (FederationDatacenter inner: list) {
				InternetLink link;
				if (outer.getId() == inner.getId()) {
					System.out.println("云服务供应商ID"+outer.getId());
				}else {
					try {
						link = this.getInternetLink(outer.getId(), inner.getId());
						System.out.println("出云服务供应商ID"+outer.getId()+"入云服务供应商ID"+inner.getId()+"网络特性："+link.getBandwidth()/1024/1024+"MB/s"+link.getLatency()+" s"+link.getBwcost()+" USD");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		int num_user = 1;   // number of grid users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        CloudSim.init(num_user, calendar, trace_flag);
		try {
			String filename = "resources/RemoteSense_13.xml";
        	WorkflowDataset dataset = new WorkflowDataset(20, filename);
        	List<FederationDatacenter> datacenters = dataset.createDatacenters();
	    	InternetEstimator internetEstimator = new InternetEstimator(datacenters,11);
	    	internetEstimator.networkRepresation(datacenters);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
