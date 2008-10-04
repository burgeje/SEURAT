package SEURAT.editors;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.ReasonGUI;
import edu.wpi.cs.jburge.SEURAT.editors.SelectOntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.History;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Importance;
import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * Requirement editor. Used to create or modify requirements.
 */
public class RequirementEditor extends RationaleEditorBase {
	public static RationaleEditorInput createInput(RationaleExplorer explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new RequirementEditor.Input(explorer, tree, parent, target, new1);
	}
	
	/**
	 * Stores last known good values of all
	 * editor controls which can be changed by 
	 * the user.
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
		 * Last known good type
		 */
		int type;
		/**
		 * Last known good status
		 */
		int status;
		/**
		 * last known good importance
		 */
		int importance;
		/**
		 * last known enabled/disabled status
		 */
		boolean enabled;
	}
	
	/**
	 * The name of the requirement
	 */
	private Text nameField;
	/**
	 * The artifact associated with the requirement (could be a requirement number that
	 * points back to an SRS)
	 */
	private Text artifactField;
	/**
	 * The description (text) of the requirement
	 */
	private Text descArea;
//	private boolean newItem;
	/**
	 * Check box to enable/disable the requirement
	 */
	private Button enableButton;
	
	/**
	 * The type of requirement - functional or non-functional
	 */  
	private Combo typeBox;
	/**
	 * Combo box to select importance
	 */
	private Combo importanceBox;
	/**
	 * The ontology entry description
	 */
	private Label ontDesc;
	/**
	 * The status of the requiement (violated, addressed, etc.)
	 */
	private Combo statusBox;
	/**
	 * Button to select an argument ontology entry
	 */
	private Button selOntButton;
	/**
	 * Arguments for the requirement
	 */
	private List forModel;
	/**
	 * Arguments against the requirement
	 */
	private List againstModel;
	Composite ourParent;
	/**
	 * Member variable used to store the last known good values
	 * of editor data.
	 */
	private DataCache dataCache = new DataCache();
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#editorType()
	 */
	public Class editorType() {
		return Requirement.class;
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#getRationaleElement()
	 */
	public RationaleElement getRationaleElement() {
		return getRequirement();
	}

	/**
	 * @return The requirement being edited
	 */
	public Requirement getRequirement() {
		return (Requirement)getEditorData().getAdapter(Requirement.class);
	}

	/**
	 * Respond to changes made to the requirement associated with
	 * this editor.
	 * 
	 * @param pElement the requirement which generated the event
	 * @param pEvent a description of the change made to the requirement
	 */
	public void onUpdate(Requirement pElement, RationaleUpdateEvent pEvent)
	{		
		// Check To See If A new argument for this rationale was created.
		if( pEvent.getElement() != pElement &&
			pEvent.getCreated() == true &&
			pEvent.getElement() instanceof Argument )
		{
			refreshArguments(pEvent);
		}
		else
		if( pEvent.getElement().equals(getRequirement()) &&
			pEvent.getDestroyed() )
		{	
			closeEditor();
		}
		else
		if( pEvent.getElement().equals(getRequirement()) &&
			( pEvent.getModified() || pEvent.getCreated() ) )
		{	
			refreshForm(pEvent);
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
		
		if( typeBox != null )
			dataCache.type = typeBox.getSelectionIndex();
		
		if( statusBox != null )
			dataCache.status = statusBox.getSelectionIndex();
		
		if( importanceBox != null )
			dataCache.importance = importanceBox.getSelectionIndex();
		
		//This dataCache field is used a bit differently...
		dataCache.enabled = enableButton.getSelection();
	}

	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#onRefreshForm(SEURAT.events.RationaleUpdateEvent)
	 */
	protected void onRefreshForm(RationaleUpdateEvent pEvent)
	{
		Enumeration iterator = null;
		int index = 0;
		boolean l_dirty = isDirty();
		
		// Something Has Changed, Reload This Element From The DB
		getRequirement().fromDatabase(getRequirement().getID());
		
		if( nameField.getText().equals(dataCache.name) )
		{
			nameField.setText(getRequirement().getName());
			dataCache.name = nameField.getText();
		}
		else 
			l_dirty = true; 
		
		if( descArea.getText().equals(dataCache.description) )
		{
			descArea.setText(getRequirement().getDescription());
			dataCache.description = descArea.getText();
		}
		else
			l_dirty = true;
		
		if( typeBox.getSelectionIndex() == dataCache.type )
		{
			iterator = ReqType.elements();
			index = 0;
			while( iterator.hasMoreElements() )
			{
				if( iterator.nextElement().equals(getRequirement().getType()) )
				{
					typeBox.select(index);
					break;
				}
				
				index++;
			}
			dataCache.type = typeBox.getSelectionIndex();
		}
		else
			l_dirty = true;
		
		if( statusBox.getSelectionIndex() == dataCache.status )
		{
			index = 0;
			iterator = ReqStatus.elements();
			while( iterator.hasMoreElements() )
			{
				if( iterator.nextElement().equals(getRequirement().getStatus()) )
				{
					statusBox.select(index);
					break;
				}
				index++;
			}
			dataCache.status = statusBox.getSelectionIndex();
		}
		else
			l_dirty = true;
		
		if( importanceBox.getSelectionIndex() == dataCache.importance )
		{
			Enumeration impEnum = Importance.elements();
			int l=0;
			Importance itype;
			while (impEnum.hasMoreElements())
			{
				itype = (Importance) impEnum.nextElement();
				
				if (itype.toString().compareTo(getRequirement().getImportance().toString()) == 0)
				{
					importanceBox.select(l);
					dataCache.importance = importanceBox.getSelectionIndex();
				}
				l++;
			}
		}
		else
			l_dirty = true;
		
		if (getRequirement().getArtifact() != null)
		{
			artifactField.setText(getRequirement().getArtifact());
		}
		else
		{
			artifactField.setText("");
		}	

		setDirty(l_dirty);
	}
	
	/**
	 * Update listboxes displaying children of the requirement.
	 * 
	 * @param pEvent a description of the change made to the requirements
	 * 		children. This parameter may be null.
	 */
	public void refreshArguments(RationaleUpdateEvent pEvent)
	{
		boolean l_dirty = isDirty();
		
		// Something Has Changed, Reload This Element From The DB
		getRequirement().fromDatabase(getRequirement().getID());
		
		// A New Argument Has Been Created As A Child Of This Requirement,
		// With Lack Of A Keen Understanding Of How Arguments Are
		// Identified As For And Against, We Will Regenerate The
		// Entire List Of Arguments For This Requirement
		Vector arguments;
		String subscribeFunc = null;
		Enumeration iterator;
		forModel.removeAll();
		againstModel.removeAll();
		
		arguments = getRequirement().getArgumentsFor();
		iterator = arguments.elements();
		while (iterator.hasMoreElements())
		{
			String arg = (String)iterator.nextElement();
			if( pEvent == null ||
					pEvent.getElement() == null ||
					!arg.equals(pEvent.getElement().getName()) ||
					!pEvent.getDestroyed() )
				{
					forModel.add( arg );
				}

			if( pEvent == null || pEvent.getElement() == null )
				continue;			
			if( !arg.equals(pEvent.getElement().getName()) )
				continue;

			// If the element was created, or it's relationship
			// to this alternative has changed we need to register
			// a new subscription.
			if( pEvent.getCreated() ||
				(pEvent.getModified() && forModel != pEvent.getTag()))
			{
					subscribeFunc = "onForArgumentUpdate";				
			}
		}    
		
		arguments = getRequirement().getArgumentsAgainst();
		iterator = arguments.elements();
		while( iterator.hasMoreElements() )
		{
			String arg = (String)iterator.nextElement();			
			if( pEvent == null ||
					pEvent.getElement() == null ||
					!arg.equals(pEvent.getElement().getName()) ||
					!pEvent.getDestroyed() )
				{
					againstModel.add( arg );
				}

			if( pEvent == null )
				continue;			
			if( !arg.equals(pEvent.getElement().getName()) )
				continue;

			// If the element was created, or it's relationship
			// to this alternative has changed we need to register
			// a new subscription.
			if( pEvent.getCreated() ||
				(pEvent.getModified() && againstModel != pEvent.getTag()))
			{
					subscribeFunc = "onAgainstArgumentUpdate";				
			}
		}

		// The New Argument Will Not Publish Further Events Using
		// The Parent As A Pseudonym, therefore we must subscribe to it's
		// publications
		//
		// A modified argument which has changed status (i.e. for -> against)
		// needs to have the old event listener replaced with a new one.
		//
		// If an argument has been deleted we can unsubscribe from that
		// argument
		try
		{
			RationaleDB l_db = RationaleDB.getHandle();
			
			if (pEvent != null )
			{
				Argument l_target = (Argument)pEvent.getElement();
				if(subscribeFunc != null)
				{
					// If the subscriberFunc is being changed then
					// we need to remove the existing subscription first.
					//
					// TODO There is room for a new (change subscription) method
					// 		in the publish subscribe manager for this purpose.
					if( pEvent.getModified() )
						l_db.Notifier().Unsubscribe(this, l_target);
					
					l_db.Notifier().Subscribe(l_target, this, subscribeFunc);
				}
				else
				if( pEvent.getDestroyed() )
				{
					l_db.Notifier().Unsubscribe(this, l_target);
				}
			}
		}
		catch( Exception e )
		{
			System.out.println("Requirement Editor: Created Argument Subscription Failure");
		}
		
		setDirty(l_dirty);
	}
	
	/**
	 * Respond to changes made to arguments that are for the requirement.
	 * 
	 * @param pElement the argument which has changed
	 * @param pEvent a description of the change made to the argument
	 */
	public void onForArgumentUpdate(Argument pElement, RationaleUpdateEvent pEvent)
	{
		try
		{
			pEvent.setTag(forModel);
			refreshArguments(pEvent);
		}
		catch( Exception eError )
		{
			System.out.println("Exception in AlternativeEditor:  ForArgumentUpdate");
		}
	}
	
	/**
	 * Respond to changes made to arguments that are against the requirement.
	 * 
	 * @param pElement the argument which has changed
	 * @param pEvent a description of the change made to the argument
	 */
	public void onAgainstArgumentUpdate(Argument pElement, RationaleUpdateEvent pEvent)
	{
		try
		{
			pEvent.setTag(againstModel);
			refreshArguments(pEvent);
		}
		catch( Exception eError )
		{
			System.out.println("Exception in AlternativeEditor:  ForArgumentUpdate");
		}
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#setupForm(org.eclipse.swt.widgets.Composite)
	 */
	public void setupForm(Composite parent) {		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		if (isCreating())
		{
			getRequirement().setType(ReqType.FR);
			getRequirement().setStatus(ReqStatus.UNDECIDED);
			getRequirement().setImportance(Importance.ESSENTIAL);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 RequirementInferences inf = new RequirementInferences();
		 Vector newStat = inf.updateRequirement(getRequirement());
		 } */
		
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getRequirement().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Description:");
		
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getRequirement().getDescription());
		descArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 50, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = descArea.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Type:");
		
		
		typeBox = new Combo(parent, SWT.NONE);
		typeBox.addModifyListener(getNeedsSaveListener());
		Enumeration typeEnum = ReqType.elements();
//		System.out.println("got enum");
		int i = 0;
		ReqType rtype;
		while (typeEnum.hasMoreElements())
		{
			rtype = (ReqType) typeEnum.nextElement();
//			System.out.println("got next element");
			typeBox.add( rtype.toString());
			if (rtype.toString().compareTo(getRequirement().getType().toString()) == 0)
			{
//				System.out.println(getRequirement().getType().toString());
				typeBox.select(i);
//				System.out.println(i);
			}
			i++;
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		typeBox.setLayoutData(gridData);
		
		typeBox.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				ReqType rType = ReqType.fromString(typeBox.getItem(typeBox.getSelectionIndex()));
				if (rType == ReqType.NFR)
				{
					selOntButton.setEnabled(true);
					getRequirement().setImportance(Importance.DEFAULT);
				}
				else
				{
					selOntButton.setEnabled(false);
					getRequirement().setImportance(Importance.ESSENTIAL);
					getRequirement().setOntology(null);
				}
				
				//Make sure our new importance is selected in the widget!
				Importance itype;
				Enumeration impEnum = Importance.elements();
				int l = 0;
				while (impEnum.hasMoreElements())
				{
					itype = (Importance) impEnum.nextElement();
					if (itype.toString().compareTo(getRequirement().getImportance().toString()) == 0)
					{
						importanceBox.select(l);
					}
					l++;
				}
			}
		});
		
		new Label(parent, SWT.NONE).setText("Status:");
		statusBox = new Combo(parent, SWT.NONE);
		statusBox.addModifyListener(getNeedsSaveListener());
		Enumeration statEnum = ReqStatus.elements();
		int j=0;
		ReqStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (ReqStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(getRequirement().getStatus().toString()) == 0)
			{
//				System.out.println(getRequirement().getStatus().toString());
				statusBox.select(j);
//				System.out.println(j);
			}
			j++;
		}
		
		new Label(parent, SWT.NONE).setText("Artifact:");
		
		artifactField = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		artifactField.addModifyListener(getNeedsSaveListener());
		if (getRequirement().getArtifact() != null)
		{
			artifactField.setText(getRequirement().getArtifact());
		}
		else
		{
			artifactField.setText("");
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		artifactField.setLayoutData(gridData);
		
	new Label(parent, SWT.NONE).setText("Importance:");
		
		importanceBox = new Combo(parent, SWT.NONE);
		Enumeration impEnum = Importance.elements();
		int l=0;
		Importance itype;
		while (impEnum.hasMoreElements())
		{
			itype = (Importance) impEnum.nextElement();
			importanceBox.add( itype.toString() );
			if (itype.toString().compareTo(getRequirement().getImportance().toString()) == 0)
			{
				importanceBox.select(l);
			}
			l++;
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		importanceBox.setLayoutData(gridData);
		importanceBox.addModifyListener(getNeedsSaveListener());
		
		new Label(parent, SWT.NONE).setText("Ontology Entry:");
		ontDesc = new Label(parent, SWT.WRAP);
		
		if (!isCreating() && ((getRequirement().getOntology() != null) && (getRequirement().getOntology().getID() > 0)))
		{
			ontDesc.setText(getRequirement().getOntology().toString());
		}
		else
		{
			ontDesc.setText("Undefined");
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		ontDesc.setLayoutData(gridData);
		
//		new Label(shell, SWT.NONE).setText(" ");
		
		selOntButton = new Button(parent, SWT.PUSH); 
		selOntButton.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		selOntButton.setLayoutData(gridData);
		ourParent = parent;
		
		if (getRequirement().getType() == ReqType.FR)
		{
			selOntButton.setEnabled(false);
		}

		selOntButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				OntEntry newOnt = null;
				SelectOntEntry ar = new SelectOntEntry(ourParent.getDisplay(), true);
				/*
				 SelectOntEntryGUI ar = new SelectOntEntryGUI(lf, true);
				 ar.show();
				 */
				newOnt = ar.getSelOntEntry();
				if (newOnt != null)
				{
					getRequirement().setOntology(newOnt);
					ontDesc.setText(newOnt.toString());
				}
				
			}
		});
		
		new Label(parent, SWT.NONE).setText("Arguments For");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		
		
		new Label(parent, SWT.NONE).setText("Arguments Against");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		
		forModel = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		int listHeight = forModel.getItemHeight() * 4;
		Rectangle trim = forModel.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		Vector listV = getRequirement().getArgumentsFor();
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			Argument arg = new Argument();
			arg.fromDatabase((String)listE.nextElement());
			
			forModel.add( arg.getName() );
			
			// Register Event Notification
			try
			{
				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onForArgumentUpdate");
			}
			catch( Exception e )
			{
				System.out.println("Requirement Editor: For Argument Update Notification Not Available!");
			}
		}    
		// add a list of arguments against to the right side
		forModel.setLayoutData(gridData);
		
		
		againstModel = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		listHeight = againstModel.getItemHeight() * 4;
		Rectangle rtrim = againstModel.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = rtrim.height;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		
		listV = getRequirement().getArgumentsAgainst();
		listE = listV.elements();
		while (listE.hasMoreElements())
		{
			Argument arg = new Argument();
			arg.fromDatabase((String)listE.nextElement());
			
			againstModel.add( arg.getName() );
			// Register Event Notification
			try
			{
				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onAgainstArgumentUpdate");
			}
			catch( Exception e )
			{
				System.out.println("Requirement Editor: Against Argument Update Notification Not Available!");
			}
		}    
		againstModel.setLayoutData(gridData);
		
		enableButton = new Button(parent, SWT.CHECK);
		enableButton.setText("Enabled");
		enableButton.addSelectionListener(getSelNeedsSaveListener());
		enableButton.setSelection(getRequirement().getEnabled());
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		enableButton.setLayoutData(gridData);
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");	

		// Register Event Notification For Changes To This Element
		try
		{
			RationaleDB.getHandle().Notifier().Subscribe(getRequirement(), this, "onUpdate");
		}
		catch( Exception e )
		{
			System.out.println("Requirement Editor: Updated Not Available!");
		}
		
		updateFormCache();
	}
	
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#saveData()
	 */
	public boolean saveData() {
		
		//We need to get changes in importance or enabled/disabled propagated without infinite loops!
		boolean updateArguments;
		
		updateArguments = false;
		
		//check to see if the ontology entry is specified
		if ((ReqType.fromString(typeBox.getItem(typeBox.getSelectionIndex())) == ReqType.FR) 
				|| (getRequirement().getOntology() != null))
		{
		ConsistencyChecker checker = new ConsistencyChecker(getRequirement().getID(), nameField.getText(), "Requirements");
		
		if(!nameField.getText().trim().equals("") &&
				(getRequirement().getName() == nameField.getText() || checker.check(false)))
		{
			getRequirement().setName(nameField.getText());
			getRequirement().setDescription(descArea.getText());
			getRequirement().setType(ReqType.fromString(typeBox.getItem(typeBox.getSelectionIndex())));
			getRequirement().setArtifact(artifactField.getText());
			getRequirement().setImportance( Importance.fromString(importanceBox.getItem(importanceBox.getSelectionIndex())));

			getRequirement().setEnabled(enableButton.getSelection());
			
			if( isCreating() ) {
				getRequirement().setParent(getParentElement());
				getRequirement().setStatus( ReqStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
				getRequirement().updateHistory(new History(getRequirement().getStatus().toString(), "Initial Entry"));
				
			}
			else {
				ReqStatus newStat = ReqStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
				if (!newStat.toString().equals(getRequirement().getStatus().toString()))
				{
					ReasonGUI rg = new ReasonGUI();
					String newReason = rg.getReason();
					getRequirement().setStatus(newStat);
					//				System.out.println(newStat.toString() + getRequirement().getStatus().toString());
					History newHist = new History(newStat.toString(), newReason);
					getRequirement().updateHistory(newHist);
					//				getRequirement().toDatabase(getRequirement().getParent(), RationaleElementType.fromString(getRequirement().getPtype()));
					//				newHist.toDatabase(getRequirement().getID(), RationaleElementType.REQUIREMENT);
				}
				
				//find out if we have any changes in importance or status
				if ((getRequirement().getEnabled() != dataCache.enabled) || (getRequirement().getImportance() != Importance.fromString(importanceBox.getItem(dataCache.importance))))
				{
					System.out.println("change in Requirement importance");
					System.out.println(getRequirement().getEnabled());
					System.out.println(getRequirement().getImportance());
					updateArguments = true; //we need to re-compute the evaluations
				}
			}
//			since this is a save, not an add, the type and parent are ignored
			getRequirement().setID(getRequirement().toDatabase(getRequirement().getParent(), RationaleElementType.fromString(getRequirement().getPtype())));
			
			//we need to update any arguments that this requirement is in. 
			Vector<Argument> argsUsing = RationaleDB.getDependentArguments(getRequirement().getID());
			Iterator argI = argsUsing.iterator();
			while (argI.hasNext())
			{
				Argument arg = (Argument) argI.next();
				
				//for now, don't get the status change - we're hoping this will do what we need
				arg.updateStatus();
			}
			return true;
		}
		else
		{
			String l_message = "";
			l_message += "The requirement name you have specified is either already"
				+ " in use or empty. Please make sure that you have specified"
				+ " a requirement name and the requirement name does not already exist"
				+ " in the database.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Requirement Name Is Invalid");
			mbox.open();
		}
		return false;
		}
		else
		{
			String l_message = "";
			l_message += "All non-functional requirements must have an associated ontology entry.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Missing Ontology Entry");
			mbox.open();
			return false;
		}
	}
	
	/**
	 * Wrap a requirement in a logical file.
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
		 * @return the requirement wrapped by this logical file
		 */
		public Requirement getData() { return (Requirement)getAdapter(Requirement.class); }
		
		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New Requirement Editor" :
				"Requirement: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Requirement.class;
		}
	}
}

