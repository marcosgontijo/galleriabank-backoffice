package com.webnowbr.siscoat.db.dao;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.webnowbr.siscoat.common.PropertyLoader;

/**
 * This factory provides many types of hibernate initialization methods.
 * @author Walter Wong
 */
public final class HibernateFactory {
    /** Controle de sessao. */
    private static SessionFactory sSessionFactory;
    /** Log. */
    private static final Log LOG = LogFactory.getLog(HibernateFactory.class);
    /** Propriedade hibernate-config. */
    private static final String CONFIG_KEY = "hibernate-config";

    /**
     * Static initialization block
     */
    static {
    	configureSessionFactory();
    }
    
    /**
     * Construtor default.
     */
    private HibernateFactory() {
    }

    /**
     * This method configures the session factory.
     */
    private synchronized static void configureSessionFactory() {
    	if(sSessionFactory != null) {
    		return;
    	}
        final Configuration configuration = new Configuration();
        // configuracao do datasource
        String hibernateConfigFile = PropertyLoader.getString(CONFIG_KEY);
        if (hibernateConfigFile != null && hibernateConfigFile.length() > 0) {
            configuration.configure(new File(hibernateConfigFile));
        } else {
            configuration.configure(); // use default configuration
        }
        // configuracao do mapping
        try {
			final Enumeration<URL> enURL = Thread.currentThread().getContextClassLoader()
					.getResources("hibernate-mappings.cfg.xml");
			while (enURL.hasMoreElements()) {
				configuration.configure(enURL.nextElement());
			}
			sSessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            LOG.fatal("Exception configuring sessionFactory", e);
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * Gets the sessionFactory. If for some reason it hasn't been initialized yet,
     * this method initializes it before returning.
     * 
     * @return SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
    	if(sSessionFactory == null) {
    		configureSessionFactory();
    	}
        return sSessionFactory;
    }

}