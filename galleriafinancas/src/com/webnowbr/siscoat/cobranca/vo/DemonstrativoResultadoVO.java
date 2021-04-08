package com.webnowbr.siscoat.cobranca.vo;

import java.util.ArrayList;
import java.util.List;

public class DemonstrativoResultadoVO {

	List<DemonstrativoResultadosGrupo> Dre;

	public List<DemonstrativoResultadosGrupo> getDre() {
		return Dre;
	}

	public void setDre(List<DemonstrativoResultadosGrupo> dre) {
		Dre = dre;
	}
	
	public void addDre(DemonstrativoResultadosGrupo dre) {
		if ( Dre == null) {
			Dre = new ArrayList<DemonstrativoResultadosGrupo>(0);
		}
		Dre.add(dre);		
	}
	
	public void addDre(List<DemonstrativoResultadosGrupo> dres) {
		if ( Dre == null) {
			Dre = new ArrayList<DemonstrativoResultadosGrupo>(0);
		}
		Dre.addAll(dres);		
	}
}
