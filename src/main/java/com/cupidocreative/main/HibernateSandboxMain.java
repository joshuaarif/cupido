package com.cupidocreative.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.cupidocreative.dao.PurchaseOrderDAO;
import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.domain.PurchaseOrderNumber;
import com.cupidocreative.hibernate.HibernateUtil;
import com.google.common.collect.Sets;

public class HibernateSandboxMain {

	public static void main(String[] args) {
		Set<PurchaseOrderHdr> orders = Sets.newLinkedHashSet();
		PurchaseOrderDAO poDAO = new PurchaseOrderDAO();

		for (int i = 0; i < 3; i++) {
			// get PO number
			PurchaseOrderNumber poHeaderNumber = poDAO.getPoNumber();

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

			poDAO.save(order);
		}
		
		HibernateUtil.closeSessionFactory();
	}

}
