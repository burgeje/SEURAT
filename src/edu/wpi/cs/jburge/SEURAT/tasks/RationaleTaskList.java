
package edu.wpi.cs.jburge.SEURAT.tasks;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.inference.UpdateManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * This defines the domain model for the Rationale Task List. Unlike
 * the initial example, we use a hash table rather than a vector since we need
 * to be able directly access elements.
 * 
 * This class is a Singleton (follows the Singleton design pattern). Yes, the
 * constructor is SUPPOSED to be private! If you need a access to the table,
 * use the getHandle method.
 * 
 * This code is based on the Eclipse Corner article 
 * "Building and delivering a table editor with SWT/JFace"
 * http://www.eclipse.org/articles/Article-Table-viewer/table_viewer.html
 * and on the example attached to the article:
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Jun 11, 2003 by lgauthier@opnworks.com
 *
 *
 **/

public final class RationaleTaskList {
	
	/**
	 * The maximum length of our status list. May need to be larger!
	 */
	private final int COUNT = 50;
	/**
	 * The handle to this static class
	 */
	private static RationaleTaskList s;
	
	/**
	 * Our collection of rationale status elements. Unlike the table
	 * view example, we use a hashtable because we need to be able to add and
	 * remove specific elements as the status changes.
	 */
	private Hashtable<String, RationaleTask> tasks = new Hashtable<String, RationaleTask>(COUNT);
	/**
	 * The viewers that care if the data changes. We'll just have one viewer.
	 */
	private Set<IRationaleTaskListViewer> changeListeners = new HashSet<IRationaleTaskListViewer>();
	
	/**
	 * Constructor
	 */
	private RationaleTaskList() {
		super();
		this.initData();
	}
	
	public static RationaleTaskList getHandle() {
		if (s == null)
		{
			s = new RationaleTaskList();
		}
		return s;
	}
	
	/**
	 * Initialize our status table by reading in the current set of status
	 * elements (RationaleStatus) from the database.
	 *
	 */
	private void initData() {
		RationaleDB db = RationaleDB.getHandle();
		Vector<RationaleStatus> statList = db.getStatus();
		Iterator iterator = statList.iterator();
		while (iterator.hasNext())
		{
			RationaleStatus stat = (RationaleStatus) iterator.next();
			//Check to see if the status element has been overriden - if yes, we don't display it
			if (!stat.getOverride())
			{
				RationaleElementType parentType = stat.getRationaleType();
				int pid = stat.getParent();
				String rationale = "not retrieved";
				if (parentType == RationaleElementType.REQUIREMENT)
				{
					Requirement req = new Requirement();
					req.fromDatabase(pid);
					rationale = req.getName(); 
				}
				else if (parentType == RationaleElementType.DECISION)
				{
					Decision dec = new Decision();
					dec.fromDatabase(pid);
					rationale = dec.getName();
				}
				else if (parentType == RationaleElementType.ALTERNATIVE)
				{
					Alternative alt = new Alternative();
					alt.fromDatabase(pid);
					rationale = alt.getName();
				} 
				
				
				RationaleTask task = new RationaleTask(
						stat.getParent(), stat.getStatus(), 
						stat.getDescription(), stat.getRationaleType(),
						rationale, stat.getDate(), stat.getStatusType());
				String key = makeKey(stat.getParent(), stat.getStatusType());
				tasks.put(key, task); //was an add
			}				
		}
		
	};
	
	/**
	 * Define a unique key for our hash table element based on the
	 * parent's element ID and the type of status message
	 * @param id - the unique id of the rationale element the status is about
	 * @param status - the status type
	 * @return a key to the hashtable for that element
	 */
	private String makeKey(int id, RationaleStatusType status)
	{
		String key;
//		System.out.println(new Integer(id).toString());
		if (status == null)
		{
			System.out.println("null status?");
		}
		key = new Integer(id).toString() + "-" + status.toString();
		return key;
	}
	
	/**
	 * Reset our table by removing all the elements and re-initializing
	 *
	 */
	public void resetTable()
	{
		Enumeration te = tasks.elements();
		while (te.hasMoreElements())
		{
			RationaleTask task = (RationaleTask) te.nextElement();
			removeTask(task);
		}
		initData();
		//put in a map to the item, indexed by name and type
		//assumes we will know the type if we are mucking with status
//		m.addItem(m.makeKey(stat.getParent(), parentType), key);
		
		te = tasks.elements();
		while (te.hasMoreElements())
		{
			RationaleTask task = (RationaleTask) te.nextElement();
			Iterator iterator = changeListeners.iterator();
			while (iterator.hasNext())
			{
				((IRationaleTaskListViewer) iterator.next()).addTask(task);
			}
			
		}
	}
	
	public Hashtable getTasks() {
		return tasks;
	}
	
	/**
	 * Given a vector of new status elements, add them to our hash table
	 * @param statV - the status entries
	 */
	public void addTasks(Vector<RationaleStatus> statV) {
		Iterator statI = statV.iterator();
		while (statI.hasNext())
		{
			RationaleStatus stat = (RationaleStatus) statI.next();
			if (stat.getParent() <= 0)
			{
				System.out.println("not adding null task");
			}
			else
			{
				addTask(stat); 
			}
			
		}
	}
	
