package workflowmapping;

import java.util.HashMap;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import application.Application;
import federation.resources.FederationDatacenter;
import workflowfederation.MonitoringHub;
import workflownetworking.InternetEstimator;


public abstract class AbstractAllocator {
	
	protected HashMap<String, String> persistentStorage;
	protected MonitoringHub monitoring;
	protected InternetEstimator netEstimator;
	protected MappingSolution solution = null;
	
	protected double startSimTime = 0d;
	protected double finishSimTime = 0d;
	protected long startRealTime = 0;
	protected long finishRealTime = 0;
	
	protected long randomSeed;
	
	/**
	 * Builds an allocator, providing the state for the mapping.
	 * @param monitoring
	 * @param netEstimator
	 */
	public AbstractAllocator()
	{
		this.persistentStorage = new HashMap<String, String>();
		randomSeed = System.currentTimeMillis();
	}
	
	public MappingSolution getSolution() 
	{
		return solution;
	}

	/**
	 * It sets the solution of this allocator with a mapping solution object
	 * @param solution
	 */
	public void setSolution(MappingSolution solution) {
		this.solution = solution;
	}
	
	//EMA: I'm not sure if or why this is needed.
	public HashMap<String, String> getStorage()
	{
		return persistentStorage;
	}
	
	
	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public MonitoringHub getMonitoringHub()
	{
		return monitoring;
	}
	
	public InternetEstimator getNetEstimator() 
	{
		return netEstimator;
	}
	

	public void setNetEstimator(InternetEstimator netEstimator)
	{
		this.netEstimator = netEstimator;
	}
	

	public void setMonitoring(MonitoringHub monitoring) 
	{
		this.monitoring = monitoring;
	}
	
	/**
	 * Given an application, it returns a MappingSolution.
	 * @param application
	 * @return
	 */
	public abstract MappingSolution[] findAllocation(Application application);
	
	
	/**
	 * Returns the list of the suitable host in a FedearationDatacenter
	 * of the given VM. It uses the method "isSuitableForVm" of class
	 * Host already defined in the original CloudSim.
	 */
	protected Host getSuitableHost(FederationDatacenter dc, Vm vm)
	{
		for (Host h: dc.getHostList())
		{
			if (h.isSuitableForVm(vm))
				return h;
		}
		return null;
	}
	
	/**
	 * Returns the duration (in simulation time) of the mapping.
	 * @return
	 */
	public double getSimDuration(){
		return finishSimTime - startSimTime;
	}
	
	/**
	 * Returns the duration (in ms) of the mapping.
	 * @return
	 */
	public long getRealDuration(){
		return finishRealTime - startRealTime;
	}
}
