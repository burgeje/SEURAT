package edu.wpi.cs.jburge.SEURAT.views;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.*;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.jdt.core.*;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.actions.*;
import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.SelectCandidate;
import edu.wpi.cs.jburge.SEURAT.editors.SelectItem;
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
import edu.wpi.cs.jburge.SEURAT.tasks.RationaleTaskList;

import org.eclipse.core.resources.IFile;
import edu.wpi.cs.jburge.SEURAT.SEURATResourcePropertiesManager;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jface.action.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import SEURAT.preferences.PreferenceConstants;
import SEURAT.xmlIO.RationaleEntry;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.decorators.*;

/**
 * The Candidate Rationale Explorer is used as temporary storage for
 * rationale elements extracted from documents that haven't yet been imported into the 
 * SEURAT rationale-base.
 * It is essentially a tree view with different icons specifying the different types of 
 * rationale elements and their status.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. 
 * <p>
 */

public class CandidateRationaleExplorer extends ViewPart implements ISelectionListener, IRationaleUpdateEventListener,
IPropertyChangeListener {

	/**
	 * The view into the tree of rationale
	 */
	private TreeViewer viewer;
	/**
	 * Provides the icons that make up our tree
	 */
	protected CandidateLabelProvider labelProvider;
	/**
	 * ?
	 */
	private DrillDownAdapter drillDownAdapter;

	/**
	 * Menu item to edit an element
	 */
	private Action editElement;

	/**
	 * Menu item to delete an element
	 */
	private Action deleteElement;

	/**
	 * Menu item to move an element
	 */
	private Action moveElement;

	/**
	 * Menu item to adopt an element and its children
	 */
	private Action adoptElement;

	/**
	 * Menu item to adopt an element and its children
	 */
	private Action changeElementType;
	/**
	 * Menu item to adopt an element under an alternative
	 */
	private Action adoptElementUnderAlternative;
	/**
	 * Menu item to adopt an element and its children under a decision
	 */
	private Action adoptElementUnderDecision;
	/**
	 * Menu item to adopt an element under a requirement
	 */
	private Action adoptElementUnderRequirement;
	/**
	 * Menu item to input rationale from an XML file
	 */
	private Action inputRationale;


	/**
	 * Points to our display
	 */
	private Display ourDisplay;

	//	protected TreeParent invisibleRoot;


	class NameSorter extends ViewerSorter {
	}



	/**
	 * The constructor.
	 */
	public CandidateRationaleExplorer() {

	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new CandidateRationaleContentProvider());
		labelProvider = new CandidateLabelProvider();
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

		viewer.setInput(((CandidateRationaleContentProvider) viewer.getContentProvider()).initialize());
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
				CandidateRationaleExplorer.this.fillContextMenu(manager);
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
		manager.add(inputRationale);
		//		manager.add(testAction);
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

		//		Object curObj = ((IStructuredSelection)curSel).getFirstElement();

		if (obj instanceof CandidateTreeParent)
		{
			CandidateTreeParent ourElement = (CandidateTreeParent) obj;
			if (ourElement.getType() == RationaleElementType.ALTERNATIVE)
			{
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(moveElement);
				manager.add(adoptElement);
				manager.add(adoptElementUnderDecision);
				manager.add(changeElementType);

			}
			else if (ourElement.getType() == RationaleElementType.REQUIREMENT)
			{
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(adoptElement);
				manager.add(adoptElementUnderRequirement);
				manager.add(changeElementType);
			}
			else if (ourElement.getType() == RationaleElementType.DECISION)
			{
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(adoptElement);
				manager.add(adoptElementUnderDecision);
				manager.add(changeElementType);
			}
			else if (ourElement.getType() == RationaleElementType.ARGUMENT)
			{
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(moveElement);
				manager.add(adoptElementUnderRequirement);
				manager.add(adoptElementUnderAlternative);
				manager.add(changeElementType);
			}
			else if (ourElement.getType() == RationaleElementType.ASSUMPTION)
			{
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(moveElement);
				manager.add(changeElementType);
				//				manager.add(adoptElementUnderArgument);
			}
			else if (ourElement.getType() == RationaleElementType.QUESTION)
			{
				manager.add(editElement);
				manager.add(deleteElement);
				manager.add(moveElement);
				manager.add(adoptElementUnderDecision);
				manager.add(adoptElementUnderAlternative);
				manager.add(changeElementType);

			}

		}

		// Other plug-ins can contribute there actions here
		manager.add(new Separator("Additions"));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		drillDownAdapter.addNavigationActions(manager);
	}

	/**
	 * This method sets up the actions invoked by chosing menu items
	 *
	 */
	private void makeActions() {

		//		System.out.println("made some actions?");


		//
		// input Rationale
		//
		inputRationale = new Action() {
			public void run() {

				FileDialog fd = new FileDialog(viewer.getControl().getShell(), SWT.OPEN);
				fd.setText("Select");
				fd.setFilterPath(".");
				//String[] filterExt = { "*.xml", "*.*" };
				String[] filterExt = { "*.docx", "*.*" };
				fd.setFilterExtensions(filterExt);
				String docFile=fd.open();
				//String selected = fd.open();
				String selected;
				try {
					selected = readFile(docFile).getName();
					System.out.println(selected);
					ImportXMLFile(selected);
				} catch (ZipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}




			}
		}; //end of the action definition		
		inputRationale.setText("Input Rationale");
		inputRationale.setToolTipText("Input Rationale from XML Files");


		//
		//edit rationale element Action
		//
		editElement = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					editElement((CandidateTreeParent) obj, rElement, ourDisplay);

				}
				refreshBranch((CandidateTreeParent) obj);
			}

		}; //end of the edit element action definition
		editElement.setText("Edit");
		editElement.setToolTipText("Edit Rationale");

		//
		// move rationale element action
		//
		moveElement = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				CandidateTreeParent parent;
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					parent = moveElement((CandidateTreeParent) obj, rElement, ourDisplay);
					refreshBranch(parent);					
				}

			}

		}; //end of the edit element action definition
		moveElement.setText("Move");
		moveElement.setToolTipText("Move Rationale");

		//
		// move rationale element action
		//
		adoptElement = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				CandidateTreeParent parent;
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					parent = adoptElement((CandidateTreeParent) obj, rElement, -1, null, ourDisplay);
					refreshBranch(parent);					
				}

			}

		}; //end of the edit element action definition
		adoptElement.setText("Adopt");
		adoptElement.setToolTipText("Adopt As Rationale");

		//
		// move rationale element action
		//
		adoptElementUnderDecision = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				CandidateTreeParent parent;
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					//We need to display a list of new parent elements and select which one we want
					SelectItem sel = new SelectItem(ourDisplay, RationaleElementType.DECISION);
					Decision parentRat = (Decision) sel.getNewItem();
					parent = adoptElement((CandidateTreeParent) obj, rElement, parentRat.getID(), RationaleElementType.DECISION,  ourDisplay);
					refreshBranch(parent);					
				}

			}

		}; //end of the edit element action definition
		adoptElementUnderDecision.setText("Adopt Under Decision");
		adoptElementUnderDecision.setToolTipText("Adopt As Rationale");

		//

		//change rationale element type Action
		//
		changeElementType = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				//We need to display a list of new parent elements and select which one we want
				RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
				SelectType eleType = new SelectType(ourDisplay);
				String type=eleType.getType();
				String typeSelected=(((CandidateTreeParent) obj).getType()).toString();

				//Change Requirement element type
				if(typeSelected.compareTo("Requirement")==0)
				{
					// change Requirement to Decision
					if (type.equals("Decision"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						//						System.out.println("abc");
						if((ele.getChildren()).isEmpty())
						{
							//							ConsistencyChecker checker = new ConsistencyChecker(ele.getID(), ele.getName(),(ele.getType()).toString());
							//							if(checker.check())

							ele.setParent(0);
							ele.setType(RationaleElementType.DECISION);
							ele.toDatabase(0, false);
							//							System.out.println("above ele.getName()");
							//							System.out.println(ele.getName());
							rebuildTree();
							//							System.out.println(ele.getName());
							/*			RationaleTreeMap map = RationaleTreeMap.getHandle();
						    Vector treeObjs = map.getKeys(map.makeKey(ele.getName(), RationaleElementType.DECISION));
							Iterator<TreeObject> treeIterator = treeObjs.iterator();
							System.out.println("I am in treeIterator.hasNext()");
							TreeObject treeEle = (TreeObject) treeIterator.next();
//							viewer.reveal(treeEle);
							viewer.expandToLevel(treeEle, 4); */ // This just does not work I don't know why. So instead, use below
							viewer.expandAll();
						}
						else
						{
							System.out.println("change Requirement to Argument error box");
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Requirement to Argument
					if (type.equals("Argument"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							CandidateTreeParent parent;
							// We have to change the element type first, to make fit the RationaleDB.java in order to display all Decision and Alternative
							ele.setType(RationaleElementType.ARGUMENT);
							parent = moveElement((CandidateTreeParent) obj, ele, ourDisplay);
							//								System.out.println(parent);
							if(parent==null)
							{
								String l_message = "";
								l_message += "Error. Can not selected element itself.";
								MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
								mbox.setMessage(l_message);
								mbox.setText("Selected Element Itself Error");
								mbox.open();
							}
							refreshBranch(parent);					
						}
						else
						{
							//							System.out.println("change Requirement to Argument error box");
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Requirement to Argument";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Requirement to Alternative
					if (type.equals("Alternative"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							if (obj instanceof CandidateTreeParent)
							{
								CandidateTreeParent parent;
								// We have to change the element type first, to make fit the RationaleDB.java in order to display all Decision and Alternative
								ele.setType(RationaleElementType.ALTERNATIVE);
								parent = moveElement((CandidateTreeParent) obj, ele, ourDisplay);
								refreshBranch(parent);					
							}
						}
						else
						{
							//							System.out.println("change Requirement to Alternative error box");
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Requirement to Alternative";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}
				}//end of Change Requirement element type


				//Change Decision element type
				if(typeSelected.compareTo("Decision")==0)
				{
					// change Decision to Requirement
					if (type.equals("Requirement"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							ele.setParent(0);
							ele.setType(RationaleElementType.REQUIREMENT);
							ele.toDatabase(0, false);
							//CandidateTreeParent parent;
							rebuildTree();
							viewer.expandAll();
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Decision to Requirement";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Decision to Argument
					if (type.equals("Argument"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							if (obj instanceof CandidateTreeParent)
							{
								CandidateTreeParent parent;
								// We have to change the element type first, to make fit the RationaleDB.java in order to display all Decision and Alternative
								ele.setType(RationaleElementType.ARGUMENT);
								parent = moveElement((CandidateTreeParent) obj, ele, ourDisplay);
								refreshBranch(parent);					
							}
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							//							l_message += "||||||change Decision to Argument";
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Decision to Alternative
					if (type.equals("Alternative"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							if (obj instanceof CandidateTreeParent)
							{
								CandidateTreeParent parent;
								// We have to change the element type first, to make fit the RationaleDB.java in order to display all Alternative
								ele.setType(RationaleElementType.ALTERNATIVE);
								parent = moveElement((CandidateTreeParent) obj, ele, ourDisplay);
								if(parent==null)
								{
									String l_message = "";
									l_message += "Error. Can not selected element itself.";
									MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
									mbox.setMessage(l_message);
									mbox.setText("Selected Element Itself Error");
									mbox.open();
								}
								refreshBranch(parent);					
							}
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Decision to Alternative";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}
				}//end of Change Decision element type


				//Change Alternative element type
				if(typeSelected.compareTo("Alternative")==0)
				{
					// change Alternative to Decision
					if (type.equals("Decision"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							ele.setParent(0);
							ele.setType(RationaleElementType.DECISION);
							ele.toDatabase(0, false);
							rebuildTree();
							viewer.expandAll();
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Alternative to Decision";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Alternative to Requirement
					if (type.equals("Requirement"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							ele.setParent(0);
							ele.setType(RationaleElementType.REQUIREMENT);
							ele.toDatabase(0, false);
							rebuildTree();
							viewer.expandAll();
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Alternative to Requirement";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Alternative to Argument
					if (type.equals("Argument"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							if (obj instanceof CandidateTreeParent)
							{
								CandidateTreeParent parent;
								// We have to change the element type first, to make fit the RationaleDB.java in order to display all Decision and Alternative
								ele.setType(RationaleElementType.ARGUMENT);
								parent = moveElement((CandidateTreeParent) obj, ele, ourDisplay);
								if(parent==null)
								{
									String l_message = "";
									l_message += "Error. Can not selected element itself.";
									MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
									mbox.setMessage(l_message);
									mbox.setText("Selected Element Itself Error");
									mbox.open();
								}
								refreshBranch(parent);					
							}
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Alternative to Argument";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}
				}//end of Change Alternative element type


				//Change Argument element type
				if(typeSelected.compareTo("Argument")==0)
				{
					//System.out.println("In Argument"); // for testing
					// change Argument to Requirement
					if (type.equals("Requirement"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							ele.setParent(0);
							ele.setType(RationaleElementType.REQUIREMENT);
							ele.toDatabase(0, false);
							rebuildTree();
							viewer.expandAll();
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Argument to Requirement";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Argument to Decision
					if (type.equals("Decision"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							ele.setParent(0);
							ele.setType(RationaleElementType.DECISION);
							ele.toDatabase(0, false);
							rebuildTree();
							viewer.expandAll();
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//							l_message += "||||||change Argument to Decision";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}

					// change Argument to Alternative
					if (type.equals("Alternative"))
					{
						CandidateRationale ele = (CandidateRationale) rElement;
						if((ele.getChildren()).isEmpty())
						{
							if (obj instanceof CandidateTreeParent)
							{
								CandidateTreeParent parent;
								// We have to change the element type first, to make fit the RationaleDB.java in order to display all Alternative
								ele.setType(RationaleElementType.ALTERNATIVE);
								parent = moveElement((CandidateTreeParent) obj, ele, ourDisplay);
								refreshBranch(parent);					
							}
						}
						else
						{
							String l_message = "";
							l_message += "The element you selected at least have a child."
								+ " Please move all elements under the element first.";
							//l_message += "||||||change Argument to Alternative";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(l_message);
							mbox.setText("Element selected have a child");
							mbox.open();
						}
					}
				}//the end of Change Argument element type
			}

		}; //change rationale element type action definition
		changeElementType.setText("Change Element Type");
		changeElementType.setToolTipText("Change Element Type");


		//
		// move rationale element action
		//
		adoptElementUnderRequirement = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				CandidateTreeParent parent;
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					//We need to display a list of new parent elements and select which one we want
					SelectItem sel = new SelectItem(ourDisplay, RationaleElementType.REQUIREMENT);
					Requirement parentRat = (Requirement) sel.getNewItem();
					parent = adoptElement((CandidateTreeParent) obj, rElement, parentRat.getID(), RationaleElementType.REQUIREMENT,  ourDisplay);
					refreshBranch(parent);					
				}

			}

		}; //end of the edit element action definition
		adoptElementUnderRequirement.setText("Adopt Under Requirement");
		adoptElementUnderRequirement.setToolTipText("Adopt As Rationale");
		//
		// move rationale element action
		//
		adoptElementUnderRequirement = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				CandidateTreeParent parent;
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					//We need to display a list of new parent elements and select which one we want
					SelectItem sel = new SelectItem(ourDisplay, RationaleElementType.REQUIREMENT);
					Requirement parentRat = (Requirement) sel.getNewItem();
					parent = adoptElement((CandidateTreeParent) obj, rElement, parentRat.getID(), RationaleElementType.REQUIREMENT,  ourDisplay);
					refreshBranch(parent);					
				}

			}

		}; //end of the edit element action definition
		adoptElementUnderRequirement.setText("Adopt Under Requirement");
		adoptElementUnderRequirement.setToolTipText("Adopt As Rationale");

		//
		// move rationale element action
		//
		adoptElementUnderAlternative = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				CandidateTreeParent parent;
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					//We need to display a list of new parent elements and select which one we want
					SelectItem sel = new SelectItem(ourDisplay, RationaleElementType.ALTERNATIVE);
					Alternative parentRat = (Alternative) sel.getNewItem();
					parent = adoptElement((CandidateTreeParent) obj, rElement, parentRat.getID(), RationaleElementType.ALTERNATIVE,  ourDisplay);
					refreshBranch(parent);					
				}

			}

		}; //end of the edit element action definition
		adoptElementUnderAlternative.setText("Adopt Under Alternative");
		adoptElementUnderAlternative.setToolTipText("Adopt As Rationale");
		//
		//delete rationale element Action
		//
		deleteElement = new Action() {
			public void run() {

				RationaleDB db = RationaleDB.getHandle();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof CandidateTreeParent)
				{
					RationaleElement rElement = getElement((CandidateTreeParent) obj, false);
					boolean canceled = rElement.delete();
					if (!canceled)
					{

						//before we update our tree, we need to make sure we 
						//do any "structural" updates!
						//			TreeParent newParent = removeElement((TreeParent) obj);
						CandidateTreeParent objDeleted = (CandidateTreeParent) obj;
						removeElement(objDeleted);					

					}
				}
			}

		};
		deleteElement.setText("Delete");
		deleteElement.setToolTipText("Delete Rationale");
		//		deleteElement.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
		//		getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));				
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
				editElement.run();
			}
		});
	}

	/**
	 * shows a message to the user
	 * @param message
	 */
	/*	private void showMessage(String message) {
	 MessageDialog.openInformation(
	 viewer.getControl().getShell(),
	 "RationaleExplorer",
	 message);
	 } */

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}


	public void dispose() {
		super.dispose();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */


	/**
	 * Updates the tree branch (to display new children, etc.)
	 * @param parent - the top of the branch to refresh
	 */
	void refreshBranch(CandidateTreeParent parent)
	{
		viewer.refresh(parent);
		Iterator childrenI = parent.getIterator();
		while (childrenI.hasNext())
		{
			refreshBranch((CandidateTreeParent) childrenI.next());
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
	RationaleElement getElement(CandidateTreeParent treeElement, boolean newElement)
	{
		RationaleElement ourElement = null;
		RationaleElementType type = treeElement.getType();
		ourElement = new CandidateRationale(type);

		if (!newElement)
		{
			ourElement.fromDatabase(treeElement.getName());
		}
		return ourElement;
	}
	/*	I'm not sure why this is here - no one seems to call it.
	 boolean updateAll(TreeParent treeElement)
	 {
	 boolean update = true;
	 if ((treeElement.getType() == RationaleElementType.TRADEOFF) ||
	 (treeElement.getType() == RationaleElementType.COOCCURRENCE))
	 {
	 update = false;
	 }
	 return update;
	 }
	 */	
	/**
	 * this creates a new element in the rationale tree
	 * @param parentElement - this is the rationaleElement parent
	 * @param rElement - this is our new (child) element
	 * @param obj - this is the parent tree element
	 */
	void createNewElement(RationaleElement parentElement, RationaleElement rElement, CandidateTreeParent obj)
	{
		boolean canceled = rElement.create(viewer.getControl().getShell().getDisplay(), parentElement);
		if (!canceled)
		{
			//need to add the new element to the tree...
			//			CandidateTreeParent newChild = new CandidateTreeParent(rElement.getName(), rElement.getElementType());				
			//			((TreeParent) obj).addChild(newChild);
			System.out.println("name in createNewElement = " + rElement.getName());
			addElement((CandidateTreeParent) obj, rElement);
			//add our new element
			refreshBranch((CandidateTreeParent) obj);
		}

	}


	/**
	 * Adding a new element to our rationale tree
	 * @param parent - the parent element in the tree
	 * @param element - the rationale element being added
	 * @return the new treeParent that corresponds to the rationaleElement
	 */
	CandidateTreeParent addElement(CandidateTreeParent parent, RationaleElement element)
	{
		CandidateTreeParent addedParent = ((CandidateRationaleContentProvider) viewer.getContentProvider()).addNewElement(parent, element);
		if (((CandidateRationale) element).getChildren() != null)
		{
			Iterator nextC = ((CandidateRationale)element).getChildren().iterator();
			while (nextC.hasNext())
			{
				addElement(addedParent, (RationaleElement) nextC.next());
			}
		}
		return addedParent;
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
	public CandidateTreeParent refreshElement (CandidateTreeParent parent, RationaleElement element)
	{
		CandidateTreeParent grandParent = parent.getAltParent();
		CandidateTreeParent newParent;
		//now, check to see if there is likely to be a structural change
		if ((element.getElementType() == RationaleElementType.ARGUMENT) ||
				(element.getElementType() == RationaleElementType.COOCCURRENCE) ||
				(element.getElementType() == RationaleElementType.CLAIM) ||
				(element.getElementType() == RationaleElementType.TRADEOFF))
		{
			//for simplicity, we will assume that yes, the structure changed
			//so, we remove the old element from the tree
			( (CandidateRationaleContentProvider) viewer.getContentProvider()).removeElement(parent);
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
	 * Removes an element from the tree. This is done when something is deleted but
	 * also done if the underlying tree structure is changed (the old is removed, and a new
	 * one is generated)
	 * @param parent the element being removed
	 * @return the parent of the element that was just removed
	 */
	public CandidateTreeParent removeElement (CandidateTreeParent parent)
	{

		CandidateTreeParent grandParent = parent.getAltParent();
		//remove the old element from the tree
		( (CandidateRationaleContentProvider) viewer.getContentProvider()).removeElement(parent);
		//re-draw this branch of the tree
		refreshBranch(grandParent);		
		return grandParent;

	}

	//

	/**
	 * This is the editing code that is called in response to a menu item from
	 * the tree OR on receipt of an event from the task list.
	 * @param obj - the selected tree element being edited
	 * @param rElement - the rationale Element being edited
	 * @param theDisplay - the parent display
	 */
	private void editElement (CandidateTreeParent obj, RationaleElement rElement, Display theDisplay)
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
	 * This is the code called when we move an element to a different part of the tree
	 * @param obj - the selected tree element being moved
	 * @param rElement - the rationale Element being moved
	 * @param theDisplay - the parent display
	 */
	private CandidateTreeParent moveElement(CandidateTreeParent obj, RationaleElement rElement, Display theDisplay)
	{
		CandidateTreeParent newParentTreeParent;
		CandidateTreeParent newChildTreeParent;
		CandidateRationale ele = (CandidateRationale) rElement;

		//We need to display a list of new parent elements and select which one we want
		SelectCandidate sel = new SelectCandidate(theDisplay, ele.getType());
		CandidateRationale parentRat = (CandidateRationale) sel.getNewItem();
		System.out.println(ele.getType());
		System.out.println(ele.getName());
		if(((String)parentRat.getName()).equals((String)ele.getName()))
		{
			return null;
		}

		//change our ID and write the element to the database
		ele.setParent(parentRat.getID());
		ele.toDatabase(parentRat.getID(), false);

		//now, we need to find the new parent in our tree... 
		newParentTreeParent = ((CandidateRationaleContentProvider) viewer.getContentProvider()).findCandidate(parentRat.getName());
		//delete from the old parent
		CandidateTreeParent oldParentTreeParent = (CandidateTreeParent)obj.getAltParent();
		//remove the old element from the tree
		( (CandidateRationaleContentProvider) viewer.getContentProvider()).removeElement((CandidateTreeParent) obj);
		//re-draw this branch of the tree
		refreshBranch(oldParentTreeParent);		
		//add to our new parent
		newChildTreeParent = addElement(newParentTreeParent, (RationaleElement) ele);
		refreshBranch(newParentTreeParent);
		return newParentTreeParent;
	}

	/**
	 * This is the code called when we decide to adopt an element and its children as rationale.
	 * @param obj - the selected tree element being moved
	 * @param rElement - the rationale Element being moved
	 * @param theDisplay - the parent display
	 */
	private CandidateTreeParent adoptElement(CandidateTreeParent obj, RationaleElement rElement, int pid, RationaleElementType ptype, Display theDisplay)
	{
		CandidateTreeParent newParentTreeParent;
		CandidateTreeParent newChildTreeParent;
		CandidateRationale ele = (CandidateRationale) rElement;
		RationaleElement newElement;

		//this element won't be adopted under a parent so we don't need to choose one. We do
		//need to create it from the candidate though...
		newElement = ele.createRationale(pid, ptype);


		//Now, delete our candidate rationale, recursively
		ele.deleteAll();

		//Now, the trick - we need to add it to the tree of rationale!
		RationaleUpdateEvent addEvt = new RationaleUpdateEvent(this);
		addEvt.fireUpdateEvent(newElement, viewer.getControl().getDisplay(), UpdateType.ADD);

		//delete from the old parent
		CandidateTreeParent oldParentTreeParent = (CandidateTreeParent)obj.getAltParent();
		//remove the old element from the tree
		( (CandidateRationaleContentProvider) viewer.getContentProvider()).removeElement((CandidateTreeParent) obj);

		return oldParentTreeParent;
	}

	/**
	 * This is the code called when we decide to adopt an element and its children as rationale.
	 * @param obj - the selected tree element being moved
	 * @param rElement - the rationale Element being moved
	 * @param theDisplay - the parent display
	 */
	private CandidateTreeParent adoptElement(CandidateTreeParent obj, RationaleElement rElement, RationaleElementType rType, Display theDisplay)
	{
		CandidateTreeParent newParentTreeParent;
		CandidateTreeParent newChildTreeParent;
		CandidateRationale ele = (CandidateRationale) rElement;

		//Now, we may or may not have a parent item. Requirements

		//We need to display a list of new parent elements and select which one we want
		SelectCandidate sel = new SelectCandidate(theDisplay, ele.getType());
		CandidateRationale parentRat = (CandidateRationale) sel.getNewItem();

		//change our ID and write the element to the database
		ele.setParent(parentRat.getID());
		ele.toDatabase(parentRat.getID(), false);

		//now, we need to find the new parent in our tree... 
		newParentTreeParent = ((CandidateRationaleContentProvider) viewer.getContentProvider()).findCandidate(parentRat.getName());
		//delete from the old parent
		CandidateTreeParent oldParentTreeParent = (CandidateTreeParent)obj.getAltParent();
		//remove the old element from the tree
		( (CandidateRationaleContentProvider) viewer.getContentProvider()).removeElement((CandidateTreeParent) obj);
		//re-draw this branch of the tree
		refreshBranch(oldParentTreeParent);		
		//add to our new parent
		newChildTreeParent = addElement(newParentTreeParent, (RationaleElement) ele);
		refreshBranch(newParentTreeParent);
		return newParentTreeParent;
	}
	/**
	 * updateTreeElement - updates our tree element after it has been edited. 
	 * This includes any name changes, any status changes, and adding any new rationale
	 * tasks to the task list. Yes, this method probably does too much... The database is updated
	 * with the status changes as well.
	 * @param obj - the tree element that was just edited
	 * @param rElement - the corresponding rationale element
	 */
	private void updateTreeElement (CandidateTreeParent obj, RationaleElement rElement)
	{
		//need to check to see if the name has changed
		if (obj.getName().compareTo(rElement.getName()) != 0)
		{
			//			System.out.println("name has changed");
			//need to save the old and new names and make the changes
			updateName(obj.getName(), rElement.getName(), rElement.getElementType());
		}
		//before we update our tree, we need to make sure we 
		//do any "structural" updates!
		CandidateTreeParent newParent = refreshElement((CandidateTreeParent) obj, rElement);
		//now make our updates
		//update what is displayed in the tree
		//how do I update if name changes?
		//		System.out.println(rElement.getName() + ": enabled = " + new Boolean(rElement.getEnabled()).toString());
		newParent.update(rElement.getName(), rElement.getEnabled());

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
		//shouldn't need to do anything here?

	}

	//we are now going to assume that the editing is done by the time
	//we receive the event
	/**
	 * When editing is done from a querie or some other method that does not
	 * have the treeParent for the edited item, the GUI where the edits were invoked from
	 * will fire a RationaleUpdateEvent so that we will know that the tree may have
	 * changed. That is handled here.
	 * @param e - the rationale update event received. It will contain the element itself.
	 */
	public void updateRationaleTree(RationaleUpdateEvent e)
	{
		//shouldn't need to do anything here?

	}

	/**
	 * This method is used to pop-up a question to ask user whether to go on with the job
	 * It is used for the association.
	 * @param message
	 * @return return value from the dialog
	 */
	private boolean showQuestion(String message) {
		return MessageDialog.openQuestion(
				viewer.getControl().getShell(),
				"RationaleExplorer",
				message);
	}

	/**
	 * Read in the XML file and import data into the database
	 */
	private void ImportXMLFile(String inputFile)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document ratDoc;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringBuffer sb = new StringBuffer();

			try {
				BufferedReader in = new BufferedReader(new FileReader(inputFile));
				String str;
				while ((str = in.readLine()) != null) {
					sb.append(str);
				}
				in.close();
			} catch (IOException e) {
				System.err.println(e.toString());
			}

			//			ratDoc = builder.parse(new File(inputFile));
			ratDoc = builder.parse(new InputSource(new StringReader(sb.toString())));
			readXMLData(ratDoc);

			//We need to re-build the tree. One way would have been to simply add each element
			//to the tree as it is created (most efficient?) but we'll just re-build it
			rebuildTree();

		} catch (SAXException sce) {
			System.err.println( sce.toString());
			System.err.println ("Creating a new Database");
			return;
		} catch (IOException ioe) {
			System.err.println (ioe.toString());
			System.err.println ("Creating a new Database");
			return;
		} catch (ParserConfigurationException pce) {
			System.err.println (pce.toString());
			System.err.println ("Creating a new Database");
			return;
		}


	}

	private static void readXMLData(Document ratDoc)
	{
		//Get a handle to our database
		RationaleDB db = RationaleDB.getHandle();
		Element ratTop = ratDoc.getDocumentElement();
		//this should be our parent Rationale element
		String source = ratTop.getAttribute("source");

		Element ratNext = (Element) ratTop.getFirstChild();
		while (ratNext != null)
		{
			String nextName;
			nextName = ratNext.getNodeName();

			int parentID;
			//	    	System.out.println(nextName);
			//here we check the type, then process
			if (nextName.compareTo("DR:requirement") == 0)
			{
				CandidateRationale nextReq = new CandidateRationale(RationaleElementType.REQUIREMENT);
				//				s.addRequirement(nextReq);
				nextReq.fromXML(ratNext, source);
				parentID = nextReq.toDatabase(0, true);

				//now, check for any arguments for/against the req

			}
			else if ((nextName.compareTo("DR:decisionproblem") == 0) ||
					(nextName.compareTo("DR:decision") == 0))
			{
				//process decision
				CandidateRationale nextDec = new CandidateRationale(RationaleElementType.DECISION);
				nextDec.fromXML(ratNext, source);
				parentID = nextDec.toDatabase(0, true);

				//now, check for any alternatives and arguments

			}
			ratNext = (Element) ratNext.getNextSibling();
		}

	}
	/**
	 * This method is used to rebuild the tree from a different copy of the database
	 * 
	 */
	public void rebuildTree()
	{
		RationaleDB.resetConnection();
		viewer.getTree().removeAll();
		viewer.getContentProvider().dispose();
		viewer.setContentProvider(new CandidateRationaleContentProvider());
		//this should re-fresh from the new database
		viewer.setInput(((CandidateRationaleContentProvider) viewer.getContentProvider()).initialize());
		viewer.expandToLevel(2);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// not needed for this class

	}

	public void addNewElement(RationaleUpdateEvent e) {
		// not needed for this class

	}

	public void associateAlternative(RationaleUpdateEvent e) {
		// not needed for this class

	}

	public void openDatabase(RationaleUpdateEvent e) {
		// need to re-build our tree - will we have timing issues?
		rebuildTree();

	}

	public void showRationaleNode(RationaleUpdateEvent e) {
		// Not needed - we only need to show using the viewer

	}

	public void updateRationaleStatus(RationaleUpdateEvent e) {
		// Not needed - no status on candidates

	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.P_DATABASETYPE) ||
				event.getProperty().equals(PreferenceConstants.P_DERBYNAME) ||
				event.getProperty().equals(PreferenceConstants.P_DATABASE)) {
			rebuildTree(); // Automatically connect to the new database
		}

	}
	/**
	 * Reads a Word file that contains tagged rationale and creates a temporary xml file
	 * that can then be imported into SEURAT as candidate rationale
	 * @param fileName - the name of the Word file
	 * @return the XML file containing the candidate rationale
	 * @throws IOException
	 * @throws ZipException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public File readFile(String fileName)throws IOException, ZipException, SAXException,ParserConfigurationException
	{
		String output="output.xml";
		File f=new File(output);
		try
		{
			String input=fileName;
			ZipFile zipFile=new ZipFile(new File(input),ZipFile.OPEN_READ);
			HashMap<String,String> commentMap = new HashMap<String,String>();
			HashMap<String,rationaleNode> rationale=new HashMap<String,rationaleNode>();
			List<String> commentOrder=new ArrayList();

			//Need to parse two different files - the comments file and the document file
			ZipEntry commentXML=zipFile.getEntry("word/comments.xml");
			ZipEntry documentXML=zipFile.getEntry("word/document.xml");
			InputStream commentXMLIS=zipFile.getInputStream(commentXML);
			InputStream documentXMLIS=zipFile.getInputStream(documentXML);
			DocumentBuilderFactory dbf1=DocumentBuilderFactory.newInstance();
			DocumentBuilderFactory dbf2=DocumentBuilderFactory.newInstance();
			org.w3c.dom.Document db1 = dbf1.newDocumentBuilder().parse(commentXMLIS);
			org.w3c.dom.Document db2 = dbf2.newDocumentBuilder().parse(documentXMLIS);
			db1.getDocumentElement().normalize();
			db2.getDocumentElement().normalize();  

			Node node=db1.getDocumentElement();
			for(Node child=node.getFirstChild();child!=null;child=child.getNextSibling())
			{
				if(child.getNodeName()=="w:comment")
				{
					commentMap.put(child.getAttributes().item(2).getNodeValue(),child.getTextContent());
				}
				recurse(child);
			}

			List<String> activeComments=new ArrayList();
			Node documentNode=db2.getDocumentElement();
			Node c;
			for(Node ch=documentNode.getFirstChild().getFirstChild();ch!=null;ch=ch.getNextSibling())
			{
				NodeList list=ch.getChildNodes();
				for(int k=0;k<list.getLength();++k)
				{
					c=list.item(k);
					if(c.getNodeName()=="w:commentRangeStart")
					{
						commentOrder.add(c.getAttributes().item(0).getNodeValue());
						activeComments.add(c.getAttributes().item(0).getNodeValue());
						String ourComment=commentMap.get(c.getAttributes().item(0).getNodeValue());
						if(ourComment!=null)
						{
							rationaleNode ele=new rationaleNode(ourComment);
							rationale.put(c.getAttributes().item(0).getNodeValue(), ele);
						}
					}
					else if(c.getNodeName()=="w:r")
					{
						Node textNode=c.getFirstChild();
						while ((textNode != null) && (textNode.getNodeName() != "w:t"))
								{
							textNode = textNode.getNextSibling();
								}
						if (textNode != null)
						{
						String text=textNode.getTextContent();
						//first child might not be the text node!
						for(String cmtID:activeComments)
						{
							rationaleNode update=rationale.get(cmtID);
							if(update!=null)
							{
								update.elementContents=update.elementContents+text;
								rationale.remove(cmtID);
								rationale.put(cmtID, update);
							}
						}
						}
					}
					else if(c.getNodeName()=="w:commentRangeEnd")
					{
						Node id=c.getAttributes().item(0);
						String idStr=id.getNodeValue();
						activeComments.remove(idStr);
					}

				}

				recurse(ch);
			}

			org.w3c.dom.Document xmlDoc=null;
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			String nmSpace="http://www.users.muohio.edu/burgeje/Rationale";
			xmlDoc=builder.newDocument();
			xmlDoc.setXmlVersion("1.0");
			xmlDoc.setXmlStandalone(true);
			Element root=xmlDoc.createElementNS(nmSpace, "DR:rationale");
			root.setAttribute("source", input);

			Element reqNode=null;
			Element decNode=null;
			Element altNode=null;

			for(String nextCmt:commentOrder)
			{
				rationaleNode nextRat=rationale.get(nextCmt);
				if(nextRat!=null)
				{
					if(nextRat.elementType.toLowerCase().equals("requirement"))
					{
						if(reqNode!=null)
						{
							root.appendChild(reqNode);
						}
						reqNode=xmlDoc.createElementNS(nmSpace, "DR:requirement");
						reqNode.setAttribute("name", nextRat.elementContents);
						Element descNode=xmlDoc.createElementNS(nmSpace, "DR:description");
						descNode.appendChild(xmlDoc.createTextNode(nextRat.elementContents));
						reqNode.appendChild(descNode);
					}
					else if(nextRat.elementType.toLowerCase().equals("decision"))
					{
						if(reqNode!=null)
						{
							root.appendChild(reqNode);
						}
						if(decNode!=null)
						{
							root.appendChild(decNode);
						}
						decNode=xmlDoc.createElementNS(nmSpace, "DR:decision");
						decNode.setAttribute("name", nextRat.elementContents);
						Element descNode=xmlDoc.createElementNS(nmSpace, "DR:description");
						descNode.appendChild(xmlDoc.createTextNode(nextRat.elementContents));
						decNode.appendChild(descNode);
					}
					else if((nextRat.elementType.toLowerCase().equals("alternative"))|| nextRat.elementType.toLowerCase().equals("alternative-selected"))
					{
						altNode=xmlDoc.createElementNS(nmSpace, "DR:alternative");
						altNode.setAttribute("name", nextRat.elementContents);
						if(nextRat.elementType.toLowerCase().equals("alternative-selected"))
						{
							altNode.setAttribute("status", "Adopted");
						}
						Element descNode=xmlDoc.createElementNS(nmSpace, "DR:description");
						descNode.appendChild(xmlDoc.createTextNode(nextRat.elementContents));
						altNode.appendChild(descNode);

						if(decNode!=null)
						{
							decNode.appendChild(altNode);
						}
						else
						{
							decNode=xmlDoc.createElementNS(nmSpace, "DR:decision");
							decNode.setAttribute("name", "unknown");
							Element descNode2=xmlDoc.createElementNS(nmSpace, "DR:description");
							descNode2.appendChild(xmlDoc.createTextNode("unknown"));
							decNode.appendChild(descNode2);
							decNode.appendChild(altNode);
						}
					}
					else if(nextRat.elementType.toLowerCase().equals("argument")||nextRat.elementType.toLowerCase().equals("argument-for")||nextRat.elementType.equals("argument-against"))
					{
						Element argNode=xmlDoc.createElementNS(nmSpace, "DR:argument");
						argNode.setAttribute("name", nextRat.elementContents);
						if(nextRat.elementContents.equals("argument-for"))
						{
							argNode.setAttribute("argtype", "Supports");
						}
						else if(nextRat.elementType.toLowerCase().equals("argument-against"))
						{
							argNode.setAttribute("argtype", "Denies");
						}
						Element descNode=xmlDoc.createElementNS(nmSpace, "DR:description");
						descNode.appendChild(xmlDoc.createTextNode(nextRat.elementContents));
						argNode.appendChild(descNode);
						if(altNode!=null)
						{
							altNode.appendChild(argNode);
						}
						else if(reqNode!=null)
						{
							reqNode.appendChild(argNode);
						}
						else
						{
							reqNode=xmlDoc.createElementNS(nmSpace, "DR:requirement");
							reqNode.setAttribute("name", "unknown");
							Element descNode3=xmlDoc.createElementNS(nmSpace, "DR:description");
							descNode3.appendChild(xmlDoc.createTextNode("unknown"));
							reqNode.appendChild(descNode3);
							reqNode.appendChild(argNode);
						}

					}
				}
			}//end for
			if(reqNode!=null)
			{
				root.appendChild(reqNode);
			}
			if(decNode!=null)
			{
				root.appendChild(decNode);
			}
			xmlDoc.appendChild(root);
			//String output="output.xml";
			//File f=new File(output);
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "no");
			//create a string from the xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(xmlDoc);
			trans.transform(source, result);
			String xmlString = sw.toString();
			//System.out.println(xmlString);
			// write the file
			FileWriter fw = new FileWriter(f);
			fw.write(xmlString);
			fw.close();

		}
		catch(IOException e)
		{
			System.err.println(e.toString());
		}
		catch (TransformerConfigurationException e) {
			System.err.println( e.toString());
		} catch (TransformerException tfe) {
			System.err.println( tfe.toString());
		}
		catch (Exception e)
		{
			System.out.println("Other exception?");
			System.err.println(e.toString());
		}

		System.out.println("Created output.xml file");
		return f;
	}
	private static void recurse(Node node) {
		for(Node child=node.getFirstChild();child!=null;child=child.getNextSibling())
		{
			if (child.hasChildNodes()) recurse(child);
		}
	}

}