package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.vo.FileUploaded;
import com.webnowbr.siscoat.common.BancosEnum;

public class ContasPagar implements Serializable {

	private static final long serialVersionUID = 1L;
	private long id;
	private Date dataPrevista;
	private String descricao;
	private ContratoCobranca contrato;
	private BigDecimal valor;
	private boolean contaPaga;
	private Date dataPagamento;

	private Date dataVencimento;
	private BigDecimal valorPagamento;
	private String numeroDocumento;
	private PagadorRecebedor pagadorRecebedor;
	private String tipoDespesa;
	private ContaContabil contaContabil;
	private String observacao;
	
	private String formaTransferencia;
	private String pix;
	private String nomeTed;
	private String cpfTed;
	private String bancoTed;
	private String contaTed;
	private String digitoContaTed;
	private String agenciaTed;
	private String ispb;
	
	private String fileListId;
	
	private Responsavel responsavel;
	
	private String linhaDigitavelStarkBank;
	private String descricaoStarkBank;
	private String numeroDocumentoPagadorStarkBank;
	
	private StarkBankBoleto comprovantePagamentoStarkBank;
	private StarkBankPix comprovantePagamentoPixStarkBank;
	
	private String tipoContaBancaria;
	
	private boolean contaCartaSplit;
	
	private List<StarkBankBaixa> listContasPagarBaixas = new ArrayList<StarkBankBaixa>();
	
	private Collection<FileUploaded> filesContas = new ArrayList<FileUploaded>();
	
<<<<<<< HEAD
	private ContasPagar ContasPagarOriginal;
	
=======
>>>>>>> refs/heads/master
	private ContasPagar contaPagarOriginal = null;
	private boolean editada;
	
	private Date dataCriacao;
	private String userCriacao;

	public ContasPagar() {
	}
	
	public ContasPagar(ContasPagar contaOriginal) {
		super();
		this.dataPrevista = contaOriginal.getDataPrevista();
		this.descricao = contaOriginal.getDescricao();
		this.contrato = contaOriginal.getContrato();
		this.valor = contaOriginal.getValor();
		this.contaPaga = contaOriginal.isContaPaga();
		this.dataPagamento = contaOriginal.getDataPagamento();
		this.dataVencimento = contaOriginal.getDataVencimento();
		this.valorPagamento = contaOriginal.getValorPagamento();
		this.numeroDocumento = contaOriginal.getNumeroDocumento();
		this.pagadorRecebedor = contaOriginal.getPagadorRecebedor();
		this.tipoDespesa = contaOriginal.getTipoDespesa();
		this.contaContabil = contaOriginal.getContaContabil();
		this.observacao = contaOriginal.getObservacao();
		this.formaTransferencia = contaOriginal.getFormaTransferencia();
		this.pix = contaOriginal.getPix();
		this.nomeTed = contaOriginal.getNomeTed();
		this.cpfTed = contaOriginal.getCpfTed();
		this.bancoTed = contaOriginal.getBancoTed();
		this.contaTed = contaOriginal.getContaTed();
		this.digitoContaTed = contaOriginal.getDigitoContaTed();
		this.agenciaTed = contaOriginal.getAgenciaTed();
		this.fileListId = contaOriginal.getFileListId();
		this.responsavel = contaOriginal.getResponsavel();
		this.linhaDigitavelStarkBank = contaOriginal.getLinhaDigitavelStarkBank();
		this.descricaoStarkBank = contaOriginal.getDescricaoStarkBank();
		this.numeroDocumentoPagadorStarkBank = contaOriginal.getNumeroDocumentoPagadorStarkBank();
		this.comprovantePagamentoStarkBank = contaOriginal.getComprovantePagamentoStarkBank();
		this.comprovantePagamentoPixStarkBank = contaOriginal.getComprovantePagamentoPixStarkBank();
		this.tipoContaBancaria = contaOriginal.getTipoContaBancaria();
		this.contaCartaSplit = contaOriginal.isContaCartaSplit();
		this.listContasPagarBaixas = contaOriginal.getListContasPagarBaixas();
		this.filesContas = contaOriginal.getFilesContas();
	}

	public List<String> completeBancos(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> bancos = new ArrayList<>();
		for (BancosEnum banco : BancosEnum.values()) {
			String bancoStr = banco.getNomeCompleto().toString();
			bancos.add(bancoStr);
		}
		return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}

