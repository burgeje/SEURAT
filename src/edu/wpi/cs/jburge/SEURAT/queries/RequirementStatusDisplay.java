/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */
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
//import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ArgType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * The Requirement Status Display shows a list of requirements with a particular
 * status value (violated, addressed, etc.) and the alternatives that have that
 * relationship with them. This is based on the TableViewer example from an
 * Eclipse Corner article. More information on the example can be found in the
 * JavaDoc for the RationaleTaskList. 
 */

public class RequirementStatusDisplay {
	
	/**
	 * Poinst to the shell
	 */
	private Shell shell;
	/**
	 * The table of information
	 */
	private Table table;
	/**
	 * The viewer
	 */
	private TableViewer tableViewer;
	/**
	 * The button that closes the display
	 */
	private Button closeButton;
	
	/**
	 * The information for the requirement
	 */
	private Vector<RequirementInformation> reqInfoList; 
	
	/**
	 * The property names to head up the columns
	 */
	private final String REQUIREMENT_COLUMN			= "Requirement";
	private final String ALTERNATIVE_COLUMN 		= "Alternative";
	
	// Set column names
	private String[] columnNames = new String[] { 
			REQUIREMENT_COLUMN,
			ALTERNATIVE_COLUMN, 
	};
	
	
	/**
	 * The status display constructor
	 * @param parent - the composit the widgets are put into
	 * @param stat - the requirement status we are interested in
	 */
	public RequirementStatusDisplay(Composite parent, ReqStatus stat) {
		
		shell = new Shell();
		shell.setText(stat.toString() + " Requirements");
		
		//Find the data for our table
		RationaleDB db = RationaleDB.getHandle();
		Vector ourReqs = db.getRequirements(stat);
		
		Iterator reqI = ourReqs.iterator();
		reqInfoList = new Vector<RequirementInformation>();
		while (reqI.hasNext())
		{
			Requirement ourReq = (Requirement) reqI.next();
			Vector<Argument> args;
			RequirementInferences inf = new RequirementInferences();
			if (stat == ReqStatus.SATISFIED)
			{
				args = inf.getArguments(ourReq, ArgType.SATISFIES);
			}
			else if (stat == ReqStatus.ADDRESSED)
			{
				args = inf.getArguments(ourReq, ArgType.ADDRESSES);
			}
			else if (stat == ReqStatus.VIOLATED)
			{
				args = inf.getArguments(ourReq, ArgType.VIOLATES);
			}
			else
			{
				args = null;
			}
			reqInfoList.addAll(createList(ourReq, args));
			
		}
		
		
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
		tableViewer.setContentProvider(new RequirementStatusContentProvider());
		tableViewer.setLabelProvider(new RequirementStatusLabelProvider());
		tableViewer.setInput(reqInfoList);
		
		// Add the buttons
		createButtons(composite);
	}
	
	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		
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
		
