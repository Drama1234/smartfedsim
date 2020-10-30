//package federation.resources;
//
//import org.cloudbus.cloudsim.CloudletScheduler;
//import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
//import org.cloudbus.cloudsim.Vm;
//
//
//
//public class FederationVm extends Vm{
//
//
//	public FederationVm(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
//			CloudletScheduler cloudletScheduler) {
//		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
//	}
//	
//	public FederationVm(Vm vm) {
//		super(vm.getId(),
//			  vm.getUserId(), 
//			  vm.getMips(), 
//			  vm.getNumberOfPes(), 
//			  vm.getRam(),
//			  vm.getBw(),
//			  vm.getSize(),
//			  vm.getVmm(), 
//			  vm.getCloudletScheduler());
//	}
//
//	public static Vm getCustomVm(int userId, double mips, int cores, int ramMB, long bwMB, long diskMB)
//	{
//		Vm vm = new Vm(-1, 
//				userId, 
//				mips, 
//				cores, 
//				ramMB, 
//				bwMB, 
//				diskMB, 
//				"Xen", 
//				new CloudletSchedulerTimeShared());
//		return vm;
//	}
//	
//	public static Vm cloneVMnewId(Vm vm)
//	{
//		Vm result = new Vm(ResourceCounter.nextVmID(), 
//				vm.getUserId(), 
//				vm.getMips(), 
//				vm.getNumberOfPes(), 
//				vm.getRam(),
//				vm.getBw(),
//				vm.getSize(),
//				vm.getVmm(), 
//				vm.getCloudletScheduler());		
//		return result;
//	}
//	
//
//}
