package SEURAT.editors;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;
import edu.wpi.cs.jburge.SEURAT.views.PatternLibrary;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

public class PatternParticipantEditor extends RationaleEditorBase{
	private Pattern ourPattern;
	private List participants, operations, associations;
	private ArrayList<PatternParticipant> parts;
	private Button addParticipant, deleteParticipant, addOperation, deleteOperation, 
	addAssociation, editAssociation, deleteAssociation, editOperation, editParticipant;
	private Display ourDisplay;
	private DataCache dataCache = new DataCache();

	private class DataCache{
		ArrayList<PatternParticipant> parts;
	}

	@Override
	protected void updateFormCache() {
		/*
		 * Obsolete?
		 */
		/*
		if (parts != null){
			dataCache.parts = new ArrayList<PatternParticipant>();
			//Since it is called AFTER each save, the database should contain the newest information.
			dataCache.parts.addAll(RationaleDB.getHandle().getParticipantsFromPatternID(ourPattern.getID()));
		}
		 */
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
		/* Obsolete?
		boolean l_dirty = isDirty();
		if (ourPattern == null) return;
		if (parts == null)
			return;

		//Simply checking whether two lists are equal will not be enough since .equals of RationaleElement only checks for id.
		if (parts.size() == dataCache.parts.size()){
			for (int i = 0; i < parts.size(); i++){
				//Check id
				if (parts.get(i).equals(dataCache.parts.get(i))){
					PatternParticipant p1 = parts.get(i), p2 = dataCache.parts.get(i);
					//Check name
					if (!p1.getName().equals(p2.getName())){
						l_dirty = true;
						break;
					}
					//Check participant hash map.
					if (p1.getParticipants().equals(p2.getParticipants())){
						//Check operations
						if (p1.getOperations().equals(p2.getOperations())){
							//Do nothing for now
						} else {
							l_dirty = true;
							break;
						}
					} else{
						l_dirty = true;
						break;
					}
				} else{
					l_dirty = true;
					break;
				}
			}
		} else {
			l_dirty = true;
		}
		setDirty(l_dirty);
		 */
	}


	public static RationaleEditorInput createInput(PatternLibrary explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new PatternParticipantEditor.Input(explorer, tree, parent, target, new1);
	}

	@Override
	public RationaleElement getRationaleElement() {
		return ourPattern;
	}

	@Override
	public boolean saveData() {
		for (int i = 0; i < parts.size(); i++){
			parts.get(i).deleteRelFromDB();
		}
		for (int i = 0; i < parts.size(); i++){
			parts.get(i).toDatabase();
		}
		setDirty(false);
		return true;
	}

	@Override
	public Class editorType() {
		return Pattern.class;
	}



	@Override
	public void setupForm(Composite parent) {
		ourDisplay = parent.getDisplay();
		if (isCreating()){
			throw new IllegalArgumentException("Cannot create patterns from this editor!");
		}
		ourPattern = getPatternFromExplorer();
		parts = new ArrayList<PatternParticipant>();

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);

