package workflowDatacenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
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
import workflowfederation.Federation;


public class DatacenterGenerator extends AbstractGenerator{

	// dc variables
	protected Range costPerMem;
	protected Range costPerSto;
	protected Range costPerCpu;
	protected Range costPerBw;
	protected Range Bw;
	
	// host variables
	protected Range ramAmount;
	protected Range bwAmount;
	protected Range stoAmount;
	
	// pes variables
	protected Range coreAmount;
	protected Range mipsAmount;
	
	public DatacenterGenerator(long seed) {
		super(seed);
		//Datacenter
		costPerMem = new Range(0.01, 0.1);//USD GB hour
		costPerSto = new Range(0.0002, 0.0020);
		costPerCpu = new Range(0.10, 0.80); 
		//host
		ramAmount = new Range(1024*40, 1024*64);//40GB-64GB
		bwAmount = new Range(100*1024*1024, 500*1024*1024);//100Mb-500Mb/s
		stoAmount = new Range(3*1024*1024, 6*1024*1024); // 3TB - 6TB max
		coreAmount = new Range(1, 8);
		mipsAmount = new Range(5000, 55000);
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
	public List<FederationDatacenter> getDatacenters(int approxNumberDatacenters, int numberTotalHost, AbstractRealDistribution distribution){		// create the list
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>(approxNumberDatacenters);
		
		// Here get the assignment vector
		int[] assign = DistributionAssignment.getAssignmentArray(approxNumberDatacenters, numberTotalHost, distribution);
		
		for (int i=0; i<approxNumberDatacenters; i++) {
			if (assign[i] <= 0)
				continue;
			//一个数据中心中分配不同个数的主机，每个主机类型相同
			long numCore, mips, ram, bw, sto;
			double costCpu, costSto, costMem;
			
			double value = distribution.sample();
			numCore = (long) coreAmount.denormalize(value);
			mips = (long) mipsAmount.denormalize(value);
			ram = (long) ramAmount.denormalize(value);
			bw = (long) bwAmount.denormalize(value);
			sto = (long) stoAmount.denormalize(value);
			
			costCpu = costPerCpu.denormalize(value);
			costSto = costPerSto.denormalize(value);
			costMem = costPerMem.denormalize(value);
			
//			numCore = (int) coreAmount.denormalize(distribution.sample());
//			mips = (int) mipsAmount.denormalize(distribution.sample());
//			ram = (int) ramAmount.denormalize(distribution.sample());
//			bw = (int) bwAmount.denormalize(distribution.sample());
//			sto = (int) stoAmount.denormalize(distribution.sample());
//			
//			costCpu = costPerCpu.denormalize(distribution.sample());
//			costSto = costPerSto.denormalize(distribution.sample());
//			costMem = costPerMem.denormalize(distribution.sample());
			
			// create the datacenters
			FederationDatacenterProfile profile = FederationDatacenterProfile.getDefault();
			profile.set(DatacenterParams.COST_PER_BW, "0");
			profile.set(DatacenterParams.COST_PER_STORAGE, Double.valueOf(String.format("%.4f", costSto))+"");
			profile.set(DatacenterParams.COST_PER_CPU, Double.valueOf(String.format("%.2f", costCpu))+"");
			profile.set(DatacenterParams.COST_PER_MEM, Double.valueOf(String.format("%.2f", costMem))+"");
			profile.set(DatacenterParams.MAX_BW_FOR_VM, bw+"");
			
			// choose a random city
//			Range rangecity = new Range(0, cities.length);
//			int index = (int) Math.floor(rangecity.denormalize(distribution.sample()));
//			City place = cities[index];
//			profile.set(DatacenterParams.CITY, place.toString());
						
			List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
			List<Host> hostList = new ArrayList<Host>();
			List<Pe> peList = new ArrayList<Pe>();// create the virtual processor (PE)
			
			for (int j=0; j<numCore; j++)
			{
				peList.add(new Pe(j, new PeProvisionerSimple(mips)));
			}
			
			// create the hosts
			HostProfile prof = HostProfile.getDefault();
			
			prof.set(HostParams.RAM_AMOUNT_MB, ram+"");
			prof.set(HostParams.BW_AMOUNT, bw+"");
			prof.set(HostParams.STORAGE_MB, sto+"");
			
			for (int k=0; k<assign[i]; k++)
			{
				hostList.add(HostFactory.get(prof, peList));
			}
			// populate the list
			list.add(FederationDatacenterFactory.get(profile, hostList, storageList));
//			System.out.println("云服务供应商大小："+list.size());
//			System.out.println("第"+(i+1)+"个云服务供应商：");
//			System.out.println("拥有"+assign[i]+"个主机");
//			System.out.println("主机性能：");
//			System.out.println("内存大小："+ ram/1024 + "GB");
//			System.out.println("主机带宽： "+ bw/1024/1024 + "Mb/s");
//			System.out.println("存储大小： "+ sto/1024/1024 + "TB");
//			System.out.println("内核性能： ");
//			System.out.println("内核数量： "+ numCore);
//			System.out.println("单核mips： "+ mips);
//			System.out.println("云服务供应商收费模型：");
//			System.out.println("云内带宽费用："+profile.get(DatacenterParams.COST_PER_BW));
//			System.out.println("存储费用："+profile.get(DatacenterParams.COST_PER_STORAGE)+"GB/h");
//			System.out.println("CPU成本："+profile.get(DatacenterParams.COST_PER_CPU)+"GB/h");
//			System.out.println("内存成本："+profile.get(DatacenterParams.COST_PER_MEM)+"GB/h");
//			System.out.println("虚拟机最大带宽："+profile.get(DatacenterParams.MAX_BW_FOR_VM)+"MB/s");
			System.out.println(list.get(i).toStringDetail());
		}
		return list;
	}

//	public static void main(String[] args) {
//		//空指针异常原因：未将数据中心添加到实体中，导致entities.size()为空，因为压根没有创建
//		Range coreAmount = new Range(1, 32);
//		int core = (int) coreAmount.denormalize(0.23);
//		System.out.println("coreamount:"+core);
//		DatacenterGenerator datacenterGenerator = new DatacenterGenerator(77);
//		datacenterGenerator.getDatacenters(20, 1000);
//		//System.out.println("云服务供应商大小:"+list.size());
//	}
}
