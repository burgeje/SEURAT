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

import java.util.Enumeration;

public final class PatternElementType {

	private String id;
	public final int ord;
	private PatternElementType prev;
	private PatternElementType next;
	
	private static int upperBound = 0;
	private static PatternElementType first = null;
	private static PatternElementType last = null;
	
	private PatternElementType(String anID) {
		this.id = anID;
		this.ord = upperBound++;
		if (first == null) first = this;
		if (last != null) {
			this.prev = last;
			last.next = this;
		}
		last = this;
	}
	
	public static PatternElementType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		if (rt == null)
			return null;
		while (ourEnum.hasMoreElements())
		{
			PatternElementType rtE = (PatternElementType) ourEnum.nextElement();
			if (rt.compareToIgnoreCase(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	public static Enumeration elements() {
		return new Enumeration() {
			private PatternElementType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				PatternElementType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static PatternElementType first() { return first; }
	public static PatternElementType last()  { return last;  }
	public PatternElementType prev()  { return this.prev; }
	public PatternElementType next()  { return this.next; }
	
	public static final PatternElementType PATTERN = new 
	PatternElementType("Pattern");
	
	public static final PatternElementType ARCHITECTURE = new 
	PatternElementType("Architecture");
	
	public static final PatternElementType DESIGN = new 
	PatternElementType("Design");
	
	public static final PatternElementType IDIOM = new 
	PatternElementType("Idiom");
}
