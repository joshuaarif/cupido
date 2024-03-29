package com.cupidocreative.pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cupidocreative.mail.GmailSender;
import com.cupidocreative.util.MailUtil;
import com.google.api.client.util.Lists;

public class PDFWorkbookGeneratorTask implements Runnable {

	private static final String GMAIL_USER = "me";
	private String emailBody;
	private String emailSubject;
	private String emailTo;
	private Log LOG = LogFactory.getLog(PDFWorkbookGeneratorTask.class);
	private String rootWorksheetFolderPath;
	private int size;
	private String targetFilePath;
	private String pdfTitle = "Cupido Creative";
	private String pdfCreator = "www.cupidocreative.com";
	private String pdfSubject = "Buku Latihan Cupido Creative, lihat lengkapnya di www.cupidocreative.com";
	private String pdfFooterImagePath;
	private boolean deleteTempPdfFile = false;

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
		setTargetFilePath(targetFilePath);
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

	public String getPdfCreator() {
		return pdfCreator;
	}

	public String getPdfFooterImagePath() {
		return pdfFooterImagePath;
	}

	public String getPdfSubject() {
		return pdfSubject;
	}

	public String getPdfTitle() {
		return pdfTitle;
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

	public boolean isDeleteTempPdfFile() {
		return deleteTempPdfFile;
	}

	@Override
	public void run() {
		PDFWorkbookGenerator generator = new PDFWorkbookGenerator();
		GmailSender gmailSender = new GmailSender();
		MailUtil mailUtil = new MailUtil();

		LOG.info("Generate from " + size + " worksheets");
		generator.generate(this.rootWorksheetFolderPath, this.targetFilePath, this.size, this.pdfTitle, this.pdfCreator,
				this.pdfSubject, this.pdfFooterImagePath);
		LOG.info("Generate from " + size + " worksheets done");
		try {
			List<File> attachments = Lists.newArrayList();
			attachments.add(new File(targetFilePath));

			LOG.info("Sending mail to : " + emailTo + ", subject : " + this.emailSubject);
			MimeMessage email = mailUtil.createEmailWithAttachment(this.emailTo, GMAIL_USER, this.emailSubject,
					this.emailBody, attachments);
			gmailSender.sendGmailMessage(email);
			LOG.info("Mail sent to " + emailTo);

			if (deleteTempPdfFile) {
				Files.deleteIfExists(FileSystems.getDefault().getPath(targetFilePath));
			}
		} catch (MessagingException | IOException e) {
			LOG.error("Send mail to " + emailTo + " failed : " + e.getMessage());
		}
	}

	public void setDeleteTempPdfFile(boolean deleteTempPdfFile) {
		this.deleteTempPdfFile = deleteTempPdfFile;
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

	public void setPdfCreator(String pdfCreator) {
		this.pdfCreator = pdfCreator;
	}

	public void setPdfFooterImagePath(String pdfFooterImagePath) {
		this.pdfFooterImagePath = pdfFooterImagePath;
	}

	public void setPdfSubject(String pdfSubject) {
		this.pdfSubject = pdfSubject;
	}

	public void setPdfTitle(String pdfTitle) {
		this.pdfTitle = pdfTitle;
	}

	public void setRootWorksheetFolderPath(String rootWorksheetFolderPath) {
		this.rootWorksheetFolderPath = rootWorksheetFolderPath;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setTargetFilePath(String targetFilePath) {
		if (!targetFilePath.toLowerCase().endsWith(".pdf")) {
			targetFilePath = targetFilePath.concat(".pdf");
		}

		this.targetFilePath = targetFilePath;
	}

}
