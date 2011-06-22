package edu.wpi.cs.jburge.SEURAT.views;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.jdt.core.*;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.actions.*;
import edu.wpi.cs.jburge.SEURAT.editors.SelectCandidate;
import edu.wpi.cs.jburge.SEURAT.editors.SelectItem;
import edu.wpi.cs.jburge.SEURAT.editors.SelectOntEntry;
import edu.wpi.cs.jburge.SEURAT.editors.SelectRationaleElement;
import edu.wpi.cs.jburge.SEURAT.editors.SelectRationaleElementForArgAndDec;
import edu.wpi.cs.jburge.SEURAT.editors.SelectRationaleElement_Treeview;
import edu.wpi.cs.jburge.SEURAT.editors.SelectType;
import edu.wpi.cs.jburge.SEURAT.inference.*;
//import edu.wpi.cs.jburge.SEURAT.queries.EditEntity;
import edu.wpi.cs.jburge.SEURAT.queries.FindImportanceOverrides;
import edu.wpi.cs.jburge.SEURAT.queries.FindStatusOverrides;
import edu.wpi.cs.jburge.SEURAT.queries.FindCommonArguments;
import edu.wpi.cs.jburge.SEURAT.queries.FindEntity;
import edu.wpi.cs.jburge.SEURAT.queries.FindRequirements;
import edu.wpi.cs.jburge.SEURAT.queries.HistoryDisplay;
import edu.wpi.cs.jburge.SEURAT.queries.RequirementRelationshipDisplay;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.reports.*;
import edu.wpi.cs.jburge.SEURAT.tasks.RationaleTaskList;

import org.eclipse.core.resources.IFile;
import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import SEURAT.editors.*;
import SEURAT.preferences.PreferenceConstants;
import SEURAT.xmlIO.RationaleEntry;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.decorators.*;

