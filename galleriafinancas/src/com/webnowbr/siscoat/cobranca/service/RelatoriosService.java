package com.webnowbr.siscoat.cobranca.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;

import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PreAprovadoPDF;
import com.webnowbr.siscoat.cobranca.db.model.PreAprovadoPDFDetalheDespesas;
import com.webnowbr.siscoat.cobranca.db.model.RegistroImovelTabela;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.RegistroImovelTabelaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.ReportUtil;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.simulador.SimulacaoVO;
import com.webnowbr.siscoat.simulador.SimuladorMB;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class RelatoriosService {

	public JasperPrint geraPDFPAprovadoComite(long idContrato) throws JRException, IOException {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		final ReportUtil ReportUtil = new ReportUtil();
		JasperReport rptSimulacao = ReportUtil.getRelatorio("AprovadoComitePDFN");
		InputStream logoStream = getClass().getResourceAsStream("/resource/novoCreditoAprovado2.png");
		InputStream rodapeStream = getClass().getResourceAsStream("/resource/novoCreditoAprovadoRodape.png");
		InputStream barraStream = getClass().getResourceAsStream("/resource/novoCreditoAprovadoBarra.png");
		
		
		JasperReport rptDetalhe = ReportUtil.getRelatorio("AprovadoComitePDFNDetalhe");
		JasperReport rptDetalheParcelas = ReportUtil.getRelatorio("AprovadoComitePDFNParcelas");
		
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

		parameters.put("SUBREPORT_DETALHE_DESPESA", rptDetalhe);
		parameters.put("SUBREPORT_DETALHE_PARCELA", rptDetalheParcelas);
		parameters.put("IMAGEMFUNDO", IOUtils.toByteArray(logoStream));
		parameters.put("IMAGEMRODAPE", IOUtils.toByteArray(rodapeStream));
		parameters.put("IMAGEMBARRA", IOUtils.toByteArray(barraStream));		
		parameters.put("MOSTRARIPCA", true);

		List<PreAprovadoPDF> list = new ArrayList<PreAprovadoPDF>();
		ContratoCobranca con = cDao.findById(idContrato);
		String cpf = "";
		if (!CommonsUtil.semValor(con.getPagador().getCpf())) {
			cpf = con.getPagador().getCpf();
		} else {
			cpf = con.getPagador().getCnpj();
		}
		SimuladorMB simuladorMB = new SimuladorMB();
		simuladorMB.clearFields();
		if (con.getPagador() != null) {
			simuladorMB.setTipoPessoa("PF");
		} else {
			simuladorMB.setTipoPessoa("PJ");
		}
		simuladorMB.setTipoCalculo("Price");
		simuladorMB.setValorImovel(con.getValorMercadoImovel());
		simuladorMB.setValorCredito(con.getValorAprovadoComite());
		simuladorMB.setTaxaJuros(con.getTaxaAprovada());
		simuladorMB.setParcelas(con.getPrazoMaxAprovado());
		simuladorMB.setCarencia(BigInteger.ONE);
		simuladorMB.setNaoCalcularMIP(false);
		simuladorMB.setNaoCalcularDFI(false);
		simuladorMB.setNaoCalcularTxAdm(false);
		simuladorMB.setMostrarIPCA(true);
		simuladorMB.setTipoCalculoFinal(con.getTipoValorComite().toUpperCase().charAt(0));
		simuladorMB.setValidar(false);
		simuladorMB.setSimularComIPCA(false);
		simuladorMB.setIpcaSimulado(BigDecimal.ZERO);
		simuladorMB.simular();
		SimulacaoVO simulador = simuladorMB.getSimulacao();

		BigDecimal parcelaPGTO = simulador.getParcelas().get(2).getValorParcela();
		BigDecimal rendaMinima = parcelaPGTO.divide(BigDecimal.valueOf(0.3), MathContext.DECIMAL128);

		String cep = con.getImovel().getCep();
		String  observacao = con.getProcessosQuitarComite();
		
		BigInteger carencia = BigInteger.ONE.add(CommonsUtil.bigIntegerValue(con.getCarenciaComite()))
				.multiply(CommonsUtil.bigIntegerValue(30));
		BigDecimal despesa = BigDecimal.ZERO;
		
		BigDecimal valorIOF = simulador.getValorTotalIOF().add(simulador.getValorTotalIOFAdicional());
		
		BigDecimal valorLiquido = BigDecimal.ZERO;
		List<PreAprovadoPDFDetalheDespesas> detalhesDespesas = new ArrayList<>();

		detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Debitos de IPTU", "Se houver"));
		List<String> ImoveisComCondominio = Arrays.asList("Casa de Condomínio", "Apartamento", "Terreno de Condomínio", 
				"Terreno", "Sala Comercial");

		if (ImoveisComCondominio.contains(con.getImovel().getTipo()))
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Debitos de Condomínio", "Se houver"));

		RegistroImovelTabelaDao rDao = new RegistroImovelTabelaDao();
		final BigDecimal valorRegistro = rDao.getValorRegistro( simulador.getValorCredito());

		List<RegistroImovelTabela> registroImovelTabela = rDao.findAll();
		Optional<RegistroImovelTabela> registroImovel = registroImovelTabela.stream()
				.sorted((o1, o2) -> o1.getTotal().compareTo(o2.getTotal()))
				.filter(a -> a.getTotal().compareTo(valorRegistro) == 1).findFirst();
		
		if (registroImovel.isPresent()) {
			despesa = despesa.add(registroImovel.get().getTotal());
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Custas Estimada Para Registro",
					CommonsUtil.formataValorMonetario(registroImovel.get().getTotal(), "R$ ")));
		}
		
		BigDecimal valorCustoEmissao = simulador.getCustoEmissaoValor();
