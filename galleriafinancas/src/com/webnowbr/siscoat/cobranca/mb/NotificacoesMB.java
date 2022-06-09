package com.webnowbr.siscoat.cobranca.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;

@ManagedBean(name = "notificacoesMB")
@SessionScoped
public class NotificacoesMB {
	
	private String frase1;
	private String frase2;
	
	private boolean enviaParaTodosResponsaveis;
	
	private List<Responsavel> listResponsaveis;
	private Responsavel selectedResponsavel;
	private long idResponsavel;
	private String nomeResponsavel;
	
	public String clearFields() {
		this.frase1 = null;
		this.frase2 = null;
		
		this.enviaParaTodosResponsaveis = true;
		this.listResponsaveis = new ArrayList<Responsavel>();
		
		clearResponsavel();
		loadResponsaveis();
		
		return "/Atendimento/Cobranca/NotificacoesResponsaveis.xhtml"; 
	}
	
	public void enviaNotificacao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		TakeBlipMB takeBlipMB = new TakeBlipMB();
		
		if (!this.enviaParaTodosResponsaveis) {
			if (this.selectedResponsavel.getId() <= 0) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Notifica Responsável] Nenhum responsável foi selecionado para o envio da mensagem!!!",""));
			} else {
				takeBlipMB.sendWhatsAppNotificaResponsavel(this.selectedResponsavel, "mensagem_responsaveis", this.frase1, this.frase2);
			}			
		} else {
			for (Responsavel responsaveis : this.listResponsaveis) {
				takeBlipMB.sendWhatsAppNotificaResponsavel(responsaveis, "mensagem_responsaveis", this.frase1, this.frase2);
			}
		}
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
					"[Notifica Responsável] Mensagem enviada com sucesso!!!",""));
	}
	
	public void changeEnviaParaTodosResponsaveis() {
		if (!this.enviaParaTodosResponsaveis) {
			clearResponsavel();			
		} 
	}
	
	public void clearResponsavel() {
		this.idResponsavel = 0;
		this.nomeResponsavel = null;
		this.selectedResponsavel = new Responsavel();
	}
	
	public final void populateSelectedResponsavel() {
		this.idResponsavel = this.selectedResponsavel.getId();
		this.nomeResponsavel = this.selectedResponsavel.getNome();
	}
	
	public final void loadResponsaveis() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		this.listResponsaveis = responsavelDao.findAll();
	}
	
	public String getFrase1() {
		return frase1;
	}

	public void setFrase1(String frase1) {
		this.frase1 = frase1;
	}

	public String getFrase2() {
		return frase2;
	}

	public void setFrase2(String frase2) {
		this.frase2 = frase2;
	}

	public boolean isEnviaParaTodosResponsaveis() {
		return enviaParaTodosResponsaveis;
	}

	public void setEnviaParaTodosResponsaveis(boolean enviaParaTodosResponsaveis) {
		this.enviaParaTodosResponsaveis = enviaParaTodosResponsaveis;
	}

	public List<Responsavel> getListResponsaveis() {
		return listResponsaveis;
	}

	public void setListResponsaveis(List<Responsavel> listResponsaveis) {
		this.listResponsaveis = listResponsaveis;
	}

	public Responsavel getSelectedResponsavel() {
		return selectedResponsavel;
	}

	public void setSelectedResponsavel(Responsavel selectedResponsavel) {
		this.selectedResponsavel = selectedResponsavel;
	}

	public long getIdResponsavel() {
		return idResponsavel;
	}

	public void setIdResponsavel(long idResponsavel) {
		this.idResponsavel = idResponsavel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
}