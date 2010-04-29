package forum.client.controllerlayer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.FileHandler;

import forum.server.domainlayer.SystemLogger;
import forum.tcpcommunicationlayer.*;

/**
 * This class handles the communication between the client and the server.
 * 
 * @author Tomer Heber
 */
public class CLIForum extends Thread {

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private long me;

	private long loggedID;
	private String loggedUsername;
	private String loggedFullName;

	public CLIForum(String addr, short port) throws IOException {
		InetAddress tInetAddress = InetAddress.getByName(addr);
		connect(tInetAddress, port);
	}
	
	private boolean registerAsNewGuest() throws IOException, ClassNotFoundException {
		out.writeObject(new AddNewGuestMessage());
		/* receive response from the server. */
		Object o = in.readObject();
		if (o == null) {
			SystemLogger.severe("Lost connection to server.");
			return false;
		}
		if (!(o instanceof ServerResponse)) {
			SystemLogger.severe("Received an invalid response from server.");
			return false;
		}

		ServerResponse res = (ServerResponse)o;
		this.me = Long.parseLong(res.getResponse());
		return true;
	}

	private boolean removeMeAsGuest() throws IOException, ClassNotFoundException {
		out.writeObject(new RemoveGuestMessage(this.me));
		/* receive response from the server. */
		Object o1 = in.readObject();
		if (o1 == null) {
			SystemLogger.severe("Lost connection to server.");
			return false;
		}
		if (!(o1 instanceof ServerResponse)) {
			SystemLogger.severe("Received an invalid response from server.");
			return false;
		}
		return true;
	}	
	
	public void run() {
		BufferedReader prompt = new BufferedReader(new InputStreamReader(System.in));
		this.loggedID = -1;
		this.loggedUsername = null;
		this.loggedFullName = null;

		try {
			printHelp();

			this.registerAsNewGuest();

			while (true) {

				if (this.me != -1) 
					System.out.println("Hello guest! Your id is " + me + ".");
				else
					System.out.println("Hello " + this.loggedFullName + "!" +
							" Your id is " + this.loggedID + ".");
				/* Receives a command from the user. */
				System.out.print("> ");
				String str = prompt.readLine();

				if (str == null) {
					break;
				}

				if (str.equals("disconnect")) {
					if (me != -1) {
					
						
						if (!this.removeMeAsGuest()) break;
						
					}
					else {
						out.writeObject(new LogoffMessage(this.loggedUsername));
						/* receive response from the server. */
						Object tLogoffResponse = in.readObject();
						if (tLogoffResponse == null) {
							SystemLogger.severe("Lost connection to server.");
							break;
						}
						if (!(tLogoffResponse instanceof ServerResponse)) {
							SystemLogger.severe("Received an invalid response from server.");
							break;
						}

						ServerResponse tLogoffServerResponse = (ServerResponse)tLogoffResponse;
						/* Check if the server has done the command. */
						if (tLogoffServerResponse.hasExecuted()) {
							System.out.println("done!");
						}
						else {
							System.out.println("failed!");
						}
					}
					break;
				}

				if (str.equals("help")) {
					printHelp();
					continue;
				}

				ClientMessage msg;
				try {
					/* Handles the command. */
					msg = handleCommand(str);
				} catch (BadCommandException e) {
					SystemLogger.info("The user has inputed an invalid command.");
					System.out.println();
					continue;
				}
				/* send the message to the server. */
				out.writeObject(msg);
				/* receive response from the server. */
				Object o = in.readObject();
				if (o == null) {
					SystemLogger.severe("Lost connection to server.");
					break;
				}
				if (!(o instanceof ServerResponse)) {
					SystemLogger.severe("Received an invalid response from server.");
					break;
				}

				ServerResponse res = (ServerResponse)o;
				/* Check if the server has done the command. */
				if (res.hasExecuted()) {
					System.out.println("done!");
				}
				else {
					System.out.println("failed!");
				}
				/* Print the response from the server */

				if (res.getResponse() != null && res.getResponse().startsWith("Welcome")) {
					this.removeMeAsGuest();
					this.me = -1;
					String[] tSplittedRes = res.getResponse().split("\t");
					this.loggedID = Long.parseLong(tSplittedRes[1]);
					this.loggedUsername = tSplittedRes[2];
					this.loggedFullName = tSplittedRes[3];
				}
				else if (res.getResponse() != null && res.getResponse().equals("The user is logged out.")) {
					this.loggedID = -1;
					this.loggedFullName = null;
					this.loggedUsername = null;
					this.registerAsNewGuest();
					System.out.println(res.getResponse());
				}
				else
					System.out.println(res.getResponse());
			}
		}
		catch (ClassNotFoundException e) {
			SystemLogger.severe("Received an invalid object from the server.");
			e.printStackTrace();
		}
		catch (IOException e) {
			SystemLogger.severe("IOException occured while trying to read/send/write.");
			e.printStackTrace();			
		}
		finally {			
			try {
				/* Closing all the streams. */
				prompt.close();
				closeConnection();
			} catch (IOException e) {
				SystemLogger.severe("IOException while trying to close streams.");
				e.printStackTrace();
			}			
		}
	}

