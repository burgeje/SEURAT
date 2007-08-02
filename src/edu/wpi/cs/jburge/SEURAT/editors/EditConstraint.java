
package edu.wpi.cs.jburge.SEURAT.editors;


import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;


import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Editor for constraints
 * @author burgeje
 *
 */
public class EditConstraint extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Shell shell;
	
	/**
	 * The constraint being edited
	 */
	private Constraint ourConstraint;
	
	/**
	 * The rationale element the constraint applies to
	 */
	private Constraint ourParent;
	
	/**
	 * The name of the constraint
	 */
	private Text nameField;
	/**
	 * The amount of the constraint (limit)
	 */
	private Text amtField;
	/**
	 * The units the constraint limit is expressed in
	 */
	private Text unitsField;
	
	/**
	 * A description of the constraint
	 */
	private Text descArea;
	
	/**
	 * Button to add the constraint
	 */
	private Button addButton;
	/**
	 * Button to cancel editing the constraint
	 */
	private Button cancelButton;
	
	/**
	 * The tree containing a hierarchy of components
	 */
	private Tree componentTree;
	/**
	 * The root of the tree
	 */
	private TreeItem root;
	
	/**
	 * The design product the constraint applies to
	 */
	private DesignProductEntry selEntry;
	
	/**
	 * The constructor for the constraint editor
	 * @param display - our display
	 * @param oureditConstraint - the constraint being created
	 * @param ontParent - the parent of the constraint in the hierarchy
	 * @param newItem - set to true if we are creating a new  constraint
	 */
	public EditConstraint(Display display, Constraint oureditConstraint, Constraint ontParent, boolean newItem)
	{
		super();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Constraint Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourConstraint = oureditConstraint;
		ourParent = ontParent;
		
		if (newItem)
		{
			ourConstraint.setName("");
		}
		
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		nameField.setText(ourConstraint.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 100);
		gridData.horizontalSpan = 3;
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourConstraint.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		DisplayUtilities.setTextDimensions(descArea, gridData, 100, 3);
		descArea.setLayoutData(gridData);
		
		//need the amount and units!
		new Label(shell, SWT.NONE).setText("Amount:");
		
		amtField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		amtField.setText(new Float(ourConstraint.getAmount()).toString());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(amtField, gridData, 100);
		amtField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Units:");
		
		unitsField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		unitsField.setText(ourConstraint.getUnits());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(unitsField, gridData, 100);
		unitsField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Design Component:");
		
		
		if (newItem)
		{
			
			new Label(shell, SWT.NONE).setText("");
			new Label(shell, SWT.NONE).setText("");
			new Label(shell, SWT.NONE).setText("");
			componentTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL);
			root = new TreeItem(componentTree, SWT.NONE);
			root.setText("Design-Product-Ontology");
			populateTree(root, "Design-Product-Ontology");
			
			gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.horizontalSpan = 4;
			DisplayUtilities.setTreeDimensions(componentTree, gridData, 4, 100);
			componentTree.setLayoutData(gridData);
			
			
		}
		else
		{
			
			new Label(shell, SWT.NONE).setText(ourConstraint.getComponent().getName());
			new Label(shell, SWT.NONE).setText("");
			new Label(shell, SWT.NONE).setText("");
		}
		
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
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
						ConsistencyChecker checker = new ConsistencyChecker(ourConstraint.getID(), nameField.getText(), "Constraints");
						
						if(ourConstraint.getName() == nameField.getText() || checker.check())
						{
							TreeItem[] selected = componentTree.getSelection();
							selEntry = new DesignProductEntry();
							selEntry.fromDatabase(selected[0].getText());
							ourConstraint.setComponent(selEntry);
							ourParent.addChild(ourConstraint);
							ourConstraint.setLevel(ourParent.getLevel() + 1);
							ourConstraint.setName(nameField.getText());
							ourConstraint.setDescription(descArea.getText());
							ourConstraint.setUnits(unitsField.getText());
							
							String amountString = amtField.getText();
							try
							{
								Float testFlt = Float.valueOf(amountString);
								ourConstraint.setAmount(testFlt.floatValue());
								
								
								//comment before this made no sense...
								ourConstraint.setID(ourConstraint.toDatabase(ourParent.getID()));	
								System.out.println("Name of added item = " + ourConstraint.getName());
								
								shell.close();
								shell.dispose();	
								
							} catch (NumberFormatException ex)
							{
								MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
								mbox.setMessage("Need to provide a valid percentage");
								mbox.open();
							}
						}
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Constraint name");
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
					
					ConsistencyChecker checker = new ConsistencyChecker(ourConstraint.getID(), nameField.getText(), "Constraints");
					
					if(ourConstraint.getName() == nameField.getText() || checker.check())
					{
						ourConstraint.setName(nameField.getText());
						ourConstraint.setDescription(descArea.getText());
						ourConstraint.setDescription(descArea.getText());
						ourConstraint.setUnits(unitsField.getText());
						
						String amountString = amtField.getText();
						try
						{
							Float testFlt = Float.valueOf(amountString);
							ourConstraint.setAmount(testFlt.floatValue());
							//since this is a save, not an add, the type and parent are ignored
							ourConstraint.setID(ourConstraint.toDatabase(0));
							
							
							shell.close();
							shell.dispose();	
							
						} catch (NumberFormatException ex)
						{
							MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
							mbox.setMessage("Need to provide a valid percentage");
							mbox.open();
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
	 * Populates our tree from the design product element list. This is recursive.
	 * @param node - the current top (parent) node
	 * @param parentName - the name of the parent
	 */
	private void populateTree(TreeItem node, String parentName )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getDesignProductElements(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			TreeItem next = null;
			DesignProductEntry child = (DesignProductEntry) onts.nextElement();
			next = new TreeItem(node, SWT.NONE);
			next.setText(child.getName());
			populateTree(next, child.getName());
		}
		
	}
	
	
	/**
	 * Retrieves our constraint
	 */
	public RationaleElement getItem()
	{
		return ourConstraint;
	}
	
	
}






