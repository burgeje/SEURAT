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

