

// package enumTest;

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * Defines an enumerated type that is used to express the different decision
 * status values.
 * @author burgeje
 *
 */
public final class DecisionStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1020290774004033995L;
	private String id;
	public final int ord;
	private DecisionStatus prev;
	private DecisionStatus next;
	
	private static int upperBound = 0;
	private static DecisionStatus first = null;
	private static DecisionStatus last = null;
	
	private DecisionStatus(String anID) {
		this.id = anID;
		this.ord = upperBound++;
		if (first == null) first = this;
		if (last != null) {
			this.prev = last;
			last.next = this;
		}
		last = this;
	}
	public static Enumeration elements() {
		return new Enumeration() {
			private DecisionStatus curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				DecisionStatus c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static DecisionStatus first() { return first; }
	public static DecisionStatus last()  { return last;  }
	public DecisionStatus prev()  { return this.prev; }
	public DecisionStatus next()  { return this.next; }
	
	public static DecisionStatus fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			DecisionStatus rtE = (DecisionStatus) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * The decision has been resolved, i.e. an alternative has been selected
	 */
	public static final DecisionStatus RESOLVED = new 
	DecisionStatus("Resolved");
	/**
	 * The decision has not been resolved
	 */
	public static final DecisionStatus UNRESOLVED = new 
	DecisionStatus("Unresolved");
	/**
	 * It has been determined that the decision can not be resolved (this
	 * could be very bad!)
	 */
	public static final DecisionStatus NONRESOLVABLE = new 
	DecisionStatus("Non-resolvable");
	/**
	 * The decision has been retracted and no longer needs to be made
	 */
	public static final DecisionStatus RETRACTED = new 
	DecisionStatus("Retracted");
	/**
	 * The decision has been addressed. I don't think this belongs here!
	 */
	public static final DecisionStatus ADDRESSED = new 
	DecisionStatus("Addressed");
	
}

