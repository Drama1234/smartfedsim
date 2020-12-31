package workflowfederation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.Vm;
import org.workflowsim.Task;

import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowDatacenter.WorkflowGenerator;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;

public class WorkflowMakespan {
	
	public static double getWorkflowTime(List<FederationDatacenter> dcs,InternetEstimator internet,Allocation allocation) {
		WorkflowGenerator workflow = (WorkflowGenerator)allocation.getApplication();
		
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
		double total_time = 0;
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		while (tasks.size() != 0) {
//			System.out.println("任务大小："+tasks.size());
			for (Task t: tasks) {
//				System.out.println("任务ID："+t.getCloudletId()+"数据中心ID："+t.getResourceId());
				ApplicationVertex av = workflow.getVertexForCloudlet(t);
				// check for the entering edges to compute the time of
				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
				double offset_time = 0;
				for (ApplicationEdge ae: in_edges){
//					System.out.println("入边："+ae.toString());
					double time = edgeTimeMap.get(ae);
//					System.out.println("时间为："+time);
					if (time > offset_time)
						offset_time = time;
				}
				
				// compute the time of the task here
				double task_time = taskTime(t, allocation);
				
				// set the time for the outer edges
				Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(av);
				
				if (out_edges.size() <= 0) // last node
				{
					total_time = offset_time + task_time;
				}
				else 
				{
					for (ApplicationEdge ae: out_edges) {
						double edge_time = edgeTime(ae, allocation, t, dcs, internet);
						total_time = offset_time + task_time + edge_time;
						edgeTimeMap.put(ae, total_time);
					}
				}
			}
			depth ++;
			tasks = workflow.getTasksWithDepth(depth);
		}
//		System.out.println("科学工作流深度："+depth);
		//计算输出数据从计算中心传输到存储中心的时间
		double output_time = getOutputDataTransfer(allocation,dcs,internet,--depth);
		System.out.println("输出时间："+output_time);
		//计算输入数据从存储中心传输到计算中心的时间
		double input_time = getInputDataTransfer(allocation, dcs, internet);
		System.out.println("输入时间："+input_time);
		total_time = total_time + output_time + input_time;
//		System.out.println("Makespan:"+total_time);
		return total_time;
	}
	
	
//	public static double getWorkflowMakespan(WorkflowGenerator workflow,List<FederationDatacenter> dcs,InternetEstimator internet) {
//		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
//		double total_time = 0;
//		int depth = 1;
//		List<Task> tasks = workflow.getTasksWithDepth(depth);
//		//计算工作流任务的处理时间和边数据传输时间
//		while (tasks.size() != 0) {
////			System.out.println("任务大小："+tasks.size());
//			for (Task t: tasks) {
////				System.out.println("任务ID："+t.getCloudletId()+"数据中心ID："+t.getResourceId());
//				ApplicationVertex av = workflow.getVertexForCloudlet(t);
//				
//				// check for the entering edges to compute the time of
//				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
//				double offset_time = 0;
//				for (ApplicationEdge ae: in_edges) {
////					System.out.println("入边："+ae.toString());
//					double time = edgeTimeMap.get(ae);
////					System.out.println("时间为："+time);
//					if (time > offset_time)
//						offset_time = time;
//				}
//				
//				// compute the time of the task here
//				double task_time = taskTime(t, workflow);
//				
//				// set the time for the outer edges
//				Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(av);
//				
//				if (out_edges.size() <= 0) // last node
//				{
//					total_time = offset_time + task_time;
//				}
//				else 
//				{
//					for (ApplicationEdge ae: out_edges) {
//						double edge_time = edgeTime(ae, workflow, t, dcs, internet);
//						total_time = offset_time + task_time + edge_time;
//						edgeTimeMap.put(ae, total_time);
//					}
//				}
//			}
//			depth ++;
//			tasks = workflow.getTasksWithDepth(depth);
//		}	
//		//计算输出数据从计算中心传输到存储中心的时间
//		double output_time = getOutputDataTransfer(workflow,dcs,internet,--depth);
//		System.out.println("输出时间："+output_time);
//		//计算输入数据从存储中心传输到计算中心的时间
//		double input_time = getInputDataTransfer(workflow, dcs, internet);
//		System.out.println("输入时间："+input_time);
//		total_time = total_time + output_time + input_time;
////		System.out.println("Makespan:"+total_time);
//		return total_time;
//	}
	
	public static double getOutputDataTransfer(Allocation allocation, List<FederationDatacenter> dcs, InternetEstimator internet, int depth) {
		WorkflowGenerator workflow = (WorkflowGenerator)allocation.getApplication();
		double output_time = 0;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
//		System.out.println("输出depth:"+depth);
//		System.out.println("任务大小："+tasks.size());
		if(tasks.size() != 0) 
		{
			for (Task task: tasks)
			{
//				System.out.println("输出任务ID："+t.getCloudletId());
				List<File> files = task.getFileList();
				double outputSize = 0;
				for (File file : files) {
					if (file.getType() == 2) {
						outputSize += file.getSize();
					}
				}
//				System.out.println("输出文件大小："+outputSize);
				output_time = inputTime(outputSize,task,workflow);
			}
		}
		return output_time;
	}
	
