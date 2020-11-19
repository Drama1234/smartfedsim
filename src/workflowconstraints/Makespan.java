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

public class Makespan {
	public static Double calculateMakespan_Network(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov,InternetEstimator internet) {
		MSApplicationNode node = app.getNodes().get(gene_index);
		double tasktime = tasktime(node,prov);
		double networktime = networktime(gene_index, chromos, app, prov,internet);
		return tasktime+networktime;
	}
	
	private static Double tasktime(MSApplicationNode node, IMSProvider prov){
		Double providerMips = (Double)prov.getComputing().getCharacteristic().get(Constant.MIPS)* (Integer)prov.getComputing().getCharacteristic().get(Constant.CPU_NUMBER);
		//Double providerMips = (Double)prov.getCharacteristic().get(Constant.MIPS) * (Double)prov.getCharacteristic().get(Constant.CPU_NUMBER);0
		Long cloudletlength = (Long)node.getCharacteristic().get(Constant.vertextLength);
		Double tasktime = (Double) (cloudletlength / providerMips);
		return tasktime;
	}

	private static double networktime(int gene_index, IChromosome chromos, IMSApplication app, IMSProvider prov, InternetEstimator internet) 
	{
		Gene[] genes = chromos.getGenes();
		int current_provId = (int) genes[gene_index].getAllele();
		
		MSApplicationNode curr_node = app.getNodes().get(gene_index);
		int geneVmId = curr_node.getID();
		MSApplication am = (MSApplication) app;
		double latency = 0;
		double transfer_time = 0;
		double time = 0;
		Set<ApplicationEdge> set = am.getEdges();
		for (ApplicationEdge e: set) {
			if (e.getSourceVmId() == geneVmId) {
				int target_index = MSPolicy.getGeneIndexFromNodeId(e.getTargetVmId(), genes, app);
				
				int tProvId = (int) genes[target_index].getAllele();
				InternetLink link = null;
				try {link = internet.getInternetLink(prov.getID(), tProvId);} 
				catch (Exception e1) {e1.printStackTrace();}
				if(link !=null) {
					latency = link.getLatency();
					transfer_time = (e.getMessageLength() * 1024)/link.getBandwidth() + latency;
//					System.out.println("============跨云传输=============");
//					System.out.println("数据传输长度："+e.getMessageLength());
//					System.out.println("网络传输带宽："+link.getBandwidth());
//					System.out.println("网络延迟："+latency);
//					System.out.println("传输时间为："+transfer_time);
				}else {
					transfer_time = (e.getMessageLength() * 1024)/(Integer)prov.getNetwork().getCharacteristic().get(Constant.BW);
//					System.out.println("============云内传输=============");
//					System.out.println("传输数据长度为："+e.getMessageLength());
//					System.out.println("云服务供应商带宽："+prov.getNetwork().getCharacteristic().get(Constant.BW));
//					System.out.println("传输时间为："+transfer_time);
				}
				time += transfer_time;
			}
		}
		return time;
	}
}
