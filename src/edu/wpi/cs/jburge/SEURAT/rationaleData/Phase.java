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

//  package enumTest;

package edu.wpi.cs.jburge.SEURAT.rationaleData;

  import java.util.*;
import java.io.*;

  /**
   * The enumerated type that defines different development phases
   * @author burgeje
   *
   */
  public final class Phase implements Serializable
 {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4613199165159273537L;
	private String id;
    public final int ord;
    private Phase prev;
    private Phase next;

    private static int upperBound = 0;
    private static Phase first = null;
    private static Phase last = null;
    
    private Phase(String anID) {
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
        private Phase curr = first;
        public boolean hasMoreElements() {
          return curr != null;
        }
        public Object nextElement() {
          Phase c = curr;
          curr = curr.next();
          return c;
        }
      };
    }
    public String toString() {return this.id; }
    public static int size() { return upperBound; }
    public static Phase first() { return first; }
    public static Phase last()  { return last;  }
    public Phase prev()  { return this.prev; }
    public Phase next()  { return this.next; }
    
	public static Phase fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			Phase rtE = (Phase) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}

	/**
	 * The requirements phase
	 */
    public static final Phase REQUIREMENTS = new 
Phase("Requirements");
    /**
     * The analysis phase
     */
    public static final Phase ANALYSIS = new 
Phase("Analysis");
    /**
     * The architecture phase
     */
    public static final Phase ARCHITECTURE = new
Phase("Architecture");    
    /**
     * The design phase
     */
    public static final Phase DESIGN = new 
Phase("Design");
    /**
     * The implementation phase
     */
    public static final Phase IMPLEMENTATION = new 
Phase("Implementation");
    /**
     * The testing phase
     */
    public static final Phase TEST = new 
Phase("Test");

  }

