package com.cupidocreative.main;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;

import com.cupidocreative.domain.PurchaseOrderNumber;
import com.cupidocreative.hibernate.HibernateUtil;

public class HibernateSandboxMain {

	public static void main(String[] args) {
		// get PO number
		PurchaseOrderNumber poHeaderNumber = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		StringBuilder sb = new StringBuilder(" from PurchaseOrderNumber where year = ")
				.append(Calendar.getInstance().get(Calendar.YEAR));
		List result = session.createQuery(sb.toString()).list();

		if (!result.isEmpty()) {
			poHeaderNumber = (PurchaseOrderNumber) result.get(0);
		}

		session.flush();

		// belum ada datanya
		if (poHeaderNumber == null) {
			poHeaderNumber = new PurchaseOrderNumber();
			poHeaderNumber.setYear(Calendar.getInstance().get(Calendar.YEAR));
			poHeaderNumber.setSequence(1);
			session.save(poHeaderNumber);
		}

		session.flush();

		System.out.println(poHeaderNumber);

		HibernateUtil.close();
	}

}
