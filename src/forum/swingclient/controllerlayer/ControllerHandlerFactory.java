package forum.swingclient.controllerlayer;

import java.io.IOException;


/**
 * @author Tomer Heber
 *
 */
public class ControllerHandlerFactory {

	private static ControllerHandler PIPE;

	/**
	 * 
	 * @return An implementation of the ControllerHandler pipe.
	 */
	public static ControllerHandler getPipe() throws IOException {
		if (PIPE == null) {
			try {
				PIPE = new ControllerHandlerImpl();
			}
			catch (IOException e) {
				throw new IOException("Can't Initialize a connection with the forum server. Sorry!!!");
			}
		}
		return PIPE;
	}
}
