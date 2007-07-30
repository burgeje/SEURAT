/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */

package edu.wpi.cs.jburge.SEURAT.queries;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;


import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.wpi.cs.jburge.SEURAT.inference.RequirementInferences;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ArgType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * Used to display any alternatives that refer to a specific requirement. This
 * is a pop-up menu from the requirement.
 * 
 * This was initially based off the TableViewer example in an Eclipse Corner
 * article - see the RationaleTaskList code for more details.
 */

public class RequirementRelationshipDisplay {

	/**
	 * Our shell
	 */
	private Shell shell;
	/**
	 * The table with our data
	 */
	private Table table;
	/**
	 * The table viewer
	 */
	private TableViewer tableViewer;
	/**
	 * Our close button
	 */
	private Button closeButton;
	
	/**
	 * Our relationships. A requirement can have several alternatives that
	 * affect it.
	 */
	private Vector<RequirementRelationship> relationshipList; 

	/**
	 * Our column table names
	 */
	private final String ALTERNATIVE_COLUMN 		= "Alternative";
	private final String RELATIONSHIP_COLUMN 	        = "Relationship";
	private final String SELECTED_COLUMN 			= "Selected";

	// Set column names
	private String[] columnNames = new String[] { 
			ALTERNATIVE_COLUMN, 
			RELATIONSHIP_COLUMN,
			SELECTED_COLUMN
			};


	/**
	 * Our constructor
	 * @param parent - the composite
	 * @param reqName - the name of the requirement
	 */
		public RequirementRelationshipDisplay(Composite parent, String reqName) {
		
		shell = new Shell();
		shell.setText("Requirement Relationship Display");
		
		//Find the data for our table
		Requirement ourReq = new Requirement();
		ourReq.fromDatabase(reqName);
		
		relationshipList = new Vector<RequirementRelationship>();
		RequirementInferences inf = new RequirementInferences();
		Vector args = inf.getArguments(ourReq, ArgType.SATISFIES);
		relationshipList.addAll(createList(args, ArgType.SATISFIES));
		args = inf.getArguments(ourReq, ArgType.ADDRESSES);
		relationshipList.addAll(createList(args, ArgType.ADDRESSES));
		args = inf.getArguments(ourReq, ArgType.VIOLATES);
		relationshipList.addAll(createList(args, ArgType.VIOLATES));	

		// Set layout for shell
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
	
		// Create a composite to hold the children
		Composite composite = new Composite(shell, SWT.NONE);
		this.addChildControls(composite);


		shell.pack(); //do I need this?
		// Ask the shell to display its content
		shell.open();
		this.run(shell);
	}

