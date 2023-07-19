package com.webnowbr.siscoat.cobranca.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.BezierConnector;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.connector.StateMachineConnector;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.RelacionamentoPagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;


@SuppressWarnings("serial")
@ManagedBean(name = "relacionamentoPagadorRecebedorMB")
@SessionScoped

public class RelacionamentoPagadorRecebedorMB implements Serializable {
	
	PagadorRecebedor pagadorRecebedor;
	List<RelacionamentoPagadorRecebedor> listRelacoes;
	
	List<RelacionamentoPagadorRecebedor> listRelacoesFaltantes;
	
	private DefaultDiagramModel model;
	StateMachineConnector connector;
	
	 //private MindmapNode root;
		//private MindmapNode selectedNode;
	

	public RelacionamentoPagadorRecebedorMB() {
		listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
	}
	
	public void populaGeral() {
		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		rprDao.populaGeralDB();
	}
	
	

    public void init() {
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);

        StateMachineConnector connector = new StateMachineConnector();
        connector.setOrientation(StateMachineConnector.Orientation.ANTICLOCKWISE);
        connector.setPaintStyle("{stroke:'#7D7463',strokeWidth:30}");
        model.setDefaultConnector(connector);

        Element start = new Element(null, "15em", "5em");
        start.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
        start.setStyleClass("start-node");

