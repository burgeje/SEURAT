/*
 * Created on Oct 24, 2003
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
public class RationaleViewContentProvider implements IStructuredContentProvider, IDeltaListener,
ITreeContentProvider {
	protected TreeParent invisibleRoot;
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
		if (!(oldInput instanceof TreeParent))
		{
//			System.out.println("not a TreeParent???");
			if (oldInput instanceof TreeObject)
			{
//				System.out.println("it is a tree object");
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
	protected void addListenerTo(TreeParent element) {
		element.addListener(this);
		for (Iterator iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent aElement = (TreeParent) iterator.next();
			addListenerTo(aElement);
		}
	}
	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object ourObj = ((TreeParent)event.receiver()).getParent();
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
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return new Object[0];
	}
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
	
	
	/**
	 * Initialize our tree from the database.  
	 * @return the root of the tree
	 */
	public TreeParent initialize() {
		
//		System.out.println("Initializing the tree");
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		map.clearMap();
		//set up our tree
		TreeParent root = new TreeParent("Rationale" + " - " + RationaleDB.getDbName(), RationaleElementType.RATIONALE);
		
		invisibleRoot = new TreeParent("", RationaleElementType.RATIONALE);
		invisibleRoot.addChild(root);
		
		//Get a sorted list of requirements.
		//For each requirement, get a sorted list of its sub-requirements
		//For each requirement, get a sorted list of its decisions
		//..... put the decisions into a hash table so when we create a list of
		//all decisions we don't reuse them. Or should we reuse them? Yes for now
		
		TreeParent reqtop = new TreeParent("Requirements", RationaleElementType.RATIONALE);
		root.addChild(reqtop);
		addRequirements(reqtop, null, null);
		TreeParent dectop = new TreeParent("Decisions", RationaleElementType.RATIONALE);
		addDecisions(dectop, null, null);
		root.addChild(dectop);
		
		TreeParent tradetop = new TreeParent("Tradeoffs", RationaleElementType.RATIONALE);
		addTradeoffs(tradetop, RationaleElementType.TRADEOFF);
		root.addChild(tradetop);
		TreeParent cotop = new TreeParent("Co-occurrences", RationaleElementType.RATIONALE);
		addTradeoffs(cotop, RationaleElementType.COOCCURRENCE);
		root.addChild(cotop);
		TreeParent ontTop = new TreeParent("Argument-Ontology", RationaleElementType.ONTENTRY);
		root.addChild(ontTop);
		addOntology(ontTop, "Argument-Ontology");
		TreeParent productTop = new TreeParent("Design-Product-Ontology", RationaleElementType.DESIGNPRODUCTENTRY);
		root.addChild(productTop);
		addProduct(productTop, "Design-Product-Ontology");
		TreeParent constraintTop = new TreeParent("Design-Constraints", RationaleElementType.CONSTRAINT);
		root.addChild(constraintTop);
		addConstraints(constraintTop, "Design-Constraints", RationaleElementType.CONSTRAINT);
		TreeParent conttop = new TreeParent("Design-Contingencies", RationaleElementType.RATIONALE);
		root.addChild(conttop);
		addContingencies(conttop, null, null);
		TreeParent designerTop = new TreeParent("Designer-Profiles", RationaleElementType.DESIGNER);
		root.addChild(designerTop);
		addDesigners(designerTop, null, null);
		
		
		return invisibleRoot;
	}
	
	private boolean atTop(String parentName, RationaleElementType parentType)
	{
		boolean top = false;
		if ((parentName == null) || (parentType == null))
		{
			top = true;
		}
		return top;
	}
	/**
	 * Add the requirements to our tree that live "below" the passed in
	 * parent element
	 * @param parent - the root element to add them to
	 * @param parentName - the parent element that has requirements under it
	 * @param parentType - the type of the parent element
	 */
	private void addRequirements(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getRequirements(parentName, parentType);
		Enumeration reqs =reqList.elements();
		boolean top = atTop(parentName, parentType);
		while (reqs.hasMoreElements())
		{
//			String childName = (String) reqs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.REQUIREMENT);
			TreeParent child = (TreeParent) reqs.nextElement();
			String childName = child.getName();
			Requirement req = new Requirement();
			req.fromDatabase(childName);
			if (!(req.hasParent() && top))
			{
				parent.addChild(child);
				child.setActive(req.getEnabled());
				//check to see if there is a related ontology entry
				if ((req.getType() == ReqType.NFR) && (req.getOntology() != null))
				{
					String ontName = req.getOntology().getName();
					TreeParent ontchild = new TreeParent(ontName, RationaleElementType.ONTENTRY);
					child.addChild(ontchild);
				}
				//add our arguments
				addArguments(child, childName, RationaleElementType.REQUIREMENT);
				//add any questions
				addQuestions(child, childName, RationaleElementType.REQUIREMENT);
				addRequirements(child, childName, RationaleElementType.REQUIREMENT);
				//add sub-decisions....
				addDecisions(child, childName, RationaleElementType.REQUIREMENT);
			}
		}			
	}
	
	/**
	 * Add the decisions that belong after the parent element passed in
	 * @param parent the parent element in the tree
	 * @param parentName the parent element's name
	 * @param parentType the type of parent
	 */
	private void addDecisions(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getDecisions(parentName, parentType);
		Enumeration decs = reqList.elements();
		boolean top = atTop(parentName, parentType);
		while (decs.hasMoreElements())
		{
//			String childName = (String) decs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.DECISION);
			TreeParent child = (TreeParent) decs.nextElement();
			String childName = child.getName(); 
			Decision dec = new Decision();
			dec.fromDatabase(childName);
			if (!(dec.hasParent() && top))
			{
				parent.addChild(child);
				//add alternatives...
				addAlternatives(child, childName, RationaleElementType.DECISION);
				//add questions...
				addQuestions(child, childName, RationaleElementType.DECISION);
				//add sub-decisions
				addDecisions(child, childName, RationaleElementType.DECISION);
				//add sub-decisions....
				addConstraints(child, childName, RationaleElementType.DECISION);
			}
		}			
	}
	
	/**
	 * Add alternatives to the tree that belong under teh parent
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param parentType the parent's type
	 */
	private void addAlternatives(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getAlternatives(parentName, parentType);
		Enumeration decs = reqList.elements();
		while (decs.hasMoreElements())
		{
//			String childName = (String) decs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.ALTERNATIVE);
			TreeParent child = (TreeParent) decs.nextElement();
			String childName = child.getName();
			Alternative alt = new Alternative();
			alt.fromDatabase(childName);
			child.setActive(alt.getStatus() == AlternativeStatus.ADOPTED);
			
			parent.addChild(child);
			//add alternatives...
			addArguments(child, childName, RationaleElementType.ALTERNATIVE);
			//add questions...
			addQuestions(child, childName, RationaleElementType.ALTERNATIVE);
			//add sub-decisions
			addDecisions(child, childName, RationaleElementType.ALTERNATIVE);
			//add constraints
			addAltConstRel(child, childName, RationaleElementType.ALTCONSTREL);
			
		}			
	}
	
	/**
	 * Add arguments to the tree that belong under the parent passed in
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param parentType the parent's type
	 */
	private void addArguments(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getArguments(parentName, parentType);
		Enumeration decs = reqList.elements();
		while (decs.hasMoreElements())
		{
//			String childName = (String) decs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.ARGUMENT);
			TreeParent child = (TreeParent) decs.nextElement();
			String childName = child.getName();
			parent.addChild(child);
			//add our child
			String argType = d.getArgumentType(childName);
			TreeParent argchild = null;
			if (argType != null)
			{
			if (argType.compareTo("Claim") == 0)
			{
				String clmName = d.getClaim(childName); 
				argchild = new TreeParent(clmName, RationaleElementType.CLAIM); 
				String ontName = d.getClaimOntEntry(clmName);
				TreeParent ontchild = new TreeParent(ontName, RationaleElementType.ONTENTRY);
				argchild.addChild(ontchild);
			}
			else if (argType.compareTo("Requirement") == 0)
			{
				String reqName = d.getRequirement(childName);
				argchild = new TreeParent(reqName, RationaleElementType.REQUIREMENT);
				RationaleErrorLevel ourStatus = 
					d.getActiveStatus(reqName, RationaleElementType.REQUIREMENT);
				argchild.setStatus(ourStatus);
				boolean active = RationaleDB.getRationaleElement(reqName, RationaleElementType.REQUIREMENT).getEnabled();
				argchild.setActive(active);
			}
			else if (argType.compareTo("Assumption") == 0)
			{
				String asmName = d.getAssumption(childName);
				argchild = new TreeParent(asmName, RationaleElementType.ASSUMPTION);
				Assumption assm = new Assumption();
				assm.fromDatabase(asmName);
				argchild.setActive(assm.getEnabled());
			}
			else if (argType.compareTo("Alternative") == 0)
			{
				String altName = d.getAlternative(childName);
				Alternative alt = new Alternative();
				alt.fromDatabase(altName);
				argchild = new TreeParent(altName, RationaleElementType.ALTERNATIVE);
				if (alt.getStatus() == AlternativeStatus.ADOPTED)
				{
					argchild.setActive(true);
				}
				else
				{
					argchild.setActive(false);
				}
				RationaleErrorLevel ourStatus = 
					d.getActiveStatus(altName, RationaleElementType.ALTERNATIVE); //was req?
				argchild.setStatus(ourStatus);
			}
			else
			{
				System.out.println("No match on the argument type");
			}
			child.addChild(argchild);
			}
			//add arguments...
			addArguments(child, childName, RationaleElementType.ARGUMENT);
			//add questions...
			addQuestions(child, childName, RationaleElementType.ARGUMENT);
			
		}			
	}
	
	/**
	 * Add tradeoffs to the tree
	 * @param parent the element they go under
	 * @param type the tradeoff type (tradeoff or co-occurence)
	 */
	private void addTradeoffs(TreeParent parent, RationaleElementType type)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getTradeoffs(type);
		Enumeration decs = reqList.elements();
		while (decs.hasMoreElements())
		{
			String childName = (String) decs.nextElement();
			TreeParent child = new TreeParent(childName, 
					type);
			parent.addChild(child);
			//add our ontologies
			String ont1 = d.getTradeOntology(childName, "ontology1");
			TreeParent ont1c = new TreeParent(ont1, RationaleElementType.ONTENTRY);
			child.addChild(ont1c);
			String ont2 = d.getTradeOntology(childName, "ontology2");
			TreeParent ont2c = new TreeParent(ont2, RationaleElementType.ONTENTRY);
			child.addChild(ont2c);
		}
		
		
	}
	
	/**
	 * Add child ontology elements to the tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 */
	private void addOntology(TreeParent parent, String parentName )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getOntology(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			String childName = (String) onts.nextElement();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.ONTENTRY);
			parent.addChild(child);
			addOntology(child, childName);
		}
		
	}
	
	/**
	 * Add product components to the product ontology
	 * @param parent the parent node
	 * @param parentName the parent's name
	 */
	private void addProduct(TreeParent parent, String parentName )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getProducts(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			String childName = (String) onts.nextElement();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.DESIGNPRODUCTENTRY);
			parent.addChild(child);
			addProduct(child, childName);
		}
		
	}
	
	/**
	 * Add constraints to the constraint tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param ptype
	 */
	private void addConstraints(TreeParent parent, String parentName, RationaleElementType ptype )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector contList = d.getConstraints(parentName, ptype);
		
		
		Enumeration conts = contList.elements();
		while (conts.hasMoreElements())
		{
			String childName = (String) conts.nextElement();
			TreeParent child = new TreeParent(childName,
					RationaleElementType.CONSTRAINT);
			parent.addChild(child);
			Constraint cont = new Constraint();
			cont.fromDatabase(childName);
			//add our design product
			DesignProductEntry prod = cont.getComponent();
			if (prod != null)
			{
				TreeParent prodChild = new TreeParent(prod.getName(), RationaleElementType.DESIGNPRODUCTENTRY);
				child.addChild(prodChild);
			}
			
			Enumeration onts = cont.getOntEntries().elements();
			while (onts.hasMoreElements())
			{
				TreeParent child1 = new TreeParent((String) ((OntEntry) onts.nextElement()).getName(), 
						RationaleElementType.ONTENTRY);
				child.addChild(child1);
			}
			
			addConstraints(child, childName, RationaleElementType.CONSTRAINT);
		}
		
	}
	
	/**
	 * Add contingencies
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param parentType the parent's type
	 */
	private void addContingencies(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getContingencies(parentName, parentType);
		Enumeration reqs =reqList.elements();
		while (reqs.hasMoreElements())
		{
//			String childName = (String) reqs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.REQUIREMENT);
			TreeParent child = (TreeParent) reqs.nextElement();
			String childName = child.getName();
			Contingency req = new Contingency();
			req.fromDatabase(childName);
			parent.addChild(child);
			child.setActive(true);
		}			
	}
	
	/**
	 * Add designers to the tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param parentType the parent's type
	 */
	private void addDesigners(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getDesigners(parentName, parentType);
		Enumeration reqs =reqList.elements();
		while (reqs.hasMoreElements())
		{
//			String childName = (String) reqs.nextElement();
//			TreeParent child = new TreeParent(childName, 
//			RationaleElementType.REQUIREMENT);
			TreeParent child = (TreeParent) reqs.nextElement();
			String childName = child.getName();
			Designer req = new Designer();
			req.fromDatabase(childName);
			parent.addChild(child);
			child.setActive(true);
			Vector areaList = req.getExpertise();
			if (areaList != null)
			{
				Enumeration areas = areaList.elements();
				while (areas.hasMoreElements())
				{
					TreeParent element = new TreeParent(((AreaExp) areas.nextElement()).getName(),
							RationaleElementType.EXPERTISE);
					child.addChild(element);
					element.setActive(true);
					
				}
			}
			
		}			
	}
	/**
	 * Add questions to the tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param parentType the parent's type
	 */
	private void addQuestions(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getQuestions(parentName, parentType);
		Enumeration decs = reqList.elements();
		while (decs.hasMoreElements())
		{
			Question quest = new Question();
			TreeParent child = (TreeParent) decs.nextElement();
			quest.fromDatabase(child.getName());
			if (quest.getStatus() == QuestionStatus.ANSWERED)
			{
				child.setActive(true);
			}
			else
			{
				child.setActive(false);
			}
			parent.addChild(child);
		}			
	}
	
	/**
	 * Add alternative-constraint relationships to the tree
	 * @param parent the parent node
	 * @param parentName the parent's name
	 * @param parentType the parent's type
	 */
	private void addAltConstRel(TreeParent parent, String parentName, RationaleElementType parentType)
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector reqList = d.getAltConstRels(parentName, RationaleElementType.ALTERNATIVE);
		Enumeration decs = reqList.elements();
		while (decs.hasMoreElements())
		{
			AltConstRel altc = new AltConstRel();
			TreeParent child = (TreeParent) decs.nextElement();
			altc.fromDatabase(child.getName());
			child.setActive(true);
			parent.addChild(child);
			//add sub-decisions....
			String childName = child.getName();
			addConstraints(child, childName, RationaleElementType.ALTCONSTREL);
		}			
	}
	
	/**
	 * Add an alt const relationship to the tree
	 * @param parent the parent node
	 * @param alconst the alt-const-relationship to add
	 * @return the tree node just added
	 */
	public TreeParent addAltConstRel(TreeParent parent, AltConstRel alconst)
	{
		TreeParent child;
		child = new TreeParent(alconst.getName(), RationaleElementType.ALTCONSTREL);
		parent.addChild(child);
		addNewElement(child, alconst.getConstr());
		
		return child;
	}
	
	/**
	 * Add a tradeoff
	 * @param parent the parent node
	 * @param trade the tradeoff
	 * @return the tree node just added
	 */
	public TreeParent addTradeoff(TreeParent parent, Tradeoff trade)
	{
		TreeParent child;
		if (trade.getTradeoff())
		{
			child = new TreeParent(trade.getName(), RationaleElementType.TRADEOFF);				
		}
		else
		{
			child = new TreeParent(trade.getName(), RationaleElementType.COOCCURRENCE);				
		}
		
		parent.addChild(child);
//		addOntEntry(child, trade.getOnt1());
//		addOntEntry(child, trade.getOnt2());
		addNewElement(child, trade.getOnt1());
		addNewElement(child, trade.getOnt2());
		return child;
	}
	
	/**
	 * Add an argument
	 * @param parent the parent node
	 * @param arg the argument
	 * @return the tree node just added
	 */
	public TreeParent addArgument(TreeParent parent, Argument arg)
	{
		TreeParent child = new TreeParent(arg.getName(), RationaleElementType.ARGUMENT);
		parent.addChild(child);
		if (arg.getClaim() != null)
		{
			Claim clm = arg.getClaim();
			addClaim(child, clm);
		}
		else if (arg.getRequirement() != null)
		{
			Requirement req = arg.getRequirement();
			addNewElement(child, req);
		}
		else if (arg.getAssumption() != null)
		{
			Assumption asm = arg.getAssumption();
			addNewElement(child, asm);
		}
		else if (arg.getAlternative() != null)
		{
			Alternative alt = arg.getAlternative();
			addNewElement(child, alt);
		}
		
		return child;
		
	}
	
	/**
	 * Add a claim to the tree
	 * @param parent the parent node
	 * @param clm the claim
	 * @return the new tree node just added
	 */
	public TreeParent addClaim(TreeParent parent, Claim clm)
	{
		TreeParent child = new TreeParent(clm.getName(), RationaleElementType.CLAIM);				
		parent.addChild(child);
//		addOntEntry(child, trade.getOnt1());
//		addOntEntry(child, trade.getOnt2());
		addNewElement(child, clm.getOntology());
		return child;
	}
	
	/**
	 * Add a claim to the tree
	 * @param parent the parent node
	 * @param clm the claim
	 * @return the new tree node just added
	 */
	public TreeParent addRequirement(TreeParent parent, Requirement req)
	{
		TreeParent child = new TreeParent(req.getName(), RationaleElementType.REQUIREMENT);				
		parent.addChild(child);
//		addOntEntry(child, trade.getOnt1());
//		addOntEntry(child, trade.getOnt2());
		if (req.getOntology() != null)
		{
			addNewElement(child, req.getOntology());
		}
		return child;
	}	
	/**
	 * Add a new element to the tree
	 * @param parent the parent node
	 * @param ele the element
	 * @return the new tree element
	 */
	public TreeParent addNewElement(TreeParent parent, RationaleElement ele)
	{
		TreeParent child = new TreeParent(ele.getName(), ele.getElementType());
		child.setActive(ele.getEnabled());
		parent.addChild(child);
		return child;
	}
	
	/**
	 * Remove an element from the tree and all its children
	 * @param child the element being removed
	 */
	public void removeElement(TreeParent child)
	{
		TreeParent parent = child.getParent();
		//first, remove all the children of the child
		child.removeChildren();
		//not removing in an iterator
		parent.removeChild(child, false);
	}
	
	/**
	 * Find the Rationale Element with a particular name
	 * @return our tree parent
	 */
	public TreeParent findRationaleElement(String name)
	{
		TreeParent ourParent;
		ourParent = findRationaleElement(invisibleRoot, name);
		return ourParent;
	}
	
	/**
	 * Find candidate (recursive version)
	 * 
	 */
	private TreeParent findRationaleElement(TreeParent node, String name)
	{
		TreeParent match = null;
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
				match = findRationaleElement((TreeParent) children[index], name);
				index++;
			}
		}
		return match;
	}
}