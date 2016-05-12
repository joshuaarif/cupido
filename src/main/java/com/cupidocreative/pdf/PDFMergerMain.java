package com.cupidocreative.pdf;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.order.PurchaseOrderProcessorTask;
import com.cupidocreative.order.XlsxReader;

public class PDFMergerMain {
	private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	private static final String ROOT_WORKSHEET_FOLDER_ADD = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
	private static final String ROOT_WORKSHEET_FOLDER_SUB = "D:/Personal/Dropbox/Cupido/Education.com/Worksheet/Generator/Subtraction/1-20/worksheet";
	private static final String TEMP_DIR = "D:/Personal/Cupido/PO";

	public static void main(String[] args) {
		// begin init
		XlsxReader xlsxReader = new XlsxReader();
		Set<PurchaseOrderHdr> orders = xlsxReader
				.readOrderFromExcel("D:/Personal/Dropbox/Cupido/Education.com/po_list.xlsx");

		// Set<PurchaseOrderHdr> orders = Sets.newLinkedHashSet();
		//
		// for (int i = 0; i < 20; i++) {
		// PurchaseOrderHdr order = new PurchaseOrderHdr();
		// order.setEmail((i % 2 == 0 ? "timotius.pamungkas@gmail.com" :
		// "timotius_pamungkas@yahoo.com"));
		// order.setPoNumber(Integer.toHexString(ThreadLocalRandom.current().nextInt(2000)).toUpperCase());
		//
		// for (int j = 0; j < 2; j++) {
		// PurchaseOrderDtl orderDtl = new PurchaseOrderDtl();
		//
		// orderDtl.setWorkbookCode((i % 2 == 0 ? "ADDITION" : "SUBTRACTION"));
		// orderDtl.setWorkbookSize(1);
		//
		// order.getPoDetails().add(orderDtl);
		// }
		//
		// orders.add(order);
		// }

		File tempDir = new File(TEMP_DIR);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		ExecutorService executorService = Executors.newFixedThreadPool(3);

		// start create from order
		for (PurchaseOrderHdr o : orders) {
			PurchaseOrderProcessorTask task = new PurchaseOrderProcessorTask(o);
			executorService.submit(task);
		}

		executorService.shutdown();
	}

}
