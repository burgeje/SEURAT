package SEURAT.search.requirements;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.ISearchResultPage;
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
import edu.wpi.cs.jburge.SEURAT.views.*;

import java.util.*;

import SEURAT.editors.OpenRationaleEditorAction;

import org.eclipse.ui.*;

import SEURAT.search.*;
import SEURAT.search.entities.EntitySearchResultMatch;

public class RequirementSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage 
{
	/**
	 * Context menu action for opening a requirement in an
	 * editor.
	 * 
	 * @author hannasm
	 */
	class OpenRequirementAction extends Action
	{
		/**
		 * The search result page which requested this action
		 */
		RequirementSearchResultPage m_Page;
		/**
		 * @param pPage the search result page which request the action
		 */
		public OpenRequirementAction(RequirementSearchResultPage pPage)
		{
			m_Page = pPage;		
			this.setText("Open Requirement");
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
				
				if( !(l_genericMatch instanceof RequirementSearchResultMatch) )
					continue;
				
				RequirementSearchResultMatch l_match = (RequirementSearchResultMatch)l_genericMatch;

				Utilities.openEditorForRationale(l_match.getRequirement());
			}
		}		
	}
	
	/**
	 * Context menu action for opening an argument in an editor
	 *  
	 * @author hannasm
	 */
	class OpenArgumentAction extends Action
	{
		/**
		 * Search result page which requested the action
		 */
		RequirementSearchResultPage m_Page;
		/**
		 * @param pPage the search result page which the action should be performed for
		 */
		public OpenArgumentAction(RequirementSearchResultPage pPage)
		{
			m_Page = pPage;		
			this.setText("Open Argument");
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
				
				if( !(l_genericMatch instanceof RequirementSearchResultMatch) )
					continue;
				
				RequirementSearchResultMatch l_match = (RequirementSearchResultMatch)l_genericMatch;

				if( l_match.getArgument() == null )
					continue;

				Utilities.openEditorForRationale(l_match.getArgument());
			}
		}		
	}
	
	/**
	 * Context menu action for opening both the requirement and
	 * the argument associated with a match
	 * 
	 * @author hannasm
	 */
	class OpenAllAction extends Action
	{
		/**
		 * The search result page which requested the action
		 */
		RequirementSearchResultPage m_Page;
		/**
		 * @param pPage the search result page which this action will be performed for
		 */
		public OpenAllAction(RequirementSearchResultPage pPage)
		{
			m_Page = pPage;		
			this.setText("Open All Associated Rationale");
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
				
				if( !(l_genericMatch instanceof RequirementSearchResultMatch) )
					continue;
				
				RequirementSearchResultMatch l_match = (RequirementSearchResultMatch)l_genericMatch;

				Utilities.openEditorForRationale(l_match.getRequirement());
				
				if( l_match.getArgument() != null )
				{
					Utilities.openEditorForRationale(l_match.getArgument());
				}
			}
		}		
	}
	
	/* Add relevant actions to the context menu while omitting
	 * actions declared by the base class which are irrelevant
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void fillContextMenu(IMenuManager mgr) {
		mgr.add(new OpenRequirementAction(this));
		mgr.add(new OpenArgumentAction(this));
		mgr.add(new OpenAllAction(this));
		
		//super.fillContextMenu(mgr);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillToolbar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	protected void fillToolbar(IToolBarManager tbm) {
		// TODO Auto-generated method stub
		super.fillToolbar(tbm);
	}

	/* Called when a search result is double clicked.
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
	 */
	@Override
	protected void showMatch(Match pMatch, int pOffset, int pLength, boolean pActivate) 
		throws PartInitException 
	{
		RequirementSearchResultMatch l_match = null;
		
		if( !(pMatch instanceof RequirementSearchResultMatch) )
			return;
		l_match = (RequirementSearchResultMatch)pMatch;

		Utilities.openEditorForRationale(l_match.getRequirement());
	}

	/**
	 * The control for the table of search results
	 */
	TableViewer m_TableViewer;
	/**
	 * The columns displayed by the table control
	 */
	TableColumn m_StatusIcon, m_Requirement, m_Argument, m_Status;
	/**
	 * The provider of search results to the table
	 */
	SearchResultPage_TableContentProvider m_TableContentProvider;
	/**
	 * The provider of information to the table cells
	 */
	TableLabelProvider m_TableLabelProvider = new TableLabelProvider();
	
	/**
	 * Index for each column in the search results table
	 */
	public static final int g_StatusIconColumn = 0, g_RequirementColumn = 1,
		g_ArgumentColumn = 2, g_StatusColumn = 3;
	
	/**
	 * Provide icons and text for each cell in the search result table
	 * 
	 * @author hannasm
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/* Return an image for each column, or null if no
		 * image should be displayed.
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof RequirementSearchResultMatch) )
				return null;
			RequirementSearchResultMatch l_match = (RequirementSearchResultMatch)pMatch;
			
			Image l_result = null;
			
			switch(pColumnIndex)
			{
			case g_StatusIconColumn:
				if( l_match.getArgument() != null )
				{
					l_result = Utilities.getRationaleElementIcon(l_match.getRequirement());
				}
				else
				{
					l_result = Utilities.getRationaleElementIcon(l_match.getRequirement());
				}
				break;
			}
			return l_result;
		}

		/* Return the text which should be displayed in a column or
		 * the empty string if no text should be displayed.
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object pMatch, int pColumnIndex) {
			if( !(pMatch instanceof RequirementSearchResultMatch) )
				return "ERROR";
			
			String l_result = "";
			RequirementSearchResultMatch l_data = (RequirementSearchResultMatch)pMatch;
			
			switch( pColumnIndex )
			{
			case g_StatusColumn:
				l_result = l_data.getRequirement().getStatus().toString();
				break;
			case g_RequirementColumn:
				l_result = l_data.getRequirement().getName();
				break;
			case g_ArgumentColumn:
				if( l_data.getArgument() == null )
					l_result = "(none)";
				else
					l_result = l_data.getArgument().getName();
				
				break;				
			}
			return l_result;
		}
	}
		
    /**
     * Construct the requirement status search result page,
     * only provide support for the flat table view.
     */
    public RequirementSearchResultPage () {
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
		
		m_StatusIcon = new TableColumn(l_table, SWT.LEFT);
		m_Requirement = new TableColumn(l_table, SWT.LEFT);
		m_Argument = new TableColumn(l_table, SWT.LEFT);
		m_Status = new TableColumn(l_table, SWT.LEFT);		
		
		m_Status.setWidth(25);
		m_Requirement.setWidth(1000);
		m_Argument.setWidth(1000);
		m_Status.setWidth(1000);
		
		SearchResultPage_TableResizeListener l_listener = new SearchResultPage_TableResizeListener(l_table);
		l_listener.addColumn(m_Status, 5);
		l_listener.addColumn(m_Requirement, 35);
		l_listener.addColumn(m_Argument, 35);
		l_listener.addColumn(m_Status, 25);
		l_table.getParent().addControlListener(l_listener);
		
		m_StatusIcon.setText("");
		m_Requirement.setText("Requirement Name");
		m_Argument.setText("Argument Name");
		m_Status.setText("Requirement Status");
		
		m_StatusIcon.addSelectionListener(new SearchResultPage_IconTableSorter(g_StatusIconColumn, pViewer));
		m_Requirement.addSelectionListener(new SearchResultPage_StringTableSorter(g_RequirementColumn, pViewer));
		m_Argument.addSelectionListener(new SearchResultPage_StringTableSorter(g_ArgumentColumn, pViewer));
		m_Status.addSelectionListener(new SearchResultPage_StringTableSorter(g_StatusColumn, pViewer));
		
		m_StatusIcon.pack();
		m_Requirement.pack();
		m_Argument.pack();
		m_Status.pack();	
		l_table.pack();
		l_table.getParent().pack();		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	protected void configureTreeViewer(TreeViewer pViewer) {
		// TODO: Implement this, use the requirements as parents and the alternatives as children
		System.err.println("RequirementSearchResultPage: Tree View Not Currently Supported");
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
