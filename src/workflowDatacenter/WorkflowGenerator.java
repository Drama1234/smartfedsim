package workflowDatacenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
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
import federation.resources.City;
import federation.resources.FederationDatacenter;
import federation.resources.VmFactory;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflowtest.Range;
import workflowtest.WorkflowDataset;

public class WorkflowGenerator extends Application{
	
//	protected AbstractRealDistribution distribution;
	
	public String daxPath;
	public static String filename = "resources/RemoteSense_13.xml";
	
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
            DistributionGenerator cluster_delay = new DistributionGenerator(DistributionGenerator.DistributionFamily.WEIBULL, 10.0, 1.0);
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
        ClusteringParameters cp = new ClusteringParameters(1, 0, method, null); // this is for having a pipe (not really!)
        

        Parameters.init(vmNum, daxPath, null,
                null, op, cp, sch_method, pln_method,
                null, 0);
        ReplicaCatalog.init(file_system);
	}
	
	public WorkflowGenerator(String filename, int userId, boolean clustering,List<FederationDatacenter> datacenterlist)
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
	
//	private Vm createVm(int userId,double value) {
//		Range mipsValue = new Range(0, 6502.18*30);
//		Range coresValue = new Range(0, 32);
//		Range ramValue = new Range(0, 64*1024);//2GB-64GB
//		Range bwValue = new Range(0, 500 * 1024 * 1024);//100MB/s-500MB/s
//		Range storageValue = new Range(0, 10 * 1024 * 1024);//250GB-10TB
////		distribution = new UniformRealDistribution();
////		double value = distribution.sample();
//		double mips;
//		int cores,ram;
//		long bw,storage;
//		mips = mipsValue.denormalize(value);
//		cores = (int)coresValue.denormalize(value);
//		ram = (int)ramValue.denormalize(value);
//		bw = (long)bwValue.denormalize(value);
//		storage = (long)storageValue.denormalize(value);
//		Vm vm = VmFactory.getCustomVm(userId, mips, cores, ram, bw, storage);
//		return vm;
//	}
	
	private Vm createSmallVm(int userId) {
		return VmFactory.getDesiredVm(
				userId, 
				6502.18, 
				1, 
				8*1024,//ram
				100 * 1024 * 1024,//100MB/s
				200 * 1024//200GB
				);
	}
	
	private Vm createMediumVm(int userId) {
		return VmFactory.getDesiredVm(
				userId, 
				6502.18*2, 
				2, 
				16*1024,//ram
				100 * 1024 * 1024,//100MB/s
				500 * 1024//500GB
				);
	}
	private Vm createlLargeVm(int userId) {
		return VmFactory.getDesiredVm(
				userId, 
				6502.18*4, 
				4, 
				32*1024,//ram
				100 * 1024 * 1024,//100MB/s
				1024 * 1024//1TB
				);
	}
	
//	private List<FederationDatacenter> getDatacenter(List<FederationDatacenter> datacenterlist, City city ) {
//		List<FederationDatacenter> bfd = new ArrayList<>();
//		for (FederationDatacenter fd : datacenterlist) {
//			if(fd.getMSCharacteristics().getCity() == city) {
//				bfd.add(fd);
//			}
//		}
//		return bfd;
//	}

	private <T extends Cloudlet> void build(int userId, List<T> tasks,List<FederationDatacenter> datacenterlist) 
	{
		
//		distribution = new UniformRealDistribution();
//		distribution.reseedRandomGenerator(77);
		
		//System.out.println("任务数量 "+tasks.size()+"数据中心数量：" +datacenterlist.size());
		
		for (T task : tasks) {
//			System.out.println(task.getCloudletLength());
			List<Cloudlet> cloudlets = new ArrayList<>();
			cloudlets.add(task);
			Vm vm = null;
			if(task.getCloudletLength() < 900) {
				vm = createSmallVm(userId);
				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm, datacenterlist.get(0));
				v.setBudget(10);
				//v.setCity(City.Beijing);
				
				//v.setFederationDatacenter(datacenterlist.get(0));
				//v.setFederationDatacenter(getDatacenter(datacenterlist, v.getEnumCity()).get(1));
				addVertex(v);
			}else if(task.getCloudletLength() < 2000) {
				vm = createMediumVm(userId);
				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
				//v.setCity(City.Beijing);
				//先随机定义一个，后续调度时再修改
				//v.setFederationDatacenter(datacenterlist.get(0));
				//v.setFederationDatacenter(getDatacenter(datacenterlist, City.Beijing).get(0));
				v.setBudget(20);
				addVertex(v);
			}else {
				vm = createlLargeVm(userId);
				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
				//v.setCity(City.Beijing);
				//先随机定义一个，后续调度时再修改
				//v.setFederationDatacenter(datacenterlist.get(0));
				//v.setFederationDatacenter(getDatacenter(datacenterlist, City.Beijing).get(0));
				v.setBudget(30);
				addVertex(v);
			}
		}

		//System.out.println("顶点有 "+vertexSet().size());
		//System.out.println(super.toString());
		
		
