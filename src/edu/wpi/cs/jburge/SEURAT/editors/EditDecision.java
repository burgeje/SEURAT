
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

package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Displays the editor for a decision
 * @author burgeje
 *
 */
public class EditDecision extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Shell shell;
	/**
	 * The decision being edited
	 */	
	private Decision ourDec;
	/**
	 * The name of the decision
	 */  
	private Text nameField;
	// private Text artifactField;
	/**
	 * A description of the decision
	 */
	private Text descArea;
//	private boolean newItem;
	/**
	 * A button to add the decision
	 */
	private Button addButton;
	/**
	 * A button to cancel edits
	 */
	private Button cancelButton;
	/**
	 * A button to specify sub-decisions
	 */
	private Button subDecButton;
	
	/**
	 * The decision type
	 */  
	private Combo typeBox;
	/**
	 * The decision status
	 */
	private Combo statusBox;
	/**
	 * The development phase where the decision is made
	 */
	private Combo phaseBox;
	/**
	 * The designer making the decision
	 */
	private Combo designerBox;
	/**
	 * A list of alternatives
	 */
	private List altModel;
	
	
	// private MessageBox errorBox; 		//dialog for the error message
	
	
	/**
	 * Edit our new decision
	 * @param	display - points back to the display
	 * @param editDec - the decision being created/edited
	 * @param newItem - true if this is a new decision
	 */  
	public EditDecision(Display display, Decision editDec, boolean newItem)
	{
		super();
		//	shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell = new Shell();
		shell.setText("Decision Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourDec = editDec;
		
		if (newItem)
		{
			ourDec.setType(DecisionType.SINGLECHOICE);
			ourDec.setStatus(DecisionStatus.UNRESOLVED);
			ourDec.setPhase(Phase.DESIGN);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 DecisionInferences inf = new DecisionInferences();
		 Vector newStat = inf.updateDecision(ourDec);
		 } */
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		nameField.setText(ourDec.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;
		DisplayUtilities.setTextDimensions(nameField, gridData, 80);
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Designer:");
		
		if (newItem)
		{
			RationaleDB db = RationaleDB.getHandle();
			designerBox = new Combo(shell, SWT.H_SCROLL);
			Vector ourDesigners = db.getNameList(RationaleElementType.DESIGNER);
			designerBox.select(0);
			
			if (ourDesigners != null)
			{
				Enumeration desEnum = ourDesigners.elements();
				while (desEnum.hasMoreElements())
				{
					String des = (String) desEnum.nextElement();
					if (des.compareTo("Designer-Profiles") != 0)
					{
						designerBox.add( des );					
					}
				}
			}
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			designerBox.setLayoutData(gridData);
			DisplayUtilities.setComboDimensions(designerBox, gridData, 100);
		}
		else
		{
			if (ourDec.getDesigner() != null)
			{
				Text desField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.READ_ONLY);
				desField.setText(ourDec.getDesigner().getName());
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
				gridData.horizontalSpan = 1;
				DisplayUtilities.setTextDimensions(desField, gridData, 80);
				desField.setLayoutData(gridData);		}
			else
			{
				new Label(shell, SWT.NONE).setText("Unknown");
			}
		}
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourDec.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.heightHint = descArea.getLineHeight() * 3;
		DisplayUtilities.setTextDimensions(descArea, gridData, 75, 5);
		descArea.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Type:");
		
		
		typeBox = new Combo(shell, SWT.NONE);
		Enumeration typeEnum = DecisionType.elements();
//		System.out.println("got enum");
		int i = 0;
		DecisionType rtype;
		while (typeEnum.hasMoreElements())
		{
			rtype = (DecisionType) typeEnum.nextElement();
//			System.out.println("got next element");
			typeBox.add( rtype.toString());
			if (rtype.toString().compareTo(ourDec.getType().toString()) == 0)
			{
//				System.out.println(ourDec.getType().toString());
				typeBox.select(i);
//				System.out.println(i);
			}
			i++;
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);	
		DisplayUtilities.setComboDimensions(typeBox, gridData, 100);
		typeBox.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Status:");
		statusBox = new Combo(shell, SWT.NONE);
		Enumeration statEnum = DecisionStatus.elements();
		int j=0;
		DecisionStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (DecisionStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(ourDec.getStatus().toString()) == 0)
			{
//				System.out.println(ourDec.getStatus().toString());
				statusBox.select(j);
//				System.out.println(j);
			}
			j++;
		}
		
		
		new Label(shell, SWT.NONE).setText("DevelopmentPhase:");
		
		phaseBox = new Combo(shell, SWT.NONE);
		Enumeration phaseEnum = Phase.elements();
		int k=0;
		Phase ptype;
		while (phaseEnum.hasMoreElements())
		{
			ptype = (Phase) phaseEnum.nextElement();
			phaseBox.add( ptype.toString() );
			if (ptype.toString().equals(ourDec.getPhase().toString()))
			{
//				System.out.println(ourDec.getPhase().toString());
				phaseBox.select(k);
			}
			k++;
		}
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		gridData.horizontalSpan = 1;
		phaseBox.setLayoutData(gridData);
		
		subDecButton = new Button(shell, SWT.CHECK);
		subDecButton.setText("Sub-Decisions Required");
		subDecButton.setSelection(!ourDec.getAlts());
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		subDecButton.setLayoutData(gridData);
//		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
		
		
		
		new Label(shell, SWT.NONE).setText("(Evaluation) Alternatives:");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		
		new Label(shell, SWT.NONE).setText("");
//		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
		
		altModel = new List(shell, SWT.SINGLE | SWT.V_SCROLL);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 6;
		int listHeight = altModel.getItemHeight() * 4;
		Rectangle trim = altModel.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		Iterator altI = ourDec.getAlternatives().iterator();
		while (altI.hasNext())
		{
			Alternative alt = (Alternative) altI.next();
			String altDesc = alt.getName();
			altDesc = "(" + Double.toString(alt.getEvaluation()) + ") " + altDesc; 
			altModel.add( altDesc);
		}    
		// add a list of arguments against to the right side
		altModel.setLayoutData(gridData);
		
		
		
//		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
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
					canceled = false;
					
					if (!nameField.getText().trim().equals(""))
					{
						ConsistencyChecker checker = new ConsistencyChecker(ourDec.getID(), nameField.getText(), "Decisions");
						
						if(ourDec.getName() == nameField.getText() || checker.check())
						{
							if ((designerBox.getItemCount() <= 0) || designerBox.getSelectionIndex() >= 0)
							{
								ourDec.setName(nameField.getText());
								ourDec.setDescription(descArea.getText());
								ourDec.setType(DecisionType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));
								ourDec.setStatus( DecisionStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
								//				ourDec.setArtifact( artifactField.getText());
								ourDec.setPhase(Phase.fromString(phaseBox.getItem(phaseBox.getSelectionIndex())));
								ourDec.updateHistory(new History(ourDec.getStatus().toString(), "Initial Entry"));
								ourDec.setAlts(!subDecButton.getSelection());
								
								if (designerBox.getItemCount() > 0) {
									String designerName = designerBox
									.getItem(designerBox.getSelectionIndex());
									Designer ourDes = new Designer();
									ourDes.fromDatabase(designerName);
									ourDec.setDesigner(ourDes);
								}
								//					comment before this made no sense...
								ourDec.setID(ourDec.toDatabase(ourDec.getParent(), ourDec.getPtype()));
								System.out.println("our ID = " + ourDec.getID());
								shell.close();
								shell.dispose();
								
							}
							else
							{
								MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
								mbox.setMessage("Need to provide the Designer name");
								mbox.open();
							}
						}
						
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Decision name");
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
					canceled = false;
					
					ConsistencyChecker checker = new ConsistencyChecker(ourDec.getID(), nameField.getText(), "Decisions");
					
					if(ourDec.getName() == nameField.getText() || checker.check())
					{
						ourDec.setName(nameField.getText());
						ourDec.setDescription(descArea.getText());
						ourDec.setType(DecisionType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));
						DecisionStatus newStat = DecisionStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
						ourDec.setAlts(!subDecButton.getSelection());
						ourDec.setPhase(Phase.fromString(phaseBox.getItem(phaseBox.getSelectionIndex())));
						if (!newStat.toString().equals(ourDec.getStatus().toString()))
						{
							ReasonGUI rg = new ReasonGUI();
							//				rg.show();
							String newReason = rg.getReason();
							ourDec.setStatus(newStat);
							//				System.out.println(newStat.toString() + ourDec.getStatus().toString());
							History newHist = new History(newStat.toString(), newReason);
							ourDec.updateHistory(newHist);
							//				ourDec.toDatabase(ourDec.getParent(), RationaleElementType.fromString(ourDec.getPtype()));
							//				newHist.toDatabase(ourDec.getID(), RationaleElementType.Decision);
						}
						//since this is a save, not an add, the type and parent are ignored
						ourDec.setID(ourDec.toDatabase(ourDec.getParent(), ourDec.getPtype()));
						
						
						//			RationaleDB db = RationaleDB.getHandle();
						//			db.addDecision(ourDec);
						
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
	 * Get our new decision
	 */  
	public RationaleElement getItem()
	{
		return ourDec;
	}
}






