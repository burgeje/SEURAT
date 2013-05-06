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

package SEURAT.search.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;

import SEURAT.editors.OpenRationaleEditorAction;
import SEURAT.search.*;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * Implementation of the Eclipse Search page for
 * displaying results of an entity search
 * 
 * @author hannasm
 *
 */
public class EntitySearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage 
{
	/**
	 * @param pMatch a result from the entity search to display in an editor
	 */
	void openRationaleInEditor(EntitySearchResultMatch pMatch)
	{
		Utilities.openEditorForRationale(pMatch.getRationale());
	}

	/**
	 * Context menu action for displaying a search result in
	 * the rationale explorer.
	 * 
	 * @author hannasm
	 *
	 */
	class ShowRationaleAction extends Action
	{
		/**
		 * The search result page which requested the action
		 */
		EntitySearchResultPage m_Page;
		/**
		 * @param pPage the search result page which requested the action
		 */
		public ShowRationaleAction(EntitySearchResultPage pPage)
		{
			m_Page = pPage;
			this.setText("Show Rationale In Rationale Explorer");
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			ISelection l_selection = m_Page.m_TableViewer.getSelection();
			
			if( !(l_selection instanceof IStructuredSelection) )
				return;
			
			IStructuredSelection l_structuredSelection = (IStructuredSelection)l_selection;

			// This is really a hack,
			// but using the display of the search UI is also not
			// an unreasonable thread to use for broadcasting
			// the event notification
			Display l_display = m_Page.m_TableViewer.getTable().getDisplay();
			
			for( Iterator l_i = l_structuredSelection.iterator();
				 l_i.hasNext() ; )
			{
				Object l_genericMatch = l_i.next();
				
				if( !(l_genericMatch instanceof EntitySearchResultMatch) )
					continue;
				
				EntitySearchResultMatch l_match = (EntitySearchResultMatch)l_genericMatch;
				// broadcast a 'find this rationale entity' event.
				RationaleUpdateEvent evt2 = new RationaleUpdateEvent(this);
				evt2.fireUpdateEvent(l_match.getRationale(), l_display, UpdateType.FIND);
			}
		}
	}
	
	/**
	 * Context menu action for displaying a search result in an editor
	 * 
	 * @author hannasm
	 *
	 */
	class OpenRationaleAction extends Action
	{
		/**
		 * Search result page which generated the action
		 */
		EntitySearchResultPage m_Page;
		/**
		 * @param pPage Search result page which generated the action
		 */
		public OpenRationaleAction(EntitySearchResultPage pPage)
		{
			m_Page = pPage;		
			this.setText("Open Rationale In Editor");
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			ISelection l_selection = m_Page.m_TableViewer.getSelection();
			
			if( !(l_selection instanceof IStructuredSelection) )
				return;
			
			IStructuredSelection l_structuredSelection = (IStructuredSelection)l_selection;

			for( Iterator l_i = l_structuredSelection.iterator();
				 l_i.hasNext() ; )
			{
				Object l_genericMatch = l_i.next();
				
				if( !(l_genericMatch instanceof EntitySearchResultMatch) )
					continue;
				
				EntitySearchResultMatch l_match = (EntitySearchResultMatch)l_genericMatch;
				openRationaleInEditor(l_match);
			}
		}		
	}
			
	/* Add relevant actions to the context menu while
	 * omitting those which are not relevant to the search results
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void fillContextMenu(IMenuManager mgr) {
		mgr.add(new OpenRationaleAction(this));
		mgr.add(new ShowRationaleAction(this));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillToolbar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	protected void fillToolbar(IToolBarManager tbm) {
		// TODO Auto-generated method stub
		super.fillToolbar(tbm);
	}

	/* Respond to a search result being double clicked
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
	 */
	@Override
	protected void showMatch(Match pMatch, int pOffset, int pLength, boolean pActivate) 
		throws PartInitException 
	{
		if( !(pMatch instanceof EntitySearchResultMatch) )
			return;
		
		EntitySearchResultMatch l_match = (EntitySearchResultMatch)pMatch;
		openRationaleInEditor(l_match);		
	}

