package com.webnowbr.siscoat.cobranca.mb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.mindmap.MindmapNode;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.RelacionamentoPagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesParcialDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.job.IpcaJobCalcular;
import com.webnowbr.siscoat.job.IpcaJobContrato;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;


@ManagedBean(name = "ipcaMB")
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
