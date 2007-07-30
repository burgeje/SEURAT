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
    

  }

