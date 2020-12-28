package workflowmapping;

import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import Constraints.PolicyContainer;
import application.Application;
import application.ApplicationVertex;
import federation.resources.FederationDatacenter;
import workflowfederation.Federation;
import workflowfederation.MonitoringHub;
import workflownetworking.InternetEstimator;
import workflowschedule.PolicyFactory;
import workflowschedule.Solution;
import workflowschedule.iface.Metascheduler;


public class GeneticAllocator extends AbstractAllocator{
	
	PolicyContainer constraint = null;
	List<FederationDatacenter> dcs = null;
	Solution[] solutions = null;
	
	public Solution[] getSolutions() {
		return solutions;
	}

	public GeneticAllocator()
	{
		super();
	}
	
	public GeneticAllocator(MonitoringHub monitoring, InternetEstimator netEstimator) {
		super();
		this.setMonitoring(monitoring);
		this.setNetEstimator(netEstimator);
	}

	@Override
	public MappingSolution[] findAllocation(Application application) {
		//仿真时间开始
		startSimTime = CloudSim.clock();
		startRealTime = System.currentTimeMillis();
		//得到云服务供应商信息
		if (monitoring != null){
			dcs = monitoring.getView(); // maybe we can avoid to perform algorithm if the view has not changed
		}
		
		solutions = Metascheduler.getMapping(application, constraint.getList(), dcs, this.netEstimator, randomSeed);
		for (int i = 0; i < solutions.length; i++) {
			System.out.println("解的数量为："+solutions.length);
			System.out.println(solutions[i].toString());
		}
		if(solutions.length==0) {
			System.out.println("解的数量为0，过程不正确！");
		}
		//仿真结束时间
		finishSimTime = CloudSim.clock();
		finishRealTime = System.currentTimeMillis();
		
		MappingSolution[] sols = new MappingSolution[solutions.length];
		for (int i=0; i < sols.length; i++)
			sols[i] = convert(solutions[i], application, dcs);
		//返回MappingSolution解的集合
		return sols;	
	}
	
	/**
	 * Convert the genetic solution object "Solution" in the 
	 * federation's MappingPlan 
	 * @param s
	 * @param application
	 * @param dcs
	 * @return
	 */
	private MappingSolution convert(Solution s, Application application, List<FederationDatacenter> dcs) {
		if (s == null) return null;
		
//		FederationLog.print(s);
		MappingSolution map = new MappingSolution(application);
		map.setAllocatorName(this.getClass().getSimpleName() + " " + "全局网络");
		//the map is HashMap<vmId,dc2Id>
		HashMap<Integer,Integer> hm = s.getAllocationMap();
		List<Vm> v_list = application.getAllVms();
		if (v_list.size() != hm.keySet().size())
			System.out.println("************ 严重错误！ *****************");
		
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
//				System.out.println(v_list.get(i).getId());
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

	public PolicyContainer getConstraint() {
		return constraint;
	}

	private void setConstraint(PolicyContainer constraint) {
		this.constraint = constraint;
	}
	
//	/**
//	 * To be called before setMonitoring for having effect.
//	 * @param constraint
//	 */
//	public void setPolicyType() {
//		if (this.getDcs() != null){
//			this.setConstraint(PolicyFactory.createPoliciesDefaultNetBw(dcs,new double[]{1, 1, 1, 1, 1, 1, 1, 1},this.netEstimator));
//		}
//	}
	
	
	
	@Override
	public void setMonitoring(MonitoringHub monitoring) 
	{
		this.monitoring = monitoring;
		this.setDcs(monitoring.getView());
		if (constraint == null){
//	    	this.setConstraint(PolicyFactory.createPoliciesDefaultNetBw(dcs,new double[]{0.1,0.1,0.1,0.2,0.1,0.1,0.3},this.netEstimator));
			this.setConstraint(PolicyFactory.createPolicies(dcs, new double[]{0.5,0.5}, this.netEstimator));
		}
	}
}
