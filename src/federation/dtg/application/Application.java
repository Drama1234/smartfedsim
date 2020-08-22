package federation.dtg.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.jgrapht.graph.SimpleDirectedGraph;


public class Application {
	private SimpleDirectedGraph<ApplicationVertex, ApplicationEdge> graph;
	private List<Cloudlet> cloudlets;//数据转换项
	private List<CloudletInputData> cloudletInputData;//输入数据项
	private List<CloudletOutputData> cloudletOutputData;//输出数据项
	private Map<Integer, Cloudlet> idToCloudlet; //数据转换项的id
	private Map<Integer, CloudletInputData> idToInputData;
	private Map<Integer, CloudletOutputData> idToOutputData;
	private Hashtable<Cloudlet, ApplicationVertex> cloudletToVertex;
	private Hashtable<Vm, ApplicationVertex> vmToVertex;
	private Hashtable<ApplicationVertex,CloudletInputData> inputToVertex;
	private Hashtable<ApplicationVertex, CloudletOutputData> VertexToOutput;
	
	public Application()
	{
		graph = new SimpleDirectedGraph<ApplicationVertex, ApplicationEdge>(ApplicationEdge.class);
		cloudlets = new ArrayList<Cloudlet>();
		cloudletToVertex = new Hashtable<Cloudlet, ApplicationVertex>();
		vmToVertex = new Hashtable<Vm, ApplicationVertex>();
		idToCloudlet = new HashMap<Integer, Cloudlet>();
		inputToVertex = new Hashtable<ApplicationVertex,CloudletInputData>();
		VertexToOutput = new Hashtable<ApplicationVertex, CloudletOutputData>();
	}
	
	/**
	 * 创建方法
	 */
	public void addVertex(ApplicationVertex av) {
		cloudlets.addAll(av.getCloudlets());
		for (Cloudlet c: av.getCloudlets()) {
			cloudletToVertex.put(c, av);
			idToCloudlet.put(c.getCloudletId(), c);
			idToInputData.put(c.getCloudletId(), cloudletInputData.get(index));
			idToOutputData.put(c.getCloudletId(), value);
			
			
		}
	}
	
	public void addEdge(ApplicationEdge ed, ApplicationVertex av1,) {
		
	}


	
	

	
	
	
}
