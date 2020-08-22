package workflownetworking;

import it.cnr.isti.smartfed.networking.SecuritySupport;

public class InternetLink {
	private long bandwidth; // bps
	private double latency; // seconds
	private SecuritySupport security;
	private double bwcost;
	
	
	public InternetLink(long bandwidth, double latency, double bwcost,SecuritySupport security)
	{
		this.bandwidth = bandwidth;
		this.latency = latency;
		this.bwcost = bwcost;
		this.security = security;
	}
		
	public double getBwcost() {
		return bwcost;
	}

	public void setBwcost(double bwcost) {
		this.bwcost = bwcost;
	}
	
	public long getBandwidth()
	{
		return bandwidth;
	}

	public void setBandwidth(long bandwidth)
	{
		this.bandwidth = bandwidth;
	}

	public double getLatency() {
		return latency;
	}

	public void setLatency(int latency)
	{
		this.latency = latency;
	}

	public SecuritySupport getSecurity()
	{
		return security;
	}

	public void setSecurity(SecuritySupport security)
	{
		this.security = security;
	}
}
