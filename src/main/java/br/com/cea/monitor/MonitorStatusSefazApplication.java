package br.com.cea.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class MonitorStatusSefazApplication  {

//	@Autowired
//	private MotorBuscaService motorBusca;

	public static void main(String[] args) {
		SpringApplication.run(MonitorStatusSefazApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//
//		PainelDisponibilidade painel = motorBusca.consultarDisponibilidadeSefaz();
//
//		log.info(String.format("Data  e hora da ultima verificação: %s",
//				painel.getUltimaVerificacao().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))));
//
//		painel.getListaServicosSefaz().forEach(servs -> {
//
//			log.info(String.format("Autorizador - %s", servs.getUf()));
//
//			servs.getListaAutorizador().forEach(serv -> {
//
//				log.info(String.format("      [%s] = [%s]", serv.getDescAutorizador(), serv.getStatus().name()));
//			});
//		});
//	}
}