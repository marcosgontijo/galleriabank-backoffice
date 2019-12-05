/**
* @(#)CharacterEncodingFilter.java  1.0 06/09/2011
*
* Copyright 2011 Funda��o CPqD, todos os diritos reservados.
* Funda��o CPqD PROPRIEDADE/CONFIDENCIAL. Uso sob termos de licen�a.
*/

package com.webnowbr.siscoat.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * Filtro geral para todo o projeto.
 * Esse filtro foi copiado do exemplo do PrimeFaces para corrigir o problema de acentua��o
 * dos campos enviados pelo TextField.
 *
 * @author marcoam
 */
public final class CharacterEncodingFilter implements Filter {

    /**
     * Filtro.
     * @param req entrada.
     * @param resp sa�da.
     * @param chain filtro.
     * @throws IOException erro.
     * @throws ServletException erro.
     */
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain)
                        throws IOException, ServletException {
        // Recebe e envia em UTF-8
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        chain.doFilter(req, resp);
    }

    /**
     * Contrutor obrigat�rio.
     * @param filterConfig configura��o.
     * @throws ServletException erro.
     */
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Destrutor do filtro.
     */
    public void destroy() {
    }
}
