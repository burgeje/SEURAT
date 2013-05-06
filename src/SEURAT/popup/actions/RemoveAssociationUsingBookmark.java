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

package SEURAT.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This method does not appear to do anything and is not invoked.
 * See RemoveBookmarkAssociation in the actions package.
 * @author burgeje
 *
 */
public class RemoveAssociationUsingBookmark implements IObjectActionDelegate {
	
	/**
	 * Constructor for Action1.
	 */
	public RemoveAssociationUsingBookmark() {
		super();
	}
	
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	
	/**
	 * Run the action
	 * @param action the action to execute
	 */
	public void run(IAction action) {
		Shell shell = new Shell();
		MessageDialog.openInformation(
				shell,
				"SEURAT Plug-in",
		"Remove Association was executed.");
	}
	
	/**
	 * Called when the selection changes
	 * @param action the action
	 * @param selection the selection
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
}
