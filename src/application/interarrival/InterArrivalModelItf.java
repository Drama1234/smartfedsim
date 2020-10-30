package application.interarrival;

import application.Application;

public interface InterArrivalModelItf {
	public Object[] getSchedulingTime(Application[] applications, String ...params);
}
