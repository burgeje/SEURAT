
package edu.wpi.cs.jburge.SEURAT.editors;

import java.awt.Frame;
import java.io.Serializable;
import java.util.Enumeration;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Edits a question. Questions are requests for more information needed to make a decision
 * and can be attached to decisions or alternatives.
 * @author burgeje
 *
 */
public class EditQuestion extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 642560974262434975L;
	private Shell shell;
	/**
	 * The question being edited
	 */	
	private Question ourQuest;
	/**
	 * The question's name
	 */
	private Text nameField;
	// private Text artifactField;
	/**
	 * A description of the question (or the question itself!)
	 */
	private Text descArea;
	/**
	 * The answer to the question
	 */
	private Text answer;
	/**
	 * The procedure to follow in order to find an answer. This could be a test or simulation
	 * that needs to be run or it can be instructions on who to ask for an answer.
	 */
	private Text procedure;
//	private boolean newItem;
	/**
	 * Button to add a question
	 */
	private Button addButton;
	/**
	 * Button to cancel editing the question
	 */
	private Button cancelButton;
	/**
	 * The status of the question (answered or unanswered)
	 */
	private Combo statusBox;
	
	/**
	 * Constructor for the question editor
	 * @param display - points back to the display
	 * @param editQuest - the question being edited
	 * @param newItem - true if this is a new item
	 */  
	public EditQuestion(Display display, Question editQuest, boolean newItem)
	{
		super();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Question Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourQuest = editQuest;
		
		if (newItem)
		{
			ourQuest.setStatus(QuestionStatus.UNANSWERED);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 QuestionInferences inf = new QuestionInferences();
		 Vector newStat = inf.updateQuestion(ourQuest);
		 } */
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourQuest.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 100);
		gridData.horizontalSpan = 3;
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourQuest.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Status:");
		statusBox = new Combo(shell, SWT.NONE);
		Enumeration statEnum = QuestionStatus.elements();
		int j=0;
		QuestionStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (QuestionStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(ourQuest.getStatus().toString()) == 0)
			{
//				System.out.println(ourQuest.getStatus().toString());
				statusBox.select(j);
//				System.out.println(j);
			}
			j++;
		}
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
		
		new Label(shell, SWT.NONE).setText("Procedure:");
		
		procedure = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		procedure.setText(ourQuest.getProcedure());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(procedure, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = procedure.getLineHeight() * 3;
		procedure.setLayoutData(gridData);  
		
		new Label(shell, SWT.NONE).setText("Answer:");
		
		answer = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		answer.setText(ourQuest.getAnswer());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(answer, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = answer.getLineHeight() * 3;
		answer.setLayoutData(gridData);
//		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
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
					//int typeIndex;
					//int statusIndex;
					canceled = false;
					if (!nameField.getText().trim().equals(""))
					{
						ConsistencyChecker checker = new ConsistencyChecker(ourQuest.getID(), nameField.getText(), "Questions");
						
						if(ourQuest.getName() == nameField.getText() || checker.check())
						{
							ourQuest.setName(nameField.getText());
							ourQuest.setDescription(descArea.getText());
							ourQuest.setProcedure(procedure.getText());
							ourQuest.setAnswer(answer.getText());
							ourQuest.setStatus( QuestionStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
							//				ourQuest.setArtifact( artifactField.getText());
							ourQuest.updateHistory(new History(ourQuest.getStatus().toString(), "Initial Entry"));
							
							
							//comment before this made no sense...
							ourQuest.setID(ourQuest.toDatabase(ourQuest.getParent(), ourQuest.getPtype()));
							
							shell.close();
							shell.dispose();
						}
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Question name");
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
					//	int typeIndex;
					canceled = false;
					//	int statusIndex;
					
					ConsistencyChecker checker = new ConsistencyChecker(ourQuest.getID(), nameField.getText(), "Questions");
					
					if(ourQuest.getName() == nameField.getText() || checker.check())
					{
						ourQuest.setName(nameField.getText());
						ourQuest.setDescription(descArea.getText());
						ourQuest.setProcedure(procedure.getText());
						ourQuest.setAnswer(answer.getText());
						QuestionStatus newStat = QuestionStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
						if (!newStat.toString().equals(ourQuest.getStatus().toString()))
						{
							Frame rf = new Frame();
							ReasonGUI rg = new ReasonGUI(rf);
							rg.setVisible(true);
							String newReason = rg.getReason();
							ourQuest.setStatus(newStat);
							//				System.out.println(newStat.toString() + ourQuest.getStatus().toString());
							History newHist = new History(newStat.toString(), newReason);
							ourQuest.updateHistory(newHist);
							//				ourQuest.toDatabase(ourQuest.getParent(), RationaleElementType.fromString(ourQuest.getPtype()));
							//				newHist.toDatabase(ourQuest.getID(), RationaleElementType.Question);
						}
						
//						since this is a save, not an add, the type and parent are ignored
						ourQuest.setID(ourQuest.toDatabase(ourQuest.getParent(), ourQuest.getPtype()));
						
						
						//			RationaleDB db = RationaleDB.getHandle();
						//			db.addQuestion(ourQuest);
						
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
	 * Get our item
	 */  
	public RationaleElement getItem()
	{
		return ourQuest;
	}
	
	
}






