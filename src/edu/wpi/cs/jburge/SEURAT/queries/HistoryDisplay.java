/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */

package edu.wpi.cs.jburge.SEURAT.queries;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;


import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

//import edu.wpi.cs.jburge.SEURAT.inference.RequirementInferences;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.ArgType;
////import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.History;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
//import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;

/**
 * Displays a table with the history for a specific rationale element. Like other
 * tables in SEURAT, this is based off the example described in an Eclipse Corner
 * article. More information can be found in the RationaleTaskList code.  
 */
public class HistoryDisplay {
	
	/**
	 * Our shell
	 */
	private Shell shell;
	/**
	 * The table containing the information
	 */
	private Table table;
	/**
	 * The viewer for our table
	 */
	private TableViewer tableViewer;
	/**
	 * The button that closes this display
	 */
	private Button closeButton;
	
	/**
	 * A vector containing our history information
	 */
	private Vector histList; 
	
	
	/**
	 * The name of our colums
	 */
	private final String DATE_COLUMN 		= "Date";
	private final String STATUS_COLUMN 	        = "Status";
	private final String REASON_COLUMN 			= "Reason";
	
	// Set column names
	private String[] columnNames = new String[] { 
			DATE_COLUMN, 
			STATUS_COLUMN,
			REASON_COLUMN
	};
	
	
	/**
	 * The constructor
	 * @param parent - the composit the widgets live in
	 * @param itemName - the name of the element we're displaying history for
	 * @param type - the type of element we're displaying history for
	 */
	public HistoryDisplay(Composite parent, String itemName, RationaleElementType type) {
		
		shell = new Shell();
		shell.setText("History Display");
		
		RationaleElement ele = RationaleDB.getRationaleElement(itemName, type);
		histList = ele.getHistoryV();
		
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
		tableViewer.setContentProvider(new HistoryContentProvider());
		tableViewer.setLabelProvider(new HistoryLabelProvider());
		// The input for the table viewer is the instance of ExampleargList
//		argList = new ExampleargList();
		tableViewer.setInput(histList);
		
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
		
		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText(DATE_COLUMN);
		column.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HistorySorter(HistorySorter.DATE));
			}
		});
		
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(STATUS_COLUMN);
		column.setWidth(80);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HistorySorter(HistorySorter.STATUS));
			}
		});
		
		
		// 3rd column with task Owner
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(REASON_COLUMN);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HistorySorter(HistorySorter.REASON));
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
		
		// Set the default sorter for the viewer - we'll sort by date
		tableViewer.setSorter(new HistorySorter(HistorySorter.DATE));
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
	 * InnerClass that acts as a proxy for the HistoryList 
	 * providing content for the Table. 
	 */
	class HistoryContentProvider implements IStructuredContentProvider  {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			/*			if (newInput != null)
			 ((HistoryList) newInput).addChangeListener(this);
			 if (oldInput != null)
			 ((HistoryList) oldInput).removeChangeListener(this);
			 */		}
		
		public void dispose() {
//			argList.removeChangeListener(this);
		}
		
		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return histList.toArray();
		}
		
		
		/* (non-Javadoc)
		 * @see IargListViewer#addTask(ExampleTask)
		 */
		public void addTask(History task) {
			tableViewer.add(task);
		}
		
		/* (non-Javadoc)
		 * @see IargListViewer#removeTask(ExampleTask)
		 */
		/*		public void removeTask(History task) {
		 tableViewer.remove(task);			
		 } */
		
		/* (non-Javadoc)
		 * @see IargListViewer#updateTask(ExampleTask)
		 */
		/*		public void updateTask(ExampleTask task) {
		 tableViewer.update(task, null);	
		 } */
	}
	
	class HistoryLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			History task = (History) element;
			switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN		
				Date ourDate = task.getDateStamp();	
				result = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(ourDate);
				
				break;
			case 1 :
				result = task.getStatus();
				break;
			case 2 :
				result = task.getReason();
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
	 * Sort our table when the appropriate column is clicked
	 * @author burgeje
	 *
	 */
	class HistorySorter extends ViewerSorter {
		
		/**
		 * Constructor argument values that indicate to sort items by 
		 * description, owner or percent complete.
		 */
		public final static int DATE 		= 1;
		public final static int STATUS 				= 2;
		public final static int REASON 	= 3;
		
		// Criteria that the instance uses 
		private int criteria;
		
		/**
		 * Creates a resource sorter that will use the given sort criteria.
		 *
		 * @param criteria the sort criterion to use
		 */
		public HistorySorter(int criteria) {
			super();
			this.criteria = criteria;
		}
		
		/* (non-Javadoc)
		 * Method declared on ViewerSorter.
		 */
		public int compare(Viewer viewer, Object o1, Object o2) {
			
			History task1 = (History) o1;
			History task2 = (History) o2;
			
			switch (criteria) {
			case DATE :
				return compareDate(task1.getDateStamp(), task2.getDateStamp());
			case STATUS :
				return compareText(task1.getStatus(), task2.getStatus());
			case REASON :
				return compareText(task1.getReason(), task2.getReason());
			default:
				return 0;
			}
		}
		
		
		/**
		 * Returns a number reflecting the collation order of two text strings
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
		 * Compares two dates
		 * @param d1 the first date
		 * @param d2 the second date
		 * @return the result of the date comparision (see the Date class, .compareTo)
		 */
		protected int compareDate(Date d1, Date d2) {
			return d1.compareTo(d2);
		}
		
		public int getCriteria() {
			return criteria;
		}
	} //end inner class
	
	/**
	 * Add the Close buttons
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
		
		//	Create and configure the "Close" button
		closeButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		closeButton.setText("Close");
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80; 
		closeButton.setLayoutData(gridData); 
		
	}
	
	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}
	
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}
	
	public Vector gethistList() {
		return histList;	
	}
	
	public Control getControl() {
		return table.getParent();
	}
	
	
	public Button getCloseButton() {
		return closeButton;
	}
}