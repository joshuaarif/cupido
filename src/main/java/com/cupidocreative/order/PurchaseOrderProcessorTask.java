package com.cupidocreative.order;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cupidocreative.common.ProcessStatus;
import com.cupidocreative.common.TaskStatus;
import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.mail.GmailSender;
import com.cupidocreative.pdf.PDFWorkbookGenerator;
import com.cupidocreative.util.MailUtil;
import com.google.common.collect.Sets;

public class PurchaseOrderProcessorTask implements Callable<TaskStatus>, Serializable {

	private static final Log LOG = LogFactory.getLog(PurchaseOrderProcessorTask.class);

	private static final File TEMP_DIR = new File("D:/Personal/Cupido/PO");
	private static final String ROOT_WORKSHEET_FOLDER_ADD = "D:/Personal/Dropbox/Cupido/Education/Worksheet/Generator/Addition/1-10/worksheet";
	private static final String ROOT_WORKSHEET_FOLDER_SUB = "D:/Personal/Dropbox/Cupido/Education/Worksheet/Generator/Subtraction/1-20/worksheet";

	private static final String PDF_TITLE = "Cupido Creative";
	private static final String PDF_CREATOR = "www.cupidocreative.com";
	private static final String PDF_SUBJECT = "Buku Latihan Cupido Creative, lihat lengkapnya di www.cupidocreative.com";

	private static final long serialVersionUID = 1892887526478255535L;
	private static final String GMAIL_USER = "me";

	/**
	 * Max attachment size in one gmail, 15 MB
	 */
	private static final long MAX_ATTACHMENT_SIZE = 15728640;

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
		// somehow ini harus ada kalau fetch type PoHeader nya LAZY, kaya ga
		// dikenalin
		// gitu fetch nya, harus EAGER kalau ga ada ini
		LOG.debug(poHeader.getPoDetails());
	}

	@Override
	public TaskStatus call() throws Exception {
		final MailUtil mailUtil = new MailUtil();
		final GmailSender gmailSender = new GmailSender();
		String rootWorksheetFolderPath;
		String targetFile;
		int size;
		Set<File> tempFiles = Sets.newLinkedHashSetWithExpectedSize(poHeader.getPoDetails().size());
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

		// generate for each order detail
		for (PurchaseOrderDtl orderDtl : poHeader.getPoDetails()) {
			rootWorksheetFolderPath = orderDtl.getWorkbookCode().equals("ADDITION") ? ROOT_WORKSHEET_FOLDER_ADD
					: ROOT_WORKSHEET_FOLDER_SUB;
			String pdfFilename = new StringBuilder().append(poHeader.getPoNumber()).append("_")
					.append(ThreadLocalRandom.current().nextInt(1000, 9999)).append(".pdf").toString();
			targetFile = new StringBuilder(TEMP_DIR.getPath()).append(File.separatorChar).append(pdfFilename)
					.toString();

			size = orderDtl.getWorkbookSize();
			tempFiles.add(new File(targetFile));

			pdfGenerator.generate(rootWorksheetFolderPath, targetFile, size, PDF_TITLE, PDF_CREATOR, PDF_SUBJECT, null);

			orderDtl.setLastUpdateDate(Calendar.getInstance().getTime());
			orderDtl.setPdfFilename(pdfFilename);
			session.update(orderDtl);
		}

		poHeader.setProcessStatus(ProcessStatus.GENERATED.getValue());
		session.update(poHeader);

		t.commit();
		session.flush();

		// send mail for each order (single/multiple attachments based on total
		// attachment size)
		try {
			long totalSize = 0;
			MimeMessage email;

			for (File f : tempFiles) {
				totalSize += f.length();
			}

			if (totalSize < MAX_ATTACHMENT_SIZE) {
				LOG.info("Sending single mail to : " + poHeader.getEmail() + ", PO : " + poHeader.getPoNumber());
				email = mailUtil.createEmailWithAttachment(poHeader.getEmail(), GMAIL_USER,
						"PO : " + poHeader.getPoNumber() + ThreadLocalRandom.current().nextInt(), "emailBody",
						tempFiles);
				gmailSender.sendGmailMessage(GmailSender.getGmailService(), GMAIL_USER, email);

				for (File attachment : tempFiles) {
					Files.deleteIfExists(FileSystems.getDefault().getPath(attachment.getPath()));
				}
			} else {
				LOG.info("Sending multiple mails to : " + poHeader.getEmail() + ", PO : " + poHeader.getPoNumber());

				for (File attachment : tempFiles) {
					email = mailUtil.createEmailWithAttachment(poHeader.getEmail(), GMAIL_USER,
							"PO : " + poHeader.getPoNumber() + ThreadLocalRandom.current().nextInt(), "emailBody",
							attachment);
					gmailSender.sendGmailMessage(GmailSender.getGmailService(), GMAIL_USER, email);

					Files.deleteIfExists(FileSystems.getDefault().getPath(attachment.getPath()));
				}
			}
			poHeader.setProcessStatus(ProcessStatus.COMPLETE.getValue());
			session.update(poHeader);

			session.flush();

			LOG.info("Mail sent to " + poHeader.getEmail());
		} catch (MessagingException | IOException e) {
			LOG.error("Send mail to " + poHeader.getEmail() + " failed : " + e.getMessage());
			return TaskStatus.FAILED;
		} finally {
			session.close();
		}

		return TaskStatus.SUCCESS;
	}

}