	/**
	 * Run and wait for a close event
	 * @param shell Instance of Shell
	 */
	private void run(Shell shell) {
		
		// Add a listener for the close button
		closeButton.addSelectionListener(new SelectionAdapter() {
       	
			// Close the view i.e. dispose of the composite's parent
			public void widgetSelected(SelectionEvent e) {
				table.getParent().getParent().dispose();
			}
		});
		
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Release resources
	 */
	public void dispose() {
		
		// Tell the label provider to release its ressources
		tableViewer.getLabelProvider().dispose();
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * @return the shell that was created	 
	 */
	private void addChildControls(Composite composite) {

		// Create a composite to hold the children
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
		composite.setLayoutData (gridData);

		// Set numColumns to 3 for the buttons 
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 4;
		composite.setLayout (layout);

		// Create the table 
		createTable(composite);
		
		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new RequirementRelationshipContentProvider());
		tableViewer.setLabelProvider(new RequirementRelationshipLabelProvider());
		// The input for the table viewer is the instance of ExampleargList
//		argList = new ExampleargList();
		tableViewer.setInput(relationshipList);

		// Add the buttons
		createButtons(composite);
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	//	final int NUMBER_COLUMNS = 4;

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);
		
		int listHeight = table.getItemHeight() * 12;
		Rectangle trim = table.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText(ALTERNATIVE_COLUMN);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RequirementRelationshipSorter(RequirementRelationshipSorter.ALTERNATIVE));
			}
		});
		
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(RELATIONSHIP_COLUMN);
		column.setWidth(80);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RequirementRelationshipSorter(RequirementRelationshipSorter.RELATIONSHIP));
			}
		});


		// 3rd column with task Owner
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(SELECTED_COLUMN);
		column.setWidth(40);
		// Add listener to column so tasks are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RequirementRelationshipSorter(RequirementRelationshipSorter.SELECTED));
			}
		});

	}

	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);

		// Set the default sorter for the viewer 
		tableViewer.setSorter(new RequirementRelationshipSorter(RequirementRelationshipSorter.RELATIONSHIP));
	}

	/*
	 * Close the window and dispose of resources
	 */
	public void close() {
		Shell shell = table.getShell();

		if (shell != null && !shell.isDisposed())
			shell.dispose();
	}
	
	/**
	 * Get our list of relationships by fetching alternatives from the database
	 * that refer to our requirement.
	 * @param args - our arguments
	 * @param type - the type of argument
	 * @return the relationships
	 */
	private Vector<RequirementRelationship> createList(Vector args, ArgType type )
	{
		Vector<RequirementRelationship> ourList = new Vector<RequirementRelationship>();
		Iterator argI = args.iterator();
		while (argI.hasNext())
		{
			Argument arg = (Argument)argI.next();
			if (arg.getPtype() == RationaleElementType.ALTERNATIVE)
			{
				Alternative alt = new Alternative();
				alt.fromDatabase(arg.getParent());
				RequirementRelationship rel = new RequirementRelationship();
				rel.setName(alt.getName());
				rel.setRelationship(type.toString());
				rel.setSelected(alt.getStatus() == AlternativeStatus.ADOPTED);
				ourList.add(rel);
			}
		
		}
		
		return ourList;
		
	}

	/** Inner class that holds our requirement relationship.
	 * 
	 * @author jburge
	 */
	class RequirementRelationship {
		String name;
		String relationship;
		boolean selected;
		
		RequirementRelationship()
		{
		}
		public String getName() {
			return name;
		}

		public String getRelationship() {
			return relationship;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setRelationship(String relationship) {
			this.relationship = relationship;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

	}
	
	/**
	 * InnerClass that acts as a proxy for the RequirementRelationshipList 
	 * providing content for the Table. 
	 */
	class RequirementRelationshipContentProvider implements IStructuredContentProvider  {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
//			argList.removeChangeListener(this);
		}

		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return relationshipList.toArray();
		}

		public void addTask(RequirementRelationship task) {
			tableViewer.add(task);
		}


	}
	
	class RequirementRelationshipLabelProvider 
		extends LabelProvider
		implements ITableLabelProvider {

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			RequirementRelationship task = (RequirementRelationship) element;
			switch (columnIndex) {
				case 0:  // COMPLETED_COLUMN
					result = task.getName();
					break;
				case 1 :
					result = task.getRelationship();
					break;
				case 2 :
					result = Boolean.toString(task.isSelected());
					break;
				default :
					break; 	
			}
			return result;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null; //no image columns
		}

	} //end inner class
	
	/**
	 * Used to sort our relationship table when we click on a column header
	 * @author burgeje
	 *
	 */
	class RequirementRelationshipSorter extends ViewerSorter {

		/**
		 * Constructor argument values that indicate to sort items alternative,
		 * relationship, or selection status
		 */
		public final static int ALTERNATIVE 		= 1;
		public final static int RELATIONSHIP 				= 2;
		public final static int SELECTED 	= 3;

		// Criteria that the instance uses 
		private int criteria;

		/**
		 * Creates a resource sorter that will use the given sort criteria.
		 *
		 * @param criteria the sort criterion to use
		 */
		public RequirementRelationshipSorter(int criteria) {
			super();
			this.criteria = criteria;
		}


		/**
		 * Compares two items
		 * @param viewer our viewer
		 * @param o1 the first object
		 * @param o2 the second object
		 * @return the comparision results
		 */
		public int compare(Viewer viewer, Object o1, Object o2) {

			RequirementRelationship task1 = (RequirementRelationship) o1;
			RequirementRelationship task2 = (RequirementRelationship) o2;

			switch (criteria) {
				case ALTERNATIVE :
					return compareText(task1.getName(), task2.getName());
				case RELATIONSHIP :
					return compareText(task1.getRelationship(), task2.getRelationship());
				case SELECTED :
					return compareText(Boolean.toString(task1.isSelected()), Boolean.toString(task2.isSelected()));
				default:
					return 0;
			}
		}

		/**
		 * Returns a number reflecting the collation order of the text strings
		 *
		 * @param str1 the first string
		 * @param str2 the second string
		 * @return a negative number if the first element is less  than the 
		 *  second element; the value 0 if the first element is
		 *  equal to the second element; and a positive number if the first
		 *  element is greater than the second element
		 */
		protected int compareText(String str1, String str2) {
			return collator.compare(str1, str2);
		}

		/**
		 * Returns the sort criteria of this this sorter.
		 *
		 * @return the sort criterion
		 */
		public int getCriteria() {
			return criteria;
		}
	} //end inner class
	
	

	/**
	 * Add the "Close" button
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Edit");
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
       	
			// Edit our alternative
			public void widgetSelected(SelectionEvent e) {
				RequirementRelationship altS = (RequirementRelationship) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				Alternative alt = new Alternative();
				alt.fromDatabase(altS.getName());
				boolean canceled = alt.display(shell.getDisplay());
				if (!canceled)
				{

				RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
				evt.fireUpdateEvent(alt, shell.getDisplay(), UpdateType.UPDATE);
				}
				shell.close();
				shell.dispose();
				
			}
		});
	
		//	Create and configure the "Close" button
		closeButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		closeButton.setText("Close");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80; 
		closeButton.setLayoutData(gridData); 
		

	}

	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}

	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	public Vector getAltList() {
		return relationshipList;	
	}

	public Control getControl() {
		return table.getParent();
	}

	public Button getCloseButton() {
		return closeButton;
	}
}