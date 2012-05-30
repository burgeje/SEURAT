
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * This enumerated type defines different ways that arguments apply to alternatives.
 * The type is what indicates if the argument is for the alternative, against the alternative,
 * or describes a relationship between alternatives.
 * @author burgeje
 *
 */
public final class ArgType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3843556013171565131L;
	private String id;
	public final int ord;
	private ArgType prev;
	private ArgType next;
	
	private static int upperBound = 0;
	private static ArgType first = null;
	private static ArgType last = null;
	
	private ArgType(String anID) {
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
			private ArgType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				ArgType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static ArgType first() { return first; }
	public static ArgType last()  { return last;  }
	public ArgType prev()  { return this.prev; }
	public ArgType next()  { return this.next; }
	
	public static ArgType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			ArgType rtE = (ArgType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * The argument is against the alternative
	 */
	public static final ArgType DENIES = new 
	ArgType("Denies");
	/**
	 * The argument is in favor
	 */
	public static final ArgType SUPPORTS = new 
	ArgType("Supports");
	/**
	 * The argument indicates that this alternative pre-supposes (requires) another
	 */
	public static final ArgType PRESUPPOSES = new 
	ArgType("Pre-supposes");
//	public static final ArgType PRESUPPOSEDBY = new 
//	ArgType("Pre-supposed-by");
	/**
	 * The argument indicates that this alternative opposes another, i.e. they can not
	 * both be selected.
	 */
	public static final ArgType OPPOSES = new 
	ArgType("Opposed");
//	public static final ArgType OPPOSEDBY = new 
//	ArgType("Opposed-by");
	
	/**
	 * New Argument type for alternatives.
	 * (YQ)
	 */
	public static final ArgType COMPLICATES = new ArgType("Complicates"),
			FACILITATES = new ArgType("Facilitates");
	/**
	 * The argument indicates that this alternative addresses a requirment. This
	 * indicates support for the requirment but does not claim that by selecting
	 * this requirement the alternative has been met.
	 */
	public static final ArgType ADDRESSES = new 
	ArgType("Addresses");
	/**
	 * The argument indicates that this alternative satisfies a requirement. This
	 * means that if this alternative is selected the requirement has been met.
	 */
	public static final ArgType SATISFIES = new 
	ArgType("Satisfies");
	/**
	 * The argument indicates that this alternative violates a requirement and that
	 * if the alternative is selected the requirement will not be met (very bad
	 * and must be avoided!).
	 */
	public static final ArgType VIOLATES = new 
	ArgType("Violates");
	/**
	 * If we imported the argument, we won't have a direction... so we need to deal.
	 */
	public static final ArgType NONE = new ArgType("None");
}

