
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

/**
 * The generic rationale element type. This contains important inherited
 * attributes like the name, description and enabled flag.
 * @author burgeje
 *
 */
public class RationaleElement implements Serializable
{
	@Override
	public boolean equals(Object obj) {
		if( obj.getClass().equals(this.getClass()) )
		{
			return ((RationaleElement)obj).id == this.id;
		}
		return super.equals(obj);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// class variables
	/**
	 * Used so that each element of this type has a new ID. Do we still need
	 * this when we're working with the database? 
	 */
	static int lastID;  //this will increment as each decision is created
	
	// instance variables
	int id;
	/**
	 * The short name used in the tree display as well as uniquely identifying
	 * the elements of this element type.
	 */
	String name;
	/**
	 * Natural language description. Needed if the name is not sufficiently clear
	 * to describe the element.
	 */
	String description;
	/**
	 * Some elements contain a history of changes.
	 */
	Stack<History> history;
	/**
	 * Identifies if this element of rationale is enabled and should be used
	 * in inferencing.
	 */
	boolean enabled;
	/**
	 * Identifies if we are reading this element in from XML and need to
	 * check to make sure we aren't duplicating it
	 *
	 */
	boolean fromXML;
	
	public RationaleElement getParentElement()
	{
		return null;
	}
	
	RationaleElement()
	{
		id = -1;
		lastID++;
		name = "";
		description = new String("");
		history = new Stack<History>();
		enabled = true;
		fromXML = false;
	} 
	
	public int getID()
	{
		return id;
	}
	
	public static int getLastID()
	{
		return lastID;
	}
	
	
	public void setID(int newID)
	{
		id = newID;
	}
	
	public static void setLastID(int last)
	{
		lastID = last;
	}
	
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String desc)
	{
		description = desc;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String nam)
	{
		name = nam;
	}
	
	public boolean getEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean en)
	{
		enabled = en;
	}
	
	public String toString()
	{
		return name;
	}
	
	public void updateHistory(History hist)
	{
		history.push(hist);
	}
	
	public Enumeration getHistory()
	{
		return history.elements();
	}
	
	public Vector getHistoryV()
	{
		return history;
	}
	
	//this is present in order to be sub-classed
	public Vector getList(RationaleElementType type)
	{
		return null;
	}
	
	/**
	 * Retrieve the element from the database. This must be implemented by the subclasses.
	 * @param id the database primary key
	 */
	public void fromDatabase(int id)
	{
		System.out.println("retrieving data from ID not supported");
	}
	/**
	 * Retrieve the element from the database. This must be implemented by
	 * the subclasses.
	 * @param name the name (which should uniquely identify elements of 
	 * this type
	 */
	public void fromDatabase(String name)
	{
	}
	
	/*	public boolean display()
	 {
	 return true;
	 }
	 */
	
	/**
	 * Displays the element in the editor. This must be implemented by the subclasses.
	 */
	public boolean display(Display disp)
	{
		System.out.println("didn't reach subclass!!!");
		return true;
	}
	
	/**
	 * Brings up the editor so a new element can be created. This must be implemented 
	 * by the subclasses.
	 * @param disp the display
	 * @param parent the parent element
	 * @return true
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
		System.out.println("didn't reach subclass!!!");
		return true;
	}
	
	/**
	 * Inference over the rationale to update the status. This must be
	 * implemented by the subclasses.
	 * @return null
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		return null;
	}
	
	/**
	 * Deletes the element. This must be implemented by the subclasses.
	 * @return true
	 */
	public boolean delete()
	{
		System.out.println("delete not defined");
		return true;
	}
	
	/**
	 * Used to associate with code. This must be implemented by alternatives.
	 * @param disp the display
	 * @return null
	 */
	public RationaleElement associateElement(Display disp)
	{
		System.out.println("associate not defined");
		return null;
	}
	
	/**
	 * Perform any inferences needed to update status of dependent elements when
	 * this element is deleted
	 * @return empty status vector (why not null?)
	 */
	public Vector<RationaleStatus> updateOnDelete()
	{
		System.out.println("update on delete not defined");
		return (new Vector<RationaleStatus>());
	}
	
	/**
	 * Identifies what type of rationale element this is
	 * @return type is RATIONALE
	 */
	public RationaleElementType getElementType()
	{
		return RationaleElementType.RATIONALE;
	}
	
	/**
	 * Creates the element history from an XML file. This is a 
	 * recursive method.  
	 * @param root  the XML Element that the history is under.
	 */
	public void historyFromXML(Element root)
	{
		Element child = (Element) root.getFirstChild();
		System.out.println("history");
		while (child != null)
		{
			History hist = new History();
			updateHistory(hist);
			hist.fromXML(child);
			child = (Element) child.getNextSibling();
			
		}
	}
	
	
}
