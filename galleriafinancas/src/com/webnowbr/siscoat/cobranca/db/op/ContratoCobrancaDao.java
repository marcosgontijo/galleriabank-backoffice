package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.GruposPagadores;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PesquisaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupo;
import com.webnowbr.siscoat.cobranca.vo.DemonstrativoResultadosGrupoDetalhe;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ContratoCobrancaDao extends HibernateDao <ContratoCobranca,Long> {

	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_CONTRATO =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela,  cd.vlrParcela "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cd.parcelapaga = TRUE "
			+ "and cc.status = 'Aprovado' "
			+ "and cc.numerocontrato = ? ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_CONTRATO =  	"select cc.numerocontrato, cd.numeroParcela, cdp.datapagamento, cdp.vlrrecebido, cdp.recebedor, cc.id  from cobranca.contratocobranca cc "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = cc.id "
			+ "inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdpj on cdpj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdp on cdp.id = cdpj.idcontratocobrancadetalhesparcial "
			+ "where cc.numerocontrato= ?";
	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_PERIODO =  	"select cc.numerocontrato, cd.numeroParcela, cdp.datapagamento, cdp.vlrrecebido, cdp.recebedor, cc.id  from cobranca.contratocobranca cc "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = cc.id "
			+ "inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdpj on cdpj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdp on cdp.id = cdpj.idcontratocobrancadetalhesparcial "
			+ "where cdp.datapagamento > ? ::timestamp "
			+ "and cdp.datapagamento < ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_DT_ATUALIZADA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela,  cd.vlrParcela " 
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cd.parcelapaga = TRUE "
			+ "and cc.status = 'Aprovado' "
			+ "and cd.datavencimentoatual >= ? ::timestamp "
			+ "and cd.datavencimentoatual <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_DT_ORIGINAL =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento,  cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela,  cd.vlrParcela " 
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cd.parcelapaga = TRUE "
			+ "and cc.status = 'Aprovado' "
			+ "and cd.datavencimento >= ? ::timestamp "
			+ "and cd.datavencimento <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PARCIAL_CONTRATO =  "select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela " 
			+ "from cobranca.contratocobrancadetalhes cd  "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca  "
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial "
			+ "where cc.status = 'Aprovado' " 
			+ "and cc.numerocontrato = ? ";
	 
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PARCIAL_PERIODO_DT_ATUALIZADA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.datapagamento, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse, cdbp.vlrrecebido - cd.vlrParcela"
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial "			
			+ "where cc.status = 'Aprovado' "
			+ "and cdbp.datavencimentoatual >= ? ::timestamp "
			+ "and cdbp.datavencimentoatual < ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PARCIAL_PERIODO_DT_ORIGINAL =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.datapagamento, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse, cdbp.vlrrecebido - cd.vlrParcela "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial "			
			+ "where cc.status = 'Aprovado' "
			+ "and cdbp.dataVencimento >= ? ::timestamp "
			+ "and cdbp.dataVencimento < ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DT_ATUALIZADA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.datavencimentoatual >= ? ::timestamp "
			+ "and cd.datavencimentoatual <= ? ::timestamp ";	 
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DT_ORIGINAL =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DT_ORIGINAL_PROMESSA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and ((cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp) or (cd.promessaPagamento >= ? ::timestamp and cd.promessaPagamento <= ? ::timestamp)) ";	

	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.datavencimentoatual >= ? ::timestamp "
			+ "and cd.datavencimentoatual <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ORIGINAL =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ORIGINAL_PROMESSA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and ((cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp) or (cd.promessaPagamento >= ? ::timestamp and cd.promessaPagamento <= ? ::timestamp)) ";	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_NUM_CONTRATO =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' ";
	
	private static final String QUERY_REGERAR_PARCELA_NUM_CONTRATO =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' ";
	/*
	private static final String QUERY_ULTIMO_NUMERO_CONTRATO = "select numerocontrato from cobranca.contratocobranca " +
			"order by id desc limit 1"; 
			*/
	private static final String QUERY_ULTIMO_NUMERO_CONTRATO = "select nextval('cobranca.cobranca_seq_contrato')" ;
	
	private static final String QUERY_CONTRATOS_PENDENTES = "select c.id from cobranca.contratocobranca c " +
			"inner join cobranca.responsavel res on c.responsavel = res.id ";
		
	private static final String QUERY_CONTRATOS_QUITADOS = " select dd.id from cobranca.contratocobranca dd " +
		"inner join cobranca.responsavel res on dd.responsavel = res.id " +
		"where dd.id not in (select distinct cc.id from cobranca.contratocobranca cc " + 
		"inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = cc.id " +
		"inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id " +
		"where cd.parcelapaga = false and cc.status='Aprovado') " +
		"and dd.status='Aprovado' ";
	
	private static final String QUERY_DELETE_OBSERVACOES = "delete from cobranca.contratocobrancaobservacoes co " +
														" where not exists ( select * from cobranca.contratocobranca_observacoes_join coj " +
														" where coj.idcontratocobrancaobservacoes = co.id)";
	
	
	private static final String QUERY_OBSERVACOES_ORDENADAS = 	"select co.id from cobranca.contratocobrancaobservacoes co " +
			"inner join cobranca.contratocobranca_observacoes_join coj on coj.idcontratocobrancaobservacoes = co.id " +
			"where coj.idcontratocobranca = ? " + 
			"order by data desc";
	
	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_CONTRATO_TOTAL =  	"SELECT idcontratocobranca, numeroParcela, dataVencimento, valor, vlrRetencao, vlrComissao, parcelaPaga, dataVencimentoatual, vlrRepasse,  vlrParcela, acrescimo " +
			"FROM ( " +
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela   " +
			"	from cobranca.contratocobrancadetalhes cd   " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes   " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes  " +
			"	inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial  " +
			"	where cc.status = 'Aprovado'   " +
			"	and cc.numerocontrato = ? " +
			"	UNION " +
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela  " +
			"	from cobranca.contratocobrancadetalhes cd  " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes  " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	where cd.parcelapaga = TRUE  " +
			"	and cc.status = 'Aprovado'  " +
			"	and cc.numerocontrato = ? " +
			"	) as consulta " +
			"order by numeroParcela ";
	/*
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ORIG_1 =  	"SELECT idcontratocobranca, numeroParcela, dataVencimento, valor, vlrRetencao, vlrComissao, parcelaPaga, dataVencimentoatual, vlrRepasse,  vlrParcela, acrescimo, liquidacao, idContratoCobrancaDetalhes " +
			"FROM ( " +
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcel, cdbp.vlrrecebido - cd.vlrParcela as acrescimo, 'parcial', cd.id  as idContratoCobrancaDetalhes   " +
			"	from cobranca.contratocobrancadetalhes cd   " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes   " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes  " +
			"	inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial  " +
			"	where cc.status = 'Aprovado'   " +
			"	and cdbp.dataVencimento >= ? ::timestamp " +
			"	and cdbp.dataVencimento <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ORIG_2 =  	" UNION " +
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cd.vlrParcela as acrescimo, 'total', cd.id as idContratoCobrancaDetalhes   " +
			"	from cobranca.contratocobrancadetalhes cd  " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes  " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	where cd.parcelapaga = TRUE  " +
			"	and cc.status = 'Aprovado'  " +
			"	and cd.datavencimento >= ? ::timestamp " +
			"	and cd.datavencimento <= ? ::timestamp ";
		
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ATUAL_1 =  	"SELECT idcontratocobranca, numeroParcela, dataVencimento, valor, vlrRetencao, vlrComissao, parcelaPaga, dataVencimentoatual, vlrRepasse,  vlrParcela, acrescimo, liquidacao, idContratoCobrancaDetalhes " +
			"FROM ( " +
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela as acrescimo, 'parcial', cd.id as idContratoCobrancaDetalhes" +
			"	from cobranca.contratocobrancadetalhes cd   " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes   " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes  " +
			"	inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial  " +
			"	where cc.status = 'Aprovado'   " +
			"	and cdbp.datavencimentoatual >= ? ::timestamp " +
			"	and cdbp.datavencimentoatual <= ? ::timestamp " ;	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ATUAL_2 =  	"	UNION " +
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cd.vlrParcela as acrescimo, 'total', cd.id  as idContratoCobrancaDetalhes " +
			"	from cobranca.contratocobrancadetalhes cd  " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes  " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	where cd.parcelapaga = TRUE  " +
			"	and cc.status = 'Aprovado'  " +
			"	and cd.datavencimentoatual >= ? ::timestamp " +
			"	and cd.datavencimentoatual <= ? ::timestamp ";
			*/
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ATUAL_1 =  	
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela as acrescimo, cd.id as idContratoCobrancaDetalhes" +
			"	from cobranca.contratocobrancadetalhes cd   " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes   " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	left join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes  " +
			"	left join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial  " +
			"	where cc.status = 'Aprovado'   " +
			"	and cdbp.datavencimentoatual >= ? ::timestamp " +
			"	and cdbp.datavencimentoatual <= ? ::timestamp "
			+ " order by cdj.idcontratocobranca, idContratoCobrancaDetalhes, cd.numeroParcela " ;	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ORIG_1 =  	
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela as acrescimo, cd.id as idContratoCobrancaDetalhes" +
			"	from cobranca.contratocobrancadetalhes cd   " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes   " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	left join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes  " +
			"	left join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial  " +
			"	where cc.status = 'Aprovado'   " +
			"	and cdbp.dataVencimento >= ? ::timestamp " +
			"	and cdbp.dataVencimento <= ? ::timestamp "
			+ " order by cdj.idcontratocobranca, idContratoCobrancaDetalhes, cd.numeroParcela " ;	
	
	
	public int numeroParcela;
	
	private static final String QUERY_GET_CONTRATOS_POR_INVESTIDOR_INFORME_RENDIMENTOS =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.datacontrato >= ? ::timestamp "
			+ "	and cc.datacontrato <= ? ::timestamp "
			+ "	and cc.status = 'Aprovado'  ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratosPorInvestidorInformeRendimentos(final long idInvestidor, final Date dataInicio, final Date dataFim) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> contratosCobranca = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String query_QUERY_GET_CONTRATOS_POR_INVESTIDOR = QUERY_GET_CONTRATOS_POR_INVESTIDOR_INFORME_RENDIMENTOS;
					
					if (idInvestidor > 0) {
						query_QUERY_GET_CONTRATOS_POR_INVESTIDOR = query_QUERY_GET_CONTRATOS_POR_INVESTIDOR +   
									"  and (cc.recebedor = " + idInvestidor + " or " +
									"      cc.recebedor2 = " + idInvestidor + " or " +
									"      cc.recebedor3 = " + idInvestidor + " or " +
									"      cc.recebedor4 = " + idInvestidor + " or " +
									"      cc.recebedor5 = " + idInvestidor + " or " +
									"      cc.recebedor6 = " + idInvestidor + " or " +
									"      cc.recebedor7 = " + idInvestidor + " or " +
									"      cc.recebedor8 = " + idInvestidor + " or " +
									"      cc.recebedor9 = " + idInvestidor + " or " +
									"      cc.recebedor10 = " + idInvestidor + ")";
						
						ps = connection
								.prepareStatement(query_QUERY_GET_CONTRATOS_POR_INVESTIDOR);		
						
						java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
						java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
		
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);	
		
						rs = ps.executeQuery();
						
						ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
						
						while (rs.next()) {
							contratosCobranca.add(contratoCobrancaDao.findById(rs.getLong(1)));
						}
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return contratosCobranca;
			}
		});	
	}
	
	private static final String QUERY_GET_CONTRATOS_POR_INVESTIDOR =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where 1=1 ";

	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratosPorInvestidor(final long idInvestidor) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> contratosCobranca = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String query_QUERY_GET_CONTRATOS_POR_INVESTIDOR = QUERY_GET_CONTRATOS_POR_INVESTIDOR;
					
					if (idInvestidor > 0) {
						query_QUERY_GET_CONTRATOS_POR_INVESTIDOR = query_QUERY_GET_CONTRATOS_POR_INVESTIDOR +  
									"  and (cc.recebedor = " + idInvestidor + " or " +
									"      cc.recebedor2 = " + idInvestidor + " or " +
									"      cc.recebedor3 = " + idInvestidor + " or " +
									"      cc.recebedor4 = " + idInvestidor + " or " +
									"      cc.recebedor5 = " + idInvestidor + " or " +
									"      cc.recebedor6 = " + idInvestidor + " or " +
									"      cc.recebedor7 = " + idInvestidor + " or " +
									"      cc.recebedor8 = " + idInvestidor + " or " +
									"      cc.recebedor9 = " + idInvestidor + " or " +
									"      cc.recebedor10 = " + idInvestidor + ")";
						
						ps = connection
								.prepareStatement(query_QUERY_GET_CONTRATOS_POR_INVESTIDOR);		
		
						rs = ps.executeQuery();
						
						ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
						
						while (rs.next()) {
							contratosCobranca.add(contratoCobrancaDao.findById(rs.getLong(1)));
						}
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return contratosCobranca;
			}
		});	
	}
	
	private final String QUERY_DATA_VENCIMENTO = "select ? ::timestamp + (? || ' MONTH')::INTERVAL"; 

	@SuppressWarnings("unchecked")
	public String ultimoNumeroContrato() {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String object = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ULTIMO_NUMERO_CONTRATO);				
	
					rs = ps.executeQuery();
					rs.next();
					
					object = rs.getString(1);
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return object;
			}
		});	
	}
	
	private static final String QUERY_OPERACOES_CONTRATO = "select cdj.idcontratocobranca "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.datavencimento >= ? ::timestamp "
			+ "and cd.datavencimento <= ? ::timestamp ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> pesquisaContratoPorData(final Date dataInicio, final Date dataFim) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> contratosCobranca = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_OPERACOES_CONTRATO);		
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
	
					rs = ps.executeQuery();
					
					ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
					
					while (rs.next()) {
						contratosCobranca.add(contratoCobrancaDao.findById(rs.getLong(1)));
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return contratosCobranca;
			}
		});	
	}
	
	private static final String QUERY_RELATORIO_BUSCA_OBSERVACAO = "select co.id, cc.id, cc.numerocontrato from cobranca.contratocobrancaobservacoes co "+
			  "inner join cobranca.contratocobranca_observacoes_join coj on coj.idcontratocobrancaobservacoes = co.id "+
			  "inner join cobranca.contratocobranca cc on cc.id = coj.idcontratocobranca ";
	
	@SuppressWarnings("unchecked")
	public List<PesquisaObservacoes> pesquisaObservacoes(final String texto) {
		return (List<PesquisaObservacoes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<PesquisaObservacoes> pesquisaObservacoesList = new ArrayList<PesquisaObservacoes>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					String QUERY_RELATORIO_BUSCA_OBSERVACAO_CUSTOM;
					QUERY_RELATORIO_BUSCA_OBSERVACAO_CUSTOM = QUERY_RELATORIO_BUSCA_OBSERVACAO + 
							 " where co.observacao like '%" + texto.toUpperCase() + "%'  "+
							  " order by data desc ";
					
					ps = connection
							.prepareStatement(QUERY_RELATORIO_BUSCA_OBSERVACAO_CUSTOM);			
	
					rs = ps.executeQuery();
					
					ContratoCobrancaObservacoes contratoCobrancaObservacoes = new ContratoCobrancaObservacoes();
					ContratoCobrancaObservacoesDao contratoCobrancaObservacoesDao = new ContratoCobrancaObservacoesDao();
					
					while (rs.next()) {
						contratoCobrancaObservacoes = contratoCobrancaObservacoesDao.findById(rs.getLong(1));
						
						PesquisaObservacoes pesquisaObservacoes = new PesquisaObservacoes(contratoCobrancaObservacoes, rs.getLong(2), rs.getString(3));
						pesquisaObservacoesList.add(pesquisaObservacoes);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return pesquisaObservacoesList;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoque(final Date dtRelInicio, final Date dtRelFim, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, 
			final long idResponsavel, final String filtrarDataVencimento, final boolean grupoPagadores, final long idGrupoPagador, final String empresa) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					if (filtrarDataVencimento.equals("Atualizada")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DT_ATUALIZADA;
					} 
					
					if (filtrarDataVencimento.equals("Original")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DT_ORIGINAL;	
					}
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DT_ORIGINAL_PROMESSA;
					}
					
					if (grupoPagadores) {
						if (idGrupoPagador > 0) {
							GruposPagadores gp = new GruposPagadores();
							GruposPagadoresDao gpd = new GruposPagadoresDao();
							
							gp = gpd.findById(idGrupoPagador);
							
							String pagadores = "";
							
							if (gp.getId() > 0) {
								if (gp.getPagador1() != null) {
									if (gp.getPagador1().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador1().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador1().getId());			
										}									
									}
								}
								if (gp.getPagador2() != null) {
									if (gp.getPagador2().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador2().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador2().getId());			
										}									
									}
								}
								if (gp.getPagador3() != null) {
									if (gp.getPagador3().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador3().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador3().getId());			
										}									
									}
								}
								if (gp.getPagador4() != null) {
									if (gp.getPagador4().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador4().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador4().getId());			
										}									
									}
								}
								if (gp.getPagador5() != null) {
									if (gp.getPagador5().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador5().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador5().getId());			
										}									
									}
								}
								if (gp.getPagador6() != null) {
									if (gp.getPagador6().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador6().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador6().getId());			
										}									
									}
								}
								if (gp.getPagador7() != null) {
									if (gp.getPagador7().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador7().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador7().getId());			
										}									
									}
								}
								if (gp.getPagador8() != null) {
									if (gp.getPagador8().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador8().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador8().getId());			
										}									
									}
								}
								if (gp.getPagador9() != null) {
									if (gp.getPagador9().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador9().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador9().getId());			
										}									
									}
								}
								if (gp.getPagador10() != null) {
									if (gp.getPagador10().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador10().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador10().getId());			
										}									
									}
								}								
							}
							
							if (!pagadores.equals("")) {
								query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador in (" + pagadores + ") ";	
							}							
						}
					} else {
						if (idPagador > 0) {
							query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
						}	
					}
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}	
											
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES =  
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}					
					
					if (!empresa.equals("TODAS")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.empresa = '" + empresa + "' ";
					}
																		
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					int params = 0;
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);	
						ps.setDate(3, dtRelInicioSQL);
						ps.setDate(4, dtRelFimSQL);	
						
						params = 4;
					} else {
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);	
						
						params = 2;
					}

					if (!grupoPagadores) {
						if (idPagador > 0) {
							params = params +1;
							ps.setLong(params, idPagador);
						}
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}							
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}

	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoContrato(final String numContrato, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_CONTRATO;
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}			
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}
										
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
								
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					ps.setString(1, numContrato);
					
					int params = 1;
					
					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), rs.getBigDecimal(10), rs.getBigDecimal(11)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoContratoTotal(final String numContrato, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_CONTRATO_TOTAL;
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}			
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}
										
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
								
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					ps.setString(1, numContrato);
					ps.setString(2, numContrato);
					
					int params = 2;
					
					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), rs.getBigDecimal(10), rs.getBigDecimal(11)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroRecebedorContrato(final String numContrato, final long idRecebedor) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDOR = null;
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_CONTRATO;
										
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDOR = 
								"  cdp.recebedor = " + idRecebedor;
					}
								
					if (query_RELATORIO_FINANCEIRO_RECEBEDOR != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and " + query_RELATORIO_FINANCEIRO_RECEBEDOR;
					}
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					ps.setString(1, numContrato);							
	
					rs = ps.executeQuery();
					
					PagadorRecebedorDao pDao = new PagadorRecebedorDao();
					PagadorRecebedor recebedor = new PagadorRecebedor();
					
					while (rs.next()) {
						recebedor = new PagadorRecebedor();
						
						if (rs.getLong(5) > 0) {
							recebedor = pDao.findById(rs.getLong(5));
						}	
						
						ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
						ContratoCobranca contratoTemp = ccDao.findById(rs.getLong(6));
						
						objects.add(new RelatorioFinanceiroCobranca(rs.getString(1), rs.getString(2), rs.getDate(3), rs.getBigDecimal(4), recebedor, contratoTemp));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroRecebedorPeriodo(final Date dtRelInicio, final Date dtRelFim, final long idRecebedor) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDOR = null;
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_PERIODO;
										
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDOR = 
								"  cdp.recebedor = " + idRecebedor;
					}
								
					if (query_RELATORIO_FINANCEIRO_RECEBEDOR != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and " + query_RELATORIO_FINANCEIRO_RECEBEDOR;
					}
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);								
							
	
					rs = ps.executeQuery();
					
					PagadorRecebedorDao pDao = new PagadorRecebedorDao();
					PagadorRecebedor recebedor = new PagadorRecebedor();
					
					while (rs.next()) {
						recebedor = new PagadorRecebedor();
						
						if (rs.getLong(5) > 0) {
							recebedor = pDao.findById(rs.getLong(5));
						}		
						
						ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
						ContratoCobranca contratoTemp = ccDao.findById(rs.getLong(6));
						
						objects.add(new RelatorioFinanceiroCobranca(rs.getString(1), rs.getString(2), rs.getDate(3), rs.getBigDecimal(4), recebedor, contratoTemp));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoPeriodo(final Date dtRelInicio, final Date dtRelFim, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel,
			final String filtrarDataVencimento) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();

					if (filtrarDataVencimento.equals("Atualizada")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_DT_ATUALIZADA;
					} else {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_DT_ORIGINAL;
					}
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}			
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}					
					
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
					int params = 2;
					
					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9),rs.getBigDecimal(10), rs.getBigDecimal(11)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoParcialContrato(final String numContrato, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PARCIAL_CONTRATO;
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}			
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}
										
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}
								
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					ps.setString(1, numContrato);
					
					int params = 1;
					
					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9),rs.getBigDecimal(10), rs.getBigDecimal(11)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoParcialPeriodo(final Date dtRelInicio, final Date dtRelFim, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel,
			final String filtrarDataVencimento) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					if (filtrarDataVencimento.equals("Atualizada")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PARCIAL_PERIODO_DT_ATUALIZADA;
					} else {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PARCIAL_PERIODO_DT_ORIGINAL;
					}
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}			
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}					
					
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}	
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}	
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
					int params = 2;
					
					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), rs.getBigDecimal(10), rs.getBigDecimal(11)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoPeriodoTotal(final Date dtRelInicio, final Date dtRelFim, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel,
			final String filtrarDataVencimento) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM_1 = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM_2 = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					if (filtrarDataVencimento.equals("Atualizada")) {
						query_RELATORIO_FINANCEIRO_CUSTOM_1 = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ATUAL_1;
						//query_RELATORIO_FINANCEIRO_CUSTOM_2 = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ATUAL_2;
					} else {
						query_RELATORIO_FINANCEIRO_CUSTOM_1 = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ORIG_1;
						//query_RELATORIO_FINANCEIRO_CUSTOM_2 = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ORIG_2;
					}
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM_1 = query_RELATORIO_FINANCEIRO_CUSTOM_1 + " and cc.pagador = ?";
						//query_RELATORIO_FINANCEIRO_CUSTOM_2 = query_RELATORIO_FINANCEIRO_CUSTOM_2 + " and cc.pagador = ?";
					}			
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM_1 = query_RELATORIO_FINANCEIRO_CUSTOM_1 + " and cc.responsavel = ?";
						//query_RELATORIO_FINANCEIRO_CUSTOM_2 = query_RELATORIO_FINANCEIRO_CUSTOM_2 + " and cc.pagador = ?";
					}					
					
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}	
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM_1 = query_RELATORIO_FINANCEIRO_CUSTOM_1 + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
						//query_RELATORIO_FINANCEIRO_CUSTOM_2 = query_RELATORIO_FINANCEIRO_CUSTOM_2 + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}	
					
					//query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM_1 + query_RELATORIO_FINANCEIRO_CUSTOM_2;
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM_1 ;
					
					//query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + "	) as consulta order by idcontratocobranca, idContratoCobrancaDetalhes, numeroParcela ";	
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
					//ps.setDate(3, dtRelInicioSQL);
					//ps.setDate(4, dtRelFimSQL);	
					
					int params = 2;
					
					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					long idDetalhe = 0;
					boolean adiciona = false;
					RelatorioFinanceiroCobranca relatorioFinanceiroCobrancaTmp = null;
					String parcelaPagaStr = "";
		
					while (rs.next()) {
						if (idDetalhe == 0) {
							idDetalhe = rs.getLong(12);	
							
							contratoCobranca = findById(rs.getLong(1));
							
							//TODO SUM iddetalhe
							if (contratoCobranca.isGeraParcelaFinal()) {
								int totalParcela = contratoCobranca.getQtdeParcelas() + 1;
								parcela = rs.getString(2) + " de " + totalParcela;
							} else {
								parcela = rs.getString(2) + " de " + contratoCobranca.getQtdeParcelas();
							}
							
							
							if (rs.getBoolean(7)) {
								parcelaPagaStr = "Liquidado";
							} else {
								parcelaPagaStr = "Liq. Parcial";
							}
							
							relatorioFinanceiroCobrancaTmp = new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
									contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), contratoCobranca.getVlrParcela(), rs.getBigDecimal(4).subtract(contratoCobranca.getVlrParcela()), parcelaPagaStr);
							
							if (rs.isLast()) {
								if (relatorioFinanceiroCobrancaTmp.getAcrescimo().compareTo(BigDecimal.ZERO) == -1) {
									relatorioFinanceiroCobrancaTmp.setAcrescimo(BigDecimal.ZERO);
								}
								objects.add(relatorioFinanceiroCobrancaTmp);								
							}
						} else {
							if (idDetalhe != rs.getLong(12)) {
								objects.add(relatorioFinanceiroCobrancaTmp);
								
								idDetalhe = rs.getLong(12);	
								
								contratoCobranca = findById(rs.getLong(1));
								
								//TODO SUM iddetalhe
								if (contratoCobranca.isGeraParcelaFinal()) {
									int totalParcela = contratoCobranca.getQtdeParcelas() + 1;
									parcela = rs.getString(2) + " de " + totalParcela;
								} else {
									parcela = rs.getString(2) + " de " + contratoCobranca.getQtdeParcelas();
								}
								
								if (rs.getBoolean(7)) {
									parcelaPagaStr = "Liquidado";
								} else {
									parcelaPagaStr = "Liq. Parcial";
								}
								
								relatorioFinanceiroCobrancaTmp = new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
										contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), contratoCobranca.getVlrParcela(), rs.getBigDecimal(4).subtract(contratoCobranca.getVlrParcela()), parcelaPagaStr);
								
								if (rs.isLast()) {
									if (relatorioFinanceiroCobrancaTmp.getAcrescimo().compareTo(BigDecimal.ZERO) == -1) {
										relatorioFinanceiroCobrancaTmp.setAcrescimo(BigDecimal.ZERO);
									}
									objects.add(relatorioFinanceiroCobrancaTmp);								
								}
							} else {
								relatorioFinanceiroCobrancaTmp.setValor(relatorioFinanceiroCobrancaTmp.getValor().add(rs.getBigDecimal(4))); 
								relatorioFinanceiroCobrancaTmp.setAcrescimo(relatorioFinanceiroCobrancaTmp.getValor().subtract(contratoCobranca.getVlrParcela()));
								
								if (rs.isLast()) {
									if (relatorioFinanceiroCobrancaTmp.getAcrescimo().compareTo(BigDecimal.ZERO) == -1) {
										relatorioFinanceiroCobrancaTmp.setAcrescimo(BigDecimal.ZERO);
									}
									objects.add(relatorioFinanceiroCobrancaTmp);								
								}
							}
						}
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_CONSULTA_CONTRATOS_ULTIMOS_10 =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.status = 'Aprovado' "
			+ "and cc.pagador not in (15, 34,14, 182, 417, 803) "
			+ "order by cc.datacontrato desc "
			+ "limit 10 ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosUltimos10() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS_ULTIMOS_10;	
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_CONSULTA_CONTRATOS_POR_NUMERO =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.status = 'Aprovado' "
			+ "and cc.pagador not in (15, 34,14, 182, 417, 803) "
			+ "and cc.numerocontrato = ? ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosPorNumeroNaoGalleria(final String numeroContrato) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS_POR_NUMERO;	
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
				
					ps.setString(1, numeroContrato);
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_CONSULTA_CONTRATOS_POR_DATA =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.status = 'Aprovado' ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosNaoGalleria(final String numeroContrato, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4,
			final long idRecebedor5, final long idRecebedor6, final long idRecebedor7,
			final long idRecebedor8, final long idRecebedor9, final long idRecebedor10) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS_POR_DATA;	
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
					
					if (numeroContrato != null && !numeroContrato.equals("")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.numerocontrato = ?";
					}
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}	
					
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador not in (15, 34,14, 182, 417, 803) ";
					
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}
					
					/*
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}
					*/
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					int params = 0;
				
					if (numeroContrato != null && !numeroContrato.equals("")) {
						params = params +1;
						ps.setString(params, numeroContrato);
					}

					if (idPagador > 0) {
						params = params +1;
						ps.setLong(params, idPagador);
					}	
					/*
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}						
					*/
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DIA = "select c.id from cobranca.contratocobranca c "
			+ "where c.status = 'Aprovado' "
			+ " order by numerocontrato ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> relatorioFinanceiroDia() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DIA;	
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoqueAtraso(final Date dtRelInicio, final Date dtRelFim, final long idPagador,
			final long idRecebedor, final long idRecebedor2, final long idRecebedor3, final long idRecebedor4, final long idRecebedor5, 
			final long idRecebedor6, final long idRecebedor7, final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, final long idResponsavel, final String filtrarDataVencimento, final boolean grupoPagadores, final long idGrupoPagador, final String empresa) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;	
				String query_RELATORIO_FINANCEIRO_RECEBEDORES = null;
				try {
					connection = getConnection();
				
					if (filtrarDataVencimento.equals("Atualizada")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA;
					} 
					
					if (filtrarDataVencimento.equals("Original")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ORIGINAL;	
					}
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ORIGINAL_PROMESSA;
					}
					
					if (grupoPagadores) {
						if (idGrupoPagador > 0) {
							GruposPagadores gp = new GruposPagadores();
							GruposPagadoresDao gpd = new GruposPagadoresDao();
							
							gp = gpd.findById(idGrupoPagador);
							
							String pagadores = "";
							
							if (gp.getId() > 0) {
								if (gp.getPagador1() != null) {
									if (gp.getPagador1().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador1().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador1().getId());			
										}									
									}
								}
								if (gp.getPagador2() != null) {
									if (gp.getPagador2().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador2().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador2().getId());			
										}									
									}
								}
								if (gp.getPagador3() != null) {
									if (gp.getPagador3().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador3().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador3().getId());			
										}									
									}
								}
								if (gp.getPagador4() != null) {
									if (gp.getPagador4().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador4().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador4().getId());			
										}									
									}
								}
								if (gp.getPagador5() != null) {
									if (gp.getPagador5().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador5().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador5().getId());			
										}									
									}
								}
								if (gp.getPagador6() != null) {
									if (gp.getPagador6().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador6().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador6().getId());			
										}									
									}
								}
								if (gp.getPagador7() != null) {
									if (gp.getPagador7().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador7().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador7().getId());			
										}									
									}
								}
								if (gp.getPagador8() != null) {
									if (gp.getPagador8().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador8().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador8().getId());			
										}									
									}
								}
								if (gp.getPagador9() != null) {
									if (gp.getPagador9().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador9().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador9().getId());			
										}									
									}
								}
								if (gp.getPagador10() != null) {
									if (gp.getPagador10().getId() > 0) {
										if (!pagadores.equals("")) {
											pagadores = pagadores + ", " + gp.getPagador10().getId();										
										} else {
											pagadores = String.valueOf(gp.getPagador10().getId());			
										}									
									}
								}								
							}
							
							if (!pagadores.equals("")) {
								query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador in (" + pagadores + ") ";	
							}							
						}
					} else {
						if (idPagador > 0) {
							query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
						}	
					}					
					
					if (idRecebedor > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = 
								"  (cc.recebedor = " + idRecebedor + " or " +
								"      cc.recebedor2 = " + idRecebedor + " or " +
								"      cc.recebedor3 = " + idRecebedor + " or " +
								"      cc.recebedor4 = " + idRecebedor + " or " +
								"      cc.recebedor5 = " + idRecebedor + " or " +
								"      cc.recebedor6 = " + idRecebedor + " or " +
								"      cc.recebedor7 = " + idRecebedor + " or " +
								"      cc.recebedor8 = " + idRecebedor + " or " +
								"      cc.recebedor9 = " + idRecebedor + " or " +
								"      cc.recebedor10 = " + idRecebedor + ")";
					}
					
					if (idRecebedor2 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor2 + " or " +
								"      cc.recebedor2 = " + idRecebedor2 + " or " +
								"      cc.recebedor3 = " + idRecebedor2 + " or " +
								"      cc.recebedor4 = " + idRecebedor2 + " or " +
								"      cc.recebedor5 = " + idRecebedor2 + " or " +
								"      cc.recebedor6 = " + idRecebedor2 + " or " +
								"      cc.recebedor7 = " + idRecebedor2 + " or " +
								"      cc.recebedor8 = " + idRecebedor2 + " or " +
								"      cc.recebedor9 = " + idRecebedor2 + " or " +
								"      cc.recebedor10 = " + idRecebedor2 + ")";
					}	
					
					if (idRecebedor3 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor3 + " or " +
								"      cc.recebedor2 = " + idRecebedor3 + " or " +
								"      cc.recebedor3 = " + idRecebedor3 + " or " +
								"      cc.recebedor4 = " + idRecebedor3 + " or " +
								"      cc.recebedor5 = " + idRecebedor3 + " or " +
								"      cc.recebedor6 = " + idRecebedor3 + " or " +
								"      cc.recebedor7 = " + idRecebedor3 + " or " +
								"      cc.recebedor8 = " + idRecebedor3 + " or " +
								"      cc.recebedor9 = " + idRecebedor3 + " or " +
								"      cc.recebedor10 = " + idRecebedor3 + ")";
					}
					
					if (idRecebedor4 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor4 + " or " +
								"      cc.recebedor2 = " + idRecebedor4 + " or " +
								"      cc.recebedor3 = " + idRecebedor4 + " or " +
								"      cc.recebedor4 = " + idRecebedor4 + " or " +
								"      cc.recebedor5 = " + idRecebedor4 + " or " +
								"      cc.recebedor6 = " + idRecebedor4 + " or " +
								"      cc.recebedor7 = " + idRecebedor4 + " or " +
								"      cc.recebedor8 = " + idRecebedor4 + " or " +
								"      cc.recebedor9 = " + idRecebedor4 + " or " +
								"      cc.recebedor10 = " + idRecebedor4 + ")";
					}
					
					if (idRecebedor5 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor5 + " or " +
								"      cc.recebedor2 = " + idRecebedor5 + " or " +
								"      cc.recebedor3 = " + idRecebedor5 + " or " +
								"      cc.recebedor4 = " + idRecebedor5 + " or " +
								"      cc.recebedor5 = " + idRecebedor5 + " or " +
								"      cc.recebedor6 = " + idRecebedor5 + " or " +
								"      cc.recebedor7 = " + idRecebedor5 + " or " +
								"      cc.recebedor8 = " + idRecebedor5 + " or " +
								"      cc.recebedor9 = " + idRecebedor5 + " or " +
								"      cc.recebedor10 = " + idRecebedor5 + ")";
					}
					
					if (idRecebedor6 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor6 + " or " +
								"      cc.recebedor2 = " + idRecebedor6 + " or " +
								"      cc.recebedor3 = " + idRecebedor6 + " or " +
								"      cc.recebedor4 = " + idRecebedor6 + " or " +
								"      cc.recebedor5 = " + idRecebedor6 + " or " +
								"      cc.recebedor6 = " + idRecebedor6 + " or " +
								"      cc.recebedor7 = " + idRecebedor6 + " or " +
								"      cc.recebedor8 = " + idRecebedor6 + " or " +
								"      cc.recebedor9 = " + idRecebedor6 + " or " +
								"      cc.recebedor10 = " + idRecebedor6 + ")";
					}
					
					if (idRecebedor7 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor7 + " or " +
								"      cc.recebedor2 = " + idRecebedor7 + " or " +
								"      cc.recebedor3 = " + idRecebedor7 + " or " +
								"      cc.recebedor4 = " + idRecebedor7 + " or " +
								"      cc.recebedor5 = " + idRecebedor7 + " or " +
								"      cc.recebedor6 = " + idRecebedor7 + " or " +
								"      cc.recebedor7 = " + idRecebedor7 + " or " +
								"      cc.recebedor8 = " + idRecebedor7 + " or " +
								"      cc.recebedor9 = " + idRecebedor7 + " or " +
								"      cc.recebedor10 = " + idRecebedor7 + ")";
					}
					
					if (idRecebedor8 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor8 + " or " +
								"      cc.recebedor2 = " + idRecebedor8 + " or " +
								"      cc.recebedor3 = " + idRecebedor8 + " or " +
								"      cc.recebedor4 = " + idRecebedor8 + " or " +
								"      cc.recebedor5 = " + idRecebedor8 + " or " +
								"      cc.recebedor6 = " + idRecebedor8 + " or " +
								"      cc.recebedor7 = " + idRecebedor8 + " or " +
								"      cc.recebedor8 = " + idRecebedor8 + " or " +
								"      cc.recebedor9 = " + idRecebedor8 + " or " +
								"      cc.recebedor10 = " + idRecebedor8 + ")";
					}
					
					if (idRecebedor9 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor9 + " or " +
								"      cc.recebedor2 = " + idRecebedor9 + " or " +
								"      cc.recebedor3 = " + idRecebedor9 + " or " +
								"      cc.recebedor4 = " + idRecebedor9 + " or " +
								"      cc.recebedor5 = " + idRecebedor9 + " or " +
								"      cc.recebedor6 = " + idRecebedor9 + " or " +
								"      cc.recebedor7 = " + idRecebedor9 + " or " +
								"      cc.recebedor8 = " + idRecebedor9 + " or " +
								"      cc.recebedor9 = " + idRecebedor9 + " or " +
								"      cc.recebedor10 = " + idRecebedor9 + ")";
					}
					
					if (idRecebedor10 > 0) {
						query_RELATORIO_FINANCEIRO_RECEBEDORES = query_RELATORIO_FINANCEIRO_RECEBEDORES + 
								" or (cc.recebedor = " + idRecebedor10 + " or " +
								"      cc.recebedor2 = " + idRecebedor10 + " or " +
								"      cc.recebedor3 = " + idRecebedor10 + " or " +
								"      cc.recebedor4 = " + idRecebedor10 + " or " +
								"      cc.recebedor5 = " + idRecebedor10 + " or " +
								"      cc.recebedor6 = " + idRecebedor10 + " or " +
								"      cc.recebedor7 = " + idRecebedor10 + " or " +
								"      cc.recebedor8 = " + idRecebedor10 + " or " +
								"      cc.recebedor9 = " + idRecebedor10 + " or " +
								"      cc.recebedor10 = " + idRecebedor10 + ")";
					}
					
					if (query_RELATORIO_FINANCEIRO_RECEBEDORES != null) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and (" + query_RELATORIO_FINANCEIRO_RECEBEDORES + ")";
					}
					
					if (idResponsavel > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.responsavel = ?";
					}
					
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cd.parcelapaga = false";
					
					if (!empresa.equals("TODAS")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = " and cc.empresa = " + empresa;
					}
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
					
					int params = 0;
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);	
						ps.setDate(3, dtRelInicioSQL);
						ps.setDate(4, dtRelFimSQL);	
						
						params = 4;
					} else {
						ps.setDate(1, dtRelInicioSQL);
						ps.setDate(2, dtRelFimSQL);	
						
						params = 2;
					}
					
					if (!grupoPagadores) {
						if (idPagador > 0) {
							params = params +1;
							ps.setLong(params, idPagador);
						}	
					}
					
					if (idResponsavel > 0) {
						params = params +1;
						ps.setLong(params, idResponsavel);
					}						
	
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
												
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ULTIMA_PARCELA =  	" select id from ( "
			+ " select count(cc.id) qtdeparcelas, cc.id from cobranca.contratocobrancadetalhes cd "
			+ " inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ " inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca  "
			+ " where cd.parcelapaga = false "
			+ " group by cc.id, cc.numerocontrato) buscaparcelas "
			+ " where qtdeparcelas = 1";
			
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoqueUltimaParcela() {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;	
				
				try {
					connection = getConnection();

					ps = connection
							.prepareStatement(QUERY_RELATORIO_FINANCEIRO_ULTIMA_PARCELA);				
	
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));										
						
						for (ContratoCobrancaDetalhes ct : contratoCobranca.getListContratoCobrancaDetalhes()) {
							
							parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
							
							if (!ct.isParcelaPaga()) {
								objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
										contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, ct.getDataVencimento(), ct.getVlrParcela(), contratoCobranca, ct.getVlrRetencao(), ct.getVlrComissao(), ct.isParcelaPaga(), ct.getDataVencimentoAtual(), ct.getId(), ct.getVlrRepasse()));
							}
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
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoqueNumContrato(final String numContrato) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;				
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_NUM_CONTRATO;
					
					if (numContrato != null) {
						if (numContrato != "") {
							query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.numerocontrato = ?";
						}
						
					}

					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);			
	
					ps.setString(1, numContrato);
					
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
												
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioRegerarParcela(final String numContrato) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;				
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_REGERAR_PARCELA_NUM_CONTRATO;
					
					if (numContrato != null) {
						if (numContrato != "") {
							query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.numerocontrato = ?";
						}
						
					}

					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);			
	
					ps.setString(1, numContrato);
					
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
												
						parcela = rs.getString(2) + " de " + contratoCobranca.getListContratoCobrancaDetalhes().size();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
								contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public Date geraDataParcela(final int numParcela, final Date dtInicio) {
		return (Date) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Date objects = new Date();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_DATA_VENCIMENTO);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtInicio.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setInt(2, numParcela);
	
					rs = ps.executeQuery();
					rs.next();
					
					objects = rs.getDate(1);
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}

	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosPendentesQuitados(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_QUITADOS;
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}
						query = query + " order by id desc";						
					} else {
						query = query + " order by id desc";
					}
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public List<Long> consultaIdContratosQuitadosInvestidor(final long idInvestidor) {
		return (List<Long>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<Long> objects = new ArrayList<Long>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_QUITADOS +  
							"  and (dd.recebedor = " + idInvestidor + " or " +
							"      dd.recebedor2 = " + idInvestidor + " or " +
							"      dd.recebedor3 = " + idInvestidor + " or " +
							"      dd.recebedor4 = " + idInvestidor + " or " +
							"      dd.recebedor5 = " + idInvestidor + " or " +
							"      dd.recebedor6 = " + idInvestidor + " or " +
							"      dd.recebedor7 = " + idInvestidor + " or " +
							"      dd.recebedor8 = " + idInvestidor + " or " +
							"      dd.recebedor9 = " + idInvestidor + " or " +
							"      dd.recebedor10 = " + idInvestidor + ")";
					
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();				
					
					
					while (rs.next()) {
						objects.add(rs.getLong(1));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosPendentesReprovados(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES;
					
					query = query + "where (status = 'Reprovado' or status = 'Desistência Cliente' ) " ;
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}
						query = query + " order by id desc";						
					} else {
						query = query + " order by id desc";
					}
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_CONTRATOS_APROVADOS_ALL = "select c.id from cobranca.contratocobranca c ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosAprovados() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_APROVADOS_ALL;
					
					query = query + "where status = 'Aprovado'" ;

					query = query + " order by id";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
		
	private static final String QUERY_CONTRATOS_APROVADOS = "select c.id from cobranca.contratocobranca c " +
			"inner join cobranca.responsavel res on c.responsavel = res.id ";
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosAprovados(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_APROVADOS;
					
					query = query + "where status = 'Aprovado'" ;
					
					query = query + " and pagador not in (15, 34,14, 182, 417, 803) ";
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}
						query = query + " order by id desc";						
					} else {
						query = query + " order by id desc";
					}
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosAprovadosGalleria(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_APROVADOS;
					
					query = query + "where status = 'Aprovado'" ;
					
					query = query + " and pagador in (15, 34,14, 182, 417, 803) ";
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}
						query = query + " order by id desc";						
					} else {
						query = query + " order by id desc";
					}
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaLeads(final String completo) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Desistência Cliente'" ;
					
					query = query + " and c.responsavel = 46 ";
					
					if (completo.equals("lead_completo")) {
						query = query + " and c.leadcompleto = true ";
					}
					
					query = query + " order by id desc";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosPendentes(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Desistência Cliente'" ;
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}
						query = query + " order by id desc";						
					} else {
						query = query + " order by id desc";
					}
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosPendentesResponsaveis(final String codResponsavel, final List<Responsavel> listResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Desistência Cliente'" ;
					
					String queryResponsavel = " res.codigo = '" + codResponsavel + "' ";
					
					if (listResponsavel.size() > 0) {
						for (Responsavel resp : listResponsavel) {
							if (!resp.getCodigo().equals("")) { 
								queryResponsavel = queryResponsavel + " or res.codigo = '" + resp.getCodigo() + "' ";
							}
						}

						if (!queryResponsavel.equals("")) {
							query = query + " and (" + queryResponsavel + ") ";
						}
						
						query = query + " order by id desc";						
					} else {
						query = query + " order by id desc";
					}
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	public Boolean limpaObservacoesNaoUsadas() {
		return (Boolean) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_DELETE_OBSERVACOES);

					ps.executeUpdate();
					return true;
						
				} finally {
					closeResources(connection, ps);
				}
			}
		});
	}  
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaObservacoes> listaObservacoesOrdenadas(final long idContrato) {
		return (List<ContratoCobrancaObservacoes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobrancaObservacoes> objects = new ArrayList<ContratoCobrancaObservacoes>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_OBSERVACOES_ORDENADAS);
					
					ps.setLong(1, idContrato);

					rs = ps.executeQuery();
					
					ContratoCobrancaObservacoesDao contratoCobrancaObservacoesDao = new ContratoCobrancaObservacoesDao();
					while (rs.next()) {
						objects.add(contratoCobrancaObservacoesDao.findById(rs.getLong(1)));												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_PARCELAS_POR_NUMERO_CONTRATO =  "select cd.id "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cc.numerocontrato = ? "
			+ "order by cd.id";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaDetalhes> getParcelasContratoPorNumeroContrato(final String numContrato) {
		return (List<ContratoCobrancaDetalhes>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaDetalhes> objects = new ArrayList<ContratoCobrancaDetalhes>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_PARCELAS_POR_NUMERO_CONTRATO);						
					
					ps.setString(1, numContrato);
					
					rs = ps.executeQuery();
					
					ContratoCobrancaDetalhes contratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					
					while (rs.next()) {
						contratoCobrancaDetalhes = contratoCobrancaDetalhesDao.findById(rs.getLong(1));
						
						objects.add(contratoCobrancaDetalhes);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				
				return objects;
			}
		});	
	}
	
	private static final String QUERY_CONTRATO_POR_DATA_CONTRATO =  "select cc.id from cobranca.contratocobranca cc "
			+ "where cc.status = 'Aprovado' "
			+ "and empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' "
			+ "and cc.datacontrato >= ? ::timestamp "
			+ "and cc.datacontrato <= ? ::timestamp ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratoPorDataContrato(final Date dtRelInicio, final Date dtRelFim) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_CONTRATO_POR_DATA_CONTRATO);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
					
					while (rs.next()) {
						contratoCobranca = ccDao.findById(rs.getLong(1));
						
						if (contratoCobranca.getPagador().getId() != 14) { 
							objects.add(contratoCobranca);		
						}									
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				
				return objects;
			}
		});	
	}
	
	private static final String QUERY_CONTRATOS_CRM = "select c.id from cobranca.contratocobranca c " +
			"inner join cobranca.responsavel res on c.responsavel = res.id ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> geraConsultaContratosCRM(final String codResponsavel, final List<Responsavel> listResponsavel, final String tipoConsulta) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_CRM;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Desistência Cliente'" ;
					
					// Verifica o tipo da consulta de contratos
					if (tipoConsulta.equals("Lead")) {
						query = query + " and inicioanalise = false ";
					}
					
					if (tipoConsulta.equals("Em Analise")) {
						query = query + " and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and (matriculaAprovadaValor = '' or matriculaAprovadaValor is null) ";
					}
					
					if (tipoConsulta.equals("Ag. Pagto. Boleto")) {
						query = query + " and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = false ";
					}
					
					if (tipoConsulta.equals("Ag. PAJU e Laudo")) {
						query = query + " and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = true and (laudoRecebido = false or pajurFavoravel = false) ";
					}
					
					if (tipoConsulta.equals("Ag. DOC")) {
						query = query + " and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and documentosCompletos = false ";
					}
					
					if (tipoConsulta.equals("Ag. CCB")) {
						query = query + " and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and documentosCompletos = true and ccbPronta = false ";
					}
					
					if (tipoConsulta.equals("Ag. Assinatura")) {
						query = query + " and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and matriculaAprovadaValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and documentosCompletos = true and ccbPronta = true  and agAssinatura = true";
					}
					
					String queryResponsavel = "";
							
					// verifica as cláusulas dos repsonsáveis
					if (codResponsavel != null || listResponsavel != null) {
						if (!queryResponsavel.equals("")) {
							queryResponsavel = " and (res.codigo = '" + codResponsavel + "' ";
						}
						
						String queryGuardaChuva = "";
						if (listResponsavel.size() > 0) {							
							for (Responsavel resp : listResponsavel) {
								if (!resp.getCodigo().equals("")) { 
									if (queryGuardaChuva.equals("")) {
										queryGuardaChuva = " and (res.codigo = '" + resp.getCodigo() + "' ";
									} else {
										queryGuardaChuva = queryGuardaChuva + " or res.codigo = '" + resp.getCodigo() + "' ";
									}									
								}
							}
						}											
						
						if (!queryResponsavel.equals("")) {
							query = query + queryResponsavel;
							
							if (!queryGuardaChuva.equals("")) {
								query = query + " or " + queryGuardaChuva;
							}
							
							query = query + ")";
						}
					}
					
					query = query + " order by id desc";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}

	private static final String QUERY_RELATORIO_VENDA_OPERACAO =  	
			" select coco.id, numerocontrato, datapagamentofim , pare.nome, coco.vlrparcela," + 
			" cobranca.CalculoContratoAntecipado(coco.vlrparcela, datapagamentoini, vlrparcelafinal , qtdeparcelas ,?::numeric(19,2), ?) valorVenda, " + 
			" cobranca.calculocontratofaltavender( coco.id, coco.vlrparcela, datapagamentoini, vlrparcelafinal , qtdeparcelas ,?::numeric(19,2)) faltaVender, " + 
			" cobranca.contratoEmDia(coco.id) contratoEmDia," + 
			" case when coco.vlrparcela = vlrparcelafinal then 'Americano' else 'Price' end Sistema " + 
			" from  cobranca.contratocobranca coco " + 
			" inner join cobranca.pagadorrecebedor pare on coco.pagador = pare.id" +  
			" where empresa like 'GALLERIA FINANÇAS SECURITIZADORA S.A.' " + 
			" and coco.status = 'Aprovado' "			+ 
			" and coco.pagador <> ? " +
			" and coco.id in (select distinct cc.id from cobranca.contratocobranca cc " + 
			" inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = cc.id " + 
			" inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id " + 
			" where cd.parcelapaga = false and cc.status='Aprovado') " ;	
	
	@SuppressWarnings("unchecked")
	public List<RelatorioVendaOperacaoVO> geraRelatorioVendaOperacao(BigDecimal taxaDesagio, Date dataDesagio) throws Exception {

		List<RelatorioVendaOperacaoVO> result = new ArrayList<RelatorioVendaOperacaoVO>(0);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String query = QUERY_RELATORIO_VENDA_OPERACAO;
			
			connection = getConnection();	
			
			ps = connection
					.prepareStatement(query);	

			ps.setBigDecimal(1, taxaDesagio);
			java.sql.Date dataDesagioSQL = new java.sql.Date(dataDesagio.getTime());
			ps.setDate(2, dataDesagioSQL);
			ps.setBigDecimal(3, taxaDesagio);
			ps.setInt(4, SiscoatConstants.GALLERIA_FINANCAS_ID);

			rs = ps.executeQuery();				
			
			while (rs.next()) {

				RelatorioVendaOperacaoVO relatorioVendaOperacaoVO = new RelatorioVendaOperacaoVO(
						rs.getLong("id"), // BigInteger
						rs.getString("numerocontrato"), // String contrato
						rs.getDate("datapagamentofim"), // Date ultimaParcela
						rs.getString("Sistema"), // String sistema
						rs.getString("nome"), // String pagador
						rs.getBigDecimal("vlrparcela"), // String BigDecimal valorParcela
						rs.getBigDecimal("valorVenda"), // String BigDecimal valorVenda
						rs.getBigDecimal("faltaVender"), // String BigDecimal faltaVender
						rs.getBoolean("contratoEmDia") // String Boolean situacao
				);

				result.add(relatorioVendaOperacaoVO);
			}

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return result;
	}


	
	private static final String QUERY_GET_DRE_ENTRADAS =  	"select codj.idContratoCobrancaDetalhes, codj.idContratoCobranca, coco.numeroContrato, pare.nome, ccde.numeroParcela, ccde.vlrparcela, vlrjurosparcela, vlramortizacaoparcela, datavencimento, datavencimentoatual"
			+ " from cobranca.ContratoCobrancaDetalhes ccde"
			+ " inner join cobranca.ContratoCobranca_Detalhes_Join codj on ccde.id = codj.idContratoCobrancaDetalhes"
			+ " inner join cobranca.ContratoCobranca coco on  codj.idContratoCobranca = coco.id"
			+ " inner join cobranca.pagadorrecebedor pare on coco.pagador = pare.id"
			+ " where datavencimentoatual between  ? ::timestamp and  ? ::timestamp"
			+ " and parcelapaga = true"
			+ " and coco.status = 'Aprovado' "
			+ " and coco.pagador not in (15, 34,14, 182, 417, 803) "
			+ " order by datavencimentoatual;";
	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreEntradas(final Date dataInicio, final Date dataFim) throws Exception {
		
				DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
				demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
				demonstrativosResultadosGrupoDetalhe.setTipo("Entradas");

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_DRE_ENTRADAS = QUERY_GET_DRE_ENTRADAS;

					ps = connection.prepareStatement(query_QUERY_GET_DRE_ENTRADAS);

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					rs = ps.executeQuery();

					while (rs.next()) {

						DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();

						demonstrativoResultadosGrupoDetalhe
								.setIdDetalhes(rs.getLong("idContratoCobrancaDetalhes"));
						demonstrativoResultadosGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
						demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
						demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
						demonstrativoResultadosGrupoDetalhe.setNumeroParcela(rs.getInt("numeroParcela"));
						
						
						Date dataVencimento = rs.getDate("datavencimentoatual");
						if( dataVencimento == null)
							 dataVencimento = rs.getDate("datavencimento");
						
						demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataVencimento);
						demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("vlrparcela"));
						demonstrativoResultadosGrupoDetalhe.setJuros(rs.getBigDecimal("vlrjurosparcela"));
						demonstrativoResultadosGrupoDetalhe.setAmortizacao(rs.getBigDecimal("vlramortizacaoparcela"));
						demonstrativosResultadosGrupoDetalhe.getDetalhe().add(demonstrativoResultadosGrupoDetalhe);
						
						demonstrativosResultadosGrupoDetalhe.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
						demonstrativosResultadosGrupoDetalhe.addJuros(demonstrativoResultadosGrupoDetalhe.getJuros());
						demonstrativosResultadosGrupoDetalhe.addAmortizacao(demonstrativoResultadosGrupoDetalhe.getAmortizacao());
						
					}
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				} finally {
					closeResources(connection, ps, rs);
				}
				return demonstrativosResultadosGrupoDetalhe;
			
	}
	
	private static final String QUERY_GET_DRE_SAIDAS = "select   ccpi.id , coco.id idContratoCobranca, coco.numeroContrato, pare.nome,  ccpi.numeroParcela, ccpi.parcelamensal,  ccpi.valorbaixado,  ccpi.juros, ccpi.amortizacao, ccpi.databaixa"
			+ " from cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_1 ccpi1 on  ccpi.id = ccpi1.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_2 ccpi2 on  ccpi.id = ccpi2.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_3 ccpi3 on  ccpi.id = ccpi3.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_4 ccpi4 on  ccpi.id = ccpi4.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_5 ccpi5 on  ccpi.id = ccpi5.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_6 ccpi6 on  ccpi.id = ccpi6.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_7 ccpi7 on  ccpi.id = ccpi7.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_8 ccpi8 on  ccpi.id = ccpi8.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_9 ccpi9 on  ccpi.id = ccpi9.idContratoCobrancaParcelasInvestidor"
	 + " left join cobranca.ContratoCobranca_Parcelas_Investidor_Join_10 ccpi10 on  ccpi.id = ccpi10.idContratoCobrancaParcelasInvestidor"
	 + " inner join  cobranca.contratocobranca coco on  coco.id =  case when ccpi1.idContratoCobrancaParcelasInvestidor is not null then ccpi1.idcontratocobrancaparcelasinvestidor1"
	 + "                                                                when ccpi2.idContratoCobrancaParcelasInvestidor is not null then ccpi2.idcontratocobrancaparcelasinvestidor2"
	 + "                                                                when ccpi3.idContratoCobrancaParcelasInvestidor is not null then ccpi3.idcontratocobrancaparcelasinvestidor3"
	 + "                                                                when ccpi4.idContratoCobrancaParcelasInvestidor is not null then ccpi4.idcontratocobrancaparcelasinvestidor4"
	 + "                                                                when ccpi5.idContratoCobrancaParcelasInvestidor is not null then ccpi5.idcontratocobrancaparcelasinvestidor5"
	 + "                                                                when ccpi6.idContratoCobrancaParcelasInvestidor is not null then ccpi6.idcontratocobrancaparcelasinvestidor6"
	 + "                                                                when ccpi7.idContratoCobrancaParcelasInvestidor is not null then ccpi7.idcontratocobrancaparcelasinvestidor7"
	 + "                                                                when ccpi8.idContratoCobrancaParcelasInvestidor is not null then ccpi8.idcontratocobrancaparcelasinvestidor8"
	 + "                                                                when ccpi9.idContratoCobrancaParcelasInvestidor is not null then ccpi9.idcontratocobrancaparcelasinvestidor9"
	 + "                                                                when ccpi10.idContratoCobrancaParcelasInvestidor is not null then ccpi10.idcontratocobrancaparcelasinvestidor10"
	 + "                                                           end"
	 + " inner join cobranca.pagadorrecebedor pare on   pare.id = case when ccpi1.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor"
	 + "                                                               when ccpi2.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor2"
	 + "                                                               when ccpi3.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor3"
	 + "                                                               when ccpi4.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor4"
	 + "                                                               when ccpi5.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor5"
	 + "                                                               when ccpi6.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor6"
	 + "                                                               when ccpi7.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor7"
	 + "                                                               when ccpi8.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor8"
	 + "                                                               when ccpi9.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor9"
	 + "                                                               when ccpi10.idContratoCobrancaParcelasInvestidor is not null then coco.recebedor10"
	 + "                                                           end"
	 + " where databaixa between ? ::timestamp and  ? ::timestamp"
	 + " and coco.status = 'Aprovado'"
	 + " and coco.pagador in (15, 34,14, 182, 417, 803)" 
	 + " and baixado = true"
	 + " order by numerocontrato;";	
	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreSaidas(final Date dataInicio, final Date dataFim) throws Exception {
		
				DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
				demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
				demonstrativosResultadosGrupoDetalhe.setTipo("Saidas");

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_DRE_SAIDAS = QUERY_GET_DRE_SAIDAS;

					ps = connection.prepareStatement(query_QUERY_GET_DRE_SAIDAS);

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);

					rs = ps.executeQuery();

					while (rs.next()) {

						DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();

						demonstrativoResultadosGrupoDetalhe
								.setIdDetalhes(rs.getLong("id"));
						demonstrativoResultadosGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
						demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
						demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
						demonstrativoResultadosGrupoDetalhe.setNumeroParcela(rs.getInt("numeroParcela"));
						Date dataVencimento = rs.getDate("databaixa");						
						demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataVencimento);
						demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("valorbaixado"));
						demonstrativoResultadosGrupoDetalhe.setJuros(rs.getBigDecimal("juros"));
						demonstrativoResultadosGrupoDetalhe.setAmortizacao(rs.getBigDecimal("amortizacao"));
						demonstrativosResultadosGrupoDetalhe.getDetalhe().add(demonstrativoResultadosGrupoDetalhe);
						
						demonstrativosResultadosGrupoDetalhe.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
						demonstrativosResultadosGrupoDetalhe.addJuros(demonstrativoResultadosGrupoDetalhe.getJuros());
						demonstrativosResultadosGrupoDetalhe.addAmortizacao(demonstrativoResultadosGrupoDetalhe.getAmortizacao());
						
					}
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				} finally {
					closeResources(connection, ps, rs);
				}
				return demonstrativosResultadosGrupoDetalhe;
			
	}
	
	
}
