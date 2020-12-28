package old.metascheduler.constraints;

import java.util.Set;

import org.jgap.Gene;
import org.jgap.IChromosome;

import application.ApplicationEdge;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.MSApplicationNode;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import old.metaschedulers.Constant;
import old.metaschedulers.MSPolicy;
import workflownetworking.InternetEstimator;

public class NetworkConstraint extends MSPolicy{
private static long highNetworkValue;

	
	public static long getHighNetworkValue() {
		return highNetworkValue;
	}

	public void setHighNetworkValue(long highNetValue) {
		highNetworkValue = highNetValue;
	}

	public NetworkConstraint(double weight, long highestValue, ConstraintScope c) {
		super(weight, MSPolicy.ASCENDENT_TYPE, c);
		highNetworkValue = highestValue;
		constraintName = "network";
	}

	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		long nodeBw =  (Long) node.getNetwork().getCharacteristic().get(Constant.BW); // what I want
		long provBw =  (Long) prov.getNetwork().getCharacteristic().get(Constant.BW); // what I have
		double maxBw = highNetworkValue;
		double distance;
		if (nodeBw > 0){
			distance = calculateDistance_ErrHandling(provBw, nodeBw, maxBw);
		}
		else 
			distance = MAXSATISFACTION_DISTANCE;
		
		return distance * getWeight();
	}
	
	public static String printMBperSec(double val){
		double res = val /1024 /1024;
		String s = res + "MBps";
		return s;
	}
	
	@Override
	public double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet){
		long app_bw = calcBwFromEdges(gene_index, chromos, app, prov);
		long provBw = (long) prov.getNetwork().getCharacteristic().get(Constant.BW);
		
		double maxBw = highNetworkValue;
		double distance;
		if (app_bw > 0)
			distance = calculateDistance_ErrHandling(provBw, app_bw, maxBw);
		else 
			distance = MAXSATISFACTION_DISTANCE;
		
		return distance * getWeight();
	}

	private static long calcBwFromEdges(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
		Gene[] genes = chromos.getGenes();
		int current_prov = (int) genes[gene_index].getAllele();
		MSApplicationNode curr_node = app.getNodes().get(gene_index); // this is safe
		int geneVmId = curr_node.getID();
		MSApplication am = (MSApplication) app;
		double bw = 0;
		Set<ApplicationEdge> set = am.getEdges();
		for (ApplicationEdge e: set){
			if (e.getSourceVmId() == geneVmId){
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), genes, app);
				
				int tProvId = (int) genes[target_index].getAllele();
				if (current_prov != tProvId){
					bw += e.getBandwidth();
				}
			}
		}
		long byteBw = ((long) (bw * 1024)); 
		
		if (DEBUG)
			System.out.println("Output reuqired bw from Node " + gene_index + " is " + printMBperSec(byteBw) + " MBps");
		return byteBw;
	}
}
