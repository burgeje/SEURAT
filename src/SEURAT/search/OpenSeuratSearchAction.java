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
