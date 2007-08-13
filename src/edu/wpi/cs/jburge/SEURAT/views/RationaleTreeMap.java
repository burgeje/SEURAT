/*
 * Created on Jan 12, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.HashMap;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * This is a structure designed to provide an alternative mapping into the
 * RationaleTree. Basically, we need an index to be able to get all
 * the handles for a specific rationale item in the tree. This is used when
 * we need to update the status displayed for an item in the tree. One rationale
 * element could appear in many places in the tree. For example, a requirement would
 * be shown under requirements but would also appear under any arguments that
 * reference it.
 * There are two types of status changes needed:
 * -- the icon may end up showing a different error status
 * -- the name of the rationale item might change
 * -- an item might be deleted!
 */
public final class RationaleTreeMap {
	
	/**
	 * The maximum size
	 */
	private final int COUNT = 250;
	/**
	 * Our static instsance This is a SINGLETON design pattern
	 */
	private static RationaleTreeMap s;
	/**
	 * The hash map that is used to find where related items are in the tree 
	 */
	private HashMap<String, Vector<TreeObject>> keys = new HashMap<String, Vector<TreeObject>>(COUNT);
	
	/**
	 * Constructor - this is supposed to be private to enforce the
	 * singleton design pattern.
	 *
	 */
	private RationaleTreeMap() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Get the handle to our static instance
	 * @return a pointer to the static instance of this class
	 */
	public static RationaleTreeMap getHandle() {
		if (s == null)
		{
			s = new RationaleTreeMap();
		}
		return s;
	}
	
	/**
	 * Clear out the map
	 *
	 */
	public void clearMap() {
		keys.clear();
	}
	
	/**
	 * Get a vector of tree objects that need to be updated
	 * @param key the key we are looking for
	 * @return the tree objects
	 */
	public Vector<TreeObject> getKeys(String key)
	{
		Vector<TreeObject> listItems = null;
		if (keys.get(key) == null)
		{
			listItems = new Vector<TreeObject>();
			keys.put(key, listItems);
		}
		else
		{
			listItems = keys.get(key);
		}
		return listItems;
		
	}
	
	/** 
	 * Add a new item to the map
	 * @param keykey the item's key
	 * @param leaf the tree object to store in the map
	 */
	public void addItem(String keykey, TreeObject leaf)
	{
		Vector<TreeObject> listItems;
		listItems = this.getKeys(keykey);
		if (listItems == null)
			System.out.println("null vector???");
		listItems.addElement(leaf);
	}
	
	/**
	 * Remove an item from our map
	 * @param keykey the item's key
	 * @param leaf the item
	 */
	public void removeItem(String keykey, TreeObject leaf)
	{
		Vector listItems;
		listItems = this.getKeys(keykey);
		listItems.remove(leaf);
	}
	
	/**
	 * Make a key into the table. The name and type combination uniquely
	 * identifies a rationale element since two items of the same type with
	 * the same name are not allowed
	 * @param name the name
	 * @param type the type
	 * @return the key
	 */
	public String makeKey(String name, RationaleElementType type)
	{
		String key;
		key = name + "-" + type.toString();
		return key;
	}
}
