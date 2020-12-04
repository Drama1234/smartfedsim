package WorkTest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.Task;

import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowDatacenter.DatacenterGenerator;
import workflowDatacenter.WorkflowGenerator;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;


public class WorkflowComputer {
	public static double getInputDataTransfer(WorkflowGenerator workflow, List<FederationDatacenter> dcs, InternetEstimator internet) {
		int depth = 1;
		double input_time = 0;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		if(tasks.size() != 0) {
			for (Task task : tasks) {
				@SuppressWarnings("unchecked")
				List<File> files = task.getFileList();
				double inputSize = 0;
				for(File file : files) {
					if(file.getType() == 1) {
//						System.out.println("文件名称："+file.getName());
//						System.out.println("文件大小："+file.getSize());
						inputSize += file.getSize();
					}
				}
				input_time += inputTime(inputSize, task, workflow);
			}
		}
		return input_time;
	}
	
	public static double getWorkflowID(WorkflowGenerator workflow) {
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		while (tasks.size() != 0) {
			System.out.println("任务数量："+tasks.size());
			for (Task t: tasks) {
				System.out.println("任务ID："+t.getCloudletId());
			}
			depth++;
			tasks = workflow.getTasksWithDepth(depth);
		}
		return 0;
	}
	
	public static double getWorkFlowCompletionTime(WorkflowGenerator workflow,List<FederationDatacenter> dcs,Allocation allocation,InternetEstimator internet) {
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
		double total_time = 0;
		double offset_time = getInputDataTransfer(workflow, dcs, internet);
		while (tasks.size() != 0) {
			for (Task t: tasks) {
				System.out.println("任务ID："+t.getResourceId());
				ApplicationVertex av = workflow.getVertexForCloudlet(t);
				
				// check for the entering edges to compute the time of
				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
				for (ApplicationEdge ae: in_edges) {
					double time = edgeTimeMap.get(ae);
					if (time > offset_time)
						offset_time = time;
				}
				
				// compute the time of the task here
				double task_time = taskTime(t, workflow);
				
				// set the time for the outer edges
				Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(av);
				
				if (out_edges.size() <= 0) // last node
				{
					total_time = offset_time + task_time;
				}
				else 
				{
					for (ApplicationEdge ae: out_edges) {
						double edge_time = edgeTime(ae, workflow, t, dcs, internet);
						total_time = offset_time + task_time + edge_time;
						edgeTimeMap.put(ae, total_time);
					}
				}
			}
			depth ++;
			tasks = workflow.getTasksWithDepth(depth);
		}
		double output_time = 0;		
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
		total_time = total_time + output_time;
		System.out.println("Total time: "+total_time);
		return total_time;
	}
	
	
	public static double makespan(WorkflowGenerator workflow,List<FederationDatacenter> dcs,InternetEstimator internet) {
		int depth = 1;
		double input_time = 0;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
		if(tasks.size() != 0) {
			for (Task task : tasks) {
				@SuppressWarnings("unchecked")
				List<File> files = task.getFileList();
				double inputSize = 0;
				for(File file : files) {
					if(file.getType() == 1) {
						System.out.println("文件名称："+file.getName());
						System.out.println("文件大小："+file.getSize());
						inputSize += file.getSize();
					}
				}
				input_time = inputTime(inputSize, task, workflow);
			}
		}
		return input_time;
	}
	
	public static double getFlowCompletionTime(WorkflowGenerator workflow, List<FederationDatacenter> dcs, InternetEstimator internet) 
	{
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
				System.out.println("输出时间为："+output_time);
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
//		double cloudsim_service_time = t.getActualCPUTime();
		
		return expected_service_time;
	}
	
	private static double inputTime(double inputSize,Task t,WorkflowGenerator workflow) {
		double bw = workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0).getDatacenterBw();
		System.out.println("数据中心带宽："+bw);
		System.out.println("任务ID："+t.getCloudletId()+"数据中心ID："+workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0));
		double input_time = inputSize / bw;
		
		return input_time;
	}

	
	public static double edgeTime(ApplicationEdge edge, WorkflowGenerator workflow, Task t, List<FederationDatacenter> dcs, InternetEstimator internet)
	{
		ApplicationVertex target_vertex = workflow.getEdgeTarget(edge);
		Task target_task = (Task) workflow.getCloudletFromVertex(target_vertex);
//		System.out.println("数据中心大小："+dcs.size());
//		for (FederationDatacenter federationDatacenter : dcs) {
//			System.out.println("联盟云ID："+federationDatacenter.getId());
//		}
//		System.out.println("任务ID："+t.getResourceId());
		FederationDatacenter dc_source = Federation.findDatacenter(dcs, t.getResourceId());
//		System.out.println("源数据中心："+dc_source.getId());
//		System.out.println("目标数据中心："+target_task.getId());
		FederationDatacenter dc_target = Federation.findDatacenter(dcs, target_task.getResourceId());
//		System.out.println("目标数据中心:"+dc_target.getId());
		double latency = 0;
		double transfer_time = 0;
		
		if (dc_source.getId() != dc_target.getId())
		{
			InternetLink link = null;
			try { 
				link = internet.getInternetLink(dc_source, dc_target);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			latency = link.getLatency();
			transfer_time = (edge.getMessageLength() * 1024* 1024) / link.getBandwidth()+latency;
		}else {
			transfer_time = (edge.getMessageLength() * 1024 * 1024) / dc_source.getMSCharacteristics().getDatacenterBw();
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
	
	public static void main(String[] args) {
		Log.enable();
		int num_user = 1;   // users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = true;  // trace events
		CloudSim.init(num_user, calendar, trace_flag);
		try {
//		    String filename = "resources/RemoteSense_13.xml";
			DatacenterGenerator dg = new DatacenterGenerator(77*15);
			int numHost = 50 * 20; // it will assign more or less 1000 host to each datacenter
			List<FederationDatacenter> dcs = dg.getDatacenters(20, numHost);
			
//			List<Application> apps = new ArrayList<Application>();
//			apps.add(new WorkflowGenerator(filename, 1, dcs));
			
			WorkflowGenerator app = new WorkflowGenerator("RemoteSense_13", 0, dcs);
			
			InternetEstimator inetEst = new InternetEstimator(dcs, 77);
			
//			WorkflowComputer.getWorkflowID(app);
			WorkflowComputer.getInputDataTransfer(app, dcs, inetEst);
//			double makespan = WorkflowComputer.getWorkFlowCompletionTime(app, dcs, inetEst);
			
//			System.out.println("最大完工时间："+makespan);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
