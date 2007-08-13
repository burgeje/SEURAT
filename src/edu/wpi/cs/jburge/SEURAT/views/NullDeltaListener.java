/*
 * Created on Oct 24, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

/**
 * Taken from the Eclipse Corner article on Tree Viewers
 */

public class NullDeltaListener implements IDeltaListener {
	protected static NullDeltaListener soleInstance = new NullDeltaListener();
	public static NullDeltaListener getSoleInstance() {
		return soleInstance;
	}
	
	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {}
	
	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {}
	
}

