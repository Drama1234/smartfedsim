package federation.resources;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

public class test {
	public static void main(String[] args) {
		HostFactory host = new HostFactory();
		List<Pe> pes = new ArrayList<Pe>();
		for (int j=0; j<2; j++)
		{
			pes.add(new Pe(j, new PeProvisionerSimple(10000)));
		}
		
		Host h = host.getDefault(pes);
		System.out.println(h.toString());
	}
	
	
}
