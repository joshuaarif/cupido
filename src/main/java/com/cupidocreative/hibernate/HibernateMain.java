package com.cupidocreative.hibernate;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.order.XlsxReader;
import com.google.common.collect.Sets;

public class HibernateMain {

	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

//		Set<PurchaseOrderHdr> orders = Sets.newLinkedHashSet();
//
//		for (int i = 0; i < 20; i++) {
//			PurchaseOrderHdr order = new PurchaseOrderHdr();
//			order.setEmail((i % 2 == 0 ? "timotius.pamungkas@gmail.com" : "timotius_pamungkas@yahoo.com"));
//			order.setPoNumber(Integer.toHexString(ThreadLocalRandom.current().nextInt(2000)).toUpperCase());
//
//			for (int j = 0; j < 2; j++) {
//				PurchaseOrderDtl orderDtl = new PurchaseOrderDtl();
//				orderDtl.setPoHeader(order);
//
//				orderDtl.setWorkbookCode((i % 2 == 0 ? "ADDITION" : "SUBTRACTION"));
//				orderDtl.setWorkbookSize(1);
//
//				order.getPoDetails().add(orderDtl);
//			}
//
//			orders.add(order);
//		}

		XlsxReader xlsxReader = new XlsxReader();
		Set<PurchaseOrderHdr> orders = xlsxReader.readOrderFromExcel("D:/Personal/Dropbox/Cupido/Education.com/po_list.xlsx");
		
		orders.forEach(o -> {
			session.save(o);

			o.getPoDetails().forEach(oDtl -> session.save(oDtl));
		});

		t.commit();
		session.close();

		System.out.println("Saved");
		HibernateUtil.close();
	}

}
