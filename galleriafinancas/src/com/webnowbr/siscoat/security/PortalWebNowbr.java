package com.webnowbr.siscoat.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.GroupAdm;
import com.webnowbr.siscoat.infra.db.model.User;

/**
 * Classe de controle de acesso dos usuarios.
 * @author domingos
 */
public class PortalWebNowbr extends AuthorizingRealm {
    /** Indica que todos os niveis sao permitidos. */
    public static final int ALL_LEVELS = 99;
    
    /** Verifica Ip Autorizado */
    public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Recupera as informacoes de dominio
     * @param principals - PrincipalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        String username = (String) getAvailablePrincipal(principals);
        // obtem as informacoes de dominio do usuario (grupos)
        UserDao dao = new UserDao();
        try {
            List<User> list = dao.findByFilter("login", username);
            if (list != null && !list.isEmpty()) {
                // just create a dummy.
                SimpleAccount account = new SimpleAccount(username, "sha256EncodedPasswordFromDatabase", getName());
                User user = list.get(0);
                for (GroupAdm group : user.getGroupList()) {
                    account.addRole(group.getAcronym()); // as siglas dos grupos sao os dominios do usuario
                }
                return account;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Obtem as informacoes de autenticacao.
     * @param token - AuthenticationToken
     * @return AuthenticationInfo - autenticacao.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token)
            throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        // acessa o banco de dados para obter os dados do usuario.
        UserDao dao = new UserDao();
        FacesContext context = FacesContext.getCurrentInstance();
        boolean isValid = true;
        String mensagemErro = "";
        
        try {
            List<User> list = dao.findByFilter("login", upToken.getUsername());       
            
            if (list != null && !list.isEmpty() ) {         
            	
                // Verificar se tem autorização de acesso no dia de hoje
            	if (list.get(0).getDiasSemana().size() > 0) {
            		isValid = false; 
            		String nomediaSemanaHoje = getNomeDiaHoje();
            		
            		mensagemErro = "Usuário não possui permissão de acesso ao sistema no dia de hoje (" + nomediaSemanaHoje + ")!";
            		            		
	                for (String dias : list.get(0).getDiasSemana()) {
	                	if (dias.equals(nomediaSemanaHoje)) {
	                		isValid = true; 
	                		mensagemErro = "";
	                	}
	                }
            	}
            	
                // Verificar se tem autorização de acesso na hora do login
            	if (list.get(0).getHoraInicioPermissaoAcesso() != null
            			&& list.get(0).getHoraFimPermissaoAcesso() != null
            			&& isValid) {
            		
            		DateFormat dateFormat = new SimpleDateFormat("HH:mm");  
            		
            		String timeHoje = getHoraHoje();
            		int timeHojeInt = Integer.valueOf(timeHoje.replaceAll(":", ""));
            		
            		int timePermittedStartInt = Integer.valueOf(dateFormat.format(list.get(0).getHoraInicioPermissaoAcesso()).replaceAll(":", ""));  
            		int timePermittedFimInt = Integer.valueOf(dateFormat.format(list.get(0).getHoraFimPermissaoAcesso()).replaceAll(":", ""));  
            		
            		if (timeHojeInt < timePermittedStartInt || timeHojeInt > timePermittedFimInt) {
            			mensagemErro = "Usuário não possui permissão de acesso ao sistema neste horário (Hora: " + timeHoje + ")!";
            			isValid = false;
            		}
            	}
            	
            	
            	// Verifica se tem IP sinalizado, se SIM se o IP tem autorização de acesso
	            if (list.get(0).getIp() != null && isValid) {
	            	if (!list.get(0).getIp().isEmpty()) {
		            	if (list.get(0).getIp().equals(getIp())) {
		            		isValid = true;
		            	} else {
		            		isValid = false;
		            		mensagemErro = "IP de acesso não autorizado: " + list.get(0).getIp() + "(" + getIp() + ")";
		            		
		            	}
	            	} else {
	            		isValid = true; 
	            	}
	            }
                
                if (isValid) {
                	return new SimpleAuthenticationInfo(upToken.getUsername(), list.get(0).getPassword(), getName());
                } else {
                	context.addMessage(null,
                	        new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagemErro, ""));
                }
            } else {
            	context.addMessage(null,
            	        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário não identificado!", ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
	/**
	 * VERIFICAR QUAL O DIA DO LOGIN
	 * @return
	 */
	public String getNomeDiaHoje() {
		TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		
		int diaSemanaHoje = dataHoje.get(dataHoje.DAY_OF_WEEK);
		String nomediaSemanaHoje = "";
		
		switch (diaSemanaHoje){ 
			case Calendar.MONDAY: 
				nomediaSemanaHoje = "Segunda-Feira";
				break;
			case Calendar.TUESDAY: 
				nomediaSemanaHoje = "Terça-Feira";
				break;
			case Calendar.WEDNESDAY: 
				nomediaSemanaHoje = "Quarta-Feira";
				break;
			case Calendar.THURSDAY: 
				nomediaSemanaHoje = "Quinta-Feira";
				break;
			case Calendar.FRIDAY: 
				nomediaSemanaHoje = "Sexta-Feira";
				break;
			case Calendar.SATURDAY: 
				nomediaSemanaHoje = "Sábado";
				break;
			case Calendar.SUNDAY: 
				nomediaSemanaHoje = "Domingo";
				break;
			default:
				nomediaSemanaHoje = "";
		}

		return nomediaSemanaHoje;
	}
	
	/**
	 * VERIFICAR QUAL A HORA DO LOGIN
	 * @return
	 */
	public String getHoraHoje() {
		TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		
		int horaHoje = dataHoje.get(dataHoje.HOUR_OF_DAY);
		int minutoHoje = dataHoje.get(dataHoje.MINUTE);
		String timeHoje = horaHoje + ":" + minutoHoje;

		return timeHoje;
	}
	

    /**
     * Verifica se o usuario possui permissao para executar determinada acao.
     * @param principals - informacoes do usuario.
     * @param permission - permissao requerida. Formato Group:Level
     * @return boolean - true se tiver permissao.
     */
    @Override
    public boolean isPermitted(final PrincipalCollection principals, final String permission) {
        String username = (String) getAvailablePrincipal(principals);
        String[] permList = permission.split(",");
        // obtem as permissoes do usuario
        UserDao dao = new UserDao();
        try {
            List<User> list = dao.findByFilter("login", username);
            if (list != null && !list.isEmpty()) {
                User user = list.get(0);
                for (int i = 0; i < permList.length; i++) {
                    if (checkPermission(permList[i].trim(), user)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Realiza a analise da permissao do usuario.
     * @param permission - permissao.
     * @param user - dados do usuario
     * @return true se possui permissao
     */
    private boolean checkPermission(final String permission, final User user) {
        String groupName = null;
        int level = -1;
        try {
            String[] perm = permission.split(":");
            groupName = perm[0].trim();
            if (perm[1].trim().equals("*")) {
                level = ALL_LEVELS; // permite todos os niveis
            } else {
                level = Integer.parseInt(perm[1].trim());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        if (user.getLevel() <= level) {
            // verifica se o usuario possui permissao de grupo
            if (groupName.equals("*")) {
                return true; // todos os grupos sao permitidos
            }
            for (GroupAdm group : user.getGroupList()) {
                if (groupName.equalsIgnoreCase(group.getAcronym())) {
                    return true; // possui o grupo e o nivel necessarios.
                }
            }
        }
        return false;
    }
}
