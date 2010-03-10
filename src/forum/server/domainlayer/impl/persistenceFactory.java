package forum.server.domainlayer.impl;

import forum.server.domainlayer.interfaces.persistenceDataHandler;

public class persistenceFactory {
	private static persistenceDataHandler PERSISTENCE_DATA_HANDLER = null;
	
	public persistenceDataHandler getPipe() {
		if (PERSISTENCE_DATA_HANDLER == null)
			PERSISTENCE_DATA_HANDLER = new JAXBpersistenceDataHandler();
		return PERSISTENCE_DATA_HANDLER;
	}
}
