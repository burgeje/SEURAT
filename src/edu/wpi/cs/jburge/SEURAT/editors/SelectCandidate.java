/*
 * Created on January 6, 2008
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;

//import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
//import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
//import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * Presents the user with a list of rationale elements of a specific type to
 * select from. The user also has the option of searching for an element in the list
 * and can add new items.
 * @author jburge
 */
public class SelectCandidate {
	
	/**
	 * Points back to the display
	 */
	private Display ourDisplay;
	/**
	 * Points to the shell
	 */
	private Shell shell;
	/**
	 * The type of rationale element that is being selected
	 */
	private RationaleElementType type;
	/**
	 * The text box where the user can enter a search string
	 */
	private Text searchText;
	/**
	 * The list of entities that the user can select from
	 */
	private List entityList;
	
	/**
	 * The string typed into the text box (searchText)
	 */
	String selectionString;
	/**
	 * The index to the selected item
	 */
	int selectionIndex;
	
	/**
	 * The selected item
	 */
	private RationaleElement ourItem;
	
	
	/**
	 * Displays a list of rationale elements that the user can select from
	 * @param display - points to the display
	 * @param eType - the **child** rationale element type
	 */
	public SelectCandidate(Display display, RationaleElementType eType) {
		
		//set up our display
		ourDisplay = display;
		type = eType;
		
		ourItem = null;
		selectionIndex = 0;
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select New Parent Item");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
//		new Label(shell, SWT.NONE).setText("Entity Type:");
		entityList = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 5;
		
		RationaleDB db = RationaleDB.getHandle();
		Vector listV = db.getCandidateList(type);
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			entityList.add( (String) listE.nextElement());
		}
		
//		int listHeight = entityList.getItemHeight() * 12;
//		Rectangle trim = entityList.computeTrim(0, 0, 0, listHeight);
//		gridData.heightHint = trim.height;
		DisplayUtilities.setListDimensions(entityList, gridData, 12, 80);
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
		

			new Label(shell, SWT.NONE).setText(" ");
		Button editB = new Button(shell, SWT.PUSH);
		editB.setText("Select");
//		editB.setSize(buttonSize);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING| GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalIndent = 5;
		gridData.widthHint = buttonSize.y;
		editB.setLayoutData(gridData);
		editB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				String name = entityList.getItem(entityList.getSelectionIndex());
				ourItem  = RationaleDB.getRationaleElement(name, RationaleElementType.CANDIDATE);				
				shell.close();
				shell.dispose();
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
	
	/**
	 * Get the item that has been selected.
	 * @return the selected rationale element
	 */
	public RationaleElement getNewItem()
	{
		return ourItem;
	}
	
}
