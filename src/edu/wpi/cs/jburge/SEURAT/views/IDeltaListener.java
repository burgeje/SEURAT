/*
 * Created on Oct 24, 2003
 *
 */
package edu.wpi.cs.jburge.SEURAT.views;

/**
 * Taken from sample code for the Eclipse Corner article on tree viewers
 */

public interface IDeltaListener {
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
