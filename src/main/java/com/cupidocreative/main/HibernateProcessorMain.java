package com.cupidocreative.main;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.cupidocreative.order.PurchaseOrderProcessorTask;

public class HibernateProcessorMain {
	private static final Log LOG = LogFactory.getLog(HibernateProcessorMain.class);

	private static final String ROOT_WORKSHEET_FOLDER_ADD = "D:/Personal/Dropbox/Cupido/Education/Worksheet/Generator/Addition/1-10/worksheet";
	private static final String ROOT_WORKSHEET_FOLDER_SUB = "D:/Personal/Dropbox/Cupido/Education/Worksheet/Generator/Subtraction/1-20/worksheet";
	private static final String TEMP_DIR = "D:/Personal/Cupido/PO";

	public static void main(String[] args) {
		// begin init
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<PurchaseOrderHdr> orders = (List<PurchaseOrderHdr>) session.createQuery("from PurchaseOrderHdr").list();

		File tempDir = new File(TEMP_DIR);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		ExecutorService executorService = Executors.newFixedThreadPool(3);

		// start create from order
		for (PurchaseOrderHdr o : orders) {
			LOG.info("Processing : " + o.getPoNumber());
			PurchaseOrderProcessorTask task = new PurchaseOrderProcessorTask(o, true, true);
			executorService.submit(task);
		}

		executorService.shutdown();
		session.close();
	}

}
