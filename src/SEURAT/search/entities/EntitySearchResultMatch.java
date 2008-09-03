package SEURAT.search.entities;

import org.eclipse.search.ui.text.Match;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;

/**
 * Storage class for each result in an entity search
 * 
 * @author hannasm
 */
public class EntitySearchResultMatch  extends Match 
{
	/**
	 * The rationale element which has been found 
	 */
	private RationaleElement m_RationaleElement;
	
	/**
	 * @param pElement the rationale element found by the query
	 */
	public EntitySearchResultMatch(RationaleElement pElement)
	{
		// This Call To The Superclass Constructor is Contrived
		// We Manage Our Data Members Manually Anyway
		super(pElement, 0, 0); 
		
		m_RationaleElement = pElement;
	}
	
	/**
	 * @return the rationale element found by the query
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
