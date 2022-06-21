package com.webnowbr.siscoat.cobranca.mb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.webnowbr.siscoat.cobranca.db.model.ContaContabil;
import com.webnowbr.siscoat.cobranca.db.model.ContasAPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.op.ContaContabilDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasAPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;

@ManagedBean(name = "contasAPagarMB")
@SessionScoped
public class ContasAPagarMB {

	private List<ContasAPagar> contasPagar;
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
	private PagadorRecebedor selectedPagadorGenerico;
	String updatePagadorRecebedor = "";
	String updateResponsavel = "";

	private List<ContaContabil> listContasContabil;
	/** Objeto selecionado na LoV - Pagador. */
	private ContaContabil selectedContaContabil;
	
	private Responsavel selectedResponsavel;
	private List<Responsavel> listResponsavel;

	public ContasAPagarMB() {

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
		this.objetoContasPagar.setTipoDespesa(tipoDespesa);
		this.objetoContasPagar.setDataPagamento(gerarDataHoje());

		return "/Atendimento/Cobranca/ContasAPagarInserir.xhtml";
	}

	public String clearFieldsEditar() {
		
		return "/Atendimento/Cobranca/ContasAPagarInserir.xhtml";
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

		return "/Atendimento/Cobranca/ContasAPagarConsultar.xhtml";
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

		return "/Atendimento/Cobranca/ContasAPagarConsultar.xhtml";
	}

	private void atualizaListagem() {
		ContasAPagarDao cDao = new ContasAPagarDao();
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
		return "/Atendimento/Cobranca/ContasAPagasConsultar.xhtml";
	}

	public void selectTipoDespesa() {
		atualizaListagem();
	}

	public String baixarConta() {
		ContasPagarDao cDao = new ContasPagarDao();
		
		if (this.objetoContasPagar.getValorPagamento() == null)
			this.objetoContasPagar.setValorPagamento(this.objetoContasPagar.getValor());
		
		if(CommonsUtil.mesmoValor(this.objetoContasPagar.getValorPagamento(), this.objetoContasPagar.getValor())) {
			this.objetoContasPagar.setContaPaga(true);
			cDao.merge(this.objetoContasPagar);
		} else {
			FacesContext facesContext = FacesContext.getCurrentInstance();;
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor pago não corresponde valor a pagar", ""));
			return "/Atendimento/Cobranca/ContasPagasConsultar.xhtml";
		}

		this.contasPagar.remove(this.objetoContasPagar);

		return "/Atendimento/Cobranca/ContasAPagarConsultar.xhtml";
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
	
	public void settarDataPagamento() {
		this.objetoContasPagar.setDataPagamento(this.objetoContasPagar.getDataVencimento());	
	}
	
	public void settarValorPagamento() {
		this.objetoContasPagar.setValorPagamento(this.objetoContasPagar.getValor());	
	}

	public void clearPagadorRecebedor() {
		this.objetoContasPagar.setPagadorRecebedor(null);
		this.selectedPagadorGenerico = new PagadorRecebedor();
	}

	public final void populateSelectedPagadorRecebedor() {
		this.objetoContasPagar.setPagadorRecebedor(this.selectedPagadorGenerico);
	}
	
	public final void pesquisaPagador() {		
		updatePagadorRecebedor = ":form:pagadorPanel";	
		updateResponsavel = ":form:pagadorPanel";
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
	
	public List<ContasAPagar> getContasPagar() {
		return contasPagar;
	}

	public void setContasPagar(List<ContasAPagar> contasPagar) {
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
		return selectedPagadorGenerico;
	}

	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagadorGenerico = selectedPagador;
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

	public PagadorRecebedor getSelectedPagadorGenerico() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagadorGenerico(PagadorRecebedor selectedPagadorGenerico) {
		this.selectedPagadorGenerico = selectedPagadorGenerico;
	}

	public String getUpdatePagadorRecebedor() {
		return updatePagadorRecebedor;
	}

	public void setUpdatePagadorRecebedor(String updatePagadorRecebedor) {
		this.updatePagadorRecebedor = updatePagadorRecebedor;
	}

	public String getUpdateResponsavel() {
		return updateResponsavel;
	}

	public void setUpdateResponavel(String updateResponsavel) {
		this.updateResponsavel = updateResponsavel;
	}
}
