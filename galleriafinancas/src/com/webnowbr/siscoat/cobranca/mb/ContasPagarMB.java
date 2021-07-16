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
import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.OperacoesIndividualizado;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ContaContabilDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaParcelasInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "contasPagarMB")
@SessionScoped
public class ContasPagarMB {

	private List<ContasPagar> contasPagar;
	private Map<String, Object> filters;
	private ContasPagar objetoContasPagar;

	private boolean updateMode;
	private boolean deleteMode;

	private String tipoDespesa;
	
	private Date relDataContratoInicio;
	private Date relDataContratoFim;

	private List<ContratoCobranca> listContratos;
	private long idContrato;
	// private String numeroContrato;
	private ContratoCobranca selectedContratoLov;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<PagadorRecebedor> listRecebedorPagador;
	/** Objeto selecionado na LoV - Pagador. */
	private PagadorRecebedor selectedPagador;

	private List<ContaContabil> listContasContabil;
	/** Objeto selecionado na LoV - Pagador. */
	private ContaContabil selectedContaContabil;
	
	private Responsavel selectedResponsavel;
	private List<Responsavel> listResponsavel;

	public ContasPagarMB() {

	}
	
	public ContratoCobranca getContrato(String numeroContratoParametro) {		
		if (numeroContratoParametro != null) { 
			if (!numeroContratoParametro.equals("")) { 
				List<ContratoCobranca> contratos = new ArrayList<ContratoCobranca>();
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
				String numeroContrato = "";
				
				if (numeroContratoParametro.length() == 4) {
					numeroContrato = "0" + numeroContratoParametro;
				} else {
					numeroContrato = numeroContratoParametro;
				}
				
				contratos = cDao.findByFilter("numeroContrato", numeroContrato);
				
				if (contratos.size() > 0) {
					return contratos.get(0);	
				}				
			}
		}
		
		return null;
	}

	public String clearFieldsInsert() {
		this.objetoContasPagar = new ContasPagar();
		objetoContasPagar.setTipoDespesa(tipoDespesa);

		return "/Atendimento/Cobranca/ContasPagarInserir.xhtml";
	}

	public String clearFieldsEditar() {

		return "/Atendimento/Cobranca/ContasPagarInserir.xhtml";
	}

	public String clearFields() {
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;
		
		if (tipoDespesa == null || tipoDespesa.isEmpty())
			tipoDespesa = "C";
		filters = new HashMap<String, Object>();
		filters.put("contaPaga", false);
		atualizaListagem();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedorPagador = pagadorRecebedorDao.findAll();

		ContaContabilDao contaContabilDao = new ContaContabilDao();
		listContasContabil = contaContabilDao.ContasContabilOrdenadaRaiz();
		
		ResponsavelDao rDao = new ResponsavelDao();
		this.listResponsavel = rDao.findAll();

		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}
	
	public String clearFieldsConsultar() {
		filters = new HashMap<String, Object>();
		filters.put("contaPaga", false);
		atualizaListagem();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedorPagador = pagadorRecebedorDao.findAll();

		ContaContabilDao contaContabilDao = new ContaContabilDao();
		listContasContabil = contaContabilDao.ContasContabilOrdenadaRaiz();
		
		ResponsavelDao rDao = new ResponsavelDao();
		this.listResponsavel = rDao.findAll();

		return "/Atendimento/Cobranca/ContasPagarConsultar.xhtml";
	}

	private void atualizaListagem() {
		ContasPagarDao cDao = new ContasPagarDao();
		//HashMap<String, Object> filtersConsulta = new HashMap<String, Object>();
		//filtersConsulta.putAll(filters);
		//filtersConsulta.put("tipoDespesa", tipoDespesa);
		//this.contasPagar = cDao.findByFilter(filtersConsulta);
		boolean contaPaga = false;
		
		if (filters.get("contaPaga") != null) {
			contaPaga = (Boolean) filters.get("contaPaga");
		} 
		
		try {
			this.contasPagar = cDao.atualizaListagemContasPagar(tipoDespesa, contaPaga, this.relDataContratoInicio, this.relDataContratoFim);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String clearFieldsContasPagas() {
		filters = new HashMap<String, Object>();
		filters.put("contaPaga", true);
		atualizaListagem();
		return "/Atendimento/Cobranca/ContasPagasConsultar.xhtml";
	}

	public void selectTipoDespesa() {
		atualizaListagem();
	}

	public String baixarConta() {
		ContasPagarDao cDao = new ContasPagarDao();

		this.objetoContasPagar.setDataPagamento(gerarDataHoje());
		this.objetoContasPagar.setContaPaga(true);
		if (this.objetoContasPagar.getValorPagamento() == null)
			this.objetoContasPagar.setValorPagamento(this.objetoContasPagar.getValor());
		cDao.merge(this.objetoContasPagar);

		this.contasPagar.remove(this.objetoContasPagar);

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
		
		if (this.objetoContasPagar.getNumeroDocumento() != null) { 
			if (!this.objetoContasPagar.getNumeroDocumento().equals("")) { 
				ContratoCobranca contrato = new ContratoCobranca();				
				contrato = getContrato(this.objetoContasPagar.getNumeroDocumento());
				if (contrato != null) {
					this.objetoContasPagar.setContrato(contrato);
				}
			}
		}

		if (this.objetoContasPagar.getId() > 0) {
			cDao.merge(this.objetoContasPagar);

			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta alterada com sucesso!", ""));
		} else {
			cDao.create(this.objetoContasPagar);

			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta cadastrada com sucesso!", ""));
		}

		return clearFields();
	}

	public String editarConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.objetoContasPagar.getNumeroDocumento() != null) { 
			if (!this.objetoContasPagar.getNumeroDocumento().equals("")) { 
				ContratoCobranca contrato = new ContratoCobranca();				
				contrato = getContrato(this.objetoContasPagar.getNumeroDocumento());
				if (contrato != null) {
					this.objetoContasPagar.setContrato(contrato);
				}
			}
		}

		cDao.merge(this.objetoContasPagar);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta alterada com sucesso!", ""));

		return clearFields();
	}

