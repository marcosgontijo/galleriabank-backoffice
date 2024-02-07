package com.webnowbr.siscoat.auxiliar;

import java.math.BigDecimal;

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
@FacesConverter(value = "bigDecimalConverter")
public class BigDecimalConverter implements javax.faces.convert.Converter {

    /** Log. */
    private Log log = LogFactory.getLog(BigDecimalConverter.class);

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

        try {

            try {
                valor = (BigDecimal) value;
            } catch (Exception e) {
                log.error("Erro ao tentar converter valor para bigdecimal. valor: " + value);
                e.printStackTrace();
            }
            String decimal = valor.subtract(
            		  new BigDecimal(valor.intValue())).multiply( new BigDecimal(100)).toPlainString();
           
            String str = valor.toString();
            if (decimal.length() < 2)
				str = valor.setScale(2).toString();

            String nValueFinal = "";

            String nValue = "";

            char[] array = str.toCharArray();

            for (int i = 0; i < array.length; i++) {
                if (array[i] == '.' && array[i] != ',') {
                    nValue += ",";
                } else if (array[i] != '.' && array[i] != ',') {
                    nValue += array[i];
                }
            }

            if (nValue.contains(",")) {
                // Insere os separadores de milhar
                String inteira = nValue.split(",")[0];
                String fracionaria = nValue.split(",")[1];
                int separadorMilharCount = 0;
                for (int i = 0; i <= inteira.length() - 1; i++) {
                    if (separadorMilharCount == 3) {
                        nValueFinal = nValueFinal + ".";
                        separadorMilharCount = 0;
                    }
                    nValueFinal += inteira.charAt((inteira.length() - 1) - i);
                    separadorMilharCount++;
                }
                String aux = "";
                for (int i = 0; i <= nValueFinal.length() - 1; i++) {
                    aux += nValueFinal.charAt((nValueFinal.length() - 1) - i);
                }
                nValueFinal = aux + "," + fracionaria;
                return nValueFinal;
            } else {
                return nValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "---";
    }
}
