package com.webnowbr.siscoat.cobranca.mb;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ViewScoped
@ManagedBean(name="campoObservacaoPopPupAntesDepois")
public class CampoObservacaoPopPupAntesDepois implements Serializable{
	
	private static final long serialVersionUID = 1L;

    private String texto;
    private int palavrasRestantes;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getPalavrasRestantes() {
        if (texto.length() > 10) {
            return 200;
        }

        // Divide o texto em palavras usando um espaço como delimitador
        String[] palavras = texto.split("\\s+");

        // Calcula o número de palavras restantes
        palavrasRestantes = Math.max(0, 200 - palavras.length);

        return palavrasRestantes;
    }

    public void contarPalavras() {
        getPalavrasRestantes();

        String mensagem = verificarQuantidadePalavras();

        if (mensagem != null) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
    }

    private String verificarQuantidadePalavras() {
        if (texto == null || texto.trim().isEmpty()) {
            return "O texto deve conter mais de 10 palavras.";
        }
        if (texto.length() >= 200) {
        	return "O texto deve conter até 200 caracteres.";
        }

        String[] palavras = texto.split("\\s+");
        int quantidadePalavras = palavras.length;

        if (quantidadePalavras > 200) {
            return "Você excedeu o limite de 200 palavras.";
        } else if (quantidadePalavras < 10) {
            return "O texto deve conter mais de 10 palavras.";
        }

        return null; 
    }
}
