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

// package enumTest;

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * Defines the enumerated type for the different values for Importance.
 * The importance of the various arguments is a key factor in evaluating
 * the support for an alternative.
 * @author burgeje
 *
 */
public final class Importance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7888046525249475587L;
	private String id;
	public final int ord;
	private Importance prev;
	private Importance next;
	
	private static int upperBound = 0;
	private static Importance first = null;
	private static Importance last = null;
	
	private Importance(String anID) {
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
			private Importance curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				Importance c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static Importance first() { return first; }
	public static Importance last()  { return last;  }
	public Importance prev()  { return this.prev; }
	public Importance next()  { return this.next; }
	
	
	
	public static Importance fromString(String imp)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			Importance impE = (Importance) ourEnum.nextElement();
			if (imp.compareTo(impE.toString()) == 0)
			{
				return impE;
			}
		}
		return Importance.DEFAULT;
	}
	
	
	public double getValue()
	{
		double val = -1.0;
//		***		System.out.println("decoding our value");
		if (id.compareTo(Importance.NOT.toString()) == 0)
		{
			val = 0.0;	
		}
		else if (id.compareTo(Importance.LOW.toString()) == 0)
		{
			val = 0.25;
		}
		else if (id.compareTo(Importance.MODERATE.toString()) == 0)
		{
			val = 0.5;
		}
		else if (id.compareTo(Importance.HIGH.toString()) == 0)
		{
			val = 0.75;
		}
		else if (id.compareTo(Importance.ESSENTIAL.toString()) == 0)
		{
			val = 1.0;
		}
//		***		System.out.println("sucessful");
		return val;
	}
	
	/**
	 * A default importance means the value is inherited from someone else
	 */
	public static final Importance DEFAULT = new 
	Importance("Default");
	/**
	 * The item is not important
	 */
	public static final Importance NOT = new 
	Importance("Not");
	/**
	 * The item is of low importance
	 */
	public static final Importance LOW = new 
	Importance("Low");
	/**
	 * The item is moderately important
	 */
	public static final Importance MODERATE = new 
	Importance("Moderate");
	/**
	 * The item has high importance
	 */
	public static final Importance HIGH = new 
	Importance("High");
	/**
	 * The item is essential. A requirement is a good
	 * example of a type of element that is essential
	 */
	public static final Importance ESSENTIAL = new 
	Importance("Essential");
}


