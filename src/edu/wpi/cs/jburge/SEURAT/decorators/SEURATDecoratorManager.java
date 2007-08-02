package edu.wpi.cs.jburge.SEURAT.decorators;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;


/**
 * @author balajik
 *
 * This class is used as a manager to organize the resources that need to 
 * be decorated. This code comes from one of the Eclipse examples. For SEURAT,
 * the only decorator used is the little rat icon that indicates if resources (files) in
 * the Package Explorer have rationale associated with them.
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
