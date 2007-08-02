/*
 * Created on Jan 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.util.*;

import org.eclipse.jface.viewers.TreeViewer;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.views.*;

/**
 * The update manager handles any updates that need to be made to the 
 * rationale tree when there is a status change. 
 * @author jburge
 */
public class UpdateManager {
	
	/**
	 * Empty Constructor
	 */
	private static UpdateManager s;
	private Vector<UpdateInformation> updatesNeeded;
	
	private TreeViewer ourTree;
	
	/**
	 * Constructor to our class. I'm not sure why this isn't a singleton...
	 * I believe the only one to call this constructor is the RationaleExplorer.
	 * The getHandle seems set up to act as if this were a singleton!
	 *
	 */
	public UpdateManager() {
		super();
		updatesNeeded = new Vector<UpdateInformation>();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Sets the tree in our update manager. This is set by the RationaleExplorer
	 * so the update manager will know who it is updating.
	 * @param viewer - the TreeViewer that displays the rationale
	 */
	public void setTree(TreeViewer viewer)
	{
		ourTree = viewer;
	}
	
	/**
	 * Returns a handle (pointer) to our UpdateManager
	 * @return the update manager.
	 */
	public static UpdateManager getHandle() {
		if (s == null)
		{
			s = new UpdateManager();
		}
		return s;
	}
	
	/**
	 * Adds a new update to the manager. This is called by the various inference
	 * classes to tell the update manager which rationale elements in the tree
	 * are going to need to have their status updated. The new status is stored
	 * in the database and will be retrieved when it is time to actually perform the
	 * update. This method merely adds an element to the list of elements that
	 * will need to be updated.
	 * @param id - the ID of the element
	 * @param name - the name of the element
	 * @param type - the type of the element
	 */
	public void addUpdate(int id, String name, RationaleElementType type)
	{
		UpdateInformation update = new UpdateInformation(id, name, type);
		if (updatesNeeded == null)
		{
			System.out.println("********null?");
		}
		updatesNeeded.addElement(update);
		
	}
	/**
	 * This is the method that actually makes the updates happen. The 
	 * status for each element in the update list is read from the database. The
	 * RationaleTreeMap is used to find out when a rationale element appears
	 * more than once in the tree (for example, a requirement is displayed in
	 * the list of requirements but can also appear as a type of argument).
	 * @return a vector of tree elements that need to be updated. This will handle any name changes. 
	 */
	public Vector<TreeObject> makeUpdates()
	{
		Vector<TreeObject> treeUpdates = new Vector<TreeObject>();
//		System.out.println("items to update = " + new Integer(updatesNeeded.size()).toString());
		RationaleDB db = RationaleDB.getHandle();
		//not sure I want this in here...
		Enumeration updateI = updatesNeeded.elements();
		while (updateI.hasMoreElements())
		{
			UpdateInformation info = (UpdateInformation) updateI.nextElement();
			//get the status information from the database
			RationaleErrorLevel error = db.getStatusLevel(info.id, info.type);
			boolean active = db.getActive(info.id, info.type);
			//now, set the new error level in the tree
			RationaleTreeMap map = RationaleTreeMap.getHandle();
			String key = map.makeKey(info.name, info.type);
//			System.out.println("Key = " + key);
			Vector<TreeObject> treeElements = map.getKeys(key);
			//now, iterate through our tree objects and set their status
			Enumeration updateT = treeElements.elements();
//			System.out.println("number of tree elements = " + new Integer(treeElements.size()).toString());
			while (updateT.hasMoreElements())
			{
				TreeObject leaf = (TreeObject) updateT.nextElement();
				leaf.setStatus(error);
				leaf.setActive(active);
//				System.out.println("set status to " + leaf.getStatus().toString() +
//				"for " + leaf.getName());
				treeUpdates.addElement(leaf);
			}
			
		}
		//now clear out our update list
		updatesNeeded.clear();
		
		return treeUpdates;
		
	}
	
	/**
	 * This is used when just updating the status in the tree
	 * based on actions taken from the task list (i.e. override)
	 */
	public void makeTreeUpdates()
	{
		if (ourTree == null)
		{
			System.out.println("Tree is not initialized");
			return;
		}
		Vector<TreeObject> treeUpdates = makeUpdates();
		//need to iterate through all the items
		Iterator treeI = treeUpdates.iterator();
		while (treeI.hasNext())
		{
			ourTree.update((TreeParent) treeI.next(), null);
		}
		
		
	}
	
	/**
	 * Get the initial status stored in the database for the list of items
	 * that we need to update. This is done after inference and before update so
	 * that the status after inferencing can be compared to the status the elements
	 * had earlier. That way, if something used to have an error and it's not in
	 * the new status list it can be removed as well as adding any new status values.
	 * @return a vector of RationaleStatus elements.
	 */
	public Vector<RationaleStatus> getInitialStatus()
	{
		Vector<RationaleStatus> curStatus = new Vector<RationaleStatus>();
		RationaleDB db = RationaleDB.getHandle();
		//not sure I want this in here...
		Iterator updateI = updatesNeeded.iterator();
		while (updateI.hasNext())
		{
			UpdateInformation info = (UpdateInformation) updateI.next();
			Vector<RationaleStatus> stat = db.getStatus(info.id, info.type);
			if (stat != null)
			{
				curStatus.addAll(stat);
			}
		}
		return curStatus;
		
	}
	
	
	/**
	 * This is a class used internally that contains the update information
	 * @author burgeje
	 *
	 */
	private class UpdateInformation {
		int id;
		String name;
		RationaleElementType type;
		
		public UpdateInformation(int id, String name, RationaleElementType type)
		{
			this.id = id;
			this.name = name;
			this.type = type;
		}
	}
	
	
}
