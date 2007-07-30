package edu.wpi.cs.jburge.SEURAT;

/**
 * @author Balaji
 *
 * Utiltity Class to store the label decoration preference. This should
 * be enhanced by users so that the values are persistent across sessions. This code 
 * came from an Eclipse demo.
 */
public class SEURATStore
{
  private static SEURATStore instance_ = null;
  
  private boolean displayTextLabelInformation_ = true;
  private boolean displayProjectName_ = true;

	/**
	 * Constructor for DemoStore.
	 */
	private SEURATStore()
	{
		super();
	}

  public static SEURATStore getInstance()
  {
	if (instance_ == null)
	{
	  instance_ = new SEURATStore();
	}
	return instance_;
  }
  
  /**
   * Function to determine whether the File Label prefix / suffix should be 
   * displayed
   * 
   * @return true owner name should be displayed
   *          false otherwise 
   */ 
  public boolean shouldDisplayTextLabel()
  {
	return displayTextLabelInformation_;
  }
  
  
  /**
   * Set values for boolean flag to display prefix/suffix
   */ 
  public void setDisplayTextLabel (boolean value)
  {
	displayTextLabelInformation_ = value;
  }
  
  /**
   * Display Project label decorator or not
   */ 
  public boolean shouldDisplayProject ()
  {
	return displayProjectName_;
  }
  
  public void setDisplayProject (boolean value)
  {
	displayProjectName_ = value;
  }
}
