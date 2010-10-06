package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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

public class EditPattern extends NewRationaleElementGUI implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7945887769854864963L;

	private Shell shell;

	private Display ourDisplay;

	private Pattern ourPattern;

	private Text nameField;

	private Combo typeBox;	

	private Text urlArea;
	/**
	 * The description of the ontology entry
	 */
	private Text descArea;

	private Text probArea;

	private Text contArea;

	private Text soluArea;

	private Text impleArea;

	private Text exampleArea;


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
	 * @param pattern - The pattern being edited
	 * @param newItem - true if this is a new item
	 * NOTE: I do not see the ability to add new item here...
	 */
	public EditPattern(Display display, Pattern pattern, boolean newItem)
	{
		super();

		shell = new Shell();
		shell.setText("Pattern Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);	

		ourPattern = pattern;
		ourDisplay = display;

		new Label(shell, SWT.NONE).setText("Name:");

		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		nameField.setText(ourPattern.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//gridData.horizontalSpan = 3;
		DisplayUtilities.setTextDimensions(nameField, gridData, 75);
		nameField.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Type:");

		typeBox = new Combo(shell, SWT.NONE);
		Enumeration typeEnum = PatternElementType.elements();
		int typeIndex = 0;
		PatternElementType patternType;
		while (typeEnum.hasMoreElements()){
			patternType = (PatternElementType)typeEnum.nextElement();
			typeBox.add(patternType.toString());
			if(patternType.toString().compareTo(ourPattern.getType().toString()) == 0){
				typeBox.select(typeIndex);
			}
			typeIndex++;
		}

		new Label(shell, SWT.NONE).setText("Online Resource URL:");	

		urlArea = new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		urlArea.setText(ourPattern.getUrl());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		DisplayUtilities.setTextDimensions(urlArea, gridData, 75);
		urlArea.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Description:");

		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourPattern.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Problem:");

		probArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		probArea.setText(ourPattern.getProblem());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(probArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = probArea.getLineHeight() * 3;
		probArea.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Context:");

		contArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		contArea.setText(ourPattern.getContext());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(contArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = contArea.getLineHeight() * 3;
		contArea.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Solution:");

		soluArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		soluArea.setText(ourPattern.getSolution());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(soluArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = soluArea.getLineHeight() * 3;
		soluArea.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Implementation:");

		impleArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		impleArea.setText(ourPattern.getImplementation());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(impleArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = impleArea.getLineHeight() * 3;
		impleArea.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Example:");

		exampleArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		exampleArea.setText(ourPattern.getExample());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(exampleArea, gridData, 75, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = exampleArea.getLineHeight() * 3;
		exampleArea.setLayoutData(gridData);

		Label aa = new Label(shell, SWT.NONE);
		aa.setText("Affected Attributes");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		aa.setLayoutData(gridData);

		new Label(shell, SWT.NONE).setText("Positively:");
		new Label(shell, SWT.NONE).setText("");


		new Label(shell, SWT.NONE).setText("Negatively:");
		new Label(shell, SWT.NONE).setText("");

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
				ourPattern.setDescription(descArea.getText());
				ourPattern.setContext(contArea.getText());
				ourPattern.setProblem(probArea.getText());
				ourPattern.setSolution(soluArea.getText());
				ourPattern.setImplementation(impleArea.getText());
				ourPattern.setExample(exampleArea.getText());
				ourPattern.setUrl(urlArea.getText());
				ourPattern.setType(PatternElementType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));

				ourPattern.toDatabase(0);

				shell.close();
				shell.dispose();	



			}
		});


		//NOT Implemented: New item...
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
	public RationaleElement getItem()
	{
		return ourPattern;
	}	

}