//		for (int i = 0; i < tasks.size(); i++) {
//			List<Cloudlet> cloudlets = new ArrayList<>();
//			cloudlets.add(tasks.get(i));
//			Vm vm = null;
//			if(i == 0){
//				vm = createVm(userId, 0.1);
//				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
//				v.setCity(City.Beijing);
//				v.setFederationDatacenter(datacenterlist.get(i));
//				v.setBudget(10);
//				addVertex(v);
//			}
//			else if(i == (tasks.size()-1)) {
//				vm = createVm(userId, 0.1);
//				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
//				v.setCity(City.Beijing);
//				v.setFederationDatacenter(datacenterlist.get(i));
//				v.setBudget(10);
//				addVertex(v);
//			}
//			else if(tasks.get(i).getCloudletLength() < 1100) {
//				vm = createVm(userId, 0.2);
//				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
//				v.setBudget(15);
//				addVertex(v);
//			}
//			else if(tasks.get(i).getCloudletLength() < 2100) {
//				vm = createVm(userId, 0.3);
//				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
//				v.setBudget(20);
//				addVertex(v);
//			}
//			else if(tasks.get(i).getCloudletLength() < 3100) {
//				vm = createVm(userId, 0.4);
//				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
//				v.setBudget(30);
//				addVertex(v);
//			}else if(tasks.get(i).getCloudletLength() >= 3100) {
//				vm = createVm(userId, 0.8);
//				ApplicationVertex v = new ApplicationVertex(userId, cloudlets, vm);
//				v.setBudget(50);
//				addVertex(v);
//			}			
//		}
//		System.out.println("任务数量 "+tasks.size()+"数据中心数量：" +datacenterlist.size());
//		Range interbwValue = new Range(10*1024*1024,35*1024*1024);//10MB/s-35MB/s
//		Range latencyValue = new Range(0.09, 0.13);
		
		
		for(T t : tasks) {
//			List<Task> parents = ((Task) t).getParentList();
//			System.out.println("父亲数量：" +parents.size());
			ApplicationVertex base = this.getVertexForCloudlet(t);
			double size = 0;
			double bandwidth = 0;
			double latency = 0;
			List<File> parentfiles = ((Task) t).getFileList();
			List<File> pfiles = new ArrayList<>();
			for (File f: parentfiles){
				if (f.getType() == 2)//output as constructed by parser
					//f.getFileAttribute().getName();
					pfiles.add(f);
					//outputSize += f.getSize(); // this size is in bytes
			}
			
			List<Task> childs = ((Task) t).getChildList();
			for (Task c:childs) {
				ApplicationVertex child = this.getVertexForCloudlet(c);
				List<File> childfiles = ((Task) c).getFileList();
				List<File> cfiles = new ArrayList<>();
				for (File f :childfiles) {
					if(f.getType() == 1) {
						cfiles.add(f);
					}
				}
				
				for (File file : cfiles) {
					for (File file2 : pfiles) {
						if(file2.getFileAttribute().getName().equals(file.getFileAttribute().getName())) {
							//System.out.println(file2.getFileAttribute().getName());
							size+=file2.getSize();
						}
					}
				}
//					//System.out.println("父亲文件："+pfile.getFileAttribute().getName());
//					for (File cfile : childfiles) {
//						//System.out.println("孩子文件："+cfile.getFileAttribute().getName());
//						
//						
////						if(pfile.getFileAttribute().getName() == cfile.getFileAttribute().getName()) {
////							//size+=cfile.getSize();
////							System.out.println(pfile.getFileAttribute().getName());
//						}
//					}
				size = size/1024d;// obtaining KB as applicationEdge requires
			
			
//			System.out.println("孩子的数量：" + childs.size());
//			for (Task c : childs) {
//				ApplicationVertex child = this.getVertexForCloudlet(c);
//				@SuppressWarnings("unchecked")
//				List<File> filess = ((Task) c).getFileList();
//				for (int i = 0; i < files.size(); i++) {
//					if (files.get(i).getType() == 1)//output as constructed by parser
//						inputSize += files.get(i).getSize(); // this size is in bytes
//				}
//				inputSize = inputSize / 1024d; // obtaining KB as applicationEdge requires
				
//				double value = distribution.sample();
//				BigDecimal b = new BigDecimal(interbwValue.denormalize(value)); 
//				bandwidth = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
//				BigDecimal b1 = new BigDecimal(latencyValue.denormalize(value)); 
//				latency = b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
				
				bandwidth = 20*1024; //20*1024 KB/s
				latency = 0.12;
				
				addEdge(new ApplicationEdge(size, bandwidth, latency), base, child);
			}
		}
//		System.out.println(super.toString());
//		System.out.println(super.vertexSet().size() + "+" + super.getEdges().size());
	}
		//System.out.println(super.toString());
		
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
        	boolean taskClustering = false;
        	
        	WorkflowDataset dataset = new WorkflowDataset(20, filename);
        	List<FederationDatacenter> datacenters = dataset.createDatacenters();
        	WorkflowGenerator app = new WorkflowGenerator("RemoteSense_13", 0, taskClustering, datacenters);
        	
			//app.export("plots/" + "RemoteSense_1" + ".dot");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
