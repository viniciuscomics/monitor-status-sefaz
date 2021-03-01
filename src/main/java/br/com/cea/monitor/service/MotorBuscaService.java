package br.com.cea.monitor.service;

import br.com.cea.monitor.model.PainelDisponibilidade;

public interface MotorBuscaService {

	public PainelDisponibilidade consultarDisponibilidadeSefaz();
	
	public PainelDisponibilidade consultarDisponibilidadeSefazPorUf(String uf);
	
	public void limparCache();
}
