package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.hibernate.engine.JoinSequence.Join;
import org.jboss.resteasy.util.CommitHeaderOutputStream;

import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.AnaliseComite;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaBRLLiquidacao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaStatus;
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
	
	private static final String QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_ATRASO =  	"select cc.numerocontrato, cd.numeroParcela, cdp.datapagamento, cdp.vlrrecebido, cdp.recebedor, cc.id  from cobranca.contratocobranca cc "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = cc.id "
			+ "inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id "
			
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdpj on cdpj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdp on cdp.id = cdpj.idcontratocobrancadetalhesparcial "
			+ "where cdp.dataVencimentoAtual > ? ::timestamp "
			+ "and cdp.dataVencimentoAtual < ? ::timestamp "
			+ "and cd.parcelaVencida = 'true' ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_ATRASO_NOVO = "select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_1 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor1 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_2 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor2 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_3 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor3 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_4 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor4 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_5 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor5 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_6 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor6 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_7 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor7 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_8 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor8 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_9 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor9 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false'"
			+ "		union all"
			+ "	select cc.numerocontrato, ccpi.numeroParcela, ccpi.dataVencimento, ccpi.saldoCredorAtualizado, ccpi.investidor, cc.id"
			+ "		from  cobranca.contratocobrancaparcelasinvestidor ccpi"
			+ "		inner join cobranca.contratocobranca_parcelas_investidor_join_10 parcelaJoin on ccpi.id = parcelaJoin.idcontratocobrancaparcelasinvestidor"
			+ "		inner join cobranca.contratocobranca cc  on parcelaJoin.idcontratocobrancaparcelasinvestidor10 = cc.id"
			+ "		where ccpi.dataVencimento > ? ::timestamp "
			+ "		and ccpi.dataVencimento < ? ::timestamp "
			+ "		and ccpi.baixado = 'false' ";
	
	
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
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0)))"
			+ "and cd.datavencimentoatual >= ? ::timestamp "
			+ "and cd.datavencimentoatual <= ? ::timestamp ";	 
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DT_ORIGINAL =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0))) "
			+ "and cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DT_ORIGINAL_PROMESSA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0)))"
			+ "and ((cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp) or (cd.promessaPagamento >= ? ::timestamp and cd.promessaPagamento <= ? ::timestamp)) ";	

	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0))) "
			+ "and cd.datavencimentoatual >= ? ::timestamp "
			+ "and cd.datavencimentoatual <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ORIGINAL =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0))) "
			+ "and cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ORIGINAL_PROMESSA =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0)))"
			+ "and ((cd.dataVencimento >= ? ::timestamp "
			+ "and cd.dataVencimento <= ? ::timestamp) or (cd.promessaPagamento >= ? ::timestamp and cd.promessaPagamento <= ? ::timestamp)) ";	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_NUM_CONTRATO =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where (cc.status = 'Aprovado' or (cc.status = 'Pendente' and cc.AgAssinatura = false and (cc.valorccb != null or cc.valorccb != 0))) ";
	
//	private static final String QUERY_REGERAR_PARCELA_NUM_CONTRATO =  	"select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
//			+ "from cobranca.contratocobrancadetalhes cd "
//			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
//			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
//			+ "where cc.status = 'Aprovado' ";
	
	private static final String QUERY_REGERAR_PARCELA_NUM_CONTRATO =  	"select cc.id idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse " 
			+ " from cobranca.contratocobranca cc "
			+ " left join cobranca.contratocobranca_detalhes_join cdj on cc.id = cdj.idcontratocobranca "  
			+ " left join cobranca.contratocobrancadetalhes cd  on cd.id = cdj.idcontratocobrancadetalhes "
			+ "where cc.status = 'Aprovado' ";
	
	
	/*
	private static final String QUERY_ULTIMO_NUMERO_CONTRATO = "select numerocontrato from cobranca.contratocobranca " +
			"order by id desc limit 1"; 
			*/
	private static final String QUERY_ULTIMO_NUMERO_CONTRATO = "select nextval('cobranca.cobranca_seq_contrato')" ;
	
	private static final String QUERY_CONTRATOS_PENDENTES = "select c.id, numeroContrato, dataContrato,  observacaolead, urlLead, quantoPrecisa,"
			+ " pagador, p.nome nomePagador, "
			+ " responsavel, res.nome nomeResponsavel, "
			+ " imovel, i.nome nomeImovel, i.cidade "
			+ " from cobranca.contratocobranca c "
			+ " inner join cobranca.responsavel res on c.responsavel = res.id "
			+ " inner join cobranca.imovelcobranca i on c.imovel = i.id "
			+ " inner join cobranca.pagadorrecebedor p on c.pagador = p.id ";
		
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
			"	and cdbp.datavencimentoatual <= ? ::timestamp ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_DT_ORIG_1 =  	
			"	select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cdbp.vlrrecebido as valor, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.vlrRepasse,  cd.vlrParcela, cdbp.vlrrecebido - cd.vlrParcela as acrescimo, cd.id as idContratoCobrancaDetalhes" +
			"	from cobranca.contratocobrancadetalhes cd   " +
			"	inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes   " +
			"	inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca   " +
			"	left join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes  " +
			"	left join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial  " +
			"	where cc.status = 'Aprovado'   " +
			"	and cdbp.dataVencimento >= ? ::timestamp " +
			"	and cdbp.dataVencimento <= ? ::timestamp ";	
	
	
	public int numeroParcela;
	
	private static final String QUERY_GET_CONTRATOS_POR_INVESTIDOR_INFORME_RENDIMENTOS =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.datacontrato >= ? ::timestamp "
			+ "	and cc.datacontrato <= ? ::timestamp "
			+ " and cc.empresa != 'GALLERIA CORRESPONDENTE BANCARIO EIRELI' " 
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
								" and ((cc.recebedor = " + idInvestidor + " and cc.recebedorenvelope = false) " +
								" or (cc.recebedor2 = " + idInvestidor + " and cc.recebedorenvelope2 = false) " + 
								" or (cc.recebedor3 = " + idInvestidor + " and cc.recebedorenvelope3 = false) " + 
								" or (cc.recebedor4 = " + idInvestidor + " and cc.recebedorenvelope4 = false) " + 
								" or (cc.recebedor5 = " + idInvestidor + " and cc.recebedorenvelope5 = false) " + 
								" or (cc.recebedor6 = " + idInvestidor + " and cc.recebedorenvelope6 = false) " + 
								" or (cc.recebedor7 = " + idInvestidor + " and cc.recebedorenvelope7 = false) " + 
								" or (cc.recebedor8 = " + idInvestidor + " and cc.recebedorenvelope8 = false) " + 
								" or (cc.recebedor9 = " + idInvestidor + " and cc.recebedorenvelope9 = false) " + 
								" or (cc.recebedor10 = " + idInvestidor + " and cc.recebedorenvelope10 = false)) " ;
