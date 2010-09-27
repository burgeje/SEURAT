package SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import edu.wpi.cs.jburge.SEURAT.editors.DisplayUtilities;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElementType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;
import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqType;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Requirement;
import edu.wpi.cs.jburge.SEURAT.views.PatternLibrary;
import edu.wpi.cs.jburge.SEURAT.views.RationaleExplorer;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

public class PatternEditor extends RationaleEditorBase {

	private Text nameField;
	
	private Text summaryArea;
	
	private Text problemArea;
	
	private Text contextArea;
	
	private Text solutionArea;
	
	private Text implementationArea;
	
	private Combo typeBox;	
	
	
	
	public static RationaleEditorInput createInput(PatternLibrary explorer, TreeParent tree,
			RationaleElement parent, RationaleElement target, boolean new1) {
		return new PatternEditor.Input(explorer, tree, parent, target, new1);
	}
	
	@Override
	public Class editorType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RationaleElement getRationaleElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveData() {
		// TODO Auto-generated method stub
		return false;
	}


	public void setupForm(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		if (isCreating())
		{
			getPattern().setType(PatternElementType.ARCHITECTURE);
		}
		/* - do we need to update our status first? probably not...
		 else
		 {
		 RequirementInferences inf = new RequirementInferences();
		 Vector newStat = inf.updateRequirement(getRequirement());
		 } */
		
		new Label(parent, SWT.NONE).setText("Name:");
		
		nameField =  new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getPattern().getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 50);
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		nameField.addModifyListener(getNeedsSaveListener());
		nameField.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Summary:");
		
		summaryArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		summaryArea.setText(getPattern().getDescription());
		summaryArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(summaryArea, gridData, 50, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = summaryArea.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		summaryArea.setLayoutData(gridData);
		
		new Label(parent, SWT.NONE).setText("Context:");
		
		summaryArea = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		summaryArea.setText(getPattern().getDescription());
		summaryArea.addModifyListener(getNeedsSaveListener());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(summaryArea, gridData, 50, 5);
		gridData.horizontalSpan = 5;
		gridData.heightHint = summaryArea.getLineHeight() * 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		summaryArea.setLayoutData(gridData);
		
//		new Label(parent, SWT.NONE).setText("Type:");
//		
//		
//		typeBox = new Combo(parent, SWT.NONE);
//		typeBox.addModifyListener(getNeedsSaveListener());
//		Enumeration typeEnum = ReqType.elements();
////		System.out.println("got enum");
//		int i = 0;
//		ReqType rtype;
//		while (typeEnum.hasMoreElements())
//		{
//			rtype = (ReqType) typeEnum.nextElement();
////			System.out.println("got next element");
//			typeBox.add( rtype.toString());
//			if (rtype.toString().compareTo(getPattern().getType().toString()) == 0)
//			{
////				System.out.println(getRequirement().getType().toString());
//				typeBox.select(i);
////				System.out.println(i);
//			}
//			i++;
//		}
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		typeBox.setLayoutData(gridData);
//		
//		new Label(parent, SWT.NONE).setText("Status:");
//		statusBox = new Combo(parent, SWT.NONE);
//		statusBox.addModifyListener(getNeedsSaveListener());
//		Enumeration statEnum = ReqStatus.elements();
//		int j=0;
//		ReqStatus stype;
//		while (statEnum.hasMoreElements())
//		{
//			stype = (ReqStatus) statEnum.nextElement();
//			statusBox.add( stype.toString() );
//			if (stype.toString().compareTo(getRequirement().getStatus().toString()) == 0)
//			{
////				System.out.println(getRequirement().getStatus().toString());
//				statusBox.select(j);
////				System.out.println(j);
//			}
//			j++;
//		}
//		
//		new Label(parent, SWT.NONE).setText("Artifact:");
//		
//		artifactField = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
//		artifactField.addModifyListener(getNeedsSaveListener());
//		if (getRequirement().getArtifact() != null)
//		{
//			artifactField.setText(getRequirement().getArtifact());
//		}
//		else
//		{
//			artifactField.setText("");
//		}
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalSpan = 1;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		artifactField.setLayoutData(gridData);
//		
//		
//		new Label(parent, SWT.NONE).setText("Arguments For");
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		
//		
//		new Label(parent, SWT.NONE).setText("Arguments Against");
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		
//		forModel = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
//		
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalSpan = 3;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.verticalAlignment = GridData.FILL;
//		int listHeight = forModel.getItemHeight() * 4;
//		Rectangle trim = forModel.computeTrim(0, 0, 0, listHeight);
//		gridData.heightHint = trim.height;
//		Vector listV = getRequirement().getArgumentsFor();
//		Enumeration listE = listV.elements();
//		while (listE.hasMoreElements())
//		{
//			Argument arg = new Argument();
//			arg.fromDatabase((String)listE.nextElement());
//			
//			forModel.add( arg.getName() );
//			
//			// Register Event Notification
//			try
//			{
//				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onForArgumentUpdate");
//			}
//			catch( Exception e )
//			{
//				System.out.println("Requirement Editor: For Argument Update Notification Not Available!");
//			}
//		}    
//		// add a list of arguments against to the right side
//		forModel.setLayoutData(gridData);
//		
//		
//		againstModel = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalSpan = 3;
//		listHeight = againstModel.getItemHeight() * 4;
//		Rectangle rtrim = againstModel.computeTrim(0, 0, 0, listHeight);
//		gridData.heightHint = rtrim.height;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.verticalAlignment = GridData.FILL;
//		
//		listV = getRequirement().getArgumentsAgainst();
//		listE = listV.elements();
//		while (listE.hasMoreElements())
//		{
//			Argument arg = new Argument();
//			arg.fromDatabase((String)listE.nextElement());
//			
//			againstModel.add( arg.getName() );
//			// Register Event Notification
//			try
//			{
//				RationaleDB.getHandle().Notifier().Subscribe(arg, this, "onAgainstArgumentUpdate");
//			}
//			catch( Exception e )
//			{
//				System.out.println("Requirement Editor: Against Argument Update Notification Not Available!");
//			}
//		}    
//		againstModel.setLayoutData(gridData);
//		
//		enableButton = new Button(parent, SWT.CHECK);
//		enableButton.setText("Enabled");
//		enableButton.addSelectionListener(getSelNeedsSaveListener());
//		enableButton.setSelection(getRequirement().getEnabled());
//		
//		gridData = new GridData();
//		gridData.horizontalSpan = 2;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		enableButton.setLayoutData(gridData);
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");
//		new Label(parent, SWT.NONE).setText(" ");	
//
//		// Register Event Notification For Changes To This Element
//		try
//		{
//			RationaleDB.getHandle().Notifier().Subscribe(getRequirement(), this, "onUpdate");
//		}
//		catch( Exception e )
//		{
//			System.out.println("Requirement Editor: Updated Not Available!");
//		}
		
		updateFormCache();

	}
	
	public Pattern getPattern() {
		return (Pattern)getEditorData().getAdapter(Pattern.class);
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
			super(patternLib, tree, parent, target, new1);
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
			return isCreating() ? "New Pattern Editor" :
				"Pattern: " + getData().getName();
		}

		/* (non-Javadoc)
		 * @see SEURAT.editors.RationaleEditorInput#targetType(java.lang.Class)
		 */
		@Override
		public boolean targetType(Class type) {
			return type == Pattern.class;
		}
	}

}
