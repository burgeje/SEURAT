package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * Enumerated type defining the different status values that can be held by 
 * a requirement. 
 * @author burgeje
 *
 */
public final class XFeatureNodeType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8284597418079774511L;
	private String id;
	public final int ord;
	private XFeatureNodeType prev;
	private XFeatureNodeType next;
	
	private static int upperBound = 0;
	private static XFeatureNodeType first = null;
	private static XFeatureNodeType last = null;
	
	/**
	 * Creates the status element. 
	 * @param anID - the status value
	 */
	private XFeatureNodeType(String anID) {
		this.id = anID;
		this.ord = upperBound++;
		if (first == null) first = this;
		if (last != null) {
			this.prev = last;
			last.next = this;
		}
		last = this;
	}
	/**
	 * Returns a list of the status types. Useful when providing choices
	 * on a GUI
	 * @return the enumeration of status types
	 */ 
	public static Enumeration elements() {
		return new Enumeration() {
			private XFeatureNodeType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				XFeatureNodeType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	
	/**
	 * Converts the type to a string so it can be displayed or
	 * stored in a database.
	 */
	public String toString() {return this.id; }
	
	/** 
	 * Calculates the number of different status values
	 * @return the number of different status values
	 */
	public static int size() { return upperBound; }
	
	/**
	 * Gets the first element
	 * @return the first element
	 */
	public static XFeatureNodeType first() { return first; }
	
	/**
	 * Gets the last element
	 * @return the last element
	 */
	public static XFeatureNodeType last()  { return last;  }
	
	/**
	 * Gets the previous item in the list
	 * @return the previous item
	 */
	public XFeatureNodeType prev()  { return this.prev; }
	
	/**
	 * Gest the next item in the list
	 * @return the next item
	 */
	public XFeatureNodeType next()  { return this.next; }
	
	/**
	 * Creates a status from a string. Needed to work with the database.
	 * @param rs the status type name
	 * @return the status enumerated type
	 */
	public static XFeatureNodeType fromString(String rs)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			XFeatureNodeType rsE = (XFeatureNodeType) ourEnum.nextElement();
			if (rs.compareTo(rsE.toString()) == 0)
			{
				return rsE;
			}
		}
		return null;
	}
	/**
	 * This is a solitary feature node - the decision will be how many
	 */
	public static final XFeatureNodeType SOLITARYFEATURENODE = new 
	XFeatureNodeType("SolitaryFeatureNode");
	public static final XFeatureNodeType SOLITARYFEATURECARDINALITYZERO = new 
	XFeatureNodeType("SolitaryFeatureCardinalityZero");
	public static final XFeatureNodeType SOLITARYFEATURECARDINALITYONE = new 
	XFeatureNodeType("SolitaryFeatureCardinalityOne");
	public static final XFeatureNodeType SOLITARYFEATURECARDINALITYMANY = new
	XFeatureNodeType("SolitaryFeatureCardinalityMany");
	/**
	 * This node indicates that the designer needs to select a subset of the group
	 */
	public static final XFeatureNodeType GROUPNODE = new 
	XFeatureNodeType("GroupNode");
	/**
	 * This node is an element of a group
	 */
	public static final XFeatureNodeType ROOTFEATURENODE = new
	XFeatureNodeType("RootFeatureNode");
	public static final XFeatureNodeType GROUPEDFEATURENODE = new 
	XFeatureNodeType("GroupedFeatureNode");
	public static final XFeatureNodeType GROUPEDFEATUREDECISION = new 
	XFeatureNodeType("GroupedFeatureDecision");
	public static final XFeatureNodeType GROUPEDFEATURECARDINALITYZERO = new 
	XFeatureNodeType("GroupedFeatureCardinalityZero");
	public static final XFeatureNodeType GROUPEDFEATURECARDINALITYONE = new
	XFeatureNodeType("GroupedFeatureCardinalityOne");
	public static final XFeatureNodeType GROUPEDFEATURECARDINALITYMANY = new
	XFeatureNodeType("GroupedFeatureCardinalityMany");
	
}

