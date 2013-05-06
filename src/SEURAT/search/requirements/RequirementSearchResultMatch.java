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
