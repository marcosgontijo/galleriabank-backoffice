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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
			+ "		c.aprovadoComite = 'true' "
			+ "		and inicioanalisedata >= ? ::timestamp "
			+ "		and inicioanalisedata <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ " ) totais "
			+ " where idresponsavel != 3"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
			+ "		c.aprovadoComite = 'true' "
			+ "		and aprovadoComiteData >= ? ::timestamp "
			+ "		and aprovadoComiteData <= ? ::timestamp "
			+ "	group by "
			+ "		r.id, "
			+ "		r.nome, "
			+ "		c.datacontrato "
			+ ") totais "
			+ " where idresponsavel != 3"
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
						
						if(!CommonsUtil.semValor(rs.getLong(1))) {
							responsavel = responsavelDao.findById(rs.getLong(1));
						}
							
						dashboard.setResponsavel(responsavel);
						
						if(!consultarGerente) {
							if (responsavel.getDonoResponsavel() != null) {
								dashboard.setGerenteResponsavel(responsavel.getDonoResponsavel().getNome());
							}
						}
						
						if (!CommonsUtil.semValor(responsavel.getResponsavelCaptador())) {
							dashboard.setCaptadorResponsavel(responsavel.getResponsavelCaptador().getNome());
						}
						
						dashboard.setDataCadastroResponsavel(responsavel.getDataCadastro());

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
							dashboard.setListaCadastrados(getTaxasDashboard(dashboard.getListaCadastrados()));
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
							
							dashboard.setListaPreAprovados(getTaxasDashboard(dashboard.getListaPreAprovados()));
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
							dashboard.setListaBoletosPagos(getTaxasDashboard(dashboard.getListaBoletosPagos()));
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
							
							dashboard.setListaCcbsEmitidas(getTaxasDashboard(dashboard.getListaCcbsEmitidas()));
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
							dashboard.setListaRegistrados(getTaxasDashboard(dashboard.getListaRegistrados()));
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
							dashboard.setListaComite(getTaxasDashboard(dashboard.getListaComite()));
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ " where idresponsavel != 3"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ " where idresponsavel != 3"
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
						
						if(!CommonsUtil.semValor(rs.getLong(1))) {
							responsavel = responsavelDao.findById(rs.getLong(1));
						}
							
						dashboard.setResponsavel(responsavel);
						
						if (responsavel.getDonoResponsavel() != null) {
							dashboard.setGerenteResponsavel(responsavel.getDonoResponsavel().getNome());
						}
					
						dashboard.setGerenteResponsavel(responsavelGerente.getNome());
						
						if (!CommonsUtil.semValor(responsavel.getResponsavelCaptador())) {
							dashboard.setCaptadorResponsavel(responsavel.getResponsavelCaptador().getNome());
						}
						
						dashboard.setDataCadastroResponsavel(responsavel.getDataCadastro());

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
							
							dashboard.setListaCadastrados(getTaxasDashboard(dashboard.getListaCadastrados()));
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
							dashboard.setListaPreAprovados(getTaxasDashboard(dashboard.getListaPreAprovados()));
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
							dashboard.setListaBoletosPagos(getTaxasDashboard(dashboard.getListaBoletosPagos()));
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
							dashboard.setListaCcbsEmitidas(getTaxasDashboard(dashboard.getListaCcbsEmitidas()));
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
							dashboard.setListaRegistrados(getTaxasDashboard(dashboard.getListaRegistrados()));
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
							dashboard.setListaComite(getTaxasDashboard(dashboard.getListaComite()));
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ " where IDGERENTE != 3"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ "	where c.numerocontrato is not null and"
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
			+ " where IDGERENTE != 3"
			+ " group by "
			+ "	IDGERENTE, "
			+ "	NOMEGerente "
			+ " order by "
			+ "	IDGERENTE asc ";
	
	private static final String QUERY_TAXAS_DASHBOARD =  " select taxapreaprovada, taxaaprovada, txjurosparcelas, valoraprovadocomite , quantoprecisa , valorccb, p.nome, c.numerocontrato"
			+ "	from cobranca.contratocobranca c"
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "	p.id = c.pagador ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getTaxasDashboard(List<ContratoCobranca> listaContratos) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where 1 = 1 and (";
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
						ContratoCobranca coco = new ContratoCobranca();
						
						coco.setPagador(new PagadorRecebedor());
						coco.setNumeroContrato(rs.getString("numerocontrato"));
						coco.getPagador().setNome(rs.getString("nome"));
						
						if(!CommonsUtil.semValor(rs.getBigDecimal("taxapreaprovada"))) {
							coco.setTaxaPreAprovada(rs.getBigDecimal("taxapreaprovada"));
						}
						if(!CommonsUtil.semValor(rs.getBigDecimal("quantoPrecisa"))) {
							coco.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
						}
						if(!CommonsUtil.semValor(rs.getBigDecimal("taxaaprovada"))) {
							coco.setTaxaAprovada(rs.getBigDecimal("taxaaprovada"));
						}	
						if(!CommonsUtil.semValor(rs.getBigDecimal("valoraprovadocomite"))) {
							coco.setValorAprovadoComite(rs.getBigDecimal("valoraprovadocomite"));
						}
						if(!CommonsUtil.semValor(rs.getBigDecimal("txjurosparcelas"))) {
							coco.setTxJurosParcelas(rs.getBigDecimal("txjurosparcelas"));
						}	
						if(!CommonsUtil.semValor(rs.getBigDecimal("valorccb"))) {
							coco.setValorCCB(rs.getBigDecimal("valorccb"));
						}
						
						objects.add(coco);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getTaxasPreAprovadaDashboard(List<ContratoCobranca> listaContratos) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where taxapreaprovada != '0' and taxapreaprovada is not null and (";
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
						ContratoCobranca coco = new ContratoCobranca();
						coco.setTaxaPreAprovada(rs.getBigDecimal("taxapreaprovada"));
						coco.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
						objects.add(coco);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getTaxasAprovadaComiteDashboard(List<ContratoCobranca> listaContratos) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where taxaaprovada != '0' and taxaaprovada is not null and (";
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
						ContratoCobranca coco = new ContratoCobranca();
						coco.setTaxaAprovada(rs.getBigDecimal("taxaaprovada"));
						coco.setValorAprovadoComite(rs.getBigDecimal("ValorAprovadoComite"));
						objects.add(coco);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getTaxasCcb(List<ContratoCobranca> listaContratos) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();	
				String query = QUERY_TAXAS_DASHBOARD;
				String queryContratos = " where txjurosparcelas != '0' and txjurosparcelas is not null and (";
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
						ContratoCobranca coco = new ContratoCobranca();
						coco.setTxJurosParcelas(rs.getBigDecimal("txjurosparcelas"));
						coco.setValorCCB(rs.getBigDecimal("valorccb"));
						objects.add(coco);
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

	///////////////////////////////////////////////////////////////////////////////
	
	private static final String QUERY_DASH_CONTRATOS_LEADS = "select\r\n"
			+ "	origemLead,\r\n"
			+ "	sum(contratosCadastrados) contratosCadastrados,\r\n"
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados,\r\n"
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados,\r\n"
			+ "	sum(leadsEmTratamento) leadsEmTratamento,\r\n"
			+ "	sum(valorLeadsEmTratamento) valorLeadsEmTratamento,\r\n"
			+ "	string_agg(numerosLeadsEmTratamento, '#$&!') numerosLeadsEmTratamento,\r\n"
			+ "	sum(leadsAgContato) leadsAgContato,\r\n"
			+ "	sum(valorLeadsAgContato) valorLeadsAgContato,\r\n"
			+ "	string_agg(numerosLeadsAgContato, '#$&!') numerosLeadsAgContato,\r\n"
			+ "	sum(leadsAgDoc) leadsAgDoc,\r\n"
			+ "	sum(valorLeadsAgDoc) valorLeadsAgDoc,\r\n"
			+ "	string_agg(numerosLeadsAgDoc, '#$&!') numerosLeadsAgDoc,\r\n"
			+ "	sum(leadsReprovados) leadsReprovados,\r\n"
			+ "	sum(valorLeadsReprovados) valorLeadsReprovados,\r\n"
			+ "	string_agg(numerosLeadsReprovados, '#$&!') numerosLeadsReprovados,\r\n"
			+ "	sum(leadsCompletos) leadsCompletos,\r\n"
			+ "	sum(valorLeadsCompletos) valorLeadsCompletos,\r\n"
			+ "	string_agg(numerosLeadsCompletos, '#$&!') numerosLeadsCompletos,\r\n"
			+ "	sum(contratosPreAprovados) contratosPreAprovados,\r\n"
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados,\r\n"
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS,\r\n"
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos,\r\n"
			+ "	sum(valorBoletosPagos) valorBoletosPagos,\r\n"
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS,\r\n"
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas,\r\n"
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas,\r\n"
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS,\r\n"
			+ "	sum(contratosRegistrados) contratosRegistrados,\r\n"
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados,\r\n"
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS,\r\n"
			+ "	sum(contratosComite) contratosComite,\r\n"
			+ "	sum(valorComite) valorComite,\r\n"
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		count(c.id) contratosCadastrados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.status != 'Aprovados'\r\n"
			+ "		and c.dataContrato >= ? ::timestamp\r\n"
			+ "		and c.dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		count(c.id) leadsEmTratamento,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsEmTratamento,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Em Tratamento'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and dataContrato >= ? ::timestamp\r\n"
			+ "		and dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		count(c.id) leadsAgContato,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgContato,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Contato'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and dataContrato >= ? ::timestamp\r\n"
			+ "		and dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		count(c.id) leadsAgDoc,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgDoc,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Doc.'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and dataContrato >= ? ::timestamp\r\n"
			+ "		and dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		count(c.id) leadsReprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsReprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Reprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and dataContrato >= ? ::timestamp\r\n"
			+ "		and dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		count(c.id) leadsCompletos,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsCompletos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Completo'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		count(c.id) contratosPreAprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.cadastroaprovadovalor = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		count(c.id) contratosBoletosPagos,\r\n"
			+ "		sum(c.quantoPrecisa) valorBoletosPagos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.pagtolaudoconfirmada = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		count(c.id) contratosCcbsEmitidas,\r\n"
			+ "		sum(c.valorccb) valorCcbsEmitidas,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.AgAssinatura = 'false'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		count(c.id) contratosRegistrados,\r\n"
			+ "		sum(c.valorccb) valorContratosRegistrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.status = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		count(c.id) contratosComite,\r\n"
			+ "		sum(c.valorAprovadoComite) valorComite ,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.aprovadoComite = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato ) totais\r\n"
			+ "group by\r\n"
			+ "	origemLead";
	
	private static final String QUERY_DASH_CONTRATOS_LEADS_POR_STATUS = "select\r\n"
			+ "	origemLead,\r\n"
			+ "	sum(contratosCadastrados) contratosCadastrados,\r\n"
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados,\r\n"
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados,\r\n"
			+ "	sum(leadsEmTratamento) leadsEmTratamento,\r\n"
			+ "	sum(valorLeadsEmTratamento) valorLeadsEmTratamento,\r\n"
			+ "	string_agg(numerosLeadsEmTratamento, '#$&!') numerosLeadsEmTratamento,\r\n"
			+ "	sum(leadsAgContato) leadsAgContato,\r\n"
			+ "	sum(valorLeadsAgContato) valorLeadsAgContato,\r\n"
			+ "	string_agg(numerosLeadsAgContato, '#$&!') numerosLeadsAgContato,\r\n"
			+ "	sum(leadsAgDoc) leadsAgDoc,\r\n"
			+ "	sum(valorLeadsAgDoc) valorLeadsAgDoc,\r\n"
			+ "	string_agg(numerosLeadsAgDoc, '#$&!') numerosLeadsAgDoc,\r\n"
			+ "	sum(leadsReprovados) leadsReprovados,\r\n"
			+ "	sum(valorLeadsReprovados) valorLeadsReprovados,\r\n"
			+ "	string_agg(numerosLeadsReprovados, '#$&!') numerosLeadsReprovados,\r\n"
			+ "	sum(leadsCompletos) leadsCompletos,\r\n"
			+ "	sum(valorLeadsCompletos) valorLeadsCompletos,\r\n"
			+ "	string_agg(numerosLeadsCompletos, '#$&!') numerosLeadsCompletos,\r\n"
			+ "	sum(contratosPreAprovados) contratosPreAprovados,\r\n"
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados,\r\n"
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS,\r\n"
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos,\r\n"
			+ "	sum(valorBoletosPagos) valorBoletosPagos,\r\n"
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS,\r\n"
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas,\r\n"
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas,\r\n"
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS,\r\n"
			+ "	sum(contratosRegistrados) contratosRegistrados,\r\n"
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados,\r\n"
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS,\r\n"
			+ "	sum(contratosComite) contratosComite,\r\n"
			+ "	sum(valorComite) valorComite,\r\n"
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		count(c.id) contratosCadastrados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.status != 'Aprovados'\r\n"
			+ "		and c.dataContrato >= ? ::timestamp\r\n"
			+ "		and c.dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		count(c.id) leadsEmTratamento,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsEmTratamento,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Em Tratamento'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and LeadEmTratamentoData >= ? ::timestamp\r\n"
			+ "		and LeadEmTratamentoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		count(c.id) leadsAgContato,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgContato,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Contato'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.leadAgContatoData >= ? ::timestamp\r\n"
			+ "		and c.leadAgContatoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		count(c.id) leadsAgDoc,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgDoc,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Doc.'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.leadAgDocData >= ? ::timestamp\r\n"
			+ "		and c.leadAgDocData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		count(c.id) leadsReprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsReprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Reprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and LeadReprovadoData >= ? ::timestamp\r\n"
			+ "		and LeadReprovadoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		count(c.id) leadsCompletos,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsCompletos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Completo'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		count(c.id) contratosPreAprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.cadastroaprovadovalor = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and CadastroAprovadoData >= ? ::timestamp\r\n"
			+ "		and CadastroAprovadoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		count(c.id) contratosBoletosPagos,\r\n"
			+ "		sum(c.quantoPrecisa) valorBoletosPagos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.pagtolaudoconfirmada = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp\r\n"
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		count(c.id) contratosCcbsEmitidas,\r\n"
			+ "		sum(c.valorccb) valorCcbsEmitidas,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.AgAssinatura = 'false'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and AgAssinaturaData >= ? ::timestamp\r\n"
			+ "		and AgAssinaturaData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		count(c.id) contratosRegistrados,\r\n"
			+ "		sum(c.valorccb) valorContratosRegistrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.status = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and agRegistroData >= ? ::timestamp\r\n"
			+ "		and agRegistroData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.urllead origemLead,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		count(c.id) contratosComite,\r\n"
			+ "		sum(c.valorAprovadoComite) valorComite ,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.aprovadoComite = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and aprovadoComiteData >= ? ::timestamp\r\n"
			+ "		and aprovadoComiteData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.urllead,\r\n"
			+ "		c.datacontrato ) totais\r\n"
			+ "group by\r\n"
			+ "	origemLead";
	
	private static final String QUERY_DASH_CONTRATOS_LEADS_CIDADE = "select\r\n"
			+ "	cidade,\r\n"
			+ "	sum(contratosCadastrados) contratosCadastrados,\r\n"
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados,\r\n"
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados,\r\n"
			+ "	sum(leadsEmTratamento) leadsEmTratamento,\r\n"
			+ "	sum(valorLeadsEmTratamento) valorLeadsEmTratamento,\r\n"
			+ "	string_agg(numerosLeadsEmTratamento, '#$&!') numerosLeadsEmTratamento,\r\n"
			+ "	sum(leadsAgContato) leadsAgContato,\r\n"
			+ "	sum(valorLeadsAgContato) valorLeadsAgContato,\r\n"
			+ "	string_agg(numerosLeadsAgContato, '#$&!') numerosLeadsAgContato,\r\n"
			+ "	sum(leadsAgDoc) leadsAgDoc,\r\n"
			+ "	sum(valorLeadsAgDoc) valorLeadsAgDoc,\r\n"
			+ "	string_agg(numerosLeadsAgDoc, '#$&!') numerosLeadsAgDoc,\r\n"
			+ "	sum(leadsReprovados) leadsReprovados,\r\n"
			+ "	sum(valorLeadsReprovados) valorLeadsReprovados,\r\n"
			+ "	string_agg(numerosLeadsReprovados, '#$&!') numerosLeadsReprovados,\r\n"
			+ "	sum(leadsCompletos) leadsCompletos,\r\n"
			+ "	sum(valorLeadsCompletos) valorLeadsCompletos,\r\n"
			+ "	string_agg(numerosLeadsCompletos, '#$&!') numerosLeadsCompletos,\r\n"
			+ "	sum(contratosPreAprovados) contratosPreAprovados,\r\n"
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados,\r\n"
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS,\r\n"
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos,\r\n"
			+ "	sum(valorBoletosPagos) valorBoletosPagos,\r\n"
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS,\r\n"
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas,\r\n"
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas,\r\n"
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS,\r\n"
			+ "	sum(contratosRegistrados) contratosRegistrados,\r\n"
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados,\r\n"
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS,\r\n"
			+ "	sum(contratosComite) contratosComite,\r\n"
			+ "	sum(valorComite) valorComite,\r\n"
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		count(c.id) contratosCadastrados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.status != 'Aprovados'\r\n"
			+ "		and c.dataContrato >= ? ::timestamp\r\n"
			+ "		and c.dataContrato <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		count(c.id) leadsEmTratamento,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsEmTratamento,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Em Tratamento'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and LeadEmTratamentoData >= ? ::timestamp\r\n"
			+ "		and LeadEmTratamentoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		count(c.id) leadsAgContato,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgContato,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Contato'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.leadAgContatoData >= ? ::timestamp\r\n"
			+ "		and c.leadAgContatoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		count(c.id) leadsAgDoc,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgDoc,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Doc.'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.leadAgDocData >= ? ::timestamp\r\n"
			+ "		and c.leadAgDocData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		count(c.id) leadsReprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsReprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Reprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and LeadReprovadoData >= ? ::timestamp\r\n"
			+ "		and LeadReprovadoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		count(c.id) leadsCompletos,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsCompletos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Completo'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		count(c.id) contratosPreAprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.cadastroaprovadovalor = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and CadastroAprovadoData >= ? ::timestamp\r\n"
			+ "		and CadastroAprovadoData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		count(c.id) contratosBoletosPagos,\r\n"
			+ "		sum(c.quantoPrecisa) valorBoletosPagos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.pagtolaudoconfirmada = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp\r\n"
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		count(c.id) contratosCcbsEmitidas,\r\n"
			+ "		sum(c.valorccb) valorCcbsEmitidas,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.AgAssinatura = 'false'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and AgAssinaturaData >= ? ::timestamp\r\n"
			+ "		and AgAssinaturaData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		count(c.id) contratosRegistrados,\r\n"
			+ "		sum(c.valorccb) valorContratosRegistrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.status = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and agRegistroData >= ? ::timestamp\r\n"
			+ "		and agRegistroData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		count(c.id) contratosComite,\r\n"
			+ "		sum(c.valorAprovadoComite) valorComite ,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.aprovadoComite = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and aprovadoComiteData >= ? ::timestamp\r\n"
			+ "		and aprovadoComiteData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato ) totais\r\n"
			+ "group by\r\n"
			+ "	cidade";
	
	private static final String QUERY_DASH_CONTRATOS_LEADS_CIDADE_POR_CAMPANHA = "select\r\n"
			+ "	cidade,\r\n"
			+ "	sum(contratosCadastrados) contratosCadastrados,\r\n"
			+ "	sum(valorContratosCadastrados) valorContratosCadastrados,\r\n"
			+ "	string_agg(numerosCadastrados, '#$&!') numerosCadastrados,\r\n"
			+ "	sum(leadsEmTratamento) leadsEmTratamento,\r\n"
			+ "	sum(valorLeadsEmTratamento) valorLeadsEmTratamento,\r\n"
			+ "	string_agg(numerosLeadsEmTratamento, '#$&!') numerosLeadsEmTratamento,\r\n"
			+ "	sum(leadsAgContato) leadsAgContato,\r\n"
			+ "	sum(valorLeadsAgContato) valorLeadsAgContato,\r\n"
			+ "	string_agg(numerosLeadsAgContato, '#$&!') numerosLeadsAgContato,\r\n"
			+ "	sum(leadsAgDoc) leadsAgDoc,\r\n"
			+ "	sum(valorLeadsAgDoc) valorLeadsAgDoc,\r\n"
			+ "	string_agg(numerosLeadsAgDoc, '#$&!') numerosLeadsAgDoc,\r\n"
			+ "	sum(leadsReprovados) leadsReprovados,\r\n"
			+ "	sum(valorLeadsReprovados) valorLeadsReprovados,\r\n"
			+ "	string_agg(numerosLeadsReprovados, '#$&!') numerosLeadsReprovados,\r\n"
			+ "	sum(leadsCompletos) leadsCompletos,\r\n"
			+ "	sum(valorLeadsCompletos) valorLeadsCompletos,\r\n"
			+ "	string_agg(numerosLeadsCompletos, '#$&!') numerosLeadsCompletos,\r\n"
			+ "	sum(contratosPreAprovados) contratosPreAprovados,\r\n"
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados,\r\n"
			+ "	string_agg(numerosPREAPROVADOS, '#$&!') numerosPREAPROVADOS,\r\n"
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos,\r\n"
			+ "	sum(valorBoletosPagos) valorBoletosPagos,\r\n"
			+ "	string_agg(numerosBOLETOSPAGOS, '#$&!') numerosBOLETOSPAGOS,\r\n"
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas,\r\n"
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas,\r\n"
			+ "	string_agg(numerosCCBSEMITIDAS, '#$&!') numerosCCBSEMITIDAS,\r\n"
			+ "	sum(contratosRegistrados) contratosRegistrados,\r\n"
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados,\r\n"
			+ "	string_agg(numerosREGISTRADOS, '#$&!') numerosREGISTRADOS,\r\n"
			+ "	sum(contratosComite) contratosComite,\r\n"
			+ "	sum(valorComite) valorComite,\r\n"
			+ "	string_agg(numerosCOMITE, '#$&!') numerosCOMITE\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		count(c.id) contratosCadastrados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosCadastrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.status != 'Aprovados'\r\n"
			+ "		and c.dataContrato >= ? ::timestamp\r\n"
			+ "		and c.dataContrato <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		count(c.id) leadsEmTratamento,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsEmTratamento,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Em Tratamento'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and LeadEmTratamentoData >= ? ::timestamp\r\n"
			+ "		and LeadEmTratamentoData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		count(c.id) leadsAgContato,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgContato,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Contato'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.leadAgContatoData >= ? ::timestamp\r\n"
			+ "		and c.leadAgContatoData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		count(c.id) leadsAgDoc,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsAgDoc,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Ag. Doc.'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and c.leadAgDocData >= ? ::timestamp\r\n"
			+ "		and c.leadAgDocData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		count(c.id) leadsReprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsReprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Reprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and LeadReprovadoData >= ? ::timestamp\r\n"
			+ "		and LeadReprovadoData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		count(c.id) leadsCompletos,\r\n"
			+ "		sum(c.quantoPrecisa) valorLeadsCompletos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.statuslead = 'Completo'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and leadCompletoData >= ? ::timestamp\r\n"
			+ "		and leadCompletoData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		count(c.id) contratosPreAprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.cadastroaprovadovalor = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and CadastroAprovadoData >= ? ::timestamp\r\n"
			+ "		and CadastroAprovadoData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		count(c.id) contratosBoletosPagos,\r\n"
			+ "		sum(c.quantoPrecisa) valorBoletosPagos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.pagtolaudoconfirmada = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp\r\n"
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		count(c.id) contratosCcbsEmitidas,\r\n"
			+ "		sum(c.valorccb) valorCcbsEmitidas,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.AgAssinatura = 'false'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and AgAssinaturaData >= ? ::timestamp\r\n"
			+ "		and AgAssinaturaData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		count(c.id) contratosRegistrados,\r\n"
			+ "		sum(c.valorccb) valorContratosRegistrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.status = 'Aprovado'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and agRegistroData >= ? ::timestamp\r\n"
			+ "		and agRegistroData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		i.cidade cidade,\r\n"
			+ "		0 contratosCadastrados,\r\n"
			+ "		0 valorContratosCadastrados,\r\n"
			+ "		null numerosCadastrados,\r\n"
			+ "		0 leadsEmTratamento,\r\n"
			+ "		0 valorLeadsEmTratamento,\r\n"
			+ "		null numerosLeadsEmTratamento,\r\n"
			+ "		0 leadsAgContato,\r\n"
			+ "		0 valorLeadsAgContato,\r\n"
			+ "		null numerosLeadsAgContato,\r\n"
			+ "		0 leadsAgDoc,\r\n"
			+ "		0 valorLeadsAgDoc,\r\n"
			+ "		null numerosLeadsAgDoc,\r\n"
			+ "		0 leadsReprovados,\r\n"
			+ "		0 valorLeadsReprovados,\r\n"
			+ "		null numerosLeadsReprovados,\r\n"
			+ "		0 leadsCompletos,\r\n"
			+ "		0 valorLeadsCompletos,\r\n"
			+ "		null numerosLeadsCompletos,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		count(c.id) contratosComite,\r\n"
			+ "		sum(c.valorAprovadoComite) valorComite ,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome), '#$&!' ) numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	inner join cobranca.imovelcobranca i on\r\n"
			+ "		i.id = c.imovel\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.aprovadoComite = 'true'\r\n"
			+ "		and c.contratolead\r\n"
			+ "		and aprovadoComiteData >= ? ::timestamp\r\n"
			+ "		and aprovadoComiteData <= ? ::timestamp\r\n"
			+ "		and c.urllead = ?\r\n"
			+ "	group by\r\n"
			+ "		i.cidade,\r\n"
			+ "		c.datacontrato ) totais\r\n"
			+ "group by\r\n"
			+ "	cidade";
	
	private static final String QUERY_DASH_CONTRATOS_LEADS_MOTIVO_REPROVA = " select "
			+ "	origemLead, "
			+ "	sum(leadVazio) leadVazio, "
			+ "	sum(valorLeadVazio) valorLeadVazio, "
			+ "	string_agg(numerosLeadVazio, '#$&!') numerosLeadVazio, "
			+ "	sum(leadDadosInconsistentes) leadDadosInconsistentes, "
			+ "	sum(valorLeadDadosInconsistentes) valorLeadDadosInconsistentes, "
			+ "	string_agg(numerosLeadDadosInconsistentes, '#$&!') numerosLeadDadosInconsistentes, "
			+ "	sum(leadsClienteNaoAtendeu) leadsClienteNaoAtendeu, "
			+ "	sum(valorLeadsClienteNaoAtendeu) valorLeadsClienteNaoAtendeu, "
			+ "	string_agg(numerosLeadsClienteNaoAtendeu, '#$&!') numerosLeadsClienteNaoAtendeu, "
			+ "	sum(leadsClientesForaPerfil) leadsClientesForaPerfil, "
			+ "	sum(valorLeadsClientesForaPerfil) valorLeadsClientesForaPerfil, "
			+ "	string_agg(numerosLeadsClientesForaPerfil, '#$&!') numerosLeadsClientesForaPerfil, "
			+ "	sum(leadOperacaoDuplicada) leadOperacaoDuplicada, "
			+ "	sum(valorLeadOperacaoDuplicada) valorLeadOperacaoDuplicada, "
			+ "	string_agg(numerosLeadOperacaoDuplicada, '#$&!') numerosLeadOperacaoDuplicada, "
			+ "	sum(leadClienteDesistiu) leadClienteDesistiu, "
			+ "	sum(valorLeadClienteDesistiu) valorLeadClienteDesistiu, "
			+ "	string_agg(numerosLeadClienteDesistiu, '#$&!') numerosLeadClienteDesistiu, "
			+ "	sum(leadImovelRuim) leadImovelRuim, "
			+ "	sum(valorLeadImovelRuim) valorLeadImovelRuim, "
			+ "	string_agg(numerosLeadImovelRuim, '#$&!') numerosLeadImovelRuim, "
			+ "	sum(leadImovelForaPerfil) leadImovelForaPerfil, "
			+ "	sum(valorLeadImovelForaPerfil) valorLeadImovelForaPerfil, "
			+ "	string_agg(numerosLeadImovelForaPerfil, '#$&!') numerosLeadImovelForaPerfil "
			+ "from ( "
			+ "	select "
			+ "		c.urllead origemLead, "
			+ "		count(c.id) leadVazio, "
			+ "		sum(c.quantoprecisa) valorLeadVazio, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and ( motivoreprovalead = '' or motivoreprovalead is null) "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead  "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		count(c.id) leadDadosInconsistentes, "
			+ "		sum(c.quantoprecisa) valorLeadDadosInconsistentes, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Dados Inconsistentes') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		count(c.id) leadsClienteNaoAtendeu, "
			+ "		sum(c.quantoprecisa) valorLeadsClienteNaoAtendeu, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente não atendeu') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		count(c.id) leadsClientesForaPerfil, "
			+ "		sum(c.quantoprecisa) valorLeadsClientesForaPerfil, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente fora do perfil') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		count(c.id) leadOperacaoDuplicada, "
			+ "		sum(c.quantoprecisa) valorLeadOperacaoDuplicada, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Operação Duplicada') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		count(c.id) leadClienteDesistiu, "
			+ "		sum(c.quantoprecisa) valorLeadClienteDesistiu, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente desistiu') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		count(c.id) leadImovelRuim, "
			+ "		sum(c.quantoprecisa) valorLeadImovelRuim, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Imóvel Ruim') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ "union all "
			+ "		select "
			+ "		c.urllead origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		count(c.id) leadImovelForaPerfil, "
			+ "		sum(c.quantoprecisa) valorLeadImovelForaPerfil, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Imóvel fora do perfil') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by c.urllead "
			+ ") totais  "
			+ "group by origemLead";

	private static final String QUERY_DASH_CONTRATOS_LEADS_CIDADES_MOTIVO_REPROVA = " select "
			+ "	origemLead, "
			+ "	sum(leadVazio) leadVazio, "
			+ "	sum(valorLeadVazio) valorLeadVazio, "
			+ "	string_agg(numerosLeadVazio, '#$&!') numerosLeadVazio, "
			+ "	sum(leadDadosInconsistentes) leadDadosInconsistentes, "
			+ "	sum(valorLeadDadosInconsistentes) valorLeadDadosInconsistentes, "
			+ "	string_agg(numerosLeadDadosInconsistentes, '#$&!') numerosLeadDadosInconsistentes, "
			+ "	sum(leadsClienteNaoAtendeu) leadsClienteNaoAtendeu, "
			+ "	sum(valorLeadsClienteNaoAtendeu) valorLeadsClienteNaoAtendeu, "
			+ "	string_agg(numerosLeadsClienteNaoAtendeu, '#$&!') numerosLeadsClienteNaoAtendeu, "
			+ "	sum(leadsClientesForaPerfil) leadsClientesForaPerfil, "
			+ "	sum(valorLeadsClientesForaPerfil) valorLeadsClientesForaPerfil, "
			+ "	string_agg(numerosLeadsClientesForaPerfil, '#$&!') numerosLeadsClientesForaPerfil, "
			+ "	sum(leadOperacaoDuplicada) leadOperacaoDuplicada, "
			+ "	sum(valorLeadOperacaoDuplicada) valorLeadOperacaoDuplicada, "
			+ "	string_agg(numerosLeadOperacaoDuplicada, '#$&!') numerosLeadOperacaoDuplicada, "
			+ "	sum(leadClienteDesistiu) leadClienteDesistiu, "
			+ "	sum(valorLeadClienteDesistiu) valorLeadClienteDesistiu, "
			+ "	string_agg(numerosLeadClienteDesistiu, '#$&!') numerosLeadClienteDesistiu, "
			+ "	sum(leadImovelRuim) leadImovelRuim, "
			+ "	sum(valorLeadImovelRuim) valorLeadImovelRuim, "
			+ "	string_agg(numerosLeadImovelRuim, '#$&!') numerosLeadImovelRuim, "
			+ "	sum(leadImovelForaPerfil) leadImovelForaPerfil, "
			+ "	sum(valorLeadImovelForaPerfil) valorLeadImovelForaPerfil, "
			+ "	string_agg(numerosLeadImovelForaPerfil, '#$&!') numerosLeadImovelForaPerfil "
			+ "from ( "
			+ "	select "
			+ "		i.cidade origemLead, "
			+ "		count(c.id) leadVazio, "
			+ "		sum(c.quantoprecisa) valorLeadVazio, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and ( motivoreprovalead = '' or motivoreprovalead is null) "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		count(c.id) leadDadosInconsistentes, "
			+ "		sum(c.quantoprecisa) valorLeadDadosInconsistentes, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Dados Inconsistentes') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade cidade, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		count(c.id) leadsClienteNaoAtendeu, "
			+ "		sum(c.quantoprecisa) valorLeadsClienteNaoAtendeu, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente não atendeu') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		count(c.id) leadsClientesForaPerfil, "
			+ "		sum(c.quantoprecisa) valorLeadsClientesForaPerfil, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente fora do perfil') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		count(c.id) leadOperacaoDuplicada, "
			+ "		sum(c.quantoprecisa) valorLeadOperacaoDuplicada, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Operação Duplicada') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		count(c.id) leadClienteDesistiu, "
			+ "		sum(c.quantoprecisa) valorLeadClienteDesistiu, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente desistiu') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		count(c.id) leadImovelRuim, "
			+ "		sum(c.quantoprecisa) valorLeadImovelRuim, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Imóvel Ruim') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		count(c.id) leadImovelForaPerfil, "
			+ "		sum(c.quantoprecisa) valorLeadImovelForaPerfil, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Imóvel fora do perfil') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		group by i.cidade "
			+ ") totais "
			+ "group by origemLead";
	
	private static final String QUERY_DASH_CONTRATOS_LEADS_CIDADES_MOTIVO_REPROVA_POR_CAMPANHA = " select "
			+ "	origemLead, "
			+ "	sum(leadVazio) leadVazio, "
			+ "	sum(valorLeadVazio) valorLeadVazio, "
			+ "	string_agg(numerosLeadVazio, '#$&!') numerosLeadVazio, "
			+ "	sum(leadDadosInconsistentes) leadDadosInconsistentes, "
			+ "	sum(valorLeadDadosInconsistentes) valorLeadDadosInconsistentes, "
			+ "	string_agg(numerosLeadDadosInconsistentes, '#$&!') numerosLeadDadosInconsistentes, "
			+ "	sum(leadsClienteNaoAtendeu) leadsClienteNaoAtendeu, "
			+ "	sum(valorLeadsClienteNaoAtendeu) valorLeadsClienteNaoAtendeu, "
			+ "	string_agg(numerosLeadsClienteNaoAtendeu, '#$&!') numerosLeadsClienteNaoAtendeu, "
			+ "	sum(leadsClientesForaPerfil) leadsClientesForaPerfil, "
			+ "	sum(valorLeadsClientesForaPerfil) valorLeadsClientesForaPerfil, "
			+ "	string_agg(numerosLeadsClientesForaPerfil, '#$&!') numerosLeadsClientesForaPerfil, "
			+ "	sum(leadOperacaoDuplicada) leadOperacaoDuplicada, "
			+ "	sum(valorLeadOperacaoDuplicada) valorLeadOperacaoDuplicada, "
			+ "	string_agg(numerosLeadOperacaoDuplicada, '#$&!') numerosLeadOperacaoDuplicada, "
			+ "	sum(leadClienteDesistiu) leadClienteDesistiu, "
			+ "	sum(valorLeadClienteDesistiu) valorLeadClienteDesistiu, "
			+ "	string_agg(numerosLeadClienteDesistiu, '#$&!') numerosLeadClienteDesistiu, "
			+ "	sum(leadImovelRuim) leadImovelRuim, "
			+ "	sum(valorLeadImovelRuim) valorLeadImovelRuim, "
			+ "	string_agg(numerosLeadImovelRuim, '#$&!') numerosLeadImovelRuim, "
			+ "	sum(leadImovelForaPerfil) leadImovelForaPerfil, "
			+ "	sum(valorLeadImovelForaPerfil) valorLeadImovelForaPerfil, "
			+ "	string_agg(numerosLeadImovelForaPerfil, '#$&!') numerosLeadImovelForaPerfil "
			+ "from ( "
			+ "	select "
			+ "		i.cidade origemLead, "
			+ "		count(c.id) leadVazio, "
			+ "		sum(c.quantoprecisa) valorLeadVazio, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and ( motivoreprovalead = '' or motivoreprovalead is null) "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		count(c.id) leadDadosInconsistentes, "
			+ "		sum(c.quantoprecisa) valorLeadDadosInconsistentes, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Dados Inconsistentes') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		count(c.id) leadsClienteNaoAtendeu, "
			+ "		sum(c.quantoprecisa) valorLeadsClienteNaoAtendeu, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente não atendeu') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		count(c.id) leadsClientesForaPerfil, "
			+ "		sum(c.quantoprecisa) valorLeadsClientesForaPerfil, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente fora do perfil') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		count(c.id) leadOperacaoDuplicada, "
			+ "		sum(c.quantoprecisa) valorLeadOperacaoDuplicada, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Operação Duplicada') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		count(c.id) leadClienteDesistiu, "
			+ "		sum(c.quantoprecisa) valorLeadClienteDesistiu, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Cliente desistiu') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		count(c.id) leadImovelRuim, "
			+ "		sum(c.quantoprecisa) valorLeadImovelRuim, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadImovelRuim, "
			+ "		0 leadImovelForaPerfil, "
			+ "		0 valorLeadImovelForaPerfil, "
			+ "		null numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Imóvel Ruim') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ "union all "
			+ "		select "
			+ "		i.cidade origemLead, "
			+ "		0 leadVazio, "
			+ "		0 valorLeadVazio, "
			+ "		null numerosLeadVazio,		 "
			+ "		0 leadDadosInconsistentes, "
			+ "		0 valorLeadDadosInconsistentes, "
			+ "		null numerosLeadDadosInconsistentes, "
			+ "		0 leadsClienteNaoAtendeu, "
			+ "		0 valorLeadsClienteNaoAtendeu, "
			+ "		null numerosLeadsClienteNaoAtendeu, "
			+ "		0 leadsClientesForaPerfil, "
			+ "		0 valorLeadsClientesForaPerfil, "
			+ "		null numerosLeadsClientesForaPerfil, "
			+ "		0 leadOperacaoDuplicada, "
			+ "		0 valorLeadOperacaoDuplicada, "
			+ "		null numerosLeadOperacaoDuplicada, "
			+ "		0 leadClienteDesistiu, "
			+ "		0 valorLeadClienteDesistiu, "
			+ "		null numerosLeadClienteDesistiu, "
			+ "		0 leadImovelRuim, "
			+ "		0 valorLeadImovelRuim, "
			+ "		null numerosLeadImovelRuim, "
			+ "		count(c.id) leadImovelForaPerfil, "
			+ "		sum(c.quantoprecisa) valorLeadImovelForaPerfil, "
			+ "		string_agg(concat(C.numerocontrato, '!&$', P.nome), '#$&!' ) numerosLeadImovelForaPerfil "
			+ "	from "
			+ "		cobranca.contratocobranca c "
			+ "	inner join cobranca.pagadorrecebedor p on "
			+ "		p.id = c.pagador "
			+ "	inner join cobranca.imovelcobranca i on  "
			+ "		i.id = c.imovel "
			+ "	where "
			+ "		c.numerocontrato is not null "
			+ "		and c.statuslead = 'Reprovado' "
			+ "		and c.contratolead "
			+ "		and (motivoreprovalead = 'Imóvel fora do perfil') "
			+ "		and LeadReprovadoData >= ? ::timestamp "
			+ "		and LeadReprovadoData <= ? ::timestamp "
			+ "		and c.urllead = ? "
			+ "		group by i.cidade "
			+ ") totais "
			+ "group by origemLead";
	
	private static final String QUERY_DASH_CONTRATOS_TAXAS_PREAPROVADA = "select\r\n"
			+ "	taxa,\r\n"
			+ "	sum(contratosPreAprovados) contratosPreAprovados,\r\n"
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados,\r\n"
			+ "	string_agg(numerosPREAPROVADOS,\r\n"
			+ "	'#$&!') numerosPREAPROVADOS,\r\n"
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos,\r\n"
			+ "	sum(valorBoletosPagos) valorBoletosPagos,\r\n"
			+ "	string_agg(numerosBOLETOSPAGOS,\r\n"
			+ "	'#$&!') numerosBOLETOSPAGOS,\r\n"
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas,\r\n"
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas,\r\n"
			+ "	string_agg(numerosCCBSEMITIDAS,\r\n"
			+ "	'#$&!') numerosCCBSEMITIDAS,\r\n"
			+ "	sum(contratosRegistrados) contratosRegistrados,\r\n"
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados,\r\n"
			+ "	string_agg(numerosREGISTRADOS,\r\n"
			+ "	'#$&!') numerosREGISTRADOS,\r\n"
			+ "	sum(contratosComite) contratosComite,\r\n"
			+ "	sum(valorComite) valorComite,\r\n"
			+ "	string_agg(numerosCOMITE,\r\n"
			+ "	'#$&!') numerosCOMITE\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,\r\n"
			+ "		count(c.id) contratosPreAprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.cadastroaprovadovalor = 'Aprovado'\r\n"
			+ "		and inicioanalisedata >= ? ::timestamp\r\n"
			+ "		and inicioanalisedata <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		count(c.id) contratosBoletosPagos,\r\n"
			+ "		sum(c.quantoPrecisa) valorBoletosPagos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.pagtolaudoconfirmada = 'true'\r\n"
			+ "		and inicioanalisedata >= ? ::timestamp\r\n"
			+ "		and inicioanalisedata <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,	\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		count(c.id) contratosCcbsEmitidas,\r\n"
			+ "		sum(c.valorccb) valorCcbsEmitidas,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.AgAssinatura = 'false'\r\n"
			+ "		and inicioanalisedata >= ? ::timestamp\r\n"
			+ "		and inicioanalisedata <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,	\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		count(c.id) contratosRegistrados,\r\n"
			+ "		sum(c.valorccb) valorContratosRegistrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.status = 'Aprovado'\r\n"
			+ "		and inicioanalisedata >= ? ::timestamp\r\n"
			+ "		and inicioanalisedata <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		count(c.id) contratosComite,\r\n"
			+ "		sum(c.valorAprovadoComite) valorComite ,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.aprovadoComite = 'true'\r\n"
			+ "		and inicioanalisedata >= ? ::timestamp\r\n"
			+ "		and inicioanalisedata <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato ) totais\r\n"
			+ "group by\r\n"
			+ "	taxa\r\n"
			+ "order by\r\n"
			+ "	taxa asc";
			
	private static final String QUERY_DASH_CONTRATOS_TAXAS_PREAPROVADA_STATUS = "select\r\n"
			+ "	taxa,\r\n"
			+ "	sum(contratosPreAprovados) contratosPreAprovados,\r\n"
			+ "	sum(valorContratosPreAprovados) valorContratosPreAprovados,\r\n"
			+ "	string_agg(numerosPREAPROVADOS,\r\n"
			+ "	'#$&!') numerosPREAPROVADOS,\r\n"
			+ "	sum(contratosBoletosPagos) contratosBoletosPagos,\r\n"
			+ "	sum(valorBoletosPagos) valorBoletosPagos,\r\n"
			+ "	string_agg(numerosBOLETOSPAGOS,\r\n"
			+ "	'#$&!') numerosBOLETOSPAGOS,\r\n"
			+ "	sum(contratosCcbsEmitidas) contratosCcbsEmitidas,\r\n"
			+ "	sum(valorCcbsEmitidas) valorCcbsEmitidas,\r\n"
			+ "	string_agg(numerosCCBSEMITIDAS,\r\n"
			+ "	'#$&!') numerosCCBSEMITIDAS,\r\n"
			+ "	sum(contratosRegistrados) contratosRegistrados,\r\n"
			+ "	sum(valorContratosRegistrados) valorContratosRegistrados,\r\n"
			+ "	string_agg(numerosREGISTRADOS,\r\n"
			+ "	'#$&!') numerosREGISTRADOS,\r\n"
			+ "	sum(contratosComite) contratosComite,\r\n"
			+ "	sum(valorComite) valorComite,\r\n"
			+ "	string_agg(numerosCOMITE,\r\n"
			+ "	'#$&!') numerosCOMITE\r\n"
			+ "from\r\n"
			+ "	(\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,\r\n"
			+ "		count(c.id) contratosPreAprovados,\r\n"
			+ "		sum(c.quantoPrecisa) valorContratosPreAprovados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.cadastroaprovadovalor = 'Aprovado'\r\n"
			+ "		and inicioanalisedata >= ? ::timestamp\r\n"
			+ "		and inicioanalisedata <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		count(c.id) contratosBoletosPagos,\r\n"
			+ "		sum(c.quantoPrecisa) valorBoletosPagos,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.pagtolaudoconfirmada = 'true'\r\n"
			+ "		and pagtoLaudoConfirmadaData >= ? ::timestamp\r\n"
			+ "		and pagtoLaudoConfirmadaData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,	\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		count(c.id) contratosCcbsEmitidas,\r\n"
			+ "		sum(c.valorccb) valorCcbsEmitidas,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.AgAssinatura = 'false'\r\n"
			+ "		and AgAssinaturaData >= ? ::timestamp\r\n"
			+ "		and AgAssinaturaData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,	\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		count(c.id) contratosRegistrados,\r\n"
			+ "		sum(c.valorccb) valorContratosRegistrados,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosREGISTRADOS,\r\n"
			+ "		0 contratosComite,\r\n"
			+ "		0 valorComite,\r\n"
			+ "		null numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.status = 'Aprovado'\r\n"
			+ "		and agRegistroData >= ? ::timestamp\r\n"
			+ "		and agRegistroData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato\r\n"
			+ "union all\r\n"
			+ "	select\r\n"
			+ "		c.taxapreaprovada taxa,\r\n"
			+ "		0 contratosPreAprovados,\r\n"
			+ "		0 valorContratosPreAprovados,\r\n"
			+ "		null numerosPREAPROVADOS,\r\n"
			+ "		0 contratosBoletosPagos,\r\n"
			+ "		0 valorBoletosPagos,\r\n"
			+ "		null numerosBOLETOSPAGOS,\r\n"
			+ "		0 contratosCcbsEmitidas,\r\n"
			+ "		0 valorCcbsEmitidas,\r\n"
			+ "		null numerosCCBSEMITIDAS,\r\n"
			+ "		0 contratosRegistrados,\r\n"
			+ "		0 valorContratosRegistrados,\r\n"
			+ "		null numerosREGISTRADOS,\r\n"
			+ "		count(c.id) contratosComite,\r\n"
			+ "		sum(c.valorAprovadoComite) valorComite ,\r\n"
			+ "		STRING_AGG(CONCAT(C.NUMEROCONTRATO, '!&$', P.nome),\r\n"
			+ "'#$&!' ) numerosCOMITE\r\n"
			+ "	from\r\n"
			+ "		cobranca.contratocobranca c\r\n"
			+ "	inner\r\n"
			+ "join cobranca.pagadorrecebedor p on\r\n"
			+ "		p.id = c.pagador\r\n"
			+ "	where\r\n"
			+ "		c.numerocontrato is not null\r\n"
			+ "		and c.aprovadoComite = 'true'\r\n"
			+ "		and aprovadoComiteData >= ? ::timestamp\r\n"
			+ "		and aprovadoComiteData <= ? ::timestamp\r\n"
			+ "	group by\r\n"
			+ "		c.taxapreaprovada,\r\n"
			+ "		c.datacontrato ) totais\r\n"
			+ "group by\r\n"
			+ "	taxa\r\n"
			+ "order by\r\n"
			+ "	taxa asc";		
	
	private static final String QUERY_DASH_CAMPANHAS = " select  distinct c.urllead "
			+ "from cobranca.contratocobranca c ";
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratosLead(final Date dataInicio, final Date dataFim, boolean consultarPorStatus) {
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
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS_POR_STATUS);					
					} else {					
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS);						
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
					
					ps.setDate(13, dtRelInicioSQL);
					ps.setDate(14, dtRelFimSQL);

					ps.setDate(15, dtRelInicioSQL);
					ps.setDate(16, dtRelFimSQL);
					
					ps.setDate(17, dtRelInicioSQL);
					ps.setDate(18, dtRelFimSQL);
					
					ps.setDate(19, dtRelInicioSQL);
					ps.setDate(20, dtRelFimSQL);		
					
					ps.setDate(21, dtRelInicioSQL);
					ps.setDate(22, dtRelFimSQL);
					
					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					while (rs.next()) {
						dashboard = new Dashboard();
						
						dashboard.setOrigemLead(rs.getString("origemLead"));

						dashboard.setContratosCadastrados(rs.getInt("contratosCadastrados"));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal("valorContratosCadastrados"));					
							
						dashboard.setLeadsEmTratamento(rs.getInt("leadsEmTratamento"));
						dashboard.setValorLeadsEmTratamento(rs.getBigDecimal("valorLeadsEmTratamento"));
						
						dashboard.setLeadsAgContato(rs.getInt("leadsAgContato"));
						dashboard.setValorLeadsAgContato(rs.getBigDecimal("valorLeadsAgContato"));
						
						dashboard.setLeadsAgDoc(rs.getInt("leadsAgDoc"));
						dashboard.setValorLeadsAgDoc(rs.getBigDecimal("valorLeadsAgDoc"));
						
						dashboard.setLeadsReprovados(rs.getInt("leadsReprovados"));
						dashboard.setValorLeadsReprovados(rs.getBigDecimal("valorLeadsReprovados"));
						
						dashboard.setLeadsCompletos(rs.getInt("leadsCompletos"));
						dashboard.setValorLeadscompletos(rs.getBigDecimal("valorLeadsCompletos"));

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
							dashboard.setListaCadastrados(getTaxasDashboard(dashboard.getListaCadastrados()));
						}
						
						List<String> listaLeadsEmTratamento = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsEmTratamento"))) {
							listaLeadsEmTratamento = Arrays.asList(rs.getString("numerosLeadsEmTratamento").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsEmTratamento(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsEmTratamento) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsEmTratamento().add(coco);
							}				
							dashboard.setListaLeadsEmTratamento(getTaxasDashboard(dashboard.getListaLeadsEmTratamento()));
						}
						
						List<String> listaLeadsAgContato = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsAgContato"))) {
							listaLeadsAgContato = Arrays.asList(rs.getString("numerosLeadsAgContato").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsAgContato(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsAgContato) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsAgContato().add(coco);
							}				
							dashboard.setListaLeadsAgContato(getTaxasDashboard(dashboard.getListaLeadsAgContato()));
						}
						
						List<String> listaLeadsAgDoc = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsAgDoc"))) {
							listaLeadsAgDoc = Arrays.asList(rs.getString("numerosLeadsAgDoc").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsAgDoc(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsAgDoc) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsAgDoc().add(coco);
							}				
							dashboard.setListaLeadsAgDoc(getTaxasDashboard(dashboard.getListaLeadsAgDoc()));
						}
						
						List<String> listaLeadsReprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsReprovados"))) {
							listaLeadsReprovados = Arrays.asList(rs.getString("numerosLeadsReprovados").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsReprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsReprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsReprovados().add(coco);
							}				
							dashboard.setListaLeadsReprovados(getTaxasDashboard(dashboard.getListaLeadsReprovados()));
						}
						
						List<String> listaLeadsCompletos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsCompletos"))) {
							listaLeadsCompletos = Arrays.asList(rs.getString("numerosLeadsCompletos").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsCompletos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsCompletos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsCompletos().add(coco);
							}				
							dashboard.setListaLeadsCompletos(getTaxasDashboard(dashboard.getListaLeadsCompletos()));
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
							
							dashboard.setListaPreAprovados(getTaxasDashboard(dashboard.getListaPreAprovados()));
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
							dashboard.setListaBoletosPagos(getTaxasDashboard(dashboard.getListaBoletosPagos()));
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
							
							dashboard.setListaCcbsEmitidas(getTaxasDashboard(dashboard.getListaCcbsEmitidas()));
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
							dashboard.setListaRegistrados(getTaxasDashboard(dashboard.getListaRegistrados()));
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
							dashboard.setListaComite(getTaxasDashboard(dashboard.getListaComite()));
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
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratosLeadCidade(final Date dataInicio, final Date dataFim, String campanha) {
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
					
					if(CommonsUtil.semValor(campanha)) {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS_CIDADE);			
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
						
						ps.setDate(13, dtRelInicioSQL);
						ps.setDate(14, dtRelFimSQL);

						ps.setDate(15, dtRelInicioSQL);
						ps.setDate(16, dtRelFimSQL);
						
						ps.setDate(17, dtRelInicioSQL);
						ps.setDate(18, dtRelFimSQL);
						
						ps.setDate(19, dtRelInicioSQL);
						ps.setDate(20, dtRelFimSQL);
						
						ps.setDate(21, dtRelInicioSQL);
						ps.setDate(22, dtRelFimSQL);
					} else {					
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS_CIDADE_POR_CAMPANHA);	
						
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);
						ps.setString(3, campanha);

						ps.setDate(4, dtRelInicioSQL);
						ps.setDate(5, dtRelFimSQL);
						ps.setString(6, campanha);

						ps.setDate(7, dtRelInicioSQL);
						ps.setDate(8, dtRelFimSQL);
						ps.setString(9, campanha);
						
						ps.setDate(10, dtRelInicioSQL);
						ps.setDate(11, dtRelFimSQL);
						ps.setString(12, campanha);

						ps.setDate(13, dtRelInicioSQL);
						ps.setDate(14, dtRelFimSQL);
						ps.setString(15, campanha);
						
						ps.setDate(16, dtRelInicioSQL);
						ps.setDate(17, dtRelFimSQL);
						ps.setString(18, campanha);
						
						ps.setDate(19, dtRelInicioSQL);
						ps.setDate(20, dtRelFimSQL);
						ps.setString(21, campanha);

						ps.setDate(22, dtRelInicioSQL);
						ps.setDate(23, dtRelFimSQL);
						ps.setString(24, campanha);
						
						ps.setDate(25, dtRelInicioSQL);
						ps.setDate(26, dtRelFimSQL);
						ps.setString(27, campanha);
						
						ps.setDate(28, dtRelInicioSQL);
						ps.setDate(29, dtRelFimSQL);
						ps.setString(30, campanha);
						
						ps.setDate(31, dtRelInicioSQL);
						ps.setDate(32, dtRelFimSQL);
						ps.setString(33, campanha);
					}
					
					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					ResponsavelDao responsavelDao = new ResponsavelDao();
					Responsavel responsavel = new Responsavel();
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					

					while (rs.next()) {
						dashboard = new Dashboard();
						
						dashboard.setOrigemLead(rs.getString("cidade"));

						dashboard.setContratosCadastrados(rs.getInt("contratosCadastrados"));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal("valorContratosCadastrados"));					
							
						dashboard.setLeadsEmTratamento(rs.getInt("leadsEmTratamento"));
						dashboard.setValorLeadsEmTratamento(rs.getBigDecimal("valorLeadsEmTratamento"));
						
						dashboard.setLeadsAgContato(rs.getInt("leadsAgContato"));
						dashboard.setValorLeadsAgContato(rs.getBigDecimal("valorLeadsAgContato"));
						
						dashboard.setLeadsAgDoc(rs.getInt("leadsAgDoc"));
						dashboard.setValorLeadsAgDoc(rs.getBigDecimal("valorLeadsAgDoc"));
						
						dashboard.setLeadsReprovados(rs.getInt("leadsReprovados"));
						dashboard.setValorLeadsReprovados(rs.getBigDecimal("valorLeadsReprovados"));
						
						dashboard.setLeadsCompletos(rs.getInt("leadsCompletos"));
						dashboard.setValorLeadscompletos(rs.getBigDecimal("valorLeadsCompletos"));

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
							dashboard.setListaCadastrados(getTaxasDashboard(dashboard.getListaCadastrados()));
						}
						
						List<String> listaLeadsEmTratamento = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsEmTratamento"))) {
							listaLeadsEmTratamento = Arrays.asList(rs.getString("numerosLeadsEmTratamento").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsEmTratamento(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsEmTratamento) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsEmTratamento().add(coco);
							}				
							dashboard.setListaLeadsEmTratamento(getTaxasDashboard(dashboard.getListaLeadsEmTratamento()));
						}
						
						List<String> listaLeadsAgContato = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsAgContato"))) {
							listaLeadsAgContato = Arrays.asList(rs.getString("numerosLeadsAgContato").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsAgContato(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsAgContato) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsAgContato().add(coco);
							}				
							dashboard.setListaLeadsAgContato(getTaxasDashboard(dashboard.getListaLeadsAgContato()));
						}
						
						List<String> listaLeadsAgDoc = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsAgDoc"))) {
							listaLeadsAgDoc = Arrays.asList(rs.getString("numerosLeadsAgDoc").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsAgDoc(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsAgDoc) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsAgDoc().add(coco);
							}				
							dashboard.setListaLeadsAgDoc(getTaxasDashboard(dashboard.getListaLeadsAgDoc()));
						}
						
						List<String> listaLeadsReprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsReprovados"))) {
							listaLeadsReprovados = Arrays.asList(rs.getString("numerosLeadsReprovados").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsReprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsReprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsReprovados().add(coco);
							}				
							dashboard.setListaLeadsReprovados(getTaxasDashboard(dashboard.getListaLeadsReprovados()));
						}
						
						List<String> listaLeadsCompletos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsCompletos"))) {
							listaLeadsCompletos = Arrays.asList(rs.getString("numerosLeadsCompletos").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsCompletos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsCompletos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsCompletos().add(coco);
							}				
							dashboard.setListaLeadsCompletos(getTaxasDashboard(dashboard.getListaLeadsCompletos()));
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
							
							dashboard.setListaPreAprovados(getTaxasDashboard(dashboard.getListaPreAprovados()));
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
							dashboard.setListaBoletosPagos(getTaxasDashboard(dashboard.getListaBoletosPagos()));
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
							
							dashboard.setListaCcbsEmitidas(getTaxasDashboard(dashboard.getListaCcbsEmitidas()));
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
							dashboard.setListaRegistrados(getTaxasDashboard(dashboard.getListaRegistrados()));
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
							dashboard.setListaComite(getTaxasDashboard(dashboard.getListaComite()));
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
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratosLeadMotivoReprova(final Date dataInicio, final Date dataFim, String tipoPesquisa, String campanha) {
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
					
					if(CommonsUtil.mesmoValor(tipoPesquisa, "Campanha")) {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS_MOTIVO_REPROVA);							
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
						ps.setDate(13, dtRelInicioSQL);
						ps.setDate(14, dtRelFimSQL);
						ps.setDate(15, dtRelInicioSQL);
						ps.setDate(16, dtRelFimSQL);				
						//ps.setDate(17, dtRelInicioSQL);
						//ps.setDate(18, dtRelFimSQL);
					} else if(!CommonsUtil.semValor(campanha)){					
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS_CIDADES_MOTIVO_REPROVA_POR_CAMPANHA);	
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);
						ps.setString(3, campanha);
						ps.setDate(4, dtRelInicioSQL);
						ps.setDate(5, dtRelFimSQL);
						ps.setString(6, campanha);
						ps.setDate(7, dtRelInicioSQL);
						ps.setDate(8, dtRelFimSQL);
						ps.setString(9, campanha);						
						ps.setDate(10, dtRelInicioSQL);
						ps.setDate(11, dtRelFimSQL);
						ps.setString(12, campanha);
						ps.setDate(13, dtRelInicioSQL);
						ps.setDate(14, dtRelFimSQL);
						ps.setString(15, campanha);						
						ps.setDate(16, dtRelInicioSQL);
						ps.setDate(17, dtRelFimSQL);
						ps.setString(18, campanha);					
						ps.setDate(19, dtRelInicioSQL);
						ps.setDate(20, dtRelFimSQL);
						ps.setString(21, campanha);
						ps.setDate(22, dtRelInicioSQL);
						ps.setDate(23, dtRelFimSQL);
						ps.setString(24, campanha);					
						//ps.setDate(25, dtRelInicioSQL);
						//ps.setDate(26, dtRelFimSQL);
						//ps.setString(27, campanha);
					} else {
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_LEADS_CIDADES_MOTIVO_REPROVA);	
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
						ps.setDate(13, dtRelInicioSQL);
						ps.setDate(14, dtRelFimSQL);
						ps.setDate(15, dtRelInicioSQL);
						ps.setDate(16, dtRelFimSQL);						
						//ps.setDate(17, dtRelInicioSQL);
						//ps.setDate(18, dtRelFimSQL);
					}		
					//ps.setDate(1, dtRelInicioSQL); //ps.setDate(2, dtRelFimSQL);
					rs = ps.executeQuery();
					Dashboard dashboard = new Dashboard();					
					while (rs.next()) {
						dashboard = new Dashboard();
						
						dashboard.setOrigemLead(rs.getString("origemLead"));

						dashboard.setContratosCadastrados(rs.getInt("leadVazio"));
						dashboard.setValorContratosCadastrados(rs.getBigDecimal("valorLeadVazio"));					
							
						dashboard.setLeadsEmTratamento(rs.getInt("leadDadosInconsistentes"));
						dashboard.setValorLeadsEmTratamento(rs.getBigDecimal("valorLeadDadosInconsistentes"));
						
						dashboard.setLeadsReprovados(rs.getInt("leadsClienteNaoAtendeu"));
						dashboard.setValorLeadsReprovados(rs.getBigDecimal("valorLeadsClienteNaoAtendeu"));
						
						dashboard.setLeadsCompletos(rs.getInt("leadsClientesForaPerfil"));
						dashboard.setValorLeadscompletos(rs.getBigDecimal("valorLeadsClientesForaPerfil"));

						dashboard.setContratosPreAprovados(rs.getInt("leadOperacaoDuplicada"));
						dashboard.setValorContratosPreAprovados(rs.getBigDecimal("valorLeadOperacaoDuplicada"));
											
						dashboard.setContratosBoletosPagos(rs.getInt("leadClienteDesistiu"));
						dashboard.setValorBoletosPagos(rs.getBigDecimal("valorLeadClienteDesistiu"));
											
						dashboard.setContratosCcbsEmitidas(rs.getInt("leadImovelRuim"));
						dashboard.setValorCcbsEmitidas(rs.getBigDecimal("valorLeadImovelRuim"));
										
						dashboard.setContratosRegistrados(rs.getInt("leadImovelForaPerfil"));
						dashboard.setValorContratosRegistrados(rs.getBigDecimal("valorLeadImovelForaPerfil"));
						
						//recebe os contratos
						List<String> listaCadastrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadVazio"))) {
							listaCadastrados = Arrays.asList(rs.getString("numerosLeadVazio").split(Pattern.quote("#$&!")));
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
							dashboard.setListaCadastrados(getTaxasDashboard(dashboard.getListaCadastrados()));
						}
						
						List<String> listaLeadsEmTratamento = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadDadosInconsistentes"))) {
							listaLeadsEmTratamento = Arrays.asList(rs.getString("numerosLeadDadosInconsistentes").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsEmTratamento(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsEmTratamento) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsEmTratamento().add(coco);
							}				
							dashboard.setListaLeadsEmTratamento(getTaxasDashboard(dashboard.getListaLeadsEmTratamento()));
						}
						
						List<String> listaLeadsReprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsClienteNaoAtendeu"))) {
							listaLeadsReprovados = Arrays.asList(rs.getString("numerosLeadsClienteNaoAtendeu").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsReprovados(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsReprovados) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsReprovados().add(coco);
							}				
							dashboard.setListaLeadsReprovados(getTaxasDashboard(dashboard.getListaLeadsReprovados()));
						}
						
						List<String> listaLeadsCompletos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadsClientesForaPerfil"))) {
							listaLeadsCompletos = Arrays.asList(rs.getString("numerosLeadsClientesForaPerfil").split(Pattern.quote("#$&!")));
							dashboard.setListaLeadsCompletos(new ArrayList<ContratoCobranca>());
							
							for(String cadastro : listaLeadsCompletos) {
								List<String> contrato = new ArrayList<String>();
								contrato = Arrays.asList(cadastro.split(Pattern.quote("!&$")));
								
								ContratoCobranca coco = new ContratoCobranca();
								coco.setPagador(new PagadorRecebedor());
								coco.setNumeroContrato(contrato.get(0));
								coco.getPagador().setNome(contrato.get(1));
								
								dashboard.getListaLeadsCompletos().add(coco);
							}				
							dashboard.setListaLeadsCompletos(getTaxasDashboard(dashboard.getListaLeadsCompletos()));
						}
						
						List<String> listaPreAprovados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadOperacaoDuplicada"))) {
							listaPreAprovados = Arrays.asList(rs.getString("numerosLeadOperacaoDuplicada").split(Pattern.quote("#$&!")));
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
							
							dashboard.setListaPreAprovados(getTaxasDashboard(dashboard.getListaPreAprovados()));
						}
						
						List<String> listaBoletosPagos = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadClienteDesistiu"))) {
							listaBoletosPagos = Arrays.asList(rs.getString("numerosLeadClienteDesistiu").split(Pattern.quote("#$&!")));
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
							dashboard.setListaBoletosPagos(getTaxasDashboard(dashboard.getListaBoletosPagos()));
						}
						
						List<String> listaCcbsEmitidas = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadImovelRuim"))) {
							listaCcbsEmitidas = Arrays.asList(rs.getString("numerosLeadImovelRuim").split(Pattern.quote("#$&!")));
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
							
							dashboard.setListaCcbsEmitidas(getTaxasDashboard(dashboard.getListaCcbsEmitidas()));
						}
						
						List<String> listaRegistrados = new ArrayList<String>();
						if(!CommonsUtil.semValor(rs.getString("numerosLeadImovelForaPerfil"))) {
							listaRegistrados = Arrays.asList(rs.getString("numerosLeadImovelForaPerfil").split(Pattern.quote("#$&!")));
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
							dashboard.setListaRegistrados(getTaxasDashboard(dashboard.getListaRegistrados()));
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
	
	@SuppressWarnings("unchecked")
	public List<String> getCampanhasDashboard() {
		return (List<String>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<String> objects = new ArrayList<String>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_DASH_CAMPANHAS);			
					rs = ps.executeQuery();
					while (rs.next()) {
						if(!CommonsUtil.semValor(rs.getString("urllead"))) {
							objects.add(rs.getString("urllead"));
						}
					}

					
				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}

		
	@SuppressWarnings("unchecked")
	public List<Dashboard> getDashboardContratosTaxasPreAprovado(final Date dataInicio, final Date dataFim, boolean consultarPorStatus) {
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
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_TAXAS_PREAPROVADA_STATUS);					
					} else {					
						ps = connection.prepareStatement(QUERY_DASH_CONTRATOS_TAXAS_PREAPROVADA);						
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

					rs = ps.executeQuery();

					Dashboard dashboard = new Dashboard();

					while (rs.next()) {
						dashboard = new Dashboard();
						
						dashboard.setTaxaOrigem(rs.getBigDecimal("taxa"));

						dashboard.setContratosCadastrados(0);
						dashboard.setValorContratosCadastrados(BigDecimal.ZERO);
						
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
							
							dashboard.setListaPreAprovados(getTaxasDashboard(dashboard.getListaPreAprovados()));
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
							dashboard.setListaBoletosPagos(getTaxasDashboard(dashboard.getListaBoletosPagos()));
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
							
							dashboard.setListaCcbsEmitidas(getTaxasDashboard(dashboard.getListaCcbsEmitidas()));
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
							dashboard.setListaRegistrados(getTaxasDashboard(dashboard.getListaRegistrados()));
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
							dashboard.setListaComite(getTaxasDashboard(dashboard.getListaComite()));
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
}
