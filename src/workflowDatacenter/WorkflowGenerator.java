package workflowDatacenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.Task;
import org.workflowsim.WorkflowParser;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.DistributionGenerator;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;


import application.Application;
import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import federation.resources.VmFactory;
import workflowtest.Range;
import workflowtest.WorkflowDataset;

public class WorkflowGenerator extends Application{
		
	public String daxPath;
	public static String filename = "resources/RemoteSense_103.xml";
	
	protected Range coreAmount;
	protected Range mipsAmount;
	protected Range ramAmount;
	protected Range bwAmount;
	protected Range diskAmount;
	
	
	void setWorkflowSimConfig() {
		int vmNum = 13;//number of vms;
		Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.FCFS;
        Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;
        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;
        
        /**
         * clustering delay must be added, if you don't need it, you can set all the clustering
         * delay to be zero, but not null
         */
        Map<Integer, DistributionGenerator> clusteringDelay = new HashMap<Integer, DistributionGenerator>();
        
        int maxLevel = 5; // RemoteSense has at most 11 horizontal levels 
        for (int level = 0; level < maxLevel; level++ ){
            DistributionGenerator cluster_delay = new DistributionGenerator(DistributionGenerator.DistributionFamily.WEIBULL, 10, 1);
            clusteringDelay.put(level, cluster_delay);//the clustering delay specified to each level is 1.0 seconds
        }
        // Add clustering delay to the overhead parameters
        OverheadParameters op = new OverheadParameters(0, null, null, null, clusteringDelay, 0);
        
        /**
         * You can only specify clusters.num or clusters.size
         * clusters.num is the number of clustered jobs per horizontal level
         * clusters.size is the number of tasks per clustered job
         * clusters.num * clusters.size = the number of tasks per horizontal level
         * Specifying the clusters.size = 2 means each job has two tasks
         */
        ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.HORIZONTAL;
        ClusteringParameters cp = new ClusteringParameters(1, 1, method, null); // this is for having a pipe (not really!)
        

        Parameters.init(vmNum, daxPath, null,
                null, op, cp, sch_method, pln_method,
                null, 0);
        ReplicaCatalog.init(file_system);
	}
	
	public WorkflowGenerator(String filename, int userId, List<FederationDatacenter> datacenterlist)
	{	
		daxPath = "resources/" + filename + ".xml";
		setWorkflowSimConfig();
		
		WorkflowParser parser = new WorkflowParser(userId, null, null, daxPath);
		
		parser.parse();
		
		List<Task> tasks = parser.getTaskList();
		
		build(userId, tasks, datacenterlist);
	}
	
	public List<Task> getTasksWithDepth(int depth){
		List<Task> tasks = new ArrayList<Task>();
		if (depth < 0)
			return tasks;
		
		for (Cloudlet c: getAllCloudlets()) {
			Task t = (Task) c;
			if (t.getDepth() == depth)
				tasks.add(t);
		}
		return tasks;
	}
	
	private Vm createSmallVm(int userId) {
		return VmFactory.getDesiredVm(
				userId, 
				6502, 
				1, 
				4*1024,//ram GB
				55 * 1024 * 1024,//55MB/s
				200 * 1024//200GB
				);
	}
	
	private Vm createMediumVm(int userId) {
		return VmFactory.getDesiredVm(
				userId, 
				6502*2, 
				2, 
				8*1024,//ram
				75 * 1024 * 1024,//75MB/s
				500 * 1024//500GB
				);
	}
	private Vm createlLargeVm(int userId) {
		return VmFactory.getDesiredVm(
				userId, 
				6502*4, 
				4, 
				16 * 1024,//ram
				90 * 1024 * 1024,//90MB/s
				1024 * 1024//1TB
				);
	}
	

