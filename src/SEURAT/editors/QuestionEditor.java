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

package SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.ReasonGUI;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.History;
import edu.wpi.cs.jburge.SEURAT.rationaleData.QuestionStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Question;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * Edit a tradeoff or co-occurrence. Tradeoffs describe ontology entries (NFRs) that should appear on opposite
 * sides of an argument. Co-occurrences describe ontology entries that should occur together
 * in arguments.
 */
public class QuestionEditor extends RationaleEditorBase {
	public static RationaleEditorInput createInput(RationaleExplorer explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new QuestionEditor.Input(explorer, tree, parent, target, new1);
	}

	/**
	 * This class provides caching features used when updating
	 * properties of a question remotely.
	 */
	private class DataCache
	{
		/**
		 * Last known good name
		 */
		String name;
		/**
		 * Last known good description
		 */
		String description;
		/**
		 * Last known good status 
		 */
		int status;
		/**
		 * Last known good procedure
		 */
		String procedure;
		/**
		 * Last known good answer
		 */
		String answer;
	}

	/**
	 * The question's name
	 */
	private Text nameField;
	// private Text artifactField;
	/**
	 * A description of the question (or the question itself!)
	 */
	private Text descArea;
	/**
	 * The answer to the question
	 */
	private Text answer;

	/**
	 * The procedure to follow in order to find an answer. This could be a test or simulation
	 * that needs to be run or it can be instructions on who to ask for an answer.
	 */
	private Text procedure;

	/**
	 * The status of the question (answered or unanswered)
	 */
	private Combo statusBox;

	/**
	 * Member variable used for storing last known good
	 * values for editable fields in the editor
	 */
	private DataCache dataCache = new DataCache();

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#editorType()
	 */
	public Class editorType() {
		return Question.class;
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#getRationaleElement()
	 */
	public RationaleElement getRationaleElement() {
		return getQuestion();
	}

	/**
	 * @return The question being edited
	 */
	public Question getQuestion() {
		return (Question)getEditorData().getAdapter(Question.class);
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
			if( pEvent.getElement().equals(getQuestion()) )
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
			System.out.println("Exception in QuestionEditor: onUpdate");
		}
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#updateFormCache()
	 */
	@Override
	protected void updateFormCache() {
		if( nameField != null )
			dataCache.name = nameField.getText();

		if( descArea != null )
			dataCache.description = descArea.getText();

		if( procedure != null )
			dataCache.procedure = procedure.getText();

		if( answer != null )
			dataCache.answer = answer.getText();

		if( statusBox != null )
			dataCache.status = statusBox.getSelectionIndex();
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#onRefreshForm(SEURAT.events.RationaleUpdateEvent)
	 */
	@Override
	protected void onRefreshForm(RationaleUpdateEvent pEvent) {
		boolean l_dirty = isDirty();

		// Something Has Changed, Reload This Element From The DB
		getQuestion().fromDatabase(getQuestion().getID());

		if( nameField.getText().equals(dataCache.name) )
		{
			nameField.setText(getQuestion().getName());
			dataCache.name = nameField.getText();
		}
		else
			l_dirty = true;

		if( descArea.getText().equals(dataCache.description) )
		{
			descArea.setText(getQuestion().getDescription());
			dataCache.description = descArea.getText();
		}
		else
			l_dirty = true;

		if( procedure.getText().equals(dataCache.procedure) )
		{
			procedure.setText(getQuestion().getProcedure());
			dataCache.procedure = procedure.getText();
		}
		else
			l_dirty = true;

		if( answer.getText().equals(dataCache.answer) )
		{
			answer.setText(getQuestion().getAnswer());
			dataCache.answer = answer.getText();
		}
		else
			l_dirty = true;

		if( statusBox.getSelectionIndex() == dataCache.status )
		{
			Enumeration iterator;
			int index;

			for( index=0, iterator=QuestionStatus.elements();
			iterator.hasMoreElements();
			index++)
			{
				QuestionStatus stype = (QuestionStatus) iterator.nextElement();
				//				Don't add status elements - they are put there in setupForm
				//				statusBox.add( stype.toString() );
				if (stype.toString().compareTo(getQuestion().getStatus().toString()) == 0)
				{
					statusBox.select(index);
				}
			}
			dataCache.status = statusBox.getSelectionIndex();
		}
		else
			l_dirty = true;

		setDirty(l_dirty);
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#setupForm(org.eclipse.swt.widgets.Composite)
	 */
	public void setupForm(Composite parent) {	
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);

		if (isCreating())
		{
			getQuestion().setStatus(QuestionStatus.UNANSWERED);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 QuestionInferences inf = new QuestionInferences();
		 Vector newStat = inf.updateQuestion(ourQuest);
		 } */

		new Label(parent, SWT.NONE).setText("Name:");

		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getQuestion().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 20);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Description:");

		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getQuestion().getDescription());
		descArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 20, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = descArea.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);