        Element idle = new Element("Idle", "10em", "20em");
        idle.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
        idle.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_RIGHT));
        idle.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_LEFT));

        Element turnedOn = new Element("TurnedOn", "10em", "35em");
        turnedOn.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
        turnedOn.addEndPoint(new BlankEndPoint(EndPointAnchor.RIGHT));
        turnedOn.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_RIGHT));

        Element activity = new Element("Activity", "45em", "35em");
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.LEFT));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM_LEFT));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP_RIGHT));
        activity.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP_LEFT));

        model.addElement(start);
        model.addElement(idle);
        model.addElement(turnedOn);
        model.addElement(activity);

        model.connect(createConnection(start.getEndPoints().get(0), idle.getEndPoints().get(0), null));
        model.connect(createConnection(idle.getEndPoints().get(1), turnedOn.getEndPoints().get(0), "Turn On"));
        model.connect(createConnection(turnedOn.getEndPoints().get(0), idle.getEndPoints().get(2), "Turn Off"));
        model.connect(createConnection(turnedOn.getEndPoints().get(1), activity.getEndPoints().get(0), null));
        model.connect(createConnection(activity.getEndPoints().get(1), turnedOn.getEndPoints().get(2), "Request Turn Off"));
        model.connect(createConnection(activity.getEndPoints().get(2), activity.getEndPoints().get(2), "Talk"));
        model.connect(createConnection(activity.getEndPoints().get(3), activity.getEndPoints().get(3), "Run"));
        model.connect(createConnection(activity.getEndPoints().get(4), activity.getEndPoints().get(4), "Walk"));
    }

    private Connection createConnection(EndPoint from, EndPoint to, String label) {
        Connection conn = new Connection(from, to, connector);
        conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        if (label != null) {
            conn.getOverlays().add(new LabelOverlay(label, "flow-label", 0.5));
        }
        return conn;
    }
	
	public String clearFieldsMindMap(PagadorRecebedor pagador) {
		model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        
        //CONNECTOR N FUNCIONAAA
        connector =  new StateMachineConnector();
        model.setDefaultConnector(connector); 
        
		RelacionamentoPagadorRecebedorDao rprDao = new RelacionamentoPagadorRecebedorDao();
		listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
		listRelacoes = rprDao.getRelacionamentos(pagador, listRelacoes);
		listRelacoesFaltantes = listRelacoes;
		
		//model.getDefaultConnector().setPaintStyle("{stroke:'#7D7463',strokeWidth:30}");
		
		Element root = new Element(pagador.getNome(), "45em", "5em");
		root.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
        model.addElement(root);
        
		List<RelacionamentoPagadorRecebedor> relacoesConsultadas = new ArrayList<RelacionamentoPagadorRecebedor>();
		
		while(listRelacoesFaltantes.size() > 0) {
			getPagadorMindMap(pagador, root, relacoesConsultadas);
		}
		return "/Cadastros/Cobranca/PagadorRecebedorMindMap.xhtml";
	}
	
	public void getPagadorMindMap(PagadorRecebedor pagador, Element nodePagador, List<RelacionamentoPagadorRecebedor> relacoesConsultadas) {
		listRelacoesFaltantes.removeAll(relacoesConsultadas);
		int i = 0;
		while (listRelacoesFaltantes.size() > 0 && i < listRelacoesFaltantes.size()) {
			RelacionamentoPagadorRecebedor relacao = listRelacoesFaltantes.get(i);
			
			if (CommonsUtil.mesmoValor(relacao.getPessoaRoot().getId(), pagador.getId())) {
				/*Element participante = new Element(relacao.getPessoaChild().getNome(), CommonsUtil.stringValue(CommonsUtil.intValue(nodePagador.getX()) - 5), CommonsUtil.stringValue(CommonsUtil.intValue(nodePagador.getY()) - 10));
				participante.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
		        model.addElement(participante);
		        model.connect(createConnection(nodePagador.getEndPoints().get(0), participante.getEndPoints().get(0), relacao.getRelacao()));			
		      */
				Element participante = createElement(relacao.getPessoaChild(),
						relacao.getRelacao() + " - " + CommonsUtil.formataNumero(relacao.getPorcentagem(), "#,##0")+ "%", nodePagador, false);
		        relacoesConsultadas.add(relacao);
				getPagadorMindMap(relacao.getPessoaChild(), participante, relacoesConsultadas);
			} else if(CommonsUtil.mesmoValor(relacao.getPessoaChild().getId(), pagador.getId())) {
				
				/*Element participante = new Element(relacao.getPessoaRoot().getNome(), CommonsUtil.stringValue(CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getX())) - 5), CommonsUtil.stringValue(CommonsUtil.intValue(nodePagador.getY()) - 10));
				participante.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
		        model.addElement(participante);
		        model.connect(createConnection(nodePagador.getEndPoints().get(0), participante.getEndPoints().get(0), relacao.getRelacao()));*/
				
				Element participante = createElement(relacao.getPessoaRoot(),
						relacao.getRelacao() + " - " + CommonsUtil.formataNumero(relacao.getPorcentagem(), "#,##0") + "%", nodePagador, true);
				relacoesConsultadas.add(relacao);
				getPagadorMindMap(relacao.getPessoaRoot(), participante, relacoesConsultadas);			
			}
			i++;
		}
	}
	
	public Element createElement(PagadorRecebedor pagador, String relacao, Element nodePagador, boolean reverso) {
		double random = Math.floor(Math.random() *(25 - -20 + 1) -25);
		double x = CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getX().replace(".0", ""))) - random;
		double y;
		if(reverso) {
			y = CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getY().replace(".0", ""))) - 10 ;
		} else {
			y = CommonsUtil.intValue(CommonsUtil.somenteNumeros(nodePagador.getY().replace(".0", ""))) + 25;
		}
		
		Element participante = new Element(pagador.getNome(),
				CommonsUtil.stringValue( x + "em"),
				CommonsUtil.stringValue( y + "em"));
		if(reverso) {
			participante.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
		} else {
			participante.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
		}
		
		model.addElement(participante);
		if(reverso) {
			model.connect(createConnection(nodePagador.getEndPoints().get(0), participante.getEndPoints().get(0), relacao));
		} else {
			model.connect(createConnection(participante.getEndPoints().get(0), nodePagador.getEndPoints().get(0), relacao));		
		}
		return participante;
	}
	
	public void getPagadorMindMap(PagadorRecebedor pagador, MindmapNode nodePagador, List<RelacionamentoPagadorRecebedor> relacoesConsultadas) {
		listRelacoesFaltantes.removeAll(relacoesConsultadas);
		int i = 0;
		while (listRelacoesFaltantes.size() > 0 && i < listRelacoesFaltantes.size()) {
			RelacionamentoPagadorRecebedor relacao = listRelacoesFaltantes.get(i);
			
			if (CommonsUtil.mesmoValor(relacao.getPessoaRoot().getId(), pagador.getId())) {
				MindmapNode participante = new DefaultMindmapNode(relacao.getPessoaChild().getNome(),
						relacao.getPessoaChild().getCpf(), "6e9ebf", true);
				nodePagador.addNode(participante);
				relacoesConsultadas.add(relacao);
				getPagadorMindMap(relacao.getPessoaChild(), participante, relacoesConsultadas);
			} else if(CommonsUtil.mesmoValor(relacao.getPessoaChild().getId(), pagador.getId())) {
				MindmapNode participante = new DefaultMindmapNode(relacao.getPessoaRoot().getNome(),
						relacao.getPessoaRoot().getCpf(), "FF9999", true);
				nodePagador.addNode(participante); 
				relacoesConsultadas.add(relacao);
				getPagadorMindMap(relacao.getPessoaRoot(), participante, relacoesConsultadas);
				
			}
			i++;
		}
	}

	
	
	public DefaultDiagramModel getModel() {
		return model;
	}

	public void setModel(DefaultDiagramModel model) {
		this.model = model;
	}
	
	
}
