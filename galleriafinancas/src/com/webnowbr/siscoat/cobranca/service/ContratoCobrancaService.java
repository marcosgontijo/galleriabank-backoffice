package com.webnowbr.siscoat.cobranca.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;

import com.webnowbr.siscoat.cobranca.db.model.ComparativoCamposEsteira;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracaoDetalhe;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaLogsAlteracaoDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaLogsAlteracaoDetalheDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentoAnaliseDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.model.User;


public class ContratoCobrancaService {
	
	private Set<Integer> verificaDuplicidadeDeIdParaNaoVerificar = new HashSet<Integer>();

	public List<ContratoCobrancaLogsAlteracaoDetalhe> comparandoValores(Object valoresAtuais,
			Object valoresBanco, List<ComparativoCamposEsteira> camposEsteira,
			ContratoCobrancaLogsAlteracao alteracao) {
		
		String nomeClasse = valoresAtuais.getClass().getSimpleName();
		
		int tamanhoLista = verificaDuplicidadeDeIdParaNaoVerificar.size();
		
		verificaDuplicidadeDeIdParaNaoVerificar.add(valoresAtuais.hashCode());
		
		List<ContratoCobrancaLogsAlteracaoDetalhe> listaDeAlteracoes = new ArrayList<>();
		
		if(tamanhoLista == verificaDuplicidadeDeIdParaNaoVerificar.size()) {
			return listaDeAlteracoes;
		}
		
		List<String> camposParaVerificar = camposEsteira.stream()
				.filter(c -> c.getValidarClasses().equals(nomeClasse))
				.map(c -> c.getNome_propiedade())
				.collect(Collectors.toList());
		
		Class<?> reflectionValues = valoresAtuais.getClass();

		for (Field field : reflectionValues.getDeclaredFields()) {
			
			Optional<ComparativoCamposEsteira> comparativoCamposEsteira = camposEsteira
					.stream().filter(f -> f.getNome_propiedade().equalsIgnoreCase(field.getName().toLowerCase()))
					.findAny();
			try {
			if(!comparativoCamposEsteira.isPresent()) {
				String nomeClasseCampo = field.getType().getSimpleName();
				if (camposEsteira.stream().filter(c -> c.getValidarClasses().equalsIgnoreCase(nomeClasseCampo.toLowerCase())).findAny().isPresent()) { 
					field.setAccessible(true);
					Object currentValues = field.get(valoresAtuais); 
					Object originalValues = field.get(valoresBanco);
					
					if (currentValues != null && originalValues == null) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(),
								nomeClasse,
								currentValues.toString(), "Não existia", alteracao,  0l));
					}
					if (currentValues == null && originalValues != null) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(),
								nomeClasse,
								"Removido", originalValues.toString(), alteracao, 0l));
					} else if (currentValues != null && originalValues != null && !currentValues.equals(valoresAtuais) ) {
						listaDeAlteracoes
								.addAll(comparandoValores(currentValues, originalValues, camposEsteira, alteracao));
					}
				}
				
				continue;
			}
			
				Object currentValues =  callGetMethods(valoresAtuais, field.getName()); // valores que estao no banco
				Object originalValues = callGetMethods(valoresBanco, field.getName()); // valores que esta vindo atual

				if (currentValues != null) {
					if(!CommonsUtil.mesmoValor(currentValues, originalValues)) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(),
								nomeClasse, CommonsUtil.stringValue(originalValues),
								CommonsUtil.stringValue(currentValues),
								alteracao, comparativoCamposEsteira.get().getOrdem()));
					}
				} 
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return listaDeAlteracoes;
	}
	
	
	public ContratoCobrancaLogsAlteracao buscaLogsAlteracao(User usuario, ContratoCobranca contratoCobranca)  {
		ContratoCobrancaLogsAlteracao contratoCobrancaLogsAlteracao = new ContratoCobrancaLogsAlteracao();
		ContratoCobrancaLogsAlteracaoDao contratoCobrancaLogsAlteracaoDao = new ContratoCobrancaLogsAlteracaoDao();
		
		contratoCobrancaLogsAlteracao = contratoCobrancaLogsAlteracaoDao.buscaLogAlteracao(usuario.getLogin(), contratoCobranca.getId());
		return contratoCobrancaLogsAlteracao;
	}
	
	public ContratoCobrancaLogsAlteracao buscaOuCriaLogsAlteracao(User usuario, ContratoCobranca contratoCobranca) {
		
		ContratoCobrancaLogsAlteracao contratoCobrancaLogsAlteracao = new ContratoCobrancaLogsAlteracao();
		ContratoCobrancaLogsAlteracaoDao contratoCobrancaLogsAlteracaoDao = new ContratoCobrancaLogsAlteracaoDao();
		
		contratoCobrancaLogsAlteracao = buscaLogsAlteracao(usuario, contratoCobranca);
		
		if (contratoCobrancaLogsAlteracao != null ) {
			return contratoCobrancaLogsAlteracao;
		} 
			
		contratoCobrancaLogsAlteracao = new ContratoCobrancaLogsAlteracao();
			
		contratoCobrancaLogsAlteracao.setDataAlteracao(DateUtil.getDataHoje());
		contratoCobrancaLogsAlteracao.setUsuario(usuario.getLogin());
		contratoCobrancaLogsAlteracao.setStatusEsteira(contratoCobranca.getStatusEsteira());
		contratoCobrancaLogsAlteracao.setContratoCobranca(contratoCobranca);
		contratoCobrancaLogsAlteracaoDao.create(contratoCobrancaLogsAlteracao);
		
		return contratoCobrancaLogsAlteracao;
	}
	
	public void adicionaAlteracoesNoPopPupParaCamposDireto() {
		ContratoCobrancaMB contratoCobrancaMb = new ContratoCobrancaMB();
		contratoCobrancaMb.setEstadoConsultaAdd(contratoCobrancaMb.getEstadoConsultaAdd());
		contratoCobrancaMb.getEstadoConsultaAdd();
		
	}
	
	public void zerarListaDeDuplicidade() {
		this.verificaDuplicidadeDeIdParaNaoVerificar.clear();
	}

	public static Object callGetMethods(Object obj, String propriedade) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        
		Optional<Method> method = CommonsUtil.getList(methods).stream()
				.filter(m -> m.getName().toLowerCase().equalsIgnoreCase("get" + propriedade.toLowerCase())
						|| m.getName().toLowerCase().equalsIgnoreCase("is" + propriedade.toLowerCase())).findAny();
        
            if ( method.isPresent() &&  isGetter(method.get())) {
                try {
                    Object value = method.get().invoke(obj);
                    return value;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return null;
    }
	
	public static boolean isGetter(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        if (method.getParameterCount() != 0) {
            return false;
        }
        if (void.class.equals(method.getReturnType())) {
            return false;
        }
        if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) {
            return false;
        }
        return true;
    }
	
	public void adicionaNovoDetalhe(User usuario, ContratoCobranca contratoCobranca, String valorAtual,
			String valorBanco, String nomeCampo) {

		ContratoCobrancaLogsAlteracao logAlteracao = buscaOuCriaLogsAlteracao(usuario, contratoCobranca);
		ContratoCobrancaLogsAlteracaoDetalhe logAlteracaoDetalhe = new ContratoCobrancaLogsAlteracaoDetalhe();

		logAlteracaoDetalhe.setLogsalteracao(logAlteracao);
		logAlteracaoDetalhe.setValorAlterado(valorAtual);
		logAlteracaoDetalhe.setValorBanco(valorBanco);
		logAlteracaoDetalhe.setNomeCampo(nomeCampo);
		adicionaNovoDetalhe(logAlteracaoDetalhe);
	}
	
	public void adicionaNovoDetalhe(ContratoCobrancaLogsAlteracaoDetalhe logAlteracaoDetalhe) {
		ContratoCobrancaLogsAlteracaoDetalheDao contraLogsAlteracaoDetalheDao = new ContratoCobrancaLogsAlteracaoDetalheDao();
		if (CommonsUtil.semValor(logAlteracaoDetalhe.getId()))
			contraLogsAlteracaoDetalheDao.create(logAlteracaoDetalhe);
		else
			contraLogsAlteracaoDetalheDao.merge(logAlteracaoDetalhe);
	}
	
	public ContratoCobrancaLogsAlteracao exibePopPupSeNaoConfirmar(String usuario) {
		ContratoCobrancaLogsAlteracao contratoCobrancaLogsAlteracao = new ContratoCobrancaLogsAlteracao();
		ContratoCobrancaLogsAlteracaoDao contratoCobrancaLogsAlteracaoDao = new ContratoCobrancaLogsAlteracaoDao();
		
		contratoCobrancaLogsAlteracao = contratoCobrancaLogsAlteracaoDao.consultaLogsNaoJustificados(usuario);
		
		return contratoCobrancaLogsAlteracao;
	}
	
}