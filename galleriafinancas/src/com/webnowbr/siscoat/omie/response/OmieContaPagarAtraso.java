package com.webnowbr.siscoat.omie.response;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OmieContaPagarAtraso {
	BigInteger nIdTitulo;
	BigInteger nIdCliente;
	private String cNomeCliente;
	BigDecimal vDoc;
	private String dVencimento;
	private String dEmissao;
	private String cCodCateg;
	private String cDescCateg;
	BigDecimal nDiasAtraso;
	private String cIcone;
	private String cCor;
	private String cUrlLogoBanco;
	
	public BigInteger getnIdTitulo() {
		return nIdTitulo;
	}
	public void setnIdTitulo(BigInteger nIdTitulo) {
		this.nIdTitulo = nIdTitulo;
	}
	public BigInteger getnIdCliente() {
		return nIdCliente;
	}
	public void setnIdCliente(BigInteger nIdCliente) {
		this.nIdCliente = nIdCliente;
	}
	public String getcNomeCliente() {
		return cNomeCliente;
	}
	public void setcNomeCliente(String cNomeCliente) {
		this.cNomeCliente = cNomeCliente;
	}
	public BigDecimal getvDoc() {
		return vDoc;
	}
	public void setvDoc(BigDecimal vDoc) {
		this.vDoc = vDoc;
	}
	public String getdVencimento() {
		return dVencimento;
	}
	public void setdVencimento(String dVencimento) {
		this.dVencimento = dVencimento;
	}
	public String getdEmissao() {
		return dEmissao;
	}
	public void setdEmissao(String dEmissao) {
		this.dEmissao = dEmissao;
	}
	public String getcCodCateg() {
		return cCodCateg;
	}
	public void setcCodCateg(String cCodCateg) {
		this.cCodCateg = cCodCateg;
	}
	public String getcDescCateg() {
		return cDescCateg;
	}
	public void setcDescCateg(String cDescCateg) {
		this.cDescCateg = cDescCateg;
	}
	public BigDecimal getnDiasAtraso() {
		return nDiasAtraso;
	}
	public void setnDiasAtraso(BigDecimal nDiasAtraso) {
		this.nDiasAtraso = nDiasAtraso;
	}
	public String getcIcone() {
		return cIcone;
	}
	public void setcIcone(String cIcone) {
		this.cIcone = cIcone;
	}
	public String getcCor() {
		return cCor;
	}
	public void setcCor(String cCor) {
		this.cCor = cCor;
	}
	public String getcUrlLogoBanco() {
		return cUrlLogoBanco;
	}
	public void setcUrlLogoBanco(String cUrlLogoBanco) {
		this.cUrlLogoBanco = cUrlLogoBanco;
	}
	
	
	
}
