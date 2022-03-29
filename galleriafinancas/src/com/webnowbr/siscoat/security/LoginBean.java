package com.webnowbr.siscoat.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.mb.InvestidorMB;
import com.webnowbr.siscoat.db.dao.HibernateFactory;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;

@ManagedBean(name = "loginBean")
@SessionScoped
/**
 * Controle de Login
 * @author domingos
 *
 */
public class LoginBean {
    private static boolean initialized = false;
	
    /** Logger. */
    private static final Log LOG = LogFactory.getLog(LoginBean.class);
    private String message = "";
    private String username;
    private String password;
    private String key;
    private String newPassword;
    private boolean renderMenu = false; 
    private int numeroContratosComite;
    private int numeroContratosDocumentosComite;

    public LoginBean() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void loginNow() throws AuthenticationException{
        boolean loggedIn = false;
        Subject currentUser = SecurityUtils.getSubject();        
        FacesContext context = FacesContext.getCurrentInstance();
        
        List<User> userTmp = new ArrayList<User>();
        
        ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
        numeroContratosComite = contratoCobrancaDao.getQuantidadeContratosComite();
        numeroContratosDocumentosComite = contratoCobrancaDao.getQuantidadeContratosDocumentosComite();
        
        // let's login the current user so we can check against roles and
        // permissions:
        if (!currentUser.isAuthenticated()) {
            LOG.info("Logging.....");
            if (username != null && password != null) {
                UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                token.setRememberMe(true);
                
                UserDao userdao = new UserDao();
                userTmp = userdao.findByFilter("login", username);
                TwoFactorAuth twoFactorAuth = new TwoFactorAuth();                
        		
                try {
                	// Google Authenticator
                	/*
                	if (userTmp.get(0).isTwoFactorAuth()) {
	                	if (twoFactorAuth.getCurrentCode(userTmp.get(0).getKey()).equals(key)) {
		                    currentUser.login(token);
		
		                    loggedIn = true;
		                    
		                    renderMenu = false;
		                    
		                    //UserDao userdao = new UserDao();   
		                    //List<User> userTmp = userdao.findByFilter("login", username);
		                    
		                    if (userTmp.size() > 0) {
		                    	TimeZone zone = TimeZone.getDefault();  
		                		Locale locale = new Locale("pt", "BR");  
		                		Calendar dataHoje = Calendar.getInstance(zone, locale);
		                		
		                    	userTmp.get(0).setUltimoAcesso(dataHoje.getTime());
		                    }
	                	} else {
	                		context.addMessage(null,
	                	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Código do Google Authenticator Inválido!", ""));
		                    LOG.info("Código do Google Authenticator Inválido!");
	                	}
                	} else {
                	*/
                		currentUser.login(token);
                		
	                    loggedIn = true;
	                    
	                    renderMenu = false;
	                    
	                    //UserDao userdao = new UserDao();   
	                    //List<User> userTmp = userdao.findByFilter("login", username);
	                    
	                    if (userTmp.size() > 0) {
	                    	TimeZone zone = TimeZone.getDefault();  
	                		Locale locale = new Locale("pt", "BR");  
	                		Calendar dataHoje = Calendar.getInstance(zone, locale);
	                		
	                    	userTmp.get(0).setUltimoAcesso(dataHoje.getTime());
	                    }
                	//}
                } catch (UnknownAccountException uae) {
                    LOG.info("There is no user with username of "
                            + token.getPrincipal());
                } catch (IncorrectCredentialsException ice) {
                	context.addMessage(null,
                	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário ou Senha Inválida!", ""));
                    LOG.info("Password for account " + token.getPrincipal()
                            + " was incorrect!");
                } catch (LockedAccountException lae) {
                	context.addMessage(null,
                	        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conta Expirada!", ""));
                    LOG.info("The account for username " + token.getPrincipal()
                            + " is locked.  "
                            + "Please contact your administrator to unlock it.");
                }
                // ... catch more exceptions here (maybe custom ones specific to
                // your application?
                catch (AuthenticationException ae) {
                    // unexpected condition? error?
                	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao efetuar Login: " + ae.getMessage(), ""));
                }
            }           
        } else {
        	loggedIn = true;
        	renderMenu = false;
        }
    		   
        if (loggedIn) {
                try {
                	String url = "";
                	Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
                	
            	    if(request instanceof HttpServletRequest) {
            	    	url = ((HttpServletRequest) request).getRequestURL().toString();
            	    }
                	
                	// faz o redirecionamento de acordo com o perfil do usuário
                	if (userTmp.size() > 0) {
                		if (userTmp.get(0).isUserInvestidor()) {
                			FacesContext.getCurrentInstance().getExternalContext().redirect("./Investidor/Dashboard.xhtml");
                		} else {
                			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
                		}
                	} else {
                		FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
                	}                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
         } else {
             setMessage("Invalid credentials");
         }
    }
    
    public void redirectAccessDenied() throws IOException {
    	
    	Subject currentUser = SecurityUtils.getSubject();
    	
    	String url = "";
    	Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    if(request instanceof HttpServletRequest) {
	    	url = ((HttpServletRequest) request).getRequestURL().toString();
	    	url = url.replace("/Denied.xhtml", "");
	    }
    	
    	if (currentUser.isAuthenticated()) {
    		FacesContext.getCurrentInstance().getExternalContext().redirect(url + "/index.xhtml");
    	} else {
    		FacesContext.getCurrentInstance().getExternalContext().redirect(url + "/Login.xhtml");
    	}    		
    }
    
    public String changePwd() {    
        FacesContext context = FacesContext.getCurrentInstance();
     
        UserDao userdao = new UserDao();
        List<User> userTmp = userdao.findByFilter("login", username);        
                    
        if (userTmp.size() > 0) {
        	
        	userTmp.get(0).setPassword(this.newPassword);
        	
        	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Portal Investidor: " + userTmp.get(0).getName() + " a sua senha foi alterada com sucesso!", ""));
        	
        	return "/LoginInvestidor.xhtml";
        } else {
        	
        	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Portal Investidor: O usuário " + this.username + " não foi localizado no sistema!", ""));
        	
        	return "";
        }
    }

	/**
     * Executa a inicializacao do banco de dados.
     * @return null (sempre)
     */
    public String getInitialize() {
        if (!initialized) {
            Thread t = new Thread() {
                public void run() {
                    HibernateFactory.getSessionFactory();
                }
            };
            t.start();
        }
        return null;
    }

	public boolean isRenderMenu() {
		return renderMenu;
	}

	public void setRenderMenu(boolean renderMenu) {
		this.renderMenu = renderMenu;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}  

 	public int getNumeroContratosComite() {
 		return numeroContratosComite;
 	}

 	public void setNumeroContratosComite(int numeroContratosComite) {
 		this.numeroContratosComite = numeroContratosComite;
 	}

	public int getNumeroContratosDocumentosComite() {
		return numeroContratosDocumentosComite;
	}

	public void setNumeroContratosDocumentosComite(int numeroContratosDocumentosComite) {
		this.numeroContratosDocumentosComite = numeroContratosDocumentosComite;
	}
 	
 	
}
