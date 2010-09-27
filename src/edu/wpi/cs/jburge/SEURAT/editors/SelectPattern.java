package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Decision;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

public class SelectPattern {
	
	private Display subDisplay;
	
	private boolean canceled;
	
	private Shell shell;
	
	private List results;
	
	private Button addButton;
	
	private Button cancelButton;
	
	private Vector<String> selections;
	
	private ArrayList<String> added;
	
	private Tree patterns;
	
	private JScrollPane scrollPane;
	
	private Decision parentDecision;

	public SelectPattern(Pattern pattern, Display display){
//		parentDecision = new Decision();
//		parentDecision.fromDatabase(((Decision)selection).getName());
		
		canceled = false;
		selections = new Vector<String>();
		subDisplay = display;
		shell = new Shell(subDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Matching results");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		patterns = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		TreeItem root = new TreeItem(patterns, SWT.NONE);
		root.setText("Matching Results and their scores");
		
//		try {
//			ArrayList patternsList = new ArrayList(patterns_values.entrySet());
//			Iterator itr = patternsList.iterator();
//			String key = "";
//			Double value = 0.0;
//			//int cnt = 0;
//			while (itr.hasNext()) {
//
//				//cnt++;
//				Map.Entry e = (Map.Entry) itr.next();
//
//				key = ((Pattern) e.getKey()).getName();
//				value = ((Double) e.getValue()).doubleValue();
//
//				TreeItem item = new TreeItem(root, SWT.NONE);
//				item.setText(key + ",  " + value);
//
//				//System.out.println(key + "," + value);
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		int listHeight = patterns.getItemHeight() * 10;
		Rectangle trim = patterns.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		patterns.setLayoutData(gridData);
		
		
		results = new List(shell, SWT.SINGLE | SWT.V_SCROLL);

		
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		listHeight = results.getItemHeight() * 10;
		trim = results.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		results.setLayoutData(gridData);

		addButton = new Button(shell, SWT.PUSH);
		addButton.setText("Add");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gridData);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) 
			{
				TreeItem[] selected = patterns.getSelection();
				String name = selected[0].getText().substring(0, selected[0].getText().indexOf(","));
				selections.add(name);
				results.add(name);
				results.redraw();
				//RationaleDB db = RationaleDB.getHandle();
				//parentDecision.addPattern(name);
				
			}
		});
		
		cancelButton = new Button(shell, SWT.PUSH); 
		cancelButton.setText("Close");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {			
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
		
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
	public SelectPattern(Vector<Pattern> cans, Display display){
//		parentDecision = new Decision();
//		parentDecision.fromDatabase(((Decision)selection).getName());
		
		canceled = false;
		selections = new Vector<String>();
		subDisplay = display;
		shell = new Shell(subDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Matching results");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		patterns = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		TreeItem root = new TreeItem(patterns, SWT.NONE);
		root.setText("Matching Results");		
		
		Enumeration ps = cans.elements();
		while(ps.hasMoreElements()){
			Pattern pattern = (Pattern)ps.nextElement();
			TreeItem item = new TreeItem(root, SWT.NONE);
			item.setText(pattern.getName());			
		}
		
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		int listHeight = patterns.getItemHeight() * 10;
		Rectangle trim = patterns.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		patterns.setLayoutData(gridData);
		
		
		results = new List(shell, SWT.SINGLE | SWT.V_SCROLL);

		
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		listHeight = results.getItemHeight() * 10;
		trim = results.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		results.setLayoutData(gridData);
		
//		if (cans != null){
//			Enumeration candidates = cans.elements();
//			while (candidates.hasMoreElements())
//			{
//				Pattern result = new Pattern(); 
//				result.fromDatabase(((Pattern)candidates.nextElement()).getName());
//				String name = result.getName();
//				results.add(name);
//				results.add(" ----Description: " + result.getDescription());
//				results.add(" ----Type: " + result.getType());
//					
//			}	
//		}
		
		addButton = new Button(shell, SWT.PUSH);
		addButton.setText("Add");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gridData);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) 
			{
				TreeItem[] selected = patterns.getSelection();
				String name = selected[0].getText();
				selections.add(name);
				results.add(name);
				results.redraw();
				//RationaleDB db = RationaleDB.getHandle();
				//parentDecision.addPattern(name);
				
			}
		});
		
		cancelButton = new Button(shell, SWT.PUSH); 
		cancelButton.setText("Close");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {			
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
		
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
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
