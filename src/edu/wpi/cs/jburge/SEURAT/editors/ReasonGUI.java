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

package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * ReasonGUI is the dialog box that lets the user type in a reason when
 * they change a field in the rationale.
 * Changed by Team 1: eliminated AWT dialogs (caused Eclipse to crash on Mac OS).
 * @author burgeje
 *
 */
public class ReasonGUI implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3195632975620802133L;
	
	/**
	 * The reason
	 */
	private String reason;
	
	/**
	 * Constructor for the GUI
	 */
	public ReasonGUI()
	{
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
				"Rationale Status Changed", "Enter Reason for Status Change:",
				null, null);

		if (dlg.open() == Window.OK) {
			reason = dlg.getValue();

		}
	}
	
	/**
	 * Get the reason
	 * @return reason for change that the user entered
	 */
	public String getReason()
	{
		return reason;
	}	
}
