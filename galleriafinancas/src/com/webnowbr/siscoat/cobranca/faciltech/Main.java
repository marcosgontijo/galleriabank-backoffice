package com.webnowbr.siscoat.cobranca.faciltech;

import org.json.JSONObject;

public class Main {
	
	public static void main(String[] args) throws Exception {
		String baseServiceUrl = "https://wscredhomogalleria.facilinformatica.com.br/wcf/rest";
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
				"-----BEGIN CERTIFICATE-----MIIBpzCCAU2gAwIBAgIUAhwpfgusukh0OS4sonkGtC00lZowCgYIKoZIzj0EAwIwKTELMAkGA1UEBhMCQlIxCzAJBgNVBAgMAlBSMQ0wCwYDVQQKDARUZXN0MB4XDTI0MDIyMjExMzEyMFoXDTI1MDIyMTExMzEyMFowKTELMAkGA1UEBhMCQlIxCzAJBgNVBAgMAlBSMQ0wCwYDVQQKDARUZXN0MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE30uMXAOjskEwkGZrYo9g4PHOIFCNJ2lkma99LyRqHExE//n7sNdcecWl8bUPEvL3sJS6j/xMoVAs4XzTSB1pHaNTMFEwHQYDVR0OBBYEFB3/lDv9Athu9bRKGjiYRJcOsvzAMB8GA1UdIwQYMBaAFB3/lDv9Athu9bRKGjiYRJcOsvzAMA8GA1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhANAo31Lei+e75lt0xTtAHS+lZKQIkO8H4YxYUVPHh4duAiAXk65ccrOu/ldFXbfdFDunF79SQ8+kugfW4LODVKpM4w==-----END CERTIFICATE-----",
				"-----BEGIN PRIVATE KEY-----MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgj6VnQLu4uag70rLXTRU+zNvpD6Jzt7NmDbnYc89CWtqhRANCAATfS4xcA6OyQTCQZmtij2Dg8c4gUI0naWSZr30vJGocTET/+fuw11x5xaXxtQ8S8vewlLqP/EyhUCzhfNNIHWkd-----END PRIVATE KEY-----");
		
		/// 2) Preparacao do "WsCredApiServerToServer", para realizar as chamadas de API
		WsCredApiServerToServer wsCredApiServerToServer = new WsCredApiServerToServer(
				baseServiceUrl, loginOperadorDeComunicacaoServerToServer, pemSignature);
		
		/// 3) Exemplo de chamada de API (GET), informando um usuario operador do sistema como usuario autenticado,
		///    alem de um usuario (cooperado ou associado) de impersonalizacao.
		/// 3.1) Neste exemplo, o usuário (operador do sistema) esta realizando uma consulta aos
		//       dados do cooperado/associado identificado por "018303-2" (poderia ser o CPF do aooperado/associado).
		JSONObject respostaDeResumoDePessoa = wsCredApiServerToServer.get(
				null, "018303-2", "/facweb.svc/resumo");
		System.out.printf("GET ResumoDePessoa: %s\r\n", respostaDeResumoDePessoa);
		
		/// 4) Exemplo de chamada de API (POST), informando um usuario operador do sistema como usuario autenticado
		JSONObject body = buildRequisicaoDeExemploDeRegistroDePessoa();

		JSONObject respostaDeRegistroDePessoa = wsCredApiServerToServer.post(
				null, null, "/RegistroDePessoa.svc/pessoas", body);
		System.out.printf("POST RegistroDePessoa: %s\r\n", respostaDeRegistroDePessoa);
	}

	private static JSONObject buildRequisicaoDeExemploDeRegistroDePessoa() {
		JSONObject result = new JSONObject();
		result.put("TipoDePessoa", "F");
		result.put("SituacaoDoAssociado", "N");
		result.put("CodigoDaAgenciaDeCredito", "1");

		JSONObject dadosDePessoaFisica = new JSONObject();

		JSONObject dadosPessoais = new JSONObject();
		dadosPessoais.put("CPF", "024.521.320-12");
		dadosPessoais.put("Nome", "Teste Facil Pessoa Fisica");
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
		dadosDaEmpresa.put("CodigoDaEmpresa", "046");
		dadosDaEmpresa.put("CodigoDaUnidadeOuSetor", "001");
		result.put("DadosDaEmpresa", dadosDaEmpresa);

		return result;
	}
}
