package SEURAT.editors;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;


import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.SelectOntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Tactic;
import edu.wpi.cs.jburge.SEURAT.rationaleData.TacticPattern;
import edu.wpi.cs.jburge.SEURAT.views.TacticLibrary;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

public class TacticEditor extends RationaleEditorBase {

	public static final String defaultCategory = "Not Selected!";

	private class DataCache{
		String name, description, category;
		int beh;
	}

	private Text nameField, descArea;
	private Button selectCategoryButton;
	private SelectionListener selSaveListener;
	private Label catDesc;
	private Combo behCombo;
	private List patterns, negQAs;
	Composite ourParent;

	/**
	 * Tactic we are editing.
	 */
	private Tactic ourTactic;

	/**
	 * Member variable storing last known good
	 * values of editable fields.
	 */
	private DataCache dataCache = new DataCache();

	public static RationaleEditorInput createInput(TacticLibrary explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new TacticEditor.Input(explorer, tree, parent, target, new1);
	}

	@Override
	public Class<Tactic> editorType() {
		return Tactic.class;
	}

	@Override
	public RationaleElement getRationaleElement() {
		return ourTactic;
	}

	@Override
	public boolean saveData() {
		//Consistency checker is unnecessary because the uniqueness of names is defined in database as an implicit constraint.
		if (catDesc.getText().equals(defaultCategory)){
			String l_message = "You must select a positive quality attribute for the tactic!";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Tactic Category Is Invalid");
			mbox.open();
			return false;
		}
		OntEntry category = new OntEntry();
		category.fromDatabase(catDesc.getText());
		Vector<String> categoryChildren = RationaleDB.getHandle().getOntology(category.getName());
		if (category.getID() < 0 || categoryChildren.size() > 0 ){
			String l_message = "The positive quality attribute must be a valid leaf node in argument ontology!";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Tactic Category Is Invalid");
			mbox.open();
			return false;
		}

		if (nameField.getText().trim().equals("")){
			String l_message = "The name you have specified is not valid. Please give the tactic a name!";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Name is Invalid");
			mbox.open();
			return false;
		}
		Tactic t = new Tactic();
		t.fromDatabase(ourTactic.getName());
		ourTactic = t;
		ourTactic.setName(nameField.getText());
		ourTactic.setCategory(category);
		ourTactic.setDescription(descArea.getText());
		ourTactic.setTime_behavior(behCombo.getSelectionIndex());
		ourTactic.toDatabase();
		return true;
	}

	/**
	 * Respond to changes which have been made to the alternative
	 * being edited. Also respond to new arguments created which
	 * are children of this alternative.
	 * 
	 * @param pElement the element which generated the event
	 * @param pEvent the rationale update event which describes
	 * 		what caused the event to be generated.
	 */
	public void onUpdate(Tactic pElement, RationaleUpdateEvent pEvent)
	{
		try{
			if (pEvent.getElement().equals(ourTactic)){
				if (pEvent.getDestroyed()){
					closeEditor();
				}
				else if (pEvent.getModified()){
					refreshForm(pEvent);
				}
			}
		} catch (Exception e){
			System.err.println("Error on update of Tactic Editor");
			e.printStackTrace();
		}
	}

	public void onUpdateTacticPattern(TacticPattern tp, RationaleUpdateEvent pEvent){
		refreshTacticPattern(pEvent);
	}

	public void onUpdateNegQA(OntEntry entry, RationaleUpdateEvent pEvent){
		refreshNegQA(pEvent);
	}

