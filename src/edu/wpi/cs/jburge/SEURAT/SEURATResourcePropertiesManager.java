package edu.wpi.cs.jburge.SEURAT;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;

import edu.wpi.cs.jburge.SEURAT.views.TreeObject;

/**
 * @author balajik
 *
 * Utility class to add, modify, get the persistent property of the
 * resource. Taken from demo code and modified for SEURAT.
 *
 */
public class SEURATResourcePropertiesManager
{
 /**
  * Constructor for SEURATResourcePersistentProperty.
  */
  public SEURATResourcePropertiesManager()
  {
  }
  
  /**
   * Get the Busy status of the resource. In this case, busy
   * indicates if the resource has rationale
   * 
   * @param resource IResource
   * 
   * @return true if the resource has rationale
   * @return false if the resource does not have rationale
   * 
   */ 
  private static boolean hasRationale(IResource resource)
  {
	String ratValue;
//***	System.out.println("Checking for rationale");
	// Get the Busy Qualified Name
	QualifiedName ratQualifiedName = SEURATPersistentPropertyTypes.
	  getInstance().getRatQualifiedName();
	try
	{
	  // Get the Rationale Value 
	  ratValue = resource.getPersistentProperty(ratQualifiedName);
	}
	catch(Exception e)
	{
	  e.printStackTrace();
	  return false;
	}
	if (ratValue != null && ratValue.equals("true"))
	{
		  return true;
	}
	
	return false;
  }
  
/**
 * This code was originally used to see if our rationale element had
 * any errors associated with it but it turns out that you can't add a
 * decorator to anything other than a resource (a file) this code does
 * nothing.
 * @param resource our TreeObject
 * @return 
 */
  private static boolean hasErrors(TreeObject resource)
	{
	  String ratValue;
//***	  System.out.println("Checking for errors");
	  // Get the Busy Qualified Name
	//  QualifiedName errorQualifiedName = SEURATPersistentPropertyTypes.
	//	getInstance().getErrorQualifiedName();
	  try
	  {
		// Get the Rationale Value 
		ratValue = "Error"; //test me
	  }
	  catch(Exception e)
	  {
		e.printStackTrace();
		return false;
	  }
	  if (ratValue != null && ratValue.equals("true"))
	  {
			return true;
	  }
	
	  return false;
	}
  /**
   * Generic method to add persistent properties for the resource
   * 
   * @param resource IResource object
   * @param localName qualifier name of the persistent property
   * @param value indicate the value of the property
   * 
   */  
  public static void addPersistentProperty (IResource resource, 
											 String localName,
											 String value)    
  {
	// Get the correct Qualified Name
	QualifiedName qName = SEURATPersistentPropertyTypes.getInstance().
	  getQualifiedName(localName);
    
	try
	{
	  resource.setPersistentProperty(qName, value);
	}
	// Catch Exception
	catch(Exception e)
	{
	  e.printStackTrace();
	  System.out.println(e.getMessage());
	}
  }
  
  /**
   * Add persistent properties for a resource list
   * 
   * @param resourceList List of IResource objects
   * @param localName qualifier name of the persistent property
   * @param value  to indicate the value of the property
   * 
   */  
  public static void addPersistentProperty (List resourceList, 
											 String localName,
											 String value)    
  {
	// Get the correct Qualified Name
	QualifiedName qName = SEURATPersistentPropertyTypes.getInstance().
	  getQualifiedName(localName);
      
	IResource resource;
	Iterator i = resourceList.iterator();
    
	while (i.hasNext())
	{
	  resource = (IResource) i.next();
	  try
	  {
		resource.setPersistentProperty(qName, value);
	  }
	  // Catch Exception
	  catch(Exception e)
	  {
		e.printStackTrace();
		System.out.println(e.getMessage());
	  }
	}
  }
  
  /**
   * Find the decorator image for the resource.
   * 
   * @param resource IResource Object
   * 
   * @return the image decoration as List. For eg: If the file has a read
   * only property set, it returns a list with Lock as the only element 
   */ 
  public static Vector findDecorationImageForResource (IResource resource)
  {
	Vector<String> qualifiedValue = new Vector<String>();
	String value ;
	if (hasRationale(resource))
	{
	  value = "Rat";
//***	  System.out.println("Has rationale");
	  qualifiedValue.add(value);
	}
	return qualifiedValue;
  }
 
  /**
   * This originally was supposed to be used to add a decorator to the 
   * rationale tree elements however, it turns out you can only add an
   * element to a resource.
   * @param resource
   * @return
   */
  public static Vector findDecorationImageForRationale (TreeObject resource)
  {
	Vector<String> qualifiedValue = new Vector<String>();
	String value ;
	if (hasErrors(resource))
	{
	  value = "Error";
	  System.out.println("Has errors");
	  qualifiedValue.add(value);
	}
/*	else if (hasWarnings(resource))
	{
		value = "Warning";
		System.out.println("Has warnings");
		qualifiedValue.add(value);
	} */
	return qualifiedValue;
  } 
  
   
}
