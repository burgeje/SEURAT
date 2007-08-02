
package edu.wpi.cs.jburge.SEURAT.editors;

import java.awt.Frame;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Requirement editor. Used to create or modify requirements.
 * @author burgeje
 *
 */
public class EditRequirement extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4465147457834959423L;
	private Shell shell;
	
	/**
	 * The requirement being edited
	 */
	private Requirement ourReq;
	
	/**
	 * The name of the requirement
	 */
	private Text nameField;
	/**
	 * The artifact associated with the requirement (could be a requirement number that
	 * points back to an SRS)
	 */
	private Text artifactField;
	/**
	 * The description (text) of the requirement
	 */
	private Text descArea;
//	private boolean newItem;
	/**
	 * Button for adding the requirement
	 */
	private Button addButton;
	/**
	 * Button for canceling the edit
	 */
	private Button cancelButton;
	/**
	 * Check box to enable/disable the requirement
	 */
	private Button enableButton;
	
	/**
	 * The type of requirement - functional or non-functional
	 */  
	private Combo typeBox;
	/**
	 * The status of the requiement (violated, addressed, etc.)
	 */
	private Combo statusBox;
	
	/**
	 * Arguments for the requirement
	 */
	private List forModel;
	/**
	 * Arguments against the requirement
	 */
	private List againstModel;
	
	/**
	 * Constructor to display the requirement editor
	 * @param display - points back to the display
	 * @param editReq - the requirement being edited
	 * @param newItem - true if this is a new requirement
	 */
	public EditRequirement(Display display, Requirement editReq, boolean newItem)
	{
		super();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Requirement Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourReq = editReq;
		
		if (newItem)
		{
			ourReq.setType(ReqType.FR);
			ourReq.setStatus(ReqStatus.UNDECIDED);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 RequirementInferences inf = new RequirementInferences();
		 Vector newStat = inf.updateRequirement(ourReq);
		 } */
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourReq.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourReq.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Type:");
		
		
		typeBox = new Combo(shell, SWT.NONE);
		Enumeration typeEnum = ReqType.elements();
//		System.out.println("got enum");
		int i = 0;
		ReqType rtype;
		while (typeEnum.hasMoreElements())
		{
			rtype = (ReqType) typeEnum.nextElement();
//			System.out.println("got next element");
			typeBox.add( rtype.toString());
			if (rtype.toString().compareTo(ourReq.getType().toString()) == 0)
			{
//				System.out.println(ourReq.getType().toString());
				typeBox.select(i);
//				System.out.println(i);
			}
			i++;
		}
		typeBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		new Label(shell, SWT.NONE).setText("Status:");
		statusBox = new Combo(shell, SWT.NONE);
		Enumeration statEnum = ReqStatus.elements();
		int j=0;
		ReqStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (ReqStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(ourReq.getStatus().toString()) == 0)
			{
//				System.out.println(ourReq.getStatus().toString());
				statusBox.select(j);
//				System.out.println(j);
			}
			j++;
		}
		
		new Label(shell, SWT.NONE).setText("Artifact:");
		
		artifactField = new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		if (ourReq.getArtifact() != null)
		{
			artifactField.setText(ourReq.getArtifact());
		}
		else
		{
			artifactField.setText("");
		}
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		artifactField.setLayoutData(gridData);
		
		
		new Label(shell, SWT.NONE).setText("Arguments For");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		
		new Label(shell, SWT.NONE).setText("Arguments Against");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		forModel = new List(shell, SWT.SINGLE | SWT.V_SCROLL);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		int listHeight = forModel.getItemHeight() * 4;
		Rectangle trim = forModel.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		Vector listV = ourReq.getArgumentsFor();
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			forModel.add( (String) listE.nextElement());
		}    
		// add a list of arguments against to the right side
		forModel.setLayoutData(gridData);
		
		
		againstModel = new List(shell, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		listHeight = againstModel.getItemHeight() * 4;
		Rectangle rtrim = againstModel.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = rtrim.height;
		
		listV = ourReq.getArgumentsAgainst();
		listE = listV.elements();
		while (listE.hasMoreElements())
		{
			againstModel.add( ((String) listE.nextElement()));
		}    
		againstModel.setLayoutData(gridData);
		
		enableButton = new Button(shell, SWT.CHECK);
		enableButton.setText("Enabled");
		enableButton.setSelection(ourReq.getEnabled());
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		enableButton.setLayoutData(gridData);
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
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
						ConsistencyChecker checker = new ConsistencyChecker(ourReq.getID(), nameField.getText(), "Requirements");
						
						if(ourReq.getName() == nameField.getText() || checker.check())
						{
							ourReq.setName(nameField.getText());
							ourReq.setDescription(descArea.getText());
							ourReq.setType(ReqType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));
							ourReq.setStatus( ReqStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
							ourReq.setArtifact( artifactField.getText());
							ourReq.updateHistory(new History(ourReq.getStatus().toString(), "Initial Entry"));
							ourReq.setEnabled(enableButton.getSelection());
							
							//comment before this made no sense...
							ourReq.setID(ourReq.toDatabase(ourReq.getParent(), RationaleElementType.fromString(ourReq.getPtype())));
							
							shell.close();
							shell.dispose();
						}
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Requirement name");
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
					
					ConsistencyChecker checker = new ConsistencyChecker(ourReq.getID(), nameField.getText(), "Requirements");
					
					if(ourReq.getName() == nameField.getText() || checker.check())
					{
						ourReq.setName(nameField.getText());
						ourReq.setDescription(descArea.getText());
						ourReq.setType(ReqType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));
						ReqStatus newStat = ReqStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
						ourReq.setArtifact(artifactField.getText());
						ourReq.setEnabled(enableButton.getSelection());
						if (!newStat.toString().equals(ourReq.getStatus().toString()))
						{
							Frame rf = new Frame();
							ReasonGUI rg = new ReasonGUI(rf);
							rg.setVisible(true);
							String newReason = rg.getReason();
							ourReq.setStatus(newStat);
							//				System.out.println(newStat.toString() + ourReq.getStatus().toString());
							History newHist = new History(newStat.toString(), newReason);
							ourReq.updateHistory(newHist);
							//				ourReq.toDatabase(ourReq.getParent(), RationaleElementType.fromString(ourReq.getPtype()));
							//				newHist.toDatabase(ourReq.getID(), RationaleElementType.REQUIREMENT);
						}
//						since this is a save, not an add, the type and parent are ignored
						ourReq.setID(ourReq.toDatabase(ourReq.getParent(), RationaleElementType.fromString(ourReq.getPtype())));
						
						
						//			RationaleDB db = RationaleDB.getHandle();
						//			db.addRequirement(ourReq);
						
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
	
	
	/**
	 * Get our requirement
	 */  
	public RationaleElement getItem()
	{
		return ourReq;
	}
	
	
}






