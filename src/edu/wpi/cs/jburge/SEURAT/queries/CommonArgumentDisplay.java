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
import java.util.Vector;


import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * The common argument display class displays a table of common arguments.
 * This was written using the TableViewer example from the Eclipse Corner
 * article about creating a table viewer. See the tasks package for more information. 
 * This is just a table though, not a table viewer. 
 */

public class CommonArgumentDisplay {
	
	/**
	 * The shell
	 */
	private Shell shell;
	/**
	 * The table of data
	 */
	private Table table;
	/**
	 * The name of the type of argument we are looking at
	 */
	private String typeName;
	/**
	 * The viewer
	 */
	private TableViewer tableViewer;
	/**
	 * A button to close the window
	 */
	private Button closeButton;
	
	/**
	 * The arguments
	 */
	private Vector argList; 
	
	
	/**
	 * The names of our table columns
	 */
	private final String ARGUMENT_COLUMN 		= "Argument";
	private final String TOTAL_COLUMN 	        = "Total";
	private final String FOR_COLUMN 			= "For";
	private final String AGAINST_COLUMN 		= "Against";
	
	// Set column names
	private String[] columnNames = new String[] { 
			ARGUMENT_COLUMN, 
			TOTAL_COLUMN,
			FOR_COLUMN,
			AGAINST_COLUMN
	};
	
	
	/**
	 * The constructor of our display
	 * @param parent - the composite
	 * @param typeName - the name of the argument type
	 * @param argList - the arguments being displayed
	 */
	public CommonArgumentDisplay(Composite parent, String typeName, Vector argList) {
		
		
		
		this.argList = argList;
		
		shell = new Shell();
		shell.setText("Common Argument Display");
		
		// Set layout for shell
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		
		this.typeName = typeName;
		
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
		tableViewer.setContentProvider(new CommonArgumentContentProvider());
		tableViewer.setLabelProvider(new CommonArgumentLabelProvider());
		tableViewer.setInput(argList);
		
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
		
		int listHeight = table.getItemHeight() * 6;
		Rectangle trim = table.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText(typeName);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new CommonArgumentSorter(CommonArgumentSorter.ARGUMENT));
			}
		});
		
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(TOTAL_COLUMN);
		column.setWidth(40);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new CommonArgumentSorter(CommonArgumentSorter.TOTAL));
			}
		});
		
		
		// 3rd column with task Owner
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(FOR_COLUMN);
		column.setWidth(40);
		// Add listener to column so tasks are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new CommonArgumentSorter(CommonArgumentSorter.FOR));
			}
		});
		
		// 4th column with task PercentComplete 
		column = new TableColumn(table, SWT.CENTER, 3);
		column.setText(AGAINST_COLUMN);
		column.setWidth(40);
		//  Add listener to column so tasks are sorted by percent when clicked
		column.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new CommonArgumentSorter(CommonArgumentSorter.AGAINST));
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
		
	}
	
	/**
	 * Close the window and dispose of resources
	 */
	public void close() {
		Shell shell = table.getShell();
		
		if (shell != null && !shell.isDisposed())
			shell.dispose();
	}
	
	
	/**
	 * InnerClass that acts as a proxy for the CommonArgumentList 
	 * providing content for the Table. 
	 */
	class CommonArgumentContentProvider implements IStructuredContentProvider  {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
//			argList.removeChangeListener(this);
		}
		
		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return argList.toArray();
		}
		
		/* (non-Javadoc)
		 * @see IargListViewer#addTask(ExampleTask)
		 */
		public void addTask(CommonArgument task) {
			tableViewer.add(task);
		}
		
	}
	
	class CommonArgumentLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {
		
		/**
		 * Get the text in the requested column
		 * @param element - the argument
		 * @param columnIndex - its index
		 */
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			CommonArgument task = (CommonArgument) element;
			switch (columnIndex) {
			case 0:  // COMPLETED_COLUMN
				result = task.getArgumentName();
				break;
			case 1 :
				result = task.getTotal();
				break;
			case 2 :
				result = task.getForCount();
				break;
			case 3 :
				result = task.getAgainstCount();;
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
	
	class CommonArgumentSorter extends ViewerSorter {
		
		/**
		 * Constructor argument values that indicate to sort items by 
		 * description, owner or percent complete.
		 */
		public final static int ARGUMENT 		= 1;
		public final static int TOTAL 				= 2;
		public final static int FOR 	= 3;
		public final static int AGAINST = 4;
		
		// Criteria that the instance uses 
		private int criteria;
		
		/**
		 * Creates a resource sorter that will use the given sort criteria.
		 *
		 * @param criteria the sort criterion to use: one of <code>NAME</code> or 
		 *   <code>TYPE</code>
		 */
		public CommonArgumentSorter(int criteria) {
			super();
			this.criteria = criteria;
		}
		
		/* (non-Javadoc)
		 * Method declared on ViewerSorter.
		 */
		public int compare(Viewer viewer, Object o1, Object o2) {
			
			CommonArgument task1 = (CommonArgument) o1;
			CommonArgument task2 = (CommonArgument) o2;
			
			switch (criteria) {
			case ARGUMENT :
				return compareText(task1.getArgumentName(), task2.getArgumentName());
			case TOTAL :
				return compareNumbers(Integer.parseInt(task1.getTotal()), Integer.parseInt(task2.getTotal()));
			case FOR :
				return compareNumbers(Integer.parseInt(task1.getForCount()), Integer.parseInt(task2.getForCount()));
			case AGAINST :
				return compareNumbers(Integer.parseInt(task1.getAgainstCount()), Integer.parseInt(task2.getAgainstCount()));				
			default:
				return 0;
			}
		}
		
		/**
		 * Returns a number reflecting the collation order of the given tasks
		 * based on the percent completed.
		 *
		 * @param n1
		 * @param n2
		 * @return a negative number if the first element is less  than the 
		 *  second element; the value zero if the first element is
		 *  equal to the second element; and a positive number if the first
		 *  element is greater than the second element
		 */
		private int compareNumbers(int n1, int n2) {
			int result = n2 - n1;
			result = result < 0 ? -1 : (result > 0) ? 1 : 0;  
			return result;
		}
		
		/**
		 * Returns a number reflecting the collation order of two textx
		 * strings
		 * @param str1 the first string
		 * @param str2 the second string
		 * @return a negative number if the first element is less  than the 
		 *  second element; the value 0if the first element is
		 *  equal to the second element; and a positive number if the first
		 *  element is greater than the second element
		 */
		protected int compareText(String str1, String str2) {
			return collator.compare(str1, str2);
		}
		
		public int getCriteria() {
			return criteria;
		}
	} //end inner class
	
	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * @param parent the parent composite
	 */
	private void createButtons(Composite parent) {
		
		new Label(parent, SWT.NONE).setText(" ");
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Edit");
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
//		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {
			
			// Edit our argument
			public void widgetSelected(SelectionEvent e) {
				CommonArgument arg = (CommonArgument) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				RationaleElementType type = arg.getType();
				RationaleElement ele = 
					RationaleDB.getRationaleElement(arg.getArgumentName(), type);
				boolean canceled = ele.display(shell.getDisplay());
				if (!canceled)
				{
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
				}
				shell.close();
				shell.dispose();
				
			}
		});
		
		//	Create and configure the "Close" button
		closeButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		closeButton.setText("Close");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.widthHint = 80; 
		closeButton.setLayoutData(gridData); 
		
	}
	
	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}
	
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}
	
	public Vector getArgList() {
		return argList;	
	}
	
	public Control getControl() {
		return table.getParent();
	}
	
	public Button getCloseButton() {
		return closeButton;
	}
}