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
import java.util.List;
import java.util.Locale;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaParcelasInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "investidorMB")
@SessionScoped
public class InvestidorMB {
	
	private int qtdeContratos;
	private int parcelasAbertas;
	private BigDecimal valorReceber;
	private BigDecimal valorRecebido;
	private BigDecimal valorInvestido;	
	private List<ContratoCobranca> contratos;
	private ContratoCobranca selectedContrato;
	private ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes;
	
	private long idInvestidor;
	
	private BigDecimal valorInvestidor;
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	private User usuarioLogado;
	
	Collection<FileUploaded> files = new ArrayList<FileUploaded>();
	FileUploaded selectedFile = new FileUploaded();
	List<FileUploaded> deletefiles = new ArrayList<FileUploaded>();
	StreamedContent downloadFile;
	StreamedContent downloadAllFiles;
	
	/***
	 * INICIO ATRIBUTOS RECIBO
	 */
	private boolean debenturePDFGerado;
	private boolean valoresLiquidosInvestidoresPDFGerado;
	private boolean irRetidoInvestidoresPDFGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent file;
	
	private String numeroCautela;
	private String serie;
	private int qtdeDebentures;
	private Date dataDebentures;
	
	private int posicaoInvestidorNoContrato;
	
	/***
	 * FIM ATRIBUTOS RECIBO
	 */
	
	/** Objeto selecionado na LoV - Pagador. */
	private PagadorRecebedor selectedPagador;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<PagadorRecebedor> listPagadores;

	/** Nome do Pagador selecionado pela LoV. */
	private String nomePagador;	

	/** Id Objeto selecionado na LoV - Pagador. */
	private long idPagador;
	
	private Date dataInicio;
	private Date dataFim;
	
	List<ContratoCobrancaParcelasInvestidor> parcelasInvestidor;
	
	public InvestidorMB() {
		
	}
	
	public String clearFieldsValorLiquido() {
		this.dataInicio = gerarDataHoje(); 
		this.dataFim = gerarDataHoje();
		
		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		
		this.valoresLiquidosInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		
		return "/Atendimento/Cobranca/InvestidorValorLiquido.xhtml";
	}
	
	public void gerarRelatorioValorLiquido() {
		
		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();
		
		this.parcelasInvestidor = cDao.getParcelasPorDataInvestidor(this.dataInicio, this.dataFim);
		
		if (this.parcelasInvestidor.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Investidores: Não há registros para os filtros informados!", ""));
		}		
	}
	
	public String clearFieldsIRRetido() {
		this.dataInicio = gerarDataHoje(); 
		this.dataFim = gerarDataHoje();
		
		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		
		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		this.listPagadores = prDao.findAll();	
		clearPagador();
		
		this.irRetidoInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		
		return "/Atendimento/Cobranca/InvestidorIRRetido.xhtml";
	}
	
	public void gerarRelatorioIRRetido() {
		
		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();
		
		// busca apenas parcelas baixadas
		this.parcelasInvestidor = cDao.getParcelasPorDataInvestidorBaixadas(this.dataInicio, this.dataFim, this.selectedPagador.getId());
		
		if (this.parcelasInvestidor.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Investidores: Não há registros para os filtros informados!", ""));
		}		
	}
	
