/*
 * Created on Jan 4, 2008
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.views;


import java.util.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.core.resources.ResourcesPlugin;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
/**
 * This provides the content for the RationaleExplorer tree view.
 * @author jburge
 *
 */
public class CandidateRationaleContentProvider implements IStructuredContentProvider, IDeltaListener,
ITreeContentProvider {
	protected CandidateTreeParent invisibleRoot;
	protected TreeViewer viewer;
	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 * <p>
	 * A typical use for this method is registering the content provider as a listener
	 * to changes on the new input (using model-specific means), and deregistering the viewer 
	 * from the old input. In response to these change notifications, the content provider
	 * propagates the changes to the viewer.
	 * </p>
	 *
	 * @param viewer the viewer
	 * @param oldInput the old input element, or <code>null</code> if the viewer
	 *   did not previously have an input
	 * @param newInput the new input element, or <code>null</code> if the viewer
	 *   does not have an input
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = (TreeViewer)viewer;
//		System.out.println("viewer ok");
		if (!(oldInput instanceof CandidateTreeParent))
		{
//			System.out.println("not a TreeParent???");
			if (oldInput instanceof TreeObject)
			{
//				System.out.println("it is a tree object");
			}
			return;
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child element of the given element.
	 * @param element - the tree element
	 */
	protected void removeListenerFrom(TreeParent element) {
		element.removeListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent nextEl = (TreeParent) iterator.next();
			removeListenerFrom(nextEl);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child element
	 * @param element - the tree element
	 * 
	 */
	protected void addListenerTo(CandidateTreeParent element) {
		element.addListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			CandidateTreeParent aElement = (CandidateTreeParent) iterator.next();
			addListenerTo(aElement);
		}
	}
	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object ourObj = ((CandidateTreeParent)event.receiver()).getParent();
		viewer.refresh(ourObj, false);
	}
	
	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
		add(event);
	}
	public void dispose() {
	}
	
	/**
	 * Returns all the elements in our tree. If the "root" is null, 
	 * initialize the tree
	 * @param parent the workspace
	 * @return the objects
	 */
	public Object[] getElements(Object parent) {
		if (parent.equals(ResourcesPlugin.getWorkspace())) {
			if (invisibleRoot==null)
			{
//				System.out.println("root is null?");
				initialize();
			} 
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	} 		
	
	
	
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
	public Object [] getChildren(Object parent) {
		if (parent instanceof CandidateTreeParent) {
			return ((CandidateTreeParent)parent).getChildren();
		}
		return new Object[0];
	}
	public boolean hasChildren(Object parent) {
		if (parent instanceof CandidateTreeParent)
			return ((CandidateTreeParent)parent).hasChildren();
		return false;
	}
	
	/**
	 * Find the candidate with a particular name
	 * @return our tree parent
	 */
	public CandidateTreeParent findCandidate(String name)
	{
		CandidateTreeParent ourParent;
		ourParent = findCandidate(invisibleRoot, name);
		return ourParent;
		
	}
	
	/**
	 * Find candidate (recursive version)
	 * 
	 */
	private CandidateTreeParent findCandidate(CandidateTreeParent node, String name)
	{
		CandidateTreeParent match = null;
		if (node.getName().compareTo(name) == 0)
		{
			match = node;
		}
		else if (node.hasChildren())
		{
			//lets get our children
			TreeObject[] children = node.getChildren();
			int index = 0;
			while ((index < children.length) && (match == null))
			{
				match = findCandidate((CandidateTreeParent) children[index], name);
				index++;
			}
		}
		return match;
	}
	
	private CandidateTreeParent reqtop;
	private CandidateTreeParent dectop;
	
	/**
	 * Initialize our tree from the database.  
	 * @return the root of the tree
	 */
	public CandidateTreeParent initialize() {
		
		//set up our tree
		CandidateTreeParent root = new CandidateTreeParent("Candidate Rationale" + " - " + RationaleDB.getDbName(), RationaleElementType.RATIONALE);
		
		invisibleRoot = new CandidateTreeParent("", RationaleElementType.RATIONALE);
		invisibleRoot.addChild(root);
		
		//Get a sorted list of requirements.
		//For each requirement, get a sorted list of its sub-requirements
		//For each requirement, get a sorted list of its decisions
		//..... put the decisions into a hash table so when we create a list of
		//all decisions we don't reuse them. Or should we reuse them? Yes for now
		
		reqtop = new CandidateTreeParent("Requirements", RationaleElementType.RATIONALE);
		root.addChild(reqtop);
		addElements(reqtop, RationaleElementType.REQUIREMENT);
		dectop = new CandidateTreeParent("Decisions", RationaleElementType.RATIONALE);
		addElements(dectop, RationaleElementType.DECISION);
		root.addChild(dectop);
		
	
		return invisibleRoot;
	}
	
	public CandidateTreeParent getReqtop()
	{
		return this.reqtop;
	}
	public CandidateTreeParent getDectop()
	{
		return this.dectop;
	}
	
	
	/**
	 * Add the requirements to our tree that live "below" the passed in
	 * parent element
	 * @param parent - the root element to add them to
	 * @param parentName - the parent element that has requirements under it
	 * @param parentType - the type of the parent element
	 */
	private void addElements(CandidateTreeParent parent, RationaleElementType ourType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector<CandidateTreeParent> reqList = d.getCandidateTreeElements(ourType);
		Enumeration reqs =reqList.elements();
		while (reqs.hasMoreElements())
		{
			CandidateTreeParent child = (CandidateTreeParent) reqs.nextElement();
			String childName = child.getName();
			CandidateRationale req = new CandidateRationale(ourType);
			req.fromDatabase(childName);
			parent.addChild(child);
			child.setActive(true);
			
			//add our children
			Enumeration children = req.getChildren().elements();
			while (children.hasMoreElements())
			{
				CandidateRationale nextC = (CandidateRationale) children.nextElement();
				CandidateTreeParent tChild = new CandidateTreeParent(nextC.getName(), nextC.getType());
				child.addChild(tChild);
				tChild.setActive(true);
				
				//and our children's children 
				Enumeration grandchildren = nextC.getChildren().elements();
				while (grandchildren.hasMoreElements())
				{
					CandidateRationale nextG = (CandidateRationale) grandchildren.nextElement();
					CandidateTreeParent gChild = new CandidateTreeParent(nextG.getName(), nextG.getType());
					tChild.addChild(gChild);
					gChild.setActive(true);
					
					//great grand children...
					Enumeration ggrandchildren = nextG.getChildren().elements();
					while (ggrandchildren.hasMoreElements())
					{
						CandidateRationale nextGG = (CandidateRationale) ggrandchildren.nextElement();
						CandidateTreeParent ggChild = new CandidateTreeParent(nextGG.getName(), nextGG.getType());
						gChild.addChild(ggChild);
						ggChild.setActive(true);
					}
				}
			}
		}			
	}
	

	/**
	 * Add a new element to the tree
	 * @param parent the parent node
	 * @param ele the element
	 * @return the new tree element
	 */
	public CandidateTreeParent addNewElement(CandidateTreeParent parent, RationaleElement ele)
	{
		CandidateTreeParent child = new CandidateTreeParent(ele.getName(), ((CandidateRationale)ele).getType());
		child.setActive(true);
		parent.addChild(child);
		return child;
	}
	
	/**
	 * Remove an element from the tree and all its children
	 * @param child the element being removed
	 */
	public void removeElement(CandidateTreeParent child)
	{
		CandidateTreeParent parent = child.getAltParent();
		//first, remove all the children of the child
		child.removeChildren();
		//not removing in an iterator
		parent.removeChild(child, false);
	}
}