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

import java.lang.reflect.Method;
import java.util.*;
import java.lang.reflect.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * @author hannasm
 *
 * @param <tPublisher>The type of all publishers which will
 * 		be subscribed to.
 * @param <tSubscriber> The type of all subscribers which will
 * 		be subscribing.
 * @param <tPublication> The type of publications produced by
 * 			publishers
 */
public class PublishSubscribeManager<tPublisher, tSubscriber, tPublication>
{
	/**
	 * @author hannasm
	 *
	 * @param <tPublisher> The type of all publishers which will
	 * 		be subscribed to.
	 * @param <tSubscriber> The type of all subscribers which will
	 * 		be subscribing.
	 */
	protected class Subscription<tPublisher, tSubscriber>
	{
		/**
		 * The object which should receive publications
		 */
		public tSubscriber m_Subscriber;
		
		/**
		 * The object which the subscriber wishes to recieve 
		 * publications from  
		 */
		public tPublisher m_Publisher;
		
		/**
		 * The function in the subscriber object which should be
		 * called when new publications are recieved. 
		 */
		public Method m_DeliveryMethod; 
	}
	
	/**
	 * A list of all subscriptions currently active in the
	 * PublishSubscribeManager.
	 */
	protected ArrayList<Subscription<tPublisher, tSubscriber>> m_Subscriptions =
		new ArrayList<Subscription<tPublisher, tSubscriber>>();
	/**
	 * A publication object which is used for retrieving class information
	 * when retrieving a method through reflection.
	 */
	protected tPublication m_EmptyPublication = null;
	/**
	 * Boolean which determines what type of comparison should
	 * be used when comparing the subscriptions publisher
	 * with the publisher of a publication. If true the
	 * comparison will use memory addresses and if false
	 * will use the method Object.equlas()
	 */
	protected boolean m_CompareExact;
	
	/**
	 * Construct a PublishSubscribeManager using exact comparisons.
	 * Publishers will be identified by their address.
	 * 
	 * @param pEmptyPublication An object of the publication type
	 * 		that can be used to retrieve class information during
	 * 		reflection lookups.
	 */
	public PublishSubscribeManager(tPublication pEmptyPublication)
	{
		this(pEmptyPublication, true);
	}
	
	/**
	 * Construct a PublishSubscribeManager using one of two
	 * different comparison methods.
	 * 	 
	 * @param pEmptyPublication An object of the publication type
	 * 		that can be used to retrieve class information during
	 * 		reflection lookups.
	 * @param pCompareExact if true publishers will be compared
	 * 		based on address. If false publishers will be compared
	 *		using the equals() method defined in all classes
	 *		deriving from type Object.
	 */
	public PublishSubscribeManager(tPublication pEmptyPublication, boolean pCompareExact)
	{
		m_EmptyPublication = pEmptyPublication;
		m_CompareExact = pCompareExact;
	}
	
	
	/**
	 * Register a new object interested in publications from a publisher.
	 * 
	 * This function requires the subscriber to have a function with the
	 * method signature 
	 * 
	 * void <i>pMethod</i>(tPublisher pPublisher, tPublication pPublication)
	 * 
	 * It is possible (and potentially undesirable) to subscribe multiple 
	 * times to the same publisher. Each subscription will result in
	 * an invocation of the specified subscription function.
	 * 
	 * @param pPublisher The object whose publications should be recieved.
	 * @param pSubscriber The object which wishes to recieve publications.
	 * @param pMethod The name of the method in pSubscriber to be invoked.
	 * 
	 * @throws NoSuchMethodException If the subscriber object does not contain
	 * 	a method named pMethod with the parameters (tPublisher, tPublication).
	 */
	public void Subscribe(tPublisher pPublisher, tSubscriber pSubscriber, String pMethod) 
		throws NoSuchMethodException
	{
		Subscription<tPublisher, tSubscriber> l_subscription =
			new Subscription<tPublisher, tSubscriber>();
		
		l_subscription.m_Subscriber = pSubscriber;
		l_subscription.m_Publisher = pPublisher;
		
		Class parameterTypes[] = {
				pPublisher.getClass(),
				m_EmptyPublication.getClass()
		};
		
		Method l_deliver = pSubscriber.getClass().getMethod(pMethod, parameterTypes);
		l_subscription.m_DeliveryMethod = l_deliver;
		
		m_Subscriptions.add(l_subscription);
	}

