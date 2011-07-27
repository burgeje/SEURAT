package SEURAT.editors;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import edu.wpi.cs.jburge.SEURAT.editors.ConsistencyChecker;
import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.ReasonGUI;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.AlternativeStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Contingency;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Designer;
import edu.wpi.cs.jburge.SEURAT.rationaleData.History;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import SEURAT.events.RationaleUpdateEvent;

/**
 * This class provides the editor for alternatives.
 */
public class AlternativeEditor extends RationaleEditorBase {
	public static RationaleEditorInput createInput(RationaleExplorer explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new AlternativeEditor.Input(explorer, tree, parent, target, new1);
	}

	/**
	 * This class provides caching features used when updating
	 * properties of an alternative remotely.
	 */
	private class DataCache
	{
		/**
		 * last known good name for the alternative
		 */
		public String name;
		/**
		 * last known good description for the alternative
		 */
		public String description;
		/**
		 * last known good designer for the alternative
		 */
		public int designer;
		/**
		 * last known good status for the alternative
		 */
		public int status;
		/**
		 * last known good contingency for the alternative
		 */
		public int contingency;
	}
	
	/**
	 * The name of the alternative
	 */
	private Text nameField;
	
	/**
	 * The description field
	 */
	private Text descArea;
	
	/**
	 * Combo box to select status
	 */
	private Combo statusBox;
	
	/**
	 * Combobox used for selecting a designer initially
	 */
	private Combo designerBox;	
	
	/**
	 * Label used to display selected designer for existing alternatives
	 */
	private Label designerLabel;
	
	/**
	 * Composite used for replacing desingerBox with designerLabel on initial save
	 */
	private Composite designerComposite;
	
	/**
	 * Combo box to select contingencies
	 */
	private Combo contingencyBox;
	
	/**
	 * The artifacts associated with this alternative
	 */
	private List artifacts;
	
	/**
	 * The relationships to other alternatives - displayed in a list
	 */
	private List relationships;
	
	/**
	 * Arguments for - displayed in a list
	 */
	private List forModel;
	
	/**
	 * Arguments against - displayed in a list
	 */
	private List againstModel;
	
	/**
	 * The parent
	 */
	private Composite parent;
	
	/**
	 * member variable that stores all data that is
	 * cached for remote updates of alternative data
	 */
	private DataCache dataCache = new DataCache();
	
