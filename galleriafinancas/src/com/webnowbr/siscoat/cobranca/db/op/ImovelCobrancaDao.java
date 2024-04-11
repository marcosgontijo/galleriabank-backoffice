package com.webnowbr.siscoat.cobranca.db.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.ImovelEstoque;
import com.webnowbr.siscoat.db.dao.HibernateDao;
import com.webnowbr.siscoat.db.dao.HibernateDao.DBRunnable;

/**
 * DAO access layer for the Tecnico entity
 * @author hv.junior
 *
 */
public class ImovelCobrancaDao extends HibernateDao <ImovelCobranca,Long> {
		
	private String QUERY_ID_IMOVEL = "select id from reservarimovel.imovel order by nome";
	
    @SuppressWarnings("unchecked")
	public List<ImovelCobranca> relatorioImovelOrdenado() {
		return (List<ImovelCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelCobranca> objects = new ArrayList<ImovelCobranca>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_ID_IMOVEL);
					
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
    
    private String QUERY_LISTA_TODOS_IMOVEIS_LAUDO_CONTRATO = " SELECT DISTINCT i.id, i.numeromatricula, i.nome, i.endereco, i.bairro, i.complemento, i.cidade, i.estado, i.telresidencial"
    														+ " ,i.observacao, i.cep, i.cartorio, i.tipo, i.areatotal, i.areaconstruida, i.linkgmaps, i.possuidivida, i.ocupacao"
    														+ " ,i.valoestimado, i.datacompra, i.idadecompra, i.nomeproprietario, i.comprovantematriculachecklist, i.comprovantefotosimovelchecklist"
    														+ " ,i.comprovanteiptuimovelchecklist, i.valoriptu, i.valorcondominio, i.cartoriomunicipio, i.numerocartorio, i.cartorioestado, i.cndiptuextratodebitochecklist"
    														+ " ,i.cndcondominioextratodebitochecklist, i.matriculagaragemchecklist, i.simuladorchecklist, i.inscricaomunicipal, i.objetocidade, i.imovelestoque, i.valormercado "
    														+ " ,i.preLaudoSolicitado, i.preLaudoEntregue, i.valorPreLaudo "
    														+ " FROM cobranca.imovelcobranca i "
    														+ " INNER JOIN cobranca.contratocobranca c ON i.id = c.imovel "
    														+ " WHERE c.id = ? "
    														+ " AND i.prelaudosolicitado = true "
    														+ " UNION ALL "
    														+ " SELECT DISTINCT i.id, i.numeromatricula, i.nome, i.endereco, i.bairro, i.complemento, i.cidade, i.estado, i.telresidencial"
    														+ " ,i.observacao, i.cep, i.cartorio, i.tipo, i.areatotal, i.areaconstruida, i.linkgmaps, i.possuidivida, i.ocupacao"
    														+ " ,i.valoestimado, i.datacompra, i.idadecompra, i.nomeproprietario, i.comprovantematriculachecklist, i.comprovantefotosimovelchecklist"
    														+ " ,i.comprovanteiptuimovelchecklist, i.valoriptu, i.valorcondominio, i.cartoriomunicipio, i.numerocartorio, i.cartorioestado, i.cndiptuextratodebitochecklist"
    														+ " ,i.cndcondominioextratodebitochecklist, i.matriculagaragemchecklist, i.simuladorchecklist, i.inscricaomunicipal, i.objetocidade, i.imovelestoque, i.valormercado "
    														+ " ,i.preLaudoSolicitado, i.preLaudoEntregue, i.valorPreLaudo "
    														+ " FROM cobranca.imovelcobranca i "
    														+ " INNER JOIN cobranca.imovelcobrancaadicionais ia ON ia.imovel = i.id "
    														+ " WHERE ia.contratocobranca = ? "
    														+ " AND i.prelaudosolicitado = true ";
    
    @SuppressWarnings("unchecked")
	public List<ImovelCobranca> listaTodosImoveisLaudoContrato(Long idCobrancaContrato) {
		return (List<ImovelCobranca>) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				List<ImovelCobranca> objects = new ArrayList<ImovelCobranca>();
				
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				try {
					
					
					String QUERY_LISTA_TODOS_IMOVEIS_LAUDO_CONTRATO_CUSTOM = QUERY_LISTA_TODOS_IMOVEIS_LAUDO_CONTRATO;
					
					connection = getConnection();
					
					ps = connection
							.prepareStatement(QUERY_LISTA_TODOS_IMOVEIS_LAUDO_CONTRATO_CUSTOM);
					
					ps.setLong(1,idCobrancaContrato);
					ps.setLong(2,idCobrancaContrato);
					
					rs = ps.executeQuery();
					
					CidadeDao cidadeDao = new CidadeDao();
					ImovelEstoqueDao imovelEstoqueDao = new ImovelEstoqueDao();
					
					while (rs.next()) {
						ImovelCobranca imovelCobranca = new ImovelCobranca();
						imovelCobranca.setId(rs.getLong(1));
						imovelCobranca.setNumeroMatricula(rs.getString(2));
						imovelCobranca.setNome(rs.getString(3));
						imovelCobranca.setEndereco(rs.getString(4));
						imovelCobranca.setBairro(rs.getString(5));
						imovelCobranca.setComplemento(rs.getString(6));
						imovelCobranca.setCidade(rs.getString(7));
						imovelCobranca.setEstado(rs.getString(8));
						imovelCobranca.setTelResidencial(rs.getString(9));
						imovelCobranca.setObservacao(rs.getString(10));
						imovelCobranca.setCep(rs.getString(11));
						imovelCobranca.setCartorio(rs.getString(12));
						imovelCobranca.setTipo(rs.getString(13));
						imovelCobranca.setAreaTotal(rs.getString(14));
						imovelCobranca.setAreaConstruida(rs.getString(15));
						imovelCobranca.setLinkGMaps(rs.getString(16));
						imovelCobranca.setPossuiDivida(rs.getString(17));
						imovelCobranca.setOcupacao(rs.getString(18));
						imovelCobranca.setValoEstimado(rs.getBigDecimal(19));
						imovelCobranca.setDataCompra(rs.getDate(20));
						imovelCobranca.setIdadeCompra(rs.getString(21));
						imovelCobranca.setNomeProprietario(rs.getString(22));
						imovelCobranca.setComprovanteMatriculaCheckList(rs.getBoolean(23));
						imovelCobranca.setComprovanteFotosImovelCheckList(rs.getBoolean(24));
						imovelCobranca.setComprovanteIptuImovelCheckList(rs.getBoolean(25));
						imovelCobranca.setValorIptu(rs.getBigDecimal(26));
						imovelCobranca.setValorCondominio(rs.getBigDecimal(27));
						imovelCobranca.setCartorioMunicipio(rs.getString(28));
						imovelCobranca.setNumeroCartorio(rs.getString(29));
						imovelCobranca.setCartorioEstado(rs.getString(30));
						imovelCobranca.setCndIptuExtratoDebitoCheckList(rs.getBoolean(31));
						imovelCobranca.setCndCondominioExtratoDebitoCheckList(rs.getBoolean(32));
						imovelCobranca.setMatriculaGaragemCheckList(rs.getBoolean(33));
						imovelCobranca.setSimuladorCheckList(rs.getBoolean(34));
						imovelCobranca.setInscricaoMunicipal(rs.getString(35));
						imovelCobranca.setObjetoCidade(cidadeDao.findById(rs.getLong(36)));
						//imovelCobranca.setImovelEstoque(imovelEstoqueDao.findById(rs.getLong(37)));
						imovelCobranca.setValorMercado(rs.getBigDecimal(38));
						imovelCobranca.setPreLaudoSolicitado(rs.getBoolean(39));
						imovelCobranca.setPreLaudoEntregue(rs.getBoolean(40));
						imovelCobranca.setValorPreLaudo(rs.getBigDecimal(41));
						
						objects.add(imovelCobranca);	
					}
							
				} finally {
					closeResources(connection, ps, rs);					
				}
				return objects;
			}
		});
	}
    
    
    public ImovelCobranca findImovelDao(String numeroMatricula, String numeroCartorio, String cartorioMunicipio){
		return (ImovelCobranca) executeDBOperation(new DBRunnable() {
			@Override
			public Object run() throws Exception {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				ImovelCobranca imovel = new ImovelCobranca();
				try {
					connection = getConnection();
					
					ps = connection
							.prepareStatement("SELECT * FROM cobranca.ImovelCobranca WHERE numeroMatricula = ? AND numeroCartorio = ? AND cartorioMunicipio = ?");
					
					ps.setString(1, numeroMatricula);
					ps.setString(2, numeroCartorio);
			        ps.setString(3, cartorioMunicipio);

					rs = ps.executeQuery();
					
					 if (rs.next()) {
		                    
		                    imovel.setId(rs.getLong("id"));
		                    return imovel;
		                }
	
				} finally {
					closeResources(connection, ps, rs);					
				}
				return imovel;
				
			}
		});	
	}
    
    
}
