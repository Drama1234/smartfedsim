//package workflowmapping;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.cloudbus.cloudsim.Cloudlet;
//import org.cloudbus.cloudsim.network.datacenter.WorkflowApp;
//import org.omg.CORBA.PUBLIC_MEMBER;
//import org.workflowsim.Task;
//
//import application.Application;
//import application.ApplicationVertex;
//import application.WorkflowApplication;
//import federation.resources.FederationDatacenter;
//import workflowfederation.MonitoringHub;
//import workflownetworking.InternetEstimator;
//
//class HEFTSchedule{
//	private Application application;
//	private int maxSimuIns;
//	public List<Cloudlet> cloudletsOrder;
//	public double crowdDist; //for crowd sorting used
//	public int usedVM;
//	public double makespan, cost;
//	
//	public HEFTSchedule(Application application,int maxSimultaneousIns,List<Cloudlet> order ) {
//		this.application = application;
//		this.maxSimuIns = maxSimultaneousIns;
//		this.cloudletsOrder = order;
//		usedVM = 0;
//		makespan = -1;
//		cost = -1;
//	}
//	
//	public HEFTSchedule clone() {
//		HEFTSchedule res = new HEFTSchedule(application, maxSimuIns, cloudletsOrder);
//		res.usedVM = this.usedVM;
//		res.makespan = makespan;
//		res.cost = cost;
//		return res;
//	}
//	
//	public boolean appendToExistVM() {
//		
//	}
//}
//
//public class MOHEFT extends AbstractAllocator{
//
//	
//	public static int tradeOffSolNum = 10;
//	public static int maxSimultaneousIns = 10;
//	
//	List<FederationDatacenter> dcs = null;
//	
//	public MOHEFT(MonitoringHub monitoring, InternetEstimator netEstimator) {
//		super();
//		this.setMonitoring(monitoring);
//		this.setNetEstimator(netEstimator);
//	}
//
//	@Override
//	public MappingSolution[] findAllocation(Application application) {
//		if (monitoring != null){
//			dcs = monitoring.getView(); // maybe we can avoid to perform algorithm if the view has not changed
//		}
//		
//		List<HEFTSchedule> froniter = new ArrayList<HEFTSchedule>();
//		
//		List<Cloudlet> cloudlets = application.getAllCloudlets();
//			
//		//1.B_bank
//		Map<Cloudlet, Double> rank = this.bRank(application);
//		
//		// 2. Sort cloudlets with b-rank
//		List<Cloudlet> sortedCloudlets = new ArrayList<Cloudlet>();
//		for (Cloudlet t : application.getAllCloudlets())
//			sortedCloudlets.add(t);
//		
//		Collections.sort(sortedCloudlets, (Cloudlet one, Cloudlet other) -> rank.get(one).compareTo(rank.get(other)));
//		
//		for (int i = 0; i < tradeOffSolNum; i++) {
//			froniter.add(new HEFTSchedule(application, i, sortedCloudlets));
//		}
//		
//		for(Cloudlet adding : sortedCloudlets) {
//			int leftCNum = sortedCloudlets.size() - sortedCloudlets.indexOf(adding);
//			if (leftCNum % 10 == 0)
//				System.out.println("[MOHEFT] try to assign " + adding + " left# " + leftCNum);
//			
//			List<HEFTSchedule> nextPlans = new ArrayList<>();
//			for (HEFTSchedule f : froniter) {
//				for (int i = 0; i < f.usedVM; i++) {// reusing exist vm
//					HEFTSchedule next = f.clone();
//					boolean succeed = next.appendToExistVM(adding, i,
//							adding.getCloudletLength() / avalVmTypes.get(f.vmTypes[i]).getMips());
//				}
//			}
//			
//		}
//	
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	
//	
//	static Map<Cloudlet, Double> bRank(Application application){
//		List<Cloudlet> cloudlets = application.getAllCloudlets();
// 		
//		Map<Cloudlet, Integer> upwardRank = new HashMap<Cloudlet, Integer>();
//		
//		int dep = 0;
//		int seted = 0;
//		
//		for (Cloudlet c : cloudlets) {
//			ApplicationVertex vertex = application.getVertexForCloudlet(c);
//			if (application.incomingEdgesOf(vertex).equals(null)) {
//				upwardRank.put(c, 0);
//				seted += 1;
//			} else
//				upwardRank.put(c, -1);
//		}
//		
//		while(seted < application.getAllCloudlets().size()) {
//			dep += 1;
//			for (Cloudlet c : cloudlets) {
//				if (upwardRank.get(c) != -1)
//					continue;
//				boolean ready = true;
//				for (Cloudlet r : application.getAllCloudletLinked(c)) {
//					if (upwardRank.get(r) == -1 || upwardRank.get(r) == dep) {
//						ready = false;
//						break;
//					} // if
//				} // for r
//				if (ready) {
//					upwardRank.put(c, dep);
//					seted += 1;
//				}
//			}	
//		}
//		
//		Map<Cloudlet, Double> res = new HashMap<Cloudlet, Double>();
//		// for the same rank cloudlet, one with more succeed have higher rank
//		int maxDeap = Collections.max(upwardRank.values());
//		List<Cloudlet> match = new ArrayList<Cloudlet>();
//		for (int deap = 0; deap <= maxDeap; deap++) {
//			match.clear();
//			for(Entry<Cloudlet, Integer> entry:upwardRank.entrySet())
//				if (entry.getValue() == deap)
//					match.add(entry.getKey());
//			match.sort((Cloudlet a, Cloudlet b) -> application.getRequirementTask(b).size() - (application.getTargetTask(a).size()));
//			for (Cloudlet i : match)
//				res.put(i, (deap + (match.indexOf(i) + 0.0) / (match.size() + 1.0)));
//		}
//		return res;
//    }
//}
//	
//
