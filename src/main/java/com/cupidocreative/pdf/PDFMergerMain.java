package com.cupidocreative.pdf;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cupidocreative.util.StringTemplateUtil;
import com.google.common.collect.Maps;

public class PDFMergerMain {
	private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	public static void main(String[] args) {
		String rootWorksheetFolder1 = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
		String rootWorksheetFolder2 = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Subtraction/1-20/worksheet";
		String targetFile1 = "D:/Personal/Cupido/Addition footer1.pdf";
		String targetFile2 = "D:/Personal/Cupido/Subtraction footer2.pdf";

		String emailTo = "timotius.pamungkas@gmail.com";
		String emailSubject = "Email subject ";
		String emailBody = "Email body";

		Map<String, String> values = Maps.newHashMap();

		values.put("name", "Timotius");

		try {
			emailBody = StringTemplateUtil.createFromST("email_body.html", '$', values);
			LOG.debug(emailBody);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int size = 3;

		PDFWorkbookGeneratorTask task1 = new PDFWorkbookGeneratorTask(rootWorksheetFolder1, targetFile1, size, emailTo,
				emailSubject + ThreadLocalRandom.current().nextInt(), emailBody);
		PDFWorkbookGeneratorTask task2 = new PDFWorkbookGeneratorTask(rootWorksheetFolder2, targetFile2, size, emailTo,
				emailSubject + ThreadLocalRandom.current().nextInt(), emailBody);

		task1.setPdfFooterImagePath("D:/Personal/Cupido/footer.png");
		task2.setPdfFooterImagePath("D:/Personal/Cupido/footer2.png");

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		executorService.submit(task1);
		executorService.submit(task2);

		executorService.shutdown();
	}

}
