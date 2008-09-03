package SEURAT.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	
	//public static final String P_PATH = "pathPreference";
	
	public static final String P_DATABASE = "databasePreference";
	
	public static final String P_DATABASEUSER = "databaseUserID";
	
	public static final String P_DATABASEPASSWORD = "databasePassword";
	
	public static final String P_DERBYNAME = "derbyDatabaseName";
	public static final String P_DERBYPATH = "derbyDatabasePath";
	public static final String P_DERBYPATHTYPE = "derbyDatabasePathType";
	
	public class DerbyPathType
	{
		public static final String WORKING_DIRECTORY = "working";
		public static final String ABSOLUTE_PATH = "absolute";
	}
	
	public static final String P_MYSQLLOCATION = "mySQLServerLocation";
	public static final String P_MYSQLADDRESS = "mySQLServerAddress";
	public static final String P_MYSQLPORT = "mySQLServerPort";
	
	public class MySQLLocationType
	{
		public static final String LOCAL = "local";
		public static final String REMOTE = "remote";
	}
	
	public static final String P_DATABASETYPE = "databaseType";
	
	public class DatabaseType
	{
		public static final String MYSQL = "mysql";
		public static final String DERBY = "derby";
	}
	
}
