package com.cupidocreative.main;

import java.util.Calendar;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cupidocreative.common.PaymentStatus;
import com.cupidocreative.common.ProcessStatus;
import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.hibernate.dao.PurchaseOrderDAO;
import com.cupidocreative.hibernate.domain.PurchaseOrderDtl;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.cupidocreative.order.XlsxReader;

public class HibernateInjectorMain {

	public static void main(String[] args) {
		XlsxReader xlsxReader = new XlsxReader();
		Set<PurchaseOrderHdr> orders = xlsxReader
				.readOrderFromExcel("D:/Personal/Dropbox/Cupido/Education/po_list.xlsx");

		for (PurchaseOrderHdr o : orders) {
			o.setCreationDate(Calendar.getInstance().getTime());
			o.setLastUpdateDate(Calendar.getInstance().getTime());
			o.setPayment_status(PaymentStatus.PAID.getValue());
			o.setProcessStatus(ProcessStatus.NEW.getValue());

			for (PurchaseOrderDtl oDtl : o.getPoDetails()) {
				oDtl.setCreationDate(Calendar.getInstance().getTime());
				oDtl.setLastUpdateDate(Calendar.getInstance().getTime());
			}
		}

		PurchaseOrderDAO poDAO = new PurchaseOrderDAO();
		orders.forEach(order -> poDAO.save(order));

		System.out.println("Saved");
		HibernateUtil.closeSessionFactory();
		;
	}

}
