package federation.resources;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;


public class DatacenterCharacteristicsMS extends DatacenterCharacteristics{
	private City city;
//	private long maxBwForVm = 0;
//	private double costVmCustom = 0;
//	private double[] costPerIntraRegion = null;
//	private double costPerInterRegion = 0;
	
	
	public City getCity() {
		return city;
	}

//	public double[] getCostVmTypes() {
//		return costVmTypes;
//	}
	
	

	public DatacenterCharacteristicsMS(City city,String architecture, String os, 
			String vmm, List<? extends Host> hostList,double timeZone, 
			double costPerCpu, double costPerMem, double costPerStorage, double costPerBw) {
		super(architecture, os, vmm, hostList, timeZone, costPerCpu, costPerMem, costPerStorage, costPerBw);
			this.city = city;
//			this.costVmCustom = costVmCustom;
//			this.costVmTypes = costPerVm;
	}
	
//	public double getCostVmCustom() {
//		return costVmCustom;
//	}

	public double getCostPerCpu() {
		return super.getCostPerSecond();	
	}
	
	public String toString() {
		String str = new String();
		str += " City: " + city;
		str	+= " Cost " + "(mem: " + super.getCostPerMem();
		str += " and cpu "+ getCostPerCpu();
		str += " and sto " + super.getCostPerStorage();
		str += " and bw: " + super.getCostPerBw();
		str += ") ";
		str += "Host: " + super.getHostList().size();
		str += " and core " + super.getHostList().get(0).getNumberOfPes();
		str += " and Ram " + super.getHostList().get(0).getRam();
		str += " and Mips " + super.getHostList().get(0).getTotalMips();
		str += " and Storage " + super.getHostList().get(0).getStorage();
		str += " and Bw " +super.getHostList().get(0).getBw();
		return str;
	}
	
	/* return the highest pes value among the hosts of the data center*/
	public int getHighestCore() {
		List<Host> list = super.getHostList();
		Host max = Collections.max(list,new Comparator<Host>() {
			public int compare(Host first,Host second) {
				if(first.getNumberOfPes() < second.getNumberOfPes()) {
					return 1;
				}else if(first.getNumberOfPes() > second.getNumberOfPes()) {
					return -1;
				}
				return 0;
			}
		});
		return max.getNumberOfPes();
	}
	
	/* Return the highest ram value among the hosts of the datacenter */
	public double getHighestRam() {
		List<Host> list = super.getHostList();
		Host max = Collections.max(list,new Comparator<Host>() {
			public int compare(Host first,Host second) {
				if(first.getRam() < second.getRam()) {
					return 1;
				}else if(first.getRam() > second.getRam()){
					return -1;
				}
				return 0;
			}
		});
		return max.getRam();
	}
	
	/*
	 * Return the highest storage value among the hosts of the datacenter
	 */
	public long getHighestStorage() {
		List<Host> list = super.getHostList();
		Host max = Collections.max(list, new Comparator<Host>() {
		    @Override
		    public int compare(Host first, Host second) {
		    	// System.out.println(first.getStorage() + " " + second.getStorage());
		        if (first.getStorage() > second.getStorage())
		            return 1;
		        else if (first.getStorage() < second.getStorage())
		            return -1;
		        return 0;
		    }
		});
		// System.out.println(max.getStorage());
		return max.getStorage();
	}
	
	
//	public long getHighestBw() {
//		return this.maxBwForVm;
//	}
//	
//	public void setHighestBw(long bw) {
//		this.maxBwForVm = bw;
//	}
//	
	public long getHighestAllocatedBwAmongHosts() {
		List<Host> list = super.getHostList();
		Host max = Collections.max(list,new Comparator<Host>() {
			public int compare(Host first,Host second) {
				if(first.getBw() < second.getBw())
					return 1;
				else if(first.getBw() > second.getBw())
					return -1;
				return 0;
			}
		});
		return max.getBw();
	}
	
}