package com.webnowbr.siscoat.cobranca.faciltech;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
	
	//POST RegistroDePessoa: {"IdPessoa":"0000000009"}
	
	public static void main(String[] args) throws Exception {
		String baseServiceUrl = "https://wscredhomogalleria.facilinformatica.com.br";
		String loginOperadorDeComunicacaoServerToServer = "BACKOFFICE";

		serverToServerTest(baseServiceUrl, loginOperadorDeComunicacaoServerToServer);
	}
	
	public static void serverToServerTest(
			String baseServiceUrl,
			String loginOperadorDeComunicacaoServerToServer) throws Exception {
		
		/// 1) Preparacao do "PemSignature", para realizar as assinaturas de seguranca, atraves dos certificados PEM.
		/// 1.a) Estes dois certificados (publico e privado) devem ser gerados localmente pelo cliente, via OpenSSL.
		/// 1.b) O primeiro certificado (CERTIFICATE) e' publico e deve ser associado a um usuario criado para
		///      realizar a a comunicacao de servidor para servidor (exemplo "APP_X"). Voce deve informar o
		///      certificado publico para a FacilTech para que nosso suporte realize a associacao do certificado
		///      publico ao devido usuario (este usuario deve ter permissoes especiais para esta finalidade).
		/// 1.c) O segundo certificado (PRIVATE KEY) e' de acesso restrito da aplicacao. Armazene em local seguro
		///      e jamais repasse ou informe-o para terceiros. A segurança de acesso ao sistema depende disso.
		/// 1.d) Caso a segurança do certificado privado fique comprometida. Deve-se gerar um novo par de
		//       e então atualizar a associacao do certificado publico ao usuario operador de comunicacao. 
		PemSignature pemSignature = new PemSignature(
				"-----BEGIN CERTIFICATE-----\r\n"
				+ "MIIBpzCCAU2gAwIBAgIUAhwpfgusukh0OS4sonkGtC00lZowCgYIKoZIzj0EAwIw\r\n"
				+ "KTELMAkGA1UEBhMCQlIxCzAJBgNVBAgMAlBSMQ0wCwYDVQQKDARUZXN0MB4XDTI0\r\n"
				+ "MDIyMjExMzEyMFoXDTI1MDIyMTExMzEyMFowKTELMAkGA1UEBhMCQlIxCzAJBgNV\r\n"
				+ "BAgMAlBSMQ0wCwYDVQQKDARUZXN0MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE\r\n"
				+ "30uMXAOjskEwkGZrYo9g4PHOIFCNJ2lkma99LyRqHExE//n7sNdcecWl8bUPEvL3\r\n"
				+ "sJS6j/xMoVAs4XzTSB1pHaNTMFEwHQYDVR0OBBYEFB3/lDv9Athu9bRKGjiYRJcO\r\n"
				+ "svzAMB8GA1UdIwQYMBaAFB3/lDv9Athu9bRKGjiYRJcOsvzAMA8GA1UdEwEB/wQF\r\n"
				+ "MAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhANAo31Lei+e75lt0xTtAHS+lZKQIkO8H\r\n"
				+ "4YxYUVPHh4duAiAXk65ccrOu/ldFXbfdFDunF79SQ8+kugfW4LODVKpM4w==\r\n"
				+ "-----END CERTIFICATE-----\r\n",
				"-----BEGIN PRIVATE KEY-----\r\n"
				+ "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgj6VnQLu4uag70rLX\r\n"
				+ "TRU+zNvpD6Jzt7NmDbnYc89CWtqhRANCAATfS4xcA6OyQTCQZmtij2Dg8c4gUI0n\r\n"
				+ "aWSZr30vJGocTET/+fuw11x5xaXxtQ8S8vewlLqP/EyhUCzhfNNIHWkd\r\n"
				+ "-----END PRIVATE KEY-----");
		
		/// 2) Preparacao do "WsCredApiServerToServer", para realizar as chamadas de API
		WsCredApiServerToServer wsCredApiServerToServer = new WsCredApiServerToServer(
				baseServiceUrl, loginOperadorDeComunicacaoServerToServer, pemSignature);
		
		/// 3) Exemplo de chamada de API (GET), informando um usuario operador do sistema como usuario autenticado,
		///    alem de um usuario (cooperado ou associado) de impersonalizacao.
		/// 3.1) Neste exemplo, o usuário (operador do sistema) esta realizando uma consulta aos
		//       dados do cooperado/associado identificado por "018303-2" (poderia ser o CPF do aooperado/associado).
		//JSONObject respostaDeResumoDePessoa = wsCredApiServerToServer.get(
		//		null, "018303-2", "/facweb.svc/resumo");
		//System.out.printf("GET ResumoDePessoa: %s\r\n", respostaDeResumoDePessoa);
		
		/// 4) Exemplo de chamada de API (POST), informando um usuario operador do sistema como usuario autenticado
		//OK PESSOAS - JSONObject body = buildRequisicaoDeExemploDeRegistroDePessoa();

		//OK PESSOAS - JSONObject respostaDeRegistroDePessoa = wsCredApiServerToServer.post(
		//OK PESSOAS - 		null, null, "/RegistroDePessoa.svc/pessoas", body);
		//OK PESSOAS - System.out.printf("POST RegistroDePessoa: %s\r\n", respostaDeRegistroDePessoa);
		
		//OK CONTRATO - JSONObject body = buildRequisicaoInserirContratoBNPL();
		
		//OK CONTRATO - JSONObject respostaInserirContratoBNPL = wsCredApiServerToServer.post(
		//OK CONTRATO - 		null, null, "/Emprestimo.svc/InserirContratoDeEmprestimoBNPL", body);
		//OK CONTRATO - System.out.printf("POST InserirContratoDeEmprestimoBNP: %s\r\n", respostaInserirContratoBNPL);
		
		JSONObject bodyContrato = buildRequisicaoInserirContratoBNPL();
		
		JSONObject body = buildRequisicaoInserirImovelComoGarantiaNoEmprestimo();

		JSONObject respostaInserirImovelComoGarantiaNoEmprestimo = wsCredApiServerToServer.post(
				null, null, "/Emprestimo.svc/InserirImovelComoGarantiaNoEmprestimo", body);
		System.out.printf("POST InserirImovelComoGarantiaNoEmprestimo: %s\r\n", respostaInserirImovelComoGarantiaNoEmprestimo);
	}

	private static JSONObject buildRequisicaoDeExemploDeRegistroDePessoa() {
		JSONObject result = new JSONObject();
		result.put("TipoDePessoa", "F");
		result.put("SituacaoDoAssociado", "N");
		result.put("CodigoDaAgenciaDeCredito", "1");

		JSONObject dadosDePessoaFisica = new JSONObject();

		JSONObject dadosPessoais = new JSONObject();
		dadosPessoais.put("CPF", "312.559.048-52");
		dadosPessoais.put("Nome", "Teste Facil Pessoa Fisica Hermes");
		dadosDePessoaFisica.put("DadosPessoais", dadosPessoais);

		JSONObject enderecoResidencial = new JSONObject();
		enderecoResidencial.put("Logradouro", "Rua Ouro Preto");
		enderecoResidencial.put("Numero", "1668");
		enderecoResidencial.put("Bairro", "Santo Agostinho");
		enderecoResidencial.put("Cidade", "Belo Horizonte");
		enderecoResidencial.put("UF", "MG");
		enderecoResidencial.put("CEP", "30170-041");
		dadosDePessoaFisica.put("EnderecoResidencial", enderecoResidencial);

		result.put("DadosDePessoaFisica", dadosDePessoaFisica);

		JSONObject dadosDaEmpresa = new JSONObject();
		dadosDaEmpresa.put("CodigoDaEmpresa", "001");
		dadosDaEmpresa.put("CodigoDaUnidadeOuSetor", "001");
		result.put("DadosDaEmpresa", dadosDaEmpresa);

		return result;
	}
	
	private static JSONObject buildRequisicaoInserirImovelComoGarantiaNoEmprestimo() {
		JSONObject result = new JSONObject();

		result.put("NumeroDaProposta", "00000025-000");
		result.put("DataDeVencimentoDaGarantia", "01-03-2024");
		result.put("ClassificacaoDoImovel", 1); // ?????
		result.put("ValorDoImovel", 50000);
		result.put("EnderecoDoImovel" , "RUA HERMES TESTE, 9999");
		result.put("NomeDoCartorio" , "CARTÓRIO TESTE");
		result.put("EnderecoDoCartorio" , "RUA TESTE CARTÓRIO");
		result.put("CEPDoCartorio" , "35457-080");
		result.put("NumeroDaMatriculaNoCartorio" , "123654");
		result.put("NumeroDoRegistroDaGarantiaNoCartorio" , "456987");
		result.put("GrauDeGarantiaRegistradaNoCartorio" , 100);
		result.put("DataDaAvaliacaoDoImovel" , "01-03-2024");
		result.put("ValorDeAvaliacaoDoImovel" , 50000);
		result.put("TipoDoImovel" , 1);
		result.put("TipoDaImplantacaoDoImovel" , 1);
		result.put("EstadoDeConservacaoDoImovel" , 1);
		result.put("PadraoDeAcabamentoDoImovel" , 1);
		result.put("AreaTotalDoImovelm2" , 500);
		result.put("QuantidadeDeDormitoriosDoImovel" , 1);
		result.put("QuantidadeDeVagasDoImovel" , 1);
		result.put("AreaPrivadaDoImovelm2" , 400);

		return result;
	}	
	
	private static JSONObject buildRequisicaoInserirContratoBNPL() {
		JSONObject result = new JSONObject();
		
		result.put("CpfOuCnpj", "312.559.048-52");
		result.put("DataDeLiberacaoDoContrato", "2023-09-30");
		result.put("IdDaLinhaDeEmprestimo", 2); // ??
		result.put("ValorFinanciado", 100000);
		result.put("ValorDoIofDaOperacao", 2783.32);
		result.put("ValorDaCadDaOperacao", 1000);
		result.put("CetMensalDaOperacao", 1.22);
		result.put("QuantidadeDeParcelas", 24);
		result.put("VencimentoInicialDoContrato", "2023-11-24");
		result.put("VencimentoFinalDoContrato", "2025-10-24");
		result.put("DataBaseDoContrato", 24);
		result.put("Observacao", "TESTE HERMES - HARD Coded");
		result.put("CodigoDoContratoOriginal", "ContratoSISCOAT");
		
		JSONArray parcelaArray = new JSONArray();
		
		JSONObject parcela = new JSONObject();
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 1);
		parcela.put("DataDeVencimento", "2023-11-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4660.74);
		parcela.put("SaldoDevedorDaParcela", 4660.74);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 2);
		parcela.put("DataDeVencimento", "2023-12-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4614.60);
		parcela.put("SaldoDevedorDaParcela",  4614.60);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 3);
		parcela.put("DataDeVencimento", "2024-01-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4568.91);
		parcela.put("SaldoDevedorDaParcela", 4568.91);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 4);
		parcela.put("DataDeVencimento", "2024-02-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4523.67);
		parcela.put("SaldoDevedorDaParcela", 4523.67);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 5);
		parcela.put("DataDeVencimento", "2024-03-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4478.88);
		parcela.put("SaldoDevedorDaParcela", 4478.88);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 6);
		parcela.put("DataDeVencimento", "2024-04-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4434.54);
		parcela.put("SaldoDevedorDaParcela", 4434.54);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 7);
		parcela.put("DataDeVencimento", "2024-05-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4390.63);
		parcela.put("SaldoDevedorDaParcela", 4390.63);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 8);
		parcela.put("DataDeVencimento", "2024-06-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4347.16);
		parcela.put("SaldoDevedorDaParcela",  4347.16);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 9);
		parcela.put("DataDeVencimento", "2024-07-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4304.12);
		parcela.put("SaldoDevedorDaParcela",  4304.12);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 10);
		parcela.put("DataDeVencimento", "2024-08-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4261.50);
		parcela.put("SaldoDevedorDaParcela",  4261.50);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 11);
		parcela.put("DataDeVencimento", "2024-09-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4219.31);
		parcela.put("SaldoDevedorDaParcela",  4219.31);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 12);
		parcela.put("DataDeVencimento", "2024-10-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4177.53);
		parcela.put("SaldoDevedorDaParcela",  4177.53);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 13);
		parcela.put("DataDeVencimento", "2024-11-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4136.17);
		parcela.put("SaldoDevedorDaParcela",  4136.17);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 14);
		parcela.put("DataDeVencimento", "2024-12-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4095.22);
		parcela.put("SaldoDevedorDaParcela", 4095.22);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 15);
		parcela.put("DataDeVencimento", "2025-01-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4054.67);
		parcela.put("SaldoDevedorDaParcela",  4054.67);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 16);
		parcela.put("DataDeVencimento", "2025-02-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 4014.53);
		parcela.put("SaldoDevedorDaParcela",  4014.53);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 17);
		parcela.put("DataDeVencimento", "2025-03-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3974.78);
		parcela.put("SaldoDevedorDaParcela", 3974.78);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 18);
		parcela.put("DataDeVencimento", "2025-04-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3935.43);
		parcela.put("SaldoDevedorDaParcela", 3935.43);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 19);
		parcela.put("DataDeVencimento", "2025-05-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3896.46);
		parcela.put("SaldoDevedorDaParcela", 3896.46);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();

		parcela.put("NumeroDaParcela", 20);
		parcela.put("DataDeVencimento", "2025-06-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3857.88);
		parcela.put("SaldoDevedorDaParcela", 3857.88);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();

		parcela.put("NumeroDaParcela", 21);
		parcela.put("DataDeVencimento", "2025-07-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3819.68);
		parcela.put("SaldoDevedorDaParcela", 3819.68);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 22);
		parcela.put("DataDeVencimento", "2025-08-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3781.87);
		parcela.put("SaldoDevedorDaParcela", 3781.87);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 23);
		parcela.put("DataDeVencimento", "2025-09-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3744.42);
		parcela.put("SaldoDevedorDaParcela", 3744.42);
		
		parcelaArray.put(parcela);
		
		parcela = new JSONObject();
		
		parcela.put("NumeroDaParcela", 24);
		parcela.put("DataDeVencimento", "2025-10-24");
		parcela.put("ValorDaParcelaNoVencimento", 4744.97);
		parcela.put("ValorPrincipalDaParcela", 3707.30);
		parcela.put("SaldoDevedorDaParcela", 3707.30);
		
		parcelaArray.put(parcela);

		result.put("Parcelas", parcelaArray);
		
		return result;
	}
}
