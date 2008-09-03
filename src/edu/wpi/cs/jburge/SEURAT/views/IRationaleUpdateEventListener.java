/*
 * Created on Apr 17, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.EventListener;

/**
 * The interface for a rationale update event. The listeners must
 * implement this interface.
 * @author jburge
 */
public interface IRationaleUpdateEventListener extends EventListener {
	public void updateRationaleTree(RationaleUpdateEvent e);
	public void showRationaleNode(RationaleUpdateEvent e);
	public void associateAlternative(RationaleUpdateEvent e);
	public void openDatabase(RationaleUpdateEvent e);
	public void updateRationaleStatus(RationaleUpdateEvent e);
	public void addNewElement(RationaleUpdateEvent e);
}
