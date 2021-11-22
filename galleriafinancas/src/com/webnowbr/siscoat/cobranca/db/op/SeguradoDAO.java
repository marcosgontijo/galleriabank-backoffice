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
	
	
	private static final String QUERY_SEGURADOS_DFI = " select coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador,\r\n"
			+ " coco.qtdeparcelas, count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep \r\n"
			+ " from cobranca.contratocobranca coco \r\n"
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca \r\n"
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id \r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id \r\n"
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false\r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id \r\n"
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = false\r\n"
			+ " where coco.temsegurodfi = true and to_char(ccd.dataVencimento, 'YYYYMM') = ? \r\n"
			+ " group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, \r\n"
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep " ;
	
	private static final String QUERY_SEGURADOS_DFI_EMPRESA = " select coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador,\r\n"
			+ " coco.qtdeparcelas, count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep \r\n"
			+ " from cobranca.contratocobranca coco \r\n"
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca \r\n"
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id \r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id \r\n"
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false\r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id \r\n"
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = false\r\n"
			+ " where coco.temsegurodfi = true and to_char(ccd.dataVencimento, 'YYYYMM') = ? \r\n"
			+ " and coco.empresa = ? \r\n"
			+ " group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, \r\n"
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep;" ;
	
	private static final String QUERY_SEGURADOS_MIP = " select coco.numerocontrato,  datacontrato, numerocontratoseguro, sum(ccd.vlrparcela) saldodevedor, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador,\r\n"
			+ " coco.qtdeparcelas, count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, pare.dtnascimento, pare.sexo \r\n"
			+ " from cobranca.contratocobranca coco \r\n"
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca \r\n"
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id \r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id \r\n"
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false\r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id \r\n"
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = false\r\n"
			+ " where coco.temseguromip = true and to_char(ccd.dataVencimento, 'YYYYMM') = ? \r\n"
			+ " group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, \r\n"
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, \r\n"
			+ " pare.dtnascimento, pare.sexo ";
	
	private static final String QUERY_SEGURADOS_MIP_EMPRESA = " select coco.numerocontrato,  datacontrato, numerocontratoseguro, sum(ccd.vlrparcela) saldodevedor, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador,\r\n"
			+ " coco.qtdeparcelas, count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, pare.dtnascimento, pare.sexo \r\n"
			+ " from cobranca.contratocobranca coco \r\n"
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca \r\n"
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id \r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id \r\n"
			+ " inner join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and ccd.parcelapaga = false\r\n"
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id \r\n"
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = false\r\n"
			+ " where coco.temseguromip = true and to_char(ccd.dataVencimento, 'YYYYMM') = ? \r\n"
			+ " and coco.empresa = ? \r\n"
			+ " group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, \r\n"
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, \r\n"
			+ " pare.dtnascimento, pare.sexo ";
	
	@SuppressWarnings("unchecked")
	public List<SeguroTabelaVO> listaSeguradosDFI(final long idContrato, Date dataDesagio, String empresa) {
		return (List<SeguroTabelaVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<SeguroTabelaVO> objects = new ArrayList<SeguroTabelaVO>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					
					if (CommonsUtil.mesmoValor(empresa, "Todas") ) {
						ps = connection.prepareStatement(QUERY_SEGURADOS_DFI);
					} else {
						ps = connection.prepareStatement(QUERY_SEGURADOS_DFI_EMPRESA);
					
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					ps.setString(1, sdf.format(dataDesagio));
					
					if (!CommonsUtil.mesmoValor(empresa, "Todas") ) {
						ps.setString(2, empresa);
					}

					rs = ps.executeQuery();
					
					String numeroContratoSeguroantigo = "numeroContratoSeguroantigo";
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
							Integer parcelasFaltantesint = CommonsUtil.integerValue(seguroTabelaVO.getParcelasFaltantes());
							seguroTabelaVO.setParcelasFaltantes(CommonsUtil.stringValue(parcelasFaltantesint));
							seguroTabelaVO.setPorcentagemPrincipal(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpfPrincipal(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpfPrincipal(rs.getString("cnpj"));
							}
							seguroTabelaVO.setNomePrincipal(rs.getString("nome"));
							seguroTabelaVO.setLogradouro(rs.getString("endereco"));
							seguroTabelaVO.setNumeroResidencia(rs.getString("numero"));
							seguroTabelaVO.setComplemento(rs.getString("complemento"));
							seguroTabelaVO.setBairro(rs.getString("bairro"));
							seguroTabelaVO.setCidade(rs.getString("cidade"));
							seguroTabelaVO.setUf(rs.getString("estado"));
							seguroTabelaVO.setCep(rs.getString("cep"));						
							numeroContratoSeguroantigo = (rs.getString("numerocontratoseguro"));
							
						} else if (seguroTabelaVO != null && CommonsUtil.semValor(seguroTabelaVO.getPorcentagem2()) && CommonsUtil.semValor(seguroTabelaVO.getCpf2()) && CommonsUtil.semValor(seguroTabelaVO.getNome2())) {
							seguroTabelaVO.setPorcentagem2(rs.getBigDecimal("porcentagemsegurador"));							
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf2(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf2(rs.getString("cnpj"));
							}
							seguroTabelaVO.setNome2(rs.getString("nome"));							
						}  else if (seguroTabelaVO != null && CommonsUtil.semValor(seguroTabelaVO.getPorcentagem3()) && CommonsUtil.semValor(seguroTabelaVO.getCpf3()) && CommonsUtil.semValor(seguroTabelaVO.getNome3())) {
							seguroTabelaVO.setPorcentagem3(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf3(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf3(rs.getString("cnpj"));
							}							
							seguroTabelaVO.setNome3(rs.getString("nome"));							
						} else if (seguroTabelaVO != null ) {
							seguroTabelaVO.setPorcentagem4(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf4(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf4(rs.getString("cnpj"));
							}
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
	public List<SeguroTabelaVO> listaSeguradosMIP(final long idContrato,  Date dataDesagio, String empresa) {
		return (List<SeguroTabelaVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<SeguroTabelaVO> objects = new ArrayList<SeguroTabelaVO>();
	
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;			
				try {
					connection = getConnection();
					

					if (CommonsUtil.mesmoValor(empresa, "Todas") ) {
						ps = connection.prepareStatement(QUERY_SEGURADOS_MIP);
					} else {
						ps = connection.prepareStatement(QUERY_SEGURADOS_MIP_EMPRESA);
						if(CommonsUtil.mesmoValor(empresa, "GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
							ps.setString(2, "GALLERIA FINANÇAS SECURITIZADORA S.A.");
						}else {
							ps.setString(2, empresa);
						}
						
					}
					
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
						Integer parcelasFaltantesint = CommonsUtil.integerValue(seguroTabelaVO.getParcelasFaltantes());
						seguroTabelaVO.setParcelasFaltantes(CommonsUtil.stringValue(parcelasFaltantesint));
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


