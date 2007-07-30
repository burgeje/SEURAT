package edu.wpi.cs.jburge.SEURAT;

import org.eclipse.core.runtime.QualifiedName;

/**
 * Utility class to maintain persistent property names and 
 * assosiated Qualified names. This was taken from the DemoResourcePersistantProperty
 * class and modified for SEURAT.
 * @author balajik
 *

 *
 */
public class SEURATPersistentPropertyTypes
{
/**
 * The static instance.
 */
  private static SEURATPersistentPropertyTypes instance_;
  
 /**
  * Rationale Indicator Qualified Name 
  */ 
  private QualifiedName ratQualifiedName_;
  
  /**
   * Rationale Status Qualified Name
   */ 
  private QualifiedName errorQualifiedName_;
  
  private QualifiedName warningQualifiedName_;
  
  /**
   * Constructor for SEURATResourcePersistentProperty.
   */
  private SEURATPersistentPropertyTypes()
  {
	// Allocate memory for all the qualified name 
	ratQualifiedName_ = new QualifiedName("RationaleDecorator", "Rat");
	errorQualifiedName_ = new QualifiedName("RationaleDecorator", "Error");
	warningQualifiedName_ = new QualifiedName("RationaleDecorator", "Warning");
  }
  
  public static SEURATPersistentPropertyTypes getInstance()
  {
	if (instance_ == null)
	{
	  instance_ = new SEURATPersistentPropertyTypes();
	}
	return instance_;
  }
  
  /**
   * Get the Rationale Qualified name 
   */ 
  public QualifiedName getRatQualifiedName()
  {
	return ratQualifiedName_;
  }
  
  /**
   * Get the Status qualified name
   */ 
  public QualifiedName getErrorQualifiedName()
  {
	return errorQualifiedName_;
  }
  
  public QualifiedName getWarningQualifiedName()
  {
  	return warningQualifiedName_;
  }
  
 
  /**
   * Get the qualified name given the local name
   * 
   * @param localName local name of the qualified name
   * @return Qualified Name
   * 
   */ 
  public QualifiedName getQualifiedName(String localName)
  {
	if(localName.equals("Rat"))
	{
	  return ratQualifiedName_;
	}
	else
	{
	  if(localName.equals("Error"))
	  { 
		return errorQualifiedName_;
	  }
	  else 
	  {
	  	if (localName.equals("Warning"))
	  	{
		  	return warningQualifiedName_;
	  	}
	  }
	  
	}
	return null;
  }
}
