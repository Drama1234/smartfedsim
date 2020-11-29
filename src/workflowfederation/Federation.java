package workflowfederation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import application.Application;
import federation.resources.DatacenterCharacteristicsMS;
import federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.test.TestResult;
import workflowmapping.AbstractAllocator;
import workflowmapping.MappingSolution;


public class Federation extends SimEntity{
	private List<Integer> datacenterIds;
	private List<FederationDatacenter> datacenters;
	private Map<Integer, DatacenterCharacteristicsMS> datacenterCharacteristicsList;
	
	private Map<Integer, Application> vmToApp; 
	private Map<Integer, Vm> idToVm;
	private Map<Application, Allocation> appToAllocation; 
	private HashMap<Integer, Integer> vmToDatacenter;
	
	private List<Cloudlet> receivedCloudlet;
	
	private AbstractAllocator mappingModule = null;
	
	private long seed;
	private boolean emptyQueue = false;
	
	private void construct() {
		datacenterIds = CloudSim.getCloudResourceList();
		datacenterCharacteristicsList = new HashMap<Integer, DatacenterCharacteristicsMS>();
		receivedCloudlet = new ArrayList<Cloudlet>();
		vmToApp = new HashMap<Integer, Application>();
		idToVm = new HashMap<Integer, Vm>();
		appToAllocation = new HashMap<Application, Allocation>();
		datacenters = new ArrayList<FederationDatacenter>();
		vmToDatacenter = new HashMap<>();
	}
	
	public Federation(AbstractAllocator allocator, long seed)
	{
		super("Federation");
		this.seed = seed;
		setMappingModule(allocator);
		construct();
	}
	
	public HashMap<Integer, Integer> getVmToDatacenter(){
		return vmToDatacenter;
	}
	
	public void setDatacenters(List<FederationDatacenter> dcs)
	{
		this.datacenters = dcs;
	}
	
	public void startEntity() {
		FederationLog.timeLogDebug("Federation (id "+ this.getId() +") is starting...");	
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}
	
	public void processEvent(SimEvent ev) {
//		System.out.println("事件标志："+ev.getTag());
		switch (ev.getTag()) {
			// Resource characteristics request
			case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
				FederationLog.timeLogDebug("Event received: " + "RESOURCE_CHARACTERISTICS_REQUEST (" + ev.getTag() +")");
				processResourceCharacteristicsRequest(ev);
				break;
			// Resource characteristics answer
			case CloudSimTags.RESOURCE_CHARACTERISTICS:
				FederationLog.timeLogDebug("Event received: " + "RESOURCE_CHARACTERISTICS (" + ev.getTag() +")");
				processResourceCharacteristics(ev);
				break;
			// application submit	
			case FederationTags.APPLICATION_IN_QUEUE:
				FederationLog.timeLogDebug("Event received: " + "APPLICATION_IN_QUEUE (" + ev.getTag() +")");
				processApplicationSubmit(ev);
				break;
			case FederationTags.EMPTY_QUEUE:
				FederationLog.timeLogDebug("Event received: " + "EMPTY_QUEUE (" + ev.getTag() +")");
				this.emptyQueue = true;
				break;
			// VM Creation answer
			case CloudSimTags.VM_CREATE_ACK:
				FederationLog.timeLogDebug("Event received: " + "VM_CREATE_ACK (" + ev.getTag() +")");
				processVmCreate(ev);
				break;
			// A finished cloudlet returned
			case CloudSimTags.CLOUDLET_RETURN:
				FederationLog.timeLogDebug("Event received: " + "CLOUDLET_RETURN (" + ev.getTag() +")");
				processCloudletReturn(ev);
				break;
			// if the simulation finishes
			case CloudSimTags.END_OF_SIMULATION:
				FederationLog.timeLogDebug("Event received: " + "END_OF_SIMULATION (" + ev.getTag() +")");
				shutdownEntity();
				break;
				// other unknown tags are processed by this method
			default:
				FederationLog.timeLog("Event received: " + "NOT_RECOGNIZED (" + ev.getTag() +")");
		}
	}
	
