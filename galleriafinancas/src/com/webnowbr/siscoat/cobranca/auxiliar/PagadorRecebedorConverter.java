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

@FacesConverter("pagadorRecebedorConverter")
public class PagadorRecebedorConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		if(value != null && value.trim().length() > 0) {
        	PagadorRecebedorDao rDao = new PagadorRecebedorDao();
      	  	PagadorRecebedor pessoa = rDao.findById(Long.valueOf(value));
           
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
				PagadorRecebedor pagadorRecebedor = (PagadorRecebedor) object;
		        return String.valueOf(pagadorRecebedor.getId());
        }
        else {
            return null;
        }
	}

}