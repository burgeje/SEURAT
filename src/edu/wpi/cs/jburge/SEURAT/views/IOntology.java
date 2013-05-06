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

package edu.wpi.cs.jburge.SEURAT.views;

import java.util.Enumeration;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;


public abstract class IOntology extends ITreeViewerSetup {
	/**
	 * Created by Jon Wright, Team 2
	 */
	
	/**
	 * Add child ontology elements to the tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 */
	protected void addOntology( TreeParent parent, String parentName, int depth )
	{
		if ( depth == 0 ) {
			return;
		}
		
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getOntology(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			String childName = (String) onts.nextElement();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.ONTENTRY);
			parent.addChild(child);
			addOntology(child, childName, depth-1);
		}
		
	}

}
