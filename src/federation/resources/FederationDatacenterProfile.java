package federation.resources;

import java.util.HashMap;
import java.util.Map;


public class FederationDatacenterProfile {
	public enum DatacenterParams{
		ARCHITECTURE("x86"),
		OS("Linux"),
		VMM("Xen"),
		TIME_ZONE("1"), // CET (?)
		COST_PER_CPU("3.0"),
		COST_PER_MEM("0.005"),
		COST_PER_STORAGE("0.001"),
		COST_PER_BW("0.0"),
		VM_ALLOCATION_POLICY("org.cloudbus.cloudsim.VmAllocationPolicySimple"),
		MAX_BW_FOR_VM("0"),//云服务供应商云内带宽
		SCHEDULING_INTERNAL("0");
		
		private String def;
		
		private DatacenterParams(String def)
		{
             this.def = def;
		}
	}
	
	protected  Map<DatacenterParams, String> data;
	
	private FederationDatacenterProfile()
	{
		data = new HashMap<DatacenterParams, String>();
		
		for (DatacenterParams p : DatacenterParams.values())
		{
			data.put(p, p.def);
		}
	}
	
	public static FederationDatacenterProfile getDefault()
	{
		return new FederationDatacenterProfile();
	}
	
	/*
	//USA 爱荷华
	public static FederationDatacenterProfile getGoogleCloudC2() {
		//按需价格
		FederationDatacenterProfile prof = new FederationDatacenterProfile();
		prof.data.put(DatacenterParams.COST_PER_SEC, "0.03398");//USD vcpu hour
		prof.data.put(DatacenterParams.COST_PER_MEM, "0.00455");//USD GB hour
		prof.data.put(DatacenterParams.COST_PER_STORAGE, "0.04");//USD GB month
		prof.data.put(DatacenterParams.COST_PER_LOCALBW, "0.00");// USD GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_USA_BW, "0.01");//USB GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_EUR_BW, "0.02");//USB GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_ASI_BW, "0.05");//USB GB
		prof.data.put(DatacenterParams.COST_PER_INTER_REGION_BW, "0.1");//USB GB 
		//50, 100, 200, 300, 400, or 500 Mbps interconnect attachment	
		prof.data.put(DatacenterParams.COST_PER_VM_MEDIUM,"0.2088");//USB hour
		prof.data.put(DatacenterParams.COST_PER_VM_LARGE,"0.4176");
		prof.data.put(DatacenterParams.COST_PER_VM_XLARGE,"0.8358");
		prof.data.put(DatacenterParams.COST_PER_VM_2XLARGE,"1.5660");
		prof.data.put(DatacenterParams.COST_PER_VM_4XLARGE,"3.1321");	
		return prof;
	}
	// amazon EC2 C6g 欧洲 法兰克福
	public static FederationDatacenterProfile getAmazonCloud() {
		FederationDatacenterProfile prof = new FederationDatacenterProfile();
		prof.data.put(DatacenterParams.COST_PER_SEC, "0.0");
		prof.data.put(DatacenterParams.COST_PER_MEM, "0.0");
		prof.data.put(DatacenterParams.COST_PER_STORAGE, "0.0");
		prof.data.put(DatacenterParams.COST_PER_VM_MEDIUM,"0.0388");//USB hour
		prof.data.put(DatacenterParams.COST_PER_VM_LARGE,"0.0776");
		prof.data.put(DatacenterParams.COST_PER_VM_XLARGE,"0.1552");
		prof.data.put(DatacenterParams.COST_PER_VM_2XLARGE,"0.3104");
		prof.data.put(DatacenterParams.COST_PER_VM_4XLARGE,"0.6208");
		prof.data.put(DatacenterParams.COST_PER_LOCALBW, "0.00");// USD GB	
		prof.data.put(DatacenterParams.COST_PER_INTRA_USA_BW, "0.01");//USD GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_EUR_BW, "0.02");//USD GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_ASI_BW, "0.09");//USD GB
		prof.data.put(DatacenterParams.COST_PER_INTER_REGION_BW, "0.12");//USD GB 
		return prof;	
	}
	//北京 c6
	public static FederationDatacenterProfile getAlibabaCloudc6() {
		FederationDatacenterProfile prof = new FederationDatacenterProfile();
		prof.data.put(DatacenterParams.COST_PER_SEC, "0.0");
		prof.data.put(DatacenterParams.COST_PER_MEM, "0.0");
		prof.data.put(DatacenterParams.COST_PER_STORAGE, "0.0");
		prof.data.put(DatacenterParams.COST_PER_VM_MEDIUM,"0.06");
		prof.data.put(DatacenterParams.COST_PER_VM_LARGE,"0.0121");//USD hour
		prof.data.put(DatacenterParams.COST_PER_VM_XLARGE,"0.241");
		prof.data.put(DatacenterParams.COST_PER_VM_2XLARGE,"0.362");
		prof.data.put(DatacenterParams.COST_PER_VM_4XLARGE,"0.483");
		prof.data.put(DatacenterParams.COST_PER_LOCALBW, "0.00");// USD GB hour
		prof.data.put(DatacenterParams.COST_PER_INTRA_USA_BW, "0.03");//USD GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_EUR_BW, "0.04");//USD GB
		prof.data.put(DatacenterParams.COST_PER_INTRA_ASI_BW, "0.005");//USD GB
		prof.data.put(DatacenterParams.COST_PER_INTER_REGION_BW, "0.14");//USD GB 
		return prof; 
	}*/
	
	public String get(DatacenterParams par)
	{
		return data.get(par);
	}
	
	public void set(DatacenterParams par, String value)
	{
		data.put(par, value);
	}
}
