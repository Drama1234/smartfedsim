package federation.resources;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import federation.resources.FederationDatacenterProfile.DatacenterParams;


public class FederationDatacenterFactory {
	private static FederationDatacenter createFederationDatacenter(FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages) {
		return createFederationDatacenter("datacenter_"+profile.get(DatacenterParams.CITY)+"_Id"+ResourceCounter.nextDatacenterID(City.valueOf(profile.get(DatacenterParams.CITY))), profile, hosts, storages);
	}
	
	private static FederationDatacenter createFederationDatacenter(String dcName, FederationDatacenterProfile profile, List<Host> hosts, List<Storage> storages) {
		// create the datacenter characteristics
		DatacenterCharacteristicsMS dc = new DatacenterCharacteristicsMS(
				City.valueOf(profile.get(DatacenterParams.CITY)),
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
		//dc.setHighestBw(Long.parseLong(profile.get(DatacenterParams.MAX_BW_FOR_VM)));
		
		// creating vm allocation policy class
		VmAllocationPolicy vmAllocationPolicy = null;
		try {
			Class clazz = Class.forName(profile.get(DatacenterParams.VM_ALLOCATION_POLICY));
			vmAllocationPolicy = (VmAllocationPolicy) clazz.getDeclaredConstructor(List.class).newInstance(
					hosts);
		}
		catch (Exception e) {
			// TODO: log the error
			e.printStackTrace();
		}
		
		// creating the federation datacenter
		FederationDatacenter fc = null;
		try {
			// fc = new FederationDatacenter("datacenter_"+ResourceCounter.nextDatacenterID(), dc, vmAllocationPolicy, storages, 
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