	/**
	 * Remove any status elements that are no longer true
	 * @param statV - our status elements
	 */
	public void removeTasks(Vector<RationaleStatus> statV) {
		Iterator statI = statV.iterator();
		while (statI.hasNext())
		{
			RationaleStatus stat = (RationaleStatus) statI.next();
			String key = makeKey(stat.getParent(), stat.getStatusType());
			RationaleTask task = (RationaleTask) tasks.get(key);
			if (task != null)
			{
				removeTask(task);
			}			
		}
	}
	/**
	 * Add a new status entry (task) to the task list.
	 * @param stat - the new entry
	 */
	public void addTask(RationaleStatus stat) {
		//make sure the task isn't already in there!
		String key = makeKey(stat.getParent(), stat.getStatusType());
		if (!tasks.containsKey(key))
		{
			RationaleElementType parentType = stat.getRationaleType();
			int pid = stat.getParent();
			String rationale = "not retrieved";
			if (parentType == RationaleElementType.REQUIREMENT)
			{
				Requirement req = new Requirement();
				req.fromDatabase(pid);
				rationale = req.getName(); 
			}
			else if (parentType == RationaleElementType.DECISION)
			{
				Decision dec = new Decision();
				dec.fromDatabase(pid);
				rationale = dec.getName();
			}
			else if (parentType == RationaleElementType.ALTERNATIVE)
			{
				Alternative alt = new Alternative();
				alt.fromDatabase(pid);
				rationale = alt.getName();
			} 
			
			
			RationaleTask task = new RationaleTask(
					stat.getParent(), stat.getStatus(), 
					stat.getDescription(), stat.getRationaleType(),
					rationale, stat.getDate(), stat.getStatusType());
			
			
//			tasks.add(tasks.size(), task);
			tasks.put(key,task);
			
			
			//Now we've udated the model, we need to let the view know
			//that a new task has been added!
			Iterator iterator = changeListeners.iterator();
			while (iterator.hasNext())
				((IRationaleTaskListViewer) iterator.next()).addTask(task);
		}
		else
		{
			System.out.println("Duplicate item not added to task list");
		}
	}
	
	/**
	 * Remove a rationale task from the list and let the view know to remove
	 * it from the display. 
	 * @param task - the task we are removing
	 */
	public void removeTask(RationaleTask task) {
//		tasks.remove(task);
		tasks.remove(makeKey(task.getParent(), task.getStatusType()));
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IRationaleTaskListViewer) iterator.next()).removeTask(task);
	}
	
	/**
	 * Remove the task (rationale status element) from the hash table
	 * @param stat - the status element
	 */
	public void removeTask(RationaleStatus stat) {
		String key = makeKey(stat.getParent(), stat.getStatusType());
		tasks.remove(key);
	}
	
	/**
	 * Invoked when the user has indicated that a task should be overriden
	 * and no longer displayed. The task is updated in the database, removed
	 * from the status hashtable (which will alert the view), and the UpdateManager
	 * is used to tell the RationaleExplorer to not display the status icon
	 * in the tree
	 * @param task - the task being overriden.
	 */
	public void overrideTask(RationaleTask task)
	{
		RationaleDB db = RationaleDB.getHandle();
		db.overrideStatus(task.getDescription(), task.getParent(), task.getRationaleType());
		removeTask(task);
		//need to update our tree item!
		UpdateManager mgr = UpdateManager.getHandle();
		//we don't know what we are... so need to find out
		RationaleElementType ourType = RationaleElementType.fromString(task.getRationaleType());
		int id = 0;
		if (ourType == RationaleElementType.REQUIREMENT)
		{
			Requirement newReq = new Requirement();
			newReq.fromDatabase(task.getRationale());
			id = newReq.getID();
		}
		else if (ourType == RationaleElementType.DECISION)
		{
			Decision newDec = new Decision();
			newDec.fromDatabase(task.getRationale());
			id = newDec.getID();
		}
		else if (ourType == RationaleElementType.ALTERNATIVE)
		{
			Alternative newAlt = new Alternative();
			newAlt.fromDatabase(task.getRationale());
			id = newAlt.getID();
		}
		else
		{
			System.out.println("Error - unexpected type!");
		}
		mgr.addUpdate(id, task.getRationale(), ourType);
		mgr.makeTreeUpdates();
	}
	
	/**
	 * When a task has changed, we need to let the view know so it can update
	 * accordingly.
	 * @param task
	 */
	public void taskChanged(RationaleTask task) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IRationaleTaskListViewer) iterator.next()).updateTask(task);
	}
	
	/**
	 * De-registers a view so that it does not need to be notified if
	 * something changes. Of course, we only have the one task list viewer...
	 * @param viewer 0 the viewer to remove
	 */
	public void removeChangeListener(IRationaleTaskListViewer viewer) {
		changeListeners.remove(viewer);
	}
	
	/**
	 * Adds a new view. We will only have the one view.
	 * @param viewer - the view to add
	 */
	public void addChangeListener(IRationaleTaskListViewer viewer) {
		changeListeners.add(viewer);
	}
	
}
