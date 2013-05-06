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
 * This enumerated type defines different positions a developer could have
 * on a project. This is just a temporary set used when creating a designer 
 * profile. Ideally we should make the set of possible positions editable 
 * so a company can customize it for their needs.
 * @author burgeje
 *
 */
public final class ProjPosType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3968237055449223364L;
	private String id;
	public final int ord;
	private ProjPosType prev;
	private ProjPosType next;
	
	private static int upperBound = 0;
	private static ProjPosType first = null;
	private static ProjPosType last = null;
	
	private ProjPosType(String anID) {
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
			private ProjPosType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				ProjPosType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static ProjPosType first() { return first; }
	public static ProjPosType last()  { return last;  }
	public ProjPosType prev()  { return this.prev; }
	public ProjPosType next()  { return this.next; }
	
	public static ProjPosType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			ProjPosType rtE = (ProjPosType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * Member of technical staff - an entry level technical position (really
	 * more a corporate position than a project one. It should be something
	 * more general like "Developer"
	 */
	public static final ProjPosType MTS = new 
	ProjPosType("Member Technical Staff");
	/**
	 * A group leader. This is someone leading a small group of developers.
	 */
	public static final ProjPosType GROUPLEAD = new 
	ProjPosType("Group Leader");
	/**
	 * A test engineer could be doing the testing or directing the testing (or both...)
	 */
	public static final ProjPosType TESTENG = new 
	ProjPosType("Test Engineer");
	/**
	 * Project lead is the person in charge of an entire project
	 */
	public static final ProjPosType PROJLEAD = new 
	ProjPosType("Project Leader");
	/**
	 * Principle investigator usually refers to the leader of a research project
	 */
	public static final ProjPosType PI = new 
	ProjPosType("Principal Investigator");
	/**
	 * System's architect - what developers call themselves when they want
	 * to feel important.
	 */
	public static final ProjPosType SYSARCH = new 
	ProjPosType("System Architect");
	
}

