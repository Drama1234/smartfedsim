package workflow.simulation.app;

import java.util.Set;

import com.weiyu.experiment.simulation.app.AppFilename;

public class RemoteSense extends AbstractApplication{
	
	public static final String namespace = "RemoteSense";
	private int numNDWI;

	@Override
	protected void populateDistributions() {
		/*
         * File size distributions.
         */
		this.distributions.put("RY", Distribution.getConstantDistribution(1024000));
		this.distributions.put("p2p5", Distribution.getConstantDistribution(136540));
		this.distributions.put("500", Distribution.getConstantDistribution(68270));
		this.distributions.put("AWI", Distribution.getConstantDistribution(34135));
		this.distributions.put("result", Distribution.getConstantDistribution(170675));
		/*
         * Runtime distributions.
         */
		this.distributions.put("SpiltData", Distribution.getConstantDistribution(0.5));
		this.distributions.put("NDWI", Distribution.getConstantDistribution(3));
		this.distributions.put("avgNDWI", Distribution.getConstantDistribution(1));
		this.distributions.put("AWI", Distribution.getConstantDistribution(2));
		this.distributions.put("MergeData", Distribution.getConstantDistribution(0.5));
	}

	@Override
	protected void processArgs(String[] args) {
		this.numNDWI = 5;
	}

	@Override
	protected void constructWorkflow() {
		int rupture = 0;
		SpiltData spiltData = new SpiltData(this, "SpiltData", "1.0", getNewJobID());
		for (int i = 0; i < numNDWI; i++) {
			String prefix = "regionyear_RID000_YID000_DID00" + rupture + "_p2p5";
			
			
		}
		
	}
}

class SpiltData extends AppJob{
	public SpiltData(RemoteSense remotesense, String name, String version, String jobID) {
        super(remotesense, remotesense.namespace, name, version, jobID);
    }
	public void finish() {
		Set<AppFilename> inputs = getInputs();
		
	}
	
}

class NDWI extends AppJob{
	public NDWI(RemoteSense remoteSense, String name, String version, String jobID) {
		super(remoteSense, remoteSense.namespace, name, version, jobID);
	}
	
}
