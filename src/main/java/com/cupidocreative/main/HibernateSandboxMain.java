package com.cupidocreative.main;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.domain.PurchaseOrderNumber;
import com.cupidocreative.hibernate.HibernateUtil;
import com.google.common.collect.Sets;

public class HibernateSandboxMain {

	public static void main(String[] args) {
		Set<PurchaseOrderHdr> orders = Sets.newLinkedHashSet();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Session sessionPoNumber = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

		for (int i = 0; i < 2000; i++) {
			// get PO number
			PurchaseOrderNumber poHeaderNumber = null;
			StringBuilder sb = new StringBuilder(" from PurchaseOrderNumber where year = ")
					.append(Calendar.getInstance().get(Calendar.YEAR));
			List result = sessionPoNumber.createQuery(sb.toString()).list();

			if (!result.isEmpty()) {
				poHeaderNumber = (PurchaseOrderNumber) result.get(0);
				poHeaderNumber.setSequence(poHeaderNumber.getSequence() + 1);
				sessionPoNumber.update(poHeaderNumber);
			} else {
				// no data for current year
				poHeaderNumber = new PurchaseOrderNumber();
				poHeaderNumber.setYear(Calendar.getInstance().get(Calendar.YEAR));
				poHeaderNumber.setSequence(1);
				sessionPoNumber.save(poHeaderNumber);
			}

			sessionPoNumber.flush();
			
			PurchaseOrderHdr order = new PurchaseOrderHdr();
			order.setEmail((i % 2 == 0 ? "xxx@gmail.com" : "yyy@yahoo.com"));
			order.setPoNumber("PO" + poHeaderNumber.getSequence());

			for (int j = 0; j < 2000; j++) {
				PurchaseOrderDtl orderDtl = new PurchaseOrderDtl();
				orderDtl.setPoHeader(order);

				orderDtl.setWorkbookCode((i % 2 == 0 ? "CODE_A" : "CODE_B"));
				orderDtl.setWorkbookSize(1);

				order.getPoDetails().add(orderDtl);
			}

			orders.add(order);

			System.out.println(poHeaderNumber);
			session.flush();
		}

		t.commit();
		session.close();
		sessionPoNumber.close();

		HibernateUtil.close();
	}

}
