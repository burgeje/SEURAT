package SEURAT.search.requirements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import edu.wpi.cs.jburge.SEURAT.inference.RequirementInferences;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ArgType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;

/**
 * Implementation of the Eclipse Search Query Interface
 * for SEURAT Requirement Status search
 * @author hannasm
 */
public class RequirementSearchQuery implements ISearchQuery {

	/**
	 * Container for the results of this query
	 */
	private RequirementSearchResultSet m_SearchResult;
	/**
	 * Requirement Status the query should filter with
	 */
	private ReqStatus m_DesiredStatus;
	/**
	 * the number of results which have been found by the query
	 */
	private int m_Results = 0;
	
	/**
	 * @param pDesiredStatus the status type to filter requirements by
	 */
	public RequirementSearchQuery(ReqStatus pDesiredStatus) {
		m_SearchResult = new RequirementSearchResultSet(this);
		m_DesiredStatus = pDesiredStatus;		
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

	public String getLabel() {
		return "SEURAT Requirement Search (" + m_Results + " results)";
	}

	public ISearchResult getSearchResult() {
		return m_SearchResult;
	}

	/* Retrieve list of requirements of a particular status type.
	 * The database query has been put inline with the code, 
	 * for better or worse.
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor pProgress) throws OperationCanceledException {
		RationaleDB db = RationaleDB.getHandle();	

		m_Results = 0;
		
		// Notify The Progress Monitor We Have Begun Our Search
		// TODO: Calculate the number of requirements total and base the 
		//       units of work for the progress monitor on that
		pProgress.beginTask("Searching For Requirements With Status: " + m_DesiredStatus.toString(), IProgressMonitor.UNKNOWN);
	
		// Locate All Relevant Requirements
		pProgress.subTask("Loading Requirements From Database");		
		Vector<Requirement> l_requirements = new Vector<Requirement>();
		Statement l_statement = null;
		ResultSet l_results = null;
		String l_query = "";
		try {
			
			l_statement = db.getConnection().createStatement();
			
			l_query = "SELECT name from "
				+ RationaleDBUtil.escapeTableName("requirements") + " "
				+ "where status = '" + m_DesiredStatus.toString() + "'";
			//***			System.out.println(findQuery);
			l_results = l_statement.executeQuery(l_query);
			
			while (l_results.next()) {
				String name = RationaleDBUtil.decode(l_results.getString("name"));
				Requirement req = new Requirement();
				req.fromDatabase(name);
				l_requirements.add(req);
				pProgress.worked(1);
			}
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Error in RequirementsSearchQuery.run()", l_query);
		} finally {
			RationaleDB.releaseResources(l_statement, l_results);
		}
		
		// Find Any Arguments Associated With Each Requirement
		pProgress.subTask("Inferring Arguments Involved");		
		for( Requirement l_requirement : l_requirements )
		{
			Vector<Argument> l_arguments = new Vector<Argument>();
			
			if (m_DesiredStatus == ReqStatus.SATISFIED)
			{
				RequirementInferences inf = new RequirementInferences();
				l_arguments = inf.getArguments(l_requirement, ArgType.SATISFIES);
			}
			else if (m_DesiredStatus == ReqStatus.ADDRESSED)
			{
				RequirementInferences inf = new RequirementInferences();
				l_arguments = inf.getArguments(l_requirement, ArgType.ADDRESSES);
			}
			else if (m_DesiredStatus == ReqStatus.VIOLATED)
			{
				RequirementInferences inf = new RequirementInferences();
				l_arguments = inf.getArguments(l_requirement, ArgType.VIOLATES);
			}
			
			for( Argument l_argument : l_arguments )
			{
				m_SearchResult.addMatch(new RequirementSearchResultMatch(l_requirement, l_argument));
				m_Results++;
			}
			
			if( l_arguments.size() <= 0 )
			{
				m_SearchResult.addMatch(new RequirementSearchResultMatch(l_requirement, null));
				m_Results++;
			}
			
			pProgress.worked(1);
		}
		
		// Finished
		pProgress.done();
        return Status.OK_STATUS;
	}

}
