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
 * Defines the type of error that has occured
 * @author burgeje
 *
 */
public final class RationaleStatusType implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 900340478599736038L;
	private String id;
	public final int ord;
	private RationaleStatusType prev;
	private RationaleStatusType next;
	
	private static int upperBound = 0;
	private static RationaleStatusType first = null;
	private static RationaleStatusType last = null;
	
	private RationaleStatusType(String anID) {
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
			private RationaleStatusType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				RationaleStatusType c = curr;
				curr = curr.next();
				return c;
			}
		};
	} 
	public String toString() {return this.id; } 
	public static int size() { return upperBound; } 
	public static RationaleStatusType first() { return first; }
	public static RationaleStatusType last()  { return last;  }
	public RationaleStatusType prev()  { return this.prev; }
	public RationaleStatusType next()  { return this.next; } 
	public static RationaleStatusType fromString(String rt) {
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			RationaleStatusType rtE = (RationaleStatusType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
	/**
	 * There isn't a selected alternative
	 */
	public static final RationaleStatusType NONE_SELECTED = new 
	RationaleStatusType("None_Selected");
	/**
	 * An alternative is selected that is not as well supported as
	 * other alternatives
	 */
	public static final RationaleStatusType LESS_SUPPORTED = new 
	RationaleStatusType("Less_Supported");
	/**
	 * An alternative is selected that has no arguments in its favor
	 */
	public static final RationaleStatusType NOT_SUPPORTED = new 
	RationaleStatusType("Not_Supported");
	/**
	 * A requirement has been violated (this error is attached to
	 * the requirement)
	 */
	public static final RationaleStatusType REQ_VIOLATION = new 
	RationaleStatusType("Requirement_Violation");
	/**
	 * An alternative is selected that violates a requirement
	 */
	public static final RationaleStatusType ALT_REQ_VIOLATION = new
	RationaleStatusType("Alt_Violates_Requirement");
	
	/**
	 * An alternative is selected that violates a non-functional requirement
	 */
	public static final RationaleStatusType NFR_VIOLATION = new
	RationaleStatusType("Violates_NFR");
	/**
	 * A tradeoff has been violated.
	 */
	public static final RationaleStatusType TRADE_VIOLATION = new
	RationaleStatusType("Tradeoff_Violation");
	/**
	 * A co-occurrence has been violated
	 */
	public static final RationaleStatusType CO_OCCURRENCE_VIOLATION = new
	RationaleStatusType("Co-occurrence_Violation");
	/**
	 * A presupposed alternative is not selected (something needed is not there)
	 */
	public static final RationaleStatusType PRESUPPOSED_NOTSEL = new
	RationaleStatusType("Presupposed_Not_Selected");
	/**
	 * An opposed alternative has been selected (conflict!)
	 */
	public static final RationaleStatusType OPPOSED_SEL = new
	RationaleStatusType("Opposed_Selected");
	/**
	 * A question has not been errored (attached to the question)
	 */
	public static final RationaleStatusType UNANSWERED_QUEST = new
	RationaleStatusType("Unanswered_Question");
	/**
	 * The alternative has an unanswered question
	 */
	public static final RationaleStatusType UNANSWERED_ALT_QUEST = new
	RationaleStatusType("Unanswered_Alternative_Question");
	/**
	 * An alternative is selected with no arguments for it
	 */
	public static final RationaleStatusType SELECTED_NONE_FOR = new
	RationaleStatusType("Selected_None_For");
	/**
	 * An alternative is selected with only arguments against it
	 */
	public static final RationaleStatusType SELECTED_ONLY_AGAINST = new
	RationaleStatusType("Selected_Only_Against");
	/**
	 * An alternative has contradictory arguments
	 */
	public static final RationaleStatusType CONTRADICTORY_ARGUMENTS = new 
	RationaleStatusType("Contradictory_Arguments");
	/**
	 * An alternative has duplicate arguments
	 */
	public static final RationaleStatusType DUPLICATE_ARGUMENTS = new
	RationaleStatusType("Duplicate_Arguments");
	/**
	 * More than one alternative has been selected for a single choice decision
	 */
	public static final RationaleStatusType MULTIPLE_SELECTION = new
	RationaleStatusType("Multiple_Selection");
	/**
	 * A decision has sub-decisions that have not been resolved
	 */
	public static final RationaleStatusType SUBDECISIONS_MISSING = new
	RationaleStatusType("Subdecisions_Missing");
	
	/**
	 * An argument is incomplete. This can happen if it was imported and not yet formalized
	 */
	public static final RationaleStatusType ARGUMENT_INCOMPLETE = new 
	RationaleStatusType("Argument_Incomplete");
	
	public static final RationaleStatusType UML_VIOLATION = 
			new RationaleStatusType("UML_Violation");
	
}
