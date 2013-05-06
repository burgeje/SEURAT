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

import org.eclipse.core.runtime.QualifiedName;

/**
 * Utility class to maintain persistent property names and 
 * assosiated Qualified names. This was taken from the DemoResourcePersistantProperty
 * class and modified for SEURAT.
 * @author balajik
 *
 
 *
 */
public class SEURATPersistentPropertyTypes
{
	/**
	 * The static instance.
	 */
	private static SEURATPersistentPropertyTypes instance_;
	
	/**
	 * Rationale Indicator Qualified Name 
	 */ 
	private QualifiedName ratQualifiedName_;
	
	/**
	 * Rationale Status Qualified Name
	 */ 
	private QualifiedName errorQualifiedName_;
	
	private QualifiedName warningQualifiedName_;
	
	/**
	 * Constructor for SEURATResourcePersistentProperty.
	 */
	private SEURATPersistentPropertyTypes()
	{
		// Allocate memory for all the qualified name 
		ratQualifiedName_ = new QualifiedName("RationaleDecorator", "Rat");
		errorQualifiedName_ = new QualifiedName("RationaleDecorator", "Error");
		warningQualifiedName_ = new QualifiedName("RationaleDecorator", "Warning");
	}
	
	public static SEURATPersistentPropertyTypes getInstance()
	{
		if (instance_ == null)
		{
			instance_ = new SEURATPersistentPropertyTypes();
		}
		return instance_;
	}
	
	/**
	 * Get the Rationale Qualified name 
	 */ 
	public QualifiedName getRatQualifiedName()
	{
		return ratQualifiedName_;
	}
	
	/**
	 * Get the Status qualified name
	 */ 
	public QualifiedName getErrorQualifiedName()
	{
		return errorQualifiedName_;
	}
	
	public QualifiedName getWarningQualifiedName()
	{
		return warningQualifiedName_;
	}
	
	
	/**
	 * Get the qualified name given the local name
	 * 
	 * @param localName local name of the qualified name
	 * @return Qualified Name
	 * 
	 */ 
	public QualifiedName getQualifiedName(String localName)
	{
		if(localName.equals("Rat"))
		{
			return ratQualifiedName_;
		}
		else
		{
			if(localName.equals("Error"))
			{ 
				return errorQualifiedName_;
			}
			else 
			{
				if (localName.equals("Warning"))
				{
					return warningQualifiedName_;
				}
			}
			
		}
		return null;
	}
}
