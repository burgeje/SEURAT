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
