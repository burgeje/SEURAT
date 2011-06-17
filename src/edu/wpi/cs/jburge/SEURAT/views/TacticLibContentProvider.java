package edu.wpi.cs.jburge.SEURAT.views;

import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

public class TacticLibContentProvider implements IStructuredContentProvider, IDeltaListener,
ITreeContentProvider{

	protected TreeParent invisibleRoot;

	protected TreeViewer viewer;

	/**
	 * This represents the actual root for the tactic library.
	 */
	private TreeParent root;

	public TreeParent initialize(){
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		map.clearMap();

		root = new TreeParent("Tactic Library - " + RationaleDB.getDbName(), RationaleElementType.RATIONALE);

		invisibleRoot = new TreeParent("", RationaleElementType.RATIONALE);
		invisibleRoot.addChild(root);

		addTactics();

		return invisibleRoot;
	}

	/**
	 * Used during initialization of the tree map to load all tactics from database.
	 */
	private void addTactics(){
		RationaleDB db = RationaleDB.getHandle();
		Iterator<TreeParent> tacticNodes = db.getTactics(null).iterator();
		while (tacticNodes.hasNext()){
			TreeParent cur = tacticNodes.next();
			Tactic curTactic = new Tactic();
			curTactic.fromDatabase(cur.getName());
			addTactic(curTactic);
		}
	}

	/**
	 * Given parent and childName and type, create the child and att it to parent.
	 * Remember also to att parent to child.
	 * @param parent
	 * @param childName
	 */
	public void addElement(TreeParent parent, String childName, RationaleElementType type){
		TreeParent child = new TreeParent(childName, type);
		child.setParent(parent);
		parent.addChild(child);
	}

	/**
	 * First, remove all children of the child, then remove the element from the tree (from the parent of the child).
	 * @param child
	 */
	public void removeElement(TreeParent child){
		TreeParent parent = child.getParent();

		child.removeChildren();
		parent.removeChild(child, false);
	}

	/**
	 * Remove all descendants of the current node in reverse DFS order
	 * @param node Must be a valid tactic node!
	 */
	public void removeTactic(TreeParent node){
		TreeParent parent = node.getParent();

		if (node == null || node.getType() != RationaleElementType.TACTIC){
			System.err.println("Invalid target to remove in method removeTactic.");
		}
		removeTacticHelper(node);

		parent.removeChild(node, false);
		//If quality attribute is no longer needed as a category, remove it!
		if (!parent.hasChildren()) {
			root.removeChild(parent, false);
		}
	}
	
	/**
	 * Reverse DFS removal. Helper method.
	 * @param node
	 */
	private void removeTacticHelper(TreeParent node){
		TreeObject[] children = node.getChildren();
		if (children.length != 0){
			for (int i = 0; i < children.length; i++){
				TreeObject child = children[i];
				if (child instanceof TreeParent){
					removeTacticHelper((TreeParent) child);
				}
				else{
					node.removeChild(child, false);
				}
			}
		}
	}

	/**
	 * Given parent and tactic created, add a new tactic to the tree.
	 * @param tactic tactic to be added
	 * @return
	 */
	public TreeParent addTactic(Tactic tactic){
		TreeParent toAdd = new TreeParent(tactic.getName(), RationaleElementType.TACTIC);
		//User selected something else when adding a tactic.
		//First, see whether the tactic category is already displayed in the tree.
		TreeParent categoryNode = null;
		Iterator<TreeObject> categories = root.getIterator();
		while (categories.hasNext()){
			TreeParent cur = (TreeParent) categories.next();
			if (cur.getName().equals(tactic.getCategory().getName())){
				categoryNode = cur;
				break;
			}
		}

		if (categoryNode == null){
			//This means a new category of tactic has been added.
			categoryNode = new TreeParent(tactic.getCategory().getName(), RationaleElementType.TACTICCATEGORY);
			categoryNode.setParent(root);
			root.addChild(categoryNode);
		}

		categoryNode.addChild(toAdd);
		toAdd.setParent(categoryNode);

		//Add children of toAdd: Patterns RATIONALE element type, and Negative Quality Attribute RATIONALE element type to categorize them.
		TreeParent patternTop = new TreeParent("Associated Patterns", RationaleElementType.RATIONALE);
		toAdd.addChild(patternTop);
		patternTop.setParent(toAdd);
		TreeParent negTop = new TreeParent("Negative Quality Attribute", RationaleElementType.RATIONALE);
		toAdd.addChild(negTop);
		negTop.setParent(toAdd);
		//Add its tactic-patterns and negative quality attributes...
		for (int i = 0; i < tactic.getPatterns().size(); i++){
			addTacticPattern(toAdd, tactic.getPatterns().get(i));
		}
		for (int i = 0; i < tactic.getBadEffects().size(); i++){
			addNegOntology(toAdd, tactic.getBadEffects().get(i));
		}
		return toAdd;
	}

	/**
	 * Given a selected tactic and data of tactic-pattern, create the tactic-pattern as a child of tactic.
	 * @param parent Must be a tactic!
	 * @param tp the new tactic pattern data to be added
	 * @return the new object added to the tree
	 */
	public TreeParent addTacticPattern(TreeParent parent, TacticPattern tp){
		Tactic tactic = new Tactic();
		tactic.fromDatabase(parent.getName());
		if (tactic.getID() < 0){
			System.err.println("Error retreiving tactic ID!");
			return null;
		}

		TreeParent tpParent = null;
		TreeObject selectedChildren[] = parent.getChildren();
		for (int i = 0; i < selectedChildren.length; i++){
			if (selectedChildren[i].getName().equals("Associated Patterns")){
				tpParent = (TreeParent) selectedChildren[i];
				break;
			}
		}
		if (tpParent == null){
			System.err.println("Tactic-pattern's parent is null while adding it");
			return null;
		}
		//Get the name of the pattern associated with tactic-pattern.
		int patternID = tp.getPatternID();
		Pattern pattern = new Pattern();
		pattern.fromDatabase(patternID);

		//Fix the tree
		TreeParent toAdd = new TreeParent(pattern.getName(), RationaleElementType.TACTICPATTERN);
		toAdd.setParent(tpParent);
		tpParent.addChild(toAdd);
		return toAdd;
	}

	/**
	 * Given a selected tactic and a negative quality attribute, associate them into the tree.
	 * @param parent Must be a tactic!
	 * @param entry the new entry data to be added
	 * @return the new object added to the tree
	 */
	public TreeParent addNegOntology(TreeParent parent, OntEntry entry){
		Tactic tactic = new Tactic();
		tactic.fromDatabase(parent.getName());
		if (tactic.getID() < 0){
			System.err.println("Error retreiving tactic ID!");
			return null;
		}

		TreeParent entryParent = null;
		TreeObject selectedChildren[] = parent.getChildren();
		for (int i = 0; i < selectedChildren.length; i++){
			if (selectedChildren[i].getName().equals("Negative Quality Attribute")){
				entryParent = (TreeParent) selectedChildren[i];
				break;
			}
		}
		if (entryParent == null){
			System.err.println("Entry's parent is null while adding ontentry");
			return null;
		}

		TreeParent toAdd = new TreeParent(entry.getName(), RationaleElementType.ONTENTRY);
		toAdd.setParent(entryParent);
		entryParent.addChild(toAdd);
		return toAdd;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if (!(oldInput instanceof TreeParent))
		{
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
		for (Iterator<TreeObject> iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent aElement = (TreeParent) iterator.next();
			addListenerTo(aElement);
		}
	}

	protected void removeListenerFrom(TreeParent element) {
		element.removeListener(this);
		for (Iterator<TreeObject> iterator = element.getIterator(); iterator.hasNext();) {
			TreeParent nextEl = (TreeParent) iterator.next();
			removeListenerFrom(nextEl);
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeParent) {
			return ((TreeParent)parentElement).getChildren();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof TreeObject) {
			return ((TreeObject)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent)element).hasChildren();
		return false;
	}

	@Override
	public void add(DeltaEvent event) {
		Object ourObj = ((TreeParent)event.receiver()).getParent();
		viewer.refresh(ourObj, false);
	}

	@Override
	public void remove(DeltaEvent event) {
		add(event);

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement.equals(ResourcesPlugin.getWorkspace())) {
			if (invisibleRoot==null)
			{
				System.out.println("root is null?");
				initialize();
			} 
			return getChildren(invisibleRoot);
		}
		return getChildren(inputElement);
	}



}
