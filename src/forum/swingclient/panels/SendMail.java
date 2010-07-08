package forum.swingclient.panels;


import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class SendMail {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_AUTH_USER = "qcforuminfo";
	private static final String SMTP_AUTH_PWD  = "123456ab";

	private static final String emailMsgTxt      = "QuadCore Rules!! \n The message was sent from Java application.";
	private static final String emailSubjectTxt  = "QuadCore Network admin message";
	private static final String emailFromAddress = "qcforuminfo@gmail.com";

	// Add List of Email address to who email needs to be sent to
	//  private static final String[] emailList = {"lital.badash@gmail.com"};//,"sepetnit@gmail.com","freifeld.royi@gmail.com","badash6@gmail.com"};

	/*public static void main(String args[]) throws Exception {
    SendMail smtpMailSender = new SendMail();
    smtpMailSender.postMail( emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
    System.out.println("Sucessfully Sent mail to All Users");
  }*/

	public void postMail(String recipient, String subject, String message , String from) throws MessagingException {
		String [] recipients = new String[1];
		recipients[0] = recipient;		
		postMail(recipients, subject, message, from);
	}

	
	public void postMail(String recipients[], String subject, String message, String from) throws MessagingException {

		boolean debug = false;

		//Set the host smtp address

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable","true");

		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];

		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}

		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/html");
		Transport.send(msg);
	}


	/**
	 * SimpleAuthenticator is used to do simple authentication
	 * when the SMTP server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}

}
