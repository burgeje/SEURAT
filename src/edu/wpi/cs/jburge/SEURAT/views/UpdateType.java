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
/*
 * Created on Oct 28, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.io.*;
import java.util.Enumeration;


/**
 * The enumerated class that describes different types of update events.
 * @author burgeje
 *
 */
public final class UpdateType  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8223161803431054747L;
	private String id;
	public final int ord;
	private UpdateType prev;
	private UpdateType next;
	
	private static int upperBound = 0;
	private static UpdateType first = null;
	private static UpdateType last = null;
	
	private UpdateType(String anID) {
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
			private UpdateType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				UpdateType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static UpdateType first() { return first; }
	public static UpdateType last()  { return last;  }
	public UpdateType prev()  { return this.prev; }
	public UpdateType next()  { return this.next; }
	
	public static UpdateType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			UpdateType rtE = (UpdateType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * Update a rationale element. The status has changed.
	 */
	public static final UpdateType UPDATE = new 
	UpdateType("Update");
	/**
	 * Find a rationale element
	 */
	public static final UpdateType FIND = new 
	UpdateType("Find");
	/**
	 * Associate a rationale element with some code
	 */
	public static final UpdateType ASSOCIATE = new UpdateType("Associate");
	/**
	 * Open an associated rationale database
	 */
	public static final UpdateType DATABASE = new UpdateType("Database");
	/**
	 * Update the status in the tree and the task list when an override is removed
	 */
	public static final UpdateType STATUS = new UpdateType("Status");
	/**
	 * Add a new rationale element (and its children) to the Rationale Explorer tree
	 */
	public static final UpdateType ADD = new UpdateType("Add");
}

