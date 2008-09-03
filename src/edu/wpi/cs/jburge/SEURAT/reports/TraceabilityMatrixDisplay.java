/**
 * Team 1, Release 1- October 17, 2007
 * Revised- October 23, 2007
 */
package edu.wpi.cs.jburge.SEURAT.reports;

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
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;

/**
 * Used to display the requirements traceability matrix, including
 * requirements, alternatives, relationship type, and associated code.
 * 
 * Built using the existing SEURAT.queries display classes as a base.
 * 
 * @author molerjc
 */
public class TraceabilityMatrixDisplay {
	
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
	 * Our traceability elements.
	 */
	private Vector<TraceabilityElement> matrix; 
	
	/**
	 * Our column table names
	 */
	private final String REQUIREMENT_COLUMN		= "Requirement";
	private final String ALTERNATIVE_COLUMN 	= "Alternative";
	private final String RELATIONSHIP_COLUMN 	= "Relationship";
	private final String ARTIFACT_COLUMN		= "Associated Artifact";
	
	// Set column names
	private String[] columnNames = new String[] { 
			REQUIREMENT_COLUMN,
			ALTERNATIVE_COLUMN, 
			RELATIONSHIP_COLUMN,
			ARTIFACT_COLUMN
	};
	
	
	/**
	 * Constructs the traceability matrix display.
	 * 
	 * @param parent - the composite
	 */
	public TraceabilityMatrixDisplay(Composite parent) {
		
		shell = new Shell();
		shell.setText("Requirements Traceability Matrix Display");
		
		matrix = new Vector<TraceabilityElement>();
		// Find the data for our matrix
		
		RationaleDB db = RationaleDB.getHandle();
		Vector<Requirement> reqs = db.getEnabledRequirements(true);
		matrix.addAll(createList(reqs));

		// Set layout for shell
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		
		// Create a composite to hold the children
		Composite composite = new Composite(shell, SWT.NONE);
		this.addChildControls(composite);
		
		
		shell.pack();
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
		tableViewer.setContentProvider(new TraceabilityMatrixContentProvider());
		tableViewer.setLabelProvider(new TraceabilityMatrixLabelProvider());
		// The input for the table viewer is the instance of ExampleargList
//		argList = new ExampleargList();
		tableViewer.setInput(matrix);
		
		// sort before displaying
		tableViewer.setSorter(new TraceabilityMatrixSorter(TraceabilityMatrixSorter.REQUIREMENT));
		
		// Add the buttons
		createButtons(composite);
	}
	
	/**
	 * Creates the table that will contain the actual requirements and their data.
	 * Also provides sorters for the columns.
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
		column.setText(REQUIREMENT_COLUMN);
		column.setWidth(120);
		// Add listener to column so tasks are sorted by requirement when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TraceabilityMatrixSorter(TraceabilityMatrixSorter.REQUIREMENT));
			}
		});
		
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(ALTERNATIVE_COLUMN);
		column.setWidth(120);
		// Add listener to column so tasks are sorted by alternative when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TraceabilityMatrixSorter(TraceabilityMatrixSorter.ALTERNATIVE));
			}
		});
		
		
		// 3rd column with task Owner
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(RELATIONSHIP_COLUMN);
		column.setWidth(120);
		// Add listener to column so tasks are sorted by relationship when clicked
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TraceabilityMatrixSorter(TraceabilityMatrixSorter.RELATIONSHIP));
			}
		});
		
		// 4th column with task Owner
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(ARTIFACT_COLUMN);
		column.setWidth(140);
		// Add listener to column so tasks are sorted by artifact when clicked
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TraceabilityMatrixSorter(TraceabilityMatrixSorter.CODE));
			}
		});
		
	}
	
	/**
	 * Creates the TableViewer, which is used to view the table.
	 */
	private void createTableViewer() {
		
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);
		
