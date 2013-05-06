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
 * This is the initial template for the enumerated type. This class is
 * not actually used.
 * @author burgeje
 *
 */
public class EnumeratedType implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2006716284312289955L;
	//	protected String id;
	public final int ord;
	private EnumeratedType prev;
	private EnumeratedType next;
	
	private static int upperBound = 0;
	private static EnumeratedType first = null;
	private EnumeratedType last = null;
	
	protected EnumeratedType(String anID) {
		ord = 0;
//		this.id = anID;
		/*	  this.ord = upperBound++;
		 if (first == null) first = this;
		 if (last != null) {
		 this.prev = last;
		 last.next = this;
		 }
		 last = this; */
	}
	public static Enumeration elements() {
		return new Enumeration() {
			private EnumeratedType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				EnumeratedType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
//	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public EnumeratedType first() { return first; }
	public EnumeratedType last()  { return last;  }
	public EnumeratedType prev()  { return this.prev; }
	public EnumeratedType next()  { return this.next; }
	/*
	 public static EnumeratedType fromString(String rt)
	 {
	 Enumeration ourEnum = elements();
	 while (ourEnum.hasMoreElements())
	 {
	 EnumeratedType rtE = (EnumeratedType) ourEnum.nextElement();
	 if (rt.compareTo(rtE.toString()) == 0)
	 {
	 return rtE;
	 }
	 }
	 return null;
	 } */
	
}
