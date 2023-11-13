package com.webnowbr.siscoat.cobranca.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webnowbr.siscoat.cobranca.model.request.FichaIndividualRequest;
import com.webnowbr.siscoat.cobranca.service.RelatoriosService;
import com.webnowbr.siscoat.common.GsonUtil;

import net.sf.jasperreports.engine.JRException;

@Path("/report")
public class ReportService {

	private final Logger logger = LoggerFactory.getLogger(ReportService.class);

	@GET
	@Path("/PDFPAprovadoComite/{idContrato}")
//	@Produces(MediaType.APPLICATION_JSON)
	public Response pDFPAprovadoComite(@PathParam("idContrato") long idContrato,
			@HeaderParam("Authorization") String authorization) {
		logger.info("Inicio Report Servicews - PDFPAprovadoComite ");
		if (RestService.verificarAutenticacao(authorization)) {
			byte[] jp = null;
			RelatoriosService relatorioService = new RelatoriosService();
			try {
				jp = relatorioService.geraPDFPAprovadoComiteByteArray(idContrato);

				return Response.status(Response.Status.OK).entity(jp).type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
						.build();

			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao gerar relatório")
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed!!!\"}";
			logger.warn("Contract Service - Editar Operacao - Authentication Failed !!!");

			return Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.APPLICATION_JSON).build();
		}

	}

	@POST
	@Path("/PDFFichaIndividual/{idContrato}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pDFPAprovadoComite(String data, @PathParam("idContrato") long idContrato,
			@HeaderParam("Authorization") String authorization) {
		logger.info("Inicio Report Servicews - PDFFichaIndividual ");

		FichaIndividualRequest fichaIndividualRequest = GsonUtil.fromJson(data, FichaIndividualRequest.class);

		if (true) { // (RestService.verificarAutenticacao(authorization)) {
			byte[] jp = null;
			RelatoriosService relatorioService = new RelatoriosService();
			try {
				jp = relatorioService.geraPdfFichaIndividual(fichaIndividualRequest);

				return Response.status(Response.Status.OK).entity(jp).type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
						.build();

			} catch (Exception e) {

				return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao gerar relatório")
						.type(MediaType.APPLICATION_JSON).build();
			}

		} else {
			String message = "{\"retorno\": \"[Galleria Bank] Authentication Failed!!!\"}";
			logger.warn("Contract Service - Editar Operacao - Authentication Failed !!!");

			return Response.status(Response.Status.FORBIDDEN).entity(message).type(MediaType.APPLICATION_JSON).build();
		}

	}
}
