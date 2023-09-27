package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ReaWebhookRetorno {

	public String id;
	public String nome;
	public String status;
	public String dataCriacao;
	public Integer quantidadeBlocosGravames;
	public List<Integer> paginasRotacionadas; /*
												 * ":[ 1, 2, 3 ],
												 */
	public String urlWebhook;
	public ReaWebhookRetornoArquivo arquivo;
	public List<ReaWebhookRetornoBloco> blocos;

	List<String> lstCompaVenda = Arrays.asList("compromisso de compra e venda", "compra e venda",
			"descritivo do imóvel");

	ReaWebhookRetornoBloco proprietarioAtual = null;
	List<ReaWebhookRetornoBloco> proprietariosAnterior = new ArrayList<>();

	public void buscaProprietarios() {
		List<ReaWebhookRetornoBloco> blocosComProprietarios = blocos.stream()
				.filter(b -> !CommonsUtil.semValor(b.getConteudo())
						&& !CommonsUtil.semValor(b.getConteudo().getExtraido())
						&& !CommonsUtil.semValor(b.getConteudo().getExtraido().getProprietarios())
						&& !CommonsUtil.semValor(b.getConteudo().getExtraido().getProprietarios().getDadosProprietarios())

				).sorted(Comparator.comparingInt(ReaWebhookRetornoBloco::getNumeroSequencia).reversed())
				.collect(Collectors.toList());

		for (ReaWebhookRetornoBloco reaWebhookRetornoBloco : blocosComProprietarios) {
			if (proprietarioAtual == null && reaWebhookRetornoBloco.relacionadoAoProprietarioAtual) {
				proprietarioAtual = reaWebhookRetornoBloco;
			} else if ( !reaWebhookRetornoBloco.relacionadoAoProprietarioAtual) {
				proprietariosAnterior.add(reaWebhookRetornoBloco);
			}
		} 
	}

//	private ReaWebhookRetornoBloco getProprietarioAtual() {
//		ReaWebhookRetornoBloco proprietarioAtual = blocos.stream()
//				.filter(b -> ( CommonsUtil.mesmoValor(b.getTipo(), "PROPRIETARIO")
//						&& lstCompaVenda.contains(b.getNomeClassificacao().toLowerCase()) ||
//						CommonsUtil.mesmoValor(b.getTipo(), "IMOVEL")
//						&& lstCompaVenda.contains(b.getNomeClassificacao().toLowerCase())
//						) && b.isRelacionadoAoProprietarioAtual())
//				.findFirst().orElse(null);
//		return proprietarioAtual;
//	}
//
//	private ReaWebhookRetornoBloco getProprietarioAnterior() {
//		ReaWebhookRetornoBloco proprietarioAtual = getProprietarioAtual();
//		ReaWebhookRetornoBloco proprietarioAnterior = blocos.stream()
//				.sorted(Comparator.comparingInt(ReaWebhookRetornoBloco::getNumeroSequencia))
//				.filter(b -> ( CommonsUtil.mesmoValor(b.getTipo(), "PROPRIETARIO")
//						&& lstCompaVenda.contains(b.getNomeClassificacao().toLowerCase()) ||
//						CommonsUtil.mesmoValor(b.getTipo(), "IMOVEL")
//						&& lstCompaVenda.contains(b.getNomeClassificacao().toLowerCase())
//						)  && !b.isRelacionadoAoProprietarioAtual()
//						&& b.numeroSequencia < proprietarioAtual.getNumeroSequencia())
//				.findFirst().orElse(null);
//		return proprietarioAnterior;
//	}
//	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Integer getQuantidadeBlocosGravames() {
		return quantidadeBlocosGravames;
	}

	public void setQuantidadeBlocosGravames(Integer quantidadeBlocosGravames) {
		this.quantidadeBlocosGravames = quantidadeBlocosGravames;
	}

	public List<Integer> getPaginasRotacionadas() {
		return paginasRotacionadas;
	}

	public void setPaginasRotacionadas(List<Integer> paginasRotacionadas) {
		this.paginasRotacionadas = paginasRotacionadas;
	}

	public String getUrlWebhook() {
		return urlWebhook;
	}

	public void setUrlWebhook(String urlWebhook) {
		this.urlWebhook = urlWebhook;
	}

	public ReaWebhookRetornoArquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(ReaWebhookRetornoArquivo arquivo) {
		this.arquivo = arquivo;
	}

	public List<ReaWebhookRetornoBloco> getBlocos() {
		return blocos;
	}

	public void setBlocos(List<ReaWebhookRetornoBloco> blocos) {
		this.blocos = blocos;
	}

	public ReaWebhookRetornoBloco getProprietarioAtual() {
		return proprietarioAtual;
	}

	public void setProprietarioAtual(ReaWebhookRetornoBloco proprietarioAtual) {
		this.proprietarioAtual = proprietarioAtual;
	}

	public List<ReaWebhookRetornoBloco> getProprietariosAnterior() {
		return proprietariosAnterior;
	}

	public void setProprietariosAnterior(List<ReaWebhookRetornoBloco> proprietariosAnterior) {
		this.proprietariosAnterior = proprietariosAnterior;
	}


}