		// Set the default sorter for the viewer 
		tableViewer.setSorter(new TraceabilityMatrixSorter(TraceabilityMatrixSorter.RELATIONSHIP));
	}
	
	/**
	 * Closes the window and dispose of resources.
	 */
	public void close() {
		Shell shell = table.getShell();
		
		if (shell != null && !shell.isDisposed())
			shell.dispose();
	}
	
	/**
	 * Gets our list of traceability elements by getting arguments, alternatives,
	 * and artifacts for the appropriate requirements.
	 * @param reqs - the requirements
	 * @return the relationships
	 */
	private Vector<TraceabilityElement> createList(Vector<Requirement> reqs)
	{
		Vector<TraceabilityElement> ourList = new Vector<TraceabilityElement>();
		RequirementInferences inf = new RequirementInferences();
		Iterator<Requirement> reqI = reqs.iterator();
		boolean curReq = false;
		while (reqI.hasNext())
		{
			Requirement req = reqI.next();
			Vector<Argument> args = inf.getArguments(req);
			Iterator<Argument> argI = args.iterator();
			while (argI.hasNext()) {
				Argument arg = argI.next();
				if (arg.getPtype() == RationaleElementType.ALTERNATIVE)	{
					Alternative alt = new Alternative();
					alt.fromDatabase(arg.getParent());
					if (alt.getStatus() == AlternativeStatus.ADOPTED) {
						// Return artifacts separated by commas
						Iterator artI = alt.getArtifacts().iterator();
						String art = "";
						if (artI.hasNext()) art = artI.next().toString();
						while (artI.hasNext()) {
							art+=", "+artI.next().toString();
						}
						TraceabilityElement rel = new TraceabilityElement(req.getName(), alt.getName(),
								arg.getType().toString(), art);
						ourList.add(rel);
						curReq = true;
					}
				}
			}
			if (!curReq) {
				// add a blank element with just the requirement name
				TraceabilityElement rel = new TraceabilityElement(req.getName(), "",
						"", "");
				ourList.add(rel);
			} else {
				curReq = false;
			}
		}
		return ourList;
	}
	
	/** 
	 * Simple inner class that represents a traceability element.
	 * 
	 * @author molerjc
	 */
	class TraceabilityElement {
		String req;
		String alt;
		String rel;
		String art;
		
		// Empty constructor
		TraceabilityElement() {}
		
		// Constructor taking all elements
		TraceabilityElement(String rq, String a, String r, String c) {
			req = rq;
			alt = a;
			rel = r;
			art = c;
		}
		
		// Getters
		public String getReq() {
			return req;
		}
		
		public String getAlt() {
			return alt;
		}
		
		public String getRel() {
			return rel;
		}
		
		public String getArt() {
			return art;
		}
		
		// Setters
		public void setReq(String req) {
			this.req = req;
		}
		
		public void setAlt(String alt) {
			this.alt = alt;
		}
		
		public void setRel(String rel) {
			this.rel = rel;
		}
		
		public void setArt(String art) {
			this.art = art;
		}
		
		// toString method
		public String toString() {
			return req + "\t" + alt + "\t" + rel + "\t" + art + "\n";
		}
	}
	
	/**
	 * Inner class that acts as a proxy for the TraceabilityMatrixList 
	 * providing content for the Table.
	 */
	class TraceabilityMatrixContentProvider implements IStructuredContentProvider  {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
//			argList.removeChangeListener(this);
		}
		
		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return matrix.toArray();
		}
		
		public void addTask(TraceabilityElement task) {
			tableViewer.add(task);
		}
		
		
	}
	
	class TraceabilityMatrixLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {
		
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			TraceabilityElement task = (TraceabilityElement) element;
			switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN
				result = task.getReq();
				break;
			case 1 :
				result = task.getAlt();
				break;
			case 2 :
				result = task.getRel();
				break;
			case 3 :
				result = task.getArt();
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
	 * Used to sort our traceability matrix when we click on a column header.
	 * Adapted from the sorter in the Requirement Relationship Display.
	 * 
	 * @author molerjc
	 *
	 */
	class TraceabilityMatrixSorter extends ViewerSorter {
		
		/**
		 * Constructor argument values that indicate whether we are sorting
		 * by requirement, alternative, relationship, or code (artifact)
		 */
		public final static int REQUIREMENT  = 1;
		public final static int ALTERNATIVE  = 2;
		public final static int RELATIONSHIP = 3;
		public final static int CODE 	     = 4;
		
		// Criteria that the instance uses 
		private int criteria;
		
		/**
		 * Creates a resource sorter that will use the given sort criteria.
		 *
		 * @param criteria the sort criterion to use
		 */
		public TraceabilityMatrixSorter(int criteria) {
			super();
			this.criteria = criteria;
		}
		
		
		/**
		 * Compares two traceability elements
		 * 
		 * @param viewer our viewer
		 * @param o1 the first object
		 * @param o2 the second object
		 * @return the comparision results
		 */
		public int compare(Viewer viewer, Object o1, Object o2) {
			
			TraceabilityElement task1 = (TraceabilityElement) o1;
			TraceabilityElement task2 = (TraceabilityElement) o2;
			
			switch (criteria) {
			case REQUIREMENT :
				return compareText(task1.getReq(), task2.getReq());
			case ALTERNATIVE :
				return compareText(task1.getAlt(), task2.getAlt());
			case RELATIONSHIP :
				return compareText(task1.getRel(), task2.getRel());
			case CODE :
				return compareText(task1.getArt(), task2.getArt());
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
			// We want blanks to go to the bottom
			if (str1.equals("")) {
				return 1;
			} else if (str2.equals("")) {
				return -1;
			}
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
	 * Add the "Output" and "Close" buttons
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Output");
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
			
			// Output this traceability matrix
			public void widgetSelected(SelectionEvent e) {
				//String[] newArray = new String[matrix.size()];
				//newArray = matrix.toArray(newArray);
				//String output = matrix.toString().replace("[", "").replace("]","").replace(", ","");
				//System.out.println(output);
				OutputTraceabilityMatrix om = new OutputTraceabilityMatrix(shell, matrix);
				
				// what if they want to output multiple times or to multiple locations?
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
	
	public Vector<TraceabilityElement> getElementList() {
		return matrix;	
	}
	
	public Control getControl() {
		return table.getParent();
	}
	
	public Button getCloseButton() {
		return closeButton;
	}
}