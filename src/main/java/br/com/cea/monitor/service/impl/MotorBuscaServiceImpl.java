package br.com.cea.monitor.service.impl;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.cea.monitor.config.ApiConfiguration;
import br.com.cea.monitor.exception.AcessoSefazException;
import br.com.cea.monitor.exception.StatusSefazException;
import br.com.cea.monitor.exception.UfNotFoundException;
import br.com.cea.monitor.exception.UltimaVerificacaoException;
import br.com.cea.monitor.model.AlertaSefaz;
import br.com.cea.monitor.model.AutorizadorSefaz;
import br.com.cea.monitor.model.PainelDisponibilidade;
import br.com.cea.monitor.model.UfSefaz;
import br.com.cea.monitor.service.MotorBuscaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MotorBuscaServiceImpl implements MotorBuscaService {

	private ApiConfiguration config;

	PainelDisponibilidade painelDisponibilidade = null;

	@Autowired
	public MotorBuscaServiceImpl(ApiConfiguration config) {
		this.config = config;
	}

	@Override
	@Cacheable("painelDisponibilidade")	
	public PainelDisponibilidade consultarDisponibilidadeSefaz() {

		if (painelDisponibilidade == null) {
			painelDisponibilidade = new PainelDisponibilidade();

			Document doc = buscarHtmlPainelDisponibilidadeSefaz();

			painelDisponibilidade.setListaServicosSefaz(
					buscarListaStatusUfsSefaz(doc,"tr[class=linhaImparCentralizada]"));
			
			painelDisponibilidade.getListaServicosSefaz().addAll(
					buscarListaStatusUfsSefaz(doc, "tr[class=linhaParCentralizada]"));

			painelDisponibilidade.setUltimaVerificacao(buscarUltimaVerificacao(doc));
		}

		return painelDisponibilidade;
	}

	private List<UfSefaz> buscarListaStatusUfsSefaz(Document doc, String selectCssAutorizadores) {

		List<UfSefaz> listaStatusUfsSefaz = new ArrayList<>();

		try {
			log.info("verificando html");
			Element table = doc.select("table[class=tabelaListagemDados]").first();

			Elements descAutorizadores = table.select("tr").select("th");
			Elements descricaoUfsAndValues = table.select(selectCssAutorizadores);

			for (int i = 0; i < descricaoUfsAndValues.size(); i++) {

				UfSefaz uf = new UfSefaz();
				uf.setUf(descricaoUfsAndValues.get(i).child(0).text());

				for (int j = 1; j < descricaoUfsAndValues.get(i).children().size(); j++) {

					AutorizadorSefaz autorizador = new AutorizadorSefaz();
					autorizador.setDescAutorizador(descAutorizadores.get(j).html());

					String status = descricaoUfsAndValues.get(i).children().get(j).select("td").select("img")
							.attr("src");
					autorizador.setStatus(getStatus(status));

					uf.getListaAutorizador().add(autorizador);
				}

				listaStatusUfsSefaz.add(uf);
			}
		} catch (Exception e) {
			log.error("Erro ao fazer parse dos status Sefaz", e);
			throw new StatusSefazException(e.getMessage());
		}

		return listaStatusUfsSefaz;
	}

	private LocalDateTime buscarUltimaVerificacao(Document doc) {

		try {
			Element table = doc.select("table[class=tabelaListagemDados]").first();

			Elements elementUltVerificacao = table.select("caption").select("span");

			String ultVerificacaoTxt = elementUltVerificacao.first().childNode(0).outerHtml();

			int idx = ultVerificacaoTxt.indexOf(":");

			if (idx > -1) {

				ultVerificacaoTxt = ultVerificacaoTxt.substring(idx + 1, ultVerificacaoTxt.length()).trim();

				LocalDateTime ultVerifcacao = LocalDateTime.parse(ultVerificacaoTxt,
						DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

				return ultVerifcacao;
			}
		} catch (Exception e) {
			log.error("Erro ao buscar data da ultima verificacao", e);
			throw new UltimaVerificacaoException(e.getMessage());
		}

		throw new UltimaVerificacaoException("Data da ultima verificacao nao encontrada");
	}

	private AlertaSefaz getStatus(String status) {

		if (Strings.isBlank(status)) {
			return AlertaSefaz.INDEFINIDO;
		} else if (status.contains("verde")) {
			return AlertaSefaz.VERDE;
		} else if (status.contains("amarel")) {
			return AlertaSefaz.AMARELO;
		} else if (status.contains("vermelh")) {
			return AlertaSefaz.VERMELHO;
		} else {
			log.info(String.format("Alerta desconhecido = [%S]", status));
			return AlertaSefaz.INDEFINIDO;
		}
	}

	private Document buscarHtmlPainelDisponibilidadeSefaz() {

		try {
			log.info("Iniciando a busca html Sefaz...");
			log.info(String.format("URL Configurada = [%s]", config.getUrlSefaz()));

			URL url = new URL(config.getUrlSefaz());

			return Jsoup.parse(url, config.getTimeoutSecond() * 1000);
		} catch (Exception e) {
			log.error("Erro ao tentar acessar o site da Sefaz", e);
			throw new AcessoSefazException(e.getMessage());
		}
	}

	@Override
	public PainelDisponibilidade consultarDisponibilidadeSefazPorUf(String uf) {
		
		PainelDisponibilidade painel = (PainelDisponibilidade) SerializationUtils.clone(this.painelDisponibilidade);		
		
		Optional<UfSefaz> optUfSefaz = painel.getListaServicosSefaz().stream()
				.filter(listuf-> listuf.getUf().equals(uf)).findAny();
		
		if(optUfSefaz.isPresent()) {			
			painel.setListaServicosSefaz(Arrays.asList(optUfSefaz.get()));
		}
		else {
			log.error("UF informada está invalida");
			throw new UfNotFoundException("UF inválida");
		}
		
		return painel;
	}

	@Override
	@CacheEvict(value = { "painelDisponibilidade" }, allEntries = true)
	public void limparCache() {		
		log.info("Limpando e atualizando cache...");
		this.painelDisponibilidade = null;
		PainelDisponibilidade painel = consultarDisponibilidadeSefaz();
		
		log.info(String.format("Data  e hora da ultima verificação: %s",
				painel.getUltimaVerificacao().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))));
	}
}
