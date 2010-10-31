package edu.wpi.cs.jburge.SEURAT.reports;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;

import edu.wpi.cs.jburge.SEURAT.decorators.SEURATLightWeightDecorator;
import edu.wpi.cs.jburge.SEURAT.editors.SelectCandidatePatterns;
import edu.wpi.cs.jburge.SEURAT.inference.AlternativePatternInferences;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;
/**
 * This class presents the wizard GUI to the users to generate candidate patterns.
 * @author yechen
 *
 */
public class GenerateCandidatePatternsComposite {
	private Display ourDisplay;
	private Shell ourShell;
	private WizardDialog ourWizard;
	private String ourDecision;
	private GCPWizard ourGCPWizard;
	
	public GenerateCandidatePatternsComposite(Display display, Object selection){
		TreeParent decisionParent = (TreeParent) ((IStructuredSelection) selection).getFirstElement();
		ourDecision = decisionParent.getName();
		ourDisplay = display;
		ourShell = new Shell (ourDisplay, SWT.PRIMARY_MODAL);
		ourGCPWizard = new GCPWizard();
		ourWizard = new WizardDialog(ourShell, ourGCPWizard);
		ourWizard.open();


	}
	
	/**
	 * This is an adaptive method which returns the newly created alternative
	 * for the RationaleExplorer tree to flush.
	 * @return newly generated alternatives
	 */
	public ArrayList<Alternative> getNewlyAddedAlternative() {
		return ourGCPWizard.getNewAlternatives();
	}

	/**
	 * The wizard control of generate candidate pattern wizard GUI.
	 * This one basically determines the forward and back
	 * of the wizard pages.
	 * @author yechen
	 *
	 */
	private class GCPWizard extends Wizard{
		
		private int matchingMethod; //0 for exact matching, 1 for contribution matching.
		//This is set by its private class MethodPage.
		private boolean scopes[]; //scopes controls whether archiecture/design/idiom should
		//be included in the category selection page (next page of scope)
		//scopes[0] for archiecture, scopes[1] for design, and scopes[2] for idiom.
		private HashMap<Integer, String> scopeMap; //Since this is used too many times,
		//in order to increase modifibility, put the map here. It is used to map
		//0 to archiecture, 1 to design, and 2 for idiom.
		
		private ArrayList<Alternative> newAlternatives; //Maintain code compatibility. This
		//creates a list of new alternatives for Wang's code on RationaleExplorer
		//to successfully flush all the data to the tree.
		
		//Due to limitations of the wizard page redraw, we must have the dynamic lists
		//declared here to provide update to those lists...
		private List selectedList, availableList, previewSelectedList;
		private HashMap<String, Integer> categories;
		private HashMap<String, Integer> selectedCategories;
		private PreviewPage previewPage; //used to detect whether we can finish it.
		
		/**
		 * Backward compatibility to Wang's code on RationaleExplorer
		 * @return
		 */
		public ArrayList<Alternative> getNewAlternatives(){
			return newAlternatives;
		}
		
		/**
		 * This method updates the availableList and clears the selectList.
		 * This method should be called every time before switching to the
		 * category selection page.
		 * The method is moved here because it has to be called by other page classes.
		 */
		public void updateData(){
			categories = new HashMap<String, Integer>();
			RationaleDB db = RationaleDB.getHandle();
			for (int i = 0; i < scopes.length; i++){
				if (scopes[i] == true){
					categories.putAll(db.getCategories(scopeMap.get(i)));
				}
			}
			availableList.removeAll();
			selectedList.removeAll();
			//Now, fill up the availableList with the available items from the availablePatterns array list
			Iterator<String> categoryNames = categories.keySet().iterator();
			while (categoryNames.hasNext()){
				availableList.add(categoryNames.next());
			}
		}
		
		/**
		 * This method updates previewSelectedList for the preview page.
		 * It has to be called after clicking "next" in the category selection page.
		 */
		public void updatePreviewList(){
			previewSelectedList.removeAll();
			Iterator<String> selectedValues = selectedCategories.keySet().iterator();
			while (selectedValues.hasNext()){
				previewSelectedList.add(selectedValues.next());
			}
		}
		
