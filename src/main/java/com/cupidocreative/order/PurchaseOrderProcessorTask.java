package com.cupidocreative.order;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cupidocreative.common.ProcessStatus;
import com.cupidocreative.common.TaskStatus;
import com.cupidocreative.hibernate.dao.PurchaseOrderDAO;
import com.cupidocreative.hibernate.domain.PurchaseOrderDtl;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.cupidocreative.mail.GmailSender;
import com.cupidocreative.pdf.PDFWorkbookGenerator;
import com.cupidocreative.util.MailUtil;
import com.cupidocreative.util.StringTemplateUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Proses PO dari excel.
 * <ol>
 * <li>Generate PDF dari PO</li>
 * <li>Update status PO (header) & last update date (hdr & dtl)</li>
 * <li>Send PO via email</li>
 * </ol>
 * 
 * @author 13748
 */
public class PurchaseOrderProcessorTask implements Callable<TaskStatus>, Serializable {

	private static final Log LOG = LogFactory.getLog(PurchaseOrderProcessorTask.class);

	private static final File TEMP_DIR = new File("D:/Personal/Cupido/PO");
	private static final String ROOT_WORKSHEET_FOLDER_ADD = "D:/Personal/Dropbox/Cupido/Education/Worksheet/Generator/Addition/1-10/worksheet";
	private static final String ROOT_WORKSHEET_FOLDER_SUB = "D:/Personal/Dropbox/Cupido/Education/Worksheet/Generator/Subtraction/1-20/worksheet";
	private static final String FOOTER_IMAGE = "D:/Personal/Cupido/Footer.png";

	private static final String PDF_TITLE = "Cupido Creative";
	private static final String PDF_CREATOR = "www.cupidocreative.com";
	private static final String PDF_SUBJECT = "Buku Latihan Cupido Creative, lihat lengkapnya di www.cupidocreative.com";

	private static final long serialVersionUID = 1892887526478255535L;

	/**
	 * Max attachment size in one gmail, 15 MB
	 */
	private static final long MAX_ATTACHMENT_SIZE = 15728640;

	private PurchaseOrderHdr poHeader;
	private PDFWorkbookGenerator pdfGenerator;
	private boolean generatePdf;
	private boolean sendMail;

	static {
		if (!TEMP_DIR.exists()) {
			TEMP_DIR.mkdirs();
		}
	}

	/**
	 * 
	 * @param poHeader
	 * @param generatePdf
	 * @param sendMail
	 *            if true & mail succesfully sent, will delete temporary files
	 */
	public PurchaseOrderProcessorTask(PurchaseOrderHdr poHeader, boolean generatePdf, boolean sendMail) {
		super();

		if (!(generatePdf || sendMail)) {
			throw new IllegalArgumentException("Must generatePdf, sendMail, or both");
		}

		this.poHeader = poHeader;
		this.pdfGenerator = new PDFWorkbookGenerator();
		this.generatePdf = generatePdf;
		this.sendMail = sendMail;
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
		PurchaseOrderDAO poDAO = new PurchaseOrderDAO();

		// generate for each order detail
		if (generatePdf) {
			for (PurchaseOrderDtl orderDtl : poHeader.getPoDetails()) {
				rootWorksheetFolderPath = orderDtl.getWorkbookCode().equals("ADDITION") ? ROOT_WORKSHEET_FOLDER_ADD
						: ROOT_WORKSHEET_FOLDER_SUB;
				String pdfFilename = new StringBuilder().append(poHeader.getPoNumber()).append("_")
						.append(ThreadLocalRandom.current().nextInt(1000, 9999)).append(".pdf").toString();
				targetFile = new StringBuilder(TEMP_DIR.getPath()).append(File.separatorChar).append(pdfFilename)
						.toString();

				size = orderDtl.getWorkbookSize();
				tempFiles.add(new File(targetFile));

				pdfGenerator.generate(rootWorksheetFolderPath, targetFile, size, PDF_TITLE, PDF_CREATOR, PDF_SUBJECT,
						FOOTER_IMAGE);

				orderDtl.setLastUpdateDate(Calendar.getInstance().getTime());
				orderDtl.setPdfFilename(pdfFilename);
				poDAO.update(orderDtl);
			}

			poHeader.setProcessStatus(ProcessStatus.GENERATED.getValue());
			poDAO.update(poHeader);
		}

		// send mail for each order (single/multiple attachments based on total
		// attachment size)
		if (sendMail) {
			try {
				long totalSize = 0;
				MimeMessage email;
				Map<String, String> stValues = Maps.newHashMap();

				// mail subject
				stValues.put("poNumber", poHeader.getPoNumber());
				String emailSubject = StringTemplateUtil.createFromST("mail/email_subject.txt",
						StringTemplateUtil.DEFAULT_DELIMITER, stValues);

				// mail body
				stValues.clear();
				stValues.put("name", poHeader.getEmail().split("@")[0]);
				String emailBody = StringTemplateUtil.createFromST("mail/email_body.html",
						StringTemplateUtil.DEFAULT_DELIMITER, stValues);

				for (File f : tempFiles) {
					totalSize += f.length();
				}

				if (totalSize < MAX_ATTACHMENT_SIZE) {
					LOG.info("Sending single mail to : " + poHeader.getEmail() + ", PO : " + poHeader.getPoNumber());
					email = mailUtil.createEmailWithAttachment(poHeader.getEmail(), GmailSender.GMAIL_USER,
							emailSubject, emailBody, tempFiles);
					gmailSender.sendGmailMessage(email);

					for (File attachment : tempFiles) {
						Files.deleteIfExists(FileSystems.getDefault().getPath(attachment.getPath()));
					}
				} else {
					LOG.info("Sending multiple mails to : " + poHeader.getEmail() + ", PO : " + poHeader.getPoNumber());

					for (File attachment : tempFiles) {
						email = mailUtil.createEmailWithAttachment(poHeader.getEmail(), GmailSender.GMAIL_USER,
								emailSubject, emailBody, attachment);
						gmailSender.sendGmailMessage(email);

						Files.deleteIfExists(FileSystems.getDefault().getPath(attachment.getPath()));
					}
				}
				poHeader.setProcessStatus(ProcessStatus.COMPLETE.getValue());
				poDAO.update(poHeader);

				LOG.info("Mail sent to " + poHeader.getEmail());
			} catch (MessagingException | IOException e) {
				LOG.error("Send mail to " + poHeader.getEmail() + ", PO : " + poHeader.getPoNumber() + " failed : "
						+ e.getMessage());
				return TaskStatus.FAILED;
			}
		}

		return TaskStatus.SUCCESS;
	}

}
