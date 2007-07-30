/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 * Modified 25 October, 2003 by Janet Burge for use in the SEURAT
 * plugin
 */

package edu.wpi.cs.jburge.SEURAT.tasks;
import java.util.Arrays;


import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 * The RationaleTaskListView implements the Rationale Task List that
 * lists the rationale status elements. 
 * 
 * This code is based on the Eclipse Corner article 
 * "Building and delivering a table editor with SWT/JFace"
 * http://www.eclipse.org/articles/Article-Table-viewer/table_viewer.html
 * and on the example attached to the article:
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Jun 11, 2003 by lgauthier@opnworks.com
 */

public class RationaleTaskListView implements ISelectionProvider {

	public RationaleTaskListView(Composite parent) {
		
		this.addChildControls(parent);
	}

	/**
	 * The table
	 */
	private Table table;
	/**
	 * The viewer
	 */
	private TableViewer tableViewer;
	

	/**
	 * Our task list - this is the data model we are using to get our
	 * status. 
	 */
	private RationaleTaskList taskList = RationaleTaskList.getHandle(); 

	/**
	 * The names of our table columns
	 */
	private final String STATUS_COLUMN 		= "status";
	private final String DESCRIPTION_COLUMN 	= "description";
	private final String RATIONALE_COLUMN 			= "rationale";
	private final String TYPE_COLUMN 		= "type";

	// Set column names
	private String[] columnNames = new String[] { 
			STATUS_COLUMN, 
			DESCRIPTION_COLUMN,
			RATIONALE_COLUMN,
			TYPE_COLUMN
			};

	/**
	 * Main method to launch the window 
	 */
	public static void main(String[] args) {

		Shell shell = new Shell();
		shell.setText("Rationale Task List");

		// Set layout for shell
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		
		// Create a composite to hold the children
		Composite composite = new Composite(shell, SWT.NONE);
		final RationaleTaskListView tableViewerExample = new RationaleTaskListView(composite);
		
		tableViewerExample.getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				tableViewerExample.dispose();			
			}
			
		});

		// Ask the shell to display its content
		shell.open();
		tableViewerExample.run(shell);
	}

	/**
	 * Run and wait for a close event
	 * @param shell Instance of Shell
	 */
	private void run(Shell shell) {
		
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
		tableViewer.setContentProvider(new ExampleContentProvider());
		tableViewer.setLabelProvider(new RationaleTaskLabelProvider());
		// The input for the table viewer is the instance of RationaleTaskList
		taskList = RationaleTaskList.getHandle();
		tableViewer.setInput(taskList);

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
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column 
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText(" ");
		column.setWidth(20);
		// Add listener to column so tasks are sorted by status when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RationaleTaskSorter(RationaleTaskSorter.STATUS));
			}	
		});	
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Description");
		column.setWidth(400);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RationaleTaskSorter(RationaleTaskSorter.DESCRIPTION));
			}
		});


		// 3rd column with the rationale element
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Rationale");
		column.setWidth(200);
		// Add listener to column so tasks are sorted by element when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RationaleTaskSorter(RationaleTaskSorter.RATIONALE));
			}
		});

		// 4th column with rationale type 
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Type");
		column.setWidth(200);
		//  Add listener to column so tasks are sorted by type when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RationaleTaskSorter(RationaleTaskSorter.RATIONALE_TYPE));
			}
		});
	}

	/**
	 * Create the TableViewer. The RationaleTaskList table is much simpler
	 * than the table example!
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);
		// Set the default sorter for the viewer 
		tableViewer.setSorter(new RationaleTaskSorter(RationaleTaskSorter.DESCRIPTION));
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
	 * InnerClass that acts as a proxy for the RationaleTaskList 
	 * providing content for the Table. It implements the IRationaleTaskListViewer 
	 * interface since it must register changeListeners with the 
	 * ExampleTaskList 
	 */
	class ExampleContentProvider implements IStructuredContentProvider, IRationaleTaskListViewer {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((RationaleTaskList) newInput).addChangeListener(this);
			if (oldInput != null)
				((RationaleTaskList) oldInput).removeChangeListener(this);
		}

		public void dispose() {
			taskList.removeChangeListener(this);
		}

		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return taskList.getTasks().values().toArray();
		}

		/* (non-Javadoc)
		 * @see ITaskListViewer#addTask(ExampleTask)
		 */
		public void addTask(RationaleTask task) {
			tableViewer.add(task);
		}

		/* (non-Javadoc)
		 * @see ITaskListViewer#removeTask(ExampleTask)
		 */
		public void removeTask(RationaleTask task) {
			tableViewer.remove(task);			
		}

		/* (non-Javadoc)
		 * @see ITaskListViewer#updateTask(ExampleTask)
		 */
		public void updateTask(RationaleTask task) {
			tableViewer.update(task, null);	
		}
	}
	
	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}
	
	//why would we set it?
	public void setSelection(ISelection sel)
	{
	}

	public RationaleTaskList getTaskList() {
		return taskList;	
	}

	public Control getControl() {
		return table.getParent();
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		//why would we need to do this?
	}
	
	public void addSelectionChangedListener (ISelectionChangedListener listener)
	{
	}
	
	public TableViewer getTableViewer()
	{
		return tableViewer;
	}
	
}
