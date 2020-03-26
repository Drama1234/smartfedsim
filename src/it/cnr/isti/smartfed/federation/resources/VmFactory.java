package it.cnr.isti.smartfed.federation.resources;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;

public class VmFactory {
	public enum VmType{
		SMALL,
		MEDIUM,
		LARGE,
		XLARGE,
		CUSTOM
	}
	
	public static Vm getVm(VmType type, int userId) {
		switch (type) {
			case SMALL:
			{
				return createSmall(userId);
			}
			case MEDIUM:
			{
				return createMedium(userId);
			}
			case LARGE:
			{
				return createLarge(userId);
			}
			case XLARGE:
			{
				return createXLarge(userId);
			}
			default:
			{
				return createSmall(userId);
			}
		}
	}
	
	private static Vm createSmall(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18, 
				1, 
				new Double(1.7 * 1024 ).intValue(), // RAM: 1.7 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(160 * 1024), // DISK: 160 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.SMALL);
		return vmt;
	}
	
	private static Vm createMedium(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18, // data not available, i assume as small instances
				1, 
				new Double(3.75 * 1024).intValue(), // 3.75 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(410 * 1024), // 410 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.MEDIUM);
		return vmt;
	}

	private static Vm createLarge(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				8022, 
				2, 
				new Double(7.5 * 1024).intValue(), // 7.5 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(850 * 1024), // 850 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.LARGE);
		return vmt;
	}
	
	private static Vm createXLarge(int userId)
	{		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				5202.15 * 4, 
				4, 
				new Double(15 * 1024).intValue(), // 15 GB
				new Long(1 * 1024 * 1024), // i assume at least 1MB p/s  
				new Long(1690 * 1024), // 1690 GB
				"Xen", 
				new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, VmType.XLARGE);
		return vmt;
	}
	
	public static Vm getCustomVm(int userId, double mips, int cores, int ramMB, long bandMB, long diskMB) {
		VmTyped vmt = new VmTyped(-1, 
				userId, 
				mips, 
				cores, 
				ramMB,
				bandMB,
				diskMB,
				"Xen", 
				new CloudletSchedulerTimeShared(), 
				VmType.CUSTOM);	
		return vmt;
	}
	
	public static Vm getDesiredVm(int userId, double mips, int cores, int ramMB, long bandMB, long diskMB){
		Vm vm = new Vm(-1, 
				userId, 
				mips, 
				cores, 
				ramMB,
				bandMB,
				diskMB,
				"Xen", 
				new CloudletSchedulerTimeShared());	
		VmTyped vmt = new VmTyped(vm, VmType.CUSTOM);
		return vmt;
	}
	
	public static Vm cloneVMnewId(Vm vm)
	{
		Vm result = new Vm(ResourceCounter.nextVmID(), 
				vm.getUserId(), 
				vm.getMips(), 
				vm.getNumberOfPes(), 
				vm.getRam(),
				vm.getBw(),
				vm.getSize(),
				vm.getVmm(), 
				vm.getCloudletScheduler());		
		return result;
	}
}
