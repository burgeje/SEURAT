package SEURAT.editors;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.ReasonGUI;
import edu.wpi.cs.jburge.SEURAT.rationaleData.CandidateRationale;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.CandidateRationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.CandidateTreeParent;

import SEURAT.events.RationaleUpdateEvent;

/**
 * This class provides the editor for CandidateRationales.
 */
public class CandidateRationaleEditor extends CandidateRationaleEditorBase {
	public static CandidateRationaleEditorInput createInput(CandidateRationaleExplorer explorer, CandidateTreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new CandidateRationaleEditor.Input(explorer, tree, parent, target, new1);
	}

	/**
	 * This class provides caching features used when updating
	 * properties of an CandidateRationale remotely.
	 */
	private class DataCache
	{
		/**
		 * last known good name for the CandidateRationale
		 */
		public String name;
		/**
		 * last known good description for the CandidateRationale
		 */
		public String description;
		/**
		 * last known source document (web page, etc.) where the item came from
		 */
		private String source;
	}
	
	/**
	 * The name of the CandidateRationale
	 */
	private Text nameField;
	
	/**
	 * The description field
	 */
	private Text descArea;
	
	
	/**
	 * The parent
	 */
	private Composite parent;
	
	/**
	 * member variable that stores all data that is
	 * cached for remote updates of CandidateRationale data
	 */
	private DataCache dataCache = new DataCache();
	
