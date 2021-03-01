package br.com.cea.monitor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UfSefaz implements Serializable{
	 
	private static final long serialVersionUID = 1L;
		
	private String uf;	
	private List<AutorizadorSefaz> listaAutorizador = new ArrayList<>();
}
