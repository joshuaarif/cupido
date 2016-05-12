package com.cupidocreative.order;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.Log;

import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.mail.GmailSender;
import com.cupidocreative.pdf.PDFWorkbookGenerator;
import com.cupidocreative.util.MailUtil;
import com.google.common.collect.Sets;

public class PurchaseOrderProcessorTask implements Callable<String>, Serializable {

	private static final Log LOG = LogFactory.getLog(PurchaseOrderProcessorTask.class);

	private static final File TEMP_DIR = new File("D:/Personal/Cupido/PO");
	private static final String ROOT_WORKSHEET_FOLDER_ADD = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
	private static final String ROOT_WORKSHEET_FOLDER_SUB = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Subtraction/1-20/worksheet";

	private static final String PDF_TITLE = "Cupido Creative";
	private static final String PDF_CREATOR = "www.cupidocreative.com";
	private static final String PDF_SUBJECT = "Buku Latihan Cupido Creative, lihat lengkapnya di www.cupidocreative.com";

	private static final long serialVersionUID = 1892887526478255535L;
	private static final String GMAIL_USER = "me";

	private PurchaseOrderHdr poHeader;
	private PDFWorkbookGenerator pdfGenerator;

	static {
		if (!TEMP_DIR.exists()) {
			TEMP_DIR.mkdirs();
		}
	}

	public PurchaseOrderProcessorTask(PurchaseOrderHdr poHeader) {
		super();
		this.poHeader = poHeader;
		this.pdfGenerator = new PDFWorkbookGenerator();
	}

	@Override
	public String call() throws Exception {
		final MailUtil mailUtil = new MailUtil();
		final GmailSender gmailSender = new GmailSender();
		String rootWorksheetFolderPath;
		String targetFile;
		int size;
		Set<File> tempFiles = Sets.newLinkedHashSetWithExpectedSize(poHeader.getPoDetails().size());

		// generate for each order detail
		for (PurchaseOrderDtl orderDtl : poHeader.getPoDetails()) {
			rootWorksheetFolderPath = orderDtl.getWorkbookCode().equals("ADDITION") ? ROOT_WORKSHEET_FOLDER_ADD
					: ROOT_WORKSHEET_FOLDER_SUB;
			targetFile = new StringBuilder(TEMP_DIR.getPath()).append(File.separatorChar).append(poHeader.getPoNumber())
					.append("_").append(ThreadLocalRandom.current().nextInt()).append(".pdf").toString();

			size = orderDtl.getWorkbookSize();
			tempFiles.add(new File(targetFile));

			pdfGenerator.generate(rootWorksheetFolderPath, targetFile, size, PDF_TITLE, PDF_CREATOR, PDF_SUBJECT, null);
		}

		// send mail for each order (multiple attachments)
		try {
			LOG.info("Sending mail to : " + poHeader.getEmail() + ", subject : " + poHeader.getPoNumber());
			MimeMessage email = mailUtil.createEmailWithAttachments(poHeader.getEmail(), GMAIL_USER,
					"PO : " + poHeader.getPoNumber() + ThreadLocalRandom.current().nextInt(), "emailBody", tempFiles);
			gmailSender.sendGmailMessage(GmailSender.getGmailService(), GMAIL_USER, email);
			LOG.info("Mail sent to " + poHeader.getEmail());

			// Files.deleteIfExists(FileSystems.getDefault().getPath(targetFile));
		} catch (MessagingException | IOException e) {
			LOG.error("Send mail to " + poHeader.getEmail() + " failed : " + e.getMessage());
		}
		return null;
	}

}