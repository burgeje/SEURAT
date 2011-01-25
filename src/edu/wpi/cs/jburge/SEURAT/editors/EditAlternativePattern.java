package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativePattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Importance;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

public class EditAlternativePattern extends NewRationaleElementGUI {

	/**
	 * The GUI shell
	 */
	private Shell shell;
	
	/**
	 * The pattern being edited
	 */
	private AlternativePattern altPattern;

	private Pattern ourPattern;
	
	private Text nameField;
	
	private Text typeBox;
	
	private Combo statusBox;
 
	private Text descArea;
	
	private Text probArea;
	
	private Text contArea;
	
	private Text soluArea;
	
	private Text impleArea;
	
	private Text exampleArea;
	
	private Text urlArea;
	
	
	private boolean newItem;
	/**
	 * Button for adding the entry
	 */
	private Button addButton;
	
	private Button addPosiOntButton;
	
	private Button addNegaOntButton;
	/**
	 * Button to cancel edits
	 */
	private Button cancelButton;
	/**
	 * The importance of this entry. This value will be inherited by any claims
	 * referencing it.
	 */
	private Combo importanceBox;
	
	private List positiveList;
	
	private List negativeList;
	
//	public boolean canceled;
	
	/**
	 * Constructor to create an editor to update/create an ontology entry
	 * @param display - points back to the display
	 * @param oureditOntEntry - the entry being edited
	 * @param ontParent - the edited item's parent in the hierarchy
	 * @param newItem - true if this is a new item
	 */
	public EditAlternativePattern(Display display, AlternativePattern pattern, boolean newItem)
	{
		super();
		altPattern = pattern;
		ourPattern = altPattern.getPatternInLibrary();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Alternative Pattern Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);		
	
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		nameField.setText(ourPattern.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//gridData.horizontalSpan = 3;
		DisplayUtilities.setTextDimensions(nameField, gridData, 75);
		nameField.setLayoutData(gridData);
		nameField.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Type:");
		
		typeBox = new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		typeBox.setText(ourPattern.getType().toString());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 75);
		typeBox.setLayoutData(gridData);
		typeBox.setEditable(false);
		

		new Label(shell, SWT.NONE).setText("Online Resource URL:");	
		
		urlArea = new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		urlArea.setText(ourPattern.getUrl());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		DisplayUtilities.setTextDimensions(urlArea, gridData, 75);
		urlArea.setLayoutData(gridData);
		urlArea.setEditable(false);
						
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourPattern.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		descArea.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Problem:");
		
		probArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		probArea.setText(ourPattern.getProblem());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(probArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = probArea.getLineHeight() * 3;
		probArea.setLayoutData(gridData);
		probArea.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Context:");
		
		contArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		contArea.setText(ourPattern.getContext());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(contArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = contArea.getLineHeight() * 3;
		contArea.setLayoutData(gridData);
		contArea.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Solution:");
		
		soluArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		soluArea.setText(ourPattern.getSolution());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(soluArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = soluArea.getLineHeight() * 3;
		soluArea.setLayoutData(gridData);
		soluArea.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Implementation:");
		
		impleArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		impleArea.setText(ourPattern.getImplementation());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(impleArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = impleArea.getLineHeight() * 3;
		impleArea.setLayoutData(gridData);
		impleArea.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Example:");
		
		exampleArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		exampleArea.setText(ourPattern.getExample());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(exampleArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = exampleArea.getLineHeight() * 3;
		exampleArea.setLayoutData(gridData);
		exampleArea.setEditable(false);
		
		new Label(shell, SWT.NONE).setText("Status:");
		statusBox = new Combo(shell, SWT.NONE);
		//statusBox.addModifyListener(getNeedsSaveListener());
		Enumeration statEnum = AlternativeStatus.elements();
		int j=0;
		AlternativeStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (AlternativeStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(altPattern.getStatus().toString()) == 0)
			{
//				System.out.println(ourAlt.getStatus().toString());
				statusBox.select(j);
				
//				System.out.println(j);
			}
			j++;
		}		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		//gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		statusBox.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Evaluation:");
		new Label(shell, SWT.NONE).setText("Test");
		
		Label aa = new Label(shell, SWT.NONE);
		aa.setText("Affected Attributes");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		aa.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Positively:");
		new Label(shell, SWT.NONE).setText("");

//		addPosiOntButton = new Button(shell, SWT.PUSH); 
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		addPosiOntButton.setLayoutData(gridData);
//		addPosiOntButton.setText("Add");
//		addPosiOntButton.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent event) 
//			{
//				OntEntry newOnt = null;
//				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);
//				
//				newOnt = ar.getSelOntEntry();
//				if (newOnt != null)
//				{
//					ourPattern.addPosiOnt(newOnt);
//				}
//
//			}
//		});
		
		new Label(shell, SWT.NONE).setText("Negatively:");
		new Label(shell, SWT.NONE).setText("");

//		addNegaOntButton = new Button(shell, SWT.PUSH); 
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		addNegaOntButton.setLayoutData(gridData);
//		addNegaOntButton.setText("Add");
//		addNegaOntButton.addSelectionListener(new SelectionAdapter() {
//
//			public void widgetSelected(SelectionEvent event) 
//			{
//				OntEntry newOnt = null;
//				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);
//				
//				newOnt = ar.getSelOntEntry();
//				if (newOnt != null)
//				{
//					ourPattern.setNegaOnt(newOnt);
//				}
//
//			}
//		});
		
		
		positiveList = new List(shell, SWT.SINGLE | SWT.V_SCROLL);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		int listHeight = positiveList.getItemHeight() * 4;
		Rectangle trim = positiveList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		if (ourPattern.getPosiOnts() != null){
			Vector pos = ourPattern.getPosiOnts();
			Enumeration patterns =pos.elements();
			while (patterns.hasMoreElements())
			{
				positiveList.add(patterns.nextElement().toString());
			}	
			
			//positiveList.add(ourPattern.getPosiOnt().getName());
		}else{
			positiveList = null;
		}
		
	
		
		
//		Vector listV = getRequirement().getArgumentsFor();
//		Enumeration listE = listV.elements();
//		while (listE.hasMoreElements())
//		{
//			Argument arg = new Argument();
//			arg.fromDatabase((String)listE.nextElement());
//			
//			forModel.add( arg.getName() );
//			
//			// Register Event Notification
//			try
//			{
//				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onForArgumentUpdate");
//			}
//			catch( Exception e )
//			{
//				System.out.println("Requirement Editor: For Argument Update Notification Not Available!");
//			}
//		} 
		// add a list of arguments against to the right side
		positiveList.setLayoutData(gridData);
		
		negativeList = new List(shell, SWT.SINGLE | SWT.V_SCROLL);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		listHeight = negativeList.getItemHeight() * 4;
		trim = negativeList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		if (ourPattern.getNegaOnts()!= null){
			Vector pos = ourPattern.getNegaOnts();
			Enumeration patterns =pos.elements();
			while (patterns.hasMoreElements())
			{
				negativeList.add(patterns.nextElement().toString());
			}	
			
			//positiveList.add(ourPattern.getPosiOnt().getName());
		}else{
			negativeList = null;
		}
//		Vector listV = getRequirement().getArgumentsFor();
//		Enumeration listE = listV.elements();
//		while (listE.hasMoreElements())
//		{
//			Argument arg = new Argument();
//			arg.fromDatabase((String)listE.nextElement());
//			
//			forModel.add( arg.getName() );
//			
//			// Register Event Notification
//			try
//			{
//				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onForArgumentUpdate");
//			}
//			catch( Exception e )
//			{
//				System.out.println("Requirement Editor: For Argument Update Notification Not Available!");
//			}
//		}    
		// add a list of arguments against to the right side
		negativeList.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		
		addButton = new Button(shell, SWT.PUSH); 
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gridData);
		addButton.setText("Save");
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) 
			{
				canceled = false;

				altPattern.setStatus( AlternativeStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
				altPattern.toDatabase(altPattern.getParent(), altPattern.getPtype());

//				if(altPattern.getStatus() == AlternativeStatus.ADOPTED){
//					altPattern.savePatternDecisions();
//				}else{
//					altPattern.deletePatternDecisions();
//				}
				
				shell.close();
				shell.dispose();	


				if (!canceled)
				{
					RationaleElement ele = 
						RationaleDB.getRationaleElement(ourPattern.getName(), RationaleElementType.ALTERNATIVEPATTERN);
					RationaleUpdateEvent evt = new RationaleUpdateEvent(this);
					evt.fireUpdateEvent(ele, shell.getDisplay(), UpdateType.UPDATE);
					shell.close();
					shell.dispose();
				}

			}
		});

		
		
//		if (newItem)
//		{
//			addButton.setText("Add");
//			addButton.addSelectionListener(new SelectionAdapter() {
//				
//				public void widgetSelected(SelectionEvent event) 
//				{
//					canceled = false;
//					if (!nameField.getText().trim().equals(""))
//					{
//						ConsistencyChecker checker = new ConsistencyChecker(ourOntEntry.getID(), nameField.getText(), "OntEntries");
//						
//						if(ourOntEntry.getName() == nameField.getText() || checker.check())
//						{
//							ourParent.addChild(ourOntEntry);
//							ourOntEntry.setLevel(ourParent.getLevel() + 1);
//							ourOntEntry.setName(nameField.getText());
//							ourOntEntry.setDescription(descArea.getText());
//							ourOntEntry.setImportance( Importance.fromString(importanceBox.getItem(importanceBox.getSelectionIndex())));
//							
//							//comment before this made no sense...
//							ourOntEntry.setID(ourOntEntry.toDatabase(ourParent.getID()));	
//							System.out.println("Name of added item = " + ourOntEntry.getName());
//							
//							shell.close();
//							shell.dispose();	
//						}
//					}
//					else
//					{
//						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
//						mbox.setMessage("Need to provide the OntEntry name");
//						mbox.open();
//					}
//				}
//			});
//			
//		}
//		else
//		{
//			addButton.setText("Save");
//			addButton.addSelectionListener(new SelectionAdapter() {
//				
//				public void widgetSelected(SelectionEvent event) 
//				{
//					canceled = false;
//					
//					ConsistencyChecker checker = new ConsistencyChecker(ourOntEntry.getID(), nameField.getText(), "OntEntries");
//					
//					if(ourOntEntry.getName() == nameField.getText() || checker.check())
//					{
//						ourOntEntry.setName(nameField.getText());
//						ourOntEntry.setDescription(descArea.getText());
//						ourOntEntry.setImportance( Importance.fromString(importanceBox.getItem(importanceBox.getSelectionIndex())));
//						//since this is a save, not an add, the type and parent are ignored
//						ourOntEntry.setID(ourOntEntry.toDatabase(0));
//						
//						//			RationaleDB db = RationaleDB.getHandle();
//						//			db.addOntEntry(ourOntEntry);
//						
//						shell.close();
//						shell.dispose();	
//					}
//					
//				}
//			});
//		}
//		
//		
//		
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
	 * Get the ontology entry
	 */  
//	public RationaleElement getItem()
//	{
//		return ourOntEntry;
//	}	
	
}

