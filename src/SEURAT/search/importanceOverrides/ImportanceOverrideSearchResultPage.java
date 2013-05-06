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

package SEURAT.search.importanceOverrides;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

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
import org.eclipse.search.ui.NewSearchUI;
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
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;

import SEURAT.editors.OpenRationaleEditorAction;
import SEURAT.search.Utilities;
import SEURAT.search.statusOverrides.StatusOverrideSearchResultMatch;
import SEURAT.search.statusOverrides.StatusOverrideSearchResultPage;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.tasks.RationaleTaskLabelProvider;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

public class ImportanceOverrideSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage 
{
	void openRationaleInEditor(ImportanceOverrideSearchResultMatch pMatch)
	{
		Class l_editorClass = Utilities.getEditorFromRationale(pMatch.getRationale());		
		if( l_editorClass == null ) {
			boolean canceled = pMatch.getRationale().display(this.getControl().getDisplay());
			if (!canceled)
			{
				RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
				evt.fireUpdateEvent(pMatch.getRationale(), this.getControl().getDisplay(), UpdateType.UPDATE);		
			}			
			return;
		}
		RationaleExplorer l_explorer = Utilities.getExplorer(pMatch.getRationale());
		TreeParent l_parent = Utilities.getTreeParentFromElement(l_explorer, pMatch.getRationale());
		
		OpenRationaleEditorAction l_openAction = new OpenRationaleEditorAction(
				l_editorClass, l_explorer, l_parent
		);
		l_openAction.run();
	}
	
	class OpenRationaleAction extends Action
	{
		ImportanceOverrideSearchResultPage m_Page;
		public OpenRationaleAction(ImportanceOverrideSearchResultPage pPage)
		{
			m_Page = pPage;		
			this.setText("Open Rationale In Editor");
		}
		
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
				
				if( !(l_genericMatch instanceof ImportanceOverrideSearchResultMatch) )
					continue;
				
