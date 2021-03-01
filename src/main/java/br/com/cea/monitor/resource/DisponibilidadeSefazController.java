package br.com.cea.monitor.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cea.monitor.exception.UfNotFoundException;
import br.com.cea.monitor.model.PainelDisponibilidade;
import br.com.cea.monitor.service.MotorBuscaService;

@RestController
@RequestMapping("/monitor")
public class DisponibilidadeSefazController {

	@Autowired
	private MotorBuscaService motorService;
	
	@GetMapping
	public ResponseEntity<PainelDisponibilidade> getPainelDisponibilidadeSefaz() {
		
		try {
			
			PainelDisponibilidade painel = motorService.consultarDisponibilidadeSefaz();
			
			if(painel != null) {
				return ResponseEntity.status(HttpStatus.OK).body(painel);
			}
			else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			 
		}
		catch(RuntimeException ex){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}		
	}
	
	@GetMapping("{uf}")
	public ResponseEntity<PainelDisponibilidade> consultaPorUf(@PathVariable String uf){
		
		try {
			
			PainelDisponibilidade painel = motorService.consultarDisponibilidadeSefazPorUf(uf.toUpperCase());
			
			return ResponseEntity.status(HttpStatus.OK).body(painel);		
			
		}
		catch (UfNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
