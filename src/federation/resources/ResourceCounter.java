package federation.resources;


public class ResourceCounter {
	private static int HOST_ID = 0;
	private static int VM_ID = 0;
	private static int CLOUDLET_ID = 0;
	private static int DATACENTER_ID = 0;
//	private static int a=0,b=0,c=0,d=0,e=0,f=0;
	
	public static int nextCloudletID()
	{
		return CLOUDLET_ID++;
	}
	
	public static int nextVmID()
	{
		return VM_ID++;
	}
	
	public static int nextHostID()
	{
		return HOST_ID++;
	}
	
	public static int nextDatacenterID() {
		return DATACENTER_ID++;
	}
	
//	public static int nextDatacenterID(City city)
//	{
//		switch (city) {
//		case Beijing:{
//			a++;
//			return a;
//		}
//		case Hongkong:{
//			b++;
//			return b;
//		}
//		case Frankfurt:{
//			c++;
//			return c;
//		}
//		case Stockholm:{
//			d++;
//			return d;
//		}
//		case LosAngeles:{
//			e++;
//			return e;
//		}
//		case NewYork:{
//			f++;
//			return f;
//		}
//		default:
//			return -1;
//		}
//	}
	
	public static void reset()
	{
		HOST_ID = 0;
		VM_ID = 0;
		CLOUDLET_ID = 0;
		DATACENTER_ID = 0;
	}	
}
