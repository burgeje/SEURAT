package SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;

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

	private Text nameField, urlField;
	
	private Text summaryArea, problemArea, contextArea, solutionArea, implementationArea, exampleArea;
	
	private List positiveList, negativeList; //The list of positive and negative quality attributes of the pattern
	
	private Button submitButton, cancelButton;
	
	private Combo typeBox;	
	
	
	
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
		// TODO Auto-generated method stub
		return getPattern();
	}
	
	/**
	 * Respond to changes made to a question.
	 * 
	 * @param pElement The question which has changed
	 * @param pEvent A description of the changes made to the question
	 */
	public void onUpdate(Question pElement, RationaleUpdateEvent pEvent)
	{
		try
		{
			if( pEvent.getElement().equals(getPattern()) )
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
	
	private Pattern getPattern(){
		return (Pattern) getEditorData().getAdapter(Pattern.class);
	}

	public void setupForm(Composite parent) {
		Pattern ourPattern = getPattern();
		ChangeListener modifiedListener = getNeedsSaveListener();
		//Get pattern, and change listener first to provide more efficiency.
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns=1;
		gridLayout.marginHeight=5;
		gridLayout.makeColumnsEqualWidth=true;
		parent.setLayout(gridLayout);
		
		if (isCreating())
		{
			getPattern().setType(PatternElementType.ARCHITECTURE);
		}
		
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourPattern.getName());
		nameField.addModifyListener(modifiedListener);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		nameField.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Type:");
		typeBox = new Combo(parent, SWT.NONE);
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
		typeBox.addModifyListener(modifiedListener);
		
		new Label(parent, SWT.NONE).setText("Online Resource URL:");	
		urlField = new Text(parent, SWT.SINGLE|SWT.BORDER);
		urlField.setText(ourPattern.getUrl());
		urlField.addModifyListener(modifiedListener);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(urlField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		
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
		
		//TODO Keep going!
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
		
		new Label(parent, SWT.NONE).setText("Affected Attributes");
		
		updateFormCache();

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
	public boolean saveData() {
		// TODO Auto-generated method stub
		return false;
	}

}
