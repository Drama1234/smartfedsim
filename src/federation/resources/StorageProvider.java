package federation.resources;

import org.cloudbus.cloudsim.Storage;

import federation.resources.StorageProfile.StorageParams;



public class StorageProvider {
	public Storage createStorage(StorageProfile profile) {
		Storage storage = null;
		
		try
		{
			Class clazz = Class.forName(profile.get(StorageParams.CLASS));
			Double capacity = Double.parseDouble(profile.get(StorageParams.CAPACITY));
			storage = (Storage)clazz.getDeclaredConstructor(Double.class).newInstance(capacity);
		}
		catch (Exception e)
		{
			// TODO: log the error
			e.printStackTrace();
		}
		
		return storage;
	}
}
