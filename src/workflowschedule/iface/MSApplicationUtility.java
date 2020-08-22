package workflowschedule.iface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Vm;

import application.Application;
import application.ApplicationEdge;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationComputing;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNetwork;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationStorage;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import workflowschedule.Constant;

public class MSApplicationUtility {
	public static String hashToString(HashMap<String, Object> map, String indent) {
		String ret = "";
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()){
			String next  = keys.next();
			Object value = map.get(next);
			next = next.toLowerCase();
			if( value instanceof Integer )
				ret += indent + next + ":  " + (Integer) value + "\n";
			else if( value instanceof Double)
				ret += indent + next + ":  " + (Double) value + "\n";
			else if (value instanceof Long)
				ret += indent + next + ":  " + (Long) value + "\n";
			else if(next instanceof String)
				ret += indent + next + ":  " + (String) value + "\n";
		}
		return ret;
	}
	
	public static String toStringMSApplication(IMSApplication app){
		String indent = "    ";
		String ret = "";
		
		List<MSApplicationNode> nodes = app.getNodes();
		for(int i=0; i<nodes.size(); i++){
			ret += " Node." + nodes.get(i).getID() + "\n";
			ret += hashToString(nodes.get(i).getComputing().getCharacteristic(), indent);
			ret += hashToString(nodes.get(i).getNetwork().getCharacteristic(), indent);
			ret += hashToString(nodes.get(i).getStorage().getCharacteristic(), indent);
		}
		return ret;
	}
	
	private static MSApplicationNode vmToMSApplicationNode(Vm vm, Set<ApplicationEdge> edges, String city, double budget,FederationDatacenter datecenter) {
		MSApplicationNode appNode = new MSApplicationNode();
		
		HashMap<String, Object> compParam =  new HashMap<String, Object>();
		HashMap<String, Object> netParam = new HashMap<String, Object>();
		HashMap<String, Object> storeParam = new HashMap<String, Object>();
		
		MSApplicationComputing computing = new MSApplicationComputing();
		MSApplicationNetwork network = new MSApplicationNetwork();
		MSApplicationStorage storage = new MSApplicationStorage();
		
		compParam.put(Constant.MIPS, vm.getMips());
		compParam.put(Constant.RAM, vm.getRam());
		compParam.put(Constant.CPU_NUMBER, vm.getNumberOfPes());
		compParam.put(Constant.BW, vm.getBw());
		compParam.put(Constant.STORE, vm.getSize());
		computing.setCharacteristic(compParam);
		
		storeParam.put(Constant.STORE, vm.getSize());
		storage.setCharacteristic(storeParam);
		
		netParam.put(Constant.INTER_BW, aggregateOutBwFromVm(edges, vm));
		network.setCharacteristic(netParam);
		
		appNode.setComputing(computing);
		appNode.setNetwork(network);
		appNode.setStorage(storage);
		
		HashMap<String, Object> nodeCharacteristic = new HashMap<String, Object>();
		nodeCharacteristic.put(Constant.BUDGET, budget);
		nodeCharacteristic.put(Constant.CITY,city);
		appNode.setCharacteristic(nodeCharacteristic);
		appNode.setID(vm.getId());
		return appNode;
	}
	private static long aggregateOutBwFromVm(Set<ApplicationEdge> edges, Vm vm){
		long bw = 0;
		for (ApplicationEdge e: edges){
			if (e.getSourceVmId() == vm.getId()){
				bw += (e.getBandwidth() * 1024); // from KB to Bytes
			}
		}
		return bw;  
	}
	
	public static IMSApplication getMSApplication(Application app){
		MSApplication newApp = new MSApplication();
		ApplicationVertex vertex ;
		List<Vm> vmList = app.getAllVms();
		
		List<MSApplicationNode> nodeList = new ArrayList<MSApplicationNode>();
		for(int i=0; i<vmList.size(); i++){
			newApp.setFirstVmIndex(vmList.get(i).getId());
			vertex = app.getVertexForVm(vmList.get(i));
			MSApplicationNode node = vmToMSApplicationNode(vmList.get(i), app.edgesOf(vertex), vertex.getCity(), vertex.getBudget(),vertex.getFederationDatacenter());
			
			if (vertex.getDesiredVm() != null){
				Vm desVm = vertex.getDesiredVm();
				HashMap<String, Object> desiredCharacteristic = new HashMap<String, Object>();
				desiredCharacteristic.put(Constant.STORE, desVm.getSize());
				desiredCharacteristic.put(Constant.RAM, desVm.getRam());
				desiredCharacteristic.put(Constant.CPU_NUMBER, desVm.getNumberOfPes());
				desiredCharacteristic.put(Constant.BW, desVm.getBw());
				node.setDesiredCharacteristics(desiredCharacteristic);
			}
			nodeList.add(node);	
		}

		newApp.setNodes(nodeList);
		newApp.setEdges(app.getEdges());
		return newApp;
	}
}