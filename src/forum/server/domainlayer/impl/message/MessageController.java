package forum.server.domainlayer.impl.message ;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.SubjectAlreadyExistsException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.persistentlayer.pipe.PersistenceDataHandler;
import forum.server.persistentlayer.pipe.PersistenceFactory;

/**
 * This class is a controller for all message's actions.
 *
 * Contains methods to get subjects, threads and messages location as domain objects.
 *
 * In addition, this class holds all the methods that are needed by the GUI to access messages
 * and present the forum pages: (like getting content of subjects and threads by their id-s) 
 * and all administrative methods of adding and deleting subjects, threads and messages.
 */
public class MessageController
{

	/** reference to the forum object which holds the users and the messages in the forum system **/
	private final PersistenceDataHandler pipe; // A pipe to the persistence layer
	private ForumSubject currSubject; // the current subject asked by the user



	/**
	 * The class constructor.
	 * 
	 * Initializes a MessageController object that handles all the forum posts activity
	 */
	public MessageController() {
		this.pipe = PersistenceFactory.getPipe();
		currSubject = null;
	}


	public Vector<ForumSubject> getForumSubjects() {
		return this.subjects;
	}



	public Map<Long, String> getForumThreadsBySubjectID(long rootSubjectID) throws SubjectNotFoundException {		
		return this.getForumSubjectByID(rootSubjectID).getForumThreadsDesc();


		public ForumSubject getForumSubjectByID(long id) throws SubjectNotFoundException {
			for (ForumSubject tSubj : this.subjects) {
				if (tSubj.getSubjectID() == id)
					return tSubj;
				try {
					ForumSubject toReturn = tSubj.getForumSubject(id);
					return toReturn;
				}
				catch (SubjectNotFoundException e) {
					continue;
				}		
			}
			throw new SubjectNotFoundException(id);
		}


		public ForumMessage getMessageByID(long msgID) throws MessageNotFoundException {
			ForumMessage toReturn = null;

			for (ForumSubject tSubj : this.subjects) {
				try {
					toReturn = tSubj.findMessage(msgID);
					return toReturn;
				}
				catch (MessageNotFoundException e) {
					continue;
				}
			}

			throw new MessageNotFoundException(msgID);
		}

		
		/* Methods */

		public void addForumSubject(ForumSubject fs) throws 
		JAXBException, IOException, SubjectAlreadyExistsException {
			for (ForumSubject tForumSubject : subjects)
				if (tForumSubject.getName().equals(fs.getName()))
					throw new SubjectAlreadyExistsException(fs.getName());
			subjects.add(fs);
			PersistenceDataHandler pipe = PersistenceFactory.getPipe();		
			pipe.addNewSubject(fs.getSubjectID(), fs.getName(), fs.getDescription());
		}


		public void updateAMessage(long messageID, String newTitle,
				String newContent) throws JAXBException, IOException, MessageNotFoundException {
			this.getMessageByID(messageID).updateMe(newTitle, newContent);

		}






































		/**
		 * add new subDirectory under an existing directory
		 * (the root is build when the forum is initialized)
		 * @param userId the user which ask to add this directory
		 * @param containerDirectoryId the id of the directory under it the new subDirectory will be added
		 * @param newDirName the name for the new subDirectory
		 * @return the new directory for presentation
		 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
		 * this action
		 * @throws NotFoundException if the container directory was not found
		 */
		public ForumSubject addSubject(final long userId, final long containerDirectoryId, final String newDirName) throws UnpermittedActionException, NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("user " + userId + " requests to add a directory named " +
					newDirName + " to directory " + containerDirectoryId + ".") ;
			final User user = this.forum.getUsers().getUser(userId) ;

			// check permission to perform this action:
			if (user.getPrivileges().contains(Privilege.ADD_DIRECTORY))
			{
				Log.getLogger(Subsystem.SERVICES).finest("permission granted.") ;
				final Directory dir = this.forum.getMessages().getDirectory(containerDirectoryId);
				final Directory newDir = this.forum.getMessages().createDirectory(newDirName) ;

				//add the new subDirectory to the directory:
				dir.addDirectory(newDir.getId());
				this.forum.getMessages().flush(dir); //update changes in directory to the persistent

				return newDir;
			}
			else
			{
				Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
				throw new UnpermittedActionException() ;
			}
		}


