

//  package enumTest;
package edu.wpi.cs.jburge.SEURAT.rationaleData;

  import java.util.*;
  import java.io.*;

  /**
   * Defines an enumerated type to specify how plausible (certain) the arguments are.
   * @author burgeje
   *
   */
  public final class Plausibility implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2889471786467886655L;
	private String id;
    public final int ord;
    private Plausibility prev;
    private Plausibility next;

    private static int upperBound = 0;
    private static Plausibility first = null;
    private static Plausibility last = null;
    
    private Plausibility(String anID) {
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
        private Plausibility curr = first;
        public boolean hasMoreElements() {
          return curr != null;
        }
        public Object nextElement() {
          Plausibility c = curr;
          curr = curr.next();
          return c;
        }
      };
    }
    public String toString() {return this.id; }
    public static int size() { return upperBound; }
    public static Plausibility first() { return first; }
    public static Plausibility last()  { return last;  }
    public Plausibility prev()  { return this.prev; }
    public Plausibility next()  { return this.next; }
    
	public static Plausibility fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			Plausibility rtE = (Plausibility) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}

	/**
	 * The person posing the argument doesn't have much confidence in it
	 */
    public static final Plausibility LOW = new 
Plausibility("Low");
    /**
     * We're moderately sure the argument is true
     */
    public static final Plausibility MEDIUM = new 
Plausibility("Medium");
    /**
     * We are very confident that the argument is true
     */
    public static final Plausibility HIGH = new 
Plausibility("High");
    /**
     * We are absolutely certain the argument is true
     */
    public static final Plausibility CERTAIN = new 
Plausibility("Certain");
   }


