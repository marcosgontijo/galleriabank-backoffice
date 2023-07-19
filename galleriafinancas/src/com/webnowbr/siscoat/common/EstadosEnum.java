package com.webnowbr.siscoat.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

public enum EstadosEnum {
	ACRE("Acre", "AC", "6d451d2b-e333-9a97-acff-bf832dfcb368"),
	ALAGOAS( "Alagoas", "AL", "14902efc-897d-7ba3-d21e-0824e12cb1aa"),
	AMAPÁ("Amapá", "AP", "fc12c3fa-df70-b5e0-7f4d-f27d8221c760"),
	AMAZONAS("Amazonas", "AM", "6b3a9936-4685-6b0f-cc9d-b64cbdc7089f"),
	BAHIA("Bahia", "BA", "cc9afeac-65de-6b05-3073-3271f168ebb0"),
	CEARÁ("Ceará", "CE", "e90c767b-f596-3049-a3db-016646975350"),
	DISTRITO_FEDERAL("Distrito Federal", "DF","607abcf9-d7a6-8b26-96c6-13cf0301f3de"),
	ESPÍRITO_SANTO("Espírito Santo", "ES","bb161fb0-dc35-b39c-526e-566366abebd9"),
	GOIÁS("Goiás", "GO", "85c20a18-f45b-6aa4-fa32-f4a08fae761f"),
	MARANHÃO("Maranhão", "MA", "a4e885d8-128c-bcf0-40a4-e4174551080b"),
	MATO_GROSSO("Mato Grosso", "MT","1de80e17-2871-c798-bfb3-f66a4d7c9a7c"),
	MATO_GROSSO_DO_SUL("Mato Grosso do Sul", "MS","fafb2447-d929-d715-bdf1-15c1b6df28a0"),
	MINAS_GERIAS("Minas Gerais", "MG","b374cd92-4a27-f25f-c1e0-9aa6aed81d32"),
	PARÁ("Pará", "PA", "2ac26f93-c3aa-e04d-1add-87562c187e9b"),
	PARAÍBA("Paraíba", "PB", "684f0ccf-6729-776f-4012-2a74fb31bd91"),
	PARANÁ("Paraná", "PR", "e0992d76-53fb-4933-cec1-eb51b79d63bb"),
	PERNAMBUCO("Pernambuco", "PE", "360d9f3c-3eab-b482-5b0f-6e5a0a09944e"),
	PIAUÍ("Piauí", "PI","eb6564fc-4232-0ca7-e5ee-ba2c328746a2"),
	//RIO_DE_JANEIRO("Rio de Janeiro", "RJ","074c0821-bf46-c6b6-5f7c-e39f5df39f98"),
	RIO_GRANDE_DO_NORTE("Rio Grande do Norte", "RN", "f4e7b2e6-2e75-b146-2826-67f9072e8d2d"),
	RIO_GRANDE_DO_SUL("Rio Grande do Sul", "RS", "76925d4c-a65f-4936-f876-bc753fcfbb38"),
	RONDÔNIA("Rondônia", "RO", "93164dfb-3ef9-467f-26f0-5b52e8bad2e3"),
	RORAIMA("Roraima", "RR", "d6565664-02ae-bdd8-55c3-9062d08a4e11"),	
	SANTA_CATARINA("Santa Catarina", "SC", "82bd7834-e4f0-aacf-0bf2-53f5bc5ac00c"),
	SÃO_PAULO("São Paulo", "SP", "52f0da38-2fb5-4a87-22ef-32670b94d916"),
	SERGIPE("Sergipe", "SE", "6b443de5-0b7e-b5c4-73ba-1689fc5dbb03"),
	TOCANTINS("Tocantins", "TO", "d358c3f6-aa96-69ed-f6e0-1e1133d87b51");

	private String nome;
	private String uf;
	private String idDocket;
	private String nomeComposto;
	private EstadosEnum(String nome, String uf, String idDocket) {
		this.nome = nome;
		this.uf = uf;	
		this.idDocket = idDocket;
		nomeComposto = uf + " - " + nome;
	}
	
	public static EstadosEnum getByUf(String uf) {
		for(EstadosEnum estado : EstadosEnum.values()) {
			if(CommonsUtil.mesmoValor(uf, estado.getUf())) {
				return estado;
			}
		}
		return null;
	}
	
	/*List<SelectItem> listaEstados =  pesquisaEstadosListaNome();
	
	public List<String> completeEstados(String query) {
	    String queryLowerCase = query.toLowerCase();
	    List<String> bancos = new ArrayList<>();
	    for(EstadosEnum banco : EstadosEnum.values()) {
	    	String bancoStr = banco.getNome().toString();
	    	bancos.add(bancoStr);
	    }
	    return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}
	
	public List<SelectItem> pesquisaEstadosListaNome() {
		System.out.println("PagadorRecebedor metodo - pesquisaBancosListaNome");
		List<SelectItem> listaEstados= new ArrayList<>();
		for(EstadosEnum estado : EstadosEnum.values()) {
			SelectItem item = new SelectItem(estado);
			listaEstados.add(item);
		}
		return listaEstados;
	}*/

	public String getNome() {
		return this.nome;
	}
	
	public String getUf() {
		return uf;
	}

	public String getIdDocket() {
		return idDocket;
	}

	public String getNomeComposto() {
		return nomeComposto;
	}
}

