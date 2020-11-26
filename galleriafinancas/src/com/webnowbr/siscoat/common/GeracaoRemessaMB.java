package com.webnowbr.siscoat.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.EmpresaCobranca;
import com.webnowbr.siscoat.cobranca.db.op.EmpresaCobrancaDao;
import com.webnowbr.siscoat.common.db.model.BoletosRemessa;
import com.webnowbr.siscoat.common.op.BoletosRemessaDao;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

/** ManagedBean. */
@ManagedBean(name = "geracaoRemessaMB")
@SessionScoped
public class GeracaoRemessaMB {
	
	private StreamedContent fileRemessa;
	private boolean gerouRemessa;
	
	private boolean tipoFiltros;
	private String numContrato;
	private Date relDataContratoInicio;
	private Date relDataContratoFim;
	
	private List<BoletosRemessa> listBoletosRemessa;
	
	private List<BoletosRemessa> selectedBoletos;
	
	private BoletosRemessa objetoBoletosRemessa;
	
	private boolean consultaGerada;
		
	public GeracaoRemessaMB() {
		
	}
	
	public String clearGeracaoRemessaMB () {
		this.selectedBoletos = new ArrayList<BoletosRemessa>();
		this.listBoletosRemessa = new ArrayList<BoletosRemessa>();
		this.consultaGerada = false;
		
		this.numContrato = null;
		this.tipoFiltros = true;
		
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataInicio = Calendar.getInstance(zone, locale); 		
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		
		this.gerouRemessa = false;
		
		return "/Atendimento/Cobranca/BoletosRemessa.xhtml";
	}
	
	public void habilitaFiltros() {
		if (this.tipoFiltros) {
			this.numContrato = null;
			TimeZone zone = TimeZone.getDefault();  
			Locale locale = new Locale("pt", "BR");  
			Calendar dataInicio = Calendar.getInstance(zone, locale); 		
			this.relDataContratoInicio = dataInicio.getTime();
			this.relDataContratoFim = dataInicio.getTime();
		} else {
			this.relDataContratoInicio = null;
			this.relDataContratoFim = null;
		}			
	}
	
	 public static String retiraCaracteresEspeciais(String stringFonte)
	 {
	        String passa = stringFonte;
	        passa = passa.replaceAll("[ÂÀÁÄÃ]", "A");
	        passa = passa.replaceAll("[âãàáä]", "a");
	        passa = passa.replaceAll("[ÊÈÉË]", "E");
	        passa = passa.replaceAll("[êèéë]", "e");
	        passa = passa.replaceAll("ÎÍÌÏ", "I");
	        passa = passa.replaceAll("îíìï", "i");
	        passa = passa.replaceAll("[ÔÕÒÓÖ]", "O");
	        passa = passa.replaceAll("[ôõòóö]", "o");
	        passa = passa.replaceAll("[ÛÙÚÜ]", "U");
	        passa = passa.replaceAll("[ûúùü]", "u");
	        passa = passa.replaceAll("Ç", "C");
	        passa = passa.replaceAll("ç", "c");
	        passa = passa.replaceAll("[ýÿ]", "y");
	        passa = passa.replaceAll("Ý", "Y");
	        passa = passa.replaceAll("ñ", "n");
	        passa = passa.replaceAll("Ñ", "N");
	        passa = passa.replaceAll("[-+=*&amp;%$#@!_]", "");
	        passa = passa.replaceAll("['\"]", "");
	        passa = passa.replaceAll("[<>()\\{\\}]", "");
	        passa = passa.replaceAll("['\\\\.,()|/]", "");
	        passa = passa.replaceAll("[^!-ÿ]{1}[^ -ÿ]{0,}[^!-ÿ]{1}|[^!-ÿ]{1}", " ");
	        return passa.toUpperCase();
	    }
	
