package forum.server;

import java.io.*;

/**
 * @author Vitali Sepetnitsky
 *
 */

/**
 * This class contains static fields which define the general settings of the forum
 * application, like the database file name, logger name etc.
 */
public class Settings {

	public static final String LOG_FILE_NAME = "forumQuadCore.log";

	public static String DB_FILES_LOCATION = 
		"src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");
	public static String DB_FILE_NAME = "QuadCoreForumDB";

	public static String SCHEMA_FILE_FULL_LOCATION = DB_FILES_LOCATION + DB_FILE_NAME + ".xsd";
	public static String DB_FILE_FULL_LOCATION 	= DB_FILES_LOCATION + DB_FILE_NAME + ".xml";

	public static final String DB_INITIAL_FILE = "src" + System.getProperty("file.separator") +
	"testing" + System.getProperty("file.separator") + "InitialDB.xml";

	/**
	 * This methods creates a new empty database file in order to allow the test methods to modify
	 * the database without changing it
	 *  
	 * @throws IOException
	 * 		In case an error occurred while trying writing to the database
	 */
	public static void switchToTestMode() throws IOException {
		Settings.DB_FILES_LOCATION = "src" + System.getProperty("file.separator") +
		"testing" + System.getProperty("file.separator");
		Settings.SCHEMA_FILE_FULL_LOCATION = Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xsd";
		Settings.DB_FILE_FULL_LOCATION 	= Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xml";

		BufferedReader tInitialDBReader = new BufferedReader(new FileReader(new File(Settings.DB_INITIAL_FILE)));			
		BufferedWriter tOutputWriter = new BufferedWriter(new FileWriter(new File(Settings.DB_FILE_FULL_LOCATION)));

		String tCurrentLine = "";
		while ((tCurrentLine = tInitialDBReader.readLine()) != null)
			tOutputWriter.write(tCurrentLine + "\n");
		tOutputWriter.close();
		tInitialDBReader.close();
	}

	/**
	 * This methods switches back to a regular running mode, after a test database was created and used
	 * in order to test the forum operations
	 */
	public static void switchToRegularMode() {
		File tDatabaseTemporaryFile = new File(Settings.DB_FILE_FULL_LOCATION);

		// delete a previously created test database
		if (tDatabaseTemporaryFile.exists())
			tDatabaseTemporaryFile.delete();

		Settings.DB_FILES_LOCATION = "src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");

		Settings.SCHEMA_FILE_FULL_LOCATION = Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xsd";
		Settings.DB_FILE_FULL_LOCATION 	= Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xml";
	}
}