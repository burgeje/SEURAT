/*	This code belongs to the SEURAT project as written by Dr. Janet Burge
    Copyright (C) 2013  Janet Burge

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
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
