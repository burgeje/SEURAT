/*
 * Created on May 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.queries;

import java.util.Enumeration;
import java.util.Vector;

//import org.eclipse.jface.viewers.DoubleClickEvent;
//import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.ui.ISelectionListener;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Claim;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * This display shows all the different arguments (arguments, claims) where their default
 * importance has been overriden.
 * @author jburge
 */
public class FindImportanceOverrides {
	
	/**
	 * The shell
	 */
	private Shell shell;
	/**
	 * A list of arguments
	 */
	private List argumentList;
	/**
	 * A list of claims
	 */
	private List claimList;
	
	/**
	 * The index of a selected element
	 */
	int selectionIndex;
	
	/**
	 * Display all the importance overrides
	 * @param disp the display
	 */
	public FindImportanceOverrides(Display display) {
		
		class MyAdapter implements MouseListener {
			
			public void mouseDoubleClick(MouseEvent e) {
			}
			
			public void mouseDown(MouseEvent e) {
			}
			
			public void mouseUp(MouseEvent e) {
			}
		}
		selectionIndex = 0;
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Importance Overrides");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText("Arguments:");
		new Label(shell, SWT.NONE).setText(" ");
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText("Claims:");
		new Label(shell, SWT.NONE).setText(" ");
		
		//left-hand column: Arguments
		argumentList = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		argumentList.addMouseListener(new MyAdapter(){
			public void mouseDoubleClick(MouseEvent e){
				String name = argumentList.getItem(argumentList.getSelectionIndex());
				RationaleElement ele = 
					RationaleDB.getRationaleElement(name, RationaleElementType.ARGUMENT);
				boolean canceled = ele.display(shell.getDisplay());
				if (!canceled)
				{
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
					
					argumentList.removeAll();
					RationaleDB db = RationaleDB.getHandle();
					Vector overStatus = db.getOverridenArguments();
					Enumeration listE = overStatus.elements();
					
					while (listE.hasMoreElements())
					{
						argumentList.add( ((Argument) listE.nextElement()).toString());
					}			
				}
			}
		});
		
		
		
		RationaleDB db = RationaleDB.getHandle();
		
		Vector overridenStatus = db.getOverridenArguments();
		Enumeration listE = overridenStatus.elements();
		
		while (listE.hasMoreElements())
		{
			argumentList.add( ((Argument) listE.nextElement()).toString());
		}
		
		int listHeight = argumentList.getItemHeight() * 8;
		Rectangle trim = argumentList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		argumentList.setLayoutData(gridData);
		
		
		//list 2: claims
		
		claimList = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		
		claimList.addMouseListener(new MyAdapter(){
			public void mouseDoubleClick(MouseEvent e){
				String name = claimList.getItem(claimList.getSelectionIndex());
				RationaleElement ele = 
					RationaleDB.getRationaleElement(name, RationaleElementType.CLAIM);
				boolean canceled = ele.display(shell.getDisplay());
				if (!canceled)
				{
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
					
					//update our list?
					claimList.removeAll();
					RationaleDB db = RationaleDB.getHandle();
					Vector overStatus = db.getOverridenClaims();
					Enumeration listE = overStatus.elements();
					
					while (listE.hasMoreElements())
					{
						claimList.add( ((Claim) listE.nextElement()).toString());
					}
				}
			}
		});
		
		overridenStatus = db.getOverridenClaims();
		listE = overridenStatus.elements();
		
		while (listE.hasMoreElements())
		{
			claimList.add( ((Claim) listE.nextElement()).toString());
		}
		
		//yes, re-using argument here is deliberate!
		listHeight = argumentList.getItemHeight() * 8;
		trim = argumentList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		claimList.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText(" ");
		
		Button editArgB = new Button(shell, SWT.PUSH);
		editArgB.setText("Edit");
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING| GridData.HORIZONTAL_ALIGN_FILL);
		
		editArgB.setLayoutData(gridData);
		editArgB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				String name = argumentList.getItem(argumentList.getSelectionIndex());
				RationaleElement ele = 
					RationaleDB.getRationaleElement(name, RationaleElementType.ARGUMENT);
				boolean canceled = ele.display(shell.getDisplay());
				if (!canceled)
				{
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
					
					argumentList.removeAll();
					RationaleDB db = RationaleDB.getHandle();
					Vector overStatus = db.getOverridenArguments();
					Enumeration listE = overStatus.elements();
					
					while (listE.hasMoreElements())
					{
						argumentList.add( ((Argument) listE.nextElement()).toString());
					}
				}
				
			}
		});
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		Button editClaimB = new Button(shell, SWT.PUSH);
		editClaimB.setText("Edit");
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING| GridData.HORIZONTAL_ALIGN_FILL);
		
		editClaimB.setLayoutData(gridData);
		editClaimB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				String name = claimList.getItem(claimList.getSelectionIndex());
				RationaleElement ele = 
					RationaleDB.getRationaleElement(name, RationaleElementType.CLAIM);
				boolean canceled = ele.display(shell.getDisplay());
				if (!canceled)
				{
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
					
					//update our list?
					claimList.removeAll();
					RationaleDB db = RationaleDB.getHandle();
					Vector overStatus = db.getOverridenClaims();
					Enumeration listE = overStatus.elements();
					
					while (listE.hasMoreElements())
					{
						claimList.add( ((Claim) listE.nextElement()).toString());
					}
				}
				
			}
		});
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Exit");
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
