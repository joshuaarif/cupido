package com.cupidocreative.hibernate;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();
	private static final Log LOG = LogFactory.getLog(HibernateUtil.class);

	private static SessionFactory buildSessionFactory() {
		try {
			URL url = HibernateUtil.class.getClassLoader().getResource("hibernate/hibernate.cfg.xml");
			return new Configuration().configure(url).buildSessionFactory();
		} catch (Throwable ex) {
			LOG.error("Error creating hibernate session factory : " + ex.getMessage());
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static void close() {
		getSessionFactory().close();
	}

}
