package Constraints;

import java.util.Set;

import org.jgap.IChromosome;

import application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflownetworking.InternetLink;
import workflowschedule.Policy;

public class LatencyConstraint extends Policy{
	private static double highNetworkLatencyValue;
	
	public LatencyConstraint(double normWeight,double highestValue) {
		super(normWeight, Policy.DESCENDENT_TYPE);
		highNetworkLatencyValue = highestValue;
		this.constraintName = "NetworkLatencyConstraint";
	}
	
	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		// get the vm id of the gene
		MSApplicationNode curr_node = app.getNodes().get(gene_index); 
		int geneVmId = curr_node.getID();
		
		// get the set of the edges
		MSApplication am = (MSApplication) app;
		Set<ApplicationEdge> set = am.getEdges();
		
		double sumofdifference = 0;
		double numofdifference = 0;
		double distance = 0;
		
		for (ApplicationEdge e : set) {
			if (e.getSourceVmId() == geneVmId) {
				int target_index = Policy.getGeneIndexFromNodeId(e.getTargetVmId(), chromos.getGenes(), app);			
				int targetProvider = (int) chromos.getGenes()[target_index].getAllele();
				
				InternetLink link = null;
				try {
					link = internet.getInternetLink(prov.getID(), targetProvider);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(link!=null) {
					double internet_latency = link.getLatency();
					double application_latency = e.getLatency();
					if (DEBUG)
						System.out.println("Eval before applying weights for " + "NodeID " + prov.getID() + " - ProvID " + targetProvider);
					double res = calculateDistance_ErrHandling(internet_latency, application_latency, highNetworkLatencyValue);
					sumofdifference += res;
					numofdifference ++;
				}
			}
		}
		if (numofdifference == 0) {
			distance = MAXSATISFACTION_DISTANCE;
		}else {
			distance = sumofdifference / numofdifference;
		}
		System.out.println("跨云延迟约束："+distance * getWeight());
		return distance * getWeight();
		
	}
}
