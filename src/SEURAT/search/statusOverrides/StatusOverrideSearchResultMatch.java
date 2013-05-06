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

package SEURAT.search.statusOverrides;

import org.eclipse.search.ui.text.Match;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Extension of the Eclipse Search Result Match Class
 * for Status Override Search Results
 * 
 * @author hannasm
 */
public class StatusOverrideSearchResultMatch extends Match 
{
	/**
	 * The Status Which Is Overriden
	 */
	private RationaleStatus m_Status;
	/**
	 * The most significant rationale element associated with this
	 * status message.
	 */
	private RationaleElement m_RationaleElement;
	
	/**
	 * @param pOverride The status override object to construct
	 * 					this match from 
	 */
	public StatusOverrideSearchResultMatch(RationaleStatus pOverride)
	{
		// This Call To The Superclass Constructor is Contrived
		// We Manage Our Data Members Manually Anyway
		super(pOverride, 0, 0); 
		
		RationaleDB l_db = RationaleDB.getHandle();
		
		m_RationaleElement = l_db.getRationaleElement(pOverride.getParent(), pOverride.getRationaleType());
		
		m_Status = pOverride;
	}
	
	/**
	 * @return The status override 
	 */
	public RationaleStatus getStatus()
	{
		return m_Status;
	}
	/**
	 * @return The most significant rationale element associated with the matched status
	 */
	public RationaleElement getRationale()
	{
		return m_RationaleElement;
	}
	
	@Override
	public Object getElement() 
	{
		// This prevents eclipse from deconstructing the match into
		// just the requirement component we passed down in the constructor.
		return this;
	}
}
