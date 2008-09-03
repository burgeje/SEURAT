/*
 * Created on Apr 17, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

/**
 * The notifier is created b the trigger when the rationale update event
 * is created. 
 * @author jburge
 */
public class RationaleUpdateNotifier implements Runnable {
	
	/**
	 * The rationale update event
	 */
	RationaleUpdateEvent evt;
	/**
	 * The update event listener
	 */
	IRationaleUpdateEventListener evl;
	/**
	 * The constructor.
	 */
	public RationaleUpdateNotifier(IRationaleUpdateEventListener el, RationaleUpdateEvent ev) {
		evt = ev;
		evl = el;
	}
	/**
	 * Depending on the type of event, run the appropriate method from the listener.
	 * This could be updating the tree, bringing up the editor for a node, associating
	 * an alternative, or opening a rationale database.
	 */	
	public void run()
	{
		if (evt.getUpdateType() == UpdateType.UPDATE)
		{
			evl.updateRationaleTree(evt);
		}
		else if (evt.getUpdateType() == UpdateType.FIND) 
		{
			evl.showRationaleNode(evt);
		}
		else if (evt.getUpdateType() == UpdateType.ASSOCIATE)
		{
			evl.associateAlternative(evt);
		}
		else if (evt.getUpdateType() == UpdateType.DATABASE)
		{
			evl.openDatabase(evt);
		}
		else if (evt.getUpdateType() == UpdateType.STATUS)
		{
			evl.updateRationaleStatus(evt);
		}
		else if (evt.getUpdateType() == UpdateType.ADD)
		{
			evl.addNewElement(evt);
		}
	}
	
}