		/**
		 * Constructor of this wizard.
		 * Initialize all variables that can be initialized first.
		 */
		public GCPWizard(){
			//Init variables
			scopes = new boolean[3];
			scopeMap = new HashMap<Integer, String>();
			scopeMap.put(0, PatternElementType.ARCHITECTURE.toString());
			scopeMap.put(1, PatternElementType.DESIGN.toString());
			scopeMap.put(2, PatternElementType.IDIOM.toString());
			categories = new HashMap<String, Integer>();
			selectedCategories = new HashMap<String, Integer>();
			
			
			//Add wizard pages.
			addPage(new MethodPage());
			addPage(new ScopePage());
			addPage(new CategorySelectionPage());
			previewPage = new PreviewPage();
			addPage(previewPage);
			this.setWindowTitle("SEURAT Architecture -- Generate Candidate Pattern Wizard");
		}
		
		@Override
		public boolean performFinish() {
			// After this wizard is finished, do the calculation and display
			//the result... Must check whether all pages have been visited first!
			//If not, display error message and does not dispose the dialog.
			
			//Create a list of candidate patterns
			ArrayList<Pattern> matchingPatterns = new ArrayList<Pattern>();
			Iterator<Integer> interestedCategories = selectedCategories.values().iterator();
			RationaleDB db = RationaleDB.getHandle();
			while (interestedCategories.hasNext()){
				matchingPatterns.addAll(db.getPatternByCategoryID(interestedCategories.next()));
			}
			
			//Create a hash table between pattern and their scores
			Hashtable<Pattern, Double> scoreTable =  null;
			if (matchingMethod == 0)
				scoreTable = new AlternativePatternInferences().exactMatching(matchingPatterns);
			else if (matchingMethod == 1)
				scoreTable = new AlternativePatternInferences().contributionMatching(matchingPatterns);
			
			if (scoreTable == null) {
				MessageBox errorMsg = new MessageBox(this.getContainer().getShell(), SWT.ICON_ERROR | SWT.OK);
				errorMsg.setText("Error when generating score");
				errorMsg.setMessage("The selected matching method is invalid or has not yet been implemented.");
				return false;
			}
			
			this.getContainer().getShell().setVisible(false);
			SelectCandidatePatterns selectPatternsGUI = new SelectCandidatePatterns(scoreTable, ourDisplay);
			if (!selectPatternsGUI.getCanceled()){
				Vector<String> selectedPatterns = selectPatternsGUI.getSelections();
				saveSelectedPatterns(selectedPatterns);
			}
			return true;
			
		}
		
		/**
		 * Backward compatible for Wang's code in RationaleExplorer.
		 * @param selected
		 */
		private void saveSelectedPatterns(Vector<String> selected){
			newAlternatives = new ArrayList<Alternative>();
			Decision decision = new Decision();
			decision.fromDatabase(ourDecision);
			Enumeration<String> patterns = selected.elements();
			while(patterns.hasMoreElements()){
				String patternName = patterns.nextElement().toString();
				Pattern pattern = new Pattern();
				pattern.fromDatabase(patternName);						
				while(RationaleDBUtil.isExist(patternName, "Alternatives")){
					patternName = patternName + "~";
				}
				Alternative alter = new Alternative();
				alter.setName(patternName);
				alter.setPatternID(pattern.getID());
				alter.generateFromPattern(decision, matchingMethod);		
				
				newAlternatives.add(alter);
			}
		}
		
		/**
		 * We can only finish this wizard if we are at the preview page.
		 * @return true if we can finish this, and false if we cannot.
		 * @Override Wizard.canFinish():boolean
		 */
		public boolean canFinish(){
			if (this.getContainer().getCurrentPage() != previewPage){
				return false;
			}
			return true;
		}
		
		/**
		 * This is the first page of the wizard.
		 * It allows the user to select whether to use exact matching or to use
		 * contribution matching.
		 * @author yechen
		 *
		 */
		private class MethodPage extends WizardPage{
			private Button[] methods;
			@Override
			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE); //looks like JPanel
				composite.setLayout(new GridLayout(1, false));
				//We can use this if we have a banner...
				//this.setImageDescriptor(ImageDescriptor.createFromFile(SEURATLightWeightDecorator.class, "smallRat.gif"));
				
				Composite contentComp = new Composite(composite, SWT.NONE);
				contentComp.setLayout(new GridLayout(1, false));
				
				new Label (contentComp, SWT.LEFT | SWT.WRAP).setText("Welcome to Generate Candidate Pattern Wizard!"); 
				new Label (contentComp, SWT.LEFT | SWT.WRAP).setText("This wizard will help you select the patterns you want to evaluate based on your rationale.");
				
