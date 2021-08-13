package com.webnowbr.siscoat.cobranca.mb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioContabilidadeEmAberto;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioContabilidadeInvestidor;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioContabilidadePosicaoRetroativaInvestidor;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

@ManagedBean(name = "contabilidadeMB")
@SessionScoped
public class ContabilidadeMB {
	List<ContratoCobranca> listContratos;
	List<RelatorioContabilidadeEmAberto> relatorioContabilidadeEmAberto;	
	
	private StreamedContent file;
	private String pathContrato;
	private String nomeContrato;
	private boolean xlsGerado;
	
	String anoBase;
	private Date dataInicio;
	private Date dataFim;
	String labelAnoBase;
	
	List<RelatorioContabilidadePosicaoRetroativaInvestidor> relatorioContabilidadePosicaoRetroativaInvestidor = new ArrayList<RelatorioContabilidadePosicaoRetroativaInvestidor>();
	
	public String clearFieldsPosicaoAtual() {
		this.listContratos = new ArrayList<ContratoCobranca>();	
		this.relatorioContabilidadeEmAberto = new ArrayList<RelatorioContabilidadeEmAberto>();
		geraRelatorioValoresAReceber();
		
		this.pathContrato = "";
		this.nomeContrato = "";
		this.file = null;
		this.xlsGerado = false;
		
		return "/Atendimento/Cobranca/ContabilidadePosicaoAtual.xhtml";
	}
	
	public String clearFieldsPosicaoRetroativa() {
		this.listContratos = new ArrayList<ContratoCobranca>();	
		this.relatorioContabilidadePosicaoRetroativaInvestidor = new ArrayList<RelatorioContabilidadePosicaoRetroativaInvestidor>();
		
		this.pathContrato = "";
		this.nomeContrato = "";
		this.anoBase = "";
		this.file = null;
		this.xlsGerado = false;
		
		return "/Atendimento/Cobranca/ContabilidadePosicaoRetroativa.xhtml";
	}
	
