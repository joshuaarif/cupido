package pdf;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import mail.GmailSender;

public class PDFMergerMain {
	// private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	public static void main(String[] args) {
		PDFWorkbookGenerator generator = new PDFWorkbookGenerator();
		GmailSender gmailSender = new GmailSender();
		String rootWorksheetFolder = "D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
		String targetFile = "D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/1-10/Test.pdf";

		generator.generate(rootWorksheetFolder, targetFile, 3);
		try {
			MimeMessage email = gmailSender.createEmailWithAttachment("timotius.pamungkas@gmail.com", "me",
					"Test Attachment", "Test Attachment body", new File(targetFile));
			gmailSender.sendMessage(GmailSender.getGmailService(), "me", email);
		} catch (MessagingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
