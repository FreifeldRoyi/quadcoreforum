package forum.client.controllerlayer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;

import forum.client.ui.events.*;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;
import forum.tcpcommunicationlayer.AddNewGuestMessage;
import forum.tcpcommunicationlayer.ClientMessage;
import forum.tcpcommunicationlayer.ServerResponse;
import forum.tcpcommunicationlayer.ViewMessageAndRepliesMessage;
import forum.tcpcommunicationlayer.ViewSubjectContentMessage;
import forum.tcpcommunicationlayer.ViewSubjectsMessage;
import forum.tcpcommunicationlayer.ViewThreadsMessage;

/**
 * You need to delete all the code in here and implement it yourself.<br>
 * This code is just for you to understand how to work with the Observer/Observed and GUI.
 * 
 * @author Tomer Heber
 */
public class ControllerHandlerImpl extends ControllerHandler implements Observer {	

	public final BlockingQueue<ServerResponse> responses;
	private final ExecutorService responsesHandlersPool = Executors.newCachedThreadPool();


	public void update(Observable observable, Object response) {
		try {
			if (response != null)
				this.responses.put((ServerResponse)response);
		}
		catch (InterruptedException e) {
			SystemLogger.severe("Interrupted while receiving a response from the server.");
		}
	}

	public ControllerHandlerImpl() throws IOException {

		/* delete old log files. */
		for (int i = 0; i < 1000; i++) {
			File tNewFile = new File("clientLog" + i + ".log");
			if (tNewFile.exists())
				tNewFile.delete();
			tNewFile = new File("clientLog" + i + ".log.lck");
			if (tNewFile.exists())
				tNewFile.delete();
		}

		String logFileName = "clientLog" + (int)(Math.random() * 1000.0) + ".log";
		try {
			/* Create a logger for the client (to a file...). */
			FileHandler handler = new FileHandler(logFileName);
			///////////////////////////////////////////////////
			SystemLogger.addFileHandler(handler);
			//////////////////////////////////////////////////
		}
		catch (SecurityException e) {
			System.out.println("Can't initialize logger beacause of a permission problem" +
					", the program will continue without logging the client events, it is" +
			" strongly recommended to restart the program.");
		}
		catch (IOException e) {		
			System.out.println("Can't initialize logger beacause of a I/O problem" +
					", the program will continue without logging the client events, it is" +
			" strongly recommended to restart the program.");
		}
		try {
			this.responses = new LinkedBlockingDeque<ServerResponse>();
			/* Start a connection to the server */
			this.connectionController = new ClientConnectionController("127.0.0.1",(short)1234);
			this.connectionController.addObserver(this);
		}
		catch (IOException ioException) {
			SystemLogger.severe("An IOException was thrown while trying to connect to the server.");
			throw ioException;
		}			
	}

	/* (non-Javadoc)
	 * @see forumtree.contol.ControllerHandler#getForumView()
	 */
	@Override
	public String getForumView() {
		return "";
	}

	@Override
	public void modifyMessage(long id, String newContent, Component comp) {		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//	notifyObservers(new ForumGUIRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			//notifyObservers(new ForumGUIErrorEvent("Failed to modify a message"));
		}
	}

	@Override
	public void addReplyToMessage(final long id, final String string, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				try {
					ServerResponse tResponse = responses.take();
					setChanged();
					if (tResponse == null)
						notifyObservers(null);
					else {
						//						notifyObservers(new ForumGUIRefreshEvent(comp,getForumView()));
						if (Math.random() > 0.5) {	
							//			notifyObservers(new ForumGUIErrorEvent("Failed to reply to a message"));
						}
					}
				} 
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}

		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}

	@Override
	public void deleteMessage(long id, Component comp) {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//		notifyObservers(new ForumGUIRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			//notifyObservers(new ForumGUIErrorEvent("Failed to delete message"));
		}
	}

	@Override
	public void addNewMessage(Component comp) {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//		notifyObservers(new ForumGUIRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			//			notifyObservers(new ForumGUIErrorEvent("Failed to add a new message"));
		}
	}

	public boolean registerAsNewGuest(final Component comp) {
		final ClientMessage toSend = new AddNewGuestMessage();
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				try {
					connectionController.handleQuery(toSend);
					ServerResponse tResponse = responses.take();
					if (tResponse == null)
						notifyObservers(new ForumGUIErrorEvent("Can't register as a guest", EventType.USER_CHANGED));
					else {
						notifyObservers(new ForumGUIRefreshEvent(comp, tResponse.getResponse(), EventType.USER_CHANGED));
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
		return true;		
	}

	public void getSubjects(long fatherID, final Component comp) {
		final ClientMessage toSend = new ViewSubjectsMessage(fatherID);
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				try {
					connectionController.handleQuery(toSend);
					ServerResponse tResponse = responses.take();
					if (tResponse == null)
						notifyObservers(new ForumGUIErrorEvent("Error returned while trying to retrieve subjects",
								EventType.SUBJECTS_UPDATED));
					else {
						notifyObservers(new ForumGUIRefreshEvent(comp, tResponse.getResponse(),
								EventType.SUBJECTS_UPDATED));
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}	
	
	public void getThreads(long subjectID, final Component comp) {
		final ClientMessage toSend = new ViewThreadsMessage(subjectID);
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				try {
					connectionController.handleQuery(toSend);
					ServerResponse tResponse = responses.take();
					if (tResponse == null)
						notifyObservers(new ForumGUIErrorEvent("Error returned while trying to retrieve threads",
								EventType.THREADS_UPDATED));
					else {
						notifyObservers(new ForumGUIRefreshEvent(comp, tResponse.getResponse(),
								EventType.THREADS_UPDATED));
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}
	
	public void getNestedMessages(long rootID, final Component comp) {
		final ClientMessage toSend = new ViewMessageAndRepliesMessage(rootID);
		System.out.println("ddddddddddddddddddddddddddddddddddddddd");
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				try {
					connectionController.handleQuery(toSend);
					ServerResponse tResponse = responses.take();
					if (tResponse == null)
						notifyObservers(new ForumGUIErrorEvent("Error returned while trying to retrieve threads",
								EventType.MESSAGES_UPDATED));
					else {
						System.out.println(tResponse.getResponse());
					//	notifyObservers(new ForumGUIRefreshEvent(comp, tResponse.getResponse(),
					//			EventType.MESSAGES_UPDATED));
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);		
	}

	
	private boolean removeMeAsGuest() throws IOException, ClassNotFoundException {
		/*out.writeObject(new RemoveGuestMessage(this.me));
		/* receive response from the server. 
		Object o1 = in.readObject();
		if (o1 == null) {
			SystemLogger.severe("Lost connection to server.");
			return false;
		}
		if (!(o1 instanceof ServerResponse)) {
			SystemLogger.severe("Received an invalid response from server.");
			return false;
		}*/
		return true;
	}	

}