	public void consultaPosicaoRetroativa() {
		this.listContratos = new ArrayList<ContratoCobranca>();	

		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			if (this.anoBase.equals("2020")) {
		
				this.dataInicio = format.parse("31/12/2019");
	
				this.dataFim = format.parse("31/12/2020");
	
				this.labelAnoBase = "Valor em 31/12/2020";
			
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				this.listContratos = new ArrayList<ContratoCobranca>();	
				
				//Map<String, Object> filters = new HashMap<String, Object>();
				//filters.put("numeroContrato", "01003");
				//this.listContratos = cDao.findByFilter(filters);			
				
				this.listContratos = cDao.findByFilter("aprovado", true);
						
				RelatorioContabilidadePosicaoRetroativaInvestidor debentures = new RelatorioContabilidadePosicaoRetroativaInvestidor();
				
				BigDecimal totalAPagarPorContrato = BigDecimal.ZERO;
				
				this.relatorioContabilidadePosicaoRetroativaInvestidor = new ArrayList<RelatorioContabilidadePosicaoRetroativaInvestidor>();
				
				for (ContratoCobranca c : this.listContratos) {
					
					totalAPagarPorContrato = BigDecimal.ZERO;
					
					debentures = new RelatorioContabilidadePosicaoRetroativaInvestidor();
					
					List<ContratoCobrancaParcelasInvestidor> contratoCobrancaParcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
									
					// Utiliza somente os contratos que são da Galleria Securitizadora e que não possuem a Galleria como Pagador
					if (c.getEmpresa() != null && c.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {									
						if (pagadorRecebedorNaoGalleria(c.getRecebedor()) != null && !c.isRecebedorEnvelope() && !c.isOcultaRecebedor()) {
							contratoCobrancaParcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
							
							// valida investidores	
							// PAGADOR 1
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor1()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 2
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor2()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 3
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor3()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 4
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor4()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 5
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor5()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 6
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor6()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 7
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor7()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 8
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor8()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 9
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor9()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							// PAGADOR 10
							for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor10()) {
								if (!parcela.isBaixado()) {
									contratoCobrancaParcelasInvestidor.add(parcela);
									if (parcela.getParcelaMensal() != null) {
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								} else {									
									if (parcela.getDataBaixa().after(this.dataFim)) {
										contratoCobrancaParcelasInvestidor.add(parcela);
										totalAPagarPorContrato = totalAPagarPorContrato.add(parcela.getParcelaMensal());
									}
								}
							}
							
							if (contratoCobrancaParcelasInvestidor.size() > 0) {
								debentures.setTemParcelas(true);
								debentures.setContrato(c);
								debentures.setListContratoCobrancaParcelasInvestidor(contratoCobrancaParcelasInvestidor);
							}
						}
					}
								
					if (debentures.isTemParcelas()) {
						this.relatorioContabilidadePosicaoRetroativaInvestidor.add(debentures);
					}
				}	
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String clearFieldsPosicaoAtualSimplificado() {
		this.listContratos = new ArrayList<ContratoCobranca>();	
		this.relatorioContabilidadeEmAberto = new ArrayList<RelatorioContabilidadeEmAberto>();
		geraRelatorioValoresAReceberSimplificado();
		
		this.pathContrato = "";
		this.nomeContrato = "";
		this.file = null;
		this.xlsGerado = false;
		
		return "/Atendimento/Cobranca/ContabilidadePosicaoAtualSimplificado.xhtml";
	}
		
	public void geraRelatorioValoresAReceber() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.relatorioContabilidadeEmAberto = new ArrayList<RelatorioContabilidadeEmAberto>();
		RelatorioContabilidadeEmAberto relatorioContabilidadeEmAbertoAux = new RelatorioContabilidadeEmAberto();
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = new ArrayList<ContratoCobranca>();	
		
		//Map<String, Object> filters = new HashMap<String, Object>();
		//filters.put("numeroContrato", "01003");
		//this.listContratos = cDao.findByFilter(filters);
		this.listContratos = cDao.findAll();
		
		List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesEmAberto = new ArrayList<ContratoCobrancaDetalhes>();
		List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesQuitadas = new ArrayList<ContratoCobrancaDetalhes>();
		
		BigDecimal saldoReceberContrato = BigDecimal.ZERO;
		
		for (ContratoCobranca c : this.listContratos) {
			
			saldoReceberContrato = BigDecimal.ZERO;
			
			relatorioContabilidadeEmAbertoAux = new RelatorioContabilidadeEmAberto();
			
			// Utiliza somente os contratos que são da Galleria Securitizadora e que não possuem a Galleria como Pagador
			if (c.getEmpresa() != null && c.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
				// Dados Básicos
				relatorioContabilidadeEmAbertoAux.setNumeroContrato(c.getNumeroContrato());
				relatorioContabilidadeEmAbertoAux.setDataContrato(c.getDataContrato());
				
				relatorioContabilidadeEmAbertoAux.setResponsavel(c.getResponsavel());
				relatorioContabilidadeEmAbertoAux.setPagador(c.getPagador());
								
				relatorioContabilidadeEmAbertoAux.setContrato(c);				
				
				// Popula Investidores e saldo credores em aberto
				BigDecimal saldoInvestidoresAberto = BigDecimal.ZERO;
				List<RelatorioContabilidadeInvestidor> listInvestidores = new ArrayList<RelatorioContabilidadeInvestidor>();
				RelatorioContabilidadeInvestidor investidor = new RelatorioContabilidadeInvestidor(); 
				
				if (pagadorRecebedorNaoGalleria(c.getRecebedor()) != null && !c.isRecebedorEnvelope() && !c.isOcultaRecebedor()) {					
					investidor.setInvestidor(c.getRecebedor());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor1(), 1));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}
				
				if (pagadorRecebedorNaoGalleria(c.getRecebedor2()) != null && !c.isRecebedorEnvelope2() && !c.isOcultaRecebedor2()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor2());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor2(), 2));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor3()) != null && !c.isRecebedorEnvelope3() && !c.isOcultaRecebedor3()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor3());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor3(), 3));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor4()) != null && !c.isRecebedorEnvelope4() && !c.isOcultaRecebedor4()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor4());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor4(), 4));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor5()) != null && !c.isRecebedorEnvelope5() && !c.isOcultaRecebedor5()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor5());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor5(), 5));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor6()) != null && !c.isRecebedorEnvelope6() && !c.isOcultaRecebedor6()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor6());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor6(), 6));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor7()) != null && !c.isRecebedorEnvelope7() && !c.isOcultaRecebedor7()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor7());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor7(), 7));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor8()) != null && !c.isRecebedorEnvelope8() && !c.isOcultaRecebedor8()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor8());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor8(), 8));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor9()) != null && !c.isRecebedorEnvelope9() && !c.isOcultaRecebedor9()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor9());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor9(), 9));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor10()) != null && !c.isRecebedorEnvelope10() && !c.isOcultaRecebedor10()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor10());
					investidor.setSaldoInvestidoresAberto(getSaldoCredor(c, c.getListContratoCobrancaParcelasInvestidor10(), 10));					
					
					if (!(investidor.getSaldoInvestidoresAberto().compareTo(BigDecimal.ZERO) == 0)) {
						saldoInvestidoresAberto = saldoInvestidoresAberto.add(investidor.getSaldoInvestidoresAberto());
						
						listInvestidores.add(investidor);
					}
				}
				
				if (listInvestidores.size() > 0) {
					relatorioContabilidadeEmAbertoAux.setListInvestidores(listInvestidores);
				}
								
				// Calcula parcelas em aberto.
				// Se pagador é a Galleria zera o valor do contrato
				if (pagadorRecebedorNaoGalleria(c.getPagador()) != null) {
					for (ContratoCobrancaDetalhes parcelasContrato : c.getListContratoCobrancaDetalhes()) {
						if (!parcelasContrato.isParcelaPaga()) {						
							ContratoCobrancaDetalhes parcelaCalculada = calculaJurosParcela(c.getTxJuros(), c.getTxMulta(), parcelasContrato);
						
							listContratoCobrancaDetalhesEmAberto.add(parcelaCalculada);
							
							if (parcelaCalculada.getVlrParcelaAtualizada() != null && parcelaCalculada.getVlrParcelaAtualizada() != BigDecimal.ZERO) {
								saldoReceberContrato = saldoReceberContrato.add(parcelaCalculada.getVlrParcelaAtualizada());
							} else {
								saldoReceberContrato = saldoReceberContrato.add(parcelaCalculada.getVlrParcela());	
							}						
						} else {
							listContratoCobrancaDetalhesQuitadas.add(parcelasContrato);
						}
					}
				} else {
					saldoReceberContrato = BigDecimal.ZERO;
				}
				
				if (!(saldoInvestidoresAberto.compareTo(BigDecimal.ZERO) == 0 && saldoReceberContrato.compareTo(BigDecimal.ZERO) == 0)) {
					relatorioContabilidadeEmAbertoAux.setSaldoInvestidoresAberto(saldoInvestidoresAberto);
					relatorioContabilidadeEmAbertoAux.setSaldoParcelasAberto(saldoReceberContrato);
					
					// Seta as listas de parcelas do contrato
					if (listContratoCobrancaDetalhesEmAberto.size() > 0) {
						relatorioContabilidadeEmAbertoAux.setListContratoCobrancaDetalhesEmAberto(listContratoCobrancaDetalhesEmAberto);
					}
					
					if (listContratoCobrancaDetalhesQuitadas.size() > 0) {
						relatorioContabilidadeEmAbertoAux.setListContratoCobrancaDetalhesQuitadas(listContratoCobrancaDetalhesQuitadas);
					}
					
					this.relatorioContabilidadeEmAberto.add(relatorioContabilidadeEmAbertoAux);
				}
			}			
		}
		
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Contabilidade: Posição atual gerada com sucesso!!!", ""));
	}
	
	public void geraRelatorioValoresAReceberSimplificado() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.relatorioContabilidadeEmAberto = new ArrayList<RelatorioContabilidadeEmAberto>();
		RelatorioContabilidadeEmAberto relatorioContabilidadeEmAbertoAux = new RelatorioContabilidadeEmAberto();
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listContratos = new ArrayList<ContratoCobranca>();	
		
		//Map<String, Object> filters = new HashMap<String, Object>();
		//filters.put("numeroContrato", "01003");
		//this.listContratos = cDao.findByFilter(filters);
		this.listContratos = cDao.findAll();
		
		BigDecimal saldoReceberContrato = BigDecimal.ZERO;
		
		for (ContratoCobranca c : this.listContratos) {
			
			saldoReceberContrato = BigDecimal.ZERO;
			
			relatorioContabilidadeEmAbertoAux = new RelatorioContabilidadeEmAberto();
			
			// Utiliza somente os contratos que são da Galleria Securitizadora e que não possuem a Galleria como Pagador
			if (c.getEmpresa() != null && c.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
				// Dados Básicos
				relatorioContabilidadeEmAbertoAux.setNumeroContrato(c.getNumeroContrato());
				relatorioContabilidadeEmAbertoAux.setDataContrato(c.getDataContrato());
				
				relatorioContabilidadeEmAbertoAux.setResponsavel(c.getResponsavel());
				relatorioContabilidadeEmAbertoAux.setPagador(c.getPagador());
								
				relatorioContabilidadeEmAbertoAux.setContrato(c);				
				
				// Popula Investidores e saldo credores em aberto
				BigDecimal saldoInvestidoresAberto = BigDecimal.ZERO;
				List<RelatorioContabilidadeInvestidor> listInvestidores = new ArrayList<RelatorioContabilidadeInvestidor>();
				RelatorioContabilidadeInvestidor investidor = new RelatorioContabilidadeInvestidor(); 
				
				if (pagadorRecebedorNaoGalleria(c.getRecebedor()) != null && !c.isRecebedorEnvelope() && !c.isOcultaRecebedor()) {					
					investidor.setInvestidor(c.getRecebedor());
					
					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor1(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor1()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor1()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor1().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor1().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor1().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor1().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor1().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}
				
				if (pagadorRecebedorNaoGalleria(c.getRecebedor2()) != null && !c.isRecebedorEnvelope2() && !c.isOcultaRecebedor2()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor2());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor2(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor2()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor2()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor2().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor2().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor2().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor2().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor2().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor2().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor3()) != null && !c.isRecebedorEnvelope3() && !c.isOcultaRecebedor3()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor3());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor3(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor3()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor3()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor3().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor3().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor3().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor3().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor3().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor3().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor4()) != null && !c.isRecebedorEnvelope4() && !c.isOcultaRecebedor4()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor4());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor4(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor4()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor4()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor4().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor4().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor4().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor4().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor4().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor4().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor5()) != null && !c.isRecebedorEnvelope5() && !c.isOcultaRecebedor5()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor5());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor5(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor5()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor5()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor5().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor5().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor5().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor5().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor5().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor5().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor6()) != null && !c.isRecebedorEnvelope6() && !c.isOcultaRecebedor6()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor6());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor6(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor6()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor6()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor6().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor6().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor6().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor6().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor6().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor6().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor7()) != null && !c.isRecebedorEnvelope7() && !c.isOcultaRecebedor7()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor7());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor7(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor7()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor7()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor7().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor7().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor7().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor7().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor7().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor7().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor8()) != null && !c.isRecebedorEnvelope8() && !c.isOcultaRecebedor8()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor8());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor8(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor8()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor8()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor8().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor8().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor8().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor8().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor8().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor8().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor9()) != null && !c.isRecebedorEnvelope9() && !c.isOcultaRecebedor9()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor9());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor9(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor9()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor9()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor9().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor9().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor9().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor9().get(0).getParcelaMensal());

								if (c.getListContratoCobrancaParcelasInvestidor9().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor9().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}

				if (pagadorRecebedorNaoGalleria(c.getRecebedor10()) != null && !c.isRecebedorEnvelope10() && !c.isOcultaRecebedor10()) {
					investidor = new RelatorioContabilidadeInvestidor();
					investidor.setInvestidor(c.getRecebedor10());

					long idParcela = getSaldoCredorSimplificado(c, c.getListContratoCobrancaParcelasInvestidor10(), 1);
					
					// tem parcelas baixadas e abertas
					if (idParcela > 0) {
						int numeroParcela = 0;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor10()) {
							if (idParcela == parcela.getId()) {
								investidor.setSaldoInvestidoresAberto(parcela.getSaldoCredorAtualizado());
								investidor.setDataParcela(parcela.getDataVencimento());
								investidor.setValorParcela(parcela.getParcelaMensal());
																
								numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
							}
						}
						
						// popula o campo juros com o valor da próxima parcela		
						numeroParcela = numeroParcela + 1;
						
						for (ContratoCobrancaParcelasInvestidor parcela : c.getListContratoCobrancaParcelasInvestidor10()) {
							if (numeroParcela == Integer.valueOf(parcela.getNumeroParcela())) {
								investidor.setValorJuros(parcela.getJuros());
							}								
						}
					} else {
						// não tem parcelas baixadas
						if (idParcela == 0) {
							if (c.getListContratoCobrancaParcelasInvestidor10().size() > 0) {
								investidor.setSaldoInvestidoresAberto(c.getListContratoCobrancaParcelasInvestidor10().get(0).getSaldoCredorAtualizado());
								investidor.setDataParcela(c.getListContratoCobrancaParcelasInvestidor10().get(0).getDataVencimento());
								investidor.setValorParcela(c.getListContratoCobrancaParcelasInvestidor10().get(0).getParcelaMensal());
								
								if (c.getListContratoCobrancaParcelasInvestidor10().size() > 1) { 
									investidor.setValorJuros(c.getListContratoCobrancaParcelasInvestidor10().get(1).getJuros());
								}
							}
						}
					}
					
					if (idParcela > 0) {
						listInvestidores.add(investidor);
					}
				}
				
				if (listInvestidores.size() > 0) {
					relatorioContabilidadeEmAbertoAux.setListInvestidores(listInvestidores);
					this.relatorioContabilidadeEmAberto.add(relatorioContabilidadeEmAbertoAux);
				}								
			}			
		}
		
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Contabilidade: Posição atual gerada com sucesso!!!", ""));
	}
	
	public BigDecimal getSaldoCredor(ContratoCobranca contrato, List<ContratoCobrancaParcelasInvestidor> contratoCobrancaParcelasInvestidor, int posicaoInvestidor) {
		BigDecimal saldoCredor = BigDecimal.ZERO;
		int countBaixados = 0;
		
		// pega sempre o saldo da última parcela baixada
		for (ContratoCobrancaParcelasInvestidor parcelaInvestidor : contratoCobrancaParcelasInvestidor) {			
			if (parcelaInvestidor.isBaixado()) {
				countBaixados = countBaixados + 1;
				saldoCredor = parcelaInvestidor.getSaldoCredorAtualizado();
			}
		}
		
		// Se o count de parcelas baixadas é igual ao número de parcelas, está quitado
		if (countBaixados == contratoCobrancaParcelasInvestidor.size()) {
			saldoCredor = BigDecimal.ZERO;
		} 
		
		// se o count de parcelas baixadas é 0 e a lista de parcelas é maior que zero
		// pega o valor recebedor final
		if (countBaixados == 0 && contratoCobrancaParcelasInvestidor.size() > 0) {
			switch(posicaoInvestidor) {
			  case 1:
				  if (contrato.getVlrFinalRecebedor1() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor1();
				  } else {
					  
				  }				  
			    break;
			  case 2:
				  if (contrato.getVlrFinalRecebedor2() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor2();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 3:
				  if (contrato.getVlrFinalRecebedor3() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor3();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 4:
				  if (contrato.getVlrFinalRecebedor4() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor4();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;  
			  case 5:
				  if (contrato.getVlrFinalRecebedor5() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor5();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 6:
				  if (contrato.getVlrFinalRecebedor6() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor6();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 7:
				  if (contrato.getVlrFinalRecebedor7() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor7();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 8:
				  if (contrato.getVlrFinalRecebedor8() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor8();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 9:
				  if (contrato.getVlrFinalRecebedor9() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor9();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  case 10:
				  if (contrato.getVlrFinalRecebedor10() != null) {
					  saldoCredor = contrato.getVlrFinalRecebedor10();
				  } else {
					  saldoCredor = BigDecimal.ZERO;
				  }	
			    break;
			  default:
				  saldoCredor = BigDecimal.ZERO;
			}
		}
	
		return saldoCredor;
	}
	
	public long getSaldoCredorSimplificado(ContratoCobranca contrato, List<ContratoCobrancaParcelasInvestidor> contratoCobrancaParcelasInvestidor, int posicaoInvestidor) {
		int countBaixados = 0;
		
		long idParcela = 0;
		
		// pega sempre o saldo da última parcela baixada
		for (ContratoCobrancaParcelasInvestidor parcelaInvestidor : contratoCobrancaParcelasInvestidor) {			
			if (parcelaInvestidor.isBaixado()) {
				countBaixados = countBaixados + 1;
				idParcela = parcelaInvestidor.getId();
			}
		}
		
		// Se o count de parcelas baixadas é igual ao número de parcelas, está quitado
		// id == -1
		if (countBaixados == contratoCobrancaParcelasInvestidor.size()) {
			idParcela = -1;
		} 
		
		// se o count de parcelas baixadas é 0 e a lista de parcelas é maior que zero
		// pega o valor recebedor final
		// id == 0
	
		return idParcela;
	}

	public ContratoCobrancaDetalhes calculaJurosParcela(BigDecimal juros, BigDecimal multa, ContratoCobrancaDetalhes parcela) {
		
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = dataHoje.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(dataHoje.getTime());
	    try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    String auxDataVencimentoStr = "";
	    Date auxDataVencimento = null;
		if (parcela.getDataVencimentoAtual() != null) {
			auxDataVencimentoStr = sdf.format(parcela.getDataVencimentoAtual());
			auxDataVencimento = parcela.getDataVencimentoAtual();
	    } else {
	    	auxDataVencimentoStr = sdf.format(parcela.getDataVencimento());
	    	auxDataVencimento = parcela.getDataVencimento();
	    }

	    try {
			auxDataVencimento = sdf.parse(auxDataVencimentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (auxDataVencimento.before(auxDataPagamento) && !parcela.isParcelaPaga()) {				
			parcela.setParcelaVencida(true);

			// calcula coluna valor atualizado	
			ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;
			
			if (parcela.getVlrJuros().compareTo(BigDecimal.ZERO) == 0) {
				contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(
						auxDataVencimento, auxDataPagamento,
						parcela.getVlrParcela(), BigDecimal.valueOf(1.00), multa);
			} else {
				contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(
						auxDataVencimento, auxDataPagamento,
						parcela.getVlrParcela(), juros, multa);
			}
			
			if (!parcela.isParcelaPaga()) {
				if (parcela.getListContratoCobrancaDetalhesParcial().size() > 0) {
					contratoCobrancaUtilsMB.recalculaValorSemMulta();
				} else {
					contratoCobrancaUtilsMB.recalculaValor();
				}
				parcela.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
			} else {
				parcela.setVlrParcelaAtualizada(null);
			}
		}
		
		return parcela;
	}
	
	/****
	 * verifica se a parcela está vencida
	 * 
	 */
	public boolean isParcelaVencida(Date dataVencimento) {
		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataHoje = dataHoje.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataHojeStr = sdf.format(auxDataHoje.getTime());
	    try {
	    	auxDataHoje = sdf.parse(auxDataHojeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String auxDataVencimentoStr = "";
		Date auxDataVencimento = null;

		auxDataVencimentoStr = sdf.format(dataVencimento);
		auxDataVencimento = dataVencimento;

		try {
			auxDataVencimento = sdf.parse(auxDataVencimentoStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
		if (auxDataVencimento.before(auxDataHoje)) {				
			return true;
		} else {
			return false;
		}
	}
	
	/*****
	 * Retorna somente os investidores que não são Galleria.
	 * @param investidor
	 * @return
	 */
	
	public PagadorRecebedor pagadorRecebedorNaoGalleria(PagadorRecebedor investidor) {
		if (investidor != null) {
			if (investidor.getId() != 15 && investidor.getId() != 34 &&
					investidor.getId() != 14 && investidor.getId() != 182 &&
							investidor.getId() != 417) {
				return investidor;
			}
		}

		return null;
	}
	
	public void getXLSPosicaoAtual() throws IOException {
		this.pathContrato = "";
		this.nomeContrato = "";
		this.file = null;
		this.xlsGerado = false;
		
		ParametrosDao pDao = new ParametrosDao(); 
		this.pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString();
		this.nomeContrato = "Relatório Posição Atual.xlsx";  	

		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
		dataHoje.set(Calendar.MINUTE, 0);  
		dataHoje.set(Calendar.SECOND, 0);  
		dataHoje.set(Calendar.MILLISECOND, 0);
		
		//dataHoje.add(Calendar.DAY_OF_MONTH, 1);
		
		String excelFileName = this.pathContrato + this.nomeContrato;//name of excel file

		String sheetName = "Resultado";//name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName) ;
		sheet.setDefaultColumnWidth(25);

		// Style para cabeçalho
		XSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = wb.createFont();
		font.setBold(true);
		cell_style.setFont(font);
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);      

		//iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Total em aberto no Contrato (R$)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Debêntures");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Debêntures");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Total em aberto Investidores (R$)");
		cell.setCellStyle(cell_style);
	
		sheet.addMergedRegion(CellRangeAddress.valueOf("E1:F1"));
		
		// cria estilo para dados em geral
		cell_style = wb.createCellStyle();
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);  

		// cria estilo especifico para coluna type numérico
		CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(HorizontalAlignment.CENTER);
		numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numericStyle.setBorderBottom(BorderStyle.THIN);
		numericStyle.setBorderTop(BorderStyle.THIN);
		numericStyle.setBorderRight(BorderStyle.THIN);
		numericStyle.setBorderLeft(BorderStyle.THIN);
		numericStyle.setWrapText(true);
		// cria a formatação para moeda
		CreationHelper ch = wb.getCreationHelper();                			
		numericStyle.setDataFormat(ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

		// cria estilo especifico para coluna type Date
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setAlignment(HorizontalAlignment.CENTER);
		dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dateStyle.setBorderBottom(BorderStyle.THIN);
		dateStyle.setBorderTop(BorderStyle.THIN);
		dateStyle.setBorderRight(BorderStyle.THIN);
		dateStyle.setBorderLeft(BorderStyle.THIN);
		dateStyle.setWrapText(true);
		// cria a formatação para Date
		dateStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("m/d/yy"));

		for (RelatorioContabilidadeEmAberto record : this.relatorioContabilidadeEmAberto) {
			countLine ++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());	

			//Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataContrato());

			//Pagador
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);

			if (record.getPagador() != null) {
				cell.setCellValue(record.getPagador().getNome());	
			}	
			
			cell = row.createCell(3);	
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getSaldoParcelasAberto()).doubleValue());
			
			cell = row.createCell(6);	
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getSaldoInvestidoresAberto()).doubleValue());
						
			int countInvestidores = 0;
			
			for (RelatorioContabilidadeInvestidor investidor : record.getListInvestidores()) {
				if (countInvestidores > 0) {
					countLine ++;
					row = sheet.createRow(countLine);
				}
				
				// Investidor
				cell = row.createCell(4);
				cell.setCellStyle(cell_style);
				cell.setCellValue(investidor.getInvestidor().getNome());	
				
				// Saldo aberto Investidor
				cell = row.createCell(5);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				
				if (investidor.getSaldoInvestidoresAberto() != null) {
					cell.setCellValue(((BigDecimal) investidor.getSaldoInvestidoresAberto()).doubleValue());
				} else {
					cell.setCellValue((BigDecimal.ZERO).doubleValue()); 
				}
				
				countInvestidores = countInvestidores + 1;
			}

			// se a lista de investidores é maior que zero, mescla as colunas estáticas
			if (record.getListInvestidores().size() > 1) {
				String area = "A" + ((countLine + 1) - (countInvestidores - 1)) + ":A" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "B" + ((countLine + 1) - (countInvestidores - 1)) + ":B" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "C" + ((countLine + 1) - (countInvestidores - 1)) + ":C" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "D" + ((countLine + 1) - (countInvestidores - 1)) + ":D" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "G" + ((countLine + 1) - (countInvestidores - 1)) + ":G" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
			} 
			
			if (record.getListInvestidores().size() == 0) {
				// senão, colocar borda coloca um conteudo vazio mescla as celulas
				cell = row.createCell(4);
				cell.setCellStyle(cell_style);
				cell.setCellValue("--");
				
				cell = row.createCell(5);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue((BigDecimal.ZERO).doubleValue());
				
				cell = row.createCell(6);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue((BigDecimal.ZERO).doubleValue());
			}
			
			// SET border em celulas mescladas
			List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
			for (CellRangeAddress rangeAddress : mergedRegions) {
			  RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
			}
		}
			
		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		//write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.xlsGerado = true;
	}
	
	public void getXLSPosicaoRetroativa() throws IOException {
		this.pathContrato = "";
		this.nomeContrato = "";
		this.file = null;
		this.xlsGerado = false;
		
		ParametrosDao pDao = new ParametrosDao(); 
		this.pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString();
		this.nomeContrato = "Relatório Posição Retroativa.xlsx";  	

		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
		dataHoje.set(Calendar.MINUTE, 0);  
		dataHoje.set(Calendar.SECOND, 0);  
		dataHoje.set(Calendar.MILLISECOND, 0);
		
		//dataHoje.add(Calendar.DAY_OF_MONTH, 1);
		
		String excelFileName = this.pathContrato + this.nomeContrato;//name of excel file

		String sheetName = "Resultado";//name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName) ;
		sheet.setDefaultColumnWidth(25);

		// Style para cabeçalho
		XSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = wb.createFont();
		font.setBold(true);
		cell_style.setFont(font);
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);      

		//iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Responsável");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Data Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Valor Parcela");
		cell.setCellStyle(cell_style);
		
		// cria estilo para dados em geral
		cell_style = wb.createCellStyle();
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);  

		// cria estilo especifico para coluna type numérico
		CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(HorizontalAlignment.CENTER);
		numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numericStyle.setBorderBottom(BorderStyle.THIN);
		numericStyle.setBorderTop(BorderStyle.THIN);
		numericStyle.setBorderRight(BorderStyle.THIN);
		numericStyle.setBorderLeft(BorderStyle.THIN);
		numericStyle.setWrapText(true);
		// cria a formatação para moeda
		CreationHelper ch = wb.getCreationHelper();                			
		numericStyle.setDataFormat(ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

		// cria estilo especifico para coluna type Date
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setAlignment(HorizontalAlignment.CENTER);
		dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dateStyle.setBorderBottom(BorderStyle.THIN);
		dateStyle.setBorderTop(BorderStyle.THIN);
		dateStyle.setBorderRight(BorderStyle.THIN);
		dateStyle.setBorderLeft(BorderStyle.THIN);
		dateStyle.setWrapText(true);
		// cria a formatação para Date
		dateStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("m/d/yy"));

		for (RelatorioContabilidadePosicaoRetroativaInvestidor record : this.relatorioContabilidadePosicaoRetroativaInvestidor) {
			countLine ++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContrato().getNumeroContrato());	

			//Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getContrato().getDataContrato());

			
			//Responsavel
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);

			if (record.getContrato().getResponsavel() != null) {
				cell.setCellValue(record.getContrato().getResponsavel().getNome());	
			}	
			
			//Pagador
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);

			if (record.getContrato().getPagador() != null) {
				cell.setCellValue(record.getContrato().getPagador().getNome());	
			}	
						
			int countInvestidores = 0;
			
			for (ContratoCobrancaParcelasInvestidor investidor : record.getListContratoCobrancaParcelasInvestidor()) {
				if (countInvestidores > 0) {
					countLine ++;
					row = sheet.createRow(countLine);
				}
				
				// Investidor
				cell = row.createCell(4);
				cell.setCellStyle(cell_style);
				
				if (investidor.getInvestidor() != null) {
					cell.setCellValue(investidor.getInvestidor().getNome());	
				}					
				
				// Data vencimento
				cell = row.createCell(5);	
				cell.setCellStyle(dateStyle);
								
				if (investidor.getDataVencimento() != null) {
					cell.setCellValue(investidor.getDataVencimento());
				}
				
				// Valor Parcela
				cell = row.createCell(6);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				
				if (investidor.getParcelaMensal() != null) {
					cell.setCellValue(((BigDecimal) investidor.getParcelaMensal()).doubleValue());
				} else {
					cell.setCellValue((BigDecimal.ZERO).doubleValue()); 
				}
			
				countInvestidores = countInvestidores + 1;
			}

			// se a lista de investidores é maior que zero, mescla as colunas estáticas
			if (record.getListContratoCobrancaParcelasInvestidor().size() > 1) {
				String area = "A" + ((countLine + 1) - (countInvestidores - 1)) + ":A" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "B" + ((countLine + 1) - (countInvestidores - 1)) + ":B" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "C" + ((countLine + 1) - (countInvestidores - 1)) + ":C" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "D" + ((countLine + 1) - (countInvestidores - 1)) + ":D" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
			} 
			
			if (record.getListContratoCobrancaParcelasInvestidor().size() == 0) {
				// senão, colocar borda coloca um conteudo vazio mescla as celulas
				cell = row.createCell(4);
				cell.setCellStyle(cell_style);
				cell.setCellValue("--");
				
				cell = row.createCell(5);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue((BigDecimal.ZERO).doubleValue());
				
				cell = row.createCell(6);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue((BigDecimal.ZERO).doubleValue());
			}
			
			// SET border em celulas mescladas
			List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
			for (CellRangeAddress rangeAddress : mergedRegions) {
			  RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
			}
		}
			
		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		//write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.xlsGerado = true;
	}
	
	public void getXLSPosicaoAtualSimplificado() throws IOException {
		this.pathContrato = "";
		this.nomeContrato = "";
		this.file = null;
		this.xlsGerado = false;
		
		ParametrosDao pDao = new ParametrosDao(); 
		this.pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString();
		this.nomeContrato = "Relatório Posição Atual Simplificado.xlsx";  	

		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
		dataHoje.set(Calendar.MINUTE, 0);  
		dataHoje.set(Calendar.SECOND, 0);  
		dataHoje.set(Calendar.MILLISECOND, 0);
		
		//dataHoje.add(Calendar.DAY_OF_MONTH, 1);
		
		String excelFileName = this.pathContrato + this.nomeContrato;//name of excel file

		String sheetName = "Resultado";//name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName) ;
		sheet.setDefaultColumnWidth(25);

		// Style para cabeçalho
		XSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = wb.createFont();
		font.setBold(true);
		cell_style.setFont(font);
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);      

		//iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Data Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Valor Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Juros");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Saldo");
		cell.setCellStyle(cell_style);
		
		// cria estilo para dados em geral
		cell_style = wb.createCellStyle();
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);  

		// cria estilo especifico para coluna type numérico
		CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(HorizontalAlignment.CENTER);
		numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numericStyle.setBorderBottom(BorderStyle.THIN);
		numericStyle.setBorderTop(BorderStyle.THIN);
		numericStyle.setBorderRight(BorderStyle.THIN);
		numericStyle.setBorderLeft(BorderStyle.THIN);
		numericStyle.setWrapText(true);
		// cria a formatação para moeda
		CreationHelper ch = wb.getCreationHelper();                			
		numericStyle.setDataFormat(ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

		// cria estilo especifico para coluna type Date
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setAlignment(HorizontalAlignment.CENTER);
		dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dateStyle.setBorderBottom(BorderStyle.THIN);
		dateStyle.setBorderTop(BorderStyle.THIN);
		dateStyle.setBorderRight(BorderStyle.THIN);
		dateStyle.setBorderLeft(BorderStyle.THIN);
		dateStyle.setWrapText(true);
		// cria a formatação para Date
		dateStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("m/d/yy"));

		for (RelatorioContabilidadeEmAberto record : this.relatorioContabilidadeEmAberto) {
			countLine ++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());	

			//Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataContrato());

			//Pagador
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);

			if (record.getPagador() != null) {
				cell.setCellValue(record.getPagador().getNome());	
			}	
						
			int countInvestidores = 0;
			
			for (RelatorioContabilidadeInvestidor investidor : record.getListInvestidores()) {
				if (countInvestidores > 0) {
					countLine ++;
					row = sheet.createRow(countLine);
				}
				
				// Investidor
				cell = row.createCell(3);
				cell.setCellStyle(cell_style);
				cell.setCellValue(investidor.getInvestidor().getNome());	
				
				// Data vencimento
				cell = row.createCell(4);	
				cell.setCellStyle(dateStyle);
								
				if (investidor.getDataParcela() != null) {
					cell.setCellValue(investidor.getDataParcela());
				}
				
				// Valor Parcela
				cell = row.createCell(5);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				
				if (investidor.getValorParcela() != null) {
					cell.setCellValue(((BigDecimal) investidor.getValorParcela()).doubleValue());
				} else {
					cell.setCellValue((BigDecimal.ZERO).doubleValue()); 
				}
				
				//Valor Juros
				cell = row.createCell(6);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				
				if (investidor.getValorJuros() != null) {
					cell.setCellValue(((BigDecimal) investidor.getValorJuros()).doubleValue());
				} else {
					cell.setCellValue((BigDecimal.ZERO).doubleValue()); 
				}
				
				// Saldo aberto Investidor
				cell = row.createCell(7);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				
				if (investidor.getSaldoInvestidoresAberto() != null) {
					cell.setCellValue(((BigDecimal) investidor.getSaldoInvestidoresAberto()).doubleValue());
				} else {
					cell.setCellValue((BigDecimal.ZERO).doubleValue()); 
				}
				
				countInvestidores = countInvestidores + 1;
			}

			// se a lista de investidores é maior que zero, mescla as colunas estáticas
			if (record.getListInvestidores().size() > 1) {
				String area = "A" + ((countLine + 1) - (countInvestidores - 1)) + ":A" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "B" + ((countLine + 1) - (countInvestidores - 1)) + ":B" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
				
				area = "C" + ((countLine + 1) - (countInvestidores - 1)) + ":C" + (countLine + 1);
				sheet.addMergedRegion(CellRangeAddress.valueOf(area));
			} 
			
			if (record.getListInvestidores().size() == 0) {
				// senão, colocar borda coloca um conteudo vazio mescla as celulas
				cell = row.createCell(4);
				cell.setCellStyle(cell_style);
				cell.setCellValue("--");
				
				cell = row.createCell(5);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue((BigDecimal.ZERO).doubleValue());
				
				cell = row.createCell(6);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue((BigDecimal.ZERO).doubleValue());
			}
			
			// SET border em celulas mescladas
			List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
			for (CellRangeAddress rangeAddress : mergedRegions) {
			  RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
			  RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
			}
		}
			
		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		//write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.xlsGerado = true;
	}
	
	/**
	 * @return the file
	 */
	public StreamedContent getFile() {
		String caminho =  this.pathContrato + this.nomeContrato;        
		String arquivo = this.nomeContrato;
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

	public List<ContratoCobranca> getListContratos() {
		return listContratos;
	}

	public void setListContratos(List<ContratoCobranca> listContratos) {
		this.listContratos = listContratos;
	}

	public List<RelatorioContabilidadeEmAberto> getRelatorioContabilidadeEmAberto() {
		return relatorioContabilidadeEmAberto;
	}

	public void setRelatorioContabilidadeEmAberto(List<RelatorioContabilidadeEmAberto> relatorioContabilidadeEmAberto) {
		this.relatorioContabilidadeEmAberto = relatorioContabilidadeEmAberto;
	}

	public String getPathContrato() {
		return pathContrato;
	}

	public void setPathContrato(String pathContrato) {
		this.pathContrato = pathContrato;
	}

	public String getNomeContrato() {
		return nomeContrato;
	}

	public void setNomeContrato(String nomeContrato) {
		this.nomeContrato = nomeContrato;
	}

	public boolean isXlsGerado() {
		return xlsGerado;
	}

	public void setXlsGerado(boolean xlsGerado) {
		this.xlsGerado = xlsGerado;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getAnoBase() {
		return anoBase;
	}

	public void setAnoBase(String anoBase) {
		this.anoBase = anoBase;
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

	public String getLabelAnoBase() {
		return labelAnoBase;
	}

	public void setLabelAnoBase(String labelAnoBase) {
		this.labelAnoBase = labelAnoBase;
	}

	public List<RelatorioContabilidadePosicaoRetroativaInvestidor> getRelatorioContabilidadePosicaoRetroativaInvestidor() {
		return relatorioContabilidadePosicaoRetroativaInvestidor;
	}

	public void setRelatorioContabilidadePosicaoRetroativaInvestidor(
			List<RelatorioContabilidadePosicaoRetroativaInvestidor> relatorioContabilidadePosicaoRetroativaInvestidor) {
		this.relatorioContabilidadePosicaoRetroativaInvestidor = relatorioContabilidadePosicaoRetroativaInvestidor;
	}
}