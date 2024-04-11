package com.webnowbr.siscoat.cobranca.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webnowbr.siscoat.cobranca.db.model.Banco;
import com.webnowbr.siscoat.cobranca.db.op.BancoDao;
import com.webnowbr.siscoat.cobranca.vo.BancoVO;
import com.webnowbr.siscoat.cobranca.vo.ProtocolMessageResultVO;
import com.webnowbr.siscoat.cobranca.vo.ProtocolResultVO;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.CsvToExcel;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.ExcelTable;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.exception.SiscoatException;



public class BancoCentral {

	/** Logger instance. */
	private static final Log LOGGER = LogFactory.getLog(BancoCentral.class);
	
	
	private ProtocolResultVO processaArquivoBancoBancoCentral(final int codigoUsuario, final String nomeArquivo,
			final String tipoConteudo, final byte[] arquivoBanco) throws SiscoatException {

		// Gera resultado da operação
		final ProtocolResultVO result = new ProtocolResultVO();
		result.setProcessingDate(DateUtil.getDataHoraAgora());
		result.setNumberOfDocuments(0);
		result.setTotalValue(.0);
		result.setStatus((short) 0);
		result.setMensagens(new ArrayList<ProtocolMessageResultVO>(0));

		
//		if (!tipoConteudo.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//				&& !tipoConteudo.equals("application/vnd.ms-excel")) {
//			LOGGER.error("Tipo de arquivo inválido. Esperado arquivo com extensão \".xlsx\" ou \".xls\"");
//			result.addErrorMessage("Tipo de arquivo inválido. Esperado arquivo com extensão \".xlsx\" ou \".xls\"");
//			return result;
//		}
		
		if (!tipoConteudo.equals("json")) {
			LOGGER.error("Tipo de arquivo inválido. Esperado arquivo com extensão \".json\"");
			result.addErrorMessage("Tipo de arquivo inválido. Esperado arquivo com extensão \".json\"");
			return result;
		}
		

		try {
			// Prepara DAOs
//			carregaDAOs();
			
//			final BancoCentralService bancoCentralService = BusinessFactory.getService(BancoCentralService.class);

			// Processa dados de entrada
			byte[] arquivoProcesado = buscaBancos();
			
			List<BancoVO> documentosDownload = lerBanco(arquivoProcesado);
			/** Listagem de Documentos do pedido */
			List<BancoVO> agencias = null;
			List<String> erros = new ArrayList<String>();
			 
			if (documentosDownload != null) {
				agencias = documentosDownload;
			}
			
			// final String nomeDoArquivoSalvo = arquivoBancoBanco
			// .getNomeDoArquivoSalvo();
			
			
			final List<BancoVO> documentos = agencias;
			if (erros != null && erros.size() > 0) {
				for (String message :erros) {
					result.addErrorMessage(message);
				}
			} else if (documentos == null || documentos.isEmpty()) {
				result.addErrorMessage("Lista de documentos vazia");
				return result;
			} else {

				BancoDao bancoDAO = new BancoDao();
				
				result.setNumberOfDocuments(documentos.size());
				for (BancoVO banco : documentos) {
					if (banco.getCodigoBanco() == null) {
						continue;
					}
					Banco bancoBase = bancoDAO.pesquisaBanco(banco.getCnpjBase(), banco.getCodigoBanco());

					if (CommonsUtil.semValor( bancoBase.getCodigoBanco() )) {
						bancoBase = new Banco();
					} else {
						continue;
					}

					bancoBase.setCodigoBanco(banco.getCodigoBanco());
					bancoBase.setCnpjBase(banco.getCnpjBase());
					bancoBase.setNomeArquivoImpressaoBoleto(banco.getNomeArquivoImpressaoBoleto());
					bancoBase.setNomeCompleto(banco.getNomeCompleto());
					bancoBase.setNomeReduzido(banco.getNomeReduzido());
					bancoBase.setFlagInativo(false);
					result.setNumberOfDocuments(result.getNumberOfDocuments() + 1);

					// Data de registro
					// Date NOW = DateUtil.getDataHoraAgora();
					bancoDAO.update(bancoBase);

				}
			}

//			for (ProtocolMessageResultVO vo : result.getMensagens()) {
//				LoteMensagem msg = new LoteMensagem();
//				msg.setNumeroLinhaArquivo(vo.getNumeroLinhaArquivo());
//				msg.setNumeroNotaFiscal(vo.getNumeroNotaFiscal());
//				msg.setNumeroDocumento(vo.getNumeroDocumento());
//				msg.setMensagem(vo.getMensagem());
//			}

			/*
			 * if (loteValido) { result.setProtocolCode(lote.getCodigoLote()); }
			 */

		} catch (DAOException e) {
			LOGGER.error("processaArquivoPracaBloqueadaItau: " + e.getMessage());
			LOGGER.error("processaArquivoPracaBloqueadaItau: EXCEPTION", e);
			throw new SiscoatException(e.getMessage(), e);
		}
		return result;
	}

	
	public byte[] buscaBancos() throws SiscoatException {

		List<BancoVO> result = new ArrayList<>(0);

		try {
			//https://www.bcb.gov.br/pom/spb/estatistica/port/ASTR003.pdf
			//final URL url = new URL("https://www.bcb.gov.br/pom/spb/estatistica/port/ParticipantesSTRport.csv");
			final URL url = new URL("https://www.bcb.gov.br/pom/spb/estatistica/port/ParticipantesSTRport.csv");

			HttpURLConnection con = null;

			String retorno = executaDownload(con, url);

			if (!CommonsUtil.semValor(retorno)) {

				byte[] excel = CsvToExcel.convertCsvToExcel(retorno.getBytes());

//				Files.write(excel, new File("D:/newfile.xlsx"));
				return excel;
			}

		} catch (Exception ex) {

			throw new SiscoatException(
					"br.com.banicred.banisys.business.service.impl.BancoCentralServiceImpl.buscaAgencias(): "
							+ ex.getMessage());
		}

		return null;
	}
	
	
	private List<BancoVO> lerBanco(byte[] arquivoProcesado) {

		Map<String, String> defaultNamesMap = new HashMap<String, String>(0);
		// Nome do campos sempre em uppercase

		defaultNamesMap.put("?ISPB", "cnpjBase");
		defaultNamesMap.put("NOME_REDUZIDO", "nomeReduzido");
		defaultNamesMap.put("NÚMERO_CÓDIGO", "codigoBanco");
		defaultNamesMap.put("PARTICIPA_DA_COMPE", "participaCompensacao");
		defaultNamesMap.put("NOME_EXTENSO", "nomeBanco");

		ExcelTable t;
		try {
			t = new ExcelTable.Builder().build(arquivoProcesado, defaultNamesMap);
		} catch (IOException e) {
			LOGGER.error("Arquivo inválido: ", e);
//			erros.add("Arquivo inválido.");
			return null;
		}
		if (t == null) {
			LOGGER.error("Arquivo inválido.");
//			erros.add("Arquivo inválido.");
			return null;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Arquivo Praca de Bloqueada Itau carregado: " + t.size() + " linhas.");
		}

		List<BancoVO> documentos = new ArrayList<BancoVO>();

		boolean isArquivoValido = true;

		for (int row = 1; row < t.size(); row++) {

			BancoVO vo = new BancoVO();

			// Carrega dados da linha
			final Object oCnpjBase = t.getCellValue("cnpjBase", row);
			final Object oNomeReduzido = t.getCellValue("nomeReduzido", row);
			final Object oCodigoBanco = t.getCellValue("codigoBanco", row);
			final Object oNomeBanco = t.getCellValue("nomeBanco", row);

			boolean isRegistroValido = true;

			// Carrega codigo banco
			String cnpjBaseBanco = null;
			if (oCnpjBase != null) {
				cnpjBaseBanco = getExcelString(oCnpjBase, true).trim();
				if (cnpjBaseBanco == null) {
					LOGGER.error("CNPJ/CPF '" + oCnpjBase + "' de Sacado inválido no registro: " + row);
//					erros.add("CNPJ/CPF '" + oCnpjBase + "' de Sacado inválido no registro: " + row);
					isRegistroValido = false;
				}
			} else {
				LOGGER.error("Cnpj do banco inválido no registro: " + row);
//				erros.add("Cnpj do banco inválido no registro: " + row);
				isRegistroValido = false;
			}
			// Valida CPF/CNPJ
			if (cnpjBaseBanco == null) {
				LOGGER.error("CNPJ/CPF do banco nulo no registro: " + row);
				isRegistroValido = false;
			} else {
				if (cnpjBaseBanco.length() < 8) {
					cnpjBaseBanco = "00000000".substring(cnpjBaseBanco.length()).concat(cnpjBaseBanco);
				}
				vo.setCnpjBase(cnpjBaseBanco);
			}

			// Carrega Nome Reduzido Banco
			if (oNomeReduzido != null && oNomeReduzido instanceof String) {
				String nomeReduzido =((String) oNomeReduzido).trim().replace("\n", "");				
				if( nomeReduzido.length() > 70 ) {
					nomeReduzido.substring(0, 70);
				}
				
				vo.setNomeReduzido(nomeReduzido);
			} else {
				LOGGER.error("Nome Reduzido Banco inválido no registro: " + row);
//				erros.add("Nome Reduzido Banco inválido no registro: " + row);
				isRegistroValido = false;
			}

			// Carrega Codigo Banco
			if (oCodigoBanco != null) {
				Integer codigoBanco = null;
				if (oCodigoBanco instanceof Integer) {
					codigoBanco = (Integer) oCodigoBanco;
				} else {
					if (oCodigoBanco instanceof Double) {
						// Type cast double to int
						double agenciaDouble = (Double) oCodigoBanco;
						int agenciaInteger = (int) agenciaDouble;
						codigoBanco = agenciaInteger;
					} else if ((oCodigoBanco instanceof String) && ( ((String) oCodigoBanco).contains("-") ||  ((String) oCodigoBanco).equalsIgnoreCase("n/a") )) {
						if ( ((String) oCodigoBanco).equalsIgnoreCase("n/a"))
							continue;
						else
							codigoBanco = null;
					} else {
						LOGGER.error("Codigo Banco inválido no registro: " + row);
//						erros.add("Codigo Banco inválido no registro: " + row);
						isRegistroValido = false;
					}
				}
				vo.setCodigoBanco(codigoBanco);
			} else {
				LOGGER.error("Codigo Banco inválido no registro: " + row);
//				erros.add("Codigo Banco inválido no registro: " + row);
				isRegistroValido = false;
			}

			// Carrega Nome Banco
			if (oNomeBanco != null && oNomeBanco instanceof String) {
				String nomeCompleto =((String) oNomeBanco).trim().replace("\n", "");				
				if( nomeCompleto.length() > 200 ) {
					nomeCompleto.substring(0,  200);
				}
				vo.setNomeCompleto(nomeCompleto);
			} else {
				LOGGER.error("Nome Banco inválido no registro: " + row);
//				erros.add("Nome Banco inválido no registro: " + row);
				isRegistroValido = false;
			}

			if (isRegistroValido && vo.getCodigoBanco() != null) {
				documentos.add(vo);
//			} else {
//				isArquivoValido = false;
			}
		}

		return documentos;
	}
	
