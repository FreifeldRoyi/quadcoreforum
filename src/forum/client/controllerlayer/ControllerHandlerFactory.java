package forum.client.controllerlayer;

import java.io.IOException;


/**
 * @author Tomer Heber
 *
 */
public class ControllerHandlerFactory {
	
	/**
	 * 
	 * @return An implementation of the ControllerHandler pipe.
	 */
	public static ControllerHandler getPipe() throws IOException {
		try {
			return new ControllerHandlerImpl();
		}
		catch (IOException e) {
			throw new IOException("Can't Initialize a connection with the forum server. Sorry!!!");
		}
	}

}