		Composite participantPane = new Composite(parent, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 1;
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		participantPane.setLayoutData(gridData);
		participantPane.setLayout(gridLayout);
		Composite editPane = new Composite(parent, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		editPane.setLayoutData(gridData);
		editPane.setLayout(gridLayout);
		Composite associationPane = new Composite(editPane, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 1;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		associationPane.setLayoutData(gridData);
		associationPane.setLayout(gridLayout);
		Composite operationPane = new Composite(editPane, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 1;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		operationPane.setLayoutData(gridData);
		operationPane.setLayout(gridLayout);

		//Participant Panel
		new Label(participantPane, SWT.NONE).setText("Participants:");
		new Label(participantPane, SWT.WRAP).setText("(Changes made to participants will be saved immediately.)");
		participants = new List(participantPane, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		participants.setLayoutData(gridData);
		participants.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				if (index >= 0 && index < parts.size()){
					refreshParticipantAttributes(parts.get(index));
					editParticipant.setEnabled(true);
					deleteParticipant.setEnabled(true);
					addOperation.setEnabled(true);
					addAssociation.setEnabled(true);
				}
				else {
					editParticipant.setEnabled(false);
					deleteParticipant.setEnabled(false);
					addOperation.setEnabled(false);
					addAssociation.setEnabled(false);
				}
				editOperation.setEnabled(false);
				editAssociation.setEnabled(false);
				deleteOperation.setEnabled(false);
				deleteAssociation.setEnabled(false);
			}
		});

		Composite participantButtonsPane = new Composite(participantPane, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		participantButtonsPane.setLayout(gridLayout);
		addParticipant = new Button(participantButtonsPane, SWT.PUSH);
		addParticipant.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				PatternParticipant p = new PatternParticipantDialog(null).getParticipant();
				participants.add(p.getName());
				parts.add(p);
				refreshParticipants();
				participants.deselectAll();
				participants.select(participants.getItemCount() - 1);
			}
		});
		addParticipant.setText("Add");
		editParticipant = new Button(participantButtonsPane, SWT.PUSH);
		editParticipant.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				if (index < 0) return;
				PatternParticipant p = new PatternParticipantDialog(parts.get(index)).getParticipant();
				participants.setItem(index, p.getName());
				parts.set(index, p);
				participants.deselectAll();
				participants.select(index);
			}
		});
		editParticipant.setText("Edit");
		editParticipant.setEnabled(false);
		deleteParticipant = new Button(participantButtonsPane, SWT.PUSH);
		deleteParticipant.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				if (index < 0) return;
				String message = "This will delete the participant and ALL operations as well as associations linked with this participant. Are You Sure?";
				MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				mbox.setMessage(message);
				mbox.setText("Deleting Participant " + participants.getItem(index));
				int response = mbox.open();
				if (response == SWT.YES){
					participants.remove(index);
					PatternParticipant p = parts.remove(index);
					p.deleteFromDB();
					refreshParticipants();
					if (parts.isEmpty()){
						addOperation.setEnabled(false);
						addAssociation.setEnabled(false);
					}
					editOperation.setEnabled(false);
					deleteOperation.setEnabled(false);
					editAssociation.setEnabled(false);
					deleteAssociation.setEnabled(false);
					participants.deselectAll();
				}
			}
		});
		deleteParticipant.setText("Delete");
		deleteParticipant.setEnabled(false);


		//Association Panel
		new Label(associationPane, SWT.NONE).setText("Associations:");
		new Label(associationPane, SWT.WRAP).setText("Items ending with [I] are indirect associations ");
		new Label(associationPane, SWT.WRAP).setText("and can only be edited on the other side of the association.");
		associations = new List(associationPane, SWT.V_SCROLL | SWT.SINGLE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 120;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		associations.setLayoutData(gridData);
		associations.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int index = associations.getSelectionIndex();
				if (index >= 0 && index < associations.getItemCount() && !associations.getItem(index).endsWith("[I]")){
					editAssociation.setEnabled(true);
					deleteAssociation.setEnabled(true);
				}
				else{
					editAssociation.setEnabled(false);
					deleteAssociation.setEnabled(false);
				}
			}
		});

		Composite associationButtonsPane = new Composite(associationPane, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		associationButtonsPane.setLayout(gridLayout);
		addAssociation = new Button(associationButtonsPane, SWT.PUSH);
		addAssociation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				if (index < 0) return;
				new AssociationDialog(parts.get(index), -1);
				refreshParticipantAttributes(parts.get(index));
				associations.deselectAll();
				associations.select(associations.getItemCount() - 1);
			}
		});
		addAssociation.setEnabled(false);
		addAssociation.setText("Add");
		editAssociation = new Button(associationButtonsPane, SWT.PUSH);
		editAssociation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				int associationIndex = associations.getSelectionIndex();
				if (index < 0 || associationIndex < 0) return;
				new AssociationDialog(parts.get(index), 
						new Integer(assocGetID(associations.getItem(associationIndex))));
				refreshParticipantAttributes(parts.get(index));
				associations.deselectAll();
				associations.select(associationIndex);
			}
		});
		editAssociation.setText("Edit");
		editAssociation.setEnabled(false);
		deleteAssociation = new Button(associationButtonsPane, SWT.PUSH);
		deleteAssociation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				int associationIndex = associations.getSelectionIndex();
				if (index < 0 || associationIndex < 0) return;
				int toRemove = assocGetID(associations.getItem(associationIndex));
				parts.get(index).getParticipants().remove(new Integer(toRemove));
				refreshParticipantAttributes(parts.get(index));
				associations.deselectAll();
				editAssociation.setEnabled(false);
				deleteAssociation.setEnabled(false);
				setDirty(true);
			}
		});
		deleteAssociation.setText("Delete");
		deleteAssociation.setEnabled(false);


		//Operation Panel
		new Label(operationPane, SWT.NONE).setText("Operations:");
		operations = new List(operationPane, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 120;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		operations.setLayoutData(gridData);
		operations.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int index = operations.getSelectionIndex();
				if (index >= 0 && index < operations.getItemCount()){
					editOperation.setEnabled(true);
					deleteOperation.setEnabled(true);
				} else{
					editOperation.setEnabled(false);
					deleteOperation.setEnabled(false);
				}
			}
		});

		Composite operationButtonsPane = new Composite(operationPane, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		operationButtonsPane.setLayout(gridLayout);
		addOperation = new Button(operationButtonsPane, SWT.PUSH);
		addOperation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				if (index < 0) return;
				new OperationDialog(parts.get(index));
				refreshParticipantAttributes(parts.get(index));
				operations.deselectAll();
				operations.select(operations.getItemCount() - 1);
			}
		});
		addOperation.setEnabled(false);
		addOperation.setText("Add");
		editOperation = new Button(operationButtonsPane, SWT.PUSH);
		editOperation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				int opIndex = operations.getSelectionIndex();
				if (index < 0 || opIndex < 0) return;
				new OperationDialog(parts.get(index), parts.get(index).getOperations().get(opIndex));
				refreshParticipantAttributes(parts.get(index));
				operations.deselectAll();
				operations.select(opIndex);
			}
		});
		editOperation.setEnabled(false);
		editOperation.setText("Edit");
		deleteOperation = new Button(operationButtonsPane, SWT.PUSH);
		deleteOperation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				int index = participants.getSelectionIndex();
				int opIndex = operations.getSelectionIndex();
				if (index < 0 || opIndex < 0) return;
				parts.get(index).getOperations().remove(opIndex);
				refreshParticipantAttributes(parts.get(index));
				operations.deselectAll();
				editOperation.setEnabled(false);
				deleteOperation.setEnabled(false);
				setDirty(true);
			}
		});
		deleteOperation.setEnabled(false);
		deleteOperation.setText("Delete");

		//Register Event Notification
		try{
			RationaleDB.getHandle().Notifier().Subscribe(ourPattern, this, "onUpdate");
		}
		catch (Exception e){
			System.err.println("Unable to subscribe on the selected object!");
		}

		Vector<PatternParticipant> p =RationaleDB.getHandle().getParticipantsFromPatternID(ourPattern.getID());
		parts.addAll(p);
		refreshParticipants();

	}

	private void refreshParticipants(){
		if (participants == null) return;
		if (ourPattern == null) return;
		participants.removeAll();
		RationaleDB db = RationaleDB.getHandle();
		Vector<PatternParticipant> p = db.getParticipantsFromPatternID(ourPattern.getID());
		for (int i = 0; i < p.size(); i++){
			participants.add(p.get(i).getName());
		}
		//Do not refresh parts vector because it is not saved to the database!
		//Instead, create a new one and copy addresses from the old to the new.
		ArrayList<PatternParticipant> newParts = new ArrayList<PatternParticipant>();
		for (int i = 0; i < parts.size(); i++){
			if (parts.get(i) != null && parts.get(i).getName() != ""){
				if (p.contains(parts.get(i)))
					newParts.add(parts.get(i));
			}
		}
		parts = newParts;
	}


	private int assocGetID(String listElement){
		String assocName = listElement.substring(0, listElement.lastIndexOf(" : "));
		PatternParticipant p = new PatternParticipant();
		p.fromDatabase(ourPattern.getID(), assocName);
		return p.getID();
	}

	/**
	 * Given a pattern participant, refresh the lists on the right panels.
	 * @param p
	 */
	private void refreshParticipantAttributes(PatternParticipant p){
		if (associations == null || operations == null) return;
		associations.removeAll();
		operations.removeAll();
		String[] indirectAssoc = (String[]) p.getIndirectAssocParticipants().keySet().toArray(new String[0]);
		for (int i = 0; i < indirectAssoc.length; i++){
			String toAdd = indirectAssoc[i];
			switch(p.getIndirectAssocParticipants().get(toAdd)){
			case UMLRelation.ASSOCIATION:
				toAdd += " -- \t[I]";
				break;
			case UMLRelation.AGGREGATION:
				toAdd += " -x \t[I]";
				break;
			case UMLRelation.DELEGATION:
				toAdd += " <- \t[I]";
				break;
			case UMLRelation.GENERALIZATION:
				toAdd += " <|- \t[I]";
				break;
			}
			associations.add(toAdd);

		}
		Integer[] assoc = (Integer[]) p.getParticipants().keySet().toArray(new Integer[0]);
		for (int i = 0; i < assoc.length; i++){
			PatternParticipant pp = new PatternParticipant();
			pp.fromDatabase(assoc[i]);
			String toAdd = pp.getName() + " : ";
			switch (p.getParticipants().get(assoc[i])){
			case UMLRelation.ASSOCIATION:
				toAdd += "--";
				break;
			case UMLRelation.AGGREGATION:
				toAdd += "x-";
				break;
			case UMLRelation.DELEGATION:
				toAdd += "->";
				break;
			case UMLRelation.GENERALIZATION:
				toAdd += "-|>";
				break;
			}
			associations.add(toAdd);
		}

		Vector<ParticipantOperation> ops = p.getOperations();
		for (int i = 0; i < ops.size(); i++){
			String toAdd = ops.get(i).getName();
			operations.add(toAdd);
		}
	}

	public void onUpdate(Pattern pElement, RationaleUpdateEvent pEvent){
		try{
			if (pEvent.getElement().equals(ourPattern)){
				if (pEvent.getDestroyed()){
					closeEditor();
				}
				if (pEvent.getModified()){
					//We are not modifying any attribute of pattern itself. Simply use the new element.
					ourPattern = pElement;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Used to get the pattern object being modified
	 * @return The pattern object we are modifying.
	 */
	private Pattern getPatternFromExplorer(){
		return (Pattern) getEditorData().getAdapter(Pattern.class);
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
			super(patternLib, tree, parent, parent, new1);
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
			return isCreating() ? "Pattern Participant Editor" :
				"Pattern Participants Of: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Pattern.class;
		}

	}

	/**
	 * This class is used when a user clicks "Add" or "Edit" on a participant.
	 * @author yechen
	 *
	 */
	private class PatternParticipantDialog{
		PatternParticipant participant;
		Shell shell;
		Text name, min, max;
		Button save, cancel;

		public PatternParticipantDialog(PatternParticipant p){
			shell = new Shell(ourDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
			if (p == null){
				participant = new PatternParticipant();
				shell.setText("Add Pattern Participant");
			}
			else{
				participant = p;
				shell.setText("Participant: " + participant.getName());
			}
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 4;
			gridLayout.makeColumnsEqualWidth = true;
			shell.setLayout(gridLayout);

			new Label(shell, SWT.NONE).setText("Name: ");
			name = new Text(shell, SWT.SINGLE | SWT.BORDER);
			name.setText(participant.getName());
			GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			DisplayUtilities.setTextDimensions(name, gridData, 25);
			gridData.horizontalSpan = 3;
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			name.setLayoutData(gridData);

			new Label(shell, SWT.NONE).setText("Min Participants (-1 = unlimited): ");
			min = new Text(shell, SWT.SINGLE | SWT.BORDER);
			min.setText("" + participant.getMin());
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			DisplayUtilities.setTextDimensions(min, gridData, 10);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			min.setLayoutData(gridData);

			new Label(shell, SWT.NONE).setText("Max Participants (-1 = unlimited): ");
			max = new Text(shell, SWT.SINGLE | SWT.BORDER);
			max.setText("" + participant.getMax());
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			DisplayUtilities.setTextDimensions(max, gridData, 10);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			max.setLayoutData(gridData);

			save = new Button(shell, SWT.PUSH);
			save.setText("Save");
			save.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event){
					//Check whether name ends with numerical values.
					if (name.getText().matches("[0-9]$")){
						String message = "Name cannot end with a numerical value!";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(message);
						mbox.setText("Participant Name is Invalid");
						mbox.open();
						return;
					}
					//Check whether the input is valid...
					if (participant.getID() < 0){
						//New participant
						//Check for name duplication
						PatternParticipant check = new PatternParticipant();
						check.fromDatabase(ourPattern.getID(), name.getText());
						if (check.getID() >= 0){
							//Invalid
							String message = "There is another participant of this pattern having the same name." +
									" Change the name and try again!";
							MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
							mbox.setMessage(message);
							mbox.setText("Participant Name Is Invalid");
							mbox.open();
							return;
						}
					}
					else{
						//Editing existing participant
						if (!name.getText().equals(participant.getName())){
							PatternParticipant check = new PatternParticipant();
							check.fromDatabase(ourPattern.getID(), name.getText());
							if (check.getID() >= 0){
								//Invalid
								String message = "There is another participant of this pattern having the same name." +
										" Change the name and try again!";
								MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
								mbox.setMessage(message);
								mbox.setText("Participant Name Is Invalid");
								mbox.open();
								return;
							}
						}
					}
					try{
						int minValue = new Integer(min.getText());
						int maxValue = new Integer(max.getText());
						if (minValue >= 0 && maxValue >= 0){
							if (minValue > maxValue){
								String message = "The minimum must be no greater than the maximum!";
								MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
								mbox.setMessage(message);
								mbox.setText("Min/Max Is Invalid");
								mbox.open();
								return;
							}
						}

						//Everything checks out... Save...
						participant.setPatternID(ourPattern.getID());
						participant.setName(name.getText());
						participant.setMin(minValue);
						participant.setMax(maxValue);
						participant.toDatabase();
						shell.dispose();
					} catch (NumberFormatException e){
						String message = "Invalid min/max input. They must be integers!";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(message);
						mbox.setText("Min/Max Is Invalid");
						mbox.open();
						return;
					}
				}
			});

			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			cancel = new Button(shell, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event){
					shell.dispose();
				}
			});

			shell.pack();
			shell.open();
			while (!shell.isDisposed()){
				if (!shell.getDisplay().readAndDispatch()){
					shell.getDisplay().sleep();
				}
			}
		}

		public PatternParticipant getParticipant(){
			return participant;
		}
	}

	/**
	 * This class is used when a user clicks on either "Add" or "Edit" on association panel.
	 * @author yechen
	 *
	 */
	private class AssociationDialog{
		PatternParticipant participant;
		Shell shell;
		Combo partCombo, typeCombo;
		Button save, cancel;
		int associatedID = -1; //Used by the save button since it must use a non-static variable.

		public AssociationDialog(PatternParticipant p, int assocID){
			associatedID = assocID;
			participant = p;
			shell = new Shell(ourDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
			shell.setText("Association Editor for participant " + p.getName());
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 4;
			shell.setLayout(gridLayout);

			new Label(shell, SWT.NONE).setText("Type: ");
			typeCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
			typeCombo.add("Association (--)");
			typeCombo.add("Aggregation (x-)");
			typeCombo.add("Delegation (->)");
			typeCombo.add("Generalization (-|>)");
			GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			typeCombo.setLayoutData(gridData);
			if (p.getParticipants().containsKey(new Integer(associatedID))){
				typeCombo.select(p.getParticipants().get(new Integer(associatedID)));
			}

			new Label(shell, SWT.NONE).setText("Associate With: ");
			partCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
			for (int i = 0; i < parts.size(); i++){
				partCombo.add(parts.get(i).getName());
				if (parts.get(i).getID() == associatedID){
					partCombo.select(i);
				}
			}
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			partCombo.setLayoutData(gridData);

			save = new Button(shell, SWT.PUSH);
			save.setText("Save");
			save.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event){
					if (typeCombo.getSelectionIndex() < 0 || partCombo.getSelectionIndex() < 0){
						String message = "Make selections in the drop down menu first!";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(message);
						mbox.setText("Association Is Invalid");
						mbox.open();
						return;
					}
					if (associatedID >= 0){
						participant.removeParticipant(associatedID);
					}
					boolean success = participant.associateParticipant(parts.get(partCombo.getSelectionIndex()).getID(), 
							typeCombo.getSelectionIndex());
					if (!success){
						String message = "Unable to save the new association. Please check to maek sure" 
								+ "there is no duplicated association or a cyclic generalization";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(message);
						mbox.setText("Association Is Invalid");
						mbox.open();
						return;
					}
					else {
						setDirty(true);
						shell.dispose();
					}
				}
			});

			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			cancel = new Button(shell, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event){
					shell.dispose();
				}
			});

			shell.pack();
			shell.open();
			while (!shell.isDisposed()){
				if (!shell.getDisplay().readAndDispatch()){
					shell.getDisplay().sleep();
				}
			}
		}

		public PatternParticipant getParticipant(){
			return participant;
		}
	}

	/**
	 * This class is used when a user either clicks "Add" or "Edit" on operations panel.
	 * @author yechen
	 *
	 */
	private class OperationDialog{
		ParticipantOperation op;
		PatternParticipant p;
		Shell shell;
		Text nameField;
		Combo assocCombo;
		Button disableAssoc, save, cancel;

		/**
		 * This is used to create a new operation
		 * @param p
		 */
		public OperationDialog(PatternParticipant p){
			this.p = p;
			op = null;
			setupForm();
		}

		/**
		 * This is used to edit an existing operation
		 * @param op
		 */
		public OperationDialog(PatternParticipant p, ParticipantOperation op){
			this.op = op;
			this.p = p;
			setupForm();
		}

		public void setupForm(){
			shell = new Shell(ourDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
			if (op == null)
				shell.setText("Adding Operation for " + p.getName());
			else{
				shell.setText("Editing Operation " + op.getName());
			}
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 4;
			gridLayout.makeColumnsEqualWidth = true;
			shell.setLayout(gridLayout);

			new Label(shell, SWT.NONE).setText("Associate With: ");
			assocCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
			for (int i = 0; i < parts.size(); i++){
				assocCombo.add(parts.get(i).getName(), i);
				if (op != null && op.getAssociatedParticipant() == parts.get(i).getID()){
					assocCombo.select(i);
				}
			}

			new Label(shell, SWT.NONE).setText("Name: ");
			nameField = new Text(shell, SWT.SINGLE | SWT.BORDER);
			if (op != null)
				nameField.setText(op.getName());
			GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			DisplayUtilities.setTextDimensions(nameField, gridData, 25);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			nameField.setLayoutData(gridData);

			disableAssoc = new Button(shell, SWT.CHECK);
			disableAssoc.setText("Disable Association");
			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalSpan = 2;
			disableAssoc.setLayoutData(gridData);
			disableAssoc.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					if (disableAssoc.getSelection()){
						assocCombo.setEnabled(false);
					}
					else {
						assocCombo.setEnabled(true);
					}
				}
			});
			disableAssoc.setSelection(false);
			if (op != null && op.getAssociatedParticipant() < 0){
				disableAssoc.setSelection(true);
				assocCombo.setEnabled(false);
			}

			save = new Button(shell, SWT.PUSH);
			save.setText("Save");
			save.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					if (nameField.getText().equals("") || 
							(assocCombo.getSelectionIndex() < 0 && !disableAssoc.getSelection())){
						String message = "Enter a name and make a selection before saving!";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(message);
						mbox.setText("Operation Is Invalid");
						mbox.open();
						return;
					}
					if (op == null){
						op = new ParticipantOperation();
						op.setPartID(p.getID());
					}
					else {
						p.getOperations().remove(op);
					}
					op.setName(nameField.getText());
					if (!disableAssoc.getSelection()){
						int associatedID = parts.get(assocCombo.getSelectionIndex()).getID();
						op.setAssociatedParticipant(associatedID);
					}
					else {
						op.setAssociatedParticipant(-1);
					}
					boolean success = p.addOperation(op);
					if (!success){
						String message = "Check to make sure the name of the operation is unique.";
						MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
						mbox.setMessage(message);
						mbox.setText("Operation Is Invalid");
						mbox.open();
						return;
					}
					else {
						setDirty(true);
						shell.dispose();
					}
				}
			});

			cancel = new Button(shell, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					shell.dispose();
				}
			});


			shell.pack();
			shell.open();
			while (!shell.isDisposed()){
				if (!shell.getDisplay().readAndDispatch()){
					shell.getDisplay().sleep();
				}
			}
		}

		public ParticipantOperation getOperation(){
			return op;
		}

		public PatternParticipant getParticipant(){
			return p;
		}
	}


}
