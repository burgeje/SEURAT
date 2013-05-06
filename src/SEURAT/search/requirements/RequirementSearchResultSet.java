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