		new Label(parent, SWT.NONE).setText("Status:");
		statusBox = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		statusBox.addModifyListener(getNeedsSaveListener());
		Enumeration statEnum = QuestionStatus.elements();
		int j=0;
		QuestionStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (QuestionStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(getQuestion().getStatus().toString()) == 0)
			{
				//				System.out.println(ourQuest.getStatus().toString());
				statusBox.select(j);
				//				System.out.println(j);
			}
			j++;
		}

		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");

		new Label(parent, SWT.NONE).setText("Procedure:");

		procedure = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		procedure.setText(getQuestion().getProcedure());
		procedure.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(procedure, gridData, 20, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = procedure.getLineHeight() * 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		procedure.setLayoutData(gridData);  

		new Label(parent, SWT.NONE).setText("Answer:");

		answer = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		answer.setText(getQuestion().getAnswer());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(answer, gridData, 20, 5);
		gridData.horizontalSpan = 3;
		gridData.heightHint = answer.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		answer.setLayoutData(gridData);

		// Register Event Notification
		try
		{
			RationaleDB.getHandle().Notifier().Subscribe(getQuestion(), this, "onUpdate");
		}
		catch( Exception e )
		{
			System.out.println("Question Editor: Updates Not Available!");
		}

		updateFormCache();
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#saveData()
	 */
	public boolean saveData() {
		ConsistencyChecker checker = new ConsistencyChecker(getQuestion().getID(), nameField.getText(), "Questions");
		if(!nameField.getText().trim().equals("") &&
				(getQuestion().getName() == nameField.getText() || checker.check(false)))
		{
			// Set the question's parent data correctly
			RationaleElement parentElement = getParentElement();
			if (isCreating()){
				getQuestion().setParent(parentElement);
			}
			getQuestion().setName(nameField.getText());
			getQuestion().setDescription(descArea.getText());
			getQuestion().setProcedure(procedure.getText());
			getQuestion().setAnswer(answer.getText());
			if (isCreating()) {
				getQuestion().setStatus( QuestionStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
				//				ourQuest.setArtifact( artifactField.getText());
				getQuestion().updateHistory(new History(getQuestion().getStatus().toString(), "Initial Entry"));
			} else {
				QuestionStatus newStat = QuestionStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
				if (!newStat.toString().equals(getQuestion().getStatus().toString()))
				{
					ReasonGUI rg = new ReasonGUI();
					String newReason = rg.getReason();
					getQuestion().setStatus(newStat);
					//				System.out.println(newStat.toString() + ourQuest.getStatus().toString());
					History newHist = new History(newStat.toString(), newReason);
					getQuestion().updateHistory(newHist);
					//				ourQuest.toDatabase(ourQuest.getParent(), RationaleElementType.fromString(ourQuest.getPtype()));
					//				newHist.toDatabase(ourQuest.getID(), RationaleElementType.Question);
				}
			}			
			//comment before this made no sense...
			Question quest = getQuestion();
			getQuestion().setID(quest.toDatabase(quest.getParent(), quest.getPtype()));
			return true;
		}
		else
		{
			String l_message = "";
			l_message += "The question name you have specified is either already"
				+ " in use or empty. Please make sure that you have specified"
				+ " a question name and the question name does not already exist"
				+ " in the database.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Question Name Is Invalid");
			mbox.open();
		}
		return false;
	}

	/**
	 * Wrap a question in a logical file
	 */
	public static class Input extends RationaleEditorInput {
		/**
		 * @param explorer RationaleExplorer
		 * @param tree the element in the RationaleExplorer tree for the argument
		 * @param parent the parent of the argument in the RationaleExplorer tree
		 * @param target the argument to associate with the logical file
		 * @param new1 true if the argument is being created, false if it already exists
		 */
		public Input(RationaleExplorer explorer, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(explorer, tree, parent, target, new1);
		}

		/**
		 * @return The question wrapped in the logical file.
		 */
		public Question getData() { return (Question)getAdapter(Question.class); }

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New Question Editor" :
				"Question: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Question.class;
		}
	}
}
