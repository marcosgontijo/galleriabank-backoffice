package com.webnowbr.siscoat.cobranca.mb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

@Path("/iugu")
public class WebServiceMB {

	/**
	 * ENDPOINT para recebimento de mudança de status IUGU
	 * URL http://localhost:8080/siscoat/rest/iugu/changeFaturaStatus
	 * 
	 * Lista de Dados enviados:
	 * event: invoice.installment_released
	 * data[id]: 1757E1D7FD5E410A9C563024250015BF
	 * data[account_id]: 70CA234077134ED0BF2E0E46B0EDC36F
	 * data[status]: paid
	 * data[subscription_id]: F4115E5E28AE4CCA941FCCCCCABE9A0A
	 * data[installment]: 1
	 * data[number_of_installments]: 12
	 * data[amount]: 10.00	
	 * 	
	 * @param idFatura
	 * @param status
	 * @return
	 */
	@POST
	@Path("/changeFaturaStatus")
	@Consumes("application/x-www-form-urlencoded")
	public Response iuguPost(@FormParam("data[id]") String idFaturaIugu, @FormParam("data[account_id]") String idContaIugu, @FormParam("data[status]") String status) {
		System.out.println("IUGU (1 - iuguPost) - ID da Fatura: " + idFaturaIugu);
		System.out.println("IUGU (2 - iuguPost) - STATUS da Fatura: " + status);
		System.out.println("IUGU (X - iuguPost) - ID conta: " + idContaIugu);
		
		IuguMB iuguMB = new IuguMB();
		iuguMB.processaMudancaStatusFaturaIugu(idFaturaIugu, idContaIugu);
		
		return Response.status(200).build();
	}
	
	/**
	 * EXEMPLO DE ENDPOINT GET COM PARAMETRO
	 */
	@GET
	@Path("/{param}")
	public Response printMessage(@PathParam("param") String msg) {

		String result = "Restful example : " + msg;

		return Response.status(200).entity(result).build();

	}	
	
	/**
	 * EXEMPLO DE ENDPOINT POST COM JSON
	 */
	// http://localhost:8080/siscoat/rest/iugu/changeFaturaStatus
	@POST
	@Path("/changeFaturaStatusJSON")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bar(String jsonIugu) {
		JSONObject response = new JSONObject(jsonIugu);
		
		/*
		 * 
		    event: invoice.installment_released
			data[id]: 1757E1D7FD5E410A9C563024250015BF
			data[account_id]: 70CA234077134ED0BF2E0E46B0EDC36F
			data[status]: paid
			data[subscription_id]: F4115E5E28AE4CCA941FCCCCCABE9A0A
			data[installment]: 1
			data[number_of_installments]: 12
			data[amount]: 10.00		
		 * 
		 */
		
		return Response.status(200).build();
	}
}