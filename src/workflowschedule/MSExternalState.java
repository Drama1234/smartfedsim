package workflowschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import application.Application;
import federation.resources.FederationDatacenter;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSApplication;
import it.cnr.isti.smartfed.metascheduler.resources.iface.IMSProvider;
import workflownetworking.InternetEstimator;
import workflowschedule.iface.MSApplicationUtility;
import workflowschedule.iface.MSProviderAdapter;

public class MSExternalState {
	private IMSApplication _application;
	private List<IMSProvider> _providers;
	private InternetEstimator _internet;
	
	public MSExternalState(Application application, List<FederationDatacenter> providers, InternetEstimator internet) {
		this._internet = internet;
		this._application = MSApplicationUtility.getMSApplication(application);
		
		/*
		 * Fill and sort provider list
		 */
		this._providers = new ArrayList<IMSProvider>();
		for (FederationDatacenter fd: providers)
		{
			IMSProvider newp = MSProviderAdapter.datacenterToMSProvider(fd);
			this._providers.add(newp);
		}
		
//		// ascending sort by datacenter id
//		Collections.sort(this._providers, new Comparator<IMSProvider>() {
//			@Override
//			public int compare(IMSProvider first, IMSProvider second) {
//				if (first.getID() > second.getID()) 
//					return 1; //greater
//				else if (first.getID() < second.getID())
//					return -1; //smaller
//				return 0; // equal
//			}
//		});
	}
	
	public IMSApplication getApplication() {
		return _application;
	}

	public List<IMSProvider> getProviders() {
		return _providers;
	}

	public InternetEstimator getInternet() {
		return _internet;
	}
}
