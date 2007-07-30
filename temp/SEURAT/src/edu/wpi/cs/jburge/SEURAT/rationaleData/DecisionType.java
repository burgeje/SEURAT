

 // package enumTest;
 package edu.wpi.cs.jburge.SEURAT.rationaleData;
 
  import java.util.*;
  import java.io.*;

  /**
   * An enumerated type to describe our different types of decisions
   * @author burgeje
   *
   */
  public final class DecisionType implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3005507580650659938L;
	private String id;
    public final int ord;
    private DecisionType prev;
    private DecisionType next;

    private static int upperBound = 0;
    private static DecisionType first = null;
    private static DecisionType last = null;
    
    private DecisionType(String anID) {
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
        private DecisionType curr = first;
        public boolean hasMoreElements() {
          return curr != null;
        }
        public Object nextElement() {
          DecisionType c = curr;
          curr = curr.next();
          return c;
        }
      };
    }
    public String toString() {return this.id; }
    public static int size() { return upperBound; }
    public static DecisionType first() { return first; }
    public static DecisionType last()  { return last;  }
    public DecisionType prev()  { return this.prev; }
    public DecisionType next()  { return this.next; }
    
	public static DecisionType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			DecisionType rtE = (DecisionType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}

	/**
	 * The decision can be resolved by chosing one and only one alternative
	 */
    public static final DecisionType SINGLECHOICE = new 
DecisionType("SingleChoice");
    /**
     * The decision can be resolved by choosing multiple alternatives
     */
    public static final DecisionType MULTIPLECHOICE = new 
DecisionType("MultipleChoice");

  }

