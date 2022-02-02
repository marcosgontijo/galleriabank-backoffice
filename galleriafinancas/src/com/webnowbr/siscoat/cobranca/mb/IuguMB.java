package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.FaturaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.OperacaoContratoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.SaldoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SaqueIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SubContaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "iuguMB")
@SessionScoped
public class IuguMB {

	/****
	 * 
	 * GALLERIA CORRESPONDETE - CONTA MESTRE ID = 34
	 * 
	 * GALLERIA SA SUBCONTA ID = 14
	 * 
	 * LIVE TOKEN GALLERIA
	 * bd88479c57011124c25638b26572e453
	 * 
	 * ID CONTA GALLERIA
	 * 7D4D20A4F1184FEB91126DFEAD86AED8
	 */

	/** INICIO - Lista dos Pagadores utilizada pela LOV. */
	private List<PagadorRecebedor> listRecebedores;
	private String nomeRecebedor;	
	private long idRecebedor;
	private PagadorRecebedor selectedRecebedor;

	private List<PagadorRecebedor> listCedentes;
	private String nomeCedente;	
	private long idCedente;
	private PagadorRecebedor selectedCedente;

	private List<PagadorRecebedor> listRecebedoresAux;
	private String nomeRecebedorAux;	
	private long idRecebedorAux;
	private PagadorRecebedor selectedRecebedorAux;
	private PagadorRecebedor selectedRecebedorGALLERIA;

	private String descricaoItem;
	private String numeroContrato;
	private String qtdeParcelas;
	private String parcela;
	private String idContrato;
	private String idParcela;
	private BigDecimal valorItem;
	private Date dataVencimento;
	private boolean splitBoletoIugu;	

	private ContratoCobranca contratoCobranca;
	private ContratoCobrancaDetalhes contratoCobrancaDetalhes;

	private String saldoSubContaLiberado;
	private String saldoSubContaALiberar;

	private List<FaturaIUGU> faturasIUGU;
	private List<FaturaIUGU> faturasDownloadIUGU;
	private List<TransferenciasIUGU> transferenciasIUGU;
	private List<SubContaIUGU> subContasIUGU;
	private LazyDataModel<FaturaIUGU> faturasIUGULazy;

	private boolean contaMestre;
	private boolean contaMestreAux;
	private boolean operacaoTransferencia;
	private boolean relPorSubconta;

	private Date relDataContratoInicio;
	private Date relDataContratoFim;

	private String urlFatura = "";

	private String statusFatura = "";
	private String paramDia;
	private String paramAno;
	private String paramMes;
	private String paramSenha;
	private String senhaStorage;

	private BigDecimal totalEntrada;
	private BigDecimal totalSaida;
	private BigDecimal totalTaxas;
	private int qtdeFaturasPagas;

	private String observacao;

	private String nomeSubConta;

	private int taxaJuros;

	private boolean relByVencimento;
	private String relByStatus;

	List<OperacaoContratoIUGU> operacaoContratoIUGU = new ArrayList<OperacaoContratoIUGU>();
	PagadorRecebedor selectedOperacaoContratoPagadorRecebedorIUGU;

	private List<ContratoCobrancaDetalhes> parcelas3meses;

	private List<ContratoCobranca> contratosCobranca;

	private boolean relIsContrato;

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	private long parcelaObservacao;

	private List<SaqueIUGU> saquesIUGU;

	private List<SaldoIUGU> saldosIUGU;

	private BigDecimal totalSaldoSubcontas;

	private BigDecimal totalMovimentacoes;
	private String totalMovimentacoesStr;

	/***
	 * INICIO ATRIBUTOS RECIBO
	 */
	private TransferenciasObservacoesIUGU transferenciasObservacoesIUGU;
	private boolean reciboPDFGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent file;
	/***
	 * FIM ATRIBUTOS RECIBO
	 */

	Collection<FileUploaded> files = new ArrayList<FileUploaded>();
	List<FileUploaded> deletefiles = new ArrayList<FileUploaded>();
	StreamedContent downloadFile;
	StreamedContent downloadAllFiles;
	StreamedContent downloadAllFaturas;
	FileUploaded selectedFile = new FileUploaded();

	/**
	 * Observacoes
	 */
	private String observacaoContratoDetalhes;
	private List<ContratoCobrancaDetalhesObservacoes> listContratoCobrancaDetalhesObservacoes = new ArrayList<ContratoCobrancaDetalhesObservacoes>();
	private Date dataObservacao;
	private ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes;

	public void clearCedentes() {
		this.idCedente = 0;
		this.nomeCedente = null;
		this.selectedCedente = null;
	}

	public void clearRecebedor() {
		this.idRecebedor = 0;
		this.nomeRecebedor = null;
		this.selectedRecebedor = null;
	}

	public final void populateSelectedRecebedor() {		
		this.idRecebedor = this.selectedRecebedor.getId();
		this.nomeRecebedor = this.selectedRecebedor.getNome();
		this.contaMestre = false;
	}	

	public final void populateSelectedCedente() {
		this.idCedente = this.selectedCedente.getId();
		this.nomeCedente = this.selectedCedente.getNome();
	}	

	public void clearRecebedorAux() {
		this.idRecebedorAux = 0;
		this.nomeRecebedorAux = null;
		this.selectedRecebedorAux = null;
	}

	public final void populateSelectedRecebedorAux() {
		this.idRecebedorAux = this.selectedRecebedorAux.getId();
		this.nomeRecebedorAux = this.selectedRecebedorAux.getNome();
	}	

	public void clearRecebedorSaldo() {
		this.idRecebedor = 0;
		this.nomeRecebedor = null;
		this.selectedRecebedor = null;

		this.operacaoTransferencia = true;
	}

	/**
	 * CHAMADO PELO MENU DE GERAÇÃO DE COBRANÇA SIMPLES
	 * @return
	 */
	public String clearFieldsGeraCobrancaSimples() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();	
		clearRecebedor();

		this.listCedentes = pagadorRecebedorDao.getSubContasIugu();
		clearCedentes();

		this.descricaoItem = "SERVIÇO DE COBRANÇA ";
		this.numeroContrato = "";
		this.parcela = "";
		this.valorItem = null;
		this.dataVencimento = null;

		this.qtdeParcelas = "1";

		this.idContrato = "";
		this.idParcela = "";

		this.splitBoletoIugu = false;

		this.urlFatura = "";

		consultarFaturasContaMestreGalleria();

		this.faturasDownloadIUGU = new ArrayList<FaturaIUGU>();

		return "/Atendimento/Cobranca/CobrancaIugu.xhtml";
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public String clearFieldsOperacoesContratoIugu() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();

		// get conta GALLERIA para usar no processo default
		this.selectedRecebedorGALLERIA = pagadorRecebedorDao.findById(Long.valueOf("34"));

		this.numeroContrato = null;
		this.contratoCobranca = new ContratoCobranca();
		this.operacaoContratoIUGU = new ArrayList<OperacaoContratoIUGU>();
		this.selectedOperacaoContratoPagadorRecebedorIUGU = new PagadorRecebedor();
		this.paramSenha = null;
		this.senhaStorage = "";
		this.parcelas3meses = new ArrayList<ContratoCobrancaDetalhes>();

		this.valorItem = null;

		this.contaMestre = true;
		this.contaMestreAux = false;

		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;

		this.relIsContrato = true;

		this.parcelaObservacao = 0;

