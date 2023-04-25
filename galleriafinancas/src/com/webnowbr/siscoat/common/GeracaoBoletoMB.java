package com.webnowbr.siscoat.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.EmpresaCobranca;
import com.webnowbr.siscoat.cobranca.db.op.EmpresaCobrancaDao;
import com.webnowbr.siscoat.common.db.model.BoletosRemessa;
import com.webnowbr.siscoat.common.op.BoletosRemessaDao;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.Beneficiario;
import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.Datas;
import br.com.caelum.stella.boleto.Endereco;
import br.com.caelum.stella.boleto.Pagador;
import br.com.caelum.stella.boleto.bancos.BancoDoBrasil;
import br.com.caelum.stella.boleto.bancos.Bradesco;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoletoHTML;


/** ManagedBean. */
@ManagedBean(name = "geracaoBoletoMB")
@SessionScoped
public class GeracaoBoletoMB {	
	
	private StreamedContent file;
	
	private EmpresaCobranca empresaCobranca;
	
	private String pathBoleto;
	private String nomeBoleto;
	
	private String tituloPagina;
	
	private boolean gerouBoleto = false;
	
	private List<Boleto> boletos;
	
	private List<BoletosRemessa> boletosRemessaLote;
	
	public GeracaoBoletoMB() {
		this.gerouBoleto = false;
		this.file = null;
		
		this.pathBoleto = null;
		this.nomeBoleto = null;
		
		this.tituloPagina = "";
		
		this.boletos = new ArrayList<Boleto>();
		this.boletosRemessaLote = new ArrayList<BoletosRemessa>();
	}
	
