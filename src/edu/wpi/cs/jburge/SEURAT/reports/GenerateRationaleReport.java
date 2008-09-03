/**
 * Team 1, Release 1- October 17, 2007
 */
package edu.wpi.cs.jburge.SEURAT.reports;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import edu.wpi.cs.jburge.SEURAT.views.TreeParent;


/**
 * Confirms the users requested report and allows them to select what
 * type of report they would like
 * 
 * 
 * @author wagnerrd
 */
public class GenerateRationaleReport {
	
	/**
	 *  our shell
	 */
	private Shell shell;
	/**
	 * The selection on the RationaleExplorer
	 */
	private Object sel;
	/**
	 * Find out what type of rationale report they want
	 */
	Combo formatSelector;
	Text previewPane;
	RationaleReportGenerator rrg;
	/**
	 * Controls whether or not Argument-Ontology entries are included in the report
	 */
	boolean argOnt = false;

	/**
	 * Constructs The Rationale Report Generator Preview Window And
	 * ALlows the user to accept or reject the generated rationale report
	 * before sending it to a file.
	 * 
	 * @param display the window manager wrapper interface which can be used
	 * 				to construct the Rationale Report Preview Window
	 * @param selection The Select Nodes In The Rationale Tree View
	 */
	public GenerateRationaleReport(Display display, Object selection) {
		sel = selection;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Generate Rationale Report");
		
		rrg = null; // initialize rrg to be null
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		Label report = new Label(shell, SWT.NONE);
		report.setText("Report root: "+selection);
		GridData ourGrid = new GridData();
		ourGrid.horizontalSpan = 2;
		report.setLayoutData(ourGrid);
		
//		new Label(shell, SWT.NONE).setText("Report root: "+selection);
		new Label(shell, SWT.NONE).setText("Output Format:");		
		formatSelector = new Combo(shell, SWT.READ_ONLY);
	    formatSelector.add("Plain");
	    formatSelector.select(0); // select plain by default
	    
	    IStructuredSelection isel = (IStructuredSelection) sel;
	    TreeParent tp = (TreeParent)isel.getFirstElement();
	    // if the root is an ontology entry, don't display the ontology entry checkbox
	    if (tp.getType().toString().equals("Ontology Entry")) {
	    	argOnt = true;
	    } else {
	    	Button argontchk = new Button(shell, SWT.CHECK);
	    	argontchk.setText("Include Argument-Ontology Entries");
	    	argontchk.setLayoutData(ourGrid);
	    	argontchk.addSelectionListener(new SelectionAdapter(){
	    		
	    		public void widgetSelected(SelectionEvent event){
	    			argOnt = !argOnt;
	    		}
				
	    	});
	    	new Label(shell, SWT.NONE).setText("");
		    new Label(shell, SWT.NONE).setText("");
	    }
		Button onlySel = new Button(shell, SWT.PUSH);
		onlySel.setText("Preview");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		onlySel.setLayoutData(gridData2);
		onlySel.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				if(formatSelector.getSelectionIndex() == RationaleReportGenerator.PLAIN){
					rrg = new RationaleReportGenerator(shell, sel, RationaleReportGenerator.PLAIN,argOnt);
					PreviewRationaleReport pr = new PreviewRationaleReport(shell, rrg.getReport());
					
				}else if(formatSelector.getSelectionIndex() == -1){ //none selected
					
				}
			}
		});
		
		Button save = new Button(shell, SWT.PUSH);
		save.setText("Save Report");
		save.setLayoutData(gridData2);
		save.addSelectionListener(new SelectionAdapter(){
			
			public void widgetSelected(SelectionEvent event){
				if(formatSelector.getSelectionIndex() == RationaleReportGenerator.PLAIN){
					rrg = new RationaleReportGenerator(shell, sel, RationaleReportGenerator.PLAIN, argOnt);
				}
				    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				    dialog.setFilterNames(new String[] { "Text Files" });
				    dialog.setFilterExtensions(new String[] { "*.txt" });
				    dialog.setFileName("Rationale Report.txt");
				    try{
						FileWriter fios = new FileWriter(dialog.open());
						fios.write(rrg.getReport());
						fios.close();
					}catch(IOException ioe){
						//alert user
						MessageDialog.openError(shell, "Error while saving file!", "The file could not be saved.");
					}
					//once the file is written, close the window!
					shell.close();
					shell.dispose();		

			}
			
			
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		GridData gridData3 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		cancel.setLayoutData(gridData3);
		cancel.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				shell.dispose();
				
			}
		});
		
		cancel.setSize(onlySel.getSize());
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
}
