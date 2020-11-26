package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.OperacoesIndividualizado;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaParcelasInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "contasPagarMB")
@SessionScoped
public class ContasPagarMB {
	
	private List<ContasPagar> contasPagar; 
	private ContasPagar objetoContasPagar;
	
	private boolean updateMode;
	private boolean deleteMode;
	
	private List<ContratoCobranca> listContratos;
	private long idContrato;
	private String numeroContrato;
	private ContratoCobranca selectedContratoLov;
	
	public ContasPagarMB() {
		
	}
	
	public String clearFieldsInsert() {
		this.objetoContasPagar = new ContasPagar();
		
		clearContrato();
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = cDao.findAll();
		
		return "/Atendimento/Cobranca/ContasPagarInserir.xhtml";
	}
	
	public String clearFieldsEditar() {		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = cDao.findAll();
		
		if (this.objetoContasPagar.getContrato() != null) {
			this.selectedContratoLov = this.objetoContasPagar.getContrato();
			populateSelectedContrato();
		}
		
		return "/Atendimento/Cobranca/ContasPagarInserir.xhtml";
	}
	
	public String clearFields() {
		ContasPagarDao cDao = new ContasPagarDao();
		Map<String, Object> filters = new HashMap<String,Object>();			
		filters.put("contaPaga", false);
		
		this.contasPagar = cDao.findByFilter(filters);
		
		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}
	
	public String clearFieldsContasPagas() {
		ContasPagarDao cDao = new ContasPagarDao();
		Map<String, Object> filters = new HashMap<String,Object>();			
		filters.put("contaPaga", true);
		
		this.contasPagar = cDao.findByFilter(filters);
		
		return "/Atendimento/Cobranca/ContasPagasConsultar.xhtml";
	}
	
	public String baixarConta() {
		ContasPagarDao cDao = new ContasPagarDao();
		
		this.objetoContasPagar.setDataPagamento(gerarDataHoje());
		this.objetoContasPagar.setContaPaga(true);
		cDao.merge(this.objetoContasPagar);
		
		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	public String salvarConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.selectedContratoLov != null) {
			if (this.selectedContratoLov.getId() > 0) {
				this.objetoContasPagar.setContrato(this.selectedContratoLov);
			} else {
				this.objetoContasPagar.setContrato(null);
			}
		} else {
			this.objetoContasPagar.setContrato(null);
		}
		
		if (this.objetoContasPagar.getId() > 0) {
			cDao.merge(this.objetoContasPagar);
			
			facesContext.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta alterada com sucesso!", ""));
		} else {
			cDao.create(this.objetoContasPagar);
			
			facesContext.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta cadastrada com sucesso!", ""));
		}
		
		return clearFields();
	}
	
	public String editarConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();
	
		cDao.merge(this.objetoContasPagar);
			
		facesContext.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta alterada com sucesso!", ""));
		
		return clearFields();
	}
	
	public String excluirConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();
	
		cDao.delete(this.objetoContasPagar);
			
		facesContext.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta excluída com sucesso!", ""));
		
		return clearFields();
	}
	
	public final void populateSelectedContrato() {
		this.idContrato = this.selectedContratoLov.getId();
		this.numeroContrato = this.selectedContratoLov.getNumeroContrato();
	}

	public void clearContrato() {
		this.idContrato = 0;
		this.numeroContrato = null;
		this.selectedContratoLov = new ContratoCobranca();
	}

	public List<ContasPagar> getContasPagar() {
		return contasPagar;
	}

	public void setContasPagar(List<ContasPagar> contasPagar) {
		this.contasPagar = contasPagar;
	}

	public ContasPagar getObjetoContasPagar() {
		return objetoContasPagar;
	}

	public void setObjetoContasPagar(ContasPagar objetoContasPagar) {
		this.objetoContasPagar = objetoContasPagar;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isDeleteMode() {
		return deleteMode;
	}

	public void setDeleteMode(boolean deleteMode) {
		this.deleteMode = deleteMode;
	}

	public List<ContratoCobranca> getListContratos() {
		return listContratos;
	}

	public void setListContratos(List<ContratoCobranca> listContratos) {
		this.listContratos = listContratos;
	}

	public long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(long idContrato) {
		this.idContrato = idContrato;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public ContratoCobranca getSelectedContratoLov() {
		return selectedContratoLov;
	}

	public void setSelectedContratoLov(ContratoCobranca selectedContratoLov) {
		this.selectedContratoLov = selectedContratoLov;
	}
}
