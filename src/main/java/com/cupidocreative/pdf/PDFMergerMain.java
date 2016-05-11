package com.cupidocreative.pdf;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cupidocreative.domain.CCPurchaseOrder;
import com.cupidocreative.util.StringTemplateUtil;
import com.google.api.client.util.Maps;
import com.google.common.collect.Sets;

public class PDFMergerMain {
	private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	private static final String ROOT_WORKSHEET_FOLDER_ADD = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
	private static final String ROOT_WORKSHEET_FOLDER_SUB = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Subtraction/1-20/worksheet";
	private static final String TEMP_DIR = "D:/Personal/Cupido/PO";

	public static void main(String[] args) {
		// begin init
		Set<CCPurchaseOrder> orders = Sets.newLinkedHashSet();

		for (int i = 0; i < 20; i++) {
			CCPurchaseOrder order = new CCPurchaseOrder();
			order.setEmail((i % 2 == 0 ? "timotius.pamungkas@gmail.com" : "timotius_pamungkas@yahoo.com"));
			order.setPoNumber(Integer.toHexString(ThreadLocalRandom.current().nextInt(2000)).toUpperCase());
			order.setWorkbookCode((i % 2 == 0 ? "ADDITION" : "SUBTRACTION"));
			order.setWorkbookSize(1);

			orders.add(order);
		}

		File tempDir = new File(TEMP_DIR);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		ExecutorService executorService = Executors.newFixedThreadPool(3);
		
		// start create from order
		orders.forEach(o -> {
			String rootWorksheetFolderPath = o.getWorkbookCode().equals("ADDITION") ? ROOT_WORKSHEET_FOLDER_ADD
					: ROOT_WORKSHEET_FOLDER_SUB;
			String targetFilePath = TEMP_DIR + "/" + o.getPoNumber();
			int size = o.getWorkbookSize();
			String emailTo = o.getEmail();
			String emailSubject = "Order " + o.getPoNumber();
			String emailBody = "Email body";

			Map<String, String> values = Maps.newHashMap();
			values.put("name", o.getEmail());
			
			try {
				emailBody = StringTemplateUtil.createFromST("email_body.html", '$', values);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PDFWorkbookGeneratorTask task = new PDFWorkbookGeneratorTask(rootWorksheetFolderPath, targetFilePath, size,
					emailTo, emailSubject, emailBody);
			task.setDeleteTempPdfFile(true);
			
			executorService.submit(task);
		});
		
		executorService.shutdown();
	}

}
