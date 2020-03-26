package it.cnr.isti.smartfed.federation.application;

import java.util.Locale;

import org.cloudbus.cloudsim.Vm;
import org.jgrapht.graph.DefaultEdge;

import it.cnr.isti.smartfed.networking.SecuritySupport;

public class ApplicationEdge extends DefaultEdge{
	private double bandwidth;
	private SecuritySupport security;
	private double latency;
	private double messageLength;
	private double messageRate;
	
	
	/**
	 * 
	 * @param mlength message length in KB sent in this link 
	 * @param mrate message rate in Hz sent in this link 
	 * @param security
	 * @param latency
	 */
	public ApplicationEdge(double mlength, double mrate, SecuritySupport security, double latency){
		this.messageLength = mlength;
		this.messageRate = mrate;
		this.bandwidth = messageLength/messageRate;
		this.security = security;
		this.latency = latency;
	} 
	
	public ApplicationEdge(double mlength, double mrate) {
		this(mlength, mrate, SecuritySupport.NO, 0);
	}
	
	public ApplicationEdge(double mlength, double mrate, double latency) {
		this(mlength, mrate, SecuritySupport.NO, latency);
	}
	
	/*
	 * Estimated required bandwidth of this link in KB/s
	 */
	public double getBandwith() {
		return this.bandwidth;
	}
	
	public double getMessageLength()
	{
		return this.messageLength;
	}

	public SecuritySupport getSecurity()
	{
		return this.security;
	}
	
	public double getLatency()
	{
		return this.latency;
	}
	
	public double getMBperHour(){
		double res = this.bandwidth / 1024 * 3600;
		return res;
	}
	
	public String toString() {
		double message = this.messageLength;
		String size = message > 1024 ? "MB": "KB";
		message = message > 1024 ? message/1024: message;
		String res = String.format(Locale.ENGLISH, "%.2f", message);
		return res + size;	
	}
	
	public Vm getSourceVm() {
		Vm vm;
		try {
			ApplicationVertex v = (ApplicationVertex)super.getSource();
			vm = v.getVms().get(0);
		}catch (Exception e) {
			vm = null;
		}
		return vm;
	}
	
	public Vm getTargetVm(){
		Vm vm;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getTarget();
			vm =  v.getVms().get(0);
		}
		catch (Exception e){
			vm = null;
		}
		return vm;
	}
	
	public int getSourceVmId(){
		int id;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getSource();
			id = v.getVms().get(0).getId();
		}
		catch (Exception e){
			id = -1;
		}
		return id;
	}
	
	public int getTargetVmId(){
		int id;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getTarget();
			id = v.getVms().get(0).getId();
		}
		catch (Exception e){
			id = -1;
		}
		return id;
	}
}
