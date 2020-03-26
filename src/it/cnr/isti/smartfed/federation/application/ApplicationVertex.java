package it.cnr.isti.smartfed.federation.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import it.cnr.isti.smartfed.federation.UtilityPrint;
import it.cnr.isti.smartfed.federation.resources.Country;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmTyped;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

public class ApplicationVertex {
	private List<Cloudlet> cloudlets;
	private List<Vm> vms;
	private Map<Cloudlet, Vm> cloudletMap;
	private Map<Vm, Cloudlet> vmMap;
	private String name;
	private int id;
	private static int counter = 0;
	private String country = "";
	private double budget = 1.0;
	private VmType vm_type;
	private Vm desiredVm = null;
	private Country countryEnum;
	
	public Vm getDesiredVm() {
		return desiredVm;
	}
	
	public void setDesiredVm(Vm desiredVm) {
		this.desiredVm = desiredVm;
	}
	
	//构造虚拟机和任务之间的关系，
	private void construct (int userId, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this.id = counter++;
		this.cloudlets = cloudlets;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.vms = new ArrayList<Vm>();
		
		for (Cloudlet c : cloudlets)
		{
			Vm clone = VmFactory.getVm(vmtype, userId);
			VmTyped cloned = new VmTyped(clone, vmtype);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);
		}
		this.vm_type = vmtype;
	}
	
	public ApplicationVertex(String name, int userId, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this.name = name;
		construct(userId, cloudlets, vmtype);
	}
	
	/**
	 * Constructor for ApplicationVertex
	 * @param userId
	 * @param cloudlets
	 * @param vmtype
	 */
	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this("", userId, cloudlets, vmtype);
	}
	
	public ApplicationVertex(int userId, List<Cloudlet> cloudlets, Vm sample) {
		this.id = counter++;
		this.vm_type = VmType.CUSTOM;
		this.cloudlets = cloudlets;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.vms = new ArrayList<Vm>();
		
		for (Cloudlet c : cloudlets){
			Vm clone = VmFactory.cloneVMnewId(sample);
			VmTyped cloned = new VmTyped(clone, vm_type);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);	
		}
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(Country place) {
		this.countryEnum = place;
		this.country = place.toString();
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
		res.append("size: ").append(this.getCloudlets().size());
		res.append(" budget: ").append(this.getBudget());
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
	
	public VmType getVmType() {
		return vm_type;
	}
	
	public char getVmTypeChar() {
		char c;
		switch (vm_type){
		case SMALL:
			c= 's';
			break;
		case MEDIUM:
			c='m';
			break;
		case LARGE:
			c = 'l';
			break;
		case XLARGE:
			c = 'x';
			break;
		case CUSTOM:
			c = 'c';
			break;
		default:
				c='c';
		}
		return c;
	}

	public void cloningFeatures(ApplicationVertex vertexForVm) {
		budget = vertexForVm.getBudget();
		country = vertexForVm.getCountry();
	}
}