	public static double getInputDataTransfer(Allocation allocation, List<FederationDatacenter> dcs, InternetEstimator internet) {
		WorkflowGenerator workflow = (WorkflowGenerator)allocation.getApplication();
		int depth = 1;
		double input_time = 0;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		if(tasks.size() != 0) {
			for (Task task : tasks) {
//				System.out.println("输入任务ID："+task.getCloudletId());
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
				input_time = inputTime(inputSize, task, workflow);
			}
		}
		return input_time;
	}
	
	private static double inputTime(double inputSize,Task t,WorkflowGenerator workflow) {
//		System.out.println("id："+t.getCloudletId());
//		System.out.println("数据中心大小："+workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0).getDatacenterBw());
		double bw = workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0).getDatacenterBw();
		System.out.println("数据中心带宽："+Double.valueOf(String.format("%.2f", bw/1024/1024))+"MB/s");
		System.out.println("任务ID："+t.getCloudletId()+" 数据中心ID："+workflow.getVertexForCloudlet(t).getfeFederationDatacenters().get(0));
		double input_time = inputSize / bw;
		
		return input_time;
	}
	
	public static double taskTime(Task task, Allocation allocation)
	{
		long filesize = task.getCloudletLength();
		ApplicationVertex av = allocation.getApplication().getVertexForCloudlet(task);
		Vm vm = av.getAssociatedVm(task);
//		FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
		
		double service_time = filesize / vm.getCurrentRequestedTotalMips();
		
//		double expected_service_time = filesize / workflow.getVertexForCloudlet(t).getAssociatedVm(t).getCurrentRequestedTotalMips();
//		double cloudsim_service_time = t.getActualCPUTime();
//		System.out.println("mips:"+workflow.getVertexForCloudlet(t).getAssociatedVm(t).getCurrentRequestedTotalMips());
		
//		return expected_service_time;
		return service_time;
	}
	
	public static double edgeTime(ApplicationEdge edge, Allocation allocation, Task t, List<FederationDatacenter> dcs, InternetEstimator internet)
	{
		WorkflowGenerator workflow = (WorkflowGenerator)allocation.getApplication();
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
			
			transfer_time = edge.getMessageLength() / link.getBandwidth() + latency;
//			System.out.println("边的信息："+dc_source.getId()+"---------->"+dc_target.getId());
//			System.out.println("边传输的文件大小："+edge.getMessageLength());
//			System.out.println("边的传输带宽："+link.getBandwidth());
//			System.out.println("传输延迟："+latency);
		}else if(dc_source.getId() == dc_target.getId()) {
			transfer_time = (edge.getMessageLength()) / dc_source.getMSCharacteristics().getDatacenterBw();
//			System.out.println("云内传输文件大小："+edge.getMessageLength());
//			System.out.println("云内传输带宽："+dc_source.getMSCharacteristics().getDatacenterBw());
		}else {
			System.out.println("数据有问题！");
			
		}
		
		//System.out.println("--- Length: "+edge.getMessageLength() * 1024);
		//System.out.println("--- Band:   "+dc_source.getMSCharacteristics().getHighestBw());
		
		return transfer_time;
	}
	
	
	
	public static double getWorkflowID(WorkflowGenerator workflow,List<FederationDatacenter> dcs,InternetEstimator internet) {
		
		Map<ApplicationEdge, Double> edgeTimeMap = new HashMap<ApplicationEdge, Double>();
//		double offset_time = getInputDataTransfer(workflow, dcs, internet);
		double total_time = 0;
		int depth = 1;
		List<Task> tasks = workflow.getTasksWithDepth(depth);
		while (tasks.size() != 0) {
//			System.out.println("任务数量："+tasks.size());
			for (Task t: tasks) {
//				System.out.println("任务ID："+t.getCloudletId());
				ApplicationVertex av = workflow.getVertexForCloudlet(t);
				Set<ApplicationEdge> in_edges = workflow.incomingEdgesOf(av);
				double offset_time = 0;
				for (ApplicationEdge ae: in_edges) {
//					System.out.println("入边："+ae.toString());
					double time = edgeTimeMap.get(ae);
					if (time > offset_time)
						offset_time = time;
				}
				Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(av);
				for (ApplicationEdge ae: out_edges) {
					edgeTimeMap.put(ae, 1.0);
				}
			}
			depth++;
			tasks = workflow.getTasksWithDepth(depth);
		}
		return 0;
	}
}
