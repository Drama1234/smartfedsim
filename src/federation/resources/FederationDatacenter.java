package federation.resources;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.workflowsim.Task;

import federation.resources.VmFactory.vmType;
import workflowfederation.FederationLog;
import workflowfederation.UtilityPrint;
import workflowfederation.WorkflowCost;



public class FederationDatacenter extends Datacenter implements Comparable<FederationDatacenter>{

	public FederationDatacenter(String name, DatacenterCharacteristics characteristics, 
			VmAllocationPolicy vmAllocationPolicy,List<Storage> storageList, double schedulingInterval) 
					throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
//		System.out.println("数据中心名称："+name);
//		System.out.println("特征:"+characteristics.toString());
//		System.out.println("虚拟机分配策略："+vmAllocationPolicy.toString());
//		System.out.println("存储列表："+storageList.size());
//		System.out.println("调度间隔:"+schedulingInterval);
	}

	
//	public String getDatacenterRepresentation() {
//		long ram = 0;
//		long net = 0;
//		long netTot = 0;
//		long mips = 0;
//		long storage = 0;
//		String  result = new String();
//		List<Host> hostlist = this.getHostList();
//		List<Vm> l = null;
//		for (int i = 0; i < hostlist.size(); i++) {
//			if (hostlist.get(i) instanceof HostDynamicWorkload){
//				HostDynamicWorkload h = (HostDynamicWorkload) hostlist.get(i);
//				ram += h.getRam()- h.getUtilizationOfRam();
//				//net += hostlist.get(i).getBw()- hostlist.get(i).getUtilizationOfBw();
//				net += h.getUtilizationOfBw();
//				netTot += h.getBw();
//				storage += h.getStorage();
//				mips += h.getTotalMips()- h.getUtilizationMips();
//			}
//			else {
//				Host h = (Host) hostlist.get(i);
//				ram += h.getRam();
//				l = h.getVmList();
//			}
//		}
//		result += "@@@@@@@@@@@@@@ Datacenter " + this.getName() + " @@@@@@@@@@@ " + hostlist.size() + " host\n";
////		result += "City: " + ((DatacenterCharacteristicsMS) super.getCharacteristics()).getCity() + "\n";
//		result += "VMs: ";
//		if (l != null)
//			for (Vm vm : l)
//				result +=  vm.getId() + " \n";
//		else 
//			result += "none" + "\n";
//		result += "RAM: " + ram + "\n";
//		result += "NET used: " + net + "\n";
//		result += "NET tot: " + netTot + "\n";
//		result += "STORAGE: " + storage + "\n";
//		result += "MIPS: " + mips + "\n";
//		return result;
//	}
	
	public String getDatacenterCharacteristicString(){
		return ((DatacenterCharacteristicsMS) super.getCharacteristics()).toString();
	}
	
	
	public String toStringDetail() {
		StringBuilder sb = new StringBuilder();
		DatacenterCharacteristicsMS chars = this.getMSCharacteristics();
		sb.append("name:").append(chars.getResourceName()).append(",");
		sb.append("bw:").append(chars.getDatacenterBw()/1024/1024).append("MB/s,");
		sb.append("host_num:").append(this.getHostList().size()).append(",");
		Host host = this.getHostList().get(0);
		if (host != null)
		{
			sb.append("host-desc:{");
			sb.append(UtilityPrint.toStringDetail(host));
			sb.append("}").append(",");
		}
		// custom costs
		sb.append("cost-custom:").append("{");
		sb.append("ram:").append(chars.getCostPerMem()).append(",");
		sb.append("storage:").append(chars.getCostPerStorage()).append(",");
		sb.append("bw:").append(chars.getCostPerBw()).append(",");
		sb.append("cpu:").append(chars.getCostPerCpu()).append("}").append(",");

		return sb.toString();
	}
	
	public DatacenterCharacteristicsMS getMSCharacteristics(){
		return (DatacenterCharacteristicsMS) super.getCharacteristics();
	}
	
	public long getDatacenterBw() {
		return ((DatacenterCharacteristicsMS)super.getCharacteristics()).getDatacenterBw();
	}
	
	@Override
	public String toString() {
		return "FederationDatacenter [id: " + this.getId() +", " + this.getName() +"]"; // + getDatacenterCharacteristic();
	}
	
	
	@Override
	public int compareTo(FederationDatacenter o) {
		double thiscost = this.getCharacteristics().getCostPerMem();
		double ocost = o.getCharacteristics().getCostPerMem();
		if (thiscost > ocost)
			return 1;
		if (thiscost == ocost)
			return 0;
		return -1;
	}
	
	protected void processCloudletSubmit(SimEvent ev, boolean ack) {
		updateCloudletProcessing();
		try {
			// gets the Cloudlet object
			Task cl = (Task) ev.getData();
			
			// checks whether this Cloudlet has finished or not
			if (cl.isFinished()) {
				Log.printLine("Already finished");
				String name = CloudSim.getEntityName(cl.getUserId());
				Log.printLine(getName() + ": Warning - Cloudlet #" + cl.getCloudletId() + " owned by " + name
						+ " is already completed/finished.");
				Log.printLine("Therefore, it is not being executed again");
				Log.printLine();

				// NOTE: If a Cloudlet has finished, then it won't be processed.
				// So, if ack is required, this method sends back a result.
				// If ack is not required, this method don't send back a result.
				// Hence, this might cause CloudSim to be hanged since waiting
				// for this Cloudlet back.
				if (ack) {
					int[] data = new int[3];
					data[0] = getId();
					data[1] = cl.getCloudletId();
					data[2] = CloudSimTags.FALSE;

					// unique tag = operation tag
					int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
					
					sendNow(cl.getUserId(), tag, data);
				}
				sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
				return;
			}
			
			// process this Cloudlet to this CloudResource
			cl.setResourceParameter(getId(),getMSCharacteristics().getCostPerCpu(), getMSCharacteristics().getCostPerBw());
			int userId = cl.getUserId();
			int vmId = cl.getVmId();
//			System.out.println("任务"+cl.getCloudletId()+"所需的文件："+cl.getRequiredFiles().size());
//			List<String> files = cl.getRequiredFiles();
//			for (String string : files) {
//				System.out.println("文件:"+string);
//			}
			
			// time to transfer the files
			double fileTransferTime = predictFileTransferTimes(cl.getFileList());
//			System.out.println("任务ID:" + cl.getCloudletId() + "文件传输时间:"+fileTransferTime);
			Host host = getVmAllocationPolicy().getHost(vmId, userId);
			Vm vm = host.getVm(vmId, userId);
			CloudletScheduler scheduler = vm.getCloudletScheduler();
//			System.out.println("调度："+scheduler.toString());
			
//			List<Double> mipsShare = scheduler.getCurrentMipsShare();
//			double capacity = 0.0;
//			for (Double mips : mipsShare) {
//				capacity += mips;
//			}
//			System.out.println("任务分配得到得MIPS:"+capacity);
//			System.out.println("任务分配得到内核数量："+mipsShare.size());
			
			double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);
			FederationLog.timeLog("Estimated finish time for cloudlet " + cl.getCloudletId() + " " + estimatedFinishTime);

			// if this cloudlet is in the exec queue
			if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
				estimatedFinishTime += fileTransferTime;
				send(getId(), estimatedFinishTime, CloudSimTags.VM_DATACENTER_EVENT);
//				System.out.println("VMID:"+vm.getId()+"已经完成的任务数量"+vm.getCloudletScheduler().isFinishedCloudlets());
			}
			
			if (ack) {
				int[] data = new int[3];
				data[0] = getId();
				data[1] = cl.getCloudletId();
				data[2] = CloudSimTags.TRUE;
//				System.out.println("cloudletID:"+cl.getCloudletId());
				// unique tag = operation tag
				int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
				sendNow(cl.getUserId(), tag, data);
			}
			
