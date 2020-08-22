package workflowconstraints;

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
import workflowschedule.MSPolicy;

public class LatencyConstraint extends MSPolicy{
	private static double highNetworkLatencyValue;
	
	public static double getHighNetworkLatencyValue() {
		return highNetworkLatencyValue;
	}
	
	public static void setHighNetworkLatencyValue(double highNetworkLatencyValue) {
		LatencyConstraint.highNetworkLatencyValue = highNetworkLatencyValue;
	}

	public LatencyConstraint(double weight,double highestLatencyValue, ConstraintScope group) {
		super(weight, MSPolicy.DESCENDENT_TYPE,group);
		setHighNetworkLatencyValue(highestLatencyValue);
		constraintName = "networkLatency";
	}

	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		throw new Error("Local evaluation not supported");
	}
	
	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet) {
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
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), chromos.getGenes(), app);			
				int targetProvider = (int) chromos.getGenes()[target_index].getAllele();
				
				// check this edge's latency requirement against internet estimator
				
				InternetLink link = null;
				try {link = internet.getInternetLink(prov.getID(), targetProvider);} 
				catch (Exception e1) {e1.printStackTrace();}
				
				double internet_latency = link.getLatency();
				double application_latency = e.getLatency();
				
				// evaluate the distance
				double res = calculateDistance_ErrHandling(internet_latency, application_latency, highNetworkLatencyValue);
				sumofdifference += res;
				numofdifference ++;
			}
		}
		if (numofdifference == 0)
		{
			distance = MAXSATISFACTION_DISTANCE;
		}
		else
		{
			distance = sumofdifference / numofdifference;
		}

		return distance * getWeight();
	}	
}
