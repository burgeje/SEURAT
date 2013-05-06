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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import SEURAT.search.commonArguments.CommonArgumentSearchResultMatch;

import java.math.*;

/**
 * Implementation of ViewerComparator and SelectionListener interfaces
 * allowing a TableViewer to sort a table column on the numeric representation
 * of the elements in that column. All non-numeric element representations
 * will be treated as equal.
 * 
 * @author hannasm
 */
public class SearchResultPage_NumericTableSorter extends ViewerComparator implements SelectionListener
{
	private int m_SortColumn;
	private TableViewer m_TableViewer;
	private ITableLabelProvider m_TableLabelProvider;
	private boolean m_ReverseDirection;
	
	/**
	 * @param pSortColumn the id of the table column to sort on
	 * @param pViewer the table viewer which will be using this sorter class
	 */
	public SearchResultPage_NumericTableSorter(int pSortColumn, TableViewer pViewer) {
		super();
		m_SortColumn = pSortColumn;
		m_TableViewer = pViewer;
		m_TableLabelProvider = ((ITableLabelProvider)pViewer.getLabelProvider());
		
		// Initialize To True, Then When Activated For The First Time Direction
		// Will Not Be Reversed
		m_ReverseDirection = true;
	}
	
	/* Compare our elements
	 * Method declared on ViewerSorter.
	 */
	public int compare(Viewer pViewer, Object pOne, Object pTwo) {
		int l_result;
		
		BigDecimal l_dOne = null;
		BigDecimal l_dTwo = null;
		
		String l_columnTextOne = m_TableLabelProvider.getColumnText(pOne, m_SortColumn);
		String l_columnTextTwo = m_TableLabelProvider.getColumnText(pTwo, m_SortColumn);
		
		try {
			l_dOne = new BigDecimal(l_columnTextOne);
		} catch( Exception e ) {};
		try {
			l_dTwo = new BigDecimal(l_columnTextTwo);
		} catch( Exception e ) {};
		
		if( l_dOne != null && l_dTwo != null )
			l_result = l_dOne.compareTo(l_dTwo);
		else
		if( l_dOne == null && l_dTwo == null )
			l_result = 0;
		else
		if( l_dOne == null )
			l_result = -1;
		else
			l_result = 1;
		
		if( m_ReverseDirection )
			l_result = -l_result;
		
		return l_result;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent pEvent) {
		widgetSelected(pEvent);			
	}

	/* 
	 * Reverses the sorting order each click
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent pEvent) {
		m_ReverseDirection = !m_ReverseDirection;
		m_TableViewer.setComparator(this);
		m_TableViewer.refresh();
	}
}