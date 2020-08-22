package federation.resources;

import java.util.HashMap;
import java.util.Map;



public class StorageProfile {
	public enum StorageParams{
		CLASS("org.cloudbus.cloudsim.HarddriveStorage"),
		CAPACITY(1024*1024*1024*1024+""); // 1TB
		
		private String def;
		
		private StorageParams(String def)
		{
             this.def = def;
		}
	}
	
	private  Map<StorageParams, String> data;
	
	private StorageProfile()
	{
		data = new HashMap<StorageParams, String>();
		
		for (StorageParams p : StorageParams.values())
		{
			data.put(p, p.def);
		}
	}
	
	public static StorageProfile getDefault()
	{
		return new StorageProfile();
	}
	
	public String get(StorageParams par)
	{
		return data.get(par);
	}
	
	public void set(StorageParams par, String value)
	{
		data.put(par, value);
	}
	
	
	
}
