package com.webnowbr.siscoat.common;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.validator.ValidatorException;

@ManagedBean(name = "validaCPF")
@SessionScoped
public class ValidaCPF {
	
	   public static boolean isCPFOnly(String cpf) {
	       if(cpf.contains(".")) {
	    	   cpf = cpf.replace(".", "");
	       }
	       if(cpf.contains("-")) {
	    	   cpf = cpf.replace("-", "");
	       }
	       String strCpf = cpf;
	        if (strCpf.equals("")) {
	            return false;
	        } 
	        int d1, d2;
	        int digito1, digito2, resto;
	        int digitoCPF;
	        String nDigResult;

	        d1 = d2 = 0;
	        digito1 = digito2 = resto = 0;

	        for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
	            digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();

	            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
	            d1 = d1 + (11 - nCount) * digitoCPF;

	            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
	            d2 = d2 + (12 - nCount) * digitoCPF;
	        }

	        //Primeiro resto da divisão por 11.  
	        resto = (d1 % 11);

	        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
	        if (resto < 2) {
	            digito1 = 0;
	        } else {
	            digito1 = 11 - resto;
	        }

	        d2 += 2 * digito1;

	        //Segundo resto da divisão por 11.  
	        resto = (d2 % 11);

	        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
	        if (resto < 2) {
	            digito2 = 0;
	        } else {
	            digito2 = 11 - resto;
	        }

	        //Digito verificador do CPF que está sendo validado.  
	        String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());

	        //Concatenando o primeiro resto com o segundo.  
	        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

	        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto. 
	        
	        boolean retorno = nDigVerific.equals(nDigResult);
	        
	        return retorno;
	    }

	   public static boolean isCPF(String cpf) {
		   if(cpf.contains(".")) {
			   cpf = cpf.replace(".", "");
		   }
		   
		   if(cpf.contains(".")) {
			   cpf = cpf.replace("-", "");
		   }
	       
	      
	       String strCpf = cpf;
	        if (strCpf.equals("")) {
	            return false;
	        } 
	        int d1, d2;
	        int digito1, digito2, resto;
	        int digitoCPF;
	        String nDigResult;

	        d1 = d2 = 0;
	        digito1 = digito2 = resto = 0;

	        for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
	            digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();

	            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
	            d1 = d1 + (11 - nCount) * digitoCPF;

	            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
	            d2 = d2 + (12 - nCount) * digitoCPF;
	        }

	        //Primeiro resto da divisão por 11.  
	        resto = (d1 % 11);

	        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
	        if (resto < 2) {
	            digito1 = 0;
	        } else {
	            digito1 = 11 - resto;
	        }

	        d2 += 2 * digito1;

	        //Segundo resto da divisão por 11.  
	        resto = (d2 % 11);

	        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
	        if (resto < 2) {
	            digito2 = 0;
	        } else {
	            digito2 = 11 - resto;
	        }

	        //Digito verificador do CPF que está sendo validado.  
	        String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());

	        //Concatenando o primeiro resto com o segundo.  
	        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

	        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto. 
	        
	        boolean retorno = nDigVerific.equals(nDigResult);
	        
	        if (!retorno) {
            	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"CPF: Erro de validação: CPF Inválido! ","");             
                throw new ValidatorException(msg);  
	        } else {
	        	return retorno;
	        }
	    }
}
