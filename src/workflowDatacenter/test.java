package workflowDatacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.workflowsim.Task;
import org.workflowsim.WorkflowParser;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.DistributionGenerator;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

public class test {
	public static String daxPath = "resources/" + "RemoteSense_13" + ".xml";
	public static void main(String[] args) {
		setWorkflowSimConfig();
		WorkflowParser parser = new WorkflowParser(1, null, null, daxPath);
		parser.parse();
		List<Task> tasks = parser.getTaskList();
		for (Task task : tasks) {
			List<Task> childs = task.getChildList();
			System.out.println("孩子的数量：" + childs.size());
		}
		build(1, tasks);
	}
	private static <T extends Cloudlet> void build(int userId, List<T> tasks){
		for (T t: tasks) {
			List<Task> childs =((Task) t).getChildList(); 
			System.out.println("孩子的数量：" + childs.size());
		}
	}
	
	
	static void setWorkflowSimConfig() {
		int vmNum = 5;//number of vms;
		Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.FCFS;
        Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;
        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;
        
        /**
         * clustering delay must be added, if you don't need it, you can set all the clustering
         * delay to be zero, but not null
         */
        Map<Integer, DistributionGenerator> clusteringDelay = new HashMap<Integer, DistributionGenerator>();
        
        int maxLevel = 4; // Montage has at most 11 horizontal levels 
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
	
}
