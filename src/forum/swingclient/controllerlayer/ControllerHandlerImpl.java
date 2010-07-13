package forum.swingclient.controllerlayer;


import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;

import forum.swingclient.ui.events.*;
import forum.swingclient.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;
import forum.tcpcommunicationlayer.*;

/**
 * You need to delete all the code in here and implement it yourself.<br>
 * This code is just for you to understand how to work with the Observer/Observed and GUI.
 * 
 * @author Tomer Heber
 */
public class ControllerHandlerImpl extends ControllerHandler implements Observer {	
	private class ClientRequestData {
		private Component comp;
		private EventType type;

		public ClientRequestData(Component comp, EventType event) {
			this.comp = comp;
			this.type = event;
		}

		public Component getComponent() {
			return this.comp;
		}

		public EventType getEventType() {
			return this.type;
		}		
	}

	public final BlockingQueue<ServerResponse> responses;
	public final BlockingQueue<ClientMessage> messages;

	public HashMap<Long, ClientRequestData> sended;


	private final ExecutorService responsesHandlersPool = Executors.newCachedThreadPool();


	public void update(Observable observable, Object response) {
		try {
			if (response != null) {
				this.responses.put((ServerResponse)response);
			}
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
			this.messages = new LinkedBlockingDeque<ClientMessage>();
			this.sended = new HashMap<Long, ClientRequestData>();
			/* Start a connection to the server */
			this.connectionController = new ClientConnectionController("127.0.0.1",(short)1234);
			this.connectionController.addObserver(this);

			Thread tSendingThread = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							ClientMessage toSend = messages.take();
							System.out.println(toSend.getID() + "id sended");
							connectionController.handleQuery(toSend);

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			Thread tReceivingThread = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							ServerResponse tResponse = responses.take();
							if (tResponse != null) {
								long id = tResponse.getID();
								ClientRequestData tCRequest = sended.get(id);
								if (tCRequest == null) {
									SystemLogger.warning("got invalid response from the server");
									continue;
								}
								else {
									sended.remove(id);
									System.out.println("\n\nserver response = \n\n");
									System.out.println(tResponse.getResponse());
									if (tResponse.hasExecuted())
										notifyObservers(new ForumGUIRefreshEvent(tCRequest.getComponent(),
												tResponse.getResponse(), tCRequest.getEventType()));
									else
										notifyObservers(new ForumGUIErrorEvent(
												tResponse.getResponse(), tCRequest.getEventType()));
								}
							}		
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

			tSendingThread.start();
			tReceivingThread.start();


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

	public void modifyMessage(final long authorID, long messageID, String newTitle,
			String newContent, Component comp) {
		getActiveUsersNumber();
		final ClientMessage toSend = new ModifyMessageMessage(authorID, 
				messageID, newTitle, newContent);
		try {
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.MESSAGES_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}
	}

	public void modifyThread(final long authorID, long threadID, String newTopic, Component comp) {
		getActiveUsersNumber();
		final ClientMessage toSend = new ModifyThreadMessage(authorID, threadID, newTopic);
		try {
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.THREADS_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}		
	}

	public void modifySubject(final long authorID, long subjectID, String newName, String newDescription, Component comp) {
		getActiveUsersNumber();
		final ClientMessage toSend = new ModifySubjectMessage(authorID, subjectID, newName, newDescription);
		try {
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SUBJECTS_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}		
	}

	public void addReplyToMessage(final long author, final long replyTo, 
			final String title, final String content, final Component comp) {
		getActiveUsersNumber();
		final ClientMessage toSend = new AddReplyMessage(author, replyTo, title, content);
		try {
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.MESSAGES_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}
	}

	public void deleteSubject(long userID, long fatherID, long subjectID, Component comp) {
		getActiveUsersNumber();
		final ClientMessage toSend = new DeleteSubjectMessage(userID, fatherID, subjectID);
		try {
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SUBJECTS_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}		
	}

	public void deleteMessage(long userID, long fatherID, long messageID, Component comp) {
		getActiveUsersNumber();
		final ClientMessage toSend = new DeleteMessageMessage(userID, fatherID, messageID);
		try {
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.MESSAGES_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
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
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new AddNewGuestMessage();
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
					/*
					ServerResponse tResponse = responses.take();
					if (tResponse == null)
						notifyObservers(new ForumGUIErrorEvent("Can't register as a guest", EventType.USER_CHANGED));
					else {
						notifyObservers(new ForumGUIRefreshEvent(comp, tResponse.getResponse(), EventType.USER_CHANGED));
					}*/
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}};
			this.responsesHandlersPool.execute(tResponseHandler);
			return true;		
	}

	public boolean logout(final Component comp, final String username) {
		/*					if (tResponse == null) {
						notifyObservers(new ForumGUIErrorEvent("Can't logout!", EventType.USER_CHANGED));
					}
		 */

		Runnable tResponseHandler = new Runnable() {
			public void run() {
				final ClientMessage toSend = new LogoffMessage(username);
				try {

					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
					getActiveUsersNumber();
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
		return true;		
	}

	public void recoverPassword(final String username, final String email, final String password, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new ChangeProfileDetailsMessage(username, password, null, null, email, true);
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);		
	}

	public void changePassword(final long memberID, final String prevPassword, final String newPassword, 
			final boolean shouldAskNewPassword, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new PasswordChangeMessage(memberID, prevPassword, newPassword, shouldAskNewPassword);
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);		
	}



	public boolean login(final long guestID, final String username, final String password, final Component comp) {
		/*		if (tResponse == null || !tResponse.hasExecuted())
			notifyObservers(new ForumGUIErrorEvent("Can't login" + 
					(tResponse != null? (": " + tResponse.getResponse()) : "!"),
					EventType.USER_CHANGED));
		 */
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new LoginMessage(guestID, username, password);
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
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

	public void getSubjects(final long fatherID, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new ViewSubjectsMessage(fatherID);
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SUBJECTS_UPDATED));
					//					notifyObservers(new ForumGUIErrorEvent("Error returned while trying to retrieve subjects",
					//						EventType.SUBJECTS_UPDATED));

					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}	

	public void addNewSubject(final long userID, final long fatherID, final String name, final String description, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new AddNewSubjectMessage(userID, fatherID, name, description);
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SUBJECTS_UPDATED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}	



	public void getThreads(final long subjectID, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				final ClientMessage toSend = new ViewThreadsMessage(subjectID);
				getActiveUsersNumber();
				try {
					//						notifyObservers(new ForumGUIErrorEvent("Error returned while trying to retrieve threads",
					//							EventType.THREADS_UPDATED));


					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.THREADS_UPDATED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}


	public void addNewThread (final long userID, final long subjectID, final String topic, final String title,
			final String content, final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new AddNewThreadMessage(userID, subjectID, topic, title, content);
				try {
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.THREADS_UPDATED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}


	public void getNestedMessages(final long rootID, boolean shouldUpdateViews, final Component comp) {
		System.out.println(rootID + " nested req");
		try {
			final ClientMessage toSend = new ViewMessageAndRepliesMessage(rootID, shouldUpdateViews);
			getActiveUsersNumber();

			//						notifyObservers(new ForumGUIErrorEvent("Error returned while trying to retrieve threads",
			//							EventType.MESSAGES_UPDATED));
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.MESSAGES_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}
	}

	public void registerToForum(final Component comp, final String username, final String password, final String email,
			final String firstName, final String lastName) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new RegisterMessage(username, password, lastName, firstName,
						email);
				try {
					//							notifyObservers(new ForumGUIErrorEvent("registererror\tError while connecting to the server!",
					//								EventType.USER_CHANGED));
					//					.println("controller register error");

					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);	
	}


	public void searchByAuthor(final Component comp, final String username) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new SearchByAuthorMessage(username);
				try {


					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SEARCH_UPDATED));

					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);	
	}

	public void searchByContent(final Component comp, final String phrase) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new SearchByContentMessage(phrase);
				try {
					//							notifyObservers(new ForumGUIErrorEvent("registererror\tError while connecting to the server!",
					//								EventType.USER_CHANGED));
					//					"".println("controller register error");

					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SEARCH_UPDATED));

					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);	
	}

	public void getActiveUsersNumber() {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				final ClientMessage toSend = new GuestsAndMembersNumberMessage();
				try {

					sended.put(toSend.getID(), new ClientRequestData(null, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}

	public void getAllMembers(final Component comp) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new ViewAllMembersMessage();
				try {							
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}

	public void promoteToModerator(final Component comp, final String username) {
		Runnable tResponseHandler = new Runnable() {
			public void run() {
				getActiveUsersNumber();
				final ClientMessage toSend = new PromoteToModeratorMessage(0, username);
				try {							
					sended.put(toSend.getID(), new ClientRequestData(comp, EventType.USER_CHANGED));
					synchronized (messages) {
						messages.put(toSend);
					}
				}
				catch (InterruptedException e) {
					SystemLogger.warning("The program was interrupted while waiting");
				}
			}
		};
		this.responsesHandlersPool.execute(tResponseHandler);
	}		

	public void getPath(Component comp, long prevFatherMessageID, long messageID) {
		try {
			final ClientMessage toSend = new GetPathMessage(messageID, prevFatherMessageID);
			getActiveUsersNumber();
			sended.put(toSend.getID(), new ClientRequestData(comp, EventType.SEARCH_UPDATED));
			synchronized (messages) {
				messages.put(toSend);
			}
		}
		catch (InterruptedException e) {
			SystemLogger.warning("The program was interrupted while waiting");
		}
	}
}