		// 1st column 
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText(REQUIREMENT_COLUMN);
		column.setWidth(200);
		// Add listener to column so items are sorted by requirement name when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RequirementStatusSorter(RequirementStatusSorter.REQUIREMENT));
			}
		});
		
		// 2nd column with alternative
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(ALTERNATIVE_COLUMN);
		column.setWidth(200);
		// Add listener to column so alternatives are sorted by name when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RequirementStatusSorter(RequirementStatusSorter.ALTERNATIVE));
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
		tableViewer.setSorter(new RequirementStatusSorter(RequirementStatusSorter.REQUIREMENT));
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
	 * Create a list of requirement information to display
	 * @param req the requirement
	 * @param alts a list of arguments referring to it with the correct status
	 * @return a list of requirement information records
	 */
	private Vector<RequirementInformation> createList(Requirement req, Vector<Argument> alts)
	{
		Vector<RequirementInformation> ourList = new Vector<RequirementInformation>();
		if (alts == null)
		{
			RequirementInformation rel = new RequirementInformation();
			rel.setName(req.getName());
			rel.setAlternative("none");
			ourList.add(rel);
			return ourList;
		}
		Iterator argI = alts.iterator();
		while (argI.hasNext())
		{
			Argument arg = (Argument)argI.next();
			if (arg.getPtype() == RationaleElementType.ALTERNATIVE)
			{
				Alternative alt = new Alternative();
				alt.fromDatabase(arg.getParent());
				RequirementInformation rel = new RequirementInformation();
				rel.setName(req.getName());
				rel.setAlternative(alt.getName());
				ourList.add(rel);
			}
			
		}
		
		return ourList;
		
	}
	
	/** Inner class that holds the data to be displayed in the table
	 * 
	 * @author jburge
	 */
	class RequirementInformation {
		String name;
		String alternative;
		
		RequirementInformation()
		{
		}
		public String getName() {
			return name;
		}
		
		public String getAlternative() {
			return alternative;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setAlternative(String alternative) {
			this.alternative = alternative;
		}
		
		
	}
	
	/**
	 * InnerClass that acts as a proxy for the Requirement Informatio List 
	 * providing content for the Table. 
	 */
	class RequirementStatusContentProvider implements IStructuredContentProvider  {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
		}
		
		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return reqInfoList.toArray();
		}
		
		/* (non-Javadoc)
		 * @see IargListViewer#addTask(ExampleTask)
		 */
		public void addTask(RequirementInformation task) {
			tableViewer.add(task);
		}
		
	}
	
	class RequirementStatusLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {
		
		
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			RequirementInformation task = (RequirementInformation) element;
			switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN
				result = task.getName();
				break;
			case 1 :
				result = task.getAlternative();
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
	 * Sorts our requirements
	 * @author burgeje
	 *
	 */
	class RequirementStatusSorter extends ViewerSorter {
		
		/**
		 * Constructor argument values that indicate to sort items by 
		 * description, owner or percent complete.
		 */
		public final static int ALTERNATIVE 		= 1;
		public final static int REQUIREMENT 				= 2;
		
		// Criteria that the instance uses 
		private int criteria;
		
		/**
		 * Creates a resource sorter that will use the given sort criteria.
		 *
		 * @param criteria the sort criterion to use
		 */
		public RequirementStatusSorter(int criteria) {
			super();
			this.criteria = criteria;
		}
		
		/* Compare our elements
		 * Method declared on ViewerSorter.
		 */
		public int compare(Viewer viewer, Object o1, Object o2) {
			
			RequirementInformation task1 = (RequirementInformation) o1;
			RequirementInformation task2 = (RequirementInformation) o2;
			
			switch (criteria) {
			case REQUIREMENT :
				return compareText(task1.getName(), task2.getName());
			case ALTERNATIVE :
				return compareText(task1.getAlternative(), task2.getAlternative());
			default:
				return 0;
			}
		}
		
		
		/**
		 * Returns a number reflecting the collation order of the given strings
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
		 * Returns the sort criteria of this sorter.
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
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
			
			// Edit our requirement
			public void widgetSelected(SelectionEvent e) {
				RequirementInformation altS = (RequirementInformation) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				Requirement req = new Requirement();
				req.fromDatabase(altS.getName());
				boolean canceled = req.display(shell.getDisplay());
				if (!canceled)
				{
					
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(req, shell.getDisplay(), UpdateType.UPDATE);
				}
				shell.close();
				shell.dispose();
				
			}
		});
		
		// Create and configure the "Show" button
		Button show = new Button(parent, SWT.PUSH | SWT.CENTER);
		show.setText("Show");
		
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80;
		show.setLayoutData(gridData);
		show.addSelectionListener(new SelectionAdapter() {
			
			// Edit our requirement
			public void widgetSelected(SelectionEvent e) {
				RequirementInformation altS = (RequirementInformation) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				if (altS == null)
				{
					return;
				}
				Requirement req = new Requirement();
				req.fromDatabase(altS.getName());
				RationaleUpdateEvent evt2 = new RationaleUpdateEvent(this);
				evt2.fireUpdateEvent(req, shell.getDisplay(), UpdateType.FIND);
				
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
	
	/**
	 * Return the column names in a collection
	 * 
	 * @return List  containing column names
	 */
	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}
	
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}
	
	public Vector getAltList() {
		return reqInfoList;	
	}
	
	public Control getControl() {
		return table.getParent();
	}
	
	public Button getCloseButton() {
		return closeButton;
	}
}