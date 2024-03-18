package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.hibernate.TransientObjectException;
import org.json.JSONObject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.auxiliar.CompactadorUtil;
import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.PorcentagemPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.Averbacao;
import com.webnowbr.siscoat.cobranca.db.model.CcbContrato;
import com.webnowbr.siscoat.cobranca.db.model.CcbParticipantes;
import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.CcbDao;
import com.webnowbr.siscoat.cobranca.db.op.CcbParticipantesDao;
import com.webnowbr.siscoat.cobranca.db.op.CcbProcessosJudiciaisDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.RegistroImovelTabelaDao;
import com.webnowbr.siscoat.cobranca.model.cep.CepResult;
import com.webnowbr.siscoat.cobranca.service.CcbService;
import com.webnowbr.siscoat.cobranca.service.CepService;
import com.webnowbr.siscoat.cobranca.service.IpcaService;
import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.job.IpcaJobCalcular;
import com.webnowbr.siscoat.security.LoginBean;
import com.webnowbr.siscoat.simulador.GoalSeek;
import com.webnowbr.siscoat.simulador.GoalSeekFunction;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;
import com.webnowbr.siscoat.simulador.SimuladorMB;

import net.sf.jasperreports.engine.JRException;

/** ManagedBean. */@ManagedBean(name = "ccbMB")
@SessionScoped
@SuppressWarnings("deprecation")
public class CcbMB {

	private boolean addTerceiro;

	private PagadorRecebedor selectedPagadorGenerico;
	private PagadorRecebedor objetoPagadorRecebedor;
	private PagadorRecebedor testemunha1Selecionado;
	private PagadorRecebedor testemunha2Selecionado;
	private List<PagadorRecebedor> listPagadores;
	
	private List<String> listaTipoDownload = new ArrayList<String>();
	
	private String tipoPesquisa;
	private String tipoDownload;
	private String ufPaju;
	
	public UploadedFile uploadedFile;
    public String fileName;
    public String fileType;
    public int fileTypeInt;
    ByteArrayInputStream bis = null;
    
    private CcbParticipantes participanteSelecionado = new CcbParticipantes();
    
    private boolean addParticipante;
    
    private CcbParticipantes socioSelecionado = new CcbParticipantes();
    private boolean addSocio;
    
    private CcbParticipantes selectedParticipante = new CcbParticipantes(); 
    
	private boolean addSegurador;
	
	private boolean mostrarDadosOcultos;
	private Segurado seguradoSelecionado;
	
    private CcbProcessosJudiciais processoSelecionado;
    
    private ContasPagar despesaSelecionada;
     
    private CcbContrato objetoCcb = new CcbContrato();
    
    private List<CcbContrato> listaCcbs = new ArrayList<CcbContrato>();
    
    private ArrayList<UploadedFile> filesList = new ArrayList<UploadedFile>();
    
    String tituloPagadorRecebedorDialog = "";
    
    private ContratoCobranca objetoContratoCobranca;
   
    private List<ContratoCobranca> listaContratosConsultar = new ArrayList<ContratoCobranca>();
    

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
	PorcentagemPorExtenso porcentagemPorExtenso = new PorcentagemPorExtenso();
	
	SimulacaoVO simulador = new SimulacaoVO();

	private boolean blockForm = false;
	
	public void bloquearForm() {
		PrimeFaces current = PrimeFaces.current();
		if(blockForm) {
			current.executeScript("PF('blockForm').show();");
		} else {
			current.executeScript("PF('blockForm').hide();");
		}
	}
	
	public boolean isBlockForm() {
		return blockForm;
	}

	public void setBlockForm(boolean blockForm) {
		this.blockForm = blockForm;
	}
	

	public void removerSegurado(Segurado segurado) {
		this.objetoCcb.getListSegurados().remove(segurado);		
		if(!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
			if(this.objetoCcb.getObjetoContratoCobranca().getListSegurados().contains(segurado)) {
				this.objetoCcb.getObjetoContratoCobranca().getListSegurados().remove(segurado);
			}
		}
	}
	
	public void concluirSegurado() {
		this.tituloPagadorRecebedorDialog = "";
		this.updatePagadorRecebedor = "";
		this.seguradoSelecionado.setPosicao(this.objetoCcb.getListSegurados().size() + 1);
		if(!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
			if(!this.objetoCcb.getObjetoContratoCobranca().getListSegurados().contains(this.seguradoSelecionado)) {		
				this.seguradoSelecionado.setContratoCobranca(this.objetoContratoCobranca);
				this.objetoCcb.getObjetoContratoCobranca().getListSegurados().add(seguradoSelecionado);
			}
		}
		this.objetoCcb.getListSegurados().add(this.seguradoSelecionado);
		this.addSegurador= false;
	}
	