	/**
	 * Respond to changes which have been made to the CandidateRationale
	 * being edited. Also respond to new CandidateRationales created which
	 * are children of this CandidateRationale.
	 * 
	 * @param pElement the CandidateRationale which generated the event
	 * @param pEvent the rationale update event which describes
	 * 		what caused the event to be generated.
	 */
	public void onUpdate(CandidateRationale pElement, RationaleUpdateEvent pEvent)
	{
		try
		{
			if( pEvent.getElement() != pElement &&
				pEvent.getCreated() &&
				pEvent.getElement() instanceof CandidateRationale )
			{
				refreshCandidateRationales(pEvent);
			}
			else
			if( pEvent.getElement().equals(getCandidateRationale()) )
			{
				if( pEvent.getDestroyed() )
				{
					closeEditor();
				}
				else
				if( pEvent.getModified() )
				{
					refreshForm(pEvent);
				}
			}			
		}
		catch( Exception eError )
		{
			System.out.println("Exception in CandidateRationaleEditor: onUpdate");
		}
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#editorType()
	 */
	public Class editorType() {
		return CandidateRationale.class;
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#getRationaleElement()
	 */
	public RationaleElement getRationaleElement() {
		return getCandidateRationale();
	}

	/**
	 * @return the CandidateRationale which is the logical file of this editor
	 */
	public CandidateRationale getCandidateRationale() {
		return (CandidateRationale)getEditorData().getAdapter(CandidateRationale.class);
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#onRefreshForm(SEURAT.events.RationaleUpdateEvent)
	 */
	@Override
	protected void onRefreshForm(RationaleUpdateEvent pEvent) {
		Enumeration iter = null;
		int index = 0;
		boolean l_dirty = isDirty();
		
		// Something Has Changed, Reload This Element From The DB
		getCandidateRationale().fromDatabase(getCandidateRationale().getID());
		
		// Only Change Fields That Have Not Been Modified
		if( nameField.getText().equals(dataCache.name) )
		{
			nameField.setText(getCandidateRationale().getName());
			dataCache.name = nameField.getText();
		}
		else
			l_dirty = true;
		
		if( descArea.getText().equals(dataCache.description))
		{
			descArea.setText(getCandidateRationale().getDescription());
			dataCache.description = descArea.getText();
		}
		else
			l_dirty = true;
		
		setDirty(l_dirty);
	}


	
	
	/**
	 * Rebuild the listboxes containing the set of all children
	 * of the CandidateRationale being edited.
	 * 
	 * @param pEvent the rationaleUpdateEvent which caused this
	 * function to be called. This CandidateRationale may be null.
	 */
	public void refreshCandidateRationales(RationaleUpdateEvent pEvent)
	{
		boolean l_dirty = isDirty();
		
		// Something Has Changed, Reload This Element From The DB
		getCandidateRationale().fromDatabase(getCandidateRationale().getID());
		
		// A New CandidateRationale Has Been Created As A Child Of This Requirement,
		// With Lack Of A Keen Understanding Of How CandidateRationales Are
		// Identified As For And Against, We Will Regenerate The
		// Entire List Of CandidateRationales For This Requirement
		Vector CandidateRationales;
		String subscribeFunc = null;
		Enumeration iterator;
		

		
		// The New CandidateRationale Will Not Publish Further Events Using
		// The Parent As A Pseudonym, therefore we must subscribe to it's
		// publications
		try
		{			
			if (pEvent != null )
			{
				RationaleDB l_db = RationaleDB.getHandle();
				CandidateRationale l_target = (CandidateRationale)pEvent.getElement();
				if(subscribeFunc != null)
				{
					// If the subscriberFunc is being changed then
					// we need to remove the existing subscription first.
					//
					// TODO There is room for a new (change subscription) method
					// 		in the publish subscribe manager for this purpose.
					if( pEvent.getModified() )
						l_db.Notifier().Unsubscribe(this, l_target);
					
					l_db.Notifier().Subscribe(l_target, this, subscribeFunc);
				}
				else
				if( pEvent.getDestroyed() )
				{
					l_db.Notifier().Unsubscribe(this, l_target);
				}
			}
		}
		catch( Exception e )
		{
			System.out.println("CandidateRationale Editor: Created CandidateRationale Subscription Failure");
		}
		
		setDirty(l_dirty);
	}
	

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#setupForm(org.eclipse.swt.widgets.Composite)
	 */
	public void setupForm(Composite parent) {	
		this.parent = parent;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
	/*	
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getCandidateRationale().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		DisplayUtilities.setTextDimensions(nameField, gridData, 150);
		nameField.setLayoutData(gridData);
		*/
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getCandidateRationale().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);
/*		
		new Label(parent, SWT.NONE).setText("Description:");
		
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getCandidateRationale().getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData,100, 2);
		gridData.horizontalSpan = 2;
		descArea.setLayoutData(gridData);
		*/
		new Label(parent, SWT.NONE).setText("Description:");
		
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getCandidateRationale().getDescription());
		descArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 50, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = descArea.getLineHeight() * 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Source:");
		Text srcArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		srcArea.setText(getCandidateRationale().getSource());
		srcArea.setEditable(false);		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(srcArea, gridData,100, 2);
		gridData.horizontalSpan = 5;
		srcArea.setLayoutData(gridData);
		// Register Event Notification
		try
		{
			RationaleDB.getHandle().Notifier().Subscribe(getCandidateRationale(), this, "onUpdate");
		}
		catch( Exception e )
		{
			System.out.println("CandidateRationale Editor: CandidateRationale Updates Not Available!");
		}
		
		updateFormCache();
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#updateFormCache()
	 */
	protected void updateFormCache()
	{
		if( nameField != null )
			dataCache.name = nameField.getText();
		
		if( descArea != null )
			dataCache.description = descArea.getText();
		
		
	}
		
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#saveData()
	 */
	public boolean saveData() {
		ConsistencyChecker checker = new ConsistencyChecker(getCandidateRationale().getID(), nameField.getText(), "candidates");
		
		if(!nameField.getText().trim().equals("") &&
				(getCandidateRationale().getName().equals(nameField.getText()) || checker.check(false)))
		{
			if (isCreating()) {
				// Set the CandidateRationale's parent data correctly
				getCandidateRationale().setParent(getParentElement().getID());

					getCandidateRationale().setName(nameField.getText());
					getCandidateRationale().setDescription(descArea.getText());


					getCandidateRationale().setID(getCandidateRationale().toDatabase(getCandidateRationale().getParent(), true));
					return true;

			} else {
				boolean changeOk = true;
				
				if(getCandidateRationale().getName().equals(nameField.getText()) || checker.check())
				{
					getCandidateRationale().setName(nameField.getText());
					getCandidateRationale().setDescription(descArea.getText());
					
						//since this is a save, not an add, the type and parent are ignored
						//				System.out.println("Saving CandidateRationale from edit-update");
						getCandidateRationale().setID(getCandidateRationale().toDatabase(getCandidateRationale().getParent(), false));
						return true;
				}
			}
		}
		else
		{
			String l_message = "";
			l_message += "The CandidateRationale name you have specified is either already"
				+ " in use or empty. Please make sure that you have specified"
				+ " an CandidateRationale name and the CandidateRationale name does not already exist"
				+ " in the database.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("CandidateRationale Name Is Invalid");
			mbox.open();
		}
		return false;
	}
	
	/** 
	 * Used to ask the user questions when saving the CandidateRationale. 
	 * These could be status questions (to supply a history) or ask for confirmation
	 * if they try to select a previously rejected CandidateRationale
	 * @param message
	 * @return the response to the dialog
	 */
	private boolean showQuestion(String message) {
		return MessageDialog.openQuestion(
				parent.getShell(),
				"Save CandidateRationale",
				message);
	}
	
	/**
	 * Wraps an CandidateRationale into a logical file.
	 */
	public static class Input extends CandidateRationaleEditorInput {
		
		/**
		 * @param explorer CandidateRationaleExplorer
		 * @param tree the element in the CandidateRationaleExplorer tree for the CandidateRationale
		 * @param parent the parent of the CandidateRationale in the CandidateRationaleExplorer tree
		 * @param target the CandidateRationale to associate with the logical file
		 * @param new1 true if the CandidateRationale is being created, false if it already exists
		 */
		public Input(CandidateRationaleExplorer explorer, CandidateTreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(explorer, tree, parent, target, new1);
		}

		/**
		 * @return the CandidateRationale that is wrapped by the logical file
		 */
		public CandidateRationale getData() { return (CandidateRationale)getAdapter(CandidateRationale.class); }
		
		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New CandidateRationale Editor" :
				"CandidateRationale: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == CandidateRationale.class;
		}		
	}
}

