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

package SEURAT.events;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

public class RationaleUpdateEvent {
	private boolean m_Create, m_Destroy, m_Modified;	
	private RationaleElement m_Element;
	private Object m_Tag;
	
	public RationaleUpdateEvent()
	{
		m_Element = null;
		m_Create = m_Destroy = m_Modified = false;
		m_Tag = null;
	}
	
	public Object getTag()
	{
		return m_Tag;
	}
	public void setTag(Object pTag)
	{
		m_Tag = pTag;
	}
	public RationaleElement getElement()
	{
		return m_Element;
	}
	public void setElement(RationaleElement pElement)
	{
		m_Element = pElement;
	}
	
	public boolean getCreated() { return m_Create; }
	public void setCreated(boolean pCreated)
	{
		m_Create = pCreated;
	}
	
	public boolean getDestroyed() { return m_Destroy; }
	public void setDestroyed(boolean pDestroyed)
	{
		m_Destroy = pDestroyed;
	}
	
	public boolean getModified() { return m_Modified; }
	public void setModified(boolean pUpdated)
	{
		m_Modified = pUpdated;
	}
}