		/**
		 * create a new post in thread.
		 * @param threadId location of the new post
		 * @param userId the id of the user requested to perform this action
		 * @param msg the wanted message in the post
		 * @return the new post for presentation
		 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
		 * this action
		 * @throws NotFoundException if the thread or the user was not found
		 * @post messages has a new post with the wanted message, and the wanted thread holds its id.
		 */
		public ForumMessage addReply(final long threadId, final long userId, final String msg)
		throws UnpermittedActionException, NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).fine("user " + userId + " requests to add a post to thread " + threadId + ".") ;
			final User user = this.forum.getUsers().getUser(userId) ;

			// check permission to perform this action:
			if (user.getPrivileges().contains(Privilege.ADD_POST))
			{
				Log.getLogger(Subsystem.SERVICES).finest("permission granted.") ;
				final Thread thread = this.forum.getMessages().getThread(threadId);
				final Post newPost = this.forum.getMessages().createPost(msg, userId) ;

				//add the post to the thread and to index:
				thread.addPost(newPost.getId());
				this.forum.getMessages().flush(thread); //update changes in thread to the persistent
				this.notifyIndexer(user, newPost) ;
				return newPost;

			}
			else
			{
				Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
				throw new UnpermittedActionException() ;
			}
		}

		/**
		 * create a new thread in a sub-forum, with a first post.
		 * @param dirId location of the new thread
		 * @param userId the member requested to perform this action
		 * @param subject the thread subject
		 * @param msg the wanted message for the first post in the new created thread
		 * @return the new thread for presentation
		 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
		 * this action
		 * @throws NotFoundException if the directory or the user was not found
		 * @post a new thread with the wanted subject was created in the the wanted thread, and its first post
		 * includes the given message.
		 */
		public ForumThread addThread(final long dirId, final long userId, final String subject, final String msg)
		throws UnpermittedActionException, NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("user " + userId + " requests to add a thread named " +
					subject + " to directory " + dirId + ".") ;
			final User user = this.forum.getUsers().getUser(userId) ;

			// check permission to perform this action:
			if (user.getPrivileges().contains(Privilege.ADD_THREAD))
			{
				Log.getLogger(Subsystem.SERVICES).finest("permission granted.") ;
				final Directory dir = this.forum.getMessages().getDirectory(dirId);
				final Thread newThread = this.forum.getMessages().createThread(subject) ;
				final Post newPost = this.forum.getMessages().createPost(msg, userId) ;
				this.notifyIndexer(user, newPost) ;

				//add the first post to the thread:
				newThread.addPost(newPost.getId());
				this.forum.getMessages().flush(newThread); //update changes in thread to the persistent

				//add the thread to the directory:
				dir.addThread(newThread.getId());
				this.forum.getMessages().flush(dir); //update changes in directory to the persistent

				return newThread;
			}
			else
			{
				Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
				throw new UnpermittedActionException() ;
			}
		}


		/**
		 * edit the wanted post (only if member is the writer of the post, or moderator)
		 * @param postId the wanted post to edit
		 * @param userId the member requested to perform this action
		 * @param msg the new message to edit instead of the current message in the post
		 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
		 * this action
		 * @throws NotFoundException if the post or the user was not found
		 * @post the post holds the new message (msg) instead of the previous one.
		 */
		public void editMessage(final long postId, final long userId, final String msg)
		throws UnpermittedActionException, NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("user " + userId + " is requesting to edit post " + postId + ".") ;
			final User user = this.forum.getUsers().getUser(userId);
			final Post post = this.forum.getMessages().getPost(postId);

			//check permission to perform this action, and check if member is the writer of post,
			//or it is a moderator that can edit any post:
			if( (user.getPrivileges().contains(Privilege.EDIT_POST) & post.getWriterId() == user.getId()) |
					(user.getPrivileges().contains(Privilege.EDIT_ANY_POST)) )
			{
				Log.getLogger(Subsystem.SERVICES).finest("permission granted.") ;
				final Member writer = this.forum.getUsers().getMember(post.getWriterId());
				this.indexAgent.removePostFromIndex(post, writer.getUsername());
				post.edit(msg);
				this.forum.getMessages().flush(post); //update changes in post to the persistent
				this.indexAgent.addNewPostToIndex(post, writer.getUsername());
			}
			else
			{
				Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
				throw new UnpermittedActionException() ;
			}
		}

		/**
		 * get all threads's posts in a presentation form
		 * @param threadId the thread to retrieve its threads
		 * @return vector of all posts in a form of passing to the presentation layer
		 * @throws NotFoundException when the thread was not found
		 */
		public Vector<ForumThread> getPosts(final long threadId) throws NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("viewing thread " + threadId + " posts.") ;
			final Thread thread = this.forum.getMessages().getThread(threadId);
			final Vector<Long> postsIds = thread.getPosts();
			final Vector<UIPost> posts = new Vector<UIPost>();
			for(int i=0; i<postsIds.size(); i++)
			{
				final long id = postsIds.elementAt(i);
				posts.add(this.forum.getMessages().getPost(id));
			}
			return posts;
		}

		/**
		 * get all directory's subDirectories in a presentation
		 * @param dirId the directory to retrieve its subDirectories
		 * @return vector of all subDirectories in a form of passing to the presentation layer
		 * @throws NotFoundException when the directory was not found
		 */
		public Vector<ForumSubject> getSubSubjects(final long dirId) throws NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("viewing directory " + dirId + " subDirectories.") ;
			final Directory directory = this.forum.getMessages().getDirectory(dirId);
			final Vector<Long> childrenIds = directory.getChildren();
			final Vector<UIDirectory> subDirectories= new Vector<UIDirectory>();
			for(int i=0; i<childrenIds.size(); i++)
			{
				final long id = childrenIds.elementAt(i);
				subDirectories.add(this.forum.getMessages().getDirectory(id));
			}
			return subDirectories;
		}


		/**
		 * get all directory's threads in a presentation form
		 * @param dirId the directory to retrieve its threads
		 * @return vector of all threads in a form of passing to the presentation layer
		 * @throws NotFoundException when the directory was not found
		 */
		public Vector<ForumThread> getThreads(final long dirId) throws NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("viewing directory " + dirId + " threads.") ;
			final Directory directory = this.forum.getMessages().getDirectory(dirId);
			final Vector<Long> threadsIds = directory.getThreads();
			final Vector<UIThread> threads = new Vector<UIThread>();
			for(int i=0; i<threadsIds.size(); i++)
			{
				final long id = threadsIds.elementAt(i);
				threads.add(this.forum.getMessages().getThread(id));
			}
			return threads;
		}

		/**
		 * view the wanted post
		 * @param postId the wanted post to view
		 * @return the post in a form of passing to the presentation layer
		 * for performing this action
		 * @throws NotFoundException if the post or the user was not found
		 * @post no change is made in the system.
		 **/
		public ForumPost viewPost(final long postId) throws NotFoundException
		{
			Log.getLogger(Subsystem.SERVICES).info("viewing post " + postId + ".") ;
			final Post post = this.forum.getMessages().getPost(postId);
			return post;
		}
	}
