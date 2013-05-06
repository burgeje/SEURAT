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
 * @(#)RationaleEntry.java 1.0 03/10/07
 *
 * You can modify the template of this file in the
 * directory ..\JCreator\Templates\Template_1\Project_Name.java
 *
 * You can also create your own project template by making a new
 * folder in the directory ..\JCreator\Template\. Use the other
 * templates as examples.
 *
 */
package SEURAT.xmlIO;


import javax.swing.JPanel;

import java.awt.*;
import java.io.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;


/**
 * Prompts the user for an XML file to use to input rationale from XML.
 * @author burgeje
 *
 */
public class RationaleEntry extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String ratFile;
	
	public RationaleEntry() 
	throws IOException 
	{
		Frame df = new Frame();
		FileDialog d = new FileDialog(df, "Select Rationale File");
		d.setFile("*.xml");
		d.setDirectory(".");
		d.setVisible(true);
		//	d.show();
		ratFile = "*.*";
		ratFile = d.getFile();
		
		if (ratFile.indexOf(".") <= 0)
		{
			ratFile = ratFile + ".info";
		}
		
		RationaleDB.setFile(ratFile);  
		RationaleDB.loadRationaleDB();;
	}
}

