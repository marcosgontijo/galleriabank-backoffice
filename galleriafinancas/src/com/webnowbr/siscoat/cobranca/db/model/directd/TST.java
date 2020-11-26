package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TST implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * EXEMPLO DE MENSAGEM
		{
		    "ConsultaUid": "2fimplmfhfi6dwernibx4wf1r",
		    "IdTipo": 1,
		    "Tipo": "Sucesso",
		    "Mensagem": "Sucesso. ",
		    "TempoExecucaoMs": 58211,
		    "CustoTotalEmCreditos": 1,
		    "SaldoEmCreditos": 10,
		    "ApiVersion": "v1",
		    "Retorno": 
		    {
		        "Nome": "ENTREGAS EXPRESSAS ME.",
		        "CPF": null,
		        "CNPJ": "91981505000115",
		        "Numero": "123321456",
		        "Ano": "2018",
		        "Expedicao": "06/12/2018, às 09:32:08",
		        "Validade": "03/06/2019",
		        "Consta": true,
		        "TotalProcessos": 4,
		        "ListaProcessos": 
		    [
		    "0012345-67.2004.5.01.0006 - TRT 01ª Região *",
		    "0175300-48.1234.5.01.4321 - TRT 01ª Região **",
		    "0111100-65.1000.5.01.0001 - TRT 01ª Região *",
		    "0004455-26.0003.5.01.0009 - TRT 01ª Região *"
		        ],
		        "Observacoes": "* Débito garantido por depósito, bloqueio de numerário ou penhora de bens suficientes. ** Débito com exigibilidade suspensa."
		    }
		}
	*****
	*****/
	
	private InfoServico infoServico;
	private String nome;
	private String cpf;
	private String cnpj;
	private String numero;
	private String ano;
	private String expedicao;
	private Date validade;
	private boolean consta;
	private int totalProcessos;
	private List<String> listaProcessos;
	private String observacoes;
	
	public TST() {
		this.infoServico = new InfoServico();
	}

	public InfoServico getInfoServico() {
		return infoServico;
	}

	public void setInfoServico(InfoServico infoServico) {
		this.infoServico = infoServico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

	public boolean isConsta() {
		return consta;
	}

	public void setConsta(boolean consta) {
		this.consta = consta;
	}

	public int getTotalProcessos() {
		return totalProcessos;
	}

	public void setTotalProcessos(int totalProcessos) {
		this.totalProcessos = totalProcessos;
	}

	public List<String> getListaProcessos() {
		return listaProcessos;
	}

	public void setListaProcessos(List<String> listaProcessos) {
		this.listaProcessos = listaProcessos;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getExpedicao() {
		return expedicao;
	}

	public void setExpedicao(String expedicao) {
		this.expedicao = expedicao;
	}
}