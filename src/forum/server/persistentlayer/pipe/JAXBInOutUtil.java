package forum.server.persistentlayer.pipe;

import forum.server.persistentlayer.*;

public interface JAXBInOutUtil {
	public ForumType unmarshalDatabase() throws DatabaseRetrievalException;
	public void marshalDatabase(ForumType forum) throws DatabaseUpdateException;
}
