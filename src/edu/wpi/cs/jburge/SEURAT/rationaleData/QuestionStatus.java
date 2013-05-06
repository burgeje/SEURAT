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
 * An enumerated type giving different status values for our question
 * @author burgeje
 *
 */
public final class QuestionStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7318269643647788507L;
	private String id;
	public final int ord;
	private QuestionStatus prev;
	private QuestionStatus next;
	
	private static int upperBound = 0;
	private static QuestionStatus first = null;
	private static QuestionStatus last = null;
	
	private QuestionStatus(String anID) {
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
			private QuestionStatus curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				QuestionStatus c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static QuestionStatus first() { return first; }
	public static QuestionStatus last()  { return last;  }
	public QuestionStatus prev()  { return this.prev; }
	public QuestionStatus next()  { return this.next; }
	
	public static QuestionStatus fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			QuestionStatus rtE = (QuestionStatus) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * The question has not been answered
	 */
	public static final QuestionStatus UNANSWERED = new 
	QuestionStatus("Unanswered");
	/**
	 * The question has been answered
	 */
	public static final QuestionStatus ANSWERED = new 
	QuestionStatus("Answered");
}

