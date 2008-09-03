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
	 * Number of results found by the search
	 */
	private int m_Results = 0;
	
	/**
	 * @param pType the type of rationale element to search for
	 */
	public EntitySearchQuery(RationaleElementType pType) {
		m_SearchResult = new EntitySearchResultSet(this);
		m_SearchType = pType;
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
		Vector l_names = l_db.getNameList(m_SearchType);
		
		m_Results = 0;
		
		// Technically we've already done the heavy lifting here
		pProgress.beginTask(
				"Searching for Rationale Entities of Type " + m_SearchType.toString(),
				l_names.size()
		);
		
		// TODO EMBED DB QUERY RETRIEVING IMPORTANCE OVERRIDES HERE?
		
		for( Object l_element : l_names )
		{
			RationaleElement l_ele = RationaleDB.getRationaleElement(l_element.toString(), m_SearchType);
			
			m_SearchResult.addMatch(new EntitySearchResultMatch(l_ele));
			m_Results++;
			pProgress.worked(1);
		}
		
		// Finished
		pProgress.done();
        return Status.OK_STATUS;
	}

}