	/**
	 * The search result table control
	 */
	TableViewer m_TableViewer;
	/**
	 * The columns which are displayed in the search result table
	 */
	TableColumn m_TypeIconColumn, m_NameColumn, m_TypeColumn;
	/**
	 * The provider of search results
	 */
	SearchResultPage_TableContentProvider m_TableContentProvider;
	/**
	 * The provider of data for each cell in the search result table
	 */
	TableLabelProvider m_TableLabelProvider = new TableLabelProvider();
	
	/**
	 * Index to each column in the search result table
	 */
	public static final int g_TypeIconColumn = 0, g_NameColumn = 1, g_TypeColumn = 2;
	
	/**
	 * Provide data for each cell in the search result table
	 * 
	 * @author hannasm
	 *
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/* Return the image to display in each cell or
		 * null if no image should be presented
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof EntitySearchResultMatch) )
				return null;
			EntitySearchResultMatch l_match = (EntitySearchResultMatch)pMatch;
			
			Image l_result = null;
			
			switch(pColumnIndex)
			{
			case g_TypeIconColumn:
				l_result = Utilities.getRationaleElementIcon(l_match.getRationale());
				break;
			}
			return l_result;
		}

		/* Return the text to display in each cell
		 * or the empty string if no text should be presented
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof EntitySearchResultMatch) )
				return "ERROR";
			
			String l_result = "";
			EntitySearchResultMatch l_data = (EntitySearchResultMatch)pMatch;
			
			switch( pColumnIndex )
			{
			case g_NameColumn:
				l_result = l_data.getRationale().getName();
				break;
			case g_TypeColumn:
				l_result = l_data.getRationale().getElementType().toString();
				break;	
			}
			return l_result;
		}
	}
	
    /**
     * Construct an entity search result page that only supports
     * a table view of the results
     */
    public EntitySearchResultPage() {
        super (AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT);
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void configureTableViewer(TableViewer pViewer) {
		m_TableViewer = pViewer;
		
		m_TableContentProvider = new SearchResultPage_TableContentProvider();
		m_TableViewer.setContentProvider(m_TableContentProvider);
		m_TableViewer.setLabelProvider(m_TableLabelProvider);
		
		Table l_table = pViewer.getTable();
		
		l_table.setHeaderVisible(true);
		
		m_TypeIconColumn = new TableColumn(l_table, SWT.LEFT);
		m_NameColumn = new TableColumn(l_table, SWT.LEFT);
		m_TypeColumn = new TableColumn(l_table, SWT.LEFT);
		
		m_TypeIconColumn.setWidth(25);
		m_NameColumn.setWidth(1000);
		m_TypeColumn.setWidth(1000);
		
		SearchResultPage_TableResizeListener l_listener = new SearchResultPage_TableResizeListener(l_table);
		l_listener.addColumn(m_TypeIconColumn, 5);
		l_listener.addColumn(m_NameColumn, 45);
		l_listener.addColumn(m_TypeColumn, 25);
		l_table.getParent().addControlListener(l_listener);
		
		m_TypeIconColumn.setText("");
		m_NameColumn.setText("Name");
		m_TypeColumn.setText("Type");
		
		m_TypeIconColumn.addSelectionListener(new SearchResultPage_IconTableSorter(g_TypeIconColumn, pViewer));
		m_NameColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_NameColumn, pViewer));
		m_TypeColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_TypeColumn, pViewer));
		
		m_TypeIconColumn.pack();
		m_NameColumn.pack();
		m_TypeColumn.pack();
		l_table.pack();
		l_table.getParent().pack();		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	protected void configureTreeViewer(TreeViewer pViewer) {
		// TODO: Implement this, use the requirements as parents and the alternatives as children
		System.err.println("StatusOverrideSearchResultPage: Tree View Not Currently Supported");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
	 */
	@Override
	protected void clear() {
		m_TableContentProvider.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
	 */
	@Override
	protected void elementsChanged(Object[] pElements) {
		m_TableContentProvider.updateElements(pElements);
	}
}

