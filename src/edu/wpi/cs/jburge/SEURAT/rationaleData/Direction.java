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
   * The enumerated type that defines the direction of a claim.
   * @author burgeje
   *
   */
  public final class Direction implements Serializable
 {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3209481861153860699L;
	private String id;
    public final int ord;
    private Direction prev;
    private Direction next;

    private static int upperBound = 0;
    private static Direction first = null;
    private static Direction last = null;
    
    private Direction(String anID) {
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
        private Direction curr = first;
        public boolean hasMoreElements() {
          return curr != null;
        }
        public Object nextElement() {
          Direction c = curr;
          curr = curr.next();
          return c;
        }
      };
    }
    public String toString() {return this.id; }
    public static int size() { return upperBound; }
    public static Direction first() { return first; }
    public static Direction last()  { return last;  }
    public Direction prev()  { return this.prev; }
    public Direction next()  { return this.next; }
    
	public static Direction fromString(String rt)
	{
		Enumeration ourEnum = elements();
		while (ourEnum.hasMoreElements())
		{
			Direction rtE = (Direction) ourEnum.nextElement();
			if (rt.compareTo(rtE.toString()) == 0)
			{
				return rtE;
			}
		}
		return null;
	}

	/**
	 * We're claiming that an alternative IS what the claim is about
	 */
    public static final Direction IS = new 
Direction("IS");
    /**
     * We're claiming that an alternative IS NOT what the claim is about
     */
    public static final Direction NOT = new 
Direction("NOT");
  }


