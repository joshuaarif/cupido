package pdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.compiler.CompiledST;

import com.google.common.collect.Maps;

import mail.EmailUtil;

public class PDFMergerMain {
	// private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	public static void main(String[] args) {
		String rootWorksheetFolder1 = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
		String rootWorksheetFolder2 = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Subtraction/1-20/worksheet";
		String targetFile1 = "D:/Personal/Cupido/Addition.pdf";
		String targetFile2 = "D:/Personal/Cupido/Subtraction.pdf";

		String emailTo = "timotius.pamungkas@gmail.com";
		String emailSubject = "Email subject ";
		String emailBody = "<strong>Email body</strong>";
		int size = 3;

		PDFWorkbookGeneratorTask task1 = new PDFWorkbookGeneratorTask(rootWorksheetFolder1, targetFile1, size, emailTo,
				emailSubject + ThreadLocalRandom.current().nextInt(), emailBody);
		PDFWorkbookGeneratorTask task2 = new PDFWorkbookGeneratorTask(rootWorksheetFolder2, targetFile2, size, emailTo,
				emailSubject + ThreadLocalRandom.current().nextInt(), emailBody);

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		// executorService.submit(task1);
		// executorService.submit(task2);

		executorService.shutdown();

		EmailUtil emailUtil = new EmailUtil();
		Map<String, String> emailValues = Maps.newHashMap();
		emailValues.put("name", "Timotius");

		try {
			String emailBody2 = emailUtil.createEmailBody("email_body.html", '$', emailValues);
			System.out.println(emailBody2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
