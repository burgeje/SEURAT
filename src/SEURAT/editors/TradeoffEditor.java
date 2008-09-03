package SEURAT.editors;

import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.SelectOntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Tradeoff;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * Edit a tradeoff or co-occurrence. Tradeoffs describe ontology entries (NFRs) that should appear on opposite
 * sides of an argument. Co-occurrences describe ontology entries that should occur together
 * in arguments.
 */
public class TradeoffEditor extends RationaleEditorBase {
	public static RationaleEditorInput createInput(RationaleExplorer explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new TradeoffEditor.Input(explorer, tree, parent, target, new1);
	}
	
	/**
	 * Keeps track of the last known good values for editor data.
	 */
	private class DataCache
	{
		/**
		 * Last known good name
		 */
		String name;
		/**
		 * Last known good description
		 */
		String description;
		/**
		 * Last known good symmetry.
		 */
		boolean symmetric;
		/**
		 * Last known good ontology entry 1
		 */
		OntEntry ont1;
		/**
		 * Last known good ontology entry 2
		 */
		OntEntry ont2;
	}
	
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
	private static boolean isTradeoff;
	
	/**
	 * The display component parented to this editor
	 */
	private Display ourDisplay;
	
	/**
	 * The currently selected ontology entry 1
	 */
	private OntEntry curOnt1;
	/**
	 * The currently selected ontology entry 2
	 */
	private OntEntry curOnt2;
	/**
	 * Member variable storing the last known good values of this
	 * editors fields.
	 */
	private DataCache dataCache = new DataCache();
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#editorType()
	 */
	public Class editorType() {
		return Tradeoff.class;
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#getRationaleElement()
	 */
	public RationaleElement getRationaleElement() {
		return getTradeoff();
	}

	/**
	 * @return the tradeoff associated with this editor
	 */
	public Tradeoff getTradeoff() {
		return (Tradeoff)getEditorData().getAdapter(Tradeoff.class);
	}
	
	/**
	 * @return the name which should be displayed 
	 * by this editor in the eclipse interface
	 */
	public String getName() {
		return getEditorData().getName();
	}
	
	/**
	 * Respond to changes made to this tradeoff.
	 * 
	 * @param pElement the tradeoff which generated the event
	 * @param pEvent a description of the changes made to the tradeoff
	 */
	public void onUpdate(Tradeoff pElement, RationaleUpdateEvent pEvent)
	{
		if( pEvent.getElement().equals(getTradeoff()) &&
			( pEvent.getCreated() || pEvent.getModified() ) )
		{	
			refreshForm(pEvent);			
		}
		else
		if( pEvent.getElement().equals(getTradeoff()) &&
			pEvent.getDestroyed() )
		{	
			closeEditor();
		}
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#updateFormCache()
	 */
	@Override
	protected void updateFormCache() {
		if( nameField != null )
			dataCache.name = nameField.getText();
		
		if( descArea != null )
			dataCache.description = descArea.getText();
		
		if( symmetryBox != null )
			dataCache.symmetric = symmetryBox.getSelectionIndex() == 1;

		dataCache.ont1 = getTradeoff().getOnt1();
		dataCache.ont2 = getTradeoff().getOnt2();
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#onRefreshForm(SEURAT.events.RationaleUpdateEvent)
	 */
	protected void onRefreshForm(RationaleUpdateEvent pEvent)
	{		
		boolean l_dirty = isDirty();
		
		getTradeoff().fromDatabase(getTradeoff().getID());
		
		if( nameField.getText().equals(dataCache.name) )
		{
			nameField.setText(getTradeoff().getName());
			dataCache.name = nameField.getText();
		}
		else
			l_dirty = true;
		
		if( descArea.getText().equals(dataCache.description) )
		{
			descArea.setText(getTradeoff().getDescription());
			dataCache.description = descArea.getText();
		}
		else
			l_dirty = true;
		
		if( (symmetryBox.getSelectionIndex() == 1) == dataCache.symmetric )
		{
			if( getTradeoff().getSymmetric() )
				symmetryBox.select(1);
			else
				symmetryBox.select(0);
			dataCache.symmetric = symmetryBox.getSelectionIndex() == 1;
		}
		else
			l_dirty = true;
		
		if( curOnt1.equals(dataCache.ont1) )
		{
			if( getTradeoff().getOnt1() == null )
				ont1Desc.setText("(not selected");
			else
				ont1Desc.setText(getTradeoff().getOnt1().getName());
			dataCache.ont1 = curOnt1 = getTradeoff().getOnt1();
		}
		else
			l_dirty = true;

		if( curOnt1.equals(dataCache.ont1) )
		{
			if( getTradeoff().getOnt2() == null )
				ont2Desc.setText("(not selected)");
			else
				ont2Desc.setText(getTradeoff().getOnt2().getName());
			dataCache.ont2 = curOnt2 = getTradeoff().getOnt2();
		}
		else
			l_dirty = true;
		
		setDirty(l_dirty);
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#setupForm(org.eclipse.swt.widgets.Composite)
	 */
	public void setupForm(Composite parent) {		
		ourDisplay = parent.getDisplay();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		isTradeoff = getTradeoff().getTradeoff();
		
		// Make sure the title is correct (had issues with tradeoff title being co-occurrence and vice versa)
		this.setPartName(getName());
		
		if (isCreating())
		{
			getTradeoff().setSymmetric(false);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 TradeoffInferences inf = new TradeoffInferences();
		 Vector newStat = inf.updateTradeoff(ourTrade);
		 } */
		
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getTradeoff().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 20);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Description:");
		
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getTradeoff().getDescription());
		descArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 20, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = descArea.getLineHeight() * 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Symmetric:");
		
		symmetryBox = new Combo(parent, SWT.NONE);
		symmetryBox.addModifyListener(getNeedsSaveListener());
		symmetryBox.add("No");
		symmetryBox.add("Yes");
		
		if (getTradeoff().getSymmetric())
		{
			symmetryBox.select(1);
		}
		else
		{
			symmetryBox.select(0);
		}
		
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		
		//row 4
		new Label(parent, SWT.NONE).setText("Ontology Entry 1:");
		ont1Desc = new Label(parent, SWT.NONE);
		curOnt1 = getTradeoff().getOnt1();
		if ( curOnt1 == null )
		{
			ont1Desc.setText("(not selected)");
		}
		else
		{
			ont1Desc.setText(curOnt1.toString());
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		ont1Desc.setLayoutData(gridData);
		
		selTradeoff1Button = new Button(parent, SWT.PUSH); 
		selTradeoff1Button.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		selTradeoff1Button.setLayoutData(gridData);
		selTradeoff1Button.addSelectionListener(getSelNeedsSaveListener());
		selTradeoff1Button.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);
				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					curOnt1 = newOnt;
					getTradeoff().setOnt1(newOnt);
					ont1Desc.setText(newOnt.toString());
				}
			}
		});
		
		//row 4
		new Label(parent, SWT.NONE).setText("Ontology Entry 2:");
		ont2Desc = new Label(parent, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		ont2Desc.setLayoutData(gridData);
		curOnt2 = getTradeoff().getOnt2();
		if (curOnt2 == null)
		{
			ont2Desc.setText("(not selected)");
		}
		else
		{
			ont2Desc.setText(curOnt2.toString());
		}
		
		selTradeoffButton = new Button(parent, SWT.PUSH); 
		selTradeoffButton.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		selTradeoffButton.setLayoutData(gridData);
		selTradeoffButton.addSelectionListener(getSelNeedsSaveListener());
		selTradeoffButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourDisplay, true);
				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					curOnt2 = newOnt;
					getTradeoff().setOnt2(newOnt);
					ont2Desc.setText(newOnt.toString());
				}
			}
		});

		// Register Event Notification For Changes To This Element
		try
		{
			RationaleDB.getHandle().Notifier().Subscribe(getTradeoff(), this, "onUpdate");
		}
		catch( Exception e )
		{
			System.out.println("Tradeoff Editor: Updated Not Available!");
		}
		
		updateFormCache();
	}
	
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#saveData()
	 */
	public boolean saveData() {
		ConsistencyChecker checker = new ConsistencyChecker(getTradeoff().getID(), nameField.getText(), "Tradeoffs");
		if(!nameField.getText().trim().equals("") &&
				(getTradeoff().getName() == nameField.getText() || checker.check(false)))
		{
			if ((getTradeoff().getOnt1() == null) || (getTradeoff().getOnt2() == null))
			{
				MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
				mbox.setMessage("Need to select both ontology entries");
				mbox.open();
			} else {
				getTradeoff().setName(nameField.getText());
				getTradeoff().setDescription(descArea.getText());
				getTradeoff().setTradeoff(isTradeoff);
				if (symmetryBox.getSelectionIndex() == 0)
				{
					getTradeoff().setSymmetric(false);
				}
				else
				{
					getTradeoff().setSymmetric(true);
				}
				// since this is a save, not an add, the type and parent are ignored
				getTradeoff().setID(getTradeoff().toDatabase());
				return true;
			}
		}
		else
		{
			String l_message = "";
			if (isTradeoff) {
				l_message += "The tradeoff name you have specified is either already"
					+ " in use or empty. Please make sure that you have specified"
					+ " a tradeoff name and the tradeoff name does not already exist"
					+ " in the database.";
			} else {
				l_message += "The co-occurrence name you have specified is either already"
					+ " in use or empty. Please make sure that you have specified"
					+ " a co-occurrence name and the co-occurrence name does not already exist"
					+ " in the database.";
			}
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			if (isTradeoff) {
				mbox.setText("Tradeoff Name Is Invalid");
			} else {
				mbox.setText("Co-Occurrence Name Is Invalid");
			}
			mbox.open();
		}
		return false;
	}
	
	/**
	 * Wrap a tradeoff into a logical file
	 */
	public static class Input extends RationaleEditorInput {
		/**
		 * @param explorer RationaleExplorer
		 * @param tree the element in the RationaleExplorer tree for the argument
		 * @param parent the parent of the argument in the RationaleExplorer tree
		 * @param target the argument to associate with the logical file
		 * @param new1 true if the argument is being created, false if it already exists
		 */		
		public Input(RationaleExplorer explorer, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(explorer, tree, parent, target, new1);
		}

		/**
		 * @return the tradeoff wrapped in this logical file
		 */
		public Tradeoff getData() { return (Tradeoff)getAdapter(Tradeoff.class); }
		
		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			if (isTradeoff) {
				return isCreating() ? "New Tradeoff Editor" :
					"Tradeoff: " + getData().getName();
			} else {
				return isCreating() ? "New Co-Occurrence Editor" :
					"Co-Occurrence: " + getData().getName();
			}
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Tradeoff.class;
		}
	}
}