	private void printHelp() {
		System.out.println(
				"help menu:" + "\n" +
				"\t" + "User operations:" + "\n" +
				"\t" + "\t" + "- help " +  "\n" +
				"\t" + "\t" + "- login <username> <password>" + "\n" +
				"\t" + "\t" + "- logoff" + "\n" +
				"\t" + "\t" + "- register <username> <password> <lastname> <firstname> <e-mail>" + "\n" +
				"\t" + "\t" + "- make moderator <username>" +
				"\n" + "\t" + "View content operations:" + "\n" +
				"\t" + "\t" + "- view_subjects <root subject id> (-1 to view the root subjects of the forum)" + "\n" +
				"\t" + "\t" + "- view_subject_content <subject id>" + "\n" +
				"\t" + "\t" + "- view_message_and_replies <message_id>" + "\n" +
				"\t" + "\t" + "- search_by_author <author_user_name>" + "\n" +
				"\t" + "\t" + "- search_by_content <phrase_to_search>" + "\n" +	
				"\n" + "\t" + "Add/Update/Delete operations:" + "\n" +
				"\t" + "\t" + "- add_new_subject <parent subject id> <new subject name> <new subject description>" + "\n" +
				"\t" + "\t" + "- open_new_thread <parent subject id> <thread topic> <message title> <message content>" + "\n" +
				"\t" + "\t" + "- add_reply <message id to reply to> <message title> <message content>" + "\n" +
				"\t" + "\t" + "- modify_message <message id to modify> <new message title> <new message content>" + "\n" +
				"\n" +				
				"\t" + "- disconnect" + "\n" +
				"//TODO add more operations (Admin, Moderator, Search)"	+ "\n"			
		);								
	}

