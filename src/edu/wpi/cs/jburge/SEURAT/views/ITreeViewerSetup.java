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

package edu.wpi.cs.jburge.SEURAT.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;

public class ITreeViewerSetup implements IDeltaListener, ITreeContentProvider {
	/**
	 * This was created by Jon Wright, Team 2
	 */
	
	protected TreeViewer view;
	
	/**
	 * This method must be overridden in the child class.
	 */
	public Object[] getElements(Object parent) {
		return null;
	}
	
	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 * <p>
	 * A typical use for this method is registering the content provider as a listener
	 * to changes on the new input (using model-specific means), and deregistering the viewer 
	 * from the old input. In response to these change notifications, the content provider
	 * propagates the changes to the viewer.
	 * </p>
	 *
	 * @param viewer the viewer
	 * @param oldInput the old input element, or <code>null</code> if the viewer
	 *   did not previously have an input
	 * @param newInput the new input element, or <code>null</code> if the viewer
	 *   does not have an input
	 */
	public void inputChanged(Viewer viewer, Object obj1, Object obj2) {
		this.view = (TreeViewer)viewer;
		if (!(obj1 instanceof TreeParent)) 
			return;
		
		if(obj1 != null) {
			removeListenerFrom((TreeParent)obj1);
		}
		if(obj2 != null) {
			addListenerTo((TreeParent)obj2);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child element of the given element.
	 * @param element - the tree element
	 */
	protected void removeListenerFrom(TreeParent element) {
		element.removeListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent nextEl = (TreeParent) iterator.next();
			removeListenerFrom(nextEl);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child element
	 * @param element - the tree element
	 * 
	 */
	protected void addListenerTo(TreeParent element) {
		element.addListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent aElement = (TreeParent) iterator.next();
			addListenerTo(aElement);
		}
	}
	
	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object ourObj = ((TreeParent)event.receiver()).getParent();
		view.refresh(ourObj, false);
	}
	
	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
		add(event);
	}
	
	public void dispose() {
		
	}
	
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
	public Object [] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return new Object[0];
	}
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
	
	/**
	 * Add a new element to the tree
	 * @param parent the parent node
	 * @param ele the element
	 * @return the new tree element
	 */
	public TreeParent addNewElement(TreeParent parent, RationaleElement ele) {
		TreeParent child = new TreeParent(ele.getName(), ele.getElementType());
		child.setActive(ele.getEnabled());
		parent.addChild(child);
		return child;
	}
	
	/**
	 * Remove an element from the tree and all its children
	 * @param child the element being removed
	 */
	public void removeElement(TreeParent child) {
		TreeParent parent = child.getParent();
		//first, remove all the children of the child
		child.removeChildren();
		//not removing in an iterator
		parent.removeChild(child, false);
	}
	
}
