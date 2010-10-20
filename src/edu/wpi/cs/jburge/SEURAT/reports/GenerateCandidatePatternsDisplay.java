package edu.wpi.cs.jburge.SEURAT.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.SelectCandidatePatterns;
import edu.wpi.cs.jburge.SEURAT.inference.AlternativePatternInferences;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

public class GenerateCandidatePatternsDisplay {

	private Shell shell;
	
	private Display ourDisplay;
	
	private String theDecision;
	
	private Display subDisplay;

	private Button exact;
	
	private List results;;
	
	private Button contribution;
	
	private Button cancelButton;
	
	private Button[] matchMethods;
	
	private Button[] patternTypes;
	
	private Button[] architectualPatternProblemCategories;
	
	private Button allArchiProbCategories;
	
	private Button[] designPatternProblemCategories;
	
	private Button allDesignProbCategories;
	
	private Link[] archiProbHelp;
	
	private Link[] designProbHelp;
	
	private Object sel;
	
	protected boolean canceled;
	
	private Hashtable candidates_Exact;
	
	private Hashtable candidates_Contribution;
	
	private ArrayList<Alternative> newlyAddedAlternative;
	
	public GenerateCandidatePatternsDisplay(Display display, Object selection) {
		sel = selection;
		IStructuredSelection isel = (IStructuredSelection) sel;
	    TreeParent tp = (TreeParent)isel.getFirstElement();
		theDecision = tp.getName();		

		ourDisplay = display;
		//shell = new Shell();
		shell = new Shell(ourDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Generating Candidate Patterns");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		GridData gridData8 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData8.horizontalSpan = 8;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.grabExcessVerticalSpace = true;
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.verticalAlignment = GridData.FILL;
		
		GridData gridData7 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData7.horizontalSpan = 7;
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.grabExcessVerticalSpace = true;
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.verticalAlignment = GridData.FILL;
		
		GridData gridData1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData1.horizontalSpan = 1;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("Select the matching approach:");
		label.setLayoutData(gridData8);
		
		matchMethods = new Button[3];		
		matchMethods[0] = new Button(shell, SWT.RADIO);
		matchMethods[0].setText("Exact Matching Patterns");
		matchMethods[0].setLayoutData(gridData7);
		Link exactMatchHelp = new Link(shell, SWT.NONE);
		exactMatchHelp.setText("<a>?</a>");
		exactMatchHelp.setToolTipText("What does it mean?");
		exactMatchHelp.setLayoutData(gridData1);
		exactMatchHelp.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "In exact matching approach, a pattern is matched with one NFR when the associated ontology of that NFR is one of the patterns affected attributes.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Exact Matching Patterns");
				mbox.open();
			}
		});
		
		matchMethods[1] = new Button(shell, SWT.RADIO);
		matchMethods[1].setText("Contribution Matching Patterns");
		matchMethods[1].setLayoutData(gridData7);
		
		Link contributionHelp = new Link(shell, SWT.NONE);
		contributionHelp.setText("<a>?</a>");
		contributionHelp.setToolTipText("What does it mean?");
		contributionHelp.setLayoutData(gridData1);
		contributionHelp.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "In contribution matching approach, a pattern is matched with one NFR when the associated ontology of that NFR is one of the patterns affected attributes or is a child of one of the affected attributes in the argument ontology.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Contribution Matching Patterns");
				mbox.open();
			}
		});
		
		matchMethods[2] = new Button(shell, SWT.RADIO);
		matchMethods[2].setText("Not matching NFRs");
		matchMethods[2].setLayoutData(gridData8);
		matchMethods[2].setSelection(true);
		
		Label seperation = new Label(shell, SWT.NONE);
		seperation.setText("---------------------------------------------------------------------------------------------");
		seperation.setLayoutData(gridData8);
		Label patternTypeLabel = new Label(shell, SWT.NONE);
		patternTypeLabel.setText("Specify candidate scope(pattern type):");
		patternTypeLabel.setLayoutData(gridData8);
		
		patternTypes = new Button[3];
		patternTypes[0] = new Button(shell, SWT.CHECK);
		patternTypes[0].setText("Architectual Pattern");
		patternTypes[0].setLayoutData(gridData8);
		patternTypes[0].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				if(patternTypes[0].getSelection()){				
					for(Button archiPatternProblem: architectualPatternProblemCategories){
						archiPatternProblem.setVisible(true);
					}
					for(Link archiHelp: archiProbHelp){
						archiHelp.setVisible(true);
					}
					allArchiProbCategories.setVisible(true);
				}else{
					for(Button archiPatternProblem: architectualPatternProblemCategories){
						archiPatternProblem.setVisible(false);
					}
					for(Link designHelp: archiProbHelp){
						designHelp.setVisible(false);
					}
					allArchiProbCategories.setVisible(false);
				}
			}
		});
		patternTypes[1] = new Button(shell, SWT.CHECK);
		patternTypes[1].setText("Design Pattern");
		patternTypes[1].setLayoutData(gridData8);
		patternTypes[1].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				if(patternTypes[1].getSelection()){
					for(Button designPatternProblem: designPatternProblemCategories){
						designPatternProblem.setVisible(true);
					}
					for(Link archiHelp: designProbHelp){
						archiHelp.setVisible(true);
					}
					allDesignProbCategories.setVisible(true);
				}else{
					for(Button designPatternProblem: designPatternProblemCategories){
						designPatternProblem.setVisible(false);
					}
					for(Link designHelp: designProbHelp){
						designHelp.setVisible(false);
					}
					allDesignProbCategories.setVisible(false);
				}
			}
		});
		patternTypes[2] = new Button(shell, SWT.CHECK);
		patternTypes[2].setText("Idiom");
		patternTypes[2].setLayoutData(gridData8);
		
		seperation = new Label(shell, SWT.NONE);
		seperation.setText("---------------------------------------------------------------------------------------------");
		seperation.setLayoutData(gridData8);
		Label problemCatagoriesLabel = new Label(shell, SWT.NONE);
		problemCatagoriesLabel.setText("Specify problem catagory:");
		problemCatagoriesLabel.setLayoutData(gridData8);
		
		GridData problemCatagoryGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		problemCatagoryGridData.horizontalSpan = 3;
		problemCatagoryGridData.grabExcessHorizontalSpace = true;
		problemCatagoryGridData.grabExcessVerticalSpace = true;
		problemCatagoryGridData.horizontalAlignment = GridData.FILL;
		problemCatagoryGridData.verticalAlignment = GridData.FILL;
		
		Label archiPatternType = new Label(shell, SWT.NONE);
		archiPatternType.setText("Architectural Pattern");
		archiPatternType.setLayoutData(problemCatagoryGridData);
		new Label(shell, SWT.NONE).setText("");
		
		Label designPatternType = new Label(shell, SWT.NONE);
		designPatternType.setText("Design Pattern");
		designPatternType.setLayoutData(problemCatagoryGridData);
		new Label(shell, SWT.NONE).setText("");
		
		architectualPatternProblemCategories = new Button[5];
		designPatternProblemCategories = new Button[6];
		archiProbHelp = new Link[4];
		designProbHelp = new Link[5];		
		
		architectualPatternProblemCategories[0] = new Button(shell, SWT.CHECK);
		architectualPatternProblemCategories[0].setText("From_Mud_To_Structure");
		architectualPatternProblemCategories[0].setLayoutData(problemCatagoryGridData);
		architectualPatternProblemCategories[0].setVisible(false);
		
		archiProbHelp[0] = new Link(shell, SWT.NONE);
		archiProbHelp[0].setText("<a>?</a>");
		archiProbHelp[0].setToolTipText("What does it mean?");
		archiProbHelp[0].setLayoutData(gridData1);
		archiProbHelp[0].setVisible(false);
		archiProbHelp[0].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "Patterns in this category help you to avoid a 'sea' of components or objects. In particular, they support a controlled decomposition of an overall system task into cooperating subtasks.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("From Mud to Structure");
				mbox.open();
			}
		});
		
		designPatternProblemCategories[0] = new Button(shell, SWT.CHECK);
		designPatternProblemCategories[0].setText("Structural_Decomposition");
		designPatternProblemCategories[0].setLayoutData(problemCatagoryGridData);
		designPatternProblemCategories[0].setVisible(false);
		
		designProbHelp[0] = new Link(shell, SWT.NONE);
		designProbHelp[0].setText("<a>?</a>");
		designProbHelp[0].setToolTipText("What does it mean?");
		designProbHelp[0].setLayoutData(gridData1);
		designProbHelp[0].setVisible(false);
		designProbHelp[0].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "This category includes patterns that support a suitable decomposition of subsystems and complex components into cooperating parts.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Structural Decomposition");
				mbox.open();
			}
		});
		
		architectualPatternProblemCategories[1] = new Button(shell, SWT.CHECK);
		architectualPatternProblemCategories[1].setText("Distributed_Systems");
		architectualPatternProblemCategories[1].setLayoutData(problemCatagoryGridData);
		architectualPatternProblemCategories[1].setVisible(false);
		
		archiProbHelp[1] = new Link(shell, SWT.NONE);
		archiProbHelp[1].setText("<a>?</a>");
		archiProbHelp[1].setToolTipText("What does it mean?");
		archiProbHelp[1].setLayoutData(gridData1);
		archiProbHelp[1].setVisible(false);
		archiProbHelp[1].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "A distributed system consists of multiple autonomous computers that communicate through a computer network.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Distributed Systems");
				mbox.open();
			}
		});
		
		designPatternProblemCategories[1] = new Button(shell, SWT.CHECK);
		designPatternProblemCategories[1].setText("Organization_Of_Work");
		designPatternProblemCategories[1].setLayoutData(problemCatagoryGridData);
		designPatternProblemCategories[1].setVisible(false);	
		
		designProbHelp[1] = new Link(shell, SWT.NONE);
		designProbHelp[1].setText("<a>?</a>");
		designProbHelp[1].setToolTipText("What does it mean?");
		designProbHelp[1].setLayoutData(gridData1);
		designProbHelp[1].setVisible(false);
		designProbHelp[1].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "This category comprised patterns that define how components collaborate together to solve a complex problem.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Organization of Work");
				mbox.open();
			}
		});
		
		architectualPatternProblemCategories[2] = new Button(shell, SWT.CHECK);
		architectualPatternProblemCategories[2].setText("Interactive_Systems");
		architectualPatternProblemCategories[2].setLayoutData(problemCatagoryGridData);
		architectualPatternProblemCategories[2].setVisible(false);
		
		archiProbHelp[2] = new Link(shell, SWT.NONE);
		archiProbHelp[2].setText("<a>?</a>");
		archiProbHelp[2].setToolTipText("What does it mean?");
		archiProbHelp[2].setLayoutData(gridData1);
		archiProbHelp[2].setVisible(false);
		archiProbHelp[2].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "Patterns in this category allow a high degree of user interaction, mainly achieved with help of graphical user interfaces to enhance the usability of an application.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Interactive Systems");
				mbox.open();
			}
		});
		
		designPatternProblemCategories[2] = new Button(shell, SWT.CHECK);
		designPatternProblemCategories[2].setText("Access_Control");
		designPatternProblemCategories[2].setLayoutData(problemCatagoryGridData);
		designPatternProblemCategories[2].setVisible(false);
		
		designProbHelp[2] = new Link(shell, SWT.NONE);
		designProbHelp[2].setText("<a>?</a>");
		designProbHelp[2].setToolTipText("What does it mean?");
		designProbHelp[2].setLayoutData(gridData1);
		designProbHelp[2].setVisible(false);
		designProbHelp[2].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "Such patterns guard and control access to services or components.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Access Control");
				mbox.open();
			}
		});
		
		architectualPatternProblemCategories[3] = new Button(shell, SWT.CHECK);
		architectualPatternProblemCategories[3].setText("Adaptable_Systems");
		architectualPatternProblemCategories[3].setLayoutData(problemCatagoryGridData);
		architectualPatternProblemCategories[3].setVisible(false);	
		
		archiProbHelp[3] = new Link(shell, SWT.NONE);
		archiProbHelp[3].setText("<a>?</a>");
		archiProbHelp[3].setToolTipText("What does it mean?");
		archiProbHelp[3].setLayoutData(gridData1);
		archiProbHelp[3].setVisible(false);
		archiProbHelp[3].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "Patterns in this category support the addition of new functionality, change of existing services, or other kinds of changes. ";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Adaptable Systems");
				mbox.open();
			}
		});
		
		designPatternProblemCategories[3] = new Button(shell, SWT.CHECK);
		designPatternProblemCategories[3].setText("Management");
		designPatternProblemCategories[3].setLayoutData(problemCatagoryGridData);
		designPatternProblemCategories[3].setVisible(false);
		
		designProbHelp[3] = new Link(shell, SWT.NONE);
		designProbHelp[3].setText("<a>?</a>");
		designProbHelp[3].setToolTipText("What does it mean?");
		designProbHelp[3].setLayoutData(gridData1);
		designProbHelp[3].setVisible(false);
		designProbHelp[3].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "This category includes patterns for handling homogenous collections of objects, services and components in their entirety.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Management");
				mbox.open();
			}
		});	
		
		architectualPatternProblemCategories[4] = new Button(shell, SWT.CHECK);
		architectualPatternProblemCategories[4].setText("Other_Architecture_Problem");
		architectualPatternProblemCategories[4].setLayoutData(problemCatagoryGridData);
		architectualPatternProblemCategories[4].setVisible(false);
		
		new Label(shell, SWT.NONE).setText("");
		
		designPatternProblemCategories[4] = new Button(shell, SWT.CHECK);
		designPatternProblemCategories[4].setText("Communication");
		designPatternProblemCategories[4].setLayoutData(problemCatagoryGridData);
		designPatternProblemCategories[4].setVisible(false);
		
		designProbHelp[4] = new Link(shell, SWT.NONE);
		designProbHelp[4].setText("<a>?</a>");
		designProbHelp[4].setToolTipText("What does it mean?");
		designProbHelp[4].setLayoutData(gridData1);
		designProbHelp[4].setVisible(false);
		designProbHelp[4].addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String l_message = "";
				l_message += "Patterns in this category help to organize communication between components.";
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION);
				mbox.setMessage(l_message);
				mbox.setText("Communication");
				mbox.open();
			}
		});
		
		allArchiProbCategories = new Button(shell, SWT.CHECK);
		allArchiProbCategories.setText("All");
		allArchiProbCategories.setLayoutData(problemCatagoryGridData);
		allArchiProbCategories.setVisible(false);
		allArchiProbCategories.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				if(allArchiProbCategories.getSelection()){
					for(Button architectualPatternProblemCategory: architectualPatternProblemCategories){
						architectualPatternProblemCategory.setSelection(true);
					}
				}else{
					for(Button architectualPatternProblemCategory: architectualPatternProblemCategories){
						architectualPatternProblemCategory.setSelection(false);
					}
				}
			}
		});
		
		new Label(shell, SWT.NONE).setText("");
		
		designPatternProblemCategories[5] = new Button(shell, SWT.CHECK);
		designPatternProblemCategories[5].setText("Other_Design_Problem");
		designPatternProblemCategories[5].setLayoutData(problemCatagoryGridData);
		designPatternProblemCategories[5].setVisible(false);
		
		new Label(shell, SWT.NONE).setText("");
		
		Label frontSpace = new Label(shell, SWT.NONE);
		frontSpace.setText("");
		frontSpace.setLayoutData(problemCatagoryGridData);
		
		new Label(shell, SWT.NONE).setText("");
		
		allDesignProbCategories = new Button(shell, SWT.CHECK);
		allDesignProbCategories.setText("All");
		allDesignProbCategories.setLayoutData(problemCatagoryGridData);
		allDesignProbCategories.setVisible(false);
		allDesignProbCategories.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				if(allDesignProbCategories.getSelection()){
					for(Button designPatternProblemCategory: designPatternProblemCategories){
						designPatternProblemCategory.setSelection(true);
					}
				}else{
					for(Button designPatternProblemCategory: designPatternProblemCategories){
						designPatternProblemCategory.setSelection(false);
					}	
				}
			}
		});
		
		new Label(shell, SWT.NONE).setText("");
		
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		
		newlyAddedAlternative = new ArrayList<Alternative>();
		
		Button selectB = new Button(shell, SWT.PUSH);
		selectB.setText("Select");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);

		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		selectB.setLayoutData(gridData);
		selectB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) 
			{				
				ArrayList<Pattern> toBeMatchedPatterns = new ArrayList<Pattern>();
				RationaleDB db = RationaleDB.getHandle();
				
				if(patternTypes[0].getSelection()){
					//architectural pattern selected
					for(Button archiProbCategory: architectualPatternProblemCategories){
						if(archiProbCategory.getSelection()){
							toBeMatchedPatterns.addAll(db.getPatternByCategory(archiProbCategory.getText()));
						}
					}
				}
				if(patternTypes[1].getSelection()){
					//design pattern selected
					for(Button designProbCategory: designPatternProblemCategories){
						if(designProbCategory.getSelection()){
							toBeMatchedPatterns.addAll(db.getPatternByCategory(designProbCategory.getText()));
						}
					}
				}
				if(patternTypes[2].getSelection()){		
					toBeMatchedPatterns.addAll(db.getPatternsByType("Idiom"));
				}
				
				if(matchMethods[0].getSelection())
				{						
					AlternativePatternInferences cpi_Exact = new AlternativePatternInferences();
					candidates_Exact = new Hashtable();
					candidates_Exact = cpi_Exact.exactMatching(toBeMatchedPatterns);
					shell.close();
					shell.dispose();
					SelectCandidatePatterns scp = new SelectCandidatePatterns(candidates_Exact, ourDisplay);
					
					if(!scp.getCanceled()){
						Vector<String> selected = scp.getSelections();					

						saveSelectedPatterns(selected, "exact");
					}
					//decision.saveAlterPatterns();
					//decision.savePatterns(decision.getName(), newIDs);
				}else if(matchMethods[1].getSelection()){
					//contribution matching is the default approach					

					AlternativePatternInferences cpi_Contribution = new AlternativePatternInferences();
					candidates_Contribution = new Hashtable();
					candidates_Contribution = cpi_Contribution.contributionMatching(toBeMatchedPatterns);
					shell.close();
					shell.dispose();
					SelectCandidatePatterns scp = new SelectCandidatePatterns(candidates_Contribution, ourDisplay);
					if(!scp.getCanceled()){
						Vector<String> selected = scp.getSelections();

						saveSelectedPatterns(selected, "contribution");
					}

				}else if(matchMethods[2].getSelection()){
					//not matching NFRs
					shell.close();
					shell.dispose();
					Vector<Pattern> patterns = new Vector<Pattern>();
					for(Pattern pattern: toBeMatchedPatterns){
						patterns.add(pattern);
					}
					SelectCandidatePatterns scp = new SelectCandidatePatterns(patterns, ourDisplay);
					if(!scp.getCanceled()){
						Vector<String> selected = scp.getSelections();
						boolean noMatch;
						saveSelectedPatterns(selected, "nomatch");
					}
				}
			}
		});
		
		cancelButton = new Button(shell, SWT.PUSH); 
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) 
			{
				canceled = true;
				shell.close();
				shell.dispose();
				//System.out.println("Cancel done.");
				while (!shell.isDisposed()) {
					if (!ourDisplay.readAndDispatch()) ourDisplay.sleep();
				}
			}
		});		
		
		
		shell.pack();
		shell.open();
		
		
		System.out.println("Candidates Generation Done!");
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		//System.out.println("Done!");

	}

	public ArrayList<Alternative> getNewlyAddedAlternative() {
		return newlyAddedAlternative;
	}

	public void setNewlyAddedAlternative(
			ArrayList<Alternative> newlyAddedAlternative) {
		this.newlyAddedAlternative = newlyAddedAlternative;
	}
	
	private void saveSelectedPatterns(Vector<String> selected, String matchingMethod){

		Decision decision = new Decision();
		decision.fromDatabase(theDecision);
		Enumeration patterns = selected.elements();
		while(patterns.hasMoreElements()){
			String patternName = patterns.nextElement().toString();
			Pattern pattern = new Pattern();
			pattern.fromDatabase(patternName);						
			while(RationaleDBUtil.isExist(patternName, "Alternatives")){
				patternName = patternName + "~";
			}
			Alternative alter = new Alternative();
			alter.setName(patternName);
			alter.setPatternID(pattern.getID());
			//alter.generateFromPattern(decision, matchingMethod);		
			
			newlyAddedAlternative.add(alter);
				
		}
	}
	
	
}