	// Asks for monitoring to datacenters
	protected void processResourceCharacteristicsRequest(SimEvent ev)
	{
		FederationLog.timeLogDebug("Sending monitoring hook to " +datacenterIds.size()+ " datacenters");
		
		for (Integer datacenterId : datacenterIds)
		{
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}
	
	// Receive monitoring from datacenters
	protected void processResourceCharacteristics(SimEvent ev)
	{
		DatacenterCharacteristicsMS characteristics = (DatacenterCharacteristicsMS) ev.getData();
		datacenterCharacteristicsList.put(characteristics.getId(), characteristics);
		
		FederationLog.timeLogDebug("Received monitoring response from datacenter #"+characteristics.getId());
		if (this.emptyQueue)
			mappingModule.getMonitoringHub().shutdownEntity();
	}
	
	// Manage the application submit
	protected void processApplicationSubmit(SimEvent ev) {
		Application ag = ((LinkedList<Application>)ev.getData()).pollFirst();
		
		FederationLog.timeLogDebug("############# APPLICATION ###################");
		FederationLog.timeLogDebug(ag.toString());
		FederationLog.timeLogDebug("#############################################");
		
		Allocation allocation = appToAllocation.get(ag);
		if (allocation == null)
		{
			// compute the mapping solution
			//long before = System.currentTimeMillis();
			MappingSolution[] sols = mappingModule.findAllocation(ag);//在具体的分配算法内部进行整理
			
			// commented because it is better to retrieve time from the mappingSolution object 
			// inside Experiment class
			
			// long after = System.currentTimeMillis();
			// TestResult.getMappingTime().addValue(after - before);
			
//			if (sols[0].isValid() == false)
//			{
//				FederationLog.timeLog("Invalid mapping solution for application: \n" + ag);
//				// mappingModule.getMonitoringHub().shutdownEntity();
//				TestResult.getFailures().addValue(1);
//				return;
//			}
			
			
//			FederationLog.timeLog("############# MAPPING SOLUTION ###################");
//			FederationLog.timeLog("(Federation)" + sols[0].toString());
//			FederationLog.timeLog("##################################################");
			
			allocation = new Allocation(ag, sols, seed);
			appToAllocation.put(ag, allocation);
		}
		this.continueAllocation(allocation);
	}
	
	private void continueAllocation(Allocation allocation)
	{
		// take the data
		Application app = allocation.getApplication();
		Vm vm = allocation.getNextVm();
				
		int dcid = allocation.pickDatacenter(vm, datacenters);
//		System.out.println("分配选择的数据中心id"+dcid);
		if (dcid == -1)
		{
			FederationLog.timeLog("WARNING: FAILED mapping of "+app);
			
			// FIXME: not sure it can be here.
			// mappingModule.getMonitoringHub().shutdownEntity();
			TestResult.getFailures().addValue(1);
			return;
		}
		
		// update some indexes
		vmToApp.put(vm.getId(), app);
		idToVm.put(vm.getId(), vm);
		
		// sending it
		sendNow(dcid, CloudSimTags.VM_CREATE_ACK, vm);
		FederationLog.timeLog("Sent "+UtilityPrint.toString(vm)+" to "+((FederationDatacenter)CloudSim.getEntity(dcid)).toString());
	}
	
	// Manage the answer to a VM creation
	protected void processVmCreate(SimEvent ev)
	{
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		Application app = vmToApp.get(vmId);
		Allocation allocation = appToAllocation.get(app);
		
		if (result == CloudSimTags.TRUE) 
		{
			FederationLog.timeLog(UtilityPrint.toString(idToVm.get(vmId)) + " creation success in "+(FederationDatacenter)CloudSim.getEntity(datacenterId));
			vmToDatacenter.put(new Integer(vmId), new Integer(datacenterId));
			allocation.setRunning(idToVm.get(vmId), datacenterId);
			
			if (allocation.isCompleted())
			{
				// TestResult.getAllocationTime().addValue(allocation.getSimDuration());
				TestResult.getVmDifference().addValue(allocation.differenceWithSolution());
				startCloudlets(allocation);
			}
			else {
				continueAllocation(allocation);
			}
		} 
		else 
		{
			FederationLog.timeLog(UtilityPrint.toString(idToVm.get(vmId))+" creation failed in "+(FederationDatacenter)CloudSim.getEntity(datacenterId));
			
			allocation.failedMapping(idToVm.get(vmId), datacenterId);
			continueAllocation(allocation);
		}
	}
	
	private void startCloudlets(Allocation allocation)
	{
		Application app = allocation.getApplication();
		for (Vm vm: app.getAllVms())
		{
			Cloudlet cloudlet = app.getVertexForVm(vm).getAssociatedCloudlet(vm);
			cloudlet.setVmId(vm.getId());
			cloudlet.setUserId(this.getId());	
			Integer dcId = allocation.getAllocatedDatacenterId(vm);
			FederationLog.timeLogDebug(" Cloudlet" + cloudlet.getCloudletId() +" is going to be submitted to "+(FederationDatacenter)CloudSim.getEntity(dcId));
			sendNow(dcId, CloudSimTags.CLOUDLET_SUBMIT_ACK, cloudlet);
		}
	}
	
	protected void processCloudletReturn(SimEvent ev) 
	{
		Cloudlet cloudlet = (Cloudlet) ev.getData();
//		System.out.println("获得的任务ID:"+cloudlet.getCloudletId());
		receivedCloudlet.add(cloudlet);	
		FederationLog.timeLog("Cloudlet" + cloudlet.getCloudletId() +" received");
//		System.out.println("接收到的任务数量"+receivedCloudlet.size()+"");
		// if all the cloudlet are finished, shutdown the monitoring
		if (receivedCloudlet.size() >= vmToDatacenter.size() && this.emptyQueue) {
			
			mappingModule.getMonitoringHub().shutdownEntity();
		}
	}

	public List<Cloudlet> getReceivedCloudlet()
	{
		return this.receivedCloudlet;
	}
	
	/**
	 * Return all the allocations of the Federation (one for each application)
	 * @return
	 */
	public Collection<Allocation> getAllocations()
	{
		return appToAllocation.values();
	}
	
	public List<FederationDatacenter> getDatacenters() 
	{
		return datacenters;
	}
	
	public void shutdownEntity() 
	{
		Log.printLine(getName() + " is shutting down...");	
	}
	
	public AbstractAllocator getMappingModule() {
		return mappingModule;
	}
	
	public void setMappingModule(AbstractAllocator mappingModule) {
		this.mappingModule = mappingModule;
	}
	
	public static FederationDatacenter findDatacenter(List<FederationDatacenter> list, Integer id)
	{
		for (FederationDatacenter fd: list)
		{
			if (fd.getId() == id)
				return fd;
		}
		return null;
	}
}
