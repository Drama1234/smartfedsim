package federation.dtg.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import it.cnr.isti.smartfed.federation.UtilityPrint;
import it.cnr.isti.smartfed.federation.resources.VmFactory;
import it.cnr.isti.smartfed.federation.resources.VmTyped;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

/**
 * 该类抽象化应用的顶点，表示输入数据项，数据转换项，输出数据项之间的关系
 * @author drama
 *
 */
public class ApplicationVertex {
	private List<CloudletInputData> cloudletInputData;//输入数据项
	private List<CloudletOutputData> cloudletOutputData;//输出数据项
	private List<Cloudlet> cloudlets;
	private List<Vm> vms;
	private Map<Cloudlet, Vm> cloudletMap;
	private Map<Vm, Cloudlet> vmMap;
	private Map<Cloudlet,CloudletInputData> inputdataMap;
	private Map<Cloudlet, CloudletOutputData> outputdataMap;
	private String name;
	private int id;
	private static int counter = 0;
	private double budget = 1.0;
	private VmType vm_type;
	private Vm desiredVm = null;
	
	public Vm getDesiredVm() {
		return desiredVm;
	}
	
	public void setDesiredVm(Vm desiredVm) {
		this.desiredVm = desiredVm;
	}
	
	public ApplicationVertex(String name,int userId,List<CloudletInputData> cloudletInputData
			,List<CloudletOutputData> cloudletOutputDatas,List<Cloudlet> cloudlets,VmType vmtype) {
		this.name = name;
		construct(userId,cloudletInputData,cloudletOutputDatas,cloudlets,vmtype);
	}
	private void construct(int userId,List<CloudletInputData> cloudletInputData
			,List<CloudletOutputData> cloudletOutputData,List<Cloudlet> cloudlets,VmType vmtype) {
		
		this.id = counter++;
		this.cloudlets = cloudlets;
		this.vms = new ArrayList<Vm>();
		this.cloudletInputData = cloudletInputData;
		this.cloudletOutputData = cloudletOutputData;
		this.cloudletMap = new HashMap<Cloudlet, Vm>();
		this.vmMap = new HashMap<Vm, Cloudlet>();
		this.inputdataMap = new HashMap<Cloudlet,CloudletInputData>();
		this.outputdataMap = new HashMap<Cloudlet,CloudletOutputData>();
		
		for (Cloudlet c : cloudlets) {
			
			Vm clone = VmFactory.getVm(vmtype, userId);
			VmTyped cloned = new VmTyped(clone, vmtype);
			this.vms.add(cloned);
			this.cloudletMap.put(c, cloned);
			this.vmMap.put(cloned, c);
			//输入输出数据处理代码，用来将数据与相应的cloudlet对应,id与cloudlet一致
			int cloudletId = c.getCloudletId();
			CloudletInputData inputdata = cloudletInputData.get(cloudletId);
			CloudletOutputData outputdata = cloudletOutputData.get(cloudletId);
			this.inputdataMap.put(c, inputdata);
			this.outputdataMap.put(c, outputdata);
		}
		this.vm_type = vmtype;
	}
	
	public ApplicationVertex(int userId,List<CloudletInputData> cloudletInputData
			,List<CloudletOutputData> cloudletOutputData, List<Cloudlet> cloudlets, VmType vmtype)
	{
		this("", userId, cloudletInputData,cloudletOutputData,cloudlets, vmtype);
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
	
	public List<CloudletInputData> getInputData(){
		return this.cloudletInputData;
	}
	
	public List<CloudletOutputData> getOutputData(){
		return this.cloudletOutputData;
	}
	
	public Vm getAssociatedVm(Cloudlet cloudlet) {
		return cloudletMap.get(cloudlet);
	}
	
	public Cloudlet getAssociatedCloudlet(Vm vm) {
		return vmMap.get(vm);
	}
	
//	public CloudletInputData getAssociatedInputData(Cloudlet cloudlet) {
//		return inputdataMap.get(cloudlet);
//	}
//	
//	public CloudletOutputData getAssociatedOutputData(Cloudlet cloudlet) {
//		return outputdataMap.get(cloudlet);
//	}
	
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
	
	public String toVertexString() {
		StringBuilder res = new StringBuilder();
		res.append("[vertex:");
		res.append("cloudletsize:").append(this.getCloudlets().size());
		res.append("inputdatasize:").append(this.getInputData().size());
		res.append("outputdatasize:").append(this.getOutputData().size());
		res.append("]");
		
		return res.toString();	
	}
	
	public String toCompleteString() {
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
}
