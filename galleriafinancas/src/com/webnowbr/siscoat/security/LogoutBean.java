package com.webnowbr.siscoat.security;

import java.io.IOException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

@ManagedBean(name = "logoutBean")
@SessionScoped
/**
 * Controle de Logout
 * @author domingos
 *
 */
public class LogoutBean {
    /** Logger. */
    private static final Log LOG = LogFactory.getLog(LoginBean.class);
   
    public LogoutBean() {
    }

    /**
     * Executa o logout do usuario
     * @param actionEvent
     */
    public void logout() {
        Subject currentUser = SecurityUtils.getSubject();
        String tmpUser = null;
        boolean investidor = false;
        
        if (currentUser.isAuthenticated()) {
            // Dashboard - User OnLine
        	tmpUser = currentUser.getPrincipal().toString();
        	
        	UserDao userdao = new UserDao();
            List<User> userTmp = userdao.findByFilter("login", tmpUser);
            if (userTmp.size() > 0) {
        		if (userTmp.get(0).isUserInvestidor()) {
        			investidor = true;
        		} else {
        			investidor = false;
        		}
        	} else {
        		investidor = false;
        	} 
            
            currentUser.logout(); 
        } else {
            LOG.info("Usuário nao logado");
        }
        // redireciona o usuario
        try {        	
        	String url = "";

		    Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
		    if(request instanceof HttpServletRequest) {
		    	url = ((HttpServletRequest) request).getRequestURL().toString();
		    	
		    	if (investidor) {
		    		url = url.substring(0, url.indexOf("sistema")) + "sistema/LoginInvestidor.xhtml";
		    	} else {
		    		url = url.substring(0, url.indexOf("sistema")) + "sistema/Login.xhtml";
		    	}
		    }
        	
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
