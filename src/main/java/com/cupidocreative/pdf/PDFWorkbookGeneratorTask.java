package com.cupidocreative.pdf;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cupidocreative.mail.GmailSender;

public class PDFWorkbookGeneratorTask implements Runnable {

	private static final String GMAIL_USER = "me";
	private String emailBody;
	private String emailSubject;
	private String emailTo;
	private Log LOG = LogFactory.getLog(PDFWorkbookGeneratorTask.class);
	private String rootWorksheetFolderPath;

	private int size;

	private String targetFilePath;

	/**
	 * Generate workbook and send mail as attachment
	 * 
	 * @param rootWorksheetFolderPath
	 *            Path directory to worksheet, eg : .../Addition/worksheet
	 * @param targetFilePath
	 *            Path file (pdf) to generated, to be attached on email
	 * @param size
	 *            Number of worksheet to generate from worksheet folder
	 * @param emailTo
	 *            Email to
	 * @param emailSubject
	 *            Email subject
	 * @param emailBody
	 *            Email body
	 */
	public PDFWorkbookGeneratorTask(String rootWorksheetFolderPath, String targetFilePath, int size, String emailTo,
			String emailSubject, String emailBody) {
		super();
		this.rootWorksheetFolderPath = rootWorksheetFolderPath;
		this.targetFilePath = targetFilePath;
		this.size = size;
		this.emailTo = emailTo;
		this.emailSubject = emailSubject;
		this.emailBody = emailBody;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public String getRootWorksheetFolderPath() {
		return rootWorksheetFolderPath;
	}

	public int getSize() {
		return size;
	}

	public String getTargetFilePath() {
		return targetFilePath;
	}

	@Override
	public void run() {
		PDFWorkbookGenerator generator = new PDFWorkbookGenerator();
		GmailSender gmailSender = new GmailSender();

		LOG.info("Generate from " + size + " worksheets");
		generator.generate(this.rootWorksheetFolderPath, this.targetFilePath, this.size);
		try {
			LOG.info("Sending mail to " + emailTo);
			MimeMessage email = gmailSender.createEmailWithAttachment(this.emailTo, GMAIL_USER, this.emailSubject,
					this.emailBody, new File(targetFilePath));
			gmailSender.sendMessage(GmailSender.getGmailService(), GMAIL_USER, email);
			LOG.info("Mail sent to " + emailTo);
		} catch (MessagingException | IOException e) {
			LOG.error("Send mail to " + emailTo + " failed : " + e.getMessage());
		}
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public void setRootWorksheetFolderPath(String rootWorksheetFolderPath) {
		this.rootWorksheetFolderPath = rootWorksheetFolderPath;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setTargetFilePath(String targetFilePath) {
		this.targetFilePath = targetFilePath;
	}

}