	public void refreshTacticPattern(RationaleUpdateEvent pEvent){
		RationaleDB l_db = RationaleDB.getHandle();
		if (ourTactic != null && patterns != null){
			patterns.removeAll();
			Tactic tactic = new Tactic();
			tactic.fromDatabase(ourTactic.getName());
			ourTactic.getPatterns().removeAllElements();
			Vector<TacticPattern> ptV = tactic.getPatterns();
			Iterator<TacticPattern> ptI = ptV.iterator();
			while (ptI.hasNext()){
				TacticPattern pt = ptI.next();

				patterns.add(pt.getPatternName());
				ourTactic.getPatterns().add(pt);

				try{
					if (pEvent != null &&  pEvent.getElement() instanceof TacticPattern && 
							((TacticPattern) (pEvent.getElement())).equals(pt) && pEvent.getModified()){
						l_db.Notifier().Unsubscribe(this, pt);
					}
					l_db.Notifier().Subscribe(pt, this, "onUpdateTacticPattern");
					if (pEvent != null && pEvent.getElement() instanceof TacticPattern && 
							((TacticPattern) (pEvent.getElement())).equals(pt) && pEvent.getDestroyed()){
						l_db.Notifier().Unsubscribe(this, pt);
					}
				} catch (Exception e){
					System.err.println("Unable to subscribe to tactic-pattern under the selected tactic!");
				}
			}
		}
	}

	public void refreshNegQA(RationaleUpdateEvent pEvent){
		RationaleDB l_db = RationaleDB.getHandle();
		if (ourTactic == null || negQAs == null) return;
		negQAs.removeAll();
		ourTactic.getBadEffects().removeAllElements();
		Tactic tactic = new Tactic();
		tactic.fromDatabase(ourTactic.getName());
		Iterator<OntEntry> negI = tactic.getBadEffects().iterator();
		while (negI.hasNext()){
			OntEntry cur = negI.next();

			negQAs.add(cur.getName());
			ourTactic.getBadEffects().add(cur);

			try{
				if ( pEvent != null && pEvent.getElement() instanceof OntEntry && 
						((OntEntry) (pEvent.getElement())).equals(cur) && pEvent.getModified()){
					l_db.Notifier().Unsubscribe(this,cur);
				}
				l_db.Notifier().Subscribe(cur, this, "onUpdateNegQA");
				if ( pEvent != null && pEvent.getElement() instanceof OntEntry && 
						((OntEntry) (pEvent.getElement())).equals(cur) && pEvent.getDestroyed()){
					l_db.Notifier().Unsubscribe(this,cur);
				}
			} catch (Exception e){
				System.err.println("Unable to subscribe to negative QA under the selected tactic!");
			}
		}
		return;
	}

	@Override
	protected void updateFormCache() {
		if (nameField != null){
			dataCache.name = nameField.getText();
		}
		if (descArea != null){
			dataCache.description = descArea.getText();
		}
		if (catDesc != null){
			dataCache.category = catDesc.getText();
		}
		if (behCombo != null){
			dataCache.beh = behCombo.getSelectionIndex();
		}
	}

	/**
	 * Update the fields of the editor with the most current version
	 * of that data. If data in the editor has been changed by the
	 * user that data is not expected to be changed.
	 * 
	 * @param pEvent the event which caused the editor to
	 * 		need refreshed. This argument must be valid.
	 */
	protected void onRefreshForm(RationaleUpdateEvent pEvent){
		boolean l_dirty = isDirty();
		Tactic ourTactic = null;
		if (!isCreating()){
			ourTactic = getTacticFromExplorer();
		}
		if (ourTactic == null) return;
		ourTactic.fromDatabase(ourTactic.getID());

		this.ourTactic = ourTactic;

		if (ourTactic.getCategory() != null){
			if(ourTactic.getCategory().getName().equals(dataCache.category)){
				dataCache.category = ourTactic.getCategory().getName();
			}
			else{
				l_dirty = true;
			}
		}

		if (nameField.getText().equals(dataCache.name)){
			nameField.setText(ourTactic.getName());
			dataCache.name = nameField.getText();
		}
		else
			l_dirty = true;

		if (descArea.getText().equals(dataCache.description)){
			descArea.setText(ourTactic.getDescription());
			dataCache.description = descArea.getText();
		}
		else
			l_dirty = true;

		if (behCombo.getSelectionIndex() == dataCache.beh){
			dataCache.beh = behCombo.getSelectionIndex();
		}
		else
			l_dirty = true;

		setDirty(l_dirty);
	}

