package federation.resources;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.supercsv.cellprocessor.ParseLong;

import federation.resources.FederationDatacenterProfile.DatacenterParams;


public class FederationDatacenterFactory {
	private static FederationDatacenter createFederationDatacenter(FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages) {
//		return createFederationDatacenter("datacenter"+"_Id"+ResourceCounter.nextDatacenterID(City.valueOf(profile.get(DatacenterParams.CITY))), profile, hosts, storages);
		return createFederationDatacenter("datacenter_"+ ResourceCounter.nextDatacenterID(), profile, hosts, storages);
	}
	
	private static FederationDatacenter createFederationDatacenter(String dcName, FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages) {
		// create the datacenter characteristics
		DatacenterCharacteristicsMS dc = new DatacenterCharacteristicsMS(
				profile.get(DatacenterParams.ARCHITECTURE),
				profile.get(DatacenterParams.OS),
				profile.get(DatacenterParams.VMM),
				hosts,
				Double.parseDouble(profile.get(DatacenterParams.TIME_ZONE)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_CPU)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_MEM)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_STORAGE)),
				Double.parseDouble(profile.get(DatacenterParams.COST_PER_BW))
				);
		dc.setDatacenterBw(Long.parseLong(profile.get(DatacenterParams.MAX_BW_FOR_VM)));
		
		// creating vm allocation policy class
		VmAllocationPolicy vmAllocationPolicy = null;
		try {
			Class clazz = Class.forName(profile.get(DatacenterParams.VM_ALLOCATION_POLICY));
			vmAllocationPolicy = (VmAllocationPolicy) clazz.getDeclaredConstructor(List.class).newInstance(hosts);
		}
		catch (Exception e) {
			// TODO: log the error
			e.printStackTrace();
		}
		
		// creating the federation datacenter
		FederationDatacenter fc = null;
		try {
			// fc = new FederationDatacenter("datacenter_"+ResourceCounter.nextDatacenterID(), dc, vmAllocationPolicy, storages,
//			System.out.println("数据中心名称："+dcName);
//			System.out.println("数据中心属性："+dc.toString());
//			System.out.println("数据中心外部存储："+storages.size());
//			System.out.println("数据中心间隔："+profile.get(DatacenterParams.SCHEDULING_INTERNAL));
//			System.out.println("虚拟机分配策略："+vmAllocationPolicy.toString());
			fc = new FederationDatacenter(dcName, dc, vmAllocationPolicy, storages,
					Double.parseDouble(profile.get(DatacenterParams.SCHEDULING_INTERNAL)));
		}
		catch (Exception e) {
			// TODO: log the error
			e.printStackTrace();
		}
		
		return fc;	
	}
	
	public static FederationDatacenter getDefault(List<Host> hosts, List<Storage> storages){
		return createFederationDatacenter(FederationDatacenterProfile.getDefault(), hosts, storages);
	}
	
	public static FederationDatacenter get(FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages){
		return createFederationDatacenter(profile, hosts, storages);
	}
	
	public static FederationDatacenter get(String name, FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages){
		return createFederationDatacenter(name, profile, hosts, storages);
	}
}
