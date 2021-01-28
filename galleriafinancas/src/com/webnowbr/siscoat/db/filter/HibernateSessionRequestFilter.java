package com.webnowbr.siscoat.db.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;

import com.webnowbr.siscoat.db.dao.HibernateFactory;


public class HibernateSessionRequestFilter implements Filter {

    private static Log log = LogFactory.getLog(HibernateSessionRequestFilter.class);

    private SessionFactory sf;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        try {

            if (sf == null) {
                sf = HibernateFactory.getSessionFactory();
            }

            // Open the session if there is no session already open.
            if (sf.getCurrentSession() != null) {
               sf.getCurrentSession().beginTransaction();
            }

            // Call the next filter (continue request processing)
            chain.doFilter(request, response);

            // Commit and cleanup
            if (sf.getCurrentSession().beginTransaction().isActive()) {
                // close the session.
                sf.getCurrentSession().beginTransaction().commit();
            }

        } catch (StaleObjectStateException staleEx) {
            log.error("This interceptor does not implement optimistic concurrency control!");
            log.error("Your application will not work until you add compensation actions!");
            System.out.println("Your application will not work until you add compensation actions!");
            // Rollback, close everything, possibly compensate for any permanent changes
            // during the conversation, and finally restart business conversation. Maybe
            // give the user of the application a chance to merge some of his work with
            // fresh data... what you do here depends on your applications design.
            throw staleEx;
        } catch (Throwable ex) {
            // Rollback only
            ex.printStackTrace();
            try {
                if (sf.getCurrentSession().beginTransaction().isActive()) {
                    log.debug("Trying to rollback database transaction after exception");
                    sf.getCurrentSession().beginTransaction().rollback();
                }
            } catch (Throwable rbEx) {
                log.error("Could not rollback transaction after exception!", rbEx);
            }

            // Let others handle it... maybe another interceptor for exceptions?
            throw new ServletException(ex);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        sf = HibernateFactory.getSessionFactory();
    }

    @Override
    public void destroy() {
    }

}