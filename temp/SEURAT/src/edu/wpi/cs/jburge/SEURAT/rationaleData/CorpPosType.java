

 // package enumTest;
 package edu.wpi.cs.jburge.SEURAT.rationaleData;
 
  import java.util.*;
  import java.io.*;

  /**
   * Defines an enumerated class for different positions within a 
   * corporation. These levels were based on those used at Charles River Analytics
   * and really just serve as a placeholder for "real" levels that should match
   * those used by the company using SEURAT. Ideally thouhg, we would want
   * the levels to be editable/extendible - more like the constraint or argument
   * ontology hierarchies.
  * @author burgeje
   *
   */
  public final class CorpPosType implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1044721296546127530L;
	
	private String id;
    public final int ord;
    private CorpPosType prev;
    private CorpPosType next;

    private static int upperBound = 0;
    private static CorpPosType first = null;
    private static CorpPosType last = null;
    
    private CorpPosType(String anID) {
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
        private CorpPosType curr = first;
        public boolean hasMoreElements() {
          return curr != null;
        }
        public Object nextElement() {
          CorpPosType c = curr;
          curr = curr.next();
          return c;
        }
      };
    }
    public String toString() {return this.id; }
    public static int size() { return upperBound; }
    public static CorpPosType first() { return first; }
    public static CorpPosType last()  { return last;  }
    public CorpPosType prev()  { return this.prev; }
    public CorpPosType next()  { return this.next; }
    
	public static CorpPosType fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			CorpPosType rtE = (CorpPosType) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}

	/**
	 * Engineer is the basic level of engineer.
	 */
    public static final CorpPosType ENGINEER = new 
CorpPosType("Engineer");
    /**
     * Senior engineer would be the next level up
     */
    public static final CorpPosType SRENGINEER = new 
CorpPosType("Senior Engineer");
    /**
     * Principle engineer is usually the highest engineering level
     */
    public static final CorpPosType PRENGINEER = new 
    CorpPosType("Principal Engineer");
    /**
     * A scientist is the base level on a research track
     */
    public static final CorpPosType SCIENTIST = new 
    CorpPosType("Scientist");
    /**
     * Senior scientist would be the next level up
     */
    public static final CorpPosType SRSCIENTIST = new 
    CorpPosType("Senior Scientist");
    /**
     * Principle scientist is an even higher scientist
     */
    public static final CorpPosType PRSCIENTIST = new 
    CorpPosType("Principle Scientist");
    /**
     * President is the president of the company
     */
    public static final CorpPosType PRESIDENT = new 
    CorpPosType("President");
  }

