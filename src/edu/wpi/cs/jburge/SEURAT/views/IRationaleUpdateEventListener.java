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
 * Created on Apr 17, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.EventListener;

/**
 * The interface for a rationale update event. The listeners must
 * implement this interface.
 * @author jburge
 */
public interface IRationaleUpdateEventListener extends EventListener {
	public void updateRationaleTree(RationaleUpdateEvent e);
	public void showRationaleNode(RationaleUpdateEvent e);
	public void associateAlternative(RationaleUpdateEvent e);
	public void openDatabase(RationaleUpdateEvent e);
	public void updateRationaleStatus(RationaleUpdateEvent e);
	public void addNewElement(RationaleUpdateEvent e);
}
