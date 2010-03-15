/**
 * 
 */
package forum.server.dummygui;

import java.util.Map;

import forum.server.domainlayer.interfaces.Forum;

/**
 * @author sepetnit
 *
 */
public class ForumFunctions 
{
	public String ESCAPE_SEQUENCE = "esc";

	public void playRegister() 
	{
		System.out.println("please type your username!");
		String username = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your password!");
		String password = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your private name!");
		String firstName = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your last name!");
		String lastName = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your email!");
		String email = ForumPromt.USER_CHOICE_SCANNER.next();


		System.out.println(ForumPromt.CONT.registerToForum(username, password, lastName, firstName, email));
		System.out.println();
	}


	public void addNewSubject(long tRootSubject) {
		// if the user isn't logged-in, log it in
		if (!ForumPromt.CONT.isAUserLoggedIn()) {
			System.out.println("You should log-in to the system in order to add a new subject!");
			System.out.println();
			if (!this.manageLogin())
				return;
		}

		System.out.println("Welcome " + ForumPromt.CONT.getCurrentlyLoggedOnUserName() + "!");
		System.out.println("You are at a new subject add form!");
		System.out.println();		

		System.out.println("please type the subject name! (type " + this.ESCAPE_SEQUENCE + " to exit)");
		String name = ForumPromt.USER_CHOICE_SCANNER.next();

		if (name.equals(ESCAPE_SEQUENCE)) {
//			checkShouldStayLogin();
			return;
		}
		System.out.println("please type the subject description! (type " + this.ESCAPE_SEQUENCE + " to exit)");
		String description = ForumPromt.USER_CHOICE_SCANNER.next();
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

	private void checkShouldStayLogin() {
		while (true) {
			System.out.println("Do you want to stay logged-in? (y or n)");
			String tUserAns = ForumPromt.USER_CHOICE_SCANNER.next();
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

	// if root == -1 ==> the view starts
	public void view(long rootSubject) {

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

				String tUserChoise = ForumPromt.USER_CHOICE_SCANNER.next();

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

				if (rootSubject > -1 && tUserChoise.equals("4"))
					this.openNewThread(rootSubject);

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


	public boolean manageLogin() { 
		System.out.println("Please type your username (or type esc to return the main menu)");
		String tUsername = ForumPromt.USER_CHOICE_SCANNER.next();
		if (tUsername.equals(ESCAPE_SEQUENCE))
			return false;

		System.out.println("Please type your password (or type esc to return the main menu)");
		String tPassword = ForumPromt.USER_CHOICE_SCANNER.next();
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

	public void viewThreads(long rootSubject) {
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

					String tStrUserChoise = ForumPromt.USER_CHOICE_SCANNER.next();

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
					String tStrUserChoise = ForumPromt.USER_CHOICE_SCANNER.next();

					if (tStrUserChoise.equals(ESCAPE_SEQUENCE))
						return;
					else if (tStrUserChoise.equals("?"))
						break;
					else if (tStrUserChoise.equals("add"))
						this.openNewThread(rootSubject);

					try {

						long tLongUserChoise = Long.parseLong(tStrUserChoise);
						if (tSubjectThreads.get(tLongUserChoise) == null)
							throw new NumberFormatException();
						this.viewThreadMessages(tLongUserChoise);
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


	private void viewThreadMessages(long rootMessageID) {

	}


	private void openNewThread(long subjectID) {
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

		String tMsgTitle = ForumPromt.USER_CHOICE_SCANNER.next();
		if (tMsgTitle.equals(ESCAPE_SEQUENCE)) {
			checkShouldStayLogin();
			return;
		}
		System.out.println("please type the message content! (type " + this.ESCAPE_SEQUENCE + " to exit)");

		String tMsgDescription = ForumPromt.USER_CHOICE_SCANNER.next();
		if (tMsgDescription.equals(ESCAPE_SEQUENCE)) {
			checkShouldStayLogin();
			return;
		}

		String tAns = ForumPromt.CONT.addNewMessage(subjectID, ForumPromt.CONT.getCurrentlyLoggedOnUserName(),
				tMsgTitle, tMsgDescription);
		if (tAns.equals("success!"))
			System.out.println("The message was added successfully!");
		else
			System.out.println(tAns);

		checkShouldStayLogin();

	}

	public void viewSubjects(long rootSubject) {
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

			String tStrUserChoise = ForumPromt.USER_CHOICE_SCANNER.next();

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
}