	/**
	 * Notify All Interested Subscribers Of A New Publication.
	 * 
	 * @param pPublisher The object which is publishing information
	 * @param pPublication The object containing publication information
	 */
	public void Publish(tPublisher pPublisher, tPublication pPublication)
	{
		Object l_parameters[] = {pPublisher, pPublication};
		
		// Create A Copy Of The Subscription List To Avoid Non-Deterministic
		// Behavior When Subscriptions Are Made And Removed By Invoked
		// Functions
		ArrayList<Subscription<tPublisher, tSubscriber>> l_subscriptions = 
			new ArrayList<Subscription<tPublisher, tSubscriber>>(m_Subscriptions);
		
		for( Subscription<tPublisher, tSubscriber> l_subscription : l_subscriptions)
		{
			if( (m_CompareExact && l_subscription.m_Publisher == pPublisher ) ||
				(!m_CompareExact && l_subscription.m_Publisher.equals(pPublisher)) )
			{
				try
				{
					l_subscription.m_DeliveryMethod.invoke(l_subscription.m_Subscriber, l_parameters);
				}
				catch( Exception e )
				{}
			}
		}
	}
	
	public void Unsubscribe(tSubscriber pSubscriber, tPublisher pPublisher)
	{
		Iterator<Subscription<tPublisher, tSubscriber>> l_iterator;
		Subscription<tPublisher, tSubscriber> l_subscription;
		
		for( l_iterator = m_Subscriptions.iterator() ;
			 l_iterator.hasNext() ; )
		{
			l_subscription = l_iterator.next();
			
			if( ( m_CompareExact && 
				l_subscription.m_Subscriber == pSubscriber &&
				l_subscription.m_Publisher == pPublisher )
				||
				(!m_CompareExact &&
					l_subscription.m_Subscriber.equals(pSubscriber) &&
					l_subscription.m_Publisher.equals(pPublisher) )
				)
			{
				l_iterator.remove();
			}
		}
	}
	
	/**
	 * Remove All Subscriptions Made By A Particular Subscriber
	 * 
	 * @param pSubscriber The subscriber which should be removed
	 * 			from the subscription list.
	 */
	public void Unsubscribe(tSubscriber pSubscriber)
	{
		Iterator<Subscription<tPublisher, tSubscriber>> l_iterator;
		Subscription<tPublisher, tSubscriber> l_subscription;
		
		for( l_iterator = m_Subscriptions.iterator() ;
			 l_iterator.hasNext() ; )
		{
			l_subscription = l_iterator.next();
			
			if( ( m_CompareExact && l_subscription.m_Subscriber == pSubscriber) ||
				(!m_CompareExact &&	l_subscription.m_Subscriber.equals(pSubscriber)))
			{
				l_iterator.remove();
			}
		}
	}
	
	/**
	 * Remove all subscriptions to a particular publisher
	 * 
	 * @param pPublisher The publisher which wants to remove
	 * all subscriptions to it.
	 */
	public void ForceUnsubscribe(tPublisher pPublisher)
	{
		Iterator<Subscription<tPublisher, tSubscriber>> l_iterator;
		Subscription<tPublisher, tSubscriber> l_subscription;
		
		for( l_iterator = m_Subscriptions.iterator() ;
			 l_iterator.hasNext() ; )
		{
			l_subscription = l_iterator.next();
			
			if( ( m_CompareExact && l_subscription.m_Publisher == pPublisher) ||
				(!m_CompareExact &&	l_subscription.m_Publisher.equals(pPublisher)))
			{
				l_iterator.remove();
			}
		}
	}
}
