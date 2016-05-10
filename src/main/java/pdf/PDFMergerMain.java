package pdf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class PDFMergerMain {
	// private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	public static void main(String[] args) {
		String rootWorksheetFolder1 = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
		String rootWorksheetFolder2 = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Subtraction/1-20/worksheet";
		String targetFile1 = "D:/Personal/Cupido/Addition.pdf";
		String targetFile2 = "D:/Personal/Cupido/Subtraction.pdf";

		String emailTo = "timotius.pamungkas@gmail.com";
		String emailSubject = "Email subject ";
		String emailBody = "Email body";
		int size = 3;

		PDFWorkbookGeneratorTask task1 = new PDFWorkbookGeneratorTask(rootWorksheetFolder1, targetFile1, size, emailTo,
				emailSubject + ThreadLocalRandom.current().nextInt(), emailBody);
		PDFWorkbookGeneratorTask task2 = new PDFWorkbookGeneratorTask(rootWorksheetFolder2, targetFile2, size, emailTo,
				emailSubject + ThreadLocalRandom.current().nextInt(), emailBody);

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		executorService.submit(task1);
		executorService.submit(task2);
		
		executorService.shutdown();
	}

}
