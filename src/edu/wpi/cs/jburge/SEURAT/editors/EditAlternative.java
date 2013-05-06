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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * This class provides the editor for alternatives
 * @author burgeje
 *
 */
public class EditAlternative extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2645812944558249119L;
	
	/**
	 * The GUI shell
	 */
	private Shell shell;
	
	/**
	 * The alternative being edited
	 */
	private Alternative ourAlt;
	/**
	 * The name of the alternative
	 */
	private Text nameField;
	
	/**
	 * The description field
	 */
	private Text descArea;
//	private boolean newItem;
	
	/**
	 * Button to add the alternative
	 */
	private Button addButton;
	/**
	 * Button to cancel the edits
	 */
	private Button cancelButton;
	
//	private Button enableButton;
	
	/**
	 * Combo box to select status
	 */
	private Combo statusBox;
	
	/**
	 * Combo box to select designer
	 */
	private Combo designerBox;
	
	/**
	 * Combo box to select contingencies
	 */
	private Combo contingencyBox;
	
	/**
	 * The artifacts associated with this alternative
	 */
	private List artifacts;
	
	/**
	 * The relationships to other alternatives - displayed in a list
	 */
	private List relationships;
	
	/**
	 * Arguments for - displayed in a list
	 */
	private List forModel;
	
	/**
	 * Arguments against - displayed in a list
	 */
	private List againstModel;
	
	
	// private MessageBox errorBox; 		//dialog for the error message
	
	
	/**
	 * Constructor for editing alternatives
	 * @param display - points back to the display
	 * @param editAlt - the alternative being edited (or an empty one if new)
	 * @param newItem - true if the alternative is new
	 */
	public EditAlternative(Display display, Alternative editAlt, boolean newItem)
	{
		super();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Alternative Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourAlt = editAlt;
		
		if (newItem)
		{
			ourAlt.setStatus(AlternativeStatus.ATISSUE); 	
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 AlternativeInferences inf = new AlternativeInferences();
		 Vector newStat = inf.updateAlternative(ourAlt);
		 } */
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourAlt.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 100);
		gridData.horizontalSpan = 2;
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Designer:");
		
		if (newItem)
		{
			RationaleDB db = RationaleDB.getHandle();
			designerBox = new Combo(shell, SWT.NONE);
			designerBox.select(0);
			Vector ourDesigners = db.getNameList(RationaleElementType.DESIGNER);
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
			DisplayUtilities.setComboDimensions(designerBox, gridData, 50);
			gridData.horizontalSpan = 2;
			designerBox.setLayoutData(gridData);
			
		}
		else
		{
			if (ourAlt.getDesigner() != null)
			{
				Text desField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.READ_ONLY);
				desField.setText(ourAlt.getDesigner().getName());
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
				gridData.horizontalSpan = 2;
				DisplayUtilities.setTextDimensions(desField, gridData, 80);
				desField.setLayoutData(gridData);
				
			}
			else
			{
				new Label(shell, SWT.NONE).setText("Unknwn");
				new Label(shell, SWT.NONE).setText("");
			}
		}
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourAlt.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 75, 3);
		gridData.horizontalSpan = 5;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		
		
		new Label(shell, SWT.NONE).setText("Status:");
		statusBox = new Combo(shell, SWT.NONE);
		Enumeration statEnum = AlternativeStatus.elements();
		int j=0;
		AlternativeStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (AlternativeStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(ourAlt.getStatus().toString()) == 0)
			{
//				System.out.println(ourAlt.getStatus().toString());
				statusBox.select(j);
//				System.out.println(j);
			}
			j++;
		}
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setComboDimensions(statusBox, gridData, 50);
		gridData.horizontalSpan = 2;
		statusBox.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Design Type:");
		
		RationaleDB db = RationaleDB.getHandle();
		contingencyBox = new Combo(shell, SWT.NONE);
		Vector ourConts = db.getNameList(RationaleElementType.CONTINGENCY);
		contingencyBox.select(0);
		int cindex = 0;
		if (ourConts != null)
		{
			Enumeration desEnum = ourConts.elements();
			while (desEnum.hasMoreElements())
			{
				String des = (String) desEnum.nextElement();
				if (des.compareTo("Design-Contingencies") != 0)
				{
					contingencyBox.add( des );	
					if (ourAlt.getContingency() != null)
					{
						if (des.compareTo(ourAlt.getContingency().getName()) == 0)
						{
							contingencyBox.select(cindex);
						}							
					}
					
					cindex ++;
				}
			}
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setComboDimensions(contingencyBox, gridData, 50);
		gridData.horizontalSpan = 2;
		contingencyBox.setLayoutData(gridData);
		
		
		new Label(shell, SWT.NONE).setText("Artifact:");
		
		artifacts = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		
		Iterator artI = ourAlt.getArtifacts().iterator();
		int ndata = 1;
		while (artI.hasNext())
		{
			artifacts.add((String) artI.next());
			ndata++;
		}
		
		if (ndata > 1)
			ndata--;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setListDimensions(artifacts, gridData, ndata, 100);
		gridData.horizontalSpan = 2;
		
		artifacts.setLayoutData(gridData);
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		
		new Label(shell, SWT.NONE).setText("Arguments For");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		
		new Label(shell, SWT.NONE).setText("Arguments Against");
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		
		forModel = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		Vector listV = ourAlt.getArgumentsFor();
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			forModel.add( ((Argument) listE.nextElement()).toString());
		}    
		// add a list of arguments against to the right side
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setListDimensions(forModel, gridData, 2, 100);
		gridData.horizontalSpan = 3;
		
		forModel.setLayoutData(gridData);
		
		
		againstModel = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		listV = ourAlt.getArgumentsAgainst();
		listE = listV.elements();
		while (listE.hasMoreElements())
		{
			againstModel.add( ((Argument) listE.nextElement()).toString());
		}  
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		DisplayUtilities.setListDimensions(againstModel, gridData, 2, 100);
		
		againstModel.setLayoutData(gridData);
		
		//now, a row for relationships
		new Label(shell, SWT.NONE).setText(" ");
		Label relL = new Label (shell, SWT.NONE);
		relL.setText("Relationships");
		gridData = new GridData(GridData.CENTER);
		gridData.horizontalSpan = 4;
		relL.setLayoutData(gridData);
		new Label(shell, SWT.NONE).setText(" ");
		
		new Label(shell, SWT.NONE).setText(" ");
		relationships = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		listV = ourAlt.getRelationships();
		listE = listV.elements();
		while (listE.hasMoreElements())
		{
			relationships.add( ((Argument) listE.nextElement()).toString());
		}    
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		DisplayUtilities.setListDimensions(relationships, gridData, 4, 100);
		
		relationships.setLayoutData(gridData);
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
					canceled = false;
					if (!nameField.getText().trim().equals(""))
					{
						ConsistencyChecker checker = new ConsistencyChecker(ourAlt.getID(), nameField.getText(), "Alternatives");
						
						if(ourAlt.getName() == nameField.getText() || checker.check())
						{
							if (((designerBox.getItemCount() <= 0) || designerBox.getSelectionIndex() >= 0)
									&& ( contingencyBox.getItemCount() <= 0 || contingencyBox.getSelectionIndex() >= 0))
							{
								ourAlt.setName(nameField.getText());
								ourAlt.setDescription(descArea.getText());
								ourAlt.setStatus( AlternativeStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
								ourAlt.updateHistory(new History(ourAlt.getStatus().toString(), "Initial Entry"));
								//comment before this made no sense...
								//				System.out.println("Saving alternative from edit");
								
								if (designerBox.getItemCount() > 0)
								{
									String designerName = designerBox.getItem(designerBox.getSelectionIndex());
									Designer ourDes = new Designer();
									ourDes.fromDatabase(designerName);
									ourAlt.setDesigner(ourDes);
								}
								
								if (contingencyBox.getItemCount() > 0)
								{
									String contName = contingencyBox.getItem(contingencyBox.getSelectionIndex());
									Contingency ourCont = new Contingency();
									ourCont.fromDatabase(contName);
									ourAlt.setContingency(ourCont);
								}
								
								ourAlt.setID(ourAlt.toDatabase(ourAlt.getParent(), ourAlt.getPtype()));
								
								shell.close();
								shell.dispose();	
							}
							else
							{
								MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
								mbox.setMessage("Need to provide both Designer Name and Design Type");
								mbox.open();
							}
						}
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Alternative name");
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
					boolean changeOk = true;
					canceled = false;
					
					ConsistencyChecker checker = new ConsistencyChecker(ourAlt.getID(), nameField.getText(), "Alternatives");
					
					if(ourAlt.getName() == nameField.getText() || checker.check())
					{
						ourAlt.setName(nameField.getText());
						ourAlt.setDescription(descArea.getText());
						if (contingencyBox.getItemCount() > 0)
						{
							String contName = contingencyBox.getItem(contingencyBox.getSelectionIndex());
							Contingency ourCont = new Contingency();
							ourCont.fromDatabase(contName);
							ourAlt.setContingency(ourCont);
						}
						AlternativeStatus newStat = AlternativeStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
						if (!newStat.toString().equals(ourAlt.getStatus().toString()))
						{
							//check to see if the alternative was rejected earlier
							boolean wasRejected = false;
							
							Enumeration histE = ourAlt.getHistory();
							while (histE.hasMoreElements())
							{
								History hist = (History) histE.nextElement();
								if (hist.getStatus().compareTo(AlternativeStatus.REJECTED.toString()) == 0)
								{
									wasRejected = true;
								}
							}
							if ((wasRejected) && (newStat == AlternativeStatus.ADOPTED))
							{
								changeOk = showQuestion("This alternative was rejected earlier. Select anyway?");
							}
							if (changeOk)
							{
								ReasonGUI rg = new ReasonGUI();
								String newReason = rg.getReason();
								ourAlt.setStatus(newStat);
								//					System.out.println(newStat.toString() + ourAlt.getStatus().toString());
								History newHist = new History(newStat.toString(), newReason);
								ourAlt.updateHistory(newHist);
								//					ourAlt.toDatabase(ourAlt.getParent(), RationaleElementType.fromString(ourAlt.getPtype()));
								//					newHist.toDatabase(ourAlt.getID(), RationaleElementType.Alternative);
							}
						}
						if (changeOk)
						{
							//since this is a save, not an add, the type and parent are ignored
							//				System.out.println("Saving alternative from edit-update");
							ourAlt.setID(ourAlt.toDatabase(ourAlt.getParent(), ourAlt.getPtype()));
							
							//				RationaleDB db = RationaleDB.getHandle();
							//				db.addAlternative(ourAlt);
							
							shell.close();
							shell.dispose();
						}
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
	 * returns the item just created
	 */
	public RationaleElement getItem()
	{
		return ourAlt;
	}
	
	/** 
	 * Used to ask the user questions when saving the alternative. 
	 * These could be status questions (to supply a history) or ask for confirmation
	 * if they try to select a previously rejected alternative
	 * @param message
	 * @return the response to the dialog
	 */
	private boolean showQuestion(String message) {
		return MessageDialog.openQuestion(
				shell,
				"Save Alternative",
				message);
	}
	
	
}