	public void gerarRemessa() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		try {
			this.fileRemessa = null;
			this.gerouRemessa = false; 
			
			BoletosRemessaDao boletosRemessaDao = new BoletosRemessaDao();
			// DATA DE GERAÇÃO DA REMESSA
			TimeZone zone = TimeZone.getDefault();  
			Locale locale = new Locale("pt", "BR");  
			Calendar dataHoje = Calendar.getInstance(zone, locale);
			
			// INICIO - REGISTRO			
			if (this.selectedBoletos.size() > 0) {				
				// INICIO - NOME DO ARQUIVO
				/*
						CBDDMM??.REM
						CB – Cobrança Bradesco
						DD – O Dia geração do arquivo
						MM – O Mês da geração do Arquivo
						?? - variáveis alfanumérico-Numéricas
						Ex.: 01, AB, A1 etc.
				*/
				
				String diaMes = String.format("%02d", dataHoje.get(Calendar.DAY_OF_MONTH));
				String mes = String.format("%02d", dataHoje.get(Calendar.MONTH) + 1);
				String ano = String.format("%02d", dataHoje.get(Calendar.YEAR) + 1);
				
				Random random = new Random();
				int sequencialInt = random.nextInt(50);
				String sequencial = String.format("%02d", sequencialInt);
				String nomeArquivo = "CB" + diaMes + mes + sequencial.substring(0, 2);			
				// FIM - NOME DO ARQUIVO
				
				// CRIA ARQUIVO
				 // Para gerar um boleto em PDF  
		        ParametrosDao pDao = new ParametrosDao(); 
				String pathRemessa = pDao.findByFilter("nome", "BOLETO_REMESSAS").get(0).getValorString();
	
		        pathRemessa = pathRemessa + nomeArquivo +".REM";
	
				FileWriter file = new FileWriter(pathRemessa);
				
				PrintWriter printFile = new PrintWriter(file);
				
				// INICIO - HEADER
				String idRegistro = "0";
				String idRemessa = "1";
				String literalRemessa = "REMESSA";
				String codigoServico = "01";
				String literalServico = StringUtils.rightPad("COBRANCA", 15, ' ');
				String codigoEmpresa = String.format("%020d", 4982832);
				String nomeEmpresa = StringUtils.rightPad("GAM ALVES COBRANCAS ME", 30, ' ');
				String numeroBanco = "237";
				String nomeBanco = StringUtils.rightPad("Bradesco", 15, ' ');
				String dataGravacaoArquivo = diaMes + mes + ano.substring(2, 4);
				String brancoHeader8 = StringUtils.rightPad("", 8, ' ');
				String idSistema = "MX";
	
				String sequencialRemessa = 	String.format("%07d", Integer.valueOf(boletosRemessaDao.ultimoSequencialRemessa()));
				String brancoHeader277 = StringUtils.rightPad("", 277, ' ');
	
				String sequencialRegistro = "000001";
						
				String valor = idRegistro + idRemessa + literalRemessa + codigoServico + literalServico + codigoEmpresa + nomeEmpresa + numeroBanco + 
						nomeBanco + dataGravacaoArquivo + brancoHeader8 + idSistema + sequencialRemessa + brancoHeader277 + sequencialRegistro;
				
				printFile.print(valor);
				// FIM - HEADER
				
				// PULA LINHA
				printFile.print("\r\n");
				
				int sequencialRegistroInt = 0;
				
				for (BoletosRemessa boletoGerado : this.selectedBoletos) {
					sequencialRegistroInt = sequencialRegistroInt + 1;
					
					String idRegistroBoleto = "1";
					String opcional = String.format("%05d", 0) + " " + String.format("%012d", 0) + " ";
					
			    	EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();
			    	EmpresaCobranca empresaCobranca = new EmpresaCobranca();
		
			    	Map<String, Object> filters = new HashMap<String, Object>();
			    	// Locação
			    	// Cobrança
			    	filters.put("sistema", boletoGerado.getSistema());
			    	
			    	List<EmpresaCobranca> listEmpresaCobranca = new ArrayList<EmpresaCobranca>();
			    	
			    	listEmpresaCobranca = empresaCobrancaDao.findByFilter(filters);
			    	
			    	// verifica se há mais de uma empresa por sistema
			    	// se sim da erro
			    	if (listEmpresaCobranca.size() > 1) {
			    		context.addMessage(null, new FacesMessage(
			    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Há mais de uma empresa de cobrança para o sistema de " + boletoGerado.getSistema() + "!", ""));
			    	}
			    	if (listEmpresaCobranca.size() == 0) {
			    		context.addMessage(null, new FacesMessage(
			    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Não há empresa de cobrança para o sistema de " + boletoGerado.getSistema() + "!", ""));
			    	}
			    	
			    	if (listEmpresaCobranca.size() == 1) {
			    		empresaCobranca = listEmpresaCobranca.get(0);
			    	}
				
					String beneficiario = "0" + "009" + String.format("%05d", Integer.parseInt(empresaCobranca.getAgencia())) + 
							String.format("%07d", Integer.parseInt(empresaCobranca.getCodigoBeneficiario())) + String.format("%01d", Integer.parseInt(empresaCobranca.getDigitoBeneficiario()));
					
					// Controle Participante 25 caracteres
					// 000 + Contrato + Parcela
					String controleParticipante = String.format("%023d", Integer.valueOf(boletoGerado.getNumeroContrato())) + String.format("%02d", Integer.valueOf(boletoGerado.getParcela()));
					
					String codigoBancoDebito = "000";
					String temMulta = "2";
					String multa = "0002";
					
					// DIGITO VERIFICADOR NOSSO NUMERO
					// CONTRATO + PARCELA = 11 CARACTER
					String nossoNumero = String.format("%09d", Integer.valueOf(boletoGerado.getNumeroContrato())) + String.format("%02d", Integer.valueOf(boletoGerado.getParcela()));
					
					// CALCULO DIGITO
					int digitoVerificador = (0*2) + (9*7) + // calculo sobre a carteira
					(Integer.valueOf(nossoNumero.substring(0, 1)) * 6) +
					(Integer.valueOf(nossoNumero.substring(1, 2)) * 5) +
					(Integer.valueOf(nossoNumero.substring(2, 3)) * 4) +
					(Integer.valueOf(nossoNumero.substring(3, 4)) * 3) +
					(Integer.valueOf(nossoNumero.substring(4, 5)) * 2) +
					(Integer.valueOf(nossoNumero.substring(5, 6)) * 7) +
					(Integer.valueOf(nossoNumero.substring(6, 7)) * 6) +
					(Integer.valueOf(nossoNumero.substring(7, 8)) * 5) +
					(Integer.valueOf(nossoNumero.substring(8, 9)) * 4) +
					(Integer.valueOf(nossoNumero.substring(9, 10)) * 3) +
					(Integer.valueOf(nossoNumero.substring(10, 11)) * 2);
					
					digitoVerificador = digitoVerificador % 11;
					String digitoVerificadorStr = null;
					
					if (digitoVerificador == 0) {
						digitoVerificadorStr = "0";
					} else if (digitoVerificador == 1) {
						digitoVerificadorStr = "P";
					} else {
						digitoVerificadorStr = String.valueOf(11 - digitoVerificador);
					}
					
					String descontoBonificacao = String.format("%010d", 0);
					String condicaoEmissao = "2";
					String boletoDebito = "N";
					String idOperacaoBanco = StringUtils.rightPad("", 10, ' ');
					String idRateioCredito = StringUtils.rightPad("", 1, ' ');
					String enderecamentoAvisoDebito = "2";
					String branco2 = StringUtils.rightPad("", 2, ' ');
					String idOcorrencia = "01";
					
					//NUMERO DOCUMENTO
					String numeroDocumento = String.format("%08d", Integer.valueOf(boletoGerado.getNumeroContrato())) + String.format("%02d",  Integer.valueOf(boletoGerado.getParcela()));
					
					//DATA VENCIMENTO - esta com soma de 1 no mes diante da data atual
					// 6 digitos
					Calendar dataVencimento = Calendar.getInstance(zone, locale);
					dataVencimento.setTime(boletoGerado.getDtVencimento());
					
					String dtVencimento = String.format("%02d", dataVencimento.get(Calendar.DAY_OF_MONTH)) 
							+ String.format("%02d", dataVencimento.get(Calendar.MONTH)) 
							+ String.format("%02d", dataVencimento.get(Calendar.YEAR)).substring(2, 4);
		
					// VALOR DO TITULO
					// 13 digitos
					String valorTitulo = String.format("%013d", Integer.valueOf(boletoGerado.getValor().toString().replace(".", "")));
	
					String bancoEncarregado = "000";
					String agenciaDepositaria = "00000";
					String especieDocumento = "99";
					String identificacao = "N";
	
					// DATA EMISSAO BOLETO
					Calendar dataAtual = Calendar.getInstance(zone, locale);
					dataAtual.setTime(boletoGerado.getDtEmissao());
					String diaHoje = String.format("%02d", dataAtual.get(Calendar.DAY_OF_MONTH));
					String mesHoje = String.format("%02d", dataAtual.get(Calendar.MONTH));
					String anoHoje = String.format("%02d", dataAtual.get(Calendar.YEAR));
					
					String dtEmissao = diaHoje + mesHoje + anoHoje.substring(2, 4);
					
					String instrucao1 = "00";
					String instrucao2 = "00";
					
					// LUCIANO
					//TODO
					// SABER SE É SÓ VALOR OU % TB?
					// 13 CARACTERES
					String valorMora = String.format("%013d", 0);
					
					//TODO
					// MESMA DA DE VENCIMENTO DO BOLETO
					String dataLimiteDesconto = dtVencimento;
					
					String valorDesconto = String.format("%013d", 0);
					String valorIOF = String.format("%013d", 0);
					String valorAbatimento = String.format("%013d", 0);
					
					// 01 - CPF
					// 02 - CNPJ
					String idInscricaoPagador = "";
					String numeroInscricao = "";
							
					if (boletoGerado.getDocumento().length() > 14) {
						idInscricaoPagador = "02";
						numeroInscricao = boletoGerado.getDocumento().replace(".", "").replace("/", "").replace("-", "");
					} else {
						idInscricaoPagador = "01";
						numeroInscricao = StringUtils.leftPad(boletoGerado.getDocumento().replace(".", "").replace("-", ""), 14, ' ');
					}
	
					// NOME DO PAGADOR
					String nomePagador = StringUtils.rightPad(retiraCaracteresEspeciais(boletoGerado.getNomeSacado()), 40, ' ');
					String enderecoPagador = StringUtils.rightPad(retiraCaracteresEspeciais(boletoGerado.getEndereco()), 40, ' ');
					
					String mensagem1_12 = StringUtils.rightPad("", 12, ' ');
	
					// CEP PAGADOR
					String cepPagador = "";
					String sufixoCepPagador = "";
					
					if (boletoGerado.getCep().equals("")) {
						cepPagador = StringUtils.rightPad("", 5, ' ');
						sufixoCepPagador = StringUtils.rightPad("", 3, ' ');
					} else {
						cepPagador = boletoGerado.getCep().substring(0, 5);
						sufixoCepPagador = boletoGerado.getCep().substring(6, 9);
					}
					
					String mensagem2_60 = StringUtils.rightPad("", 60, ' ');
					
					String sequecialRegistro = String.format("%06d",sequencialRegistroInt);
					
					String registros = idRegistroBoleto + opcional + beneficiario + controleParticipante + codigoBancoDebito + temMulta +
							multa + nossoNumero + digitoVerificadorStr + descontoBonificacao + condicaoEmissao + boletoDebito + idOperacaoBanco +
							idRateioCredito + enderecamentoAvisoDebito + branco2 + idOcorrencia + numeroDocumento + dtVencimento + valorTitulo + 
							bancoEncarregado + agenciaDepositaria + especieDocumento + identificacao + dtEmissao + instrucao1 + instrucao2 + 
							valorMora + dataLimiteDesconto + valorDesconto + valorIOF + valorAbatimento + idInscricaoPagador + numeroInscricao + 
							nomePagador + enderecoPagador + mensagem1_12 + cepPagador + sufixoCepPagador + mensagem2_60 + sequecialRegistro;
					printFile.print(registros);
					
					// PULA LINHA
					printFile.print("\r\n");				
				
				}
				// FIM - REGISTROS
							
				//INICIO TRAILLER
				String idRegistroTrailler = "9";
				String branco393 = StringUtils.rightPad("", 393, ' ');
	
				//PEGAR ULTIMO SEQUENCIAL DOS REGISTROS
				String seqUltimoRegistro = String.format("%06d",sequencialRegistroInt);			
				
				String trailler = idRegistroTrailler + branco393 + seqUltimoRegistro;
				
				printFile.print(trailler);
				//FIM TRAILLER
	
				file.close();

				// disponibiliza download
		        FileInputStream stream = null;
		        
		        this.gerouRemessa = true; 
				try {
					stream = new FileInputStream(pathRemessa);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}      
				
		        this.fileRemessa = new DefaultStreamedContent(stream, pathRemessa, nomeArquivo +".REM"); 
		        
		        // atualiza status dos registros
		        for (BoletosRemessa b : this.selectedBoletos) {
		        	b.setGeradoRemessa(true);
		        	b.setDtRemessa(dataHoje.getTime());
		        	b.setNomeArquivoRemessa(nomeArquivo +".REM");
		        	
		        	boletosRemessaDao.merge(b);
		        }	
		        context.addMessage(null, new FacesMessage(
	    				FacesMessage.SEVERITY_INFO, "Geração de Remessa de Boleto Bradesco: Arquivo de remessa gerado com Sucesso! Arquivo: " + nomeArquivo +".REM", ""));
			} else {
				context.addMessage(null, new FacesMessage(
	    				FacesMessage.SEVERITY_ERROR, "Geração de Remessa de Boleto Bradesco: Não há boletos com envio pendente!", ""));
			}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Remessa de Boleto Bradesco: Erro de geração!", e.getMessage()));
		}
	}
	
	public void consultarBoletos() {
		BoletosRemessaDao boletosRemessaDao = new BoletosRemessaDao();
		
		this.consultaGerada = false; 
		
		this.listBoletosRemessa = new ArrayList<BoletosRemessa>();
		
		if (this.tipoFiltros) {
			this.listBoletosRemessa = boletosRemessaDao.consultaBoletoPeriodo(this.relDataContratoInicio, this.relDataContratoFim);
		} else {
			if (this.numContrato.length() == 4) {
				this.listBoletosRemessa = boletosRemessaDao.consultaBoletoContrato("0" + this.numContrato);
			} else {
				this.listBoletosRemessa = boletosRemessaDao.consultaBoletoContrato(this.numContrato);
			}				
		}

		if (this.listBoletosRemessa.size() > 0) {
			this.consultaGerada = false;	
		}
	}
	
	public void excluirBoleto() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		BoletosRemessaDao boletosRemessaDao = new BoletosRemessaDao();
		boletosRemessaDao.delete(this.objetoBoletosRemessa);
		this.listBoletosRemessa.remove(this.objetoBoletosRemessa);
		
		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Geração de Remessa de Boleto Bradesco: Boleto excluído com Sucesso! Contrato: " + 
						this.objetoBoletosRemessa.getNumeroContrato() + " - Parcela: " + this.objetoBoletosRemessa.getParcela() , ""));		
	}

