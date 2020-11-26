package workflowDatacenter;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;


public class AbstractGenerator {
	protected AbstractRealDistribution distribution;
	protected long seed;
	
	public AbstractGenerator(long seed)
	{
		distribution = new UniformRealDistribution();
		this.resetSeed(seed);
	}
	
	/**
	 * Change the seed of the generator
	 * @param seed
	 */
	public void resetSeed(long seed)
	{
		this.seed = seed;
		distribution.reseedRandomGenerator(seed);
	}
	
	public AbstractGenerator() {
		distribution = new UniformRealDistribution();
	}

}