	/**
	 * This method receives commands and creates the proper message to send to the server.
	 * 
	 * @param str The command to do.
	 * @return The message to send back to the server.
	 * @throws BadCommandException This exception is thrown is the command given by the user is invalid.
	 */
	private ClientMessage handleCommand(String str) throws BadCommandException {		
		try {
			final long tUserID = this.me != -1 ? this.me : this.loggedID;

			StringTokenizer tStringTokenizer = new StringTokenizer(str);
			String command = tStringTokenizer.nextToken();
			/** adding a new thread with a new first message **/ 
			if (command.equals("login"))
				return new LoginMessage(this.loggedID, tStringTokenizer.nextToken(), tStringTokenizer.nextToken());
			if (command.equals("logoff")) {
				String tUserName = this.loggedUsername != null? this.loggedUsername : "";
				return new LogoffMessage(tUserName);
			}
			if (command.equals("register"))
				return new RegisterMessage(tStringTokenizer.nextToken(), tStringTokenizer.nextToken(),
						tStringTokenizer.nextToken(), tStringTokenizer.nextToken(), tStringTokenizer.nextToken());
			if (command.equals("register"))
				return new RegisterMessage(tStringTokenizer.nextToken(), tStringTokenizer.nextToken(),
						tStringTokenizer.nextToken(), tStringTokenizer.nextToken(), tStringTokenizer.nextToken());
			if (command.equals("view_subjects"))
				return new ViewSubjectsMessage(Long.parseLong(tStringTokenizer.nextToken()));
			if (command.equals("view_subject_content"))
				return new ViewSubjectContentMessage(Long.parseLong(tStringTokenizer.nextToken()));
			if (command.equals("view_message_and_replies"))
				return new ViewSubjectContentMessage(Long.parseLong(tStringTokenizer.nextToken()));
			if (command.equals("add_new_subject"))
				return new AddNewSubjectMessage(tUserID, Long.parseLong(tStringTokenizer.nextToken()), 
						tStringTokenizer.nextToken(), tStringTokenizer.nextToken());
			if (command.equals("open_new_thread"))
				return new AddNewThreadMessage(tUserID,
						Long.parseLong(tStringTokenizer.nextToken()), tStringTokenizer.nextToken(),
						tStringTokenizer.nextToken(), tStringTokenizer.nextToken());
			if (command.equals("add_reply"))
				return new AddReplyMessage(tUserID,
						Long.parseLong(tStringTokenizer.nextToken()), tStringTokenizer.nextToken(),
						tStringTokenizer.nextToken());
			if (command.equals("modify_message"))
				return new AddReplyMessage(tUserID,
						Long.parseLong(tStringTokenizer.nextToken()), tStringTokenizer.nextToken(),
						tStringTokenizer.nextToken());
			if (command.equals("make moderator"))
				return new PromoteToModeratorMessage(tUserID, tStringTokenizer.nextToken());
			if (command.equals("search_by_author"))
				return new SearchByAuthorMessage(tStringTokenizer.nextToken());
			if (command.equals("search_by_content")) {
				String tPhraseToSearch = "";
				while (tStringTokenizer.hasMoreTokens())
					tPhraseToSearch += tStringTokenizer.nextToken() + "";
				tPhraseToSearch = tPhraseToSearch.substring(0, tPhraseToSearch.length() - 1);
				return new SearchByContentMessage(tPhraseToSearch);
			}
		}
		catch(Exception e) {
			throw new BadCommandException("The command " + str + " is invalid.");
		}

		throw new BadCommandException("The command " + str + " is unknown.");		
	}	

	private void connect(InetAddress addr, short port) throws IOException {
		SocketAddress sa = new InetSocketAddress(addr,port);
		/* Connect to the server */
		this.socket = new Socket();
		this.socket.connect(sa);
		out = new ObjectOutputStream(this.socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(this.socket.getInputStream());
		SystemLogger.info("The client is connected to the server.");
	}

	private void closeConnection() throws IOException {
		in.close();
		out.close();
		this.socket.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			File tNewFile = new File("clientLog"+i+".log");
			if (tNewFile.exists())
				tNewFile.delete();
		}
		
		String logFileName = "clientLog"+(int)(Math.random()*1000.0)+".log";
		if (args.length > 0) {
			logFileName = args[0];
		}
		try {
			/* Create a logger for the client (to a file...). */
			FileHandler handler = new FileHandler(logFileName);
			///////////////////////////////////////////////////
			SystemLogger.addFileHandler(handler);
			//////////////////////////////////////////////////
		} catch (SecurityException e) {		
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		}

		try {
			/* Start the client */
			Thread thread = new CLIForum("127.0.0.1",(short)1234);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		} catch (IOException e) {
			SystemLogger.severe("An IOException was thrown while trying to connect to the server.");
		}			
		SystemLogger.info("Exiting...");
	}
}
