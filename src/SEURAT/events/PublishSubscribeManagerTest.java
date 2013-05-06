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

import java.lang.*;

public class PublishSubscribeManagerTest {

	protected class Subscriber
	{
		public void Subscriber(){};
		
		public void Print(String pString)
		{
			System.out.println(pString);
		}
	}
	
	public class SubscriptionOne extends Subscriber
	{
		public SubscriptionOne(){};
		
		public void GetSubscription(Integer pPublisher, String pPublication) {
			Print("Subscription One Recieved: " + pPublication);
		}
	}
	
	public class SubscriptionTwo extends Subscriber
	{
		public SubscriptionTwo(){};
		
		public void GotPublication(Integer pPublisher, String pPublication) {
			Print("Subscripton Two Recieved: " + pPublication);
		}
		
		public void SecondTest(Integer pPublisher, String pPublication) {
			Print("Subscription Two (Alternate) Recieved: " + pPublication);
		}
	}
	
	public void run()
	{
		SubscriptionOne l_subOne = new SubscriptionOne();
		SubscriptionTwo l_subTwo = new SubscriptionTwo();
		
		PublishSubscribeManager<Integer, Subscriber, String> l_manager = 
			new PublishSubscribeManager<Integer, Subscriber, String>("hello");
		
		Integer l_one, l_two, l_three;
		
		l_one = new Integer(1);
		l_two = new Integer(2);
		l_three = new Integer(3);
		
		try 
		{
			l_manager.Subscribe(l_one, l_subOne, "GetSubscription");
			l_manager.Subscribe(l_one, l_subTwo, "GotPublication");
			l_manager.Subscribe(l_one, l_subTwo, "SecondTest");
			
			l_manager.Subscribe(l_two, l_subOne, "GetSubscription");
			l_manager.Subscribe(l_two, l_subTwo, "SecondTest");
			
			l_manager.Subscribe(l_three, l_subOne, "GetSubscription");
		}
		catch( Exception e )
		{
			System.out.println("Error While Subscribing");
		}
		
		l_manager.Publish(l_one, "First Publication");
		l_manager.Publish(l_two, "Second Publication");
		l_manager.Publish(l_three, "Third Publication");
		
		l_manager.Publish(new Integer(1), "Similar Publisher");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PublishSubscribeManagerTest l_test = new PublishSubscribeManagerTest();
		l_test.run();
	}

}
