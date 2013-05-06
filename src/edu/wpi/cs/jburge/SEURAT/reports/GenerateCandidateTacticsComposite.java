/*	This code belongs to the SEURAT project as written by Dr. Janet Burge
    Copyright (C) 2013  Janet Burge

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package edu.wpi.cs.jburge.SEURAT.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.wpi.cs.jburge.SEURAT.editors.SelectOntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Tactic;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

public class GenerateCandidateTacticsComposite {
	private Display ourDisplay;
	private Shell ourShell;
	private WizardDialog ourWizard;
	private String ourDecision;
	private GCTWizard ourGCTWizard;
	private static Tree ontologyTree;

	/**
	 * Constructor of the class, opens the wizard.
	 * @param display
	 * @param selection Must be a decision!
	 */
	public GenerateCandidateTacticsComposite(Display display, Object selection){
		TreeParent decisionParent = (TreeParent) ((IStructuredSelection) selection).getFirstElement();
		ourDecision = decisionParent.getName();
		ourDisplay = display;
		ourShell = new Shell (ourDisplay, SWT.PRIMARY_MODAL);
		ourGCTWizard = new GCTWizard();
		ourWizard = new WizardDialog(ourShell, ourGCTWizard);
		ourWizard.open();
	}

	/**
	 * This is an adaptive method which returns the newly created alternative
	 * for the RationaleExplorer tree to flush.
	 * @return newly generated alternatives
	 */
	public ArrayList<Alternative> getNewlyAddedAlternative() {
		return ourGCTWizard.getNewAlternatives();
	}

	private class GCTWizard extends Wizard{

		private ArrayList<Alternative> newAlternatives; //These are new tactic-alternatives to be added
		private Vector<OntEntry> selectedCategories = null; //Which quality attribute the tactic should improve on?
		private Vector<Alternative> selectedPatternAlts = null; //Which pattern alternatives should we try to generate score with?
		private List selectedList, availableList, previewSelectedList;
		private HashMap<String, Tactic> tactics;
		private PreviewPage previewPage;
		private List altList;

		@Override
		public boolean performFinish() {
			Decision dec = new Decision();
			dec.fromDatabase(ourDecision);

			newAlternatives = new ArrayList<Alternative>();
			String[] interestedTactics = selectedList.getItems();
			for (int i = 0; i < interestedTactics.length; i++){
				Tactic cur = new Tactic();
				cur.fromDatabase(interestedTactics[i]);
				String tacticName = cur.getName();
				while(RationaleDBUtil.isExist(tacticName, "Alternatives")){
					tacticName += "~";
				}
				Alternative alt = new Alternative();
				alt.setName(tacticName);
				alt.generateFromTactic(dec, cur, selectedPatternAlts);
				newAlternatives.add(alt);
			}
			return true;
		}

		public ArrayList<Alternative> getNewAlternatives(){
			return newAlternatives;
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
		 * This method updates the availableList and clears the selectList.
		 * This method should be called every time before switching to the
		 * category selection page.
		 * The method is moved here because it has to be called by other page classes.
		 */
		public void updateData(){
			tactics = new HashMap<String, Tactic>();
			RationaleDB db = RationaleDB.getHandle();
			if (selectedCategories == null) {
				System.err.println("Selected category is null during update data???");
				return;
			}
			if (selectedList != null){
				selectedList.removeAll();
				availableList.removeAll();
			}
			for (int i = 0; i < selectedCategories.size(); i++){
				OntEntry curEntry = selectedCategories.get(i);
				Iterator<TreeParent> treeParentsI = db.getTactics(curEntry.getName()).iterator();
				while (treeParentsI.hasNext()){
					String curName = treeParentsI.next().getName();
					Tactic curTactic = new Tactic();
					curTactic.fromDatabase(curName);
					if (curTactic.getID() < 0){
						System.err.println("Invalid tactic during update data???");
						return;
					}
					tactics.put(curName, curTactic);
					availableList.add(curName);
				}
			}
		}

		public void updatePreviewList(){
			previewSelectedList.removeAll();
			String[] selectedValues = selectedList.getItems();
			for (int i = 0 ; i < selectedValues.length; i++){
				previewSelectedList.add(selectedValues[i]);
			}
		}

		public GCTWizard(){
			tactics = new HashMap<String, Tactic>();

			addPage (new CategorySelectionPage());
			addPage (new PatternSelectionPage());
			addPage (new TacticSelectionPage());
			previewPage = new PreviewPage();
			addPage(previewPage);
			setWindowTitle("Generate Candidate Tactic Wizard");

		}

		private class CategorySelectionPage extends WizardPage{
			public CategorySelectionPage(){
				super("Select Category");
			}

			public void createControl(Composite parent) {
				selectedCategories = new Vector<OntEntry>();
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));

				new Label (composite, SWT.LEFT | SWT.WRAP).setText("Welcome to Generate Candidate Tactic Wizard!"); 
				new Label (composite, SWT.LEFT | SWT.WRAP).setText("");
				new Label (composite, SWT.LEFT | SWT.WRAP).setText("Please select the positive quality attribute of tactics.");

				ontologyTree = new Tree(composite, SWT.MULTI | SWT.V_SCROLL| SWT.H_SCROLL);
				TreeItem root = new TreeItem(ontologyTree, SWT.NONE);
				root.setText("Argument-Ontology");
				SelectOntEntry.populateTree(root, "Argument-Ontology");
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
				ontologyTree.setLayoutData(gridData);

				setControl(composite);

			}

			public IWizardPage getNextPage(){
				RationaleDB db = RationaleDB.getHandle();
				TreeItem[] selected = ontologyTree.getSelection();
				if (selectedCategories != null){
					selectedCategories.removeAllElements();

					for (int i = 0; i < selected.length; i++){
						selectedCategories = db.getOntologyLeaves(selected[i].getText());
					}
				}

				return super.getNextPage();
			}
		}

		private class PatternSelectionPage extends WizardPage{
			public PatternSelectionPage(){
				super("Select Pattern");
			}

			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				selectedPatternAlts = new Vector<Alternative>();

				//Get a vector of all alternatives generated from pattern
				RationaleDB db = RationaleDB.getHandle();
				Vector<Alternative> patternAlts = db.getAlternativesFromPattern("Architecture");
				
				//TODO
				new Label(composite, SWT.LEFT | SWT.WRAP).setText("Select patterns that may affect the tactic. You must generate the patterns as alternatives first.");
				altList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
				GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				altList.setLayoutData(gridData);
				for (Alternative pa : patternAlts){
					altList.add(pa.getName());
				}
				

				setControl(composite);

			}

			public IWizardPage getNextPage(){
				selectedPatternAlts.removeAllElements();
				for (String s : altList.getSelection()){
					Alternative pa = new Alternative();
					pa.fromDatabase(s);
					selectedPatternAlts.add(pa);
				}
				updateData();
				return super.getNextPage();
			}
		}

		private class TacticSelectionPage extends WizardPage{

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

			public TacticSelectionPage(){
				super ("Select Tactic");
			}

			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));

				new Label(composite, SWT.LEFT | SWT.WRAP).setText("Please select the tactic you wish to evaluate.");
				Composite selectionComp = new Composite(composite, SWT.NONE);
				selectionComp.setLayout(new GridLayout(5, true));
				GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = GridData.FILL;
				selectionComp.setLayoutData(gridData);

				new Label(selectionComp, SWT.LEFT | SWT.WRAP).setText("Available Tactics");
				new Label(selectionComp, SWT.NONE);
				new Label(selectionComp, SWT.CENTER).setText(" ");
				new Label(selectionComp, SWT.NONE);
				new Label(selectionComp, SWT.RIGHT | SWT.WRAP).setText("Selected Tactics");
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

			/**
			 * This method is not intended to jump to pages!
			 * Instead, this method is used to alter selectedPatterns array list
			 */
			public IWizardPage getNextPage(){
				updatePreviewList();
				return super.getNextPage();
			}
		}

		private class PreviewPage extends WizardPage{

			public PreviewPage(){
				super("Preview Selections");
			}

			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NONE);
				composite.setLayout(new GridLayout(1, false));
				new Label(composite, SWT.WRAP | SWT.CENTER | SWT.BOLD).setText("The tactics are ready to be evaluated");
				new Label(composite, SWT.WRAP | SWT.LEFT).setText("You have selected the following tactics " +
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

		}

	}
}
