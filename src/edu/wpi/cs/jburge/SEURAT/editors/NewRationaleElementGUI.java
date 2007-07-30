/*
 * Created on Apr 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

import java.io.Serializable;

/**
 * @author jburge
 *
 * This is the superclass for all the other rationale element editors. This
 * should never be instantiated directly.
 */
public class NewRationaleElementGUI implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Indicates if editing has been cancelled.
	 */
	protected boolean canceled;
	
	public NewRationaleElementGUI ()
	{
		canceled = false;
	}
	
	public RationaleElement getItem()
	{
		return null; //this should not get called...
	}
    
	public boolean getCanceled()
	{
		return canceled;
	}
}
