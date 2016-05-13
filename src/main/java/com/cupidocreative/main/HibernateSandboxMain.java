package com.cupidocreative.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

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

			System.out.println(poHeaderNumber);

			PurchaseOrderHdr order = new PurchaseOrderHdr();
			order.setEmail((i % 2 == 0 ? "xxx@gmail.com" : "yyy@yahoo.com"));
			order.setPoNumber("PO" + poHeaderNumber.getSequence());

			for (int j = 0; j < 20; j++) {
				PurchaseOrderDtl orderDtl = new PurchaseOrderDtl();
				orderDtl.setPoHeader(order);

				orderDtl.setWorkbookCode((i % 2 == 0 ? "CODE_A" : "CODE_B"));
				orderDtl.setWorkbookSize(1);

				order.getPoDetails().add(orderDtl);
			}

			orders.add(order);

			order.setCreatedBy(-1);
			order.setCreationDate(new Date());
			order.setLastUpdateDate(new Date());
			order.setLastUpdatedBy(-1);
			session.save(order);
			session.flush();
		}

		session.close();
		sessionPoNumber.close();

		HibernateUtil.close();
	}

}
