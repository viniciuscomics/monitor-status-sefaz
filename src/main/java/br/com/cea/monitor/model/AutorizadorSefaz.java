package br.com.cea.monitor.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class AutorizadorSefaz implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String descAutorizador;	
	private AlertaSefaz status;	
}
