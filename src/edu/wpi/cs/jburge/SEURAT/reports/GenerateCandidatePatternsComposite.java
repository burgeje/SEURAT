package edu.wpi.cs.jburge.SEURAT.reports;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternEvalScore;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
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
	
	/**
	 * Constructor for the class, opens the wizard.
	 * @param display
	 * @param selection Must be a decision!
	 */
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
	 * Given a parent swt widget component, construct a visual tree representation
	 * of a PatternEvalScore model.
	 * Note: The layout of the tree is not set by this method. It can be customized later.
	 * @param parent The parent widget component
	 * @param eval The model to construct from
	 * @return Tree representation of eval
	 */
	public TreeItem constructPatternEvalSubtree(Tree parent, PatternEvalScore eval){
		TreeItem ret = new TreeItem(parent, SWT.NONE);
		
		int numSati = eval.getNumSatisfactions();
		int numViol = eval.getNumViolations();
		if (numViol > 0) ret.setForeground(ourDisplay.getSystemColor(SWT.COLOR_RED));
		ret.setText(eval.toString());
		
		TreeItem satiRoot = new TreeItem(ret, SWT.NONE);
		satiRoot.setText("Addresses " + numSati + " Requirements");
		TreeItem satiCat = new TreeItem(satiRoot, SWT.NONE);
		satiCat.setText("Exact (" + eval.getExactSati().size() + ")");
		for (Requirement req : eval.getExactSati()){
			TreeItem cur = new TreeItem(satiCat, SWT.NONE);
			cur.setText(req.getName() + ": " + req.getOntology());
		}
		satiCat = new TreeItem(satiRoot, SWT.NONE);
		satiCat.setText("Contributing (" + eval.getContribSati().size() + ")");
		for (Requirement req : eval.getContribSati()){
			TreeItem cur = new TreeItem(satiCat, SWT.NONE);
			cur.setText(req.getName() + ": " + req.getOntology());
		}
		satiCat = new TreeItem(satiRoot, SWT.NONE);
		satiCat.setText("Potential (" + eval.getPossibleSati().size() + ")");
		for (Requirement req : eval.getPossibleSati()){
			TreeItem cur = new TreeItem(satiCat, SWT.NONE);
			cur.setText(req.getName() + ": " + req.getOntology());
		}
		
		TreeItem violRoot = new TreeItem(ret, SWT.NONE);
		violRoot.setText("Violates " + numViol +  " Requirements");
		TreeItem violCat = new TreeItem(violRoot, SWT.NONE);
		violCat.setText("Exact (" + eval.getExactViol().size() + ")");
		for (Requirement req : eval.getExactViol()){
			TreeItem cur = new TreeItem(violCat, SWT.NONE);
			cur.setText(req.getName() + ": " + req.getOntology());
		}
		violCat = new TreeItem(violRoot, SWT.NONE);
		violCat.setText("Contributing (" + eval.getContribViol().size() + ")");
		for (Requirement req : eval.getContribViol()){
			TreeItem cur = new TreeItem(violCat, SWT.NONE);
			cur.setText(req.getName() + ": " + req.getOntology());
		}
		violCat = new TreeItem(violRoot, SWT.NONE);
		violCat.setText("Potential (" + eval.getPossibleViol().size() + ")");
		for (Requirement req : eval.getPossibleViol()){
			TreeItem cur = new TreeItem(violCat, SWT.NONE);
			cur.setText(req.getName() + ": " + req.getOntology());
		}
		
		
		return ret;
	}

	/**
	 * The wizard control of generate candidate pattern wizard GUI.
	 * This one basically determines the forward and back
	 * of the wizard pages.
	 * @author yechen
	 *
	 */
	private class GCPWizard extends Wizard{
		
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
		private List selectedList, availableList;
		private List availablePatternsList, selectedPatternsList;
		private Tree patternDetails;
		
		private HashMap<String, Integer> categories;
		private HashMap<String, Integer> selectedCategories;
		private HashMap<String, PatternEvalScore> candidates;
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
		public void updateCategories(){
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
		
		public void updatePatternLists(){
			candidates = new HashMap<String, PatternEvalScore>();
			RationaleDB db = RationaleDB.getHandle();
			for (String cat: selectedCategories.keySet()){
				ArrayList<Pattern> patterns = db.getPatternByCategory(cat);
				for (Pattern p: patterns){
					PatternEvalScore s = new PatternEvalScore(p);
					s.contributionMatching();
					candidates.put(s.toString(), s);
				}
			}
			availablePatternsList.removeAll();
			selectedPatternsList.removeAll();
			patternDetails.removeAll();
			for (String s: candidates.keySet()){
				availablePatternsList.add(s);
			}
			
			//Sort available pattern list
			Vector<String> toSort = new Vector<String>();
			for (String s: availablePatternsList.getItems()){
				toSort.add(s);
			}
			Collections.sort(toSort, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					PatternEvalScore s1 = candidates.get(o1), s2 = candidates.get(o2);
					return s2.compareTo(s1);
				}
			});
			
			availablePatternsList.removeAll();
			for (String s: toSort){
				availablePatternsList.add(s);
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
			candidates = new HashMap<String, PatternEvalScore>();
			
			
			//Add wizard pages.
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

			
			this.getContainer().getShell().setVisible(false);
			Vector<String> selected = new Vector<String>();
			for (String fName: selectedPatternsList.getItems()){
				String pName = candidates.get(fName).getPattern().getName();
				selected.add(pName);
			}
			saveSelectedPatterns(selected);
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
				alter.generateFromPattern(decision);
				
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
		 * This is the second page of the wizard.
		 * This page allows the user to select the scope of the problem categories they're interested in.
		 * @author yechen
		 *
		 */
		private class ScopePage extends WizardPage{
			private Button scopeButtons[];
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
				updateCategories();
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

				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
				
			}
			

			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				//get all available patterns for the selection
				GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				
				//now set up the gui framework
				composite.setLayout(new GridLayout(1, false));
				new Label(composite, SWT.LEFT | SWT.WRAP).setText("Please select the" +
						" problem categories you would like to evaluate.");
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
				updatePatternLists();
				return super.getNextPage();
				
				
			}
		}
		
		/**
		 * This is the last page of the wizard.
		 * In this page, the user enters the patterns to be exported to the rationale explorer.
		 * Scores will be displayed in a tree-structure.
		 * @author yechen
		 *
		 */
		private class PreviewPage extends WizardPage{
			private Button moveToSelected, moveToAvailable;
			
			/**
			 * This one reconstructs the data of the IWizardPage's lists
			 */
			public IWizardPage getPreviousPage(){
				IWizardPage prevPage = super.getPreviousPage();
				updateCategories();
				prevPage.getControl().redraw();
				prevPage.getControl().update();
				return prevPage;
			}

			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				new Label(composite, SWT.WRAP | SWT.LEFT).setText("If you would like to modify the selection, you may click on the \"Back\" button now. ");
				new Label(composite, SWT.WRAP | SWT.LEFT).setText("Select the patterns you would like to consider as alternatives for the decision. The details will be shown in a expandable tree.");
				
				GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				
				//This is the panel for the dual-selection list
				Composite selectionComp = new Composite(composite, SWT.NONE);
				selectionComp.setLayout(new GridLayout(5, true));
				selectionComp.setLayoutData(gridData);
				
				
				new Label(selectionComp, SWT.LEFT | SWT.WRAP).setText("Available Patterns");
				new Label(selectionComp, SWT.NONE);
				new Label(selectionComp, SWT.CENTER).setText(" ");
				new Label(selectionComp, SWT.NONE);
				new Label(selectionComp, SWT.RIGHT | SWT.WRAP).setText("Selected Patterns");
				
				availablePatternsList = new List(selectionComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalSpan=2;
				gridData.horizontalAlignment = GridData.FILL;
				availablePatternsList.setLayoutData(gridData);
				
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
				
				selectedPatternsList = new List(selectionComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalSpan=2;
				gridData.horizontalAlignment = GridData.FILL;
				selectedPatternsList.setLayoutData(gridData);
				
				new Label(composite, SWT.WRAP).setText("Details of the selected patterns are shown below.");
				patternDetails = new Tree(composite, SWT.V_SCROLL | SWT.H_SCROLL);
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				gridData.heightHint = 200;
				patternDetails.setLayoutData(gridData);
				
				
				gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				setControl(composite);
				setPageComplete(true);
			}

			public PreviewPage(){
				super ("Ready to Match");
			}
			
			private class ButtonsListener implements SelectionListener{

				public void widgetSelected(SelectionEvent e) {
					if (e.widget == moveToSelected){
						String[] selectedItems = availablePatternsList.getSelection();
						for (int i = 0; i < selectedItems.length; i++){
							String tempName = new String(selectedItems[i]);
							selectedPatternsList.add(tempName);
							availablePatternsList.remove(tempName);
						}
					}
					if (e.widget == moveToAvailable){
						String[] selectedItems = selectedPatternsList.getSelection();
						for (int i = 0; i < selectedItems.length; i++){
							String tempName = new String (selectedItems[i]);
							availablePatternsList.add(tempName);
							selectedPatternsList.remove(tempName);
						}
					}
					
					//Sort available pattern list
					Vector<String> toSort = new Vector<String>();
					for (String s: availablePatternsList.getItems()){
						toSort.add(s);
					}
					Collections.sort(toSort, new Comparator<String>() {

						@Override
						public int compare(String o1, String o2) {
							PatternEvalScore s1 = candidates.get(o1), s2 = candidates.get(o2);
							return s2.compareTo(s1);
						}
					});
					
					availablePatternsList.removeAll();
					for (String s: toSort){
						availablePatternsList.add(s);
					}
					
					//Sort selected pattern list
					toSort = new Vector<String>();
					for (String s: selectedPatternsList.getItems()){
						toSort.add(s);
					}
					Collections.sort(toSort, new Comparator<String>() {

						@Override
						public int compare(String o1, String o2) {
							PatternEvalScore s1 = candidates.get(o1), s2 = candidates.get(o2);
							return s2.compareTo(s1);
						}
					});
					
					selectedPatternsList.removeAll();
					for (String s: toSort){
						selectedPatternsList.add(s);
					}
					
					//Fill in the tree
					patternDetails.removeAll();
					for (String s: selectedPatternsList.getItems()){
						constructPatternEvalSubtree(patternDetails, candidates.get(s));
					}
					
					
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
				
			}

		}

	}




}


