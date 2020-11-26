package WorkTest;
/*
 * 该类的策略如下：
 * 1，根据传递过来的任务的数量创建应用程序
 * 2，它创建了一个主机列表，包含仅能够支持该应用程序的所有主机
 * 3，它创建数据中心，根据概率作为参数分配主机
 */

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

import application.Application;
import federation.resources.FederationDatacenter;
import federation.resources.FederationDatacenterFactory;
import federation.resources.HostFactory;
import federation.resources.HostProfile;
import federation.resources.HostProfile.HostParams;
import workflownetworking.InternetEstimator;
import workflowtest.InterfaceDataSet;


public class PreciseDataset implements InterfaceDataSet{
	private int numberOfCloudlets; 
	private double probNewDc;
	
	private Application application = null;
	private List<FederationDatacenter> datacenters;
	private List<Host> hostList = new ArrayList<Host>();
	
	public PreciseDataset(int numberOfCloudlets, double probNewDc)
	{
		this.numberOfCloudlets = numberOfCloudlets;
		this.probNewDc = probNewDc;
	}
	
	private void _internalCreateApplications(int userId) {
		application = new SimpleApplication(userId, 1);
	}
	private void _internalCreateDatacenter() {
		int pe_index = 0;
		
		for (Vm vm :application.getAllVms()) {
			// create the pe
			List<Pe> peList = new ArrayList<Pe>();
			double expected_mips = vm.getMips();
			Pe pe = new Pe(pe_index++, new PeProvisionerSimple(expected_mips+1));
			peList.add(pe);
			
			// create the host
			HostProfile profile = HostProfile.getDefault();
			profile.set(HostParams.RAM_AMOUNT_MB, vm.getCurrentRequestedRam()+"");
			Host host = HostFactory.get(profile, peList);
			hostList.add(host);
		}	
	}
	
	public void init(int userId)
	{		
		_internalCreateApplications(userId);
		_internalCreateDatacenter();
	}
	
	@Override
	public List<FederationDatacenter> createDatacenters() {
		List<FederationDatacenter> list = new ArrayList<FederationDatacenter>();
		List<Storage> storageList = new ArrayList<Storage>(); // if empty, no SAN attached
		
		// create the DCs, for now one host per DC
		for (Host h: hostList)
		{
			List<Host> tmpList = new ArrayList<Host>();
			tmpList.add(h);
			list.add(FederationDatacenterFactory.getDefault(tmpList, storageList));
		}
		
		datacenters = list;
		
		return list;
	}

	@Override
	public List<Application> createApplications(int userId, List<FederationDatacenter> datacenters) {
		List<Application> apps = new ArrayList<Application>();
		apps.add(application);
		return apps;
	}

	@Override
	public InternetEstimator createInternetEstimator(List<FederationDatacenter> datacenters) {
		InternetEstimator inetEst = new InternetEstimator(datacenters,77);
		return inetEst;
	}
	
	
}
