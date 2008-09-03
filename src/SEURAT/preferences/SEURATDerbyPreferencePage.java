package SEURAT.preferences;


import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;

public class SEURATDerbyPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	/**
	 * This constructors sets up the SEURAT Derby Preferences Page
	 * so that the field editors can be initialized and automatically
	 * handle changing SEURAT's Derby Database Settings.
	 */
	public SEURATDerbyPreferencePage()
	{
		super(GRID);
		setPreferenceStore(SEURATPlugin.getDefault().getPreferenceStore());
		setDescription("SEURAT Derby Database Setup Preferences");		
	}
	
	private DirectoryFieldEditor pathEditor;
	private RadioGroupFieldEditor pathTypeEditor;
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField( new StringFieldEditor(
				PreferenceConstants.P_DERBYNAME, "&Database name:",  getFieldEditorParent()
		));
		String l_radioData[][] = new String[][]
		{ 
			new String[]{"Use Working Directory", "working"}, 
			new String[]{"Use Path In Filesystem", "absolute"} 
		};

		pathEditor = new DirectoryFieldEditor(PreferenceConstants.P_DERBYPATH, 
				"&Database Location:", getFieldEditorParent());

		pathTypeEditor = new RadioGroupFieldEditor(
			PreferenceConstants.P_DERBYPATHTYPE, 
			"Database Location Configuration",
			2,
			l_radioData,
			getFieldEditorParent()
		);
		
		addField(pathEditor);
		addField(pathTypeEditor);
	}
	
	public void init(IWorkbench workbench) {
	}
}
