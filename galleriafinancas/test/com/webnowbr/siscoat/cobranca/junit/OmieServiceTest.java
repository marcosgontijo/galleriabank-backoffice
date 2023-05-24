package com.webnowbr.siscoat.cobranca.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.webnowbr.siscoat.cobranca.service.OmieService;
import com.webnowbr.siscoat.omie.request.IOmieParam;
import com.webnowbr.siscoat.omie.request.ListarExtratoRequest;
import com.webnowbr.siscoat.omie.request.ObterResumoFinRequest;
import com.webnowbr.siscoat.omie.request.OmieRequestBase;
import com.webnowbr.siscoat.omie.response.OmieListarExtratoResponse;
import com.webnowbr.siscoat.omie.response.OmieObterResumoFinResponse;

import br.com.galleriabank.serasarelato.cliente.util.GsonUtil;

class OmieServiceTest {

	@Test
	void test() {

		OmieRequestBase omieRequestBase = new OmieRequestBase();
		omieRequestBase.setApp_key("2935249398081");
		omieRequestBase.setApp_secret("93ae368e030f73844558bfb4eaabf71b");
		omieRequestBase.setCall("ObterResumoFinancas");
		List<IOmieParam> params = new ArrayList<>();

		ObterResumoFinRequest obterResumoFinRequest = new ObterResumoFinRequest();
		obterResumoFinRequest.setdDia("15/05/2023");
		obterResumoFinRequest.setlApenasResumo(true);
		obterResumoFinRequest.setlExibirCategoria(false);
		params.add(obterResumoFinRequest);

		omieRequestBase.setParam(params);

		OmieService omieService = new OmieService();
		OmieObterResumoFinResponse omieObterResumoFinResponse = omieService.obterResumoFinancas(omieRequestBase);

		System.out.print(GsonUtil.toJson(omieObterResumoFinResponse));
	}

	void test2() {
		OmieRequestBase omieRequestBase = new OmieRequestBase();
		omieRequestBase.setApp_key("2935249398081");
		omieRequestBase.setApp_secret("93ae368e030f73844558bfb4eaabf71b");
		omieRequestBase.setCall("ObterExtrato");
		List<IOmieParam> params = new ArrayList<>();
		
		ListarExtratoRequest listarExtratoRequest = new ListarExtratoRequest();
		listarExtratoRequest.setcCodIntCC(null);
		listarExtratoRequest.setnCodCC(null);
		listarExtratoRequest.setdPeriodoInicial("01/03/2023");
		listarExtratoRequest.setdPeriodoFinal("31/03/2023");
		listarExtratoRequest.setcExibirApenasSaldo(null);
		params.add(listarExtratoRequest);
		
		omieRequestBase.setParam(params);

		OmieService omieService = new OmieService();
		OmieListarExtratoResponse omieListarExtratoResponse = omieService.listarExtratoResponse(omieRequestBase);
		
		System.out.print(GsonUtil.toJson(omieListarExtratoResponse));
}
	}
