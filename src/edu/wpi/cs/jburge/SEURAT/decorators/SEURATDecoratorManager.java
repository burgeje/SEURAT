/*	This code belongs to the SEURAT project as written by Dr. Janet Burge
    Copyright (C) 2013  Janet Burge

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package edu.wpi.cs.jburge.SEURAT.decorators;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;


/**
 * This class is used as a manager to organize the resources that need to 
 * be decorated. This code comes from one of the Eclipse examples. For SEURAT,
 * the only decorator used is the little rat icon that indicates if resources (files) in
 * the Package Explorer have rationale associated with them.
 * 
 * @author balajik
 *

 */
public class SEURATDecoratorManager 
{
	/**
	 * The resources that are to be decorated
	 */ 
	private static List<IResource> resourcesToBeUpdated_ = new Vector<IResource>();
	
	/**
	 * Constructor for DemoDecoratorManager.
	 */
	public SEURATDecoratorManager() 
	{
	}
	
	public static List getSuccessResources ()
	{
		return resourcesToBeUpdated_;
	}
	
	public static void addSuccessResources (List<IResource> successResourceList)
	{
		resourcesToBeUpdated_ = new Vector<IResource>();
		resourcesToBeUpdated_.addAll(successResourceList); 
	}
	
	public static void appendSuccessResources (List<IResource> successResourceList)
	{
		resourcesToBeUpdated_.addAll(successResourceList); 
	}
	
	public static void addSuccessResources (IResource resource)
	{
		resourcesToBeUpdated_.add(resource);
//		System.out.println("added our new resource");
	}
	
	public static boolean contains (IResource resource)
	{
		return resourcesToBeUpdated_.contains (resource);
	}
	
	public static void removeResource (IResource resource)
	{
		if (resourcesToBeUpdated_.contains (resource))
		{
			resourcesToBeUpdated_.remove (resource);
//			System.out.println("removed resource");
		}
	}
	
	/*
	 private static void printSuccessResources()
	 {
	 Iterator<IResource> i = resourcesToBeUpdated_.iterator();
	 //	System.out.println("The resources that are updated ");
	  while (i.hasNext())
	  {
	  IResource resource = (IResource) i.next();
	  //	  System.out.println(resource.getName()); 
	   }
	   }*/
	
}
