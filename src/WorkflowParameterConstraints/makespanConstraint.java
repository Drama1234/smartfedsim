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
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;
import workflowschedule.CIntegerGene;
import workflowschedule.Constant;
import workflowschedule.Policy;

public class makespanConstraint extends Policy{
	public makespanConstraint(double weight) {
		super(weight, Policy.DESCENDENT_TYPE);
		this.constraintName = "makespanConstraint";
	}
	
	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		
		Double time = (Double) node.getCharacteristic().get(Constant.TASK_TIME);
		Double makespan = calculateMakespan_Network(gene_index, chromos, app, prov,internet);
		time = Double.valueOf(String.format("%.2f", time));
		Double maxTime = time;
		if (DEBUG)
			System.out.println("Eval before applying weights for " + "NodeID " + node.getID() + " - ProvID " + prov.getID());
		double distance = calculateDistance_ErrHandling(makespan, time, maxTime);
		((CIntegerGene) chromos.getGene(gene_index)).setAllocationTime(makespan);
		return distance * getWeight();
	}
	
	public static Double calculateMakespan_Network(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		MSApplicationNode node = app.getNodes().get(gene_index);
		double tasktime = tasktime(node,prov);
		double networktime = networktime(gene_index, chromos, app, prov,internet);
		return tasktime + networktime;
	}
	
	private static Double tasktime(MSApplicationNode node, IMSProvider prov){
		Double providerMips = (Double)prov.getComputing().getCharacteristic().get(Constant.MIPS);
		Long cloudletlength = (Long)node.getCharacteristic().get(Constant.vertextLength);
		Double tasktime = (Double) (cloudletlength / providerMips);
		return tasktime;
	}
	
	private static double networktime(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet) {
		Gene[] genes = chromos.getGenes();
		MSApplicationNode node = app.getNodes().get(gene_index);
		int geneVmId = node.getID();
		MSApplication am = (MSApplication) app;
		double latency = 0;
		double transfer_time = 0;
		double time = 0;
		
		Set<ApplicationEdge> set = am.getEdges();
		for (ApplicationEdge e: set) {
			if (e.getSourceVmId() == geneVmId) {
				int target_index = Policy.getGeneIndexFromNodeId(e.getTargetVmId(), genes, app);
				int tProvId = (int) genes[target_index].getAllele();
				InternetLink link = null;
				try {link = internet.getInternetLink(prov.getID(), tProvId);} 
				catch (Exception e1) {e1.printStackTrace();}
				
				if(link !=null) {
//					System.out.println("边为："+prov.getID()+"--------->"+tProvId);
					latency = link.getLatency();
					transfer_time = e.getMessageLength()/link.getBandwidth() + latency;
				}else {
//					System.out.println("云服务供应商ID为："+prov.getID());
					transfer_time = e.getMessageLength()/(Long)prov.getNetwork().getCharacteristic().get(Constant.BW);
				}
				time += transfer_time;
			}
		}
		return time;
	}
}
