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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = SEURATPlugin.getDefault()
		.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_DATABASE, "SEURAT_base");
		store.setDefault(PreferenceConstants.P_DATABASEUSER, "root");
		store.setDefault(PreferenceConstants.P_DATABASEPASSWORD, "");
		store.setDefault(PreferenceConstants.P_DERBYNAME, "SEURAT_base");
		store.setDefault(PreferenceConstants.P_DERBYPATHTYPE, PreferenceConstants.DerbyPathType.WORKING_DIRECTORY);
		store.setDefault(PreferenceConstants.P_DERBYPATH, "./");		
		store.setDefault(PreferenceConstants.P_DATABASETYPE, PreferenceConstants.DatabaseType.DERBY);
		store.setDefault(PreferenceConstants.P_MYSQLLOCATION, PreferenceConstants.MySQLLocationType.LOCAL);
		store.setDefault(PreferenceConstants.P_MYSQLADDRESS, "");
		store.setDefault(PreferenceConstants.P_MYSQLPORT, "");
	}
	
}
