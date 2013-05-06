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
 * Created on Oct 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import org.eclipse.core.runtime.IAdaptable;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
/**
 * This defines a record in the rationale tree.
 * @author jburge
 *
 */

public class TreeObject implements IAdaptable {
	/**
	 * The name of the element. This is displayed in the tree
	 */
	private String name;
	/**
	 * The type of element
	 */
	private RationaleElementType type;
	/**
	 * The parent element in the tree
	 */
	private TreeParent parent;
	/**
	 * The alternative parent (if working with Candidates)
	 */
	private CandidateTreeParent altParent;
	/**
	 * The element's status
	 */
	private RationaleErrorLevel status;
	/**
	 * Indicate if the element is active
	 */
	private boolean active;
	/**
	 * The listener
	 */
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	
	/**
	 * The constructor
	 * @param name the element name
	 * @param type the element type
	 */
	public TreeObject(String name, RationaleElementType type) {
		this.name = name;
		this.type = type;
		this.status = null;
		this.active = true;
	}
	
	/**
	 * Update an element when it's name changes or it changes from active
	 * to inactive or vice versa
	 * @param name the name
	 * @param active the status
	 */
	public void update(String name, boolean active)
	{
		this.name = name;
		this.active = active;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String nm) 
	{
		name = nm;
	}
	
	public RationaleErrorLevel getStatus() {
		return status;
	}
	public RationaleElementType getType() {
		return type;
	}
	
	public void setStatus(RationaleErrorLevel stat) {
		status = stat;
	}
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	
	public void setAltParent(CandidateTreeParent parent) {
		this.altParent = parent;
	}
	public TreeParent getParent() {
		return parent;
	}
	public CandidateTreeParent getAltParent() {
		return altParent;
	}
	public void setActive(boolean act)
	{
		active = act;
	}
	
	public boolean getActive()
	{
		return active;
	}
	public String toString() {
		return getName();
	}
	
	
	/**
	 * Add a listener to our element
	 * @param listener
	 */
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	/**
	 * Remove a listener from our element
	 * @param listener
	 */
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}
	
	public Object getAdapter (Class key) {
		return null;
	}
	
}
