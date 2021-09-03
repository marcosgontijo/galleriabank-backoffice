package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.seguro.mb.SeguroTabelaMB;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;

public class SeguradoDAO extends HibernateDao <Segurado,Long> {
	
	
	private static final String QUERY_SEGURDOS_DFI = "select coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.nome, segu.porcentagemsegurador, " +
		      "coco.qtdeparcelas, count( ccd.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep  " +
		       "from cobranca.contratocobranca coco " +
		       "inner join cobranca.segurado segu on coco.id = segu.contratocobranca " +
		       "inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id " +
		       "inner join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id " +
		       "left join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false " +
		       "where coco.temsegurodfi = true and to_char(ccd.dataVencimento, 'YYYYMM') = ? " +
		       "group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.nome, segu.porcentagemsegurador, " +
		       "coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep " ;
	
	private static final String QUERY_SEGURDOS_MIP = "select coco.numerocontrato,  datacontrato, numerocontratoseguro, sum(ccd.vlrparcela) saldodevedor, pare.cpf, pare.nome, \r\n"
			+ "	segu.porcentagemsegurador, coco.qtdeparcelas, count( ccd.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, \r\n"
			+ "	pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, pare.dtnascimento, pare.sexo \r\n"
			+ "from cobranca.contratocobranca coco\r\n"
			+ "inner join cobranca.segurado segu on coco.id = segu.contratocobranca\r\n"
			+ "inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id\r\n"
			+ "inner join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id\r\n"
			+ "left join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false\r\n"
			+ "where coco.temseguromip = true  and to_char(ccd.dataVencimento, 'YYYYMM') = ? \r\n"
			+ "group by coco.numerocontrato,  datacontrato, numerocontratoseguro, pare.cpf, pare.nome, segu.porcentagemsegurador,\r\n"
			+ "coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, \r\n"
			+ "pare.dtnascimento, pare.sexo" ;
	