	public void pesquisaSegurado() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.getPagadoresRecebedores();		
		this.tipoPesquisa = "Segurado";
		this.tituloPagadorRecebedorDialog = "Segurados";
		this.updatePagadorRecebedor = " :form:SeguradoresPanel ";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
	}
		
	public void enviarMoneyPlus() {
		FacesContext context = FacesContext.getCurrentInstance();

		BmpDigitalCCBMB bmpMB = new BmpDigitalCCBMB();	
		boolean validacao = true;
		
		/**
		 * TRATA EMITENTE
		 */
		PagadorRecebedor eminenteDTO = null;
		for (CcbParticipantes pessoa : this.objetoCcb.getListaParticipantes()) {
			if (pessoa.getTipoParticipante().equals("EMITENTE")) {
				eminenteDTO = pessoa.getPessoa();
			}
		}
		if (eminenteDTO != null) {
			// se não existe pessoa na money plus, cria!
			if (eminenteDTO.getCodigoMoneyPlus() == null || eminenteDTO.getCodigoMoneyPlus().equals("")) {
				bmpMB.enviaEmitente(eminenteDTO, objetoCcb.getEmitentePrincipal().getNacionalidade());	
				bmpMB.enviaEndereco(eminenteDTO);
			}			
		} else {	
			validacao = false;
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[MoneyPlus] Envia Pessoa - Emitente não encontrado", ""));
		}
		
		/**
		 * TRATA FIDUCIANTE
		 */
		PagadorRecebedor fiducianteDTO = null;		
		if (validacao) {			
			for (CcbParticipantes pessoa : objetoCcb.getListaParticipantes()) {
				if (pessoa.getTipoParticipante().equals("TERCEIRO GARANTIDOR")) {
					fiducianteDTO = pessoa.getPessoa();
				}
			}
			if (fiducianteDTO != null) {
				// se não existe pessoa na money plus, cria!
				if (fiducianteDTO.getCodigoMoneyPlus() == null || fiducianteDTO.getCodigoMoneyPlus().equals("")) {
					bmpMB.enviaEmitente(fiducianteDTO, objetoCcb.getEmitentePrincipal().getNacionalidade());	
					bmpMB.enviaEndereco(fiducianteDTO);
				}			
			} else {	
				fiducianteDTO = eminenteDTO;
			}
		}
		
		/**
		 * ENVIA PROPOSTA
		 */
		if (validacao) {			
			bmpMB.enviaProposta(eminenteDTO, fiducianteDTO, this.objetoCcb.getNumeroParcelasPagamento(), this.objetoCcb.getTaxaDeJurosMes(), this.objetoCcb.getValorIOF(), this.objetoCcb.getNumeroBanco(), 
					this.objetoCcb.getAgencia(), this.objetoCcb.getContaCorrente(), this.objetoCcb.getValorCredito(), this.objetoCcb.getNumeroCcb(), this.objetoCcb.getVencimentoPrimeiraParcelaPagamento(), this.objetoCcb.getValorParcela());
		}		
	}
		
	public void pesquisaParticipante() {
		this.tituloPagadorRecebedorDialog = "Participante";
		this.tipoPesquisa = "Participante";
		this.updatePagadorRecebedor = ":form:ParticipantesPanel :form:Dados";
		//this.participanteSelecionado = new CcbParticipantes();
		//this.participanteSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void concluirParticipante() {
		CcbParticipantesDao ccbDao = new CcbParticipantesDao();
		
		this.participanteSelecionado.setTipoOriginal(participanteSelecionado.getTipoParticipante());

		this.objetoCcb.getListaParticipantes().add(this.participanteSelecionado);
		criarPagadorRecebedorNoSistema(this.participanteSelecionado.getPessoa());
		this.participanteSelecionado.setPessoa(this.objetoPagadorRecebedor);
		CcbParticipantesDao ccbPartDao = new CcbParticipantesDao();
		if(ccbPartDao.findByFilter("pessoa", this.participanteSelecionado.getPessoa()).size() > 0){
			this.participanteSelecionado.setId(ccbPartDao.findByFilter("pessoa", this.participanteSelecionado.getPessoa()).get(0).getId());
			ccbDao.merge(this.participanteSelecionado);
		} else {
			ccbDao.create(this.participanteSelecionado);
		}
		
		if(CommonsUtil.mesmoValor(participanteSelecionado.getTipoParticipante(), "EMITENTE")) {
			if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal())) {
				objetoCcb.setEmitentePrincipal(participanteSelecionado);
			}
		}
		
		atualizaDadosEmitente();
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		this.addParticipante = false;
	}

	
	
	public void editarParticipante(CcbParticipantes participante) {
		this.addParticipante = true;
		this.participanteSelecionado = new CcbParticipantes();
		this.setParticipanteSelecionado(participante);
		this.removerParticipante(participante);
	}
	
	public void removerParticipante(CcbParticipantes participante) {
		this.objetoCcb.getListaParticipantes().remove(participante);
	}
	
	public void clearParticipante() {
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void pesquisaSocio() {
		this.tituloPagadorRecebedorDialog = "Socio";
		this.tipoPesquisa = "Socio";
		this.updatePagadorRecebedor = ":form:SociosPanel ";
		//this.socioSelecionado = new CcbParticipantes();
		//this.socioSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void concluirSocio() {
		CcbParticipantesDao ccbDao = new CcbParticipantesDao();
		this.getParticipanteSelecionado().getSocios().add(socioSelecionado); 
		criarPagadorRecebedorNoSistema(this.socioSelecionado.getPessoa());
		CcbParticipantesDao ccbPartDao = new CcbParticipantesDao();
		
		//colocado merge de pagRece
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		pagadorRecebedorDao.merge(this.socioSelecionado.getPessoa());
		if(ccbPartDao.findByFilter("pessoa", this.socioSelecionado.getPessoa()).size() > 0){
			this.socioSelecionado.setId(ccbPartDao.findByFilter("pessoa", this.socioSelecionado.getPessoa()).get(0).getId());
			ccbDao.merge(this.socioSelecionado);
		} else {
			ccbDao.create(this.socioSelecionado);
		}
		this.socioSelecionado = new CcbParticipantes();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSocio = false;
	}
	
	public void editarSocio(CcbParticipantes socio) {
		this.addSocio = true;
		this.socioSelecionado = new CcbParticipantes();
		this.setSocioSelecionado(socio);
		this.removerSocio(socio);
	}
	
	public void removerSocio(CcbParticipantes socio) {
		this.getParticipanteSelecionado().getSocios().remove(socio);
	}
	
	public void clearSocio() {
		this.socioSelecionado = new CcbParticipantes();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public ContasPagar buscarDespesa(String descricao, String numeroContrato) {
		try {
			ContasPagarDao contasPagarDao = new ContasPagarDao();
			List<ContasPagar> lista = contasPagarDao.buscarDespesa(descricao, numeroContrato);
			if(lista.size() > 0)
				return lista.get(0);
		} catch (Exception e) {
		}
		return null;
	}
	
	public void criarDespesa(String descricao, BigDecimal valor) {
		criarDespesa(descricao, valor, "Boleto");
	}
	
	public void criarDespesa(String descricao, BigDecimal valor, String formaTransferencia) {
		despesaSelecionada = new ContasPagar();
		despesaSelecionada.setDescricao(descricao);
		despesaSelecionada.setValor(valor);
		despesaSelecionada.setFormaTransferencia(formaTransferencia);
		addDespesa();
	}
	
	public void addDespesa() {
		despesaSelecionada.setTipoDespesa("C");
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			despesaSelecionada.setNumeroDocumento(objetoCcb.getObjetoContratoCobranca().getNumeroContrato());
			despesaSelecionada.setPagadorRecebedor(objetoCcb.getObjetoContratoCobranca().getPagador());
			despesaSelecionada.setResponsavel(objetoCcb.getObjetoContratoCobranca().getResponsavel());
			
			if(CommonsUtil.mesmoValor(despesaSelecionada.getDescricao(), "Crédito CCI")) {
				if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "TED")) {
					if(!CommonsUtil.semValor(objetoCcb.getCCBNome())) {
						despesaSelecionada.setNomeTed(objetoCcb.getCCBNome());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBCNPJ())) {
						despesaSelecionada.setCpfTed(objetoCcb.getCCBCNPJ());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBBanco())) {
						despesaSelecionada.setBancoTed(objetoCcb.getCCBBanco());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBAgencia())) {
						despesaSelecionada.setAgenciaTed(objetoCcb.getCCBAgencia());
					}
					if(!CommonsUtil.semValor(objetoCcb.getCCBCC())) {
						despesaSelecionada.setContaTed(objetoCcb.getCCBCC());
					}
					if(!CommonsUtil.semValor(objetoCcb.getCCBCC())) {
						despesaSelecionada.setContaTed(objetoCcb.getCCBCC());
					}
					
					if(!CommonsUtil.semValor(objetoCcb.getCCBDigito())) {
						despesaSelecionada.setDigitoContaTed(objetoCcb.getCCBDigito());
					}
					
					if(!CommonsUtil.semValor(despesaSelecionada.getNomeTed())) {
						objetoCcb.getObjetoContratoCobranca().setNomeBancarioContaPagar(despesaSelecionada.getNomeTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getCpfTed())) {
						objetoCcb.getObjetoContratoCobranca().setCpfCnpjBancarioContaPagar(despesaSelecionada.getCpfTed());
					}				
					if(!CommonsUtil.semValor(despesaSelecionada.getBancoTed())) {
						objetoCcb.getObjetoContratoCobranca().setBancoBancarioContaPagar(despesaSelecionada.getBancoTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getAgenciaTed())) {
						objetoCcb.getObjetoContratoCobranca().setAgenciaBancarioContaPagar(despesaSelecionada.getAgenciaTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getContaTed())) {
						objetoCcb.getObjetoContratoCobranca().setContaBancarioContaPagar(despesaSelecionada.getContaTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getDigitoContaTed())) {
						objetoCcb.getObjetoContratoCobranca().setDigitoContaBancarioContaPagar(despesaSelecionada.getDigitoContaTed());
					}
				} else if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "Pix")) {
					if(!CommonsUtil.semValor(objetoCcb.getCCBPix())) {
						despesaSelecionada.setPix(objetoCcb.getCCBPix());
					}
					
					if(!CommonsUtil.semValor(despesaSelecionada.getPix())) {
						//objetoCcb.getObjetoContratoCobranca().se(despesaSelecionada.getPix());
					}
				}
			} else if(CommonsUtil.mesmoValor(despesaSelecionada.getDescricao(), "Transferência")) {
				if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "TED")) {
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoNome())) {
						despesaSelecionada.setNomeTed(objetoCcb.getIntermediacaoNome());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoCNPJ())) {
						despesaSelecionada.setCpfTed(objetoCcb.getIntermediacaoCNPJ());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoBanco())) {
						despesaSelecionada.setBancoTed(objetoCcb.getIntermediacaoBanco());
					}
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoAgencia())) {
						despesaSelecionada.setAgenciaTed(objetoCcb.getIntermediacaoAgencia());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoCC())) {
						despesaSelecionada.setContaTed(objetoCcb.getIntermediacaoCC());
					}
				} else if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "Pix")) {
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoPix())) {
						despesaSelecionada.setPix(objetoCcb.getIntermediacaoPix());
					}
				}
			}
			
			if(!this.objetoCcb.getObjetoContratoCobranca().getListContasPagar().contains(this.despesaSelecionada)) {	
				despesaSelecionada.setContrato(objetoCcb.getObjetoContratoCobranca());
				//objetoCcb.getObjetoContratoCobranca().getListContasPagar().add(despesaSelecionada);
			}
		}
		
		this.objetoCcb.getDespesasAnexo2().add(despesaSelecionada);
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		if (!CommonsUtil.semValor(despesaSelecionada.getContaPagarOriginal()) && 
				despesaSelecionada.getContaPagarOriginal().getId() > 0) {
			despesaSelecionada.getContaPagarOriginal().setEditada(true);
			despesaSelecionada.getContaPagarOriginal().setContrato(null);
			this.objetoContratoCobranca.getListContasPagar().remove(despesaSelecionada.getContaPagarOriginal());
			this.objetoCcb.getDespesasAnexo2().remove(despesaSelecionada.getContaPagarOriginal());
			contasPagarDao.merge(despesaSelecionada.getContaPagarOriginal());
		} 
		
		if (despesaSelecionada.getId() <= 0) {
			this.despesaSelecionada.setDataCriacao(DateUtil.gerarDataHoje());
			this.despesaSelecionada.setUserCriacao(getLoginBean().getUsername());
			contasPagarDao.create(despesaSelecionada);
		} else {
			contasPagarDao.merge(despesaSelecionada);
		}
		
		if(CommonsUtil.semValor(this.objetoCcb.getValorDespesas())) {
			this.objetoCcb.setValorDespesas(BigDecimal.ZERO);
		}
		calcularValorDespesa();
		contasPagarDao.create(despesaSelecionada);
		despesaSelecionada = new ContasPagar();
		calculaValorLiquidoCredito();
		//calcularSimulador();
	}
	
	public void removeDespesa(ContasPagar conta) {
		this.objetoCcb.getDespesasAnexo2().remove(conta);
		conta.setNumeroDocumento(null);
		if(!CommonsUtil.semValor(conta.getContrato())) {
			conta.setContrato(null);
		}
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			if(this.objetoCcb.getObjetoContratoCobranca().getListContasPagar().contains(conta)) {
				objetoCcb.getObjetoContratoCobranca().getListContasPagar().remove(conta);
			}
		}
		calcularValorDespesa();
	}
	
	public void editDespesa(ContasPagar conta) {
		despesaSelecionada = new ContasPagar(conta);
		despesaSelecionada.setContaPagarOriginal(conta);
		//despesaSelecionada = conta;
	}
	
	public void addProcesso() {
		processoSelecionado.getContaPagar().setValor(processoSelecionado.getValorAtualizado());
		processoSelecionado.getContaPagar().setDescricao("Processo N°: " + processoSelecionado.getNumero());
		processoSelecionado.getContaPagar().setContrato(objetoContratoCobranca);
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			processoSelecionado.getContaPagar().setNumeroDocumento(objetoCcb.getObjetoContratoCobranca().getNumeroContrato());
			processoSelecionado.getContaPagar().setPagadorRecebedor(objetoCcb.getObjetoContratoCobranca().getPagador());
			processoSelecionado.getContaPagar().setResponsavel(objetoCcb.getObjetoContratoCobranca().getResponsavel());
			processoSelecionado.getContaPagar().setTipoDespesa("C");
			processoSelecionado.getContaPagar().setFormaTransferencia("Boleto");
			
			if(!this.objetoCcb.getObjetoContratoCobranca().getListProcessos().contains(this.processoSelecionado)) {
				processoSelecionado.setContrato(objetoCcb.getObjetoContratoCobranca());
				//objetoCcb.getObjetoContratoCobranca().getListContasPagar().add(despesaSelecionada);
			}
		}
		this.objetoCcb.getProcessosJucidiais().add(processoSelecionado);
		calcularValorDespesa();
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		contasPagarDao.create(processoSelecionado.getContaPagar());
		CcbProcessosJudiciaisDao processoDao = new CcbProcessosJudiciaisDao();
		processoDao.create(processoSelecionado);
		processoSelecionado = new CcbProcessosJudiciais();
	}
	
	public void removeProcesso(CcbProcessosJudiciais processo) {
		this.objetoCcb.getProcessosJucidiais().remove(processo);
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			if(this.objetoCcb.getObjetoContratoCobranca().getListProcessos().contains(processo)) {
				objetoCcb.getObjetoContratoCobranca().getListProcessos().remove(processo);
			}
		}
		calcularValorDespesa();
	}
		
	public void pesquisaContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listaContratosConsultar = cDao.consultaContratosCCBs();
	}
	
	public void populateSelectedContratoCobranca() {
		ContratoCobranca contrato = objetoContratoCobranca;
		
		CcbDao ccbDao = new CcbDao();
		 List<CcbContrato> ccbs = ccbDao.findByFilter("objetoContratoCobranca", contrato);
		
		if(!CommonsUtil.semValor(ccbs)) {			
			this.objetoCcb = ccbs.get(0);
			objetoContratoCobranca = this.objetoCcb.getObjetoContratoCobranca();
		}else {
			//listarDownloads();
			this.objetoCcb.setObjetoContratoCobranca(contrato);
			this.objetoCcb.setNumeroOperacao(contrato.getNumeroContrato());
			this.objetoCcb.setUsarNovoCustoEmissao(true);
		}

		blockForm = !this.objetoCcb.getObjetoContratoCobranca().isAgRegistro();
		
		DateFormat dateFormat = new SimpleDateFormat("MMyy");  
		String strDate = dateFormat.format(objetoCcb.getDataDeEmissao());  
		String numeroCci = objetoContratoCobranca.getNumeroContrato() + strDate;
		objetoCcb.setNumeroCcb(numeroCci);
		
		if (CommonsUtil.mesmoValor(contrato.getAvaliacaoLaudo(), "Compass")) {
			this.objetoCcb.setElaboradorNome("Compass Avaliações Imobiliárias");
			this.objetoCcb.setElaboradorCrea("CAU A40301-6");
			this.objetoCcb.setResponsavelNome("Ana Maria F. Cooke");
			this.objetoCcb.setResponsavelCrea("CAU A40301-6");
		} else if (CommonsUtil.mesmoValor(contrato.getAvaliacaoLaudo(), "Galache")) {
			this.objetoCcb.setElaboradorNome("Galache Engenharia Ltda");
			this.objetoCcb.setElaboradorCrea("1009877");
			this.objetoCcb.setResponsavelNome("Tales R. S. Galache");
			this.objetoCcb.setResponsavelCrea("5060563873-D");
		}
		
		ImovelCobranca imovel = contrato.getImovel();
		this.objetoCcb.setCepImovel(imovel.getCep());	
		this.objetoCcb.setNumeroImovel(imovel.getNumeroMatricula());
		this.objetoCcb.setInscricaoMunicipal(CommonsUtil.somenteNumeros(imovel.getInscricaoMunicipal()));
		this.objetoCcb.setCidadeImovel(imovel.getCidade());
		this.objetoCcb.setCartorioImovel(CommonsUtil.somenteNumeros(imovel.getNumeroCartorio()));
		this.objetoCcb.setUfImovel(imovel.getEstado());
		String[] endereco = imovel.getEndereco().split(Pattern.quote(","));
		if(endereco.length > 0) {
			this.objetoCcb.setLogradouroRuaImovel(endereco[0]);
		}
		if(endereco.length > 1) {
			this.objetoCcb.setLogradouroNumeroImovel(CommonsUtil.removeEspacos(endereco[1]));
		}
		this.objetoCcb.setBairroImovel(imovel.getBairro());
		this.objetoCcb.setDataCompraImovel(imovel.getDataCompra());
		//listaArquivos();
		
		//Popular Campos para Simulação
		this.objetoCcb.setVlrImovel(contrato.getValorMercadoImovel());
		this.objetoCcb.setVendaLeilao(contrato.getValorVendaForcadaImovel());
		this.objetoCcb.setPrecoVendaCompra(contrato.getValorCompraVenda());
		objetoCcb.setValorCredito(objetoContratoCobranca.getValorAprovadoCCB());
		objetoCcb.setTaxaDeJurosMes(objetoContratoCobranca.getTaxaAprovada());
		objetoCcb.setPrazo(objetoContratoCobranca.getPrazoAprovadoCCB().toString());
		objetoCcb.setSistemaAmortizacao(objetoContratoCobranca.getTipoCalculoAprovadoCCB());
		objetoCcb.setCarencia(CommonsUtil.stringValue(objetoContratoCobranca.getCarenciaComite()));
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoValorComite(), "liquido")) {
			objetoCcb.setTipoCalculoFinal('L');
		} else {
			objetoCcb.setTipoCalculoFinal('B');
		}
		
		//Calcular Parcelas
		prepararDespesasContrato();
		calcularValorDespesa();
		calcularSimulador();
		
		this.objetoContratoCobranca = null;
	}

	private void listarDownloads() {
		listaTipoDownload.clear();
		listaTipoDownload.add("TODOS");
		listaTipoDownload.add("CCI");
		listaTipoDownload.add("Carta Split");
		listaTipoDownload.add("AnexoII");
		listaTipoDownload.add("Cessao");
		listaTipoDownload.add("Endossos Em Preto");
		listaTipoDownload.add("FinanciamentoCCI");
		listaTipoDownload.add("Aquisicao/Emprestimo");
		listaTipoDownload.add("Ficha Cadastro Nova");
		listaTipoDownload.add("Ficha PPE - PF");
		listaTipoDownload.add("Ficha PLD e FT - PJ");
		listaTipoDownload.add("Declaração Não União Estavel");
		listaTipoDownload.add("Declaração de União Estavel");
		listaTipoDownload.add("Declaração Destinação Recursos");
		listaTipoDownload.add("Termo Responsabilidade Paju Vencido");
		listaTipoDownload.add("Termo Paju Estados");
		listaTipoDownload.add("Termo Incomunicabilidade Imovel");
		listaTipoDownload.add("Ficha Cadastro");
		listaTipoDownload.add("Averbacao");
		listaTipoDownload.add("Aditamento Carta de Desconto");
		listaTipoDownload.add("Aditamento Data Parcela");
	}
	
	private void listarDownloadsAditamento() {
		listaTipoDownload.clear();
		listaTipoDownload.add("Aditamento Carta de Desconto");
		listaTipoDownload.add("Aditamento Data Parcela");
		listaTipoDownload.add("Endossos Em Preto");
		listaTipoDownload.add("Carta Split");
		listaTipoDownload.add("Cessao");
		listaTipoDownload.add("AnexoII");
		listaTipoDownload.add("AnexoI");
	}

	public void clearContratoCobranca() {
		this.objetoContratoCobranca = null;
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listaContratosConsultar = cDao.consultaContratosCCBs();
	}
	
	public List<String> completeBancosNome(String query) {
        String queryLowerCase = query.toLowerCase();
        List<String> bancos = new ArrayList<>();
        for(BancosEnum banco : BancosEnum.values()) {
        	String bancoStr = banco.getNome().toString();
        	bancos.add(bancoStr);
        }
        return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}
	
	public List<String> completeBancosCodigo(String query) {
        String queryLowerCase = query.toLowerCase();
        List<String> bancos = new ArrayList<>();
        for(BancosEnum banco : BancosEnum.values()) {
        	String bancoStr = banco.getCodigo().toString();
        	bancos.add(bancoStr);
        }
        return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}
	
	public void populateCodigosBanco() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNomeBanco(), banco.getNome().toString())) {
				this.objetoCcb.setNumeroBanco(banco.getCodigo());
				break;
			}
		}
	}
	
	public void populateNomesBanco() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNumeroBanco(),banco.getCodigo())) {
				this.objetoCcb.setNomeBanco(banco.getNome());
				//PrimeFaces.current().ajax().update(":nomeBanco");
				break;
			}
		}
	}
	
	public void populateCodigosBancoVendedor() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNomeBancoVendedor(), banco.getNome().toString())) {
				this.objetoCcb.setNumeroBancoVendedor(banco.getCodigo());
				break;
			}
		}
	}
	
	public void populateNomesBancoVendedor() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNumeroBancoVendedor(),banco.getCodigo())) {
				this.objetoCcb.setNomeBancoVendedor(banco.getNome());
				//PrimeFaces.current().ajax().update(":nomeBanco");
				break;
			}
		}
	}

	String updatePagadorRecebedor = ":form";

	public void pesquisaTestemunha1() {
		this.tituloPagadorRecebedorDialog = "Testemunha 1";
		this.tipoPesquisa = "Testemunha1";
		this.updatePagadorRecebedor = ":form:Dados";
		this.testemunha1Selecionado = new PagadorRecebedor();
	}
	
	public void pesquisaTestemunha2() {
		this.tituloPagadorRecebedorDialog = "Testemunha 2";
		this.tipoPesquisa = "Testemunha2";
		this.updatePagadorRecebedor = ":form:Dados";
		this.testemunha2Selecionado = new PagadorRecebedor();
	}
	
	public void populateParcelaSeguro() {
		if(this.objetoCcb.getNumeroParcelasPagamento() != null) {
			this.objetoCcb.setNumeroParcelasDFI(this.objetoCcb.getNumeroParcelasPagamento());
			this.objetoCcb.setNumeroParcelasMIP(this.objetoCcb.getNumeroParcelasPagamento());
		} if(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento() != null) {
			this.objetoCcb.setVencimentoPrimeiraParcelaDFI(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
			this.objetoCcb.setVencimentoPrimeiraParcelaMIP(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
		} if(this.objetoCcb.getVencimentoUltimaParcelaPagamento() != null) {
			this.objetoCcb.setVencimentoUltimaParcelaDFI(this.objetoCcb.getVencimentoUltimaParcelaPagamento());
			this.objetoCcb.setVencimentoUltimaParcelaMIP(this.objetoCcb.getVencimentoUltimaParcelaPagamento());
		}
	}
	
	public void calculaDatavencimentoFinal() {
		Integer parcelas = CommonsUtil.integerValue(this.objetoCcb.getNumeroParcelasPagamento());	
		parcelas -= 1;
		Calendar c = Calendar.getInstance();
		c.setTime(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
		c.add(Calendar.MONTH, parcelas);
		this.objetoCcb.setVencimentoUltimaParcelaPagamento(c.getTime());
	}
	
	public void removeValor(CcbProcessosJudiciais processo) {
		this.objetoCcb.getProcessosJucidiais().remove(processo);
		calcularValorDespesa();
	}
	
	public void calcularValorDespesa() {
		BigDecimal total =  BigDecimal.ZERO;
		
		if(!this.objetoCcb.getDespesasAnexo2().isEmpty()) {
			for(ContasPagar despesas : this.objetoCcb.getDespesasAnexo2()) {
				if(!CommonsUtil.semValor(despesas.getValor()))
					total = total.add(despesas.getValor());
			}
		}
		
		if(!this.objetoCcb.getProcessosJucidiais().isEmpty()) {
			for(CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
				if(!CommonsUtil.semValor(processo.getValorAtualizado()))
					total = total.add(processo.getValorAtualizado());
			}
		}
		this.objetoCcb.setValorDespesas(total);
	}
	
	public void atualizaValorTransferencia() {
		ContasPagar despesaTransferencia = buscarDespesa("Transferência", objetoCcb.getObjetoContratoCobranca().getNumeroContrato());
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		if(!CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getCobrarComissaoCliente(), "Sim")) 
			return;		
		if(CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getTipoCobrarComissaoCliente(), "Real")) 
			return;		
		if(CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getBrutoLiquidoCobrarComissaoCliente(), "Bruto") && 
				CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getTipoValorComite(), "bruto")) 
			return;
		
		BigDecimal valorTranferencia = BigDecimal.ZERO;
		BigDecimal comissao = BigDecimal.ZERO;
		if(!CommonsUtil.semValor( objetoCcb.getObjetoContratoCobranca().getComissaoClientePorcentagem())) {
			comissao =  objetoCcb.getObjetoContratoCobranca().getComissaoClientePorcentagem();
			comissao = comissao.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
		}
		if(CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getBrutoLiquidoCobrarComissaoCliente(), "Liquido")){
			if(CommonsUtil.semValor(objetoCcb.getValorLiquidoCredito())) 
				return;
			valorTranferencia = (objetoCcb.getValorLiquidoCredito().add(objetoCcb.getIntermediacaoValor())).multiply(comissao);
		} else if (CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getBrutoLiquidoCobrarComissaoCliente(), "Bruto") && 
				CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getTipoValorComite(), "liquido")) {
			if(CommonsUtil.semValor(objetoCcb.getValorCredito())) 
				return;
			valorTranferencia = objetoCcb.getValorCredito().multiply(comissao);
		}
		
		if(CommonsUtil.mesmoValor(valorTranferencia, objetoCcb.getIntermediacaoValor())) 
			return;
		if(CommonsUtil.semValor(despesaTransferencia)) 
			return;
		
		objetoCcb.setIntermediacaoValor(valorTranferencia);
		despesaTransferencia.setValor(valorTranferencia);
		calcularValorDespesa();
		calculaValorLiquidoCredito();
		contasPagarDao.merge(despesaTransferencia);
	}
	
	private void prepararDespesasContrato() {
		//Adicionar Despesas
		this.objetoCcb.getProcessosJucidiais().clear();
		for(CcbProcessosJudiciais processo : objetoContratoCobranca.getListProcessos()) {
			if(!processo.isSelecionadoComite()) {
				continue;
			}
			
			if(!this.objetoCcb.getProcessosJucidiais().contains(processo)) {
				this.objetoCcb.getProcessosJucidiais().add(processo);
			}
		}
		
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		
		ContasPagar despesaLaudo = buscarDespesa("Laudo", objetoContratoCobranca.getNumeroContrato());
		if(!CommonsUtil.semValor(objetoContratoCobranca.getValorLaudoPajuFaltante())) {
			BigDecimal valorLaudoPaju = objetoContratoCobranca.getValorLaudoPajuFaltante();
			if(objetoContratoCobranca.isPajuVencido()) {
				valorLaudoPaju = valorLaudoPaju.add(BigDecimal.valueOf(500));
			}
			if(CommonsUtil.semValor(objetoCcb.getLaudoDeAvaliacaoValor()) || CommonsUtil.semValor(despesaLaudo)) {
				criarDespesa("Laudo", valorLaudoPaju);
			} else {
				despesaLaudo.setValor(valorLaudoPaju);
				contasPagarDao.merge(despesaLaudo);
			}
			objetoCcb.setLaudoDeAvaliacaoValor(valorLaudoPaju);
		} else if(!CommonsUtil.semValor(despesaLaudo)) {
			despesaLaudo.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaLaudo);
			objetoContratoCobranca.getListContasPagar().remove(despesaLaudo);
			objetoCcb.setLaudoDeAvaliacaoValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaLaudo);
		}
		
		ContasPagar despesaTransferencia = buscarDespesa("Transferência", objetoContratoCobranca.getNumeroContrato());
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getCobrarComissaoCliente(), "Sim")) {
			BigDecimal valorTranferencia = BigDecimal.ZERO;
			BigDecimal comissao = BigDecimal.ZERO;
			if(CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoCobrarComissaoCliente(), "Real")) {
				if(!CommonsUtil.semValor(objetoContratoCobranca.getComissaoClienteValorFixo())) {
					valorTranferencia = objetoContratoCobranca.getComissaoClienteValorFixo();
				}
			} else if(CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoCobrarComissaoCliente(), "Porcentagem")) {
				if(!CommonsUtil.semValor(objetoContratoCobranca.getComissaoClientePorcentagem())) {
					comissao = objetoContratoCobranca.getComissaoClientePorcentagem();
					comissao = comissao.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
				}
				
				if(CommonsUtil.mesmoValor(objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente(), "Bruto") &&
						CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoValorComite(), "bruto")) {
					valorTranferencia = objetoContratoCobranca.getValorAprovadoCCB().multiply(comissao);
				} else if(CommonsUtil.mesmoValor(objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente(), "Bruto") &&
						CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoValorComite(), "liquido")) {
					valorTranferencia = objetoCcb.getValorCredito().multiply(comissao);
				} else if(CommonsUtil.mesmoValor(objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente(), "Liquido")) {
					valorTranferencia = objetoCcb.getValorLiquidoCredito().add(objetoCcb.getIntermediacaoValor()).multiply(comissao);
				}
			}
			
			objetoCcb.setIntermediacaoValor(valorTranferencia);
			String conta = "";
			if(!CommonsUtil.semValor(objetoContratoCobranca.getResponsavel().getConta())) {
				conta = objetoContratoCobranca.getResponsavel().getConta();
			}
			if(!CommonsUtil.semValor(objetoContratoCobranca.getResponsavel().getContaDigito())) {
				conta = conta + "-" + objetoContratoCobranca.getResponsavel().getContaDigito();
			}
			objetoCcb.setIntermediacaoBanco(objetoContratoCobranca.getResponsavel().getBanco());
			objetoCcb.setIntermediacaoAgencia(objetoContratoCobranca.getResponsavel().getAgencia());
			objetoCcb.setIntermediacaoCC(conta);
			objetoCcb.setIntermediacaoCNPJ(objetoContratoCobranca.getResponsavel().getCpfCnpjCC());
			objetoCcb.setIntermediacaoNome(objetoContratoCobranca.getResponsavel().getNomeCC());
			objetoCcb.setIntermediacaoPix(objetoContratoCobranca.getResponsavel().getPix());
			objetoCcb.setIntermediacaoTipoConta(objetoContratoCobranca.getResponsavel().getTipoConta());
			
			if(CommonsUtil.semValor(objetoCcb.getIntermediacaoValor()) || CommonsUtil.semValor(despesaTransferencia)) {
				criarDespesa("Transferência", valorTranferencia, "TED");
			} else {
				conta = "";
				if(!CommonsUtil.semValor(objetoContratoCobranca.getResponsavel().getConta())) {
					conta = objetoContratoCobranca.getResponsavel().getConta();
				}
				if(!CommonsUtil.semValor(objetoContratoCobranca.getResponsavel().getContaDigito())) {
					conta = conta + "-" +  objetoContratoCobranca.getResponsavel().getContaDigito();
				}
				despesaTransferencia.setBancoTed(objetoContratoCobranca.getResponsavel().getBanco());
				despesaTransferencia.setAgenciaTed(objetoContratoCobranca.getResponsavel().getAgencia());
				despesaTransferencia.setContaTed(conta);
				despesaTransferencia.setCpfTed(objetoContratoCobranca.getResponsavel().getCpfCnpjCC());
				despesaTransferencia.setNomeTed(objetoContratoCobranca.getResponsavel().getNomeCC());
				despesaTransferencia.setPix(objetoContratoCobranca.getResponsavel().getPix());
				
				despesaTransferencia.setValor(valorTranferencia);
				contasPagarDao.merge(despesaTransferencia);
			}
		} else if(!CommonsUtil.semValor(despesaTransferencia)) {
			despesaTransferencia.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaTransferencia);
			objetoContratoCobranca.getListContasPagar().remove(despesaTransferencia);
			objetoCcb.setIntermediacaoValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaTransferencia);
		}
		
		ContasPagar despesaIQ = buscarDespesa("IQ", objetoContratoCobranca.getNumeroContrato());
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getDivida(), "Sim")) {
			if(CommonsUtil.semValor(objetoCcb.getIqValor())|| CommonsUtil.semValor(despesaIQ)) {
				criarDespesa("IQ", objetoContratoCobranca.getDividaValor());				
			} else {
				despesaIQ.setValor(objetoContratoCobranca.getDividaValor());
				contasPagarDao.merge(despesaIQ);
			}
			objetoCcb.setIqValor(objetoContratoCobranca.getDividaValor());
		} else if(!CommonsUtil.semValor(despesaIQ)) {
			despesaIQ.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaIQ);
			objetoContratoCobranca.getListContasPagar().remove(despesaIQ);
			objetoCcb.setIqValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaIQ);
		}
		
		ContasPagar despesaIPTU = buscarDespesa("IPTU", objetoContratoCobranca.getNumeroContrato());
		if(!CommonsUtil.semValor(objetoContratoCobranca.getDividaIPTU())) {
			if(CommonsUtil.semValor(objetoCcb.getIptuEmAtrasoValor())|| CommonsUtil.semValor(despesaIPTU)) {
				criarDespesa("IPTU", objetoContratoCobranca.getDividaIPTU());
			} else {
				despesaIPTU.setValor(objetoContratoCobranca.getDividaIPTU());
				contasPagarDao.merge(despesaIPTU);
			}
			objetoCcb.setIptuEmAtrasoValor(objetoContratoCobranca.getDividaIPTU());
		} else if(!CommonsUtil.semValor(despesaIPTU)) {
			despesaIPTU.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaIPTU);
			objetoContratoCobranca.getListContasPagar().remove(despesaIPTU);
			objetoCcb.setIptuEmAtrasoValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaIPTU);
		}
		
		ContasPagar despesaCondominio = buscarDespesa("Condomínio", objetoContratoCobranca.getNumeroContrato());
		if(!CommonsUtil.semValor(objetoContratoCobranca.getDividaCondominio())) {
			if(CommonsUtil.semValor(objetoCcb.getCondominioEmAtrasoValor())|| CommonsUtil.semValor(despesaCondominio)) {
				criarDespesa("Condomínio", objetoContratoCobranca.getDividaCondominio());
			} else {
				despesaCondominio.setValor(objetoContratoCobranca.getDividaCondominio());
				contasPagarDao.merge(despesaCondominio);
			}
			objetoCcb.setCondominioEmAtrasoValor(objetoContratoCobranca.getDividaCondominio());
		} else if(!CommonsUtil.semValor(despesaCondominio)) {
			despesaCondominio.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaCondominio);
			objetoContratoCobranca.getListContasPagar().remove(despesaCondominio);
			objetoCcb.setCondominioEmAtrasoValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaCondominio);
		}
		
		ContasPagar despesaAverbacao = buscarDespesa("Averbação", objetoContratoCobranca.getNumeroContrato());
		if(objetoContratoCobranca.getListAverbacao().size() > 0) {		
			BigDecimal averbacaoTotal = BigDecimal.ZERO;
			for(Averbacao averbacao : objetoContratoCobranca.getListAverbacao()) {
				averbacaoTotal = averbacaoTotal.add(averbacao.getValor());
			}
			if (CommonsUtil.semValor(objetoCcb.getAverbacaoValor()) || CommonsUtil.semValor(despesaAverbacao)) {
				criarDespesa("Averbação", averbacaoTotal);
			} else {
				despesaAverbacao.setValor(averbacaoTotal);
				contasPagarDao.merge(despesaAverbacao);
			}
			objetoCcb.setAverbacaoValor(averbacaoTotal);
		} else if(!CommonsUtil.semValor(despesaAverbacao)) {
			despesaAverbacao.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaAverbacao);
			objetoContratoCobranca.getListContasPagar().remove(despesaAverbacao);
			objetoCcb.setAverbacaoValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaAverbacao);
		}
		
		ContasPagar despesaRegistro = buscarDespesa("Cartório", objetoContratoCobranca.getNumeroContrato());
		if(CommonsUtil.semValor(despesaRegistro))
			despesaRegistro = buscarDespesa("Registro", objetoContratoCobranca.getNumeroContrato());
		if(!CommonsUtil.semValor(objetoContratoCobranca.getValorCartorio())) {
			RegistroImovelTabelaDao rDao = new RegistroImovelTabelaDao();
			BigDecimal valorRegistro = objetoContratoCobranca.getValorCartorio();
			if(CommonsUtil.semValor(objetoCcb.getRegistroImovelValor())|| CommonsUtil.semValor(despesaRegistro)) {
				criarDespesa("Cartório", valorRegistro);
			} else {
				despesaRegistro.setValor(valorRegistro);
				contasPagarDao.merge(despesaRegistro);
			}
			objetoCcb.setRegistroImovelValor(valorRegistro);
			objetoCcb.setCustasCartorariasValor(valorRegistro);
		} else if(!CommonsUtil.semValor(despesaRegistro)) {
			despesaRegistro.setValor(BigDecimal.ZERO);
			objetoCcb.getDespesasAnexo2().remove(despesaRegistro);
			objetoContratoCobranca.getListContasPagar().remove(despesaRegistro);
			objetoCcb.setRegistroImovelValor(BigDecimal.ZERO);
			contasPagarDao.delete(despesaRegistro);
		}
		
		for(ContasPagar conta : objetoContratoCobranca.getListContasPagar()) {
			if(!objetoCcb.getDespesasAnexo2().contains(conta)) {
				objetoCcb.getDespesasAnexo2().add(conta);
			}
		}
	}
	
	public List<String> completeTextNomes(){
		List<String> listaNome = new ArrayList<>();
		listaNome.add("Galache Engenharia Ltda");
		listaNome.add("Tales R. S. Galache");
		listaNome.add("Compass Avaliações Imobiliárias");
		listaNome.add("Ana Maria F. Cooke");
		return listaNome.stream().collect(Collectors.toList());
	}
	
	public List<String> completeTextCrea(){
		List<String> listaCrea = new ArrayList<>();
		listaCrea.add("1009877");
		listaCrea.add("5060563873-D");
		listaCrea.add("CAU A40301-6");
		return listaCrea;
	}
	
	public String EmitirCcbPreContrato() {
		return EmitirCcbPreContrato("normal");
	}
	
	public String EmitirCcbPreContrato(String tipoEmissao) {
		try {
			clearFieldsInserirCcb();
			List<CcbContrato> ccbContratoDB = new ArrayList<CcbContrato>();
			CcbDao ccbDao = new CcbDao();
			ccbContratoDB = ccbDao.findByFilter("objetoContratoCobranca", objetoContratoCobranca);
			
			if(CommonsUtil.mesmoValor(tipoEmissao, "aditamento")) {
				listarDownloadsAditamento();
			}

			if (ccbContratoDB.size() > 0) {
				objetoCcb = ccbContratoDB.get(0);
				this.objetoContratoCobranca = objetoCcb.getObjetoContratoCobranca();
			} else {
				this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
			}	

			if (objetoCcb.getListaParticipantes().size() <= 0) {
				if(objetoContratoCobranca.getListaParticipantes().size() > 0) {
					for(CcbParticipantes participante : objetoContratoCobranca.getListaParticipantes()) {
						objetoCcb.getListaParticipantes().add(participante);
					}
				} else {
					//procura e setta pagador	
					this.selectedPagadorGenerico = getPagadorById(this.objetoContratoCobranca.getPagador().getId());
					pesquisaParticipante();
					populateSelectedPagadorRecebedor();	
					addParticipante = true;
					
					if(participanteSelecionado.isEmpresa()) {
						objetoCcb.setTipoPessoaEmitente("PJ");
					} else {
						objetoCcb.setTipoPessoaEmitente("PF");
					}
					
					participanteSelecionado.setTipoParticipante("EMITENTE");
					concluirParticipante();
					
					if(!CommonsUtil.semValor(objetoContratoCobranca.getPagador().getCpfConjuge())) {
						selectedPagadorGenerico = null;
						this.selectedPagadorGenerico = getPagadorByFilter("cpf", objetoContratoCobranca.getPagador().getCpfConjuge());
						if(!CommonsUtil.semValor(selectedPagadorGenerico)) {
							pesquisaParticipante();
							populateSelectedPagadorRecebedor();	
							addParticipante = true;
							
							if(participanteSelecionado.isEmpresa()) {
								objetoCcb.setTipoPessoaEmitente("PJ");
							} else {
								objetoCcb.setTipoPessoaEmitente("PF");
							}
							
							participanteSelecionado.setTipoParticipante("EMITENTE");
							concluirParticipante();
						}
					}
				}
			}
			
			populateSelectedContratoCobranca();
			calculaPorcentagemImovel();
			
			if(CommonsUtil.semValor(objetoCcb.getCpfTestemunha1())) {
				//larissa
				pesquisaTestemunha1();
				selectedPagadorGenerico = ccbDao.ConsultaTestemunha((long) 47572);
				populateSelectedPagadorRecebedor();
			}
			
			if(CommonsUtil.semValor(objetoCcb.getCpfTestemunha2())) {
				//bianca
				pesquisaTestemunha2();
				selectedPagadorGenerico = ccbDao.ConsultaTestemunha((long) 47570);
				populateSelectedPagadorRecebedor();
			}
			criarCcbNosistema();
			return "/Atendimento/Cobranca/Ccb.xhtml";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String emitirAditamento(Set<ContasPagar> despesas) {
		try {
			clearFieldsInserirCcb();
			//EmitirCcbPreContrato();
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			contratoCobrancaDao.merge(objetoContratoCobranca);
			List<CcbContrato> ccbContratoDB = new ArrayList<CcbContrato>();
			CcbDao ccbDao = new CcbDao();
			ccbContratoDB = ccbDao.findByFilter("objetoContratoCobranca", objetoContratoCobranca);
			if (ccbContratoDB.size() > 0) {
				objetoCcb = ccbContratoDB.get(0);
				this.objetoContratoCobranca = objetoCcb.getObjetoContratoCobranca();
			} else {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato de Cobrança: esse contrato não tem uma emissão feita!",""));
				return null;
			}
			this.objetoCcb.setDespesasAnexo2(new ArrayList<ContasPagar>(despesas));
			calcularValorDespesa();
			calcularSimulador();
			calculaValorLiquidoCredito();
			listarDownloadsAditamento();
			
			if(CommonsUtil.semValor(this.objetoCcb.getCarenciaAnterior()))
				this.objetoCcb.setCarenciaAnterior(this.objetoCcb.getCarencia());
			if(CommonsUtil.semValor(this.objetoCcb.getPrazoAnterior()))
				this.objetoCcb.setPrazoAnterior(this.objetoCcb.getPrazo());
			if(CommonsUtil.semValor(this.objetoCcb.getNumeroCcbAnterior()))
				this.objetoCcb.setNumeroCcbAnterior(this.objetoCcb.getNumeroCcb());
			if(CommonsUtil.semValor(this.objetoCcb.getDataDeEmissaoAnterior()))
				this.objetoCcb.setDataDeEmissaoAnterior(this.objetoCcb.getDataDeEmissao());
			if(CommonsUtil.semValor(this.objetoCcb.getVencimentoUltimaParcelaPagamentoAnterior()))
				this.objetoCcb.setVencimentoUltimaParcelaPagamentoAnterior(this.objetoCcb.getVencimentoUltimaParcelaPagamento());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public ContratoCobranca getContratoById(long idContrato) {
		ContratoCobranca contrato; //= new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();				
		contrato = cDao.findById(idContrato);	
		return contrato;
	}
	
	public PagadorRecebedor getPagadorById(long idPagador) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();				
		pagador = pDao.findById(idPagador);	
		return pagador;
	}
	
	public PagadorRecebedor getPagadorByFilter(String field, String value) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();				
		pagador = pDao.findByFilter(field, value).get(0);	
		return pagador;
	}
	
	public PagadorRecebedor getTestemunha(long idPagador) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();				
		pagador = pDao.findById(idPagador);	
		return pagador;
	}
	
	public void populateSelectedPagadorRecebedor() {
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		selectedPagadorGenerico = pDao.findById(selectedPagadorGenerico.getId());
		if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Participante")) {
			CcbParticipantesDao ccbPartDao = new CcbParticipantesDao();
			CcbParticipantes participanteBD = participanteSelecionado;
			if(ccbPartDao.findByFilter("pessoa", selectedPagadorGenerico).size() > 0){
				participanteBD = ccbPartDao.findByFilter("pessoa", selectedPagadorGenerico).get(0);
				participanteBD = (ccbPartDao.findById(participanteBD.getId()));
			}
			if(CommonsUtil.semValor(selectedPagadorGenerico.getCpf())) {
				participanteBD.setEmpresa(true);
			}
			if(this.selectedPagadorGenerico.getSexo() != null) {
				if(this.selectedPagadorGenerico.getSexo() == "MASCULINO") {
					participanteBD.setFeminino(false);
				} else if(this.selectedPagadorGenerico.getSexo() == "FEMININO") {
					participanteBD.setFeminino(true);
				} else {
					participanteBD.setFeminino(false);
				}
			}
			participanteBD.setPessoa(this.selectedPagadorGenerico);
			participanteSelecionado = participanteBD;
			
		} else if (CommonsUtil.mesmoValor(this.tipoPesquisa ,"Testemunha1")) {
			this.testemunha1Selecionado = (this.selectedPagadorGenerico);
			this.objetoCcb.setNomeTestemunha1(this.testemunha1Selecionado.getNome());
			this.objetoCcb.setCpfTestemunha1(this.testemunha1Selecionado.getCpf());
			this.objetoCcb.setRgTestemunha1(this.testemunha1Selecionado.getRg());
			
		} else if (CommonsUtil.mesmoValor(this.tipoPesquisa ,"Testemunha2")) {
			this.testemunha2Selecionado = (this.selectedPagadorGenerico);
			this.objetoCcb.setNomeTestemunha2(this.testemunha2Selecionado.getNome());
			this.objetoCcb.setCpfTestemunha2(this.testemunha2Selecionado.getCpf());
			this.objetoCcb.setRgTestemunha2(this.testemunha2Selecionado.getRg());
			
		} else if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Socio")) {
			if(this.selectedPagadorGenerico.getSexo() != null) {
				if(this.selectedPagadorGenerico.getSexo() == "MASCULINO") {
					this.socioSelecionado.setFeminino(false);
				} else if(this.selectedPagadorGenerico.getSexo() == "FEMININO") {
					this.socioSelecionado.setFeminino(true);
				} else {
					this.socioSelecionado.setFeminino(false);
				}
			}
			this.socioSelecionado.setPessoa(this.selectedPagadorGenerico);
		} 
		else if ( CommonsUtil.mesmoValor("Segurado", tipoPesquisa)) {
			this.seguradoSelecionado.setPessoa(this.selectedPagadorGenerico);
		}
	}
	
	public void pesquisaEmitente() {
		this.selectedParticipante = new CcbParticipantes();
	}
	
	public void populateSelectedParticipante() {
		objetoCcb.setEmitentePrincipal(selectedParticipante);
		atualizaDadosEmitente();
		this.selectedParticipante = new CcbParticipantes();
	}
	
	private void atualizaDadosEmitente() {
		PagadorRecebedor emitente = objetoCcb.getEmitentePrincipal().getPessoa();
		objetoCcb.setNomeEmitente(emitente.getNome());
		if(!CommonsUtil.semValor(emitente.getCpf())) {
			objetoCcb.setCpfEmitente(emitente.getCpf());
			this.objetoCcb.setCCBDocumento("CPF");
			this.objetoCcb.setCCBCNPJ(emitente.getCpf());
			objetoCcb.setTipoPessoaEmitente("PF");
		} else if (!CommonsUtil.semValor(emitente.getCnpj())) {
			objetoCcb.setCpfEmitente(emitente.getCnpj());
			this.objetoCcb.setCCBDocumento("CNPJ");
			this.objetoCcb.setCCBCNPJ(emitente.getCnpj());
			objetoCcb.setTipoPessoaEmitente("PJ");
		}
		
		if(!CommonsUtil.semValor(emitente.getConta())) {
			this.objetoCcb.setContaCorrente(emitente.getConta() + "-" + emitente.getContaDigito());
			this.objetoCcb.setCCBCC(emitente.getConta() + "-" + emitente.getContaDigito());
		}
		
		if(!CommonsUtil.semValor(emitente.getTipoConta())) {
			this.objetoCcb.setTipoContaBanco(emitente.getTipoConta());
			this.objetoCcb.setCCBTipoConta(emitente.getTipoConta());
		}
		
		if(!CommonsUtil.semValor(emitente.getAgencia())) {
			this.objetoCcb.setAgencia(emitente.getAgencia());
			this.objetoCcb.setCCBAgencia(emitente.getAgencia());
		}
		
		if(!CommonsUtil.semValor(emitente.getBanco())) {		
			this.objetoCcb.setCCBBanco(emitente.getBanco());
			String[] banco = emitente.getBanco().split(Pattern.quote("|"));
			if(banco.length == 1) {
				if(CommonsUtil.eSomenteNumero(banco[0])) {
					this.objetoCcb.setNumeroBanco(CommonsUtil.trimNull(banco[0]));
					populateNomesBanco();
				} else {
					this.objetoCcb.setNomeBanco(CommonsUtil.trimNull(banco[0]));
					populateCodigosBanco();
				}
			} else {
				if (!CommonsUtil.semValor(banco) && banco.length > 1) {
				this.objetoCcb.setNomeBanco(CommonsUtil.trimNull(banco[1]));
				}
				if (!CommonsUtil.semValor(banco) && banco.length > 0) {
					this.objetoCcb.setNumeroBanco(CommonsUtil.trimNull(banco[0]));
				}
			}
		}
		
		if(!CommonsUtil.semValor(emitente.getNomeCC())) {
			this.objetoCcb.setTitularConta(emitente.getNomeCC());
			this.objetoCcb.setCCBNome(emitente.getNomeCC());
		}
		if(!CommonsUtil.semValor(emitente.getContaDigito())) {
			this.objetoCcb.setDigitoBanco(emitente.getContaDigito());
			this.objetoCcb.setCCBDigito(emitente.getContaDigito());
		}
		if(!CommonsUtil.semValor(emitente.getPix())) {
			this.objetoCcb.setPixBanco(emitente.getPix());
			this.objetoCcb.setCCBPix(emitente.getPix());
		}
	}
	
	public boolean validaCPF(FacesContext facesContext, UIComponent uiComponent, Object object) {
		return ValidaCPF.isCPF(object.toString());
	}
	
	public void populaReferenciaBancariaCPF() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCPF = ValidaCPF.isCPF(this.objetoPagadorRecebedor.getCpf());

		if (!validaCPF) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CPF inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCpfCC(this.objetoPagadorRecebedor.getCpf());
			this.objetoPagadorRecebedor.setNomeCC(this.objetoPagadorRecebedor.getNome());
		}
	}

	public void populaReferenciaBancariaCNPJ() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCNPJ = ValidaCNPJ.isCNPJ(this.objetoPagadorRecebedor.getCnpj());

		if (!validaCNPJ) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CNPJ inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCnpjCC(this.objetoPagadorRecebedor.getCnpj());
			this.objetoPagadorRecebedor.setNomeCC(this.objetoPagadorRecebedor.getNome());
		}
	}
	
	public void populateDadosEmitente() {
		for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {																	
			if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
				if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
					this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
				}
				if(CommonsUtil.semValor(objetoCcb.getEmitentePrincipal().getPessoa().getCpf())) {
					if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
						this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
					} else {
						this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
					}
				}
				if(CommonsUtil.semValor(this.objetoCcb.getTipoPessoaEmitente())) {
					if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
						this.objetoCcb.setTipoPessoaEmitente("PF");
					} else {
						this.objetoCcb.setTipoPessoaEmitente("PJ");
					}
				}
			}
		}
	}
	
	public void criarPagadorRecebedorNoSistema(PagadorRecebedor pagador) {
		PagadorRecebedor pagadorRecebedor = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		
		this.objetoPagadorRecebedor = pagador;
		

		if (this.objetoPagadorRecebedor.getId() <= 0) {
			List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
			boolean registraPagador = false;
			Long idPagador = (long) 0;

			if (this.objetoPagadorRecebedor.getCpf() != null) {
				boolean validaCPF = ValidaCPF.isCPF(this.objetoPagadorRecebedor.getCpf());
				if(validaCPF) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf", this.objetoPagadorRecebedor.getCpf());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						populaReferenciaBancariaCPF();
						pagadorRecebedor = this.objetoPagadorRecebedor;
						registraPagador = true;
					}
				}
			}
			
			if (this.objetoPagadorRecebedor.getCnpj() != null) {
				boolean validaCNPJ = ValidaCNPJ.isCNPJ(this.objetoPagadorRecebedor.getCnpj());
				if(validaCNPJ) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj", this.objetoPagadorRecebedor.getCnpj());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						populaReferenciaBancariaCNPJ();
						pagadorRecebedor = this.objetoPagadorRecebedor;
						registraPagador = true;
					}
				}
			}

			if (pagadorRecebedor == null) {
				pagadorRecebedor = this.objetoPagadorRecebedor;
			}

			if (this.objetoPagadorRecebedor.getSite() != null && this.objetoPagadorRecebedor.getSite().equals("")) {
				if (!this.objetoPagadorRecebedor.getSite().contains("http")) {
					this.objetoPagadorRecebedor
							.setSite("HTTP://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
				}
			}

			if (registraPagador) {
				idPagador = pagadorRecebedorDao.create(pagadorRecebedor);
				pagadorRecebedor = pagadorRecebedorDao.findById(idPagador);
			}
		} else {
			pagadorRecebedorDao.merge(this.objetoPagadorRecebedor);
			pagadorRecebedor = this.objetoPagadorRecebedor;
		}
		
		pagadorRecebedor.criarConjugeNoSistema();
	}
	
	public void salvarCcb() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			CcbDao ccbDao = new CcbDao();
			if (this.objetoCcb.getId() > 0) {
				ccbDao.merge(this.objetoCcb);
				System.out.println("CCB Merge ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " / " + "Salvar");
			} else {
				ccbDao.create(this.objetoCcb);
				System.out.println("CCB Create ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " / " + "Salvar");
			}
			salvarContrato();
		} catch (Exception e) {
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CCB: " + e.getCause(), ""));
		} finally {
			if (this.objetoCcb.getId() > 0) {
				//this.setAviso("CCB: Contrato salvo no sistema " + objetoCcb.getNumeroCcb() + " / "
				//	+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " (" + objetoCcb.getId() + ")");
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CCB: Contrato salvo no sistema", ""));
			} else {
				//this.setAviso("CCB: Erro ao salver contrato no sistema " + objetoCcb.getNumeroCcb() + " / "
				//		+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "CCB: Erro ao salvar contrato no sistema", ""));
			}		
		}
	}
	
	public void criarCcbNosistema() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			CcbDao ccbDao = new CcbDao();
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				if(CommonsUtil.semValor(participante.getTipoOriginal())) {
					participante.setTipoOriginal(participante.getTipoParticipante());
				} else {
					participante.setTipoParticipante(participante.getTipoOriginal());
				}
			}
			
			ContasPagarDao cpDao = new ContasPagarDao();
			CcbProcessosJudiciaisDao pjDao = new CcbProcessosJudiciaisDao();
			for (CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
				if(!CommonsUtil.semValor(processo.getContaPagar())) {
					ContasPagar conta = processo.getContaPagar();
					if(conta.getId() <= 0) {
						cpDao.create(conta);
					} else {
						cpDao.merge(conta);
					}
				}
				if(processo.getId() <= 0) {
					pjDao.create(processo);
				} else {
					pjDao.merge(processo);
				}
			}
			
			for (ContasPagar conta : this.objetoCcb.getDespesasAnexo2()) {
				if(conta.getId() <= 0) {
					cpDao.create(conta);
				} else {
					cpDao.merge(conta);
				}
			}
			
			salvarContrato();
			
			if (this.objetoCcb.getId() > 0) {
				ccbDao.merge(this.objetoCcb);
				System.out.println("CCB Merge ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " / " + this.tipoDownload);
			} else {
				ccbDao.create(this.objetoCcb);
				System.out.println("CCB Create ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " / " + this.tipoDownload);
			}
		} catch (Exception e) {
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CCB: " + e.getCause(), ""));
		} finally {
			if (this.objetoCcb.getId() > 0) {
				//this.setAviso("CCB: Contrato salvo no sistema " + objetoCcb.getNumeroCcb() + " / "
				//	+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " (" + objetoCcb.getId() + ")");
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CCB: Contrato salvo no sistema", ""));
			} else {
				//this.setAviso("CCB: Erro ao salver contrato no sistema " + objetoCcb.getNumeroCcb() + " / "
				//		+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "CCB: Erro ao salvar contrato no sistema", ""));
			}		
		}
	}

	private void salvarContrato() {
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			ContratoCobranca contrato = objetoCcb.getObjetoContratoCobranca();
			if(contrato.getId() > 0) {
				if(!CommonsUtil.semValor(objetoCcb.getNumeroCcb())) {
					contrato.setNumeroContratoSeguro(objetoCcb.getNumeroCcb());
				}
				if(!CommonsUtil.semValor(objetoCcb.getDataDeEmissao())) {
					contrato.setDataInicio(objetoCcb.getDataDeEmissao());
				}
				if(!CommonsUtil.semValor(objetoCcb.getSistemaAmortizacao())) {
					contrato.setTipoCalculo(objetoCcb.getSistemaAmortizacao());
				}
				if(!CommonsUtil.semValor(objetoCcb.getPrazo())) {
					contrato.setQtdeParcelas(CommonsUtil.intValue(objetoCcb.getPrazo()));
				}
				if(!CommonsUtil.semValor(objetoCcb.getValorCredito())) {
					contrato.setValorCCB(objetoCcb.getValorCredito());
				}
				if(!CommonsUtil.semValor(objetoCcb.getTaxaDeJurosMes())) {
					contrato.setTxJurosParcelas(objetoCcb.getTaxaDeJurosMes());
				}
				if(!CommonsUtil.semValor(objetoCcb.getCarencia())) {
					contrato.setMesesCarencia(CommonsUtil.intValue(objetoCcb.getCarencia()));
				}
				contrato.setValorCartaSplit(objetoCcb.getValorLiquidoCredito());
				contrato.setNomeBancarioCartaSplit(objetoCcb.getNomeEmitente());
				contrato.setCpfCnpjBancarioCartaSplit(objetoCcb.getCpfEmitente());
				contrato.setBancoBancarioCartaSplit(objetoCcb.getNomeBanco());
				contrato.setAgenciaBancarioCartaSplit(objetoCcb.getAgencia());
				contrato.setContaBancarioCartaSplit(objetoCcb.getContaCorrente());		
				contrato.setPixCartaSplit(objetoCcb.getPixBanco());
				
				contrato.setValorCartaSplitGalleria(objetoCcb.getValorDespesas());
				contrato.setNomeBancarioCartaSplitGalleria("Galleria Correspondente Bancário Eireli");
				contrato.setCpfCnpjBancarioCartaSplitGalleria("34.787.885/0001-32");
				contrato.setBancoBancarioCartaSplitGalleria("001 | Banco do Brasil S.A.");
				contrato.setAgenciaBancarioCartaSplitGalleria("1515-6");
				contrato.setContaBancarioCartaSplitGalleria("131094-1");	
				contrato.setPixCartaSplitGalleria("b56b12e2-f476-4272-8d16-c1a5a31cc660");

				contrato.setValorCustoEmissao(objetoCcb.getValorIOF());
				contrato.setNomeBancarioCustoEmissao("Galleria SCD");
				contrato.setBancoBancarioCustoEmissao("001 | Banco do Brasil S.A.");
				contrato.setAgenciaBancarioCustoEmissao("6937");
				contrato.setContaBancarioCustoEmissao("120621-4");
				contrato.setCpfCnpjBancarioCustoEmissao("51.604.356/0001-75");
				contrato.setPixCustoEmissao("51.604.356/0001-75");
				
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				try {
					cDao.merge(contrato);
				} catch (TransientObjectException e) {
					contrato.toString();
					e.printStackTrace();
				} catch (DAOException e) {
					contrato.toString();
					e.printStackTrace();
				} 
			}
		}
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	    filesList.add(uploadedFile);
    }
	
	public void clearFiles() {
		uploadedFile = null;
	    fileName = null;
	    fileType = null;
	}
	
	public void removerArquivo(UploadedFile file) {
		this.getFilesList().remove(file);		
	}

	public String verificaEstadoCivil(Boolean sexo, String estadoCivil) {
		if(sexo == true) {
			if(CommonsUtil.mesmoValor(estadoCivil, "SOLTEIRO")) {
				return "SOLTEIRA";
			} else if(CommonsUtil.mesmoValor(estadoCivil, "CASADO")) {
				return "CASADA";
			} else if(CommonsUtil.mesmoValor(estadoCivil, "VIÚVO")) {
				return "VIÚVA";
			} else if(CommonsUtil.mesmoValor(estadoCivil, "DIVORCIADO")) {
				return "DIVORCIADA";
			} else {
				return estadoCivil;
			}
		} else {
			return estadoCivil;
		}
	}
	
	public String verificaNacionalidade(Boolean sexo, String nacionalidade) {
		if(sexo == true) {
			if(CommonsUtil.mesmoValor(nacionalidade, "brasileiro")) {
				return "brasileira";
			} else {
				return nacionalidade;
			}
		} else {
			return nacionalidade;
		}
	}

	public StreamedContent readXWPFile() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		atualizaDadosEmitente();
		CcbService ccbService = new CcbService(filesList, objetoCcb, simulador);
		Map<String, byte[]> listaArquivos = new HashMap<String, byte[]>();
		byte[] arquivos = null;
		List<String> listaDocumentos = new ArrayList<String>();
		for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			participante.atualizaDados();
    		if(participante.getSocios().size() > 0){
    			for(CcbParticipantes socio : participante.getSocios()) {
    				socio.atualizaDados();
    			}
    		}
    	}		    	
		for(String s : listaTipoDownload) {
			String s2 = new String(s);
			listaDocumentos.add(s2);
		}
	    try {
	    	if(!CommonsUtil.semValor(this.tipoDownload) && !CommonsUtil.mesmoValor(this.tipoDownload, "TODOS")) {
	    		//listaDocumentos = listaTipoDownload;
	    		listaTipoDownload.clear();
	    		listaTipoDownload.add(this.tipoDownload);
	    	}
	    	boolean arquicoUnico = true;
    		if(listaTipoDownload.size() > 1){
    			arquicoUnico = false;
    		}
	    	for(String tipoDownload : listaTipoDownload) {
	    		byte[] arquivo;
	    		String nomeDoc;
		    	if(CommonsUtil.mesmoValor(tipoDownload,"CCB")){
		    		arquivo = ccbService.geraCcbDinamica();
		    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "CCB.docx";
		    		if(arquicoUnico)
		    			ccbService.geraDownloadByteArray(arquivo,nomeDoc);
		    		else
		    			listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "AF")) {
					arquivo = ccbService.geraAFDinamica();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "AF.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "NC")) {
					arquivo = ccbService.geraNCDinamica();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "NC.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Carta Split")) {
					arquivo = ccbService.geraCartaSplitDinamica();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "CartaSplit.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "AnexoI")) {
					arquivo = ccbService.geraAnexoI();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "AnexoI.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "AnexoII")) {
					arquivo = ccbService.geraAnexoII();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "AnexoII.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "CCI")) {
					arquivo = ccbService.geraCci();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "CCI.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Cessao")) {
					arquivo = ccbService.geraCessao();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "Cessao.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "InstrumentoEmissaoCCI")) {
					arquivo = ccbService.geraInstrumentoEmissaoCCI();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "InstrumentoEmissaoCCI.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Endossos Em Preto")) {
					arquivo = ccbService.geraEndossosEmPretoGalleria();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "EndossosEmPretoGalleria.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Aquisicao/Emprestimo")) {
					arquivo = ccbService.geraCciAquisicao();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "AquisicaoCCI.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "FinanciamentoCCI")) {
					arquivo = ccbService.geraCciFinanciamento();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "FinanciamentoCCI.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Aditamento Carta de Desconto")) {
					arquivo = ccbService.geraAditamentoCartaDeDesconto();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "AditamentoCartaDesconto.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Aditamento Data Parcela")) {
					arquivo = ccbService.geraAditamentoDataParcela();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "AditamentoDataParcela.docx";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Ficha PPE - PF")) {
					arquivo = ccbService.geraFichaPPE();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "Ficha PPE.pdf";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if (CommonsUtil.mesmoValor(tipoDownload, "Ficha PLD e FT - PJ")) {
					arquivo = ccbService.geraFichaPLDeFT();
					nomeDoc = objetoCcb.getNumeroOperacao() + " - " + "Ficha PLD e FT.pdf";
					if (arquicoUnico)
						ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					else
						listaArquivos.put(nomeDoc, arquivo);
				} else if(CommonsUtil.mesmoValor(tipoDownload,"Declaração Não União Estavel")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraDeclaracaoNaoUniaoEstavel(socio);
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "DeclaracaoNaoUniaoEstavel.docx";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		if(!participante.isEmpresa() && !participante.isUniaoEstavel()) {
			    			arquivo = ccbService.geraDeclaracaoNaoUniaoEstavel(participante);
			    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "DeclaracaoNaoUniaoEstavel.docx";
				    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
				    		listaArquivos.put(nomeDoc, arquivo);
			    		}
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Declaração de União Estavel")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraDeclaracaoUniaoEstavel(socio);
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "DeclaracaoUniaoEstavel.docx";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		if(!participante.isEmpresa() && participante.isUniaoEstavel()) {
			    			arquivo = ccbService.geraDeclaracaoUniaoEstavel(participante);
			    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "DeclaracaoUniaoEstavel.docx";
				    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc); 	
				    		listaArquivos.put(nomeDoc, arquivo);
			    		}
			    	}		    	
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Declaração Destinação Recursos")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraDeclaracaoDestinacaoRecursos(socio);
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "DeclaracaoDestinacaoRecursos.docx";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
		    			arquivo = ccbService.geraDeclaracaoDestinacaoRecursos(participante);
		    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" +  "DeclaracaoDestinacaoRecursos.docx";
			    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
			    		listaArquivos.put(nomeDoc, arquivo);
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Termo Responsabilidade Paju Vencido")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraTermoResponsabilidadeAnuenciaPaju(socio);
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "TermoPaju.docx";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		arquivo = ccbService.geraTermoResponsabilidadeAnuenciaPaju(participante);
			    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "TermoPaju.docx";
			    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc); 	
			    		listaArquivos.put(nomeDoc, arquivo);
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Termo Paju Estados")) {
			    	List<String> estados = estadosTermoPaju();
			    	if(!CommonsUtil.semValor(ufPaju)) {
		    			estados.clear();
		    			estados.add(ufPaju);
		    		}
		    		for(String ufEstado : estados) {
				    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
				    		if(participante.getSocios().size() > 0){
				    			for(CcbParticipantes socio : participante.getSocios()) {
				    				arquivo = ccbService.geraTermoPajuEstado(socio, ufEstado);
					    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "TermoPaju"+ ufEstado +".docx";
						    		listaArquivos.put(nomeDoc, arquivo);
				    			}
				    		}
				    		arquivo = ccbService.geraTermoPajuEstado(participante, ufEstado);
				    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "TermoPaju"+ ufEstado +".docx";	
				    		listaArquivos.put(nomeDoc, arquivo);
				    	}
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Termo Incomunicabilidade Imovel")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraTermoIncomunicabilidadeImovel(socio);
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "TermoPaju.docx";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		if(!participante.isEmpresa() && !CommonsUtil.semValor(participante.getPessoa().getNomeConjuge())) {
				    		arquivo = ccbService.geraTermoIncomunicabilidadeImovel(participante);
				    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "TermoPaju.docx";
				    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc); 	
				    		listaArquivos.put(nomeDoc, arquivo);
			    		}
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Averbacao")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				if(socio.getPessoa().getListAverbacao().size() <= 0) 
			    					continue;
			    				arquivo = ccbService.geraAverbacao(socio);
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "Averbacao.docx";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		if(participante.getPessoa().getListAverbacao().size() <= 0) 
	    					continue;
			    		arquivo = ccbService.geraAverbacao(participante);
			    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "Averbacao.docx";
			    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc); 	
			    		listaArquivos.put(nomeDoc, arquivo);
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Ficha Cadastro Nova")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraFichaCadastroNova(socio.getPessoa());
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "Ficha Cadastro.pdf";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		arquivo = ccbService.geraFichaCadastroNova(participante.getPessoa());
			    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "Ficha Cadastro.pdf";
			    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc); 	
			    		listaArquivos.put(nomeDoc, arquivo);
			    	}
			    } else if(CommonsUtil.mesmoValor(tipoDownload,"Ficha Cadastro")) {
			    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
			    		if(participante.getSocios().size() > 0){
			    			for(CcbParticipantes socio : participante.getSocios()) {
			    				arquivo = ccbService.geraFichaCadastro(socio.getPessoa());
				    			nomeDoc = objetoCcb.getNumeroOperacao() + " - " + socio.getPessoa().getNome() + "_" + "Ficha Cadastro.pdf";
					    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc);
					    		listaArquivos.put(nomeDoc, arquivo);
			    			}
			    		}
			    		arquivo = ccbService.geraFichaCadastro(participante.getPessoa());
			    		nomeDoc = objetoCcb.getNumeroOperacao() + " - " + participante.getPessoa().getNome() + "_" + "Ficha Cadastro.pdf";
			    		//ccbService.geraDownloadByteArray(arquivo, nomeDoc); 	
			    		listaArquivos.put(nomeDoc, arquivo);
			    	}
			    } else {
		    		
		    	}
	    	}
	    	
	    	if(listaArquivos.size() > 1) {
	    		arquivos = CompactadorUtil.compactarZipByte(listaArquivos);
				final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
						FacesContext.getCurrentInstance());
				String nomeArquivoDownload = String.format(objetoCcb.getNumeroOperacao() + " Contratos.zip",
						"");
				gerador.open(nomeArquivoDownload);
				gerador.feed(new ByteArrayInputStream(arquivos));
				gerador.close();
	    	} else if(listaArquivos.size() == 1) {
	    		Map.Entry<String,byte[]> entry = listaArquivos.entrySet().iterator().next();
	    		arquivos = entry.getValue();
	    		String nomeArquivoDownload = entry.getKey();
				final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
						FacesContext.getCurrentInstance());
				gerador.open(nomeArquivoDownload);
				gerador.feed(new ByteArrayInputStream(arquivos));
				gerador.close();
	    	}
	    	
			//listaTipoDownload.clear();
	  	    //listaTipoDownload = listaDocumentos;
	  	    salvarCcb();
	    } catch (Exception e) {
	    	e.printStackTrace();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o documento!  " + e + ";" + e.getCause(),
							""));
			//listaTipoDownload.clear();
	  	   	//listaTipoDownload = listaDocumentos;
	  	    salvarCcb();
	    }  
	    listaTipoDownload.clear();
	    for(String s : listaDocumentos) {
			String s2 = new String(s);
			listaTipoDownload.add(s2);
		}
	    //listarDownloads();
	    return null;
	}
		
	public void geraFichaCadastro(PagadorRecebedor pagador) throws IOException{
		try {
			CcbService ccbService = new CcbService(filesList, objetoCcb, simulador);
			byte[] arquivo = ccbService.geraFichaCadastro(pagador);
			ccbService.geraDownloadByteArray(arquivo, "Ficha Cadastro");
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 	
	}

	public void clearPagadorRecebedor() {
		this.participanteSelecionado = new CcbParticipantes();
	}
	
	public void clearDespesas() {
		despesaSelecionada = new ContasPagar();
		processoSelecionado = new CcbProcessosJudiciais();
	}

	public void calculaValorLiquidoCredito() {
		BigDecimal valor = BigDecimal.ZERO;
		if(!CommonsUtil.semValor(this.objetoCcb.getValorCredito())) {
			valor = this.objetoCcb.getValorCredito();
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getCustoEmissao())) {
			valor = valor.subtract(this.objetoCcb.getCustoEmissao());
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getValorIOF())) {
			valor = valor.subtract(this.objetoCcb.getValorIOF());
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getValorDespesas())) {
			valor = valor.subtract(this.objetoCcb.getValorDespesas());
		}
		this.objetoCcb.setValorLiquidoCredito(valor);
	}
	
	public void calculaPorcentagemImovel() {
		if (!CommonsUtil.semValor(this.objetoCcb.getValorCredito()) && this.objetoCcb.getVendaLeilao() != null) {
			this.objetoCcb.setPorcentagemImovel(((this.objetoCcb.getVendaLeilao().divide(this.objetoCcb.getValorCredito(), MathContext.DECIMAL128)).multiply(BigDecimal.valueOf(100))).setScale(2, BigDecimal.ROUND_HALF_UP));	
		}
	}
	
	public void calcularSimulador() {
		populateDadosEmitente();
		this.simulador = new SimulacaoVO();	
		BigDecimal tarifaIOFDiario = BigDecimal.ZERO;
		BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL.divide(BigDecimal.valueOf(100));
		simulador.setTipoPessoa(this.objetoCcb.getTipoPessoaEmitente());
		
		List<String> imoveis = Arrays.asList("Apartamento", "Casa", "Casa de Condomínio", "Terreno");
		
		if(!objetoCcb.getEmitentePrincipal().isEmpresa() &&
				imoveis.contains(objetoCcb.getObjetoContratoCobranca().getImovel().getTipo())
				&& CommonsUtil.mesmoValor(objetoCcb.getObjetoContratoCobranca().getTipoOperacao(), "Emprestimo")) {
			tarifaIOFDiario = BigDecimal.ZERO;
			tarifaIOFAdicional = BigDecimal.ZERO;
		} else {
			if (CommonsUtil.mesmoValor(this.objetoCcb.getTipoPessoaEmitente(), "PF")) {		
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));		
			} else {		
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));		
			}
		}
		
		BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
		
		final BigDecimal custoEmissaoPercentual;
		if (CommonsUtil.semValor(objetoCcb.getPercentualCustoEmissao())) {
			if (objetoCcb.isUsarNovoCustoEmissao()) {
				objetoCcb.setPercentualCustoEmissao(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO_NOVO);
			} else {
				objetoCcb.setPercentualCustoEmissao(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO);
			}
		}		
		custoEmissaoPercentual = objetoCcb.getPercentualCustoEmissao();

		if (objetoCcb.getValorCredito().multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)))
				.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
			custoEmissaoValor = objetoCcb.getValorCredito().multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)));
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getPrazo()) && !CommonsUtil.semValor(objetoCcb.getCarencia())) {
			this.objetoCcb.setNumeroParcelasPagamento(CommonsUtil.stringValue(Long.parseLong(this.objetoCcb.getPrazo()) - Long.parseLong(objetoCcb.getCarencia())));
		}
		simulador.setDataSimulacao(this.objetoCcb.getDataDeEmissao());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		simulador.setValorCredito(this.objetoCcb.getValorCredito());
		simulador.setTaxaJuros(this.objetoCcb.getTaxaDeJurosMes());
		simulador.setCarencia(BigInteger.valueOf( Long.parseLong(this.objetoCcb.getPrazo()) - Long.parseLong(this.objetoCcb.getNumeroParcelasPagamento())));
		simulador.setQtdParcelas(BigInteger.valueOf(Long.parseLong(this.objetoCcb.getPrazo())));
		simulador.setValorImovel(this.objetoCcb.getVlrImovel());
		simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setCustoEmissaoPercentual(custoEmissaoPercentual);
		simulador.setTipoCalculo(this.objetoCcb.getSistemaAmortizacao());
		simulador.setNaoCalcularDFI(false);
		simulador.setNaoCalcularMIP(false);
		simulador.setNaoCalcularTxAdm(false);
		if (CommonsUtil.mesmoValor('L', this.objetoCcb.getTipoCalculoFinal())) {
			GoalSeek goalSeek = new GoalSeek(CommonsUtil.doubleValue(simulador.getValorCredito()), 
					CommonsUtil.doubleValue(simulador.getValorCredito().divide(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)),
					CommonsUtil.doubleValue(simulador.getValorCredito().multiply(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)));		
			GoalSeekFunction gsFunfction = new GoalSeekFunction();
			BigDecimal valorBruto = CommonsUtil.bigDecimalValue(gsFunfction.getGoalSeek(goalSeek, simulador));
			simulador.setValorCredito(valorBruto.setScale(2, RoundingMode.HALF_UP));
		} else {
			simulador.calcular();
		}
		simulador.calcularValorLiberado();
		
		BigDecimal jurosAoAno = BigDecimal.ZERO;
		jurosAoAno = BigDecimal.ONE.add((this.objetoCcb.getTaxaDeJurosMes().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
		jurosAoAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosAoAno), 12));
		jurosAoAno = jurosAoAno.subtract(BigDecimal.ONE);
		jurosAoAno = jurosAoAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
		jurosAoAno = jurosAoAno.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.simulador.setTaxaJurosAoAno(jurosAoAno);
		
		if (simulador.getParcelas().size() > 0 ) {
			BigDecimal cet = BigDecimal.ZERO;
			BigDecimal cetAno = BigDecimal.ZERO;
			double cetDouble = 0.0;
			
			double[] cash_flows = new double[simulador.getQtdParcelas().intValue() + 1];
			
			cash_flows[0] = simulador.getValorCreditoLiberado().negate().doubleValue();
			
			for (int i = 1; i <= simulador.getQtdParcelas().intValue(); i++) {
				BigDecimal calc_value = simulador.getParcelas().get(i).getAmortizacao().add(simulador.getParcelas().get(i).getJuros());
				cash_flows[i] = calc_value.doubleValue();
			}
			
		
			
			
			for (SimulacaoDetalheVO parcela : this.simulador.getParcelas()) {
				boolean encontrouParcela = false;
				BigDecimal saldoAnterior = BigDecimal.ZERO;				

				for (ContratoCobrancaDetalhes detalhe : this.objetoCcb.getObjetoContratoCobranca().getListContratoCobrancaDetalhes()) {

					if (CommonsUtil.mesmoValor(parcela.getNumeroParcela().toString(), detalhe.getNumeroParcela())) {

						detalhe.setVlrSaldoInicial(saldoAnterior);
						// detalhe.setVlrSaldoParcela(
						// parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));

						if (this.objetoContratoCobranca.isCorrigidoNovoIPCA()) {
							detalhe.setVlrSaldoParcela(
									parcela.getSaldoDevedorFinal().setScale(2, BigDecimal.ROUND_HALF_EVEN));
						} else {
							detalhe.setVlrSaldoParcela(
									parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));
						}

						detalhe.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));
						detalhe.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
						detalhe.setVlrAmortizacaoParcela(parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
						detalhe.setSeguroDFI(parcela.getSeguroDFI());
						detalhe.setSeguroMIP(parcela.getSeguroMIP());
						detalhe.setTaxaAdm(parcela.getTxAdm());
						if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
							detalhe.setParcelaPaga(true);
							detalhe.setOrigemBaixa("concluirReparcelamento");
							detalhe.setDataPagamento(detalhe.getDataVencimento());
							detalhe.setVlrParcela(BigDecimal.ZERO);
						}

						if (DateUtil.isAfterDate(detalhe.getDataVencimento(), DateUtil.getDataHoje())
								&& !detalhe.isParcelaPaga()) {
							detalhe.setParcelaVencida(true);
						} else
							detalhe.setParcelaVencida(false);

						if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalhe.isParcelaPaga()) {
							detalhe.setParcelaVencendo(true);
						} else
							detalhe.setParcelaVencendo(false);


						encontrouParcela = true;
						break;
					}
					saldoAnterior = detalhe.getVlrSaldoParcela();
				}
			}
					
