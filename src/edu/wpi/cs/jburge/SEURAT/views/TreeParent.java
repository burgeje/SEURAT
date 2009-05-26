/*
 * Created on Oct 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * This uses the TreeObjects that describe each node in the tree to form
 * the actual tree. This is a bit more complicated because as the tree
 * is updated we also keep track of a "Tree Map" that can be used to
 * map a rationale element to every place that it occurs in the tree. This
 * is because some elements appear on their own and in arguments.
 * @author jburge
 *
 */
public class TreeParent extends TreeObject {
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
	public TreeParent(String name, RationaleElementType type) {
		super(name, type);
		children = new ArrayList<TreeObject>();
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		String key = map.makeKey(this.getName(),this.getType()); 
		map.addItem(key, this); 
	}
	
	public ArrayList<TreeObject> getChildrenList(){
		return this.children;
	}
	
	/**
	 * Add a new child to the tree
	 * @param child the new child
	 */
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
		
	}
	/**
	 * Remove a child from the map. If this is not being done inside an iterator,
	 * remove it from the tree as well.
	 * @param child the child from the tree
	 * @param iterated true if called from an iterator
	 */
	public void removeChild(TreeObject child, boolean iterated) {
		//remove from the tree map
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		String key = map.makeKey(child.getName(),child.getType()); 
		map.removeItem(key, child);
		if (!iterated)
		{
			//remove from our list of children
			children.remove(child);
			child.setParent(null);
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
			TreeParent child = (TreeParent) treeI.next();
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