	public List<String> contaPagarDescricaoLista(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> listaNome = new ArrayList<>();
		listaNome.add("Crédito CCI");
		listaNome.add("Transferência");
		listaNome.add("ITBI");
		listaNome.add("Cartório");
		listaNome.add("Certidão");
		listaNome.add("Certidão de Casamento");
		listaNome.add("Honorário");
		listaNome.add("Devolução");
		listaNome.add("IPTU");
		listaNome.add("Condomínio");
		listaNome.add("IQ");
		listaNome.add("Laudo");
		listaNome.add("Processo");
		listaNome.add("Averbação");
		listaNome.add("Comissão");
		listaNome.add("Registro");
		listaNome.add("Laudo De Avaliação");
		listaNome.add("Crédito Cliente");

		return listaNome.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
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

	public Date getDataPrevista() {
		return dataPrevista;
	}

	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public boolean isContaPaga() {
		return contaPaga;
	}

	public void setContaPaga(boolean contaPaga) {
		this.contaPaga = contaPaga;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	
	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public PagadorRecebedor getPagadorRecebedor() {
		return pagadorRecebedor;
	}

	public void setPagadorRecebedor(PagadorRecebedor pagadorRecebedor) {
		this.pagadorRecebedor = pagadorRecebedor;
	}

	public String getTipoDespesa() {
		return tipoDespesa;
	}

	public void setTipoDespesa(String tipoDespesa) {
		this.tipoDespesa = tipoDespesa;
	}

	public ContaContabil getContaContabil() {
		return contaContabil;
	}

	public void setContaContabil(ContaContabil contaContabil) {
		this.contaContabil = contaContabil;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public String getFormaTransferencia() {
		return formaTransferencia;
	}

	public void setFormaTransferencia(String formaTransferencia) {
		this.formaTransferencia = formaTransferencia;
	}

	public String getPix() {
		return pix;
	}

	public void setPix(String pix) {
		this.pix = pix;
	}

	public String getNomeTed() {
		return nomeTed;
	}

	public void setNomeTed(String nomeTed) {
		this.nomeTed = nomeTed;
	}

	public String getCpfTed() {
		return cpfTed;
	}

	public void setCpfTed(String cpfTed) {
		this.cpfTed = cpfTed;
	}

	public String getBancoTed() {
		return bancoTed;
	}

	public void setBancoTed(String bancoTed) {
		this.bancoTed = bancoTed;
	}

	public String getContaTed() {
		return contaTed;
	}

	public void setContaTed(String contaTed) {
		this.contaTed = contaTed;
	}

	public String getAgenciaTed() {
		return agenciaTed;
	}

	public void setAgenciaTed(String agenciaTed) {
		this.agenciaTed = agenciaTed;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getFileListId() {
		return fileListId;
	}

	public void setFileListId(String fileListId) {
		this.fileListId = fileListId;
	}

	public Collection<FileUploaded> getFilesContas() {
		return filesContas;
	}

	public void setFilesContas(Collection<FileUploaded> filesContas) {
		this.filesContas = filesContas;
	}

	public String getLinhaDigitavelStarkBank() {
		return linhaDigitavelStarkBank;
	}

	public void setLinhaDigitavelStarkBank(String linhaDigitavelStarkBank) {
		this.linhaDigitavelStarkBank = linhaDigitavelStarkBank;
	}

	public String getDescricaoStarkBank() {
		return descricaoStarkBank;
	}

	public void setDescricaoStarkBank(String descricaoStarkBank) {
		this.descricaoStarkBank = descricaoStarkBank;
	}

	public String getNumeroDocumentoPagadorStarkBank() {
		return numeroDocumentoPagadorStarkBank;
	}

	public void setNumeroDocumentoPagadorStarkBank(String numeroDocumentoPagadorStarkBank) {
		this.numeroDocumentoPagadorStarkBank = numeroDocumentoPagadorStarkBank;
	}

	public StarkBankBoleto getComprovantePagamentoStarkBank() {
		return comprovantePagamentoStarkBank;
	}

	public void setComprovantePagamentoStarkBank(StarkBankBoleto comprovantePagamentoStarkBank) {
		this.comprovantePagamentoStarkBank = comprovantePagamentoStarkBank;
	}

	public StarkBankPix getComprovantePagamentoPixStarkBank() {
		return comprovantePagamentoPixStarkBank;
	}

	public void setComprovantePagamentoPixStarkBank(StarkBankPix comprovantePagamentoPixStarkBank) {
		this.comprovantePagamentoPixStarkBank = comprovantePagamentoPixStarkBank;
	}

	public String getDigitoContaTed() {
		return digitoContaTed;
	}

	public void setDigitoContaTed(String digitoContaTed) {
		this.digitoContaTed = digitoContaTed;
	}

	public List<StarkBankBaixa> getListContasPagarBaixas() {
		return listContasPagarBaixas;
	}

	public void setListContasPagarBaixas(List<StarkBankBaixa> listContasPagarBaixas) {
		this.listContasPagarBaixas = listContasPagarBaixas;
	}

	public boolean isContaCartaSplit() {
		return contaCartaSplit;
	}

	public void setContaCartaSplit(boolean contaCartaSplit) {
		this.contaCartaSplit = contaCartaSplit;
	}

<<<<<<< HEAD
	public ContasPagar getContasPagarOriginal() {
		return ContasPagarOriginal;
	}

	public void setContasPagarOriginal(ContasPagar contasPagarOriginal) {
		ContasPagarOriginal = contasPagarOriginal;
	}

=======
>>>>>>> refs/heads/master
	public String getTipoContaBancaria() {
		return tipoContaBancaria;
	}

	public void setTipoContaBancaria(String tipoContaBancaria) {
		this.tipoContaBancaria = tipoContaBancaria;
	}

	public ContasPagar getContaPagarOriginal() {
		return contaPagarOriginal;
	}

	public void setContaPagarOriginal(ContasPagar contaPagarOriginal) {
		this.contaPagarOriginal = contaPagarOriginal;
	}

	public boolean isEditada() {
		return editada;
	}

	public void setEditada(boolean editada) {
		this.editada = editada;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getUserCriacao() {
		return userCriacao;
	}
	
	public void setUserCriacao(String userCriacao) {
		this.userCriacao = userCriacao;
	}

	public String getIspb() {
		return ispb;
	}

	public void setIspb(String ispb) {
		this.ispb = ispb;
	}
}