package workflowmapping;

import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import application.Application;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowconstraints.PolicyContainer;
import workflowfederation.Federation;
import workflowfederation.FederationLog;
import workflowfederation.MonitoringHub;
import workflownetworking.InternetEstimator;
import workflowschedule.MSPolicyFactory;
import workflowschedule.Solution;
import workflowschedule.iface.Metascheduler;

public class GeneticWork extends AbstractAllocator{
	PolicyContainer constraint = null;
	List<FederationDatacenter> dcs = null;
	Solution[] solutions = null;
	
	public Solution[] getSolutions() {
		return solutions;
	}

	public GeneticWork()
	{
		super();
	}
	
	public GeneticWork(MonitoringHub monitoring, InternetEstimator netEstimator) {
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application) {
		startSimTime = CloudSim.clock();
		startRealTime = System.currentTimeMillis();
		
		if (monitoring != null){
			dcs = monitoring.getView(); // maybe we can avoid to perform algorithm if the view has not changed
		}
		
		solutions = Metascheduler.getMapping(application, constraint.getList(), dcs, this.netEstimator, randomSeed);
		//System.out.println(chooseSolution(solutions));
		finishSimTime = CloudSim.clock();
		finishRealTime = System.currentTimeMillis();
		
		MappingSolution[] sols = new MappingSolution[solutions.length];
		for (int i=0; i < sols.length; i++)
			sols[i] = convert(solutions[i], application, dcs);
		
		this.setSolution(sols[0]);
		return sols;
	}
	
	private MappingSolution convert(Solution s, Application application, List<FederationDatacenter> dcs) {
		if (s == null) return null;
		FederationLog.print(s);
		
		MappingSolution map = new MappingSolution(application);
		map.setAllocatorName(this.getClass().getSimpleName() + " " + "全局网络");
		
		HashMap<Integer,Integer> hm = s.getAllocationMap();
		List<Vm> v_list = application.getAllVms();
		if (v_list.size() != hm.keySet().size())
			System.out.println("************ Big error ********************");
		
		for (Integer vmId: hm.keySet()) {
			Vm vm = findForId(v_list, vmId);
			ApplicationVertex vertex = application.getVertexForVm(vm);
			Cloudlet cl = vertex.getAssociatedCloudlet(vm);
			
			FederationDatacenter dc = Federation.findDatacenter(dcs, hm.get(vmId));
			map.set(cl, dc);
		}
		map.setValid(s.getCompleteSatisfaction());
		return map;
	}
	
	private Vm findForId(List<Vm> v_list, Integer vmId) {
		boolean found = false;
		Vm vm = null;
		for (int i = 0; i < v_list.size() && !found; i++){
			if (v_list.get(i).getId() == vmId){
				System.out.println(v_list.get(i).getId());
				found = true;
				vm = v_list.get(i);
			}
		}
		return vm;
	}
	
	public List<FederationDatacenter> getDcs() {
		return dcs;
	}
	
	private void setDcs(List<FederationDatacenter> dcs) {
		this.dcs = dcs;
	}
	
	private void setConstraint(PolicyContainer constraint) {
		this.constraint = constraint;
	}
	
	public void setPolicyType() {
		if (this.getDcs() != null){
			this.setConstraint(MSPolicyFactory.createPoliciesDefaultNetBw(dcs,new double[]{1, 1, 1, 1,1,1,1}));
		}
	}
	
	@Override
	public void setMonitoring(MonitoringHub monitoring) 
	{
		this.monitoring = monitoring;
		this.setDcs(monitoring.getView());
		if (constraint == null){
			this.setConstraint(MSPolicyFactory.createPoliciesDefaultNetBw(dcs,new double[]{1, 1, 1, 1,1,1,1}));
		}
	}
}
