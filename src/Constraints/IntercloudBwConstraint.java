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
					application_interbw = application_interbw/1024/1024; 
					internet_interBw = internet_interBw/1024/1024;
					double maxBW = highNetworkBwValue/1024/1024;
					maxBW = Double.valueOf(String.format("%.2f", maxBW));
					if (DEBUG)
						System.out.println("Eval before applying weights for " + "NodeID " + prov.getID()+ " - ProvID " + target_Provider);
					double res = calculateDistance_ErrHandling(internet_interBw, application_interbw, maxBW);
					sumofdifference += res;
					numofdifference ++;
				}
			}
		}
		if (numofdifference == 0) {
			distance = MAXSATISFACTION_DISTANCE;
		}else{
			//个数越多，差异越大，值越小
			distance = sumofdifference / numofdifference;
		}
		System.out.println("跨云带宽约束："+distance);
		return distance * getWeight();
	}
}
