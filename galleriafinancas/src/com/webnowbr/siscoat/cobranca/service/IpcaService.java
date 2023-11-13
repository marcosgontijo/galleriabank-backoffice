package com.webnowbr.siscoat.cobranca.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;

import br.com.galleriabank.debit.cliente.model.indice.Indice;
import br.com.galleriabank.debit.cliente.model.indice.TabelaIndices;

public class IpcaService {
	public void verificaNovoIPCA() {

		IPCADao ipcaDao = new IPCADao();

		DebitService debitService = new DebitService();
		TabelaIndices tabelaIndices = debitService.criarConsultaTabela("ipca");

		// este método pegara o último IPCA na base, com data anterior a data base,
		// mesmo que não do mesmo mês.
		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(DateUtil.getDataHoje());

		if (!CommonsUtil.semValor(tabelaIndices.getIndices())) {

			List<Indice> listNovosIpca = tabelaIndices.getIndices().stream()
					.filter(i -> i.getDataAsDate() != null && i.getDataAsDate().compareTo(ultimoIpca.getData()) > 0)
					.collect(Collectors.toList());

			for (Indice ultimoIndiceHistorico : listNovosIpca) {
				IPCA ipca = new IPCA();
				ipca.setData(ultimoIndiceHistorico.getDataAsDate());
				if (ultimoIndiceHistorico.getValor().compareTo(BigDecimal.ZERO) > 0)
					ipca.setTaxa(ultimoIndiceHistorico.getValor());
				else
					ipca.setTaxa(BigDecimal.ZERO);
				ipcaDao.create(ipca);
			}

		}

	}
}