	public String excluirConta() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ContasPagarDao cDao = new ContasPagarDao();

		cDao.delete(this.objetoContasPagar);

		this.contasPagar.remove(this.objetoContasPagar);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contas a Pagar: Conta excluída com sucesso!", ""));

		return clearFields();
	}

	public void clearPagadorRecebedor() {
		this.objetoContasPagar.setPagadorRecebedor(null);
		this.selectedPagador = new PagadorRecebedor();
	}

	public final void populateSelectedPagadorRecebedor() {
		this.objetoContasPagar.setPagadorRecebedor(this.selectedPagador);
	}
	
	public final void populateSelectedResponsavel() {
		this.objetoContasPagar.setResponsavel(this.selectedResponsavel);
	}
	
	public void clearResponsavel() {
		this.objetoContasPagar.setResponsavel(null);
		this.selectedResponsavel = new Responsavel();
	}

	public void ContaContabil() {
		this.objetoContasPagar.setContaContabil(null);
		this.selectedContaContabil = new ContaContabil();
	}

	public final void populateSelectedContaContabil() {
		this.objetoContasPagar.setContaContabil(this.selectedContaContabil);
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

	public String getTipoDespesa() {
		return tipoDespesa;
	}

	public void setTipoDespesa(String tipoDespesa) {
		this.tipoDespesa = tipoDespesa;
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

//	public String getNumeroContrato() {
//		return numeroContrato;
//	}
//
//	public void setNumeroContrato(String numeroContrato) {
//		this.numeroContrato = numeroContrato;
//	}

	public ContratoCobranca getSelectedContratoLov() {
		return selectedContratoLov;
	}

	public void setSelectedContratoLov(ContratoCobranca selectedContratoLov) {
		this.selectedContratoLov = selectedContratoLov;
	}

	public List<PagadorRecebedor> getListRecebedorPagador() {
		return listRecebedorPagador;
	}

	public void setListRecebedorPagador(List<PagadorRecebedor> listRecebedorPagador) {
		this.listRecebedorPagador = listRecebedorPagador;
	}

	public PagadorRecebedor getSelectedPagador() {
		return selectedPagador;
	}

	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagador = selectedPagador;
	}

	public List<ContaContabil> getListContasContabil() {
		return listContasContabil;
	}

	public void setListContasContabil(List<ContaContabil> listContasContabil) {
		this.listContasContabil = listContasContabil;
	}

	public ContaContabil getSelectedContaContabil() {
		return selectedContaContabil;
	}

	public void setSelectedContaContabil(ContaContabil selectedContaContabil) {
		this.selectedContaContabil = selectedContaContabil;
	}

	public Date getRelDataContratoInicio() {
		return relDataContratoInicio;
	}

	public void setRelDataContratoInicio(Date relDataContratoInicio) {
		this.relDataContratoInicio = relDataContratoInicio;
	}

	public Date getRelDataContratoFim() {
		return relDataContratoFim;
	}

	public void setRelDataContratoFim(Date relDataContratoFim) {
		this.relDataContratoFim = relDataContratoFim;
	}

	public Responsavel getSelectedResponsavel() {
		return selectedResponsavel;
	}

	public void setSelectedResponsavel(Responsavel selectedResponsavel) {
		this.selectedResponsavel = selectedResponsavel;
	}

	public List<Responsavel> getListResponsavel() {
		return listResponsavel;
	}

	public void setListResponsavel(List<Responsavel> listResponsavel) {
		this.listResponsavel = listResponsavel;
	}
}
