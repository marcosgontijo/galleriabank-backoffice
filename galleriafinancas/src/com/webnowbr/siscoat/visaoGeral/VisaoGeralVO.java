package com.webnowbr.siscoat.visaoGeral;

import java.util.ArrayList;
import java.util.List;

public class VisaoGeralVO {

	List<VisaoGeralGrupo> Vg;
	
	public List<VisaoGeralGrupo> getVg() {
		return Vg;
	}

	public void setVg(List<VisaoGeralGrupo> vg) {
		Vg = vg;
	}

	public void addVg(VisaoGeralGrupo vg) {
		if ( Vg == null) {
			Vg = new ArrayList<VisaoGeralGrupo>(0);
		}
		Vg.add(vg);		
	}
	
	public void addVg(List<VisaoGeralGrupo> vgs) {
		if ( Vg == null) {
			Vg = new ArrayList<VisaoGeralGrupo>(0);
		}
		Vg.addAll(vgs);		
	}
}
