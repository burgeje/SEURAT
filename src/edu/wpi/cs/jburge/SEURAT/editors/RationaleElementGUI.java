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
 * Created on Apr 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;


import java.awt.*;
import java.io.Serializable;

/**
 * Not used. Perhapse a pre-cursor to "NewRationaleElementGUi"?
 */
public class RationaleElementGUI extends Dialog implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1331449023774558576L;
	protected boolean canceled;
	
	public RationaleElementGUI (Frame parent, String str, boolean bl)
	{
		super(parent, str, bl);
		canceled = false;
	}
	
	public RationaleElement getItem()
	{
		return null; //this should not get called...
	}
	
	public boolean getCanceled()
	{
		return canceled;
	}
}
