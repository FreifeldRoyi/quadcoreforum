package forum.client.controllerlayer;


/**
 * @author Tomer Heber
 *
 */
public class ControllerHandlerFactory {
	
	/**
	 * 
	 * @return An implementation of the ControllerHandler pipe.
	 */
	public static ControllerHandler getPipe() {
		// return null;
		//TODO create an implementation of the ControllerHander and return it.
		return new ControllerHandlerImpl();
	}

}
