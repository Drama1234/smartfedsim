package workflowfederation;


import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import application.Application;
import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowmapping.MappingSolution;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;

/**
 * This class contains facilities to compute the cost
 * for VMs.
 * @author carlini, anastasi
 *
 */

public class CostComputer {
	/**
	 * Compute the cost for the given MappingSolution.
	 * If a cloudlet is not assigned, its cost is not considered.
	 * @param solution
	 * @return
	 */
	public static double expectedCost(MappingSolution solution)
	{
		Application application = solution.getApplication();
		double amount = 0d;
		
		for (Cloudlet c: application.getAllCloudlets())
		{
			FederationDatacenter datacenter = solution.getMapping().get(c);
			Vm vm = application.getVertexForCloudlet(c).getAssociatedVm(c);
			
			if (datacenter != null)
				amount += singleVmCost(vm, datacenter);
		}
		
		return amount;
	}
	
	
	/**
	 * Computes the cost for the given allocation.
	 * If a VM is not allocated, its cost is not considered.
	 * @param allocation
	 * @return
	 */
	public static double actualCost(Allocation allocation,InternetEstimator internet){
		double amount = 0d, cost = 0d;
		
		for (Vm vm: allocation.getApplication().getAllVms()) {
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			ApplicationVertex vertex = allocation.getApplication().getVertexForVm(vm);
			Set<ApplicationEdge> edges = allocation.getApplication().edgesOf(vertex);
			
			if (datacenter != null) {
				System.out.println("Vm cost is " +vm.getId() +" is " + singleVmCost(vm, datacenter)) ;
				cost = singleVmCost(vm, datacenter); 
				amount += cost;
				System.out.println("Net cost is for vm " + vm.getId() + " is " + computeNetCosts(vm, edges, allocation, datacenter,internet));
				amount += computeNetCosts(vm, edges, allocation, datacenter,internet);
			}
			System.out.println("");
		}
		return amount;
	}
	
	/**
	 * It calculates only the net costs (used by Experiment)
	 * @param allocation
	 * @return
	 */
	public static double actualNetCost(Allocation allocation,InternetEstimator internet){
		double amount = 0d;
		
		for (Vm vm: allocation.getApplication().getAllVms())
		{
			FederationDatacenter datacenter = allocation.getAllocatedDatacenter(vm);
			ApplicationVertex vertex = allocation.getApplication().getVertexForVm(vm);
			Set<ApplicationEdge> edges = allocation.getApplication().edgesOf(vertex);
			
			if (datacenter != null){	
					amount += computeNetCosts(vm, edges, allocation, datacenter,internet);
			}
		}
		return amount;
	}
	
	public static double computeNetCosts(Vm vm, Set<ApplicationEdge> es, Allocation a, FederationDatacenter f,InternetEstimator internet) {
		double cost = 0;
		int sourceVmId = vm.getId();
		int sourceProvId = a.getAllocatedDatacenterId(vm);
		for (ApplicationEdge e: es){
			
			Vm targetVm  = e.getTargetVm();
			int targetProvId = a.getAllocatedDatacenterId(targetVm);
			
			InternetLink link = null;
			try {link = internet.getInternetLink(sourceProvId, targetProvId);} 
			catch (Exception e1) {e1.printStackTrace();}
			
			double interBwCost = link.getBwcost();
			
			cost += computeLinkCost(e, sourceVmId, sourceProvId, targetProvId, interBwCost);
		}
		return cost;
	}
	
	
	/**
	 * Given an edge, compute the cost for the network to be charged to the source Vm.
	 * @param e The Edge
	 * @param sVmId The source Vm Id
	 * @param sProvId The provider Id for the source Vm
	 * @param tProvId The provider Id for the target Vm
	 * @param price	The cost of transmitting 1 MB
	 * @return
	 */
	public static double computeLinkCost(ApplicationEdge e, int sVmId, int sProvId, int tProvId,double price){
		double cost = 0;
		if (e.getSourceVmId() == sVmId){
			if (sProvId != tProvId){
				//System.out.println("Data: "+e.getMBperHour()+ "Price: "+price);
				//传输过来是MB，转换为GB
				cost += e.getMBperHour() / 1024 * price;
			}
		}
		return cost;
	}
	
	/**
	 * Compute the cost of a single VM on the given datacenter.
	 * @param vm
	 * @param type
	 * @param datacenter
	 * @return
	 */
	public static double singleVmCost(Vm vm, FederationDatacenter datacenter)
	{
		double amount = 0d;
		amount = calculateCostCustomVm(datacenter, vm);
		FederationLog.timeLogDebug("(CostComputer) total vm cost: " + amount);
		return amount;
	}
	
	/**
	 * Cost per MB per hour (in the DataCenter characteristics it is 
	 * expressed in GB, as usual for providers, but VMs express it 
	 * in MB)
	 * @param fd
	 * @return
	 */
	public static double getCostPerMem(FederationDatacenter fd){
		double costPerMem = fd.getMSCharacteristics().getCostPerMem();
		return costPerMem / 1024;
	}

	public static double getCostPerCpu(FederationDatacenter fd){
		double costPerCpu = fd.getMSCharacteristics().getCostPerCpu();
		return costPerCpu;
	}
	
	/**
	 * Cost per MB per hour
	 * @param fd
	 * @return
	 */
	public static double getCostPerStorage(FederationDatacenter fd){
		double costPerSto = fd.getMSCharacteristics().getCostPerStorage();
		return costPerSto / 1024;
	}
	
	/**
	 * Calculating cost by considering Cpu, Ram, Storage.
	 * For bandwidth we assume free charge for internal networking and thus 
	 * it cannot be included here because we do not know the entire allocation. 
	 * Please see actualCost or expectedCost for methods that include bandwidth charge.
	 * @param fd
	 * @param vm
	 * @return
	 */
	private static double calculateCostCustomVm(FederationDatacenter fd, Vm vm)
	{
		//double costPerCPU = vm.getNumberOfPes(). // used for cost per cpu
		
		double costCPU = vm.getNumberOfPes() * getCostPerCpu(fd);
		double costRam = vm.getRam() * getCostPerMem(fd); 
		double costStorage = vm.getSize() * getCostPerStorage(fd);
		FederationLog.timeLogDebug("(CostComputer) custom_vm: " + costRam + " + " + costStorage + "+" + costCPU);
		double costVm = costRam + costStorage + costCPU;
		return costVm;
	}
}
