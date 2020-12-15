package workflowfederation;

import java.text.DecimalFormat;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class UtilityPrint {
	public static String toString(Cloudlet c) {
		return "Cloudlet [ID="+c.getCloudletId()+",PES="+c.getNumberOfPes()+"]";
	}
	
	public static void printCloudletList(List<Cloudlet> list){
		System.out.println(getCloudletList(list));
	}
	
	public static String getCloudletList(List<Cloudlet> list) {
		String s = new String();
		String indent = "    ";
		s += "\n";
		s += "========== OUTPUT ==========" + "\n";
		s += "Cloudlet ID" + indent + "STATUS" + indent
				+ "DataCenter ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time" + "\n";
		
		DecimalFormat dft = new DecimalFormat("###.##");
		for (Cloudlet cloudlet : list ) {
			s += indent + cloudlet.getCloudletId() + indent + indent;

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				s += "SUCCESS";
				s += indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()) + "\n";
			}
			else {
				s += "Status is " + Cloudlet.getStatusString(cloudlet.getCloudletStatus());
				s += indent + indent + cloudlet.getResourceId();
			}
		}
		return s;
	}
	
	public static String toString(Vm vm)
	{
		return "VM #"+vm.getId(); 
	}
	
	public static String toStringDetail(Vm vm)
	{
		return "RAM: "+vm.getRam()/1024+"GB"+" DISK: "+vm.getSize()/1024+"GB" + " MIPS: "+vm.getMips()+ " NET: "+vm.getBw()/1024/1024+"MB/s";
	}
	public static String toStringDetail(Host host)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("ram:").append(host.getRam()/1024).append("GB,");
		sb.append("net:").append(host.getBw()/1024/1024).append("MB/s,");
		sb.append("storage:").append(host.getStorage()/1024/1024).append("TB,");
		sb.append("core:").append(host.getNumberOfPes());
		sb.append(",mips:").append(host.getTotalMips()/host.getNumberOfPes());
		
		return sb.toString();
	}
}