	private String getExcelString(Object object, boolean somenteNumero) {
		String sObject = null;

		if (object instanceof String) {
			sObject = (String) object;
		} else {
			if (object instanceof Double) {
				sObject = String.valueOf(((Double) object).longValue());
			} else {
				sObject = null;
			}
		}
		if (sObject != null && somenteNumero) {
			sObject = CommonsUtil.somenteNumeros(sObject);
		}
		return sObject;
	}
	
	private String executaDownload(HttpURLConnection con, URL url) throws IOException {

		con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("User-Agent", "my-agent");
		con.connect();
//		con.setRequestMethod("GET");
//		con.setDoOutput(true);
//		con.setInstanceFollowRedirects(false);
////		con.setRequestProperty("Content-Type", "application/json");
//		con.setUseCaches(false);
//		con.setRequestProperty("User-Agent",
		// "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2)
		// Gecko/20100316 Firefox/3.6.2");

//		System.setProperty("http.agent", "");

//		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//		int responseCode = httpConn.getResponseCode();
//		if (responseCode == HttpURLConnection.HTTP_OK) {

			InputStream inputStream = con.getInputStream();
					
			StringBuilder textBuilder = new StringBuilder();

			try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				int c = 0;
				while ((c = reader.read()) != -1) {
					textBuilder.append((char) c);
				}
			}

			return textBuilder.toString();
//		}
//		return null;
	}
}
