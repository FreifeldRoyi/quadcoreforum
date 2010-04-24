/**
 * 
 */
package testing.domain.message;

import java.util.Collection;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.message.ForumSubject;

/**
 * @author sepetnit
 *
 */
public class ForumSubjectTest extends TestCase {

	private static final int DEFAULT_SUBJECT_ID = 34;
	private static final String DEFAULT_SUBJECT_NAME = "test subject name";
	private static final String DEFAULT_SUBJECT_DESCRIPTION = "test subject descripion";
	private static final boolean DEFAULT_IS_TOP_LEVEL = false;


	private ForumSubject subjectUnderTest;

	/**
	 * 
	 * Initializes the tested subject to be a default one
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.subjectUnderTest = new ForumSubject(ForumSubjectTest.DEFAULT_SUBJECT_ID, 
				ForumSubjectTest.DEFAULT_SUBJECT_NAME, ForumSubjectTest.DEFAULT_SUBJECT_DESCRIPTION,
				ForumSubjectTest.DEFAULT_IS_TOP_LEVEL);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#addSubSubject(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#deleteSubSubject(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#getSubSubjects()
	 */
	@Test
	public void testSubSubjectsHandling() {	
		int tNumOfSub = this.subjectUnderTest.getSubSubjects().size();
		this.subjectUnderTest.addSubSubject(67L);
		this.subjectUnderTest.addSubSubject(672L);
		this.subjectUnderTest.addSubSubject(26L);
		Collection<Long> tSubSubjectsIDs = this.subjectUnderTest.getSubSubjects();
		assertTrue(tNumOfSub + 3 == tSubSubjectsIDs.size());		
		assertTrue(tSubSubjectsIDs.contains(67L));
		assertTrue(tSubSubjectsIDs.contains(26L));
		this.subjectUnderTest.deleteSubSubject(67L);
		tSubSubjectsIDs = this.subjectUnderTest.getSubSubjects();
		assertTrue(tNumOfSub + 2 == tSubSubjectsIDs.size());		
		assertTrue(tSubSubjectsIDs.contains(26L));
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#addThread(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#deleteThread(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#getThreads()
	 */
	@Test
	public void testThreadsHandling() {	
		int tNumOfThreads = this.subjectUnderTest.getThreads().size();
		this.subjectUnderTest.addThread(67L);
		this.subjectUnderTest.addThread(26L);
		Collection<Long> tThreadsIDs = this.subjectUnderTest.getThreads();
		assertTrue(tNumOfThreads + 2 == tThreadsIDs.size());		
		assertTrue(tThreadsIDs.contains(67L));
		assertTrue(tThreadsIDs.contains(26L));
		this.subjectUnderTest.deleteThread(67L);
		tThreadsIDs = this.subjectUnderTest.getThreads();
		assertTrue(tNumOfThreads + 1 == tThreadsIDs.size());		
		assertTrue(tThreadsIDs.contains(26L));
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumSubject#toString()}.
	 */
	@Test
	public void testSubjToString() {
		assertEquals(this.subjectUnderTest.toString(), 34 + "\t" + ForumSubjectTest.DEFAULT_SUBJECT_NAME + "\t" +
				ForumSubjectTest.DEFAULT_SUBJECT_DESCRIPTION);
	}
}
