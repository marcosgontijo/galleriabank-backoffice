package com.webnowbr.siscoat.cobranca.faciltech;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.json.JSONObject;

import com.webnowbr.siscoat.cobranca.db.model.CDI;
import com.webnowbr.siscoat.cobranca.db.op.CDIDao;
import com.webnowbr.siscoat.common.DateUtil;


@ManagedBean(name = "testeFacilMB")
@SessionScoped

public class TesteFacil {
	
	public void getTesteCriaPessoa() {
		String baseServiceUrl = "https://wscredhomogalleria.facilinformatica.com.br/wcf/rest";
		String loginOperadorDeComunicacaoServerToServer = "BACKOFFICE";

		try {
			serverToServerTest(baseServiceUrl, loginOperadorDeComunicacaoServerToServer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
