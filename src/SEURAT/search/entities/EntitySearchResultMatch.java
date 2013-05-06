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