//			System.out.println("任务是否完成："+scheduler.isFinishedCloudlets());
//			sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
			
		} catch (ClassCastException c) {
			Log.printLine(getName() + ".processCloudletSubmit(): " + "ClassCastException error.");
			c.printStackTrace();
		} catch (Exception e) {
			Log.printLine(getName() + ".processCloudletSubmit(): " + "Exception error.");
			e.printStackTrace();
		}
		
		checkCloudletCompletion();
		
//		List<? extends Host> list = getVmAllocationPolicy().getHostList();
//		System.out.println("虚拟机分配策略得到的主机列表大小:"+list.size());
//		for (int i = 0; i < list.size(); i++) {
//			Host host = list.get(i);
//			for (Vm vm : host.getVmList()) {
//				System.out.println("虚拟机大小"+vm.getSize());
////				System.out.println("虚拟机分配大小："+vm.getCloudletScheduler().);
//				if (vm.getCloudletScheduler().isFinishedCloudlets()) {
//					System.out.println("是否任务完成："+vm.getCloudletScheduler().isFinishedCloudlets());
//					Cloudlet cl = vm.getCloudletScheduler().getNextFinishedCloudlet();
//					System.out.println("任务："+cl.getCloudletId());
//				}else
//					System.out.println("任务列表为空");
//			}
//		}
	}
	
	protected void updateCloudletProcessing() {
		if (CloudSim.clock() < 0.111 || CloudSim.clock() > getLastProcessTime() + 0.1) {
			List<? extends Host> list = getVmAllocationPolicy().getHostList();
			double smallerTime = Double.MAX_VALUE;
			for (int i = 0; i < list.size(); i++) {
				Host host = list.get(i);
				// inform VMs to update processing
				double time = host.updateVmsProcessing(CloudSim.clock());
				// what time do we expect that the next cloudlet will finish?
				if (time < smallerTime) {
					smallerTime = time;
				}
			}
			// gurantees a minimal interval before scheduling the event
			if (smallerTime < CloudSim.clock() + 0.11) {
				smallerTime = CloudSim.clock() + 0.11;
			}
			if (smallerTime != Double.MAX_VALUE) {
				schedule(getId(), (smallerTime - CloudSim.clock()), CloudSimTags.VM_DATACENTER_EVENT);
			}
			setLastProcessTime(CloudSim.clock());
		}
	}
	
	protected double predictFileTransferTimes(List<File> requiredFiles) {
		double time = 0.0;
		
		for (File file : requiredFiles) {
			if(file!=null)
				time += file.getSize() / this.getDatacenterBw();
		}
		
//		Iterator<File> iter = requiredFiles.iterator();
//		while (iter.hasNext()) {
//			String fileName = iter.next();
//			
////			System.out.println("数据中心存储大小:"+getStorageList().size());
//			for (int i = 0; i < getStorageList().size(); i++) {
//				Storage tempStorage = getStorageList().get(i);
//				File tempFile = tempStorage.getFile(fileName);
//				System.out.println("文件名称："+fileName);
//				System.out.println("文件大小："+tempFile.getSize());
//				if (tempFile != null) {
//					System.out.println("数据中心存储传输速率："+tempStorage.getMaxTransferRate());
//					time += tempFile.getSize() / tempStorage.getMaxTransferRate();
////					time += 
//					break;
//				}
//			}
//		}
		return time;
	}
	

	public Double getDebtsForUser(Integer user_id)
	{
		return this.getDebts().get(user_id);
	}
	
	@Override
	public void startEntity()
	{
		// remove the unwanted log printing
		Log.disable();
		super.startEntity();
		Log.enable();
	}
	
	@Override
	public void shutdownEntity()
	{
		// remove the unwanted log printing
		Log.disable();
		super.shutdownEntity();
		Log.enable();
	}
	
	/**
	 * Prints the debts.
	 */
	public void printDebts() {
		Log.printLine("*****Datacenter: " + getName() + "*****");
		Log.printLine("User id\t\tDebt");

		Set<Integer> keys = getDebts().keySet();
		Iterator<Integer> iter = keys.iterator();
		DecimalFormat df = new DecimalFormat("#.###");
		while (iter.hasNext()) {
			int key = iter.next();
			double value = getDebts().get(key);
			Log.printLine(key + "\t\t" + df.format(value));
		}	
	}
	
	/**
	 * Process the event for an User/Broker who wants to create a VM in this Datacenter. This
	 * Datacenter will then send the status back to the User/Broker.
	 * 
	 * @param ev a Sim_event object
	 * @param ack the ack
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev, boolean ack) {
		
		Vm generic_vm = (Vm) ev.getData();
		Vm vm = new VmTyped(generic_vm, vmType.CUSTOM);
		

		boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
//		System.out.println("分配成功与否："+result);

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = vm.getId();

			if (result) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			send(vm.getUserId(), 0.1, CloudSimTags.VM_CREATE_ACK, data);
		}

		if (result) {
			double amount = 0.0;
			double myamount = 0.0;
			if (getDebts().containsKey(vm.getUserId())) {
				amount = getDebts().get(vm.getUserId());
			}
			
			// cloudsim code
			// amount += getCharacteristics().getCostPerMem() * vm.getRam();
			// amount += getCharacteristics().getCostPerStorage() * vm.getSize();
			
			// our code
			myamount = WorkflowCost.singleVmCost(vm, this);
			amount += myamount;
			
			getDebts().put(vm.getUserId(), amount);

			getVmList().add(vm);

			if (vm.isBeingInstantiated()) {
				vm.setBeingInstantiated(false);
			}
			List<Double> mips = getVmAllocationPolicy().getHost(vm).getVmScheduler().getAllocatedMipsForVm(vm);
//			System.out.println("vm属性："+vm.getMips());
//			System.out.println("虚拟机分配的MIPS大小："+mips.size());
//			for (Double double1 : mips) {
//				System.out.println("mips的值："+mips);
//			}
			vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler().getAllocatedMipsForVm(vm));
		}
	}
}
