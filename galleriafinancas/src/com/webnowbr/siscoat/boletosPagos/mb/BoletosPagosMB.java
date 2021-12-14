package com.webnowbr.siscoat.boletosPagos.mb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.webnowbr.siscoat.boletosPagos.vo.BoletosPagosVO;
import com.webnowbr.siscoat.cobranca.db.op.BoletosPagosDao;
import com.webnowbr.siscoat.cobranca.db.op.SeguradoDAO;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;



/** ManagedBean. */
@ManagedBean(name = "boletosPagosMB")
@SessionScoped
public class BoletosPagosMB {
	private Date dataInicio;
	private Date dataFinal;
	
	private List<BoletosPagosVO> boletosPagos;
	
	public String clearFields() {
		return "/Relatorios/Pagamentos/BoletosPagos.xhtml";
	}

	public String consultarBoletos() {
		this.boletosPagos = new ArrayList<BoletosPagosVO>(0);

		try {
			BoletosPagosDao boletosPagosDao = new BoletosPagosDao();
			this.boletosPagos = boletosPagosDao.listaBoletosPagos(0, this.dataInicio, this.dataFinal);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<BoletosPagosVO> getBoletosPagos() {
		return boletosPagos;
	}

	public void setBoletosPagos(List<BoletosPagosVO> boletosPagos) {
		this.boletosPagos = boletosPagos;
	}
	
	
}
