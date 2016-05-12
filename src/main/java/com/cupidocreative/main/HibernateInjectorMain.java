package com.cupidocreative.main;

import java.util.Calendar;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cupidocreative.common.PaymentStatus;
import com.cupidocreative.common.ProcessStatus;
import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.order.XlsxReader;

public class HibernateInjectorMain {

	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

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
		// orderDtl.setPoHeader(order);
		//
		// orderDtl.setWorkbookCode((i % 2 == 0 ? "ADDITION" : "SUBTRACTION"));
		// orderDtl.setWorkbookSize(1);
		//
		// order.getPoDetails().add(orderDtl);
		// }
		//
		// orders.add(order);
		// }

		XlsxReader xlsxReader = new XlsxReader();
		Set<PurchaseOrderHdr> orders = xlsxReader
				.readOrderFromExcel("D:/Personal/Dropbox/Cupido/Education/po_list.xlsx");

		for (PurchaseOrderHdr o : orders) {
			o.setCreationDate(Calendar.getInstance().getTime());
			o.setLastUpdateDate(Calendar.getInstance().getTime());
			o.setPayment_status(PaymentStatus.PAID.getValue());
			o.setProcessStatus(ProcessStatus.NEW.getValue());
			session.save(o);

			for (PurchaseOrderDtl oDtl : o.getPoDetails()) {
				oDtl.setCreationDate(Calendar.getInstance().getTime());
				oDtl.setLastUpdateDate(Calendar.getInstance().getTime());
				session.save(oDtl);
			}
		}

		// List<PurchaseOrderHdr> list = (List<PurchaseOrderHdr>)
		// session.createQuery("from PurchaseOrderHdr").list();
		// System.out.println(list.size());
		//
		// for (PurchaseOrderHdr o : list) {
		// System.out.println(o);
		// }

		t.commit();
		session.close();

		System.out.println("Saved");
		HibernateUtil.close();
	}

}