	public BigDecimal getTotalLiquidoInvestidor(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoTodosInvestidores() {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		if (this.parcelasInvestidor != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
				}			
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalParcelaInvestidor(long idInvestidor) {
		BigDecimal totalParcela= BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalParcela = totalParcela.add(parcelas.getParcelaMensal());
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalParcelaTodosInvestidores() {
		BigDecimal totalParcela = BigDecimal.ZERO;
		
		if (this.parcelasInvestidor != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensal());
				}			
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalIRRetidoInvestidor(long idInvestidor) {
		BigDecimal totalIRRetido = BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalIRRetido = totalIRRetido.add(parcelas.getIrRetido());
			}
		}
		
		return totalIRRetido;
	}
	
	public void clearFields() {
		this.qtdeContratos = 0;
		this.parcelasAbertas = 0;
		this.valorReceber = BigDecimal.ZERO;
		this.valorRecebido = BigDecimal.ZERO;
		this.valorInvestidor = BigDecimal.ZERO;
		this.valorInvestido = BigDecimal.ZERO;	
		this.idInvestidor = 0;
		this.files = new ArrayList<FileUploaded>();
		this.posicaoInvestidorNoContrato = 0;
		
		this.usuarioLogado = new User();
		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
	}
	
	public void getContratosInvestidor() {
		clearFields();
		this.usuarioLogado = getUsuarioLogado();
		
		if (usuarioLogado != null) {
			// Busca o cadastro do Recebedor pelo ID do Usuário Logado
			PagadorRecebedorDao prDao = new PagadorRecebedorDao();
			PagadorRecebedor pr = new PagadorRecebedor();
			pr = prDao.getRecebedorByUsuarioInvestidor(usuarioLogado.getId());
			
			if (pr.getId() > 0) {
				this.idInvestidor = pr.getId();
				
				// get contratos por investidor
				ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
				this.contratos = contratoDao.getContratosPorInvestidor(this.idInvestidor);
				
				getCardsDashboards();		
			}
		}
	}
	
	// busca a posicao do investidor no contrato
	public void buscaPosicaoInvestidorNoContrato(ContratoCobranca contrato) {
		this.posicaoInvestidorNoContrato = 0;
		
		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 1;
			}
		}
		
		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 2;
			}	
		}
		
		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 3;
			}		
		}
		
		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 4;
			}		
		}
		
		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 5;
			}
		}
		
		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 6;
			}		
		}
		
		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 7;
			}	
		}
		
		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 8;
			}	
		}
		
		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 9;
			}	
		}
		
		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 10;
			}
		}		
	}
	
	// calcula quantidade de parcelas abertas
	public void getCardsDashboards() {
		this.qtdeContratos = this.contratos.size();
		
		this.valorInvestido = BigDecimal.ZERO;
		BigDecimal valorInvestidoContrato = BigDecimal.ZERO;
		
		for (ContratoCobranca c : this.contratos) {
			// busca o valor do investidor no contrato
			getInformacoesDoInvestidorNoContrato(c);			
			
			buscaPosicaoInvestidorNoContrato(c);
			
			// busca valor investido, sendo o saldo credor da primeira parcela não paga
			valorInvestidoContrato = getValorInvestidoNoContrato(c);
			
			if (this.posicaoInvestidorNoContrato == 1) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor1()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}				
			}
				
			if (this.posicaoInvestidorNoContrato == 2) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor2()) {
					if (cd.isBaixado()) { 				
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}

			if (this.posicaoInvestidorNoContrato == 3) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor3()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 4) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor4()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 5) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor5()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 6) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor6()) {
					if (cd.isBaixado()) { 				
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}		
			
			if (this.posicaoInvestidorNoContrato == 7) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor7()) {
					if (cd.isBaixado()) { 				
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 8) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor8()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}	
			
			if (this.posicaoInvestidorNoContrato == 9) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor9()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 10) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor10()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}	
			
			// Atribui o valor investido no contrato em questão a variavel global			
			this.valorInvestido = this.valorInvestido.add(valorInvestidoContrato);
				
			// busca valores a receber e pagos
			if (this.posicaoInvestidorNoContrato == 1) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor1()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 2) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor2()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 3) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor3()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 4) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor4()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 5) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor5()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 6) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor6()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 7) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor7()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 8) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor8()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 9) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor9()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 10) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor10()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
		}
	}

	/***
	 * Lista ois arquivos contidos no diretório
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos(ContratoCobranca contrato) {
		//DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao(); 
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString() + contrato.getNumeroContrato() + "/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
			}
		}
		return lista;
	}
	
	public void getFilesDoInvestidorNoContrato(ContratoCobranca contrato) {
		// get files do contrato
		this.files = new ArrayList<FileUploaded>();
		this.files = listaArquivos(contrato);
	}
	
	public void getInformacoesDoInvestidorNoContrato(ContratoCobranca contrato) {		
		this.selectedContrato = contrato;
		
		// get valor do investidor no contrato
		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());
			}			
		}
		
		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor2();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());
			}			
		}
		
		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor3();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());
			}			
		}
		
		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor4();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());
			}			
		}
		
		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor5();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());
			}			
		}
		
		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor6();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());
			}			
		}
		
		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor7();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());
			}			
		}
		
		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor8();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());
			}			
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor9();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());
			}			
		}
		
		
		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor10();
				this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());
			}			
		}
	}
	
	public BigDecimal getValorInvestidoNoContrato(ContratoCobranca contrato) {
		BigDecimal valor = BigDecimal.ZERO;
		
		if (contrato.getRecebedorParcelaFinal1() != null) {
			if (contrato.getRecebedorParcelaFinal1().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor1();
			}			
		}
		
		if (contrato.getRecebedorParcelaFinal2() != null) {
			if (contrato.getRecebedorParcelaFinal2().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor2();
			}			
		}
		
		if (contrato.getRecebedorParcelaFinal3() != null) {
			if (contrato.getRecebedorParcelaFinal3().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor3();
			}			
		}
		
		if (contrato.getRecebedorParcelaFinal4() != null) {
			if (contrato.getRecebedorParcelaFinal4().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor4();
			}			
		}
		
		if (contrato.getRecebedorParcelaFinal5() != null) {
			if (contrato.getRecebedorParcelaFinal5().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor5();
			}			
		}
		
		return valor;
	}
	
	public String goToAlteraSenhaInvestidor() {
		

		return "./AlteraSenhaInvestidor.xhtml";
	}
	
	public User getUsuarioLogado() {
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			return usuarioLogado;
		} else {
			return null;	
		}
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
	 * Exemplo de Zip de um Diretório inteiro
	 * @param srcFolder
	 * @param destZipFile
	 * @throws Exception
	 */
	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
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
	
	public void viewFile(String fileName, ContratoCobranca contrato) {
        
        try {        	
    			FacesContext facesContext = FacesContext.getCurrentInstance();
    			ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            BufferedInputStream input = null;
            BufferedOutputStream output = null;
            
            
            ParametrosDao pDao = new ParametrosDao(); 
    			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString() + contrato.getNumeroContrato() + "/" + fileName;
    			
    			/*
    			   	  'docx'  => 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
				  'xlsx'  => 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
				  'word'  => 'application/msword',
				  'xls'   => 'application/excel',
				  'pdf'   => 'application/pdf'
				  'psd'   => 'application/x-photoshop'
    			 */
    			String mineFile = "";
    			
    			if (fileName.contains(".jpg") || fileName.contains(".JPG")) {
    				mineFile = "image-jpg";
    			}
    			
    			if (fileName.contains(".jpeg") || fileName.contains(".jpeg")) {
    				mineFile = "image-jpeg";
    			}
    			
    			if (fileName.contains(".png") || fileName.contains(".PNG")) {
    				mineFile = "image-png";
    			}
    			
    			if (fileName.contains(".pdf") || fileName.contains(".PDF")) {
    				mineFile = "application/pdf";
    			}
    			
    			File arquivo = new File(pathContrato); 
    		
			input = new BufferedInputStream(new FileInputStream(arquivo), 10240);
			
	        response.reset();
	        // lire un fichier pdf
	        response.setHeader("Content-type", mineFile); 
	        
	        response.setContentLength((int)arquivo.length());

	        response.setHeader("Content-disposition", "inline; filename=" +arquivo.getName());
	        output = new BufferedOutputStream(response.getOutputStream(), 10240);

	        // Write file contents to response.
	        byte[] buffer = new byte[10240];
	        int length;
	        while ((length = input.read(buffer)) > 0) {
	            output.write(buffer, 0, length);
	        }

	        // Finalize task.
	        output.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	public String clearGeraDebenture() {
		this.debenturePDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		
		this.numeroCautela = "";
		this.serie = "";
		this.qtdeDebentures = 0;
		this.dataDebentures = gerarDataHoje();
		
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();	
		
		return "/Atendimento/Cobranca/GerarDocumentoDebentures.xhtml";
	}

	public void geraDebenture() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
			
			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);
			
			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao(); 
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Debenture.pdf";
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
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("GALLERIA FINANÇAS SECURITIZADORA S.A.", headerFull));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setPaddingTop(20f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CNPJ/MF: 34.425.347/0001-06", headerFull));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Rua Avenida Doutor José Bonifácio Coutinho, 150 - Jardim Madalena - Campinas - SP", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Data de Constituição da Sociedade: 05/08/2019, com seus atos constitutivos arquivados na Junta Comercial do Estado do São Paulo em 02 de Setembro de 2019, sob o nº ED003063-6/000.", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(40f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Objeto Social: A Sociedade tem por Objeto a aquisição e securitização de recebíveis comerciais e industriais.", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(40f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prazo de Duração da Sociedade: Indeterminado", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setPaddingTop(40f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("DEBÊNTURES SIMPLES, SUBORDINADAS", headerFullRed));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Cautela N°", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Quantidade de Debêntures", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.numeroCautela, headerFullRed));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(this.qtdeDebentures), headerFullRed));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			BigDecimal valorDebenture = new BigDecimal(this.qtdeDebentures).multiply(new BigDecimal(1000));
			
			String documento = "";
			
			if (this.selectedPagador.getCpf() != null) {
				documento = this.selectedPagador.getCpf();
			} else {
				documento = this.selectedPagador.getCnpj();
			}
			
			cell1 = new PdfPCell(new Phrase("Esta cautela representativa de Cem debêntures, não conversíveis em ações, da 1ª (primeira) emissão privada, série " + this.serie + ", no valor nominal unitário de R$ 1.000,00 (Um mil reais), totalizando R$ " + df.format(valorDebenture) + ", e demais características especificadas no instrumento particular de escritura da primeira emissão privada de debêntures simples da Galleria Finanças Securitizadora S.A., ficando disponível cópia autenticada na sede desta companhia. Confere a " + this.selectedPagador.getNome() + ", " + documento + ", os direitos que a lei e a escritura de emissão lhes asseguram.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(6);
			table.addCell(cell1);	
			
			cell1 = new PdfPCell(new Phrase("Campinas (SP), " + dia.format(this.dataDebentures) + " de " + mes.format(this.dataDebentures) + " de " + ano.format(this.dataDebentures), headerFull));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(6);
			table.addCell(cell1);			
			
			cell1 = new PdfPCell(new Phrase("______________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(80f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("______________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(80f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Fabricio Figueiredo", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("João Augusto Magatti Alves", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Diretor Comercial", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Diretor Administrativo", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Debênture: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Debênture: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.debenturePDFGerado = true;

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
	
	public void geraPDFValorLiquidoInvestidores() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
			
			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);
			
			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao(); 
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Investidores - Valores Líquidos a Receber.pdf";
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
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Investidores - Valores Líquidos a Receber - " + sdfDataRel.format(this.dataInicio) + " a " +  sdfDataRel.format(this.dataFim), header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			PagadorRecebedor investidorTemp = new PagadorRecebedor();
			
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				// Popula investidor para fazer a quebra do relatório
				if (investidorTemp != parcelas.getInvestidor()) {
					// se for a primeira passada não terá total
					if (investidorTemp.getId() > 0) {
						cell1 = new PdfPCell(new Phrase("Total:", titulo));
						cell1.setBorder(0);
						cell1.setBorderWidthLeft(1);
						cell1.setBorderColorLeft(BaseColor.BLACK);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);	
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);	
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(4);
						table.addCell(cell1);
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidor(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);	
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);		
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidor(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);	
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);		
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
					}					
					
					investidorTemp = parcelas.getInvestidor();
										
					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome() , header));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);	
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG." + investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(), titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);	
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Contrato", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Pagador", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Data de Vencimento", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);		
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Valor Bruto da Parcela", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Valor Líquido a Receber", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}
				
				cell1 = new PdfPCell(new Phrase(parcelas.getNumeroContrato(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);		
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(parcelas.getPagador().getNome(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);		
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(parcelas.getDataVencimento()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getParcelaMensal()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);	
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getValorLiquido()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);	
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}
			
			// gera a última linha de total dos registros e total geral
			if (this.parcelasInvestidor.size() > 0) {
				if (investidorTemp.getId() > 0) {
					cell1 = new PdfPCell(new Phrase("Total:", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);	
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(4);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidor(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);	
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);		
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidor(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);	
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);		
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}	
				
				cell1 = new PdfPCell(new Phrase("Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidores()), header));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);			
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell1.setPaddingTop(15f);
				cell1.setPaddingBottom(15f);	
				cell1.setUseBorderPadding(true);
				cell1.setColspan(6);
				table.addCell(cell1);				
			}
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.valoresLiquidosInvestidoresPDFGerado = true;

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
	
	public void geraPDFIRRetidoInvestidores() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
			
			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
			Locale locale = new Locale("pt", "BR"); 
			Calendar date = Calendar.getInstance(zone, locale);  
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);
			
			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao(); 
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Investidores - IR Retido.pdf";
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
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Investidores - IR Retido - " + sdfDataRel.format(this.dataInicio) + " a " +  sdfDataRel.format(this.dataFim), header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);		
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			PagadorRecebedor investidorTemp = new PagadorRecebedor();
			
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				// Popula investidor para fazer a quebra do relatório
				if (investidorTemp != parcelas.getInvestidor()) {
					// se for a primeira passada não terá total
					if (investidorTemp.getId() > 0) {
						cell1 = new PdfPCell(new Phrase("Total IR Retido:", titulo));
						cell1.setBorder(0);
						cell1.setBorderWidthLeft(1);
						cell1.setBorderColorLeft(BaseColor.BLACK);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);	
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);	
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(5);
						table.addCell(cell1);
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalIRRetidoInvestidor(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);	
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);		
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
					}					
					
					investidorTemp = parcelas.getInvestidor();
										
					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome(), header));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);	
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Contrato", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Data da Baixa", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);		
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase("Valor IR Retido", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);			
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);
				}
				
				cell1 = new PdfPCell(new Phrase(parcelas.getNumeroContrato(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);		
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(parcelas.getDataBaixa()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getIrRetido()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);	
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);	
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);
			}
			
			// gera última linha do total
			if (this.parcelasInvestidor.size() > 0) {
				if (investidorTemp.getId() > 0) {
					cell1 = new PdfPCell(new Phrase("Total IR Retido:", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);	
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);	
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(5);
					table.addCell(cell1);
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalIRRetidoInvestidor(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);	
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);		
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}	
			}
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.irRetidoInvestidoresPDFGerado = true;

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
	
	public final void populateSelectedPagador() {
		this.idPagador = this.selectedPagador.getId();
		this.nomePagador = this.selectedPagador.getNome();
	}

	public void clearPagador() {
		this.idPagador = 0;
		this.nomePagador = null;
		this.selectedPagador = new PagadorRecebedor();
	}

	public int getQtdeContratos() {
		return qtdeContratos;
	}

	public void setQtdeContratos(int qtdeContratos) {
		this.qtdeContratos = qtdeContratos;
	}

	public int getParcelasAbertas() {
		return parcelasAbertas;
	}

	public void setParcelasAbertas(int parcelasAbertas) {
		this.parcelasAbertas = parcelasAbertas;
	}

	public BigDecimal getValorReceber() {
		return valorReceber;
	}

	public void setValorReceber(BigDecimal valorReceber) {
		this.valorReceber = valorReceber;
	}

	public BigDecimal getValorRecebido() {
		return valorRecebido;
	}

	public void setValorRecebido(BigDecimal valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

	public List<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}

	public ContratoCobranca getSelectedContrato() {
		return selectedContrato;
	}

	public void setSelectedContrato(ContratoCobranca selectedContrato) {
		this.selectedContrato = selectedContrato;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public BigDecimal getValorInvestidor() {
		return valorInvestidor;
	}

	public void setValorInvestidor(BigDecimal valorInvestidor) {
		this.valorInvestidor = valorInvestidor;
	}

	public ContratoCobrancaDetalhes getSelectedContratoCobrancaDetalhes() {
		return selectedContratoCobrancaDetalhes;
	}

	public void setSelectedContratoCobrancaDetalhes(ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes) {
		this.selectedContratoCobrancaDetalhes = selectedContratoCobrancaDetalhes;
	}

	public void setUsuarioLogado(User usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public BigDecimal getValorInvestido() {
		return valorInvestido;
	}

	public void setValorInvestido(BigDecimal valorInvestido) {
		this.valorInvestido = valorInvestido;
	}

	public Collection<FileUploaded> getFiles() {
		return files;
	}

	public void setFiles(Collection<FileUploaded> files) {
		this.files = files;
	}
	
	public FileUploaded getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(FileUploaded selectedFile) {
		this.selectedFile = selectedFile;
	}

	public List<FileUploaded> getDeletefiles() {
		return deletefiles;
	}

	public void setDeletefiles(List<FileUploaded> deletefiles) {
		this.deletefiles = deletefiles;
	}

	public StreamedContent getDownloadAllFiles() {
		return downloadAllFiles;
	}

	public void setDownloadAllFiles(StreamedContent downloadAllFiles) {
		this.downloadAllFiles = downloadAllFiles;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
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

	public boolean isDebenturePDFGerado() {
		return debenturePDFGerado;
	}

	public void setDebenturePDFGerado(boolean debenturePDFGerado) {
		this.debenturePDFGerado = debenturePDFGerado;
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

	public long getIdInvestidor() {
		return idInvestidor;
	}

	public void setIdInvestidor(long idInvestidor) {
		this.idInvestidor = idInvestidor;
	}

	public String getNumeroCautela() {
		return numeroCautela;
	}

	public void setNumeroCautela(String numeroCautela) {
		this.numeroCautela = numeroCautela;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public int getQtdeDebentures() {
		return qtdeDebentures;
	}

	public void setQtdeDebentures(int qtdeDebentures) {
		this.qtdeDebentures = qtdeDebentures;
	}

	public Date getDataDebentures() {
		return dataDebentures;
	}

	public void setDataDebentures(Date dataDebentures) {
		this.dataDebentures = dataDebentures;
	}

	public PagadorRecebedor getSelectedPagador() {
		return selectedPagador;
	}

	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagador = selectedPagador;
	}

	public List<PagadorRecebedor> getListPagadores() {
		return listPagadores;
	}

	public void setListPagadores(List<PagadorRecebedor> listPagadores) {
		this.listPagadores = listPagadores;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public long getIdPagador() {
		return idPagador;
	}

	public void setIdPagador(long idPagador) {
		this.idPagador = idPagador;
	}

	public int getPosicaoInvestidorNoContrato() {
		return posicaoInvestidorNoContrato;
	}

	public void setPosicaoInvestidorNoContrato(int posicaoInvestidorNoContrato) {
		this.posicaoInvestidorNoContrato = posicaoInvestidorNoContrato;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public List<ContratoCobrancaParcelasInvestidor> getParcelasInvestidor() {
		return parcelasInvestidor;
	}

	public void setParcelasInvestidor(List<ContratoCobrancaParcelasInvestidor> parcelasInvestidor) {
		this.parcelasInvestidor = parcelasInvestidor;
	}

	public boolean isValoresLiquidosInvestidoresPDFGerado() {
		return valoresLiquidosInvestidoresPDFGerado;
	}

	public void setValoresLiquidosInvestidoresPDFGerado(boolean valoresLiquidosInvestidoresPDFGerado) {
		this.valoresLiquidosInvestidoresPDFGerado = valoresLiquidosInvestidoresPDFGerado;
	}

	public boolean isIrRetidoInvestidoresPDFGerado() {
		return irRetidoInvestidoresPDFGerado;
	}

	public void setIrRetidoInvestidoresPDFGerado(boolean irRetidoInvestidoresPDFGerado) {
		this.irRetidoInvestidoresPDFGerado = irRetidoInvestidoresPDFGerado;
	}
	
	
}
