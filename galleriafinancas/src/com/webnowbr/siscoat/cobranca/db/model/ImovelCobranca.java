package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.db.op.CidadeDao;
import com.webnowbr.siscoat.common.CommonsUtil;

public class ImovelCobranca implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroMatricula;
	private String nome;
	private String endereco;
	private String bairro;
	private String complemento;
	private String cidade;
	private String estado;
	private String telResidencial;
	private String observacao;
	private String cep;
	private String numeroCartorio;
	private String cartorio;
	private String cartorioEstado;
	private String cartorioMunicipio;
	private String tipo;
	private String areaConstruida;
	private String areaTotal;
	private String linkGMaps;
	private String possuiDivida;
	private String ocupacao;
	private BigDecimal valoEstimado;
	private Date dataCompra;
	private String idadeCompra;
	private String nomeProprietario;
	private BigDecimal valorIptu;
	private BigDecimal valorCondominio;
	private BigDecimal valorMercado;
	private BigDecimal valorLeilao;
	private String inscricaoMunicipal;
	private boolean comprovanteMatriculaCheckList;
	private boolean comprovanteFotosImovelCheckList;
	private boolean comprovanteIptuImovelCheckList;
	private boolean cndIptuExtratoDebitoCheckList;//
	private boolean cndCondominioExtratoDebitoCheckList;//
	private boolean matriculaGaragemCheckList;//
	private boolean simuladorCheckList;//
	
	private ImovelEstoque imovelEstoque;
	private int numeroQuartos;
	private int numeroBanheiros;
	private int numeroSuites;
	private int numeroGaragens;
	private String numeroImovel;
	private String enderecoSemNumero;
	
	private Cidade objetoCidade;
	private int subCategoria;
	
	//Novos campos referente ao pre laudo
	private boolean preLaudoSolicitado;
	private boolean preLaudoEntregue;
	private BigDecimal valorPreLaudo;

	
	
	public ImovelCobranca(){
		resetarBololean();
	}
	
	public ImovelCobranca(long id, String numeroMatricula, String nome, String endereco, String bairro, String complemento,
						 String cidade, String estado, String telResidencial, String observacao, String cep){
		resetarBololean();
		this.id = id;
		this.numeroMatricula = numeroMatricula;
		this.nome = nome;
		this.endereco = endereco;
		this.bairro = bairro;
		this.complemento = complemento;
		this.cidade = cidade;
		this.estado = estado;
		this.telResidencial = telResidencial;
		this.observacao = observacao;
		this.cep = cep;
	}
	
	
	
	public ImovelCobranca(ImovelCobranca imovel) {
		super();
		this.endereco = imovel.getEndereco();
		this.bairro = imovel.getBairro();
		this.complemento = imovel.getComplemento();
		this.cidade = imovel.getCidade();
		this.estado = imovel.getEstado();
		this.cep = imovel.getCep();
		this.numeroCartorio = imovel.getNumeroCartorio();
		this.cartorio = imovel.getCartorio();
		this.cartorioEstado = imovel.getCartorioEstado();
		this.cartorioMunicipio = imovel.getCartorioMunicipio();
		this.linkGMaps = imovel.getLinkGMaps();
		this.possuiDivida = imovel.getPossuiDivida();
		this.ocupacao = imovel.getOcupacao();
		this.valoEstimado = imovel.getValoEstimado();
		this.dataCompra = imovel.getDataCompra();
		this.idadeCompra = imovel.getIdadeCompra();
		this.nomeProprietario = imovel.getNomeProprietario();
		this.valorIptu = imovel.getValorIptu();
		this.valorCondominio = imovel.getValorCondominio();
		this.inscricaoMunicipal = imovel.getInscricaoMunicipal();
		this.objetoCidade = imovel.getObjetoCidade();
		this.preLaudoSolicitado = imovel.isPreLaudoSolicitado();
		this.preLaudoEntregue = imovel.isPreLaudoEntregue();	
	}

	public void calcularDataDeCompra() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date dateHoje = dataHoje.getTime();
		if(!CommonsUtil.semValor(this.getDataCompra())) {	
			long idadeLong = dateHoje.getTime() - this.getDataCompra().getTime();
			idadeLong = TimeUnit.DAYS.convert(idadeLong, TimeUnit.MILLISECONDS);
			idadeLong = idadeLong / 30;
			idadeLong = idadeLong / 12;
		    this.setIdadeCompra(CommonsUtil.stringValue(idadeLong));
		}
	}
	
	public void resetarBololean() {
		this.comprovanteMatriculaCheckList = false;
		this.comprovanteFotosImovelCheckList = false;
		this.comprovanteIptuImovelCheckList = false;
		this.preLaudoEntregue = false;
		this.preLaudoSolicitado = false;
	}
	
	private static LinkedHashMap<String, String> siglasEstados = new LinkedHashMap<>();
    static {
        siglasEstados.put("AC", "Acre");
        siglasEstados.put("AL", "Alagoas");
        siglasEstados.put("AP", "Amapá");
        siglasEstados.put("AM", "Amazonas");
        siglasEstados.put("BA", "Bahia");
        siglasEstados.put("CE", "Ceará");
        siglasEstados.put("DF", "Distrito Federal");
        siglasEstados.put("ES", "Espírito Santo");
        siglasEstados.put("GO", "Goiás");
        siglasEstados.put("MA", "Maranhão");
        siglasEstados.put("MT", "Mato Grosso");
        siglasEstados.put("MS", "Mato Grosso do Sul");
        siglasEstados.put("MG", "Minas Gerais");
        siglasEstados.put("PA", "Pará");
        siglasEstados.put("PB", "Paraíba");
        siglasEstados.put("PR", "Paraná");
        siglasEstados.put("PE", "Pernambuco");
        siglasEstados.put("PI", "Piauí");
        siglasEstados.put("RJ", "Rio de Janeiro");
        siglasEstados.put("RN", "Rio Grande do Norte");
        siglasEstados.put("RS", "Rio Grande do Sul");
        siglasEstados.put("RO", "Rondônia");
        siglasEstados.put("RR", "Roraima");
        siglasEstados.put("SC", "Santa Catarina");
        siglasEstados.put("SP", "São Paulo");
        siglasEstados.put("SE", "Sergipe");
        siglasEstados.put("TO", "Tocantins");
    }
    public static String getEstadoPorSigla(String sigla) {
        return siglasEstados.getOrDefault(sigla, "UF de estado inválido");
    }
    public  LinkedHashMap<String, String> getSiglasEstados(){
    	siglasEstados = new LinkedHashMap<>();
        siglasEstados.put("AC", "Acre");
        siglasEstados.put("AL", "Alagoas");
        siglasEstados.put("AP", "Amapá");
        siglasEstados.put("AM", "Amazonas");
        siglasEstados.put("BA", "Bahia");
        siglasEstados.put("CE", "Ceará");
        siglasEstados.put("DF", "Distrito Federal");
        siglasEstados.put("ES", "Espírito Santo");
        siglasEstados.put("GO", "Goiás");
        siglasEstados.put("MA", "Maranhão");
        siglasEstados.put("MT", "Mato Grosso");
        siglasEstados.put("MS", "Mato Grosso do Sul");
        siglasEstados.put("MG", "Minas Gerais");
        siglasEstados.put("PA", "Pará");
        siglasEstados.put("PB", "Paraíba");
        siglasEstados.put("PR", "Paraná");
        siglasEstados.put("PE", "Pernambuco");
        siglasEstados.put("PI", "Piauí");
        siglasEstados.put("RJ", "Rio de Janeiro");
        siglasEstados.put("RN", "Rio Grande do Norte");
        siglasEstados.put("RS", "Rio Grande do Sul");
        siglasEstados.put("RO", "Rondônia");
        siglasEstados.put("RR", "Roraima");
        siglasEstados.put("SC", "Santa Catarina");
        siglasEstados.put("SP", "São Paulo");
        siglasEstados.put("SE", "Sergipe");
        siglasEstados.put("TO", "Tocantins");
        return siglasEstados;
    }
	
	public List<String> pegarListaCidades() {
		List<String> cidades = new ArrayList<>();
		String estadoStr = getEstadoPorSigla(estado);
		CidadeDao cidadeDao = new CidadeDao();
		cidades = cidadeDao.pegarCidadesPeloEstado(estadoStr);
		return cidades;
	}
	
	public List<String> completeCidadesImovel(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> cidades = new ArrayList<>();
		List<String> listaCidades = pegarListaCidades();
		if(!CommonsUtil.semValor(listaCidades)) {
			for (String cidade : listaCidades) {
				cidades.add(cidade);
			}
		}
		Collections.sort(cidades);
		return cidades.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	 }
	
	public List<String> completeTipoImovel(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> listaTipos = new ArrayList<>();
		for(String tipo : listaTipoImovel()) {
			listaTipos.add(tipo);//.split(Pattern.quote(","))[0]
		}
		
		return listaTipos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}
	
	public List<String> listaTipoImovel() {
		List<String> tipos = new ArrayList<>();
		tipos.add("Apartamento,Apartamento");
		tipos.add("Casa,Casa");
		tipos.add("Galpão,Galpão");
		tipos.add("Sala Comercial,Sala Comercial");
		tipos.add("Prédio Comercial,Prédio Comercial");
		tipos.add("Prédio Misto,Prédio Misto");
		tipos.add("Casa de Condomínio,Casa de condomínio");
		tipos.add("Garagem,Garagem");
		//tipos.add("Casa de Condomínio acima1000,Casa de condomínio (acima 1000m²)");
		tipos.add("Terreno,Terreno");
		//tipos.add("Terreno de Condomínio,Terreno de Condomínio");
		tipos.add("Chácara,Chácara");
		tipos.add("Rural,Rural");
		tipos.add("Casa em construção,Casa em construção");
		tipos.add("Posto,Posto");
		return tipos;
	}
	
	public String separarTipoVirgula(String value, int i) {
		if(CommonsUtil.semValor(value)) {
			return "";
		}
				
		if(value.contains(",")) {
			return value.split(Pattern.quote(","))[i];
		} else {
			List<String> tipos = listaTipoImovel();
			List<String> lista = tipos.stream().filter(t -> t.toLowerCase().contains(value.toLowerCase())).collect(Collectors.toList());
			if(lista.size() <= 0) {
				return "";
			}
			String retorno = lista.get(0);
			return retorno.split(Pattern.quote(","))[i];
		}
	}
	
	public String getValue(String value) {
		return separarTipoVirgula(value, 0);
	}
	
	public String getLabel(String value) {
		return separarTipoVirgula(value, 1);
	}
	

	public void consultarObjetoCidade() {
		String estadoStr = getEstadoPorSigla(estado);
		CidadeDao cidadeDao = new CidadeDao();
		objetoCidade = cidadeDao.busccaCidadeConculta(cidade, estadoStr, false);
	}
	
	public void popularObjetoCidade() {
		String estadoStr = getEstadoPorSigla(estado);
		CidadeDao cidadeDao = new CidadeDao();
		objetoCidade = cidadeDao.buscaCidade(cidade, estadoStr);
	}
	
	
	
	public String getEnderecoCompleto() {
		String enderecoCompleto =   getEnderecoSimplificado() +
									(!CommonsUtil.semValor(cidade)? ", " + cidade:"") +
									(!CommonsUtil.semValor(estado)? ", " + estado:"") +
									(!CommonsUtil.semValor(cep)? ", " + cep:"");
									
		return enderecoCompleto;
	}
	
	public String getEnderecoSimplificado() {
		String enderecoCompleto =   (!CommonsUtil.semValor(endereco)? endereco:"") +
									(!CommonsUtil.semValor(bairro)? ", " + bairro:"") +
									(!CommonsUtil.semValor(complemento)? ", " + complemento:"") ;
									
		return enderecoCompleto;
	}
	

	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the endereco
	 */
	public String getEndereco() {
		return endereco;
	}

	/**
	 * @param endereco the endereco to set
	 */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	/**
	 * @return the bairro
	 */
	public String getBairro() {
		return bairro;
	}

	/**
	 * @param bairro the bairro to set
	 */
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	/**
	 * @return the complemento
	 */
	public String getComplemento() {
		return complemento;
	}

	/**
	 * @param complemento the complemento to set
	 */
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	/**
	 * @return the cidade
	 */
	public String getCidade() {
		return cidade;
	}

	/**
	 * @param cidade the cidade to set
	 */
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	/**
	 * @return the estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * @return the telResidencial
	 */
	public String getTelResidencial() {
		return telResidencial;
	}

	/**
	 * @param telResidencial the telResidencial to set
	 */
	public void setTelResidencial(String telResidencial) {
		this.telResidencial = telResidencial;
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
	 * @return the cep
	 */
	public String getCep() {
		return cep;
	}

	/**
	 * @param cep the cep to set
	 */
	public void setCep(String cep) {
		this.cep = cep;
	}	

	/**
	 * @return the numeroMatricula
	 */
	public String getNumeroMatricula() {
		return numeroMatricula;
	}

	/**
	 * @param numeroMatricula the numeroMatricula to set
	 */
	public void setNumeroMatricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}

	/**
	 * @return the cartorio
	 */
	public String getCartorio() {
		return cartorio;
	}

	/**
	 * @param cartorio the cartorio to set
	 */
	public void setCartorio(String cartorio) {
		this.cartorio = cartorio;
	}
	
	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the areaConstruida
	 */
	public String getAreaConstruida() {
		return areaConstruida;
	}

	/**
	 * @param areaConstruida the areaConstruida to set
	 */
	public void setAreaConstruida(String areaConstruida) {
		this.areaConstruida = areaConstruida;
	}

	/**
	 * @return the areaTotal
	 */
	public String getAreaTotal() {
		return areaTotal;
	}

	/**
	 * @param areaTotal the areaTotal to set
	 */
	public void setAreaTotal(String areaTotal) {
		this.areaTotal = areaTotal;
	}

	/**
	 * @return the linkGMaps
	 */
	public String getLinkGMaps() {
		return linkGMaps;
	}

	public String getPossuiDivida() {
		return possuiDivida;
	}

	public void setPossuiDivida(String possuiDivida) {
		this.possuiDivida = possuiDivida;
	}

	/**
	 * @param linkGMaps the linkGMaps to set
	 */
	public void setLinkGMaps(String linkGMaps) {
		this.linkGMaps = linkGMaps;
	}

	@Override  
	    public boolean equals(Object obj){  
	        if (this == obj)  
	            return true;  
	        if (obj == null)  
	            return false;  
	        if (!(obj instanceof ImovelCobranca))  
	            return false;  
	        ImovelCobranca other = (ImovelCobranca) obj;  
	        if (nome == null){  
	            if (other.nome != null)  
	                return false;  
	        } else if (!nome.equals(other.nome))  
	            return false;  
	        return true;  
	    }

	public String getOcupacao() {
		return ocupacao;
	}

	public void setOcupacao(String ocupacao) {
		this.ocupacao = ocupacao;
	}

	public BigDecimal getValoEstimado() {
		return valoEstimado;
	}

	public void setValoEstimado(BigDecimal valoEstimado) {
		this.valoEstimado = valoEstimado;
	}

	public Date getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	public String getIdadeCompra() {
		return idadeCompra;
	}

	public void setIdadeCompra(String idadeCompra) {
		this.idadeCompra = idadeCompra;
	}

	public String getNomeProprietario() {
		return nomeProprietario;
	}

	public void setNomeProprietario(String nomeProprietario) {
		this.nomeProprietario = nomeProprietario;
	}

	public boolean isComprovanteMatriculaCheckList() {
		return comprovanteMatriculaCheckList;
	}

	public void setComprovanteMatriculaCheckList(boolean comprovanteMatriculaCheckList) {
		this.comprovanteMatriculaCheckList = comprovanteMatriculaCheckList;
	}

	public boolean isComprovanteFotosImovelCheckList() {
		return comprovanteFotosImovelCheckList;
	}

	public void setComprovanteFotosImovelCheckList(boolean comprovanteFotosImovelCheckList) {
		this.comprovanteFotosImovelCheckList = comprovanteFotosImovelCheckList;
	}

	public boolean isComprovanteIptuImovelCheckList() {
		return comprovanteIptuImovelCheckList;
	}

	public void setComprovanteIptuImovelCheckList(boolean comprovanteIptuImovelCheckList) {
		this.comprovanteIptuImovelCheckList = comprovanteIptuImovelCheckList;
	}

	public BigDecimal getValorIptu() {
		return valorIptu;
	}

	public void setValorIptu(BigDecimal valorIptu) {
		this.valorIptu = valorIptu;
	}

	public BigDecimal getValorCondominio() {
		return valorCondominio;
	}

	public void setValorCondominio(BigDecimal valorCondominio) {
		this.valorCondominio = valorCondominio;
	}

	public String getNumeroCartorio() {
		return numeroCartorio;
	}

	public void setNumeroCartorio(String numeroCartorio) {
		this.numeroCartorio = numeroCartorio;
	}

	public String getCartorioEstado() {
		return cartorioEstado;
	}

	public void setCartorioEstado(String cartorioEstado) {
		this.cartorioEstado = cartorioEstado;
	}

	public String getCartorioMunicipio() {
		return cartorioMunicipio;
	}

	public void setCartorioMunicipio(String cartorioMunicipio) {
		this.cartorioMunicipio = cartorioMunicipio;
	}

	public boolean isCndIptuExtratoDebitoCheckList() {
		return cndIptuExtratoDebitoCheckList;
	}

	public void setCndIptuExtratoDebitoCheckList(boolean cndIptuExtratoDebitoCheckList) {
		this.cndIptuExtratoDebitoCheckList = cndIptuExtratoDebitoCheckList;
	}

	public boolean isCndCondominioExtratoDebitoCheckList() {
		return cndCondominioExtratoDebitoCheckList;
	}

	public void setCndCondominioExtratoDebitoCheckList(boolean cndCondominioExtratoDebitoCheckList) {
		this.cndCondominioExtratoDebitoCheckList = cndCondominioExtratoDebitoCheckList;
	}

	public boolean isMatriculaGaragemCheckList() {
		return matriculaGaragemCheckList;
	}

	public void setMatriculaGaragemCheckList(boolean matriculaGaragemCheckList) {
		this.matriculaGaragemCheckList = matriculaGaragemCheckList;
	}

	public boolean isSimuladorCheckList() {
		return simuladorCheckList;
	}

	public void setSimuladorCheckList(boolean simuladorCheckList) {
		this.simuladorCheckList = simuladorCheckList;
	}

	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}


	public Cidade getObjetoCidade() {
		return objetoCidade;
	}

	public void setObjetoCidade(Cidade objetoCidade) {
		this.objetoCidade = objetoCidade;
	}	
	
	public ImovelEstoque getImovelEstoque() {
		return imovelEstoque;
	}

	public void setImovelEstoque(ImovelEstoque imovelEstoque) {
		this.imovelEstoque = imovelEstoque;
	}

	
	public int getNumeroQuartos() {
		return numeroQuartos;
	}

	public void setNumeroQuartos(int numeroQuartos) {
		this.numeroQuartos = numeroQuartos;
	}

	public int getNumeroBanheiros() {
		return numeroBanheiros;
	}

	public void setNumeroBanheiros(int numeroBanheiros) {
		this.numeroBanheiros = numeroBanheiros;
	}

	public int getNumeroSuites() {
		return numeroSuites;
	}

	public void setNumeroSuites(int numeroSuites) {
		this.numeroSuites = numeroSuites;
	}

	public int getNumeroGaragens() {
		return numeroGaragens;
	}

	public void setNumeroGaragens(int numeroGaragens) {
		this.numeroGaragens = numeroGaragens;
	}

	public int getCategoria() {
		if (CommonsUtil.mesmoValor(this.getTipo(), "Apartamento")) {
			this.setSubCategoria(1);
			return 1;
		}
		if (CommonsUtil.mesmoValor(this.getTipo(), "Casa") || this.getTipo().toLowerCase().contains("condomínio")) {
			this.setSubCategoria(2);
			return 2;
		}
		return 0;
	}
	
	public String getNumeroImovel() {
		return numeroImovel;
	}

	public void separaEnderecoNumero(String string) {
		String[] split = string.split(",");
		if (split.length > 1) {
			this.enderecoSemNumero = split[0].trim();
			this.numeroImovel = split[1].trim();
		} else {
			this.enderecoSemNumero = string;
			this.numeroImovel = "";
		}
	}
	
	public String getEnderecoSemNumero() {
		return enderecoSemNumero;
	}

	public BigDecimal getValorMercado() {
		return valorMercado;
	}

	public void setValorMercado(BigDecimal valorMercado) {
		this.valorMercado = valorMercado;
	}

	public boolean isPreLaudoSolicitado() {
		return this.preLaudoSolicitado;	
	}
	
	public void setPreLaudoSolicitado( boolean preLaudoSolicitado) {
		this.preLaudoSolicitado = preLaudoSolicitado;
	}
	
	public boolean isPreLaudoEntregue() {
		return this.preLaudoEntregue;	
	}
	
	public void setPreLaudoEntregue( boolean preLaudoEntregue) {
		this.preLaudoEntregue = preLaudoEntregue;
	}
	

	public BigDecimal getValorPreLaudo() {
		return this.valorPreLaudo;
	}
	
	public void setValorPreLaudo( BigDecimal valorPreLaudo) {
		this.valorPreLaudo = valorPreLaudo;
	}

	public BigDecimal getValorLeilao() {
		return valorLeilao;
	}

	public void setValorLeilao(BigDecimal valorLeilao) {
		this.valorLeilao = valorLeilao;
	}

	public int getSubCategoria() {
		return subCategoria;
	}

	public void setSubCategoria(int subCategoria) {
		this.subCategoria = subCategoria;
	}

	
}
