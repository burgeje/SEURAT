package edu.wpi.cs.jburge.SEURAT.editors;


import java.io.Serializable;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Candidate Rationale editor - Candidates are temporary rationale that require formalization
 * @author burgeje
 *
 */
public class EditCandidateRationale extends NewRationaleElementGUI implements Serializable {
	
	private static final long serialVersionUID = 7684998496991891462L;
	
	private Shell shell;
	
	private CandidateRationale ourCandidate;
	
	private Text nameField;
	
	private Text descArea;
//	private boolean newItem;
	
	private Button addButton;
	private Button cancelButton;
	
	// private MessageBox errorBox; 		//dialog for the error message
	
	
	
	public EditCandidateRationale(Display display, CandidateRationale editCandidate, boolean newItem)
	{
		super();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Candidate Rationale Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourCandidate = editCandidate;
		
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourCandidate.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		DisplayUtilities.setTextDimensions(nameField, gridData, 150);
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourCandidate.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData,100, 2);
		gridData.horizontalSpan = 2;
		descArea.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Source:");
		Text srcArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		srcArea.setText(ourCandidate.getSource());
		srcArea.setEditable(false);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(srcArea, gridData,100, 2);
		gridData.horizontalSpan = 2;
		srcArea.setLayoutData(gridData);
		
			
		addButton = new Button(shell, SWT.PUSH); 
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gridData);
		if (newItem)
		{
			addButton.setText("Add");
			addButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) 
				{
					//int typeIndex;
					//int statusIndex;
					canceled = false;
					if (!nameField.getText().trim().equals(""))
					{
						ConsistencyChecker checker = new ConsistencyChecker(ourCandidate.getID(), nameField.getText(), "Assumptions");
						
						if(ourCandidate.getName() == nameField.getText() || checker.check())
						{
							ourCandidate.setName(nameField.getText());
							ourCandidate.setDescription(descArea.getText());
							
//							comment before this made no sense...
							ourCandidate.toDatabase(ourCandidate.getParent(), true);
							shell.close();
							shell.dispose();
						}
						
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Candidate Rationale Element name");
						mbox.open();
					}
				}
			});
			
		}
		else
		{
			addButton.setText("Save");
			addButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) 
				{
					//int typeIndex;
					canceled = false;
					//int statusIndex;
					
					ConsistencyChecker checker = new ConsistencyChecker(ourCandidate.getID(), nameField.getText(), "Assumptions");
					
					if(ourCandidate.getName() == nameField.getText() || checker.check())
					{
						ourCandidate.setName(nameField.getText());
						ourCandidate.setDescription(descArea.getText());
						//since this is a save, not an add, the type and parent are ignored
						ourCandidate.toDatabase(ourCandidate.getParent(), false);
						
						//			RationaleDB db = RationaleDB.getHandle();
						//			db.addAssumption(ourAssump);
						
						shell.close();
						shell.dispose();
					}
					
				}
			});
		}
		
		
		
		cancelButton = new Button(shell, SWT.PUSH); 
		cancelButton.setText("Cancel");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				canceled = true;
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
	
	
	
	public RationaleElement getItem()
	{
		return ourCandidate;
	}
	
	
}







