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
 * Created on Jan 4, 2008
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * This uses the TreeObjects that describe each node in the tree to form
 * the tree of candidate rationale. This is different from TreeParent (which it
 * was copied from) by not having any mapping - this is because we are not
 * doing anything complicated with the candidates and we DON'T want to risk
 * mucking up the real rationale tree map!
 * @author jburge
 *
 */
public class CandidateTreeParent extends TreeObject {
	/**
	 * All the children of this tree element
	 */
	private ArrayList<TreeObject> children;
	
	/**
	 * The constructor. Creates a list of children, adds the new
	 * TreeParent to the Tree Map.
	 * @param name the name
	 * @param type the type of element
	 */
	public CandidateTreeParent(String name, RationaleElementType type) {
		super(name, type);
		children = new ArrayList<TreeObject>();
	}
	
	/**
	 * Add a new child to the tree
	 * @param child the new child
	 */
	public void addChild(TreeObject child) {
		children.add(child);
		child.setAltParent(this);
		
	}
	/**
	 * If this is not being done inside an iterator,
	 * remove it from the tree as well.
	 * @param child the child from the tree
	 * @param iterated true if called from an iterator
	 */
	public void removeChild(TreeObject child, boolean iterated) {
		//remove from the tree map
		if (!iterated)
		{
			//remove from our list of children
			children.remove(child);
			child.setAltParent(null);
		}
		
	}
	
	/**
	 * Removing all the children of this parent.
	 *
	 */
	public void removeChildren()
	{
		Iterator treeI = this.getIterator();
		while (treeI.hasNext())
		{
			CandidateTreeParent child = (CandidateTreeParent) treeI.next();
			child.removeChildren();
			removeChild(child, true);
			//the remove MUST be done via the iterator, otherwise there
			//will be a concurrent modification exception
			treeI.remove();
		}
	}
	public TreeObject [] getChildren() {
		return (TreeObject [])children.toArray(new TreeObject[children.size()]);
	}
	
	public Iterator getIterator()
	{
		return children.iterator();
	}
	/**
	 * Check if this tree parent has any children
	 * @return true if children exist
	 */
	public boolean hasChildren() {
		return children.size()>0;
	}
	
}
