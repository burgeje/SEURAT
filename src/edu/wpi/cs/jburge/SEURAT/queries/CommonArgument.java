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
/*
 * Created on May 18, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.queries;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * A CommonArgument defines a record displayed on the common 
 * argument display.
 * @author jburge
 */
public class CommonArgument {
	
	/**
	 * The name of our argument
	 */
	private String argumentName;
	/**
	 * What type it is
	 */
	private RationaleElementType type;
	/**
	 * The total number of references to it that appear in the rationale
	 */
	private String total;
	/**
	 * How many times it appears as an argument for an alternative
	 */
	private String forCount;
	/**
	 * How many times it appears as an argument against an alternative
	 */
	private String againstCount;
	
	/**
	 * Constructor
	 */
	public CommonArgument() {
		super();
		total = "0";
		forCount = "0";
		againstCount = "0";
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @return String
	 */
	public String getAgainstCount() {
		return againstCount;
	}
	
	/**
	 * @return String
	 */
	public String getArgumentName() {
		return argumentName;
	}
	
	/**
	 * @return String
	 */
	public String getForCount() {
		return forCount;
	}
	
	/**
	 * @return String
	 */
	public String getTotal() {
		return total;
	}
	
	public RationaleElementType getType() {
		return type;
	}
	/**
	 * Sets the againstCount.
	 * @param againstCount The againstCount to set
	 */
	public void setAgainstCount(String againstCount) {
		this.againstCount = againstCount;
	}
	
	
	
	
	/**
	 * Sets the argumentName.
	 * @param argumentName The argumentName to set
	 */
	public void setArgumentName(String argumentName) {
		this.argumentName = argumentName;
	}
	
	/**
	 * Sets the forCount.
	 * @param forCount The forCount to set
	 */
	public void setForCount(String forCount) {
		this.forCount = forCount;
	}
	
	/**
	 * Sets the total.
	 * @param total The total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}
	
	public void setType(RationaleElementType type) {
		this.type = type;
	}
	
	/**
	 * Increments the for count. This is a bit tricky since the counts
	 * are currently stored as strings... The total count is incremented too.
	 *
	 */
	public void incrementFor()
	{
		int forCt = Integer.parseInt(forCount);
		forCt++;
		setForCount(Integer.toString(forCt));
		int totalCt = Integer.parseInt(total);
		totalCt++;
		setTotal(Integer.toString(totalCt));	
	}
	
	/**
	 * Increments the against count. A bit tricky since counts
	 * are stored as strings. The total count is incremented too.
	 *
	 */
	public void incrementAgainst()
	{
		int againstCt = Integer.parseInt(againstCount);
		againstCt++;
		setAgainstCount(Integer.toString(againstCt));
		int totalCt = Integer.parseInt(total);
		totalCt++;
		setTotal(Integer.toString(totalCt));	
	}
	
}
