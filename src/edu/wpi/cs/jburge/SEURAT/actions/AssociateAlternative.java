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

package edu.wpi.cs.jburge.SEURAT.actions;

import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

import org.eclipse.swt.widgets.Display;

import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;


/**
 * This class is used to associate an alternative with the Java code that
 * implements it. This is invoked using an extension to the popup menus
 * on the package explorer. The extension is defined in plugin.xml
 * @author wangw2
 *
 */
public class AssociateAlternative extends ActionDelegate implements IViewActionDelegate{
	
	/**
	 * The item that has been selected in the Package Explorer
	 */
	IStructuredSelection selection;
	
	/**
	 * Detects when the selected item has changed. In this case, the selected
	 * item refers to the java element in the package explorer.
	 */
	public void selectionChanged (IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
	/**
	 * Executes the action. We get the selected java element and use it to create
	 * a rationale update event to kick off the association (we need to somehow get from
	 * the code to the rationale tree so we know what rationale element we are
	 * associating with! 
	 * @param action - the action
	 */	
	public void run (IAction action) 
	{
		IJavaElement navigatorSelection;
		if (selection.getFirstElement() instanceof IJavaElement)
		{
			
			navigatorSelection = (IJavaElement) selection.getFirstElement();
			RationaleUpdateEvent evt2 = new RationaleUpdateEvent(this);
			evt2.fireAssociateEvent(navigatorSelection, Display.getDefault(),UpdateType.ASSOCIATE);
		}
		
	}
	
	public AssociateAlternative() {
		super();
	}
	
	public void init(IViewPart view) {
		
	}
	
}

