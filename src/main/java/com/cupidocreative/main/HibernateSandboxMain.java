package com.cupidocreative.main;

import java.util.List;
import java.util.Set;

import com.cupidocreative.common.ProcessStatus;
import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.hibernate.dao.PurchaseOrderDAO;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.google.common.collect.Sets;

public class HibernateSandboxMain {

	public static void main(String[] args) {
		PurchaseOrderDAO poDAO = new PurchaseOrderDAO();
		Set<String> processStatus = Sets.newHashSet();
		Set<String> paymentStatus = Sets.newHashSet();
		
		processStatus.add(ProcessStatus.NEW.getValue());

		List<PurchaseOrderHdr> hdrs = poDAO.findPoHeaders("timotius.pamungkas@gmail.com", null, paymentStatus,
				processStatus);

		hdrs.forEach(hdr -> System.out.println(hdr.getId()));

		HibernateUtil.closeSessionFactory();
	}

}