	@SuppressWarnings("unchecked")
	public List<SeguroTabelaVO> listaSeguradosDFI(final long idContrato, Date dataDesagio) {
		return (List<SeguroTabelaVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<SeguroTabelaVO> objects = new ArrayList<SeguroTabelaVO>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_SEGURDOS_DFI);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					ps.setString(1, sdf.format(dataDesagio));

					rs = ps.executeQuery();
					
					String numeroContratoSeguroantigo = "";
					SeguroTabelaVO seguroTabelaVO = null;
										
					while (rs.next()) {	
						if (!CommonsUtil.mesmoValor(rs.getString("numerocontratoseguro"), numeroContratoSeguroantigo)){
							if(seguroTabelaVO != null) {
								objects.add(seguroTabelaVO);
							}
							seguroTabelaVO = new SeguroTabelaVO();
							
							seguroTabelaVO.setDataContrato(rs.getDate("datacontrato"));
							
							if(CommonsUtil.compare(rs.getDate("datacontrato").getMonth(), dataDesagio.getMonth()) == 0 && 
									CommonsUtil.compare(rs.getDate("datacontrato").getYear(), dataDesagio.getYear()) == 0 ) {
								seguroTabelaVO.setCodigoSegurado("01");
							} else {
								seguroTabelaVO.setCodigoSegurado("02");
							}
							
							seguroTabelaVO.setNumeroContratoSeguro(rs.getString("numerocontratoseguro"));
							seguroTabelaVO.setAvaliacao(rs.getBigDecimal("valorimovel"));
							seguroTabelaVO.setParcelasOriginais(rs.getString("qtdeparcelas"));
							seguroTabelaVO.setParcelasFaltantes(rs.getString("qtdeparcelasFaltantes"));
							seguroTabelaVO.setPorcentagemPrincipal(rs.getBigDecimal("porcentagemsegurador"));
							seguroTabelaVO.setCpfPrincipal(rs.getString("cpf"));
							seguroTabelaVO.setNomePrincipal(rs.getString("nome"));
							seguroTabelaVO.setLogradouro(rs.getString("endereco"));
							seguroTabelaVO.setNumeroResidencia(rs.getString("numero"));
							seguroTabelaVO.setComplemento(rs.getString("complemento"));
							seguroTabelaVO.setBairro(rs.getString("bairro"));
							seguroTabelaVO.setCidade(rs.getString("cidade"));
							seguroTabelaVO.setUf(rs.getString("estado"));
							seguroTabelaVO.setCep(rs.getString("cep"));						
							numeroContratoSeguroantigo = (rs.getString("numerocontratoseguro"));						
						} else if (CommonsUtil.semValor(seguroTabelaVO.getPorcentagem2()) && CommonsUtil.semValor(seguroTabelaVO.getCpf2()) && CommonsUtil.semValor(seguroTabelaVO.getNome2())) {
							seguroTabelaVO.setPorcentagem2(rs.getBigDecimal("porcentagemsegurador"));
							seguroTabelaVO.setCpf2(rs.getString("cpf"));
							seguroTabelaVO.setNome2(rs.getString("nome"));							
						}  else if (CommonsUtil.semValor(seguroTabelaVO.getPorcentagem3()) && CommonsUtil.semValor(seguroTabelaVO.getCpf3()) && CommonsUtil.semValor(seguroTabelaVO.getNome3())) {
							seguroTabelaVO.setPorcentagem3(rs.getBigDecimal("porcentagemsegurador"));
							seguroTabelaVO.setCpf3(rs.getString("cpf"));
							seguroTabelaVO.setNome3(rs.getString("nome"));							
						} else {
							seguroTabelaVO.setPorcentagem4(rs.getBigDecimal("porcentagemsegurador"));
							seguroTabelaVO.setCpf4(rs.getString("cpf"));
							seguroTabelaVO.setNome4(rs.getString("nome"));
						}
																		
					}
					if(seguroTabelaVO != null) {
						objects.add(seguroTabelaVO);
					}
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
				
				
			}
		});	
	}
	
	@SuppressWarnings("unchecked")
	public List<SeguroTabelaVO> listaSeguradosMIP(final long idContrato,  Date dataDesagio) {
		return (List<SeguroTabelaVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<SeguroTabelaVO> objects = new ArrayList<SeguroTabelaVO>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_SEGURDOS_MIP);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					ps.setString(1, sdf.format(dataDesagio));
					
					rs = ps.executeQuery();
					
					while (rs.next()) {
						SeguroTabelaVO seguroTabelaVO = new SeguroTabelaVO();
						seguroTabelaVO.setDataContrato(rs.getDate("datacontrato"));
						
						if(CommonsUtil.compare(rs.getDate("datacontrato").getMonth(), dataDesagio.getMonth()) == 0 && 
								CommonsUtil.compare(rs.getDate("datacontrato").getYear(), dataDesagio.getYear()) == 0 ) {
							seguroTabelaVO.setCodigoSegurado("01");
							
						} else {
							seguroTabelaVO.setCodigoSegurado("02");
						}
						
						seguroTabelaVO.setSaldoDevedor(rs.getBigDecimal("saldodevedor"));
						seguroTabelaVO.setcpfPrincipal(rs.getString("cpf"));
						seguroTabelaVO.setNomePrincipal(rs.getString("nome"));
						seguroTabelaVO.setParcelasOriginais(rs.getString("qtdeparcelas"));
						seguroTabelaVO.setParcelasFaltantes(rs.getString("qtdeparcelasFaltantes"));
						seguroTabelaVO.setPorcentagemPrincipal(rs.getBigDecimal("porcentagemsegurador"));
						seguroTabelaVO.setNumeroContratoSeguro(rs.getString("numerocontratoseguro"));
						seguroTabelaVO.setLogradouro(rs.getString("endereco"));
						seguroTabelaVO.setNumeroResidencia(rs.getString("numero"));
						seguroTabelaVO.setComplemento(rs.getString("complemento"));
						seguroTabelaVO.setBairro(rs.getString("bairro"));
						seguroTabelaVO.setCidade(rs.getString("cidade"));
						seguroTabelaVO.setUf(rs.getString("estado"));
						seguroTabelaVO.setCep(rs.getString("cep"));
						seguroTabelaVO.setDataNascimento(rs.getString("dtnascimento"));
						seguroTabelaVO.setSexo(rs.getString("sexo"));
						objects.add(seguroTabelaVO);												
					}
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
}


