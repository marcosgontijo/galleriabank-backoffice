package com.webnowbr.siscoat.cobranca.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.mindmap.MindmapNode;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;


@ManagedBean(name = "relacionamentoPagadorRecebedorMB")
@SessionScoped

public class RelacionamentoPagadorRecebedorMB {
	
	PagadorRecebedor pagadorRecebedor;
	List<RelacionamentoPagadorRecebedor> listRelacoes;
	
	 private MindmapNode root;
	 private MindmapNode selectedNode;
	

	public RelacionamentoPagadorRecebedorMB() {
		listRelacoes = new ArrayList<RelacionamentoPagadorRecebedor>();
	}
	
	
	
	
	
}
