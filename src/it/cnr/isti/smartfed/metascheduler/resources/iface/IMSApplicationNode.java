package it.cnr.isti.smartfed.metascheduler.resources.iface;
import org.jgap.IApplicationData;

public interface IMSApplicationNode extends IApplicationData{
	public void setID(int ID);
	public int getID();
	
	public void setBudget(double budget);
	public double getBudget();
	
	public void setProviderId(int providerId);
	public int getProviderId();	
}
