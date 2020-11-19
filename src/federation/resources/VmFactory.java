package federation.resources;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;

public class VmFactory {
	public enum vmType{
		SMALL,
		MEDIUM,
		LARGE,
		XLARGE,
		X2LARGE,
		CUSTOM
	}
		
	public static Vm getVm(vmType type, int userId) {
		switch (type) {
		case SMALL:
			return createSmall(userId);
		case MEDIUM:
			return createMedium(userId);
		case LARGE:
			return createLarge(userId);
		case XLARGE:
			return createXLarge(userId);
		case X2LARGE:
			return createX2Large(userId);
		default:
			return createSmall(userId);
		}
	 }
	
	private static Vm createSmall(int userId) {
		
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18 * 2, //mips
				2, 
				new Double(4 * 1024 ).intValue(),//4GB ram
				new Long(100 * 1024 * 1024), // i assume at least 100MB p/s  
				new Long(250 * 1024), //250GB硬盘
				"xen", new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, vmType.SMALL);
		return vm;
	}
	
	private static Vm createMedium(int userId) {
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				5202.15 * 4, //mips
				4, 
				new Double(8 * 1024 ).intValue(),//8GB ram
				new Long(100 * 1024 * 1024), // i assume at least 100MB p/s  
				new Long(500 * 1024), //500GB硬盘
				"xen", new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, vmType.MEDIUM);
		return vm;
	}
	
	private static Vm createLarge(int userId) {
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				5202.15 * 6, //mips
				6, 
				new Double(16 * 1024 ).intValue(),//16GB ram
				new Long(100 * 1024 * 1024), // i assume at least 100MB p/s  
				new Long(1024 * 1024), //1TB硬盘
				"xen", new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, vmType.LARGE);
		return vm;
	}
	
	private static Vm createXLarge(int userId) {
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18 * 16, //mips
				16, 
				new Double(32 * 1024 ).intValue(),//32GB ram
				new Long(100 * 1024 * 1024), // i assume at least 100MB p/s  
				new Long( 5 * 1024 * 1024), //5TB硬盘
				"xen", new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, vmType.XLARGE);
		return vm;
	}
	
	private static Vm createX2Large(int userId) {
		Vm vm = new Vm(ResourceCounter.nextVmID(), 
				userId, 
				6502.18 * 32, //mips
				32, 
				new Double(64 * 1024 ).intValue(),//64GB ram
				new Long(100 * 1024 * 1024), // i assume at least 100MB p/s  
				new Long( 10 * 1024 * 1024), //10TB硬盘
				"xen", new CloudletSchedulerTimeShared());
		VmTyped vmt = new VmTyped(vm, vmType.X2LARGE);
		return vm;
	}
	
	
	public static Vm getCustomVm(int userId, double mips, int cores, int ramMB, long bwMB, long diskMB)
	{		
		VmTyped vmt = new VmTyped(-1, 
				userId, 
				mips, 
				cores, 
				ramMB,
				bwMB,
				diskMB,
				"Xen", 
				new CloudletSchedulerTimeShared(), 
				vmType.CUSTOM);
		return vmt;
	}
	
	public static Vm getDesiredVm(int userId, double mips, int cores, int ramMB, long bwMB, long diskMB){
		Vm vm = new Vm(-1, 
				userId, 
				mips, 
				cores, 
				ramMB,
				bwMB,
				diskMB,
				"Xen", 
				new CloudletSchedulerTimeShared());	
		VmTyped vmt = new VmTyped(vm, vmType.CUSTOM);
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