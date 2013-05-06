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
