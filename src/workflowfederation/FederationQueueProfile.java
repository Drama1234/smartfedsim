package workflowfederation;

import java.util.HashMap;
import java.util.Map;


public class FederationQueueProfile {
	private static final int DEFAULT_LENGTH = 10;
	private static final String DEFAULT_MODEL = "application.interarrival.NormalModel";

	public enum QueueParams{
		INTER_ARRIVAL_MODEL(DEFAULT_MODEL),
		INTER_ARRIVAL_PARAMS(""),
		LENGTH(new Integer(DEFAULT_LENGTH).toString());
		
		private String def;		
		private QueueParams(String def)
		{
             this.def = def;
		}
	}
	
	private  Map<QueueParams, String> data;
	
	private FederationQueueProfile()
	{
		data = new HashMap<QueueParams, String>();
		for (QueueParams p : QueueParams.values())
		{
			data.put(p, p.def);
		}
	}

	public static FederationQueueProfile getDefault()
	{
		return new FederationQueueProfile();
	}
	
	public String get(QueueParams par)
	{
		return data.get(par);
	}
	
	public void set(QueueParams par, String value)
	{
		data.put(par, value);
	}
}
