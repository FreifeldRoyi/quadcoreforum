/**
 * 
 */
package forum.server.dummygui;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

/**
 * @author sepetnit
 *
 */
public class ForumFunctions 
{
	public String ESCAPE_SEQUENCE = "esc";

	public void playRegister() 
	{
		try {
			System.out.println("please type your username!");
			String username = ForumPromt.USER_CHOICE_SCANNER.readLine();
			System.out.println("please type your password!");
			String password = ForumPromt.USER_CHOICE_SCANNER.readLine();
			System.out.println("please type your private name!");
			String firstName = ForumPromt.USER_CHOICE_SCANNER.readLine();
			System.out.println("please type your last name!");
			String lastName = ForumPromt.USER_CHOICE_SCANNER.readLine();
			System.out.println("please type your email!");
			String email = ForumPromt.USER_CHOICE_SCANNER.readLine();


			System.out.println(ForumPromt.CONT.registerToForum(username, password, lastName, firstName, email));
			System.out.println();
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}
	}


	public void addNewSubject(long tRootSubject) {
		try {

			// if the user isn't logged-in, log it in
			if (!ForumPromt.CONT.isAUserLoggedIn()) {
				System.out.println("You should log-in to the system in order to add a new subject!");
				System.out.println();
			}

			System.out.println("Welcome " + ForumPromt.CONT.getCurrentlyLoggedOnUserName() + "!");
			System.out.println("You are at a new subject add form!");
			System.out.println();		

			System.out.println("please type the subject name! (type " + this.ESCAPE_SEQUENCE + " to exit)");

			String name = ForumPromt.USER_CHOICE_SCANNER.readLine();



			if (name.equals(ESCAPE_SEQUENCE)) {
				//			checkShouldStayLogin();
				return;
			}
			System.out.println("please type the subject description! (type " + this.ESCAPE_SEQUENCE + " to exit)");
			String description = ForumPromt.USER_CHOICE_SCANNER.readLine();
			if (description.equals(ESCAPE_SEQUENCE)) {
				//			checkShouldStayLogin();
				return;
			}

			if (tRootSubject == -1) {
				System.out.println(ForumPromt.CONT.addNewSubject(name, description));
				System.out.println();
				//			checkShouldStayLogin();
			}
			else {
				System.out.println(ForumPromt.CONT.addNewSubSubject(tRootSubject, name, description));
				System.out.println();
				//			checkShouldStayLogin();
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}

	}

	private void checkShouldStayLogin() {
		try {
			while (true) {
				System.out.println("Do you want to stay logged-in? (y or n)");
				String tUserAns = ForumPromt.USER_CHOICE_SCANNER.readLine();
				if (tUserAns.equals("y"))
					new ForumLogin().playLogged();
				else if (tUserAns.equals("n"))
					return;
				else if (tUserAns.equals("?"))
					continue;
				else {
					System.out.println("This chose isn't performed, please try again! (type ? to help)"); 
					System.out.println(); 
					System.out.println("The system waits for your choose ...");
				}

			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}

	}

	// if root == -1 ==> the view starts
	public void view(long rootSubject) {
		try {
			while (true) {
				System.out.println("Welcome to the messages view");
				System.out.println();
				if (rootSubject == -1)
					System.out.println("You are at the forum root!!!");
				else
					System.out.println("You are at the subject " + 
							ForumPromt.CONT.getForumSubjectByID(rootSubject));

				System.out.println();
				System.out.println("Please choose one of the following operations:\n"); 

				if (rootSubject == -1)
					System.out.println("1: view the forum subjects");
				else
					System.out.println("1: view the sub-subjects");


				if (rootSubject > -1)
					System.out.println("2: view the Threads");

				if (ForumPromt.CONT.isAUserLoggedIn())
					System.out.println("3: add a new sub-subject");

				if (rootSubject > -1)
					System.out.println("4: open a new thread");

				System.out.println("5: return the previous menu");
				System.out.println("6: exit the program");

				System.out.println();
				System.out.println("The system waits for your choose ..."); 





				while (true) {

					String tUserChoise = ForumPromt.USER_CHOICE_SCANNER.readLine();

					if (tUserChoise.equals("?"))
						break;

					if (tUserChoise.equals("5"))
						return;

					if (tUserChoise.equals("6")) {
						System.out.println("Exiting ..."); 
						System.out.println("Done");
						System.exit(0);			
					}


					if (rootSubject > -1 && tUserChoise.equals("2")) {
						this.viewThreads(rootSubject);
						break;
					}		

					if (tUserChoise.equals("3")) {	
						this.addNewSubject(rootSubject);
						break;
					}

					if (rootSubject > -1 && tUserChoise.equals("4")) {
						this.openNewThread(rootSubject);
						break;
					}

						
					if (tUserChoise.equals("1")) {	
						this.viewSubjects(rootSubject);
						break;
					}

					System.out.println("This chose isn't performed, please try again! (type " + 
							"5" + " to return the previous menu and ? to help)"); 
					System.out.println(); 
					System.out.println("The system waits for your choise ...");
				}
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}

	}


	public boolean manageLogin() { 
		try {
			System.out.println("Please type your username (or type esc to return the main menu)");
			String tUsername = ForumPromt.USER_CHOICE_SCANNER.readLine();
			if (tUsername.equals(ESCAPE_SEQUENCE))
				return false;

			System.out.println("Please type your password (or type esc to return the main menu)");
			String tPassword = ForumPromt.USER_CHOICE_SCANNER.readLine();
			if (tPassword.equals(ESCAPE_SEQUENCE))
				return false;

			String tRegAns = ForumPromt.CONT.login(tUsername, tPassword);
			if (tRegAns.equals("success!"))
				return true;
			else {
				System.out.println();
				System.out.println(tRegAns);
				return false;
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
			return false;
		}

	}

	public void viewThreads(long rootSubject) {
		try {
			while (true) {

				Map<Long, String> tSubjectThreads = ForumPromt.CONT.getSubjectThreads(rootSubject);
				if (tSubjectThreads == null) // subject not found exception has occured
					return;


				System.out.println("in order to open a new thread under this subject type \"add\".");
				System.out.println();
				System.out.println();

				if (tSubjectThreads.isEmpty()) {
					System.out.println("Thrs subject " + ForumPromt.CONT.getForumSubjectByID(rootSubject) +
							" has no opened threads!!! (type " + this.ESCAPE_SEQUENCE + " to exit or ? to help)");

					while (true) {

						String tStrUserChoise = ForumPromt.USER_CHOICE_SCANNER.readLine();

						if (tStrUserChoise.equals(ESCAPE_SEQUENCE))
							return;
						else if (tStrUserChoise.equals("?"))
							break;
						else if (tStrUserChoise.equals("add")) {
							this.openNewThread(rootSubject);
							return;
						}

						System.out.println("This chose isn't performed, please try again! (type " + 
								this.ESCAPE_SEQUENCE + " to exit or ? to help)"); 
						System.out.println(); 
						System.out.println("The system waits for your choise ...");
					}


				}
				else {
					System.out.println("please choose the desired id (type " + 
							this.ESCAPE_SEQUENCE + " to exit or ? to help)");
					System.out.println();		

					System.out.println("The threads of the subject " + 
							ForumPromt.CONT.getForumSubjectByID(rootSubject) + " are: ");
					for (Long tThreadID : tSubjectThreads.keySet())
						System.out.println(tThreadID + " : " + tSubjectThreads.get(tThreadID));



					while (true) {
						String tStrUserChoise = ForumPromt.USER_CHOICE_SCANNER.readLine();

						if (tStrUserChoise.equals(ESCAPE_SEQUENCE))
							return;
						else if (tStrUserChoise.equals("?"))
							break;
						else if (tStrUserChoise.equals("add")) {
							this.openNewThread(rootSubject);
							break;
						}
							

						try {

							long tLongUserChoise = Long.parseLong(tStrUserChoise);
							if (tSubjectThreads.get(tLongUserChoise) == null)
								throw new NumberFormatException();
							this.viewThreadMessages(tLongUserChoise);
							break;
						}
						catch (NumberFormatException e) {
							System.out.println("This chose isn't performed, please try again! (type " + 
									this.ESCAPE_SEQUENCE + " to exit or ? to help)"); 
							System.out.println(); 
							System.out.println("The system waits for your choise ...");
						}
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}
	}


	/**
	 * Prints the message and its replies
	 * 
	 * @param rootMessageID
	 * 		The root message
	 * @return
	 */
	private Collection<String> printRootMessages(long rootMessageID) {
		Vector<String> toReturn = new Vector<String>();

		if (!toReturn.contains(rootMessageID))
			toReturn.add(rootMessageID + "");

		Map<Long, String> tReplies = ForumPromt.CONT.getMessageAndRepliesByRoot(rootMessageID);

		System.out.println("message: id = " + rootMessageID);
		System.out.println(tReplies.get(rootMessageID));
		System.out.println("replies: (");		

		for (Long tReply : tReplies.keySet()) {

			if (!toReturn.contains(tReply))
				toReturn.add(tReply + "");
			
			if (tReply !=  rootMessageID)
				toReturn.addAll(this.printRootMessages(tReply));

		}

		System.out.println(")");
		return toReturn;
	}


	private boolean replyToAMessage(Collection<String> tMessageIds) {
		try {
			String tReplyChoice = "-2";

			while (true) {
				tReplyChoice = ForumPromt.USER_CHOICE_SCANNER.readLine();

				if (tReplyChoice.equals("?"))
					return false;

				if (tReplyChoice.equals("esc"))
					return true;


				if (!tMessageIds.contains(tReplyChoice)) {
					System.out.println("the given message id isn't valid, please try again " +
					"(type esc to exit and ? to help)");
					System.out.println();
					System.out.println("The system waits for your choice ..."); 
					continue;
				}



				System.out.println("please type a new title for your reply");

				String tNewTitle = ForumPromt.USER_CHOICE_SCANNER.readLine();

				System.out.println("please type a new content for your reply");

				String tNewContent =  ForumPromt.USER_CHOICE_SCANNER.readLine();

				ForumPromt.CONT.replyToMessage(Long.parseLong(tReplyChoice),
						ForumPromt.CONT.getCurrentlyLoggedOnUserName(), 
						tNewTitle, tNewContent);
				return true;
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
			return false;
		}


	}



	// returns true iff the operation ended
	private boolean editAMessage(Collection<String> tMessageIds) {
		try {
			String tEditChoice = "-2";

			while (true) {
				tEditChoice = ForumPromt.USER_CHOICE_SCANNER.readLine();

				if (tEditChoice.equals("?"))
					return false;

				if (tEditChoice.equals("esc"))
					return true;


				if (!tMessageIds.contains(tEditChoice)) {
					System.out.println("the given message id isn't valid, please try again " +
					"(type esc to exit and ? to help)");
					System.out.println();
					System.out.println("The system waits for your choice ..."); 
					continue;
				}



				System.out.println("please type a new title for the message " + tEditChoice);

				String tNewTitle = ForumPromt.USER_CHOICE_SCANNER.readLine();

				System.out.println("please type a new content for the message " +
						tEditChoice);

				String tNewContent =  ForumPromt.USER_CHOICE_SCANNER.readLine();

				System.out.println(ForumPromt.CONT.updateAMessage(Long.parseLong(tEditChoice), tNewTitle, tNewContent));

				return true;
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
			return false;
		}

	}


	private void viewThreadMessages(long rootMessageID) {
		try {

			while (true) {
				Collection<String> tMessageIds = printRootMessages(rootMessageID);
				System.out.println();
				System.out.println("Please choose one of the following operations:\n"); 
				if (ForumPromt.CONT.isAUserLoggedIn()) {
					System.out.println("\t" + "1"         + ": reply to message"); 
					System.out.println("\t" + "2" 		  + ": edit a message"); 
				}
				System.out.println("\t" + "3"         + ": return to the previous screen"); 

				String tUserChoice = ForumPromt.USER_CHOICE_SCANNER.readLine();

				if (tUserChoice.equals("esc"))
					break;

				if (tUserChoice.equals("?"))
					continue;

				if (tUserChoice.equals("3"))
					break;

				if (tUserChoice.equals("2") && ForumPromt.CONT.isAUserLoggedIn()) {

					while (true) {					
						System.out.println("type the id of the message you want to edit");
						if (editAMessage(tMessageIds))
							break;
					}
					continue;
				}

				if (tUserChoice.equals("1") && ForumPromt.CONT.isAUserLoggedIn()) {
					while (true) {
						System.out.println("type the id of the message you want to reply");
						if (replyToAMessage(tMessageIds))
							break;
					}
					continue;
				}



				System.out.println("This choice isn't performed, please try again! (type " + 
						"esc" + 
				" to exit and ? to main menu)"); 
				System.out.println(); 
				System.out.println("The system waits for your choice ..."); 
			}
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}

	}


	private void openNewThread(long subjectID) {
		try {
			if (!ForumPromt.CONT.isAUserLoggedIn()) {
				System.out.println("You should log-in to the system in order to post messages!");
				System.out.println();
				if (!this.manageLogin())
					return;
			}

			System.out.println("Welcome " + ForumPromt.CONT.getCurrentlyLoggedOnUserName() + "!");
			System.out.println("You are at the messages posting form!");
			System.out.println();		

			System.out.println("please type the message title! (type " + this.ESCAPE_SEQUENCE + " to exit)");

			String tMsgTitle = ForumPromt.USER_CHOICE_SCANNER.readLine();
			if (tMsgTitle.equals(ESCAPE_SEQUENCE)) {
				checkShouldStayLogin();
				return;
			}
			System.out.println("please type the message content! (type " + this.ESCAPE_SEQUENCE + " to exit)");

			String tMsgDescription = ForumPromt.USER_CHOICE_SCANNER.readLine();
			
			if (tMsgDescription.equals(ESCAPE_SEQUENCE))
				return;

			String tAns = ForumPromt.CONT.addNewMessage(subjectID, ForumPromt.CONT.getCurrentlyLoggedOnUserName(),
					tMsgTitle, tMsgDescription);
			if (tAns.equals("success!"))
				System.out.println("The message was added successfully!");
			else
				System.out.println(tAns);
		}
		catch (IOException e) {
			System.out.println("promter error!");
		}

	}

	public void viewSubjects(long rootSubject) {
		try {
			while (true) {
				System.out.println();
				Map<Long, String> subjects = null;
				if (rootSubject == -1) 
					subjects = ForumPromt.CONT.getForumSubjects();
				else
					subjects = ForumPromt.CONT.getSubjectsByRoot(rootSubject);

				System.out.println("please choose the desired subject id (type " + 
						this.ESCAPE_SEQUENCE + " to exit)");
				System.out.println();


				for (Long subjectsID : subjects.keySet())
					System.out.println(subjectsID + ": " + subjects.get(subjectsID));

				String tStrUserChoise = ForumPromt.USER_CHOICE_SCANNER.readLine();

				if (tStrUserChoise.equals(ESCAPE_SEQUENCE))
					break;
				else if (tStrUserChoise.equals("?"))
					continue;

				try {

					long tLongUserChoise = Long.parseLong(tStrUserChoise);
					if (subjects.get(tLongUserChoise) == null)
						throw new NumberFormatException();
					this.view(tLongUserChoise);
				}
				catch (NumberFormatException e) {
					System.out.println("This chose isn't performed, please try again! (type " + 
							this.ESCAPE_SEQUENCE + " to exit and ? to help)"); 
					System.out.println(); 
					System.out.println("The system waits for your choise ...");
				}
			}

		}
		catch (IOException e) {
			System.out.println("promter error!");
		}
	}
}
