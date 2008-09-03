package edu.wpi.cs.jburge.SEURAT.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

import org.eclipse.swt.widgets.Display;

import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;


/**
 * This class is used to open the SEURAT rationale database associated with
 * an Eclipse project. This is done via an extension to the Eclipse popup menus, 
 * defined in the plugin.xml file.
 * 
 * @author molerjc
 */
public class OpenRationale extends ActionDelegate implements IObjectActionDelegate {
	
	/**
	 * The item that has been selected in the Package Explorer
	 */
	IStructuredSelection selection;
	
	/**
	 * Detects when the selected item has changed. In this case, the selected
	 * item refers to the element in the Package Explorer.
	 * @param action - the action
	 * @param selection - the selection
	 */
	public void selectionChanged (IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
	/**
	 * Executes the action.  We get the selected element and use it to create
	 * a rationale update event to kick off the association.
	 * @param action - the action
	 */	
	public void run (IAction action) 
	{
		if (selection.getFirstElement() instanceof IJavaProject) {
			IJavaProject proj = (IJavaProject) selection.getFirstElement();
			RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
			evt.fireOpenDBEvent(proj, Display.getDefault(), UpdateType.DATABASE);
		}
		/* This part must be included for rationale DBs to be opened for projects
		 * that are currently closed or that are non-Java projects.  This may or
		 * may not be desirable, but the code is easy to remove if not wanted.
		 */
		else if (selection.getFirstElement() instanceof IProject) {
			IProject proj = (IProject) selection.getFirstElement();
			RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
			evt.fireOpenDBEvent(proj, Display.getDefault(), UpdateType.DATABASE);
		}
	}
	
	/**
	 * The constructor- just constructs an ActionDelegate.
	 */
	public OpenRationale() {
		super();
	}
	
	/**
	 * This method is not needed or used for the Open Rationale function,
	 * but had to be specified due to the extended class.
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}
}

