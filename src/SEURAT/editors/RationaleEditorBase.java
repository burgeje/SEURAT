package SEURAT.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.*;

import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.PatternLibrary;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * The base class for the new SEURAT rationale editors.  Extends the Eclipse EditorPart class,
 * which is a base class for all workbench editors.  Also provides common save methods
 * and various helper methods.
 */
public abstract class RationaleEditorBase extends EditorPart 
	implements IPartListener
{
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partActivated(IWorkbenchPart part) {	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partBroughtToTop(IWorkbenchPart part) {		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partClosed(IWorkbenchPart part) {		
		if( part == this )
		{
			// Each Editor Needs To unsubscribe Itself from
			// the RationaleDB update managers when it is closed
			// So that it can be garbage collected and to
			// minimize the number of subscriptions in the
			// update managers.
			RationaleDB.getHandle().Notifier().Unsubscribe(this);
		}			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partDeactivated(IWorkbenchPart part) {		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	public void partOpened(IWorkbenchPart part) {
		if( part == this )
		{
			//System.out.println("I was opened");
		}
	}

	/**
	 * State variable describing whether the editor needs resaved
	 */
	private boolean dirty;
	/**
	 * listener for most SWT controls used to update the
	 * dirty state of the editor.
	 */
	private ChangeListener onChangeForceSave = new ChangeListener();
	/**
	 * Listener for checkbox controls uesd to update the
	 * dirty state of the editor.
	 */
	private SelChangeListener onSelChangeForceSave = new SelChangeListener();
	
	/**
	 * This class updates the 'dirty' status of the editor to
	 * reflect changes made to the form by the user. Every
	 * AWT element which the user can change should have this
	 * element attached (call addModifyListener).
	 */
	public class ChangeListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			setDirty(true);
		}
	}
	
	/**
	 * This class does the same thing as the ChangeListener but
	 * implements the SelectionListener interface instead.  Required
	 * for save updating with check boxes.
	 */
	public class SelChangeListener implements SelectionListener {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			setDirty(true);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			setDirty(true);
		}
	}
	
	/**
	 * Get an instance of a ModifyListener subclass which will automatically
	 * changed the status of the editor when form elements are modified.
	 * 
	 * @return An instance of the ChangeListener class
	 */
	public ChangeListener getNeedsSaveListener() { return onChangeForceSave; }
	
	/**
	 * Get an instance of a SelectionListener subclass which will automatically
	 * changed the status of the editor.
	 * 
	 * @return An instance of the SelChangeListener class
	 */
	public SelChangeListener getSelNeedsSaveListener() { return onSelChangeForceSave; }
	
	/**
	 * Get the rationale element that we are editing.
	 * Must be implemented by all subclasses.
	 */
	
	public abstract RationaleElement getRationaleElement();
	
	/**
	 * This function is used to save form information to the database, it is called
	 * every time the form is saved, regardless of whether the element is being
	 * created or just being modified.
	 * Implementations should return true after the call to the database and return
	 * false at the end of the method (to handle multiple cases where a save might
	 * be invalid).
	 */
	public abstract boolean saveData();
	
	/**
	 * This function is used to perform any actions related only to creating new
	 * elements. This includes adding the newly created element to the tree.
	 */
	public TreeParent saveNew() {
		//////////////////////////////////////////////
		// ---> Get Necessarry Data From Logical File
		RationaleExplorer explorer;
		PatternLibrary pattern;
		RationaleElement parentElement;
		explorer = (RationaleExplorer)getEditorInput().getAdapter(RationaleExplorer.class);
		
		if (explorer == null){
			pattern = (PatternLibrary)getEditorInput().getAdapter(PatternLibrary.class);
			//parentElement = (RationaleElement)getEditorInput().getAdapter(RationaleElement.class);
			TreeParent parentTree = getTreeParent();
			TreeParent newEle = pattern.createUpdate(parentTree, getRationaleElement());
			return newEle;
		}
		
		parentElement = (RationaleElement)getEditorInput().getAdapter(RationaleElement.class);
		
		TreeParent parentTree;
		parentTree = (TreeParent)getEditorInput().getAdapter(TreeParent.class);

		TreeParent newEle = explorer.createUpdate(parentTree, getRationaleElement());
		return newEle;
	}
	
	/**
	 * This function is used to perform any actions specific to editing an element,
	 * including updating the task list and making any changes to elements in the
	 * rationale tree that have been modified.
	 */
	public void saveExisting() {
		//////////////////////////////////////////////
		// ---> Get Necessarry Data From Logical File
		RationaleExplorer explorer;
		PatternLibrary pattern;
		TreeParent parentTree;
		parentTree = (TreeParent)getEditorInput().getAdapter(TreeParent.class);
		
		explorer = (RationaleExplorer)getEditorInput().getAdapter(RationaleExplorer.class);
		if (explorer == null){
			pattern = (PatternLibrary)getEditorInput().getAdapter(PatternLibrary.class);
			parentTree = pattern.editUpdate(parentTree, getRationaleElement());
			((RationaleEditorInput) getEditorInput()).setTreeParent(parentTree);
			return;
		}
	
		
		// <--- Get Necessarry Data From Logical File
		//////////////////////////////////////////////
		
		// Use Rationale Explorer Helper To Update Tree
		parentTree = explorer.editUpdate(parentTree, getRationaleElement());
		
		((RationaleEditorInput)getEditorInput()).setTreeParent(parentTree);
	}

	/**
	 * Get The Class Object Of The Type Of RationaleElement This Editor
	 * Supports
	 * 
	 * @return a class object representing the only RationaleElement type that
	 * this editor can edit correctly.
	 */
	public abstract Class editorType();
	
	/**
	 * Creates the editor form.
	 * 
	 * @param pParent The AWT composite component which should be used
	 * 		as the parent of the editor
	 */
	public abstract void setupForm(Composite pParent);
	
	/**
	 * Called after each save to ensure the editors cache
	 * is correctly updated to indicate the values most recently
	 * saved.
	 */
	protected void updateFormCache()
	{
	}
	
	/**
	 * This function is called by Eclipse when the user attempts to save.
	 * Will not do anything if the save is invalid.
	 */
	public void doSave(IProgressMonitor monitor) {	
		try
		{
			boolean saveOK = saveData();
			
			if (saveOK) {
				if( isCreating() ) {
					TreeParent p = saveNew();
					getEditorData().setCreated();
					getEditorData().setTreeParent(p);
					this.setPartName(getEditorData().getName());
				} else {
					saveExisting();
				}		
				setDirty(false);
				updateFormCache();
				refreshForm(null);
			}
		}
		catch( Exception eError )
		{
			System.out.println("Exception In RationaleEditorBase: doSave");
			eError.printStackTrace();
			eError.printStackTrace();
		}
	}
	
	/**
	 * Called By Eclipse To Create The Editors UI. This function
	 * automatically set's up the things it can and delegates
	 * the form creation part to [[Function Name Here]]
	 */
	public void createPartControl(Composite parent) {
		// Force The Editor To Fill It's Window Region With The First
		// Control It Receives
		parent.setLayout(new FillLayout());
		
		// Create The Scroll Based Container As The Only Component Of The Editor Window
		ScrolledComposite scrollableEditorContainer = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		scrollableEditorContainer.setLayout(new FillLayout());
		scrollableEditorContainer.setExpandHorizontal(true);
		scrollableEditorContainer.setExpandVertical(true);
		
		// Create A Simple Composite Object To Store The Editor Layout
		// And Make It The Only Component Of THe Scrollable Composite
		Composite editorContainer = new Composite(scrollableEditorContainer, SWT.NONE);
		
		setupForm(editorContainer);

		// After The Editor Container Has Been Initialized 
		// Force The Scrolled Container To Use The Editor Container
		// As It's Only Component
		scrollableEditorContainer.setMinSize(editorContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrollableEditorContainer.setContent(editorContainer);
		
		setDirty(isCreating());
	}

	
	/**
	 * Helper Method For Retrieving A Proerply Casted Instance Of The
	 * Input Data.
	 * 
	 * @return A Type Casted Version Of The Editors Input Data
	 */
	public RationaleEditorInput getEditorData() {
		return (RationaleEditorInput)getEditorInput();
	}
	/**
	 * Helper Method For Retrieving a Properly Casted Instance Of
	 * The Rationale Element Which is A Parent to the current one in
	 * the rationale tree.
	 * 
	 * @return a RationaleElement which is the parent of the one being edited
	 */
	public RationaleElement getParentElement() {
		return (RationaleElement)getEditorData().getAdapter(RationaleElement.class);
	}
	
	/**
	 * Helper Method For Finding The Active Rationale Explorer
	 * 
	 * @return A RationaleExplorer object which is the one that is displaying
	 * the RationaleElement being edited
	 */
	public RationaleExplorer getExplorer() {
		return (RationaleExplorer)getEditorData().getAdapter(RationaleElement.class);
	}
	/**
	 * Helper Method For Finding The Tree Element Which owns the current Ratioanle Element
	 * 
	 * @return return a TreeParent object which is the node in the RationaleExplorer which
	 * 	contains the TreeNode that pertains to the RationaleElement being edited.
	 */
	public TreeParent getTreeParent() {
		return (TreeParent)getEditorData().getAdapter(TreeParent.class);
	}
	
	/**
	 * Check whether the input of the editor is currently being edited or is currently waiting
	 * to be created for the first time.
	 * 
	 * @return true if the rationale element does not exist in the database and needs created and
	 * false if the rationale element is in the database and just needs updated
	 */
	public boolean isCreating() {
		return getEditorData().isCreating();
	}
	
	/**
	 * Used by eclipse to determine whether the editor has been modified since
	 * the last save. (If this function returns true the asterisk
	 * will be shown next to the filename in the tab)
	 */
	public boolean isDirty() {
		// TODO Updated This Method To Only Return TRUE When Content Has Changed
		return dirty;
	}
	
	/**
	 * Set The 'Modified' State of the editor. An editor is dirty if it has
	 * been modified since the last save, and therefore must be resaved
	 * for any changes to take affect.
	 * 
	 * @param pDirty true if the editor needs resaved and false if the editor
	 * 		has not changed since the last save
	 */
	public void setDirty(boolean pDirty) {
		dirty = pDirty;
		firePropertyChange(PROP_DIRTY);
	}
	
	/**
	 * Sets up some properties of the underlying editor interface and
	 * ensures that the editor can display the given information.
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (!(input instanceof RationaleEditorInput))
			throw new PartInitException("Invalid Input: Must be RationaleEditorInput");
		RationaleEditorInput l_input = (RationaleEditorInput)input;
		
		if( !l_input.targetType(editorType()) )
			throw new PartInitException("Invalid Rationale Element - Editor Interaction");
		
		setSite(site);
		this.setInput(input);
		this.setPartName(input.getName());
		
		// Register This As A Part Listener To Get Notifications
		// Of Open / Close / Focus Events
		this.getSite().getPage().addPartListener(this);
	}
	
	/**
	 * Save-As is not currently allowed for rationale elements, therefore this
	 * function is empty.
	 */
	public void doSaveAs() {
		// It is not possible to save-as in this editor
	}

	/**
	 * Used by eclipse to decide whether to allow save-as... this is currently disabled
	 * for all editors.
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	
	/**
	 * Called When The Editor Recieves Focus, this is currently
	 * not used by the base editor
	 */
	public void setFocus() {		
	}

	/**
	 * Call this method to re-load rationale element data from
	 * the editor. This function will not do anything if the
	 * element has not yet been saved to the database.
	 * 
	 * This function is also not expected to update fields
	 * which have been modified but not saved.
	 * 
	 * @param pEvent
	 */
	public void refreshForm(RationaleUpdateEvent pEvent) {
		// Don't apply a refresh if the element hasn't been saved to the DB yet
		if( getRationaleElement().getID() == -1 )
			return;
		
		onRefreshForm(pEvent);
	}
	
	/**
	 * Close the editor, generates a partClosed event
	 * that invokes the IPartListener interface of this editor
	 * also.
	 */
	public void closeEditor()
	{
		getSite().getPage().closeEditor(this, false);
	}
	
	/**
	 * Update the fields of the editor with the most current version
	 * of that data. If data in the editor has been changed by the
	 * user that data is not expected to be changed.
	 * 
	 * @param pEvent the event which caused the editor to
	 * 		need refreshed. This argument must be valid.
	 */
	protected void onRefreshForm(RationaleUpdateEvent pEvent) {
	}
}
