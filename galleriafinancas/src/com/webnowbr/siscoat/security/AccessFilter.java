package com.webnowbr.siscoat.security;

import java.io.IOException;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class AccessFilter implements Filter {
    /** Logger. */
    private static final Log LOG = LogFactory.getLog(LoginBean.class);

    public void destroy() {
    }

    /**
     * Executa o filtro para acesso do usuario
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filter)
                                throws IOException, ServletException {
        HttpServletResponse httpres = (HttpServletResponse)res;
        Subject currentUser = SecurityUtils.getSubject();
        
        if (currentUser.isAuthenticated()) {
            filter.doFilter(req, res);
        } else {
            httpres.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
     }
}

