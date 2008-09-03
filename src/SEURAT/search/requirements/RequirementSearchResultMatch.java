package SEURAT.search.requirements;

import org.eclipse.search.ui.text.Match;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;

/**
 * Container for a match found by a Requirement Status Search
 * @author hannasm
 */
public class RequirementSearchResultMatch extends Match 
{
	/**
	 * The requirement which was found
	 */
	private Requirement m_Requirement;
	/**
	 * An argument which references this requirement
	 */
	private Argument m_Argument;
	
	/**
	 * @param pRequirement the requirement
	 * @param pArgument the argument referencing the requirement
	 */
	public RequirementSearchResultMatch(Requirement pRequirement, Argument pArgument)
	{
		// This Call To The Superclass Constructor is Contrived
		// We Manage Our Data Members Manually Anyway
		super(pRequirement, 0, 0); 
		
		m_Requirement = pRequirement;
		m_Argument = pArgument;
	}
	
	/**
	 * @return the requirement found
	 */
	public Requirement getRequirement() 
	{
		return m_Requirement;
	}
	/**
	 * @return the argument which references the requirement
	 */
	public Argument getArgument()
	{
		return m_Argument;
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
