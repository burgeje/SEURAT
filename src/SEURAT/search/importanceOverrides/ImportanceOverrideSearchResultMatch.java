package SEURAT.search.importanceOverrides;

import org.eclipse.search.ui.text.Match;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Storage class for each result in an importance override
 * query.
 * 
 * @author hannasm
 */
public class ImportanceOverrideSearchResultMatch extends Match 
{
	/**
	 * The rationale element whose importance has been overridden
	 */
	private RationaleElement m_RationaleElement;
	
	/**
	 * @param pElement a rationale element whose importance has been overridden
	 */
	public ImportanceOverrideSearchResultMatch(RationaleElement pElement)
	{
		// This Call To The Superclass Constructor is Contrived
		// We Manage Our Data Members Manually Anyway
		super(pElement, 0, 0); 
		
		m_RationaleElement = pElement;
	}
	
	/**
	 * @return a rationale element whose importance has been overridden
	 */
	public RationaleElement getRationale()
	{
		return m_RationaleElement;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.Match#getElement()
	 */
	@Override
	public Object getElement() 
	{
		// This prevents eclipse from deconstructing the match into
		// just the requirement component we passed down in the constructor.
		return this;
	}
}
