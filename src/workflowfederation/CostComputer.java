package workflowfederation;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import application.Application;
import federation.resources.FederationDatacenter;
import federation.resources.VmFactory.vmType;
import it.cnr.isti.smartfed.federation.FederationLog;
import workflowmapping.MappingSolution;

public class CostComputer {
	public static double expectedCost(MappingSolution solution) 
	{
		Application application = solution.getApplication();
		double amount = 0d;
		
		for (Cloudlet c: application.getAllCloudlets())
		{
			FederationDatacenter datacenter = solution.getMapping().get(c);
			Vm vm = application.getVertexForCloudlet(c).getAssociatedVm(c);
			vmType type = application.getVertexForVm(vm).getVmType();
			
			if (datacenter != null)
				amount += singleVmCost(vm, type, datacenter);
		}
		return amount;
	}
	
	/**
	 * Compute the cost of a single VM on the given datacenter.
	 * @param vm
	 * @param type
	 * @param datacenter
	 * @return
	 */
	public static double singleVmCost(Vm vm, vmType type, FederationDatacenter datacenter)
	{
		double costs[] = datacenter.getMSCharacteristics().getCostVmTypes();
		double amount = 0d;
		
		switch (type)
		{
		case SMALL:
			amount = costs[0];
			break;
		case MEDIUM:
			amount = costs[1];
			break;
		case LARGE:
			amount = costs[2];
			break;
		case XLARGE:
			amount = costs[3];
			break;
		case X2LARGE:
			amount = costs[4];
			break;
		case CUSTOM:
					
		}
		
		// the provider does not have a price for this type (case of providers with mixed cost models)
		if (Double.isNaN(amount)){
			amount = calculateCostCustomVm(datacenter, vm);
		}
		
		FederationLog.timeLogDebug("(CostComputer) total vm cost: " + amount);
		return amount;
	}
	
	public static double getCostPerMem(FederationDatacenter fd){
		double costPerMem = fd.getMSCharacteristics().getCostPerMem();
		return costPerMem / 1024;
	}
	
	public static double getCostPerStorage(FederationDatacenter fd){
		double costPerSto = fd.getMSCharacteristics().getCostPerStorage();
		return costPerSto / 1024;
	}
	
	public static double getCostPerBw(FederationDatacenter fd){
		double costPerBw = fd.getMSCharacteristics().getCostPerBw();
		return costPerBw / 1024d;
	}
	
	private static double calculateCostCustomVm(FederationDatacenter fd, Vm vm)
	{
		double costPerSec = fd.getMSCharacteristics().getCostPerSecond(); // used for cost per cpu
		
		double costCPU = vm.getNumberOfPes() * costPerSec;
		double costRam = vm.getRam() * getCostPerMem(fd); 
		double costStorage = vm.getSize() * getCostPerStorage(fd);
		FederationLog.timeLogDebug("(CostComputer) custom_vm: " + costRam + " + " + costStorage + "+" + costCPU);
		double costVm = costRam + costStorage + costCPU;
		return costVm;
	}
}
