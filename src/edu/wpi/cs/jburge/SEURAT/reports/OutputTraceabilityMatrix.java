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
/**
 * Team 1, Release 1- October 17, 2007
 * Revised- October 23, 2007
 */
package edu.wpi.cs.jburge.SEURAT.reports;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Vector;
import java.util.Iterator;
import edu.wpi.cs.jburge.SEURAT.reports.TraceabilityMatrixDisplay.TraceabilityElement;

/**
 * Used to output a traceability matrix.  Currently outputs to HTML,
 * but should be able to be modified to output in some other format
 * without having to change anything in any other classes.
 * 
 * @author molerjc
 */
public class OutputTraceabilityMatrix {
	
	/**
	 *  our shell
	 */
	private Shell shell;
	/**
	 * The traceability matrix
	 */
	private Vector<TraceabilityElement> matrix;
	/**
	 * FileDialog to get the filepath
	 */
	private FileDialog path;
	/**
	 * Constructs the traceability matrix report and allows the user to select a file path,
	 * then writes it to file.
	 */
	public OutputTraceabilityMatrix(Composite parent, Vector<TraceabilityElement> m) {
		matrix = m;
		shell = new Shell();
		
		// Open a FileDialog so the user can select the path
		path = new FileDialog(shell, SWT.SAVE);
		String[] ext = {"*.html"};
		String[] name = {"HTML (*.html)"};
		path.setFilterExtensions(ext);
		path.setFilterNames(name);
		
		shell.pack();
		
		// Open the path that the user selected
		String filePath = path.open();
		
		// Construct a report in HTML
		// May add support for other formats
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(filePath));
			fw.write("<html><head><title>Requirements Traceability Matrix</title></head>\n");
			fw.write("<body><table border='1'><tr><th>Requirement</th><th>Alternative</th>"+
					"<th>Relationship</th><th>Associated Artifact</th></tr>\n");
			Iterator<TraceabilityElement> iter = matrix.iterator();
			while (iter.hasNext()) {
				String[] data = iter.next().toString().split("\t");
				fw.write("<tr><td>"+data[0]+"</td><td>"+data[1]+"</td><td>"+data[2]+"</td><td>"+
						data[3]+"</td></tr>\n");
			}
			fw.write("</table></body></html>");
			fw.close();
		} catch (java.io.IOException ioe) {
			throw new RuntimeException("Error writing the Requirements Traceability Matrix to file.");
		}
		// we are done
		shell.dispose();
	}
}
