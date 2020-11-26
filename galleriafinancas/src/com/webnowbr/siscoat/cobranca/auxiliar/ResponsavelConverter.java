package com.webnowbr.siscoat.cobranca.auxiliar;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;

@FacesConverter("ResponsavelConverter")
public class ResponsavelConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		if(value != null && value.trim().length() > 0) {
            try {
            	  ResponsavelDao rDao = new ResponsavelDao();
            	  List<Responsavel> respList = rDao.findAll();
                 
            	 for(Responsavel resp: respList)
            	 {
            		 if(resp.getNome().equals(value))
            		 {
            			 return resp;
            		 }
            	 }
                  	
                return null;
                
            } catch(NumberFormatException e) {
                throw new ConverterException();
            }
        }
        else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
			if(object != null) {
            return String.valueOf(((Responsavel) object).getNome());
        }
        else {
            return null;
        }
	}

}