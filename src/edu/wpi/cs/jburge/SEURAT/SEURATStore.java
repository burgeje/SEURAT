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

package edu.wpi.cs.jburge.SEURAT;


/**
 * Utiltity Class to store the label decoration preference. This should
 * be enhanced by users so that the values are persistent across sessions. This code 
 * came from an Eclipse demo.
 */
public class SEURATStore
{
	private static SEURATStore instance_ = null;
	
	private boolean displayTextLabelInformation_ = true;
	private boolean displayProjectName_ = true;
	
	/**
	 * Constructor for DemoStore.
	 */
	private SEURATStore()
	{
		super();
	}
	
	public static SEURATStore getInstance()
	{
		if (instance_ == null)
		{
			instance_ = new SEURATStore();
		}
		return instance_;
	}
	
	/**
	 * Function to determine whether the File Label prefix / suffix should be 
	 * displayed
	 * 
	 * @return true owner name should be displayed
	 *          false otherwise 
	 */ 
	public boolean shouldDisplayTextLabel()
	{
		return displayTextLabelInformation_;
	}
	
	
	/**
	 * Set values for boolean flag to display prefix/suffix
	 */ 
	public void setDisplayTextLabel (boolean value)
	{
		displayTextLabelInformation_ = value;
	}
	
	/**
	 * Display Project label decorator or not
	 */ 
	public boolean shouldDisplayProject ()
	{
		return displayProjectName_;
	}
	
	public void setDisplayProject (boolean value)
	{
		displayProjectName_ = value;
	}
}
