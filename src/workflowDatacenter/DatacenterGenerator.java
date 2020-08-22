package workflowDatacenter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

import federation.resources.City;
import federation.resources.FederationDatacenter;
import federation.resources.FederationDatacenterFactory;
import federation.resources.FederationDatacenterProfile;
import federation.resources.FederationDatacenterProfile.DatacenterParams;
import federation.resources.HostFactory;
import federation.resources.HostProfile;
import federation.resources.HostProfile.HostParams;
import it.cnr.isti.smartfed.federation.generation.Range;



public class DatacenterGenerator extends AbstractGenerator{

	// dc variables
	protected Range costPerMem;
	protected Range costPerSto;
	protected Range costPerCpu;
	protected Range costPerBw;
	
	// host variables
	protected Range ramAmount;
	protected Range bwAmount;
	protected Range stoAmount;
	
	// pes variables
	protected Range coreAmount;
	protected Range mipsAmount;
	
	protected City[] cities;
	
	public void setCities(City[] c){
		this.cities = c;
	}
	
	public DatacenterGenerator(long seed) {
		super(seed);
		//dc
		costPerMem = new Range(0.001, 0.005);//USD GB hour
		costPerSto = new Range(0.01, 0.05);
		costPerCpu = new Range(0.01, 0.05); 
		//costPerBw = new Range(0.05, 0.15); //former (0.001, 0.05)
		//host
		ramAmount = new Range(1024*1, 1024*64);//1GB-64GB
		bwAmount = new Range(100*1024*1024, 800*1024*1024);//100Mb-800Mb/s
		stoAmount = new Range(250*1024, 10*1024*1024); // 250GB - 10TB max
		coreAmount = new Range(1, 32);
		mipsAmount = new Range(1000, 50000);
		
		cities = City.values();	
	}
	
	/**
	 * Generates the list of datacenters by assigning hosts to datacenters according
	 * to a uniform distribution. If a datacenter will result with 0 hosts, it will not
	 * be created.
	 * 
	 * @param numOfDatacenters
	 * @param numHost
	 * @return
	 */
	public List<FederationDatacenter> getDatacenters(int numOfDatacenters, int numHost)
	{
		UniformRealDistribution urd = new UniformRealDistribution();
		urd.reseedRandomGenerator(this.seed);
		
		return getDatacenters(numOfDatacenters, numHost, urd);
	}
	
	/**
	 * Generates the list of datacenters, and assigns the host to datacenters according
	 * the given distribution. 
	 * 
	 * Note that a distribution can very well assign zero hosts to a datacenter.
	 * However, since cloudsim does not support zero-host datacenter, we do not create 
	 * the empty datacenters.
	 * 
	 * @param approxNumberDatacenters - the approximate total number of datacenters that will be created
	 * @param numberTotalHost - the total number of host in all datacenters
	 * @param distribution
	 * @return
	 */
	public List<FederationDatacenter> getDatacenters(int approxNumberDatacenters, int numberTotalHost, AbstractRealDistribution distribution){
		// create the list
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>(approxNumberDatacenters);
		
		// Here get the assignment vector
		int[] assign = DistributionAssignment.getAssignmentArray(approxNumberDatacenters, numberTotalHost, distribution);
		
		for (int i=0; i<approxNumberDatacenters; i++) {
			if (assign[i] <= 0)
				continue;
			//一个数据中心中分配不同个数的主机，每个主机类型不同
			int numCore, mips, ram, bw, sto;
			double costCpu, costSto, costMem;
			
			double value = distribution.sample();
			
			costCpu = costPerCpu.denormalize(value);
			costSto = costPerSto.denormalize(value);
			costMem = costPerMem.denormalize(value);
			
			
			// create the datacenters
			FederationDatacenterProfile profile = FederationDatacenterProfile.getDefault();
			profile.set(DatacenterParams.COST_PER_BW, "0");
			profile.set(DatacenterParams.LATENCT, "0");
			profile.set(DatacenterParams.COST_PER_STORAGE, costSto+"");
			profile.set(DatacenterParams.COST_PER_CPU, costCpu+"");
			profile.set(DatacenterParams.COST_PER_MEM, costMem+"");
			//profile.set(DatacenterParams.MAX_BW_FOR_VM, bw+"");
			
			// choose a random city
			Range rangecity = new Range(0, cities.length);
			int index = (int) Math.floor(rangecity.denormalize(distribution.sample()));
			City place = cities[index];
			profile.set(DatacenterParams.CITY, place.toString());
						
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
			List<Host> hostList = new ArrayList<Host>();
			List<Pe> peList = new ArrayList<Pe>();// create the virtual processor (PE)
			
			for (int k=0; k<assign[i]; k++)
			{
				double values = distribution.sample();
				numCore = (int) coreAmount.denormalize(values);
				mips = (int) mipsAmount.denormalize(values);
				ram = (int) ramAmount.denormalize(values);
				bw = (int) bwAmount.denormalize(values);
				sto = (int) stoAmount.denormalize(values);
				
				for (int j=0; j<numCore; j++)
				{
					peList.add(new Pe(j, new PeProvisionerSimple(mips)));
				}
				
				// create the hosts
				HostProfile prof = HostProfile.getDefault();
				
				prof.set(HostParams.RAM_AMOUNT_MB, ram+"");
				prof.set(HostParams.BW_AMOUNT, bw+"");
				prof.set(HostParams.STORAGE_MB, sto+"");
				hostList.add(HostFactory.get(prof, peList));
			}

			// populate the list
			list.add(FederationDatacenterFactory.get(profile, hostList, storageList));
		}
		return list;
	}
}