	private Tactic getTacticFromExplorer(){
		return (Tactic) getEditorData().getAdapter(Tactic.class);
	}

	@Override
	public void setupForm(Composite parent) {
		if (!isCreating()){
			ourTactic = getTacticFromExplorer();
		}
		else ourTactic = new Tactic();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = false;
		parent.setLayout(gridLayout);

		if (isCreating()){
			ourTactic.setTime_behavior(0);
			ourTactic.setCategory(null);
			if (getTreeParent().getType() == RationaleElementType.TACTICCATEGORY){
				ourTactic.setCategory(new OntEntry());
				ourTactic.getCategory().fromDatabase(getTreeParent().getName());
			}
		}

		new Label(parent, SWT.NONE).setText("Name:");
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourTactic.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Improves:");
		catDesc = new Label(parent, SWT.WRAP);
		if (ourTactic.getCategory() != null && ourTactic.getCategory().getID() >= 0){
			catDesc.setText(ourTactic.getCategory().getName());
		}
		else{
			catDesc.setText(defaultCategory);
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan=4;
		catDesc.setLayoutData(gridData);

		selectCategoryButton = new Button(parent, SWT.PUSH);
		selectCategoryButton.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
		selectCategoryButton.setLayoutData(gridData);
		ourParent = parent;
		selSaveListener = this.getSelNeedsSaveListener();

		selectCategoryButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourParent.getDisplay(), true);
				newOnt = ar.getSelOntEntry();
				if (newOnt != null){
					if (newOnt.getName().equals(TacticPattern.CHANGEONTNAME)){
						String l_message = "The positive quality attribute must not be the tactic impact atrribute!";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(l_message);
						mbox.setText("Tactic Category Is Invalid");
						mbox.open();
						return;
					}

					ourTactic.setCategory(newOnt);
					catDesc.setText(newOnt.toString());
					selSaveListener.widgetSelected(event);
				}
			}
		});

		new Label(parent, SWT.NONE).setText("Description:");
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourTactic.getDescription());
		descArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 25, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = descArea.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Time Behavior:");
		behCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		behCombo.addModifyListener(getNeedsSaveListener());
		for (int i = 0; i < Tactic.behaviorCategories.length; i++){
			behCombo.add(Tactic.behaviorCategories[i]);
		}
		behCombo.select(ourTactic.getTime_behavior());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 5;
		behCombo.setLayoutData(gridData);

		Label patLabel = new Label(parent, SWT.NONE);
		patLabel.setText("Associated Patterns:");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.BEGINNING;
		patLabel.setLayoutData(gridData);

		Label negQALabel = new Label(parent, SWT.NONE);
		negQALabel.setText("Additional Negative QA's:");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.BEGINNING;
		negQALabel.setLayoutData(gridData);

		patterns = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 120;
		refreshTacticPattern(null);
		patterns.setLayoutData(gridData);

		negQAs = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 120;
		refreshNegQA(null);
		negQAs.setLayoutData(gridData);

		//Register Event Notification
		try{
			RationaleDB.getHandle().Notifier().Subscribe(ourTactic, this, "onUpdate");
		}
		catch (Exception e){
			System.err.println("Unable to subscribe on the selected object!");
		}

	}

	public static class Input extends RationaleEditorInput {
		/**
		 * @param explorer RationaleExplorer
		 * @param tree the element in the RationaleExplorer tree for the argument
		 * @param parent the parent of the argument in the RationaleExplorer tree
		 * @param target the argument to associate with the logical file
		 * @param new1 true if the argument is being created, false if it already exists
		 */
		public Input(TacticLibrary tacticLib, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(tacticLib, tree, parent, target, new1);
		}

		/**
		 * @return the requirement wrapped by this logical file
		 */
		public Tactic getData() { return (Tactic)getAdapter(Tactic.class); }

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New Tactic Editor" :
				"Tactic: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Tactic.class;
		}
	}

}
