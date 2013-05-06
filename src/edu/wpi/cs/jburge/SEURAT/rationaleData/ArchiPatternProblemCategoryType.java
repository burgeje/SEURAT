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

public final class ArchiPatternProblemCategoryType {

	private String id;
	public final int ord;
	private ArchiPatternProblemCategoryType prev;
	private ArchiPatternProblemCategoryType next;
	
	private static int upperBound = 0;
	private static ArchiPatternProblemCategoryType first = null;
	private static ArchiPatternProblemCategoryType last = null;
	
	private ArchiPatternProblemCategoryType(String anID) {
		this.id = anID;
		this.ord = upperBound++;
		if (first == null) first = this;
		if (last != null) {
			this.prev = last;
			last.next = this;
		}
		last = this;
	}
	
	public static ArchiPatternProblemCategoryType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		if (rt == null)
			return null;
		while (ourEnum.hasMoreElements())
		{
			ArchiPatternProblemCategoryType rtE = (ArchiPatternProblemCategoryType) ourEnum.nextElement();
			if (rt.compareToIgnoreCase(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	public static Enumeration elements() {
		return new Enumeration() {
			private ArchiPatternProblemCategoryType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				ArchiPatternProblemCategoryType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static ArchiPatternProblemCategoryType first() { return first; }
	public static ArchiPatternProblemCategoryType last()  { return last;  }
	public ArchiPatternProblemCategoryType prev()  { return this.prev; }
	public ArchiPatternProblemCategoryType next()  { return this.next; }
	
	public static final ArchiPatternProblemCategoryType FROMMUDTOSTRUCTURE = new 
	ArchiPatternProblemCategoryType("From_Mud_To_Structure");
	
	public static final ArchiPatternProblemCategoryType DISTRIBUTEDSYSTEMS = new 
	ArchiPatternProblemCategoryType("Distributed_Systems");
	
	public static final ArchiPatternProblemCategoryType INTERACTIVESYSTEMS = new 
	ArchiPatternProblemCategoryType("Interactive_Systems");
	
	public static final ArchiPatternProblemCategoryType ADAPTABLESYSTEMS = new 
	ArchiPatternProblemCategoryType("Adaptable_Systems");
}