		return "/Atendimento/Cobranca/OperacoesContratoIugu.xhtml";
	}

	public void limpaFiltros() {
		if (this.relIsContrato) {
			this.relDataContratoInicio = null;
			this.relDataContratoFim = null;
		} else {
			this.numeroContrato = null;
			this.relDataContratoInicio = gerarDataHoje();
			this.relDataContratoFim = gerarDataHoje();

		}
		this.parcelas3meses = new ArrayList<ContratoCobrancaDetalhes>();
		this.contratosCobranca = new ArrayList<ContratoCobranca>();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();

		this.numeroContrato = null;
		this.contratoCobranca = new ContratoCobranca();
		this.operacaoContratoIUGU = new ArrayList<OperacaoContratoIUGU>();
		this.selectedOperacaoContratoPagadorRecebedorIUGU = new PagadorRecebedor();
		this.paramSenha = null;
		this.senhaStorage = "";

		this.valorItem = null;

		this.contaMestre = true;
		this.contaMestreAux = false;
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void consultaOperacoesContratoIugu() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;
		this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();

		this.parcelaObservacao = 0;

		if (this.relIsContrato) {
			if (this.numeroContrato == null || this.numeroContrato.equals("")) {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "SISCOAT - Consulta Contrato: para efetuar a transferência é obrigatório informar um número de contrato!", ""));

				valid = false;
			}
		} else {
			if (this.relDataContratoInicio == null && this.relDataContratoFim == null ) {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "SISCOAT - Consulta Contrato: para efetuar a consulta é obrigatório informar o período da pesquisa!", ""));

				valid = false;
			}
		}

		if (valid) {
			ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
			this.contratosCobranca = new ArrayList<ContratoCobranca>();
			this.operacaoContratoIUGU = new ArrayList<OperacaoContratoIUGU>();

			/***
			 * Busca o contrato de acordo com o parâmetro informado
			 */

			if (this.relIsContrato) {
				String numeroContratoStr;

				if (this.numeroContrato.length() == 4) {
					numeroContratoStr = "0" + this.numeroContrato;
				} else {
					numeroContratoStr = this.numeroContrato;
				}	

				this.contratosCobranca = ccDao.findByFilter("numeroContrato", numeroContratoStr);

				if (this.contratosCobranca.size() > 0) {
					this.contratoCobranca = this.contratosCobranca.get(0);
				}

				consultaSaldosContratoSelecionado();
			} else {
				// query por data de vencimento
				this.contratosCobranca = ccDao.pesquisaContratoPorData(this.relDataContratoInicio, this.relDataContratoFim);
			}
		}
	}

	public void consultaSaldosContratoSelecionado() {		

		// pega as parcelas dos 2 últimos meses
		this.parcelas3meses = new ArrayList<ContratoCobrancaDetalhes>();
		Date data3meses = gerarDataMenos3Meses();
		Date data1mes = gerarDataMais1Mes();
		Date dataHoje = gerarDataHoje();
		this.operacaoContratoIUGU = new ArrayList<OperacaoContratoIUGU>();
		this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();
		this.parcelaObservacao = 0;
		this.observacao = "";

		//seleciona parcela dos últimos 3 meses
		for (ContratoCobrancaDetalhes c : this.contratoCobranca.getListContratoCobrancaDetalhes()) {
			if (c.getDataVencimento().after(data3meses) && c.getDataVencimento().before(data1mes)) {
				this.parcelas3meses.add(c);
			}
		}

		// Pre Seleciona a parcela do mês para inserção automática de observação de parcela
		Locale locale = new Locale("pt", "BR"); 
		SimpleDateFormat getMes = new SimpleDateFormat("MM", locale);
		String mesParcela = "";
		String mesAtual = getMes.format(dataHoje);
		for (ContratoCobrancaDetalhes parcela : this.parcelas3meses) {
			mesParcela = getMes.format(parcela.getDataVencimento());
			if (mesAtual.equals(mesParcela)) {
				this.parcelaObservacao = parcela.getId();
			}
		}

		//sinaliza status da parcela
		for (ContratoCobrancaDetalhes c : this.parcelas3meses) {
			if (!c.isParcelaPaga()) {
				if (c.getDataVencimentoAtual().before(dataHoje)) {
					c.setParcelaVencida(true);
				} else {
					c.setParcelaVencendo(true);
				}
			}
		}

		if (this.contratoCobranca.getRecebedor() != null && this.contratoCobranca.getRecebedor().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor(), this.contratoCobranca.getVlrRecebedor(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor())));
		}		

		if (this.contratoCobranca.getRecebedor2() != null && this.contratoCobranca.getRecebedor2().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor2()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor2(), this.contratoCobranca.getVlrRecebedor2(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor2())));
		}

		if (this.contratoCobranca.getRecebedor3() != null && this.contratoCobranca.getRecebedor3().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor3()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor3(), this.contratoCobranca.getVlrRecebedor3(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor3())));
		}	

		if (this.contratoCobranca.getRecebedor4() != null && this.contratoCobranca.getRecebedor4().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor4()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor4(), this.contratoCobranca.getVlrRecebedor4(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor4())));
		}	

		if (this.contratoCobranca.getRecebedor5() != null && this.contratoCobranca.getRecebedor5().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor5()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor5(), this.contratoCobranca.getVlrRecebedor5(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor5())));
		}	

		if (this.contratoCobranca.getRecebedor6() != null && this.contratoCobranca.getRecebedor6().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor6()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor6(), this.contratoCobranca.getVlrRecebedor6(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor6())));
		}	

		if (this.contratoCobranca.getRecebedor7() != null && this.contratoCobranca.getRecebedor7().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor7()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor7(), this.contratoCobranca.getVlrRecebedor7(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor7())));
		}	

		if (this.contratoCobranca.getRecebedor8() != null && this.contratoCobranca.getRecebedor8().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor8()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor8(), this.contratoCobranca.getVlrRecebedor8(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor8())));
		}	

		if (this.contratoCobranca.getRecebedor9() != null && this.contratoCobranca.getRecebedor9().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor9()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor9(), this.contratoCobranca.getVlrRecebedor9(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor9())));
		}	

		if (this.contratoCobranca.getRecebedor10() != null && this.contratoCobranca.getRecebedor10().getId() != 184 && !this.contratoCobranca.isOcultaRecebedor10()) {
			this.operacaoContratoIUGU.add(new OperacaoContratoIUGU(this.contratoCobranca.getRecebedor10(), this.contratoCobranca.getVlrRecebedor10(),
					consultarSaldoSubConta(this.contratoCobranca.getRecebedor10())));
		}
	}

	public String redirecionTelaOperacao() {
		this.operacaoContratoIUGU = new ArrayList<OperacaoContratoIUGU>();
		consultaSaldosContratoSelecionado();
		return "/Atendimento/Cobranca/OperacoesContratoIugu.xhtml";
	}

	/**
	 * GERA A DATA DE HOJE
	 * @return
	 */
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	/**
	 * GERA A DATA de 3 MESES ATRAS
	 * @return
	 */
	public Date gerarDataMenos3Meses() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.add(Calendar.MONTH, -3);

		return dataHoje.getTime();
	}

	/**
	 * GERA A DATA de 1 MES a FRENTE
	 * @return
	 */
	public Date gerarDataMais1Mes() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.add(Calendar.MONTH, +1);

		return dataHoje.getTime();
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void processaTransferenciaLoteOperacoesContratoIugu(String origem) {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		this.senhaStorage = this.paramSenha;

		if (this.selectedRecebedor == null && !this.contaMestre) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: para efetuar a transferência é obrigatória a seleção da Conta Origem!", ""));

			valid = false;
		}

		if (valid) {
			for (OperacaoContratoIUGU op : this.operacaoContratoIUGU) {	
				if (!this.contaMestre) {
					if (this.selectedRecebedor.getId() != op.getRecebedor().getId()) {
						if (op.getVlrRecebedor().compareTo(BigDecimal.ZERO) == 1) {
							// conta destino
							this.selectedRecebedorAux = op.getRecebedor();
							// valor da operacao
							this.valorItem = op.getVlrRecebedor();

							processaTransferenciaSubcontasTela();	
						}
					}
				} else {
					if (this.valorItem.compareTo(BigDecimal.ZERO) == 1) {
						// conta destino
						this.selectedRecebedorAux = op.getRecebedor();
						// valor da operacao
						this.valorItem = op.getVlrRecebedor();

						processaTransferenciaSubcontasTela();	
					}
				}

			}
		}	

		this.observacao = "";

		if (origem.equals("tela")) {
			this.valorItem = null;

			// reload da pesquisa
			consultaSaldosContratoSelecionado();
		}
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void processaTransferenciaOperacoesContratoIugu(String origem) {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		this.senhaStorage = this.paramSenha;

		if (this.selectedRecebedor == null && !this.contaMestre) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: para efetuar a transferência é obrigatória a seleção da Conta Origem!", ""));

			valid = false;
		}

		if (valid) {
			if (!this.contaMestre) {
				if (this.selectedRecebedor.getId() != this.selectedOperacaoContratoPagadorRecebedorIUGU.getId()) {
					// conta destino
					if (this.valorItem.compareTo(BigDecimal.ZERO) == 1) {
						this.selectedRecebedorAux = this.selectedOperacaoContratoPagadorRecebedorIUGU;

						processaTransferenciaSubcontasTela();
					}
				}
			} else {
				// conta destino
				if (this.valorItem.compareTo(BigDecimal.ZERO) == 1) {
					this.selectedRecebedorAux = this.selectedOperacaoContratoPagadorRecebedorIUGU;

					processaTransferenciaSubcontasTela();
				}
			}
		}	

		this.observacao = "";

		if (origem.equals("tela")) {
			this.valorItem = null;

			// reload da pesquisa
			consultaSaldosContratoSelecionado();
		}
	}

	public void consultarTransferenciasSubContaAposOperacao(String liveToken) {
		try {						
			FacesContext context = FacesContext.getCurrentInstance();

			this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.iugu.com/v1/withdraw_requests?api_token=" + liveToken);

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consultar Transferências Bancárias: Erro ao consultar transferências bancárias! (Erro: " + erro + ")!", ""));

				} else {							
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					JSONArray transferencias = myResponse.getJSONArray("items");

					for (int i = 0; i < transferencias.length(); i++) {
						TransferenciasIUGU transferenciasIUGU = new TransferenciasIUGU();

						JSONObject obj = transferencias.getJSONObject(i);

						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

						String createdDateOriginal = obj.getString("created_at").substring(8, 10) + "/" + obj.getString("created_at").substring(5, 7) + "/" + obj.getString("created_at").substring(0, 4) + 
								" " + obj.getString("created_at").substring(11, 19) ;
						Date createdDate = new java.sql.Date( ((java.util.Date)formatter.parse(createdDateOriginal)).getTime() );

						String updatedDateOriginal = obj.getString("created_at").substring(8, 10) + "/" + obj.getString("created_at").substring(5, 7) + "/" + obj.getString("created_at").substring(0, 4) + 
								" " + obj.getString("created_at").substring(11, 19) ;
						Date updatedDate = new java.sql.Date( ((java.util.Date)formatter.parse(updatedDateOriginal)).getTime() );

						transferenciasIUGU.setId(obj.getString("id"));
						transferenciasIUGU.setCreated_at(createdDate);
						transferenciasIUGU.setUpdated_at(updatedDate);
						transferenciasIUGU.setStatus(obj.getString("status"));
						transferenciasIUGU.setTotal(obj.getString("amount"));    
						transferenciasIUGU.setRecebedor(obj.getString("account_name"));		

						// busca a observação referente a transferência bancária.
						TransferenciasObservacoesIUGUDao transferenciasObservacoesIUGUDao = new TransferenciasObservacoesIUGUDao();
						List<TransferenciasObservacoesIUGU> transferenciasObservacoesIUGU = new ArrayList<TransferenciasObservacoesIUGU>();
						transferenciasObservacoesIUGU = transferenciasObservacoesIUGUDao.findByFilter("idTransferencia", obj.getString("id"));

						if (transferenciasObservacoesIUGU.size() > 0) {
							transferenciasIUGU.setObservacao(transferenciasObservacoesIUGU.get(0).getObservacao());
						}						

						this.transferenciasIUGU.add(transferenciasIUGU);
					}				

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consultar Transferências Bancárias: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	public void getTranferenciasRecebdoresContrato() {
		List<TransferenciasIUGU> transferenciasIUGUCollect = new ArrayList<TransferenciasIUGU>();

		for (OperacaoContratoIUGU op : this.operacaoContratoIUGU) {
			// consulta as operações e pega a última gerada			
			TransferenciasIUGU tempTransferenciasIUGU = new TransferenciasIUGU();
			consultarTransferenciasSubContaAposOperacao(op.getRecebedor().getIuguLiveApiToken());

			if (this.transferenciasIUGU.size() > 0) {				
				tempTransferenciasIUGU = this.transferenciasIUGU.get(0);
				transferenciasIUGUCollect.add(tempTransferenciasIUGU);	
			}
		}

		this.transferenciasIUGU.clear();
		this.transferenciasIUGU = transferenciasIUGUCollect;
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void processaSaqueLoteOperacoesContratoIugu(String origem) {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		this.senhaStorage = this.paramSenha;

		if (valid) {			
			for (OperacaoContratoIUGU op : this.operacaoContratoIUGU) {		
				if (op.getVlrRecebedor().compareTo(BigDecimal.ZERO) == 1) {
					// conta destino
					this.selectedRecebedor = op.getRecebedor();
					// valor da operacao
					this.valorItem = op.getVlrRecebedor();

					processaSaqueSubcontaTela();	
				}
			}
		}	

		this.observacao = "";

		if (origem.equals("tela")) {
			this.valorItem = null;

			// reload da pesquisa
			consultaSaldosContratoSelecionado();
		}
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void processaSaqueOperacoesContratoIugu(String origem) {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean valid = true;

		this.senhaStorage = this.paramSenha;

		if (valid) {
			if (this.valorItem.compareTo(BigDecimal.ZERO) == 1) {
				// cedente do boleto
				this.selectedRecebedor = this.selectedOperacaoContratoPagadorRecebedorIUGU;

				processaSaqueSubcontaTela();
			}
		}	

		this.observacao = "";

		if (origem.equals("tela")) {
			this.valorItem = null;

			// reload da pesquisa
			consultaSaldosContratoSelecionado();
		}
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void processaTransferenciaSaqueOperacoesContratoIugu() {		
		String observacaoTemp = this.observacao;
		processaTransferenciaOperacoesContratoIugu("mb");
		this.observacao = observacaoTemp;
		processaSaqueOperacoesContratoIugu("mb");

		this.valorItem = null;

		// reload da pesquisa
		consultaSaldosContratoSelecionado();
	}

	/**
	 * CHAMADO PELO MENU OPERAÇÕES CONTRATO
	 * @return
	 */
	public void processaTransferenciaSaqueLoteOperacoesContratoIugu() {
		String observacaoTemp = this.observacao;
		processaTransferenciaLoteOperacoesContratoIugu("mb");
		this.observacao = observacaoTemp;
		processaSaqueLoteOperacoesContratoIugu("mb");

		this.valorItem = null;

		// reload da pesquisa
		consultaSaldosContratoSelecionado();
	}

	/**
	 * CHAMADO PELO MENU DE GERAÇÃO DE COBRANÇA SIMPLES
	 * @return
	 */
	public void geraCobrancaSimplesMenu() {
		String liveToken = "bd88479c57011124c25638b26572e453";
		long idCedente = 0;

		if (this.selectedCedente != null) {
			if (this.selectedCedente.getId() > 0) {			
				liveToken = this.selectedCedente.getIuguLiveApiToken();
				idCedente = this.selectedCedente.getId();
			}
		}

		if (this.qtdeParcelas.equals("1")) {
			geraCobrancaSimples(liveToken, idCedente, "tela");
		} else {
			geraCobrancaSimplesLote(liveToken, idCedente, "tela");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		consultarFaturasSubContaFaturaSimplesParam(liveToken);
	}

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA GERAÇÃO DA COBRANÇA SIMPLES
	 * 
	 * @return
	 */
	public String composeJSONCobrancaSimplesLote(Calendar dataVencimento, String mes, String dia, String parcela, String origemChamada) {
		String jsonFavorecido = "";
		String jsonItens = "";
		String jsonPayer = "";
		String jsonCustomVariables = "";

		String documento = "";
		if (this.selectedRecebedor.getCpf() == null) {
			documento = this.selectedRecebedor.getCnpj().replace(".", "").replace("-", "").replace("/", "");
		} else {
			documento = this.selectedRecebedor.getCpf().replace(".", "").replace("-", "");
		}

		String descricaoCompleta = "";		
		
		if (origemChamada.equals("tela")) {
			if (this.descricaoItem.equals("SERVIÇO DE COBRANÇA ")) {
				descricaoCompleta = "SERVIÇO DE COBRANÇA";

				if (!this.numeroContrato.equals("")) {
					descricaoCompleta = descricaoCompleta + " - CONTRATO: " + this.numeroContrato;
				}

				if (!this.parcela.equals("")) {
					descricaoCompleta = descricaoCompleta + " / PARCELA: " + this.parcela;
				}
			} else {
				descricaoCompleta = this.descricaoItem;
			}
		} else {
			descricaoCompleta = "SERVIÇO DE COBRANÇA";

			if (!this.numeroContrato.equals("")) {
				descricaoCompleta = descricaoCompleta + " - CONTRATO: " + this.numeroContrato;
			}

			if (!this.parcela.equals("")) {
				descricaoCompleta = descricaoCompleta + " / PARCELA: " + this.parcela;
			}
		}

		jsonItens = "{\"description\":\"" + descricaoCompleta + "\",\"quantity\":1,\"price_cents\":" + this.valorItem.toString().replace(".", "").replace(",", "") + "}";

		String bairro = "";

		if (this.selectedRecebedor.getBairro() != null) {
			if (this.selectedRecebedor.getBairro().length() > 0) {
				bairro = this.selectedRecebedor.getBairro();
			} else {
				bairro = "Bairro";
			}
		} else {
			bairro = "Bairro";
		}
		
		String endereco = ""; 
		if (this.selectedRecebedor.getNumero() != null) {
			if (!this.selectedRecebedor.getNumero().equals("")) {
				endereco = this.selectedRecebedor.getEndereco() + ", " + this.selectedRecebedor.getNumero();
			}
		} else {
			endereco = this.selectedRecebedor.getEndereco();
		}

		jsonPayer = "\"payer\":{\"cpf_cnpj\":\"" + documento + "\",\"name\":\"" + this.selectedRecebedor.getNome() + "\",\"email\":\"" + this.selectedRecebedor.getEmail()
		+ "\",\"address\":{\"zip_code\":\"" + this.selectedRecebedor.getCep().replace(".", "").replace("-", "") + "\",\"street\":\"" + endereco 
		+ "\",\"district\":\"" + bairro
		+ "\",\"number\":\"" + 000 + "\"}}";


		jsonCustomVariables = "{\"value\":\"" + this.idContrato + "\",\"name\":\"idContrato\"},"
				+ 			  "{\"value\":\"" + this.idParcela +   "\",\"name\":\"idParcela\"}";

		jsonFavorecido = "{\\\"email\\\":\\\"" + this.selectedRecebedor.getEmail() + "\\\",\\\"due_date\\\":\\\"" + 
				dataVencimento.get(Calendar.YEAR) + mes + dia + "\\\", \\\"items\\\":[" + jsonItens + "]," + jsonPayer + "}";

		jsonFavorecido = "{\"email\":\"" + this.selectedRecebedor.getEmail() + "\", \"due_date\":\"" + 
				dataVencimento.get(Calendar.YEAR) + mes + dia + "\",\"items\":[" + jsonItens + "],\"custom_variables\":[" + jsonCustomVariables + "]," +  jsonPayer + ",\"payable_with\":[\"all\"]}";

		return jsonFavorecido;
	}

	/****
	 * 
	 * GERAÇÃO DA COBRANÇA SIMPLES
	 * 
	 * @return
	 */
	public void geraCobrancaSimplesLote(String idLiveTokenIugu, long idCedentem, String origemChamada) {
		int HTTP_COD_SUCESSO = 200;
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.selectedRecebedor != null) {
			boolean dadosValidos = true;

			// validações
			if (this.selectedRecebedor.getCpf() == null && this.selectedRecebedor.getCnpj() == null) {
				dadosValidos = false;

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (CPF ou CNPJ inválidos) !", ""));				
			}

			if (this.selectedRecebedor.getEndereco().equals("") || this.selectedRecebedor.getCep().equals("")) {
				dadosValidos = false;

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (Endereço ou CEP inválidos) !", ""));	
			}


			if (dadosValidos) {
				try {							
					URL myURL = new URL("https://api.iugu.com/v1/invoices?api_token=" + idLiveTokenIugu);

					TimeZone zone = TimeZone.getDefault();  
					Locale locale = new Locale("pt", "BR");  
					Calendar dataHoje = Calendar.getInstance(zone, locale);
					dataHoje.setTime(this.dataVencimento);
					dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
					dataHoje.set(Calendar.MINUTE, 0);  
					dataHoje.set(Calendar.SECOND, 0);  
					dataHoje.set(Calendar.MILLISECOND, 0);	

					String mes = String.valueOf(dataHoje.get(Calendar.MONTH) + 1);
					if (mes.length() == 1) {
						mes = "0" + mes;
					}

					String dia = String.valueOf(dataHoje.get(Calendar.DAY_OF_MONTH));
					if (dia.length() == 1) {
						dia = "0" + dia;
					}	

					int parcela = Integer.valueOf(this.parcela);

					int qtdeParcelas = Integer.valueOf(this.qtdeParcelas);

					this.faturasDownloadIUGU = new ArrayList<FaturaIUGU>();

					for (int i = 0; i < qtdeParcelas; i++) {
						if (i > 0) {
							parcela = parcela + 1;
							dataHoje.add(Calendar.MONTH, 1);

							mes = String.valueOf(dataHoje.get(Calendar.MONTH) + 1);
							if (mes.length() == 1) {
								mes = "0" + mes;
							}

							dia = String.valueOf(dataHoje.get(Calendar.DAY_OF_MONTH));
							if (dia.length() == 1) {
								dia = "0" + dia;
							}	
						}
						String dados = composeJSONCobrancaSimplesLote(dataHoje, mes, dia, String.valueOf(parcela), origemChamada);
						//JSONObject jsonObj = new JSONObject("{\"email\":\"webnowbr@gmail.com\",\"due_date\":\"20181212\",\"items\":[{\"description\":\"Cobrança\",\"quantity\":1,\"price_cents\":1486}],\"payer\":{\"cpf_cnpj\":\"31255904852\",\"name\":\"HERMES VIEIRA JUNIOR\",\"address\":{\"zip_code\":\"13073035\",\"street\":\"ENDEREÇO COMPLETO\",\"number\":\"1111\"}}}");
						JSONObject jsonObj = new JSONObject(dados);
						byte[] postDataBytes = jsonObj.toString().getBytes();

						HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
						myURLConnection.setUseCaches(false);
						myURLConnection.setRequestMethod("POST");
						myURLConnection.setRequestProperty("Accept", "application/json");
						myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
						myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
						myURLConnection.setDoOutput(true);
						myURLConnection.getOutputStream().write(postDataBytes);

						//myURLConnection.setDoInput(true);

						// LEITURA DOS DADOS EM STRING
						Thread.sleep(500);						

						if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
							context.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Erro na geração da Cobrança! " + myURLConnection.getResponseCode() + ": " + getErroIugu(myURLConnection.getErrorStream()), ""));
							//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode() + ": " + myResponse.getString("error"));				
						} else {
							// Seta o ID da fatura na Parcela do Siscoat
							JSONObject myResponse = null;

							myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

							this.urlFatura = myResponse.getString("secure_url");

							FaturaIUGU fatura = new FaturaIUGU();
							fatura.setSacado(this.selectedRecebedor.getNome());
							fatura.setSecure_url(this.urlFatura + ".pdf");

							this.faturasDownloadIUGU.add(fatura);

							context.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Cobrança gerada com sucesso!", ""));
						}

						myURLConnection.disconnect();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				consultarFaturasSubContaFaturaSimplesParam(this.selectedRecebedor.getIuguLiveApiToken());
			}
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Favor selecionar um favorecido!", ""));
		}
	}

	/**
	 * CHAMADO PELO MENU DE CONSULTA SALDO SUBCONTAS
	 * @return
	 */
	public String clearFieldsSaldoSubContaIugu() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();
		this.listRecebedoresAux = this.listRecebedores;	
		clearRecebedorAux();

		this.paramSenha = null;
		this.senhaStorage = "";

		this.valorItem = null;

		this.contaMestre = true;
		this.contaMestreAux = false;
		this.operacaoTransferencia = true;

		this.saldoSubContaLiberado = null;
		this.saldoSubContaALiberar = null;

		this.observacao = "";

		clearGeraRecibo();

		return "/Atendimento/Cobranca/SaldoSubContasIugu.xhtml";
	}


	/**
	 * CONSULTA SALDO SUBCONTAS
	 */
	public void consultarSaldoSubConta() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.iugu.com/v1/accounts/7D4D20A4F1184FEB91126DFEAD86AED8?api_token=bd88479c57011124c25638b26572e453");

			if (!this.contaMestre) {
				if (this.selectedRecebedor.getId() <= 0) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Saldo SubConta IUGU: Para efetuar a consulta é necessário selecionar uma SubConta!", ""));

					valid = false;
				} else {
					myURL = new URL("https://api.iugu.com/v1/accounts/" + this.selectedRecebedor.getIuguAccountId() + "?api_token=" + this.selectedRecebedor.getIuguLiveApiToken());
				}				
			} else {
				myURL = new URL("https://api.iugu.com/v1/accounts/7D4D20A4F1184FEB91126DFEAD86AED8?api_token=bd88479c57011124c25638b26572e453");
			}	

			if (valid) {			
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Saldo SubConta IUGU: Erro ao consultar SubConta! (Erro: " + erro + ")!", ""));

				} else {				
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					this.saldoSubContaLiberado = myResponse.getString("balance_available_for_withdraw");
					this.saldoSubContaALiberar = myResponse.getString("balance");

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consulta Saldo SubConta IUGU: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * CONSULTA SALDO SUBCONTAS POR PARAMETRO
	 */
	public BigDecimal consultarSaldoSubConta(PagadorRecebedor recebedor) {
		BigDecimal saldoIUGU = BigDecimal.ZERO;

		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.iugu.com/v1/accounts/" + recebedor.getIuguAccountId() + "?api_token=" + recebedor.getIuguLiveApiToken());

			if (valid) {			
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */

				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());
				} else {				
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());
					String valorStr = myResponse.getString("balance_available_for_withdraw");
					System.out.println(valorStr);

					if (valorStr.contains("R$")) {
						valorStr = valorStr.replace("R$ ", "");
						valorStr = valorStr.replace(".", "");
						valorStr = valorStr.replace(",", ".");
					} else {
						valorStr = valorStr.replace(" BRL","");
						valorStr = valorStr.replace(",", "");
					}

					saldoIUGU = new BigDecimal(valorStr);

					System.out.println(saldoIUGU);
				}

				myURLConnection.disconnect();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return saldoIUGU;
	}	


	/**
	 * CHAMADO PELO MENU DE EXTRATO FINANCEIRO SUBCONTAS
	 * @return
	 */
	public String clearFieldsExtratoFinanceiroIugu() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();

		this.paramAno = "2021";
		this.paramMes = null;

		this.contaMestre = true;

		return "/Atendimento/Cobranca/ExtratoFinanceiroIugu.xhtml";
	}


	/**
	 * CONSULTA EXTRATO FINANCEIRO SUBCONTAS
	 */
	public void consultarExtratoFinanceiroSubConta() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			String urlComposta = "https://api.iugu.com/v1/accounts/financial?year=" + this.paramAno + "&month=" + this.paramMes + "&limit=1000&";

			String token = "bd88479c57011124c25638b26572e453";

			URL myURL = new URL(urlComposta + "api_token=bd88479c57011124c25638b26572e453");

			if (!this.contaMestre) {
				if (this.selectedRecebedor.getId() <= 0) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Extrato Financeiro SubConta IUGU: Para efetuar a consulta é necessário selecionar uma SubConta!", ""));

					valid = false;
				} else {
					myURL = new URL(urlComposta + "api_token=" + this.selectedRecebedor.getIuguLiveApiToken());
					token = this.selectedRecebedor.getIuguLiveApiToken();
				}				
			} else {
				myURL = new URL(urlComposta + "api_token=bd88479c57011124c25638b26572e453");
				token = "bd88479c57011124c25638b26572e453";
			}				

			//String dados = "{\"auto_withdraw\":false,\"fines\":true,\"per_day_interest\":true,\"late_payment_fine\":2}";

			//JSONObject jsonObj = new JSONObject(dados);
			//byte[] postDataBytes = jsonObj.toString().getBytes();

			if (valid) {			
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);
				//myURLConnection.getOutputStream().write(postDataBytes);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Extrato Financeiro SubConta IUGU: Erro ao consultar SubConta! (Erro: " + erro + ")!", ""));

				} else {				
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					JSONArray transacoes = myResponse.getJSONArray("transactions");

					this.faturasIUGU = new ArrayList<FaturaIUGU>();

					FaturaIUGU faturaTemp = new FaturaIUGU();
					this.totalMovimentacoes = BigDecimal.ZERO;

					for (int i = 0; i < transacoes.length(); i++) {
						FaturaIUGU faturaIUGU = new FaturaIUGU();

						JSONObject obj = transacoes.getJSONObject(i);

						String entry_dateOriginal = obj.getString("entry_date").substring(8, 10) + "/" + obj.getString("entry_date").substring(5, 7) + "/" + obj.getString("entry_date").substring(0, 4);
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
						Date entry_date = new java.sql.Date( ((java.util.Date)formatter.parse(entry_dateOriginal)).getTime() );

						faturaIUGU.setDataTransacao(entry_date);				

						String valorTemp = obj.getString("amount");

						String reference = obj.getString("reference");

						if (valorTemp.contains("R$")) {
							valorTemp = valorTemp.replace("R$ ", "");
							valorTemp = valorTemp.replace(".", "");
							valorTemp = valorTemp.replace(",", ".");
						} else {
							valorTemp = valorTemp.replace(" BRL","");
							valorTemp = valorTemp.replace(",", "");
						}

						faturaIUGU.setValor(valorTemp);
						faturaIUGU.setValorNum(new BigDecimal(valorTemp)); 

						faturaIUGU.setTipoTransacao(obj.getString("type"));

						if (faturaIUGU.getTipoTransacao().equals("credit")) {
							this.totalMovimentacoes = this.totalMovimentacoes.add(faturaIUGU.getValorNum());
						} else {
							this.totalMovimentacoes = this.totalMovimentacoes.subtract(faturaIUGU.getValorNum());
						}

						String valorSaldo = obj.getString("balance");

						if (valorSaldo.contains("R$")) {
							valorSaldo = valorSaldo.replace("R$ ", "");
							valorSaldo = valorSaldo.replace(".", "");
							valorSaldo = valorSaldo.replace(",", ".");
						} else {
							valorSaldo = valorSaldo.replace(" BRL","");
							valorSaldo = valorSaldo.replace(",", "");
						}

						faturaIUGU.setSaldo(valorSaldo);
						faturaIUGU.setSaldoNum(new BigDecimal(valorSaldo));

						/**
						 * tratamento da descrição
						 */

						// faturaIUGU.setDescricaoTransacao(obj.getString("description"));

						String descricaoOriginal = obj.getString("description");
						String descricaoModificada = null;
						String idConta = null;
						String idConta2 = null;
						PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
						PagadorRecebedor pagadorRecebedor = new PagadorRecebedor();
						PagadorRecebedor pagadorRecebedor2 = new PagadorRecebedor();

						ContratoCobranca contratoFatura = new ContratoCobranca();
						ContratoCobrancaDetalhes contratoDetalhesFatura = new ContratoCobrancaDetalhes();

						if (descricaoOriginal.contains("Liberação: Invoice") && !descricaoOriginal.contains("Tarifa")) {
							idConta = descricaoOriginal.substring(descricaoOriginal.indexOf("#") + 1,
									descricaoOriginal.length());

							descricaoModificada = "Liberação: Fatura ID " + idConta;

							String contratoDetalhe = getContratoByFaturaIUGU(idConta);

							if (contratoDetalhe != null && contratoDetalhe.length() == 7) {
								String numeroContrato = contratoDetalhe.substring(0, 5);

								String numeroParcela = contratoDetalhe.substring(5, contratoDetalhe.length());

								ContratoCobrancaDao ccDao = new ContratoCobrancaDao();								

								List<ContratoCobranca> contratoFaturaTmp = new ArrayList<ContratoCobranca>();
								contratoFaturaTmp = ccDao.findByFilter("numeroContrato", numeroContrato);

								if (contratoFaturaTmp.size() > 0) {
									contratoFatura = contratoFaturaTmp.get(0);
									
									for (ContratoCobrancaDetalhes ccd: contratoFatura.getListContratoCobrancaDetalhes()) {
										if (ccd.getNumeroParcela().equals(numeroParcela)) {
											contratoDetalhesFatura = ccd;
										}
									}		
	
									faturaIUGU.setNumeroContrato(contratoFatura.getNumeroContrato());
									faturaIUGU.setValorParcela(contratoDetalhesFatura.getVlrParcela());
									faturaIUGU.setDue_date(contratoDetalhesFatura.getDataVencimento());
	
									faturaIUGU.setSacado(contratoFatura.getPagador().getNome());
	
									faturaTemp.setIdTransacao(idConta);
									faturaTemp.setNumeroContrato(contratoFatura.getNumeroContrato());
									faturaTemp.setValorParcela(contratoDetalhesFatura.getVlrParcela());
									faturaTemp.setDue_date(contratoDetalhesFatura.getDataVencimento());
									faturaTemp.setSacado(contratoFatura.getPagador().getNome());
								}

								if (!this.contaMestre) {
									faturaIUGU.setCedente(this.selectedRecebedor.getNome());	
									faturaTemp.setCedente(this.selectedRecebedor.getNome());
								} else {
									faturaIUGU.setCedente("GALLERIA Mestre");
									faturaTemp.setCedente("GALLERIA Mestre");	
								}
							}
						}

						if (descricaoOriginal.contains("Transferencia de Conta") && !descricaoOriginal.contains("Tarifa")) {
							int index1 = descricaoOriginal.indexOf("#");

							descricaoModificada = null;

							idConta = descricaoOriginal.substring(index1 + 1, 59);

							idConta = idConta.replace("-", "").toUpperCase();

							pagadorRecebedor = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta);

							if (pagadorRecebedor.getNome() != null) {
								descricaoModificada = "Transferencia da SubConta " + pagadorRecebedor.getNome();
							} else {
								if (idConta.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
									descricaoModificada =  "Transferencia da SubConta GALLERIA Mestre";
								} else {
									descricaoModificada = "Transferencia da SubConta " + idConta;
								}
							}

							idConta2 = descricaoOriginal.substring(descricaoOriginal.indexOf("#", 60) + 1,
									descricaoOriginal.length());

							idConta2 = idConta2.replace("-", "").toUpperCase();

							pagadorRecebedor2 = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta2);

							if (pagadorRecebedor2.getNome() != null) {
								descricaoModificada = descricaoModificada + " para a SubConta " + pagadorRecebedor2.getNome();
							} else {
								if (idConta2.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
									descricaoModificada = descricaoModificada + " para a SubConta GALLERIA Mestre";
								} else {
									descricaoModificada = descricaoModificada + " para a SubConta " + idConta2;
								}
							}
						}

						if (descricaoOriginal.contains("Tarifas - Transferencia de Conta")) {
							int index1 = descricaoOriginal.indexOf("#");

							descricaoModificada = null;

							idConta = descricaoOriginal.substring(index1 + 1, 69);

							idConta = idConta.replace("-", "").toUpperCase();

							pagadorRecebedor = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta);

							if (pagadorRecebedor.getNome() != null) {
								descricaoModificada = "Transferencia da SubConta " + pagadorRecebedor.getNome();
							} else {
								if (idConta.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
									descricaoModificada =  "Transferencia da SubConta GALLERIA Mestre";
								} else {
									descricaoModificada = "Transferencia da SubConta " + idConta;
								}
							}

							idConta2 = descricaoOriginal.substring(descricaoOriginal.indexOf("#", 69) + 1,
									descricaoOriginal.length());

							idConta2 = idConta2.replace("-", "").toUpperCase();

							pagadorRecebedor2 = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta2);

							if (pagadorRecebedor2.getNome() != null) {
								descricaoModificada = descricaoModificada + " para a SubConta " + pagadorRecebedor2.getNome();
							} else {
								if (idConta2.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
									descricaoModificada = descricaoModificada + " para a SubConta GALLERIA Mestre";
								} else {
									descricaoModificada = descricaoModificada + " para a SubConta " + idConta2;
								}
							}
						}

						if (descricaoOriginal.contains("Tarifas - Liberação: Invoice")) {
							idConta = descricaoOriginal.substring(descricaoOriginal.indexOf("#") + 1,
									descricaoOriginal.length());

							descricaoModificada = "Tarifas - Liberação: Fatura ID " + idConta;

							// recupera dados do contrato da fatura paga
							faturaIUGU.setIdTransacao(faturaTemp.getIdTransacao());
							faturaIUGU.setNumeroContrato(faturaTemp.getNumeroContrato());
							faturaIUGU.setValorParcela(faturaTemp.getValorParcela());
							faturaIUGU.setDue_date(faturaTemp.getDue_date());
							faturaIUGU.setSacado(faturaTemp.getSacado());
							faturaIUGU.setCedente(faturaTemp.getCedente());							
							faturaTemp = new FaturaIUGU();
						}

						if (descricaoModificada == null) {
							descricaoModificada = descricaoOriginal;
						}

						faturaIUGU.setDescricaoTransacao(descricaoModificada);

						// Busca nome do sacado e data de vencimento da fatura
						JSONObject retornoFatura = getContratoByFaturaIUGUAndToken(reference, token);

						if (retornoFatura != null) {
							String dueDateOriginal = retornoFatura.getString("due_date").substring(8, 10) + "/" + retornoFatura.getString("due_date").substring(5, 7) + "/" + retornoFatura.getString("due_date").substring(0, 4);
							Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime());

							faturaIUGU.setDue_date(dueDate);
							faturaIUGU.setSecure_url(retornoFatura.getString("secure_url")); 

							JSONArray variables = retornoFatura.getJSONArray("variables");

							for (int k = 0; k < variables.length(); k++) {
								JSONObject objVariables = variables.getJSONObject(k);

								if (objVariables.getString("variable").equals("payer.email")) {
									faturaIUGU.setEmail(objVariables.getString("value"));
								}

								if (objVariables.getString("variable").equals("payer.name")) {
									faturaIUGU.setSacado(objVariables.getString("value"));
								}
							}							

							JSONArray dadosContrato = retornoFatura.getJSONArray("items");

							for (int m = 0; m < dadosContrato.length(); m++) {
								JSONObject dados = dadosContrato.getJSONObject(m);

								String description = dados.getString("description");

								if (description.contains("SERVIÇO DE COBRANÇA - CONTRATO:")) {
									description = description.substring(32, description.indexOf("/") - 1);
									faturaIUGU.setNumeroContrato(description);
								}

							}	
						}

						// transforma texto do Tipo Transação
						if (faturaIUGU.getTipoTransacao().equals("credit")) {
							faturaIUGU.setTipoTransacao("Crédito");
						}

						if (faturaIUGU.getTipoTransacao().equals("debit")) {
							faturaIUGU.setTipoTransacao("Débito");
						}

						this.faturasIUGU.add(faturaIUGU);
					}		

					if (this.totalMovimentacoes != BigDecimal.ZERO) {
						totalMovimentacoesStr = this.totalMovimentacoes.toString().replace(".", ",");
					}

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consulta Extrato Financeiro SubConta IUGU: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	public String getContratoByFaturaIUGU(String idFatura) {

		String retorno = null;

		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			String urlComposta = "https://api.iugu.com/v1/invoices/" + idFatura;

			URL myURL = new URL(urlComposta + "?api_token=bd88479c57011124c25638b26572e453");

			if (!this.contaMestre) {
				if (this.selectedRecebedor.getId() <= 0) {
					valid = false;
				} else {
					myURL = new URL(urlComposta + "?api_token=" + this.selectedRecebedor.getIuguLiveApiToken());
				}				
			} else {
				myURL = new URL(urlComposta + "?api_token=bd88479c57011124c25638b26572e453");
			}		

			if (valid) {			
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);
				//myURLConnection.getOutputStream().write(postDataBytes);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */

				if (myURLConnection.getResponseCode() == HTTP_COD_SUCESSO) {	
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					JSONArray items = myResponse.getJSONArray("items");

					for (int i = 0; i < items.length(); i++) {
						JSONObject obj = items.getJSONObject(i);

						retorno = obj.getString("description");
						retorno = retorno.replaceAll("SERVIÇO DE COBRANÇA - CONTRATO: ", "");
						retorno = retorno.replaceAll(" / PARCELA: ", "");		

						if (retorno.length() == 7) {
							return retorno;
						} else {
							return null;
						}

					}
				} else {
					return retorno;
				}

				myURLConnection.disconnect();
			} else {
				return retorno;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}

	public JSONObject getContratoByFaturaIUGUAndToken(String idFatura, String likeToken) {

		String retorno = null;
		JSONObject myResponse = null;

		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			String urlComposta = "https://api.iugu.com/v1/invoices/" + idFatura;

			URL myURL = new URL(urlComposta + "?api_token=" + likeToken);

			if (valid) {			
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);
				//myURLConnection.getOutputStream().write(postDataBytes);

				String erro = "";				

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */

				if (myURLConnection.getResponseCode() == HTTP_COD_SUCESSO) {	
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());
				} else {
					return myResponse;
				}

				myURLConnection.disconnect();
			} else {
				return myResponse;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myResponse;
	}


	/**
	 * CONSULTA CONCILIAÇÃO PELO EXTRATO FINANCEIRO
	 */
	public void clearContadoresConciliacao() {		
		this.totalEntrada = BigDecimal.ZERO;
		this.totalSaida = BigDecimal.ZERO;
		this.totalTaxas = BigDecimal.ZERO;
		this.qtdeFaturasPagas = 0;
	}

	/**
	 * CONSULTA CONCILIAÇÃO PELO EXTRATO FINANCEIRO
	 */
	public void consultarConciliacaoExtratoFinanceiroSubConta() {

		// TODO saldo total por subconta
		// TODO saldo total vivo sistema
		// TODO timestamp da transacao
		// TODO COokpit

		FacesContext context = FacesContext.getCurrentInstance();

		List<PagadorRecebedor> contasIUGU = new ArrayList<PagadorRecebedor>();
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		contasIUGU = pDao.getSubContasIugu();		

		this.faturasIUGU = new ArrayList<FaturaIUGU>();

		clearContadoresConciliacao();

		for (PagadorRecebedor contaIUGU : contasIUGU) {
			try {			
				int HTTP_COD_SUCESSO = 200;

				String urlComposta = "https://api.iugu.com/v1/accounts/financial?year=" + this.paramAno + "&month=" + this.paramMes + "&day=" + this.paramDia + "&limit=1000&";	

				URL myURL = new URL(urlComposta + "api_token=" + contaIUGU.getIuguLiveApiToken());

				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);
				//myURLConnection.getOutputStream().write(postDataBytes);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());

					//context.addMessage(null, new FacesMessage(
					//		FacesMessage.SEVERITY_ERROR, "Consulta Extrato Financeiro SubConta IUGU: Erro ao consultar SubConta! (Erro: " + erro + ")!", ""));

				} else {				
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					JSONArray transacoes = myResponse.getJSONArray("transactions");

					for (int i = 0; i < transacoes.length(); i++) {
						JSONObject obj = transacoes.getJSONObject(i);
						String descricaoOriginal = obj.getString("description");

						if (!descricaoOriginal.contains("Transferencia de Conta")) {
							FaturaIUGU faturaIUGU = new FaturaIUGU();

							String entry_dateOriginal = obj.getString("entry_date").substring(8, 10) + "/" + obj.getString("entry_date").substring(5, 7) + "/" + obj.getString("entry_date").substring(0, 4);
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							Date entry_date = new java.sql.Date( ((java.util.Date)formatter.parse(entry_dateOriginal)).getTime() );

							faturaIUGU.setDataTransacao(entry_date);
							faturaIUGU.setTipoTransacao(obj.getString("type"));

							faturaIUGU.setValor(obj.getString("amount"));
							faturaIUGU.setSaldo(obj.getString("balance")); 

							String valorTemp = faturaIUGU.getValor();

							if (valorTemp.contains("R$")) {
								valorTemp = valorTemp.replace("R$ ", "");
								valorTemp = valorTemp.replace(".", "");
								valorTemp = valorTemp.replace(",", ".");
							} else {
								valorTemp = valorTemp.replace(" BRL","");
								valorTemp = valorTemp.replace(",", "");
							}

							String saldoTemp = faturaIUGU.getValor();

							if (saldoTemp.contains("R$")) {
								saldoTemp = saldoTemp.replace("R$ ", "");
								saldoTemp = saldoTemp.replace(".", "");
								saldoTemp = saldoTemp.replace(",", ".");
							} else {
								saldoTemp = saldoTemp.replace(" BRL","");
								saldoTemp = saldoTemp.replace(",", "");
							}

							// se crédito, troca o label e soma o valor total recebido
							if (faturaIUGU.getTipoTransacao().equals("credit")) {
								faturaIUGU.setTipoTransacao("Crédito");
								this.totalEntrada = this.totalEntrada.add(new BigDecimal(valorTemp));

								// faz somatoria da qtde de faturas pagas
								if (descricaoOriginal.contains("Liberação: Invoice")) {
									this.qtdeFaturasPagas = this.qtdeFaturasPagas + 1;
								}
							} 

							// se crédito, troca o label e soma o valor total debitado (entre taxa / transf e saque)
							if (faturaIUGU.getTipoTransacao().equals("debit")) {
								faturaIUGU.setTipoTransacao("Débito");

								if (descricaoOriginal.contains("Tarifas - Liberação")) {
									this.totalTaxas = this.totalTaxas.add(new BigDecimal(valorTemp));
								} else {
									this.totalSaida = this.totalSaida.add(new BigDecimal(valorTemp));
								}												
							}

							//trata os totais por subcontas
							if (faturaIUGU.getTotalMovimentacoes() == null) {
								faturaIUGU.setTotalMovimentacoes(BigDecimal.ZERO);
							}

							if (faturaIUGU.getTotalSaldo() == null) {
								faturaIUGU.setTotalSaldo(BigDecimal.ZERO);
							}

							if (faturaIUGU.getTipoTransacao().equals("credit")) {
								faturaIUGU.setTotalMovimentacoes(faturaIUGU.getTotalMovimentacoes().subtract(new BigDecimal(valorTemp)));
								faturaIUGU.setTotalSaldo(faturaIUGU.getTotalSaldo().subtract(new BigDecimal(saldoTemp)));
							} else {
								faturaIUGU.setTotalMovimentacoes(faturaIUGU.getTotalMovimentacoes().add(new BigDecimal(valorTemp)));
								faturaIUGU.setTotalSaldo(faturaIUGU.getTotalSaldo().add(new BigDecimal(saldoTemp)));
							}

							faturaIUGU.setNomeContaIUGU(contaIUGU.getNome());

							/**
							 * tratamento da descrição
							 */

							// faturaIUGU.setDescricaoTransacao(obj.getString("description"));

							String descricaoModificada = null;
							String idConta = null;
							String idConta2 = null;
							PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
							PagadorRecebedor pagadorRecebedor = new PagadorRecebedor();
							PagadorRecebedor pagadorRecebedor2 = new PagadorRecebedor();

							if (descricaoOriginal.contains("Liberação: Invoice") && !descricaoOriginal.contains("Tarifa")) {
								idConta = descricaoOriginal.substring(descricaoOriginal.indexOf("#") + 1,
										descricaoOriginal.length());

								descricaoModificada = "Liberação: Fatura ID " + idConta;
							}

							if (descricaoOriginal.contains("Transferencia de Conta") && !descricaoOriginal.contains("Tarifa")) {
								int index1 = descricaoOriginal.indexOf("#");

								descricaoModificada = null;

								idConta = descricaoOriginal.substring(index1 + 1, 59);

								idConta = idConta.replace("-", "").toUpperCase();

								pagadorRecebedor = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta);

								if (pagadorRecebedor.getNome() != null) {
									descricaoModificada = "Transferencia da SubConta " + pagadorRecebedor.getNome();
								} else {
									if (idConta.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
										descricaoModificada =  "Transferencia da SubConta GALLERIA Mestre";
									} else {
										descricaoModificada = "Transferencia da SubConta " + idConta;
									}
								}

								idConta2 = descricaoOriginal.substring(descricaoOriginal.indexOf("#", 60) + 1,
										descricaoOriginal.length());

								idConta2 = idConta2.replace("-", "").toUpperCase();

								pagadorRecebedor2 = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta2);

								if (pagadorRecebedor2.getNome() != null) {
									descricaoModificada = descricaoModificada + " para a SubConta " + pagadorRecebedor2.getNome();
								} else {
									if (idConta2.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
										descricaoModificada = descricaoModificada + " para a SubConta GALLERIA Mestre";
									} else {
										descricaoModificada = descricaoModificada + " para a SubConta " + idConta2;
									}
								}
							}

							if (descricaoOriginal.contains("Tarifas - Transferencia de Conta")) {
								int index1 = descricaoOriginal.indexOf("#");

								descricaoModificada = null;

								idConta = descricaoOriginal.substring(index1 + 1, 69);

								idConta = idConta.replace("-", "").toUpperCase();

								pagadorRecebedor = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta);

								if (pagadorRecebedor.getNome() != null) {
									descricaoModificada = "Transferencia da SubConta " + pagadorRecebedor.getNome();
								} else {
									if (idConta.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
										descricaoModificada =  "Transferencia da SubConta GALLERIA Mestre";
									} else {
										descricaoModificada = "Transferencia da SubConta " + idConta;
									}
								}

								idConta2 = descricaoOriginal.substring(descricaoOriginal.indexOf("#", 69) + 1,
										descricaoOriginal.length());

								idConta2 = idConta2.replace("-", "").toUpperCase();

								pagadorRecebedor2 = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idConta2);

								if (pagadorRecebedor2.getNome() != null) {
									descricaoModificada = descricaoModificada + " para a SubConta " + pagadorRecebedor2.getNome();
								} else {
									if (idConta2.equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
										descricaoModificada = descricaoModificada + " para a SubConta GALLERIA Mestre";
									} else {
										descricaoModificada = descricaoModificada + " para a SubConta " + idConta2;
									}
								}
							}

							if (descricaoOriginal.contains("Tarifas - Liberação: Invoice")) {
								idConta = descricaoOriginal.substring(descricaoOriginal.indexOf("#") + 1,
										descricaoOriginal.length());

								descricaoModificada = "Tarifas - Liberação: Fatura ID " + idConta;
							}

							if (descricaoModificada == null) {
								descricaoModificada = descricaoOriginal;
							}

							faturaIUGU.setDescricaoTransacao(descricaoModificada);

							// busca a observação referente a transferência bancária.
							if (descricaoOriginal.contains("Pedido de saque")) {
								TransferenciasObservacoesIUGUDao transferenciasObservacoesIUGUDao = new TransferenciasObservacoesIUGUDao();
								List<TransferenciasObservacoesIUGU> transferenciasObservacoesIUGU = new ArrayList<TransferenciasObservacoesIUGU>();
								transferenciasObservacoesIUGU = transferenciasObservacoesIUGUDao.findByFilter("idTransferencia", obj.getString("reference"));

								if (transferenciasObservacoesIUGU.size() > 0) {
									faturaIUGU.setObservacaoSaque(transferenciasObservacoesIUGU.get(0).getObservacao());
								}	
							}

							this.faturasIUGU.add(faturaIUGU);
						}
					}				
				}

				myURLConnection.disconnect();
				//}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// processa somatório dos saldos das subcontas IUGU
		//processaConciliacaoSaldoSubcontas();

		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Consulta Extrato Financeiro SubConta IUGU: Consulta efetuada com sucesso!", ""));
	}

	/**
	 * CHAMADO PELO MÉTODO consultarConciliacaoExtratoFinanceiroSubConta
	 * PARA CONSOLIDAR TODOS OS SALDOS DAS SUBCONTAS DO SISTEMA
	 */
	public void processaConciliacaoSaldoSubcontas() {
		FacesContext context = FacesContext.getCurrentInstance();

		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		List<PagadorRecebedor> subcontas = new ArrayList<PagadorRecebedor>();
		subcontas = prDao.getSubContasIugu();
		this.saldosIUGU = new ArrayList<SaldoIUGU>();
		SaldoIUGU saldoIUGU = new SaldoIUGU();

		this.totalSaldoSubcontas = BigDecimal.ZERO;

		// SOMA COM O SALDO DA SUBCONTA MESTRE
		PagadorRecebedor pMestre = new PagadorRecebedor();
		pMestre.setNome("GALLERIA - CONTA MESTRE");
		pMestre.setIuguAccountId("7D4D20A4F1184FEB91126DFEAD86AED8");
		pMestre.setIuguLiveApiToken("bd88479c57011124c25638b26572e453");

		saldoIUGU = new SaldoIUGU();
		BigDecimal saldo = BigDecimal.ZERO;

		saldo = consultarSaldoSubConta(pMestre);

		saldoIUGU.setSubConta(pMestre);
		saldoIUGU.setTotalSaldo(saldo);

		this.totalSaldoSubcontas = this.totalSaldoSubcontas.add(saldo);

		this.saldosIUGU.add(saldoIUGU);

		// SOMA DEMAIS CONTAS
		if (subcontas.size() > 0) {
			for (PagadorRecebedor subconta : subcontas) {
				saldoIUGU = new SaldoIUGU();
				saldo = BigDecimal.ZERO;

				saldo = consultarSaldoSubConta(subconta);

				saldoIUGU.setSubConta(subconta);
				saldoIUGU.setTotalSaldo(saldo);

				if (saldo.compareTo(BigDecimal.ZERO) != 0) {					
					this.saldosIUGU.add(saldoIUGU);	
				}
			}

			// Soma Todos os saldos
			for (SaldoIUGU su : this.saldosIUGU) {
				if (!su.getSubConta().getIuguAccountId().equals("7D4D20A4F1184FEB91126DFEAD86AED8")) {
					this.totalSaldoSubcontas = this.totalSaldoSubcontas.add(su.getTotalSaldo());
				}
			}						
		}	

		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Consulta Extrato Financeiro SubConta IUGU: Consulta efetuada com sucesso!", ""));
	}

	/**
	 * CHAMADO PELO MENU DE CONSULTA SALDO SUBCONTAS
	 * @return
	 */
	public String clearFieldsSubContasIugu() {
		this.nomeSubConta = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.findAll();
		clearRecebedor();

		//consultarSubContasIUGU();

		return "/Atendimento/Cobranca/SubContasIugu.xhtml";
	}

	/**
	 * CHAMADO PELO MENU DE CONSULTA SALDO SUBCONTAS
	 * @return
	 */
	public String consultaSubContaPorNome() {
		consultarSubContasIUGU();

		return "/Atendimento/Cobranca/SubContasIugu.xhtml";
	}

	/**
	 * CHAMADO PELO MENU DE CONSULTA SALDO SUBCONTAS
	 */
	public BigDecimal consultarSaldoSubContaIdIUGU(String iuguAccountId, String iuguLiveApiToken) {
		BigDecimal saldoIUGU = BigDecimal.ZERO;

		try {			

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.iugu.com/v1/accounts/" + iuguAccountId + "?api_token=" + iuguLiveApiToken);

			if (valid) {			
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */

				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());
				} else {				
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());
					String valorStr = myResponse.getString("balance_available_for_withdraw");
					valorStr = valorStr.replace("R$ ", "").replace(".", "").replace(",", ".");

					if (valorStr.equals("0.00")) {
						saldoIUGU = BigDecimal.ZERO;
					} else {
						saldoIUGU = new BigDecimal(valorStr);
					}					
				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return saldoIUGU;
	}

	/**
	 * CONSULTA SALDO SUBCONTAS
	 */
	public void consultarSubContasIUGU() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			this.subContasIUGU = new ArrayList<SubContaIUGU>();

			int HTTP_COD_SUCESSO = 200;

			URL myURL;

			if (this.nomeSubConta != null) {
				if (!this.nomeSubConta.equals("")) {
					myURL = new URL("https://api.iugu.com/v1/marketplace?limit=1000&query=" + this.nomeSubConta + "&api_token=bd88479c57011124c25638b26572e453");
				} else {
					myURL = new URL("https://api.iugu.com/v1/marketplace?limit=1000&api_token=bd88479c57011124c25638b26572e453");
				}				
			} else {
				myURL = new URL("https://api.iugu.com/v1/marketplace?limit=1000&api_token=bd88479c57011124c25638b26572e453"); 
			}

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Consulta SubContas IUGU: Erro ao consultar SubContas! (Erro: " + erro + ")!", ""));

			} else {	
				PagadorRecebedorDao prDao = new PagadorRecebedorDao();
				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				JSONArray subConta = myResponse.getJSONArray("items");

				for (int i = 0; i < subConta.length(); i++) {
					SubContaIUGU subContaIUGU = new SubContaIUGU();

					JSONObject obj = subConta.getJSONObject(i);

					subContaIUGU.setId(obj.getString("id"));
					subContaIUGU.setName(obj.getString("name"));

					Object verified = obj.get("verified");

					if (!verified.equals(null)) {
						if ((Boolean) verified) {
							subContaIUGU.setVerificada("Sim");
						} else {
							subContaIUGU.setVerificada("Não");
						}
					} else {
						subContaIUGU.setVerificada("Não");
					}

					// verifica se subconta existe no sistema e busca o saldo do mesmo
					List<PagadorRecebedor> pr = new ArrayList<PagadorRecebedor>();
					pr = prDao.findByFilter("iuguAccountId", subContaIUGU.getId());

					if (pr.size() > 0) {
						subContaIUGU.setSaldo(consultarSaldoSubContaIdIUGU(pr.get(0).getIuguAccountId(), pr.get(0).getIuguLiveApiToken()));
						this.subContasIUGU.add(subContaIUGU);	
					}					
				}

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Consulta SubContas IUGU: Consulta efetuada com sucesso!", ""));				
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * CHAMADO PELO MENU DE CONSULTA FATURAS SUBCONTAS
	 * @return
	 */
	public String clearFieldsFaturasSubContaIugu() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();
		
		clearRecebedor();
		this.faturasIUGU = new ArrayList<FaturaIUGU>();

		return "/Atendimento/Cobranca/FaturasSubContasIugu.xhtml";
	}	

	/**
	 * CONSULTA FATURAS SUBCONTAS
	 */
	public void consultarFaturasContaMestreGalleria() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoices?limit=10&api_token=bd88479c57011124c25638b26572e453");

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Consultar Faturas SubConta IUGU: Erro ao consultar faturas SubConta! (Erro: " + erro + ")!", ""));

			} else {				
				this.faturasIUGU = new ArrayList<FaturaIUGU>();

				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				JSONArray faturas = myResponse.getJSONArray("items");

				for (int i = 0; i < faturas.length(); i++) {
					FaturaIUGU faturaIUGU = new FaturaIUGU();

					JSONObject obj = faturas.getJSONObject(i);

					String dueDateOriginal = obj.getString("due_date").substring(8, 10) + "/" + obj.getString("due_date").substring(5, 7) + "/" + obj.getString("due_date").substring(0, 4);
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime() );

					faturaIUGU.setId(obj.getString("id"));
					faturaIUGU.setDue_date(dueDate);
					faturaIUGU.setSecure_url(obj.getString("secure_url"));
					faturaIUGU.setTotal(obj.getString("total"));
					faturaIUGU.setStatus(obj.getString("status"));    

					JSONArray variables = obj.getJSONArray("variables");

					for (int j = 0; j < variables.length(); j++) {
						JSONObject objVariables = variables.getJSONObject(j);

						if (objVariables.getString("variable").equals("payer.email")) {
							faturaIUGU.setEmail(objVariables.getString("value"));
						}

						if (objVariables.getString("variable").equals("payer.name")) {
							faturaIUGU.setSacado(objVariables.getString("value"));
						}
					}	

					this.faturasIUGU.add(faturaIUGU);
				}				

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Consultar Faturas SubConta IUGU: Consulta efetuada com sucesso!", ""));

			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * CONSULTA FATURAS SUBCONTAS
	 */
	public void consultarFaturasSubContaFaturaSimples() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoices?api_token=" + this.selectedCedente.getIuguLiveApiToken());

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Consultar Faturas SubConta IUGU: Erro ao consultar faturas SubConta! (Erro: " + erro + ")!", ""));

			} else {				
				this.faturasIUGU = new ArrayList<FaturaIUGU>();

				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				JSONArray faturas = myResponse.getJSONArray("items");

				for (int i = 0; i < faturas.length(); i++) {
					FaturaIUGU faturaIUGU = new FaturaIUGU();

					JSONObject obj = faturas.getJSONObject(i);

					String dueDateOriginal = obj.getString("due_date").substring(8, 10) + "/" + obj.getString("due_date").substring(5, 7) + "/" + obj.getString("due_date").substring(0, 4);
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime() );

					faturaIUGU.setId(obj.getString("id"));
					faturaIUGU.setDue_date(dueDate);
					faturaIUGU.setSecure_url(obj.getString("secure_url"));
					faturaIUGU.setTotal(obj.getString("total"));
					faturaIUGU.setStatus(obj.getString("status"));    

					JSONArray variables = obj.getJSONArray("variables");

					for (int j = 0; j < variables.length(); j++) {
						JSONObject objVariables = variables.getJSONObject(j);

						if (objVariables.getString("variable").equals("payer.email")) {
							faturaIUGU.setEmail(objVariables.getString("value"));
						}

						if (objVariables.getString("variable").equals("payer.name")) {
							faturaIUGU.setSacado(objVariables.getString("value"));
						}
					}	

					this.faturasIUGU.add(faturaIUGU);
				}				

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Consultar Faturas SubConta IUGU: Consulta efetuada com sucesso!", ""));

			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * CONSULTA FATURAS SUBCONTAS
	 */
	public void consultarFaturasSubContaFaturaSimplesParam(String liveToken) {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoices?api_token=" + liveToken);

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Consultar Faturas SubConta IUGU: Erro ao consultar faturas SubConta! (Erro: " + erro + ")!", ""));

			} else {				
				this.faturasIUGU = new ArrayList<FaturaIUGU>();

				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				JSONArray faturas = myResponse.getJSONArray("items");

				for (int i = 0; i < faturas.length(); i++) {
					FaturaIUGU faturaIUGU = new FaturaIUGU();

					JSONObject obj = faturas.getJSONObject(i);

					String dueDateOriginal = obj.getString("due_date").substring(8, 10) + "/" + obj.getString("due_date").substring(5, 7) + "/" + obj.getString("due_date").substring(0, 4);
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime() );

					faturaIUGU.setId(obj.getString("id"));
					faturaIUGU.setDue_date(dueDate);
					faturaIUGU.setSecure_url(obj.getString("secure_url"));
					faturaIUGU.setTotal(obj.getString("total"));
					faturaIUGU.setStatus(obj.getString("status"));    

					JSONArray variables = obj.getJSONArray("variables");

					for (int j = 0; j < variables.length(); j++) {
						JSONObject objVariables = variables.getJSONObject(j);

						if (objVariables.getString("variable").equals("payer.email")) {
							faturaIUGU.setEmail(objVariables.getString("value"));
						}

						if (objVariables.getString("variable").equals("payer.name")) {
							faturaIUGU.setSacado(objVariables.getString("value"));
						}
					}	

					this.faturasIUGU.add(faturaIUGU);
				}				

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Consultar Faturas SubConta IUGU: Consulta efetuada com sucesso!", ""));

			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	



	/**
	 * CHAMADO PELO MENU DE CONSULTA FATURAS PERIODO
	 * @return
	 */
	public String clearFieldsFaturasPeriodoIugu() {
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		this.faturasIUGU = new ArrayList<FaturaIUGU>();
		this.relByVencimento = true;
		this.relByStatus = "Todas";

		return "/Atendimento/Cobranca/FaturasIuguPeriodo.xhtml";
	}	

	/**
	 * CHAMADO PELO MENU DE CONSULTA FATURAS PERIODO
	 */

	public void geraRelatorioFaturaConsolidadasPeriodo() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.faturasIUGU = new ArrayList<FaturaIUGU>();
					
		String login = getUsuarioLogado();

		//for (PagadorRecebedor pr : this.listRecebedores) {				
		//	consultarFaturasSubContaByToken(pr.getIuguLiveApiToken(), pr.getNome());			
		//}	
		
		consultarFaturasSubContaByToken("bd88479c57011124c25638b26572e453", "Galleria Correspondente Bancario Eireli");

		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Consultar Faturas SubConta IUGU: Consulta efetuada com sucesso!", ""));
	}

	/**
	 * CHAMADO PELO MENU DE CONCILIAÇÃO DE SAQUES
	 * @return
	 */
	public String clearFieldsConciliacaoSaques() {
		this.paramDia = null;
		this.paramMes = null;
		this.paramAno = null;

		clearContadoresConciliacao();

		populaDataPesquisa();

		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;
		this.relPorSubconta = false;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		this.saquesIUGU = new ArrayList<SaqueIUGU>();
		this.saldosIUGU = new ArrayList<SaldoIUGU>();
		this.totalSaldoSubcontas = BigDecimal.ZERO;

		this.relByStatus = "Todas";
		clearRecebedor();

		return "/Atendimento/Cobranca/ConciliacaoSaques.xhtml";
	}	

	/**
	 * CHAMADO PELA TELA DE CONCILIAÇÃO DE SAQUES
	 * @return
	 */
	public void populaDataPesquisa() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		String dia = String.valueOf(dataHoje.get(Calendar.DAY_OF_MONTH));
		if (dia.length() == 1) {
			dia = "0" + dia;
		}	
		this.paramDia = dia;

		String mes = String.valueOf(dataHoje.get(Calendar.MONTH) + 1);
		if (mes.length() == 1) {
			mes = "0" + mes;
		}
		this.paramMes = mes;

		String ano = String.valueOf(dataHoje.get(Calendar.YEAR));
		this.paramAno = ano;
	}

	/**
	 * CHAMADO PELA TELA DE CONCILIAÇÃO DE SAQUES
	 * @return
	 */
	public void changeTipoConciliacao() {
		if (!this.relPorSubconta) {
			this.relDataContratoInicio = null;
			this.relDataContratoFim = null;
			clearRecebedor();
		} 
	}

	/**
	 * CHAMADO PELA TELA DE CONCILIAÇÃO SAQUES 
	 */

	public void processarConciliacaoSaque() {

		this.faturasIUGU = new ArrayList<FaturaIUGU>();							

		//TODO	
		/*
		for (PagadorRecebedor pr : this.listRecebedores) {
			// para o usuario thaina estamos ocultando as faturas captiva, diorama e galache
			if (login.equals("thaina")) {
				if (pr.getId() != 645 && pr.getId() != 157 && pr.getId() != 775) {
					consultarFaturasSubContaByToken(pr.getIuguLiveApiToken(), pr.getNome());
				}
			} else {
				consultarFaturasSubContaByToken(pr.getIuguLiveApiToken(), pr.getNome());
			}				
		}	
		 */

		getConciliacaoSaque("44eb0cf2fd8e6e3e12bb879e517b5851");
	}

	/**
	 * CHAMADO PELA TELA DE CONCILIAÇÃO SAQUES 
	 * TODO O SERVIÇO IUGU ESTA COM PROBLEMA, SÓ RETORNA CONCILIAÇÃO DA CONTA MESTRE E NÃO DAS SUBCONTAS
	 * MESMO ASSIM O DA MESTRE ESTÁ COM PROBLEMAS NO RETORNO
	 *  */

	public void getConciliacaoSaque(String IuguLiveApiToken) {
		FacesContext context = FacesContext.getCurrentInstance();

		try {			
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/withdraw_conciliations?api_token=44eb0cf2fd8e6e3e12bb879e517b5851");

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			/*
			JSONObject jsonObj = new JSONObject("{\"status\":" + this.relByStatus + "}");
			byte[] postDataBytes = jsonObj.toString().getBytes();
			myURLConnection.getOutputStream().write(postDataBytes);
			 */
			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Conciliação de Saques: Erro ao processar conciliação! (Erro: " + erro + ")!", ""));
			} else {				
				this.saquesIUGU = new ArrayList<SaqueIUGU>();

				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				if (myResponse.has("withdraw_requests")) {
					JSONArray saques = myResponse.getJSONArray("withdraw_requests");

					for (int i = 0; i < saques.length(); i++) {
						SaqueIUGU saqueIUGU = new SaqueIUGU();

						JSONObject obj = saques.getJSONObject(i);

						String createdAtOriginal = obj.getString("created_at").substring(8, 10) + "/" + obj.getString("created_at").substring(5, 7) + "/" + obj.getString("created_at").substring(0, 4);
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
						Date createdAt = new java.sql.Date( ((java.util.Date)formatter.parse(createdAtOriginal)).getTime() );
						/*
						saqueIUGU.setId(obj.getString("id"));
						saqueIUGU.setCreated_at(createdAt);
						saqueIUGU.setAmount(obj.getString("amount"));
						saqueIUGU.setStatus(obj.getString("status"));    
						 */

						this.saquesIUGU.add(saqueIUGU);
					}				

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Conci: Consulta efetuada com sucesso!", ""));
				} else {

				}
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	// get perfil do usuário logado!!!
	public boolean getPerfilLogadoIsIuguPosto() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);				
			} 
		}

		if (usuario.isUserIuguPosto()) {
			return true;	
		} else {
			return false;
		}		
	}

	// get usuario logado!!!
	public String getUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);				
			} 
		}

		return usuario.getLogin();
	}

	/**
	 * CONSULTA FATURAS SUBCONTAS
	 */
	public void consultarFaturasSubContaByToken(String token, String cedente) {
		try {			
			int HTTP_COD_SUCESSO = 200;
			URL myURL;

			/*
			paid_at_from=2018-10-02T00%3A00%3A00-03%3A00
			paid_at_to=2018-10-02T23%3A59%3A59-03%3A00

			status_filter=paid 
			pending, paid, canceled, partially_paid, refunded, expired, authorized

			api_token=663718c5d28ceac2d975c85159e36412
			 */
			String urlComposed = "https://api.iugu.com/v1/invoices?";

			if (this.relByVencimento) {
				Calendar calVencimento = Calendar.getInstance();
				calVencimento.setTime(this.relDataContratoInicio);

				String dia = String.valueOf(calVencimento.get(Calendar.DAY_OF_MONTH));
				if (dia.length() == 1) {
					dia = "0" + dia;
				}

				String mes = String.valueOf(calVencimento.get(Calendar.MONTH)+1);
				if (mes.length() == 1) {
					mes = "0" + mes;
				}

				String ano = String.valueOf(calVencimento.get(Calendar.YEAR));

				urlComposed = urlComposed + "due_date=" + ano + "-" + mes + "-" + dia;

				if (this.relByStatus.equals("paid")) {
					urlComposed = urlComposed + "&status_filter=paid";
				}
				if (this.relByStatus.equals("pending")) {
					urlComposed = urlComposed + "&status_filter=pending";
				}
				if (this.relByStatus.equals("expired")) {
					urlComposed = urlComposed + "&status_filter=expired";
				}
				if (this.relByStatus.equals("canceled")) {
					urlComposed = urlComposed + "&status_filter=canceled";
				}
			} else {
				Calendar calInicio = Calendar.getInstance();
				calInicio.setTime(this.relDataContratoInicio);

				String diaInicio = String.valueOf(calInicio.get(Calendar.DAY_OF_MONTH));
				if (diaInicio.length() == 1) {
					diaInicio = "0" + diaInicio;
				}

				String mesInicio = String.valueOf(calInicio.get(Calendar.MONTH)+1);
				if (mesInicio.length() == 1) {
					mesInicio = "0" + mesInicio;
				}

				String anoInicio = String.valueOf(calInicio.get(Calendar.YEAR));

				Calendar calFim = Calendar.getInstance();
				calFim.setTime(this.relDataContratoFim);

				String diaFim = String.valueOf(calFim.get(Calendar.DAY_OF_MONTH));
				if (diaFim.length() == 1) {
					diaFim = "0" + diaFim;
				}

				String mesFim = String.valueOf(calFim.get(Calendar.MONTH)+1);
				if (mesFim.length() == 1) {
					mesFim = "0" + mesFim;
				}

				String anoFim = String.valueOf(calFim.get(Calendar.YEAR));

				urlComposed = urlComposed + "paid_at_from=" + anoInicio + "-" + mesInicio + "-" + diaInicio + "T00%3A00%3A00-03%3A00&paid_at_to=" + anoFim + "-" + mesFim + "-" + diaFim + "T23%3A59%3A59-03%3A00";
			}

			urlComposed = urlComposed + "&api_token=" + token;

			myURL = new URL(urlComposed);

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;	

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() == HTTP_COD_SUCESSO) {				
				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());
				
				JSONArray faturas = myResponse.getJSONArray("items");

				for (int i = 0; i < faturas.length(); i++) {
					FaturaIUGU faturaIUGU = new FaturaIUGU();

					JSONObject obj = faturas.getJSONObject(i);

					String dueDateOriginal = obj.getString("due_date").substring(8, 10) + "/" + obj.getString("due_date").substring(5, 7) + "/" + obj.getString("due_date").substring(0, 4);					
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime() );

					faturaIUGU.setId(obj.getString("id"));
					faturaIUGU.setDue_date(dueDate);
					faturaIUGU.setSecure_url(obj.getString("secure_url"));
					faturaIUGU.setTotal(obj.getString("total"));
					faturaIUGU.setStatus(obj.getString("status"));    
					faturaIUGU.setCedente(cedente);

					if (!obj.isNull("paid_at")) {
						String paidAtOriginal = obj.getString("paid_at").substring(8, 10) + "/" + obj.getString("paid_at").substring(5, 7) + "/" + obj.getString("paid_at").substring(0, 4);
						Date paidAt = new java.sql.Date( ((java.util.Date)formatter.parse(paidAtOriginal)).getTime() );
						faturaIUGU.setPaid_at(paidAt);
					}

					JSONArray variables = obj.getJSONArray("variables");

					for (int j = 0; j < variables.length(); j++) {
						JSONObject objVariables = variables.getJSONObject(j);

						if (objVariables.getString("variable").equals("payer.email")) {
							faturaIUGU.setEmail(objVariables.getString("value"));
						}

						if (objVariables.getString("variable").equals("payer.name")) {
							faturaIUGU.setSacado(objVariables.getString("value"));
						}
					}	

					this.faturasIUGU.add(faturaIUGU);
				}				
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	


	/**
	 * CONSULTA FATURAS SUBCONTAS
	 */
	public void consultarFaturasSubConta() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoices?api_token=" + this.selectedRecebedor.getIuguLiveApiToken());

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Consultar Faturas SubConta IUGU: Erro ao consultar faturas SubConta! (Erro: " + erro + ")!", ""));

			} else {				
				this.faturasIUGU = new ArrayList<FaturaIUGU>();

				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				JSONArray faturas = myResponse.getJSONArray("items");

				for (int i = 0; i < faturas.length(); i++) {
					FaturaIUGU faturaIUGU = new FaturaIUGU();

					JSONObject obj = faturas.getJSONObject(i);

					String dueDateOriginal = obj.getString("due_date").substring(8, 10) + "/" + obj.getString("due_date").substring(5, 7) + "/" + obj.getString("due_date").substring(0, 4);
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime() );

					faturaIUGU.setId(obj.getString("id"));
					faturaIUGU.setDue_date(dueDate);
					faturaIUGU.setSecure_url(obj.getString("secure_url"));
					faturaIUGU.setTotal(obj.getString("total"));
					faturaIUGU.setStatus(obj.getString("status"));    

					JSONArray variables = obj.getJSONArray("variables");

					for (int j = 0; j < variables.length(); j++) {
						JSONObject objVariables = variables.getJSONObject(j);

						if (objVariables.getString("variable").equals("payer.email")) {
							faturaIUGU.setEmail(objVariables.getString("value"));
						}

						if (objVariables.getString("variable").equals("payer.name")) {
							faturaIUGU.setSacado(objVariables.getString("value"));
						}
					}	

					this.faturasIUGU.add(faturaIUGU);
				}				

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Consultar Faturas SubConta IUGU: Consulta efetuada com sucesso!", ""));

			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * CHAMADO PELO MENU DE FATURAS CONSOLIDADAS
	 * @return
	 */
	public String clearFieldsFaturasConsolidadasIugu() {	
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;
		this.statusFatura = "Todas";

		this.faturasIUGU = new ArrayList<FaturaIUGU>();

		return "/Atendimento/Cobranca/FaturasConsolidadasIugu.xhtml";
	}

	/**
	 * CONSULTA FATURAS CONSOLIDADAS
	 */
	public void consultarFaturasConsolidadas() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoice_conciliations?api_token=bd88479c57011124c25638b26572e453");

			String params = null;

			if (!this.statusFatura.equals("Todas")) {
				if (this.statusFatura.equals("Pendentes")) {
					params = "pending";
				}

				if (this.statusFatura.equals("Pagas")) {
					params = "paid";
				}

				if (this.statusFatura.equals("Vencidas")) {
					params = "expired";
				}

				if (this.statusFatura.equals("Canceladas")) {
					params = "canceled";
				}				
			}		

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			if (params != null) {
				JSONObject jsonObj = new JSONObject("{\"status\":" + params + "}");
				byte[] postDataBytes = jsonObj.toString().getBytes();
				myURLConnection.getOutputStream().write(postDataBytes);
			}

			String erro = "";
			JSONObject myResponse = null;

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Consultar Faturas Consolidadas IUGU: Erro ao consultar faturas consolidadas! (Erro: " + erro + ")!", ""));

			} else {				
				this.faturasIUGU = new ArrayList<FaturaIUGU>();

				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				JSONArray faturas = myResponse.getJSONArray("invoices");

				for (int i = 0; i < faturas.length(); i++) {
					FaturaIUGU faturaIUGU = new FaturaIUGU();

					JSONObject obj = faturas.getJSONObject(i);

					String dueDateOriginal = obj.getString("due_date");					

					faturaIUGU.setId(obj.getString("id"));
					faturaIUGU.setDue_date_str(dueDateOriginal);
					faturaIUGU.setTotal(obj.getString("total"));
					faturaIUGU.setStatus(obj.getString("status"));    

					JSONArray variables = obj.getJSONArray("custom_variables");

					for (int j = 0; j < variables.length(); j++) {
						JSONObject objVariables = variables.getJSONObject(j);
						String idContrato = null;
						String idParcela = null;

						try {
							if (objVariables.getString("name").equals("idContrato")) {
								idContrato = objVariables.getString("value");

								if (idContrato != null) {
									// GET dados do sacado
									ContratoCobranca contratoCobranca = new ContratoCobranca();
									ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
									contratoCobranca = contratoCobrancaDao.findById(Long.valueOf(idContrato));

									faturaIUGU.setSacado(contratoCobranca.getPagador().getNome());
									faturaIUGU.setEmail(contratoCobranca.getPagador().getEmail());
								} 
							}

							if (objVariables.getString("name").equals("idParcela")) {
								idParcela = objVariables.getString("value");

								if (idParcela != null) {								
									// GET dados da parcela
									ContratoCobrancaDetalhes contratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
									ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

									try {
										contratoCobrancaDetalhes = contratoCobrancaDetalhesDao.findById(Long.valueOf(idParcela));

										if (contratoCobrancaDetalhes != null) {								
											if (contratoCobrancaDetalhes.getSecureURLIugu() != null) {
												faturaIUGU.setSecure_url(contratoCobrancaDetalhes.getSecureURLIugu());
											}

											if (contratoCobrancaDetalhes.getCedenteIugu() != null) {
												faturaIUGU.setCedente(contratoCobrancaDetalhes.getCedenteIugu().getNome());
											}
										}
									} catch (NumberFormatException e) {

									}
								}
							}
						} catch (NumberFormatException e) {							
						}
					}	

					this.faturasIUGU.add(faturaIUGU);
				}				

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Consultar Faturas Consolidadas IUGU: Consulta efetuada com sucesso!", ""));
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SOLICITA SAQUE DO RECEBEDOR
	 */
	public void cancelarFaturaIUGU(String idFatura, String iuguLiveApiToken) {
		/***
		 * Solicita saque para todos diferentes de GALLERIA
		 */
		try {			
			FacesContext context = FacesContext.getCurrentInstance();
			System.out.println("IUGU (1 - cancelaFaturaIUGU) - Cancela Fatura IUGU INICIO: Fatura " + idFatura + " / LiveToken " + iuguLiveApiToken );

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoices/" + idFatura + "/cancel?api_token=" + iuguLiveApiToken);

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("PUT");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			//myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			String erro = "";

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());
				System.out.println("IUGU (2 - cancelaFaturaIUGU) - Cancela Fatura IUGU ERRO: Fatura " + idFatura + " LiveToken " + iuguLiveApiToken );

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "IUGU - Efetua Saque Subcontas: Erro: " + erro, ""));
			} else {
				Thread.sleep(300);
				consultarFaturasSubConta();
			}

			System.out.println("IUGU (3 - cancelaFaturaIUGU) - Cancela Fatura IUGU FIM: Fatura " + idFatura + " LiveToken " + iuguLiveApiToken );

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SOLICITA SAQUE DO RECEBEDOR
	 */
	public void cancelarFaturaIUGUFaturaSimples(String idFatura, String iuguLiveApiToken) {
		/***
		 * Solicita saque para todos diferentes de GALLERIA
		 */

		if (iuguLiveApiToken.equals("")) {
			iuguLiveApiToken = "bd88479c57011124c25638b26572e453";
		} 

		try {			
			FacesContext context = FacesContext.getCurrentInstance();
			System.out.println("IUGU (1 - cancelaFaturaIUGU) - Cancela Fatura IUGU INICIO: Fatura " + idFatura + " / LiveToken " + iuguLiveApiToken );

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/invoices/" + idFatura + "/cancel?api_token=" + iuguLiveApiToken);

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("PUT");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			//myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			String erro = "";

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());
				System.out.println("IUGU (2 - cancelaFaturaIUGU) - Cancela Fatura IUGU ERRO: Fatura " + idFatura + " LiveToken " + iuguLiveApiToken );

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "IUGU - Efetua Saque Subcontas: Erro: " + erro, ""));
			} else {
				Thread.sleep(300);
				consultarFaturasSubContaFaturaSimples();
			}

			System.out.println("IUGU (3 - cancelaFaturaIUGU) - Cancela Fatura IUGU FIM: Fatura " + idFatura + " LiveToken " + iuguLiveApiToken );

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * CHAMADO PELO MENU DE CONSULTA TRANSFERENCIAS SUBCONTAS
	 * @return
	 */
	public String clearFieldsTransferenciasSubContaIugu() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();
		this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();

		this.contaMestre = true;

		return "/Atendimento/Cobranca/TransferenciasSubContasIugu.xhtml";
	}


	/**
	 * CONSULTA TRANSFERENCIAS SUBCONTAS
	 */
	public void consultarTransferenciasSubConta() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.iugu.com/v1/transfers?api_token=bd88479c57011124c25638b26572e453");

			if (!this.contaMestre) {
				if (this.selectedRecebedor.getId() <= 0) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consultar Transferências SubConta IUGU: Para efetuar a consulta é necessário selecionar uma SubConta!", ""));

					valid = false;
				} else {
					myURL = new URL("https://api.iugu.com/v1/transfers?api_token=" + this.selectedRecebedor.getIuguLiveApiToken());
				}				
			} else {
				myURL = new URL("https://api.iugu.com/v1/transfers?api_token=bd88479c57011124c25638b26572e453");
			}	

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consultar Transferências SubConta IUGU: Erro ao consultar transferências SubConta! (Erro: " + erro + ")!", ""));

				} else {							
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					JSONArray transferencias = myResponse.getJSONArray("sent");

					for (int i = 0; i < transferencias.length(); i++) {
						TransferenciasIUGU transferenciasIUGU = new TransferenciasIUGU();

						JSONObject obj = transferencias.getJSONObject(i);

						String dueDateOriginal = obj.getString("created_at").substring(8, 10) + "/" + obj.getString("created_at").substring(5, 7) + "/" + obj.getString("created_at").substring(0, 4) + 
								" " + obj.getString("created_at").substring(11, 19);

						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						Date dueDate = new java.sql.Date( ((java.util.Date)formatter.parse(dueDateOriginal)).getTime() );

						transferenciasIUGU.setId(obj.getString("id"));
						transferenciasIUGU.setCreated_at(dueDate);
						transferenciasIUGU.setTotal(obj.getString("amount_localized"));    

						JSONObject objReceiver = obj.getJSONObject("receiver");
						transferenciasIUGU.setRecebedor(objReceiver.getString("name"));	

						// busca a observação referente a transferência bancária.
						TransferenciasObservacoesIUGUDao transferenciasObservacoesIUGUDao = new TransferenciasObservacoesIUGUDao();
						List<TransferenciasObservacoesIUGU> transferenciasObservacoesIUGU = new ArrayList<TransferenciasObservacoesIUGU>();
						transferenciasObservacoesIUGU = transferenciasObservacoesIUGUDao.findByFilter("idTransferencia", obj.getString("id"));

						if (transferenciasObservacoesIUGU.size() > 0) {
							transferenciasIUGU.setObservacao(transferenciasObservacoesIUGU.get(0).getObservacao());
						}				

						this.transferenciasIUGU.add(transferenciasIUGU);
					}				

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consultar Transferências SubConta IUGU: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * CHAMADO PELO MENU DE CONSULTA TRANSFERENCIAS BANCARIAS
	 * @return
	 */
	public String clearFieldsTransferenciasBancaria() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();
		this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();
		this.contaMestre = true;

		this.files = listaArquivos();

		return "/Atendimento/Cobranca/TransferenciasBancarias.xhtml";
	}


	/***
	 * Lista ois arquivos contidos no diretório
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos() {
		//DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao(); 
		String pathContrato = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString() + "/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				//String nome = arquivo.getName();
				// String dt_ateracao = formatData.format(new Date(arquivo.lastModified()));
				lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
			}
		}
		return lista;
	}

	/**
	 * Método para fazer download de todos os arquivos do diretório do contrato
	 * @return
	 */
	public StreamedContent getDownloadAllFaturas() {
		try {
			// recupera path do contrato
			ParametrosDao pDao = new ParametrosDao(); 
			String pathContrato = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			// cria objetos para ZIP
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			// cria arquivo ZIP
			String nomeArquivo = "";
			if (this.faturasDownloadIUGU.size() > 0) { 
				nomeArquivo = "Boletos_" + this.faturasDownloadIUGU.get(0).getSacado() + ".zip";
			} else {
				nomeArquivo = "Boletos" + ".zip";	
			}

			fileWriter = new FileOutputStream(pathContrato + nomeArquivo);	

			zip = new ZipOutputStream(fileWriter);

			// Percorre arquivos selecionados e adiciona ao ZIP
			int contador = 0;

			for (FaturaIUGU faturas : this.faturasDownloadIUGU) {				
				contador = contador + 1 ;
				URL url = new URL(faturas.getSecure_url());
				File file = new File(pathContrato + faturas.getSacado() + "Boleto " + contador + ".pdf");

				FileUtils.copyURLToFile(url, file);

				addFileToZip("", file.getAbsolutePath(), zip);
			} 

			// Fecha o ZIP
			zip.flush();
			zip.close();

			// Recupera ZIP gerado para fazer download
			FileInputStream stream = new FileInputStream(pathContrato + nomeArquivo);
			downloadAllFaturas = new DefaultStreamedContent(stream, pathContrato, nomeArquivo);

		} catch (Exception e) {
			System.out.println(e);
		}

		return this.downloadAllFaturas;
	}

	/**
	 * Método para fazer download de todos os arquivos do diretório do contrato
	 * @return
	 */
	public StreamedContent getDownloadAllFiles() {
		try {
			// recupera path do contrato
			ParametrosDao pDao = new ParametrosDao(); 
			String pathContrato = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			// cria objetos para ZIP
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			// cria arquivo ZIP
			fileWriter = new FileOutputStream(pathContrato + "Recibo" + ".zip");
			zip = new ZipOutputStream(fileWriter);

			// Percorre arquivos selecionados e adiciona ao ZIP
			for (FileUploaded f : deletefiles) {
				addFileToZip("", f.getFile().getAbsolutePath(), zip);
			} 

			// Fecha o ZIP
			zip.flush();
			zip.close();

			// Recupera ZIP gerado para fazer download
			FileInputStream stream = new FileInputStream(pathContrato + "Recibo" + ".zip");
			downloadAllFiles = new DefaultStreamedContent(stream, pathContrato, "Recibo" + ".zip");

		} catch (Exception e) {
			System.out.println(e);
		}

		return this.downloadAllFiles;
	}

	/***
	 * Faz download de um único arquivo - linha do DataTable
	 * @return
	 */
	public StreamedContent getDownloadFile() {    
		if (this.selectedFile != null) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(this.selectedFile.getFile().getAbsolutePath());
				downloadFile = new DefaultStreamedContent(stream, this.selectedFile.getPath(), this.selectedFile.getFile().getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Cobrança - Download de Arquivos - Arquivo Não Encontrado");
			}			
		}	
		return this.downloadFile;
	}

	/***
	 * Exemplo de adicionar arquivos a um zip existente
	 * @param path
	 * @param srcFile
	 * @param zip
	 * @throws Exception
	 */
	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
			throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	/**
	 * Exemplo de adicionar uam pasta a um zip existente
	 * @param path
	 * @param srcFolder
	 * @param zip
	 * @throws Exception
	 */
	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
			throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}


	/**
	 * CONSULTA TRANSFERENCIAS BANCARIAS
	 */
	public void consultarTransferenciasBancarias() {
		try {			
			FacesContext context = FacesContext.getCurrentInstance();

			this.transferenciasIUGU = new ArrayList<TransferenciasIUGU>();

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.iugu.com/v1/withdraw_requests?api_token=bd88479c57011124c25638b26572e453");

			if (!this.contaMestre) {
				if (this.selectedRecebedor.getId() <= 0) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consultar Transferências Bancárias IUGU: Para efetuar a consulta é necessário selecionar uma SubConta!", ""));

					valid = false;
				} else {
					myURL = new URL("https://api.iugu.com/v1/withdraw_requests?api_token=" + this.selectedRecebedor.getIuguLiveApiToken());
				}				
			} else {
				myURL = new URL("https://api.iugu.com/v1/withdraw_requests?api_token=bd88479c57011124c25638b26572e453");
			}	

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consultar Transferências Bancárias: Erro ao consultar transferências bancárias! (Erro: " + erro + ")!", ""));

				} else {							
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					JSONArray transferencias = myResponse.getJSONArray("items");

					for (int i = 0; i < transferencias.length(); i++) {
						TransferenciasIUGU transferenciasIUGU = new TransferenciasIUGU();

						JSONObject obj = transferencias.getJSONObject(i);

						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

						String createdDateOriginal = obj.getString("created_at").substring(8, 10) + "/" + obj.getString("created_at").substring(5, 7) + "/" + obj.getString("created_at").substring(0, 4) + 
								" " + obj.getString("created_at").substring(11, 19) ;
						Date createdDate = new java.sql.Date( ((java.util.Date)formatter.parse(createdDateOriginal)).getTime() );

						String updatedDateOriginal = obj.getString("created_at").substring(8, 10) + "/" + obj.getString("created_at").substring(5, 7) + "/" + obj.getString("created_at").substring(0, 4) + 
								" " + obj.getString("created_at").substring(11, 19) ;
						Date updatedDate = new java.sql.Date( ((java.util.Date)formatter.parse(updatedDateOriginal)).getTime() );

						transferenciasIUGU.setId(obj.getString("id"));
						transferenciasIUGU.setCreated_at(createdDate);
						transferenciasIUGU.setUpdated_at(updatedDate);
						transferenciasIUGU.setStatus(obj.getString("status"));
						transferenciasIUGU.setTotal(obj.getString("amount"));    
						transferenciasIUGU.setRecebedor(obj.getString("account_name"));		

						// busca a observação referente a transferência bancária.
						TransferenciasObservacoesIUGUDao transferenciasObservacoesIUGUDao = new TransferenciasObservacoesIUGUDao();
						List<TransferenciasObservacoesIUGU> transferenciasObservacoesIUGU = new ArrayList<TransferenciasObservacoesIUGU>();
						transferenciasObservacoesIUGU = transferenciasObservacoesIUGUDao.findByFilter("idTransferencia", obj.getString("id"));

						if (transferenciasObservacoesIUGU.size() > 0) {
							transferenciasIUGU.setObservacao(transferenciasObservacoesIUGU.get(0).getObservacao());
						}						

						this.transferenciasIUGU.add(transferenciasIUGU);
					}				

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consultar Transferências Bancárias: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * PROCESSA MUDANÇA DE STATUS DE FATURA IUGU E FAZ TRANSFERENCIA
	 * MÉTODO CHAMADO PELO ENDPOINT - changeFaturaStatus
	 */
	public void processaMudancaStatusFaturaIugu(String idFaturaIugu, String idContaIugu) {

		int HTTP_COD_SUCESSO = 200;

		String liveToken = "";

		PagadorRecebedor recebedorCedente = new PagadorRecebedor();
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		recebedorCedente = pagadorRecebedorDao.getRecebedorByAccountIdIugu(idContaIugu);
		/// DEVE SER PROBLEMA DA CONTA NULA OU NÃO ENCONTRADA

		try {							
			URL myURL = new URL("https://api.iugu.com/v1/invoices/" + idFaturaIugu + "?api_token=" + recebedorCedente.getIuguLiveApiToken());

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			// LEITURA DOS DADOS EM STRING
			Thread.sleep(500);
			if (myURLConnection.getResponseCode() == HTTP_COD_SUCESSO) {	
				System.out.println("IUGU (3 - processaMudancaStatusFaturaIugu) - GET Fatura: Fatura " + idFaturaIugu + " / Recebedor " + recebedorCedente.getNome());
				/**
				 *  INICIO - Extrai as varias customizaveis da Fatura (IdContrato e IdPrcela)
				 */
				JSONObject myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());
				JSONArray customVariables = myResponse.getJSONArray("custom_variables");

				for(int i = 0; i < customVariables.length(); i++){
					JSONObject obj = customVariables.getJSONObject(i);

					if (obj.getString("name").equals("idContrato")) {
						this.idContrato = obj.getString("value");
					} else {
						this.idParcela = obj.getString("value");
					}	             
				}

				System.out.println("IUGU (4 - processaMudancaStatusFaturaIugu) - GET Fatura SUCESSO: Fatura " + idFaturaIugu + " / Recebedor " + recebedorCedente.getNome() + 
						" / ID Contrato " + this.idContrato + " / Parcela " + this.idParcela);

				/**
				 *  FIM - Extrai as varias customizaveis da Fatura (IdContrato e IdPrcela)
				 */

				/**
				 *  INICIO - PROCESSO DE SPLITTER IUGU
				 *  
				 *  1 - Busca o objeto Contrato 
				 *  2 - Busca o objeto Parcela
				 *  3 - Verifica se foi selecionada a opção de Split
				 *  4 - Verifica se todos recebedores possuem subconta
				 *  5 - Faz as transferências entre contas IUGU
				 *  6 - Atualiza flag fezTransfIugu na Parcela
				 */

				ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
				ContratoCobranca contrato = new ContratoCobranca();

				if (!idContrato.equals("")) {
					contrato = contratoDao.findById(Long.valueOf(idContrato));					

					if (contrato.getId() > 0) {
						System.out.println("IUGU (5 - processaMudancaStatusFaturaIugu) - GET Contrato SUCESSO: Fatura " + idFaturaIugu + " / Recebedor " + recebedorCedente.getNome() + 
								" / Contrato " + contrato.getNumeroContrato() + " / Parcela " + this.idParcela);

						boolean processaTransferencia = false;
						boolean parcelaFinal = false;

						for (int i = 0; i < contrato.getListContratoCobrancaDetalhes().size(); i++) {
							if (contrato.getListContratoCobrancaDetalhes().get(i).getId() == Long.valueOf(this.idParcela)) {
								System.out.println("IUGU (6 - processaMudancaStatusFaturaIugu) - GET Parcela SUCESSO: Fatura " + idFaturaIugu + " / Recebedor " + recebedorCedente.getNome() + 
										" / Contrato " + contrato.getNumeroContrato() + " / Parcela " + this.idParcela);
								/**
								 * VERIFICA SE SERÁ SPLITADO
								 */
								if (contrato.getListContratoCobrancaDetalhes().get(i).isGeraSplitterIugu()) {
									/**
									 * VERIFICA SE A PARCELA É A FINAL
									 */
									System.out.println("IUGU (7 - processaMudancaStatusFaturaIugu) - Split Fatura SIM ");

									if (contrato.isGeraParcelaFinal()) {
										if ((contrato.getListContratoCobrancaDetalhes().size() - 1) == i) {
											parcelaFinal = true;
										}								
									}

									processaTransferencia = true;
									break;
								}
							}
						}

						/**
						 * PROCESSA AS TRANSFERENCIAS
						 */
						if (processaTransferencia) {
							/**
							 * PROCESSA AS TRANSFERENCIAS RECEBEDOR A RECEBEDOR
							 */
							System.out.println("IUGU (8 - processaMudancaStatusFaturaIugu) - Processa Transferencia SIM  ");

							processaTransferenciaRecebedorIugu(contrato, this.idParcela, parcelaFinal, recebedorCedente);
							/**
							 * SETA A PARCELA COM A FLAG FEZ TRANSFERENCIA
							 */
							System.out.println("IUGU (9 - processaMudancaStatusFaturaIugu) - Grava Transferencia SIM  ");
							setFezTransferenciaIugu(this.idParcela);

							System.out.println("IUGU (10 - processaMudancaStatusFaturaIugu) - FIM do Processamento de Mudança de Status ");
						}
						/**
						 *  FIM - PROCESSO DE SPLITTER IUGU
						 */
					}
				} else {
					System.out.println("IUGU (processaMudancaStatusFaturaIugu) - GET Contrato ERRO: Contrato/Parcela nulo");
				}
			} else {
				System.out.println("IUGU (processaMudancaStatusFaturaIugu) - GET Fatura ERRO: Fatura " + idFaturaIugu + " / Recebedor " + recebedorCedente.getNome());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}

	/**
	 * A CADA RECEBEDOR FAZ A CHAMADA PARA EFETIVAR A TRANSFERENCIA
	 * @param contrato
	 */
	public void processaTransferenciaRecebedorIugu(ContratoCobranca contrato, String idParcela, boolean parcelaFinal, PagadorRecebedor cedente) {		

		String valorCedente = "";

		System.out.println("IUGU (1 - processaTransferenciaRecebedorIugu) - Processa Transferências INICIO: Contrato " + contrato.getNumeroContrato() + " / Cedente " + cedente.getNome());

		if (!parcelaFinal) {
			/**
			 * VERIFICA RECEBEDORES DO CONTRATO E PARA CADA UM FAZ A TRANSFERENCIA
			 */
			if (contrato.getRecebedor() != null) {
				if (contrato.getRecebedor().getIuguAccountId() != null) {
					if (contrato.getRecebedor().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor().getIuguAccountId(), 
								contrato.getVlrRecebedor().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor().getIuguAccountId(),
						//		contrato.getVlrRecebedor().toString().replace(".", "").replace(",", ""), contrato.getRecebedor().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor2() != null) {
				if (contrato.getRecebedor2().getIuguAccountId() != null) {
					if (contrato.getRecebedor2().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor2().getIuguAccountId(), 
								contrato.getVlrRecebedor2().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor2().getIuguAccountId(),
						//		contrato.getVlrRecebedor2().toString().replace(".", "").replace(",", ""), contrato.getRecebedor2().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor2().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor3() != null) {
				if (contrato.getRecebedor3().getIuguAccountId() != null) {
					if (contrato.getRecebedor3().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor3().getIuguAccountId(), 
								contrato.getVlrRecebedor3().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor3().getIuguAccountId(),
						//		contrato.getVlrRecebedor3().toString().replace(".", "").replace(",", ""), contrato.getRecebedor3().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor3().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor4() != null) {
				if (contrato.getRecebedor4().getIuguAccountId() != null) {
					if (contrato.getRecebedor4().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor4().getIuguAccountId(), 
								contrato.getVlrRecebedor4().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor4().getIuguAccountId(),
						//		contrato.getVlrRecebedor4().toString().replace(".", "").replace(",", ""), contrato.getRecebedor4().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor4().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor5() != null) {
				if (contrato.getRecebedor5().getIuguAccountId() != null) {
					if (contrato.getRecebedor5().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor5().getIuguAccountId(), 
								contrato.getVlrRecebedor5().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor5().getIuguAccountId(),
						//		contrato.getVlrRecebedor5().toString().replace(".", "").replace(",", ""), contrato.getRecebedor5().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor5().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor6() != null) {
				if (contrato.getRecebedor6().getIuguAccountId() != null) {
					if (contrato.getRecebedor6().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor6().getIuguAccountId(), 
								contrato.getVlrRecebedor6().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor6().getIuguAccountId(),
						//		contrato.getVlrRecebedor6().toString().replace(".", "").replace(",", ""), contrato.getRecebedor6().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor6().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor7() != null) {
				if (contrato.getRecebedor7().getIuguAccountId() != null) {
					if (contrato.getRecebedor7().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor7().getIuguAccountId(), 
								contrato.getVlrRecebedor7().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor7().getIuguAccountId(),
						//		contrato.getVlrRecebedor7().toString().replace(".", "").replace(",", ""), contrato.getRecebedor7().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor7().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor8() != null) {
				if (contrato.getRecebedor8().getIuguAccountId() != null) {
					if (contrato.getRecebedor8().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor8().getIuguAccountId(), 
								contrato.getVlrRecebedor8().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor8().getIuguAccountId(),
						//		contrato.getVlrRecebedor8().toString().replace(".", "").replace(",", ""), contrato.getRecebedor8().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor8().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor9() != null) {
				if (contrato.getRecebedor9().getIuguAccountId() != null) {
					if (contrato.getRecebedor9().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor9().getIuguAccountId(), 
								contrato.getVlrRecebedor9().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor9().getIuguAccountId(),
						//		contrato.getVlrRecebedor9().toString().replace(".", "").replace(",", ""), contrato.getRecebedor9().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor9().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedor10() != null) {
				if (contrato.getRecebedor10().getIuguAccountId() != null) {
					if (contrato.getRecebedor10().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedor10().getIuguAccountId(), 
								contrato.getVlrRecebedor10().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedor10().getIuguAccountId(),
						//		contrato.getVlrRecebedor10().toString().replace(".", "").replace(",", ""), contrato.getRecebedor10().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrRecebedor10().toString().replace(".", "").replace(",", "");
					}
				}				
			}
		} else {
			/**
			 * VERIFICA RECEBEDORES DA PARCELA FINAL DO CONTRATO E PARA CADA UM FAZ A TRANSFERENCIA
			 */
			if (contrato.getRecebedorParcelaFinal1() != null) {
				if (contrato.getRecebedorParcelaFinal1().getIuguAccountId() != null) {
					if (contrato.getRecebedorParcelaFinal1().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedorParcelaFinal1().getIuguAccountId(), 
								contrato.getVlrFinalRecebedor1().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//solicitaSaqueIugu(contrato.getRecebedorParcelaFinal1().getIuguAccountId(),
						//		contrato.getVlrFinalRecebedor1().toString().replace(".", "").replace(",", ""), contrato.getRecebedorParcelaFinal1().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrFinalRecebedor1().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedorParcelaFinal2() != null) {
				if (contrato.getRecebedorParcelaFinal2().getIuguAccountId() != null) {
					if (contrato.getRecebedorParcelaFinal2().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedorParcelaFinal2().getIuguAccountId(), 
								contrato.getVlrFinalRecebedor2().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//	solicitaSaqueIugu(contrato.getRecebedorParcelaFinal2().getIuguAccountId(),
						//		contrato.getVlrFinalRecebedor2().toString().replace(".", "").replace(",", ""), contrato.getRecebedorParcelaFinal2().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrFinalRecebedor2().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedorParcelaFinal3() != null) {
				if (contrato.getRecebedorParcelaFinal3().getIuguAccountId() != null) {
					if (contrato.getRecebedorParcelaFinal3().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedorParcelaFinal3().getIuguAccountId(), 
								contrato.getVlrFinalRecebedor3().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//	solicitaSaqueIugu(contrato.getRecebedorParcelaFinal3().getIuguAccountId(),
						//			contrato.getVlrFinalRecebedor3().toString().replace(".", "").replace(",", ""), contrato.getRecebedorParcelaFinal3().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrFinalRecebedor3().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedorParcelaFinal4() != null) {
				if (contrato.getRecebedorParcelaFinal4().getIuguAccountId() != null) {
					if (contrato.getRecebedorParcelaFinal4().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedorParcelaFinal4().getIuguAccountId(), 
								contrato.getVlrFinalRecebedor4().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//	solicitaSaqueIugu(contrato.getRecebedorParcelaFinal4().getIuguAccountId(),
						//			contrato.getVlrFinalRecebedor4().toString().replace(".", "").replace(",", ""), contrato.getRecebedorParcelaFinal4().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrFinalRecebedor4().toString().replace(".", "").replace(",", "");
					}
				}				
			}
			if (contrato.getRecebedorParcelaFinal5() != null) {
				if (contrato.getRecebedorParcelaFinal5().getIuguAccountId() != null) {
					if (contrato.getRecebedorParcelaFinal5().getIuguAccountId() != cedente.getIuguAccountId()) {
						efetuaTransferenciaIugu(contrato.getRecebedorParcelaFinal5().getIuguAccountId(), 
								contrato.getVlrFinalRecebedor5().toString().replace(".", "").replace(",", ""), 
								String.valueOf(contrato.getId()), 
								idParcela, cedente.getIuguLiveApiToken());

						//	solicitaSaqueIugu(contrato.getRecebedorParcelaFinal5().getIuguAccountId(),
						//			contrato.getVlrFinalRecebedor5().toString().replace(".", "").replace(",", ""), contrato.getRecebedorParcelaFinal5().getIuguLiveApiToken());
					} else {
						valorCedente = contrato.getVlrFinalRecebedor5().toString().replace(".", "").replace(",", "");
					}
				}				
			}
		}	

		System.out.println("IUGU (2 - processaTransferenciaRecebedorIugu) - Processa Transferências FIM: Contrato " + contrato.getNumeroContrato() + " / Cedente " + cedente.getNome());

		/***
		 * SOLICITA SAQUE DO CEDENTE
		 */
		System.out.println("IUGU (3 - processaTransferenciaRecebedorIugu) - Solicita Saque Cedente: Cedente " + cedente.getNome());

		System.out.println("IUGU (processaTransferenciaRecebedorIugu - PRINT VARIAVEIS) - Solicita Saque Cedente: getIuguAccountId " + cedente.getIuguAccountId() + " / valorCedente " + valorCedente + " / getIuguLiveApiToken() " + cedente.getIuguLiveApiToken());

		//	solicitaSaqueIugu(cedente.getIuguAccountId(), valorCedente, cedente.getIuguLiveApiToken());

		System.out.println("IUGU (4 - processaTransferenciaRecebedorIugu) - FIM do Processo de Transferência de Valores! ");
	}

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA TRANSFERENCIA DE VALORES SUBCONTAS IUGU
	 * 
	 * @return
	 */
	public String composeJSONTransfSubContaIugu(String idSubcontaIugu, String amountCents, String idContrato, String idParcela) {
		String json = "";
		String jsonCustomVariables = "";

		jsonCustomVariables = "{\"value\":\"" + idContrato + "\",\"name\":\"idContrato\"},"
				+ 			  "{\"value\":\"" + idParcela +   "\",\"name\":\"idParcela\"}";

		json = "{\"receiver_id\":\"" + idSubcontaIugu + "\",\"amount_cents\":" + amountCents + ",\"custom_variables\":[" + jsonCustomVariables + "]}";

		return json;
	}

	/**
	 * EFETUA TRANSFERENCIA ENTRE CONTAS
	 */
	public void efetuaTransferenciaIugu(String idSubcontaIugu, String amountCents, String idContrato, String idParcela, String iuguLiveApiToken) {
		try {		
			System.out.println("IUGU (1 - efetuaTransferenciaIugu) - Efetua Transferência Subcontas: Subconta " + idSubcontaIugu + " / Id Contrato " + idContrato + " / Id Parcela " + idParcela);
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.iugu.com/v1/transfers?api_token=" + iuguLiveApiToken );

			String dados = composeJSONTransfSubContaIugu(idSubcontaIugu, amountCents, idContrato, idParcela);			

			JSONObject jsonObj = new JSONObject(dados);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			JSONObject myResponse = null;

			/**
			 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
			 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
			 * https://api.iugu.com/v1/withdraw_requests/id"
			 */
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				erro = getErroIugu(myURLConnection.getErrorStream());
				System.out.println("IUGU (efetuaTransferenciaIugu) - Efetua Transferência Subcontas ERRO: Subconta " + idSubcontaIugu + " / Id Contrato " + idContrato + 
						" / Id Parcela " + idParcela + " / Erro " + erro);
			} else {				
				myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

				System.out.println("IUGU (2.1 - efetuaTransferenciaIugu) - Insere observação da Transferência Subcontas SUCESSO: Subconta " + idSubcontaIugu + " / Id Contrato " + idContrato + " / Id Parcela " + idParcela);

				TransferenciasObservacoesIUGUDao transferenciasObservacoesIUGUDao = new TransferenciasObservacoesIUGUDao();
				TransferenciasObservacoesIUGU transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
				transferenciasObservacoesIUGU.setIdTransferencia(myResponse.getString("id"));
				transferenciasObservacoesIUGU.setObservacao(observacao);

				transferenciasObservacoesIUGUDao.create(transferenciasObservacoesIUGU);

				System.out.println("IUGU (2.2 - efetuaTransferenciaIugu) - Efetua Transferência Subcontas SUCESSO: Subconta " + idSubcontaIugu + " / Id Contrato " + idContrato + " / Id Parcela " + idParcela);
			}

			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SOLICITA SAQUE DO RECEBEDOR
	 */
	public void solicitaSaqueIugu(String idSubcontaIugu, String amountCents, String iuguLiveApiToken, String observacao) {
		/***
		 * Solicita saque para todos diferentes de GALLERIA
		 */
		if (!idSubcontaIugu.equals("275B315A7C464B168A71C29B09FEB081")) {
			try {			
				FacesContext context = FacesContext.getCurrentInstance();
				System.out.println("IUGU (1 - solicitaSaqueIugu) - Efetua Saque Subcontas INICIO: Subconta " + idSubcontaIugu + " / Valor solicitado " + amountCents );

				int HTTP_COD_SUCESSO = 200;

				URL myURL = new URL("https://api.iugu.com/v1/accounts/" + idSubcontaIugu + "/request_withdraw?api_token=" + iuguLiveApiToken);			

				JSONObject jsonObj = new JSONObject("{\"amount\":" + amountCents + "}");
				byte[] postDataBytes = jsonObj.toString().getBytes();

				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("POST");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);
				myURLConnection.getOutputStream().write(postDataBytes);

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				String erro = "";
				JSONObject myResponse = null;

				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					erro = getErroIugu(myURLConnection.getErrorStream());
					System.out.println("IUGU (solicitaSaqueIugu) - Efetua Saque Subcontas FIM: Subconta " + idSubcontaIugu + " / Valor solicitado " + amountCents + " / Erro " + erro);

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "IUGU - Efetua Saque Subcontas: Erro: " + erro, ""));
				} else {				
					myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

					System.out.println("IUGU (2.1 - solicitaSaqueIugu) - Insere observação do Saque Subcontas SUCESSO: Subconta " + idSubcontaIugu);

					TransferenciasObservacoesIUGUDao transferenciasObservacoesIUGUDao = new TransferenciasObservacoesIUGUDao();
					TransferenciasObservacoesIUGU transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
					transferenciasObservacoesIUGU.setIdTransferencia(myResponse.getString("id"));
					transferenciasObservacoesIUGU.setObservacao(observacao);

					transferenciasObservacoesIUGUDao.create(transferenciasObservacoesIUGU);

					this.transferenciasObservacoesIUGU = transferenciasObservacoesIUGU;

					System.out.println("IUGU (2.2 - solicitaSaqueIugu) - Efetua Saque Subcontas SUCESSO: Subconta " + idSubcontaIugu);
				}

				// LEITURA DOS DADOS EM STRING
				Thread.sleep(500);

				System.out.println("IUGU (3 - solicitaSaqueIugu) - Efetua Saque Subcontas FIM: Subconta " + idSubcontaIugu);

				myURLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/****
	 * 
	 * PROCESSA TRANSFERENCIA ENTRE SUBCONTAS IUGU VIA TELA 
	 * 
	 * @return
	 */
	public void processaTransferenciaSubcontasTela() {
		FacesContext context = FacesContext.getCurrentInstance();

		this.senhaStorage = this.paramSenha;

		if (this.paramSenha == null || this.paramSenha.equals("")) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: Para efetuar a transferência é obrigatório informar a senha correta!", ""));
		} else {
			if (this.paramSenha.equals("821436")) {
				boolean valid = true;

				if (!this.contaMestre) {
					if (this.selectedRecebedor == null) {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: para efetuar a transferência é obrigatória a seleção das SubContas!", ""));

						valid = false;
					} else { 
						if (this.selectedRecebedor.getId() <= 0) {
							context.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: para efetuar a transferência é obrigatória a seleção das SubContas!", ""));

							valid = false;
						}
					}
				} 	

				if (!this.contaMestreAux) {
					if (this.selectedRecebedorAux == null) {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: para efetuar a transferência é obrigatória a seleção da SubConta Destino!", ""));

						valid = false;
					} else {
						if (this.selectedRecebedorAux.getId() <= 0) {
							context.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: para efetuar a transferência é obrigatória a seleção da SubConta Destino!", ""));

							valid = false;
						}
					}
				}

				if (this.contaMestre && this.contaMestreAux) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: a conta Origem e Destino não podem ser Galleria Finanças!", ""));

					valid = false;
				}

				if (valid) {
					if (!this.contaMestre) {
						if (this.contaMestreAux) {
							//efetua transferencia da conta selecionada para outra a conta mestre
							efetuaTransferenciaIugu("7D4D20A4F1184FEB91126DFEAD86AED8", this.valorItem.toString().replace(".", "").replace(",", ""), "transf_siscoat", "transf_siscoat", this.selectedRecebedor.getIuguLiveApiToken());
						} else {
							// efetua transferencia da conta selecionada para outra conta selecionada
							efetuaTransferenciaIugu(this.selectedRecebedorAux.getIuguAccountId(), this.valorItem.toString().replace(".", "").replace(",", ""), "transf_siscoat", "transf_siscoat", this.selectedRecebedor.getIuguLiveApiToken());
						}
					} else {
						// efetua transferencia da conta mestre para a conta destino
						efetuaTransferenciaIugu(this.selectedRecebedorAux.getIuguAccountId(), this.valorItem.toString().replace(".", "").replace(",", ""), "transf_siscoat", "transf_siscoat", "bd88479c57011124c25638b26572e453");
					}

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "IUGU - Transferência entre SubContas: Transferência efetuada com sucesso!", ""));
				}
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "IUGU - Transferência entre SubContas: Para efetuar a transferência é obrigatório informar a senha correta!", ""));

			}
		}
	}

	public void clearGeraRecibo() {
		this.reciboPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		this.transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
	}

	public void geraReciboSaque() {
		/*
		this.transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
		this.transferenciasObservacoesIUGU.setId(1);
		this.transferenciasObservacoesIUGU.setIdTransferencia("jdsfhdsfhjskfhjhslafdshf");
		this.transferenciasObservacoesIUGU.setObservacao("asdklfhjksdhfjd dsjfhjhdsfjashgdfj ");

		this.valorItem = new BigDecimal("30000.00");
		 */
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;
		OutputStream os = null;

		try {
			/*
			 *  Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao(); 
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			String favorecido = "";
			if (!this.contaMestre) {				
				favorecido = this.selectedRecebedor.getNome();

			} else {				
				favorecido = "Galleria Finanças";				
			}	

			//todo
			favorecido = "Galleria Finanças";	

			doc = new Document(PageSize.A4.rotate(), 10, 80, 10, 80);
			this.nomePDF = "Recibo - " + favorecido + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);  	

			// Associa a stream de saída ao 
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();     			
			/*
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.8f, 0.8f});
			table.setWidthPercentage(50.0f); 
			
			BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/iugu.jpg"));
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(buff, "jpg", bos);
	        Image img = Image.getInstance(bos.toByteArray());
	        
			img.setAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(img);
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);			
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Iugu Gestão de Pagamento", header));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Comprovante de Pagamento", tituloBranco));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(new BaseColor(92, 156, 204));
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("ID da Transação: " + this.transferenciasObservacoesIUGU.getIdTransferencia(), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(2f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Data: " + sdfDataRelComHoras.format(date.getTime()), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(2f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor R$ " + df.format(this.valorItem), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Dados do Favorecido", tituloBranco));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(new BaseColor(92, 156, 204));
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			if (!this.contaMestre) {				
				cell1 = new PdfPCell(new Phrase("Banco: " + this.selectedRecebedor.getBanco(), titulo));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(2f);
				cell1.setPaddingBottom(2f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Agência: " + this.selectedRecebedor.getAgencia(), titulo));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(2f);
				cell1.setPaddingBottom(2f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Conta: " + this.selectedRecebedor.getConta(), titulo));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(2f);
				cell1.setPaddingBottom(2f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Titular: " + this.selectedRecebedor.getNome(), titulo));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(2f);
				cell1.setPaddingBottom(2f);
				cell1.setColspan(2);
				table.addCell(cell1);

			} else {				
				cell1 = new PdfPCell(new Phrase("Titular: Galleria Finanças", titulo));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(2f);
				cell1.setPaddingBottom(2f);
				cell1.setColspan(2);
				table.addCell(cell1);
			}	

			cell1 = new PdfPCell(new Phrase("Obs.: Disponível no próximo dia útil após às 13:00.", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(20f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(2);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Recibo de Pagamento: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Recibo de Pagamento: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.reciboPDFGerado = true;

			if (doc != null) {
				//fechamento do documento
				doc.close();
			}
			if (os != null) {
				//fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/****
	 * 
	 * PROCESSA SAQUE SUBCONTA IUGU VIA TELA 
	 * 
	 * @return
	 */

	public void processaSaqueSubcontaTela() {
		FacesContext context = FacesContext.getCurrentInstance();

		this.senhaStorage = this.paramSenha;

		clearGeraRecibo();

		if (this.paramSenha == null || this.paramSenha.equals("")) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "IUGU - Saque SubConta: Para efetuar o saque é obrigatório informar a senha correta!", ""));
		} else {

			if (this.paramSenha.equals("821436")) {

				boolean valid = true;

				if (!this.contaMestre) {
					if (this.selectedRecebedor.getId() <= 0) {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "IUGU - Saque SubConta: para efetuar o saque é obrigatória a seleção da SubConta!", ""));

						valid = false;
					} 
				}

				if (valid) {
					if (!this.contaMestre) {
						//cpqd
						solicitaSaqueIugu(this.selectedRecebedor.getIuguAccountId(),  this.valorItem.toString(), 
								this.selectedRecebedor.getIuguLiveApiToken(), this.observacao);
					} else {
						solicitaSaqueIugu("7D4D20A4F1184FEB91126DFEAD86AED8",  this.valorItem.toString(), 
								"bd88479c57011124c25638b26572e453", this.observacao);
					}

					gravaSaqueObservacaoParcelaAutomatica(this.selectedRecebedor.getIuguLiveApiToken(), this.parcelaObservacao);
					//TODO contas a receber
					geraReciboSaque();

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "IUGU - Saque SubConta: Saque efetuado com sucesso! (ID da Transação: " + this.transferenciasObservacoesIUGU.getIdTransferencia() + ")", ""));
				}
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "IUGU - Saque SubConta: Para efetuar o saque é obrigatório informar a senha correta!", ""));

			}
		}
	}	

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA GERAÇÃO DA COBRANÇA SIMPLES
	 * 
	 * @return
	 */
	public String composeJSONParcelaContrato(PagadorRecebedor cliente) {
		String json = "";

		String documento = "";
		if (cliente.getCpf() == null) {
			documento = cliente.getCnpj().replace(".", "").replace("-", "").replace("/", "");
		} else {
			documento = cliente.getCpf().replace(".", "").replace("-", "");
		}

		json = "{\"email\":\"" + cliente.getEmail() + "\",\"name\":\"" + cliente.getNome() + "\",\"cpf_cnpj\":\"" + documento + "\"}";

		return json;
	}

	/****
	 * 
	 * GERAÇÃO DA COBRANÇA SIMPLES PELA PARCELA
	 * 
	 * @return
	 */
	public void geraBoletoDaParcela(ContratoCobranca contrato, String parcela, long idParcela, Date vencimento, BigDecimal valor, boolean splitBoletoIugu, long cedente) {

		// get dados cedente
		PagadorRecebedorDao cedenteDao = new PagadorRecebedorDao();
		PagadorRecebedor cedenteObj = cedenteDao.findById(cedente);

		// carrega pagador
		this.idRecebedor = contrato.getPagador().getId();
		this.nomeRecebedor = contrato.getPagador().getNome();
		this.selectedRecebedor = contrato.getPagador();

		//criaClienteIugu(contrato.getPagador()); 

		// FAZER PARA RECEBDEDOR FINAL TB

		this.dataVencimento = vencimento;

		this.numeroContrato = contrato.getNumeroContrato();
		this.parcela = parcela;

		// Armazena na Fatura para dar match no retorno do gatilho do splitter
		this.idContrato = String.valueOf(contrato.getId());
		this.idParcela = String.valueOf(idParcela);

		this.valorItem = valor;

		this.splitBoletoIugu = splitBoletoIugu;

		/***
		 * SE SPLITTER
		 *  --VERIFICA SE TODOS RECEBEDORES POSSUEM SUBCONTAS
		 *  ----- SE NÃO, GERA AS SUBCONTAS E VALIDA AS MESMAS
		 */
		String retorno = "";

		// carrega pagador
		this.idRecebedor = contrato.getPagador().getId();
		this.nomeRecebedor = contrato.getPagador().getNome();
		this.selectedRecebedor = contrato.getPagador();


		if (this.splitBoletoIugu) {

			retorno = validaSubcontaRecebedorIugu(contrato);

			if (retorno.equals("")) {
				geraCobrancaSimples(cedenteObj.getIuguLiveApiToken(), cedenteObj.getId(), "code");
			}
		} else {
			/**
			 * CHAMADA DA GERAÇÃO DE FATURA
			 */
			geraCobrancaSimples("760bffed8d4907e7a3fe0f32f260a4ec", 14, "code");
		}



	}

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA GERAÇÃO DA COBRANÇA SIMPLES
	 * 
	 * @return
	 */
	public String composeJSONCobrancaSimples(String origemChamada) {
		String jsonFavorecido = "";
		String jsonItens = "";
		String jsonPayer = "";
		String jsonCustomVariables = "";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.setTime(this.dataVencimento);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
		dataHoje.set(Calendar.MINUTE, 0);  
		dataHoje.set(Calendar.SECOND, 0);  
		dataHoje.set(Calendar.MILLISECOND, 0);

		String mes = String.valueOf(dataHoje.get(Calendar.MONTH) + 1);
		if (mes.length() == 1) {
			mes = "0" + mes;
		}

		String dia = String.valueOf(dataHoje.get(Calendar.DAY_OF_MONTH));
		if (dia.length() == 1) {
			dia = "0" + dia;
		}		

		String documento = "";
		if (this.selectedRecebedor.getCpf() == null) {
			documento = this.selectedRecebedor.getCnpj().replace(".", "").replace("-", "").replace("/", "");
		} else {
			documento = this.selectedRecebedor.getCpf().replace(".", "").replace("-", "");
		}

		String descricaoCompleta = "";

		if (origemChamada.equals("tela")) {
			if (this.descricaoItem.equals("SERVIÇO DE COBRANÇA ")) {
				descricaoCompleta = "SERVIÇO DE COBRANÇA";

				if (!this.numeroContrato.equals("")) {
					descricaoCompleta = descricaoCompleta + " - CONTRATO: " + this.numeroContrato;
				}

				if (!this.parcela.equals("")) {
					descricaoCompleta = descricaoCompleta + " / PARCELA: " + this.parcela;
				}
			} else {
				descricaoCompleta = this.descricaoItem;
			}
		} else {
			descricaoCompleta = "SERVIÇO DE COBRANÇA";

			if (!this.numeroContrato.equals("")) {
				descricaoCompleta = descricaoCompleta + " - CONTRATO: " + this.numeroContrato;
			}

			if (!this.parcela.equals("")) {
				descricaoCompleta = descricaoCompleta + " / PARCELA: " + this.parcela;
			}
		}

		jsonItens = "{\"description\":\"" + descricaoCompleta + "\",\"quantity\":1,\"price_cents\":" + this.valorItem.toString().replace(".", "").replace(",", "") + "}";

		String bairro = "";

		if (this.selectedRecebedor.getBairro() != null) {
			if (this.selectedRecebedor.getBairro().length() > 0) {
				bairro = this.selectedRecebedor.getBairro();
			} else {
				bairro = "Bairro";
			}
		} else {
			bairro = "Bairro";
		}
		
		String endereco = ""; 
		if (this.selectedRecebedor.getNumero() != null) {
			if (!this.selectedRecebedor.getNumero().equals("")) {
				endereco = this.selectedRecebedor.getEndereco() + ", " + this.selectedRecebedor.getNumero();
			}
		} else {
			endereco = this.selectedRecebedor.getEndereco();
		}

	
		            		
		            		
		jsonPayer = "\"payer\":{\"cpf_cnpj\":\"" + documento + "\",\"name\":\"" + this.selectedRecebedor.getNome() + "\",\"email\":\"" + this.selectedRecebedor.getEmail()
		+ "\",\"address\":{\"zip_code\":\"" + this.selectedRecebedor.getCep().replace(".", "").replace("-", "") + "\",\"street\":\"" + endereco 
		+ "\",\"district\":\"" + bairro
		+ "\",\"number\":\"" + 000 + "\"}}";


		jsonCustomVariables = "{\"value\":\"" + this.idContrato + "\",\"name\":\"idContrato\"},"
				+ 			  "{\"value\":\"" + this.idParcela +   "\",\"name\":\"idParcela\"}";

		jsonFavorecido = "{\\\"email\\\":\\\"" + this.selectedRecebedor.getEmail() + "\\\",\\\"due_date\\\":\\\"" + 
				dataHoje.get(Calendar.YEAR) + mes + dia + "\\\", \\\"items\\\":[" + jsonItens + "]," + jsonPayer + ",\"payable_with\":[\"all\"]}";

		jsonFavorecido = "{\"email\":\"" + this.selectedRecebedor.getEmail() + "\", \"due_date\":\"" + 
				dataHoje.get(Calendar.YEAR) + mes + dia + "\",\"items\":[" + jsonItens + "],\"custom_variables\":[" + jsonCustomVariables + "]," +  jsonPayer + ",\"payable_with\":[\"all\"]}";

		return jsonFavorecido;
	}

	/****
	 * 
	 * GERAÇÃO DA COBRANÇA SIMPLES
	 * 
	 * @return
	 */
	public void geraCobrancaSimples(String idLiveTokenIugu, long idCedente, String origemChamada) {
		int HTTP_COD_SUCESSO = 200;
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.selectedRecebedor != null) {
			boolean dadosValidos = true;

			// validações
			if (this.selectedRecebedor.getCpf() == null && this.selectedRecebedor.getCnpj() == null) {
				dadosValidos = false;

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (CPF ou CNPJ inválidos) !", ""));				
			}

			if (this.selectedRecebedor.getEndereco().equals("") || this.selectedRecebedor.getCep().equals("")) {
				dadosValidos = false;

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (Endereço ou CEP inválidos) !", ""));	
			}


			if (dadosValidos) {
				try {							
					URL myURL = new URL("https://api.iugu.com/v1/invoices?api_token=" + idLiveTokenIugu);

					String dados = composeJSONCobrancaSimples(origemChamada);
					//JSONObject jsonObj = new JSONObject("{\"email\":\"webnowbr@gmail.com\",\"due_date\":\"20181212\",\"items\":[{\"description\":\"Cobrança\",\"quantity\":1,\"price_cents\":1486}],\"payer\":{\"cpf_cnpj\":\"31255904852\",\"name\":\"HERMES VIEIRA JUNIOR\",\"address\":{\"zip_code\":\"13073035\",\"street\":\"ENDEREÇO COMPLETO\",\"number\":\"1111\"}}}");
					JSONObject jsonObj = new JSONObject(dados);
					byte[] postDataBytes = jsonObj.toString().getBytes();

					HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
					myURLConnection.setUseCaches(false);
					myURLConnection.setRequestMethod("POST");
					myURLConnection.setRequestProperty("Accept", "application/json");
					myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
					myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
					myURLConnection.setDoOutput(true);
					myURLConnection.getOutputStream().write(postDataBytes);

					//myURLConnection.setDoInput(true);

					// LEITURA DOS DADOS EM STRING
					Thread.sleep(500);
					if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Erro na geração da Cobrança! " + myURLConnection.getResponseCode() + ": " + getErroIugu(myURLConnection.getErrorStream()), ""));
						//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode() + ": " + myResponse.getString("error"));				
					} else {
						// Seta o ID da fatura na Parcela do Siscoat
						JSONObject myResponse = null;

						myResponse = getJsonSucessoIugu(myURLConnection.getInputStream());

						this.urlFatura = myResponse.getString("secure_url");

						if (!this.idParcela.equals("")) {

							if ( myResponse != null ) {
								String idFaturaIugu =  myResponse.getString("id");
								String secureURL =  myResponse.getString("secure_url");

								setIdFaturaParcelaIugu(this.idParcela, idFaturaIugu, this.splitBoletoIugu, secureURL, idCedente);
							}							
						}

						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Cobrança gerada com sucesso!", ""));
					}

					myURLConnection.disconnect();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Favor selecionar um favorecido!", ""));
		}
	}
	
	public String composeJSONCobrancaContrato() {
		String jsonFavorecido = "";
		String jsonItens = "";
		String jsonPayer = "";
		String jsonCustomVariables = "";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.setTime(this.dataVencimento);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
		dataHoje.set(Calendar.MINUTE, 0);  
		dataHoje.set(Calendar.SECOND, 0);  
		dataHoje.set(Calendar.MILLISECOND, 0);

		String mes = String.valueOf(dataHoje.get(Calendar.MONTH) + 1);
		if (mes.length() == 1) {
			mes = "0" + mes;
		}

		String dia = String.valueOf(dataHoje.get(Calendar.DAY_OF_MONTH));
		if (dia.length() == 1) {
			dia = "0" + dia;
		}		

		String documento = "";
		if (this.selectedRecebedor.getCpf() == null) {
			documento = this.selectedRecebedor.getCnpj().replace(".", "").replace("-", "").replace("/", "");
		} else {
			documento = this.selectedRecebedor.getCpf().replace(".", "").replace("-", "");
		}

		String descricaoCompleta = this.contratoCobranca.getNumeroContrato() + " - Laudo + Parecer Jurídico";

		jsonItens = "{\"description\":\"" + descricaoCompleta + "\",\"quantity\":1,\"price_cents\":" + this.valorItem.toString().replace(".", "").replace(",", "") + "}";

		String bairro = "";

		if (this.selectedRecebedor.getBairro() != null) {
			if (this.selectedRecebedor.getBairro().length() > 0) {
				bairro = this.selectedRecebedor.getBairro();
			} else {
				bairro = "Bairro";
			}
		} else {
			bairro = "Bairro";
		}
		
		String endereco = ""; 
		if (this.selectedRecebedor.getNumero() != null) {
			if (!this.selectedRecebedor.getNumero().equals("")) {
				endereco = this.selectedRecebedor.getEndereco() + ", " + this.selectedRecebedor.getNumero();
			}
		} else {
			endereco = this.selectedRecebedor.getEndereco();
		}       		
		            		
		jsonPayer = "\"payer\":{\"cpf_cnpj\":\"" + documento + "\",\"name\":\"" + this.selectedRecebedor.getNome() + "\",\"email\":\"" + this.selectedRecebedor.getEmail()
		+ "\",\"address\":{\"zip_code\":\"" + this.selectedRecebedor.getCep().replace(".", "").replace("-", "") + "\",\"street\":\"" + endereco 
		+ "\",\"district\":\"" + bairro
		+ "\",\"number\":\"" + 000 + "\"}}";


		jsonCustomVariables = "{\"value\":\"" + this.idContrato + "\",\"name\":\"idContrato\"},"
				+ 			  "{\"value\":\"" + this.idParcela +   "\",\"name\":\"idParcela\"}";

		jsonFavorecido = "{\\\"email\\\":\\\"" + this.selectedRecebedor.getEmail() + "\\\",\\\"due_date\\\":\\\"" + 
				dataHoje.get(Calendar.YEAR) + mes + dia + "\\\", \\\"items\\\":[" + jsonItens + "]," + jsonPayer + ",\"payable_with\":[\"all\"]}";

		jsonFavorecido = "{\"email\":\"" + this.selectedRecebedor.getEmail() + "\", \"due_date\":\"" + 
				dataHoje.get(Calendar.YEAR) + mes + dia + "\",\"items\":[" + jsonItens + "],\"custom_variables\":[" + jsonCustomVariables + "]," +  jsonPayer + ",\"payable_with\":[\"all\"]}";

		return jsonFavorecido;
	}
	
	public void geraCobrancaSimplesContrato(String idLiveTokenIugu, long idCedente) {
		int HTTP_COD_SUCESSO = 200;
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.selectedRecebedor != null) {
			boolean dadosValidos = true;

			// validações
			if (this.selectedRecebedor.getCpf() == null && this.selectedRecebedor.getCnpj() == null) {
				dadosValidos = false;

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (CPF ou CNPJ inválidos) !", ""));				
			}

			if (this.selectedRecebedor.getEndereco() == null) {
				this.selectedRecebedor.setEndereco("Endereço");
			}
			
			if (this.selectedRecebedor.getCep() == null) {
				this.selectedRecebedor.setCep("13091-611");
			}
			
			this.selectedRecebedor.setEmail("joaopedro.galleriafinancas@gmail.com");
			
			if (this.selectedRecebedor.getEndereco().equals("") || this.selectedRecebedor.getCep().equals("")) {
				dadosValidos = false;
	
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (Endereço ou CEP inválidos) !", ""));	
			}

			if (dadosValidos) {
				try {							
					URL myURL = new URL("https://api.iugu.com/v1/invoices?api_token=" + idLiveTokenIugu);

					String dados = composeJSONCobrancaContrato();
					//JSONObject jsonObj = new JSONObject("{\"email\":\"webnowbr@gmail.com\",\"due_date\":\"20181212\",\"items\":[{\"description\":\"Cobrança\",\"quantity\":1,\"price_cents\":1486}],\"payer\":{\"cpf_cnpj\":\"31255904852\",\"name\":\"HERMES VIEIRA JUNIOR\",\"address\":{\"zip_code\":\"13073035\",\"street\":\"ENDEREÇO COMPLETO\",\"number\":\"1111\"}}}");
					JSONObject jsonObj = new JSONObject(dados);
					byte[] postDataBytes = jsonObj.toString().getBytes();

					HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
					myURLConnection.setUseCaches(false);
					myURLConnection.setRequestMethod("POST");
					myURLConnection.setRequestProperty("Accept", "application/json");
					myURLConnection.setRequestProperty("Content-Type", "application/json");
					myURLConnection.setDoOutput(true);
					myURLConnection.getOutputStream().write(postDataBytes); 
					/*
					
					OkHttpClient client = new OkHttpClient();

					MediaType mediaType = MediaType.parse("application/json");
					RequestBody body = RequestBody.create(mediaType, "{\"ensure_workday_due_date\":false}");
					Request request = new Request.Builder()
					  .url("https://api.iugu.com/v1/invoices")
					  .post(body)
					  .addHeader("Accept", "application/json")
					  .addHeader("Content-Type", "application/json")
					  .build();

					Response response = client.newCall(request).execute();
*/
					//myURLConnection.setDoInput(true);

					// LEITURA DOS DADOS EM STRING
					Thread.sleep(500);
					if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Erro na geração da Cobrança! " + myURLConnection.getResponseCode() + ": " + getErroIugu(myURLConnection.getErrorStream()), ""));
						//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode() + ": " + myResponse.getString("error"));				
					} else {
						// Seta o ID da fatura na Parcela do Siscoat
						JSONObject myResponse = null;

						myResponse = getJsonSucessoIugu(myURLConnection.getInputStream()); 

						this.urlFatura = myResponse.getString("secure_url");

						if (!this.idParcela.equals("")) {

							if ( myResponse != null ) {
								String idFaturaIugu =  myResponse.getString("id");
								String secureURL =  myResponse.getString("secure_url");

								setContratoFaturaIugu(secureURL);
							}							
						}

						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Cobrança gerada com sucesso!", ""));
					}

					myURLConnection.disconnect();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Favor selecionar um favorecido!", ""));
		}
	}

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA GERAÇÃO DA SUBCONTA
	 * 
	 * @return
	 */
	public String composeJSONSubConta(PagadorRecebedor recebedor) {
		String json = "";

		json = "{\"name\":\"" + recebedor.getNome() + "\"}";

		return json;
	}

	/****
	 * 
	 * CRIA SUBCONTA IUGU
	 * 
	 * @param recebedor
	 */	
	public String criaSubContaIugu(PagadorRecebedor recebedor) {
		FacesContext context = FacesContext.getCurrentInstance();

		int HTTP_COD_SUCESSO = 200;

		boolean dadosValidos = true;

		String retorno = "";

		// validações
		if (recebedor.getCpfCC() == null && recebedor.getCnpjCC() == null) {
			dadosValidos = false;

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (CPF ou CNPJ inválidos) !", ""));				
		}

		if (recebedor.getEndereco().equals("") || recebedor.getCep().equals("")) {
			dadosValidos = false;

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (Endereço ou CEP inválidos) !", ""));	
		}

		String dados = composeJSONSubConta(recebedor);

		if (dadosValidos) {
			try {							
				URL myURL = new URL("https://api.iugu.com/v1/marketplace/create_account?api_token=bd88479c57011124c25638b26572e453");

				JSONObject jsonObj = new JSONObject(dados);
				byte[] postDataBytes = jsonObj.toString().getBytes();

				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("POST");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Content-Type", "application/json");
				myURLConnection.setDoOutput(true);
				myURLConnection.getOutputStream().write(postDataBytes);
				// LEITURA DOS DADOS EM STRING
				Thread.sleep(500);
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "SubConta IUGU: Erro na criação da SubConta ! HTTP error code : " + myURLConnection.getResponseCode() + ": " + getErroSimplesIugu(myURLConnection.getErrorStream()), ""));

					retorno = getErroIugu(myURLConnection.getErrorStream());
					//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode());		
				} else {			
					BufferedReader in = new BufferedReader(
							new InputStreamReader(myURLConnection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					//READ JSON response and print
					JSONObject myResponse = new JSONObject(response.toString());

					//SETA A SUBCONTA DO RECEBEDOR
					String account_id =  myResponse.getString("account_id");
					String name =  myResponse.getString("name");
					String live_api_token =  myResponse.getString("live_api_token");
					String test_api_token =  myResponse.getString("test_api_token");
					String user_token =  myResponse.getString("user_token");

					setSubContaIugu(recebedor, account_id, name, live_api_token, test_api_token, user_token);

					configuraSubConta(live_api_token, 5);

					retorno = "SubConta criada com sucesso!";
				}
				myURLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return retorno;
	}


	/****
	 * 
	 * CHAMADO PELO METODO criaSubContaIugu PARA CONFIGURAR A CONTA
	 * 
	 * @return
	 */		
	public void configuraSubConta(String live_api_token, int juros) {

		int HTTP_COD_SUCESSO = 200;

		String dados = "{\"auto_withdraw\":false,\"fines\":true,\"per_day_interest\":true,\"late_payment_fine\":" + juros + "}";

		try {							
			URL myURL = new URL("https://api.iugu.com/v1/accounts/configuration?api_token=" + live_api_token);

			JSONObject jsonObj = new JSONObject(dados);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);
			// LEITURA DOS DADOS EM STRING
			Thread.sleep(500);
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				System.out.println("IUGU (configuraSubConta) - Erro ao configurar a subconta (Erro: " + getErroIugu(myURLConnection.getErrorStream()) + ")");
				//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode());		
			} else {					
				System.out.println("IUGU (configuraSubConta) - Sucesso ao configurar a subconta!");
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String clearAlteraTaxaJurosSubContas() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		// get somente PagadorRecebedor com subcontas
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();
		this.contaMestre = true;
		
		this.taxaJuros = 5;

		return "/Cadastros/Cobranca/AlteraTaxaJurosSubContasIugu.xhtml";				
	}	

	public void alteraTaxaJurosSubContas() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.selectedRecebedor != null) {
			configuraSubConta(this.selectedRecebedor.getIuguLiveApiToken(), this.taxaJuros);
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Taxa de Juros Alterada com Sucesso para " + this.selectedRecebedor.getNome() + "! ( Nova Taxa: " +  this.taxaJuros + " )", ""));
		} else {
			List<PagadorRecebedor> subContas = new ArrayList<PagadorRecebedor>();
			PagadorRecebedorDao prDao = new PagadorRecebedorDao();
			subContas = prDao.getSubContasIugu();

			for (PagadorRecebedor subConta : subContas) {
				if (subConta.getIuguLiveApiToken() != null) {
					if (!subConta.getIuguLiveApiToken().equals("")) {
						configuraSubConta(subConta.getIuguLiveApiToken(), this.taxaJuros);
					}		
				}
			}
			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Taxa de Juros Alterada com Sucesso! ( Nova Taxa: " +  this.taxaJuros + " )", ""));
		}		
		
		this.selectedRecebedor = null;
	}

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA EDICAO DO DOMICILIO BANCARIO
	 * 
	 * @return
	 */
	public String composeJSONDomicilioBancarioSubConta(PagadorRecebedor recebedor) {
		String json = "";
		String banco = "";
		/*
		if (recebedor.getBanco().contains("Itaú")) {
			banco = "Itaú";
		}

		if (recebedor.getBanco().contains("Bradesco")) {
			banco = "Bradesco";
		}

		if (recebedor.getBanco().contains("Caixa Econômica")) {
			banco = "Caixa Econômica";
		}

		if (recebedor.getBanco().contains("Banco do Brasil")) {
			banco = "Banco do Brasil";
		}

		if (recebedor.getBanco().contains("Santander")) {
			banco = "Santander";
		}

		if (recebedor.getBanco().contains("Sicredi")) {
			banco = "Sicredi";
		}

		if (recebedor.getBanco().contains("BRB")) {
			banco = "BRB";
		}
		 */

		if (recebedor.getBanco().contains("Itaú")) {
			banco = "041";
		}

		if (recebedor.getBanco().contains("Bradesco")) {
			banco = "237";
		}

		if (recebedor.getBanco().contains("Caixa Econômica")) {
			banco = "104";
		}

		if (recebedor.getBanco().contains("Banco do Brasil")) {
			banco = "001";
		}

		if (recebedor.getBanco().contains("Santander")) {
			banco = "033";
		}

		if (recebedor.getBanco().contains("Sicredi")) {
			banco = "748";
		}

		if (recebedor.getBanco().contains("BRB")) {
			banco = "070";
		}

		json = "{\"data\":"
				+ "{\"agency\":\"" + recebedor.getAgencia() + "\", "
				+ " \"account\":\"" + recebedor.getConta() + "\", "
				+ " \"account_type\":\"cc\", "			
				+ " \"bank\":\"" + banco + "\"}"
				+ "}";

		return json;
	}



	/**
	 * CHAMADO PELO MENU DE ALTERA DOMICILIO BANCARIO DA SUBCONTA
	 * @return
	 */
	public String clearFieldsAlteraDomicilioBancarioSubConta() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.getSubContasIugu();	
		clearRecebedor();

		return "/Atendimento/Cobranca/AlteraDomicilioBancarioSubConta.xhtml";
	}

	/****
	 * 
	 * CHAMADO PELO METODO ALTERA DOMICILIO BANCARIO DA SUBCONTA
	 * 
	 * @return
	 */		
	public void alteraDomicilioBancarioSubConta(PagadorRecebedor subconta) {

		int HTTP_COD_SUCESSO = 200;

		String dados = composeJSONDomicilioBancarioSubConta(subconta);

		try {							
			URL myURL = new URL("https://api.iugu.com/v1/bank_verification?api_token=" + subconta.getIuguLiveApiToken());

			JSONObject jsonObj = new JSONObject(dados);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);
			// LEITURA DOS DADOS EM STRING
			Thread.sleep(500);
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				System.out.println("IUGU (alteraDomicilioBancarioSubConta) - Erro ao alterar o domicilio bancário da SubConta (Erro: " + getErroIugu(myURLConnection.getErrorStream()) + ")");
			} else {					
				System.out.println("IUGU (alteraDomicilioBancarioSubConta) - Sucesso ao alterar o domicilio bancário da SubConta!");
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA VALIDAÇÃO DA SUBCONTA IUGU
	 * 
	 * @return
	 */
	public String composeJSONValidaSubConta(PagadorRecebedor recebedor) {
		String json = "";

		String documento = "";
		String person_type = "";	

		if (recebedor.getCpfCC() == null) {
			documento = recebedor.getCnpjCC().replace(".", "").replace("-", "").replace("/", "");
			person_type = "Pessoa Jurídica";
		} else {
			documento = recebedor.getCpfCC().replace(".", "").replace("-", "");
			person_type = "Pessoa Física";
		}		

		String banco = "";
		/**
		 * Estes não estão no Siscoat:
		 * 		'Banrisul'
		 *		'Sicoob'
		 *		'Inter'
		 */

		if (recebedor.getBanco().contains("Itaú")) {
			banco = "Itaú";
		}

		if (recebedor.getBanco().contains("Bradesco")) {
			banco = "Bradesco";
		}

		if (recebedor.getBanco().contains("Caixa Econômica")) {
			banco = "Caixa Econômica";
		}

		if (recebedor.getBanco().contains("Banco do Brasil")) {
			banco = "Banco do Brasil";
		}

		if (recebedor.getBanco().contains("Santander")) {
			banco = "Santander";
		}

		if (recebedor.getBanco().contains("Sicredi")) {
			banco = "Sicredi";
		}

		if (recebedor.getBanco().contains("BRB")) {
			banco = "BRB";
		}

		if (recebedor.getBanco().contains("077")) {
			banco = "Inter";
		}

		if (recebedor.getBanco().contains("756")) {
			banco = "Sicoob";
		}

		/** TODO verificar como fica a questão do Nome e do CPF do responsável */
		
		String endereco = ""; 
		if (recebedor.getNumero() != null) {
			if (!recebedor.getNumero().equals("")) {
				endereco = recebedor.getEndereco() + ", " + recebedor.getNumero();
			}
		} else {
			endereco = recebedor.getEndereco();
		}
		
		json = "{\"data\":"
				+ "{\"price_range\":\"1000000\", "
				+ " \"physical_products\":\"false\", "
				+ " \"business_type\":\"SERVIÇO DE COBRANÇA\", "
				+ " \"person_type\":\"" + person_type + "\", "
				+ " \"automatic_transfer\":\"false\", "
				+ " \"address\":\"" + endereco + "\", "
				+ " \"cep\":\"" + recebedor.getCep().replace(".", "").replace("-", "") + "\", "
				+ " \"city\":\"" + recebedor.getCidade() + "\", "
				+ " \"state\":\"" + recebedor.getEstado() + "\", "
				+ " \"telephone\":\"" + recebedor.getTelCelular().replace("-", "").replace("(", "").replace(")", "") + "\", "
				+ " \"resp_name\":\"Tulio Eduardo Guilger\", "
				+ " \"resp_cpf\":\"33905627884\", "
				+ " \"bank\":\"" + banco + "\", "
				+ " \"bank_ag\":\"" + recebedor.getAgencia() + "\", "
				+ " \"account_type\":\"Corrente\", "
				+ " \"bank_cc\":\"" + recebedor.getConta() + "\", ";

		if (person_type.equals("Pessoa Física")) {
			json = json + " \"cpf\":\"" + documento + "\", ";
		} else {
			json = json + " \"cnpj\":\"" + documento + "\", ";
			json = json + " \"company_name\":\"" + recebedor.getNome() + "\", ";
		}

		json = json + " \"name\":\"" + recebedor.getNome() + "\", "
				+ "}"
				+ "}";

		/* Gerado pelos testes no Iugu 
			var data = JSON.stringify({
				  "data": {
				    "price_range": "1000000",
				    "physical_products": "false",
				    "business_type": "SERVIÇO DE COBRANÇA",
				    "person_type": "Pessoa Física",
				    "automatic_transfer": "true",
				    "address": "AVENIDA IMPERATRIZ LEOPOLDINA",
				    "cep": "13073035",
				    "city": "CAMPINAS",
				    "state": "SÃO PAULO",
				    "telephone": "19991653911",
				    "resp_name": "HERMES VIEIRA JUNIOR",
				    "resp_cpf": "31255904852",
				    "bank": "Itaú",
				    "bank_ag": "2964",
				    "account_type": "Corrente",
				    "bank_cc": "30614-2",
				    "cpf": "31255904852",
				    "name": "HERMES VIEIRA JUNIOR"
				  }
				});
		 */		

		return json;
	}

	/****
	 * 
	 * VERIFICA SUBCONTA IUGU
	 * 
	 * @param recebedor
	 */	
	public void verificaSubContaIugu(PagadorRecebedor recebedor) {
		FacesContext context = FacesContext.getCurrentInstance();

		int HTTP_COD_SUCESSO = 200;

		boolean dadosValidos = true;

		// validações
		if (recebedor.getCpfCC() == null && recebedor.getCnpjCC() == null) {
			dadosValidos = false;

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorre;-tos (CPF ou CNPJ inválidos) !", ""));				
		}

		if (recebedor.getEndereco().equals("") || recebedor.getCep().equals("")) {
			dadosValidos = false;

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (Endereço ou CEP inválidos) !", ""));	
		}

		String dados = composeJSONValidaSubConta(recebedor);

		if (dadosValidos) {
			try {							
				URL myURL = new URL("https://api.iugu.com/v1/accounts/" + recebedor.getIuguAccountId() + "/request_verification?api_token=" + recebedor.getIuguUserToken());

				JSONObject jsonObj = new JSONObject(dados);
				byte[] postDataBytes = jsonObj.toString().getBytes();

				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("POST");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Content-Type", "application/json");
				myURLConnection.setDoOutput(true);
				myURLConnection.getOutputStream().write(postDataBytes);
				// LEITURA DOS DADOS EM STRING
				Thread.sleep(500);
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Erro na validação da SubConta Iugu! HTTP error code : "+ + myURLConnection.getResponseCode() + ": " + getErroIugu(myURLConnection.getErrorStream()), ""));

					//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode());		
				} else {			
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Validação da conta Iugu feita com sucesso!", ""));
				}
				myURLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

	public void criarSubContaIUGUTela() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.selectedRecebedor.getId() <= 0) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "SubConta IUGU: Para efetuar a criação e verificação de uma nova SubConta é necessário seleciona um cadastro!", ""));

		} else {
			if (this.selectedRecebedor.getIuguAccountId() != null)  {
				verificaSubContaIugu(this.selectedRecebedor);
			} else {				
				processaSubContaIugu(this.selectedRecebedor);
				consultarSubContasIUGU();
			}

			// Configura conta com 5% de juros no atraso
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			PagadorRecebedor pTemp = pDao.findById(this.selectedRecebedor.getId());
			configuraSubConta(pTemp.getIuguLiveApiToken(), 5);

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "SubConta IUGU: Criação da sub-conta e verificação solicitada com sucesso! (SubConta: " + pTemp.getNome() + " / Id SubConta: " + pTemp.getIuguAccountId() + ")", ""));
		}			
	}

	/**
	 * PROCESSA SUBCONTA IUGU
	 *   - criaSubContaIugu(recebedor);
		 - verificaSubContaIugu(recebedor);
	 */
	public String processaSubContaIugu(PagadorRecebedor recebedor) {
		FacesContext context = FacesContext.getCurrentInstance();

		String retorno = "";
		retorno = criaSubContaIugu(recebedor);
		verificaSubContaIugu(recebedor);

		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, retorno, ""));

		return retorno;
	}

	/**
	 * SE SPLITTER
	 *   -- VALIDA SE TODOS OS RECEBEDORES DO CONTRATO POSSUEM SUBCONTA
	 *   ---- SENÃO CRIA SUBCONTAS
	 * @param contrato
	 */
	public String validaSubcontaRecebedorIugu(ContratoCobranca contrato) {
		/**
		 * VERIFICA RECEBEDORES DO CONTRATO
		 */

		String retorno = "";

		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor());
			}				
		}
		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor2());
			}				
		}
		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor3());
			}				
		}
		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor4());
			}				
		}
		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor5());
			}				
		}
		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor6());
			}				
		}
		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor7());
			}				
		}
		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor8());
			}				
		}
		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor9());
			}				
		}
		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedor10());
			}				
		}

		/**
		 * VERIFICA RECEBEDORES DA PARCELA FINAL DO CONTRATO
		 */
		if (contrato.getRecebedorParcelaFinal1() != null) {
			if (contrato.getRecebedorParcelaFinal1().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedorParcelaFinal1());
			}				
		}
		if (contrato.getRecebedorParcelaFinal2() != null) {
			if (contrato.getRecebedorParcelaFinal2().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedorParcelaFinal2());
			}				
		}
		if (contrato.getRecebedorParcelaFinal3() != null) {
			if (contrato.getRecebedorParcelaFinal3().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedorParcelaFinal3());
			}				
		}
		if (contrato.getRecebedorParcelaFinal4() != null) {
			if (contrato.getRecebedorParcelaFinal4().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedorParcelaFinal4());
			}				
		}
		if (contrato.getRecebedorParcelaFinal5() != null) {
			if (contrato.getRecebedorParcelaFinal5().getIuguUserToken() == null) {
				retorno = processaSubContaIugu(contrato.getRecebedorParcelaFinal5());
			}				
		}

		return retorno;
	}

	/****
	 * ATUALIZAÇÃO DA PARCELA DO ID DA FATURA GERADA
	 * 
	 */
	public void setIdFaturaParcelaIugu(String idParcelaSiscoat, String idFaturaIugu, boolean splitBoletoIugu, String secureURL, long idCedente) {
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

		ContratoCobrancaDetalhes contratoCobrancaDetalhes = contratoCobrancaDetalhesDao.findById(Long.valueOf(idParcelaSiscoat));

		contratoCobrancaDetalhes.setIdFaturaIugu(idFaturaIugu);
		contratoCobrancaDetalhes.setGeraSplitterIugu(splitBoletoIugu);
		contratoCobrancaDetalhes.setSecureURLIugu(secureURL);

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		if (idCedente != 0) {
			contratoCobrancaDetalhes.setCedenteIugu(pagadorRecebedorDao.findById(idCedente));
		} else {
			contratoCobrancaDetalhes.setCedenteIugu(pagadorRecebedorDao.findById(Long.valueOf(184)));
		}		

		contratoCobrancaDetalhesDao.merge(contratoCobrancaDetalhes);
	}
	
	public void setContratoFaturaIugu(String secureURL) {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();

		this.contratoCobranca.setIuguLaudoPaju(secureURL);

		cDao.merge(this.contratoCobranca);
	}

	/****
	 * ATUALIZAÇÃO DO PAGADOR COM SET DO ID CLIENTE IUGU
	 * 
	 */
	public void setIdClienteIugu(PagadorRecebedor cliente, String idIugu) {
		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		cliente.setIdIugu(idIugu);
		prDao.merge(cliente);
	}

	/****
	 * ATUALIZAÇÃO DA PARCELA COM FLAG FEZ TRANSFERENCIA
	 * 
	 */
	public void setFezTransferenciaIugu(String idParcela) {
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		ContratoCobrancaDetalhes contratoCobrancaDetalhes = contratoCobrancaDetalhesDao.findById(Long.valueOf(idParcela));

		contratoCobrancaDetalhes.setFezTransferenciaIugu(true);

		contratoCobrancaDetalhesDao.merge(contratoCobrancaDetalhes);
		System.out.println("IUGU (1 - setFezTransferenciaIugu) - Gravou com Sucesso  / Id Parcela: " + idParcela);
	}

	/****
	 * ATUALIZAÇÃO DO RECEBEDOR COM SET DOS DADOS DA SUBCONTA
	 * 
	 */
	public void setSubContaIugu(PagadorRecebedor cliente, String iuguAccountId
			, String iuguNameAccount
			, String iuguLiveApiToken
			, String iuguTestApiToken
			, String iuguUserToken) {

		PagadorRecebedorDao prDao = new PagadorRecebedorDao();

		cliente.setIuguAccountId(iuguAccountId);
		cliente.setIuguNameAccount(iuguNameAccount);
		cliente.setIuguLiveApiToken(iuguLiveApiToken);
		cliente.setIuguTestApiToken(iuguTestApiToken);
		cliente.setIuguUserToken(iuguUserToken);

		prDao.merge(cliente);

		this.selectedRecebedor = cliente;
	}

	/***
	 * 
	 * PARSE DO ERRO PROCESSAMENTO SIMPLES RETORNADO PELO IUGU
	 * 
	 * @param inputStream
	 * @return
	 */
	public String getErroSimplesIugu(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());
			String erros = "";

			erros = myResponse.getString("errors");

			return erros;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}


	/***
	 * 
	 * PARSE DO ERRO RETORNADO PELO IUGU
	 * 
	 * @param inputStream
	 * @return
	 */
	public String getErroIugu(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());
			String erros = "";

			List<String> listKeyErrors = new ArrayList<String>(myResponse.getJSONObject("errors").keySet());

			for (String errorsKey : listKeyErrors) { 
				if (erros.equals("")) {
					erros = "[" + errorsKey + "]" + myResponse.getJSONObject("errors").getJSONArray(errorsKey).toString();	
				} else {
					erros = erros + " / [" + errorsKey + "]" + myResponse.getJSONObject("errors").getJSONArray(errorsKey).toString();	
				}
			}

			return erros;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}


	/***
	 * 
	 * PARSE DO RETORNO SUCESSO IUGU
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJsonSucessoIugu(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	/****
	 * 
	 * CRIA USUÁRIO NO IUGU
	 * 
	 * @return
	 */
	public void criaClienteIugu(PagadorRecebedor cliente) {
		FacesContext context = FacesContext.getCurrentInstance();

		int HTTP_COD_SUCESSO = 200;

		boolean dadosValidos = true;

		// validações
		if (cliente.getCpfCC() == null && cliente.getCnpjCC() == null) {
			dadosValidos = false;

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (CPF ou CNPJ inválidos) !", ""));				
		}

		if (cliente.getEndereco().equals("") || cliente.getCep().equals("")) {
			dadosValidos = false;

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Dados do cliente incorretos (Endereço ou CEP inválidos) !", ""));	
		}

		String dados = composeJSONParcelaContrato(cliente);

		if (dadosValidos) {
			try {							
				URL myURL = new URL("https://api.iugu.com/v1/customers?api_token=bd88479c57011124c25638b26572e453");

				JSONObject jsonObj = new JSONObject(dados);
				byte[] postDataBytes = jsonObj.toString().getBytes();

				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("POST");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Content-Type", "application/json");
				myURLConnection.setDoOutput(true);
				myURLConnection.getOutputStream().write(postDataBytes);
				// LEITURA DOS DADOS EM STRING
				Thread.sleep(500);
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Cobrança Iugu: Erro na geração da Cobrança! HTTP error code : "+ + myURLConnection.getResponseCode() + ": " + getErroIugu(myURLConnection.getErrorStream()), ""));

					//throw new RuntimeException("HTTP error code : "+ myURLConnection.getResponseCode());		
				} else {			
					BufferedReader in = new BufferedReader(
							new InputStreamReader(myURLConnection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					//PRINT in String
					//System.out.println(response.toString());

					//READ JSON response and print
					JSONObject myResponse = new JSONObject(response.toString());
					//System.out.println("result after Reading JSON Response");

					// GET DO ID DO CLIENTE
					//System.out.println("origin- "+myResponse.getString("id"));

					//SETA O ID IUGU NO PAGADOR 
					String idIugu =  myResponse.getString("id");
					setIdClienteIugu(cliente, idIugu);

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Cobrança Iugu: Cobrança gerada com sucesso!", ""));
				}
				myURLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public final void addObservacaoDetalhes() {
		ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes = new ContratoCobrancaDetalhesObservacoes();
		contratoCobrancaDetalhesObservacoes.setData(this.dataObservacao);
		contratoCobrancaDetalhesObservacoes.setObservacao(this.observacao);

		if (this.loginBean != null) {
			contratoCobrancaDetalhesObservacoes.setUsuario(loginBean.getUsername());
		}

		this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().add(contratoCobrancaDetalhesObservacoes);

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		contratoCobrancaDetalhesDao.merge(this.contratoCobrancaDetalhes);	
	}

	public final void addObservacaoDetalhesAutomatica(String observacaoComposta) {
		ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes = new ContratoCobrancaDetalhesObservacoes();
		contratoCobrancaDetalhesObservacoes.setData(this.dataObservacao);
		contratoCobrancaDetalhesObservacoes.setObservacao(observacaoComposta);

		if (this.loginBean != null) {
			contratoCobrancaDetalhesObservacoes.setUsuario(loginBean.getUsername());
		}

		this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().add(contratoCobrancaDetalhesObservacoes);

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		contratoCobrancaDetalhesDao.merge(this.contratoCobrancaDetalhes);	
	}

	public void excluirObservacaoDetalhes() {
		List<ContratoCobrancaDetalhesObservacoes> listObservacoes = new ArrayList<ContratoCobrancaDetalhesObservacoes>();
		for (int i = 0; i < this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().size(); i++) {
			if (this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().get(i).getId() != this.contratoCobrancaDetalhesObservacoes.getId()) {
				listObservacoes.add(this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().get(i));
			}
		}

		this.contratoCobrancaDetalhes.setListContratoCobrancaDetalhesObservacoes(listObservacoes);

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		contratoCobrancaDetalhesDao.merge(this.contratoCobrancaDetalhes);			
		contratoCobrancaDetalhesDao.limpaObservacoesNaoUsadas();
	}

	/****
	 * FUNÇÃO QUE GRAVA OS DETALHES DO SAQUE NA OBSERVAÇÃO DA PARCELA
	 */
	public void gravaSaqueObservacaoParcela(String livetoken, long idParcela) {	
		if (idParcela > 0) {
			TransferenciasIUGU tempTransferenciasIUGU = new TransferenciasIUGU();
			consultarTransferenciasSubContaAposOperacao(livetoken);

			if (this.transferenciasIUGU.size() > 0) {				
				tempTransferenciasIUGU = this.transferenciasIUGU.get(0);

				ContratoCobrancaDetalhesDao ccdDao = new ContratoCobrancaDetalhesDao(); 
				this.contratoCobrancaDetalhes = ccdDao.findById(idParcela);

				if (this.contratoCobrancaDetalhes != null) {	
					if (this.contratoCobrancaDetalhes.getId() > 0) {
						this.dataObservacao = gerarDataHoje();					
						this.observacao = "Sacado: " + tempTransferenciasIUGU.getRecebedor() + 
								"| Dt. Solicitação: " + tempTransferenciasIUGU.getCreated_at() + 
								" | Valor R$ " + tempTransferenciasIUGU.getTotal() + 
								" | Observação: " + tempTransferenciasIUGU.getObservacao();

						addObservacaoDetalhes();
					}
				}

				this.transferenciasIUGU.clear();
			}				
		}	
	}

	/****
	 * FUNÇÃO QUE GRAVA OS DETALHES DO SAQUE NA OBSERVAÇÃO DA PARCELA
	 */
	public void gravaSaqueObservacaoParcelaAutomatica(String livetoken, long idParcela) {	
		if (idParcela > 0) {
			ContratoCobrancaDetalhesDao ccdDao = new ContratoCobrancaDetalhesDao(); 
			this.contratoCobrancaDetalhes = ccdDao.findById(idParcela);

			if (this.contratoCobrancaDetalhes != null) {	
				if (this.contratoCobrancaDetalhes.getId() > 0) {
					String observacao="";
					this.dataObservacao = gerarDataHoje();					
					observacao = "Sacado: " + this.selectedRecebedor.getNome() + 
							"| Dt. Solicitação: " + this.dataObservacao + 
							" | Valor R$ " + this.valorItem + 
							" | Observação: " + this.observacao;

					addObservacaoDetalhesAutomatica(observacao);
				}
			}			
		}	
	}

	/**
	 * @return the listRecebedores
	 */
	public List<PagadorRecebedor> getListRecebedores() {
		return listRecebedores;
	}

	/**
	 * @param listRecebedores the listRecebedores to set
	 */
	public void setListRecebedores(List<PagadorRecebedor> listRecebedores) {
		this.listRecebedores = listRecebedores;
	}

	/**
	 * @return the nomeRecebedor
	 */
	public String getNomeRecebedor() {
		return nomeRecebedor;
	}

	/**
	 * @param nomeRecebedor the nomeRecebedor to set
	 */
	public void setNomeRecebedor(String nomeRecebedor) {
		this.nomeRecebedor = nomeRecebedor;
	}

	/**
	 * @return the idRecebedor
	 */
	public long getIdRecebedor() {
		return idRecebedor;
	}

	/**
	 * @param idRecebedor the idRecebedor to set
	 */
	public void setIdRecebedor(long idRecebedor) {
		this.idRecebedor = idRecebedor;
	}

	/**
	 * @return the selectedRecebedor
	 */
	public PagadorRecebedor getSelectedRecebedor() {
		return selectedRecebedor;
	}

	/**
	 * @param selectedRecebedor the selectedRecebedor to set
	 */
	public void setSelectedRecebedor(PagadorRecebedor selectedRecebedor) {
		this.selectedRecebedor = selectedRecebedor;
	}

	/**
	 * @return the descricaoItem
	 */
	public String getDescricaoItem() {
		return descricaoItem;
	}

	/**
	 * @param descricaoItem the descricaoItem to set
	 */
	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	/**
	 * @return the valorItem
	 */
	public BigDecimal getValorItem() {
		return valorItem;
	}

	/**
	 * @param valorItem the valorItem to set
	 */
	public void setValorItem(BigDecimal valorItem) {
		this.valorItem = valorItem;
	}

	/**
	 * @return the numeroContrato
	 */
	public String getNumeroContrato() {
		return numeroContrato;
	}

	/**
	 * @param numeroContrato the numeroContrato to set
	 */
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	/**
	 * @return the parcela
	 */
	public String getParcela() {
		return parcela;
	}

	/**
	 * @param parcela the parcela to set
	 */
	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

	/**
	 * @return the dataVencimento
	 */
	public Date getDataVencimento() {
		return dataVencimento;
	}

	/**
	 * @param dataVencimento the dataVencimento to set
	 */
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	/**
	 * @return the contratoCobrancaDetalhes
	 */
	public ContratoCobrancaDetalhes getContratoCobrancaDetalhes() {
		return contratoCobrancaDetalhes;
	}

	/**
	 * @param contratoCobrancaDetalhes the contratoCobrancaDetalhes to set
	 */
	public void setContratoCobrancaDetalhes(ContratoCobrancaDetalhes contratoCobrancaDetalhes) {
		this.contratoCobrancaDetalhes = contratoCobrancaDetalhes;
	}

	/**
	 * @return the idContrato
	 */
	public String getIdContrato() {
		return idContrato;
	}

	/**
	 * @param idContrato the idContrato to set
	 */
	public void setIdContrato(String idContrato) {
		this.idContrato = idContrato;
	}

	/**
	 * @return the idParcela
	 */
	public String getIdParcela() {
		return idParcela;
	}

	/**
	 * @param idParcela the idParcela to set
	 */
	public void setIdParcela(String idParcela) {
		this.idParcela = idParcela;
	}

	/**
	 * @return the saldoSubContaLiberado
	 */
	public String getSaldoSubContaLiberado() {
		return saldoSubContaLiberado;
	}

	/**
	 * @param saldoSubContaLiberado the saldoSubContaLiberado to set
	 */
	public void setSaldoSubContaLiberado(String saldoSubContaLiberado) {
		this.saldoSubContaLiberado = saldoSubContaLiberado;
	}

	/**
	 * @return the saldoSubContaALiberar
	 */
	public String getSaldoSubContaALiberar() {
		return saldoSubContaALiberar;
	}

	/**
	 * @param saldoSubContaALiberar the saldoSubContaALiberar to set
	 */
	public void setSaldoSubContaALiberar(String saldoSubContaALiberar) {
		this.saldoSubContaALiberar = saldoSubContaALiberar;
	}

	/**
	 * @return the faturasIUGU
	 */
	public List<FaturaIUGU> getFaturasIUGU() {
		return faturasIUGU;
	}

	/**
	 * @param faturasIUGU the faturasIUGU to set
	 */
	public void setFaturasIUGU(List<FaturaIUGU> faturasIUGU) {
		this.faturasIUGU = faturasIUGU;
	}

	/**
	 * @return the transferenciasIUGU
	 */
	public List<TransferenciasIUGU> getTransferenciasIUGU() {
		return transferenciasIUGU;
	}

	/**
	 * @param transferenciasIUGU the transferenciasIUGU to set
	 */
	public void setTransferenciasIUGU(List<TransferenciasIUGU> transferenciasIUGU) {
		this.transferenciasIUGU = transferenciasIUGU;
	}

	/**
	 * @return the listRecebedoresAux
	 */
	public List<PagadorRecebedor> getListRecebedoresAux() {
		return listRecebedoresAux;
	}

	/**
	 * @param listRecebedoresAux the listRecebedoresAux to set
	 */
	public void setListRecebedoresAux(List<PagadorRecebedor> listRecebedoresAux) {
		this.listRecebedoresAux = listRecebedoresAux;
	}

	/**
	 * @return the nomeRecebedorAux
	 */
	public String getNomeRecebedorAux() {
		return nomeRecebedorAux;
	}

	/**
	 * @param nomeRecebedorAux the nomeRecebedorAux to set
	 */
	public void setNomeRecebedorAux(String nomeRecebedorAux) {
		this.nomeRecebedorAux = nomeRecebedorAux;
	}

	/**
	 * @return the idRecebedorAux
	 */
	public long getIdRecebedorAux() {
		return idRecebedorAux;
	}

	/**
	 * @param idRecebedorAux the idRecebedorAux to set
	 */
	public void setIdRecebedorAux(long idRecebedorAux) {
		this.idRecebedorAux = idRecebedorAux;
	}

	/**
	 * @return the selectedRecebedorAux
	 */
	public PagadorRecebedor getSelectedRecebedorAux() {
		return selectedRecebedorAux;
	}

	/**
	 * @param selectedRecebedorAux the selectedRecebedorAux to set
	 */
	public void setSelectedRecebedorAux(PagadorRecebedor selectedRecebedorAux) {
		this.selectedRecebedorAux = selectedRecebedorAux;
	}

	/**
	 * @return the contaMestre
	 */
	public boolean isContaMestre() {
		return contaMestre;
	}

	/**
	 * @param contaMestre the contaMestre to set
	 */
	public void setContaMestre(boolean contaMestre) {
		this.contaMestre = contaMestre;
	}

	/**
	 * @return the operacaoTransferencia
	 */
	public boolean isOperacaoTransferencia() {
		return operacaoTransferencia;
	}

	/**
	 * @param operacaoTransferencia the operacaoTransferencia to set
	 */
	public void setOperacaoTransferencia(boolean operacaoTransferencia) {
		this.operacaoTransferencia = operacaoTransferencia;
	}


	/**
	 * @return the subContasIUGU
	 */
	public List<SubContaIUGU> getSubContasIUGU() {
		return subContasIUGU;
	}


	/**
	 * @param subContasIUGU the subContasIUGU to set
	 */
	public void setSubContasIUGU(List<SubContaIUGU> subContasIUGU) {
		this.subContasIUGU = subContasIUGU;
	}


	public boolean isContaMestreAux() {
		return contaMestreAux;
	}


	public void setContaMestreAux(boolean contaMestreAux) {
		this.contaMestreAux = contaMestreAux;
	}


	/**
	 * @return the urlFatura
	 */
	public String getUrlFatura() {
		return urlFatura;
	}


	/**
	 * @param urlFatura the urlFatura to set
	 */
	public void setUrlFatura(String urlFatura) {
		this.urlFatura = urlFatura;
	}


	/**
	 * @return the relDataContratoInicio
	 */
	public Date getRelDataContratoInicio() {
		return relDataContratoInicio;
	}


	/**
	 * @param relDataContratoInicio the relDataContratoInicio to set
	 */
	public void setRelDataContratoInicio(Date relDataContratoInicio) {
		this.relDataContratoInicio = relDataContratoInicio;
	}


	/**
	 * @return the relDataContratoFim
	 */
	public Date getRelDataContratoFim() {
		return relDataContratoFim;
	}


	/**
	 * @param relDataContratoFim the relDataContratoFim to set
	 */
	public void setRelDataContratoFim(Date relDataContratoFim) {
		this.relDataContratoFim = relDataContratoFim;
	}


	/**
	 * @return the statusFatura
	 */
	public String getStatusFatura() {
		return statusFatura;
	}


	/**
	 * @param statusFatura the statusFatura to set
	 */
	public void setStatusFatura(String statusFatura) {
		this.statusFatura = statusFatura;
	}


	public List<PagadorRecebedor> getListCedentes() {
		return listCedentes;
	}


	public void setListCedentes(List<PagadorRecebedor> listCedentes) {
		this.listCedentes = listCedentes;
	}


	public String getNomeCedente() {
		return nomeCedente;
	}


	public void setNomeCedente(String nomeCedente) {
		this.nomeCedente = nomeCedente;
	}


	public long getIdCedente() {
		return idCedente;
	}


	public void setIdCedente(long idCedente) {
		this.idCedente = idCedente;
	}


	public PagadorRecebedor getSelectedCedente() {
		return selectedCedente;
	}


	public void setSelectedCedente(PagadorRecebedor selectedCedente) {
		this.selectedCedente = selectedCedente;
	}

	/**
	 * @return the paramAno
	 */
	public String getParamAno() {
		return paramAno;
	}

	/**
	 * @param paramAno the paramAno to set
	 */
	public void setParamAno(String paramAno) {
		this.paramAno = paramAno;
	}

	/**
	 * @return the paramMes
	 */
	public String getParamMes() {
		return paramMes;
	}

	/**
	 * @param paramMes the paramMes to set
	 */
	public void setParamMes(String paramMes) {
		this.paramMes = paramMes;
	}

	/**
	 * @return the paramSenha
	 */
	public String getParamSenha() {
		return paramSenha;
	}

	/**
	 * @param paramSenha the paramSenha to set
	 */
	public void setParamSenha(String paramSenha) {
		this.paramSenha = paramSenha;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the contratoCobranca
	 */
	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	/**
	 * @param contratoCobranca the contratoCobranca to set
	 */
	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	/**
	 * @return the operacaoContratoIUGU
	 */
	public List<OperacaoContratoIUGU> getOperacaoContratoIUGU() {
		return operacaoContratoIUGU;
	}

	/**
	 * @param operacaoContratoIUGU the operacaoContratoIUGU to set
	 */
	public void setOperacaoContratoIUGU(List<OperacaoContratoIUGU> operacaoContratoIUGU) {
		this.operacaoContratoIUGU = operacaoContratoIUGU;
	}

	/**
	 * @return the selectedOperacaoContratoPagadorRecebedorIUGU
	 */
	public PagadorRecebedor getSelectedOperacaoContratoPagadorRecebedorIUGU() {
		return selectedOperacaoContratoPagadorRecebedorIUGU;
	}

	/**
	 * @param selectedOperacaoContratoPagadorRecebedorIUGU the selectedOperacaoContratoPagadorRecebedorIUGU to set
	 */
	public void setSelectedOperacaoContratoPagadorRecebedorIUGU(
			PagadorRecebedor selectedOperacaoContratoPagadorRecebedorIUGU) {
		this.selectedOperacaoContratoPagadorRecebedorIUGU = selectedOperacaoContratoPagadorRecebedorIUGU;
	}

	public String getSenhaStorage() {
		return senhaStorage;
	}

	public void setSenhaStorage(String senhaStorage) {
		this.senhaStorage = senhaStorage;
	}

	/**
	 * @return the taxaJuros
	 */
	public int getTaxaJuros() {
		return taxaJuros;
	}

	/**
	 * @param taxaJuros the taxaJuros to set
	 */
	public void setTaxaJuros(int taxaJuros) {
		this.taxaJuros = taxaJuros;
	}

	/**
	 * @return the parcelas3meses
	 */
	public List<ContratoCobrancaDetalhes> getParcelas3meses() {
		return parcelas3meses;
	}

	/**
	 * @param parcelas3meses the parcelas3meses to set
	 */
	public void setParcelas3meses(List<ContratoCobrancaDetalhes> parcelas3meses) {
		this.parcelas3meses = parcelas3meses;
	}

	/**
	 * @return the relIsContrato
	 */
	public boolean isRelIsContrato() {
		return relIsContrato;
	}

	/**
	 * @param relIsContrato the relIsContrato to set
	 */
	public void setRelIsContrato(boolean relIsContrato) {
		this.relIsContrato = relIsContrato;
	}

	/**
	 * @return the contratosCobranca
	 */
	public List<ContratoCobranca> getContratosCobranca() {
		return contratosCobranca;
	}

	public boolean isReciboPDFGerado() {
		return reciboPDFGerado;
	}

	public void setReciboPDFGerado(boolean reciboPDFGerado) {
		this.reciboPDFGerado = reciboPDFGerado;
	}

	public String getPathPDF() {
		return pathPDF;
	}

	public void setPathPDF(String pathPDF) {
		this.pathPDF = pathPDF;
	}

	public String getNomePDF() {
		return nomePDF;
	}

	public void setNomePDF(String nomePDF) {
		this.nomePDF = nomePDF;
	}

	public StreamedContent getFile() {
		String caminho =  this.pathPDF + this.nomePDF;        
		String arquivo = this.nomePDF;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		file = new DefaultStreamedContent(stream, caminho, arquivo); 

		return file;  
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public class FileUploaded {
		private File file;
		private String name;
		private String path;

		public FileUploaded() {
		}

		public FileUploaded(String name, File file, String path) {
			this.name = name;
			this.file = file;
			this.path = path;
		}
		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}
		/**
		 * @param file the file to set
		 */
		public void setFile(File file) {
			this.file = file;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}
	}

	public Collection<FileUploaded> getFiles() {
		return files;
	}

	public void setFiles(Collection<FileUploaded> files) {
		this.files = files;
	}

	public List<FileUploaded> getDeletefiles() {
		return deletefiles;
	}

	public void setDeletefiles(List<FileUploaded> deletefiles) {
		this.deletefiles = deletefiles;
	}

	public FileUploaded getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(FileUploaded selectedFile) {
		this.selectedFile = selectedFile;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

	public void setDownloadAllFiles(StreamedContent downloadAllFiles) {
		this.downloadAllFiles = downloadAllFiles;
	}

	public void setObservacaoContratoDetalhes(String observacaoContratoDetalhes) {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		this.dataObservacao = dataHoje.getTime();

		this.observacao = "";

		this.listContratoCobrancaDetalhesObservacoes = new ArrayList<ContratoCobrancaDetalhesObservacoes>();

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

		this.listContratoCobrancaDetalhesObservacoes = contratoCobrancaDetalhesDao.listaObservacoesOrdenadas(this.contratoCobrancaDetalhes.getId());

		System.out.println("setObservacaoContratoDetalhes");
	}

	public List<ContratoCobrancaDetalhesObservacoes> getListContratoCobrancaDetalhesObservacoes() {
		return listContratoCobrancaDetalhesObservacoes;
	}

	public void setListContratoCobrancaDetalhesObservacoes(
			List<ContratoCobrancaDetalhesObservacoes> listContratoCobrancaDetalhesObservacoes) {
		this.listContratoCobrancaDetalhesObservacoes = listContratoCobrancaDetalhesObservacoes;
	}

	public Date getDataObservacao() {
		return dataObservacao;
	}

	public void setDataObservacao(Date dataObservacao) {
		this.dataObservacao = dataObservacao;
	}

	public String getObservacaoContratoDetalhes() {
		return observacaoContratoDetalhes;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public ContratoCobrancaDetalhesObservacoes getContratoCobrancaDetalhesObservacoes() {
		return contratoCobrancaDetalhesObservacoes;
	}

	public void setContratoCobrancaDetalhesObservacoes(
			ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes) {
		this.contratoCobrancaDetalhesObservacoes = contratoCobrancaDetalhesObservacoes;
	}

	public boolean isRelByVencimento() {
		return relByVencimento;
	}

	public void setRelByVencimento(boolean relByVencimento) {
		this.relByVencimento = relByVencimento;
	}

	public String getRelByStatus() {
		return relByStatus;
	}

	public void setRelByStatus(String relByStatus) {
		this.relByStatus = relByStatus;
	}

	public String getNomeSubConta() {
		return nomeSubConta;
	}

	public void setNomeSubConta(String nomeSubConta) {
		this.nomeSubConta = nomeSubConta;
	}

	public long getParcelaObservacao() {
		return parcelaObservacao;
	}

	public void setParcelaObservacao(long parcelaObservacao) {
		this.parcelaObservacao = parcelaObservacao;
	}

	public boolean isRelPorSubconta() {
		return relPorSubconta;
	}

	public void setRelPorSubconta(boolean relPorSubconta) {
		this.relPorSubconta = relPorSubconta;
	}

	public List<SaqueIUGU> getSaquesIUGU() {
		return saquesIUGU;
	}

	public void setSaquesIUGU(List<SaqueIUGU> saquesIUGU) {
		this.saquesIUGU = saquesIUGU;
	}

	public String getParamDia() {
		return paramDia;
	}

	public void setParamDia(String paramDia) {
		this.paramDia = paramDia;
	}

	public BigDecimal getTotalEntrada() {
		return totalEntrada;
	}

	public void setTotalEntrada(BigDecimal totalEntrada) {
		this.totalEntrada = totalEntrada;
	}

	public BigDecimal getTotalSaida() {
		return totalSaida;
	}

	public void setTotalSaida(BigDecimal totalSaida) {
		this.totalSaida = totalSaida;
	}

	public BigDecimal getTotalTaxas() {
		return totalTaxas;
	}

	public void setTotalTaxas(BigDecimal totalTaxas) {
		this.totalTaxas = totalTaxas;
	}

	public int getQtdeFaturasPagas() {
		return qtdeFaturasPagas;
	}

	public void setQtdeFaturasPagas(int qtdeFaturasPagas) {
		this.qtdeFaturasPagas = qtdeFaturasPagas;
	}

	public LazyDataModel<FaturaIUGU> getFaturasIUGULazy() {
		return faturasIUGULazy;
	}

	public void setFaturasIUGULazy(LazyDataModel<FaturaIUGU> faturasIUGULazy) {
		this.faturasIUGULazy = faturasIUGULazy;
	}

	public List<SaldoIUGU> getSaldosIUGU() {
		return saldosIUGU;
	}

	public void setSaldosIUGU(List<SaldoIUGU> saldosIUGU) {
		this.saldosIUGU = saldosIUGU;
	}

	public BigDecimal getTotalSaldoSubcontas() {
		return totalSaldoSubcontas;
	}

	public void setTotalSaldoSubcontas(BigDecimal totalSaldoSubcontas) {
		this.totalSaldoSubcontas = totalSaldoSubcontas;
	}

	public BigDecimal getTotalMovimentacoes() {
		return totalMovimentacoes;
	}

	public void setTotalMovimentacoes(BigDecimal totalMovimentacoes) {
		this.totalMovimentacoes = totalMovimentacoes;
	}

	public String getTotalMovimentacoesStr() {
		return totalMovimentacoesStr;
	}

	public void setTotalMovimentacoesStr(String totalMovimentacoesStr) {
		this.totalMovimentacoesStr = totalMovimentacoesStr;
	}

	public String getQtdeParcelas() {
		return qtdeParcelas;
	}

	public void setQtdeParcelas(String qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}

	public List<FaturaIUGU> getFaturasDownloadIUGU() {
		return faturasDownloadIUGU;
	}

	public void setFaturasDownloadIUGU(List<FaturaIUGU> faturasDownloadIUGU) {
		this.faturasDownloadIUGU = faturasDownloadIUGU;
	}
}