/*						
						query_QUERY_GET_CONTRATOS_POR_INVESTIDOR = query_QUERY_GET_CONTRATOS_POR_INVESTIDOR +   
								"  and (cc.recebedorenvelope = false and cc.recebedorenvelope2 = false and cc.recebedorenvelope3 = false and "
								+ " cc.recebedorenvelope4 = false and cc.recebedorenvelope5 = false and cc.recebedorenvelope6 = false and "
								+ " cc.recebedorenvelope7 = false and cc.recebedorenvelope8 = false and cc.recebedorenvelope9 = false and cc.recebedorenvelope10 = false) ";
*/
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
	
	private static final String QUERY_RELATORIO_FINANCEIRO_CONTABILIDADE_ =  "select cc.id, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrjurosparcela, cd.vlramortizacaoparcela, cd.id, cd.parcelapaga, cdbp.datapagamento, cdbp.vlrrecebido "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial "			
			+ "where cc.status = 'Aprovado' "
			+ "and cdbp.dataPagamento >= ? ::timestamp "
			+ "and cdbp.dataPagamento < ? ::timestamp "
			+ "and cc.empresa != 'GALLERIA CORRESPONDENTE BANCARIO EIRELI' "
			+ "and cc.pagador not in (15, 34,14, 182, 417, 803) "
			+ "order by cdbp.id ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_CONTABILIDADE =  "select cc.id, cd.id, sum(cdbp.vlrrecebido) "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial "			
			+ "where cc.status = 'Aprovado' "
			+ "and cdbp.dataPagamento >= ? ::timestamp "
			+ "and cdbp.dataPagamento < ? ::timestamp "
			+ "and cc.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' "
			+ "and cc.pagador not in (15, 34,14, 182, 417, 803) "
			+ "group by cc.id, cd.id "
			+ "order by cc.id ";
	
	private static final String QUERY_DATA_ULTIMO_PAGAMENTO =  "select cd.id, cdbp.dataPagamento "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cd.id = cdbpj.idcontratocobrancadetalhes "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdbp on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial "			
			+ "where cd.id = ? "
			+ "and cdbp.dataPagamento >= ? ::timestamp "
			+ "and cdbp.dataPagamento < ? ::timestamp ";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroContabilidade(final Date dtRelInicio, final Date dtRelFim) {
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
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_CONTABILIDADE;
																		
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	

					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					ContratoCobrancaDetalhes contratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
					ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
					
					String parcela = "";
					
					while (rs.next()) {
						contratoCobranca = findById(rs.getLong(1));
						contratoCobrancaDetalhes = contratoCobrancaDetalhesDao.findById(rs.getLong(2));
						
						parcela = contratoCobrancaDetalhes.getNumeroParcela() + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						/*
						// Calcula baixas, e pega ultima data de pagamento
						BigDecimal somaBaixas = BigDecimal.ZERO;
						Date dataUltimoPagamento = null;
				
						for (ContratoCobrancaDetalhesParcial cBaixas : contratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()) {
							dataUltimoPagamento = cBaixas.getDataPagamento();
							//somaBaixas = somaBaixas.add(cBaixas.getVlrRecebido());
						}
						
						// Valida campos
						if (somaBaixas.compareTo(BigDecimal.ZERO) == 0) {
							somaBaixas = null;
						}
						*/
						BigDecimal vlrjurosparcela = null;
						if (contratoCobrancaDetalhes.getVlrJurosParcela() != null) {
							vlrjurosparcela = contratoCobrancaDetalhes.getVlrJurosParcela();
							if (vlrjurosparcela != null) {
								if (vlrjurosparcela.compareTo(BigDecimal.ZERO) == 0) {
									vlrjurosparcela = null;
								}
							}
						}
						
						BigDecimal vlramortizacaoparcela = null;
						if (contratoCobrancaDetalhes.getVlrAmortizacaoParcela() != null) {
							vlramortizacaoparcela = contratoCobrancaDetalhes.getVlrAmortizacaoParcela();
							if (vlramortizacaoparcela != null) {
								if (vlramortizacaoparcela.compareTo(BigDecimal.ZERO) == 0) {
									vlramortizacaoparcela = null;
								}
							}
						}
							
						Connection connectionInterno = null;
						PreparedStatement psInterno = null;
						ResultSet rsInterno = null;
						
						connectionInterno = getConnection();
																			
						psInterno = connectionInterno
								.prepareStatement(QUERY_DATA_ULTIMO_PAGAMENTO);
		
						psInterno.setLong(1, contratoCobrancaDetalhes.getId());
						psInterno.setDate(2, dtRelInicioSQL);
						psInterno.setDate(3, dtRelFimSQL);	

						rsInterno = psInterno.executeQuery();								
							
						Date dataUltimoPagamento = null;
						
						while (rsInterno.next()) {
							if (dataUltimoPagamento == null) {
								dataUltimoPagamento = rsInterno.getDate(2);
							} else {
								if (rsInterno.getDate(2).compareTo(dataUltimoPagamento) > 0) {
									dataUltimoPagamento = rsInterno.getDate(2);
								}
							}
						}

						BigDecimal valorParcela = contratoCobranca.getVlrParcela();
						
						if (valorParcela != null) {
							if (contratoCobrancaDetalhes.isParcelaPaga()) {
								if (rs.getBigDecimal(3).compareTo(valorParcela) > 0) {
									if (!contratoCobranca.isTemSeguro()) {
										if (vlramortizacaoparcela != null) {
											objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
													dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3).subtract(vlramortizacaoparcela), vlramortizacaoparcela));
										} else {
											objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
													dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3), vlramortizacaoparcela));
										}
									} else {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), contratoCobrancaDetalhes.getVlrJurosParcela(), vlramortizacaoparcela));
									}
								} else {
									if (vlramortizacaoparcela != null) {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3).subtract(vlramortizacaoparcela), vlramortizacaoparcela));
									} else {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3), vlramortizacaoparcela));
									}
								}
							} else {
								if (rs.getBigDecimal(3).compareTo(valorParcela) > 0) {
									if (vlramortizacaoparcela != null) {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3).subtract(vlramortizacaoparcela), vlramortizacaoparcela));
									} else {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3), vlramortizacaoparcela));
									}
								} else {
									if (rs.getBigDecimal(3).compareTo(valorParcela) < 0) {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), rs.getBigDecimal(3), BigDecimal.ZERO));
									} else {
										objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getPagador().getNome(), parcela, contratoCobrancaDetalhes.getDataVencimento(), valorParcela,
												dataUltimoPagamento, rs.getBigDecimal(3), vlrjurosparcela, vlramortizacaoparcela));
									}
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
						
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
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
						
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), rs.getBigDecimal(10), rs.getBigDecimal(11)));												
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
						
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), rs.getBigDecimal(10), rs.getBigDecimal(11)));												
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
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroRecebedorAtraso(final Date dtRelInicio, final Date dtRelFim) {
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
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_RECEBEDOR_ATRASO_NOVO;
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
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
						
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome,recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9),rs.getBigDecimal(10), rs.getBigDecimal(11)));												
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
						
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
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
						
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
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
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_CRI1 =  	
			"select cc.numerocontrato, r.nome, pr.nome, cc.valorccb,  cd.numeroparcela || ' de ' || cc.qtdeparcelas, cd.vlrParcela, cdbp.vlrrecebido, cc.numeroContratoSeguro, cd.numeroparcela, cdbp.datavencimento, cdbp.datapagamento " + 
			"from cobranca.contratocobrancadetalhesparcial cdbp  " +
			"inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial " +
			"inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobrancadetalhes = cdbpj.idcontratocobrancadetalhes  " +
			"inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca  " +
			"inner join cobranca.contratocobrancadetalhes cd on cd.id = cdj.idcontratocobrancadetalhes  " +
			"inner join cobranca.responsavel r on cc.responsavel = r.id   " +
			"inner join cobranca.pagadorrecebedor pr on cc.pagador = pr.id  " +
			"where  " +
			"cdbp.dataPagamento >= ? ::timestamp  " +	
			"and cdbp.dataPagamento <= ? ::timestamp  " +
			"and cc.empresa = 'CRI 1' " +
			"order by cc.numerocontrato, cd.numeroparcela ";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoPeriodoTotalCRI1(final Date dtRelInicio, final Date dtRelFim) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_CRI1;

				try {
					connection = getConnection();
						
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					RelatorioFinanceiroCobranca relatorioFinanceiroCobrancaAux = new RelatorioFinanceiroCobranca();
		
					while (rs.next()) { 
						relatorioFinanceiroCobrancaAux = new RelatorioFinanceiroCobranca();
						
						relatorioFinanceiroCobrancaAux.setNumeroContrato(rs.getString(1));
						relatorioFinanceiroCobrancaAux.setNomeResponsavel(rs.getString(2));
						relatorioFinanceiroCobrancaAux.setNomePagador(rs.getString(3));
						relatorioFinanceiroCobrancaAux.setValorCCB(rs.getBigDecimal(4));
						relatorioFinanceiroCobrancaAux.setParcela(rs.getString(5));
						relatorioFinanceiroCobrancaAux.setVlrParcela(rs.getBigDecimal(6));
						relatorioFinanceiroCobrancaAux.setVlrTotalPago(rs.getBigDecimal(7));
						relatorioFinanceiroCobrancaAux.setDataVencimento(rs.getDate(10));
						relatorioFinanceiroCobrancaAux.setDataPagamento(rs.getDate(11));
						
						if (!rs.getString(8).equals("")) {
							relatorioFinanceiroCobrancaAux.setParcelaCCB(rs.getString(8) + "/" + rs.getString(9));
						} else {
							relatorioFinanceiroCobrancaAux.setParcelaCCB("");
						}
												
						objects.add(relatorioFinanceiroCobrancaAux);								
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_FIDC =  	
			"select cc.numerocontrato, r.nome, pr.nome, cc.valorccb,  cd.numeroparcela || ' de ' || cc.qtdeparcelas, cd.vlrParcela, cdbp.vlrrecebido, cc.numeroContratoSeguro, cd.numeroparcela, cdbp.datavencimento, cdbp.datapagamento " + 
			"from cobranca.contratocobrancadetalhesparcial cdbp  " +
			"inner join cobranca.cobranca_detalhes_parcial_join cdbpj on cdbp.id = cdbpj.idcontratocobrancadetalhesparcial " +
			"inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobrancadetalhes = cdbpj.idcontratocobrancadetalhes  " +
			"inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca  " +
			"inner join cobranca.contratocobrancadetalhes cd on cd.id = cdj.idcontratocobrancadetalhes  " +
			"inner join cobranca.responsavel r on cc.responsavel = r.id   " +
			"inner join cobranca.pagadorrecebedor pr on cc.pagador = pr.id  " +
			"where  " +
			"cdbp.dataPagamento >= ? ::timestamp  " +	
			"and cdbp.dataPagamento <= ? ::timestamp  " +
			"and cc.empresa = 'FIDC GALLERIA' " +
			"order by cc.numerocontrato, cd.numeroparcela ";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioFinanceiroBaixadoPeriodoTotalFIDC(final Date dtRelInicio, final Date dtRelFim) {
		return (List<RelatorioFinanceiroCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<RelatorioFinanceiroCobranca> objects = new ArrayList<RelatorioFinanceiroCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_BAIXADO_PERIODO_TOTAL_FIDC;

				try {
					connection = getConnection();
						
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);						
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);								
	
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					RelatorioFinanceiroCobranca relatorioFinanceiroCobrancaAux = new RelatorioFinanceiroCobranca();
		
					while (rs.next()) { 
						relatorioFinanceiroCobrancaAux = new RelatorioFinanceiroCobranca();
						
						relatorioFinanceiroCobrancaAux.setNumeroContrato(rs.getString(1));
						relatorioFinanceiroCobrancaAux.setNomeResponsavel(rs.getString(2));
						relatorioFinanceiroCobrancaAux.setNomePagador(rs.getString(3));
						relatorioFinanceiroCobrancaAux.setValorCCB(rs.getBigDecimal(4));
						relatorioFinanceiroCobrancaAux.setParcela(rs.getString(5));
						relatorioFinanceiroCobrancaAux.setVlrParcela(rs.getBigDecimal(6));
						relatorioFinanceiroCobrancaAux.setVlrTotalPago(rs.getBigDecimal(7));
						relatorioFinanceiroCobrancaAux.setDataVencimento(rs.getDate(10));
						relatorioFinanceiroCobrancaAux.setDataPagamento(rs.getDate(11));
						
						if (!rs.getString(8).equals("")) {
							relatorioFinanceiroCobrancaAux.setParcelaCCB(rs.getString(8) + "/" + rs.getString(9));
						} else {
							relatorioFinanceiroCobrancaAux.setParcelaCCB("");
						}
												
						objects.add(relatorioFinanceiroCobrancaAux);								
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
			final String filtrarDataVencimento, String statusParcela) {
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
					
					if (statusParcela.equals("Baixa Total")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cd.parcelaPaga = true ";						
					}
					
					if (statusParcela.equals("Baixa Parcial")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cd.parcelaPaga = false "; 
					}
					
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " order by cdj.idcontratocobranca, idContratoCobrancaDetalhes, cd.numeroParcela " ;
					
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
								
								if (contratoCobranca.getRecebedor() != null && contratoCobranca.getVlrParcela() != null) {
									relatorioFinanceiroCobrancaTmp = new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
											contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), contratoCobranca.getVlrParcela(), rs.getBigDecimal(4).subtract(contratoCobranca.getVlrParcela()), parcelaPagaStr);
								} else {
									if (contratoCobranca.getVlrParcela() != null) {
										relatorioFinanceiroCobrancaTmp = new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
												contratoCobranca.getPagador().getNome(), "", parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), contratoCobranca.getVlrParcela(), rs.getBigDecimal(4).subtract(contratoCobranca.getVlrParcela()), parcelaPagaStr);
									} else if (contratoCobranca.getRecebedor() != null)  {
										relatorioFinanceiroCobrancaTmp = new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
												contratoCobranca.getPagador().getNome(), contratoCobranca.getRecebedor().getNome(), parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), BigDecimal.ZERO, rs.getBigDecimal(4), parcelaPagaStr);
									} else {
										relatorioFinanceiroCobrancaTmp = new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), contratoCobranca.getResponsavel().getNome(),
												contratoCobranca.getPagador().getNome(), "", parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getBigDecimal(9), BigDecimal.ZERO, rs.getBigDecimal(4), parcelaPagaStr);
									}
									
								}
								if (rs.isLast()) {
									if (relatorioFinanceiroCobrancaTmp.getAcrescimo().compareTo(BigDecimal.ZERO) == -1) {
										relatorioFinanceiroCobrancaTmp.setAcrescimo(BigDecimal.ZERO);
									}
									objects.add(relatorioFinanceiroCobrancaTmp);								
								}
							} else {
								relatorioFinanceiroCobrancaTmp.setValor(relatorioFinanceiroCobrancaTmp.getValor().add(rs.getBigDecimal(4)));
								
								if (contratoCobranca.getVlrParcela() != null) {
									relatorioFinanceiroCobrancaTmp.setAcrescimo(relatorioFinanceiroCobrancaTmp.getValor().subtract(contratoCobranca.getVlrParcela()));
								} else {
									relatorioFinanceiroCobrancaTmp.setAcrescimo(relatorioFinanceiroCobrancaTmp.getValor());
								}
								
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
			+ "and cc.pagador not in (15, 34,14, 182, 417, 803) ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosUltimos10(String empresa) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS_ULTIMOS_10;	
				try {					
					if (empresa.equals("Securitizadora")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' ";
					} else if (empresa.equals("FIDC")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'FIDC GALLERIA' ";
					} else if (empresa.equals("CRI 1")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'CRI 1' ";
					}
					
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
					+ " order by cc.datacontrato desc "
					+ "limit 10 ";
					
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
	
	private static final String QUERY_CONSULTA_CONTRATOS_BRL_CESSAO =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.numerocontrato = ? ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosBRLCessao(String numeroContrato) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS_BRL_CESSAO;	
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
	
	private static final String QUERY_CONSULTA_BRL_CONTRATO_MIGRACAO =  	"select cc.id "
			+ " from cobranca.contratocobranca cc "
			+ " where cc.cedenteBRLCessao = ? " 
			+ " and cc.empresa = 'FIDC GALLERIA' ";

	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosBRLLiquidacaoMigracao(final String cedenteCessao) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;				
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_BRL_CONTRATO_MIGRACAO;

					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);			

					ps.setString(1, cedenteCessao);
				
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();				
					
					while (rs.next()) {
						contratoCobranca = new ContratoCobranca();
						
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
	
	private static final String QUERY_CONSULTA_BRL_CONTRATO =  	"select cc.id, cd.numeroParcela, cd.vlrJurosParcela, cd.vlrAmortizacaoParcela, cdp.dataVencimento , cdp.dataPagamento , cdp.vlrParcela , cdp.vlrRecebido, cd.id, cd.valorJurosSemIPCA, cd.valorAmortizacaoSemIPCA  "
			+ " from cobranca.contratocobrancadetalhes cd "
			+ " inner join cobranca.cobranca_detalhes_parcial_join cdpj on cdpj.idcontratocobrancadetalhes = cd.id "
			+ " inner join cobranca.contratocobrancadetalhesparcial cdp on cdp.id = cdpj.idcontratocobrancadetalhesparcial " 
			+ " inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes "
			+ " inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca  "
			+ " where cdp.dataPagamento >= ? ::timestamp "
			+ " and cdp.dataPagamento <= ? ::timestamp "
			+ " and cc.cedenteBRLCessao = ? ";	

	@SuppressWarnings("unchecked")
	public List<ContratoCobrancaBRLLiquidacao> consultaContratosBRLLiquidacao(final Date dataBaixaInicial, final Date dataBaixaFinal, final String cedenteCessao) {
		return (List<ContratoCobrancaBRLLiquidacao>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobrancaBRLLiquidacao> objects = new ArrayList<ContratoCobrancaBRLLiquidacao>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;				
				try {
					connection = getConnection();
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_BRL_CONTRATO;
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataBaixaInicial.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataBaixaFinal.getTime());

					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);			
	
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					ps.setString(3, cedenteCessao);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					ContratoCobrancaBRLLiquidacao contratoCobrancaBRLLiquidacao = new ContratoCobrancaBRLLiquidacao();					
					
					while (rs.next()) {
						contratoCobrancaBRLLiquidacao = new ContratoCobrancaBRLLiquidacao();
						
						contratoCobranca = findById(rs.getLong(1));
						
						contratoCobrancaBRLLiquidacao.setContrato(contratoCobranca);
						
						//contratoCobrancaBRLLiquidacao.setNumeroParcela(rs.getString(2));
						
						String parcela = rs.getString(2);
						if (parcela.length() == 1) {
							parcela = "00" + parcela;
						} else if (parcela.length() == 2) {
							parcela = "0" + parcela;
						} else {
							parcela = parcela;
						}
						contratoCobrancaBRLLiquidacao.setNumeroParcela(parcela);
						contratoCobrancaBRLLiquidacao.setVlrJurosParcela(rs.getBigDecimal(3));
						contratoCobrancaBRLLiquidacao.setVlrAmortizacaoParcela(rs.getBigDecimal(4));
						contratoCobrancaBRLLiquidacao.setDataVencimento(rs.getDate(5));
						contratoCobrancaBRLLiquidacao.setDataPagamento(rs.getDate(6));
						contratoCobrancaBRLLiquidacao.setVlrParcela(rs.getBigDecimal(7));
						contratoCobrancaBRLLiquidacao.setVlrRecebido(rs.getBigDecimal(8));
						contratoCobrancaBRLLiquidacao.setId(rs.getLong(9));
						contratoCobrancaBRLLiquidacao.setVlrJurosSemIPCA(rs.getBigDecimal(10));
						contratoCobrancaBRLLiquidacao.setVlrAmortizacaoSemIPCA(rs.getBigDecimal(11));

						objects.add(contratoCobrancaBRLLiquidacao);
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	/*
	 * private static final String QUERY_CONSULTA_CONTRATOS = "select cc.id " +
	 * "from cobranca.contratocobranca cc " + "where cc.status = 'Aprovado' " +
	 * "and cc.pagador not in (15, 34,14, 182, 417, 803) ";
	 * 
	 * @SuppressWarnings("unchecked") public List<ContratoCobranca>
	 * consultaContratos(String empresa) { return (List<ContratoCobranca>)
	 * executeDBOperation(new DBRunnable() {
	 * 
	 * @Override public Object run() throws Exception { List<ContratoCobranca>
	 * objects = new ArrayList<ContratoCobranca>();
	 * 
	 * Connection connection = null; PreparedStatement ps = null; ResultSet rs =
	 * null; String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS;
	 * try {
	 * 
	 * if (empresa.equals("Todas")) { }
	 * 
	 * if (empresa.equals("Securitizadora")) { query_RELATORIO_FINANCEIRO_CUSTOM =
	 * query_RELATORIO_FINANCEIRO_CUSTOM +
	 * " and cc.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' "; }
	 * 
	 * if (empresa.equals("FIDC")) { query_RELATORIO_FINANCEIRO_CUSTOM =
	 * query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.empresa = 'FIDC GALLERIA' "; }
	 * 
	 * query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM +
	 * " order by cc.datacontrato desc ";
	 * 
	 * connection = getConnection();
	 * 
	 * ps = connection .prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
	 * 
	 * rs = ps.executeQuery();
	 * 
	 * ContratoCobranca contratoCobranca = new ContratoCobranca();
	 * 
	 * while (rs.next()) { contratoCobranca = findById(rs.getLong(1));
	 * 
	 * objects.add(contratoCobranca); }
	 * 
	 * } finally { closeResources(connection, ps, rs); } return objects; } }); }
	 */
	private static final String QUERY_CONSULTA_CONTRATOS =  	"select cc.id "
			+ "from cobranca.contratocobranca cc "
			+ "where cc.status = 'Aprovado' "
			+ "and cc.pagador not in (15, 34,14, 182, 417, 803) ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratos(String empresa) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_CONTRATOS;	
				try {
					
					if (empresa.equals("Todas")) {
					}
					
					if (empresa.equals("Securitizadora")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' ";
					}
				
					if (empresa.equals("FIDC")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'FIDC GALLERIA' ";
					}
					
					if (empresa.equals("CRI 1")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'CRI 1' ";
					}
					
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
					+ " order by cc.datacontrato desc ";
					
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
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosPerformance(final String tipoParametroConsultaContrato, final String parametroConsultaContrato, String empresa) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_CONSULTAR_CONTRATO_PERFORMANCE;	
				
				try {
					connection = getConnection();
					
					
					
					
					/**
					if (numeroContrato != null && !numeroContrato.equals("")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.numerocontrato = ?";
					}
					
					if (idPagador > 0) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " and cc.pagador = ?";
					}
					
					
		            <f:selectItem itemLabel="Contrato" itemValue="numeroContrato" />
		            <f:selectItem itemLabel="CPF Pagador" itemValue="CPF" />
		            <f:selectItem itemLabel="CNPJ Pagador" itemValue="CNPJ" />
		            <f:selectItem itemLabel="CCB" itemValue="numeroCCB" />
		            
		            */	
										
		            if (tipoParametroConsultaContrato.equals("cpfPagador") || tipoParametroConsultaContrato.equals("cnpjPagador") || tipoParametroConsultaContrato.equals("nomePagador")) {
		            	query_CONSULTAR_CONTRATO_PERFORMANCE =   "select cc.id, p.id " +
								  "from cobranca.contratocobranca cc " + 
								  "inner join cobranca.pagadorrecebedor p on p.id = cc.pagador " +
								  "where cc.status = 'Aprovado' ";
		            	
		            	if (tipoParametroConsultaContrato.equals("nomePagador")) {
		            		query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE + " and unaccent(p.nome) ilike unaccent('%" + parametroConsultaContrato + "%')";
		            	} 
		            	
		            	if (tipoParametroConsultaContrato.equals("cpfPagador")) {
		            		query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE + " and p.cpf = '" + parametroConsultaContrato + "'";
		            	} 
		            	
		            	if (tipoParametroConsultaContrato.equals("cnpjPagador")) {
		            		query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE + " and p.cnpj = '" + parametroConsultaContrato + "'";
		            	}
		            } else {
		            	query_CONSULTAR_CONTRATO_PERFORMANCE =   "select cc.id " +
								  "from cobranca.contratocobranca cc " + 
								  "where cc.status = 'Aprovado' ";
		            	
		            	if (tipoParametroConsultaContrato.equals("numeroContrato")) {
		            		query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE + " and cc.numerocontrato = '" + parametroConsultaContrato + "'";
		            	}
		            	
		            	if (tipoParametroConsultaContrato.equals("numeroCCB")) {
		            		query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE + " and cc.numeroContratoSeguro = '" + parametroConsultaContrato + "'";
		            	}
		            }

					if (empresa.equals("Todas")) {
					}
					
					if (empresa.equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
						query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE 
								+  " and cc.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' ";
					}
				
					if (empresa.equals("FIDC GALLERIA")) {
						query_CONSULTAR_CONTRATO_PERFORMANCE = query_CONSULTAR_CONTRATO_PERFORMANCE 
								+  " and cc.empresa = 'FIDC GALLERIA' ";
					}
					
					ps = connection
							.prepareStatement(query_CONSULTAR_CONTRATO_PERFORMANCE);

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
			final long idRecebedor8, final long idRecebedor9, final long idRecebedor10, String empresa) {
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
					
					if (empresa.equals("Todas")) {
					}
					
					if (empresa.equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.' ";
					}
				
					if (empresa.equals("FIDC GALLERIA")) {
						query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM 
								+  " and cc.empresa = 'FIDC GALLERIA' ";
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
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DIA_FIDC = "select c.id from cobranca.contratocobranca c "
			+ "where c.status = 'Aprovado' "
			+ "and c.pagador not in (15, 34,14, 182, 417, 803) "
			+ "and c.empresa = 'FIDC GALLERIA'"
			+ " order by numerocontrato";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DIA_PRE_APROVADO = " select c.id from cobranca.contratocobranca c "
			+ "	where (c.status = 'Pendente' and c.AgAssinatura = false and c.agregistro = true and (c.valorccb != null or c.valorccb != 0)) "
			+ "	and c.pagador not in (15, 34,14, 182, 417, 803) "
			+ "	order by numerocontrato ";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DIA_SECURITIZADORA = "select c.id from cobranca.contratocobranca c "
			+ "where c.status = 'Aprovado' "
			+ "and c.pagador not in (15, 34,14, 182, 417, 803) "
			+ "and c.empresa = 'GALLERIA FINANÇAS SECURITIZADORA S.A.'"
			+ " order by numerocontrato";
	
	private static final String QUERY_RELATORIO_FINANCEIRO_DIA_CRI_1 = " select c.id from cobranca.contratocobranca c "
			+ "	where c.status = 'Aprovado' "
			+ "	and c.pagador not in (15, 34,14, 182, 417, 803) "
			+ " and c.empresa = 'CRI 1' "
			+ "	order by numerocontrato ";

	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> relatorioFinanceiroDia(String tipoContratoCobrancaFinanceiroDia) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				String query_RELATORIO_FINANCEIRO_CUSTOM = "";
				
				if (tipoContratoCobrancaFinanceiroDia.equals("Securitizadora")) {
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DIA_SECURITIZADORA;	
				}
				
				if (tipoContratoCobrancaFinanceiroDia.equals("FIDC")) {
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DIA_FIDC;		
				}
				
				if (tipoContratoCobrancaFinanceiroDia.equals("PreAprovado")) {
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DIA_PRE_APROVADO;		
				}
				
				if (tipoContratoCobrancaFinanceiroDia.equals("CRI 1")) {
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_DIA_CRI_1;		
				}
				
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
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA_FULL_CRI1 = "select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse, "
			+ " cc.numeroContratoSeguro "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.parcelapaga = false "
			+ "and cc.pagador not in (15, 34, 14, 182, 417, 803) "
			+ "and cc.empresa = 'CRI 1' "
			+ "and cd.datavencimentoatual < ? ::timestamp "
			+ " order by cd.dataVencimentoatual desc ";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoqueAtrasoFullCRI1(final Date dtRelFim) {
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
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA_FULL_CRI1;
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
					
					ps.setDate(1, dtRelFimSQL);	
					
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					
					boolean exiteParcela = false;
					
					while (rs.next()) {
						exiteParcela = false;
						
						contratoCobranca = findById(rs.getLong(1));
						
						String parcela = rs.getString(2);
						if (parcela.length() == 1) {
							parcela = "00" + parcela;
						} else if (parcela.length() == 2) {
							parcela = "0" + parcela;
						} else {
							parcela = parcela;
						}
					
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
							
							
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10), rs.getString(11)));																	
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA_FULL_FIDC = "select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse, "
			+ " cc.numeroContratoSeguro "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.parcelapaga = false "
			+ "and cc.pagador not in (15, 34, 14, 182, 417, 803) "
			+ "and cc.empresa = 'FIDC GALLERIA' "
			+ "and cd.datavencimentoatual < ? ::timestamp "
			+ " order by cd.dataVencimentoatual desc ";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoqueAtrasoFullFIDC(final Date dtRelFim) {
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
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA_FULL_FIDC;
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
					
					ps.setDate(1, dtRelFimSQL);	
					
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					
					boolean exiteParcela = false;
					
					while (rs.next()) {
						exiteParcela = false;
						
						contratoCobranca = findById(rs.getLong(1));
						
						String parcela = rs.getString(2);
						if (parcela.length() == 1) {
							parcela = "00" + parcela;
						} else if (parcela.length() == 2) {
							parcela = "0" + parcela;
						} else {
							parcela = parcela;
						}
					
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
							
							
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10), rs.getString(11)));																	
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA_FULL = "select cdj.idcontratocobranca, cd.numeroParcela, cd.dataVencimento, cd.vlrParcela, cd.vlrRetencao, cd.vlrComissao, cd.parcelaPaga, cd.dataVencimentoatual, cd.id, cd.vlrRepasse "
			+ "from cobranca.contratocobrancadetalhes cd "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes " 
			+ "inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " 
			+ "where cc.status = 'Aprovado' "
			+ "and cd.parcelapaga = false "
			+ "and cc.pagador not in (15, 34, 14, 182, 417, 803) "
			+ "and cc.empresa != 'GALLERIA CORRESPONDENTE BANCARIO EIRELI' "
			+ "and cd.datavencimentoatual >= ? ::timestamp "
			+ "and cd.datavencimentoatual <= ? ::timestamp "
			+ " order by cd.dataVencimentoatual desc ";
	
	@SuppressWarnings("unchecked")
	public List<RelatorioFinanceiroCobranca> relatorioControleEstoqueAtrasoFull(final Date dtRelInicio, final Date dtRelFim) {
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
					
					query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_RELATORIO_FINANCEIRO_ATRASO_DT_ATUALIZADA_FULL;
					
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dtRelInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dtRelFim.getTime());
					
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);	
					
	
					rs = ps.executeQuery();
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					String parcela = "";
					
					boolean exiteParcela = false;
					
					while (rs.next()) {
						exiteParcela = false;
						
						contratoCobranca = findById(rs.getLong(1));
												
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						// verifica se o contrato já está na lista (uma única linha por contrato
						for (RelatorioFinanceiroCobranca parcelaLista : objects) {
							if (parcelaLista.getNumeroContrato().equals(contratoCobranca.getNumeroContrato())) {
								exiteParcela = true;
								break;
							}
						}
						
						if (!exiteParcela) {
							String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
							String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
							String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
							
							
							objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
									pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));
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
												
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
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
							
							parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
							
							if (!ct.isParcelaPaga()) {
								String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
								String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
								String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
								
								
								objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
										pagadorNome, recebedorNome, parcela, ct.getDataVencimento(), ct.getVlrParcela(), contratoCobranca, ct.getVlrRetencao(), ct.getVlrComissao(), ct.isParcelaPaga(), ct.getDataVencimentoAtual(), ct.getId(), ct.getVlrRepasse()));
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
												
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(),  responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
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
												
						parcela = rs.getString(2) + " de " + contarTotalParcelas(contratoCobranca.getListContratoCobrancaDetalhes());
						
						String responsavelNome = (contratoCobranca.getResponsavel()==null)?"": contratoCobranca.getResponsavel().getNome();
						String pagadorNome = (contratoCobranca.getPagador()==null)?"":contratoCobranca.getPagador().getNome();
						String recebedorNome = (contratoCobranca.getRecebedor()==null)?"":contratoCobranca.getRecebedor().getNome();
						
						
						objects.add(new RelatorioFinanceiroCobranca(contratoCobranca.getNumeroContrato(), contratoCobranca.getDataContrato(), responsavelNome,
								pagadorNome, recebedorNome, parcela, rs.getDate(3), rs.getBigDecimal(4), contratoCobranca, rs.getBigDecimal(5), rs.getBigDecimal(6), rs.getBoolean(7), rs.getDate(8), rs.getLong(9), rs.getBigDecimal(10)));												
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
	
	private static final String QUERY_CONTRATOS_PENDENTES_REPROVADOS = " select c.id, c.numerocontrato, c.datacontrato, pare.nome, res.nome, c.motivoReprovacaoAnalise, c.motivoReprovaLead, c.quantoprecisa, imv.numeroMatricula from cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel res on c.responsavel = res.id "
			+ "	inner join cobranca.pagadorrecebedor pare on c.pagador = pare.id "
			+ "	inner join cobranca.imovelcobranca imv on c.imovel = imv.id "
			+ "	where (status = 'Reprovado' or status = 'Desistência Cliente' ) " ;
	
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

					String query = QUERY_CONTRATOS_PENDENTES_REPROVADOS;

					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}				
					} 
					
					query = query + " order by id desc ";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong(1));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setMotivoReprovacaoAnalise(rs.getString("motivoReprovacaoAnalise"));
						contratoCobranca.setMotivoReprovaLead(rs.getString("motivoReprovaLead"));
						contratoCobranca.setDataContrato(rs.getDate("datacontrato"));
						contratoCobranca.getPagador().setNome(rs.getString(4));
						contratoCobranca.getResponsavel().setNome(rs.getString(5));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contratoCobranca.getImovel().setNumeroMatricula(rs.getString("numeromatricula"));
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
	public Collection<ContratoCobranca> consultaContratosPendentesReprovadosResponsaveis(final String codResponsavel, final List<Responsavel> listResponsavel ) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES_REPROVADOS;
					
					
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
					} 
					
					query = query + " order by id desc ";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong(1));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setMotivoReprovacaoAnalise(rs.getString("motivoReprovacaoAnalise"));
						contratoCobranca.setMotivoReprovaLead(rs.getString("motivoReprovaLead"));
						contratoCobranca.setDataContrato(rs.getDate("datacontrato"));
						contratoCobranca.getPagador().setNome(rs.getString(4));
						contratoCobranca.getResponsavel().setNome(rs.getString(5));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contratoCobranca.getImovel().setNumeroMatricula(rs.getString("numeromatricula"));
						objects.add(contratoCobranca);
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_CONTRATOS_PENDENTES_BAIXADOS = " select c.id, c.numerocontrato, c.datacontrato, pare.nome, res.nome, c.quantoprecisa, imv.cidade from cobranca.contratocobranca c "
			+ "	inner join cobranca.responsavel res on c.responsavel = res.id "
			+ "	inner join cobranca.pagadorrecebedor pare on c.pagador = pare.id "
			+ "	inner join cobranca.imovelcobranca imv on c.imovel = imv.id "
			+ "	where status = 'Baixado' " ;
		
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosPendentesBaixados(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES_BAIXADOS;
					
					///
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " and res.codigo = '" + codResponsavel + "' ";
						}				
					} 
					////////
				
					query = query + " order by id desc ";

					ps = connection.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong(1));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setDataContrato(rs.getDate("datacontrato"));
						contratoCobranca.getPagador().setNome(rs.getString(4));
						contratoCobranca.getResponsavel().setNome(rs.getString(5));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contratoCobranca.getImovel().setCidade(rs.getString("cidade"));
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
	public Collection<ContratoCobranca> consultaContratosPendentesBaixadosResponsaveis(final String codResponsavel, final List<Responsavel> listResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_PENDENTES_BAIXADOS;
					
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
					} 
					///
					
					////////
				
					query = query + " order by id desc ";
					
					ps = connection.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong(1));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setDataContrato(rs.getDate("datacontrato"));
						contratoCobranca.getPagador().setNome(rs.getString(4));
						contratoCobranca.getResponsavel().setNome(rs.getString(5));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoprecisa"));
						contratoCobranca.getImovel().setCidade(rs.getString("cidade"));
						objects.add(contratoCobranca);											
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_CONSULTA_CONTRATOS_REPROVADOS =  	"select cc.id "
			+ " from cobranca.contratocobranca cc "
			+ " where (status = 'Reprovado' or status = 'Desistência Cliente' ) "
			+ " and cc.numerocontrato = ? ";
	
	@SuppressWarnings("unchecked")
	public Collection<ContratoCobranca> consultaContratosReprovados(final String numeroContrato) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONSULTA_CONTRATOS_REPROVADOS;
					
										
					
					ps = connection
							.prepareStatement(query);
					
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
	
	
	private static final String QUERY_CONSULTA_TOTAL_PRE_CONTRATOS_BAIXADOS =  	"select cc.id "
			+ " from cobranca.contratocobranca cc "
			+ " where cc.status = 'Baixado' ";
	
	private static final String QUERY_CONSULTA_PRE_CONTRATOS_BAIXADOS =  	"select cc.id "
			+ " from cobranca.contratocobranca cc "
			+ " where cc.status = 'Baixado' "
			+ " and cc.numerocontrato = ? ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaPreContratosBaixados(final String numeroContrato) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = QUERY_CONSULTA_PRE_CONTRATOS_BAIXADOS;	
				try {
					connection = getConnection();
					ps = connection.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
				
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
	public Collection<ContratoCobranca> consultaLeadsTerceiros(final String codResponsavel) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {		
					String query = QUERY_CONTRATOS_PENDENTES;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Baixado' and status != 'Desistência Cliente'" ;
						
					// verifica as cláusulas dos repsonsáveis
					String queryResponsavel = "";
					if (codResponsavel != null) {
						if (queryResponsavel.equals("")) {
							queryResponsavel = " and (res.codigo = '" + codResponsavel + "' ";
						}				
						
						if (!queryResponsavel.equals("")) {
							query = query + queryResponsavel;
							
							query = query + ")";
						}
					} else {
						query = query + " and (res.codigo = '11610')";
					}
						
					query = query + " and (c.statusLead = 'Novo Lead' or c.statusLead = 'Reprovado') and c.statusLead != 'Completo' ";
						
					query = query + " order by id desc";
					
					connection = getConnection();
					
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
	public Collection<ContratoCobranca> consultaLeads(final String codResponsavel, final List<Responsavel> listResponsavel, final String status) {
		return (Collection<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {		
					String query = QUERY_CONTRATOS_PENDENTES;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Baixado' and status != 'Desistência Cliente'" ;
					
					if (status != null || !status.equals("")) {			
						if (status.equals("Novo Lead")) {
							query = query + " and c.responsavel = 46 ";
						} else {
							// verifica as cláusulas dos repsonsáveis
							String queryResponsavel = "";
							if (codResponsavel != null || listResponsavel != null) {
								if (queryResponsavel.equals("")) {
									queryResponsavel = " and (res.codigo = '" + codResponsavel + "' ";
								}
								
								String queryGuardaChuva = "";
								if (listResponsavel.size() > 0) {							
									for (Responsavel resp : listResponsavel) {
										if (!resp.getCodigo().equals("")) { 
											if (queryGuardaChuva.equals("")) {
												queryGuardaChuva = " res.codigo = '" + resp.getCodigo() + "' ";
											} else {
												queryGuardaChuva = queryGuardaChuva + " or res.codigo = '" + resp.getCodigo() + "' ";
											}		
											
											// busca recursiva no guarda-chuva
											ResponsavelDao rDao = new ResponsavelDao();
											List<String> retornoGuardaChuvaRecursivo = new ArrayList<String>();
											retornoGuardaChuvaRecursivo = rDao.getGuardaChuvaRecursivoPorResponsavel(resp.getCodigo());									
											for (String codigoResponsavel : retornoGuardaChuvaRecursivo) {
												queryGuardaChuva = queryGuardaChuva + " or res.codigo = '" + codigoResponsavel + "' ";
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
						}
						query = query + " and c.statusLead = '" + status + "'";
					}
					
					query = query + " order by id desc";
					
					connection = getConnection();
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					
					while (rs.next()) {
						ContratoCobranca contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong("id"));
						contratoCobranca.setNumeroContrato(rs.getString("numeroContrato"));
						contratoCobranca.setDataContrato(rs.getDate("dataContrato"));
						contratoCobranca.setObservacaolead(rs.getString("observacaolead"));
						contratoCobranca.setUrlLead(rs.getString("urllead"));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
						PagadorRecebedor pagador = new PagadorRecebedor();
						pagador.setId(rs.getLong("pagador"));
						pagador.setNome(rs.getString("nomePagador"));				
						Responsavel responsavel = new Responsavel();
						responsavel.setId(rs.getLong("responsavel"));
						responsavel.setNome(rs.getString("nomeResponsavel"));		
						ImovelCobranca imovel = new ImovelCobranca();
						imovel.setId(rs.getLong("imovel"));
						imovel.setCidade(rs.getString("cidade"));
						imovel.setNome(rs.getString("nomeImovel"));
						contratoCobranca.setPagador(pagador);
						contratoCobranca.setResponsavel(responsavel);
						contratoCobranca.setImovel(imovel);
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	private static final String QUERY_CONTRATOS_JSON_CESSAO = " select c.id from cobranca.contratocobranca c ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosJSONCessao() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_JSON_CESSAO;
					
					query = query + " where cedenteBRLCessao != '' ";
					
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

					String query = QUERY_CONTRATOS_PENDENTES_CONSULTA;
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) { 
							query = query + " where res.codigo = '" + codResponsavel + "' ";
						}				
					} 
					
					query = query + "  group by coco.id, numeroContrato, datacontrato, quantoPrecisa, res.nome, pare.nome, gerente.nome, "
							+ "	statuslead, inicioAnalise, cadastroAprovadoValor, matriculaAprovadaValor, pagtoLaudoConfirmada, "
							+ "	laudoRecebido, pajurFavoravel,  documentosCompletos, ccbPronta, agAssinatura, "
							+ "	agRegistro, preAprovadoComite, documentosComite, aprovadoComite, analiseReprovada, analiseComercial, comentarioJuridicoEsteira,"
							+ " status, pedidoLaudo, pedidoLaudoPajuComercial, pedidoPreLaudo, pedidoPreLaudoComercial, pedidoPajuComercial, pendenciaLaudoPaju, avaliacaoLaudo ";
						
					query = query + " order by id desc";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						
						contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong("id"));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setDataContrato(rs.getDate("datacontrato"));
						contratoCobranca.getResponsavel().setNome(rs.getString(5));
						contratoCobranca.getResponsavel().setDonoResponsavel(new Responsavel());
						contratoCobranca.getResponsavel().getDonoResponsavel().setNome(rs.getString(7));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
						contratoCobranca.setStatusLead(rs.getString("statuslead"));
						contratoCobranca.getPagador().setNome(rs.getString(6));
						contratoCobranca.setInicioAnalise(rs.getBoolean("inicioAnalise"));
						contratoCobranca.setCadastroAprovadoValor(rs.getString("cadastroAprovadoValor"));
						contratoCobranca.setMatriculaAprovadaValor(rs.getString("matriculaAprovadaValor"));
						contratoCobranca.setPagtoLaudoConfirmada(rs.getBoolean("pagtolaudoconfirmada"));
						contratoCobranca.setLaudoRecebido(rs.getBoolean("laudoRecebido"));
						contratoCobranca.setPajurFavoravel(rs.getBoolean("pajurFavoravel"));
						contratoCobranca.setDocumentosCompletos(rs.getBoolean("documentosCompletos"));
						contratoCobranca.setCcbPronta(rs.getBoolean("ccbpronta"));
						contratoCobranca.setAgAssinatura(rs.getBoolean("agassinatura"));
						contratoCobranca.setAgRegistro(rs.getBoolean("agregistro"));
						contratoCobranca.setPreAprovadoComite(rs.getBoolean("preAprovadoComite"));
						contratoCobranca.setDocumentosComite(rs.getBoolean("documentosComite"));
						contratoCobranca.setAprovadoComite(rs.getBoolean("AprovadoComite"));
						contratoCobranca.setAnaliseReprovada(rs.getBoolean("analisereprovada"));
						contratoCobranca.setComentarioJuridicoEsteira(rs.getBoolean("comentarioJuridicoEsteira"));
						contratoCobranca.setAnaliseComercial(rs.getBoolean("analiseComercial"));
						
						contratoCobranca.setPedidoLaudo(rs.getBoolean("pedidoLaudo"));
						contratoCobranca.setPedidoLaudoPajuComercial(rs.getBoolean("pedidoLaudoPajuComercial"));
						contratoCobranca.setPedidoPreLaudo(rs.getBoolean("pedidoPreLaudo"));
						contratoCobranca.setPedidoPreLaudoComercial(rs.getBoolean("pedidoPreLaudoComercial"));
						contratoCobranca.setPedidoPajuComercial(rs.getBoolean("pedidoPajuComercial"));
						contratoCobranca.setPendenciaLaudoPaju(rs.getBoolean("pendenciaLaudoPaju"));
						contratoCobranca.setAvaliacaoLaudo(rs.getString("avaliacaoLaudo"));
						
						contratoCobranca.setStatus(rs.getString("status"));
						//contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}
	
	
	private static final String QUERY_CONTRATOS_PENDENTES_CONSULTA = " select coco.id, coco.numeroContrato, coco.datacontrato, coco.quantoPrecisa, res.nome,  "
			+ "	pare.nome, gerente.nome, "
			+ "	coco.statuslead, coco.inicioAnalise, coco.cadastroAprovadoValor, coco.matriculaAprovadaValor, coco.pagtoLaudoConfirmada, "
			+ "	coco.laudoRecebido, coco.pajurFavoravel,  coco.documentosCompletos, coco.ccbPronta, coco.agAssinatura, "
			+ "	coco.agRegistro, coco.preAprovadoComite, coco.documentosComite, coco.aprovadoComite, coco.analiseReprovada, coco.analiseComercial, coco.comentarioJuridicoEsteira, coco.status,"
			+ " pedidoLaudo, pedidoLaudoPajuComercial, pedidoPreLaudo, pedidoPreLaudoComercial, pedidoPajuComercial, pendenciaLaudoPaju, avaliacaoLaudo   "
			+ "	from cobranca.contratocobranca coco "
			+ "	inner join cobranca.responsavel res on coco.responsavel = res.id "
			+ "	inner join cobranca.pagadorrecebedor pare on pare.id = coco.pagador "
			+ "	left join cobranca.responsavel gerente on res.donoresponsavel = gerente.id ";
	
	
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

					String query = QUERY_CONTRATOS_PENDENTES_CONSULTA;
					
					String queryResponsavel = " res.codigo = '" + codResponsavel + "' ";
					
					if (listResponsavel.size() > 0) {
						for (Responsavel resp : listResponsavel) {
							if (!resp.getCodigo().equals("")) { 
								queryResponsavel = queryResponsavel + " or res.codigo = '" + resp.getCodigo() + "' ";
							}
						}				
					} 
				
					query = query + " where  " + queryResponsavel + " ";
					
					query = query + "  group by coco.id, numeroContrato, datacontrato, quantoPrecisa, res.nome, pare.nome, gerente.nome, "
							+ "	statuslead, inicioAnalise, cadastroAprovadoValor, matriculaAprovadaValor, pagtoLaudoConfirmada, "
							+ "	laudoRecebido, pajurFavoravel,  documentosCompletos, ccbPronta, agAssinatura, "
							+ "	agRegistro, preAprovadoComite, documentosComite, aprovadoComite, analiseReprovada, analiseComercial, comentarioJuridicoEsteira, status,"
							+ " pedidoLaudo, pedidoLaudoPajuComercial, pedidoPreLaudo, pedidoPreLaudoComercial , pedidoPajuComercial, pendenciaLaudoPaju, avaliacaoLaudo   ";
						
					query = query + " order by id desc";
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					while (rs.next()) {
						
						contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong("id"));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setDataContrato(rs.getDate("datacontrato"));
						contratoCobranca.getResponsavel().setNome(rs.getString(5));
						contratoCobranca.getResponsavel().setDonoResponsavel(new Responsavel());
						contratoCobranca.getResponsavel().getDonoResponsavel().setNome(rs.getString(7));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal("quantoPrecisa"));
						contratoCobranca.setStatusLead(rs.getString("statuslead"));
						contratoCobranca.getPagador().setNome(rs.getString(6));
						contratoCobranca.setInicioAnalise(rs.getBoolean("inicioAnalise"));
						contratoCobranca.setCadastroAprovadoValor(rs.getString("cadastroAprovadoValor"));
						contratoCobranca.setMatriculaAprovadaValor(rs.getString("matriculaAprovadaValor"));
						contratoCobranca.setPagtoLaudoConfirmada(rs.getBoolean("pagtolaudoconfirmada"));
						contratoCobranca.setLaudoRecebido(rs.getBoolean("laudoRecebido"));
						contratoCobranca.setPajurFavoravel(rs.getBoolean("pajurFavoravel"));
						contratoCobranca.setDocumentosCompletos(rs.getBoolean("documentosCompletos"));
						contratoCobranca.setCcbPronta(rs.getBoolean("ccbpronta"));
						contratoCobranca.setAgAssinatura(rs.getBoolean("agassinatura"));
						contratoCobranca.setAgRegistro(rs.getBoolean("agregistro"));
						contratoCobranca.setPreAprovadoComite(rs.getBoolean("preAprovadoComite"));
						contratoCobranca.setDocumentosComite(rs.getBoolean("documentosComite"));
						contratoCobranca.setAprovadoComite(rs.getBoolean("AprovadoComite"));
						contratoCobranca.setAnaliseReprovada(rs.getBoolean("analisereprovada"));
						contratoCobranca.setComentarioJuridicoEsteira(rs.getBoolean("comentarioJuridicoEsteira"));
						contratoCobranca.setPedidoPajuComercial(rs.getBoolean("pedidoPajuComercial"));
						contratoCobranca.setAvaliacaoLaudo(rs.getString("avaliacaoLaudo"));
						contratoCobranca.setStatus(rs.getString("status"));
						//contratoCobranca = findById(rs.getLong(1));
						
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
	
	private static final String QUERY_CONTRATOS_CRM = "select c.id, c.numeroContrato, c.dataContrato, res.nome, c.quantoPrecisa, im.cidade, c.statuslead, pr.nome, c.inicioAnalise, c.cadastroAprovadoValor, c.matriculaAprovadaValor, c.pagtoLaudoConfirmada, c.laudoRecebido, c.pajurFavoravel, " + 
		    "c.documentosCompletos, c.ccbPronta, c.agAssinatura, c.agRegistro, c.preAprovadoComite, c.documentosComite, c.aprovadoComite, c.analiseReprovada, c.dataUltimaAtualizacao, c.preAprovadoComiteUsuario, c.inicioanaliseusuario, c.analiseComercial, c.comentarioJuridicoEsteira, c.status, " +
			"c.pedidoLaudo, c.pedidoLaudoPajuComercial, c.pedidoPreLaudo, c.pedidoPreLaudoComercial, c.pedidoPajuComercial, c.pendenciaLaudoPaju, " +
		    "c.avaliacaoLaudoObservacao, c.dataPrevistaVistoria, c.geracaoLaudoObservacao, c.iniciouGeracaoLaudo, c.analistaGeracaoPAJU " +
			"from cobranca.contratocobranca c " +		
			"inner join cobranca.responsavel res on c.responsavel = res.id " +
			"inner join cobranca.pagadorrecebedor pr on pr.id = c.pagador " +
			"inner join cobranca.imovelcobranca im on c.imovel = im.id ";
	
	private static final String QUERY_CONTRATOS_CRM_COMITE = "select * " +
			" from cobranca.analisecomite ";
			
	
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
					String query = QUERY_CONTRATOS_CRM;
					
					query = query + "where status != 'Aprovado' and status != 'Reprovado' and status != 'Baixado' and status != 'Desistência Cliente' " ;
					
					// Verifica o tipo da consulta de contratos
					if (tipoConsulta.equals("Todos")) {
						//
						query = query + " and res.codigo != 'lead' and c.statusLead != 'Em Tratamento'";
					}
					
					if (tipoConsulta.equals("Lead")) {
						query = query + " and inicioanalise = false and c.statusLead != 'Completo' and c.statusLead != 'Reprovado' and c.statusLead != 'Arquivado' "; 
					}
					
					if (tipoConsulta.equals("Aguardando Análise")) {
						query = query + " and analiseReprovada = false and inicioanalise = false and c.statusLead = 'Completo' and res.codigo != 'lead' "; 
					}
					
					if (tipoConsulta.equals("Em Analise")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true and (cadastroAprovadoValor = '' or cadastroAprovadoValor is null)  and (matriculaAprovadaValor = '' or matriculaAprovadaValor is null)";
					}
					
					if (tipoConsulta.equals("Analise Pendente")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true and cadastroAprovadoValor = 'Pendente' ";
					}
					
					/*if (tipoConsulta.equals("Ag. Pagto. Laudo")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and (pagtoLaudoConfirmada = false or pedidoLaudo = false) ";
					}*/
					if (tipoConsulta.equals("Análise Aprovada CRM")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pedidoLaudoPajuComercial = false";
					}
					
					if (tipoConsulta.equals("Análise Aprovada")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and (pagtoLaudoConfirmada = false or pedidoLaudo = false) ";
					}
					
					/*if (tipoConsulta.equals("Pedir Pré-Laudo")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pedidoPreLaudoComercial = true and pedidoPreLaudo = false ";
					}*/
					
					if (tipoConsulta.equals("Pedir Laudo")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' "
								+ " and pendenciaLaudoPaju = false "
								+ " and pedidoLaudoPajuComercial = true and pedidoLaudo = false"
								+ " and (avaliacaoLaudo is  null or (avaliacaoLaudo is not null and avaliacaoLaudo not like 'Compass')) ";
					}
					
					if (tipoConsulta.equals("Avaliação de Imóvel")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' "
								+ " and pendenciaLaudoPaju = false "
								+ " and pedidoLaudoPajuComercial = true and pedidoLaudo = false and avaliacaoLaudo = 'Compass' ";
					}
					
					if (tipoConsulta.equals("Avaliação de Imóvel - Galache")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' "
								+ " and pendenciaLaudoPaju = false "
								+ " and pedidoLaudoPajuComercial = true and pedidoLaudo = false and avaliacaoLaudo = 'Galache' ";
					}
					
					if (tipoConsulta.equals("Geração do PAJU")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' "
								+ " and pendenciaLaudoPaju = false "
								+ " and pedidoLaudoPajuComercial = true and pagtoLaudoConfirmada = true and pajurFavoravel = false";
					}
					
					if (tipoConsulta.equals("Pedir PAJU")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true "
								+ " and cadastroAprovadoValor = 'Aprovado' "
								+ " and pendenciaLaudoPaju = false "
								+ " and pedidoLaudoPajuComercial = true and pagtoLaudoConfirmada = false and pajurFavoravel = false ";
					}
					
					if (tipoConsulta.equals("Laudo Paju Pendente")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true "
								+ " and cadastroAprovadoValor = 'Aprovado'  "
								+ " and pendenciaLaudoPaju = true "
								+ " and (laudoRecebido = false or pajurFavoravel = false) ";
					}
					
					if (tipoConsulta.equals("Ag. PAJU e Laudo")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pendenciaLaudoPaju = false and pedidoLaudoPajuComercial = true and (laudoRecebido = false or pajurFavoravel = false) ";
					} 
					
					if (tipoConsulta.equals("Ag. PAJU")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pendenciaLaudoPaju = false and pagtoLaudoConfirmada = true and pajurFavoravel = false ";
					} 
					
					if (tipoConsulta.equals("Ag. Laudo")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pendenciaLaudoPaju = false and pedidoLaudo = true and laudoRecebido = false";
					} 
					
					if (tipoConsulta.equals("Análise Comercial")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = false ";
					} 
					
					if (tipoConsulta.equals("Comentario Jurídico")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = false ";
					} 
					
					if (tipoConsulta.equals("Pré-Comite")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and preAprovadoComite = false";
					}
					
					if (tipoConsulta.equals("Ag. Documentos Comite")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and preAprovadoComite = true and documentosComite = false";
					}
					
					if (tipoConsulta.equals("Ag. Comite")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and preAprovadoComite = true and documentosComite = true and aprovadoComite = false";
					}
					
					if (tipoConsulta.equals("Ag. DOC")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and preAprovadoComite = true and documentosComite = true and aprovadoComite = true and  documentosCompletos = false";
					}
					
					if (tipoConsulta.equals("Ag. CCB")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and documentosCompletos = true and preAprovadoComite = true and documentosComite = true and aprovadoComite = true and ccbPronta = false";
					}
					
					if (tipoConsulta.equals("Ag. Assinatura")) {
						query = query + "  and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and documentosCompletos = true and preAprovadoComite = true and documentosComite = true and aprovadoComite = true and ccbPronta = true  and agAssinatura = true";
					}
					
					if (tipoConsulta.equals("Ag. Registro")) {
						query = query + " and analiseReprovada = false and c.statusLead = 'Completo' and inicioanalise = true"
								+ " and cadastroAprovadoValor = 'Aprovado' and pagtoLaudoConfirmada = true and laudoRecebido = true and pajurFavoravel = true and analiseComercial = true and comentarioJuridicoEsteira = true and documentosCompletos = true and preAprovadoComite = true and documentosComite = true and aprovadoComite = true and ccbPronta = true  and agAssinatura = false and agRegistro = true";
					}
					
					if (tipoConsulta.equals("Análise Reprovada")) {
						query = query + " and analiseReprovada = true";
					}
					
					String queryResponsavel = "";
							
					// verifica as cláusulas dos repsonsáveis
					if (codResponsavel != null || listResponsavel != null) {
						if (queryResponsavel.equals("")) {
							queryResponsavel = " and (res.codigo = '" + codResponsavel + "' ";
						}
						
						String queryGuardaChuva = "";
						if (listResponsavel != null) {
							if (listResponsavel.size() > 0) {
								for (Responsavel resp : listResponsavel) {
									if (!resp.getCodigo().equals("")) {
										// adiciona membro guarda-chuva
										if (queryGuardaChuva.equals("")) {
											queryGuardaChuva = " res.codigo = '" + resp.getCodigo() + "' ";
										} else {
											queryGuardaChuva = queryGuardaChuva + " or res.codigo = '"
													+ resp.getCodigo() + "' ";
										}

										// busca recursiva no guarda-chuva
										ResponsavelDao rDao = new ResponsavelDao();
										List<String> retornoGuardaChuvaRecursivo = new ArrayList<String>();
										retornoGuardaChuvaRecursivo = rDao
												.getGuardaChuvaRecursivoPorResponsavel(resp.getCodigo());
										for (String codigoResponsavel : retornoGuardaChuvaRecursivo) {
											queryGuardaChuva = queryGuardaChuva + " or res.codigo = '"
													+ codigoResponsavel + "' ";
										}
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
					
					connection = getConnection();
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					List<String> idsContratoCobranca = new ArrayList<String>(0);
					
					while (rs.next()) {
						
						contratoCobranca = new ContratoCobranca();

						contratoCobranca.setId(rs.getLong(1));
						contratoCobranca.setNumeroContrato(rs.getString(2));
						contratoCobranca.setDataContrato(rs.getTimestamp(3));
						contratoCobranca.setNomeResponsavel(rs.getString(4));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal(5));
						contratoCobranca.setNomeCidadeImovel(rs.getString(6));
						contratoCobranca.setStatusLead(rs.getString(7));
						contratoCobranca.setNomePagador(rs.getString(8));
						contratoCobranca.setInicioAnalise(rs.getBoolean(9));
						contratoCobranca.setCadastroAprovadoValor(rs.getString(10));
						contratoCobranca.setMatriculaAprovadaValor(rs.getString(11));
						contratoCobranca.setPagtoLaudoConfirmada(rs.getBoolean(12));
						contratoCobranca.setLaudoRecebido(rs.getBoolean(13));
						contratoCobranca.setPajurFavoravel(rs.getBoolean(14));
						contratoCobranca.setDocumentosCompletos(rs.getBoolean(15));
						contratoCobranca.setCcbPronta(rs.getBoolean(16));
						contratoCobranca.setAgAssinatura(rs.getBoolean(17));
						contratoCobranca.setAgRegistro(rs.getBoolean(18));
						contratoCobranca.setPreAprovadoComite(rs.getBoolean(19));
						contratoCobranca.setDocumentosComite(rs.getBoolean(20));
						contratoCobranca.setAprovadoComite(rs.getBoolean(21));
						contratoCobranca.setAnaliseReprovada(rs.getBoolean(22)); 
						contratoCobranca.setDataUltimaAtualizacao(rs.getTimestamp(23));
						contratoCobranca.setPreAprovadoComiteUsuario(rs.getString(24));
						contratoCobranca.setInicioAnaliseUsuario(rs.getString(25));
						contratoCobranca.setAnaliseComercial(rs.getBoolean(26));
						contratoCobranca.setComentarioJuridicoEsteira(rs.getBoolean(27));
						contratoCobranca.setStatus(rs.getString(28));	
						contratoCobranca.setPedidoLaudo(rs.getBoolean(29));
						contratoCobranca.setPedidoLaudoPajuComercial(rs.getBoolean(30));
						contratoCobranca.setPedidoPreLaudo(rs.getBoolean(31));
						contratoCobranca.setPedidoPreLaudoComercial(rs.getBoolean(32));
						contratoCobranca.setPedidoPajuComercial(rs.getBoolean(33));
						contratoCobranca.setPendenciaLaudoPaju(rs.getBoolean(34));
						contratoCobranca.setAvaliacaoLaudoObservacao(rs.getString(35));
						contratoCobranca.setDataPrevistaVistoria(rs.getDate(36));
						contratoCobranca.setGeracaoLaudoObservacao(rs.getString(37));
						contratoCobranca.setIniciouGeracaoLaudo(rs.getBoolean(38));
						
						ResponsavelDao rDao = new ResponsavelDao();
						contratoCobranca.setAnalistaGeracaoPAJU(rDao.findById(rs.getLong(39)));
						
						idsContratoCobranca.add( CommonsUtil.stringValue(contratoCobranca.getId()));

						
						//contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
					rs.close();
					
					if (!CommonsUtil.semValor(idsContratoCobranca)) {
						query = QUERY_CONTRATOS_CRM_COMITE;
						query = query + " where contratocobranca in (" + String.join(",", idsContratoCobranca) + " ) ";
						// connection = getConnection();
						ps = connection.prepareStatement(query);

						// (0, CommonsUtil.getArray(idsContratoCobranca ));
						rs = ps.executeQuery();
						while (rs.next()) {

							Long idCobranca = rs.getLong("contratocobranca");

							ContratoCobranca contratoCobrancaFind = objects.stream()
									.filter(c -> CommonsUtil.mesmoValor(c.getId(), idCobranca)).findFirst()
									.orElse(null);
							if (contratoCobrancaFind.getListaAnaliseComite() == null) {
								contratoCobrancaFind.setListaAnaliseComite(new HashSet<AnaliseComite>());
							}
							AnaliseComite analiseComite = new AnaliseComite();
							
							String votoComiteBD = rs.getString("VotoAnaliseComite");
							String marcadorVoto = "";
							
							if(CommonsUtil.mesmoValor(votoComiteBD, "Aprovado")) {
								marcadorVoto = " ✓";
							} else if(CommonsUtil.mesmoValor(votoComiteBD, "Reprovado")) {
								marcadorVoto = " X";
							} else {
								marcadorVoto = " -";
							}
							
							analiseComite.setUsuarioComite(rs.getString("usuarioComite") + marcadorVoto);
							contratoCobrancaFind.getListaAnaliseComite().add(analiseComite);
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
	public List<ContratoCobranca> geraConsultaContratosCRMBaixadoReprovado(final String codResponsavel, final List<Responsavel> listResponsavel, final String tipoConsulta) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					String query = QUERY_CONTRATOS_CRM;
					
					query = query + "where status != 'Aprovado' and status != 'Desistência Cliente' and status != 'Pendente' " ;
					
					// Verifica o tipo da consulta de contratos
					if (tipoConsulta.equals("Baixado")) {
						//
						query = query + " and status = 'Baixado' ";
					}
					if (tipoConsulta.equals("Reprovado")) {
						query = query + " and status = 'Reprovado' "; 
					}
					
					String queryResponsavel = "";
							
					// verifica as cláusulas dos repsonsáveis
					if (codResponsavel != null || listResponsavel != null) {
						if (queryResponsavel.equals("")) {
							queryResponsavel = " and (res.codigo = '" + codResponsavel + "' ";
						}
						
						String queryGuardaChuva = "";
						if (listResponsavel != null) {
							if (listResponsavel.size() > 0) {
								for (Responsavel resp : listResponsavel) {
									if (!resp.getCodigo().equals("")) {
										// adiciona membro guarda-chuva
										if (queryGuardaChuva.equals("")) {
											queryGuardaChuva = " res.codigo = '" + resp.getCodigo() + "' ";
										} else {
											queryGuardaChuva = queryGuardaChuva + " or res.codigo = '"
													+ resp.getCodigo() + "' ";
										}

										// busca recursiva no guarda-chuva
										ResponsavelDao rDao = new ResponsavelDao();
										List<String> retornoGuardaChuvaRecursivo = new ArrayList<String>();
										retornoGuardaChuvaRecursivo = rDao
												.getGuardaChuvaRecursivoPorResponsavel(resp.getCodigo());
										for (String codigoResponsavel : retornoGuardaChuvaRecursivo) {
											queryGuardaChuva = queryGuardaChuva + " or res.codigo = '"
													+ codigoResponsavel + "' ";
										}
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
					
					connection = getConnection();
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
					
					ContratoCobranca contratoCobranca = new ContratoCobranca();
					List<String> idsContratoCobranca = new ArrayList<String>(0);
					
					while (rs.next()) {
						
						contratoCobranca = new ContratoCobranca();
						
						contratoCobranca.setId(rs.getLong(1));
						contratoCobranca.setNumeroContrato(rs.getString(2));
						contratoCobranca.setDataContrato(rs.getTimestamp(3));
						contratoCobranca.setNomeResponsavel(rs.getString(4));
						contratoCobranca.setQuantoPrecisa(rs.getBigDecimal(5));
						contratoCobranca.setNomeCidadeImovel(rs.getString(6));
						contratoCobranca.setStatusLead(rs.getString(7));
						contratoCobranca.setNomePagador(rs.getString(8));
						contratoCobranca.setInicioAnalise(rs.getBoolean(9));
						contratoCobranca.setCadastroAprovadoValor(rs.getString(10));
						contratoCobranca.setMatriculaAprovadaValor(rs.getString(11));
						contratoCobranca.setPagtoLaudoConfirmada(rs.getBoolean(12));
						contratoCobranca.setLaudoRecebido(rs.getBoolean(13));
						contratoCobranca.setPajurFavoravel(rs.getBoolean(14));
						contratoCobranca.setDocumentosCompletos(rs.getBoolean(15));
						contratoCobranca.setCcbPronta(rs.getBoolean(16));
						contratoCobranca.setAgAssinatura(rs.getBoolean(17));
						contratoCobranca.setAgRegistro(rs.getBoolean(18));
						contratoCobranca.setPreAprovadoComite(rs.getBoolean(19));
						contratoCobranca.setDocumentosComite(rs.getBoolean(20));
						contratoCobranca.setAprovadoComite(rs.getBoolean(21));
						contratoCobranca.setAnaliseReprovada(rs.getBoolean(22)); 
						contratoCobranca.setDataUltimaAtualizacao(rs.getTimestamp(23));
						contratoCobranca.setPreAprovadoComiteUsuario(rs.getString(24));
						contratoCobranca.setInicioAnaliseUsuario(rs.getString(25));
						contratoCobranca.setAnaliseComercial(rs.getBoolean(26));
						contratoCobranca.setComentarioJuridicoEsteira(rs.getBoolean(27));
						contratoCobranca.setStatus(rs.getString(28));
						idsContratoCobranca.add( CommonsUtil.stringValue(contratoCobranca.getId()));
						
						
						//contratoCobranca = findById(rs.getLong(1));
						
						objects.add(contratoCobranca);												
					}
					rs.close();
					
					if (!CommonsUtil.semValor(idsContratoCobranca)) {
						query = QUERY_CONTRATOS_CRM_COMITE;
						query = query + " where contratocobranca in (" + String.join(",", idsContratoCobranca) + " ) ";
						// connection = getConnection();
						ps = connection.prepareStatement(query);

						// (0, CommonsUtil.getArray(idsContratoCobranca ));
						rs = ps.executeQuery();
						while (rs.next()) {

							Long idCobranca = rs.getLong("contratocobranca");

							ContratoCobranca contratoCobrancaFind = objects.stream()
									.filter(c -> CommonsUtil.mesmoValor(c.getId(), idCobranca)).findFirst()
									.orElse(null);
							if (contratoCobrancaFind.getListaAnaliseComite() == null) {
								contratoCobrancaFind.setListaAnaliseComite(new HashSet<AnaliseComite>());
							}
							AnaliseComite analiseComite = new AnaliseComite();
							
							String votoComiteBD = rs.getString("VotoAnaliseComite");
							String marcadorVoto = "";
							
							if(CommonsUtil.mesmoValor(votoComiteBD, "Aprovado")) {
								marcadorVoto = " ✓";
							} else if(CommonsUtil.mesmoValor(votoComiteBD, "Reprovado")) {
								marcadorVoto = " X";
							} else {
								marcadorVoto = " -";
							}
							
							analiseComite.setUsuarioComite(rs.getString("usuarioComite") + marcadorVoto);
							contratoCobrancaFind.getListaAnaliseComite().add(analiseComite);
						}
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


	
	private static final String QUERY_GET_DRE_ENTRADAS =  	"select codj.idContratoCobrancaDetalhes, codj.idContratoCobranca, coco.numeroContrato, pare.nome, ccde.numeroParcela,"
			+ "        ccdp.vlrrecebido,      "
			+ "        ccdp.datapagamento , "
			+ "	COALESCE ( ( "
			+ "          select  sum( vlrrecebido ) "
			+ "            from  cobranca.contratocobrancadetalhesparcial ccdp2 "
			+ "                  inner join cobranca.cobranca_detalhes_parcial_join cdpj2 on cdpj2.idcontratocobrancadetalhesparcial = ccdp2.id "
			+ "            where cdpj2.idcontratocobrancadetalhes  = codj.idContratoCobrancaDetalhes and "
			+ "                  ccdp2.id < ccdp.id ),0) totalrecebidoAnterior, "
			+ "	vlrjurosparcela, "
			+ "	vlramortizacaoparcela, "
			+ " ccdp.saldoapagar, "
			+ "	ccdp.vlrrecebido, "
			+ "	ccde.seguromip, "
			+ "	ccde.segurodfi "
			+ " from cobranca.ContratoCobrancaDetalhes ccde"
			+ " inner join cobranca.cobranca_detalhes_parcial_join cdpj on ccde.id = cdpj.idcontratocobrancadetalhes  "
			+ " inner join cobranca.contratocobrancadetalhesparcial ccdp on cdpj.idcontratocobrancadetalhesparcial = ccdp.id"
			+ " inner join cobranca.ContratoCobranca_Detalhes_Join codj on ccde.id = codj.idContratoCobrancaDetalhes"
			+ " inner join cobranca.ContratoCobranca coco on  codj.idContratoCobranca = coco.id"
			+ " inner join cobranca.pagadorrecebedor pare on coco.pagador = pare.id"
			+ " where ccdp.datapagamento between  ? ::timestamp and  ? ::timestamp"
			+ " and coco.status = 'Aprovado' "
			+ " and coco.pagador not in (15, 34,14, 182, 417, 803) "
			+ " order by ccdp.datapagamento;";
	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreEntradas(final Date dataInicio, final Date dataFim) throws Exception {
		
				DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
				demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
				demonstrativosResultadosGrupoDetalhe.setTipo("Entradas");
				demonstrativosResultadosGrupoDetalhe.setCodigo(1);

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
						
						if(CommonsUtil.mesmoValor(rs.getString("numeroParcela"), "Amortização")) {
							continue;
						}
						
						demonstrativoResultadosGrupoDetalhe.setNumeroParcela(rs.getInt("numeroParcela"));
						
						
						Date dataVencimento = rs.getDate("datapagamento");
						
						demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataVencimento);
						
						
						BigDecimal totalrecebidoAnterior = rs.getBigDecimal("totalrecebidoAnterior");
						BigDecimal vlrjurosparcela = rs.getBigDecimal("vlrjurosparcela");
						BigDecimal vlramortizacaoparcela = rs.getBigDecimal("vlramortizacaoparcela");
						BigDecimal saldoapagar = rs.getBigDecimal("saldoapagar");
						BigDecimal vlrrecebido = rs.getBigDecimal("vlrrecebido");
						BigDecimal seguroMip = rs.getBigDecimal("seguromip");
						BigDecimal seguroDfi = rs.getBigDecimal("segurodfi");
						
						if ( vlrjurosparcela == null)
							vlrjurosparcela = BigDecimal.ZERO;
						if ( vlramortizacaoparcela == null)
							vlramortizacaoparcela = BigDecimal.ZERO;
						if ( saldoapagar == null)
							saldoapagar = BigDecimal.ZERO;
						if ( vlrrecebido == null)
							vlrrecebido = BigDecimal.ZERO;
						
						if (CommonsUtil.semValor(seguroMip))
							seguroMip = BigDecimal.ZERO;
						if (CommonsUtil.semValor(seguroDfi))
							seguroDfi = BigDecimal.ZERO;
						
						demonstrativoResultadosGrupoDetalhe.setValor((vlrrecebido.subtract(seguroDfi)).subtract(seguroMip));
								
						if ( vlrjurosparcela == BigDecimal.ZERO)						
							demonstrativoResultadosGrupoDetalhe.setJuros(vlrjurosparcela);						
						else if ( totalrecebidoAnterior == BigDecimal.ZERO && saldoapagar  == BigDecimal.ZERO ) //recebeu tudo em uma parcela				
							demonstrativoResultadosGrupoDetalhe.setJuros(vlrjurosparcela);
						else if ( totalrecebidoAnterior.compareTo(vlrjurosparcela) >=0 )		//recebeu todos os juros
							demonstrativoResultadosGrupoDetalhe.setJuros( BigDecimal.ZERO);
						else if ( totalrecebidoAnterior.add(vlrrecebido).compareTo(vlrjurosparcela) >= 0 )		//esta recebendo os juros		
							demonstrativoResultadosGrupoDetalhe.setJuros(vlrjurosparcela.subtract(totalrecebidoAnterior));
						else if ( totalrecebidoAnterior.compareTo(vlrjurosparcela) == -1 )			//ainda nao recebeu os juros 
							demonstrativoResultadosGrupoDetalhe.setJuros(vlrrecebido);
						
						else	
							demonstrativoResultadosGrupoDetalhe.setJuros( BigDecimal.ZERO);	

						
						if ( vlramortizacaoparcela == BigDecimal.ZERO)						
							demonstrativoResultadosGrupoDetalhe.setJuros(vlramortizacaoparcela);
						
						else if ( totalrecebidoAnterior.compareTo(vlrjurosparcela.add(vlramortizacaoparcela)) == 1 )		//recebeu todos os juros
							demonstrativoResultadosGrupoDetalhe.setAmortizacao( BigDecimal.ZERO);
						
						else if ( vlrrecebido.subtract(demonstrativoResultadosGrupoDetalhe.getJuros()).compareTo(BigDecimal.ZERO) ==1 &&
								demonstrativoResultadosGrupoDetalhe.getJuros().compareTo(BigDecimal.ZERO)==1) { 
							//Sobrou valor pago do juros
							BigDecimal vlrResidual  =  vlrrecebido.subtract(demonstrativoResultadosGrupoDetalhe.getJuros());
							if ( vlrResidual.compareTo(vlramortizacaoparcela) >= 0)
								demonstrativoResultadosGrupoDetalhe.setAmortizacao(vlramortizacaoparcela);
							else
								demonstrativoResultadosGrupoDetalhe.setAmortizacao(vlrResidual);
						} else if( demonstrativoResultadosGrupoDetalhe.getJuros().compareTo(BigDecimal.ZERO)==0) {
							//Pagou juros
							BigDecimal vlrResidual  =  totalrecebidoAnterior.subtract( vlrjurosparcela ).add(vlrrecebido);
							
							if ( vlrResidual.compareTo(vlramortizacaoparcela) >= 0 )
								demonstrativoResultadosGrupoDetalhe.setAmortizacao(vlramortizacaoparcela.subtract(totalrecebidoAnterior.subtract( vlrjurosparcela )) );
							else {
								demonstrativoResultadosGrupoDetalhe.setAmortizacao(vlrrecebido);
							}
							
						}

						//demonstrativoResultadosGrupoDetalhe.setAmortizacao(rs.getBigDecimal("vlramortizacaoparcela"));
						
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
	
	private static final String QUERY_GET_DRE_SEGUROS = " select "
			+ "		codj.idContratoCobrancaDetalhes, "
			+ "		codj.idContratoCobranca, "
			+ "		coco.numeroContrato, "
			+ "		pare.nome, "
			+ "		ccde.numeroParcela, "
			+ "		ccdp.datapagamento, "
			+ "		ccde.seguromip, "
			+ "		ccde.segurodfi "
			+ " from "
			+ "		cobranca.ContratoCobrancaDetalhes ccde "
			+ " inner join cobranca.cobranca_detalhes_parcial_join cdpj on "
			+ "		ccde.id = cdpj.idcontratocobrancadetalhes "
			+ " inner join cobranca.contratocobrancadetalhesparcial ccdp on "
			+ "		cdpj.idcontratocobrancadetalhesparcial = ccdp.id "
			+ " inner join cobranca.ContratoCobranca_Detalhes_Join codj on "
			+ "		ccde.id = codj.idContratoCobrancaDetalhes "
			+ " inner join cobranca.ContratoCobranca coco on "
			+ "		codj.idContratoCobranca = coco.id "
			+ " inner join cobranca.pagadorrecebedor pare on "
			+ "		coco.pagador = pare.id "
			+ " where "
			+ "		ccdp.datapagamento between  ? ::timestamp and  ? ::timestamp "
			+ "		and coco.status = 'Aprovado' "
			+ "		and coco.pagador not in (15, 34, 14, 182, 417, 803) "
			+ "		and (ccde.seguromip notnull or ccde.segurodfi notnull) "
			+ " order by "
			+ "		ccdp.datapagamento ";
	
	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreSeguros(final Date dataInicio, final Date dataFim) throws Exception {
		
				DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
				demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
				demonstrativosResultadosGrupoDetalhe.setTipo("Seguro");
				demonstrativosResultadosGrupoDetalhe.setCodigo(1);

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					String query_QUERY_GET_DRE_ENTRADAS = QUERY_GET_DRE_SEGUROS;
					ps = connection.prepareStatement(query_QUERY_GET_DRE_ENTRADAS);
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					rs = ps.executeQuery();

					while (rs.next()) {

						DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();
						demonstrativoResultadosGrupoDetalhe.setIdDetalhes(rs.getLong("idContratoCobrancaDetalhes"));
						demonstrativoResultadosGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
						demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
						demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
						
						if(CommonsUtil.mesmoValor(rs.getString("numeroParcela"), "Amortização")) {
							continue;
						}
					
						demonstrativoResultadosGrupoDetalhe.setNumeroParcela(rs.getInt("numeroParcela"));
						Date dataVencimento = rs.getDate("datapagamento");
						demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataVencimento);	
						BigDecimal seguroMip = rs.getBigDecimal("seguromip");
						BigDecimal seguroDfi = rs.getBigDecimal("segurodfi");
						
						if (CommonsUtil.semValor(seguroMip))
							seguroMip = BigDecimal.ZERO;
						if (CommonsUtil.semValor(seguroDfi))
							seguroDfi = BigDecimal.ZERO;
						
						demonstrativoResultadosGrupoDetalhe.setValor(seguroDfi.add(seguroMip));
						demonstrativoResultadosGrupoDetalhe.setJuros(BigDecimal.ZERO);
						demonstrativoResultadosGrupoDetalhe.setAmortizacao(BigDecimal.ZERO);
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
	// + " and coco.pagador not in (15, 34,14, 182, 417, 803)" 
	 + " and baixado = true"
	 + " and not (saldocredoratualizado = 0 "
	 + "  and amortizacao > 0)"
	 + " order by numerocontrato;";	
	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreSaidas(final Date dataInicio, final Date dataFim) throws Exception {
		
				DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
				demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
				demonstrativosResultadosGrupoDetalhe.setTipo("Saidas");
				demonstrativosResultadosGrupoDetalhe.setCodigo(1);

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
						if(CommonsUtil.semValor(CommonsUtil.integerValue(rs.getString("numeroParcela")))) {
							demonstrativoResultadosGrupoDetalhe.setNumeroParcela(9999999);
						} else {
							demonstrativoResultadosGrupoDetalhe.setNumeroParcela(rs.getInt("numeroParcela"));
						}
						
						Date dataVencimento = rs.getDate("databaixa");						
						demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataVencimento);
						
						if(!CommonsUtil.semValor(rs.getBigDecimal("valorbaixado"))) {
							demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("valorbaixado"));				
						} else {
							demonstrativoResultadosGrupoDetalhe.setValor(BigDecimal.ZERO);	
						}
								
						demonstrativoResultadosGrupoDetalhe.setAmortizacao(rs.getBigDecimal("amortizacao"));
						demonstrativoResultadosGrupoDetalhe.setJuros(demonstrativoResultadosGrupoDetalhe.getValor().subtract(demonstrativoResultadosGrupoDetalhe.getAmortizacao()));
						demonstrativosResultadosGrupoDetalhe.getDetalhe().add(demonstrativoResultadosGrupoDetalhe);

						BigDecimal resto = demonstrativoResultadosGrupoDetalhe.getValor()
								.subtract(demonstrativoResultadosGrupoDetalhe.getJuros())
								.subtract(demonstrativoResultadosGrupoDetalhe.getAmortizacao());
						
						if (resto.compareTo(BigDecimal.ZERO) > 0) {
							demonstrativoResultadosGrupoDetalhe
									.setJuros(demonstrativoResultadosGrupoDetalhe.getJuros().add(resto));
						}

						demonstrativosResultadosGrupoDetalhe.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
						demonstrativosResultadosGrupoDetalhe.addJuros(demonstrativoResultadosGrupoDetalhe.getJuros());
						demonstrativosResultadosGrupoDetalhe
								.addAmortizacao(demonstrativoResultadosGrupoDetalhe.getAmortizacao());						
					}
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				} finally {
					closeResources(connection, ps, rs);
				}
				return demonstrativosResultadosGrupoDetalhe;			
	}
	
	private static final String QUERY_GET_DRE_RESGATES = "select   ccpi.id , coco.id idContratoCobranca, coco.numeroContrato, pare.nome,  ccpi.numeroParcela, ccpi.parcelamensal,  ccpi.valorbaixado,  ccpi.juros, ccpi.amortizacao, ccpi.databaixa"
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
	// + " and coco.pagador not in (15, 34,14, 182, 417, 803)" 
	 + " and baixado = true"
	 + " and saldocredoratualizado = 0 "
	 + " and amortizacao > 0 "
	 + " order by numerocontrato;";
	
	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreResgates(final Date dataInicio, final Date dataFim) throws Exception {
		
				DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
				demonstrativosResultadosGrupoDetalhe.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
				demonstrativosResultadosGrupoDetalhe.setTipo("Resgates");
				demonstrativosResultadosGrupoDetalhe.setCodigo(1);

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();

					String query_QUERY_GET_DRE_SAIDAS = QUERY_GET_DRE_RESGATES;

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
						
						if(!CommonsUtil.semValor(rs.getBigDecimal("valorbaixado"))) {
							demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("valorbaixado"));				
						} else {
							demonstrativoResultadosGrupoDetalhe.setValor(BigDecimal.ZERO);	
						}
								
						demonstrativoResultadosGrupoDetalhe.setAmortizacao(rs.getBigDecimal("amortizacao"));
						demonstrativoResultadosGrupoDetalhe.setJuros(demonstrativoResultadosGrupoDetalhe.getValor().subtract(demonstrativoResultadosGrupoDetalhe.getAmortizacao()));
						demonstrativosResultadosGrupoDetalhe.getDetalhe().add(demonstrativoResultadosGrupoDetalhe);

						BigDecimal resto = demonstrativoResultadosGrupoDetalhe.getValor()
								.subtract(demonstrativoResultadosGrupoDetalhe.getJuros())
								.subtract(demonstrativoResultadosGrupoDetalhe.getAmortizacao());
						
						if (resto.compareTo(BigDecimal.ZERO) > 0) {
							demonstrativoResultadosGrupoDetalhe
									.setJuros(demonstrativoResultadosGrupoDetalhe.getJuros().add(resto));
						}

						demonstrativosResultadosGrupoDetalhe.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
						demonstrativosResultadosGrupoDetalhe.addJuros(demonstrativoResultadosGrupoDetalhe.getJuros());
						demonstrativosResultadosGrupoDetalhe
								.addAmortizacao(demonstrativoResultadosGrupoDetalhe.getAmortizacao());						
					}
				} catch (SQLException e) {
					throw new Exception(e.getMessage());
				} finally {
					closeResources(connection, ps, rs);
				}
				return demonstrativosResultadosGrupoDetalhe;			
	}
	
	private static final String QUERY_GET_DRE_CONTRATOS = "select " 
			+ "       coco.datacontrato,"
			+ "       coco.id idContratoCobranca, "
			+ "       coco.numeroContrato, "
			+ "       pare.nome,"
			+ "       valorccb "
			+ " from cobranca.contratocobranca coco"
			+ " inner join cobranca.pagadorrecebedor pare on coco.pagador = pare.id"
	 + " where datacontrato between ? ::timestamp and  ? ::timestamp"
	 + " and coco.status = 'Aprovado'"
	 + "and pagador not in (15, 34,14, 182, 417, 803)"
	 + " order by numerocontrato;";	

	@SuppressWarnings("unchecked")
	public DemonstrativoResultadosGrupo getDreContrato(final Date dataInicio, final Date dataFim)
			throws Exception {
		
		DemonstrativoResultadosGrupo demonstrativosResultadosGrupoDetalhe = new DemonstrativoResultadosGrupo();
		demonstrativosResultadosGrupoDetalhe
				.setDetalhe(new ArrayList<DemonstrativoResultadosGrupoDetalhe>(0));
		demonstrativosResultadosGrupoDetalhe.setTipo("CCBs emitidas");
		demonstrativosResultadosGrupoDetalhe.setCodigo(3);

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query_QUERY_GET_DRE_SAIDAS = QUERY_GET_DRE_CONTRATOS;

			ps = connection.prepareStatement(query_QUERY_GET_DRE_SAIDAS);

			java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
			java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());

			ps.setDate(1, dtRelInicioSQL);
			ps.setDate(2, dtRelFimSQL);

			rs = ps.executeQuery();

			while (rs.next()) {
				DemonstrativoResultadosGrupoDetalhe demonstrativoResultadosGrupoDetalhe = new DemonstrativoResultadosGrupoDetalhe();

				demonstrativoResultadosGrupoDetalhe.setIdContratoCobranca(rs.getLong("idContratoCobranca"));
				demonstrativoResultadosGrupoDetalhe.setNumeroContrato(rs.getString("numeroContrato"));
				demonstrativoResultadosGrupoDetalhe.setNome(rs.getString("nome"));
				Date dataContrato = rs.getDate("datacontrato");
				demonstrativoResultadosGrupoDetalhe.setDataVencimento(dataContrato);
				demonstrativoResultadosGrupoDetalhe.setValor(rs.getBigDecimal("valorccb"));

					demonstrativosResultadosGrupoDetalhe.getDetalhe()
							.add(demonstrativoResultadosGrupoDetalhe);
					demonstrativosResultadosGrupoDetalhe
							.addValor(demonstrativoResultadosGrupoDetalhe.getValor());
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return demonstrativosResultadosGrupoDetalhe;
	}
	
	private static final String QUERY_ORIGEM_LEADS = 
					" select "  
					+ " (select count(id) from cobranca.contratocobranca   " 
					+ " where urllead = 'Refinanciamento de Imóvel') refinamento,    " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead = 'Empréstimo para negativados') emprestimonegativados,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead = 'Empréstimo online') emprestimoonline,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead = 'Empréstimo Home Equity') emprestimoequity,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead = 'Empréstimo com terreno em garantia') emprestimoterreno,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead = 'Empréstimo online YT') emprestimoonlineyt,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead = 'Simulador online') simuladoronline,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead like '%creditocasa%') creditocasa,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead like '%emprestimoimobiliario%') emprestimoimobiliario,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead like '%creditoimobiliario%') creditoimobiliario,  "  
					+ " (select count(id) from cobranca.contratocobranca  where urllead like '%garantiadeimovel%') garantiadeimovel," 
					+ " (select count(id) from cobranca.contratocobranca  where urllead like '%homeequity%') homeequity,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead like '%emprestimocomgarantiadeimovel%') emprestimocomgarantiadeimovel,   " 
					+ " (select count(id) from cobranca.contratocobranca  where urllead != 'Refinanciamento de Imóvel' and  urllead != 'Empréstimo para negativados' and   " 
					+ "  urllead != 'Empréstimo online' and  urllead != 'Empréstimo Home Equity' and  urllead != 'Empréstimo com terreno em garantia'  " 
					+ "  and  urllead != 'Empréstimo online YT' and  urllead != 'Simulador online' and  urllead != '%creditocasa%' and  urllead != '%emprestimoimobiliario%' " 
					+ "  and  urllead != '%creditoimobiliario%' and  urllead != '%garantiadeimovel%' and  urllead != '%homeequity%' and  urllead != '%emprestimocomgarantiadeimovel%') outros  ";

	@SuppressWarnings("unchecked")
	public Dashboard getOrigemLeads()
			throws Exception {
		
		Dashboard dashboard = new Dashboard();

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query_QUERY_ORIGEM_LEADS = QUERY_ORIGEM_LEADS;

			ps = connection.prepareStatement(query_QUERY_ORIGEM_LEADS);

			rs = ps.executeQuery();

			while (rs.next()) {	
				dashboard.setRefinanciamentoDeImovel(rs.getInt("refinamento"));
				dashboard.setEmprestimoParaNegativados(rs.getInt("emprestimonegativados"));
				dashboard.setEmprestimoOnline(rs.getInt("emprestimoonline"));
				dashboard.setEmprestimoHomeEquity(rs.getInt("emprestimoequity"));
				dashboard.setEmprestimoComTerrenoEmGarantia(rs.getInt("emprestimoterreno"));
				dashboard.setEmprestimoOnlineYT(rs.getInt("emprestimoonlineyt"));					
				dashboard.setSimuladorOnline(rs.getInt("simuladoronline"));
				dashboard.setOutrasOrigens(rs.getInt("outros"));
				dashboard.setCreditocasa(rs.getInt("creditocasa"));
				dashboard.setEmprestimoimobiliario(rs.getInt("emprestimoimobiliario"));
				dashboard.setCreditoimobiliario(rs.getInt("creditoimobiliario"));
				dashboard.setGarantiadeimovel(rs.getInt("garantiadeimovel"));
				dashboard.setHomeequity(rs.getInt("homeequity"));
				dashboard.setEmprestimocomgarantiadeimovel(rs.getInt("emprestimocomgarantiadeimovel"));
				
				
				
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return dashboard;
	}
	
	private static final String QUERY_STATUS_LEADS = "select  " 
			+ " (select count(id) from cobranca.contratocobranca " 
			+ " where statusLead = 'Novo Lead') novolead,  " 
			+ " (select count(id) from cobranca.contratocobranca " 
			+ " where statusLead = 'Em Tratamento') leademtratamento ";

	@SuppressWarnings("unchecked")
	public Dashboard getStatusLeads()
			throws Exception {
		
		Dashboard dashboard = new Dashboard();

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();

			String query_QUERY_STATUS_LEADS = QUERY_STATUS_LEADS;

			ps = connection.prepareStatement(query_QUERY_STATUS_LEADS);

			rs = ps.executeQuery();

			while (rs.next()) {	
				dashboard.setNovoLead(rs.getInt("novolead"));
				dashboard.setLeadEmTratamento(rs.getInt("leademtratamento"));
			}
			
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		} finally {
			closeResources(connection, ps, rs);
		}
		return dashboard;
	}
	
	private static final String QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL = "select idparcela, numeroparcela, count(idparcela) from ( "
			+ "select cc.id, cd.id idparcela, cd.numeroparcela numeroparcela, 'parcial' tipobaixa from cobranca.contratocobranca cc "
			+ "inner join cobranca.contratocobranca_detalhes_join cdj on cdj.idcontratocobranca = cc.id "
			+ "inner join cobranca.contratocobrancadetalhes cd on cdj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.cobranca_detalhes_parcial_join cdpj on cdpj.idcontratocobrancadetalhes = cd.id "
			+ "inner join cobranca.contratocobrancadetalhesparcial cdp on cdp.id = cdpj.idcontratocobrancadetalhesparcial "
			+ "where cc.id = ?  "
			+ "and cd.parcelapaga = false ";
	
	private static final String QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL = "select cc.id, cd.id idparcela, cd.numeroparcela numeroparcela, 'integral' tipobaixa " + 
			"			from cobranca.contratocobrancadetalhes cd  " + 
			"			inner join cobranca.contratocobranca_detalhes_join cdj on cd.id = cdj.idcontratocobrancadetalhes  " + 
			"			inner join cobranca.contratocobranca cc on cc.id = cdj.idcontratocobranca " + 
			"			where cc.id = ?  " + 
			"			and cd.parcelapaga = false ";			

	@SuppressWarnings("unchecked")
	public String getParcelasAtraso(final Date dataHoje, final String filtrarDataVencimento, final long idContratoCobranca) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {	
				
				int countParcelas = 0;
				String baixaParcial = "|| ";
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				String query_RELATORIO_FINANCEIRO_CUSTOM = null;
				String query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL = QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL;	
				String query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL = QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL;	
				
				String retorno = "";
				
				try {
					connection = getConnection();
				
					if (filtrarDataVencimento.equals("Atualizada")) {
						query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL + " and cd.datavencimentoatual <= ? ::timestamp ";
						query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL + " and cd.datavencimentoatual <= ? ::timestamp ";
					} 
					
					if (filtrarDataVencimento.equals("Original")) {
						query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL + " and cd.dataVencimento <= ? ::timestamp ";
						query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL + " and cd.dataVencimento <= ? ::timestamp ";
					}
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL + " and ((cd.dataVencimento <= ? ::timestamp) or (cd.promessaPagamento <= ? ::timestamp)) ";
						query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL + " and ((cd.dataVencimento <= ? ::timestamp) or (cd.promessaPagamento <= ? ::timestamp)) ";
					}

					query_RELATORIO_FINANCEIRO_CUSTOM = query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_PARCIAL + " union all " +  query_QUERY_GET_PARCELAS_ATRASO_CONTRATO_INTEGRAL;
					query_RELATORIO_FINANCEIRO_CUSTOM = query_RELATORIO_FINANCEIRO_CUSTOM + " ) atrasos group by idparcela, numeroparcela order by idparcela ";
										
					ps = connection
							.prepareStatement(query_RELATORIO_FINANCEIRO_CUSTOM);
					
					int qtdeParams = 1;
					
					ps.setLong(qtdeParams, idContratoCobranca);
					qtdeParams = qtdeParams + 1;
					
					java.sql.Date dtrHojeSQL = new java.sql.Date(dataHoje.getTime());
					
					if (filtrarDataVencimento.equals("Atualizada")) {
						ps.setDate(qtdeParams, dtrHojeSQL);
						qtdeParams = qtdeParams + 1;
					} 
					
					if (filtrarDataVencimento.equals("Original")) {
						ps.setDate(qtdeParams, dtrHojeSQL);
						qtdeParams = qtdeParams + 1;
					}
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						ps.setDate(qtdeParams, dtrHojeSQL);
						ps.setDate(qtdeParams, dtrHojeSQL);
						qtdeParams = qtdeParams + 2;
					}	
					
					ps.setLong(qtdeParams, idContratoCobranca);
					qtdeParams = qtdeParams + 1;
					
					if (filtrarDataVencimento.equals("Atualizada")) {
						ps.setDate(qtdeParams, dtrHojeSQL);
						qtdeParams = qtdeParams + 1;
					} 
					
					if (filtrarDataVencimento.equals("Original")) {
						ps.setDate(qtdeParams, dtrHojeSQL);
						qtdeParams = qtdeParams + 1;
					}
					
					if (filtrarDataVencimento.equals("Original e Promessa")) {
						ps.setDate(qtdeParams, dtrHojeSQL);
						ps.setDate(qtdeParams, dtrHojeSQL);
						qtdeParams = qtdeParams + 2;
					}	
	
					rs = ps.executeQuery();
			
					boolean primeiraBaixaParcial = true;	
					
					while (rs.next()) {
						if (rs.getInt(3) > 1) {
							if (primeiraBaixaParcial) {
								baixaParcial = baixaParcial + rs.getInt(2);
								primeiraBaixaParcial = false;
							} else {
								baixaParcial = baixaParcial + " - " + rs.getInt(2);
							}							
						}
						countParcelas = countParcelas + 1;											
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				
				return String.valueOf(countParcelas) + baixaParcial;
			}
		});	
	}	
	
	private static final String QUERY_CONTRATOS_PENDENTES_VALIDACAO_NOVO_CONTRATO = "select c.numerocontrato, imovel.numeromatricula, imovel.cep from cobranca.contratocobranca c " + 
																					" inner join cobranca.imovelcobranca imovel on imovel.id = c.imovel ";
	
	@SuppressWarnings("unchecked")
	public String validaImovelNovoContrato(final String matriculaLimpa, final String cepLimpo) {
		return (String) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				String retorno = null;
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {		
					String query = QUERY_CONTRATOS_PENDENTES_VALIDACAO_NOVO_CONTRATO;
					
					query = query + "where status != 'Aprovado' and status != 'Baixado' " ;
					
					connection = getConnection();
					
					ps = connection
							.prepareStatement(query);
					
					rs = ps.executeQuery();
				
					while (rs.next()) {
						
						if (rs.getString(2) != null && rs.getString(3) != null) {
							String matriculaLimpaBD = CommonsUtil.somenteNumeros(rs.getString(2).replace(".", "").replace("-", ""));
							String cepLimpoBD = CommonsUtil.somenteNumeros(rs.getString(3).replace(".", "").replace("-", ""));
							
							if (matriculaLimpaBD.equals(matriculaLimpa) && cepLimpoBD.equals(cepLimpo)) {
								retorno = rs.getString(1);
							}	
						}
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return retorno;
			}
		});	
	}
	
	
	
	

	private static final String QUERY_ATUALIZACAO_IPCA = 	"select cont.id "
			+ " from cobranca.contratocobranca cont"
			+ " where tipocalculo in ( 'Price', 'SAC')" 
			+ " and cont.corrigidoipca = true"
			+ " and cont.recalculaIPCA = true"
			;
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getContratosCalculoIpca() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();

				Connection connection = null;
				PreparedStatement ps = null;
				
				ResultSet rs = null;
				try {
					connection = getConnection();

					ps = connection.prepareStatement(QUERY_ATUALIZACAO_IPCA);
	
					rs = ps.executeQuery();
					
					while (rs.next()) {
						objects.add(findById(rs.getLong(1)));
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}	
	
	private static final String QUERY_CONTRATOS_PARA_RELATORIO = " select coco.id, coco.numeroContrato, coco.datacontrato, r.nome,  "
			+ "	pare.nome, r1.nome "
			+ "	from cobranca.contratocobranca coco "
			+ "	inner join cobranca.pagadorrecebedor pare on pare.id = coco.pagador "
			+ " inner join cobranca.responsavel r on r.id = coco.responsavel"
			+ " left join cobranca.responsavel r1 on r1.id = r.donoResponsavel"
			+ " where coco.statuslead = 'Completo' "
			+ " and inicioAnaliseData >= ? ::timestamp "
			+ " and inicioAnaliseData <= ? ::timestamp ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> getDashboardContratosParaRelatorio(final Date dataInicio, final Date dataFim, final String codResponsavel, final boolean geral) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();
					String query = QUERY_CONTRATOS_PARA_RELATORIO;
					
					if (codResponsavel != null) {
						if (!codResponsavel.equals("")) {
							if(geral) {
								query = query + " and  r.codigo = '" + codResponsavel + "'";
							} else {
								query = query + " and (r1.codigo = '" + codResponsavel + "' " + " or  r.codigo = '" + codResponsavel + "') " ;
							}
						}				
					}

					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					java.sql.Date dtRelFimSQL = new java.sql.Date(dataFim.getTime());
					
					query = query + " order by id ";

					ps = connection.prepareStatement(query);

					ps.setDate(1, dtRelInicioSQL);
					ps.setDate(2, dtRelFimSQL);
					
					rs = ps.executeQuery();

					ContratoCobranca contrato = new ContratoCobranca();

					while (rs.next()) {
						contrato = new ContratoCobranca();
						
						contrato.setNumeroContrato(rs.getString("numerocontrato"));
						contrato.setDataContrato(rs.getDate("datacontrato"));
						
						contrato.setResponsavel(new Responsavel());
						contrato.getResponsavel().setNome(rs.getString(4));
						
						contrato.setPagador(new PagadorRecebedor());
						contrato.getPagador().setNome(rs.getString(5));
						
						contrato.getResponsavel().setDonoResponsavel(new Responsavel());
						contrato.getResponsavel().getDonoResponsavel().setNome(rs.getString(6));
						
						objects.add(contrato);
					}

				} finally {
					closeResources(connection, ps, rs);
				}
				return objects;
			}
		});
	}
	

	private Integer contarTotalParcelas(List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhes) {
		int totalParcelas = 0;

		for (ContratoCobrancaDetalhes contratoCobrancaDetalhes : listContratoCobrancaDetalhes) {
			if (CommonsUtil.mesmoValor(contratoCobrancaDetalhes.getNumeroParcela(), "0")
					|| contratoCobrancaDetalhes.isAmortizacao() )
				continue;
			totalParcelas++;
		}

		return totalParcelas;
	}
	
	private static final String QUERY_QUANTIDADE_CONTRATOS_COMITE = " select numeroContrato from cobranca.contratocobranca c\r\n"
			+ "where c.InicioAnalise = true and c.CadastroAprovadoValor = 'Aprovado'\r\n"
			+ "and c.PagtoLaudoConfirmada = true and c.LaudoRecebido = true and c.PajurFavoravel = true\r\n"
			+ "and c.PreAprovadoComite = true and c.documentosComite = true and c.AprovadoComite = false and status = 'Pendente'";
	
	@SuppressWarnings("unchecked")
	public int getQuantidadeContratosComite() {
		return (int) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				int object = 0;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();
					String query = QUERY_QUANTIDADE_CONTRATOS_COMITE;
					ps = connection.prepareStatement(query);					
					rs = ps.executeQuery();

					ContratoCobranca contrato = new ContratoCobranca();
					List<String> listaContratos = new ArrayList<String>();

					while (rs.next()) {	
						listaContratos.add(rs.getString("numerocontrato"));
					}
					
					object = listaContratos.size();

				} finally {
					closeResources(connection, ps, rs);
				}
				return object;
			}
		});
	}
	
	private static final String QUERY_QUANTIDADE_CONTRATOS_DOCUMENTOS_COMITE = " select numeroContrato from cobranca.contratocobranca c\r\n"
			+ "where c.InicioAnalise = true and c.CadastroAprovadoValor = 'Aprovado'\r\n"
			+ "and c.PagtoLaudoConfirmada = true and c.LaudoRecebido = true and c.PajurFavoravel = true\r\n"
			+ "and c.PreAprovadoComite = true and c.documentosComite = false and status = 'Pendente'";
	
	@SuppressWarnings("unchecked")
	public int getQuantidadeContratosDocumentosComite() {
		return (int) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				int object = 0;

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = getConnection();
					String query = QUERY_QUANTIDADE_CONTRATOS_DOCUMENTOS_COMITE;
					ps = connection.prepareStatement(query);					
					rs = ps.executeQuery();

					ContratoCobranca contrato = new ContratoCobranca();
					List<String> listaContratos = new ArrayList<String>();

					while (rs.next()) {	
						listaContratos.add(rs.getString("numerocontrato"));
					}
					
					object = listaContratos.size();

				} finally {
					closeResources(connection, ps, rs);
				}
				return object;
			}
		});
	}

	private static final String QUERY_CONTRATOS_GET_STATUS = "select c.id, ccbPronta, agAssinatura, agRegistro, pajurFavoravel, laudoRecebido, cadastroAprovadoValor, preaprovadocomite, documentosComite, analiseComercial, comentarioJuridicoEsteira, aprovadocomite, valorPreLaudo "
			 + " from cobranca.contratocobranca c "  
			 + " where c.id = ?";
	
	@SuppressWarnings("unchecked")
	public ContratoCobrancaStatus consultaStatusContratos(final long idContrato) {
		return (ContratoCobrancaStatus) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				ContratoCobrancaStatus contratoCobrancaStatus = new ContratoCobrancaStatus();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_GET_STATUS;
					
					ps = connection
							.prepareStatement(query);
					
					ps.setLong(1, idContrato);
					
					rs = ps.executeQuery();
					
					while (rs.next()) {									
						contratoCobrancaStatus.setCcbPronta(rs.getBoolean(2));
						contratoCobrancaStatus.setAgRegistro(rs.getBoolean(4));
						contratoCobrancaStatus.setAgAssinatura(rs.getBoolean(3));
						contratoCobrancaStatus.setPajuFavoravel(rs.getBoolean(5));
						contratoCobrancaStatus.setLaudoRecebido(rs.getBoolean(6));
						contratoCobrancaStatus.setContratoPreAprovado(rs.getString(7));	
						contratoCobrancaStatus.setPreAprovadoComite(rs.getBoolean(8));
						contratoCobrancaStatus.setDocumentosComite(rs.getBoolean(9));
						contratoCobrancaStatus.setAnaliseComercial(rs.getBoolean(10));
						contratoCobrancaStatus.setComentarioJuridicoEsteira(rs.getBoolean(11));
						contratoCobrancaStatus.setAprovadoComite(rs.getBoolean(12));
						contratoCobrancaStatus.setValorPreLaudo(rs.getBigDecimal(13)); 
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return contratoCobrancaStatus;
			}
		});	
	}
	
	private static final String QUERY_CONTRATOS_MONEY_PLUS = "select c.id "
			 + " from cobranca.contratocobranca c "  
			 + " where c.numerocontrato = ? and c.codigoPropostaMoneyPlus = ? ";
	
	@SuppressWarnings("unchecked")
	public ContratoCobranca getContratoPropostaMoneyPlus(final String numeroContrato, final String codigoPropostaMoneyPlus) {
		return (ContratoCobranca) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				ContratoCobranca contratoCobranca = new ContratoCobranca();
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();

					String query = QUERY_CONTRATOS_MONEY_PLUS;
					
					ps = connection
							.prepareStatement(query);
					
					ps.setString(1, numeroContrato);
					ps.setString(2, codigoPropostaMoneyPlus);
					
					rs = ps.executeQuery();
					
					while (rs.next()) {									
						contratoCobranca = cDao.findById(rs.getLong(1));
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return contratoCobranca;
			}
		});	
	}
	
	private static final String QUERY_PARCELA_BY_CONTRATO_NUMEROPARCELA = " select coco.numerocontrato, ccd.id, ccd.numeroparcela, ccd.vlrSaldoParcela , ccd.vlrsaldoinicial "
			+ " from cobranca.contratocobranca coco "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id  "
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes "
			+ " where coco.numerocontrato = ? "
			+ " AND ccd.numeroparcela = ? "
			+ " order by coco.numerocontrato ";
	
	@SuppressWarnings("unchecked")
	public BigDecimal getSaldoDevedorByContratoNumeroParcela(final String numeroContrato, final String numeroParcela) {
		return (BigDecimal) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;		
				BigDecimal saldoDevedor = BigDecimal.ZERO;
				try {
					connection = getConnection();

					String query = QUERY_PARCELA_BY_CONTRATO_NUMEROPARCELA;
					
					ps = connection.prepareStatement(query);
					
					ps.setString(1, numeroContrato);
					ps.setString(2, numeroParcela);
					
					rs = ps.executeQuery();
					
					saldoDevedor = BigDecimal.ZERO;
					
					while (rs.next()) {									
						saldoDevedor = rs.getBigDecimal("vlrSaldoParcela");
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return saldoDevedor;
			}
		});	
	}
	
	private static final String QUERY_CONSULTA_CONTRATOS_CCBS =  "select cc.id, cc.numerocontrato, cc.numerocontratoseguro, pare.nome "
			+ " from cobranca.contratocobranca cc"
			+ " inner join cobranca.pagadorrecebedor pare on cc.pagador = pare.id  ";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> consultaContratosCCBs() {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONSULTA_CONTRATOS_CCBS);
	
					rs = ps.executeQuery();
					while (rs.next()) {
						ContratoCobranca contratoCobranca = new ContratoCobranca();
						contratoCobranca.setId(rs.getLong("id"));
						contratoCobranca.setNumeroContrato(rs.getString("numerocontrato"));
						contratoCobranca.setNumeroContratoSeguro(rs.getString("numerocontratoseguro"));
						contratoCobranca.setPagador(new PagadorRecebedor());
						contratoCobranca.getPagador().setNome(rs.getString("nome"));
						objects.add(contratoCobranca);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
	
	private static final String QUERY_CONSULTA_CONTRATOS_A_SEREM_BAIXADOS =  " select "
			+ "	c.id, ContratoResgatadoBaixar, ContratoResgatadoData"
			+ " FROM "
			+ "	cobranca.contratocobranca c "
			+ " WHERE "
			+ "	status != 'Aprovado' "
			+ "	AND status != 'Baixado' "
			+ "	AND status != 'Desistência Cliente' "
			+ "	AND analiseReprovada = FALSE "
			+ "	AND c.statusLead = 'Completo' "
			+ " and agassinatura = true "
			+ "	and (DATE_PART('day', ? ::timestamp - c.dataultimaatualizacao) > 30 "
			+ "	or dataultimaatualizacao is null)";
	
	@SuppressWarnings("unchecked")
	public List<ContratoCobranca> ConsultaContratosASeremBaixados(final Date dataInicio) {
		return (List<ContratoCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ContratoCobranca> objects = new ArrayList<ContratoCobranca>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				TimeZone zone = TimeZone.getDefault();
				Locale locale = new Locale("pt", "BR");
				Calendar dataHoje = Calendar.getInstance(zone, locale);
				Date auxDataHoje = dataHoje.getTime();
				try {
					connection = getConnection();
					ps = connection.prepareStatement(QUERY_CONSULTA_CONTRATOS_A_SEREM_BAIXADOS);
					
					java.sql.Date dtRelInicioSQL = new java.sql.Date(dataInicio.getTime());
					ps.setDate(1, dtRelInicioSQL);
					
					rs = ps.executeQuery();
					while (rs.next()) {
						ContratoCobranca contratoCobranca = new ContratoCobranca();
						Date data = rs.getDate("ContratoResgatadoData");
						boolean baixar = true;
						if (rs.getBoolean("ContratoResgatadoBaixar")) {
							if (getDifferenceDays(data, auxDataHoje) <= 30) {
								baixar = false;
							}
						}
						
						if(baixar) {
							contratoCobranca.setId(rs.getLong("id"));
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
	
	public static long getDifferenceDays(Date d1, Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	    return diff;
	}
	
}
