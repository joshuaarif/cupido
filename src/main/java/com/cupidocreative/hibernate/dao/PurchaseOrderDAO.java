package com.cupidocreative.hibernate.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.hibernate.domain.PurchaseOrderDtl;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.cupidocreative.hibernate.domain.PurchaseOrderNumber;
import com.google.common.collect.Lists;

public class PurchaseOrderDAO {

	private static final Log LOG = LogFactory.getLog(PurchaseOrderDAO.class);

	@SuppressWarnings("unchecked")
	public List<PurchaseOrderHdr> findPoHeaders(String email, String poNumber, Set<String> paymentStatus,
			Set<String> processStatus) {
		List<PurchaseOrderHdr> result = null;
		Session session = HibernateUtil.openSession();
		Criteria c = session.createCriteria(PurchaseOrderHdr.class);

		if (StringUtils.isNotEmpty(email)) {
			c.add(Restrictions.eq("email", email));
		}

		if (StringUtils.isNotEmpty(poNumber)) {
			c.add(Restrictions.ilike("poNumber", poNumber, MatchMode.ANYWHERE));
		}

		if (paymentStatus != null && !paymentStatus.isEmpty()) {
			paymentStatus.forEach(s -> {
				c.add(Restrictions.eq("paymentStatus", s));
			});
		}

		if (processStatus != null && !processStatus.isEmpty()) {
			processStatus.forEach(s -> {
				c.add(Restrictions.eq("processStatus", s));
			});
		}

		c.addOrder(Order.desc("id"));

		result = (List<PurchaseOrderHdr>) c.list();

		return result == null ? Lists.newArrayList() : result;
	}

	public PurchaseOrderNumber getPoNumber() {
		PurchaseOrderNumber poHeaderNumber = null;

		try (Session session = HibernateUtil.openSession()) {
			StringBuilder sb = new StringBuilder(" from PurchaseOrderNumber where year = ")
					.append(Calendar.getInstance().get(Calendar.YEAR));
			@SuppressWarnings("rawtypes")
			List result = session.createQuery(sb.toString()).list();
			Transaction t = session.beginTransaction();

			if (!result.isEmpty()) {
				poHeaderNumber = (PurchaseOrderNumber) result.get(0);
				poHeaderNumber.setSequence(poHeaderNumber.getSequence() + 1);
				session.update(poHeaderNumber);
			} else {
				// no data for current year
				poHeaderNumber = new PurchaseOrderNumber();
				poHeaderNumber.setYear(Calendar.getInstance().get(Calendar.YEAR));
				poHeaderNumber.setSequence(1);
				session.save(poHeaderNumber);
			}

			t.commit();
		} catch (Exception ex) {
			LOG.error("Can't get PurchaseOrderNumber : " + ex.getMessage());
		}

		return poHeaderNumber;
	}

	public void save(PurchaseOrderHdr order) {
		try (Session session = HibernateUtil.openSession()) {
			Transaction t = session.beginTransaction();
			session.save(order);
			order.getPoDetails().forEach(dtl -> session.save(dtl));
			t.commit();
		} catch (Exception ex) {
			LOG.error("Error save order number " + order.getPoNumber() + " : " + ex.getMessage());
		}
	}

	public void saveOrUpdate(PurchaseOrderHdr order) {
		try (Session session = HibernateUtil.openSession()) {
			Transaction t = session.beginTransaction();
			session.saveOrUpdate(order);
			order.getPoDetails().forEach(dtl -> session.saveOrUpdate(dtl));
			t.commit();
		} catch (Exception ex) {
			LOG.error("Error save order number " + order.getPoNumber() + " : " + ex.getMessage());
		}
	}

	public void update(PurchaseOrderDtl orderDtl) {
		try (Session session = HibernateUtil.openSession()) {
			Transaction t = session.beginTransaction();
			session.update(orderDtl);
			t.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
	}

	public void update(PurchaseOrderHdr order) {
		try (Session session = HibernateUtil.openSession()) {
			Transaction t = session.beginTransaction();
			session.update(order);
			order.getPoDetails().forEach(dtl -> session.saveOrUpdate(dtl));
			t.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
	}
}
