package br.com.cea.monitor.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PainelDisponibilidade implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	
	private LocalDateTime ultimaVerificacao;
	
	private List<UfSefaz> listaServicosSefaz =new ArrayList<>();	
	
}
