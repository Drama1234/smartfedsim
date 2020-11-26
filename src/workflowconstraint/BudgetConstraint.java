package workflowconstraint;

import java.util.List;
import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

import application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflowfederation.CostComputer;
import workflowfederation.WorkflowComputer;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;
import workflowschedule.CIntegerGene;
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class BudgetConstraint extends MSPolicy{
//	private double highRamCost;
//	private double highStorageCost;
//	private double highCpuCost;
//	private double highNetworkCost;
	
	/**
	 * Allows for setting highest costs for multiple resources
	 * @param weight
	 * @param highestValues
	 */
	public BudgetConstraint(double weight, ConstraintScope c) {
		super(weight, MSPolicy.DESCENDENT_TYPE, c);
		this.constraintName = "Budget";
//		highCpuCost = highestValues[0];
//		highRamCost = highestValues[1];
//		highStorageCost = highestValues[2];
//		highNetworkCost = highestValues[3];
	}
	
	private static double storageCost(MSApplicationNode node, IMSProvider prov){
		double costPerStorage = (Double) prov.getStorage().getCharacteristic().get(Constant.COST_STORAGE);
		long storage = (long) node.getStorage().getCharacteristic().get(Constant.STORE);
		Double cost = storage * costPerStorage;
		return cost;
	}
	
	private static double ramCost(MSApplicationNode node, IMSProvider prov){
		Double costPerMem = (Double) prov.getCharacteristic().get(Constant.COST_MEM);
		Integer ram = (Integer) node.getComputing().getCharacteristic().get(Constant.RAM);
		Double cost = ram * costPerMem;
		return cost;
	}
	
	private static double cpuCost(MSApplicationNode node, IMSProvider prov){
		Double costPerCPU = (Double) prov.getCharacteristic().get(Constant.COST_CPU);
		Integer cpu_number = (Integer) node.getComputing().getCharacteristic().get(Constant.CPU_NUMBER);
		Double cost = cpu_number * costPerCPU;
		return cost;
	}

	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		// TODO Auto-generated method stub
		throw new Error("Local evaluation not supported");
	}
	
	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet) {
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		Double budget = (Double) node.getCharacteristic().get(Constant.BUDGET);
		Double cost = calculateCost_Network(gene_index, chromos, app, prov,internet);
		Double maxCost = budget;
		
		((CIntegerGene) chromos.getGene(gene_index)).setAllocationCost(cost);
		//定义总体成本约束即可，没有必要单独计算每一项
//		Double c_maxCost = highCpuCost*CpuNumberConstraint.getHighCpuValue();
//		Double s_maxCost = highRamCost*RamConstraint.getHighRamValue();
//		Double r_maxCost = highStorageCost*StorageConstraint.getHighStorageValue();
		
		double distance = calculateDistance_ErrHandling(cost, budget, maxCost);
		
		return distance * getWeight();
	}
	
	public static Double calculateCost_Network(int index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet){
		MSApplicationNode node = app.getNodes().get(index);
		Double cpu_cost = cpuCost(node, prov);
		Double r_cost = ramCost(node, prov);
		Double s_cost = storageCost(node, prov);
		Double net_cost = netCost(index, chromos, app, prov,internet);
		// System.out.println(r_cost + " + " + s_cost);
		return r_cost + s_cost + cpu_cost + net_cost;
	}
	
	private static double netCost(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet){
		//Double costPerNet = (Double) prov.getNetwork().getCharacteristic().get(Constant.COST_BW);
		Gene[] genes = chromos.getGenes();
		int current_prov = (int) genes[gene_index].getAllele();
		MSApplicationNode curr_node = app.getNodes().get(gene_index); // this is safe
		int geneVmId = curr_node.getID();
		MSApplication am = (MSApplication) app;
		double cost = 0;
		Set<ApplicationEdge> set = am.getEdges();
		
		for (ApplicationEdge e: set){
			if (e.getSourceVmId() == geneVmId){
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), genes, app);
				
				int tProvId = (int) genes[target_index].getAllele();
				InternetLink link = null;
				try {
					link = internet.getInternetLink(prov.getID(), tProvId);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(link != null) {
					double interBwCost = link.getBwcost();
					cost += CostComputer.computeLinkCost(e, geneVmId, current_prov, tProvId, interBwCost);
				}
				
//				System.out.println("目标云服务供应商ID:"+tProvId);
//				InternetLink link = null;
//				try {link = internet.getInternetLink(prov.getID(), tProvId);} 
//				catch (Exception e1) {e1.printStackTrace();}
//				if(link==null) {
//					System.out.println("原云服务供应商"+prov.getID()+"---------->"+"目标云服务供应商："+tProvId);
//					System.out.println("该边没有通");
//				}
//				double interBwCost = link.getBwcost();
//				
//				cost += CostComputer.computeLinkCost(e, geneVmId, current_prov, tProvId, interBwCost);
			}
		}
		return cost;
	}	
}
