package br.com.cea.monitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.cea.monitor.service.MotorBuscaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TarefaSefaz {

	@Autowired
	private MotorBuscaService motorBuscaService;
	
	@Scheduled(fixedDelay = 1000*60)
	public void chamarDisponibilidadeSefaz() {
		
		try {			
			motorBuscaService.limparCache();			
		}
		catch (RuntimeException e) {
			log.error("Erro na consulta Sefaz.",e);
		}
	}
}
