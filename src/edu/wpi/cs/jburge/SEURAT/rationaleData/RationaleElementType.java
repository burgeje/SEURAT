
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

/**
 * This defines the enumerated type that provides the different types
 * of rationale elements
 * @author burgeje
 *
 */
public final class RationaleElementType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4869801155106301652L;
	private String id;
	public final int ord;
	private RationaleElementType prev;
	private RationaleElementType next;
	
	private static int upperBound = 0;
	private static RationaleElementType first = null;
	private static RationaleElementType last = null;
	
	private RationaleElementType(String anID) {
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
			private RationaleElementType curr = first;
			public boolean hasMoreElements() {
				return curr != null;
			}
			public Object nextElement() {
				RationaleElementType c = curr;
				curr = curr.next();
				return c;
			}
		};
	}
	public String toString() {return this.id; }
	public static int size() { return upperBound; }
	public static RationaleElementType first() { return first; }
	public static RationaleElementType last()  { return last;  }
	public RationaleElementType prev()  { return this.prev; }
	public RationaleElementType next()  { return this.next; }
	
	public static RationaleElementType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		if (rt == null)
			return null;
		while (ourEnum.hasMoreElements())
		{
			RationaleElementType rtE = (RationaleElementType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}
	
//	RATIONALE is not really a type of element, it is used to signify the root of the tree
	/**
	 * A type of RATIONALE specifies the root of our rationale tree. It isn't actually
	 * an element type.
	 */
	public static final RationaleElementType RATIONALE = new 
	RationaleElementType("Rationale");
	/**
	 * Requirements are things our system is required to do
	 */
	public static final RationaleElementType REQUIREMENT = new 
	RationaleElementType("Requirement");
	/**
	 * Decisions are decision problems that need to be solved in order to
	 * define and implement our system.
	 */
	public static final RationaleElementType DECISION = new 
	RationaleElementType("Decision");
	/**
	 * Arguments argue for and against alternatives for addressing the
	 * decisions. They can also argue for and against requirements and about
	 * other arguments
	 */
	public static final RationaleElementType ARGUMENT = new 
	RationaleElementType("Argument");
	/**
	 * Alternatives are alternative solutions to the decision problem. They
	 * should eventually map so some sort of developed artifact (code, diagrams, 
	 * etc.) where the alternative is implemented
	 */
	public static final RationaleElementType ALTERNATIVE = new 
	RationaleElementType("Alternative");
	/**
	 * Claims are claims we are making about an alternative within an argument
	 */
	public static final RationaleElementType CLAIM = new 
	RationaleElementType("Claim");
	/**
	 * Assumptions are things we think are true but that may not always
	 * hold true in the future
	 */
	public static final RationaleElementType ASSUMPTION = new 
	RationaleElementType("Assumption");
	/**
	 * Ontology entries appear in the Argument Ontology and are usually
	 * generic non-functional requirements (such as safety or scalability)
	 */
	public static final RationaleElementType ONTENTRY = new 
	RationaleElementType("Ontology Entry");
	/**
	 * Tradeoffs refer to two ontology entries where if we want more of one, we
	 * need less of the other. Flexibility vs. cost, for example.
	 */
	public static final RationaleElementType TRADEOFF = new 
	RationaleElementType("Tradeoff");
	/**
	 * Co-occurences refer to two ontology entries where if one is true, the
	 * other must be also. 
	 */
	public static final RationaleElementType COOCCURRENCE = new 
	RationaleElementType("Co-Occurrence");
	/**
	 * Questions are things that need to be answered in order to select an
	 * alternative or make a decision
	 */
	public static final RationaleElementType QUESTION = new
	RationaleElementType("Question");
	/**
	 * Design product entries are components of the product being designed
	 */
	public static final RationaleElementType DESIGNPRODUCTENTRY = new
	RationaleElementType("Design Component");
	/**
	 * Constraints limit the choices we can make when developing a design
	 * component. For example, we may have a weight limit for part of a spacecraft.
	 */
	public static final RationaleElementType CONSTRAINT = new 
	RationaleElementType("Constraint");
	/**
	 * Contingencies are added to account for uncertainty. For example, we
	 * may have a mass contingency for part of a spacecraft of 20% as a
	 * safety margin. The more is known about the design choice being made, the
	 * smaller the contingency is likely to be.
	 */
	public static final RationaleElementType CONTINGENCY = new 
	RationaleElementType("Contingency");
	/**
	 * The designer is the person writing the rationale.
	 */
	public static final RationaleElementType DESIGNER = new 
	RationaleElementType("Designer");
	/**
	 * Expertise is an area of expertise that may be held by a designer
	 */
	public static final RationaleElementType EXPERTISE = new
	RationaleElementType("Expertise");
	/**
	 * The alternative-constraint relationship refers to the relationship
	 * between an alternative and a constraint - how much does this particular 
	 * constraint refer to this alternative?
	 */
	public static final RationaleElementType ALTCONSTREL = new
	RationaleElementType("Alternative Constraint");
	/**
	 * We've added a new type for elements that are candidate rationale not yet adopted
	 */
	public static final RationaleElementType CANDIDATE = new
	RationaleElementType("Candidate");
	/**
	 * None - should this be occuring?
	 */
	public static final RationaleElementType NONE = new
	RationaleElementType("None");
}


