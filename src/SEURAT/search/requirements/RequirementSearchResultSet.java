package SEURAT.search.requirements;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.text.*;
import org.eclipse.search.ui.*;

/**
 * Concrete implementation of the search results found
 * by a requirement status search
 * 
 * @author hannasm
 */
public class RequirementSearchResultSet extends AbstractTextSearchResult
{
	/**
	 * storage for the query which provides matches
	 */
	ISearchQuery m_Query;
	
	/**
	 * @param pQuery the query which will provide matches
	 */
	public RequirementSearchResultSet(ISearchQuery pQuery)
	{
		m_Query = pQuery;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getEditorMatchAdapter()
	 */
	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
	 */
	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getLabel()
	 */
	public String getLabel() {
		return m_Query.getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return m_Query;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip() {
		return m_Query.getLabel();
	}
	
}
