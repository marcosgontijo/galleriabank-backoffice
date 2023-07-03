package com.webnowbr.siscoat.cobranca.rest;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.mb.IuguMB;

@Path("/iugu")
public class IuguService {
	
	private final Logger logger = LoggerFactory.getLogger(IuguService.class);
	
	private List<ContratoCobranca> objetoContratoCobrancaList;
	private IuguMB iuguMB = new IuguMB();
	
	@POST
	@Path("/CriarFatura")
	@Produces(MediaType.APPLICATION_JSON)
	public Response criarFatura(String dadosEntrada) { 
		logger.info("Inicio Contract Service - Criar Fatura ");
		
		JSONObject dadosEntradaAPP = new JSONObject(dadosEntrada);
		
		iuguMB.setContratoCobranca(new ContratoCobranca());
		iuguMB.getContratoCobranca().setNumeroContrato(dadosEntradaAPP.has("numeroContrato") ? dadosEntradaAPP.getString("numeroContrato") : null);	
		iuguMB.setValorItem(dadosEntradaAPP.has("valorFatura") ? new BigDecimal(dadosEntradaAPP.getString("valorFatura")) : null);	
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.objetoContratoCobrancaList = contratoCobrancaDao.findByFilter("numeroContrato", iuguMB.getContratoCobranca().getNumeroContrato());
		if(this.objetoContratoCobrancaList.isEmpty()) {
			String message = "{\"retorno\": \"[Galleria Bank] Numero do Contrato não foi encontrato!!!\"}";
			logger.warn("Fatura Service - Criar Fatura - Numero do Contrato não foi encontrato !!!");
				      .status(Response.Status.FORBIDDEN)
				      .entity(message)
				      .type(MediaType.APPLICATION_JSON)
				      .build();	
		}else {
			iuguMB.setContratoCobranca(this.objetoContratoCobrancaList.get(0));
				String message = iuguMB.geraCobrancaSimplesIuguApi(iuguMB.getContratoCobranca());
				if(message.contains("https://faturas.iugu.com/")) {
					logger.info("Fim Iugu Service - Criar Fatura - Fatura criada com sucesso !!!", message);
					return Response
							.status(Response.Status.OK)
							.entity(message)
							.type(MediaType.APPLICATION_JSON)
							.build();
				}else {
					logger.warn("Fim Iugu Service - Criar Fatura - Ocorreu um erro na geração da Cobrança!");
					return Response
						      .status(Response.Status.BAD_REQUEST)
						      .entity(message)
						      .type(MediaType.APPLICATION_JSON)
						      .build();	
				}
		}
	}

}
