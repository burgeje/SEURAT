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
 * Created on Jan 5, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * Not used. The original idea was to use this to build special enumeration boxes but
 * they must not have been necessary.
 */
public class EnumerationBox extends JComboBox {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param arg0
	 */
	public EnumerationBox(ComboBoxModel arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public EnumerationBox(Object[] arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param arg0
	 */
	public EnumerationBox(Vector arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public EnumerationBox() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public EnumerationBox(Enumeration typeEnum, String selected) {
		int i = 0;
		while (typeEnum.hasMoreElements())
		{
			String newItem = (String) typeEnum.nextElement();
			this.addItem(newItem);
			if (newItem.equals(selected))
			{
				this.setSelectedItem(this.getItemAt(i));
			}
			i++;
		}
	}
}
