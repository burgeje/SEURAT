package SEURAT.search;

import java.util.Enumeration;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.editors.SelectItem;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ArgType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Assumption;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Claim;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Importance;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Plausibility;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import org.eclipse.swt.layout.*;

import SEURAT.search.entities.EntitySearchQuery;
import SEURAT.search.importanceOverrides.ImportanceOverrideSearchQuery;
import SEURAT.search.requirements.RequirementSearchQuery;
import SEURAT.search.statusOverrides.StatusOverrideSearchQuery;
import SEURAT.search.commonArguments.CommonArgumentSearchQuery;

/*
 * Specialization Of Eclipse Search Page Interface
 * For Accessing All Of The Query Features Of
 * SEURAT
 */
public class SearchPage extends DialogPage implements ISearchPage {

	/**
	 * Instance Variable Providing Easy Access To The Search 
	 * Window Owner
	 */
	private ISearchPageContainer m_SearchPageContainer;
	
	/*
	 * Start The Specified Query When The Search Button Is
	 * Clicked
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchPage#performAction()
	 */
	public boolean performAction() 
	{
		ISearchQuery l_query = null;

		if( m_SearchTypeData.m_SpecialParameters instanceof CommonArgumentControl )
		{
			CommonArgumentControl l_control = (CommonArgumentControl)m_SearchTypeData.m_SpecialParameters;
			RationaleElementType l_type = null;
			Enumeration l_i = RationaleElementType.elements();
			while (l_i.hasMoreElements())
			{
				l_type = (RationaleElementType)l_i.nextElement();
				if( l_type.toString().equals(l_control.m_ArgumentType.getText()) )
					break;
			}
			
			// TODO: Handle Error When Invalid Selection
			
			l_query = new CommonArgumentSearchQuery(l_type, l_control.m_SelectedOnly.getSelection());
		}
		else
		if( m_SearchTypeData.m_SpecialParameters instanceof EntitySearchControl )
		{
			EntitySearchControl l_control = (EntitySearchControl)m_SearchTypeData.m_SpecialParameters;
			RationaleElementType l_type = null;
			Enumeration l_i = RationaleElementType.elements();
			while (l_i.hasMoreElements())
			{
				l_type = (RationaleElementType)l_i.nextElement();
				if( l_type.toString().equals(l_control.m_EntityTypes.getText()) )
					break;
			}

			// TODO: Handle Error When Invalid Selection
			l_query = new EntitySearchQuery(l_type, l_control.m_SearchString.getText());
		}
		else
		if( m_SearchTypeData.m_SpecialParameters instanceof RequirementStatusControl )
		{
			RequirementStatusControl l_control = (RequirementStatusControl)m_SearchTypeData.m_SpecialParameters;
			ReqStatus l_status = null;
			Enumeration reqE = ReqStatus.elements();
			while (reqE.hasMoreElements())
			{
				l_status = (ReqStatus)reqE.nextElement();
				if( l_status.toString().equals(l_control.m_RequirementStatus.getText()) )
					break;
			}

			// TODO: Handle Error When Invalid Selection
			l_query = new RequirementSearchQuery(l_status);
		}
		else
		if( m_SearchTypeData.m_StatusOverride.getSelection() )
		{			
			l_query = new StatusOverrideSearchQuery();			
		}
		else
		if( m_SearchTypeData.m_ImportanceOverride.getSelection() )
			l_query = new ImportanceOverrideSearchQuery();
			
		NewSearchUI.runQueryInBackground(l_query);
			
		return true;
	}

	/**
	 * Called By The Eclipse Interface To Inform Each
	 * SearchPage Instance About The Search Window
	 * Which has instantiated it.
	 */
	public void setContainer(ISearchPageContainer pSearchPageContainer) {
		m_SearchPageContainer = pSearchPageContainer;
	}

	/**
	 * Refresh All SWT Elements In The SEURAT Search Page
	 */
	void refreshForm()
	{
		m_ControlMain.layout();
		m_ControlMain.getParent().layout();			
	}
	
	/**
	 * Container For Search Parameters Which
	 * Are Dependent On The Type Of Search Being Performed
	 * 
	 * @author hannasm
	 */
	class SearchTypeData
	{
		/**
		 * Specifies A Default Implementation For The Search
		 * Type Selection Listener
		 * 
		 * @author hannasm
		 */
		class SearchTypeData_SelectionBase implements SelectionListener
		{
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent pEvent) {				
				widgetSelected(pEvent);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent pEvent) {
				destroySpecialParameters();
				
				setupParametersControl();

				refreshForm();
			}	
			
			/**
			 * Initialize the controls used for specifying
			 * query specific parameters.
			 */
			public void setupParametersControl() {
				m_SpecialParameters = null;
			}
		}
		
		/**
		 * Alias for SearchTypeData_SelectionBase
		 * 
		 * @author hannasm
		 */
		class DefaultSelected extends SearchTypeData_SelectionBase {}
		
