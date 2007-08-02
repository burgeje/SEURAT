/*
 * Created on Apr 17, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.EventObject;
import java.util.Iterator;


import org.eclipse.jdt.core.*;

import org.eclipse.swt.widgets.Display;
import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;

/**
 * when an event occurs in one view that other views need to know about, fire
 * this is the event that gets fired off. We fire an event when a rationale element
 * is modified and we fire an event when an association is invoked from the Java
 * file (Package Explorer).
 * @author jburge
 */
public class RationaleUpdateEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1894221614191445361L;
	/**
	 * The rationale element
	 */
	private RationaleElement element;
	/**
	 * A pointer to the display
	 */
	private Display ourDisplay;
	/**
	 * The type of update being performed
	 */
	private UpdateType ourType;
	/**
	 * The java element being associated with
	 */
	private IJavaElement jElement;
	/**
	 * Constructor
	 */
	public RationaleUpdateEvent(Object source) {
		super(source);
		
	}
	
	public RationaleElement getRationaleElement()
	{
		return element;
	}
	
	public Display getDisplay()
	{
		return ourDisplay;
	}
	
	public UpdateType getUpdateType()
	{
		return ourType;
	}
	
	public IJavaElement getIJavaElement()
	{
		return jElement;
	}
	
	/**
	 * Fire off an update event
	 * @param ele the rationale element involved
	 * @param disp the display
	 * @param type the update type
	 */
	public void fireUpdateEvent(RationaleElement ele, Display disp, UpdateType type)
	{
		element = ele;
		ourType = type; 
		
		trigger(disp);
		
	}
	
	//this is a event for the association from the Java file direction
	//need the attribute of type IJavaElement
	/**
	 * Fire an association event when the associate menu item is invoked from the
	 * Package explorer
	 * @param jEle - the java element
	 * @param disp - the display
	 * @param type - the update type
	 */
	public void fireAssociateEvent (IJavaElement jEle, Display disp, UpdateType type){
		ourType = type;
		jElement = jEle;
		
		trigger(disp);
	}
	
	//this is a method for triggering the RationaleUpdateNotifier
	/**
	 * Trigger the RationaleUpdateNotifier
	 * @param disp the display
	 */
	private void trigger (Display disp){
		SEURATPlugin plugin = SEURATPlugin.getDefault();
		Iterator i = plugin.getUpdateListenerI();
		while (i.hasNext())
		{
			RationaleUpdateNotifier srn = new RationaleUpdateNotifier((IRationaleUpdateEventListener)i.next(), this);
			disp.syncExec(srn);
		}
	}
	
}