	/**
	 * Respond to changes which have been made to the alternative
	 * being edited. Also respond to new arguments created which
	 * are children of this alternative.
	 * 
	 * @param pElement the alternative which generated the event
	 * @param pEvent the rationale update event which describes
	 * 		what caused the event to be generated.
	 */
	public void onUpdate(Alternative pElement, RationaleUpdateEvent pEvent)
	{
		try
		{
			if( pEvent.getElement() != pElement &&
				pEvent.getCreated() &&
				pEvent.getElement() instanceof Argument )
			{
				refreshArguments(pEvent);
			}
			else
			if( pEvent.getElement().equals(getAlternative()) )
			{
				if( pEvent.getDestroyed() )
				{
					closeEditor();
				}
				else
				if( pEvent.getModified() )
				{
					refreshForm(pEvent);
					if (statusBox != null && !isCreating()){
						if (getAlternative().isUMLAssociated()){
							statusBox.setEnabled(false);
						}
						else {
							statusBox.setEnabled(true);
						}
					}
				}
			}			
		}
		catch( Exception eError )
		{
			System.out.println("Exception in AlternativeEditor: onUpdate");
		}
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#editorType()
	 */
	public Class editorType() {
		return Alternative.class;
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#getRationaleElement()
	 */
	public RationaleElement getRationaleElement() {
		return getAlternative();
	}

	/**
	 * @return the alternative which is the logical file of this editor
	 */
	public Alternative getAlternative() {
		return (Alternative)getEditorData().getAdapter(Alternative.class);
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#onRefreshForm(SEURAT.events.RationaleUpdateEvent)
	 */
	@Override
	protected void onRefreshForm(RationaleUpdateEvent pEvent) {
		Enumeration iter = null;
		int index = 0;
		boolean l_dirty = isDirty();
		
		// Something Has Changed, Reload This Element From The DB
		getAlternative().fromDatabase(getAlternative().getID());
		
		// Only Change Fields That Have Not Been Modified
		if( nameField.getText().equals(dataCache.name) )
		{
			nameField.setText(getAlternative().getName());
			dataCache.name = nameField.getText();
		}
		else
			l_dirty = true;
		
		if( descArea.getText().equals(dataCache.description))
		{
			descArea.setText(getAlternative().getDescription());
			dataCache.description = descArea.getText();
		}
		else
			l_dirty = true;
		
		unloadDesignerControls();
		loadDesignerLabel();
		dataCache.designer = 0;		
			
		if( statusBox.getSelectionIndex() == dataCache.status )
		{
			for( iter = AlternativeStatus.elements(), index = 0 ; 
				iter.hasMoreElements() ; 
				index++ )
			{
				AlternativeStatus stype = (AlternativeStatus) iter.nextElement();
				if (stype.toString().equals(getAlternative().getStatus().toString()))
				{
					statusBox.select(index);
				}			
			}
			dataCache.status = statusBox.getSelectionIndex();
		}
		else
			l_dirty = true;
		
		if( contingencyBox.getSelectionIndex() == dataCache.contingency )
		{
			Vector ourConts = RationaleDB.getHandle().getNameList(RationaleElementType.CONTINGENCY);
			contingencyBox.select(0);
			if (ourConts != null)
			{
				for( iter = ourConts.elements(), index = 0 ;
					 iter.hasMoreElements() ; index++ )
				{
					String des = (String)iter.nextElement();
					
					if (des.equals("Design-Contingencies"))
						continue;
						
					if (getAlternative().getContingency() != null &&
						des.equals(getAlternative().getContingency().getName()))
					{
						contingencyBox.select(index);
					}							
				}
			}
			dataCache.contingency = contingencyBox.getSelectionIndex();
		}
		else
			l_dirty = true;
		
		if (pEvent != null && 
				pEvent.getTag() != null &&
				pEvent.getTag().equals("artifacts")) {
				// we need to reload the artifacts box
				artifacts.removeAll();
				
				Iterator artI = getAlternative().getArtifacts().iterator();
				int ndata = 1;
				while (artI.hasNext())
				{
					artifacts.add((String) artI.next());
					ndata++;
				}
				
				if (ndata > 1)
					ndata--;
			}
		
		setDirty(l_dirty);
	}

	/**
	 * Remove the designer control which is currently displayed
	 * in the form. 
	 */
	public void unloadDesignerControls()
	{
		if( designerLabel != null )
		{
			designerLabel.dispose();
			designerLabel = null;
		}
		if( designerBox != null )
		{
			designerBox.dispose();
			designerBox = null;
		}		
	}
	
	/**
	 * Insert a designer combobox into the form.
	 */
	public void loadDesignerComboBox()
	{
		designerBox = new Combo(designerComposite, SWT.NONE);
		designerBox.select(0);	
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setComboDimensions(designerBox, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		designerBox.setLayoutData(gridData);
		designerBox.addModifyListener(getNeedsSaveListener());
		
		RationaleDB db = RationaleDB.getHandle();
		Vector ourDesigners = db.getNameList(RationaleElementType.DESIGNER);
		if (ourDesigners != null)
		{
			Enumeration desEnum = ourDesigners.elements();
			while (desEnum.hasMoreElements())
			{
				String des = (String) desEnum.nextElement();
				if (des.compareTo("Designer-Profiles") != 0)
				{
					designerBox.add( des );					
				}
			}
		}
	}
	
	/**
	 * Insert a label containing the selected designer's name
	 * into the form.
	 */
	public void loadDesignerLabel()
	{
		designerLabel = new Label(designerComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		designerLabel.setLayoutData(gridData);
		
		if (getAlternative().getDesigner() != null)
		{
			designerLabel.setText(getAlternative().getDesigner().getName());
		}
		else
		{
			designerLabel.setText("No designer is associated with this Alternative");		
		}
		
		designerComposite.layout();
	}
	
	
	/**
	 * Rebuild the listboxes containing the set of all children
	 * of the alternative being edited.
	 * 
	 * @param pEvent the rationaleUpdateEvent which caused this
	 * function to be called. This argument may be null.
	 */
	public void refreshArguments(RationaleUpdateEvent pEvent)
	{
		boolean l_dirty = isDirty();
		
		// Something Has Changed, Reload This Element From The DB
		getAlternative().fromDatabase(getAlternative().getID());
		
		// A New Argument Has Been Created As A Child Of This Requirement,
		// With Lack Of A Keen Understanding Of How Arguments Are
		// Identified As For And Against, We Will Regenerate The
		// Entire List Of Arguments For This Requirement
		Vector arguments;
		String subscribeFunc = null;
		Enumeration iterator;
		forModel.removeAll();
		againstModel.removeAll();
		relationships.removeAll();
		
		arguments = getAlternative().getArgumentsFor();
		iterator = arguments.elements();
		while (iterator.hasMoreElements())
		{
			Argument arg = (Argument)iterator.nextElement();
			
			// TODO: This probably isn't necessary but prevents possible
			// 		null pointer exceptions
			if( arg == null ) continue;
			
			if( pEvent == null ||
				!arg.equals(pEvent.getElement()) ||
				!pEvent.getDestroyed() )
			{
				forModel.add( arg.getName() );
			}

			if( pEvent == null )
				continue;			
			if( !arg.equals(pEvent.getElement()) )
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
		
		arguments = getAlternative().getArgumentsAgainst();
		iterator = arguments.elements();
		while( iterator.hasMoreElements() )
		{
			Argument arg = (Argument)iterator.nextElement();	
			
			// TODO: This probably isn't necesarry but prevents possible
			// 		null pointer exceptions
			if( arg == null ) continue;
			
			if( pEvent == null ||
				!arg.equals(pEvent.getElement()) ||
				!pEvent.getDestroyed() )
			{
				againstModel.add( arg.getName() );
			}

			if( pEvent == null )
				continue;			
			if( !arg.equals(pEvent.getElement()) )
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

		arguments = getAlternative().getRelationships();
		iterator = arguments.elements();
		while( iterator.hasMoreElements() )
		{
			Argument arg = (Argument)iterator.nextElement();
			
			// TODO: This probably isn't necesarry but prevents possible
			// 		null pointer exceptions
			if( arg == null ) continue;
			
			if( pEvent == null ||
				!arg.equals(pEvent.getElement()) ||
				!pEvent.getDestroyed() )
			{
				relationships.add( arg.getName() );
			}

			if( pEvent == null )
				continue;			
			if( !arg.equals(pEvent.getElement()) )
				continue;

			// If the element was created, or it's relationship
			// to this alternative has changed we need to register
			// a new subscription.
			if( pEvent.getCreated() ||
				(pEvent.getModified() && relationships != pEvent.getTag()))
			{
				subscribeFunc = "onRelationshipUpdate";				
			}
		}
		
		// The New Argument Will Not Publish Further Events Using
		// The Parent As A Pseudonym, therefore we must subscribe to it's
		// publications
		try
		{			
			if (pEvent != null )
			{
				RationaleDB l_db = RationaleDB.getHandle();
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
			System.out.println("Alternative Editor: Created Argument Subscription Failure");
		}
		
		setDirty(l_dirty);
	}
	
	/**
	 * Respond to events generated by an argument which is for
	 * the alternative being edited.
	 * 
	 * @param pElement the argument which generated the event
	 * @param pEvent a description of what changes were made to the 
	 * 		argument
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
	 * Respond to events generated by an argument which is
	 * against the alternative being edited.
	 * 
	 * @param pElement the argument which generated the event
	 * @param pEvent a description of what changes were made to
	 * 		the argument
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
	/**
	 * Respond to events generated by an argument which is
	 * related but not strictly for or against the alternative
	 * being edited.
	 * 
	 * @param pElement the argument which generated the event
	 * @param pEvent a description of what changes were made to 
	 * 		the argument
	 */
	public void onRelationshipUpdate(Argument pElement, RationaleUpdateEvent pEvent)
	{
		try
		{
			pEvent.setTag(relationships);
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
		this.parent = parent;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		if (isCreating())
		{
			getAlternative().setStatus(AlternativeStatus.ATISSUE);
		}
		
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getAlternative().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);
		
		designerComposite = new Composite(parent, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.makeColumnsEqualWidth = true;
		designerComposite.setLayout(gridLayout);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 6;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;		
		designerComposite.setLayoutData(gridData);

		new Label(designerComposite, SWT.NONE).setText("Designer:");		
		if (isCreating())
		{
			loadDesignerComboBox();
		}
		else
		{
			loadDesignerLabel();
			// Cache Last Known Value
			dataCache.designer = 0;
		}
		
		new Label(parent, SWT.NONE).setText("Description:");
		
		descArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(getAlternative().getDescription());
		descArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 50, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = descArea.getLineHeight() * 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		descArea.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Status:");
		statusBox = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		statusBox.addModifyListener(getNeedsSaveListener());
		Enumeration statEnum = AlternativeStatus.elements();
		int j=0;
		AlternativeStatus stype;
		while (statEnum.hasMoreElements())
		{
			stype = (AlternativeStatus) statEnum.nextElement();
			statusBox.add( stype.toString() );
			if (stype.toString().compareTo(getAlternative().getStatus().toString()) == 0)
			{
//				System.out.println(ourAlt.getStatus().toString());
				statusBox.select(j);
				
//				System.out.println(j);
			}
			j++;
		}		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		statusBox.setLayoutData(gridData);
		if (!isCreating() && getAlternative().isUMLAssociated()){
			statusBox.setEnabled(false);
		}
		else statusBox.setEnabled(true);
		
		new Label(parent, SWT.NONE).setText("Design Type:");
		
		RationaleDB db = RationaleDB.getHandle();
		contingencyBox = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		contingencyBox.addModifyListener(getNeedsSaveListener());
		Vector ourConts = db.getNameList(RationaleElementType.CONTINGENCY);
		contingencyBox.select(0);
		int cindex = 0;
		if (ourConts != null)
		{
			Enumeration desEnum = ourConts.elements();
			while (desEnum.hasMoreElements())
			{
				String des = (String) desEnum.nextElement();
				if (des.compareTo("Design-Contingencies") != 0)
				{
					contingencyBox.add( des );	
					if (getAlternative().getContingency() != null)
					{
						if (des.compareTo(getAlternative().getContingency().getName()) == 0)
						{
							contingencyBox.select(cindex);
						}							
					}
					
					cindex ++;
				}
			}
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		contingencyBox.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Artifact:");
		
		artifacts = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		Iterator artI = getAlternative().getArtifacts().iterator();
		int ndata = 1;
		while (artI.hasNext())
		{
			artifacts.add((String) artI.next());
			ndata++;
		}
		
		if (ndata > 1)
			ndata--;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setListDimensions(artifacts, gridData, ndata, 100);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		
		artifacts.setLayoutData(gridData);
			
		new Label(parent, SWT.NONE).setText("Arguments For");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		
		new Label(parent, SWT.NONE).setText("Arguments Against");
		new Label(parent, SWT.NONE).setText(" ");
		new Label(parent, SWT.NONE).setText(" ");
		
		forModel = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		Vector listV = getAlternative().getArgumentsFor();
		Enumeration listE = listV.elements();
		while (listE.hasMoreElements())
		{
			Argument arg = (Argument)listE.nextElement();			
			arg.fromDatabase(arg.getID());
			
			forModel.add( arg.getName() );
			
			// Register Event Notification
			try
			{
				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onForArgumentUpdate");
			}
			catch( Exception e )
			{
				System.out.println("Alternative Editor: For Argument Updates Not Available!");
			}
		}    
		// add a list of arguments against to the right side
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL );
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		DisplayUtilities.setListDimensions(forModel, gridData, 2, 100);
		gridData.horizontalSpan = 3;
		
		forModel.setLayoutData(gridData);
		
		
		againstModel = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		listV = getAlternative().getArgumentsAgainst();
		listE = listV.elements();
		while (listE.hasMoreElements())
		{
			Argument arg = (Argument)listE.nextElement();			
			arg.fromDatabase(arg.getID());
			
			againstModel.add( arg.getName() );
			
			// Register Event Notification
			try
			{
				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onAgainstArgumentUpdate");
			}
			catch( Exception e )
			{
				System.out.println("Alternative Editor: Against Argument Updates Not Available!");
			}
		}  
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		DisplayUtilities.setListDimensions(againstModel, gridData, 2, 100);
		
		againstModel.setLayoutData(gridData);
		
		//now, a row for relationships
		new Label(parent, SWT.NONE).setText(" ");
		Label relL = new Label (parent, SWT.NONE);
		relL.setText("Relationships");
		gridData = new GridData(GridData.CENTER);
		gridData.horizontalSpan = 4;
		relL.setLayoutData(gridData);
		new Label(parent, SWT.NONE).setText(" ");
		
		new Label(parent, SWT.NONE).setText(" ");
		relationships = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		listV = getAlternative().getRelationships();
		listE = listV.elements();
		while (listE.hasMoreElements())
		{
			Argument arg = (Argument)listE.nextElement();			
			arg.fromDatabase(arg.getID());
			
			relationships.add( arg.getName() );
			
			// Register Event Notification
			try
			{
				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onRelationshipUpdate");
			}
			catch( Exception e )
			{
				System.out.println("Alternative Editor: Relationship Updates Not Available!");
			}
		}    
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		DisplayUtilities.setListDimensions(relationships, gridData, 4, 100);
		
		relationships.setLayoutData(gridData);
		
		// Register Event Notification
		try
		{
			RationaleDB.getHandle().Notifier().Subscribe(getAlternative(), this, "onUpdate");
		}
		catch( Exception e )
		{
			System.out.println("Alternative Editor: Alternative Updates Not Available!");
		}
		
		updateFormCache();
	}
	
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#updateFormCache()
	 */
	protected void updateFormCache()
	{
		if( nameField != null )
			dataCache.name = nameField.getText();
		
		if( designerBox != null )
			dataCache.designer = designerBox.getSelectionIndex();
		else
			dataCache.designer = 0;
		
		if( descArea != null )
			dataCache.description = descArea.getText();
		
		if( statusBox != null )
			dataCache.status = statusBox.getSelectionIndex();
		
		if( contingencyBox != null )
			dataCache.contingency = contingencyBox.getSelectionIndex();
		
	}
		
	/* (non-Javadoc)
	 * @see SEURAT.editors.RationaleEditorBase#saveData()
	 */
	public boolean saveData() {
		ConsistencyChecker checker = new ConsistencyChecker(getAlternative().getID(), nameField.getText(), "Alternatives");
		
		if(!nameField.getText().trim().equals("") &&
				(getAlternative().getName() == nameField.getText() || checker.check(false)))
		{
			if (isCreating()) {
				// Set the alternative's parent data correctly
				getAlternative().setParent(getParentElement());
				if (((designerBox.getItemCount() <= 0) || designerBox.getSelectionIndex() >= 0)
						&& ( contingencyBox.getItemCount() <= 0 || contingencyBox.getSelectionIndex() >= 0))
				{
					getAlternative().setName(nameField.getText());
					getAlternative().setDescription(descArea.getText());
					getAlternative().setStatus( AlternativeStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex())));
					getAlternative().updateHistory(new History(getAlternative().getStatus().toString(), "Initial Entry"));
					if (designerBox.getItemCount() > 0)
					{
						String designerName = designerBox.getItem(designerBox.getSelectionIndex());
						Designer ourDes = new Designer();
						ourDes.fromDatabase(designerName);
						getAlternative().setDesigner(ourDes);
					}
					
					if (contingencyBox.getItemCount() > 0)
					{
						String contName = contingencyBox.getItem(contingencyBox.getSelectionIndex());
						Contingency ourCont = new Contingency();
						ourCont.fromDatabase(contName);
						getAlternative().setContingency(ourCont);
					}
					getAlternative().setID(getAlternative().toDatabase(getAlternative().getParent(), getAlternative().getPtype()));
					return true;
				}
				else
				{
					MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
					mbox.setMessage("Need to provide both Designer Name and Design Type");
					mbox.open();
				}
			} else {
				boolean changeOk = true;
				
				if(getAlternative().getName() == nameField.getText() || checker.check())
				{
					getAlternative().setName(nameField.getText());
					getAlternative().setDescription(descArea.getText());
					if (contingencyBox.getItemCount() > 0)
					{
						String contName = contingencyBox.getItem(contingencyBox.getSelectionIndex());
						Contingency ourCont = new Contingency();
						ourCont.fromDatabase(contName);
						getAlternative().setContingency(ourCont);
					}
					AlternativeStatus newStat = AlternativeStatus.fromString(statusBox.getItem(statusBox.getSelectionIndex()));
					if (!newStat.toString().equals(getAlternative().getStatus().toString()))
					{
						//check to see if the alternative was rejected earlier
						boolean wasRejected = false;
						
						Enumeration histE = getAlternative().getHistory();
						while (histE.hasMoreElements())
						{
							History hist = (History) histE.nextElement();
							if (hist.getStatus().compareTo(AlternativeStatus.REJECTED.toString()) == 0)
							{
								wasRejected = true;
							}
						}
						if ((wasRejected) && (newStat == AlternativeStatus.ADOPTED))
						{
							changeOk = showQuestion("This alternative was rejected earlier. Select anyway?");
						}
						if (changeOk)
						{
							ReasonGUI rg = new ReasonGUI();
							String newReason = rg.getReason();
							getAlternative().setStatus(newStat);
							//					System.out.println(newStat.toString() + ourAlt.getStatus().toString());
							History newHist = new History(newStat.toString(), newReason);
							getAlternative().updateHistory(newHist);
							//					ourAlt.toDatabase(ourAlt.getParent(), RationaleElementType.fromString(ourAlt.getPtype()));
												newHist.toDatabase(getAlternative().getID(), RationaleElementType.ALTERNATIVE);
						}
					}
					if (changeOk)
					{
						//since this is a save, not an add, the type and parent are ignored
						//				System.out.println("Saving alternative from edit-update");
						getAlternative().setID(getAlternative().toDatabase(getAlternative().getParent(), getAlternative().getPtype()));
						return true;
					}
				}
			}
		}
		else
		{
			String l_message = "";
			l_message += "The alternative name you have specified is either already"
				+ " in use or empty. Please make sure that you have specified"
				+ " an alternative name and the alternative name does not already exist"
				+ " in the database.";
			MessageBox mbox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			mbox.setMessage(l_message);
			mbox.setText("Alternative Name Is Invalid");
			mbox.open();
		}
		return false;
	}
	
	/** 
	 * Used to ask the user questions when saving the alternative. 
	 * These could be status questions (to supply a history) or ask for confirmation
	 * if they try to select a previously rejected alternative
	 * @param message
	 * @return the response to the dialog
	 */
	private boolean showQuestion(String message) {
		return MessageDialog.openQuestion(
				parent.getShell(),
				"Save Alternative",
				message);
	}
	
	/**
	 * Wraps an alternative into a logical file.
	 */
	public static class Input extends RationaleEditorInput {
		
		/**
		 * @param explorer RationaleExplorer
		 * @param tree the element in the RationaleExplorer tree for the alternative
		 * @param parent the parent of the alternative in the RationaleExplorer tree
		 * @param target the alternative to associate with the logical file
		 * @param new1 true if the alternative is being created, false if it already exists
		 */
		public Input(RationaleExplorer explorer, TreeParent tree,
				RationaleElement parent, RationaleElement target, boolean new1) {
			super(explorer, tree, parent, target, new1);
		}

		/**
		 * @return the Alternative that is wrapped by the logical file
		 */
		public Alternative getData() { return (Alternative)getAdapter(Alternative.class); }
		
		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#getName()
		 */
		@Override
		public String getName() {
			return isCreating() ? "New Alternative Editor" :
				"Alternative: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Alternative.class;
		}		
	}
}

