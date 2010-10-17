package SEURAT.editors;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Question;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.views.PatternLibrary;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * This is the workbench editor of Patterns.
 * @author yechen
 *
 */
public class PatternEditor extends RationaleEditorBase {

	private class DataCache{
		String name, url, description, problem, context, solution, implementation, example;
		String type;
		int problemcategory;
	}

	private Text nameField, urlField;

	private Text summaryArea, problemArea, contextArea, solutionArea, implementationArea, exampleArea;

	private List positiveList, negativeList; //The list of positive and negative quality attributes of the pattern

	private Combo typeBox, problemCatBox;

	private String patternType; //the type of the pattern (archi, design, idiom)

	private Pattern ourPattern; //the pattern we're editing

	private HashMap<String, Integer> problemMap; //This is a hashmap between the category id and category name, to increase effiency of saving.



	/**
	 * Member variable storing last known good
	 * values of editable fields.
	 */
	private DataCache dataCache = new DataCache();

	public static RationaleEditorInput createInput(PatternLibrary explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new PatternEditor.Input(explorer, tree, parent, target, new1);
	}

	@Override
	public Class editorType() {
		return Pattern.class;
	}

	@Override
	public RationaleElement getRationaleElement() {
		return ourPattern;
	}

	/**
	 * Update the editor controls in response to a change to the editor.
	 * 
	 * 
	 * @param pElement the pattern which generated the event
	 * @param pEvent a description of what changed in the description
	 */
	public void onUpdate(Pattern pElement, RationaleUpdateEvent pEvent)
	{
		if (ourPattern == null) return;
		try
		{
			if( pEvent.getElement().equals(ourPattern) )
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
			System.out.println("Exception in PatternEditor: onUpdate");
		}
	}

	/**
	 * Used to get the pattern object being modified
	 * @return The pattern object we are modifying.
	 */
	private Pattern getPatternFromExplorer(){
		return (Pattern) getEditorData().getAdapter(Pattern.class);
	}

