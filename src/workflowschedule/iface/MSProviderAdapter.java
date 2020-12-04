package workflowschedule.iface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.Host;

import federation.resources.DatacenterCharacteristicsMS;
import federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.resources.MSProvider;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderComputing;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderNetwork;
import it.cnr.isti.smartfed.metascheduler.resources.MSProviderStorage;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflowfederation.WorkflowCost;
import workflowschedule.Constant;

public class MSProviderAdapter {
	private static String hashToString(HashMap<String, Object> map, String indent){
		String ret = "";
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()){
			String next  = keys.next();
			Object value = map.get(next);
			next = next.toLowerCase();
			if (value instanceof Integer)
				ret += indent + next + ":  " + (Integer) value + "\n";
			else if(value instanceof Double)
				ret += indent + next + ":  " + (Double) value + "\n";
			else if (value instanceof Long)
				ret += indent + next + ":  " + (Long) value + "\n";
			else if(next instanceof String)
				ret += indent + next + ":  " + (String) value + "\n";
		}
		return ret;
	}
	
	public static String providerListToString(List<IMSProvider> list){
		String ret = "";
		String indent = "    ";
		for(int i=0; i<list.size(); i++){
			ret += hashToString(list.get(i).getCharacteristic(), indent);
			ret += hashToString(list.get(i).getComputing().getCharacteristic(), indent);
			ret += hashToString(list.get(i).getNetwork().getCharacteristic(), indent);
			ret += hashToString(list.get(i).getStorage().getCharacteristic(), indent);
			ret +="\n";
		}
		return ret;
	}
	
	private static HashMap<String, Object> aggregateHostInfo(List<Host> hostList){
		//		System.out.println("### AGGREGATE INFO: DATACENTER_UTILITY");
		HashMap<String, Object> map = new HashMap<String, Object>();
		long storage =0;
		int ram =0;
		long bw =0;
		double mips =0;
		int cores = 0;
		for(int i=0; i<hostList.size(); i++){
			storage += hostList.get(i).getStorage();
			ram += hostList.get(i).getRam();
			bw += hostList.get(i).getBw();
			mips += hostList.get(i).getTotalMips();
			cores += hostList.get(i).getNumberOfPes();
		}
		map.put(Constant.STORE, storage);
		map.put(Constant.MIPS, mips);
		map.put(Constant.RAM, ram);
		map.put(Constant.BW, bw);
		map.put(Constant.CPU_NUMBER, cores);
		return map;
	}
	
	public static IMSProvider datacenterToMSProvider(FederationDatacenter datacenter){
		MSProvider provider = new MSProvider();
		HashMap<String, Object> providerCharacteristic = new HashMap<String, Object>();
		HashMap<String, Object> networkCharacteristic = new HashMap<String, Object>();
		HashMap<String, Object> computingCharacteristic = new HashMap<String, Object>();
		HashMap<String, Object> storageCharacteristic = new HashMap<String, Object>();

		List<Host> hostList = datacenter.getHostList();
		DatacenterCharacteristicsMS dcCharacter = datacenter.getMSCharacteristics();
		//aggregating host list
		HashMap<String, Object> aggregateHost = new HashMap<String, Object>(); //aggregateHostInfo(hostList);
		aggregateHost.put(Constant.STORE, hostList.get(0).getStorage());
		aggregateHost.put(Constant.MIPS, hostList.get(0).getAvailableMips());
		aggregateHost.put(Constant.RAM, hostList.get(0).getRam());
		aggregateHost.put(Constant.BW, hostList.get(0).getBw());
		aggregateHost.put(Constant.CPU_NUMBER, hostList.get(0).getNumberOfPes());

		//computing
		computingCharacteristic.put(Constant.RAM, aggregateHost.get(Constant.RAM));
		computingCharacteristic.put(Constant.MIPS, aggregateHost.get(Constant.MIPS));
		computingCharacteristic.put(Constant.STORE, aggregateHost.get(Constant.STORE));
		computingCharacteristic.put(Constant.CPU_NUMBER, aggregateHost.get(Constant.CPU_NUMBER));
		
		computingCharacteristic.put(Constant.COST_MEM, dcCharacter.getCostPerMem());
		computingCharacteristic.put(Constant.COST_CPU, dcCharacter.getCostPerCpu());
	//	computingCharacteristic.put(Constant.COST_BW, dcCharacter.getCostPerBw());
		computingCharacteristic.put(Constant.COST_STORAGE, dcCharacter.getCostPerStorage());
		

		//network
		networkCharacteristic.put(Constant.BW, dcCharacter.getDatacenterBw());
		networkCharacteristic.put(Constant.COST_BW, dcCharacter.getCostPerBw());
//		networkCharacteristic.put(Constant.INTER_BW, dcCharacter.getHighestAllocatedBwAmongHosts());
//		networkCharacteristic.put(Constant.BW, dcCharacter.getHighestAllocatedBwAmongHosts());
//		networkCharacteristic.put(Constant.COST_BW,dcCharacter.getCostPerBw());
		
		
		//networkCharacteristic.put(Constant.COST_BW, dcCharacterisitc.getCostPerBw());
		//networkCharacteristic.put(Constant.COST_BW, CostComputer.getCostPerBw(datacenter));

		//storage
		storageCharacteristic.put(Constant.STORE, aggregateHost.get(Constant.STORE));
		storageCharacteristic.put(Constant.COST_STORAGE, WorkflowCost.getCostPerStorage(datacenter));

		//provider
		providerCharacteristic.put(Constant.providerID, dcCharacter.getId());
		providerCharacteristic.put(Constant.COST_CPU, dcCharacter.getCostPerCpu());
		providerCharacteristic.put(Constant.COST_MEM, WorkflowCost.getCostPerMem(datacenter));
		//providerCharacteristic.put(Constant.COST_MEM, CostComputer.getCostPerMem(datacenter));
//		providerCharacteristic.put(Constant.CITY, dcCharacter.getCity());
		providerCharacteristic.put(Constant.VM_INSTANCES, hostList.size());
//		providerCharacteristic.put(Constant.COST_VM, dcCharacter.getCostVmTypes());

		provider.setID(dcCharacter.getId());
		provider.setCharacteristic(providerCharacteristic);
		provider.setComputing(new MSProviderComputing());
		provider.setNetwork(new MSProviderNetwork());
		provider.setStorage(new MSProviderStorage());
		
		provider.getComputing().setCharacteristic(computingCharacteristic);
		provider.getStorage().setCharacteristic(storageCharacteristic);
		provider.getNetwork().setCharacteristic(networkCharacteristic);

		return provider;
	}
	
	public static IMSProvider findProviderById(List<IMSProvider> providerList, Integer providerID) {
		IMSProvider p = null;
		boolean found = false;
		for (int i =0; i<providerList.size() && !found; i++){
			if (providerList.get(i).getID() == providerID){
				found = true;
				p = providerList.get(i);
			}
		}
		return p;
	}
}
