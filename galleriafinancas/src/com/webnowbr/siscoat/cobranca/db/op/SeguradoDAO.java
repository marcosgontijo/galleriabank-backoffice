package com.webnowbr.siscoat.cobranca.db.op;

import java.math.BigDecimal;
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
	
	
	private static final String QUERY_SEGURADOS_DFI = " select coco.numerocontrato, datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, coco.qtdeparcelas - count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep "
			+ " from cobranca.contratocobranca coco "
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca "
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and to_char(ccd.dataVencimento, 'YYYYMM') = ? "
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = true and ccd1.numeroparcela not in('0', 'Acerto Saldo', 'Amortização') "
			+ " where coco.temsegurodfi = true and ( ccd.id is not null or  to_char(coco.datacontrato, 'YYYYMM') = ? ) "
			+ " group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, segu.posicao "
			+ " order by coco.numerocontrato asc, segu.posicao asc, segu.porcentagemsegurador desc, pare.nome asc " ;
	
	private static final String QUERY_SEGURADOS_DFI_EMPRESA = " select coco.numerocontrato, datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, coco.qtdeparcelas - count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep "
			+ " from cobranca.contratocobranca coco "
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca "
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and to_char(ccd.dataVencimento, 'YYYYMM') = ? "
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = true and ccd1.numeroparcela not in('0', 'Acerto Saldo', 'Amortização') "
			+ " where coco.temsegurodfi = true and ( ccd.id is not null or to_char(coco.datacontrato, 'YYYYMM') = ?) "
			+ " and coco.empresa = ? "
			+ " group by coco.numerocontrato,  datacontrato, numerocontratoseguro, valorimovel, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, segu.posicao "
			+ " order by coco.numerocontrato asc, segu.posicao asc, segu.porcentagemsegurador desc, pare.nome asc " ;
	
	private static final String QUERY_SEGURADOS_MIP = " select coco.numerocontrato, ccd.numeroparcela, datacontrato, numerocontratoseguro, ccd.vlrSaldoInicial saldodevedor, coco.valorccb, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, coco.qtdeparcelas - count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, pare.dtnascimento, pare.sexo "
			+ " from cobranca.contratocobranca coco "
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca "
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and to_char(ccd.dataVencimento, 'YYYYMM') = ? "
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = true and ccd1.numeroparcela not in('0', 'Acerto Saldo', 'Amortização') "
			+ " where coco.temseguromip = true and (  ccd.id is not null or to_char(coco.datacontrato, 'YYYYMM') = ? ) "
			+ " group by coco.numerocontrato, ccd.numeroparcela, datacontrato, numerocontratoseguro, saldodevedor, valorccb, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, "
			+ " pare.dtnascimento, pare.sexo , segu.posicao "
			+ " order by coco.numerocontrato asc, segu.posicao asc, segu.porcentagemsegurador desc, pare.nome asc ";
	
	private static final String QUERY_SEGURADOS_MIP_EMPRESA = " select coco.numerocontrato, ccd.numeroparcela, datacontrato, numerocontratoseguro, ccd.vlrSaldoInicial saldodevedor, coco.valorccb, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, coco.qtdeparcelas - count( ccd1.id  ) qtdeparcelasFaltantes, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, pare.dtnascimento, pare.sexo "
			+ " from cobranca.contratocobranca coco "
			+ " inner join cobranca.segurado segu on coco.id = segu.contratocobranca "
			+ " inner join cobranca.pagadorrecebedor pare on segu.pessoa = pare.id "
			+ " left join cobranca.contratocobranca_detalhes_join ccdj ON ccdj.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd ON ccd.id = ccdj.idcontratocobrancadetalhes and to_char(ccd.dataVencimento, 'YYYYMM') = ? "
			+ " inner join cobranca.contratocobranca_detalhes_join ccdj1 ON ccdj1.idcontratocobranca = coco.id "
			+ " left join cobranca.contratocobrancadetalhes ccd1 ON ccd1.id = ccdj1.idcontratocobrancadetalhes and ccd1.parcelapaga = true and ccd1.numeroparcela not in('0', 'Acerto Saldo', 'Amortização') "
			+ " where coco.temseguromip = true and ( ccd.id is not null or to_char(coco.datacontrato, 'YYYYMM') = ? ) "
			+ " and coco.empresa = ? "
			+ " group by coco.numerocontrato, ccd.numeroparcela, datacontrato, numerocontratoseguro, saldodevedor, valorccb, pare.cpf, pare.cnpj, pare.nome, segu.porcentagemsegurador, "
			+ " coco.qtdeparcelas, pare.endereco, pare.numero, pare.complemento, pare.bairro, pare.cidade, pare.estado, pare.cep, "
			+ " pare.dtnascimento, pare.sexo, segu.posicao "
			+ " order by coco.numerocontrato asc, segu.posicao asc, segu.porcentagemsegurador desc, pare.nome asc ";
	
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
					ps.setString(2, sdf.format(dataDesagio));
					
					if (!CommonsUtil.mesmoValor(empresa, "Todas") ) {
						ps.setString(3, empresa);
					}

					rs = ps.executeQuery();
					
					String numeroContratoantigo = "numeroContratoSeguroantigo";
					SeguroTabelaVO seguroTabelaVO = null;
					double porcentagemtotal = 0;
										
					while (rs.next()) {	
						if (!CommonsUtil.mesmoValor(rs.getString("numerocontrato"), numeroContratoantigo)){
							seguroTabelaVO = new SeguroTabelaVO();
							
							seguroTabelaVO.setDataContrato(rs.getDate("datacontrato"));
							
							if(CommonsUtil.compare(rs.getDate("datacontrato").getMonth(), dataDesagio.getMonth()) == 0 && 
									CommonsUtil.compare(rs.getDate("datacontrato").getYear(), dataDesagio.getYear()) == 0 ) {
								seguroTabelaVO.setCodigoSegurado("01");
							} else if(CommonsUtil.mesmoValor(CommonsUtil.bigDecimalValue(rs.getString("qtdeparcelasFaltantes")), BigDecimal.ZERO)){
								seguroTabelaVO.setCodigoSegurado("03");
							} else {
								seguroTabelaVO.setCodigoSegurado("02");
							}
							seguroTabelaVO.setNumeroContrato(rs.getString("numerocontrato"));
							seguroTabelaVO.setNumeroContratoSeguro(rs.getString("numerocontratoseguro"));
							seguroTabelaVO.setAvaliacao(rs.getBigDecimal("valorimovel")); 
							seguroTabelaVO.setParcelasOriginais(rs.getString("qtdeparcelas"));
							seguroTabelaVO.setParcelasFaltantes(rs.getString("qtdeparcelasFaltantes"));
							Integer parcelasFaltantesint = CommonsUtil.integerValue(seguroTabelaVO.getParcelasFaltantes());
							seguroTabelaVO.setParcelasFaltantes(CommonsUtil.stringValue(parcelasFaltantesint));
							seguroTabelaVO.setPorcentagemPrincipal(rs.getBigDecimal("porcentagemsegurador"));
							porcentagemtotal = seguroTabelaVO.getPorcentagemPrincipal().doubleValue();
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
							numeroContratoantigo = (rs.getString("numerocontrato"));
							if(CommonsUtil.mesmoValor(porcentagemtotal, CommonsUtil.doubleValue(100))) {
								objects.add(seguroTabelaVO);
							}
							
						} else if (seguroTabelaVO != null && CommonsUtil.semValor(seguroTabelaVO.getPorcentagem2()) && CommonsUtil.semValor(seguroTabelaVO.getCpf2()) && CommonsUtil.semValor(seguroTabelaVO.getNome2())) {
							seguroTabelaVO.setPorcentagem2(rs.getBigDecimal("porcentagemsegurador"));							
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf2(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf2(rs.getString("cnpj"));
							}
							seguroTabelaVO.setNome2(rs.getString("nome"));			
							porcentagemtotal += seguroTabelaVO.getPorcentagem2().doubleValue();
							if(CommonsUtil.mesmoValor(porcentagemtotal, CommonsUtil.doubleValue(100))) {
								objects.add(seguroTabelaVO);
							}
						}  else if (seguroTabelaVO != null && CommonsUtil.semValor(seguroTabelaVO.getPorcentagem3()) && CommonsUtil.semValor(seguroTabelaVO.getCpf3()) && CommonsUtil.semValor(seguroTabelaVO.getNome3())) {
							seguroTabelaVO.setPorcentagem3(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf3(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf3(rs.getString("cnpj"));
							}							
							seguroTabelaVO.setNome3(rs.getString("nome"));		
							porcentagemtotal += seguroTabelaVO.getPorcentagem3().doubleValue();
							if(CommonsUtil.mesmoValor(porcentagemtotal, CommonsUtil.doubleValue(100))) {
								objects.add(seguroTabelaVO);
							}
						} else if (seguroTabelaVO != null ) {
							seguroTabelaVO.setPorcentagem4(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf4(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf4(rs.getString("cnpj"));
							}
							seguroTabelaVO.setNome4(rs.getString("nome"));
							objects.add(seguroTabelaVO);
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
	public List<SeguroTabelaVO> listaSeguradosMIP(final long idContrato,  Date dataDesagio, String empresa) {
		return (List<SeguroTabelaVO>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Collection<SeguroTabelaVO> objects = new ArrayList<SeguroTabelaVO>();
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
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
							ps.setString(3, "GALLERIA FINANÇAS SECURITIZADORA S.A.");
						}else {
							ps.setString(3, empresa);
						}
						
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					String dataDesagioSdf = sdf.format(dataDesagio);
					
					ps.setString(1, dataDesagioSdf);
					ps.setString(2, dataDesagioSdf);
					
					rs = ps.executeQuery();
					
					String numeroContratoAntigo = "numeroContratoAntigo";
					SeguroTabelaVO seguroTabelaVO = null;
					double porcentagemtotal = 0;
										
					while (rs.next()) {	
						if (!CommonsUtil.mesmoValor(rs.getString("numerocontrato"), numeroContratoAntigo)){
							seguroTabelaVO = new SeguroTabelaVO();
							seguroTabelaVO.setDataContrato(rs.getDate("datacontrato"));
							
							if(CommonsUtil.compare(rs.getDate("datacontrato").getMonth(), dataDesagio.getMonth()) == 0 && 
									CommonsUtil.compare(rs.getDate("datacontrato").getYear(), dataDesagio.getYear()) == 0 ) {
								seguroTabelaVO.setCodigoSegurado("01");
							} else if(CommonsUtil.mesmoValor(CommonsUtil.bigDecimalValue(rs.getString("qtdeparcelasFaltantes")), BigDecimal.ZERO)){
								seguroTabelaVO.setCodigoSegurado("03");
							} else {
								seguroTabelaVO.setCodigoSegurado("02");
							}
							
							String numeroparcela = rs.getString("numeroparcela");
							
							if(CommonsUtil.semValor(rs.getBigDecimal("saldodevedor"))) {
								if(!CommonsUtil.mesmoValor(numeroparcela, "Amortização") &&
										!CommonsUtil.mesmoValor(rs.getString("numeroParcela"), "Acerto Saldo")) {
									String numeroparcelaAnterior = CommonsUtil.stringValue((CommonsUtil.integerValue(numeroparcela) - 1));
									seguroTabelaVO.setSaldoDevedor(cDao.getSaldoDevedorByContratoNumeroParcela(rs.getString("numerocontrato"), numeroparcelaAnterior));
									if(CommonsUtil.semValor(seguroTabelaVO.getSaldoDevedor())) {
										seguroTabelaVO.setSaldoDevedor(rs.getBigDecimal("valorccb"));
									}
								}
							} else {
								seguroTabelaVO.setSaldoDevedor(rs.getBigDecimal("saldodevedor"));
							}

							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpfPrincipal(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpfPrincipal(rs.getString("cnpj"));
							}
							
							seguroTabelaVO.setNumeroContrato(rs.getString("numerocontrato"));
							seguroTabelaVO.setNomePrincipal(rs.getString("nome"));
							seguroTabelaVO.setParcelasOriginais(rs.getString("qtdeparcelas"));
							seguroTabelaVO.setParcelasFaltantes(rs.getString("qtdeparcelasFaltantes"));
							Integer parcelasFaltantesint = CommonsUtil.integerValue(seguroTabelaVO.getParcelasFaltantes());
							seguroTabelaVO.setParcelasFaltantes(CommonsUtil.stringValue(parcelasFaltantesint));
							seguroTabelaVO.setPorcentagemPrincipal(rs.getBigDecimal("porcentagemsegurador"));
							porcentagemtotal = seguroTabelaVO.getPorcentagemPrincipal().doubleValue();
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
							numeroContratoAntigo = (rs.getString("numerocontrato"));
							if(CommonsUtil.mesmoValor(porcentagemtotal, CommonsUtil.doubleValue(100))) {
								objects.add(seguroTabelaVO);
							}
							
						} else if (seguroTabelaVO != null && CommonsUtil.semValor(seguroTabelaVO.getPorcentagem2()) && CommonsUtil.semValor(seguroTabelaVO.getCpf2()) && CommonsUtil.semValor(seguroTabelaVO.getNome2())) {
							seguroTabelaVO.setPorcentagem2(rs.getBigDecimal("porcentagemsegurador"));							
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf2(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf2(rs.getString("cnpj"));
							}
							seguroTabelaVO.setNome2(rs.getString("nome"));
							seguroTabelaVO.setDataNascimento2(rs.getString("dtnascimento"));
							seguroTabelaVO.setSexo2(rs.getString("sexo"));
							porcentagemtotal += seguroTabelaVO.getPorcentagem2().doubleValue();
							if(CommonsUtil.mesmoValor(porcentagemtotal, CommonsUtil.doubleValue(100))) {
								objects.add(seguroTabelaVO);
							}
						}  else if (seguroTabelaVO != null && CommonsUtil.semValor(seguroTabelaVO.getPorcentagem3()) && CommonsUtil.semValor(seguroTabelaVO.getCpf3()) && CommonsUtil.semValor(seguroTabelaVO.getNome3())) {
							seguroTabelaVO.setPorcentagem3(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf3(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf3(rs.getString("cnpj"));
							}							
							seguroTabelaVO.setNome3(rs.getString("nome"));
							seguroTabelaVO.setDataNascimento3(rs.getString("dtnascimento"));
							seguroTabelaVO.setSexo3(rs.getString("sexo"));
							porcentagemtotal += seguroTabelaVO.getPorcentagem3().doubleValue();
							if(CommonsUtil.mesmoValor(porcentagemtotal, CommonsUtil.doubleValue(100))) {
								objects.add(seguroTabelaVO);
							}
						} else if (seguroTabelaVO != null ) {
							seguroTabelaVO.setPorcentagem4(rs.getBigDecimal("porcentagemsegurador"));
							if ( !CommonsUtil.semValor(rs.getString("cpf")) ) {
								seguroTabelaVO.setCpf4(rs.getString("cpf"));
							}else {
								seguroTabelaVO.setCpf4(rs.getString("cnpj"));
							}
							seguroTabelaVO.setNome4(rs.getString("nome"));
							seguroTabelaVO.setDataNascimento4(rs.getString("dtnascimento"));
							seguroTabelaVO.setSexo4(rs.getString("sexo"));
							objects.add(seguroTabelaVO);
						}
					}
					
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});	
	}	
}


