package workflowfederation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.File;
import org.workflowsim.Task;


import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowDatacenter.WorkflowGenerator;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;


public class WorkflowComputer {
	public static double getFlowCompletionTime(WorkflowGenerator workflow, List<FederationDatacenter> dcs, InternetEstimator internet) {
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
		System.out.println("");
		double process_time = 0;
		double input_time = 0;
		double output_time = 0;
		
		//计算输入数据从存储中心传输到计算中心的时间
		if(tasks.size() != 0) {
			for(Task t: tasks) {
				List<File> files = t.getFileList();
				double inputSize = 0;
				for(File file : files) {
					if(file.getType() == 1) {
						inputSize += file.getSize();
					}
				}
				input_time = inputTime(inputSize, t, workflow);
			}
		}
		
		while (tasks.size() != 0)
		{
			for (Task t: tasks)
			{
				ApplicationVertex av = workflow.getVertexForCloudlet(t);
				
				// check for the entering edges to compute the time of 
				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
//				System.out.println("工作流应用程序的入边："+in_edges.size());
				double offset_time = 0;
				for (ApplicationEdge ae: in_edges)
				{
					double time = edgeTimeMap.get(ae);
//					System.out.println("时间大小"+time);
					if (time > offset_time)
						offset_time = time;
				}
				
				// compute the time of the task here
				double task_time = taskTime(t, workflow);
				
				// set the time for the outer edges
				Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(av);
				
				if (out_edges.size() <= 0) // last node
				{
					process_time = offset_time + task_time;
				}
				else
				{
					for (ApplicationEdge ae: out_edges)
					{
						double edge_time = edgeTime(ae, workflow, t, dcs, internet);
						process_time = offset_time + task_time + edge_time;
						edgeTimeMap.put(ae, process_time);
					}
				}
				// System.out.println("Task "+t.getCloudletId()+ " offset: "+offset_time+ " task time: "+task_time+ " total_time: "+total_time);
			}
			depth ++;
			tasks = workflow.getTasksWithDepth(depth);
		}
		//计算输出数据从计算中心传输到存储中心的时间
		tasks = workflow.getTasksWithDepth(depth--);
		if(tasks.size() != 0) 
		{
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
		
		System.out.println("总共时间: "+ totaltime);
		return totaltime;
	}
	
	public static double taskTime(Task t, WorkflowGenerator workflow)
	{
		long filesize = t.getCloudletLength();	
		double expected_service_time = filesize / workflow.getVertexForCloudlet(t).getAssociatedVm(t).getMips();
		double cloudsim_service_time = t.getActualCPUTime();
		
		return expected_service_time;
	}
	
	private static double inputTime(double inputSize,Task t,WorkflowGenerator workflow) {
		double bw = workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0).getDatacenterBw();
		double input_time = inputSize / bw;
		
		return input_time;
	}

	
	public static double edgeTime(ApplicationEdge edge, WorkflowGenerator workflow, Task t, List<FederationDatacenter> dcs, InternetEstimator internet)
	{
		double latency = 0;
		double transfer_time = 0;
		
		//任务所在的云服务供应商
		FederationDatacenter dc_source = Federation.findDatacenter(dcs, t.getResourceId());
		//入边传输时间
		ApplicationVertex input_vertex = workflow.getEdgeSource(edge);
		Task input_task = (Task) workflow.getCloudletFromVertex(input_vertex);
		FederationDatacenter dc_input = Federation.findDatacenter(dcs, input_task.getResourceId());
		//出边传输时间
		ApplicationVertex target_vertex = workflow.getEdgeTarget(edge);
		Task target_task = (Task) workflow.getCloudletFromVertex(target_vertex);
		FederationDatacenter dc_target = Federation.findDatacenter(dcs, target_task.getResourceId());
		
		if (dc_source.getId() == dc_target.getId()||dc_source.getId()==dc_input.getId()){
			transfer_time = (edge.getMessageLength() * 1024 * 1024)/dc_source.getMSCharacteristics().getDatacenterBw();
			System.out.println("联盟云Id:"+dc_source.getId()+" 云内传输带宽："+dc_source.getMSCharacteristics().getDatacenterBw()/(1024*1024)+" MB/s");
		}else if(!dc_input.equals(null)){
			InternetLink link = null;
			try { 
				link = internet.getInternetLink(dc_input, dc_source);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}		
			latency = link.getLatency();
			transfer_time = (edge.getMessageLength() * 1024 * 1024)/link.getBandwidth() + latency;
			System.out.println("跨云传输带宽:"+link.getBandwidth()/(1024*1024)+"MB/s");
		}else if(!dc_target.equals(null)){
			InternetLink link = null;
			try { 
				link = internet.getInternetLink(dc_source,dc_target);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			latency = link.getLatency();
			transfer_time = (edge.getMessageLength() * 1024 * 1024)/link.getBandwidth() + latency;
			System.out.println("跨云传输带宽:"+link.getBandwidth()/(1024*1024)+"MB/s");
		}
		//double transfer_time = (edge.getMessageLength() * 1024) / dc_source.getMSCharacteristics().getHighestBw();
		
//		System.out.println("--- Length: "+ edge.getMessageLength());
//		System.out.println("--- Transfer_time: "+ transfer_time);
//		System.out.println("--- Latency: " + latency);
		
		return transfer_time;
	}
	
	public static double getFlowCost(List<FederationDatacenter> dcs,Allocation allocation, InternetEstimator internet)
	{
		double total = CostComputer.actualCost(allocation,dcs,internet);
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
