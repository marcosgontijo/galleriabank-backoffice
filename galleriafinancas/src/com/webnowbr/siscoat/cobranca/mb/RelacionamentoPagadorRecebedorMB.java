package com.webnowbr.siscoat.cobranca.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.PrimeFaces;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StateMachineConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.RelacionamentoPagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;

@SuppressWarnings("serial")
@ManagedBean(name = "relacionamentoMB")
@SessionScoped

public class RelacionamentoPagadorRecebedorMB implements Serializable {

	PagadorRecebedor pagadorRecebedor;
	List<RelacionamentoPagadorRecebedor> listRelacoes;

	Set<PagadorRecebedor> listPessoas = new HashSet<PagadorRecebedor>();

	List<RelacionamentoPagadorRecebedor> listRelacoesFaltantes;

	private DefaultDiagramModel model;
	StateMachineConnector connector;

	public RelacionamentoPagadorRecebedorMB() {
		listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
	}

	public void abrirPagadorRecebdor(PagadorRecebedor pagador) {

	}

	public String clearFieldsMindMap(PagadorRecebedor pagador) {
		init();

		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
		listRelacoes = rprDao.getRelacionamentos(pagador, listRelacoes);
		listRelacoesFaltantes = listRelacoes;
		Element root = new Element(pagador, "45em", "15em");
		root.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
		root.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
		model.addElement(root);

		List<RelacionamentoPagadorRecebedor> relacoesConsultadas = new ArrayList<RelacionamentoPagadorRecebedor>();
		listPessoas = new HashSet<PagadorRecebedor>();
		listPessoas.add(pagador);
		getPagadorMindMap(pagador, root, relacoesConsultadas);
		return "/Cadastros/Cobranca/PagadorRecebedorMindMap.xhtml";
	}

	public void getPagadorMindMap(PagadorRecebedor pagador, Element nodePagador,
			List<RelacionamentoPagadorRecebedor> relacoesConsultadas) {
		int i = 0;
		while (listRelacoesFaltantes.size() > 0 && i < listRelacoesFaltantes.size()) {
			RelacionamentoPagadorRecebedor relacao = listRelacoesFaltantes.get(i);
			if (relacoesConsultadas.contains(relacao)) {
				i++;
				continue;
			}
			if (CommonsUtil.mesmoValor(relacao.getPessoaRoot().getId(), pagador.getId())) {
				Element participante = createElement(
						relacao.getPessoaChild(), relacao.getRelacao() + " - "
								+ CommonsUtil.formataNumero(relacao.getPorcentagem(), "#,##0") + "%",
						nodePagador, false);
				listPessoas.add(relacao.getPessoaRoot());
				relacoesConsultadas.add(relacao);
				getPagadorMindMap(relacao.getPessoaChild(), participante, relacoesConsultadas);
			} else if (CommonsUtil.mesmoValor(relacao.getPessoaChild().getId(), pagador.getId())) {
				Element participante = createElement(
						relacao.getPessoaRoot(), relacao.getRelacao() + " - "
								+ CommonsUtil.formataNumero(relacao.getPorcentagem(), "#,##0") + "%",
						nodePagador, true);
				listPessoas.add(relacao.getPessoaRoot());
				relacoesConsultadas.add(relacao);
				getPagadorMindMap(relacao.getPessoaRoot(), participante, relacoesConsultadas);
			}
			i++;
		}
	}

	public void populaGeral() {
		// populaGeralEngine();
		populaGeralSeresa();
	}

	public void populaGeralEngine() {
		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		rprDao.populaGeralDBEngine();
	}

	public void populaGeralSeresa() {
		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		rprDao.populaGeralDB(0, 4000);
		rprDao.populaGeralDB(3999, 10000);
		rprDao.populaGeralDB(9999, 1000000000);
	}

	public void init() {
		model = new DefaultDiagramModel();
		model.setMaxConnections(-1);

		connector = new StateMachineConnector();
		model.setDefaultConnector(connector);
	}

	private Connection createConnection(EndPoint from, EndPoint to, String label) {
		Connection conn = new Connection(from, to, connector);
		conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));
		if (label != null) {
			conn.getOverlays().add(new LabelOverlay(label, "flow-label", 0.5));
		}
		return conn;
	}

	public Element createElement(PagadorRecebedor pagador, String relacao, Element nodePagador, boolean reverso) {
		double random = Math.floor(Math.random() * (25 - -20 + 1) - 25);
		double x = CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getX().replace(".0", ""))) - random;
		double y;
		if (reverso) {
			y = CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getY().replace(".0", ""))) - 10;
		} else {
			y = CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getY().replace(".0", ""))) + 25;
		}

		long qtdNode = model.getElements().stream()
				.filter(e -> CommonsUtil.mesmoValor(e.getY(), CommonsUtil.stringValue(y) + "em")).count();

		x = CommonsUtil.intValue(qtdNode * 30) + 10;

		Element participante = null;
		for (Element element : model.getElements()) {
			if (CommonsUtil.mesmoValor(((PagadorRecebedor) element.getData()).getNome(), pagador.getNome())) {
				participante = element;
			}
		}
		if (CommonsUtil.semValor(participante)) {
			participante = new Element(pagador, CommonsUtil.stringValue(x + "em"), CommonsUtil.stringValue(y + "em"));
			participante.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
			participante.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
			model.addElement(participante);
		}

		if (reverso) {
			model.connect(
					createConnection(nodePagador.getEndPoints().get(1), participante.getEndPoints().get(0), relacao));
		} else {
			model.connect(
					createConnection(participante.getEndPoints().get(1), nodePagador.getEndPoints().get(0), relacao));
		}
		return participante;
	}

	public Set<PagadorRecebedor> getListPessoas() {
		return listPessoas;
	}

	public void setListPessoas(Set<PagadorRecebedor> listPessoas) {
		this.listPessoas = listPessoas;
	}

	public DefaultDiagramModel getModel() {
		return model;
	}

	public void setModel(DefaultDiagramModel model) {
		this.model = model;
	}

}
