package workflowfederation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.File;
import org.workflowsim.Task;


import application.ApplicationEdge;
import application.ApplicationVertex;
import application.WorkflowApplication;
import federation.resources.FederationDatacenter;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;


public class WorkflowComputer {
	public static double getFlowCompletionTime(WorkflowApplication workflow, List<FederationDatacenter> dcs, InternetEstimator internet) {
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
		double process_time = 0;
		double input_time = 0;
		double output_time = 0;
		double total_time = 0;
		
		if (tasks.size() != 0)
		{
			for (Task t: tasks)
			{
				@SuppressWarnings("unchecked")
				List<File> files = t.getFileList();
				double inputSize = 0;
				for (File file : files) {
					if (file.getType() == 1) {
							inputSize += file.getSize();
					}
				}
				input_time = inputTime(inputSize,t,workflow);
			}
		}
		depth ++;
		tasks = workflow.getTasksWithDepth(depth);
		
		while(tasks.size() != 0) {
			for (Task t: tasks) {
				ApplicationVertex av = workflow.getVertexForCloudlet(t);
				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
				double offset_time = 0;
				for (ApplicationEdge ae: in_edges) {
					double time = edgeTimeMap.get(ae);
					if (time > offset_time)
						offset_time = time;
				}
				// compute the time of the task here
				double task_time = taskTime(t, workflow);//数据量本身并不能代表实际在虚拟机内的执行时间，因此设定runtime来表示具体的执行时间
				for (ApplicationEdge ae: in_edges) {
					double edge_time = edgeTime(ae, workflow, t, dcs, internet);
					process_time = offset_time + task_time + edge_time;
					edgeTimeMap.put(ae, process_time);
				}
			}
			depth ++;
			tasks = workflow.getTasksWithDepth(depth);
		}
		tasks = workflow.getTasksWithDepth(depth--);
		if (tasks.size() != 0) {
			for (Task t: tasks)
			{
				List<File> files = t.getFileList();
				double outputSize = 0;
				for (File file : files) {
					if (file.getType() == 2) {
							outputSize += file.getSize();
					}
				}
				output_time = inputTime(outputSize,t,workflow);
			}
		}
		double totaltime = input_time + output_time + process_time;
		System.out.println("Total time: "+ totaltime);
		return totaltime;
	}
	
	public static double taskTime(Task t, WorkflowApplication workflow)
	{
		long filesize = t.getCloudletLength();	
		double expected_service_time = filesize / workflow.getVertexForCloudlet(t).getAssociatedVm(t).getMips();
		double cloudsim_service_time = t.getActualCPUTime();
		
		return expected_service_time;
	}
	
	private static double inputTime(double inputSize,Task t,WorkflowApplication workflow) {
		double input_time = inputSize / workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0).getDatacenterBw();
		
		return input_time;
	}
	
	public static double edgeTime(ApplicationEdge edge, WorkflowApplication workflow, Task t, List<FederationDatacenter> dcs, InternetEstimator internet)
	{
		ApplicationVertex target_vertex = workflow.getEdgeTarget(edge);
		Task target_task = (Task) workflow.getCloudletFromVertex(target_vertex);
		
		FederationDatacenter dc_source = Federation.findDatacenter(dcs, t.getResourceId());
		FederationDatacenter dc_target = Federation.findDatacenter(dcs, target_task.getResourceId());
		double latency = 0;
		double transfer_time = 0;
		if (dc_source.getId() != dc_target.getId())
		{
			InternetLink link = null;
			try { link = internet.getInternetLink(dc_source, dc_target);} 
			catch (Exception e) {e.printStackTrace();}
					
			latency = link.getLatency();
			
			transfer_time = (edge.getMessageLength() * 1024)/link.getBandwidth();
		}
		else {
			transfer_time = (edge.getMessageLength() * 1024)/dc_source.getMSCharacteristics().getDatacenterBw();
		}
		
		//qdouble transfer_time = (edge.getMessageLength() * 1024) / dc_source.getMSCharacteristics().getHighestBw();
		
		System.out.println("--- Length: "+ edge.getMessageLength());
		System.out.println("--- Transfer_time: "+ transfer_time);
		System.out.println("--- Latency: " + latency);
		
		return latency + transfer_time;
	}
	
	public static double getFlowCost(WorkflowApplication workflow,List<FederationDatacenter> dcs,Allocation allocation, double completionTime, InternetEstimator internet)
	{
		double total = CostComputer.actualCost(allocation,dcs,internet);
//		double net = CostComputer.actualNetCost(allocation,internet);
		return total;
//		
//		// cost of only resources per hour
//		double resourcesPerHour = total - net;
//		
//		// times we execute a workflow per hour
//		double workflowPerHour = 3600d / completionTime;
//		
//		// cost of network for one run of the workflow
//		double netOneRun = net;
//		
//		// cost of only network per hour
//		double netPerHour = netOneRun * workflowPerHour;
//		
//		return resourcesPerHour + netPerHour;
	}
}
