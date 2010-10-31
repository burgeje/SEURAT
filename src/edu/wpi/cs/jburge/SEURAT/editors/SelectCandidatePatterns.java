package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
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

import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * This class presents the user the GUI to select patterns given a list of available
 * pattern problem categories.
 * <br>
 * There are two locations where we need this: when we're doing candidate pattern selection
 * in rationale explorer and selecting sub-patterns of some pattern in pattern library.
 * <br>
 * As such, there are two constructors for this class. I have a "setupForm" function to
 * set up the basic GUI, and then do the actions, depending on the input of available patterns
 * seperately in the constructor.
 *
 */
public class SelectCandidatePatterns {
	
	private Display subDisplay;
	
	private boolean canceled;
	
	private Shell shell;
	
	private List results;
	
	private Button addButton;
	
	private Button removeButton;
	
	private Button doneButton;
	
	private Button cancelButton;
	
	private Vector<String> selections;
	
	private ArrayList<String> added;
	
	private Tree patterns;
	
	private JScrollPane scrollPane;
	
	private Decision parentDecision;
	
	/**
	 * This method sets up the basic GUI and the remove button's listener.
	 * The constructors are resonsible to set up add, cancel, done listeners and pack.
	 * (Because I don't know the list size right now)
	 * 
	 * Hopefully the code is cleaner to read now.
	 * @param display
	 */
	public void setupForm(Display display){
		canceled = false;
		selections = new Vector<String>();
		subDisplay = display;
		shell = new Shell(subDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Candidate Patterns");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.WRAP | SWT.LEFT).setText("Avaialble Patterns");
		new Label(shell, SWT.NONE);
		
		new Label(shell, SWT.WRAP | SWT.LEFT).setText("Selected Patterns");
		new Label(shell, SWT.NONE);
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		patterns = new Tree(shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		int listHeight = patterns.getItemHeight() * 10;
		int listWidth = patterns.getItemCount() * 2 + 400;
		Rectangle trim = patterns.computeTrim(0, 0, listWidth, listHeight);
		gridData.heightHint = trim.height;
		gridData.widthHint = trim.width;
		patterns.setLayoutData(gridData);
		
		
		results = new List(shell, SWT.SINGLE | SWT.V_SCROLL);

		
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		listHeight = results.getItemHeight() * 10;
		trim = results.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		results.setLayoutData(gridData);
		
		addButton = new Button(shell, SWT.PUSH);
		addButton.setText("=>");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gridData);
		
		removeButton = new Button(shell, SWT.PUSH);
		removeButton.setText("Empty Selected List");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		removeButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				results.removeAll();
				results.redraw();
			}
		});
		removeButton.setLayoutData(gridData);
		
		doneButton = new Button(shell, SWT.PUSH); 
		doneButton.setText("Done");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		doneButton.setLayoutData(gridData);
		
		cancelButton = new Button(shell, SWT.PUSH); 
		cancelButton.setText("Cancel");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		cancelButton.setLayoutData(gridData);
		

	}

	/**
	 * This constructor is for "generate candidate pattern" in rationale explorer.
	 * @param patterns_values
	 * @param display
	 */
	public SelectCandidatePatterns(Hashtable<Pattern, Double> patterns_values, Display display){
		setupForm(display);
		
		TreeItem root = new TreeItem(patterns, SWT.NONE);
		root.setText("Available Patterns (scores)");
		
		//Now, sort the things in the hashtable.
		try {
			//Sorting credit to http://forums.sun.com/thread.jspa?threadID=228509&start=0
			Map.Entry<Pattern, Double>[] sortingArr = (Map.Entry<Pattern, Double>[]) patterns_values.entrySet().toArray(
					new Map.Entry[patterns_values.size()]);
			Arrays.sort(sortingArr, new Comparator<Map.Entry<Pattern, Double>>(){

				@Override
				public int compare(Entry<Pattern, Double> o1,
						Entry<Pattern, Double> o2) {
					//Sort in DESCENDING ORDER, so this is a reversed compareTo!
					int firstCompare = o2.getValue().compareTo(o1.getValue());
					if (firstCompare != 0) return firstCompare;
					//If the values are the same, sort in alphabetical order (ascending).
					return o1.getKey().getName().compareTo(o2.getKey().getName());
				}
				
			});
			
			for (int i = 0; i < sortingArr.length; i++){
				TreeItem item = new TreeItem(root, SWT.NONE);
				item.setText(sortingArr[i].getKey() + ", " + sortingArr[i].getValue());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		

		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) 
			{
				TreeItem[] selected = patterns.getSelection();
				for (int i = 0; i < selected.length; i++){
					String name = selected[i].getText().substring(0, selected[i].getText().indexOf(","));
					selections.add(name);
					results.add(name);
				}
				results.redraw();
			}
		});
		

		doneButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) 
			{
				shell.close();
				shell.dispose();
				System.out.println("Close done.");
				while (!shell.isDisposed()) {
					if (!subDisplay.readAndDispatch()) subDisplay.sleep();
				}
			}
		});
		
		cancelButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) 
			{
				canceled = true;			
				
				shell.close();
				shell.dispose();
				System.out.println("Close cancel");
				while (!shell.isDisposed()) {
					if (!subDisplay.readAndDispatch()) subDisplay.sleep();
				}
			}
		});
		
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}
	
	/**
	 * This constructor is used when adding sub-pattern in pattern library.
	 * @param cans
	 * @param display
	 */
	public SelectCandidatePatterns(Vector<Pattern> cans, Display display){
		setupForm(display);
		
		TreeItem root = new TreeItem(patterns, SWT.NONE);
		root.setText("Matching results and evaluation values");		
		
		Enumeration<Pattern> ps = cans.elements();
		while(ps.hasMoreElements()){
			Pattern pattern = (Pattern)ps.nextElement();
			TreeItem item = new TreeItem(root, SWT.NONE);
			item.setText(pattern.getName());			
		}
		
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) 
			{
				TreeItem[] selected = patterns.getSelection();
				for (int i = 0; i < selected.length; i++){
					String name = selected[i].getText();
					selections.add(name);
					results.add(name);
				}
				results.redraw();
			}
		});
		
		doneButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) 
			{
				//canceled = true;
				
				
				shell.close();
				shell.dispose();
				System.out.println("Close done.");
				while (!shell.isDisposed()) {
					if (!subDisplay.readAndDispatch()) subDisplay.sleep();
				}
			}
		});
		
		cancelButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) 
			{
				canceled = true;			
				
				shell.close();
				shell.dispose();
				System.out.println("Close cancel");
				while (!shell.isDisposed()) {
					if (!subDisplay.readAndDispatch()) subDisplay.sleep();
				}
			}
		});
		
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
	public void setupForm(){
		
	}
	
	public void setCanceled(boolean c){
		canceled = c;
	}
	
	public boolean getCanceled(){
		return canceled;
	}
	
	public Vector<String> getSelections(){
		return selections;
	}

	public ArrayList<String> getAdded() {
		return added;
	}

	public void setAdded(ArrayList<String> added) {
		this.added = added;
	}

	public Tree getPatterns() {
		return patterns;
	}

	public void setPatterns(Tree patterns) {
		this.patterns = patterns;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}
	
	
	
}