		/**
		 * Create the entity query specific controls when
		 * selection changes
		 * 
		 * @author hannasm
		 */
		class EntitySelected extends SearchTypeData_SelectionBase
		{
			/* (non-Javadoc)
			 * @see SEURAT.search.SearchPage.SearchTypeData.SearchTypeData_SelectionBase#setupParametersControl()
			 */
			public void setupParametersControl() {
				m_SpecialParameters = new EntitySearchControl
				(
					createSpecialParameters("Entity Search (Parameters)")
				);
			}			
		}
		/**
		 * Create the requirement status query specific controls
		 * when the selection changes.
		 * 
		 * @author hannasm
		 */
		class RequirementStatusSelected extends SearchTypeData_SelectionBase
		{
			/* (non-Javadoc)
			 * @see SEURAT.search.SearchPage.SearchTypeData.SearchTypeData_SelectionBase#setupParametersControl()
			 */
			public void setupParametersControl() {
				m_SpecialParameters = new RequirementStatusControl
				(
					createSpecialParameters("Requirement Status Search (Parameters)")
				);	
			}			
		}
		/**
		 * Creates the common argument query specific controls
		 * when the selection changes.
		 * 
		 * @author hannasm
		 */
		class CommonArgumentOptionsSelected extends SearchTypeData_SelectionBase
		{
			/* (non-Javadoc)
			 * @see SEURAT.search.SearchPage.SearchTypeData.SearchTypeData_SelectionBase#setupParametersControl()
			 */
			public void setupParametersControl() {
				m_SpecialParameters = new CommonArgumentControl
				(
					createSpecialParameters("Common Argument Search (Parameters)")
				);
			}			
		}
		
