/*
 * Created on Jul 11, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.actions;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import edu.wpi.cs.jburge.SEURAT.decorators.SEURATDecoratorManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * @author jburge
 *
 * This class removes associations with the code. In this case, the item selected is the item from 
 * the Java Package Explorer and all alternatives associated with the resource will be unassociated. This is
 * invoked via a pop-up menu extension on the Bookmark View.
 * <p>
 * This currently is not working - the menu item is not showing up for unknown reasons.
 */
public class RemoveBookmarkAssociation extends ActionDelegate implements IViewActionDelegate {
	
	/**
	 * The selected item in the Java Package explorer
	 */
	IStructuredSelection selection;
	
	/**
	 * Update the selection if it changes
	 * @param action - not used
	 * @param selection - the new selected item from the Package Explorer
	 */		
	public void selectionChanged (IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
		
	}
	/**
	 * Run the action when requested from the popup menu
	 */
	public void run (IAction action) {
		System.out.println("found our action!");
		// This seems to think that our marker will always be the first element - correct?
		if (selection.getFirstElement() instanceof IMarker)
		{
			IMarker ourMarker = (IMarker) selection.getFirstElement();
			try {
				if (ourMarker.getType().compareTo("SEURAT.ratmarker") == 0)
				{
					IResource ourResource = ourMarker.getResource();
					/*					boolean proceed = showQuestion("Do you want to delete all associations and bookmarks for this resource (file)?");
					 if (!proceed)
					 {
					 return;
					 } */
					//remove the association from the database
					RationaleDB d = RationaleDB.getHandle();
					d.removeAssociation(ourResource.getName(), (String) ourMarker.getAttribute(IMarker.MESSAGE), ourMarker.getAttribute(IMarker.LINE_NUMBER, 0));
					//first, delete this marker
					ourMarker.delete();
					//next, find out if we have other markers
//					ourResource.deleteMarkers("SEURAT.ratmarker", true, IResource.DEPTH_INFINITE);
//					ourResource.setPersistentProperty()
					IMarker[] allMarkers = ourResource.findMarkers("SEURAT.ratmarker", true, IResource.DEPTH_INFINITE);
					if (allMarkers.length == 0)
					{
						SEURATResourcePropertiesManager.addPersistentProperty (ourResource,
								"Rat", "false");
						SEURATDecoratorManager.addSuccessResources (ourResource);
					}
					else
					{
						System.out.println("still markers left...");
					}
					
				}
			}
			catch (Exception exp)
			{
				System.out.println("exception removing marker");
				System.out.println(exp);
			}
			
			
			
		}
//		***		System.out.println(selection.getFirstElement().getClass());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}
	
	
}
