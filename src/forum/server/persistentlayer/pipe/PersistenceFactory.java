/**
 * 
 */
package forum.server.persistentlayer.pipe;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;


/**
 * @author sepetnit
 *
 */


public class PersistenceFactory 
{
	private static PersistenceDataHandler PERSISTENCE_DATA_HANDLER = null;

	/**
	 * Initializes the forum database (in case it hasn't initialized yet, and returns a pipe which
	 * handles the database operations)
	 */
	public static PersistenceDataHandler getPipe() 
	{
		if (PERSISTENCE_DATA_HANDLER == null) 
		{
			try
			{
				PERSISTENCE_DATA_HANDLER = JAXBpersistenceDataHandler.getInstance();
			} 
			catch (JAXBException e) 
			{
				System.out.println("An error was encountered while" +
						"connecting to the database, the program will exit!!!");
				System.exit(-1);
			}
			catch (SAXException e) 
			{
				System.out.println("An error was encountered while" +
				" parsing the database file, the program will exit!!!");
				System.exit(-1);
			}
		}
		return PERSISTENCE_DATA_HANDLER;
	}
}