	@Override
	protected void updateFormCache() {
		if (nameField != null){
			dataCache.name = nameField.getText();
		}
		if (urlField != null){
			dataCache.url = urlField.getText();
		}
		if (summaryArea != null){
			dataCache.description = summaryArea.getText();
		}
		if (problemArea != null){
			dataCache.problem = problemArea.getText();
		}
		if (contextArea != null){
			dataCache.context = contextArea.getText();
		}
		if (solutionArea != null){
			dataCache.solution = solutionArea.getText();
		}
		if (implementationArea != null){
			dataCache.implementation = implementationArea.getText();
		}
		if (exampleArea != null){
			dataCache.example = exampleArea.getText();
		}
		if (ourPattern != null){
			dataCache.problemcategory = ourPattern.getProblemCategory();
			if (ourPattern.getType() != null)
				dataCache.type = ourPattern.getType().toString();
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
	@Override
	protected void onRefreshForm(RationaleUpdateEvent pEvent) {
		boolean l_dirty = isDirty();
		Enumeration iterator;
		int index;
		Pattern ourPattern = getPatternFromExplorer();
		//Probable race condition? Possible that we don't even have this in database...
		if (ourPattern == null) return;
		ourPattern.fromDatabase(ourPattern.getID());

		//Otherwise, we have a newer pattern from database... update the object reference.
		this.ourPattern = ourPattern;

		if (ourPattern.getType() != null){
			if (ourPattern.getType().toString().equals(dataCache.type)){
				dataCache.type = ourPattern.getType().toString();
			}
			else
				l_dirty = true;
		}
		
		if (ourPattern.getProblemCategory() == dataCache.problemcategory){
			fillCategories();
			dataCache.problemcategory = ourPattern.getProblemCategory();
		}
		else
			l_dirty = true;

		if (nameField.getText().equals(dataCache.name)){
			nameField.setText(ourPattern.getName());
			dataCache.name = nameField.getText();
		}
		else
			l_dirty = true;

		if (urlField.getText().equals(dataCache.url)){
			urlField.setText(ourPattern.getUrl());
			dataCache.url = urlField.getText();
		}
		else
			l_dirty = true;

		if (summaryArea.getText().equals(dataCache.description)){
			summaryArea.setText(ourPattern.getDescription());
			dataCache.description = summaryArea.getText();
		}
		else
			l_dirty = true;

		if (problemArea.getText().equals(dataCache.problem)){
			problemArea.setText(ourPattern.getProblem());
			dataCache.problem = problemArea.getText();
		}
		else
			l_dirty = true;

		if (contextArea.getText().equals(dataCache.context)){
			contextArea.setText(ourPattern.getContext());
			dataCache.context = contextArea.getText();
		}
		else
			l_dirty = true;

		if (solutionArea.getText().equals(dataCache.solution)){
			solutionArea.setText(ourPattern.getSolution());
			dataCache.solution = solutionArea.getText();
		}
		else
			l_dirty = true;

		if (implementationArea.getText().equals(dataCache.implementation)){
			implementationArea.setText(ourPattern.getImplementation());
			dataCache.implementation = implementationArea.getText();
		}
		else
			l_dirty = true;

		if (exampleArea.getText().equals(dataCache.example)){
			exampleArea.setText(ourPattern.getExample());
			dataCache.example = exampleArea.getText();
		}
		else
			l_dirty = true;

		setDirty(l_dirty);
	}




	/**
	 * The GUI.
	 */
	public void setupForm(Composite parent) {
		if (!isCreating())
			ourPattern = getPatternFromExplorer();
		else ourPattern = new Pattern();
		ChangeListener modifiedListener = getNeedsSaveListener();
		//Get pattern, and change listener first to provide more efficiency.
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns=6;
		gridLayout.marginHeight=5;
		gridLayout.makeColumnsEqualWidth=true;
		parent.setLayout(gridLayout);

		if (isCreating())
		{
			String parentType = this.getTreeParent().getName();
			if (parentType.equals("Architectural Patterns")){
				patternType = PatternElementType.ARCHITECTURE.toString();
			}
			else if (parentType.equals("Design Patterns")){
				patternType = PatternElementType.DESIGN.toString();
			}
			else if (parentType.equals("Idioms")){
				patternType = PatternElementType.IDIOM.toString();
			}
		}
		else {
			patternType = ourPattern.getType().toString();
		}

		new Label(parent, SWT.NONE).setText("Name:");

		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourPattern.getName());
		nameField.addModifyListener(modifiedListener);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		nameField.setLayoutData(gridData);

		/*		
		new Label(parent, SWT.NONE).setText("Type:");
		typeBox = new Combo(parent, SWT.NONE);
		fillTypes();
		typeBox.addModifyListener(modifiedListener);
		 */

		new Label(parent, SWT.NONE).setText("Category: ");
		problemCatBox = new Combo(parent, SWT.NONE);
		fillCategories();
		problemCatBox.addModifyListener(modifiedListener);


		new Label(parent, SWT.NONE).setText("Online Resource URL:");	
		urlField = new Text(parent, SWT.SINGLE|SWT.BORDER);
		urlField.setText(ourPattern.getUrl());
		urlField.addModifyListener(modifiedListener);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(urlField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		urlField.setLayoutData(gridData);


		new Label(parent, SWT.NONE).setText("Description:");
		summaryArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		summaryArea.setText(ourPattern.getDescription());
		summaryArea.addModifyListener(modifiedListener);
		gridData = createTextAreaGridData(summaryArea);
		DisplayUtilities.setTextDimensions(summaryArea, gridData, 50, 5);
		summaryArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Problem: ");
		problemArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		problemArea.setText(ourPattern.getProblem());
		problemArea.addModifyListener(modifiedListener);
		gridData = createTextAreaGridData(problemArea);
		DisplayUtilities.setTextDimensions(problemArea, gridData, 50, 5);
		problemArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Context");
		contextArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		contextArea.setText(ourPattern.getContext());
		contextArea.addModifyListener(modifiedListener);
		gridData = createTextAreaGridData(contextArea);
		DisplayUtilities.setTextDimensions(contextArea, gridData, 50, 5);
		contextArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Solution");
		solutionArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		solutionArea.setText(ourPattern.getSolution());
		solutionArea.addModifyListener(modifiedListener);
		gridData = createTextAreaGridData(solutionArea);
		DisplayUtilities.setTextDimensions(solutionArea, gridData, 50, 5);
		solutionArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Implementation");
		implementationArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		implementationArea.setText(ourPattern.getImplementation());
		implementationArea.addModifyListener(modifiedListener);
		gridData = createTextAreaGridData(implementationArea);
		DisplayUtilities.setTextDimensions(implementationArea, gridData, 50, 5);
		implementationArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Example");
		exampleArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		exampleArea.setText(ourPattern.getExample());
		exampleArea.addModifyListener(modifiedListener);
		gridData = createTextAreaGridData(exampleArea);
		DisplayUtilities.setTextDimensions(exampleArea, gridData, 50, 5);
		exampleArea.setLayoutData(gridData);

		//Attributes...
		Label attLabel = new Label(parent, SWT.NONE);
		attLabel.setText("Affected Attributes");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan=6;
		gridData.horizontalAlignment=SWT.CENTER;
		attLabel.setLayoutData(gridData);

		Label posLabel = new Label(parent, SWT.NONE);
		posLabel.setText("Positive Attributes");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan=3;
		posLabel.setLayoutData(gridData);

		Label negLabel = new Label(parent, SWT.NONE);
		negLabel.setText("Negative Attributes");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan=3;
		negLabel.setLayoutData(gridData);

		positiveList = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		//Fill the list of positive attributes...
		if (ourPattern.getPosiOnts() != null){
			Vector pos = ourPattern.getPosiOnts();
			Enumeration patterns =pos.elements();
			while (patterns.hasMoreElements())
			{
				positiveList.add(patterns.nextElement().toString());
			}	
		}else{
			positiveList = null;
		}
		positiveList.setLayoutData(gridData);

		negativeList = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		//Fill the list of negative attributes...
		if (ourPattern.getNegaOnts() != null){
			Enumeration patterns = ourPattern.getPosiOnts().elements();
			while (patterns.hasMoreElements()){
				negativeList.add(patterns.nextElement().toString());
			}
		}
		else negativeList = null;
		negativeList.setLayoutData(gridData);

		updateFormCache();
	}

	/**
	 * This will fill up the problemCatBox with all problem categories.
	 * It will also select the one that's being used.
	 */
	private void fillCategories(){
		if (problemCatBox == null) return;
		problemCatBox.removeAll();
		RationaleDB db = RationaleDB.getHandle();

		int ourCategory = ourPattern.getProblemCategory();
		problemMap = db.getCategories(patternType.toString());

		Iterator<String> categories = problemMap.keySet().iterator();
		while (categories.hasNext()){
			String currentCategory = categories.next();
			problemCatBox.add(currentCategory);
			if (problemMap.get(currentCategory) == ourCategory)
				problemCatBox.select(problemCatBox.indexOf(currentCategory));
		}
	}

	/**
	 * This will fill up typebox with the most recent type amounts
	 * @deprecated This should no longer be useful. Another feature will be implemented soon.
	 */
	private void fillTypes(){
		if (typeBox==null) return;
		typeBox.removeAll();
		Enumeration typeEnum = PatternElementType.elements();
		int typeIndex = 0;
		PatternElementType patternType;
		while (typeEnum.hasMoreElements()){
			patternType = (PatternElementType) typeEnum.nextElement();
			typeBox.add(patternType.toString());
			if(patternType.toString().compareTo(ourPattern.getType().toString()) == 0){
				typeBox.select(typeIndex);
			}
			typeIndex++;
		}
	}


	/**
	 * This creates a default setup of GridData that is used 
	 * @return GridData to be used for create text area.
	 */
	private GridData createTextAreaGridData(Text textArea){
		GridData result = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		result.horizontalSpan = 5;
		result.grabExcessHorizontalSpace = true;
		result.heightHint = textArea.getLineHeight() * 3;
		result.horizontalAlignment = GridData.FILL;
		return result;
	}



	public static class Input extends RationaleEditorInput {
		/**
		 * @param explorer RationaleExplorer
		 * @param tree the element in the RationaleExplorer tree for the argument
		 * @param parent the parent of the argument in the RationaleExplorer tree
		 * @param target the argument to associate with the logical file
		 * @param new1 true if the argument is being created, false if it already exists
		 */
		public Input(PatternLibrary patternLib, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(patternLib, tree, parent, target, new1);
		}

		/**
		 * @return the requirement wrapped by this logical file
		 */
		public Pattern getData() { return (Pattern)getAdapter(Pattern.class); }

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New Pattern Editor" :
				"Pattern: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Pattern.class;
		}
	}


	@Override
	/**
	 * This function is used to save form information to the database, it is called
	 * every time the form is saved, regardless of whether the element is being
	 * created or just being modified.
	 * Implementations should return true after the call to the database and return
	 * false at the end of the method (to handle multiple cases where a save might
	 * be invalid).
	 */
	public boolean saveData() {
		//TODO
		ConsistencyChecker checker = new ConsistencyChecker(ourPattern.getID(), nameField.getText(), "patterns");
		if (!nameField.getText().trim().equals("") &&
				(ourPattern.getName() == nameField.getText() || checker.check(false))){
			//Valid pattern name
			//Pattern name consistent with DB... Update record.
			ourPattern.setName(nameField.getText());
			ourPattern.setType(PatternElementType.fromString(patternType));
			//ourPattern.setType(PatternElementType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));
			ourPattern.setUrl(urlField.getText());
			ourPattern.setDescription(summaryArea.getText());
			ourPattern.setProblem(problemArea.getText());
			ourPattern.setContext(contextArea.getText());
			ourPattern.setSolution(solutionArea.getText());
			ourPattern.setImplementation(implementationArea.getText());
			ourPattern.setExample(exampleArea.getText());
			ourPattern.setProblemCategory(problemMap.get(problemCatBox.getItem(problemCatBox.getSelectionIndex())));
			ourPattern.toDatabase(ourPattern.getID());
			return true;
		}
		else{
			//Invalid pattern name
			String l_message = "The pattern name you have specified is either already"
				+ " in use or empty. Please make sure that you have specified"
				+ " a pattern name and the pattern name does not already exist"
				+ " in the database.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Pattern Name Is Invalid");
			mbox.open();
		}
		return false;
	}

}
