package workflowfederation;

import java.util.List;

import application.Application;
import application.interarrival.InterArrivalModelItf;
import workflowfederation.FederationQueueProfile.QueueParams;


public class FederationQueueProvider {
	public static FederationQueue createFederationQueue(FederationQueueProfile profile, Federation federation, 
			List<Application> applications){
		
		Application[] apps_array = new Application[applications.size()];
		applications.toArray(apps_array);
		
		String arrivalModelName = profile.get(QueueParams.INTER_ARRIVAL_MODEL);
		String interval = profile.get(QueueParams.INTER_ARRIVAL_PARAMS); 
		FederationQueue fq = null;
		
		try {
			
			InterArrivalModelItf arrivalModel = (InterArrivalModelItf) Class.forName(arrivalModelName).newInstance();
			Object[] ret = arrivalModel.getSchedulingTime(apps_array, interval);
//			System.out.println("时间戳:"+ret[0]);
			fq = new FederationQueue(federation, ret);
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return fq;
	}
	
	public static FederationQueue getFederationQueue(FederationQueueProfile profile, Federation federation, List<Application> applications){
		return createFederationQueue(profile, federation, applications);
	}
}