		/**
		 * Initialize the Search Type Group and radio buttons for
		 * all available query types.
		 * 
		 * @param pParent the Composite which should be the parent element.
		 */
		public SearchTypeData(Composite pParent)
		{		
			m_SpecialParameters = null;
			
			GridData l_gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			l_gridData.grabExcessHorizontalSpace = true;
			l_gridData.horizontalAlignment = GridData.FILL;
			l_gridData.grabExcessVerticalSpace = false;
			l_gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
			
			m_SearchType = new Group(pParent, SWT.SHADOW_OUT);
			m_SearchType.setText("Search Type");
			m_SearchType.setLayoutData(l_gridData);
			
			GridLayout l_searchTypeLayout = new GridLayout(2, true);
			l_searchTypeLayout.marginLeft = 5;
			l_searchTypeLayout.marginRight = 5;
			l_searchTypeLayout.verticalSpacing = 5;
			l_searchTypeLayout.horizontalSpacing = 5;
			m_SearchType.setLayout(l_searchTypeLayout);
			
			m_Entity = new Button(m_SearchType, SWT.RADIO);
			m_Entity.setText("Entity");
			m_Entity.addSelectionListener(new EntitySelected());
			
			m_StatusOverride = new Button(m_SearchType, SWT.RADIO);
			m_StatusOverride.setText("Status Override");
			m_StatusOverride.addSelectionListener(new DefaultSelected());
			
			m_Requirement = new Button(m_SearchType, SWT.RADIO);
			m_Requirement.setText("Requirement");
			m_Requirement.addSelectionListener(new RequirementStatusSelected());
			
			m_ImportanceOverride = new Button(m_SearchType, SWT.RADIO);
			m_ImportanceOverride.setText("Importance Override");		
			m_ImportanceOverride.addSelectionListener(new DefaultSelected());
			
			m_CommonArgument = new Button(m_SearchType, SWT.RADIO);
			m_CommonArgument.setText("Common Argument");
			m_CommonArgument.addSelectionListener(new CommonArgumentOptionsSelected());
			
			m_Entity.setSelection(true);
			new EntitySelected().widgetSelected(null);
		}
		/**
		 * Group Box Which Is Parent To All Search Type
		 * Radio Buttons.
		 */
		Group m_SearchType;
		/**
		 * Radio Button For Entity Query
		 */
		Button m_Entity;
		/**
		 * Radio Button For Requirement Status Query
		 */
		Button m_Requirement;
		/**
		 * Radio Button For Common Argument Query
		 */
		Button m_CommonArgument;
		/**
		 * Radio Button For Status Override Query
		 */
		Button m_StatusOverride;
		/**
		 * Radio Button For Importance Override Query
		 */
		Button m_ImportanceOverride;
		/**
		 * A Tag-Like Object Which May or May Not
		 * Indicate Query Specific Data
		 */
		Object m_SpecialParameters;
	};
	
	/**
	 * Top Level Element Defining the SEURAT Search Page
	 */
	Composite m_ControlMain;
	/**
	 * Lower level element to hold the search string
	 */
	Composite m_SearchComposite;
	/**
	 * Descriptive Label For The SEURAT Search Page
	 */
	Label m_MainLabel;	
	/**
	 * Instance Variable Which Stores All The Query Specific
	 * Search Data
	 */
	SearchTypeData m_SearchTypeData;
	/**
	 * Group Of Controls Which Can Be Both Null Or 
	 * The Top-Level Element For Query Specific Parameters
	 */
	Group m_SpecialParameters;
	
	/**
	 * Construct the controls needed to fully specify
	 * entity queries.
	 * 
	 * @author hannasm
	 */
	class EntitySearchControl
	{
		Combo m_EntityTypes;
		Text m_SearchString;
		
		public EntitySearchControl(Composite pParent)
		{
			m_EntityTypes = new Combo(pParent, SWT.NONE);
			
			Enumeration typeEnum = RationaleElementType.elements();
			while (typeEnum.hasMoreElements())
			{
				RationaleElementType rtype = (RationaleElementType) typeEnum.nextElement();
				if ((rtype != RationaleElementType.NONE) &&
						(rtype != RationaleElementType.RATIONALE))
				{
					m_EntityTypes.add(rtype.toString());
				}				
			}
			m_EntityTypes.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			m_EntityTypes.select(0);
			
			m_SearchComposite = new Composite(pParent, SWT.NONE);
			
			GridLayout ssLayout = new GridLayout(2, true);
			ssLayout.marginLeft = 5;
			ssLayout.marginRight = 5;
			ssLayout.verticalSpacing = 5;
			ssLayout.horizontalSpacing = 5;
			
			
			m_SearchComposite.setLayout(ssLayout);
			
			new Label(m_SearchComposite, SWT.NONE).setText("Search Text (case sensitive):");
			
			m_SearchString = new Text(m_SearchComposite, SWT.SINGLE | SWT.BORDER);
			m_SearchString.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		}
	}

	/**
	 * Construct the controls needed to fully specify
	 * requirement status queries.
	 * 
	 * @author hannasm
	 */
	class RequirementStatusControl
	{
		Combo m_RequirementStatus;
		
		public RequirementStatusControl(Composite pParent)
		{
			m_RequirementStatus = new Combo(pParent, SWT.NONE);
			
			Enumeration reqE = ReqStatus.elements();
			while (reqE.hasMoreElements())
			{
				m_RequirementStatus.add(((ReqStatus) reqE.nextElement()).toString());
			}
			m_RequirementStatus.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			m_RequirementStatus.select(0);
		}
	}

	/**
	 * Construct the controls needed to fully specify
	 * common argument queries.
	 * 
	 * @author hannasm
	 */
	class CommonArgumentControl
	{
		Combo m_ArgumentType;
		Button m_SelectedOnly;
		
		public CommonArgumentControl(Composite pParent)
		{
			m_ArgumentType = new Combo(pParent, SWT.NONE);
			m_ArgumentType.add(RationaleElementType.ASSUMPTION.toString());
			m_ArgumentType.add(RationaleElementType.CLAIM.toString());
			m_ArgumentType.add(RationaleElementType.ONTENTRY.toString());
			m_ArgumentType.select(0);
			
			m_SelectedOnly = new Button(pParent, SWT.CHECK);
			m_SelectedOnly.setText("Find Selected Alternatives Only");
		}
	}
	
	/**
	 * Destroy the control used for specifying
	 * query specific parameters
	 */
	private void destroySpecialParameters() {
		if( m_SpecialParameters == null ) return;
		
		m_SpecialParameters.dispose();
		m_SpecialParameters = null;
	}
	
	/**
	 * @param pLabel the name to give to the query specific parameters
	 *					group box
	 *
	 * @return a SWT group box parented to the search pages main element
	 */
	private Group createSpecialParameters(String pLabel) {
		GridData l_gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		l_gridData.grabExcessHorizontalSpace = true;
		l_gridData.horizontalAlignment = GridData.FILL;
		l_gridData.grabExcessVerticalSpace = false;
		l_gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		
		m_SpecialParameters = new Group(m_ControlMain, SWT.SHADOW_OUT);
		m_SpecialParameters.setText(pLabel);
		m_SpecialParameters.setLayoutData(l_gridData);
		
		m_SpecialParameters.setLayout(new GridLayout(1, true));
		
		return m_SpecialParameters;
	}
	
	/* 
	 * Construct the SEURAT search page parented to the specified SWT control.
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite pParent) {
		pParent.setLayout(new GridLayout());
		
		m_ControlMain = new Composite(pParent, SWT.NONE);
		
		//FillLayout l_controlLayout = new FillLayout(SWT.VERTICAL);		
		//m_ControlMain.setLayout(l_controlLayout);
		
		m_ControlMain.setLayout(new GridLayout(1, true));

		GridData l_gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		l_gridData.grabExcessHorizontalSpace = true;
		l_gridData.horizontalAlignment = GridData.FILL;
		l_gridData.grabExcessVerticalSpace = false;
		l_gridData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		m_MainLabel = new Label(m_ControlMain, SWT.NONE);
		m_MainLabel.setText("SEURAT - Rationale Search");
		m_MainLabel.setLayoutData(l_gridData);
		
		m_SearchTypeData = new SearchTypeData(m_ControlMain);

		m_ControlMain.pack();
		m_ControlMain.layout();
		
		setControl(m_ControlMain);
	}

	/* 
	 * Get the top-level control for the SEURAT search page
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#getControl()
	 */
	public Control getControl() {
		return m_ControlMain;
	}

}
