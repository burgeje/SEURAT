
package edu.wpi.cs.jburge.SEURAT.editors;

import java.io.Serializable;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Edit a tradeoff or co-occurrence. Tradeoffs describe ontology entries (NFRs) that should appear on opposite
 * sides of an argument. Co-occurrences describe ontology entries that should occur together
 * in arguments.
 * @author burgeje
 *
 */
public class EditTradeoff extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4461925110677043275L;
	private Display ourDisplay;
	private Shell shell;
	
	/**
	 * The tradeoff being edited
	 */
	private Tradeoff ourTrade;
	/**
	 * The name of the tradeoff
	 */ 
	private Text nameField;
	/**
	 * The description of the tradeoff
	 */
	private Text descArea;
//	private boolean newItem;
	
	/**
	 * Button to add our tradeoff
	 */
	private Button addButton;
	/**
	 * Button to cancel edits
	 */
	private Button cancelButton;
	
	/**
	 * Button to select the first ontology entry
	 */
	private Button selTradeoff1Button;
	/**
	 * Button to select the second ontology entry
	 */
	private Button selTradeoffButton;
	/**
	 * Checkbox to indicate if the tradeoff is symmetric (if both sides ALWAYS have to be present)
	 */
	private Combo symmetryBox;;
	/**
	 * Label describing ontology entry 1 (its name)
	 */
	private Label ont1Desc;
	/**
	 * Label describing ontology entry 2 (its name)
	 */
	private Label ont2Desc;
	
	/**
	 * Indicates if this is a tradeoff or a co-occurence 
	 */
	private boolean isTradeoff;
	
	
	// private MessageBox errorBox; 		//dialog for the error message
	
	
	/**
	 * Constructor for the editor
	 * @param display - points to the display
	 * @param editTrade - the tradeoff being edited
	 * @param newItem - true if a new tradeoff/co-occurence
	 */  
	public EditTradeoff(Display display, Tradeoff editTrade, boolean newItem)
	{
		super();
		ourDisplay = display;
		
		isTradeoff = editTrade.getTradeoff();
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		if (isTradeoff)
		{
			shell.setText("Tradeoff Information");
		}
		else
		{
			shell.setText("Co-occurrence Information");
		}
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourTrade = editTrade;
		
		if (newItem)
		{
			ourTrade.setSymmetric(false);
		}
		else
		{
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 TradeoffInferences inf = new TradeoffInferences();
		 Vector newStat = inf.updateTradeoff(ourTrade);
		 } */
		
//		row 1
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourTrade.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 20);
		gridData.horizontalSpan = 3;
		nameField.setLayoutData(gridData);
		
//		row 2
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourTrade.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 20, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		
//		row 3
		new Label(shell, SWT.NONE).setText("Symmetric:");
		
		symmetryBox = new Combo(shell, SWT.NONE);
		symmetryBox.add("No");
		symmetryBox.add("Yes");
		
		if (ourTrade.getSymmetric())
		{
			symmetryBox.select(1);
		}
		else
		{
			symmetryBox.select(0);
		}
		
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		
		//row 4
		new Label(shell, SWT.NONE).setText("Ontology Entry 1:");
		ont1Desc = new Label(shell, SWT.NONE);
		if (newItem)
		{
			ont1Desc.setText("");
		}
		else
		{
			ont1Desc.setText(ourTrade.getOnt1().toString());
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		ont1Desc.setLayoutData(gridData);
		
		selTradeoff1Button = new Button(shell, SWT.PUSH); 
		selTradeoff1Button.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		selTradeoff1Button.setLayoutData(gridData);
		selTradeoff1Button.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);
				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					ourTrade.setOnt1(newOnt);
					ont1Desc.setText(newOnt.toString());
				}
			}
		});
		
		//row 4
		new Label(shell, SWT.NONE).setText("Ontology Entry 2:");
		ont2Desc = new Label(shell, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		ont2Desc.setLayoutData(gridData);
		if (newItem)
		{
			ont2Desc.setText("");
		}
		else
		{
			ont2Desc.setText(ourTrade.getOnt2().toString());
		}
		
		selTradeoffButton = new Button(shell, SWT.PUSH); 
		selTradeoffButton.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		selTradeoffButton.setLayoutData(gridData);
		selTradeoffButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);
				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					ourTrade.setOnt2(newOnt);
					ont2Desc.setText(newOnt.toString());
				}
			}
		});
		
		
		new Label(shell, SWT.NONE).setText(" ");
		new Label(shell, SWT.NONE).setText(" ");
//		new Label(shell, SWT.NONE).setText(" ");
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
					
					if (nameField.getText().trim().equals(""))
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the name");
						mbox.open();				}
					else if ((ourTrade.getOnt1() == null) || (ourTrade.getOnt2() == null))
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to select both ontology entries");
						mbox.open();
					}
					else
					{
						ConsistencyChecker checker = new ConsistencyChecker(ourTrade.getID(), nameField.getText(), "Tradeoffs");
						
						if(ourTrade.getName() == nameField.getText() || checker.check())
						{
							ourTrade.setName(nameField.getText());
							ourTrade.setDescription(descArea.getText());
							ourTrade.setTradeoff(isTradeoff);
							if (symmetryBox.getSelectionIndex() == 0)
							{
								ourTrade.setSymmetric(false);
							}
							else
							{
								ourTrade.setSymmetric(true);
							}
							ourTrade.setID(ourTrade.toDatabase());
							shell.close();
							shell.dispose();	
						}
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
					
					ConsistencyChecker checker = new ConsistencyChecker(ourTrade.getID(), nameField.getText(), "Tradeoffs");
					
					if(ourTrade.getName() == nameField.getText() || checker.check())
					{
						if (nameField.getText().trim().equals(""))
						{
							MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
							mbox.setMessage("Need to provide the name");
							mbox.open();				}
						else if ((ourTrade.getOnt1() == null) || (ourTrade.getOnt2() == null))
						{
							MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
							mbox.setMessage("Need to select both ontology entries");
							mbox.open();
						}
						else
						{
							ourTrade.setName(nameField.getText());
							ourTrade.setDescription(descArea.getText());
							ourTrade.setTradeoff(isTradeoff);
							if (symmetryBox.getSelectionIndex() == 0)
							{
								ourTrade.setSymmetric(false);
							}
							else
							{
								ourTrade.setSymmetric(true);
							}
							ourTrade.setID(ourTrade.toDatabase());
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
	 * Gets our tradeoff or co-occurrence
	 */ 
	public RationaleElement getItem()
	{
		return ourTrade;
	}
	
	
}






