package application.interarrival;

import application.Application;

public class NormalModel implements InterArrivalModelItf{
	private final long intervalAmongJobs = 100;

	@Override
	public Object[] getSchedulingTime(Application[] applications, String... params) {
		// TODO Auto-generated method stub
		
		long interval;
		
		if (params.length != 0 && !params[0].equals("")){
			interval = Long.parseLong(params[0]);
		} else {
			interval = intervalAmongJobs;
		}
		
		long[] timestamps = new long[applications.length];
		long currentTimestamp = 0;
		
		return null;
	}

}
