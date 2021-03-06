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

public final class DesignPatternProblemCategoryType {

	private String id;
	public final int ord;
	private DesignPatternProblemCategoryType prev;
	private DesignPatternProblemCategoryType next;
	
	private static int upperBound = 0;
	private static DesignPatternProblemCategoryType first = null;
	private static DesignPatternProblemCategoryType last = null;
	
	private DesignPatternProblemCategoryType(String anID) {
		this.id = anID;
		this.ord = upperBound++;
		if (first == null) first = this;
		if (last != null) {
			this.prev = last;
			last.next = this;
		}
		last = this;
	}
	
	public static DesignPatternProblemCategoryType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		if (rt == null)
			return null;
		while (ourEnum.hasMoreElements())
		{
			DesignPatternProblemCategoryType rtE = (DesignPatternProblemCategoryType) ourEnum.nextElement();
			if (rt.compareToIgnoreCase(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	public static Enumeration elements() {
		return new Enumeration() {
			private DesignPatternProblemCategoryType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				DesignPatternProblemCategoryType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static DesignPatternProblemCategoryType first() { return first; }
	public static DesignPatternProblemCategoryType last()  { return last;  }
	public DesignPatternProblemCategoryType prev()  { return this.prev; }
	public DesignPatternProblemCategoryType next()  { return this.next; }
	
	public static final DesignPatternProblemCategoryType STRUCTURALDECOMPOSITION = new 
	DesignPatternProblemCategoryType("Structural_Decomposition");
	
	public static final DesignPatternProblemCategoryType ORGANIZATIONOFWORK = new 
	DesignPatternProblemCategoryType("Organization_Of_Work");
	
	public static final DesignPatternProblemCategoryType ACCESSCONTROL = new 
	DesignPatternProblemCategoryType("Access_Control");
	
	public static final DesignPatternProblemCategoryType MANAGEMENT = new 
	DesignPatternProblemCategoryType("Management");
	
	public static final DesignPatternProblemCategoryType COMMUNICATION = new 
	DesignPatternProblemCategoryType("Communication");
}