//		despesa = despesa.add(valorCustoEmissao);
//		detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Custo de Emissão", 
//				CommonsUtil.formataValorMonetario(valorCustoEmissao, "R$ ")));
		
		if(con.getValorLaudoPajuFaltante().compareTo(BigDecimal.ZERO) > 0) {
			despesa = despesa.add(con.getValorLaudoPajuFaltante());
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Laudo + Parecer Juridico",
				CommonsUtil.formataValorMonetario(con.getValorLaudoPajuFaltante(), "R$ ")));
		}	

		for (CcbProcessosJudiciais processo : con.getListProcessos().stream()
				.filter(p -> p.isSelecionadoComite() && p.getQuitar().contains("Quitar"))
				.collect(Collectors.toList())) {
			
			String retiraObservaco = processo.getNumero() + " - "
			+ CommonsUtil.formataValorMonetario(processo.getValorAtualizado(), "R$ ") + "\n";
			
			observacao = observacao.replace( retiraObservaco, "");
			despesa = despesa.add(processo.getValorAtualizado());
			detalhesDespesas.add(new PreAprovadoPDFDetalheDespesas("Processo Nº " + processo.getNumero(),
					CommonsUtil.formataValorMonetario(processo.getValorAtualizado(), "R$ ")));
		}

		valorLiquido =  simulador.getValorCredito().subtract(valorIOF).subtract(valorCustoEmissao).subtract(despesa);

		// adicionar cep e carencia ( 1 + carencia * 30 )
		PreAprovadoPDF documento = new PreAprovadoPDF(con.getPagador().getNome(), con.getDataContrato(),
				con.getNumeroContrato(), cpf, con.getTaxaAprovada(), observacao,
				con.getImovel().getCidade(), con.getImovel().getNumeroMatricula(), con.getImovel().getEstado(),
				con.getPrazoMaxAprovado().toString(), simulador.getValorCredito(), con.getValorMercadoImovel(),
				parcelaPGTO, con.getTipoValorComite(), cep, carencia, despesa,
				valorCustoEmissao, valorIOF , valorLiquido, detalhesDespesas, simulador.getParcelas());
		list.add(documento);
		
		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);
	}

	public byte[] geraPDFPAprovadoComiteByteArray(long idContrato) throws JRException, IOException {
		JasperPrint jp = geraPDFPAprovadoComite(idContrato);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
				
		JasperExportManager
		.exportReportToPdfStream(jp, bos);

		
		return bos.toByteArray();
	}
	

}
