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

package edu.wpi.cs.jburge.SEURAT.views;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import SEURAT.editors.*;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.actions.AssociateUMLAction;
import edu.wpi.cs.jburge.SEURAT.editors.SelectCandidatePatterns;
import edu.wpi.cs.jburge.SEURAT.editors.SelectOntEntry;
import edu.wpi.cs.jburge.SEURAT.inference.UpdateManager;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AltConstRel;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AreaExp;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Assumption;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Claim;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Constraint;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Contingency;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.DesignProductEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Designer;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternDecision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternParticipant;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Question;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Tradeoff;
import edu.wpi.cs.jburge.SEURAT.tasks.RationaleTaskList;

public class PatternLibrary extends ViewPart implements ISelectionListener, IRationaleUpdateEventListener,
IPropertyChangeListener {

	private TreeViewer viewer;

	private DrillDownAdapter drillDownAdapter;

	private Display ourDisplay;

	private static PatternLibrary pl;

	private Action editElement;
	private Action deleteElement;
	private Action addPosiOntEntry;
	private Action addNegaOntEntry;
	private Action addPatternEditor;
	private Action editPatternParticipants;
	private Action exportPatternParticipants;
	private Action exportExistingUML;
	private Action showPatternEditor;
	private Action addPatternDecision;
	private Action addCandidatePattern;
	private Action showPatternDecisionEditor;
	private Action deletePattern;
	private Action importXML;
	private Action exportXML;

	protected RationaleLabelProvider labelProvider;

	// create the viewer and initialize it
	public void createPartControl(Composite parent) {

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new PatternLibContentProvider());
		viewer.setLabelProvider(new RationaleLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(ResourcesPlugin.getWorkspace());
		//get our display information so we can use it later
		ourDisplay = viewer.getControl().getDisplay();

		//		//initialize our update manager
		UpdateManager mgr = UpdateManager.getHandle();
		mgr.setTree(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		PatternLibContentProvider pattern = new PatternLibContentProvider();
		viewer.setInput(pattern.initialize());		

		//add an action listener for the workbench window
		getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

		//add an action listener for this so we can get events
		SEURATPlugin plugin = SEURATPlugin.getDefault();
		plugin.addUpdateListener(this);

		// add this as a property change listener so we can be notified of preference changes
		plugin.getPreferenceStore().addPropertyChangeListener(this);

		//get the initial selected value
		selectionChanged(null, getViewSite().getWorkbenchWindow().getSelectionService().getSelection());

		pl = this;

	}

	public void rebuildTree()
	{
		RationaleDB.resetConnection();
		viewer.getTree().removeAll();
		viewer.getContentProvider().dispose();
		viewer.setContentProvider(new PatternLibContentProvider());
		//this should re-fresh from the new database
		viewer.setInput(((PatternLibContentProvider) viewer.getContentProvider()).initialize());
	}

	public void setFocus() {
		viewer.getControl().setFocus();

	}

	public static PatternLibrary getHandle() {
		if (pl == null)
		{
			pl = new PatternLibrary();
		}
		return pl;
	}

	public TreeViewer getViewer() { return viewer; }

	public RationaleElement getElement(TreeObject treeElement, boolean newElement)
	{
		RationaleElement ourElement = null;
		RationaleElementType type = treeElement.getType();
		String name = treeElement.getName();
		if (type == RationaleElementType.ASSUMPTION)
		{
			ourElement = new Assumption();
		}
		else if (type == RationaleElementType.ARGUMENT)
		{
			ourElement = new Argument();
		}
		else if (type == RationaleElementType.DECISION)
		{
			ourElement = new Decision();
		}
		else if (type == RationaleElementType.ALTERNATIVE)
		{
			ourElement = new Alternative();
		}
		else if (type == RationaleElementType.ONTENTRY)
		{
			ourElement = new OntEntry();
		}
		else if (type == RationaleElementType.PATTERN)
		{
			ourElement = new Pattern();
		}
		else if (type == RationaleElementType.PATTERNDECISION)
		{
			ourElement = new PatternDecision();
		}
		else if (type == RationaleElementType.DESIGNPRODUCTENTRY)
		{
			ourElement = new DesignProductEntry();
		}
		else if (type == RationaleElementType.CONSTRAINT)
		{
			ourElement = new Constraint();
		}
		else if (type == RationaleElementType.CONTINGENCY)
		{
			ourElement = new Contingency();
		}
		else if (type == RationaleElementType.DESIGNER)
		{
			ourElement = new Designer();
		}
		else if (type == RationaleElementType.REQUIREMENT)
		{
			ourElement = new Requirement();
		}
		else if (type == RationaleElementType.CLAIM)
		{
			ourElement = new Claim();
		}
		else if (type == RationaleElementType.EXPERTISE)
		{
			ourElement = new AreaExp();
		}
		else if (type == RationaleElementType.ALTCONSTREL)
		{
			ourElement = new AltConstRel();
		}
		else if (type == RationaleElementType.QUESTION)
		{
			ourElement = new Question();
		}
		else if (type == RationaleElementType.TRADEOFF)
		{
			ourElement = new Tradeoff(true);
		}
		else if (type == RationaleElementType.COOCCURRENCE)
		{
			ourElement = new Tradeoff(false);
		}
		else if (type == RationaleElementType.RATIONALE)
		{
			if (name.compareTo("Tradeoffs") == 0)
			{
				//				System.out.println("found our tradeoff");
				ourElement = new Tradeoff(true);
			}
			else if (name.compareTo("Co-occurrences") == 0)
			{
				ourElement = new Tradeoff(false);
			}
			else if (name.compareTo("Requirements") == 0)
			{
				ourElement = new Requirement();
			}
			else if (name.compareTo("Decisions") == 0)
			{
				ourElement = new Decision();
			}
			else if (name.compareTo("Design-Contingencies") == 0)
			{
				ourElement = new Contingency();
			}
			else if (name.compareTo("Designer-Profiles") == 0)
			{
				ourElement = new Designer();
			}
		}
		if (!newElement && !name.equals("imaginary root"))
		{
			ourElement.fromDatabase(treeElement.getName());
		}
		return ourElement;
	}

	//set up context menu
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PatternLibrary.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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



	// actions invoked by chosing menu items
	private void makeActions() {

		showPatternEditor = new OpenRationaleEditorAction(PatternEditor.class, this, false);
		// pattern editor
		editElement = new Action() {
			public void run() {

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent)
				{
					TreeParent ourElement = (TreeParent) obj;
					if (ourElement.getType() == RationaleElementType.PATTERN) {

						Pattern patternSelected = new Pattern();
						patternSelected.fromDatabase(((TreeObject)obj).getName());
						showPatternEditor.run();
						//boolean canceled = patternSelected.display(ourDisplay);						
					}else if(ourElement.getType() == RationaleElementType.PATTERNDECISION){
						showPatternDecisionEditor.run();
					}else {
						try {
							RationaleElement rElement = getElement((TreeParent) obj, false);
							editElement((TreeParent) obj, rElement, ourDisplay);
						} catch (NullPointerException npe) {
							/* NPE is thrown when we double-click on an invalid element,
							 such as the root of the tree.  In this case we don't want
							 to do anything, no error pop-up, just leave it with no effect.
							 Could change it to expand the tree if possible. */
						}
					}
				}else {
					try {
						RationaleElement rElement = getElement((TreeParent) obj, false);
						editElement((TreeParent) obj, rElement, ourDisplay);
					} catch (NullPointerException npe) {
						/* NPE is thrown when we double-click on an invalid element,
						 such as the root of the tree.  In this case we don't want
						 to do anything, no error pop-up, just leave it with no effect.
						 Could change it to expand the tree if possible. */
					}
				}
				//showPatternEditor.run();

			}			
		}; //end of the edit element action definition
		editElement.setText("Edit");
		editElement.setToolTipText("Edit Pattern");

		addPatternEditor = new OpenRationaleEditorAction(PatternEditor.class, this, true, RationaleElementType.PATTERN);
		addPatternEditor.setText("Add pattern");
		addPatternEditor.setToolTipText("Add a new pattern");
		
		editPatternParticipants = new OpenRationaleEditorAction(PatternParticipantEditor.class, this, false, RationaleElementType.PATTERN);
		editPatternParticipants.setText("Edit Pattern Participants");
		editPatternParticipants.setToolTipText("Edit participants of the selected pattern");

		//delete element in P.L.
		deleteElement = new Action() {
			public void run() {
				RationaleDB db = RationaleDB.getHandle();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent)
				{
					TreeParent ourElement = (TreeParent) obj;
					if (ourElement.getType() == RationaleElementType.PATTERN) {
						if(ourElement.getParent().getType() == RationaleElementType.PATTERNDECISION){
							db.removeCandidatePattern(ourElement.getName(), ourElement.getParent().getName());
						}						
					}else if(ourElement.getType() == RationaleElementType.PATTERNDECISION){
						PatternDecision pd = new PatternDecision();
						pd.fromDatabase(ourElement.getName());
						Vector<Pattern> canPatterns = pd.getCandidatePatterns();
						Enumeration canPEnu = canPatterns.elements();
						while(canPEnu.hasMoreElements()){
							db.removeCandidatePattern(((Pattern)canPEnu.nextElement()).getName(), ourElement.getName());
						}
						db.deleteRationaleElement(pd);						
					}else if(ourElement.getType() == RationaleElementType.ONTENTRY){
						if(ourElement.getParent().getName().compareTo("Positive") == 0){
							db.removePatternOnt(ourElement.getName(), ourElement.getParent().getParent().getParent().getName(), "IS");
						}else{
							db.removePatternOnt(ourElement.getName(), ourElement.getParent().getParent().getParent().getName(), "NOT");
						}
					}else {

					}

					TreeParent parent = ourElement.getParent();
					PatternLibContentProvider provider = (PatternLibContentProvider) viewer.getContentProvider();
					provider.removeElement(ourElement);
					refreshBranch(parent);
				}
			}			
		}; //end of the edit element action definition
		deleteElement.setText("Delete");
		deleteElement.setToolTipText("Delete the item");

		deletePattern = new Action(){
			public void run(){
				RationaleDB db = RationaleDB.getHandle();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent){
					TreeParent ourElement = (TreeParent) obj;
					if (ourElement.getType() == RationaleElementType.PATTERN){
						db.removePattern(ourElement.getName());

						TreeParent parent = ourElement.getParent();
						PatternLibContentProvider provider = (PatternLibContentProvider) viewer.getContentProvider();
						provider.removePattern(ourElement);
						refreshBranch(parent);
					}
				}
				
			}
		};
		deletePattern.setText("Delete Pattern and ALL elements in the pattern.");
		deletePattern.setToolTipText("Deletes this pattern and all of its children");

		addCandidatePattern = new Action() {
			public void run() {
				RationaleDB db = RationaleDB.getHandle();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof TreeParent)
				{
					TreeParent ourElement = (TreeParent) obj;
					SelectCandidatePatterns scp = new SelectCandidatePatterns(db.getPatterns(), ourDisplay);
					if((scp.getSelections()!=null) && (scp.getSelections().size() > 0)){
						db.saveCandidatePatterns(ourElement.getName(), scp.getSelections());
						//						PatternDecision pd = new PatternDecision();
						//						pd.fromDatabase(ourElement.getName());

						//Now, update the tree...
						PatternLibContentProvider provider = (PatternLibContentProvider) viewer.getContentProvider();
						Iterator<String> toAdd = scp.getSelections().iterator();
						while (toAdd.hasNext()){
							provider.addElement(ourElement, toAdd.next(), RationaleElementType.PATTERN);
						}
						//rebuildTree();
						refreshBranch(ourElement);
					}

				}				
			}			
		}; //end of the edit element action definition
		addCandidatePattern.setText("Add Candidate Pattern");
		addCandidatePattern.setToolTipText("Add Candidate Pattern");

		addPosiOntEntry = new Action() {
			public void run() {

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				Pattern ourPattern = new Pattern();
				ourPattern.fromDatabase(obj.toString());
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);

				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					ourPattern.addPosiOnt(newOnt);
					ourPattern.toDatabase(0);
					//Put this to the tree...
					( (PatternLibContentProvider) viewer.getContentProvider()).addOntology((TreeParent) obj, newOnt);
					refreshBranch((TreeParent) obj);
				}
				//EditPattern ep = new EditPattern(ourDisplay, patternSelected, true);


			}			
		};
		addPosiOntEntry.setText("Add Positive Ontology Entry");
		addPosiOntEntry.setToolTipText("Add new Ontology Entry affected positively by this pattern");

		addNegaOntEntry = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				Pattern ourPattern = new Pattern();
				ourPattern.fromDatabase(obj.toString());
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);

				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					ourPattern.addNegaOnt(newOnt);
					ourPattern.toDatabase(0);
					//Put this to the tree...
					( (PatternLibContentProvider) viewer.getContentProvider()).addOntology((TreeParent) obj, newOnt);
					refreshBranch((TreeParent) obj);
				}				
			}			
		};
		addNegaOntEntry.setText("Add Negative Ontology Entry");
		addNegaOntEntry.setToolTipText("Add new Ontology Entry affected negatively by this pattern");

		addPatternDecision = new OpenRationaleEditorAction(PatternDecisionEditor.class, this, true, RationaleElementType.PATTERNDECISION);
		addPatternDecision.setText("Add Decision");
		showPatternDecisionEditor = new OpenRationaleEditorAction(PatternDecisionEditor.class, this, false);


		importXML = new Action(){
			public void run(){
				Shell shell = new Shell();
				FileDialog path = new FileDialog(shell, SWT.SAVE);
				String[] ext = {"*.xml"};
				String[] name = {"XML (*.xml)"};
				path.setFilterExtensions(ext);
				path.setFilterNames(name);
				// set default path to the static filename from RationaleDB
				path.setFileName(RationaleDB.getOntName());
				
				shell.pack();
				
				// Open the path that the user selected
				String filePath = path.open();
				
				if (filePath == null){
					showInformation("XML import was aborted by the user.");
					return;
				}
				
				if (RationaleDBUtil.importFromXML(filePath)){
					showInformation("XML has been imported successfully.");
				}
				else showInformation ("Cannot import this XML.");
				rebuildTree();
				if (RationaleExplorer.getHandle() != null)
					RationaleExplorer.getHandle().rebuildTree(false);
				if (TacticLibrary.getHandle() != null){
					TacticLibrary.getHandle().rebuildTree();
				}
			}
		};
		
		importXML.setText("Import from XML");
		importXML.setToolTipText("Load an XML from the a file and store it to a database");
		
		exportXML = new Action(){
			public void run(){
				Shell shell = new Shell();
				FileDialog path = new FileDialog(shell, SWT.SAVE);
				String[] ext = {"*.xml"};
				String[] name = {"XML (*.xml)"};
				path.setFilterExtensions(ext);
				path.setFilterNames(name);
				// set default path to the static filename from RationaleDB
				path.setFileName(RationaleDB.getOntName());
				
				shell.pack();
				
				// Open the path that the user selected
				String filePath = path.open();
				
				if (filePath == null){
					showInformation("XML Export was aborted by the user.");
					return;
				}
				
				if (RationaleDBUtil.exportToXML(filePath)){
					showInformation("XML has been exported successfully.");
				}
				else showInformation("Cannot create this XML. Choose another filename, stop modifying, and try again.");
				
			}
			
		};
		
		exportXML.setText("Export XML");
		exportXML.setToolTipText("Exports the database to an XML file and store it to a database");
		
		exportPatternParticipants = new AssociateUMLAction(false, false, viewer);
		exportPatternParticipants.setText("Export to XMI (UML)");
		exportPatternParticipants.setToolTipText("This exports the pattern participants to a diagram.");
		
		//Definitely needs a wizard!
		exportExistingUML = new AssociateUMLAction(false, true, viewer);
		exportExistingUML.setText("Export to an existing XMI (UML) file");
		exportExistingUML.setToolTipText("This exports the pattern participants to an existing UML file");

	}
	

	private class NumberInputDialog extends Dialog {
		  Integer value;
		  String title, prompt;

		  /**
		   * @param parent
		   */
		  public NumberInputDialog(Shell parent, String title, String prompt) {
		    super(parent);
		    this.title = title;
		    this.prompt = prompt;
		  }

		  /**
		   * Makes the dialog visible.
		   * 
		   * @return
		   */
		  public Integer open() {
		    Shell parent = getParent();
		    final Shell shell =
		      new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		    shell.setText(title);

		    shell.setLayout(new org.eclipse.swt.layout.GridLayout(2, true));

		    Label label = new Label(shell, SWT.NULL);
		    label.setText(prompt);

		    final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);

		    final Button buttonOK = new Button(shell, SWT.PUSH);
		    buttonOK.setText("Ok");
		    buttonOK.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END));

		    text.addListener(SWT.Modify, new Listener() {
		      public void handleEvent(Event event) {
		        try {
		          value = new Integer(text.getText());
		          if (value >= 0)
		        	  buttonOK.setEnabled(true);
		          else buttonOK.setEnabled(false);
		        } catch (Exception e) {
		          buttonOK.setEnabled(false);
		        }
		      }
		    });

		    buttonOK.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		        shell.dispose();
		      }
		    });

		    shell.addListener(SWT.Traverse, new Listener() {
		      public void handleEvent(Event event) {
		        if(event.detail == SWT.TRAVERSE_ESCAPE)
		          event.doit = false;
		      }
		    });

		    text.setText("");
		    shell.pack();
		    shell.open();

		    Display display = parent.getDisplay();
		    while (!shell.isDisposed()) {
		      if (!display.readAndDispatch())
		        display.sleep();
		    }

		    return value;
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
			"RationaleExplorer",
			message);
	}


	/**
	 * For context menu setup
	 * @param manager
	 */
	private void fillContextMenu (IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();

		if (obj instanceof TreeParent)
		{
			TreeParent ourElement = (TreeParent) obj;

			if(ourElement.getType() == RationaleElementType.ONTENTRY){
				manager.add(editElement);
				manager.add(deleteElement);
				//manager.add(addPosiOntEntry);

				//manager.add(new Separator());
				//manager.add(search);
			} else if(ourElement.getType() == RationaleElementType.PATTERNDECISION){
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(addCandidatePattern);
				//manager.add(new Separator());
				//manager.add(search);
			} else if(ourElement.getType() == RationaleElementType.PATTERN){
					manager.add(editElement);
					manager.add(deletePattern);
					manager.add(addPosiOntEntry);
					manager.add(addNegaOntEntry);
					manager.add(addPatternDecision);
					manager.add(editPatternParticipants);
					manager.add(exportPatternParticipants);
					manager.add(exportExistingUML);
					//manager.add(new Separator());
					//manager.add(search);
			} else if (ourElement.getType() == RationaleElementType.RATIONALE){
				if (ourElement.getName().equals("Architectural Patterns") || 
						ourElement.getName().equals("Design Patterns") || 
						ourElement.getName().equals("Idioms")){
					manager.add(addPatternEditor);
				}
			}
			else if(ourElement.getName().compareTo("Decisions") == 0){
				//manager.add(addDecision);
			}						
		}
		manager.add(new Separator());
		//manager.add(generateRatReportFromHere);
		manager.add(new Separator("Additions"));		
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		drillDownAdapter.addNavigationActions(manager);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(importXML);
		manager.add(exportXML);
	}

	public void refreshBranch(TreeParent parent)
	{
		viewer.refresh(parent);
		Iterator childrenI = parent.getIterator();
		while (childrenI.hasNext())
		{
			refreshBranch((TreeParent) childrenI.next());
		}
		//System.out.println("Refresh done!");
	}
	


	private void editElement (TreeParent obj, RationaleElement rElement, Display theDisplay)
	{
		//		boolean canceled = rElement.display();
		boolean canceled = rElement.display(viewer.getControl().getShell().getDisplay());
		//		System.out.println("canceled? = " + canceled);
		if (!canceled)
		{
			updateTreeElement(obj, rElement);
		}

	}

	/**
	 * Update method for editing an existing element.  This has the same function as the
	 * editElement method, but is designed to be called by the new editors instead (there is
	 * no display passed from the new editors).
	 * 
	 * @param p - the selected tree element being edited
	 * @param e - the pattern element being edited
	 */
	public TreeParent editUpdate(TreeParent p, RationaleElement e) {
		return updateTreeElement(p, e);
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
		RationaleDB db = RationaleDB.getHandle();		

		// Add The Element TO The Tree
		TreeParent newEle = addElement(p, e);

		// Update Status Of Element
		Vector<RationaleStatus> status = null;

		status = e.updateStatus();

		// Update Rationale Task List And Database With New Status
		RationaleTaskList tlist = RationaleTaskList.getHandle();
		Vector<RationaleStatus> oldStatus = null;
		UpdateManager manager = UpdateManager.getHandle();

		oldStatus = manager.getInitialStatus();

		if( status != null ) {
			updateStatus(oldStatus, status);
			db.addStatus(status);
			tlist.addTasks(status);			
		}
		if( oldStatus.size() > 0 ) {
			db.removeStatus(oldStatus);
			tlist.removeTasks(oldStatus);
		}

		// Refresh Affected Branch Of Tree
		refreshBranch(p);

		// Update Anything In Tree Affected By Insertion
		Vector<TreeObject> treeUpdates = manager.makeUpdates();
		Iterator<TreeObject> treeIterator = treeUpdates.iterator();
		while( treeIterator.hasNext() )
		{
			getViewer().update((TreeParent)treeIterator.next(), null);
		}
		//TODO What is this for?
		/*
		RationaleTreeMap map = RationaleTreeMap.getHandle();		
		Vector treeObjs = map.getKeys(map.makeKey(e.getName(), e.getElementType()));
		//if there's more than one we don't care, just get the first
		viewer.reveal(treeObjs.elementAt(0));
		viewer.expandToLevel(treeObjs.elementAt(0), 4);
		 */
		return newEle;
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
		if (obj.getName().compareTo(rElement.getName()) != 0)
		{
			//			System.out.println("name has changed");
			//need to save the old and new names and make the changes
			updateName(obj.getName(), rElement.getName(), rElement.getElementType());
			
			//Since pattern name has been changed, we need to refresh tactic library if it is opened.
			if (TacticLibrary.getHandle() != null){
				TacticLibrary.getHandle().rebuildTree();
			}
		}
		Vector<RationaleStatus> newStat = rElement.updateStatus();
		//		System.out.println("new stat ln (editor) = " + newStat.size());
		Vector<RationaleStatus> curStatus = null; 
		UpdateManager manager = UpdateManager.getHandle();
		curStatus = manager.getInitialStatus();										
		RationaleTaskList tlist = RationaleTaskList.getHandle();
		if (newStat != null)
		{
			System.out.println("updated status");
			//Before updating the task list, compare the status lists!
			updateStatus(curStatus, newStat);

		}
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

		//before we update our tree, we need to make sure we 
		//do any "structural" updates!
		TreeParent newParent = refreshElement((TreeParent) obj, rElement);
		//now make our updates
		Vector treeUpdates = manager.makeUpdates();
		//update what is displayed in the tree
		//how do I update if name changes?
		//		System.out.println(rElement.getName() + ": enabled = " + new Boolean(rElement.getEnabled()).toString());
		newParent.update(rElement.getName(), rElement.getEnabled());
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

		return newParent;
	}

	/**
	 * Refreshes the display of a tree element. This is called after an element is
	 * edited (I believe adding an element has its own refresh). The only types that
	 * change structure when edited are the Argument (it could have a different argument
	 * type), Claim (you could select a different Ontology Entry) and cooccurrence and tradeoff elements. Other elements only get children added
	 * by saying "New" under the parent. That doesn't require a refreshElement call to update
	 * 
	 * @param parent - the parent whose children need to be refreshed
	 * @param element - the element corresponding to the parent (provides the type)
	 * @return returns the tree parent - the one passed in is discarded and a new one created
	 */
	public TreeParent refreshElement (TreeParent parent, RationaleElement element)
	{
		TreeParent grandParent = parent.getParent();
		TreeParent newParent;
		//now, check to see if there is likely to be a structural change
		if ((element.getElementType() == RationaleElementType.ARGUMENT) ||
				(element.getElementType() == RationaleElementType.COOCCURRENCE) ||
				(element.getElementType() == RationaleElementType.CLAIM) ||
				(element.getElementType() == RationaleElementType.TRADEOFF))
		{
			RationaleViewContentProvider content = ( (RationaleViewContentProvider) viewer.getContentProvider());

			//for simplicity, we will assume that yes, the structure changed
			//so, we remove the old element from the tree
			content.removeElement(parent);
			//and then, we create a new element based on the RationaleElement just
			//modified
			newParent = addElement(grandParent, element);	
			refreshBranch(grandParent);		
			return newParent;	

		}
		else
		{
			return parent;
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

		if (element instanceof Tradeoff)
		{
			//			System.out.println("adding Tradeoff");
			return ( (RationaleViewContentProvider) viewer.getContentProvider()).addTradeoff(parent, (Tradeoff) element);		
		}
		else if (element instanceof Argument)
		{
			//			System.out.println("adding Argument");
			//will need a special argument provider
			return ( (RationaleViewContentProvider) viewer.getContentProvider()).addArgument(parent, (Argument) element);
		}
		else if (element instanceof Claim)
		{
			//will need a special claim provider
			return ( (RationaleViewContentProvider) viewer.getContentProvider()).addClaim(parent, (Claim) element);
		}
		else if (element instanceof Pattern)
		{
			return ( (PatternLibContentProvider) viewer.getContentProvider()).addPattern(parent, (Pattern) element);
		}
		else if (element instanceof PatternDecision){
			return ( (PatternLibContentProvider) viewer.getContentProvider()).addDecision(parent, (PatternDecision) element);
		}
		//else if (element instanceof OntEntry){
		//	return ( (PatternLibContentProvider) viewer.getContentProvider()).addOntology(parent, (OntEntry) element);
		//}
		else return null;
	}

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

	/**
	 * Sets up the new status of a Rationale Element after it (or one of its children) 
	 * is edited. This is done by taking new status elements and adding them to the database
	 * and taking old no longer true status values and removing them
	 * @param curStatus - on entry, this vector contains the previous status values, on exit it contains those that no longer apply
	 * @param newStatus - on entry, the complete status, on exit, only the new changes to add to the DB
	 * @return returns a flag indicating if the status changed
	 */
	public boolean updateStatus(Vector<RationaleStatus> curStatus, Vector<RationaleStatus> newStatus)
	{
		boolean different = true;
		Vector<RationaleStatus> removeCur = new Vector<RationaleStatus>();
		Vector<RationaleStatus> removeNew = new Vector<RationaleStatus>();

		Iterator curS = curStatus.iterator();
		//for each item in curStatus
		while (curS.hasNext())
		{
			RationaleStatus curSt = (RationaleStatus) curS.next();
			Iterator newS = newStatus.iterator();
			while (newS.hasNext())
			{
				RationaleStatus newSt = (RationaleStatus) newS.next();
				//if a matching item in newStatus is found,
				if (curSt.equivalentTo(newSt))
				{
					//remove item from current status
					removeCur.add(curSt);
					//remove item from new status
					removeNew.add(newSt);
				}
			}
		}

		newStatus.removeAll(removeNew);
		curStatus.removeAll(removeCur);

		//this will leave curStatus with a list of items that should
		//be removed from the database and from the list
		//this will leave newStatus with a list of items that should
		//be added to the database and to the list
		return different;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub

	}


	public void associateAlternative(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}


	public void openDatabase(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}


	public void showRationaleNode(RationaleUpdateEvent e) {
		// TODO Auto-generated method stub

	}


	public void updateRationaleTree(RationaleUpdateEvent e) {
		MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR);
		mbox.setMessage("Got the event for updateRationaleTree");
		mbox.open();

	}


	public void propertyChange(PropertyChangeEvent event) {
		

	}

	public void updateRationaleStatus(RationaleUpdateEvent e) {
		// 

	}

	public void addNewElement(RationaleUpdateEvent e) {
		

	}

}
