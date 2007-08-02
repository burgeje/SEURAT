
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * This is an enumerated type that gives the different categories of arguments.
 * @author burgeje
 *
 */
public final class ArgCategory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8212887956540232682L;
	private String id;
	public final int ord;
	private ArgCategory prev;
	private ArgCategory next;
	
	private static int upperBound = 0;
	private static ArgCategory first = null;
	private static ArgCategory last = null;
	
	private ArgCategory(String anID) {
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
			private ArgCategory curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				ArgCategory c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static ArgCategory first() { return first; }
	public static ArgCategory last()  { return last;  }
	public ArgCategory prev()  { return this.prev; }
	public ArgCategory next()  { return this.next; }
	
	public static ArgCategory fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			ArgCategory rtE = (ArgCategory) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * The argument is making a claim about an alternative that maps to an
	 * entry in the argument ontology
	 */
	public static final ArgCategory CLAIM = new 
	ArgCategory("Claim");
	/**
	 * The argument refers to a requirement - the alternative addresses, satisifes,
	 * or violates it
	 */
	public static final ArgCategory REQUIREMENT = new 
	ArgCategory("Requirement");
	/**
	 * The argument refers to an assumption that is being made about the system, its 
	 * environment, its uses, or any other factors influencing decisions that are
	 * not known to be facts or true under all circumstances.
	 */
	public static final ArgCategory ASSUMPTION = new 
	ArgCategory("Assumption");
	/**
	 * The argument describes a dependency between alternatives. They may require
	 * each other or they may be in conflict.
	 */
	public static final ArgCategory ALTERNATIVE = new 
	ArgCategory("Alternative");
	
}

