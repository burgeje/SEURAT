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

package SEURAT.search;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.*;
import org.eclipse.core.commands.*;
import org.eclipse.search.ui.NewSearchUI;

/*
 * Action Class Which Embedded In the Eclipse Search Menu
 * And Possibly Other Locations which provides easy access
 * to the SEURAT Search Page
 */
public class OpenSeuratSearchAction implements IWorkbenchWindowActionDelegate
{
	/**
	 * Instance Variable For Accessing The Workbench
	 */
	private IWorkbenchWindow m_Window;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		m_Window = null;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow pWindow) {
		m_Window = pWindow;		
	}

	/*
	 * Open The SEURAT Search Page
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if( m_Window == null ||
			m_Window.getActivePage() == null )
		{
			// TODO: JDT generates an error message here but this should rarely happen anyway
			return;
		}
		NewSearchUI.openSearchDialog(m_Window, "SEURAT.search.searchPage");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// comment from jdt plugin: do nothing since the action isn't selection dependent.		
	}
}