	 public void geraBoleto(String sistema, String contrato, String nome, String cpf, String cnpj,
	    		String endereco, String bairro, String cep, String cidade, String uf, Date dataVencimento, BigDecimal valor, String numeroParcela,
	    		String enderecoEmpresa, String BairroEmpresa, String CepEmpresa, String CidadeEmpresa, String EstadoEmpresa,  String NomeEmpresa, String CnpjEmpresa, String AgenciaEmpresa, String DigitoAgenciaEmpresa, String CodigoBeneficiarioEmpresa, 
	    		String DigitoBeneficiarioEmpresa, String NumeroConvenioEmpresa, String CarteiraEmpresa, String Instrucao1Empresa, String Instrucao2Empresa, String Instrucao3Empresa, String Instrucao4Empresa, String Instrucao5Empresa, String LocalPagamentoEmpresa ){  
	        

			FacesContext context = FacesContext.getCurrentInstance();

	    	Map<String, Object> filters = new HashMap<String, Object>();
	    	filters.put("sistema", sistema);
	    	
	    		
	    		this.pathBoleto = null;
	    		this.nomeBoleto = null;
	    		this.gerouBoleto = true;

	    		// Dados do Beneficiario
	    		// Classe Endereço
		        Endereco enderecoBeneficiario = Endereco.novoEndereco()
		                .comLogradouro(empresaCobranca.getEndereco())  
		                .comBairro(BairroEmpresa)  
		                .comCep(CepEmpresa)  
		                .comCidade(CidadeEmpresa)
		                .comUf(EstadoEmpresa);
		        // Classe Beneficiario
		        Beneficiario beneficiario = Beneficiario.novoBeneficiario()
		        		.comNomeBeneficiario(NomeEmpresa + " - CNPJ: " + CnpjEmpresa)  
		                .comAgencia(AgenciaEmpresa).comDigitoAgencia(DigitoAgenciaEmpresa)  
		                .comCodigoBeneficiario(CodigoBeneficiarioEmpresa)  
		                .comDigitoCodigoBeneficiario(DigitoBeneficiarioEmpresa)  
		                .comNumeroConvenio(NumeroConvenioEmpresa)  
		                .comCarteira(CarteiraEmpresa)  
		                .comEndereco(enderecoBeneficiario)
		                .comNossoNumero(contrato + String.format("%02d", Integer.valueOf(numeroParcela)));

		        // Datas do boleto
		        // Vencimento
		        Locale locale = new Locale("pt-br", "BR");  
		        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
				String dataVencimentoStr = sdf.format(dataVencimento.getTime());	        
		        
		        TimeZone zone = TimeZone.getDefault();  	        
				Calendar calEmissao = Calendar.getInstance(zone, locale);
				String calEmissaoStr = sdf.format(calEmissao.getTime());	

		        Datas datas = Datas.novasDatas()
		                .comDocumento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6,10)))
		                .comProcessamento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6, 10)))
		                .comVencimento(Integer.valueOf(dataVencimentoStr.substring(0, 2)), Integer.valueOf(dataVencimentoStr.substring(3, 5)), Integer.valueOf(dataVencimentoStr.substring(6, 10))); 
		        	
		        // Dados do Pagador
		        Endereco enderecoPagador = Endereco.novoEndereco()
		                .comLogradouro(endereco)  
		                .comBairro(bairro)  
		                .comCep(cep)  
		                .comCidade(cidade)  
		                .comUf(uf);  
		        
		        String documento = "";
		        if (cnpj != null) {
		        	documento = cnpj;
		        } else {
		        	documento = cpf;
		        }
		        
		        Pagador pagador = Pagador.novoPagador()  
		                .comNome(nome)  
		                .comDocumento(documento)
		                .comEndereco(enderecoPagador);
		
		        // Dados do Boleto
		        Banco banco = new Bradesco(); 

		        Boleto boleto = Boleto.novoBoleto()  
		                .comBanco(banco)  
		                .comDatas(datas)  
		                .comBeneficiario(beneficiario)  
		                .comPagador(pagador)  
		                .comValorBoleto(valor)  
		                .comNumeroDoDocumento(contrato + String.format("%02d", Integer.valueOf(numeroParcela)))  
		                .comInstrucoes(Instrucao1Empresa, Instrucao2Empresa, Instrucao3Empresa, Instrucao4Empresa, Instrucao5Empresa)  
		                .comLocaisDePagamento(LocalPagamentoEmpresa, ""); 


		        GeradorDeBoleto gerador = new GeradorDeBoleto(boleto);  

		        // Para gerar um boleto em PDF  
		        gerador.geraPDF("BancoDoBrasil.pdf");  
    
		        
				calEmissao.set(Calendar.HOUR_OF_DAY, 0);  
				calEmissao.set(Calendar.MINUTE, 0);  
				calEmissao.set(Calendar.SECOND, 0);  
				calEmissao.set(Calendar.MILLISECOND, 0);
				

		        // Para gerar um array de bytes a partir de um PDF  
		        byte[] bPDF = gerador.geraPDF();  


				

		        
				calEmissao.set(Calendar.HOUR_OF_DAY, 0);  
				calEmissao.set(Calendar.MINUTE, 0);  
				calEmissao.set(Calendar.SECOND, 0);  
				calEmissao.set(Calendar.MILLISECOND, 0);

	    } 
	
	
	/*
	 * public void geraBoleto(String sistema, String contrato, String nome, String
	 * cpf, String cnpj, String endereco, String bairro, String cep, String cidade,
	 * String uf, Date dataVencimento, BigDecimal valor, String numeroParcela) {
	 * 
	 * EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();
	 * EmpresaCobranca empresaCobranca = new EmpresaCobranca(); FacesContext context
	 * = FacesContext.getCurrentInstance();
	 * 
	 * Map<String, Object> filters = new HashMap<String, Object>();
	 * filters.put("sistema", sistema);
	 * 
	 * List<EmpresaCobranca> listEmpresaCobranca = new ArrayList<EmpresaCobranca>();
	 * 
	 * listEmpresaCobranca = empresaCobrancaDao.findByFilter(filters); this.file =
	 * null;
	 * 
	 * this.pathBoleto = null; this.nomeBoleto = null; this.gerouBoleto = true;
	 * 
	 * empresaCobranca = listEmpresaCobranca.get(0);
	 * 
	 * 
	 * Endereco enderecoBeneficiario = Endereco.novoEndereco()
	 * .comLogradouro(empresaCobranca.getEndereco())
	 * .comBairro(empresaCobranca.getBairro()) .comCep(empresaCobranca.getCep())
	 * .comCidade(empresaCobranca.getCidade()) .comUf(empresaCobranca.getEstado());
	 * 
	 * Beneficiario beneficiario = Beneficiario.novoBeneficiario()
	 * .comNomeBeneficiario(empresaCobranca.getNome() + " - CNPJ: " +
	 * empresaCobranca.getCnpj())
	 * .comAgencia(empresaCobranca.getAgencia()).comDigitoAgencia(empresaCobranca.
	 * getDigitoAgencia())
	 * .comCodigoBeneficiario(empresaCobranca.getCodigoBeneficiario())
	 * .comDigitoCodigoBeneficiario(empresaCobranca.getDigitoBeneficiario())
	 * .comNumeroConvenio(empresaCobranca.getNumeroConvenio())
	 * .comCarteira(empresaCobranca.getCarteira())
	 * .comEndereco(enderecoBeneficiario) .comNossoNumero(contrato +
	 * String.format("%02d", Integer.valueOf(numeroParcela)));
	 * 
	 * 
	 * Locale locale = new Locale("pt-br", "BR"); SimpleDateFormat sdf = new
	 * SimpleDateFormat("dd/MM/yyyy", locale); String dataVencimentoStr =
	 * sdf.format(dataVencimento.getTime());
	 * 
	 * TimeZone zone = TimeZone.getDefault(); Calendar calEmissao =
	 * Calendar.getInstance(zone, locale); String calEmissaoStr =
	 * sdf.format(calEmissao.getTime());
	 * 
	 * Datas datas = Datas.novasDatas()
	 * .comDocumento(Integer.valueOf(calEmissaoStr.substring(0, 2)),
	 * Integer.valueOf(calEmissaoStr.substring(3, 5)),
	 * Integer.valueOf(calEmissaoStr.substring(6,10)))
	 * .comProcessamento(Integer.valueOf(calEmissaoStr.substring(0, 2)),
	 * Integer.valueOf(calEmissaoStr.substring(3, 5)),
	 * Integer.valueOf(calEmissaoStr.substring(6, 10)))
	 * .comVencimento(Integer.valueOf(dataVencimentoStr.substring(0, 2)),
	 * Integer.valueOf(dataVencimentoStr.substring(3, 5)),
	 * Integer.valueOf(dataVencimentoStr.substring(6, 10)));
	 * 
	 * 
	 * Endereco enderecoPagador = Endereco.novoEndereco() .comLogradouro(endereco)
	 * .comBairro(bairro) .comCep(cep) .comCidade(cidade) .comUf(uf);
	 * 
	 * String documento = ""; if (cnpj != null) { documento = cnpj; } else {
	 * documento = cpf; }
	 * 
	 * Pagador pagador = Pagador.novoPagador() .comNome(nome)
	 * .comDocumento(documento) .comEndereco(enderecoPagador);
	 * 
	 * 
	 * Banco banco = new BancoDoBrasil();
	 * 
	 * Boleto boleto = Boleto.novoBoleto() .comBanco(banco) .comDatas(datas)
	 * .comBeneficiario(beneficiario) .comPagador(pagador) .comValorBoleto(valor)
	 * .comNumeroDoDocumento(contrato + String.format("%02d",
	 * Integer.valueOf(numeroParcela)))
	 * .comInstrucoes(empresaCobranca.getInstrucao1(),
	 * empresaCobranca.getInstrucao2(), empresaCobranca.getInstrucao3(),
	 * empresaCobranca.getInstrucao4(), empresaCobranca.getInstrucao5())
	 * .comLocaisDePagamento(empresaCobranca.getLocalPagamento(), "");
	 * 
	 * GeradorDeBoleto gerador = new GeradorDeBoleto(boleto);
	 * 
	 * gerador.geraPDF("BancoDoBrasil.pdf"); }
	 */
	
	
	// armazena boletos em série para geração unica
    public void geraBoletosBancoDoBrasil(String sistema, String contrato, String nome, String cpf, String cnpj,
    		String endereco, String bairro, String cep, String cidade, String uf, Date dataVencimento, BigDecimal valor, String numeroParcela){  
        
    	EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();
    	EmpresaCobranca empresaCobranca = new EmpresaCobranca();
		FacesContext context = FacesContext.getCurrentInstance();

    	Map<String, Object> filters = new HashMap<String, Object>();
    	filters.put("sistema", sistema);
    	
    	List<EmpresaCobranca> listEmpresaCobranca = new ArrayList<EmpresaCobranca>();
    	
    	listEmpresaCobranca = empresaCobrancaDao.findByFilter(filters);
    	
    	// verifica se há mais de uma empresa por sistema
    	// se sim da erro
    	/*
    	if (listEmpresaCobranca.size() > 1) {
    		context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Há mais de uma empresa de cobrança para o sistema de " + sistema + "!", ""));
    	}
    	if (listEmpresaCobranca.size() == 0) {
    		context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Não há empresa de cobrança para o sistema de " + sistema + "!", ""));
    	}
    	if (listEmpresaCobranca.size() == 1) {
    	
    	TRATAR AS GALLERIAS SA E CORRESPODENTE
    	*/
    		this.file = null;
    		
    		this.pathBoleto = null;
    		this.nomeBoleto = null;
    		this.gerouBoleto = true;
    		
    		empresaCobranca = listEmpresaCobranca.get(0);

    		// Dados do Beneficiario
    		// Classe Endereço
	        Endereco enderecoBeneficiario = Endereco.novoEndereco()
	                .comLogradouro(empresaCobranca.getEndereco())  
	                .comBairro(empresaCobranca.getBairro())  
	                .comCep(empresaCobranca.getCep())  
	                .comCidade(empresaCobranca.getCidade())
	                .comUf(empresaCobranca.getEstado());
	        // Classe Beneficiario
	        Beneficiario beneficiario = Beneficiario.novoBeneficiario()  
	                .comNomeBeneficiario(empresaCobranca.getNome() + " - CNPJ: " + empresaCobranca.getCnpj())  
	                .comAgencia(empresaCobranca.getAgencia()).comDigitoAgencia(empresaCobranca.getDigitoAgencia())  
	                .comCodigoBeneficiario(empresaCobranca.getCodigoBeneficiario())  
	                .comDigitoCodigoBeneficiario(empresaCobranca.getDigitoBeneficiario())  
	                .comNumeroConvenio(empresaCobranca.getNumeroConvenio())  
	                .comCarteira(empresaCobranca.getCarteira())  
	                .comEndereco(enderecoBeneficiario)
	                .comNossoNumero(contrato + String.format("%02d", Integer.valueOf(numeroParcela)));

	        // Datas do boleto
	        // Vencimento
	        Locale locale = new Locale("pt-br", "BR");  
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
			String dataVencimentoStr = sdf.format(dataVencimento.getTime());	        
	        
	        TimeZone zone = TimeZone.getDefault();  	        
			Calendar calEmissao = Calendar.getInstance(zone, locale);
			String calEmissaoStr = sdf.format(calEmissao.getTime());	

	        Datas datas = Datas.novasDatas()
	                .comDocumento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6,10)))
	                .comProcessamento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6, 10)))
	                .comVencimento(Integer.valueOf(dataVencimentoStr.substring(0, 2)), Integer.valueOf(dataVencimentoStr.substring(3, 5)), Integer.valueOf(dataVencimentoStr.substring(6, 10))); 
	        	
	        // Dados do Pagador
	        Endereco enderecoPagador = Endereco.novoEndereco()
	                .comLogradouro(endereco)  
	                .comBairro(bairro)  
	                .comCep(cep)  
	                .comCidade(cidade)  
	                .comUf(uf);  
	        
	        String documento = "";
	        if (cnpj != null) {
	        	documento = cnpj;
	        } else {
	        	documento = cpf;
	        }
	        
	        Pagador pagador = Pagador.novoPagador()  
	                .comNome(nome)  
	                .comDocumento(documento)
	                .comEndereco(enderecoPagador);
	
	        // Dados do Boleto
	        Banco banco = new BancoDoBrasil(); 

	        Boleto boleto = Boleto.novoBoleto()  
	                .comBanco(banco)  
	                .comDatas(datas)  
	                .comBeneficiario(beneficiario)  
	                .comPagador(pagador)  
	                .comValorBoleto(valor)  
	                .comNumeroDoDocumento(contrato + String.format("%02d", Integer.valueOf(numeroParcela)))  
	                .comInstrucoes(empresaCobranca.getInstrucao1(), empresaCobranca.getInstrucao2(), empresaCobranca.getInstrucao3(), empresaCobranca.getInstrucao4(), empresaCobranca.getInstrucao5())  
	                .comLocaisDePagamento(empresaCobranca.getLocalPagamento(), "");   

	        GeradorDeBoleto gerador = new GeradorDeBoleto(boleto);  

	        // Para gerar um boleto em PDF  
	        gerador.geraPDF("BancoDoBrasil.pdf");  

	        // Para gerar um boleto em PNG  
	        gerador.geraPNG("BancoDoBrasil.png");  	        
	        
			calEmissao.set(Calendar.HOUR_OF_DAY, 0);  
			calEmissao.set(Calendar.MINUTE, 0);  
			calEmissao.set(Calendar.SECOND, 0);  
			calEmissao.set(Calendar.MILLISECOND, 0);
			

	        // Para gerar um array de bytes a partir de um PDF  
	        byte[] bPDF = gerador.geraPDF();  

	        // Para gerar um array de bytes a partir de um PNG  
	        byte[] bPNG = gerador.geraPNG();
			
	        // popula tabela de arquivo remessa
	        BoletosRemessa boletosRemessa = new BoletosRemessa(sistema, contrato, numeroParcela, dataVencimento, calEmissao.getTime(),
	        		valor, documento, nome, endereco, bairro, cep, cidade, uf, false);
	        
	        boletosRemessaLote.add(boletosRemessa);
	        
	        boletos.add(boleto);
    	//} 
    }
	
	// armazena boletos em série para geração unica
    public void geraBoletosBradesco(String sistema, String contrato, String nome, String cpf, String cnpj,
    		String endereco, String bairro, String cep, String cidade, String uf, Date dataVencimento, BigDecimal valor, String numeroParcela){  
        
    	EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();
    	EmpresaCobranca empresaCobranca = new EmpresaCobranca();
		FacesContext context = FacesContext.getCurrentInstance();

    	Map<String, Object> filters = new HashMap<String, Object>();
    	filters.put("sistema", sistema);
    	
    	List<EmpresaCobranca> listEmpresaCobranca = new ArrayList<EmpresaCobranca>();
    	
    	listEmpresaCobranca = empresaCobrancaDao.findByFilter(filters);
    	
    	// verifica se há mais de uma empresa por sistema
    	// se sim da erro
    	if (listEmpresaCobranca.size() > 1) {
    		context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Há mais de uma empresa de cobrança para o sistema de " + sistema + "!", ""));
    	}
    	if (listEmpresaCobranca.size() == 0) {
    		context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Não há empresa de cobrança para o sistema de " + sistema + "!", ""));
    	}
    	if (listEmpresaCobranca.size() == 1) {
    		this.file = null;
    		
    		this.pathBoleto = null;
    		this.nomeBoleto = null;
    		this.gerouBoleto = true;
    		
    		empresaCobranca = listEmpresaCobranca.get(0);

    		// Dados do Beneficiario
    		// Classe Endereço
	        Endereco enderecoBeneficiario = Endereco.novoEndereco()
	                .comLogradouro(empresaCobranca.getEndereco())  
	                .comBairro(empresaCobranca.getBairro())  
	                .comCep(empresaCobranca.getCep())  
	                .comCidade(empresaCobranca.getCidade())
	                .comUf(empresaCobranca.getEstado());
	        // Classe Beneficiario
	        Beneficiario beneficiario = Beneficiario.novoBeneficiario()  
	                .comNomeBeneficiario(empresaCobranca.getNome() + " - CNPJ: " + empresaCobranca.getCnpj())  
	                .comAgencia(empresaCobranca.getAgencia()).comDigitoAgencia(empresaCobranca.getDigitoAgencia())  
	                .comCodigoBeneficiario(empresaCobranca.getCodigoBeneficiario())  
	                .comDigitoCodigoBeneficiario(empresaCobranca.getDigitoBeneficiario())  
	                .comNumeroConvenio(empresaCobranca.getNumeroConvenio())  
	                .comCarteira(empresaCobranca.getCarteira())  
	                .comEndereco(enderecoBeneficiario)
	                .comNossoNumero(contrato + String.format("%02d", Integer.valueOf(numeroParcela)));

	        // Datas do boleto
	        // Vencimento
	        Locale locale = new Locale("pt-br", "BR");  
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
			String dataVencimentoStr = sdf.format(dataVencimento.getTime());	        
	        
	        TimeZone zone = TimeZone.getDefault();  	        
			Calendar calEmissao = Calendar.getInstance(zone, locale);
			String calEmissaoStr = sdf.format(calEmissao.getTime());	

	        Datas datas = Datas.novasDatas()
	                .comDocumento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6,10)))
	                .comProcessamento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6, 10)))
	                .comVencimento(Integer.valueOf(dataVencimentoStr.substring(0, 2)), Integer.valueOf(dataVencimentoStr.substring(3, 5)), Integer.valueOf(dataVencimentoStr.substring(6, 10))); 
	        	
	        // Dados do Pagador
	        Endereco enderecoPagador = Endereco.novoEndereco()
	                .comLogradouro(endereco)  
	                .comBairro(bairro)  
	                .comCep(cep)  
	                .comCidade(cidade)  
	                .comUf(uf);  
	        
	        String documento = "";
	        if (cnpj != null) {
	        	documento = cnpj;
	        } else {
	        	documento = cpf;
	        }
	        
	        Pagador pagador = Pagador.novoPagador()  
	                .comNome(nome)  
	                .comDocumento(documento)
	                .comEndereco(enderecoPagador);
	
	        // Dados do Boleto
	        Banco banco = new Bradesco(); 

	        Boleto boleto = Boleto.novoBoleto()  
	                .comBanco(banco)  
	                .comDatas(datas)  
	                .comBeneficiario(beneficiario)  
	                .comPagador(pagador)  
	                .comValorBoleto(valor)  
	                .comNumeroDoDocumento(contrato + String.format("%02d", Integer.valueOf(numeroParcela)))  
	                .comInstrucoes(empresaCobranca.getInstrucao1(), empresaCobranca.getInstrucao2(), empresaCobranca.getInstrucao3(), empresaCobranca.getInstrucao4(), empresaCobranca.getInstrucao5())  
	                .comLocaisDePagamento(empresaCobranca.getLocalPagamento(), ""); 
	        
			calEmissao.set(Calendar.HOUR_OF_DAY, 0);  
			calEmissao.set(Calendar.MINUTE, 0);  
			calEmissao.set(Calendar.SECOND, 0);  
			calEmissao.set(Calendar.MILLISECOND, 0);
			
	        // popula tabela de arquivo remessa
	        BoletosRemessa boletosRemessa = new BoletosRemessa(sistema, contrato, numeroParcela, dataVencimento, calEmissao.getTime(),
	        		valor, documento, nome, endereco, bairro, cep, cidade, uf, false);
	        
	        boletosRemessaLote.add(boletosRemessa);
	        
	        boletos.add(boleto);
    	} 
    }
    
    // atraves do list boletos gera o PDF
    public void geraPDFBoletos(String nomeArquivo) {
    	FacesContext context = FacesContext.getCurrentInstance();
        
    	try {
    		if (boletos.size() > 0) {
	    		Boleto[] boletosArray = new Boleto[boletos.size()];
	    		
	    		for (int i = 0; i < boletos.size(); i++) {
	    			boletosArray[i] = boletos.get(i);
	    		}
	    		
		        GeradorDeBoletoHTML gerador = new GeradorDeBoletoHTML(boletosArray);  
		        
		        //GeradorDeBoletoHTML gerador = new GeradorDeBoletoHTML(boleto);
		
		        // Para gerar um boleto em PDF  
		        ParametrosDao pDao = new ParametrosDao(); 
				this.pathBoleto = pDao.findByFilter("nome", "LOCACAO_PATH_BOLETO").get(0).getValorString();

		        this.nomeBoleto = nomeArquivo +".pdf";
		        
		        gerador.geraPDF(pathBoleto + nomeBoleto);  
		        
		        BoletosRemessaDao boletosRemessaDao = new BoletosRemessaDao();
		        
		        for (BoletosRemessa boletos : this.boletosRemessaLote) {
			        boletosRemessaDao.merge(boletos);
		        }
		
		        // Para gerar um boleto em PNG  
		        //gerador.geraPNG("c:/temp/BancoDoBrasil.png");  
		        
		        //gerador.geraHTML(response.getWriter(), request);
		
		        // Para gerar um array de bytes a partir de um PDF  
		        //byte[] bPDF = gerador.geraPDF();  
		
		        // Para gerar um array de bytes a partir de um PNG  
		        //byte[] bPNG = gerador.geraPNG();
    		}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Ocorreu um problema ao gerar o(s) boleto(s)!" + e, ""));
		}
    }

    public void geraBoletoBradesco(String sistema, String contrato, String nome, String cpf, String cnpj,
    		String endereco, String bairro, String cep, String cidade, String uf, Date dataVencimento, BigDecimal valor,  BigDecimal valorAtualizado, String nomeArquivo, String numeroParcela){  
        
    	EmpresaCobrancaDao empresaCobrancaDao = new EmpresaCobrancaDao();
    	EmpresaCobranca empresaCobranca = new EmpresaCobranca();
		FacesContext context = FacesContext.getCurrentInstance();

    	Map<String, Object> filters = new HashMap<String, Object>();
    	filters.put("sistema", sistema);
    	
    	List<EmpresaCobranca> listEmpresaCobranca = new ArrayList<EmpresaCobranca>();
    	
    	listEmpresaCobranca = empresaCobrancaDao.findByFilter(filters);
    	
    	// verifica se há mais de uma empresa por sistema
    	// se sim da erro
    	if (listEmpresaCobranca.size() > 1) {
    		context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Há mais de uma empresa de cobrança para o sistema de " + sistema + "!", ""));
    	}
    	if (listEmpresaCobranca.size() == 0) {
    		context.addMessage(null, new FacesMessage(
    				FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Não há empresa de cobrança para o sistema de " + sistema + "!", ""));
    	}
    	if (listEmpresaCobranca.size() == 1) {
    		try {
	    		this.file = null;
	    		
	    		this.pathBoleto = null;
	    		this.nomeBoleto = null;
	    		this.gerouBoleto = true;
	    		
	    		empresaCobranca = listEmpresaCobranca.get(0);
	
	    		// Dados do Beneficiario
	    		// Classe Endereço
		        Endereco enderecoBeneficiario = Endereco.novoEndereco()
		                .comLogradouro(empresaCobranca.getEndereco())  
		                .comBairro(empresaCobranca.getBairro())  
		                .comCep(empresaCobranca.getCep())  
		                .comCidade(empresaCobranca.getCidade())
		                .comUf(empresaCobranca.getEstado());
		        // Classe Beneficiario
		        Beneficiario beneficiario = Beneficiario.novoBeneficiario()  
		                .comNomeBeneficiario(empresaCobranca.getNome() + " - CNPJ: " + empresaCobranca.getCnpj())  
		                .comAgencia(empresaCobranca.getAgencia()).comDigitoAgencia(empresaCobranca.getDigitoAgencia())  
		                .comCodigoBeneficiario(empresaCobranca.getCodigoBeneficiario())  
		                .comDigitoCodigoBeneficiario(empresaCobranca.getDigitoBeneficiario())  
		                .comNumeroConvenio(empresaCobranca.getNumeroConvenio())  
		                .comCarteira(empresaCobranca.getCarteira())  
		                .comEndereco(enderecoBeneficiario)
		                .comNossoNumero(contrato + String.format("%02d", Integer.valueOf(numeroParcela)));
	
		        // Datas do boleto
		        // Vencimento
		        Locale locale = new Locale("pt-br", "BR");  
		        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
				String dataVencimentoStr = sdf.format(dataVencimento.getTime());	        
		        
		        TimeZone zone = TimeZone.getDefault();  	        
				Calendar calEmissao = Calendar.getInstance(zone, locale);
				String calEmissaoStr = sdf.format(calEmissao.getTime());	

		        Datas datas = Datas.novasDatas()
		                .comDocumento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6,10)))
		                .comProcessamento(Integer.valueOf(calEmissaoStr.substring(0, 2)), Integer.valueOf(calEmissaoStr.substring(3, 5)), Integer.valueOf(calEmissaoStr.substring(6, 10)))
		                .comVencimento(Integer.valueOf(dataVencimentoStr.substring(0, 2)), Integer.valueOf(dataVencimentoStr.substring(3, 5)), Integer.valueOf(dataVencimentoStr.substring(6, 10))); 
		        	
		        // Dados do Pagador
		        Endereco enderecoPagador = Endereco.novoEndereco()
		                .comLogradouro(endereco)  
		                .comBairro(bairro)  
		                .comCep(cep)  
		                .comCidade(cidade)  
		                .comUf(uf);  
		        
		        String documento = "";
		        if (!cnpj.equals("")) {
		        	documento = cnpj;
		        } else {
		        	documento = cpf;
		        }
		        
		        Pagador pagador = Pagador.novoPagador()  
		                .comNome(nome)  
		                .comDocumento(documento)
		                .comEndereco(enderecoPagador);
		
		        // Dados do Boleto
		        Banco banco = new Bradesco(); 
		        
		        BigDecimal valorBoleto = BigDecimal.ZERO;
		        
		        if (valorAtualizado.compareTo(BigDecimal.ZERO) == 0) {
		        	valorBoleto = valor;
		        } else {
		        	valorBoleto = valorAtualizado;
		        }
	
		        Boleto boleto = Boleto.novoBoleto()  
		                .comBanco(banco)  
		                .comDatas(datas)  
		                .comBeneficiario(beneficiario)  
		                .comPagador(pagador)  
		                .comValorBoleto(valorBoleto)  
		                .comNumeroDoDocumento(contrato + String.format("%02d", Integer.valueOf(numeroParcela)))  
		                .comInstrucoes(empresaCobranca.getInstrucao1(), empresaCobranca.getInstrucao2(), empresaCobranca.getInstrucao3(), empresaCobranca.getInstrucao4(), empresaCobranca.getInstrucao5())  
		                .comLocaisDePagamento(empresaCobranca.getLocalPagamento(), "");          
		               
		        GeradorDeBoletoHTML gerador = new GeradorDeBoletoHTML(boleto);  
		        
		        //GeradorDeBoletoHTML gerador = new GeradorDeBoletoHTML(boleto);
		        
				calEmissao.set(Calendar.HOUR_OF_DAY, 0);  
				calEmissao.set(Calendar.MINUTE, 0);  
				calEmissao.set(Calendar.SECOND, 0);  
				calEmissao.set(Calendar.MILLISECOND, 0);
				
		        BoletosRemessa boletosRemessa = new BoletosRemessa(sistema, contrato, numeroParcela, dataVencimento, calEmissao.getTime(),
		        		valor, documento, nome, endereco, bairro, cep, cidade, uf, false);
		        
		        BoletosRemessaDao boletosRemessaDao = new BoletosRemessaDao();
		        boletosRemessaDao.merge(boletosRemessa);
		
		        // Para gerar um boleto em PDF  
		        ParametrosDao pDao = new ParametrosDao(); 
		        this.pathBoleto = pDao.findByFilter("nome", "LOCACAO_PATH_BOLETO").get(0).getValorString();	    		
	    		
	    		if (!nomeArquivo.equals("")) { 
	    			this.nomeBoleto = nomeArquivo +".pdf";
	    		} else {
	    			this.nomeBoleto = "Boleto Bradesco - Contrato: " + contrato + ".pdf";
	    		}
	            
	            this.tituloPagina = contrato;
	            
		        gerador.geraPDF(pathBoleto + nomeBoleto);  
		
		        // Para gerar um boleto em PNG  
		        //gerador.geraPNG("c:/temp/BancoDoBrasil.png");  
		        
		        //gerador.geraHTML(response.getWriter(), request);
		
		        // Para gerar um array de bytes a partir de um PDF  
		        //byte[] bPDF = gerador.geraPDF();  
		
		        // Para gerar um array de bytes a partir de um PNG  
		        //byte[] bPNG = gerador.geraPNG();
    		} catch (Exception e) {
    			context.addMessage(null, new FacesMessage(
    					FacesMessage.SEVERITY_ERROR, "Geração de Boleto Bradesco: Ocorreu um problema ao gerar o boleto! (Boleto: "
    							+ contrato + ")" + e, ""));
    		}
	    } 
    }

	/**
	 * @param file the file to set
	 */
	public void setFile(StreamedContent file) {
		this.file = file;
	}

    public StreamedContent getFile() {
    	String caminho =  this.pathBoleto + this.nomeBoleto;        
        String arquivo = this.nomeBoleto;
        FileInputStream stream = null;
        
        this.gerouBoleto = false; 
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
        file = new DefaultStreamedContent(stream, caminho, arquivo); 
        
        return file;    
    }

	/**
	 * @return the pathBoleto
	 */
	public String getPathBoleto() {
		return pathBoleto;
	}

	/**
	 * @param pathBoleto the pathBoleto to set
	 */
	public void setPathBoleto(String pathBoleto) {
		this.pathBoleto = pathBoleto;
	}

	/**
	 * @return the nomeBoleto
	 */
	public String getNomeBoleto() {
		return nomeBoleto;
	}

	/**
	 * @param nomeBoleto the nomeBoleto to set
	 */
	public void setNomeBoleto(String nomeBoleto) {
		this.nomeBoleto = nomeBoleto;
	}

	/**
	 * @return the gerouBoleto
	 */
	public boolean isGerouBoleto() {
		return gerouBoleto;
	}

	/**
	 * @param gerouBoleto the gerouBoleto to set
	 */
	public void setGerouBoleto(boolean gerouBoleto) {
		this.gerouBoleto = gerouBoleto;
	}

	/**
	 * @return the tituloPagina
	 */
	public String getTituloPagina() {
		return tituloPagina;
	}

	/**
	 * @param tituloPagina the tituloPagina to set
	 */
	public void setTituloPagina(String tituloPagina) {
		this.tituloPagina = tituloPagina;
	}

	public List<BoletosRemessa> getBoletosRemessaLote() {
		return boletosRemessaLote;
	}

	public void setBoletosRemessaLote(List<BoletosRemessa> boletosRemessaLote) {
		this.boletosRemessaLote = boletosRemessaLote;
	}
}  
