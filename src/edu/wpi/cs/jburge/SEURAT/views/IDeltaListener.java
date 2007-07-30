/*
 * Created on Oct 24, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

/**
 * @author jburge
 *
* Taken from sampe code for the Eclipse Corner article on tree viewers
 */

public interface IDeltaListener {
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
