package application;

import org.cloudbus.cloudsim.Cloudlet;

public class ExpectationCloudlet extends Cloudlet{
	Integer expectationTime;
	Integer expectationBw;
	
	/**
	 * 
	 * @param cloudletId
	 * @param cloudletLength
	 * @param pesNumber
	 * @param cloudletFileSize
	 * @param cloudletOutputSize
	 * @param expTime
	 * @param expBw
	 */
	public ExpectationCloudlet(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, 
			int expTime,int expBw) {
		super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, 
				CloudletProvider.getDefaultUtilModel(), 
				CloudletProvider.getDefaultUtilModel(),
				CloudletProvider.getDefaultUtilModel());
			this.expectationTime = expTime;
			this.expectationBw = expBw;
	}
}
