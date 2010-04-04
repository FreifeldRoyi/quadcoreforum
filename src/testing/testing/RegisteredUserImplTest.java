package forum.server.domainlayer.testing;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.RegisteredUserImpl;

public class RegisteredUserImplTest extends TestCase {

	RegisteredUserImpl user;
	
	@Before
	public void setUp() throws Exception {
		String usrNm = "Royif";
		String pass = "1234";
		String prvNm = "Royi";
		String lstNm = "Freifeld";
		String eml = "mail";
		user = new RegisteredUserImpl(usrNm, pass, prvNm, lstNm, eml);
	}


	@Test
	public void testGetEMail() {
		if(!user.getEMail().equals("mail")){
			fail("EMail is not equal");
		}
	}
	
	@Test
	public void testGetLastName() {
		if(!user.getLastName().equals("Freifeld")){
			fail("LastName is not equal");
		}
	}

	@Test
	public void testGetPassword() {
		if(!user.getPassword().equals("1234")){
			fail("Password is not equal");
		}
	}

	@Test
	public void testGetPostedMsgNumber() {
		assertEquals(0, user.getPostedMsgNumber());
	}

	@Test
	public void testGetPrivateName() {
		if(!user.getPrivateName().equals("Royi")){
			fail("PrivateName is not equal");
		}
	}

	@Test
	public void testGetUsername() {
		if(!user.getUsername().equals("Royif")){
			fail("UserName is not equal");
		}
	}

	@Test
	public void testSetLastName() {
		if(!user.getLastName().equals("Freifeld")){
			fail("LastName is not equal");
		}
		user.setLastName("Frei");
		if(!user.getLastName().equals("Frei")){
			fail("LastName wasnt set");
		}
	}

	@Test
	public void testSetPassword() {
		if(!user.getPassword().equals("1234")){
			fail("Password is not equal");
		}
		user.setPassword("2345");
		if(!user.getPassword().equals("2345")){
			fail("Password wasnt set");
		}
	}

	@Test
	public void testSetPostedMsgNumber() {
		assertEquals(0, user.getPostedMsgNumber());
		user.setPostedMsgNumber(3);
		assertEquals(3, user.getPostedMsgNumber());
	}

	@Test
	public void testSetPrivateName() {
		if(!user.getPrivateName().equals("Royi")){
			fail("PrivateName is not equal");
		}
		user.setPrivateName("Yakir");
		if(!user.getPrivateName().equals("Yakir")){
			fail("PrivateName wasnt set");
		}
	}

	@Test
	public void testIncPostedMsgNum() {
		assertEquals(0, user.getPostedMsgNumber());
		user.incPostedMsgNum();
		assertEquals(1, user.getPostedMsgNumber());
	}

}
