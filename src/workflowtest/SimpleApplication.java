package workflowtest;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import application.Application;
import application.ApplicationVertex;
import application.CloudletProvider;
import federation.resources.VmFactory.vmType;


public class SimpleApplication extends Application{
	/**
	 * Application with a vertex containing a single VM 
	 * @param userId
	 */
	public SimpleApplication(int userId)
	{
		this(userId, 1);
	}
	
	/**
	 * Creates one cloudlet for each vertex. VertexNumber is given as parameter.
	 * @param userId
	 * @param vertexNumber
	 */
	public SimpleApplication (int userId, int vertexNumber)
	{
		for (int i=0; i<vertexNumber; i++){
			List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
			cloudletList.add(CloudletProvider.getDefault());
			this.addVertex(new ApplicationVertex(userId, cloudletList, vmType.SMALL));
		}
	}
	/**
	 * Creates one cloudlet for each vertex. VertexNumber is given as parameter.
	 * @param userId
	 * @param vertexNumber
	 */
	public SimpleApplication (int userId, int vertexNumber, Vm customVm)
	{
		for (int i=0; i<vertexNumber; i++){
			List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
			cloudletList.add(CloudletProvider.getDefault());
			this.addVertex(new ApplicationVertex(userId, cloudletList,customVm));
		}
	}

}
