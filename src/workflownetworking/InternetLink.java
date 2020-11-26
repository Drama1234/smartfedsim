package workflownetworking;

public class InternetLink {
	private long bandwidth; // bps
	private double latency; // seconds
	private double bwcost;
	
	
	public InternetLink(long bandwidth, double latency, double bwcost)
	{
		this.bandwidth = bandwidth;
		this.latency = latency;
		this.bwcost = bwcost;
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
}
