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

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import SEURAT.search.importanceOverrides.ImportanceOverrideSearchResultMatch;
import SEURAT.search.importanceOverrides.ImportanceOverrideSearchResultSet;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Implementation of the Eclipse ISearchQuery interface
 * for the SEURAT Entity Search
 * 
 *  @author hannasm
 *
 */
/**
 * @author Administrator
 *
 */
public class EntitySearchQuery implements ISearchQuery {
	/**
	 * Storage class for the results of the entity search
	 */
	private EntitySearchResultSet m_SearchResult;
	/**
	 * Entity type to search for
	 */
	private RationaleElementType m_SearchType;
	/**
	 * Substring to look for
	 */
	private String m_SearchString;
	/**
	 * Number of results found by the search
	 */
	private int m_Results = 0;
	
	/**
	 * @param pType the type of rationale element to search for
	 */
	public EntitySearchQuery(RationaleElementType pType, String searchString) {
		m_SearchResult = new EntitySearchResultSet(this);
		m_SearchType = pType;
		m_SearchString = searchString;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRerun()
	 */
	public boolean canRerun() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	public boolean canRunInBackground() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	public String getLabel() {
		return "SEURAT Entity Search (" + m_Results + " results)";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		return m_SearchResult;
	}

	/* Retrieve all rationale elements of the relevant type from
	 * the database and add the results to the result set.
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor pProgress) throws OperationCanceledException {		
		RationaleDB l_db = RationaleDB.getHandle();	
		
		//Need to decide what types of elements to search for
		RationaleElementType l_type = null;
		Vector<RationaleElementType> searchEle = new Vector<RationaleElementType>();
		
		if (m_SearchType != null)
		{
			searchEle.add(m_SearchType);
		}
		else
		{
			//now, the true meaning of "All" would be all the possible types but we will
			//restrict this to just requirements, decisions, alternatives, arguments, and assumptions
			searchEle.add(RationaleElementType.REQUIREMENT);
			searchEle.add(RationaleElementType.DECISION);
			searchEle.add(RationaleElementType.ALTERNATIVE);
			searchEle.add(RationaleElementType.ARGUMENT);
			searchEle.add(RationaleElementType.ASSUMPTION);
		}
		
		Enumeration l_i = searchEle.elements();
		while (l_i.hasMoreElements())
		{
			RationaleElementType sType = (RationaleElementType) l_i.nextElement();
			Vector l_names = l_db.getNameList(sType, m_SearchString);

			m_Results = 0;

			// Technically we've already done the heavy lifting here
			pProgress.beginTask(
					"Searching for Rationale Entities of Type " + sType.toString(),
					l_names.size()
			);

			// TODO EMBED DB QUERY RETRIEVING IMPORTANCE OVERRIDES HERE?

			for( Object l_element : l_names )
			{
				RationaleElement l_ele = RationaleDB.getRationaleElement(l_element.toString(), sType);

				m_SearchResult.addMatch(new EntitySearchResultMatch(l_ele));
				m_Results++;
				pProgress.worked(1);
			}
		}
		// Finished
		pProgress.done();
        return Status.OK_STATUS;
	}

}
