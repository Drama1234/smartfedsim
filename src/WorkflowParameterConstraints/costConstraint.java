package WorkflowParameterConstraints;

import java.util.List;
import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

import application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflowfederation.WorkflowCost;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;
import workflowschedule.CIntegerGene;
import workflowschedule.Constant;
import workflowschedule.Policy;

public class costConstraint extends Policy{
	public costConstraint(double weight) {
		super(weight, Policy.DESCENDENT_TYPE);
		this.constraintName = "costConstraint";
	}
	
	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		
		Double budget = (Double) node.getCharacteristic().get(Constant.BUDGET);
		Double cost = calculateCost_Network(gene_index, chromos, app, prov,internet);
		cost = Double.valueOf(String.format("%.2f", cost));
		Double maxCost = budget;
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		double distance = calculateDistance_ErrHandling(cost, budget, maxCost);
		((CIntegerGene) chromos.getGene(gene_index)).setAllocationCost(cost);
		return distance * getWeight();
	}
	
	public static Double calculateCost_Network(int index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet){
		MSApplicationNode node = app.getNodes().get(index);
		Double cpu_cost = cpuCost(node, prov);
//		FederationLog.print("cpu_cost:"+cpu_cost);
		Double ram_cost = ramCost(node, prov);
//		FederationLog.print("ram_cost:"+ram_cost);
		Double storage_cost = storageCost(node, prov);
//		FederationLog.print("storage_cost:"+storage_cost);
		Double net_cost = netCost(index, chromos, app, prov,internet);
		net_cost = Double.valueOf(String.format("%.2f",net_cost));
//		FederationLog.print("net_cost:"+net_cost);
		// System.out.println(r_cost + " + " + s_cost);
		return ram_cost + storage_cost + cpu_cost + net_cost;
	}
	
	private static double storageCost(MSApplicationNode node, IMSProvider prov) {
		double costPerStorage = (Double) prov.getStorage().getCharacteristic().get(Constant.COST_STORAGE)/1024;
		long storage = (Long) node.getStorage().getCharacteristic().get(Constant.STORE);
		double cost = storage * costPerStorage;
		return cost;
	}
	
	private static double ramCost(MSApplicationNode node, IMSProvider prov){
		double costPerMem = (Double) prov.getCharacteristic().get(Constant.COST_MEM)/1024;
		int ram = (Integer) node.getComputing().getCharacteristic().get(Constant.RAM);
		double cost = ram * costPerMem;
		return cost;
	}
	
	private static double cpuCost(MSApplicationNode node, IMSProvider prov){
		double costPerCPU = (Double) prov.getCharacteristic().get(Constant.COST_CPU);
		int cpu_number = (Integer) node.getComputing().getCharacteristic().get(Constant.CPU_NUMBER);
		double cost = cpu_number * costPerCPU;
		return cost;
	}
	
	private static double netCost(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet){
		Gene[] genes = chromos.getGenes();
		int current_prov = (int) genes[gene_index].getAllele();
		// get the vm id of the gene
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		int geneVmId = node.getID();
		// get the set of the edges
		MSApplication am = (MSApplication) app;
		double cost = 0;
		Set<ApplicationEdge> set = am.getEdges();
		
		for (ApplicationEdge e: set){
			if (e.getSourceVmId() == geneVmId){
				int target_index = Policy.getGeneIndexFromNodeId(e.getTargetVmId(), chromos.getGenes(), app);			
				int target_Provider = (int) chromos.getGenes()[target_index].getAllele();
				InternetLink link = null;
				try {
					link = internet.getInternetLink(prov.getID(), target_Provider);
					if(link!=null) {
						double interBwCost = link.getBwcost();
						cost += WorkflowCost.computeLinkCost(e, geneVmId, current_prov, target_Provider, interBwCost);
					}else {
						cost += 0;
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return cost;
	}
}
