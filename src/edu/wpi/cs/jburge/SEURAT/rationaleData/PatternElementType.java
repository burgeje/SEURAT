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