	/**
	 * @return the fileRemessa
	 */
	public StreamedContent getFileRemessa() {
		return fileRemessa;
	}

	/**
	 * @param fileRemessa the fileRemessa to set
	 */
	public void setFileRemessa(StreamedContent fileRemessa) {
		this.fileRemessa = fileRemessa;
	}

	/**
	 * @return the gerouRemessa
	 */
	public boolean isGerouRemessa() {
		return gerouRemessa;
	}

	/**
	 * @param gerouRemessa the gerouRemessa to set
	 */
	public void setGerouRemessa(boolean gerouRemessa) {
		this.gerouRemessa = gerouRemessa;
	}

	public boolean isTipoFiltros() {
		return tipoFiltros;
	}

	public void setTipoFiltros(boolean tipoFiltros) {
		this.tipoFiltros = tipoFiltros;
	}

	public String getNumContrato() {
		return numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
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

	public List<BoletosRemessa> getListBoletosRemessa() {
		return listBoletosRemessa;
	}

	public void setListBoletosRemessa(List<BoletosRemessa> listBoletosRemessa) {
		this.listBoletosRemessa = listBoletosRemessa;
	}

	public boolean isConsultaGerada() {
		return consultaGerada;
	}

	public void setConsultaGerada(boolean consultaGerada) {
		this.consultaGerada = consultaGerada;
	}

	public List<BoletosRemessa> getSelectedBoletos() {
		return selectedBoletos;
	}

	public void setSelectedBoletos(List<BoletosRemessa> selectedBoletos) {
		this.selectedBoletos = selectedBoletos;
	}

	public BoletosRemessa getObjetoBoletosRemessa() {
		return objetoBoletosRemessa;
	}

	public void setObjetoBoletosRemessa(BoletosRemessa objetoBoletosRemessa) {
		this.objetoBoletosRemessa = objetoBoletosRemessa;
	}
}
