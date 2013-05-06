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

package SEURAT.search.statusOverrides;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PartInitException;
import SEURAT.editors.*;
import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.views.*;
import edu.wpi.cs.jburge.SEURAT.inference.UpdateManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleStatus;
import edu.wpi.cs.jburge.SEURAT.tasks.*;

import java.util.*;

import SEURAT.editors.OpenRationaleEditorAction;

import SEURAT.search.*;

import org.eclipse.ui.*;

public class StatusOverrideSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage 
{
	/**
	 * @param pMatch the search result to open in an editor
	 */
	void openRationaleInEditor(StatusOverrideSearchResultMatch pMatch)
	{
		Utilities.openEditorForRationale(pMatch.getRationale());
	}
	
	/**
	 * The context menu action for opening rationale elements
	 * 
	 * @author hannasm
	 */
	class OpenRationaleAction extends Action
	{
		/**
		 * Storage For Search Result Page Using This Action
		 */
		StatusOverrideSearchResultPage m_Page;
		/**
		 * @param pPage Search Result page the action will be opening from
		 */
		public OpenRationaleAction(StatusOverrideSearchResultPage pPage)
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
				
				if( !(l_genericMatch instanceof StatusOverrideSearchResultMatch) )
					continue;
				
				StatusOverrideSearchResultMatch l_match = (StatusOverrideSearchResultMatch)l_genericMatch;
				openRationaleInEditor(l_match);
			}
		}		
	}
	
	/**
	 * Context Menu Action For Cancelling A Status Override
	 * @author hannasm
	 */
	class RemoveStatusOverrideAction extends Action
	{
		/**
		 * The Search Result Page Which Is Using This Action
		 */
		StatusOverrideSearchResultPage m_Page;
		/**
		 * @param pPage the search result page to use when performing the action
		 */
		public RemoveStatusOverrideAction(StatusOverrideSearchResultPage pPage)
		{
			m_Page = pPage;		
			this.setText("Remove Status Override");
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
				
				if( !(l_genericMatch instanceof StatusOverrideSearchResultMatch) )
					continue;
				
				StatusOverrideSearchResultMatch l_match = (StatusOverrideSearchResultMatch)l_genericMatch;
				
				l_match.getStatus().setOverride(false);
				l_match.getStatus().toDatabase(l_match.getStatus().getParent());
				
				// handle updating of task list - duplicated code from rationale explorer, somewhat inefficient way of doing it
				Vector<RationaleStatus> updatedStatus = new Vector<RationaleStatus>();
				updatedStatus.add(l_match.getStatus());
				RationaleTaskList tlist = RationaleTaskList.getHandle();
				tlist.addTasks(updatedStatus);
				//need to update the rationale tree also
				//need to update our tree item!
				UpdateManager mgr = UpdateManager.getHandle();
				Iterator<RationaleStatus> statusI = updatedStatus.iterator();
				while (statusI.hasNext())
				{
					RationaleStatus stat = (RationaleStatus) statusI.next();
					RationaleElement ourEle = RationaleDB.getRationaleElement(stat.getParent(), stat.getRationaleType());				
					mgr.addUpdate(stat.getParent(), ourEle.getName(), stat.getRationaleType());					
				}
				mgr.makeTreeUpdates();
			}
			
			NewSearchUI.runQueryInBackground(new StatusOverrideSearchQuery());
		}		
	}
	
	/* Add the actions which are relevant to the search result
	 * page while omitting those which are added by default
	 * however are irrelevant.
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void fillContextMenu(IMenuManager mgr) {
		mgr.add(new OpenRationaleAction(this));
		mgr.add(new RemoveStatusOverrideAction(this));
	}

	/* TODO: Add toolbar buttons here
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillToolbar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	protected void fillToolbar(IToolBarManager tbm) {
		// TODO Auto-generated method stub
		super.fillToolbar(tbm);
	}

	/* Respond To Double Click Event
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
	 */
	@Override
	protected void showMatch(Match pMatch, int pOffset, int pLength, boolean pActivate) 
		throws PartInitException 
	{
		if( !(pMatch instanceof StatusOverrideSearchResultMatch) )
			return;
		
		StatusOverrideSearchResultMatch l_match = (StatusOverrideSearchResultMatch)pMatch;
		openRationaleInEditor(l_match);		
	}

	/**
	 * Control For The Search Result Table
	 */
	TableViewer m_TableViewer;
	/**
	 * Columns Displayed In The Search Result Table
	 */
	TableColumn m_StatusIconColumn, m_StatusTypeColumn, m_DescriptionColumn, 
		m_RationaleElementColumn, m_RationaleTypeColumn;
	/**
	 * Provider Of The Search Results 
	 */
	SearchResultPage_TableContentProvider m_TableContentProvider;
	/**
	 * Provider Of Data For Each Cell In The Table
	 */
	TableLabelProvider m_TableLabelProvider = new TableLabelProvider();
	
	/**
	 * Indexes into the search result table
	 */
	public static final int g_StatusIconColumn = 0, g_StatusTypeColumn = 1, g_DescriptionColumn = 2,
		g_RationaleElementColumn = 3, g_RationaleTypeColumn = 4;

	/**
	 * Provide data for each cell in the search result table.
	 * 
	 * @author hannasm
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/* Retrieve image or return null if no image needed.
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof StatusOverrideSearchResultMatch) )
				return null;
			StatusOverrideSearchResultMatch l_match = (StatusOverrideSearchResultMatch)pMatch;
			
			Image l_result = null;
			
			switch(pColumnIndex)
			{
			case g_StatusIconColumn:
				l_result = Utilities.getErrorLevelIcon(l_match.getStatus().getStatus());
				break;
			}
			return l_result;
		}

		/* Retrieve column text or return an empty string.
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof StatusOverrideSearchResultMatch) )
				return "ERROR";
			
			String l_result = "";
			StatusOverrideSearchResultMatch l_data = (StatusOverrideSearchResultMatch)pMatch;
			
			switch( pColumnIndex )
			{
			case g_StatusTypeColumn:				
				l_result = 	l_data.getStatus().getStatusType().toString();				
				break;
			case g_DescriptionColumn:
				l_result = l_data.getStatus().getDescription();
				break;
			case g_RationaleElementColumn:
				l_result = l_data.getRationale().getName();
				break;
			case g_RationaleTypeColumn:
				l_result = l_data.getStatus().getRationaleType().toString();
				break;			
			}
			return l_result;
		}
	}
		
    /**
     * Construct Search Result Page, only allows table view of results
     */
    public StatusOverrideSearchResultPage () {
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
		
		m_StatusIconColumn = new TableColumn(l_table, SWT.LEFT);
		m_StatusTypeColumn = new TableColumn(l_table, SWT.LEFT);
		m_DescriptionColumn = new TableColumn(l_table, SWT.LEFT);
		m_RationaleElementColumn = new TableColumn(l_table, SWT.LEFT);
		m_RationaleTypeColumn = new TableColumn(l_table, SWT.LEFT);		
		
		m_StatusIconColumn.setWidth(25);
		m_StatusTypeColumn.setWidth(1000);
		m_DescriptionColumn.setWidth(1000);
		m_RationaleElementColumn.setWidth(1000);
		m_RationaleTypeColumn.setWidth(1000);
		
		SearchResultPage_TableResizeListener l_listener = new SearchResultPage_TableResizeListener(l_table);
		l_listener.addColumn(m_StatusIconColumn, 5);
		l_listener.addColumn(m_StatusTypeColumn, 10);
		l_listener.addColumn(m_DescriptionColumn, 45);
		l_listener.addColumn(m_RationaleElementColumn, 45);
		l_listener.addColumn(m_RationaleTypeColumn, 25);
		l_table.getParent().addControlListener(l_listener);
		
		m_StatusIconColumn.setText("");
		m_StatusTypeColumn.setText("Status Type");
		m_DescriptionColumn.setText("Description");
		m_RationaleElementColumn.setText("Rationale Element");
		m_RationaleTypeColumn.setText("Rationale Type");
		
		m_StatusIconColumn.addSelectionListener(new SearchResultPage_IconTableSorter(g_StatusIconColumn, pViewer));
		m_StatusTypeColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_StatusTypeColumn, pViewer));
		m_DescriptionColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_DescriptionColumn, pViewer));
		m_RationaleElementColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_RationaleElementColumn, pViewer));
		m_RationaleTypeColumn.addSelectionListener(new SearchResultPage_StringTableSorter(g_RationaleTypeColumn, pViewer));
		
		m_StatusIconColumn.pack();
		m_StatusTypeColumn.pack();
		m_DescriptionColumn.pack();
		m_RationaleElementColumn.pack();
		m_RationaleTypeColumn.pack();	
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

