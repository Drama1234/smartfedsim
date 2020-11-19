package it.cnr.isti.smartfed.federation.application;
//package application;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.cloudbus.cloudsim.Cloudlet;
//import org.cloudbus.cloudsim.File;
//import org.cloudbus.cloudsim.Vm;
//import org.cloudbus.cloudsim.core.CloudSim;
//import org.workflowsim.Task;
//import org.workflowsim.WorkflowParser;
//import org.workflowsim.utils.ClusteringParameters;
//import org.workflowsim.utils.DistributionGenerator;
//import org.workflowsim.utils.OverheadParameters;
//import org.workflowsim.utils.Parameters;
//import org.workflowsim.utils.ReplicaCatalog;
//
//import federation.resources.City;
//import it.cnr.isti.smartfed.federation.resources.Country;
//import it.cnr.isti.smartfed.federation.resources.VmFactory;
//
//
//public class WorkflowApplication extends Application{
//	public static String fileName = "RemoteSense_13";
//	public String daxPath = "resources/" + fileName + ".xml";
//	void setWorkflowSimConfig()
//	{
//		int vmNum = 5;//number of vms;
//		Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.FCFS;
//        Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;
//        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;
//
//        /**
//         * clustering delay must be added, if you don't need it, you can set all the clustering
//         * delay to be zero, but not null
//         */
//        Map<Integer, DistributionGenerator> clusteringDelay = new HashMap<Integer, DistributionGenerator>();
//         
//        int maxLevel = 4; // Montage has at most 11 horizontal levels 
//        for (int level = 0; level < maxLevel; level++ ){
//            DistributionGenerator cluster_delay = new DistributionGenerator(DistributionGenerator.DistributionFamily.WEIBULL, 10.0, 1.0);
//            clusteringDelay.put(level, cluster_delay);//the clustering delay specified to each level is 1.0 seconds
//        }
//        // Add clustering delay to the overhead parameters
//        OverheadParameters op = new OverheadParameters(0, null, null, null, clusteringDelay, 0);
//        
//        /**
//         * You can only specify clusters.num or clusters.size
//         * clusters.num is the number of clustered jobs per horizontal levelh
//         * clusters.size is the number of tasks per clustered job
//         * clusters.num * clusters.size = the number of tasks per horizontal level
//         * Specifying the clusters.size = 2 means each job has two tasks
//         */
//        ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
//        ClusteringParameters cp = new ClusteringParameters(1, 0, method, null); // this is for having a pipe (not really!)
//        
//
//        Parameters.init(vmNum, daxPath, null,
//                null, op, cp, sch_method, pln_method,
//                null, 0);
//        ReplicaCatalog.init(file_system);
//	}
//	
//	public WorkflowApplication(int userId, boolean clustering) {
//		setWorkflowSimConfig();
//		
//		WorkflowParser parser = new WorkflowParser(userId, null, null, daxPath);
//		parser.parse();
//		List<Task> tasks = parser.getTaskList();
//		
//		build(userId, tasks);
//	}
//	
//	public WorkflowApplication(List<Task> tasks, int userId, boolean clustering)
//	{
//			build(userId, tasks);
//	}
//	
//	public WorkflowApplication(String filename, int userId, boolean clustering)
//	{	
//		daxPath = "resources/" + filename + ".xml";
//		setWorkflowSimConfig();
//		
//		WorkflowParser parser = new WorkflowParser(userId, null, null, daxPath);
//		parser.parse();
//		List<Task> tasks = parser.getTaskList();
//		
//		build(userId, tasks);
//	}
//	
//	public List<Task> getTasksWithDepth(int depth)
//	{
//		List<Task> tasks = new ArrayList<Task>();
//		if (depth < 0)
//			return tasks;
//		
//		for (Cloudlet c: getAllCloudlets())
//		{
//			Task t = (Task) c;
//			if (t.getDepth() == depth)
//				tasks.add(t);
//		}
//		return tasks;
//	}
//	
//	
//	private Vm createSmallVm_NoID(int userId){
//		return VmFactory.getDesiredVm(
//			userId, 
//			6502.18, 
//			1, 
//			new Double(1.7 * 1024 ).intValue(), // RAM: 1.7 GB
//			new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
//			new Long(160 * 1024) // DISK: 160 GB
//			);
//	}
//	
//	private Vm createXLargeVm_NoID(int userId){
//		return VmFactory.getDesiredVm(
//			userId, 
//			5202.15 * 4, 
//			4, 
//			new Double(15 * 1024).intValue(), // 15 GB
//			new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
//			new Long(1690 * 1024) // 1690 GB
//			);
//	}
//	
//	private <T extends Cloudlet> void build(int userId, List<T> tasks){
//		for (T t: tasks){
//			List<Cloudlet> cloudlets = new ArrayList<>();
//			cloudlets.add(t);
//			Vm vm = null;
//			if (t.getCloudletLength() > 1000)
//				vm = createXLargeVm_NoID(userId);
//			else 
//				vm = createSmallVm_NoID(userId);
//			
//			ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
////			v.setCity(City.Beijing);
//			v.setBudget(50);
//			addVertex(v);
//		}
//		
//		for (T t: tasks){
//			ApplicationVertex base = this.getVertexForCloudlet(t);
//			double outputSize = 0;
//			double mrate = 1;
//			List<File> files = ((Task) t).getFileList();
//			for (File f: files){
//				if (f.getType() == 2)//output as constructed by parser
//					outputSize += f.getSize(); // this size is in bytes
//			}
//	
//			outputSize = outputSize / 1024d; // obtaining KB as applicationEdge requires
//			
//			List<Task> childs = ((Task) t).getChildList();
//			for (Task c: childs){
//				ApplicationVertex child = this.getVertexForCloudlet(c);
//				//addEdge(new ApplicationEdge(outputSize, mrate), base, child);
//			}
//		}
//		System.out.println(super.vertexSet().size() + "+" + super.getEdges().size());
//	}
//	
//	public static void main(String[] args){
//		int num_user = 1;   // number of grid users
//        Calendar calendar = Calendar.getInstance();
//        boolean trace_flag = false;  // mean trace events
//
//        // Initialize the CloudSim library
//        CloudSim.init(num_user, calendar, trace_flag);
//        
//        
//		WorkflowApplication g;
//		try {
//			boolean taskClustering = false;
//			g = new WorkflowApplication(0, taskClustering);
//			String add = taskClustering == true ? "clust" : "";
//			g.export("plots/" + fileName + add + ".dot");
//			System.out.println(g);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
