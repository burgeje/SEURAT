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
 * Created on Oct 24, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

/**
 * Taken from the Eclipse Corner article on Tree Viewers
 */

public class NullDeltaListener implements IDeltaListener {
	protected static NullDeltaListener soleInstance = new NullDeltaListener();
	public static NullDeltaListener getSoleInstance() {
		return soleInstance;
	}
	
	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {}
	
	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {}
	
}

