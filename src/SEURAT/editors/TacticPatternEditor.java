package SEURAT.editors;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;


import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.SelectPattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Tactic;
import edu.wpi.cs.jburge.SEURAT.rationaleData.TacticPattern;
import edu.wpi.cs.jburge.SEURAT.views.TacticLibrary;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * This editor can be used to edit tactic-pattern in the tactic library.
 * It MUST be called by right clicking a tactic if it creates a new one!
 * @author yechen
 *
 */
public class TacticPatternEditor extends RationaleEditorBase {
	
	private static final String defaultPattern = "Not Selected!";
	
	private class DataCache{
		String description;
		String pattern;
		String score, numChanges;
		int sc, bc;
	}
	
	/**
	 * TacticPattern we are editing
	 */
	private TacticPattern ourTacticPattern;
	
	private Combo scCombo, bcCombo;
	private Composite ourParent;
	private SelectionListener selSaveListener;
	private Text description, score, numChanges;
	private Button computeScore, selectPattern;
	private Label patternLabel;
	private Label tacticLabel;
	
	/**
	 * Member variable storing last known good
	 * values of editable fields.
	 */
	private DataCache dataCache = new DataCache();
	
	/**
	 * Respond to changes which have been made to the alternative
	 * being edited. Also respond to new arguments created which
	 * are children of this alternative.
	 * 
	 * @param pElement the element which generated the event
	 * @param pEvent the rationale update event which describes
	 * 		what caused the event to be generated.
	 */
	public void onUpdate(TacticPattern pElement, RationaleUpdateEvent pEvent)
	{
		try{
			if (pEvent.getElement().equals(ourTacticPattern)){
				if (pEvent.getDestroyed()){
					closeEditor();
				}
				else if (pEvent.getModified()){
					refreshForm(pEvent);
				}
				else if (pEvent.getCreated()){
					ourTacticPattern.fromDatabase(TacticPattern.combineNames(ourTacticPattern.getPatternName(), 
							ourTacticPattern.getTacticName()));
					refreshForm(pEvent);
				}
			}
		} catch (Exception e){
			System.err.println("Error on update of Tactic-Pattern Editor");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void updateFormCache() {
		if (description != null){
			dataCache.description = description.getText();
		}
		if (score != null){
			dataCache.score = score.getText();
		}
		if (scCombo != null){
			dataCache.sc = scCombo.getSelectionIndex();
		}
		if (bcCombo != null){
			dataCache.bc = bcCombo.getSelectionIndex();
		}
		if (patternLabel != null){
			dataCache.pattern = patternLabel.getText();
		}
		if (numChanges != null){
			dataCache.numChanges = numChanges.getText();
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
		TacticPattern tp = null;
		if (!isCreating()){
			tp = getTacticPatternFromExplorer();
		}
		if (tp == null) return;
		tp.fromDatabase(tp.getID());
		
		this.ourTacticPattern = tp;
		
		if (scCombo.getSelectionIndex() == dataCache.sc){
			dataCache.sc = scCombo.getSelectionIndex();
		}
		else l_dirty = true;
		
		if (bcCombo.getSelectionIndex() == dataCache.bc){
			dataCache.bc = bcCombo.getSelectionIndex();
		}
		else l_dirty = true;
		
		if (description.getText().equals(dataCache.description)){
			dataCache.description = description.getText();
		}
		else l_dirty = true;
		
		if (score.getText().equals(dataCache.score)){
			dataCache.score = score.getText();
		}
		else l_dirty = true;
		
		if (patternLabel.getText().equals(dataCache.pattern)){
			dataCache.pattern = patternLabel.getText();
		}
		else l_dirty = true;
		
		if (numChanges.getText().equals(dataCache.numChanges)){
			dataCache.numChanges = numChanges.getText();
		}
		else l_dirty = true;
		
		setDirty(l_dirty);
	}
	
	public static RationaleEditorInput createInput(TacticLibrary explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		if (!new1){
			target.fromDatabase(parent.getName());
		}
		return new TacticPatternEditor.Input(explorer, tree, parent, target, new1);
	}

	@Override
	public RationaleElement getRationaleElement() {
		return ourTacticPattern;
	}


	@Override
	public Class editorType() {
		return TacticPattern.class;
	}

	@Override
	public void setupForm(Composite parent) {
		if (!isCreating()){
			ourTacticPattern = getTacticPatternFromExplorer();
		}
		else ourTacticPattern = new TacticPattern();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		new Label(parent, SWT.NONE).setText("Tactic Name:");
		tacticLabel = new Label(parent, SWT.NONE);
		if (isCreating())
			tacticLabel.setText(getTreeParent().getName());
		else
			tacticLabel.setText(ourTacticPattern.getTacticName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tacticLabel.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Pattern Name:");
		patternLabel = new Label(parent, SWT.NONE);
		if (!isCreating())
			patternLabel.setText(ourTacticPattern.getPatternName());
		else patternLabel.setText(defaultPattern);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan=4;
		patternLabel.setLayoutData(gridData);
		
		selectPattern = new Button(parent, SWT.PUSH);
		selectPattern.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
		selectPattern.setLayoutData(gridData);
		ourParent = parent;
		selSaveListener = this.getSelNeedsSaveListener();
		selectPattern.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				Pattern newPattern = null;
				SelectPattern sp = new SelectPattern(ourParent.getDisplay());
				newPattern = sp.getSelPattern();
				if (newPattern != null){
					ourTacticPattern.setPatternID(newPattern.getID());
					patternLabel.setText(newPattern.getName());
					selSaveListener.widgetSelected(event);
				}
			}
		});
		
		new Label(parent, SWT.NONE).setText("Strctural Changes:");
		scCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		fillComboBox(scCombo);
		scCombo.select(ourTacticPattern.getStruct_change());
		scCombo.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan=2;
		gridData.grabExcessHorizontalSpace=true;
		scCombo.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Behavioral Changes:");
		bcCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		fillComboBox(bcCombo);
		bcCombo.select(ourTacticPattern.getBeh_change());
		bcCombo.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan=2;
		gridData.grabExcessHorizontalSpace=true;
		bcCombo.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Number Of Changes:");
		numChanges = new Text(parent, SWT.SINGLE | SWT.BORDER);
		numChanges.addModifyListener(getNeedsSaveListener());
		numChanges.setText("" + ourTacticPattern.getNumChanges());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		gridData.grabExcessHorizontalSpace=true;
		numChanges.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Overall (0-10):");
		score = new Text(parent, SWT.SINGLE | SWT.BORDER);
		score.addModifyListener(getNeedsSaveListener());
		score.setText("" + ourTacticPattern.getOverallScore());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		gridData.grabExcessHorizontalSpace=true;
		score.setLayoutData(gridData);
		
		
		computeScore = new Button(parent, SWT.PUSH);
		computeScore.setText("Auto-Scoring");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan=2;
		computeScore.setLayoutData(gridData);
		computeScore.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int numChangesInt = Integer.parseInt(numChanges.getText());
				if (numChangesInt < 0){
					String l_message = "Number of changes has to be positive!";
					MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
					mbox.setMessage(l_message);
					mbox.setText("Number of changes is Invalid");
					mbox.open();
					return;
				}
				if (bcCombo.getSelectionIndex() < 0 || scCombo.getSelectionIndex() < 0) {
					String l_message = "You must first select the structure and behavior change types!";
					MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
					mbox.setMessage(l_message);
					mbox.setText("Change Types Invalid");
					mbox.open();
					return;
				}
				//TODO Verify!
				int computed = TacticPattern.AUTOSCORECHANGEBASE[scCombo.getSelectionIndex()];
				int mode = scCombo.getSelectionIndex();
				computed += TacticPattern.AUTOSCORECHANGEDELTA[mode] * numChangesInt;
				if (computed > 10) computed = 10;
				boolean isValid = false;
				int response = SWT.NO;
				if (TacticPattern.AUTOSCOREMAXNUMCHANGES[mode] < 0 || 
						numChangesInt <= TacticPattern.AUTOSCOREMAXNUMCHANGES[mode]){
					isValid = true;
				}
				if (!isValid){
					String l_message = "Number of Changes exceeded maximum allowed by the selected change type." + 
					"(Entered: " + numChangesInt + ", Max: " + TacticPattern.AUTOSCOREMAXNUMCHANGES[mode]
					+ ") Do you want to continue?";
					MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					mbox.setText("Number of Changes Out of Range");
					mbox.setMessage(l_message);
					response = mbox.open();
				}
				if (isValid || response == SWT.YES)
					score.setText("" + computed);
			}
		});
		
		new Label(parent, SWT.NONE).setText("Description:");
		description = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		description.setText(ourTacticPattern.getDescription());
		description.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(description, gridData, 25, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = description.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		description.setLayoutData(gridData);
		
		//Register Event Notification
		try{
			RationaleDB.getHandle().Notifier().Subscribe(ourTacticPattern, this, "onUpdate");
		}
		catch (Exception e){
			System.err.println("Unable to subscribe on the selected object!");
		}
		
	}
	
	public void fillComboBox(Combo combo){
		if (combo == null) return;
		for (int i = 0; i < TacticPattern.CHANGECATEGORIES.length; i++){
			combo.add(TacticPattern.CHANGECATEGORIES[i]);
		}
	}
	
	@Override
	public boolean saveData() {
		if (patternLabel.getText().equals(defaultPattern)){
			String l_message = "You must select a pattern before saving!";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Pattern Is Invalid");
			mbox.open();
			return false;
		}
		if (scCombo.getSelectionIndex() < 0 ||  bcCombo.getSelectionIndex() < 0){
			String l_message = "You must make a valid selection on the changes combo box! " + 
			"(If more than one type of change is needed, select the type with overall score the highest.) ";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Structure/Behavioral Changes Is Invalid");
			mbox.open();
			return false;
		}
		try{
			int score = Integer.parseInt(this.score.getText());
			int numChanges = Integer.parseInt(this.numChanges.getText());
			if (score < 0 || numChanges < 0){
				String l_message = "Both the overall score and the number of changes has to be positive!";
				MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
				mbox.setMessage(l_message);
				mbox.setText("Overall changes score is Invalid");
				mbox.open();
				return false;
			}
			ourTacticPattern.setStruct_change(scCombo.getSelectionIndex());
			ourTacticPattern.setBeh_change(bcCombo.getSelectionIndex());
			ourTacticPattern.setDescription(description.getText());
			ourTacticPattern.setOverallScore(score);
			ourTacticPattern.setNumChanges(numChanges);
			
			Tactic tactic = new Tactic();
			tactic.fromDatabase(tacticLabel.getText());
			Pattern pattern = new Pattern();
			pattern.fromDatabase(patternLabel.getText());
			ourTacticPattern.setTacticID(tactic.getID());
			ourTacticPattern.setPatternID(pattern.getID());
			ourTacticPattern.toDatabase();
			return true;
		} catch (NumberFormatException e){
			String l_message = "The overall changes must be an integer!";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Overall changes score is Invalid");
			mbox.open();
			return false;
		}
	}
	
	private TacticPattern getTacticPatternFromExplorer(){
		return (TacticPattern) getEditorData().getAdapter(TacticPattern.class);
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
		public TacticPattern getData() { return (TacticPattern)getAdapter(TacticPattern.class); }

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New TacticPattern Editor" :
				"TacticPattern: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == TacticPattern.class;
		}
	}
}
