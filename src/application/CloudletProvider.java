package application;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

import application.CloudletProfile.CloudletParams;
import it.cnr.isti.smartfed.federation.resources.ResourceCounter;

public class CloudletProvider {
	private static Cloudlet createCloudlet(CloudletProfile profile) {
		UtilizationModel uCPU = null;
		UtilizationModel uRAM = null; 
		UtilizationModel uBW = null;
		
		try
		{
			uCPU = (UtilizationModel)Class.forName(profile.get(CloudletParams.CPU_MODEL)).newInstance();
			uRAM = (UtilizationModel)Class.forName(profile.get(CloudletParams.RAM_MODEL)).newInstance();
			uBW = (UtilizationModel)Class.forName(profile.get(CloudletParams.BW_MODEL)).newInstance();
		}
		catch (Exception e)
		{
			// TODO: log the error
			e.printStackTrace();
		}
		
		Cloudlet c = new Cloudlet(ResourceCounter.nextCloudletID(), 
				Integer.parseInt(profile.get(CloudletParams.LENGTH)), 
				Integer.parseInt(profile.get(CloudletParams.PES_NUM)), 
				Integer.parseInt(profile.get(CloudletParams.FILE_SIZE)),
				Integer.parseInt(profile.get(CloudletParams.OUTPUT_SIZE)),
				uCPU, uRAM, uBW);
		
		return c;
	}
	
	public static UtilizationModel getDefaultUtilModel(){
		CloudletProfile profile = CloudletProfile.getDefault();
		UtilizationModel uCPU = null;

		
		try {
			uCPU = (UtilizationModel)Class.forName(profile.get(CloudletParams.CPU_MODEL)).newInstance();
		} 
		catch (Exception e) {
			uCPU = new UtilizationModelFull();
		}
		return uCPU;
	}
	
	public static Cloudlet getDefault()
	{
		return createCloudlet(CloudletProfile.getDefault());
	}
	
	public static Cloudlet get(CloudletProfile profile)
	{
		return createCloudlet(profile);
	}
	
	

}
