

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * Defines an enumerated type that defines the different values for Alternative Status
 * @author burgeje
 *
 */
public final class AlternativeStatus implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	public final int ord;
	private AlternativeStatus prev;
	private AlternativeStatus next;
	
	private static int upperBound = 0;
	private static AlternativeStatus first = null;
	private static AlternativeStatus last = null;
	
	private AlternativeStatus(String anID) {
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
			private AlternativeStatus curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				AlternativeStatus c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static AlternativeStatus first() { return first; }
	public static AlternativeStatus last()  { return last;  }
	public AlternativeStatus prev()  { return this.prev; }
	public AlternativeStatus next()  { return this.next; }
	
	public static AlternativeStatus fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			AlternativeStatus rtE = (AlternativeStatus) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * The alternative has been selected
	 */
	public static final AlternativeStatus ADOPTED = new 
	AlternativeStatus("Adopted");
	/**
	 * The status of the alternative has not yet been determined
	 */
	public static final AlternativeStatus ATISSUE = new 
	AlternativeStatus("At_Issue");
	/**
	 * The alternative has been rejected as a possible solution
	 */
	public static final AlternativeStatus REJECTED = new 
	AlternativeStatus("Rejected");
	
	
}
