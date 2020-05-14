/*
Copyright 2013 2014 ISTI-CNR
 
This file is part of SmartFed.

SmartFed is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
SmartFed is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with SmartFed. If not, see <http://www.gnu.org/licenses/>.

*/

package it.cnr.isti.smartfed.test;

import it.cnr.isti.smartfed.federation.application.Application;
import it.cnr.isti.smartfed.federation.application.ApplicationVertex;
import it.cnr.isti.smartfed.federation.application.CloudletProvider;
import it.cnr.isti.smartfed.federation.resources.VmFactory.VmType;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class SimpleApplication extends Application
{
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
			this.addVertex(new ApplicationVertex(userId, cloudletList, VmType.SMALL));
		}
	}
	
	/**
	 * Application Constructor 
	 * @param userId
	 * @param vertexNumber
	 * @param customVm
	 */
	public SimpleApplication (int userId, int vertexNumber, Vm customVm)
	{
		for (int i=0; i<vertexNumber; i++){
			List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
			cloudletList.add(CloudletProvider.getDefault());
			this.addVertex(new ApplicationVertex(userId, cloudletList, customVm));
		}
	}
	
}
