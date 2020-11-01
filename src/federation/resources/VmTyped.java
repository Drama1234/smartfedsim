package federation.resources;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

import federation.resources.VmFactory.vmType;

public class VmTyped extends Vm{
	vmType type = vmType.CUSTOM;
	
	
	public VmTyped(int id, int userId, double mips, int numberOfPes, int ram,
			long bw, long size, String vmm, CloudletScheduler cloudletScheduler, vmType vm_type) {
		
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
		type = vm_type;
	}
	
	public VmTyped(Vm vm, vmType vm_type){
		super(	vm.getId(),
				vm.getUserId(), 
				vm.getMips(), 
				vm.getNumberOfPes(), 
				vm.getRam(),
				vm.getBw(),
				vm.getSize(),
				vm.getVmm(), 
				vm.getCloudletScheduler());
		setType(vm_type);
	}
	
	public vmType getType() {
		return type;
	}

	public void setType(vmType type) {
		this.type = type;
	}
	
	public String toString(){
		String s = "";
		s+= "RAM:" + this.getRam() + "MIPS:" + this.getMips() + "PES:" + this.getNumberOfPes() + "STORAGE:" + this.getSize();
		s+= "type:" + this.getType();
		return s;
	}
}
