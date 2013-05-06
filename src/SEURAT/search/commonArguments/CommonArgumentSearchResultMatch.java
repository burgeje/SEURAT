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