//			  REPROCESSA IPCA
			  if ( this.objetoCcb.getObjetoContratoCobranca().isCorrigidoIPCA() &&
					  !this.objetoCcb.getObjetoContratoCobranca().isCorrigidoNovoIPCA() ){
				  

					IpcaService ipcaService = new IpcaService();
					IpcaJobCalcular ipcaJobCalcular = new IpcaJobCalcular();
					
				  Calendar dataCorteParcelasMalucas = Calendar.getInstance();
					dataCorteParcelasMalucas.set(Calendar.YEAR, 2023);
					dataCorteParcelasMalucas.set(Calendar.MONTH, 0);
					dataCorteParcelasMalucas.set(Calendar.DAY_OF_MONTH, 1);
					
					 Date  dataCorteBaixa = dataCorteParcelasMalucas.getTime();
					
				  if (this.objetoCcb.getObjetoContratoCobranca().isCorrigidoIPCAHibrido()) {
						// atualiza data corte da baixa no contrato e banco				
					  this.objetoCcb.getObjetoContratoCobranca().setDataCorteBaixaIPCAHibrido(dataCorteBaixa);				
						
					  ipcaService.atualizaIPCAPorContratoMaluco(this.objetoCcb.getObjetoContratoCobranca(), dataCorteBaixa,ipcaJobCalcular);
					} else {
						if (this.objetoCcb.getObjetoContratoCobranca().isCorrigidoIPCA()) {
							ipcaService.atualizaIPCAPorContrato(this.objetoCcb.getObjetoContratoCobranca(), dataCorteBaixa,ipcaJobCalcular);
						}
					}			
			  }
			int maxGuess = 500;
			cetDouble = SimuladorMB.irr(cash_flows, maxGuess);
			
			if (CommonsUtil.mesmoValor(CommonsUtil.stringValue(cetDouble), "NaN")) {
				cetDouble = 0;
			} 
			
			cetDouble = cetDouble * 100; 
			cet = CommonsUtil.bigDecimalValue(cetDouble);	
			cetAno = BigDecimal.ONE.add((cet.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
			cetAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(cetAno), 12));
			cetAno = cetAno.subtract(BigDecimal.ONE);
			cetAno = cetAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
			
			cetAno = cetAno.setScale(2, BigDecimal.ROUND_HALF_UP);
			cet = cet.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			this.simulador.setCetAoAno(cetAno);
			this.simulador.setCetAoMes(cet);
		}
		
		int numeroUltimaParcela = simulador.getParcelas().get(simulador.getParcelas().size() - 1).getNumeroParcela().intValue();
		Date dataUltimaParcela = DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), numeroUltimaParcela, Calendar.MONTH);
		
		this.objetoCcb.setVencimentoUltimaParcelaPagamento(dataUltimaParcela);
		this.objetoCcb.setVencimentoUltimaParcelaDFI(dataUltimaParcela);
		this.objetoCcb.setVencimentoUltimaParcelaMIP(dataUltimaParcela);
		this.objetoCcb.setValorIOF(simulador.getIOFTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setCustoEmissao(simulador.getCustoEmissaoValor().setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setTaxaDeJurosAno(simulador.getTaxaJurosAoAno());
		this.objetoCcb.setCetMes(simulador.getCetAoMes());
		this.objetoCcb.setCetAno(simulador.getCetAoAno());
		
		BigDecimal montante = BigDecimal.ZERO;
		BigDecimal montanteDfi = BigDecimal.ZERO;
		BigDecimal montanteMip = BigDecimal.ZERO;
		
		BigDecimal vlrPrimeiraParcela = BigDecimal.ZERO;
		BigDecimal vlrPrimeiraDfi = BigDecimal.ZERO;
		BigDecimal vlrPrimeiraMip = BigDecimal.ZERO;
		
		int numeroPrimeiraParcela = 0;
		Date dataPrimeiraParcela;
		
		for(SimulacaoDetalheVO parcela : simulador.getParcelas()) {
			if(!CommonsUtil.semValor(parcela.getAmortizacao().add(parcela.getJuros()))) {
				if(CommonsUtil.semValor(vlrPrimeiraParcela)) {
					vlrPrimeiraParcela = parcela.getAmortizacao().add(parcela.getJuros());
					numeroPrimeiraParcela = parcela.getNumeroParcela().intValue();
				}
			}		
			if(!CommonsUtil.semValor(parcela.getSeguroDFI())) {
				if(CommonsUtil.semValor(vlrPrimeiraDfi)) {
					vlrPrimeiraDfi = parcela.getSeguroDFI();
				}
			}			
			if(!CommonsUtil.semValor(parcela.getSeguroDFI())) {
				if(CommonsUtil.semValor(vlrPrimeiraMip)) {
					vlrPrimeiraMip = parcela.getSeguroMIP();
				}
			}

			montante = montante.add(parcela.getAmortizacao().add(parcela.getJuros()));
			montanteDfi = montanteDfi.add(parcela.getSeguroDFI());
			montanteMip = montanteMip.add(parcela.getSeguroMIP());
		}
		
		dataPrimeiraParcela = DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), numeroPrimeiraParcela, Calendar.MONTH);
		this.objetoCcb.setVencimentoPrimeiraParcelaPagamento(dataPrimeiraParcela);
		this.objetoCcb.setVencimentoPrimeiraParcelaDFI(dataPrimeiraParcela);
		this.objetoCcb.setVencimentoPrimeiraParcelaMIP(dataPrimeiraParcela);
		
		this.objetoCcb.setMontanteMIP(montanteMip.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setMontanteDFI(montanteDfi.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setMontantePagamento(montante.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		this.objetoCcb.setValorMipParcela(vlrPrimeiraMip.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setValorDfiParcela(vlrPrimeiraDfi.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setValorParcela(vlrPrimeiraParcela.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		populateParcelaSeguro();
		if(CommonsUtil.mesmoValor(this.objetoCcb.getTipoCalculoFinal(),'L')) {
			this.objetoCcb.setValorCredito(simulador.getValorCredito().setScale(2, BigDecimal.ROUND_HALF_UP));
			this.objetoCcb.setTipoCalculoFinal('B');
		} 
		calculaValorLiquidoCredito();
		
		calculaPorcentagemImovel();
		atualizaValorTransferencia();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Percelas Geradas com sucesso", ""));	
	}
		
	public String clearFieldsConsultarCcb() {
		CcbDao ccbDao = new CcbDao();
		listaCcbs = ccbDao.ConsultaCCBs();
		
		return "/Atendimento/Cobranca/CcbConsultar.xhtml";
	}
	
	public String clearFieldsEditarCcb() {
		loadLovs();	
		clearPagadorRecebedor();
		clearDespesas();
		listarDownloads();
		this.simulador = new SimulacaoVO();
		//this.seguradoSelecionado = new Segurado();
		//this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		CcbDao ccbDao = new CcbDao();
		this.objetoCcb = ccbDao.findById(objetoCcb.getId());
		
		blockForm = !this.objetoCcb.getObjetoContratoCobranca().isAgRegistro();
		
		objetoContratoCobranca = objetoCcb.getObjetoContratoCobranca();
		if(!CommonsUtil.semValor(objetoCcb.getPrazo()) && !CommonsUtil.semValor(objetoCcb.getNumeroParcelasPagamento())){
			objetoCcb.setCarencia(CommonsUtil.stringValue(CommonsUtil.integerValue(objetoCcb.getPrazo())
					- CommonsUtil.integerValue(objetoCcb.getNumeroParcelasPagamento())));
		}
		mostrarDadosOcultos = false;
		
		emitirAditamento(objetoContratoCobranca.getListContasPagar());
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	
	
	public String clearFieldsInserirCcb() {
		loadLovs();	
		clearDespesas();
		listarDownloads();
		this.addSegurador = false;
		this.objetoCcb = new CcbContrato();
		this.objetoCcb.setListaParticipantes(new ArrayList<CcbParticipantes>());
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		this.objetoCcb.setDataDeEmissao(DateUtil.gerarDataHoje());
		this.addTerceiro = false;
		this.selectedPagadorGenerico = null;
		this.testemunha1Selecionado = null;
		this.testemunha2Selecionado = null;
		
		this.uploadedFile = null;
	    this.fileName = null;
	    this.fileType = null;
	    this.fileTypeInt = 0;
	    
	    mostrarDadosOcultos = false;
	    
	    this.simulador = new SimulacaoVO();
	    
	    clearPagadorRecebedor();
	    
	    CcbDao ccbDao = new CcbDao();
	    int serie = CommonsUtil.intValue(ccbDao.ultimaSerieCCB()) + 1;
	    objetoCcb.setSerieCcb(CommonsUtil.stringValue(serie));
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.getPagadoresRecebedores();
		
		filesList = new ArrayList<UploadedFile>();
		/*for(FileUploaded file : listaArquivos()) {
			//filesList.add((UploadedFile) file.file);
	    }*/
	}
	
	public List<String> estadosTermoPaju() {
		List<String> estados = new ArrayList<String>();
		estados.add("MT");
		estados.add("MA");
		estados.add("GO");
		estados.add("CE");
		estados.add("PR");
		estados.add("RS");
		return estados;
	}
	
	public void getEnderecoByViaNet() {

		try {
			CepService cepService = new CepService();
			CepResult consultaCep = cepService.consultaCep(this.participanteSelecionado.getPessoa().getCep());

			if (CommonsUtil.semValor(consultaCep) || !CommonsUtil.semValor(consultaCep.getErro())) {

			} else {

				if (!CommonsUtil.semValor(consultaCep.getEndereco())) {
					this.participanteSelecionado.getPessoa().setEndereco(consultaCep.getEndereco());
				}
				if (!CommonsUtil.semValor(consultaCep.getBairro())) {
					this.participanteSelecionado.getPessoa().setBairro(consultaCep.getBairro());
				}
				if (!CommonsUtil.semValor(consultaCep.getCidade())) {
					this.participanteSelecionado.getPessoa().setCidade(consultaCep.getCidade());
				}
				if (!CommonsUtil.semValor(consultaCep.getEstado())) {
					this.participanteSelecionado.getPessoa().setEstado(consultaCep.getEstado());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void getEnderecoByViaNetSocio() {

		try {
			CepService cepService = new CepService();
			CepResult consultaCep = cepService.consultaCep(this.socioSelecionado.getPessoa().getCep());

			if (CommonsUtil.semValor(consultaCep) || !CommonsUtil.semValor(consultaCep.getErro())) {

			} else {

				if (!CommonsUtil.semValor(consultaCep.getEndereco())) {
					this.socioSelecionado.getPessoa().setEndereco(consultaCep.getEndereco());
				}
				if (!CommonsUtil.semValor(consultaCep.getBairro())) {
					this.socioSelecionado.getPessoa().setBairro(consultaCep.getBairro());
				}
				if (!CommonsUtil.semValor(consultaCep.getCidade())) {
					this.socioSelecionado.getPessoa().setCidade(consultaCep.getCidade());
				}
				if (!CommonsUtil.semValor(consultaCep.getEstado())) {
					this.socioSelecionado.getPessoa().setEstado(consultaCep.getEstado());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void getEnderecoByViaNetImovelCobranca() {
		
		try {
			CepService cepService = new CepService();
			CepResult consultaCep = cepService.consultaCep(this.objetoCcb.getCepImovel());

			if (CommonsUtil.semValor(consultaCep) || !CommonsUtil.semValor(consultaCep.getErro())) {
				
			} else {

				if (!CommonsUtil.semValor(consultaCep.getEndereco())) {
					this.objetoCcb.setLogradouroRuaImovel(consultaCep.getEndereco());
				}
				if (!CommonsUtil.semValor(consultaCep.getBairro())) {
					this.objetoCcb.setBairroImovel(consultaCep.getBairro());
				}
				if (!CommonsUtil.semValor(consultaCep.getCidade())) {
					this.objetoCcb.setCidadeImovel(consultaCep.getCidade());
				}
				if (!CommonsUtil.semValor(consultaCep.getEstado())) {
					this.objetoCcb.setUfImovel(consultaCep.getEstado());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public void atualizarObjetos() {
		System.out.println("AttCcbs inicio");
		CcbDao ccbDao = new CcbDao();
		ccbDao.atualizaCcbs();
		System.out.println("AttCcbs Concluido");
	}
	
	public PagadorRecebedor getSelectedPagadorGenerico() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagadorGenerico(PagadorRecebedor selectedPagadorGenerico) {
		this.selectedPagadorGenerico = selectedPagadorGenerico;
	}

	public List<PagadorRecebedor> getListPagadores() {
		return listPagadores;
	}

	public void setListPagadores(List<PagadorRecebedor> listPagadores) {
		this.listPagadores = listPagadores;
	}

	public List<PagadorRecebedor> getListRecebedorPagador() {
		return listPagadores;
	}

	public String getUpdatePagadorRecebedor() {
		return updatePagadorRecebedor;
	}

	public void setUpdatePagadorRecebedor(String updatePagadorRecebedor) {
		this.updatePagadorRecebedor = updatePagadorRecebedor;
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public String getTipoDownload() {
		return tipoDownload;
	}

	public void setTipoDownload(String tipoDownload) {
		this.tipoDownload = tipoDownload;
	}

	
	public boolean isAddTerceiro() {
		return addTerceiro;
	}

	public void setAddTerceiro(boolean addTerceiro) {
		this.addTerceiro = addTerceiro;
	}

	public PagadorRecebedor getTestemunha1Selecionado() {
		return testemunha1Selecionado;
	}

	public void setTestemunha1Selecionado(PagadorRecebedor testemunha1Selecionado) {
		this.testemunha1Selecionado = testemunha1Selecionado;
	}

	public PagadorRecebedor getTestemunha2Selecionado() {
		return testemunha2Selecionado;
	}

	public void setTestemunha2Selecionado(PagadorRecebedor testemunha2Selecionado) {
		this.testemunha2Selecionado = testemunha2Selecionado;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getFileTypeInt() {
		return fileTypeInt;
	}

	public void setFileTypeInt(int fileTypeInt) {
		this.fileTypeInt = fileTypeInt;
	}

	public String getTituloPagadorRecebedorDialog() {
		return tituloPagadorRecebedorDialog;
	}

	public void setTituloPagadorRecebedorDialog(String tituloPagadorRecebedorDialog) {
		this.tituloPagadorRecebedorDialog = tituloPagadorRecebedorDialog;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public ByteArrayInputStream getBis() {
		return bis;
	}

	public void setBis(ByteArrayInputStream bis) {
		this.bis = bis;
	}

	public ArrayList<UploadedFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<UploadedFile> filesList) {
		this.filesList = filesList;
	}

	public CcbParticipantes getParticipanteSelecionado() {
		return participanteSelecionado;
	}

	public void setParticipanteSelecionado(CcbParticipantes participanteSelecionado) {
		this.participanteSelecionado = participanteSelecionado;
	}

	public boolean isAddParticipante() {
		return addParticipante;
	}
	
	public void setAddParticipante(boolean addParticipante) {
		this.addParticipante = addParticipante;
	}

	public CcbParticipantes getSocioSelecionado() {
		return socioSelecionado;
	}

	public void setSocioSelecionado(CcbParticipantes socioSelecionado) {
		this.socioSelecionado = socioSelecionado;
	}

	public boolean isAddSocio() {
		return addSocio;
	}

	public void setAddSocio(boolean addSocio) {
		this.addSocio = addSocio;
	}

	public CcbContrato getObjetoCcb() {
		return objetoCcb;
	}

	public void setObjetoCcb(CcbContrato objetoCcb) {
		this.objetoCcb = objetoCcb;
	}

	public List<CcbContrato> getListaCcbs() {
		return listaCcbs;
	}

	public void setListaCcbs(List<CcbContrato> listaCcbs) {
		this.listaCcbs = listaCcbs;
	}

	public List<ContratoCobranca> getListaContratosConsultar() {
		return listaContratosConsultar;
	}

	public void setListaContratosConsultar(List<ContratoCobranca> listaContratosConsultar) {
		this.listaContratosConsultar = listaContratosConsultar;
	}

	public boolean isAddSegurador() {
		return addSegurador;
	}

	public void setAddSegurador(boolean addSegurador) {
		this.addSegurador = addSegurador;
	}

	public Segurado getSeguradoSelecionado() {
		return seguradoSelecionado;
	}

	public void setSeguradoSelecionado(Segurado seguradoSelecionado) {
		this.seguradoSelecionado = seguradoSelecionado;
	}

	public boolean isMostrarDadosOcultos() {
		return mostrarDadosOcultos;
	}

	public void setMostrarDadosOcultos(boolean mostrarDadosOcultos) {
		this.mostrarDadosOcultos = mostrarDadosOcultos;
	}

	public ContasPagar getDespesaSelecionada() {
		return despesaSelecionada;
	}

	public void setDespesaSelecionada(ContasPagar despesaSelecionada) {
		this.despesaSelecionada = despesaSelecionada;
	}

	public CcbProcessosJudiciais getProcessoSelecionado() {
		return processoSelecionado;
	}

	public void setProcessoSelecionado(CcbProcessosJudiciais processoSelecionado) {
		this.processoSelecionado = processoSelecionado;
	}

	public List<String> getListaTipoDownload() {
		return listaTipoDownload;
	}

	public void setListaTipoDownload(List<String> listaTipoDownload) {
		this.listaTipoDownload = listaTipoDownload;
	}

	public CcbParticipantes getSelectedParticipante() {
		return selectedParticipante;
	}

	public void setSelectedParticipante(CcbParticipantes selectedParticipante) {
		this.selectedParticipante = selectedParticipante;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public String getUfPaju() {
		return ufPaju;
	}

	public void setUfPaju(String ufPaju) {
		this.ufPaju = ufPaju;
	}	
}