	private void build(int userId, List<Task> tasks,List<FederationDatacenter> datacenterlist) 
	{
		//System.out.println("任务数量 "+tasks.size()+"数据中心数量：" +datacenterlist.size());
		for (Task task : tasks) {
			
//			System.out.println(task.getCloudletLength());
			List<Cloudlet> cloudlets = new ArrayList<>();
			cloudlets.add(task);
			
			Vm vm = null;
			
			if(task.getType().equalsIgnoreCase("SpiltData")||task.getType().equalsIgnoreCase("MergeData")) {
				vm = createSmallVm(userId);
				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm, datacenterlist.get(0));
				v.setBudget(1);
				v.setTask_time(1);
				addVertex(v);
			}else if(task.getCloudletLength() < 2100) {
				vm = createMediumVm(userId);
				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
				v.setBudget(1);
				v.setTask_time(1);
				addVertex(v);
			}else {
				vm = createlLargeVm(userId);
				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
				v.setBudget(1);
				v.setTask_time(1);
				addVertex(v);
			}
		}

		//System.out.println("顶点有 "+vertexSet().size());
		//System.out.println(super.toString());
		
//		System.out.println("任务数量 "+tasks.size()+"数据中心数量：" +datacenterlist.size());
//		Range interbwValue = new Range(10*1024*1024,35*1024*1024);//10MB/s-35MB/s
//		Range latencyValue = new Range(0.09, 0.13);
		
		
		for(Task t:tasks) {
//			List<Task> parents = ((Task) t).getParentList();
//			System.out.println("父亲数量：" +parents.size());
			ApplicationVertex base = this.getVertexForCloudlet(t);
			long bandwidth = 0;
			double latency = 0;
			List<File> files = t.getFileList();
			List<File> pfiles = new ArrayList<>();
			for (File f: files){
				if (f.getType() == 2)//output as constructed by parser
					//f.getFileAttribute().getName();
					pfiles.add(f);
					//outputSize += f.getSize(); // this size is in bytes
			}
			
			List<Task> childs = ((Task) t).getChildList();
			for (Task c:childs) {
				double size = 0;
				ApplicationVertex child = this.getVertexForCloudlet(c);
				List<File> childfiles = c.getFileList();
				List<File> cfiles = new ArrayList<>();
				for (File f :childfiles) {
					if(f.getType() == 1) {
						cfiles.add(f);
					}
				}
				
				for (File file : cfiles) {
					for (File file2 : pfiles) {
						if(file2.getFileAttribute().getName().equals(file.getFileAttribute().getName())) {
							size+=file2.getSize();//KB
						}
					}
				}
			
//				size = size/1024d;// obtaining KB as applicationEdge requires
			
				bandwidth = 20*1024*1024; //20MB/s
				latency = 0.11;//0.12s
				addEdge(new ApplicationEdge(size, bandwidth, latency), base, child);
			}
		}
//		System.out.println(super.toString());
//		System.out.println(super.vertexSet().size() + "+" + super.getEdges().size());
//		System.out.println(super.toString());
	}
		
		
//		for (T t : tasks) {
//			ApplicationVertex base = this.getVertexForCloudlet(t);
//			
//			List<Task> childs = ((Task) t).getChildList();
//			for (Task c : childs) {
//				ApplicationVertex child = this.getVertexForCloudlet(c);
//				double inputSize = 0;
//				double bandwidth = 0;
//				double latency = 0;
//				List<File> files = ((Task) c).getFileList();
//				for (File f: files){
//					if (f.getType() == 1)//output as constructed by parser
//						inputSize += f.getSize(); // this size is in bytes
//				}
//				inputSize = inputSize / 1024d; // obtaining KB as applicationEdge requires
//				addEdge(new ApplicationEdge(inputSize, bandwidth,latency), base, child);
//			}
//		}
		
		
	public static void main(String[] args){
		int num_user = 1;   // number of grid users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        CloudSim.init(num_user, calendar, trace_flag);
        try {
        	WorkflowDataset dataset = new WorkflowDataset(100, filename);
        	List<FederationDatacenter> datacenters = dataset.createDatacenters();
        	WorkflowGenerator app = new WorkflowGenerator("RemoteSense_103", 0, datacenters);
//			app.export("plots/" + "RemoteSense_1" + ".dot");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
