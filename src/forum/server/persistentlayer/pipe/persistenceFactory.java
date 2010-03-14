package forum.server.persistentlayer.pipe;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

public class persistenceFactory 
{
	private static persistenceDataHandler PERSISTENCE_DATA_HANDLER = null;

	/**
	 * Initializes the forum database (in case it hasn't initialized yet, and returns a pipe which
	 * handles the database operations)
	 */
	public persistenceDataHandler getPipe() 
	{
		if (PERSISTENCE_DATA_HANDLER == null) 
		{
			try
			{
				PERSISTENCE_DATA_HANDLER = new JAXBpersistenceDataHandler();
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
				"parsing the database file, the program will exit!!!");
				System.exit(-1);
			}
		}
		return PERSISTENCE_DATA_HANDLER;
	}
}