				ImportanceOverrideSearchResultMatch l_match = (ImportanceOverrideSearchResultMatch)l_genericMatch;
				openRationaleInEditor(l_match);
			}
		}		
	}
		
	@Override
	protected void fillContextMenu(IMenuManager mgr) {
		mgr.add(new OpenRationaleAction(this));
	}

	@Override
	protected void fillToolbar(IToolBarManager tbm) {
		// TODO Auto-generated method stub
		super.fillToolbar(tbm);
	}

	@Override
	protected void showMatch(Match pMatch, int pOffset, int pLength, boolean pActivate) 
		throws PartInitException 
	{
		if( !(pMatch instanceof ImportanceOverrideSearchResultMatch) )
			return;
		
		ImportanceOverrideSearchResultMatch l_match = (ImportanceOverrideSearchResultMatch)pMatch;
		openRationaleInEditor(l_match);		
	}

	TableViewer m_TableViewer;
	TableColumn m_TypeIconColumn, m_NameColumn, m_TypeColumn, m_ImportanceColumn;
	TableContentProvider m_TableContentProvider = new TableContentProvider();
	TableLabelProvider m_TableLabelProvider = new TableLabelProvider();
	
	public static final int g_TypeIconColumn = 0, g_NameColumn = 1, g_TypeColumn = 2, g_ImportanceColumn = 3;
	
	private class TableContentProvider implements IStructuredContentProvider
	{
		public void dispose() {
			return;			
		}

		private Object[] m_Matches = new Object[0];
		
		public Object[] getElements(Object arg0) {
			return m_Matches;
		}

		public void inputChanged(Viewer pViewer, Object pFirst, Object pSecond) {
			if( pFirst != pSecond ) {
				clear();
			}			
		}
		
		public void updateElements(Object[] pNewInput)
		{
	        if (pNewInput.length > 0) {
	            m_TableViewer.remove(m_Matches);
	            Object [] l_newInput = new Object [pNewInput.length];
	            System.arraycopy (pNewInput, 0, l_newInput, 0, pNewInput.length);
	            m_Matches = l_newInput;
	            m_TableViewer.add (m_Matches);
	            m_TableViewer.refresh (true, true);
	        }
		}
		
		public void clear() {
			m_TableViewer.remove(m_Matches);
			m_Matches = new Object[0];
			m_TableViewer.refresh(true, true);			
		}		
	}
	
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof ImportanceOverrideSearchResultMatch) )
				return null;
			ImportanceOverrideSearchResultMatch l_match = (ImportanceOverrideSearchResultMatch)pMatch;
			
			Image l_result = null;
			
			switch(pColumnIndex)
			{
			case g_TypeIconColumn:
				l_result = Utilities.getRationaleElementIcon(l_match.getRationale());
				break;
			}
			
			return l_result;
		}

		public String getColumnText(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof ImportanceOverrideSearchResultMatch) )
				return "ERROR";
			
			String l_result = "";
			ImportanceOverrideSearchResultMatch l_data = (ImportanceOverrideSearchResultMatch)pMatch;
			
			switch( pColumnIndex )
			{
			case g_NameColumn:
				l_result = l_data.getRationale().getName();
				break;
			case g_TypeColumn:
				l_result = l_data.getRationale().getElementType().toString();
				break;	
			case g_ImportanceColumn:
				// molerjc - cast element as appropriate type and get the importance
				RationaleElement elt = l_data.getRationale();
				if (elt.getElementType() == RationaleElementType.ARGUMENT) {
					l_result = ((Argument) elt).getImportance().toString();
				}
				else if (elt.getElementType() == RationaleElementType.CLAIM) {
					l_result = ((Claim) elt).getImportance().toString();
				}
				break;
			}
			return l_result;
		}
	}

	class TableSorter extends ViewerComparator implements SelectionListener
	{
		public void widgetDefaultSelected(SelectionEvent pEvent) {
			widgetSelected(pEvent);			
		}

		public void widgetSelected(SelectionEvent pEvent) {
			m_TableViewer.setComparator(this);	
		}

		private int m_SortColumn;
		
		public TableSorter(int pSortColumn) {
			super();
			m_SortColumn = pSortColumn;
		}
		
		/* Compare our elements
		 * Method declared on ViewerSorter.
		 */
		public int compare(Viewer pViewer, Object pOne, Object pTwo) {
			if( pOne == null || !(pOne instanceof ImportanceOverrideSearchResultMatch) )
				return 0;
			if( pTwo == null || !(pTwo instanceof ImportanceOverrideSearchResultMatch) )
				return 0;			

			// Leverage the label provider to make this function really simple
			return m_TableLabelProvider.getColumnText(pOne, m_SortColumn).compareTo
			(
				m_TableLabelProvider.getColumnText(pTwo, m_SortColumn)
			);
		}
	}
		
    public ImportanceOverrideSearchResultPage () {
        super (AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT);
    }

    public class TableResizeListener extends ControlAdapter 
    {
    	private Table m_Table;
    	
    	private int m_TotalWeight;
    	private class ColumnData
    	{
    		public TableColumn m_Column;
    		public int m_Weight;
    		
    		public ColumnData(TableColumn pColumn, int pWeight)
    		{
    			m_Column = pColumn;
    			m_Weight = pWeight;
    		}
    	}
    	ArrayList<ColumnData> m_Columns;
    	
    	public void addColumn(TableColumn pColumn, int pWeight)
    	{
    		m_Columns.add(new ColumnData(pColumn, pWeight));
    		m_TotalWeight += pWeight;
    	}
    	
    	public TableResizeListener(Table l_table)
    	{
    		m_Table = l_table;
    		m_Columns = new ArrayList<ColumnData>();
    	}
    	
		public void controlResized(ControlEvent e) {
			Rectangle area = m_Table.getParent().getClientArea();
			Point size = m_Table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			ScrollBar vBar = m_Table.getVerticalBar();
			int width = area.width - m_Table.computeTrim(0,0,0,0).width - vBar.getSize().x;
			if (size.y > area.height + m_Table.getHeaderHeight()) {
				// Subtract the scrollbar width from the total column width
				// if a vertical scrollbar will be required
				Point vBarSize = vBar.getSize();
				width -= vBarSize.x;
			}
			Point oldSize = m_Table.getSize();
			if (oldSize.x > area.width) {
				
				for( ColumnData l_column : m_Columns )
					l_column.m_Column.setWidth(width * l_column.m_Weight / m_TotalWeight);
				
				m_Table.setSize(area.width, area.height);
			} else {
				// table is getting bigger so make the table 
				// bigger first and then make the columns wider
				// to match the client area width
				m_Table.setSize(area.width, area.height);

				for( ColumnData l_column : m_Columns )
					l_column.m_Column.setWidth(width * l_column.m_Weight / m_TotalWeight);
			}
		}
    }
    
	@Override
	protected void configureTableViewer(TableViewer pViewer) {
		m_TableViewer = pViewer;
		
		m_TableViewer.setContentProvider(m_TableContentProvider);
		m_TableViewer.setLabelProvider(m_TableLabelProvider);
		
		Table l_table = pViewer.getTable();
		
		l_table.setHeaderVisible(true);
		
		m_TypeIconColumn = new TableColumn(l_table, SWT.LEFT);
		m_NameColumn = new TableColumn(l_table, SWT.LEFT);
		m_TypeColumn = new TableColumn(l_table, SWT.LEFT);
		m_ImportanceColumn = new TableColumn(l_table, SWT.LEFT);

		
		m_TypeIconColumn.setWidth(25);
		m_NameColumn.setWidth(1000);
		m_TypeColumn.setWidth(1000);
		m_ImportanceColumn.setWidth(1000);
		
		TableResizeListener l_listener = new TableResizeListener(l_table);
		l_listener.addColumn(m_TypeIconColumn, 5);
		l_listener.addColumn(m_NameColumn, 45);
		l_listener.addColumn(m_TypeColumn, 25);
		l_listener.addColumn(m_ImportanceColumn, 25);

		l_table.getParent().addControlListener(l_listener);
		
		m_TypeIconColumn.setText("");
		m_NameColumn.setText("Name");
		m_TypeColumn.setText("Type");
		m_ImportanceColumn.setText("Importance");
		
		m_NameColumn.addSelectionListener(new TableSorter(g_NameColumn));
		m_TypeColumn.addSelectionListener(new TableSorter(g_TypeColumn));
		m_ImportanceColumn.addSelectionListener(new TableSorter(g_ImportanceColumn));

		
		m_TypeIconColumn.pack();
		m_NameColumn.pack();
		m_TypeColumn.pack();
		m_ImportanceColumn.pack();
		l_table.pack();
		l_table.getParent().pack();		
	}

	@Override
	protected void configureTreeViewer(TreeViewer pViewer) {
		// TODO: Implement this, use the requirements as parents and the alternatives as children
		System.err.println("StatusOverrideSearchResultPage: Tree View Not Currently Supported");
	}

	@Override
	protected void clear() {
		m_TableContentProvider.clear();
	}
	
	@Override
	protected void elementsChanged(Object[] pElements) {
		m_TableContentProvider.updateElements(pElements);
	}
}

