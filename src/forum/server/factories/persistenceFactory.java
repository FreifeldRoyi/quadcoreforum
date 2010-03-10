package forum.server.factories;

import forum.server.domainlayer.additional.persistenceDataHandlerImpl;
import forum.server.domainlayer.interfaces.persistenceDataHandler;

public class persistenceFactory {
	private static persistenceDataHandler PERSISTENCE_DATA_HANDLER = null;
	
	public persistenceDataHandler getPipe() {
		if (PERSISTENCE_DATA_HANDLER == null)
			PERSISTENCE_DATA_HANDLER = new persistenceDataHandlerImpl();
		return PERSISTENCE_DATA_HANDLER;
	}
	
	
}
