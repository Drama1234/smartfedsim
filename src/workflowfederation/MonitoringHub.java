package workflowfederation;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import com.rits.cloning.Cloner;

import federation.resources.FederationDatacenter;

public class MonitoringHub extends SimEntity{
	private List<FederationDatacenter> public_view;
	private List<FederationDatacenter> internal_view;
	private List<FederationDatacenter> dcs;
	private int schedInterval_ms; // milliseconds
	
	private boolean isShutdown = false;
	
	public MonitoringHub(List<FederationDatacenter> dcs, int schedulingInterval)
	{
		super("MonitoringHub");
		this.dcs = dcs;
		this.schedInterval_ms = schedulingInterval;
		
		// schedule the event and prepare the views
		CloudSim.send(this.getId(), this.getId(), schedInterval_ms, FederationTags.MONITOR_UPDATE, null);
		internal_view = cloneList(dcs);
		public_view = internal_view;
	}
	
	public List<FederationDatacenter> getView()
	{
		return public_view;
	}
	
	@Override
	public void processEvent(SimEvent event) 
	{
		if (event.getTag() == FederationTags.MONITOR_UPDATE)
		{
			// update the view
			internal_view = cloneList(dcs);
			public_view = internal_view;
			FederationLog.timeLog(this.getName()+" received MONITOR_UPDATE ("+event.getTag()+")");
			
			// reschedule the next monitoring update event
			if (this.isShutdown == false)
				CloudSim.send(this.getId(), this.getId(), schedInterval_ms, FederationTags.MONITOR_UPDATE, null);
		}
	}

	@Override
	public void shutdownEntity() 
	{
		FederationLog.timeLogDebug(this.getName() +" is shutting down...");
		this.isShutdown = true;	
	}

	@Override
	public void startEntity() {
		FederationLog.debugLog("Monitoring hub (cloudSim id "+ getId() + ") is starting ...");
	}
	
	private List<FederationDatacenter> cloneList(List<FederationDatacenter> list)
	{
		Cloner cloner = new Cloner();
		return cloner.deepClone(list);
	}
}