package com.cupidocreative.order;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.mail.internet.MimeMessage;

import com.cupidocreative.common.TaskStatus;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.cupidocreative.mail.GmailSender;
import com.cupidocreative.util.MailUtil;
import com.cupidocreative.util.StringTemplateUtil;
import com.google.common.collect.Maps;

/**
 * Kirim konfirmasi pembelian dan perintah pembayaran ke email user
 * 
 * @author timpamungkas
 *
 */
public class PurchaseOrderAcceptedTask implements Callable<TaskStatus>, Serializable {

	private static final long serialVersionUID = -1309246763163704133L;

	private PurchaseOrderHdr poHeader;

	public PurchaseOrderAcceptedTask(PurchaseOrderHdr poHeader) {
		super();
		this.poHeader = poHeader;
	}

	@Override
	public TaskStatus call() throws Exception {
		final MailUtil mailUtil = new MailUtil();
		final GmailSender gmailSender = new GmailSender();
		Map<String, String> stValues = Maps.newHashMap();
		String emailSubject = StringTemplateUtil.createFromST("mail/email_subject.txt",
				StringTemplateUtil.DEFAULT_DELIMITER, stValues);
		stValues.clear();
		stValues.put("name", poHeader.getEmail().split("@")[0]);
		String emailBody = StringTemplateUtil.createFromST("mail/email_body.html", StringTemplateUtil.DEFAULT_DELIMITER,
				stValues);

		MimeMessage email = mailUtil.createEmail(poHeader.getEmail(), GmailSender.GMAIL_USER, emailSubject, emailBody);
		gmailSender.sendGmailMessage(email);

		return TaskStatus.SUCCESS;
	}

	public PurchaseOrderHdr getPoHeader() {
		return poHeader;
	}

	public void setPoHeader(PurchaseOrderHdr poHeader) {
		this.poHeader = poHeader;
	}

}
