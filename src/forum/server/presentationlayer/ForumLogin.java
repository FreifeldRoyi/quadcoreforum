/**
 * 
 */
package forum.server.presentationlayer;

import java.io.IOException;

import forum.server.exceptions.user.*;


/**
 * @author sepetnit
 *
 */
public class ForumLogin {
	//private Forum forum;
	//	private ForumPromt prevMenu;

	private static String ESCAPE_SEQUENCE = "esc";
	private static String SUCCESS_MESSAGE = "success!";

	private enum LoggedUserOperations { 
		LOGGED_VIEW, LOGGED_ADD_NEW, LOGGED_LOGOFF, LOGGED_EXIT, LOGGED_ERROR, LOGGED_HELP; 

		/** 
		 * This method is used instead the standard ordinal() method, used with enums. 
		 * It allows to get the enum order starting from 1 in order to display these numbers 
		 * on the forum menu. 
		 *  
		 * @return 
		 * 		ordinal() + 1 
		 */ 
		public int ordinalPlus1() { 
			return this.ordinal() + 1; 
		} 
	} 

	/** 
	 * Prints a guide which shows the user all the available operations he can choose to perform 
	 */ 
	public void printStartScreenHelp() { 
		System.out.println("Please choose one of the following operations:\n"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_VIEW.ordinalPlus1()		+ ": view forum messages as a logged-in user"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_ADD_NEW.ordinalPlus1() 	+ ": add a new subject"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_LOGOFF.ordinalPlus1()    	+ ": logoff the forum\n"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_EXIT.ordinalPlus1()       + ": exit the program\n"); 
		System.out.println("The system waits for your choose ..."); 
	}

	/** 
	 * Gets the user choice from the available operation list, parses it and returns the suitable enum 
	 * element or an element which indicates that an error occurred. 
	 *  
	 * @return 
	 *              The desired operation typed by user as an enum element 
	 */ 
	public LoggedUserOperations getStartScreenOperation() { 
		String tReadText = ""; 
		try { 
			tReadText = ForumPromt.USER_CHOICE_SCANNER.readLine();
			for (LoggedUserOperations val : LoggedUserOperations.values())
				if (tReadText.equals(val.ordinalPlus1() + ""))
					return val;
				else if (tReadText.equals("?"))
					return LoggedUserOperations.LOGGED_HELP;
			return LoggedUserOperations.LOGGED_ERROR;
		} 
		catch (Exception tIOException) { 
			System.out.println("An error has occured for some reason, can't read" + 
			"the required operation from the keyboard, please restart the program"); 
			System.exit(-1); 
			return LoggedUserOperations.LOGGED_ERROR; // just to calm the compiler (this code is unreachable) 
		} 
	} 

	/** 
	 * Redirects the chosen start operation to the promt methods which are responsible to 
	 * handle this type of operation 
	 *  
	 * @param userChose 
	 *              The operation chosen by user (represented as an enum element)  
	 * @return 
	 *              true if a valid operation has chosen and false otherwise 
	 * @throws NotConnectedException 
	 */ 
	public boolean redirectOperations(LoggedUserOperations userChose) throws NotConnectedException { 
		switch (userChose) { 
		case LOGGED_ADD_NEW:
			new ForumFunctions().addNewSubject(-1);
			return true; 
		case LOGGED_VIEW: 
			new ForumFunctions().view(-1);
			return true; 
		case LOGGED_LOGOFF: 
			String tCurrentUserName = ForumPromt.CONT.getCurrentlyLoggedOnUserName();
			ForumPromt.CONT.logout(tCurrentUserName);
			throw new NotConnectedException(tCurrentUserName);
		case LOGGED_EXIT:  
			System.out.println("Exiting ..."); 
			System.exit(0); 
			return true; // just to calm the compiler 
		case LOGGED_HELP: 
			return true; 
		default: 
			return false; 
		} 
	}

	public void manageLogin() { 
		try {
			System.out.println("Please type your username (or type " + ESCAPE_SEQUENCE + " to return the main menu)");
			String tUsername = ForumPromt.USER_CHOICE_SCANNER.readLine();
			if (tUsername.equals(ESCAPE_SEQUENCE))
				return;

			System.out.println("Please type your password (or type 2" + ESCAPE_SEQUENCE + " to return the main menu)");
			String tPassword = ForumPromt.USER_CHOICE_SCANNER.readLine();
			if (tPassword.equals(ESCAPE_SEQUENCE))
				return;

			String tRegAns = ForumPromt.CONT.login(tUsername, tPassword);
			if (tRegAns.equals(SUCCESS_MESSAGE))
				this.playLogged();
			else
				System.out.println(tRegAns);
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}

	}

	public void playLogged() { 
		while (true) {
			System.out.println("Welcome " + ForumPromt.CONT.getCurrentlyLoggedOnUserName() + " "
					+ "!"); // here the login should be handled

			printStartScreenHelp(); // print welcome ... 

			// get the user choice and redirect it or exit if exit is pressed 
			try {
				while (!redirectOperations(getStartScreenOperation())) { 
					System.out.println("This chose isn't performed, please try again! (press " + 
							LoggedUserOperations.LOGGED_EXIT.ordinalPlus1() + 
					" to exit and ? to help)"); 
					System.out.println();
					System.out.println("The system waits for your choice ..."); 
				}
			} catch (NotConnectedException e) {
				break;
			} 
		} 
	}
}