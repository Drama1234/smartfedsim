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


import federation.resources.FederationDatacenter;
import federation.resources.VmFactory;
import federation.resources.VmFactory.vmType;
import federation.resources.VmTyped;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import workflowfederation.UtilityPrint;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class abstracts the application vertex.
 * An application vertex can contain one or more Cloudlet, 
 * each representing an instance of a code that can be run
 * on a VM (i.e. a web server).
 * 
 * For the sake of simplicity, we consider each cloudlet running on 
 * a dedicated VM. All the VMs inside an application vertex are of the
 * same type.
 *
 * This class also contains the method to get the associated VM from
 * a cloudlet, and viceversa.
 * 
 * @author carlini, anastasi
 *
 */
public class ApplicationVertex
{
	private List<Cloudlet> cloudlets;
	private List<Vm> vms;
	private List<FederationDatacenter> federationDatacenters;
	private Map<Cloudlet, Vm> cloudletMap;
	private Map<Vm, Cloudlet> vmMap;
	private String name;
	private int id;
	private static int counter = 0;
	private double budget = 0;
	private double task_time = 0;
	private vmType vm_type;
	private Vm desiredVm = null;
	//得到用户期望的虚拟机
	public Vm getDesiredVm() {
		return desiredVm;
	}

	/**
	 * Setting desired Vm, that may be different from that one specified by VmType as available in Amazon. 
	 * Introduced for STRATOS comparison, assuming homogeneus Vms for all cloudlets.
	 * @param desiredVm
	 */
	public void setDesiredVm(Vm desiredVm) {
		this.desiredVm = desiredVm;
	}
	
//	private void construct(int userId, List<Cloudlet> cloudlets,vmType vmtype)
//	{
//		this.id = counter++;
//		this.cloudlets = cloudlets;
//		this.cloudletMap = new HashMap<Cloudlet, Vm>();
//		this.vmMap = new HashMap<Vm, Cloudlet>();
//		this.vms = new ArrayList<Vm>();
//		
//		for (Cloudlet c : cloudlets)
//		{
//			Vm clone = VmFactory.getVm(vmtype, userId);
//			VmTyped cloned = new VmTyped(clone, vmtype);
//			this.vms.add(cloned);
//			this.cloudletMap.put(c, cloned);
//			this.vmMap.put(cloned, c);
//		}
//	}
//	
//	public ApplicationVertex(String name, int userId, List<Cloudlet> cloudlets, vmType vmtype)
//	{
//		this.name = name;
//		construct(userId, cloudlets, vmtype);
//	}
	
	/**
	 * Constructor for ApplicationVertex
	 * @param userId
	 * @param cloudlets
	 * @param vmtype
	 */
//	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, vmType vmtype)
//	{
//		this("", userId, cloudlets, vmtype);
//	}
	/**
	 * Constructor for ApplicationVertex
	 * @param userId
	 * @param cloudlets
	 * @param vm
	 */
	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, Vm vm)
	{
		this("", userId, cloudlets, vm);
	}
	
	public ApplicationVertex(String name,int userId, List<Cloudlet> cloudlets, Vm sample)
	{
		this.name = name;
		this.id = counter++;
		this.vm_type = vmType.CUSTOM;
		this.cloudlets = cloudlets;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.vms = new ArrayList<Vm>();
		this.federationDatacenters = new ArrayList<FederationDatacenter>();
		
		for (Cloudlet c : cloudlets)
		{
			Vm clone = VmFactory.cloneVMnewId(sample);
			VmTyped cloned = new VmTyped(clone, vm_type);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);
			this.federationDatacenters = null;
		}
	}
	
	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, Vm sample, FederationDatacenter federationDatacenter) {
		this.id = counter++;
		this.vm_type = vmType.CUSTOM;
		this.cloudlets = cloudlets;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.vms = new ArrayList<Vm>();
		this.federationDatacenters = new ArrayList<FederationDatacenter>();
		
		for(Cloudlet c : cloudlets) {
			Vm clone = VmFactory.cloneVMnewId(sample);
			VmTyped cloned = new VmTyped(clone, vm_type);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);
			this.federationDatacenters.add(federationDatacenter);
		}
	}
	
	public double getTask_time() {
		return task_time;
	}

	public void setTask_time(double task_time) {
		this.task_time = task_time;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}

	public void setName(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Cloudlet> getCloudlets() {
		return this.cloudlets;
	}
	
	public List<Vm> getVms() {
		return this.vms;
	}
	
	public List<FederationDatacenter> getfeFederationDatacenters(){
		if (this.federationDatacenters!=null) {
//			System.out.println("指定了云服务供应商");
			return this.federationDatacenters;
		}
		else {
//			System.out.println("没有指定云服务供应商");
			return null;
		}
	}
	
	public Vm getAssociatedVm(Cloudlet cloudlet) {
		return cloudletMap.get(cloudlet);
	}
	
	public Cloudlet getAssociatedCloudlet(Vm vm) {
		return vmMap.get(vm);
	}
	
	
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("");
		String prefix = "";
		for (Vm vm: this.getVms())
		{
			res.append(prefix);
			prefix = "-";
			res.append("#").append(vm.getId());
		}
		res.append("");
		
		return name + "[id_" + id + "] " + res.toString();
	}
	
	public String toCompleteString() 
	{
		StringBuilder res = new StringBuilder();
		
		res.append("Vertex [");
		res.append(" id: ").append(this.getId());
		res.append(" size: ").append(this.getCloudlets().get(0).getCloudletLength());
		res.append(" budget: ").append(this.getBudget());
		
		
		if(this.getfeFederationDatacenters() != null) {
			res.append(" datacenter: ").append(this.getfeFederationDatacenters().get(0).toString());
		} 
		
		res.append(" VMs: {");
		String prefix = "";
		for (Vm vm: this.getVms())
		{
			res.append(prefix);
			prefix = ",";
			res.append("#").append(vm.getId());
		}
		res.append(" - ").append(UtilityPrint.toStringDetail(this.getVms().get(0)));
		res.append("}]");
		
		return res.toString();
	}

	public vmType getVmType() {
		return vm_type;
	}

	public void cloningFeatures(ApplicationVertex vertexForVm) {
		budget = vertexForVm.getBudget();
	}
}
