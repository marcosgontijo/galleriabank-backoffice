package com.webnowbr.siscoat.cobranca.auxiliar;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;

@FacesConverter("responsavelByIdConverter")
public class ResponsavelByIdConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		if(value != null && value.trim().length() > 0) {
			ResponsavelDao rDao = new ResponsavelDao();
        	Responsavel pessoa = rDao.findById(Long.valueOf(value));
           
      	  	if (pessoa.getId() > 0) {
      	  		return pessoa;
      	  	} else {
      	  		return null;
      	  	}
        }
        else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
			if(object != null) {
				Responsavel responsavel = (Responsavel) object;
		        return String.valueOf(responsavel.getId());
        }
        else {
            return null;
        }
	}

}