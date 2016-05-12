package com.cupidocreative.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailUtil {

	/**
	 * Create a MimeMessage using the parameters provided.
	 *
	 * @param to
	 *            Email address of the receiver.
	 * @param from
	 *            Email address of the sender, the mailbox account.
	 * @param subject
	 *            Subject of the email.
	 * @param bodyText
	 *            Body text of the email.
	 * @return MimeMessage to be used to send email.
	 * @throws MessagingException
	 */
	public MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
	}

	public MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText,
			File attachment) throws MessagingException, IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);
		InternetAddress tAddress = new InternetAddress(to);
		InternetAddress fAddress = new InternetAddress(from);

		email.setFrom(fAddress);
		email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
		email.setSubject(subject);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/html");
		mimeBodyPart.setHeader("Content-Type", "text/html; charset=\"UTF-8\"");

		Multipart multipart = new MimeMultipart();

		mimeBodyPart = new MimeBodyPart();
		attachFile(attachment, multipart, mimeBodyPart);

		email.setContent(multipart);

		return email;
	}

	public MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText,
			Collection<File> attachment) throws MessagingException, IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);
		InternetAddress tAddress = new InternetAddress(to);
		InternetAddress fAddress = new InternetAddress(from);

		email.setFrom(fAddress);
		email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
		email.setSubject(subject);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/html");
		mimeBodyPart.setHeader("Content-Type", "text/html; charset=\"UTF-8\"");

		Multipart multipart = new MimeMultipart();

		for (File f : attachment) {
			mimeBodyPart = new MimeBodyPart();
			attachFile(f, multipart, mimeBodyPart);
		}

		email.setContent(multipart);

		return email;
	}

	protected void attachFile(File attachment, Multipart multipart, MimeBodyPart mimeBodyPart)
			throws MessagingException, IOException {
		DataSource source = new FileDataSource(attachment);
		mimeBodyPart.setDataHandler(new DataHandler(source));
		mimeBodyPart.setFileName(attachment.getName());

		String contentType = Files.probeContentType(FileSystems.getDefault().getPath(attachment.getPath()));
		mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + attachment.getName() + "\"");
		mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

		multipart.addBodyPart(mimeBodyPart);
	}

}
