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

import java.io.*;
import java.util.Enumeration;

/**
 * Enumerated type defining different types of requirements
 * @author burgeje
 *
 */
public final class ReqType  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -136740287858193289L;
	private String id;
	public final int ord;
	private ReqType prev;
	private ReqType next;
	
	private static int upperBound = 0;
	private static ReqType first = null;
	private static ReqType last = null;
	
	private ReqType(String anID) {
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
			private ReqType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				ReqType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static ReqType first() { return first; }
	public static ReqType last()  { return last;  }
	public ReqType prev()  { return this.prev; }
	public ReqType next()  { return this.next; }
	
	public static ReqType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			ReqType rtE = (ReqType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * Functional requirement
	 */
	public static final ReqType FR = new 
	ReqType("FR");
	/**
	 * Non-functional requirement
	 */
	public static final ReqType NFR = new 
	ReqType("NFR");
	
}

