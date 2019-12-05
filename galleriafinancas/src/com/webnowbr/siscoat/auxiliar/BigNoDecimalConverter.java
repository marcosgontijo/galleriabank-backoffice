package com.webnowbr.siscoat.auxiliar;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converte um valor String para BigDecimal.
 * @author joaob
 *
 */
@FacesConverter(value = "bigNoDecimalConverter")
public class BigNoDecimalConverter implements javax.faces.convert.Converter {

    /** Log. */
    private Log log = LogFactory.getLog(BigNoDecimalConverter.class);

    /**
     * Obtem o objeto.
     * @param ctx FacesContext
     * @param comp UIComponent
     * @param value String
     * @return Object
     */
    public final Object getAsObject(final FacesContext ctx, final UIComponent comp, final String value) {

        BigDecimal result = null;

        try {
            if (value == null || "".equals(value)) {
                return new BigDecimal("0");
            }

            String nValue = "";
            char[] array = value.toCharArray();
            for (int i = 0; i < array.length; i++) {
                if (array[i] != '.' && array[i] == ',') {
                    nValue += ".";
                } else if (array[i] != '.' && array[i] != ',') {
                    nValue += array[i];
                }
            }

            result = new BigDecimal(nValue);

            int pos = nValue.indexOf(".");
            if (pos > 0) {
                result.setScale(nValue.substring(pos + 1).length());
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal("0");
    }

    /**
     * Obtem o objeto.
     * @param ctx FacesContext
     * @param comp UIComponent
     * @param value Object
     * @return String
     */
    public final String getAsString(final FacesContext ctx, final UIComponent comp, final Object value) {
        BigDecimal valor = null;        
        DecimalFormat numFormat;
       	String number = "---";
        
        try {
            try {
                valor = (BigDecimal) value;
	           	numFormat = new DecimalFormat("#,###,###");
	           	number = numFormat.format(valor);	           	 
            } catch (Exception e) {
                log.error("Erro ao tentar converter valor para bigdecimal. valor: " + value);
                e.printStackTrace();
            }               
        } catch (Exception e) {
            e.printStackTrace();
        }
		return number;
    }
}
