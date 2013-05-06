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

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Implementation of the Eclipse ISearchQuery Interface
 * for the SEURAT importance override search
 * @author hannasm
 */
public class ImportanceOverrideSearchQuery implements ISearchQuery {

	/**
	 * Storage for the search results found by the query
	 */
	private ImportanceOverrideSearchResultSet m_SearchResult;
	/**
	 * The number of results found be the query
	 */
	private int m_Results = 0;
	
	public ImportanceOverrideSearchQuery() {
		m_SearchResult = new ImportanceOverrideSearchResultSet(this);
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
		return "SEURAT Importance Override Search (" + m_Results + " results)";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		return m_SearchResult;
	}

	/* Retrieve all importance overrides from the database and add them
	 * to our query results.
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor pProgress) throws OperationCanceledException {		
		RationaleDB l_db = RationaleDB.getHandle();		
		Vector l_arguments = l_db.getOverridenArguments();
		Vector l_claims = l_db.getOverridenClaims();

		m_Results = 0;
		
		// Technically we've already done the heavy lifting here
		pProgress.beginTask(
				"Searching For Overridden Rationale Importance",
				l_arguments.size() + l_claims.size()
		);
		
		// TODO EMBED DB QUERY RETRIEVING IMPORTANCE OVERRIDES HERE?
		
		for( Object l_element : l_arguments )
		{
			pProgress.worked(1);
			if( l_element instanceof RationaleElement )			
			{
				m_SearchResult.addMatch(new ImportanceOverrideSearchResultMatch((RationaleElement)l_element));
				m_Results++;
			}
		}
		for( Object l_element : l_claims )
		{
			pProgress.worked(1);
			if( l_element instanceof RationaleElement )			
			{
				m_SearchResult.addMatch(new ImportanceOverrideSearchResultMatch((RationaleElement)l_element));
				m_Results++;
			}
		}
		
		// Finished
		pProgress.done();
        return Status.OK_STATUS;
	}

}
