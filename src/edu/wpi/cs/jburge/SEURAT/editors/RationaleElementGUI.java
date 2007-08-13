/*
 * Created on Apr 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;


import java.awt.*;
import java.io.Serializable;

/**
 * Not used. Perhapse a pre-cursor to "NewRationaleElementGUi"?
 */
public class RationaleElementGUI extends Dialog implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1331449023774558576L;
	protected boolean canceled;
	
	public RationaleElementGUI (Frame parent, String str, boolean bl)
	{
		super(parent, str, bl);
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
