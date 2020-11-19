package federation.resources;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import workflowfederation.FederationLog;


public class SimpleHost extends Host{

	public SimpleHost(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage,
			List<? extends Pe> peList, VmScheduler vmScheduler) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
	}
	
	/**
	 * Allocates PEs and memory to a new VM in the Host.
	 * 
	 * @param vm Vm being started
	 * @return $true if the VM could be started in the host; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	public boolean vmCreate(Vm vm) {
		if (getStorage() < vm.getSize()) {
			
			FederationLog.debugLog("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
					+ " failed by storage ( " + vm.getSize() + "vs " + getStorage() + ")");
			FederationDatacenter d = (FederationDatacenter) this.getDatacenter();
			FederationLog.debugLog("Datacenter sholud have " + d.getMSCharacteristics().getHighestStorage() + " storage");
			
			return false;
		}

		if (!getRamProvisioner().allocateRamForVm(vm, vm.getCurrentRequestedRam())) {
			FederationLog.debugLog("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
					+ " failed by RAM (" + vm.getRam() + " vs " + this.getRam() + ")");
			return false;
		}

		if (!getBwProvisioner().allocateBwForVm(vm, vm.getCurrentRequestedBw())) {
			FederationLog.debugLog("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
					+ " failed by BW");
			getRamProvisioner().deallocateRamForVm(vm);
			return false;
		}

		if (!getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
			FederationLog.debugLog("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
					+ " failed by MIPS (" + vm.getMips() + " vs " + this.getTotalMips() + ")");
			System.out.println("虚拟机请求的MIPS数量："+vm.getCurrentRequestedMips());
			System.out.println("分配虚拟机的mips:"+vm.getMips());
			System.out.println("分配虚拟机的内核数量："+vm.getNumberOfPes());
			System.out.println("主机MIPS："+this.getMaxAvailableMips());
			System.out.println("主机总体mips:"+this.getTotalMips());
			System.out.println("主机内核数量："+this.getNumberOfPes());
			getRamProvisioner().deallocateRamForVm(vm);
			getBwProvisioner().deallocateBwForVm(vm);
			return false;
		}

		setStorage(getStorage() - vm.getSize());
		getVmList().add(vm);
		vm.setHost(this);
		return true;
	}
}
