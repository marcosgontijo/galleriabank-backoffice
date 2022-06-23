package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Dashboard;
import com.webnowbr.siscoat.cobranca.db.model.GruposPagadores;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PesquisaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class DashboardDao extends HibernateDao <Dashboard,Long> {

	// total contratos
	// contratos analise aprovada
	// contratos analise reprovada
	// aguardando pagamento
	// pagamento confirmando, aguardando laudo e paju
	
	private static final String QUERY_DASH_CONTRATOS =  " select "
			+ "	idresponsavel, "
			+ "	nomeresponsavel, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	( "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		count(c.id) contratosCadastrados, "
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.statuslead = 'Completo' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		count(c.id) contratosPreAprovados, "
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.cadastroaprovadovalor = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		count(c.id) contratosBoletosPagos, "
			+ "		sum(c.quantoPrecisa) valorBoletosPagos, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.pagtolaudoconfirmada = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		count(c.id) contratosCcbsEmitidas, "
			+ "		sum(c.valorccb) valorCcbsEmitidas, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS,"
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.AgAssinatura = 'false' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select		 "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		count(c.id) contratosRegistrados, "
			+ "		sum(c.valorccb) valorContratosRegistrados,	 "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,"
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.status = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato"
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ "		count(c.id) contratosComite, "
			+ "		sum(c.valorAprovadoComite) valorComite , "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE"
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.aprovadoComite = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " ) totais "
			+ " group by "
			+ "	idresponsavel, "
			+ "	nomeresponsavel "
			+ " order by "
			+ "	IDRESPONSAVEL asc";
	
	private static final String QUERY_DASH_CONTRATOS_POR_STATUS =  	" select "
			+ "	idresponsavel, "
			+ "	nomeresponsavel, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	( "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		count(c.id) contratosCadastrados, "
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ " 	0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.statuslead = 'Completo' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		count(c.id) contratosPreAprovados, "
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ " 	0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.cadastroaprovadovalor = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		count(c.id) contratosBoletosPagos, "
			+ "		sum(c.quantoPrecisa) valorBoletosPagos, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ " 	0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.pagtolaudoconfirmada = 'true' "
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select		 "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		count(c.id) contratosCcbsEmitidas, "
			+ "		sum(c.valorccb) valorCcbsEmitidas, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS, "
			+ " 	0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.AgAssinatura = 'false' "
			+ "		and AgAssinaturaData >= ? ::timestamp "
			+ "		and AgAssinaturaData <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select		 "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		count(c.id) contratosRegistrados, "
			+ "		sum(c.valorccb) valorContratosRegistrados,	 "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,"
			+ " 	0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.status = 'Aprovado' "
			+ "		and agRegistroData >= ? ::timestamp "
			+ "		and agRegistroData <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados,	 "
			+ "		null numerosREGISTRADOS,"
			+ "		count(c.id) contratosComite,"
			+ "		sum(c.valorAprovadoComite) valorComite ,"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	where "
			+ "		c.aprovadoComite = 'true' "
			+ "		and aprovadoComiteData >= ? ::timestamp "
			+ "		and aprovadoComiteData <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ ") totais "
			+ " group by "
			+ "	idresponsavel, "
			+ "	nomeresponsavel "
			+ " order by "
			+ "	IDRESPONSAVEL asc";
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratos(final Date dataInicio, final Date dataFim, boolean consultarPorStatus, boolean consultarGerente) {
		return (List<Dashboard>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Dashboard> objects = new ArrayList<Dashboard>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					if(consultarPorStatus) {
						if(consultarGerente) {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_APENAS_GERENTE_POR_STATUS);
						} else {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_STATUS);
						}
					} else {
						if(consultarGerente) {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_APENAS_GERENTE);
						} else {
							ps = connection.prepareStatement(QUERY_DASH_CONTRATOS);
						}
					}
					
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					ps.setDate(3, dtRelInicioSQL);
					ps.setDate(4, dtRelFimSQL);

					ps.setDate(5, dtRelInicioSQL);
					ps.setDate(6, dtRelFimSQL);

					ps.setDate(7, dtRelInicioSQL);
					ps.setDate(8, dtRelFimSQL);

					ps.setDate(9, dtRelInicioSQL);
					ps.setDate(10, dtRelFimSQL);
					
					ps.setDate(11, dtRelInicioSQL);
					ps.setDate(12, dtRelFimSQL);

					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					ResponsavelDao responsavelDao = new ResponsavelDao();
					Responsavel responsavel = new Responsavel();
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					

					while (rs.next()) {
						dashboard = new Dashboard();
						
						if(CommonsUtil.semValor(rs.getString(2))) {
							dashboard.setNomeResponsavel("-");
						} else {
							dashboard.setNomeResponsavel(rs.getString(2));
						}
						
						responsavel = responsavelDao.findById(rs.getLong(1));
						dashboard.setResponsavel(responsavel);
						
						if(!consultarGerente) {
							if (responsavel.getDonoResponsavel() != null) {
								dashboard.setGerenteResponsavel(responsavel.getDonoResponsavel().getNome());
							}
						}

						dashboard.setContratosCadastrados(rs.getInt("contratosCadastrados"));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal("valorContratosCadastrados"));
						dashboard.getListaCadastrados();
						
						dashboard.setContratosPreAprovados(rs.getInt("contratosPreAprovados"));
						dashboard.setValorContratosPreAprovados(rs.getBigDecimal("valorContratosPreAprovados"));
						
						
						dashboard.setContratosBoletosPagos(rs.getInt("contratosBoletosPagos"));
						dashboard.setValorBoletosPagos(rs.getBigDecimal("valorBoletosPagos"));
						
						
						dashboard.setContratosCcbsEmitidas(rs.getInt("contratosCcbsEmitidas"));
						dashboard.setValorCcbsEmitidas(rs.getBigDecimal("valorCcbsEmitidas"));
						
						
						dashboard.setContratosRegistrados(rs.getInt("contratosRegistrados"));
						dashboard.setValorContratosRegistrados(rs.getBigDecimal("valorContratosRegistrados"));
						
						dashboard.setContratosComite(rs.getInt("contratosComite"));
						dashboard.setValorContratosComite(rs.getBigDecimal("valorComite"));
						
						//recebe os contratos
						List<String> listaCadastrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosCadastrados"))) {
							listaCadastrados = Arrays.asList(rs.getString("numerosCadastrados").split(Pattern.quote("#$&!")));
							dashboard.setListaCadastrados(new ArrayList<ContratoCobranca>());
							
							
							for(String cadastro : listaCadastrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCadastrados().add(coco);
							}
						}
						
						List<String> listaPreAprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosPREAPROVADOS"))) {
							listaPreAprovados = Arrays.asList(rs.getString("numerosPREAPROVADOS").split(Pattern.quote("#$&!")));
							dashboard.setListaPreAprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaPreAprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaPreAprovados().add(coco);
							}
						}
						
						List<String> listaBoletosPagos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosBOLETOSPAGOS"))) {
							listaBoletosPagos = Arrays.asList(rs.getString("numerosBOLETOSPAGOS").split(Pattern.quote("#$&!")));
							dashboard.setListaBoletosPagos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaBoletosPagos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaBoletosPagos().add(coco);
							}
						}
						
						List<String> listaCcbsEmitidas = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosCCBSEMITIDAS"))) {
							listaCcbsEmitidas = Arrays.asList(rs.getString("numerosCCBSEMITIDAS").split(Pattern.quote("#$&!")));
							dashboard.setListaCcbsEmitidas(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaCcbsEmitidas) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCcbsEmitidas().add(coco);
							}
						}
						
						List<String> listaRegistrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosREGISTRADOS"))) {
							listaRegistrados = Arrays.asList(rs.getString("numerosREGISTRADOS").split(Pattern.quote("#$&!")));
							dashboard.setListaRegistrados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaRegistrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaRegistrados().add(coco);
							}
						}
						
						List<String> listaComite = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosComite"))) {
							listaComite = Arrays.asList(rs.getString("numerosComite").split(Pattern.quote("#$&!")));
							dashboard.setListaComite(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaComite) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaComite().add(coco);
							}
						}

						objects.add(dashboard);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE =  " select "
			+ "	idresponsavel, "
			+ "	nomeresponsavel, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	( "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		count(c.id) contratosCadastrados, "
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.statuslead = 'Completo' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		count(c.id) contratosPreAprovados, "
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.cadastroaprovadovalor = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		count(c.id) contratosBoletosPagos, "
			+ "		sum(c.quantoPrecisa) valorBoletosPagos, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.pagtolaudoconfirmada = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		count(c.id) contratosCcbsEmitidas, "
			+ "		sum(c.valorccb) valorCcbsEmitidas, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.AgAssinatura = 'false' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		count(c.id) contratosRegistrados, "
			+ "		sum(c.valorccb) valorContratosRegistrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,"
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.status = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		count(c.id) contratosComite, "
			+ "		sum(c.valorAprovadoComite) valorComite , "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.aprovadoComite = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ ") totais "
			+ " group by "
			+ "	idresponsavel, "
			+ "	nomeresponsavel "
			+ " order by "
			+ "	IDRESPONSAVEL asc";
	
	private static final String QUERY_DASH_CONTRATOS_POR_GERENTE_POR_STATUS =  " select "
			+ "	idresponsavel, "
			+ "	nomeresponsavel, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS,"
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE  "
			+ " from "
			+ "	( "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		count(c.id) contratosCadastrados, "
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.statuslead = 'Completo' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		count(c.id) contratosPreAprovados, "
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.cadastroaprovadovalor = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		count(c.id) contratosBoletosPagos, "
			+ "		sum(c.quantoPrecisa) valorBoletosPagos, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.pagtolaudoconfirmada = 'true' "
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		count(c.id) contratosCcbsEmitidas, "
			+ "		sum(c.valorccb) valorCcbsEmitidas, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.AgAssinatura = 'false' "
			+ "		and AgAssinaturaData >= ? ::timestamp "
			+ "		and AgAssinaturaData <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		count(c.id) contratosRegistrados, "
			+ "		sum(c.valorccb) valorContratosRegistrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.status = 'Aprovado' "
			+ "		and agRegistroData >= ? ::timestamp "
			+ "		and agRegistroData <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " union all "
			+ "	select "
			+ "		r.id idresponsavel, "
			+ "		r.nome nomeresponsavel, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		count(c.id) contratosComite, "
			+ "		sum(c.valorAprovadoComite) valorComite , "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join cobranca.responsavel r1 on "
			+ "		r1.id = r.donoResponsavel "
			+ "	where "
			+ "		c.AprovadoComite = 'true' "
			+ "		and AprovadoComiteData >= ? ::timestamp "
			+ "		and AprovadoComiteData <= ? ::timestamp "
			+ "		and (r1.ID = ? "
			+ "			or r.ID = ?) "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ ") totais "
			+ " group by "
			+ "	idresponsavel, "
			+ "	nomeresponsavel "
			+ " order by "
			+ "	IDRESPONSAVEL asc ";
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratosPorGerente(final Date dataInicio, final Date dataFim, final long idGerenteResponsavel, boolean consultarPorStatus) {
		return (List<Dashboard>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Dashboard> objects = new ArrayList<Dashboard>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					if(consultarPorStatus) {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_GERENTE_POR_STATUS);
					} else {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_POR_GERENTE);
					}

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					ps.setLong(3, idGerenteResponsavel);
					ps.setLong(4, idGerenteResponsavel);
									
					ps.setDate(5, dtRelInicioSQL);
					ps.setDate(6, dtRelFimSQL);
					ps.setLong(7, idGerenteResponsavel);
					ps.setLong(8, idGerenteResponsavel);
										
					ps.setDate(9, dtRelInicioSQL);
					ps.setDate(10, dtRelFimSQL);
					ps.setLong(11, idGerenteResponsavel);
					ps.setLong(12, idGerenteResponsavel);
									
					ps.setDate(13, dtRelInicioSQL);
					ps.setDate(14, dtRelFimSQL);
					ps.setLong(15, idGerenteResponsavel);
					ps.setLong(16, idGerenteResponsavel);
									
					ps.setDate(17, dtRelInicioSQL);
					ps.setDate(18, dtRelFimSQL);
					ps.setLong(19, idGerenteResponsavel);
					ps.setLong(20, idGerenteResponsavel);
					
					ps.setDate(21, dtRelInicioSQL);
					ps.setDate(22, dtRelFimSQL);
					ps.setLong(23, idGerenteResponsavel);
					ps.setLong(24, idGerenteResponsavel);

					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					ResponsavelDao responsavelDao = new ResponsavelDao();
					Responsavel responsavel = new Responsavel();
					Responsavel responsavelGerente = new Responsavel();

					responsavel = responsavelDao.findById(idGerenteResponsavel);
					responsavelGerente = responsavelDao.findById(idGerenteResponsavel);

					while (rs.next()) {
						dashboard = new Dashboard();
						dashboard.setNomeResponsavel(rs.getString(2));

						responsavel = responsavelDao.findById(rs.getLong(1));
						dashboard.setResponsavel(responsavel);

						dashboard.setGerenteResponsavel(responsavelGerente.getNome());

						dashboard.setContratosCadastrados(rs.getInt("contratosCadastrados"));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal("valorContratosCadastrados"));
						
						
						dashboard.setContratosPreAprovados(rs.getInt("contratosPreAprovados"));
						dashboard.setValorContratosPreAprovados(rs.getBigDecimal("valorContratosPreAprovados"));
						
						
						dashboard.setContratosBoletosPagos(rs.getInt("contratosBoletosPagos"));
						dashboard.setValorBoletosPagos(rs.getBigDecimal("valorBoletosPagos"));
						
						
						dashboard.setContratosCcbsEmitidas(rs.getInt("contratosCcbsEmitidas"));
						dashboard.setValorCcbsEmitidas(rs.getBigDecimal("valorCcbsEmitidas"));
						
						
						dashboard.setContratosRegistrados(rs.getInt("contratosRegistrados"));
						dashboard.setValorContratosRegistrados(rs.getBigDecimal("valorContratosRegistrados"));
						

						dashboard.setContratosComite(rs.getInt("contratosComite"));
						dashboard.setValorContratosComite(rs.getBigDecimal("valorComite"));
						
						//recebe os contratos
						List<String> listaCadastrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosCadastrados"))) {
							listaCadastrados = Arrays.asList(rs.getString("numerosCadastrados").split(Pattern.quote("#$&!")));
							dashboard.setListaCadastrados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaCadastrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCadastrados().add(coco);
							}
						}
						
						List<String> listaPreAprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosPREAPROVADOS"))) {
							listaPreAprovados = Arrays.asList(rs.getString("numerosPREAPROVADOS").split(Pattern.quote("#$&!")));
							dashboard.setListaPreAprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaPreAprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaPreAprovados().add(coco);
							}
						}
						
						List<String> listaBoletosPagos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosBOLETOSPAGOS"))) {
							listaBoletosPagos = Arrays.asList(rs.getString("numerosBOLETOSPAGOS").split(Pattern.quote("#$&!")));
							dashboard.setListaBoletosPagos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaBoletosPagos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaBoletosPagos().add(coco);
							}
						}
						
						List<String> listaCcbsEmitidas = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosCCBSEMITIDAS"))) {
							listaCcbsEmitidas = Arrays.asList(rs.getString("numerosCCBSEMITIDAS").split(Pattern.quote("#$&!")));
							dashboard.setListaCcbsEmitidas(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaCcbsEmitidas) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaCcbsEmitidas().add(coco);
							}
						}
						
						List<String> listaRegistrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosREGISTRADOS"))) {
							listaRegistrados = Arrays.asList(rs.getString("numerosREGISTRADOS").split(Pattern.quote("#$&!")));
							dashboard.setListaRegistrados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaRegistrados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaRegistrados().add(coco);
							}
						}
						
						List<String> listaComite = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosComite"))) {
							listaComite = Arrays.asList(rs.getString("numerosComite").split(Pattern.quote("#$&!")));
							dashboard.setListaComite(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaComite) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaComite().add(coco);
							}
						}

						objects.add(dashboard);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	
	private static final String QUERY_DASH_CONTRATOS_APENAS_GERENTE =  " select "
			+ "	IDGERENTE, "
			+ "	NOMEGerente, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	(select "
			+ "	IDRESPONSAVEL, "
			+ "	IDGERENTE, "
			+ "	NOMERESPONSAVEL, "
			+ "	NOMEGerente, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	( "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		count(c.id) contratosCadastrados, "
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.statuslead = 'Completo' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		count(c.id) contratosPreAprovados, "
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.cadastroaprovadovalor = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		count(c.id) contratosBoletosPagos, "
			+ "		sum(c.quantoPrecisa) valorBoletosPagos, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.pagtolaudoconfirmada = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		count(c.id) contratosCcbsEmitidas, "
			+ "		sum(c.valorccb) valorCcbsEmitidas, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.AgAssinatura = 'false' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		count(c.id) contratosRegistrados, "
			+ "		sum(c.valorccb) valorContratosRegistrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!') numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.status = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS,"
			+ "		count(c.id) contratosComite, "
			+ "		sum(c.valorAprovadoComite) valorComite , "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.AprovadoComite = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ ") totais "
			+ " group by "
			+ "	IDRESPONSAVEL, "
			+ "	IDGERENTE, "
			+ "	NOMERESPONSAVEL, "
			+ "	nomegerente) totalGerente "
			+ " group by "
			+ "	IDGERENTE, "
			+ "	NOMEGerente "
			+ " order by "
			+ "	IDGERENTE asc";
	
	
	private static final String QUERY_DASH_CONTRATOS_APENAS_GERENTE_POR_STATUS =  " select "
			+ "	IDGERENTE, "
			+ "	NOMEGerente, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	(select "
			+ "	IDRESPONSAVEL, "
			+ "	IDGERENTE, "
			+ "	NOMERESPONSAVEL, "
			+ "	NOMEGerente, "
			+ "	sum(contratosCadastrados) contratosCadastrados, "
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados, "
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados, "
			+ "	sum(contratosPreAprovados) contratosPreAprovados, "
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados, "
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS, "
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos, "
			+ "	sum(valorBoletosPagos) valorBoletosPagos, "
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS, "
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas, "
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas, "
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS, "
			+ "	sum(contratosRegistrados) contratosRegistrados, "
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados, "
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS, "
			+ "	sum(contratosComite) contratosComite, "
			+ "	sum(valorComite) valorComite, "
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE "
			+ " from "
			+ "	( "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		count(c.id) contratosCadastrados, "
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.statuslead = 'Completo' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		count(c.id) contratosPreAprovados, "
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.cadastroaprovadovalor = 'Aprovado' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		count(c.id) contratosBoletosPagos, "
			+ "		sum(c.quantoPrecisa) valorBoletosPagos, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.pagtolaudoconfirmada = 'true' "
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp "
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		count(c.id) contratosCcbsEmitidas, "
			+ "		sum(c.valorccb) valorCcbsEmitidas, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.AgAssinatura = 'false' "
			+ "		and AgAssinaturaData >= ? ::timestamp "
			+ "		and AgAssinaturaData <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		count(c.id) contratosRegistrados, "
			+ "		sum(c.valorccb) valorContratosRegistrados, "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS, "
			+ "		0 contratosComite, "
			+ "		0 valorComite, "
			+ "		null numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.status = 'Aprovado' "
			+ "		and agRegistroData >= ? ::timestamp "
			+ "		and agRegistroData <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " union all "
			+ "	select "
			+ "		R.ID IDRESPONSAVEL, "
			+ "		R1.ID IDGERENTE, "
			+ "		R.NOME NOMERESPONSAVEL, "
			+ "		R1.NOME NOMEGerente, "
			+ "		0 contratosCadastrados, "
			+ "		0 valorContratosCadastrados, "
			+ "		null numerosCadastrados, "
			+ "		0 contratosPreAprovados, "
			+ "		0 valorContratosPreAprovados, "
			+ "		null numerosPREAPROVADOS, "
			+ "		0 contratosBoletosPagos, "
			+ "		0 valorBoletosPagos, "
			+ "		null numerosBOLETOSPAGOS, "
			+ "		0 contratosCcbsEmitidas, "
			+ "		0 valorCcbsEmitidas, "
			+ "		null numerosCCBSEMITIDAS, "
			+ "		0 contratosRegistrados, "
			+ "		0 valorContratosRegistrados, "
			+ "		null numerosREGISTRADOS,"
			+ "		count(c.id) contratosComite, "
			+ "		sum(c.valorAprovadoComite) valorComite , "
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel r on "
			+ "		r.id = c.responsavel "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	left join COBRANCA.RESPONSAVEL R1 on "
			+ "		R1.ID = R.DONORESPONSAVEL "
			+ "	where "
			+ "		c.AprovadoComite = 'true' "
			+ "		and AprovadoComiteData >= ? ::timestamp "
			+ "		and AprovadoComiteData <= ? ::timestamp "
			+ "	group by "
			+ "		R.ID, "
			+ "		IDGERENTE, "
			+ "		R.NOME, "
			+ "		C.DATACONTRATO "
			+ " ) totais "
			+ " group by "
			+ "	IDRESPONSAVEL, "
			+ "	IDGERENTE, "
			+ "	NOMERESPONSAVEL, "
			+ "	nomegerente) totalGerente "
			+ " group by "
			+ "	IDGERENTE, "
			+ "	NOMEGerente "
			+ " order by "
			+ "	IDGERENTE asc ";
	
	
	private static final String QUERY_TAXAS_DASHBOARD =  " select taxapreaprovada, taxaaprovada, txjurosparcelas "
			+ "	from cobranca.contratocobranca c ";
	
	@SuppressWarnings("unchecked")
	public List<BigDecimal> getTaxasPreAprovadaDashboard(List<ContratoCobranca> listaContratos) {
		return (List<BigDecimal>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BigDecimal> objects = new ArrayList<BigDecimal>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where taxapreaprovada != '0' and (";
				if (!CommonsUtil.semValor(listaContratos)) {
					boolean iniciado = false;
					for( ContratoCobranca contrato : listaContratos) {
						if(!iniciado) {
							queryContratos = queryContratos + " numerocontrato =  '" + contrato.getNumeroContrato() +"' ";
							iniciado = true;
						} else {
							queryContratos = queryContratos + " or numerocontrato =  '" + contrato.getNumeroContrato() +"'";
						}					
					}
				} else {
					queryContratos = queryContratos + " numerocontrato =  '00000' ";
				}
				queryContratos = queryContratos + ")";
				query = query + queryContratos;
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();					
					while (rs.next()) {
						objects.add(rs.getBigDecimal("taxapreaprovada"));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<BigDecimal> getTaxasAprovadaComiteDashboard(List<ContratoCobranca> listaContratos) {
		return (List<BigDecimal>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BigDecimal> objects = new ArrayList<BigDecimal>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where taxaaprovada != '0' and (";
				if (!CommonsUtil.semValor(listaContratos)) {
					boolean iniciado = false;
					for( ContratoCobranca contrato : listaContratos) {
						if(!iniciado) {
							queryContratos = queryContratos + " numerocontrato =  '" + contrato.getNumeroContrato() +"' ";
							iniciado = true;
						} else {
							queryContratos = queryContratos + " or numerocontrato =  '" + contrato.getNumeroContrato() +"'";
						}					
					}
				} else {
					queryContratos = queryContratos + " numerocontrato =  '00000' ";
				}
				queryContratos = queryContratos + ")";
				query = query + queryContratos;
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();					
					while (rs.next()) {
						objects.add(rs.getBigDecimal("taxaaprovada"));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<BigDecimal> getTaxasCcb(List<ContratoCobranca> listaContratos) {
		return (List<BigDecimal>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<BigDecimal> objects = new ArrayList<BigDecimal>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where txjurosparcelas != '0' and (";
				if (!CommonsUtil.semValor(listaContratos)) {
					boolean iniciado = false;
					for( ContratoCobranca contrato : listaContratos) {
						if(!iniciado) {
							queryContratos = queryContratos + " numerocontrato =  '" + contrato.getNumeroContrato() +"' ";
							iniciado = true;
						} else {
							queryContratos = queryContratos + " or numerocontrato =  '" + contrato.getNumeroContrato() +"'";
						}					
					}
				} else {
					queryContratos = queryContratos + " numerocontrato =  '00000' ";
				}
				queryContratos = queryContratos + ")";
				query = query + queryContratos;
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(query);
					rs = ps.executeQuery();					
					while (rs.next()) {
						objects.add(rs.getBigDecimal("txjurosparcelas"));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}

	private static final String QUERY_CONTRATOS_LEAD =  " select numerocontrato, imv.cidade, imv.cep, imv.estado, datacontrato, motivoReprovaLead, motivoReprovaSelectItem, quantoprecisa, valoraprovadocomite, valorccb, urllead, statuslead, status, inicioanalise, cadastroaprovadovalor, PagtoLaudoConfirmada, LaudoRecebido, PajurFavoravel, AprovadoComite, AgAssinatura from cobranca.contratocobranca cc "
			+ " inner join cobranca.imovelcobranca imv on imv.id = cc.imovel "
			+ " inner join cobranca.pagadorrecebedor pare on pare.id = cc.pagador "
			+ " where urllead is not null and urllead != '' and pare.nome not LIKE '%teste%' and pare.nome  not LIKE '%Teste%' "
			+ " order by cc.id desc ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratosLead() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_LEAD);
					rs = ps.executeQuery();					

					while (rs.next()) {
						ContratoCobranca contrato = new ContratoCobranca();
						
						contrato.setNumeroContrato(rs.getString("numerocontrato"));
						contrato.setImovel(new ImovelCobranca());
						contrato.getImovel().setCidade(rs.getString("cidade"));
						contrato.getImovel().setCep(rs.getString("cep"));
						contrato.getImovel().setEstado(rs.getString("estado"));
						contrato.setDataContrato(rs.getTimestamp("datacontrato"));
						contrato.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contrato.setValorAprovadoComite(rs.getBigDecimal("valorAprovadoComite"));
						contrato.setValorCCB(rs.getBigDecimal("valorccb"));
						contrato.setUrlLead(rs.getString("urllead"));
						contrato.setMotivoReprovaLead(rs.getString("motivoReprovaLead"));
						contrato.setMotivoReprovaSelectItem(rs.getString("motivoReprovaSelectItem"));
						contrato.setStatusLead(rs.getString("statuslead"));
						contrato.setStatus(rs.getString("status"));
						contrato.setInicioAnalise(rs.getBoolean("inicioanalise"));
						contrato.setCadastroAprovadoValor(rs.getString("cadastroaprovadovalor"));
						contrato.setPagtoLaudoConfirmada(rs.getBoolean("PagtoLaudoConfirmada"));
						contrato.setLaudoRecebido(rs.getBoolean("laudorecebido"));
						contrato.setPajurFavoravel(rs.getBoolean("pajurfavoravel"));
						contrato.setAprovadoComite(rs.getBoolean("aprovadocomite"));
						contrato.setAgAssinatura(rs.getBoolean("AgAssinatura"));
						objects.add(contrato);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}	
	
	private static final String QUERY_CONTRATOS_REPROVADO = " select "
			+ "	numerocontrato, "
			+ "	imv.cidade, "
			+ "	imv.cep, "
			+ "	imv.estado, "
			+ "	datacontrato, "
			+ "	motivoReprovaLead, "
			+ "	motivoReprovaSelectItem, "
			+ "	quantoprecisa, "
			+ "	valoraprovadocomite, "
			+ "	valorccb, "
			+ "	pare.nome "
			+ " from "
			+ "	cobranca.contratocobranca cc  "
			+ " inner join cobranca.imovelcobranca imv on "
			+ "	imv.id = cc.imovel  "
			+ " inner join cobranca.pagadorrecebedor pare on "
			+ "	pare.id = cc.pagador  "
			+ " where "
			+ "	analisereprovada = true "
			+ "	or reprovado = true "
			+ "	or CadastroAprovadoValor = 'Reprovado' "
			+ "	or StatusContrato = 'Reprovado' "
			+ "	or statusLead = 'Reprovado' "
			+ "	or status = 'Reprovado' "
			+ " order by "
			+ "	cc.id desc";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratosReprovados() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONTRATOS_REPROVADO);
					rs = ps.executeQuery();					

					while (rs.next()) {
						ContratoCobranca contrato = new ContratoCobranca();
						
						contrato.setNumeroContrato(rs.getString("numerocontrato"));
						contrato.setImovel(new ImovelCobranca());
						contrato.getImovel().setCidade(rs.getString("cidade"));
						contrato.getImovel().setCep(rs.getString("cep"));
						contrato.getImovel().setEstado(rs.getString("estado"));
						contrato.setPagador(new PagadorRecebedor());
						contrato.getPagador().setNome(rs.getString("nome"));
						contrato.setDataContrato(rs.getTimestamp("datacontrato"));
						contrato.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contrato.setValorAprovadoComite(rs.getBigDecimal("valorAprovadoComite"));
						contrato.setValorCCB(rs.getBigDecimal("valorccb"));
						contrato.setMotivoReprovaLead(rs.getString("motivoReprovaLead"));
						contrato.setMotivoReprovaSelectItem(rs.getString("motivoReprovaSelectItem"));

						objects.add(contrato);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
}
