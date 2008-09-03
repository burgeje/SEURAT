/*
 * Created on Apr 17, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.EventObject;
import java.util.Iterator;


import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.*;

import org.eclipse.swt.widgets.Display;
import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleStatus;

/**
 * when an event occurs in one view that other views need to know about, fire
 * this is the event that gets fired off. We fire an event when a rationale element
 * is modified and we fire an event when an association is invoked from the Java
 * file (Package Explorer). We also fire an event when we remove a status override via the
 * integrated search display.
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
	 * The status element that has changed
	 */
	private RationaleStatus ourStatus;
	/**
	 * The project being associated with, if applicable
	 */
	private IProject iProject;

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
	
	public IProject getProject()
	{
		return iProject;
	}
	
	public RationaleStatus getOurStatus() {
		return ourStatus;
	}

	public void setOurStatus(RationaleStatus ourStatus) {
		this.ourStatus = ourStatus;
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
	
	/**
	 * Fire an event to open the associated database when Open Rationale is called
	 * from the Package Explorer.  This one handles IJavaProject type elements, which
	 * means it appears only for opened Java projects.
	 * 
	 * @param jEle - the java element
	 * @param disp - the display
	 * @param type - the update type
	 */
	public void fireOpenDBEvent(IJavaElement jEle, Display disp, UpdateType type){
		ourType = type;
		jElement = jEle;
		iProject = null; // so the RationaleExplorer knows which type to use
		
		trigger(disp);
	}
	
	/**
	 * Fire an event to open the associated database when Open Rationale is called
	 * from the Package Explorer, this time for an IProject type element (which is
	 * any closed project or a non-Java open project).
	 * 
	 * @param iProj- the IProject
	 * @param disp - the display
	 * @param type - the update type
	 */
	public void fireOpenDBEvent(IProject iProj, Display disp, UpdateType type){
		ourType = type;
		iProject = iProj;
		jElement = null; // so the RationaleExplorer knows which type to use
		ourStatus = null;
		
		trigger(disp);
	}
	
	/**
	 * Fire an event to update the Rationale Task List and the Rationale Explorer when a
	 * status override is removed
	 * 
	 * @param status - the Status change
	 * @param disp - the display
	 * @param type - the update type
	 */
	public void fireStatusUpdateEvent(RationaleStatus status, Display disp, UpdateType type)
	{
		ourType = type;
		ourStatus = status;
		jElement = null;
		iProject = null;
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
