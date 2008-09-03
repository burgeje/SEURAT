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