public class TacticLibrary extends ViewPart implements ISelectionListener, IRationaleUpdateEventListener,
IPropertyChangeListener{

	/**
	 * This is the handle for tactic library
	 */
	private static TacticLibrary handle;

	/**
	 * The view into the tree of rationale
	 */
	private TreeViewer viewer;
	/**
	 * Provides the icons that make up our tree
	 */
	protected RationaleLabelProvider labelProvider;
	/**
	 * ?
	 */
	private DrillDownAdapter drillDownAdapter;

	/**
	 * the associate action is used to associate code with rationale
	 */
	private AssociateArtifactAction associate;

	//Actions available in this library goes here...
	private Action addTactic;
	private Action editTactic;
	private Action deleteTactic;
	private Action associateNegOntology;
	private Action deleteNegOntology;
	private Action addTacticPattern;
	private Action editTacticPattern;
	private Action deleteTacticPattern;

	//Double clicking allows editing the element
	private Action editElement;


	/**
	 * Points to our display
	 */
	private Display ourDisplay;

	/**
	 * The Java Element selected
	 */
	private IJavaElement navigatorSelection;

	/**
	 * The rationale object selected in the tree view (used in associations)
	 */
	private Object obj;

	/**
	 * The index for the first character of the artifact we're looking for.  This determines
	 * where we will place the marker once we find it.
	 */
	private int cstart;
	/**
	 * The Java resource
	 */
	private IResource ourRes;

	/**
	 * Constructor
	 */
	public TacticLibrary(){

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void associateAlternative(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openDatabase(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRationaleStatus(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNewElement(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets up your selection when a different Java element is chosen
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) 
		{
			if (((IStructuredSelection) selection).getFirstElement() instanceof IJavaElement) 
			{
				//				System.out.println("found Java Element");
				navigatorSelection = (IJavaElement) ((IStructuredSelection) selection).getFirstElement();
				associate.setSelection(navigatorSelection);
			}
			else
			{
				//				System.out.println("not a java item");
			}
		}

	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new TacticLibContentProvider());
		labelProvider = new RationaleLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setSorter(null);
		viewer.setInput(ResourcesPlugin.getWorkspace());

		//get our display information so we can use it later
		ourDisplay = viewer.getControl().getDisplay();

		//initialize our update manager
		UpdateManager mgr = UpdateManager.getHandle();
		mgr.setTree(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		viewer.setInput(((TacticLibContentProvider) viewer.getContentProvider()).initialize());
		viewer.expandToLevel(2);

		//add an action listener for the workbench window
		getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

		//add an action listener for this so we can get events
		SEURATPlugin plugin = SEURATPlugin.getDefault();
		plugin.addUpdateListener(this);

		// add this as a property change listener so we can be notified of preference changes
		plugin.getPreferenceStore().addPropertyChangeListener(this);

		//get the initial selected value
		selectionChanged(null, getViewSite().getWorkbenchWindow().getSelectionService().getSelection());

		handle = this;

	}

	public void rebuildTree()
	{
		RationaleDB.resetConnection();
		viewer.getTree().removeAll();
		viewer.getContentProvider().dispose();
		viewer.setContentProvider(new TacticLibContentProvider());
		//this should re-fresh from the new database
		viewer.setInput(((TacticLibContentProvider) viewer.getContentProvider()).initialize());
	}

	/**
	 * Set up our context menu
	 *
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Set up our action bars - toolbar, pull down
	 *
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This method is used to populate the pulldown menu on this view.
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {

	}

	/**
	 * This method is used to populate the context menus for each of the different
	 * types of rationale elements.
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();

		if (obj instanceof TreeParent)
		{
			TreeParent ourElement = (TreeParent) obj;

			if (ourElement.getType() == RationaleElementType.TACTIC){
				manager.add(editTactic);
				manager.add(deleteTactic);
				manager.add(addTacticPattern);
				manager.add(associateNegOntology);
			}
			else if (ourElement.getType() == RationaleElementType.TACTICPATTERN){
				manager.add(editTacticPattern);
				manager.add(deleteTacticPattern);
			}
			else if (ourElement.getType() == RationaleElementType.ONTENTRY){
				if (!ourElement.hasChildren()){
					//This is a negative ontentry.
					manager.add(deleteNegOntology);
				}
			}
			else if (ourElement.getType() == RationaleElementType.TACTICCATEGORY){
				manager.add(addTactic);
			}
			else if (ourElement.getType() == RationaleElementType.RATIONALE){
				if (ourElement.getName().contains("Tactic Library"))
					manager.add(addTactic);
			}
		}
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		drillDownAdapter.addNavigationActions(manager);
	}

	/**
	 * This method sets up the actions invoked by chosing menu items
	 *
	 */
	private void makeActions() {		
		/* SEURAT ECLIPSE-STYLE EDITORS ACTION DEFINITIONS */

		// Currently unused editors

		// Assumption editor removed for consistency reasons (pop-up boxes to select assumption, then
		// a tab to edit it? would be confusing to users
		/*addAssumptionEditor = new OpenRationaleEditorAction(AssumptionEditor.class, this, true);
		addAssumptionEditor.setText("Create New Assumption (With Editor)");
		addAssumptionEditor.setToolTipText("Create A New Assumption Using The Assumption Editor Window");

		showAssumptionEditor = new OpenRationaleEditorAction(AssumptionEditor.class, this, false);
		showAssumptionEditor.setText("Assumption Editor Page");
		showAssumptionEditor.setToolTipText("Edit An Assumption In An Editor Window");*/

		// Editors that are used

		addTactic = new OpenRationaleEditorAction(TacticEditor.class, this, true);
		addTactic.setText("New Tactic");
		addTactic.setToolTipText("Add a new tactic to the library.");
		editTactic = new OpenRationaleEditorAction(TacticEditor.class, this, false);
		editTactic.setText("Edit Tactic");
		editTactic.setToolTipText("Edit the selected tactic.");
		deleteTactic = new Action(){
			public void run(){
				RationaleDB db = RationaleDB.getHandle();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent){
					TreeParent ourElement = (TreeParent) obj;
					if (ourElement.getType() == RationaleElementType.TACTIC){
						db.removeTactic(ourElement.getName());
						TreeParent parent = ourElement.getParent();
						TreeParent grandParent = parent.getParent();
						TacticLibContentProvider provider = (TacticLibContentProvider) viewer.getContentProvider();
						provider.removeTactic(ourElement);
						refreshBranch(grandParent);
					}
				}
			}
		};
		deleteTactic.setText("Delete Tactic and ALL associations in the library");
		deleteTactic.setToolTipText("Iteratively delete all assocations of the tactic in the library, then delete the tactic. " +
		"The original data of patterns and quality attributes in argument ontology remain unchanged.");

		associateNegOntology = new Action(){
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent){
					TreeParent ourElement = (TreeParent) obj;
					if (ourElement.getType() == RationaleElementType.TACTIC){
						OntEntry newOnt = null;
						SelectOntEntry ar = new SelectOntEntry(viewer.getControl().getDisplay(), true);
						newOnt = ar.getSelOntEntry();
						if (newOnt != null){
							//Get the tactic first loaded first...
							Tactic tactic = new Tactic();
							tactic.fromDatabase(ourElement.getName());
							if (newOnt.getName().equals(TacticPattern.CHANGEONTNAME)){
								showInformation("Selected ontology is not valid because it is a tactic impact attribute");
								return;
							}
							if (tactic.getID() >= 0){
								tactic.addBadEffect(newOnt);
								tactic.toDatabase();
								//Add to the tree...
								TacticLibContentProvider provider = (TacticLibContentProvider) viewer.getContentProvider();
								provider.addNegOntology(ourElement, newOnt);
								refreshBranch(ourElement);
							}
							else {
								System.err.println("Cannot retrieve tactic info while associating with negative ontology");
								return;
							}
						}
					}
				}
			}
		};
		associateNegOntology.setText("Add Negative Quality");
		associateNegOntology.setToolTipText("Add another negative quality to the selected tactic.");

		deleteNegOntology = new Action(){
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent){
					TreeParent ourElement = (TreeParent) obj;
					if (ourElement.getType() != RationaleElementType.ONTENTRY){
						System.err.println("Selected element is not an ontology. Abort deletion");
						return;
					}

					TreeParent ourTactic = ourElement.getParent().getParent();
					if (ourTactic instanceof TreeParent && ourTactic.getType() == RationaleElementType.TACTIC){
						Tactic tactic = new Tactic();
						tactic.fromDatabase(ourTactic.getName());
						boolean success = tactic.deleteBadEffect(ourElement.getName());
						if (!success){
							System.err.println("Cannot find/delete a negative quality attribute for tactic library!");
						}
						else{
							tactic.toDatabase();
							//Remove from the tree...
							TacticLibContentProvider provider = (TacticLibContentProvider) viewer.getContentProvider();
							provider.removeElement(ourElement);
							refreshBranch(ourTactic);
						}
					}
				}
			}
		};
		deleteNegOntology.setText("Delete QA");
		deleteNegOntology.setText("Deletes this quality attribute from the associated tactic.");

		addTacticPattern = new OpenRationaleEditorAction(TacticPatternEditor.class, this, true, RationaleElementType.TACTICPATTERN);
		addTacticPattern.setText("Associate Pattern");
		addTacticPattern.setToolTipText("Associate a pattern to the selected tactic.");
		editTacticPattern = new OpenRationaleEditorAction(TacticPatternEditor.class, this, false, RationaleElementType.TACTICPATTERN);
		editTacticPattern.setText("Edit Tactic-Pattern");
		editTacticPattern.setToolTipText("Edits the selected tactic-pattern association");
		deleteTacticPattern = new Action(){
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent){
					TreeParent ourElement = (TreeParent) obj;
					TreeParent ourParent = ourElement.getParent();
					if (ourElement.getType() == RationaleElementType.TACTICPATTERN){
						TacticPattern tp = new TacticPattern();
						tp.fromDatabase(TacticPattern.combineNames(ourElement.getName(), ourElement.getParent().getParent().getName()));
						boolean success = tp.deleteFromDB();
						if (!success){
							System.err.println("Cannot delete the tactic-pattern from DB!");
							return;
						}
						TacticLibContentProvider provider = (TacticLibContentProvider) viewer.getContentProvider();
						provider.removeElement(ourElement);
						refreshBranch(ourParent);
					}
					else{
						System.err.println("The selected element to delete is not tactic-pattern!");
					}
				}
			}
		};
		deleteTacticPattern.setText("Delete Association");
		deleteTacticPattern.setToolTipText("Delete the selected pattern-tactic association.");

		//
		//edit rationale element Action
		//For the new editors, will get the rationale type and run the appropriate editor.
		//
		editElement = new Action() {
			public void run() {

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof TreeParent)
				{
					TreeParent ourElement = (TreeParent) obj;

					if (ourElement.getType() == RationaleElementType.TACTIC){
						editTactic.run();
					}
					else if (ourElement.getType() == RationaleElementType.TACTICPATTERN){
						editTacticPattern.run();
					}
				}
			}
		};
	}

	/**
	 * hookDoubleClickAction is used to edit the rationale elements when you 
	 * double-click them
	 *
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				//				doubleClickAction.run();

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent)
				{
					try {
						editElement.run();
					} catch (NullPointerException npe) {
						/* This means they clicked on an invalid node.
						 * Don't display an error, just leave it with no effect */
					}
				}
			}
		});
	}


	@Override
	public void setFocus() {
		viewer.getControl().setFocus();

	}

	public void dispose() {
		getViewSite().getWorkbenchWindow().getSelectionService().
		removeSelectionListener(this);
		super.dispose();
	}

	/**
	 * Updates the tree branch (to display new children, etc.)
	 * @param parent - the top of the branch to refresh
	 */
	public void refreshBranch(TreeParent parent)
	{
		viewer.refresh(parent);
		Iterator<TreeObject> childrenI = parent.getIterator();
		while (childrenI.hasNext())
		{
			refreshBranch((TreeParent) childrenI.next());
		}
	}

	/**
	 * This method serves as a factory method that either creates a new element of the
	 * type specified by the treeElement passed in to it or that uses the name of the treeElement
	 * to get the corresponding RationaleElement from the database
	 * @param treeElement - either the parent of the new element or the element itself
	 * @param newElement - a flag indicating if we are creating a new RationaleElement
	 * @return the RationaleElement created or read from the database
	 */
	public RationaleElement getElement(TreeObject treeElement, boolean newElement)
	{
		RationaleElement ourElement = null;
		RationaleElementType type = treeElement.getType();
		String name = treeElement.getName();
		if (type == RationaleElementType.TACTIC)
		{
			ourElement = new Tactic();
		}
		else if (type == RationaleElementType.TACTICPATTERN){
			ourElement = new TacticPattern();
		}
		else if (type == RationaleElementType.ONTENTRY){
			ourElement = new OntEntry();
		}
		else if (type == RationaleElementType.RATIONALE){
			if (name.indexOf("Tactic Library") == 0){
				ourElement = new Tactic();
			}
		}
		else if (type == RationaleElementType.TACTICCATEGORY){
			ourElement = new Tactic();
		}
		if (!newElement && !name.equals("imaginary root"))
		{
			if (type == RationaleElementType.TACTICPATTERN){
				TreeParent ourSelection = (TreeParent) treeElement;
				ourElement.fromDatabase(TacticPattern.combineNames(ourSelection.getName(),
						ourSelection.getParent().getParent().getName()));
			}
			else {
				ourElement.fromDatabase(treeElement.getName());
			}
		}
		return ourElement;
	}

	/**
	 * this creates a new element in the rationale tree
	 * @param parentElement - this is the rationaleElement parent
	 * @param rElement - this is our new (child) element
	 * @param obj - this is the parent tree element
	 */
	void createNewElement(RationaleElement parentElement, RationaleElement rElement, TreeParent obj)
	{
		RationaleDB db = RationaleDB.getHandle();
		boolean canceled = rElement.create(viewer.getControl().getShell().getDisplay(), parentElement);
		if (!canceled)
		{
			//need to add the new element to the tree...
			//			TreeParent newChild = new TreeParent(rElement.getName(), rElement.getElementType());				
			//			((TreeParent) obj).addChild(newChild);
			System.out.println("name in createNewElement = " + rElement.getName());
			addElement((TreeParent) obj, rElement);
			//we update the parent status, not the new element status
			//this might not be correct...
			Vector<RationaleStatus> newStat = null;
			/*			if (parentElement.getID() > 0)
			 {
			 newStat = parentElement.updateStatus();
			 }
			 */
			//try updating our own status - shouldn't that automatically get our parent?
			Vector<RationaleStatus> curStatus = null; 
			UpdateManager manager = UpdateManager.getHandle();
			curStatus = manager.getInitialStatus();										
			RationaleTaskList tlist = RationaleTaskList.getHandle();
			if (curStatus.size() > 0)
			{
				db.removeStatus(curStatus);
				//update our task list as well
				tlist.removeTasks(curStatus);
			}

			//add our new element
			refreshBranch((TreeParent) obj);

			Vector<TreeObject> treeUpdates = manager.makeUpdates();
			//update what is displayed in the tree
			//how do I update if name changes?
			//			System.out.println(rElement.getName() + ": enabled = " + new Boolean(rElement.getEnabled()).toString());
			//need to iterate through all the items
			Iterator<TreeObject> treeI = treeUpdates.iterator();
			while (treeI.hasNext())
			{
				viewer.update((TreeParent) treeI.next(), null);
			}
		}
	}

	/**
	 * Update method for creating a new element using the new editors.  The function
	 * is similar to the createNewElement method except that this is called from the
	 * rationale editor itself, creating some minor differences.  This method
	 * and the editUpdate method, and their "old editor" counterparts, should
	 * probably be refactored.
	 * 
	 * @param p - the parent tree element
	 * @param e - the new (child) rationale element
	 * @return the TreeParent object representing the new element - This is done so that
	 * the rationale editor class that calls this method can get the correct reference to
	 * the new element and update itself accordingly- otherwise it will be editing the parent!
	 */
	public TreeParent createUpdate(TreeParent p, RationaleElement e) {	

		// Add The Element TO The Tree
		TreeParent newEle = addElement(p, e);


		// Update Rationale Task List And Database With New Status

		UpdateManager manager = UpdateManager.getHandle();

		// Refresh Affected Branch Of Tree
		refreshBranch(p);

		// Update Anything In Tree Affected By Insertion
		Vector<TreeObject> treeUpdates = manager.makeUpdates();
		Iterator<TreeObject> treeIterator = treeUpdates.iterator();
		while( treeIterator.hasNext() )
		{
			getViewer().update((TreeParent)treeIterator.next(), null);
		}

		Vector<TreeObject> treeObjs;
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		if (e.getElementType() == RationaleElementType.TACTICPATTERN){
			treeObjs = map.getKeys(map.makeKey(TacticPattern.sepNames(e.getName())[0], e.getElementType()));
		}
		else {
			treeObjs = map.getKeys(map.makeKey(e.getName(), e.getElementType()));
		}
		//if there's more than one we don't care, just get the first
		viewer.reveal(treeObjs.elementAt(0));
		viewer.expandToLevel(treeObjs.elementAt(0), 4);

		return newEle;
	}

	public TreeViewer getViewer() { return viewer; }

	/**
	 * This is used to assocate different types of rationale elements
	 * with each other. For example, a constraint has an ontology element associated. A 
	 * constraint can be associated with a decision or alternative but the alternative 
	 * association is handled differently (see action AltConstRel).
	 * @param parentElement the rationale element (Decision or Constraint)
	 * @param obj the tree element corresponding to the parent
	 */
	void associateElement(RationaleElement parentElement, TreeParent obj)
	{
		RationaleDB db = RationaleDB.getHandle();
		RationaleElement newChild = parentElement.associateElement(viewer.getControl().getShell().getDisplay());
		if (newChild != null)
		{
			addElement((TreeParent) obj, newChild);
			//we update the parent status, not the new element status
			//this might not be correct...
			Vector<RationaleStatus> newStat = null;
			/*			if (parentElement.getID() > 0)
			 {
			 newStat = parentElement.updateStatus();
			 }
			 */
			//try updating our own status - shouldn't that automatically get our parent?
			newStat = newChild.updateStatus();
			Vector<RationaleStatus> curStatus = null; 
			UpdateManager manager = UpdateManager.getHandle();
			curStatus = manager.getInitialStatus();										
			RationaleTaskList tlist = RationaleTaskList.getHandle();
			if (curStatus.size() > 0)
			{
				db.removeStatus(curStatus);
				//update our task list as well
				tlist.removeTasks(curStatus);
			}

			//add our new element
			refreshBranch((TreeParent) obj);

			Vector treeUpdates = manager.makeUpdates();
			//update what is displayed in the tree
			//how do I update if name changes?
			//			System.out.println(rElement.getName() + ": enabled = " + new Boolean(rElement.getEnabled()).toString());
			//need to iterate through all the items
			Iterator treeI = treeUpdates.iterator();
			while (treeI.hasNext())
			{
				viewer.update((TreeParent) treeI.next(), null);
			}
		}
	}

	/**
	 * Adding a new element to our rationale tree
	 * @param parent - the parent element in the tree
	 * @param element - the rationale element being added
	 * @return the new treeParent that corresponds to the rationaleElement
	 */
	public TreeParent addElement(TreeParent parent, RationaleElement element)
	{
		if (element instanceof Tactic){
			return ( (TacticLibContentProvider) viewer.getContentProvider()).addTactic((Tactic) element);
		}
		else if (element instanceof TacticPattern){
			return ( (TacticLibContentProvider) viewer.getContentProvider()).addTacticPattern(parent, (TacticPattern) element);
		}
		else if (element instanceof OntEntry){
			return ( (TacticLibContentProvider) viewer.getContentProvider()).addNegOntology(parent, (OntEntry) element);
		}
		return null;
	}

	/**
	 * Removes an element from the tree. This is done when something is deleted but
	 * also done if the underlying tree structure is changed (the old is removed, and a new
	 * one is generated)
	 * @param parent the element being removed
	 * @return the parent of the element that was just removed
	 */
	public TreeParent removeElement (TreeParent parent)
	{
		TreeParent grandParent = parent.getParent();
		//shouldn't we remove the item from the map too?
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		String key = map.makeKey(parent.getName(), parent.getType());
		map.removeItem(key, parent);
		//remove the old element from the tree
		( (TacticLibContentProvider) viewer.getContentProvider()).removeElement(parent);
		//re-draw this branch of the tree
		refreshBranch(grandParent);		
		return grandParent;

	}

	/**
	 * Update method for editing an existing element.  This has the same function as the
	 * editElement method, but is designed to be called by the new editors instead (there is
	 * no display passed from the new editors).
	 * 
	 * @param p - the selected tree element being edited
	 * @param e - the rationale element being edited
	 */
	public TreeParent editUpdate(TreeParent p, RationaleElement e) {
		return updateTreeElement(p, e);
	}

	/**
	 * updateTreeElement - updates our tree element after it has been edited. 
	 * This includes any name changes, any status changes, and adding any new rationale
	 * tasks to the task list. Yes, this method probably does too much... The database is updated
	 * with the status changes as well.
	 * @param obj - the tree element that was just edited
	 * @param rElement - the corresponding rationale element
	 */
	private TreeParent updateTreeElement (TreeParent obj, RationaleElement rElement)
	{
		RationaleDB db = RationaleDB.getHandle();
		//need to check to see if the name has changed
		if (rElement.getElementType() != RationaleElementType.TACTICPATTERN){
			if (obj.getName().compareTo(rElement.getName()) != 0)
			{
				//			System.out.println("name has changed");
				//need to save the old and new names and make the changes
				updateName(obj.getName(), rElement.getName(), rElement.getElementType());
			}
		}
		else {
			String patternName = TacticPattern.sepNames(rElement.getName())[0]; //New name
			if (obj.getName().compareTo(patternName) != 0){
				updateName(obj.getName(), patternName, rElement.getElementType());
			}
		}
		Vector<RationaleStatus> newStat = rElement.updateStatus();
		//		System.out.println("new stat ln (editor) = " + newStat.size());
		Vector<RationaleStatus> curStatus = null; 
		UpdateManager manager = UpdateManager.getHandle();
		curStatus = manager.getInitialStatus();										
		RationaleTaskList tlist = RationaleTaskList.getHandle();
		if (curStatus.size() > 0)
		{
			db.removeStatus(curStatus);
			//update our task list as well
			//			System.out.println("removing a task");
			tlist.removeTasks(curStatus);
		}
		//moved to after to see if this helps (???)
		if (newStat != null)
		{
			//update the database
			db.addStatus(newStat);
			//update tasks too
			tlist.addTasks(newStat);
		}

		//now make our updates
		Vector treeUpdates = manager.makeUpdates();
		//update what is displayed in the tree
		//how do I update if name changes?
		//		System.out.println(rElement.getName() + ": enabled = " + new Boolean(rElement.getEnabled()).toString());
		if (rElement.getElementType() != RationaleElementType.TACTICPATTERN){
			obj.update(rElement.getName(), rElement.getEnabled());
		}
		else{
			String patternName = TacticPattern.sepNames(rElement.getName())[0]; //New name
			obj.update(patternName, rElement.getEnabled());
		}
		//need to iterate through all the items
		Iterator treeI = treeUpdates.iterator();
		if (!treeI.hasNext())
		{
			//refresh ourself - needed for root elements with no status
			viewer.refresh((TreeParent) obj);
		}
		while (treeI.hasNext())
		{

			//			viewer.update((TreeParent) treeI.next(), null);
			viewer.refresh((TreeParent) treeI.next());
		}
		
		if (rElement.getElementType() == RationaleElementType.TACTIC && rElement instanceof Tactic){
			Tactic t = (Tactic) rElement;
			if (!t.getCategory().getName().equals(obj.getParent().getName())){
				rebuildTree();
				viewer.reveal(obj.getParent());
				viewer.reveal(obj);
				viewer.expandToLevel(2);
			}
		}

		return obj;
	}

	/**
	 * this updates the RationaleTreeMap that uses the name of the rationale elements
	 * to find all its occurrences in the tree
	 * @param oldName - the previous name
	 * @param newName - the new name
	 * @param type - the rationale element type
	 */
	public void updateName(String oldName, String newName, RationaleElementType type)
	{
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		String oldkey = map.makeKey(oldName, type);
		String newkey = map.makeKey(newName, type);
		Vector treeElements = map.getKeys(oldkey);
		//now, iterate through our tree objects and set their name
		Enumeration updateT = treeElements.elements();
		//	System.out.println("number of tree elements = " + new Integer(treeElements.size()).toString());
		while (updateT.hasMoreElements())
		{
			TreeObject leaf = (TreeObject) updateT.nextElement();
			leaf.setName(newName);
			map.removeItem(oldkey, leaf);
			map.addItem(newkey, leaf);
		}

	}

	//we are now going to assume that the editing is done by the time
	//we receive the event
	/**
	 * When editing is done from a query or some other method that does not
	 * have the treeParent for the edited item, the GUI where the edits were invoked from
	 * will fire a RationaleUpdateEvent so that we will know that the tree may have
	 * changed. That is handled here.
	 * @param e - the rationale update event received. It will contain the element itself.
	 */
	public void updateRationaleTree(RationaleUpdateEvent e)
	{
		RationaleElement ele = e.getRationaleElement();
		//get the tree parent corresponding to the element...
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		Vector treeObjs = map.getKeys(map.makeKey(ele.getName(), ele.getElementType()));
		//if there's more than one we don't care, just get the first
		TreeParent ourObj = (TreeParent) treeObjs.elementAt(0); 
		updateTreeElement(ourObj, ele);

	}

	/**
	 * This method takes the tree and expands a node when requested by the user. The
	 * request comes in with a RationaleUpdateEvent
	 * @param e - the rationale update event
	 */
	public void showRationaleNode(RationaleUpdateEvent e)
	{
		RationaleElement ele = e.getRationaleElement();
		//get the tree parent corresponding to the element...
		RationaleTreeMap map = RationaleTreeMap.getHandle();
		Vector<TreeObject> treeObjs = map.getKeys(map.makeKey(ele.getName(), ele.getElementType()));
		/* We used to just look at the top element
		viewer.reveal(treeObjs.elementAt(0));
		viewer.expandToLevel(treeObjs.elementAt(0), 4); */
		//expand *all* the occurences!
		Iterator<TreeObject> treeIterator = treeObjs.iterator();
		while( treeIterator.hasNext() )
		{
			TreeObject treeEle = (TreeObject) treeIterator.next();
			viewer.reveal(treeEle);
			viewer.expandToLevel(treeEle, 4);
		}
	}

	/**
	 * This method is used to pop-up a message to inform the user that an action has been completed.
	 * It is used when saving the argument ontology but could easily be re-used elsewhere.
	 * @param message
	 * @return return value from the dialog
	 */
	private void showInformation(String message) {
		MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"TacticExplorer",
				message);
	}
	
	public static TacticLibrary getHandle(){
		return handle;
	}


}
