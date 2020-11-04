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
import workflowschedule.Constant;
import workflowschedule.MSPolicy;

public class NetworkBWConstraint extends MSPolicy{
	private static long highNetworkBwValue;
	private static long highProviderBwValue;
	private static double weigthNetworkBw;
	
	public static double getWeigthNetworkBw() {
		return weigthNetworkBw;
	}

	public static void setWeigthNetworkBw(double weigthNetworkBw) {
		NetworkBWConstraint.weigthNetworkBw = weigthNetworkBw;
	}

	public static long getHighProviderBwValue() {
		return highProviderBwValue;
	}

	public static void setHighProviderBwValue(long highProviderBwValue) {
		NetworkBWConstraint.highProviderBwValue = highProviderBwValue;
	}

	public static long getHighNetworkBwValue() {
		return highNetworkBwValue;
	}

	public static void setHighNetworkBwValue(long highNetworkBwValue) {
		NetworkBWConstraint.highNetworkBwValue = highNetworkBwValue;
	}
	
	public NetworkBWConstraint(double weightProviderBw, double weigthNetworkBw,long highestNetworkBw, long highestProviderBw,ConstraintScope c) {
		super(weightProviderBw, MSPolicy.ASCENDENT_TYPE,c);
		setHighProviderBwValue(highestProviderBw);
		setHighNetworkBwValue(highestNetworkBw);
		setWeigthNetworkBw(weigthNetworkBw);
		constraintName = "networkBw";
	}
	
	@Override
	protected double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
		throw new Error("Local evaluation not supported");
	}

//	public double evaluateLocalPolicy(Gene g, MSApplicationNode node, IMSProvider prov, InternetEstimator internet) {
//		long nodeBw =  (Long) node.getNetwork().getCharacteristic().get(Constant.BW); // what I want
//		long provBw =  (Long) prov.getNetwork().getCharacteristic().get(Constant.BW); // what I have
//		double maxBw = highProviderBwValue;
//		double distance;
//		if (nodeBw > 0){
//			distance = calculateDistance_ErrHandling(provBw, nodeBw, maxBw);
//		}
//		else 
//			distance = MAXSATISFACTION_DISTANCE;
//		
//		return distance * getWeight();
//	}
	@Override
	public double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		// get the vm id of the gene
		MSApplicationNode curr_node = app.getNodes().get(gene_index);
		int geneVmId = curr_node.getID();
		
		// get the set of the edges
		MSApplication am = (MSApplication) app;
		Set<ApplicationEdge> set = am.getEdges();
		
		// get the provider id of the gene
		Gene[] genes = chromos.getGenes();
//		int current_prov = (int) genes[gene_index].getAllele();
		
		double sumofdifference = 0;
		double numofdifference = 0;
		double distanceprovider = 0;
		double distancenetwork = 0;
		double distance = 0;
		double providerbw=0;
		
		for (ApplicationEdge e: set){
			if (e.getSourceVmId() == geneVmId){
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), genes, app);
				int tatget_ProvId = (int) genes[target_index].getAllele();
				// check this edge's bw requirement against internet estimator
				
				InternetLink link = null;
				try {
					link = internet.getInternetLink(prov.getID(), tatget_ProvId);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if(link!=null) {
					providerbw += e.getBandwidth();
					double maxInterBw = highNetworkBwValue;
					long internet_interbw = link.getBandwidth();
					double application_interbw = e.getBandwidth();
					//应用程序带宽需求KB/s转换为MB/s
					long application_interBw = ((long) (application_interbw / 1024)); 
					// evaluate the distance
					double res = calculateDistance_ErrHandling(internet_interbw, application_interBw,maxInterBw);
					sumofdifference += res;
					numofdifference ++;
				}
				
//				try {link = internet.getInternetLink(prov.getID(), tatget_ProvId);} 
//				catch (Exception e1) {e1.printStackTrace();}
//				
//				if (current_prov != tatget_ProvId){
//					providerbw += e.getBandwidth();
//				}
//				
//				double maxInterBw = highNetworkBwValue;
//				long internet_interbw = link.getBandwidth();
//				double application_interbw = e.getBandwidth();
//				//应用程序带宽需求KB/s转换为MB/s
//				long application_interBw = ((long) (application_interbw * 1024)); 
//				// evaluate the distance
//				double res = calculateDistance_ErrHandling(internet_interbw, application_interBw,maxInterBw);
//				sumofdifference += res;
//				numofdifference ++;
				else {
					//System.out.println("节点带宽"+curr_node.getNetwork().getCharacteristic().get(Constant.BW));
					Long nodeBw =  (Long)curr_node.getNetwork().getCharacteristic().get(Constant.BW);
//					System.out.println("云服务供应商带宽"+prov.getNetwork().getCharacteristic().get(Constant.BW).getClass());
					
					int provBw =  (int) prov.getNetwork().getCharacteristic().get(Constant.BW)+0;
					//System.out.println("转换后节点带宽"+nodeBw+"转换后云服务供应商带宽"+provBw);
					double maxBw = highProviderBwValue;
					if (nodeBw > 0){
						 distanceprovider = calculateDistance_ErrHandling(provBw, nodeBw, maxBw);
						 //System.out.println("");
					}
					else 
						 distanceprovider = MAXSATISFACTION_DISTANCE;
					
//					distanceprovider = distance * getWeight();
				}
			}
		}
//		long appBw = ((long) (providerbw * 1024));
//		//System.out.println("云服务供应商带宽"+prov.getNetwork().getCharacteristic().get(Constant.BW));
//		
//		long provBw = (long) prov.getNetwork().getCharacteristic().get(Constant.BW);
//		double maxProviderBw = highProviderBwValue;
//		
		if(numofdifference != 0) {
			distancenetwork = sumofdifference / numofdifference;
			distance = distancenetwork * getWeigthNetworkBw()  + distanceprovider * getWeight();
		}else {
			distance = distanceprovider * getWeight();
		}
		return distance;
	}
	
//	@Override
//	public double evaluateGlobalPolicy(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet){
//		long app_bw = calcBwFromEdges(gene_index, chromos, app, prov);
//		long provBw = (long) prov.getNetwork().getCharacteristic().get(Constant.BW);
//		
//		double maxBw = highNetworkBwValue;
//		double distance;
//		if (app_bw > 0)
//			distance = calculateDistance_ErrHandling(provBw, app_bw, maxBw);
//		else 
//			distance = MAXSATISFACTION_DISTANCE;
//		
//		return distance * getWeight();
//	}
//	
//	private static long calcBwFromEdges(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov){
//		Gene[] genes = chromos.getGenes();
//		int current_prov = (int) genes[gene_index].getAllele();
//		MSApplicationNode curr_node = app.getNodes().get(gene_index); // this is safe
//		int geneVmId = curr_node.getID();
//		MSApplication am = (MSApplication) app;
//		double bw = 0;
//		Set<ApplicationEdge> set = am.getEdges();
//		for (ApplicationEdge e: set){
//			if (e.getSourceVmId() == geneVmId){
//				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), genes, app);
//				
//				int tProvId = (int) genes[target_index].getAllele();
//				if (current_prov != tProvId){
//					bw += e.getBandwidth();
//				}
//			}
//		}
//		long byteBw = ((long) (bw * 1024)); 
//		
//		if (DEBUG)
//			System.out.println("Output reuqired bw from Node " + gene_index + " is " + printMBperSec(byteBw) + " MBps");
//		return byteBw;
//	}
	
	public static String printMBperSec(double val){
		double res = val /1024 /1024;
		String s = res + "MBps";
		return s;
	}
}
