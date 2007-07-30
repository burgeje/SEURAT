
package edu.wpi.cs.jburge.SEURAT.rationaleData;

  import java.util.*;
  import java.io.*;
 
  /**
   * Enumerated type defining the different status values that can be held by 
   * a requirement. 
   * @author burgeje
   *
   */
  public final class ReqStatus implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8284597418079774511L;
	private String id;
    public final int ord;
    private ReqStatus prev;
    private ReqStatus next;
    
    private static int upperBound = 0;
    private static ReqStatus first = null;
    private static ReqStatus last = null;
    
    /**
     * Creates the status element. 
     * @param anID - the status value
     */
    private ReqStatus(String anID) {
		this.id = anID;
		this.ord = upperBound++;
		if (first == null) first = this;
		if (last != null) {
		  this.prev = last;
		  last.next = this;
		}
		last = this;
	  }
   /**
    * Returns a list of the status types. Useful when providing choices
    * on a GUI
    * @return the enumeration of status types
    */ 
    public static Enumeration elements() {
      return new Enumeration() {
        private ReqStatus curr = first;
        public boolean hasMoreElements() {
          return curr != null;
        }
        public Object nextElement() {
          ReqStatus c = curr;
          curr = curr.next();
          return c;
        }
      };
    }
    
    /**
     * Converts the type to a string so it can be displayed or
     * stored in a database.
     */
    public String toString() {return this.id; }
    
    /** 
     * Calculates the number of different status values
     * @return the number of different status values
     */
    public static int size() { return upperBound; }
    
    /**
     * Gets the first element
     * @return the first element
     */
    public static ReqStatus first() { return first; }
    
    /**
     * Gets the last element
     * @return the last element
     */
    public static ReqStatus last()  { return last;  }
    
    /**
     * Gets the previous item in the list
     * @return the previous item
     */
    public ReqStatus prev()  { return this.prev; }
    
    /**
     * Gest the next item in the list
     * @return the next item
     */
    public ReqStatus next()  { return this.next; }
 
    /**
     * Creates a status from a string. Needed to work with the database.
     * @param rs the status type name
     * @return the status enumerated type
     */
	public static ReqStatus fromString(String rs)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			ReqStatus rsE = (ReqStatus) ourEnum.nextElement();
			if (rs.compareTo(rsE.toString()) == 0)
			{
				return rsE;
			}
		}
		return null;
	}
/**
 * The requirement has been addressed, but not necessarily statisfied
 */
	 public static final ReqStatus ADDRESSED = new 
ReqStatus("Addressed");
	 /**
	  * The requirement has been satisfied
	  */
   public static final ReqStatus SATISFIED = new 
ReqStatus("Satisfied");
   /**
    * The final disposition of the requirement has not yet been determined.
    */
	public static final ReqStatus UNDECIDED = new 
ReqStatus("Undecided");
	/**
	 * The requirement has been violated.
	 */
    public static final ReqStatus VIOLATED = new 
ReqStatus("Violated");
    /**
     * The requirement has been retracted
     */
    public static final ReqStatus RETRACTED = new 
ReqStatus("Retracted");
    /**
     * The requirement has been rejected
     */
    public static final ReqStatus REJECTED = new 
ReqStatus("Rejected");
 /*   public static final ReqStatus DEFERRED = new
    ReqStatus("Deferred");
    */

  }

