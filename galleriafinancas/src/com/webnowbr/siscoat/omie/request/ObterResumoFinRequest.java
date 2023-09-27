package com.webnowbr.siscoat.omie.request;

public class ObterResumoFinRequest  implements IOmieParam {
	private String dDia;
	private boolean lApenasResumo;
	private boolean lExibirCategoria;

	public String getdDia() {
		return dDia;
	}

	public void setdDia(String dDia) {
		this.dDia = dDia;
	}

	public boolean islApenasResumo() {
		return lApenasResumo;
	}

	public void setlApenasResumo(boolean lApenasResumo) {
		this.lApenasResumo = lApenasResumo;
	}

	public boolean islExibirCategoria() {
		return lExibirCategoria;
	}

	public void setlExibirCategoria(boolean lExibirCategoria) {
		this.lExibirCategoria = lExibirCategoria;
	}


	
	
}
