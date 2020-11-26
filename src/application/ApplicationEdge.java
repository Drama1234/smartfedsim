/*
Copyright 2013 ISTI-CNR
 
This file is part of SmartFed.

SmartFed is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
SmartFed is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with SmartFed. If not, see <http://www.gnu.org/licenses/>.

*/

package application;

import java.util.Locale;

import org.cloudbus.cloudsim.Vm;
import org.jgrapht.graph.DefaultEdge;

public class ApplicationEdge extends DefaultEdge
{
	private static final long serialVersionUID = 1423234l;
	
	/* requirements */
	private long bandwidth;
	private double latency;
	private double messageLength;
	
	public ApplicationEdge(double mlength, long bandwidth, double latency)
	{
		this.messageLength = mlength;
		this.bandwidth = bandwidth;
		this.latency = latency;
	}
	
	/**
	 * Estimated required bandwidth of this link in KB/s
	 * @return
	 */
	public long getBandwidth()
	{
		return this.bandwidth;
	}

	
	public double getMessageLength()
	{
		return this.messageLength;
	}


	public double getLatency()
	{
		return this.latency;
	}
	
//	public double getMBperHour(){
//		double res = this.bandwidth / 1024 * 3600;
//		return res;
//	}
	
	public String toString(){
		StringBuilder res = new StringBuilder();
		
		double message = this.messageLength;
		String size = message > (1024*1024) ? "MB": "KB";
		message = message > (1024*1024) ? message/1024/1024: message/1024;
		String messages = String.format(Locale.ENGLISH, "%.2f", message);
		double bandwidth = this.bandwidth/(1024*1024);
		String bw = String.format(Locale.ENGLISH, "%.0f",bandwidth);
		
		
		res.append(" Edge [");
		res.append(" vm:(").append(getSourceVmId()).append("->").append(getTargetVmId()).append(")");
		res.append(" message length: ").append(messages).append(size);
		res.append(" bandwith: ").append(bw).append("MB/s");
		res.append(" latency: ").append(this.latency).append("s");
		res.append(" ]");
		
		
		return res.toString();
		// return "(" + super.getSource() + "->" + super.getTarget() + ")";
	}
	
	public Vm getSourceVm(){
		Vm vm;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getSource();
			vm = v.getVms().get(0);
		}
		catch (Exception e){
			vm = null;
		}
		return vm;
	}
	
	public Vm getTargetVm(){
		Vm vm;
		try {
			ApplicationVertex v = (ApplicationVertex) super.getTarget();
			vm = v.getVms().get(0);
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
