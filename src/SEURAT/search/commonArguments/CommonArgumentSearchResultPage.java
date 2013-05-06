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

package SEURAT.search.commonArguments;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
 * Search Result Page capable of displaying the results
 * of a common argument search
 * 
 * @author hannasm
 *
 */
public class CommonArgumentSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage 
{
	/**
	 * @param pMatch the search result to display in an editor
	 */
	void openRationaleInEditor(CommonArgumentSearchResultMatch pMatch)
	{
		Utilities.openEditorForRationale(pMatch.getRationale());
	}
	
	/**
	 * Context menu action for opening a search result in an editor
	 * 
	 * @author hannasm
	 *
	 */
	class OpenRationaleAction extends Action
	{
		/**
		 * The search result performing the action
		 */
		CommonArgumentSearchResultPage m_Page;
		/**
		 * @param pPage The search result performing the action
		 */
		public OpenRationaleAction(CommonArgumentSearchResultPage pPage)
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
				
				if( !(l_genericMatch instanceof CommonArgumentSearchResultMatch) )
					continue;
				
				CommonArgumentSearchResultMatch l_match = (CommonArgumentSearchResultMatch)l_genericMatch;
				openRationaleInEditor(l_match);
			}
		}		
	}
	
	/* Add relevant actions to the context menu and omit those 
	 * which are irrelevant
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillContextMenu(IMenuManager mgr) {
		mgr.add(new OpenRationaleAction(this));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillToolbar(org.eclipse.jface.action.IToolBarManager)
	 */
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
		if( !(pMatch instanceof CommonArgumentSearchResultMatch) )
			return;
		
		CommonArgumentSearchResultMatch l_match = (CommonArgumentSearchResultMatch)pMatch;
		openRationaleInEditor(l_match);		
	}

	/**
	 * The search result table control
	 */
	TableViewer m_TableViewer;
	/**
	 * The columns displayed in the search result table
	 */
	TableColumn m_TypeIconColumn, m_NameColumn, m_TotalColumn, m_ForColumn, m_AgainstColumn, m_TypeColumn;
	/**
	 * Provider of data to the search result table
	 */
	SearchResultPage_TableContentProvider m_TableContentProvider;
	/**
	 * Provider of data to each cell in the search result table
	 */
	TableLabelProvider m_TableLabelProvider = new TableLabelProvider();
	
	/**
	 * Index of each column in the search result table
	 */
	public static final int g_TypeIconColumn = 0, g_NameColumn = 1, 
		g_TotalColumn = 2, g_ForColumn = 3, g_AgainstColumn = 4, g_TypeColumn = 5;
	
	/**
	 * Provide the data for each cell in the search result table
	 * 
	 * @author hannasm
	 *
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/* Return the image to display in each cell, or
		 * return null if no image should be displayed 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof CommonArgumentSearchResultMatch) )
				return null;
			CommonArgumentSearchResultMatch l_match = (CommonArgumentSearchResultMatch)pMatch;
			
			Image l_result = null;
			
			switch(pColumnIndex)
			{
			case g_TypeIconColumn:
				l_result = Utilities.getRationaleElementIcon(l_match.getRationale());
				break;
			}
			return l_result;
		}

		/* Return the text to be displayed in each cell, or return
		 * the empty string if no text should be displayed
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof CommonArgumentSearchResultMatch) )
				return "ERROR";
			
			String l_result = "";
			CommonArgumentSearchResultMatch l_data = (CommonArgumentSearchResultMatch)pMatch;
			
			switch( pColumnIndex )
			{
			case g_NameColumn:
				l_result = l_data.getRationale().getName();
				break;
			case g_TypeColumn:
				l_result = l_data.getRationale().getElementType().toString();
				break;	
			case g_TotalColumn:
				l_result = l_data.getTotal();
				break;
			case g_ForColumn:
				l_result = l_data.getFor();
				break;
			case g_AgainstColumn:
				l_result = l_data.getAgainst();
				break;
			}
			return l_result;
		}
	}
	
    /**
     * Construct a Common Argument Search Result Page that only
     * supports a table view of the data
     */
    public CommonArgumentSearchResultPage () {
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
		m_TotalColumn = new TableColumn(l_table, SWT.LEFT);
		m_ForColumn = new TableColumn(l_table, SWT.LEFT);
		m_AgainstColumn = new TableColumn(l_table, SWT.LEFT);
		m_TypeColumn = new TableColumn(l_table, SWT.LEFT);
		
		m_TypeIconColumn.setWidth(25);
		m_NameColumn.setWidth(1000);
		m_TotalColumn.setWidth(1000);
		m_ForColumn.setWidth(1000);
		m_AgainstColumn.setWidth(1000);
		m_TypeColumn.setWidth(1000);
		
		SearchResultPage_TableResizeListener l_listener = new SearchResultPage_TableResizeListener(l_table);
		l_listener.addColumn(m_TypeIconColumn, 5);
		l_listener.addColumn(m_NameColumn, 45);
		l_listener.addColumn(m_TotalColumn, 10);
		l_listener.addColumn(m_ForColumn, 10);
		l_listener.addColumn(m_AgainstColumn, 10);
		l_listener.addColumn(m_TypeColumn, 25);
		l_table.getParent().addControlListener(l_listener);
		
		m_TypeIconColumn.setText("");
		m_NameColumn.setText("Name");
		m_TotalColumn.setText("Total");
		m_ForColumn.setText("For");
		m_AgainstColumn.setText("Against");
		m_TypeColumn.setText("Type");
		
		m_TypeIconColumn.addSelectionListener(new SearchResultPage_IconTableSorter(g_TypeIconColumn, pViewer));
		m_NameColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_NameColumn, pViewer));
		m_TypeColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_TypeColumn, pViewer));
		m_TotalColumn.addSelectionListener(new SearchResultPage_NumericTableSorter(g_TotalColumn, pViewer));
		m_ForColumn.addSelectionListener(new SearchResultPage_NumericTableSorter(g_ForColumn, pViewer));
		m_AgainstColumn.addSelectionListener(new SearchResultPage_NumericTableSorter(g_AgainstColumn, pViewer));		
		
		m_TypeIconColumn.pack();
		m_NameColumn.pack();
		m_TotalColumn.pack();
		m_ForColumn.pack();
		m_AgainstColumn.pack();
		m_TypeColumn.pack();
		l_table.pack();
		l_table.getParent().pack();		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	protected void configureTreeViewer(TreeViewer pViewer) {
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

