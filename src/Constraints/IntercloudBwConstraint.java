package Constraints;

import java.util.List;
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

public class IntercloudBwConstraint extends Policy{
	private static double highNetworkBwValue;
	
	public IntercloudBwConstraint(double normWeight, double highestValue) {
		super(normWeight, Policy.ASCENDENT_TYPE);
		highNetworkBwValue= highestValue;
		this.constraintName = "IntercloudBwConstraint";
	}

	@Override
	protected double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		// get the vm id of the gene
		List<MSApplicationNode> nodes = app.getNodes();
		MSApplicationNode node = nodes.get(gene_index);
		int geneVmId = node.getID();
		
		// get the set of the edges
		MSApplication am = (MSApplication) app;
		Set<ApplicationEdge> set = am.getEdges();
		
		double sumofdifference = 0;
		double numofdifference = 0;
		double distance = 0;
		
		for (ApplicationEdge e : set) {
			if (e.getSourceVmId() == geneVmId) {
				int target_index = Policy.getGeneIndexFromNodeId(e.getTargetVmId(), chromos.getGenes(), app);			
				int target_Provider = (int) chromos.getGenes()[target_index].getAllele();
				
				InternetLink link = null;
				try {
					link = internet.getInternetLink(prov.getID(), target_Provider);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(link!=null) {
					long internet_interBw = link.getBandwidth();//what I have
					long application_interbw = e.getBandwidth();//what I want
					//应用程序带宽需求KB/s转换为MB/s
					long application_interBw = application_interbw / 1024; 
					
					double res = calculateDistance_ErrHandling(internet_interBw, application_interBw, highNetworkBwValue);
					sumofdifference += res;
					numofdifference ++;
				}
			}
		}
		if (numofdifference == 0) {
			distance = MAXSATISFACTION_DISTANCE;
		}else{
			distance = sumofdifference / numofdifference;
		}
		return distance * getWeight();
	}
}
