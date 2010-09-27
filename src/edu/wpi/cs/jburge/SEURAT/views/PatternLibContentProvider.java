/**
 * This provides the content for the Pattern Library tree view.
 * @author wangw2
 *
 */
package edu.wpi.cs.jburge.SEURAT.views;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternDecision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;

public class PatternLibContentProvider implements IStructuredContentProvider, IDeltaListener,
ITreeContentProvider{
	
	protected TreeParent invisibleRoot;
	
	protected TreeViewer viewer;

	/**
	 * 
	 * Initializes the roots of the invisibleRoot object.
	 * @return
	 */
	public TreeParent initialize() {
		
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		map.clearMap();
		
//		System.out.println("Start to initialize the pattern tree");
		TreeParent root = new TreeParent("Pattern Library - " + RationaleDB.getDbName(), RationaleElementType.RATIONALE);
				
		invisibleRoot = new TreeParent("", RationaleElementType.RATIONALE);
		invisibleRoot.addChild(root);		
		
		TreeParent architop = new TreeParent("Architectural Patterns", RationaleElementType.RATIONALE);
		root.addChild(architop);
		addArchitecturePatterns(architop, null, null);
		
		TreeParent designtop = new TreeParent("Design Patterns", RationaleElementType.RATIONALE);
		root.addChild(designtop);
		addDesignPatterns(designtop, null, null);
		
		TreeParent idiomtop = new TreeParent("Idioms", RationaleElementType.RATIONALE);
		root.addChild(idiomtop);
		addIdioms(idiomtop, null, null);

		return invisibleRoot;
	}
	
	/**
	 * Add a new pattern element, create its ontology, positive, negative argument roots, as well as its
	 * decision root inside the parent object.
	 * @param parent The parent node to add the pattern
	 * @param parentName
	 * @param parentType
	 */
	private void addArchitecturePatterns(TreeParent parent, String parentName, RationaleElementType parentType){
		
		RationaleDB d = RationaleDB.getHandle();
		Vector patternList = d.getArchitecturePatterns(parentName, parentType);
		Enumeration patterns =patternList.elements();
		while (patterns.hasMoreElements())
		{
			TreeParent child = (TreeParent) patterns.nextElement();
			String childName = child.getName();
			parent.addChild(child);
			
			//Pattern childPattern = (Pattern)patterns.nextElement();
			//String OntName = patterns.nextElement().posiOnt.getName();
			
			TreeParent ontTop = new TreeParent("Affected Quality Attributes", RationaleElementType.RATIONALE);
			child.addChild(ontTop);
			TreeParent posiTop = new TreeParent("Positive", RationaleElementType.RATIONALE);
			TreeParent negaTop = new TreeParent("Negative", RationaleElementType.RATIONALE);
			ontTop.addChild(posiTop);
			ontTop.addChild(negaTop);
			
			addOntEntries(posiTop, childName, true);
			addOntEntries(negaTop, childName, false);
			
			TreeParent decTop = new TreeParent("Decisions", RationaleElementType.RATIONALE);
			child.addChild(decTop);
			addSubDecisions(decTop, childName, parentName);
		}	
	}
	
	private void addOntEntries(TreeParent parent, String parentName, boolean isPositive)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getPatternOntologies(parentName, isPositive);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			String childName = onts.nextElement().toString();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.ONTENTRY);
			parent.addChild(child);
			//addOntology(child, childName);
		}
	}
	
	private void addSubDecisions(TreeParent parent, String parentName, String grandParentName){
		RationaleDB d = RationaleDB.getHandle();
		Vector subDecisions = d.getPatternDecisions(parentName, grandParentName);
		Enumeration subs = subDecisions.elements();
		while (subs.hasMoreElements())
		{
			String childName = subs.nextElement().toString();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.PATTERNDECISION);
			parent.addChild(child);
			addCandidatePatterns(child, childName);//addOntology(child, childName);
			//addDecisions(child, childName, RationaleElementType.DECISION);
		}
	}
	
	private void addCandidatePatterns(TreeParent parent, String parentName){
		RationaleDB d = RationaleDB.getHandle();
		PatternDecision parentPatternDecision = new PatternDecision();
		parentPatternDecision.fromDatabase(parentName);
		Vector candidatePatterns = d.getCandidatePatterns(parentName);
		Enumeration candidates = candidatePatterns.elements();
		while (candidates.hasMoreElements()){
			String childName = ((Pattern)candidates.nextElement()).getName();
			TreeParent child = new TreeParent(childName, RationaleElementType.PATTERN);
			parent.addChild(child);
		}
	}
	
	private void addDecisions(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getDecisions(parentName, parentType);
		Enumeration decs = reqList.elements();
		while (decs.hasMoreElements())
		{
//			String childName = (String) decs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.DECISION);
			TreeParent child = (TreeParent) decs.nextElement();
			String childName = child.getName(); 
			parent.addChild(child);
			//add alternatives...
			//addAlternatives(child, childName, RationaleElementType.DECISION);
			//add questions...
			//addQuestions(child, childName, RationaleElementType.DECISION);
			//add sub-decisions
			addDecisions(child, childName, RationaleElementType.DECISION);
			//add sub-decisions....
			//addConstraints(child, childName, RationaleElementType.DECISION);
			//add candidate patterns
			//addPatterns(child, childName);
			
		}			
	}
	
	private void addDesignPatterns(TreeParent parent, String parentName, RationaleElementType parentType){
		
		RationaleDB d = RationaleDB.getHandle();
		Vector patternList = d.getDesignPatterns(parentName, parentType);
		Enumeration patterns =patternList.elements();
		while (patterns.hasMoreElements())
		{
			TreeParent child = (TreeParent) patterns.nextElement();
			String childName = child.getName();
			parent.addChild(child);
			
			TreeParent ontTop = new TreeParent("Affected Quality Attributes", RationaleElementType.RATIONALE);
			child.addChild(ontTop);
			TreeParent posiTop = new TreeParent("Positive", RationaleElementType.RATIONALE);
			TreeParent negaTop = new TreeParent("Negative", RationaleElementType.RATIONALE);
			ontTop.addChild(posiTop);
			ontTop.addChild(negaTop);
			
			addOntEntries(posiTop, childName, true);
			addOntEntries(negaTop, childName, false);
			
			TreeParent decTop = new TreeParent("Decisions", RationaleElementType.RATIONALE);
			child.addChild(decTop);
			addSubDecisions(decTop, childName, parentName);
		}		
	}
	
	private void addIdioms(TreeParent parent, String parentName, RationaleElementType parentType){
		
		RationaleDB d = RationaleDB.getHandle();
		Vector patternList = d.getIdioms();
		Enumeration patterns =patternList.elements();
		while (patterns.hasMoreElements())
		{
			TreeParent child = (TreeParent) patterns.nextElement();
			String childName = child.getName();
			parent.addChild(child);
			
			TreeParent ontTop = new TreeParent("Affected Quality Attributes", RationaleElementType.RATIONALE);
			child.addChild(ontTop);
			TreeParent posiTop = new TreeParent("Positive", RationaleElementType.RATIONALE);
			TreeParent negaTop = new TreeParent("Negative", RationaleElementType.RATIONALE);
			ontTop.addChild(posiTop);
			ontTop.addChild(negaTop);
			
			addOntEntries(posiTop, childName, true);
			addOntEntries(negaTop, childName, false);
			
			TreeParent decTop = new TreeParent("Decisions", RationaleElementType.RATIONALE);
			child.addChild(decTop);
			addSubDecisions(decTop, childName, parentName);
		}		
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(ResourcesPlugin.getWorkspace())) {
			if (invisibleRoot==null)
			{
				System.out.println("root is null?");
				initialize();
			} 
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		System.out.println("viewer ok");
		if (!(oldInput instanceof TreeParent))
		{
			System.out.println("not a TreeParent???");
			if (oldInput instanceof TreeObject)
			{
				System.out.println("it is a tree object");
			}
			return;
		}
		if(oldInput != null) {
			removeListenerFrom((TreeParent)oldInput);
		}
		if(newInput != null) {
			addListenerTo((TreeParent)newInput);
		}
		
	}
	
	protected void addListenerTo(TreeParent element) {
		element.addListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent aElement = (TreeParent) iterator.next();
			addListenerTo(aElement);
		}
	}
	
	protected void removeListenerFrom(TreeParent element) {
		element.removeListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent nextEl = (TreeParent) iterator.next();
			removeListenerFrom(nextEl);
		}
	}

	public void add(DeltaEvent event) {
		Object ourObj = ((TreeParent)event.receiver()).getParent();
		viewer.refresh(ourObj, false);
		
	}

	public void remove(DeltaEvent event) {
		add(event);
		
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return new Object[0];
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
	
}