				new Label (contentComp, SWT.LEFT).setText("");
				new Label (contentComp, SWT.LEFT | SWT.WRAP).setText("How would you like to calculate the patterns?");
				
				Composite buttonComp = new Composite(composite, SWT.NONE);
				buttonComp.setLayout(new GridLayout (1, true));
				
				methods = new Button[2];
				
				methods[0] = new Button(buttonComp, SWT.RADIO);
				methods[0].setSelection(true);
				methods[0].setText("Exact Matching");
				
				methods[1] = new Button(buttonComp, SWT.RADIO);
				methods[1].setText("Contribution Matching");
				setControl(composite);
			}

			public MethodPage(){
				super("Select Matching Method");
			}
			
			/**
			 * This method is not intended to jump to pages!
			 * Instead, this method is used to change the matchingMethod 'global'
			 * variable according to current radio selection...
			 * This method should support future expansion of radio buttons.
			 */
			public IWizardPage getNextPage(){
				
				//Since methods are radio buttons, there is only one of them can be true
				//If true, then break out to avoid useless computations.
				for (int i = 0; i < methods.length; i++){
					if (methods[i].getSelection()){
						matchingMethod = i;
						break;
					}
				}
				return super.getNextPage();
			}
			//Note: public IWizardPage getNextPage() can control which page it jumps to...
		}
		
		/**
		 * This is the second page of the wizard.
		 * This page allows the user to select the scope of the problem categories they're interested in.
		 * @author yechen
		 *
		 */
		private class ScopePage extends WizardPage{
			private Button scopeButtons[];
			@Override
			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				
				Composite labelComp = new Composite(composite, SWT.NONE);
				Composite buttonComp = new Composite(composite, SWT.NONE);
				labelComp.setLayout(new GridLayout(1, false));
				buttonComp.setLayout(new GridLayout(1, true));
				
				new Label(labelComp, SWT.LEFT | SWT.WRAP).setText("  Please select " +
						"the scope of problems of the patterns you want to evalute");
				new Label(labelComp, SWT.LEFT | SWT.WRAP).setText("  Only the problems " + 
						"that are valid for at least one of the scopes will be included " +
						"in the next page.");
				
				scopeButtons = new Button[scopes.length];
				scopeButtons[0] = new Button(buttonComp, SWT.CHECK);
				scopeButtons[0].setText(scopeMap.get(0));
				scopeButtons[1] = new Button(buttonComp, SWT.CHECK);
				scopeButtons[1].setText(scopeMap.get(1));
				scopeButtons[2] = new Button(buttonComp, SWT.CHECK);
				scopeButtons[2].setText(scopeMap.get(2));
				
				setControl(composite);
			}

			public ScopePage(){
				super("Select Problem Scope");
			}
			
			/**
			 * This method is not intended to jump to pages!
			 * Instead, this method is used to change elements in scopes[], For each
			 * element scopes[i] in scopes, scopes[i] == true if and only if 
			 * scopeButtons[i] is selected before going to the next page.
			 * This method should support future expansion of radio buttons.
			 */
			public IWizardPage getNextPage(){
				for (int i = 0; i < scopes.length; i++){
					if (! (i < scopeButtons.length)){
						System.err.println("GCP: Error in Generate Candidate Patterns Composite");
						System.err.println("GCP: Make sure scopeButtons length matches scope's length!");
						break;
					}
					
					if (scopeButtons[i] == null){
						System.err.println("GCP: Scope button is null at " + i);
						break;
					}
					else scopes[i] = scopeButtons[i].getSelection();
				}
				initNextPage();
				return super.getNextPage();
			}
			
			private void initNextPage(){
				IWizardPage nextPage = super.getNextPage();
				updateData();
				nextPage.getControl().redraw();
				nextPage.getControl().update();
			}
		}
		
		/**
		 * This is the third page of the wizard.
		 * Users may select the problem categories that are valid to the scopes here.
		 * @author yechen
		 *
		 */
		private class CategorySelectionPage extends WizardPage{

			private Button moveToSelected, moveToAvailable;

			
			private class ButtonsListener implements SelectionListener{

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.widget == moveToSelected){
						String[] selectedItems = availableList.getSelection();
						for (int i = 0; i < selectedItems.length; i++){
							String tempName = new String(selectedItems[i]);
							selectedList.add(tempName);
							availableList.remove(tempName);
						}
					}
					if (e.widget == moveToAvailable){
						String[] selectedItems = selectedList.getSelection();
						for (int i = 0; i < selectedItems.length; i++){
							String tempName = new String (selectedItems[i]);
							availableList.add(tempName);
							selectedList.remove(tempName);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
				
			}
			

			@Override
			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				//get all available patterns for the selection
				GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				
				//now set up the gui framework
				composite.setLayout(new GridLayout(1, false));
				new Label(composite, SWT.LEFT | SWT.WRAP).setText("Please select the" +
						"problems you would like to evaluate.");
				//This is the panel for the dual-selection list
				Composite selectionComp = new Composite(composite, SWT.NONE);
				selectionComp.setLayout(new GridLayout(5, true));
				selectionComp.setLayoutData(gridData);
				
				new Label(selectionComp, SWT.LEFT | SWT.WRAP).setText("Available Problems");
				new Label(selectionComp, SWT.NONE);
				new Label(selectionComp, SWT.CENTER).setText(" ");
				new Label(selectionComp, SWT.NONE);
				new Label(selectionComp, SWT.RIGHT | SWT.WRAP).setText("Selected Problems");
				availableList = new List(selectionComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalSpan=2;
				gridData.horizontalAlignment = GridData.FILL;
				availableList.setLayoutData(gridData);
				
				//This is panel for the two buttons in the middle of the d-s list
				Composite buttonComp = new Composite(selectionComp, SWT.NONE);
				buttonComp.setLayout(new GridLayout(1, true));
				moveToSelected = new Button(buttonComp, SWT.PUSH | SWT.CENTER);
				moveToAvailable = new Button(buttonComp, SWT.PUSH | SWT.CENTER);
				moveToSelected.setText("=>");
				moveToAvailable.setText("<=");
				ButtonsListener listen = new ButtonsListener();
				moveToSelected.addSelectionListener(listen);
				moveToAvailable.addSelectionListener(listen);
				
				selectedList = new List(selectionComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalSpan=2;
				gridData.horizontalAlignment = GridData.FILL;
				selectedList.setLayoutData(gridData);
				
				setControl(composite);
			}

			public CategorySelectionPage(){
				super ("Select Problem Categories");
			}
			
			/**
			 * This method is not intended to jump to pages!
			 * Instead, this method is used to alter selectedPatterns array list
			 */
			public IWizardPage getNextPage(){
				selectedCategories = new HashMap<String, Integer>();
				String selectedItems[] = selectedList.getItems();
				for (int i = 0; i < selectedItems.length; i++){
					selectedCategories.put(selectedItems[i], categories.get(selectedItems[i]));
				}
				updatePreviewList();
				return super.getNextPage();
			}
		}
		
		/**
		 * This is the last page of the wizard.
		 * Before this wizard passes the arguments to do various calculations, it is a good
		 * idea to let the users to review what they have selected.
		 * @author yechen
		 *
		 */
		private class PreviewPage extends WizardPage{
			
			/**
			 * This one reconstructs the data of the IWizardPage's lists
			 */
			public IWizardPage getPreviousPage(){
				IWizardPage prevPage = super.getPreviousPage();
				updateData();
				prevPage.getControl().redraw();
				prevPage.getControl().update();
				return prevPage;
			}

			@Override
			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				new Label(composite, SWT.WRAP | SWT.CENTER | SWT.BOLD).setText("The patterns are ready to be evaluated.");
				new Label(composite, SWT.WRAP | SWT.LEFT).setText("You have selected the following problems " +
						"to evaluate.");
				new Label(composite, SWT.WRAP | SWT.LEFT).setText("If you would like to modify the selection, you may click on the \"Back\" button now. ");
				new Label(composite, SWT.WRAP | SWT.LEFT).setText("If you would like to proceed, please press Finish button. The wizard will close and you will be presented with evaluated pattern.");
				previewSelectedList = new List(composite, SWT.BORDER | SWT.V_SCROLL);
				previewSelectedList.setEnabled(false);
				GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				previewSelectedList.setLayoutData(gridData);
				setControl(composite);
				setPageComplete(true);
			}

			public PreviewPage(){
				super ("Ready to Match");
			}

		}

	}




}


