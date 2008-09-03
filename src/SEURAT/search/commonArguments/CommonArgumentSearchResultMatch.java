package SEURAT.search.commonArguments;

import org.eclipse.search.ui.text.Match;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;

/**
 * Container for a result in a common argument search
 * 
 * @author hannasm
 *
 */
public class CommonArgumentSearchResultMatch extends Match 
{
	/**
	 * The rationale element which met the criteria
	 */
	private RationaleElement m_RationaleElement;
	/**
	 * String representations of the number of occurrences
	 * of the rationale element
	 */
	private String m_Total, m_For, m_Against;
	
	/**
	 * @param pElement the rationale element which was found
	 * @param pTotal  a string representing the number of total occurrences
	 * @param pFor a string representing the number of ocurrences for an alternative
	 * @param pAgainst a string representing the number of ocurrences against an alternative
	 */
	public CommonArgumentSearchResultMatch(RationaleElement pElement, String pTotal, String pFor, String pAgainst)
	{
		// This Call To The Superclass Constructor is Contrived
		// We Manage Our Data Members Manually Anyway
		super(pElement, 0, 0); 
		
		m_RationaleElement = pElement;
		m_Total = pTotal;
		m_For = pFor;
		m_Against = pAgainst;
	}
	
	/**
	 * @return the rationale element 
	 */
	public RationaleElement getRationale()
	{
		return m_RationaleElement;
	}
	/**
	 * @return total number of occurrences 
	 */
	public String getTotal() { return m_Total; }
	/**
	 * @return the number of occurrences for an alternative
	 */
	public String getFor() { return m_For; }
	/**
	 * @return the number of occurrences against an alternative
	 */
	public String getAgainst() { return m_Against; }
	
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
