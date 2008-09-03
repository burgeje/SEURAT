package SEURAT.events;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

public class RationaleElementUpdateEventGenerator <tElement extends RationaleElement>{

	private tElement m_Element;

	public RationaleElementUpdateEventGenerator(tElement pElement)
	{
		m_Element = pElement;
	}
	
	public void Created()
	{
		Broadcast(MakeCreated());
	}
	
	public RationaleUpdateEvent MakeCreated()
	{
		RationaleUpdateEvent l_event = new RationaleUpdateEvent();
		
		l_event.setElement(m_Element);
		l_event.setCreated(true);
		
		return l_event;
	}
	
	public void Updated()
	{
		Broadcast(MakeUpdated());
	}
	public RationaleUpdateEvent MakeUpdated()
	{
		RationaleUpdateEvent l_event = new RationaleUpdateEvent();
		
		l_event.setElement(m_Element);
		l_event.setModified(true);
		
		return l_event;
	}
	
	public void Destroyed()
	{
		Broadcast(MakeDestroyed());
	}
	public RationaleUpdateEvent MakeDestroyed()
	{
		RationaleUpdateEvent l_event = new RationaleUpdateEvent();
		
		l_event.setElement(m_Element);
		l_event.setDestroyed(true);
		
		return l_event;
	}
	
	public void Broadcast(RationaleUpdateEvent pEvent)
	{
		RationaleElement parent = m_Element.getParentElement();
		
		// If The Element Was Created And A Parent Can Be Identified
		// Broadcast The Created Event As The Parent
		if( pEvent.getCreated() &&
			parent != null )
		{
			RationaleDB.getHandle().Notifier().Publish(parent, pEvent);
			return;
		}
		
		RationaleDB.getHandle().Notifier().Publish(m_Element, pEvent);
		
		// After A Destroyed Event Is Sent, Remove All Subscriptions To
		// The Rationale Element Generating The Destroyed Message
		if( pEvent.getDestroyed() ) {
			RationaleDB.getHandle().Notifier().ForceUnsubscribe(pEvent.getElement());
		}
	}
}
