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
		omieRequestBase.setApp_key("2935241731422");
		omieRequestBase.setApp_secret("88961e398f6eaa1df414837312d5bd71");
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
	
	@Test
	void test2() {
		
		long[] contas = new long[4];
		
		contas[0] = 3297923118l; //BB Sec
		contas[1] = 3303125728l; //Inter Sec
		contas[2] = 3303126311l; //Bradesco Sec
		contas[3] = 3303154498l; //Itaú Sec
		
		
	
		
		OmieRequestBase omieRequestBase = new OmieRequestBase();
		omieRequestBase.setApp_key("2935241731422");
		omieRequestBase.setApp_secret("88961e398f6eaa1df414837312d5bd71");
		omieRequestBase.setCall("ListarExtrato");
		List<IOmieParam> params = new ArrayList<>();
		
	for (int i=0; i< contas.length; i++) {
		
		ListarExtratoRequest listarExtratoRequest = new ListarExtratoRequest();
		listarExtratoRequest.setcCodIntCC("");
		listarExtratoRequest.setnCodCC(contas[i]);
		listarExtratoRequest.setdPeriodoInicial("01/03/2023");
		listarExtratoRequest.setdPeriodoFinal("31/03/2023");
		listarExtratoRequest.setcExibirApenasSaldo("S");
		
		params = new ArrayList<>();
		params.add(listarExtratoRequest);
		
		
		omieRequestBase.setParam(params);

		OmieService omieService = new OmieService();
		OmieListarExtratoResponse omieListarExtratoResponse = omieService.listarExtratoResponse(omieRequestBase);
		
		System.out.println(GsonUtil.toJson(omieListarExtratoResponse));
}
	}
	}

