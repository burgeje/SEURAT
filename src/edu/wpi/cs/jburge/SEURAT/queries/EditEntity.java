/*
 * Created on May 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.queries;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * This display is used to select a rationale entity to edit. A list of all the entities
 * of a specified type is displayed. The user can search for a specific one by name.
 * @author jburge
 */
public class EditEntity {
	
	
	/**
	 * The shell
	 */
	private Shell shell;
	/**
	 * The type of element we are selecting
	 */
	private RationaleElementType type;
	/**
	 * The box for search text
	 */
	private Text searchText;
	/**
	 * The list of entities
	 */
	private List entityList;
	/**
	 * The selection text
	 */
	String selectionString;
	/**
	 * The index into the list found when selecting
	 */
	int selectionIndex;
	
	
	/**
	 * Display our editEntity display
	 * @param display - points to the display
	 * @param eType - the type of entity we are looking for
	 */
	public EditEntity(Display display, RationaleElementType eType) {
		
		//set up our display
//		display = new Display();
		type = eType;
		
		selectionIndex = 0;
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select " + type.toString());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
//		new Label(shell, SWT.NONE).setText("Entity Type:");
		entityList = new List(shell, SWT.SINGLE | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 5;
		
		RationaleDB db = RationaleDB.getHandle();
		Vector listV = db.getNameList(type);
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			entityList.add( (String) listE.nextElement());
		}
		
		int listHeight = entityList.getItemHeight() * 12;
		Rectangle trim = entityList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		entityList.setLayoutData(gridData);
		
		//now, our search controls
		new Label(shell, SWT.NONE).setText("Search:");	
		
		//and our text to enter
		searchText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		searchText.setLayoutData(gridData);	
		
		new Label(shell, SWT.NONE).setText(" ");	
		new Label(shell, SWT.NONE).setText(" ");	
		
		Button searchB = new Button(shell, SWT.PUSH);
		searchB.setText("Search");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		Point buttonSize = searchB.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//		gridData.horizontalSpan = 3;
		searchB.setLayoutData(gridData);
		searchB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event)
			{
				int limit = entityList.getItemCount();
//				System.out.println("number items = " + Integer.toString(limit));
				boolean found = false;
				int numSearched = 0;
				
				selectionString = searchText.getText();
				while ((!found) && (numSearched < limit))
				{
					String name = entityList.getItem(selectionIndex);
					if (name.lastIndexOf(selectionString) >= 0)
					{
//						System.out.println("name = " + name);
//						System.out.println("selection = " + selectionString);
//						System.out.println("comparison = " + name.lastIndexOf(selectionString));
						found = true;
						entityList.select(selectionIndex);
						entityList.setTopIndex(selectionIndex);
						selectionIndex++;
					}
					else
					{
						selectionIndex++;
					}
					if (selectionIndex >= limit)
					{
						selectionIndex = 0;
					}
					numSearched++;
				}
			}
		});
		
		new Label(shell, SWT.NONE).setText(" ");	
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");		
		new Label(shell, SWT.NONE).setText(" ");
		
		Button showB = new Button(shell, SWT.PUSH);
		showB.setText("Show");
//		editB.setSize(buttonSize);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING| GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalIndent = 5;
		gridData.widthHint = buttonSize.y;
		showB.setLayoutData(gridData);
		showB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				String name = entityList.getItem(entityList.getSelectionIndex());
				RationaleElement ele = 
					RationaleDB.getRationaleElement(name, type);
				RationaleUpdateEvent evt2 = new RationaleUpdateEvent(this);
				evt2.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.FIND);
				
				//shell.close();
				//shell.dispose();
				
			}
		});				
		Button editB = new Button(shell, SWT.PUSH);
		editB.setText("Edit");
//		editB.setSize(buttonSize);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING| GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalIndent = 5;
		gridData.widthHint = buttonSize.y;
		editB.setLayoutData(gridData);
		editB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				String name = entityList.getItem(entityList.getSelectionIndex());
				RationaleElement ele = 
					RationaleDB.getRationaleElement(name, type);
//				RationaleUpdateEvent evt2 = new RationaleUpdateEvent(this);
//				evt2.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.FIND);
				
				boolean canceled = ele.display(shell.getDisplay());
				
				if (!canceled)
				{
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
					shell.close();
					shell.dispose();
				}
				
			}
		});
		
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Cancel");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		//	cancelB.setSize(searchB.getSize());
//		gridData2.horizontalIndent = 5;
		cancelB.setLayoutData(gridData2);
		cancelB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				shell.dispose();
				
			}
		});
		
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
}
