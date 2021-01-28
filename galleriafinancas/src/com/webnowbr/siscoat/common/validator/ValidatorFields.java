package com.webnowbr.siscoat.common.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.*;

public class ValidatorFields implements Validator{

    public ValidatorFields() {
    }

    public void validate(FacesContext context, UIComponent component, Object obj)  
            throws ValidatorException {   
    	
    	if (component.getId().equals("email")) {
            String email = (String) obj;  
            
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");  
            Matcher m = p.matcher(email);  
      
            boolean matchFound = m.matches();  
      
            if (!matchFound) {  
            	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Endereço de E-mail Inválido!","");             
                throw new ValidatorException(msg);  
            }      		
    	} 
    }  
}