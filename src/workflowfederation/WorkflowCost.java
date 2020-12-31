package workflowfederation;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Vm;
import org.workflowsim.Task;

import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowDatacenter.WorkflowGenerator;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;

public class WorkflowCost {
	public static double getWorkflowCost(List<FederationDatacenter> dcs,Allocation allocation, InternetEstimator internet) {
		double amount = 0d, cost = 0d;
		double edge_time = 0;
		double time = 0;
		double vm_time = 0;
		double network_amount = 0;
		WorkflowGenerator workflow = (WorkflowGenerator)allocation.getApplication();
		
		for(ApplicationVertex av: allocation.getApplication().vertexSet()) {
			Vm vm = allocation.getApplication().getVmForVertex(av);
			Task task = (Task) allocation.getApplication().getCloudletFromVertex(av);
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			Set<ApplicationEdge> edges = workflow.edgesOf(av);
			
			//计算任务在虚拟机上的执行时间
			double task_time = WorkflowMakespan.taskTime(task, allocation);
			//计算任务前序和后继节点之间网络传输的时间
			for (ApplicationEdge ae: edges) {
				//入边传输时间
				time = WorkflowMakespan.edgeTime(ae, allocation, task, dcs, internet);
				edge_time += time;
			}
			vm_time = task_time + edge_time;
			vm_time = Double.valueOf(String.format("%.2f",vm_time));
			if(datacenter != null) {
				cost = (singleVmCost(vm, datacenter)/60) * vm_time;
				cost = Double.valueOf(String.format("%.2f",cost));
//				System.out.println("vm cost:"+Double.valueOf(String.format("%.2f",singleVmCost(vm, datacenter)))+" vm_time:"+vm_time);
//				System.out.println("Vm " +vm.getId() +" rent cost of task" + av.getId() + " is " + cost);
				amount += cost;
			}			
		}
		//计算网络成本
		network_amount = netWorkCost(workflow,allocation,dcs,internet);
		amount = amount + network_amount;
		
		return amount;
	}
	
	public static double netWorkCost(WorkflowGenerator workflow, Allocation allocation, List<FederationDatacenter> dcs, InternetEstimator internet) {
		double cost = 0;
		for (Vm vm: allocation.getApplication().getAllVms()) {
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			ApplicationVertex vertex = workflow.getVertexForVm(vm);
			Set<ApplicationEdge> out_edges = workflow.outgoingEdgesOf(vertex);
			Task source_task = (Task)workflow.getCloudletFromVertex(vertex);
			if (out_edges.size() <= 0) {
				cost += 0;
			}else {
				for (ApplicationEdge ae: out_edges) {
					ApplicationVertex target_vertex = workflow.getEdgeTarget(ae);
					Task target_task = (Task) workflow.getCloudletFromVertex(target_vertex);
					
					FederationDatacenter dc_source = Federation.findDatacenter(dcs, source_task.getResourceId());
					FederationDatacenter dc_target = Federation.findDatacenter(dcs, target_task.getResourceId());
					
					if (dc_source.getId() != dc_target.getId()) {
						InternetLink link = null;
						try { link = internet.getInternetLink(dc_source, dc_target);} 
						catch (Exception e) {e.printStackTrace();}
						double interBwCost = link.getBwcost();
						cost += computeLinkCost(ae, vm.getId(), dc_source.getId(), dc_target.getId(), interBwCost);
					}else {
						cost+=0;
					}
				}
			}
		}
		System.out.println("网络成本为："+Double.valueOf(String.format("%.2f", cost)));
		return cost;
	}
	
	
	public static double actualNetCost(Allocation allocation,InternetEstimator internet) {
		double amount = 0d;
		for (Vm vm: allocation.getApplication().getAllVms()) {
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			ApplicationVertex vertex = allocation.getApplication().getVertexForVm(vm);
			Set<ApplicationEdge> edges = allocation.getApplication().edgesOf(vertex);
			
			if (datacenter != null){	
				amount += computeNetCosts(vm, edges, allocation, datacenter, internet);
			}
		}
		return amount;
	}
	
	
	public static double computeNetCosts(Vm vm, Set<ApplicationEdge> edges, Allocation allocation, FederationDatacenter datacenter,InternetEstimator internet) {
		double cost = 0;
		int sourceVmId = vm.getId();
		int sourceProvId = allocation.getAllocatedDatacenterId(vm);
		System.out.println("虚拟机ID"+sourceVmId+"-供应商ID"+sourceProvId);
		for (ApplicationEdge e: edges){
			Vm targetVm  = e.getTargetVm();
			int targetProvId = allocation.getAllocatedDatacenterId(targetVm);
			System.out.println("原供应商ID"+sourceProvId+"目标供应商ID"+targetProvId);
			if(sourceProvId!=targetProvId) {
				InternetLink link = null;
				try {
					link = internet.getInternetLink(sourceProvId, targetProvId);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				double interBwCost = link.getBwcost();
				cost += computeLinkCost(e, sourceVmId, sourceProvId, targetProvId, interBwCost);
			}else if(sourceProvId == targetProvId) {
				cost += 0;
			}else {
				System.out.println("输入有问题！");
			}
		}
		return cost;
	}
	
	public static double computeLinkCost(ApplicationEdge e, int sVmId, int sProvId, int tProvId, double price){
		double cost = 0;
		if (e.getSourceVmId() == sVmId){
			if (sProvId != tProvId){
				//System.out.println("Data: "+e.getMBperHour()+ "Price: "+price);
				//传输过来是KB，转换为GB
				cost += e.getMessageLength()/(1024*1024) * price;
//				System.out.println("边的文件大小*："+e.getMessageLength());
				//cost += e.getMBperHour() / 1024 * price;
			}
		}
		return cost;
	}
	
	public static double singleVmCost(Vm vm, FederationDatacenter datacenter)
	{
		double amount = 0d;
		amount = calculateCostCustomVm(datacenter, vm);
		FederationLog.timeLogDebug("(CostComputer) total vm cost: " + amount);
		return amount;
	}
	
	public static double getCostPerMem(FederationDatacenter fd){
		double costPerMem = fd.getMSCharacteristics().getCostPerMem();
		return costPerMem / 1024;
	}

	public static double getCostPerCpu(FederationDatacenter fd){
		double costPerCpu = fd.getMSCharacteristics().getCostPerCpu();
		return costPerCpu;
	}
	//GB
	public static double getCostPerStorage(FederationDatacenter fd){
		double costPerSto = fd.getMSCharacteristics().getCostPerStorage();
		return costPerSto / 1024;
	}
	
	private static double calculateCostCustomVm(FederationDatacenter fd, Vm vm)
	{
		//double costPerCPU = vm.getNumberOfPes(). // used for cost per cpu
		
		double costCPU = vm.getNumberOfPes() * getCostPerCpu(fd);
		double costRam = vm.getRam() * getCostPerMem(fd); 
		double costStorage = vm.getSize() * getCostPerStorage(fd);
		FederationLog.timeLogDebug("(CostComputer) custom_vm: " + costRam + " + " + costStorage + " + " + costCPU);
		double costVm = costRam + costStorage + costCPU;
		return costVm;
	}
}
