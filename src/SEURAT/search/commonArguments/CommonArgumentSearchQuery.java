package SEURAT.search.commonArguments;

import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import SEURAT.search.importanceOverrides.ImportanceOverrideSearchResultMatch;
import SEURAT.search.importanceOverrides.ImportanceOverrideSearchResultSet;
import edu.wpi.cs.jburge.SEURAT.inference.ArgumentInferences;
import edu.wpi.cs.jburge.SEURAT.queries.CommonArgument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Implementation of the Eclipse ISearchQuery interface
 * for performing a common argument search
 * 
 * @author hannasm
 *
 */
public class CommonArgumentSearchQuery implements ISearchQuery {

	/**
	 * Container for the search results
	 */
	private CommonArgumentSearchResultSet m_SearchResult;
	/**
	 * The type of common elements to look for
	 */
	private RationaleElementType m_ElementType;
	/**
	 * Controls whether results are filtered if they are
	 * not selected
	 */
	private boolean m_SelectedOnly;
	/**
	 * The number of search results found 
	 */
	private int m_Results = 0;
	
	/**
	 * @param pType the type of rationale element to search for
	 * @param pSelected determines whether results should be filtered if they are not selected
	 */
	public CommonArgumentSearchQuery(RationaleElementType pType, boolean pSelected) {
		m_SearchResult = new CommonArgumentSearchResultSet(this);
		m_ElementType = pType;
		m_SelectedOnly = pSelected;
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
		return "SEURAT Common Argument Search (" + m_Results + " results)";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		return m_SearchResult;
	}

	/* Request a filtered set of results from the database
	 * and add them to the search result set.
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor pProgress) throws OperationCanceledException {		
		RationaleDB l_db = RationaleDB.getHandle();		
		ArgumentInferences inf = new ArgumentInferences();
		Vector l_arguments = inf.argumentStatistics(m_ElementType, m_SelectedOnly);
		
		m_Results = 0;
		
		// Technically we've already done the heavy lifting here
		pProgress.beginTask(
				"Searching For Common Arguments",
				l_arguments.size()
		);

		
		// TODO EMBED DB QUERY RETRIEVING IMPORTANCE OVERRIDES HERE?
		
		for( Object l_element : l_arguments )
		{
			pProgress.worked(1);
			CommonArgument l_arg = (CommonArgument)l_element;
			
			RationaleElement l_ratElement = RationaleDB.getRationaleElement(l_arg.getArgumentName(), l_arg.getType());
			
			m_SearchResult.addMatch(new CommonArgumentSearchResultMatch(
				l_ratElement, l_arg.getTotal(), l_arg.getForCount(), l_arg.getAgainstCount()
			));
			m_Results++;
		}
		
		// Finished
		pProgress.done();
        return Status.OK_STATUS;
	}

}
