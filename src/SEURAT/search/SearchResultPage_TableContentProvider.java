package SEURAT.search;

import org.eclipse.jface.viewers.*;
import org.eclipse.search.*;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

/**
 * Implementation of a generic, minimal; table content provider
 * 
 * Most of this code is adapted from the AbstractSearchResultPage class found
 * in the package org.eclipse.pde.internal.ui.search
 * 
 * @author hannasm
 */
public class SearchResultPage_TableContentProvider implements IStructuredContentProvider
{
	/**
	 * Reference to the table viewer we are providing content to 
	 */
	private TableViewer m_TableViewer;
	/**
	 * The query we are providing elements from
	 */
	private AbstractTextSearchResult m_SearchQuery;
	
	/**
	 * @param pViewer the table viewer which we will be providing content to
	 */
	public SearchResultPage_TableContentProvider()
	{}
	
	/* 
	 * Cleanup any references to allow garbage colelction.
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		return;			
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object pElement) {
		if( pElement instanceof AbstractTextSearchResult )
			return ((AbstractTextSearchResult)pElement).getElements();
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer pViewer, Object pFirst, Object pSecond) {
		m_TableViewer = (TableViewer)pViewer;		
		m_SearchQuery = (AbstractTextSearchResult)pSecond;
	}
	
	/**
	 * @param pElements the set of elements which should be refreshed
	 */
	public void updateElements(Object[] pElements)
	{
		for (int i= 0; i < pElements.length; i++) {
			if (m_SearchQuery.getMatchCount(pElements[i]) > 0) {
				if (m_TableViewer.testFindItem(pElements[i]) != null)
					m_TableViewer.refresh(pElements[i]);
				else
					m_TableViewer.add(pElements[i]);
			} else {
				m_TableViewer.remove(pElements[i]);
			}
		}
	}
	
	/**
	 * the list of elements in the table has been updated
	 */
	public void clear() {
		m_TableViewer.refresh();
	}		
}
