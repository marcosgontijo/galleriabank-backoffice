--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: cobranca; Type: SCHEMA; Schema: -; Owner: webnowbr
--

CREATE SCHEMA cobranca;


ALTER SCHEMA cobranca OWNER TO webnowbr;

--
-- Name: infra; Type: SCHEMA; Schema: -; Owner: webnowbr
--

CREATE SCHEMA infra;


ALTER SCHEMA infra OWNER TO webnowbr;

--
-- Name: posto; Type: SCHEMA; Schema: -; Owner: webnowbr
--

CREATE SCHEMA posto;


ALTER SCHEMA posto OWNER TO webnowbr;

--
-- Name: reservarimovel; Type: SCHEMA; Schema: -; Owner: webnowbr
--

CREATE SCHEMA reservarimovel;


ALTER SCHEMA reservarimovel OWNER TO webnowbr;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- Name: atualiza(); Type: FUNCTION; Schema: public; Owner: webnowbr
--

CREATE FUNCTION atualiza() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    r reservarimovel.reservarimovel%rowtype;
BEGIN
    FOR r IN SELECT * FROM reservarimovel.reservarimovel
	ORDER BY id
    LOOP
        -- can do some processing here

        update reservarimovel.reservarimovel ri
		set numerocontrato = lpad(cast(((nextval('locacao') - 1) + 1000) as varchar),5,'0')
	where ri.id = r.id;

    END LOOP;
        
                RETURN 1;
        END;
$$;


ALTER FUNCTION public.atualiza() OWNER TO webnowbr;

SET search_path = cobranca, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: boletosremessa; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE boletosremessa (
    id bigint NOT NULL,
    numerocontrato character varying(255) NOT NULL,
    parcela character varying(255),
    dtvencimento timestamp without time zone,
    dtemissao timestamp without time zone,
    dtremessa timestamp without time zone,
    nomearquivoremessa character varying(255),
    geradoremessa boolean,
    sistema character varying(255),
    valor numeric(19,2),
    documento character varying(255),
    nomesacado character varying(255),
    endereco character varying(255),
    bairro character varying(255),
    cep character varying(255),
    cidade character varying(255),
    uf character varying(255)
);


ALTER TABLE cobranca.boletosremessa OWNER TO webnowbr;

--
-- Name: calculos; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE calculos (
    id bigint NOT NULL,
    datacalculo timestamp without time zone,
    dataatualizacao timestamp without time zone,
    txjuros numeric(19,2),
    multa numeric(19,2),
    identificacaocalculo character varying(255),
    descricao text,
    recebedor bigint,
    imprimetaxas boolean,
    honorarios numeric(19,2)
);


ALTER TABLE cobranca.calculos OWNER TO webnowbr;

--
-- Name: calculos_detalhes_join; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE calculos_detalhes_join (
    idcalculos bigint NOT NULL,
    idcalculosdetalhes bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE cobranca.calculos_detalhes_join OWNER TO webnowbr;

--
-- Name: calculosdetalhes; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE calculosdetalhes (
    id bigint NOT NULL,
    datavencimento timestamp without time zone,
    datapagamento timestamp without time zone,
    vlrparcela numeric(19,2),
    txjuros numeric(19,2),
    honorarios numeric(19,2),
    multa numeric(19,2),
    total numeric(19,2),
    numeroparcela integer,
    vlrtxjuros numeric(19,2),
    vlrmulta numeric(19,2),
    vlrhonorarios numeric(19,2),
    observacao character varying(255)
);


ALTER TABLE cobranca.calculosdetalhes OWNER TO webnowbr;

--
-- Name: cobranca_detalhes_parcial_join; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE cobranca_detalhes_parcial_join (
    idcontratocobrancadetalhes bigint NOT NULL,
    idcontratocobrancadetalhesparcial bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE cobranca.cobranca_detalhes_parcial_join OWNER TO webnowbr;

--
-- Name: contratocobranca; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobranca (
    id bigint NOT NULL,
    pagador bigint,
    recebedor bigint,
    recebedor2 bigint,
    recebedor3 bigint,
    recebedor4 bigint,
    recebedor5 bigint,
    recebedor6 bigint,
    recebedor7 bigint,
    recebedor8 bigint,
    recebedor9 bigint,
    recebedor10 bigint,
    responsavel bigint,
    imovel bigint,
    datacontrato timestamp without time zone,
    datainicio timestamp without time zone,
    diames integer,
    qtdeparcelas integer,
    txadministracao numeric(19,2),
    txjuros numeric(19,2),
    vlrinvestimento numeric(19,2),
    vlrrepasse numeric(19,2),
    vlrlucro numeric(19,2),
    acao character varying(255),
    observacao text,
    observacao2 text,
    geraparcelafinal boolean,
    vlrcomissao numeric(19,2),
    datapagamentoini timestamp without time zone,
    datapagamentofim timestamp without time zone,
    vlrparcela numeric(19,2),
    numerocontrato character varying(255),
    vlrrecebedor numeric(19,2),
    vlrrecebedor2 numeric(19,2),
    vlrrecebedor3 numeric(19,2),
    vlrrecebedor4 numeric(19,2),
    vlrrecebedor5 numeric(19,2),
    vlrrecebedor6 numeric(19,2),
    vlrrecebedor7 numeric(19,2),
    vlrrecebedor8 numeric(19,2),
    vlrrecebedor9 numeric(19,2),
    vlrrecebedor10 numeric(19,2),
    contratorestritoadm boolean,
    status character varying(255),
    vlrparcelafinal numeric(19,2),
    estadocivil character varying(255),
    temmaisimoveis character varying(255),
    finalidade character varying(255),
    iprf character varying(255),
    profissao character varying(255),
    quantoprecisa numeric(19,2),
    vlrparcelastr character varying(255),
    recebedorparcelafinal1 bigint,
    vlrfinalrecebedor1 numeric(19,2),
    recebedorparcelafinal2 bigint,
    vlrfinalrecebedor2 numeric(19,2),
    recebedorparcelafinal3 bigint,
    vlrfinalrecebedor3 numeric(19,2),
    recebedorparcelafinal4 bigint,
    vlrfinalrecebedor4 numeric(19,2),
    recebedorparcelafinal5 bigint,
    vlrfinalrecebedor5 numeric(19,2),
    reprovadodata timestamp without time zone,
    reprovadousuario character varying(255),
    reprovado boolean,
    aprovadodata timestamp without time zone,
    aprovadousuario character varying(255),
    aprovado boolean,
    inicioanalisedata timestamp without time zone,
    inicioanaliseusuario character varying(255),
    inicioanalise boolean,
    aguardandodocumentodata timestamp without time zone,
    aguardandodocumentousuario character varying(255),
    aguardandodocumento boolean,
    matriculaaprovadadata timestamp without time zone,
    matriculaaprovadausuario character varying(255),
    matriculaaprovada boolean,
    fotoimovelreprovadadata timestamp without time zone,
    fotoimovelreprovadausuario character varying(255),
    fotoimovelreprovada boolean,
    matriculareprovadadata timestamp without time zone,
    matriculareprovadausuario character varying(255),
    matriculareprovada boolean,
    fotoimovelaprovadadata timestamp without time zone,
    fotoimovelaprovadausuario character varying(255),
    fotoimovelaprovada boolean,
    semfotoimoveldata timestamp without time zone,
    semfotoimovelusuario character varying(255),
    semfotoimovel boolean,
    documentoscompletosdata timestamp without time zone,
    documentoscompletosusuario character varying(255),
    documentoscompletos boolean,
    documentosincompletosdata timestamp without time zone,
    documentosincompletosusuario character varying(255),
    documentosincompletos boolean,
    cadastroaprovadodata timestamp without time zone,
    cadastroaprovadousuario character varying(255),
    cadastroaprovado boolean,
    cadastroreprovadodata timestamp without time zone,
    cadastroreprovadousuario character varying(255),
    cadastroreprovado boolean,
    aguardandocertidoesdata timestamp without time zone,
    aguardandocertidoesusuario character varying(255),
    aguardandocertidoes boolean,
    aguardandocnddata timestamp without time zone,
    aguardandocndusuario character varying(255),
    aguardandocnd boolean,
    agendarvisitaempresadata timestamp without time zone,
    agendarvisitaempresausuario character varying(255),
    agendarvisitaempresa boolean,
    visitaempresaaprovadadata timestamp without time zone,
    visitaempresaaprovadausuario character varying(255),
    visitaempresaaprovada boolean,
    visitaempresareprovadadata timestamp without time zone,
    visitaempresareprovadausuario character varying(255),
    visitaempresareprovada boolean,
    agendarvisitaimoveldata timestamp without time zone,
    agendarvisitaimovelusuario character varying(255),
    agendarvisitaimovel boolean,
    visitaimovelaprovadadata timestamp without time zone,
    visitaimovelaprovadausuario character varying(255),
    visitaimovelaprovada boolean,
    visitaimovelreprovadadata timestamp without time zone,
    visitaimovelreprovadausuario character varying(255),
    visitaimovelreprovada boolean,
    enviadocobrancalaudodata timestamp without time zone,
    enviadocobrancalaudousuario character varying(255),
    enviadocobrancalaudo boolean,
    pagtolaudoconfirmadadata timestamp without time zone,
    pagtolaudoconfirmadausuario character varying(255),
    pagtolaudoconfirmada boolean,
    laudosolicitadodata timestamp without time zone,
    laudosolicitadousuario character varying(255),
    laudosolicitado boolean,
    laudorecebidodata timestamp without time zone,
    laudorecebidousuario character varying(255),
    laudorecebido boolean,
    pajursolicitadodata timestamp without time zone,
    pajursolicitadousuario character varying(255),
    pajursolicitado boolean,
    pajurfavoraveldata timestamp without time zone,
    pajurfavoravelusuario character varying(255),
    pajurfavoravel boolean,
    pajurdesfavoraveldata timestamp without time zone,
    pajurdesfavoravelusuario character varying(255),
    pajurdesfavoravel boolean,
    reanalisarpajurdata timestamp without time zone,
    reanalisarpajurusuario character varying(255),
    reanalisarpajur boolean,
    aguardandoinvestidordata timestamp without time zone,
    aguardandoinvestidorusuario character varying(255),
    aguardandoinvestidor boolean,
    agendadocartoriodata timestamp without time zone,
    agendadocartoriousuario character varying(255),
    agendadocartorio boolean,
    contratoassinadodata timestamp without time zone,
    contratoassinadousuario character varying(255),
    contratoassinado boolean,
    ocultarecebedor boolean,
    ocultarecebedor2 boolean,
    ocultarecebedor3 boolean,
    ocultarecebedor4 boolean,
    ocultarecebedor5 boolean,
    ocultarecebedor6 boolean,
    ocultarecebedor7 boolean,
    ocultarecebedor8 boolean,
    ocultarecebedor9 boolean,
    ocultarecebedor10 boolean
);


ALTER TABLE cobranca.contratocobranca OWNER TO webnowbr;

--
-- Name: contratocobranca_detalhes_join; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobranca_detalhes_join (
    idcontratocobranca bigint NOT NULL,
    idcontratocobrancadetalhes bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE cobranca.contratocobranca_detalhes_join OWNER TO webnowbr;

--
-- Name: contratocobranca_favorecidos_join; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobranca_favorecidos_join (
    idcontratocobrancadetalhes bigint NOT NULL,
    idcontratocobrancafavorecidos bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE cobranca.contratocobranca_favorecidos_join OWNER TO webnowbr;

--
-- Name: contratocobranca_observacoes_join; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobranca_observacoes_join (
    idcontratocobranca bigint NOT NULL,
    idcontratocobrancaobservacoes bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE cobranca.contratocobranca_observacoes_join OWNER TO webnowbr;

--
-- Name: contratocobrancadetalhes; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobrancadetalhes (
    id bigint NOT NULL,
    numeroparcela character varying(255),
    datavencimento timestamp without time zone,
    datavencimentoatual timestamp without time zone,
    datapagamento timestamp without time zone,
    vlrparcela numeric(19,2),
    vlrjuros numeric(19,2),
    vlrparcelaatualizada numeric(19,2),
    vlrsaldoparcela numeric(19,2),
    vlrrepasse numeric(19,2),
    vlrretencao numeric(19,2),
    vlrcomissao numeric(19,2),
    parcelapaga boolean,
    vlrrecebido numeric(19,2),
    idfaturaiugu character varying(255),
    secureurliugu character varying(255),
    gerasplitteriugu boolean,
    promessapagamento timestamp without time zone,
    feztransferenciaiugu boolean,
    cedenteiugu bigint
);


ALTER TABLE cobranca.contratocobrancadetalhes OWNER TO webnowbr;

--
-- Name: contratocobrancadetalhes_observacoes_join; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobrancadetalhes_observacoes_join (
    idcontratocobrancadetalhes bigint NOT NULL,
    idcontratocobrancadetalhesobservacoes bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE cobranca.contratocobrancadetalhes_observacoes_join OWNER TO webnowbr;

--
-- Name: contratocobrancadetalhesobservacoes; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobrancadetalhesobservacoes (
    id bigint NOT NULL,
    data timestamp without time zone,
    observacao character varying(255),
    usuario character varying(255)
);


ALTER TABLE cobranca.contratocobrancadetalhesobservacoes OWNER TO webnowbr;

--
-- Name: contratocobrancadetalhesparcial; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobrancadetalhesparcial (
    id bigint NOT NULL,
    numeroparcela character varying(255),
    datavencimento timestamp without time zone,
    datavencimentoatual timestamp without time zone,
    datapagamento timestamp without time zone,
    vlrparcela numeric(19,2),
    vlrrecebido numeric(19,2),
    recebedor bigint,
    observacaorecebedor character varying(255),
    vlrparcelaatualizado numeric(19,2),
    saldoapagar numeric(19,2)
);


ALTER TABLE cobranca.contratocobrancadetalhesparcial OWNER TO webnowbr;

--
-- Name: contratocobrancafavorecidos; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobrancafavorecidos (
    id bigint NOT NULL,
    banco character varying(255),
    agencia character varying(255),
    conta character varying(255),
    nome character varying(255),
    cpf character varying(255),
    cnpj character varying(255),
    vlrrecebido numeric(19,2)
);


ALTER TABLE cobranca.contratocobrancafavorecidos OWNER TO webnowbr;

--
-- Name: contratocobrancaobservacoes; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE contratocobrancaobservacoes (
    id bigint NOT NULL,
    data timestamp without time zone,
    observacao character varying(255),
    usuario character varying(255)
);


ALTER TABLE cobranca.contratocobrancaobservacoes OWNER TO webnowbr;

--
-- Name: empresacobranca; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE empresacobranca (
    id bigint NOT NULL,
    nome character varying(255),
    cnpj character varying(255),
    endereco character varying(255),
    bairro character varying(255),
    cep character varying(255),
    cidade character varying(255),
    estado character varying(255),
    agencia character varying(255),
    digitoagencia character varying(255),
    codigobeneficiario character varying(255),
    digitobeneficiario character varying(255),
    numeroconvenio character varying(255),
    carteira character varying(255),
    localpagamento character varying(255),
    instrucao1 character varying(255),
    instrucao2 character varying(255),
    instrucao3 character varying(255),
    instrucao4 character varying(255),
    instrucao5 character varying(255),
    sistema character varying(255),
    prefixonumerodoc character varying(255),
    nossonumero character varying(255)
);


ALTER TABLE cobranca.empresacobranca OWNER TO webnowbr;

--
-- Name: filainvestidores; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE filainvestidores (
    id bigint NOT NULL,
    datainsercao timestamp without time zone,
    datadisponibilidade timestamp without time zone,
    valordisponivel numeric(19,2),
    observacao text,
    investidor bigint
);


ALTER TABLE cobranca.filainvestidores OWNER TO webnowbr;

--
-- Name: gruposfavorecidos; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE gruposfavorecidos (
    id bigint NOT NULL,
    nomegrupo character varying(255) NOT NULL,
    recebedor1 bigint,
    recebedor2 bigint,
    recebedor3 bigint,
    recebedor4 bigint,
    recebedor5 bigint,
    recebedor6 bigint,
    recebedor7 bigint,
    recebedor8 bigint,
    recebedor9 bigint,
    recebedor10 bigint
);


ALTER TABLE cobranca.gruposfavorecidos OWNER TO webnowbr;

--
-- Name: grupospagadores; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE grupospagadores (
    id bigint NOT NULL,
    nomegrupo character varying(255) NOT NULL,
    pagador1 bigint,
    pagador2 bigint,
    pagador3 bigint,
    pagador4 bigint,
    pagador5 bigint,
    pagador6 bigint,
    pagador7 bigint,
    pagador8 bigint,
    pagador9 bigint,
    pagador10 bigint
);


ALTER TABLE cobranca.grupospagadores OWNER TO webnowbr;

--
-- Name: imovelcobranca; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE imovelcobranca (
    id bigint NOT NULL,
    numeromatricula character varying(255),
    nome character varying(255),
    endereco character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    cidade character varying(255),
    estado character varying(255),
    telresidencial character varying(255),
    observacao character varying(255),
    cep character varying(255),
    cartorio character varying(255),
    tipo character varying(255),
    areatotal character varying(255),
    areaconstruida character varying(255),
    linkgmaps character varying(600),
    possuidivida character varying(255)
);


ALTER TABLE cobranca.imovelcobranca OWNER TO webnowbr;

--
-- Name: pagadorrecebedor; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE pagadorrecebedor (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    endereco character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    cidade character varying(255),
    estado character varying(255),
    telresidencial character varying(255),
    telcelular character varying(255),
    email character varying(255),
    dtnascimento timestamp without time zone,
    observacao1 text,
    observacao2 text,
    cpf character varying(255),
    cnpj character varying(255),
    rg character varying(255),
    dataemissaorg timestamp without time zone,
    cep character varying(255),
    sexo character varying(255),
    cargoconjuge character varying(255),
    banco character varying(255),
    agencia character varying(255),
    conta character varying(255),
    nomecc character varying(255),
    telresidencialconjuge character varying(255),
    telcelularconjuge character varying(255),
    dtnascimentoconjuge timestamp without time zone,
    cpfcc character varying(255),
    cnpjcc character varying(255),
    atividade character varying(255),
    nomeconjuge character varying(255),
    cpfconjuge character varying(255),
    rgconjuge character varying(255),
    dataemissaorgconjuge timestamp without time zone,
    sexoconjuge character varying(255),
    casado boolean,
    estadocivil character varying(255),
    coobrigado boolean,
    idiugu character varying(255),
    iuguaccountid character varying(255),
    iugunameaccount character varying(255),
    iuguliveapitoken character varying(255),
    iugutestapitoken character varying(255),
    iuguusertoken character varying(255),
    site character varying(255),
    contato character varying(255),
    enderecoconjuge character varying(255),
    bairroconjuge character varying(255),
    complementoconjuge character varying(255),
    cidadeconjuge character varying(255),
    estadoconjuge character varying(255),
    cepconjuge character varying(255),
    emailconjuge character varying(255),
    nomecoobrigado character varying(255),
    cpfcoobrigado character varying(255),
    rgcoobrigado character varying(255),
    enderecocoobrigado character varying(255),
    bairrocoobrigado character varying(255),
    complementocoobrigado character varying(255),
    cidadecoobrigado character varying(255),
    estadocoobrigado character varying(255),
    cepcoobrigado character varying(255),
    cargocoobrigado character varying(255),
    emailcoobrigado character varying(255),
    dataemissaorgcoobrigado timestamp without time zone,
    nomecoobrigadocasado character varying(255),
    cpfcoobrigadocasado character varying(255),
    rgcoobrigadocasado character varying(255),
    dataemissaorgcoobrigadocasado timestamp without time zone,
    telresidencialcoobrigadocasado character varying(255),
    telcelularcoobrigadocasado character varying(255),
    cargocoobrigadocasado character varying(255),
    dtnascimentocoobrigadocasado timestamp without time zone,
    sexocoobrigadocasado character varying(255),
    enderecocoobrigadocasado character varying(255),
    bairrocoobrigadocasado character varying(255),
    complementocoobrigadocasado character varying(255),
    cidadecoobrigadocasado character varying(255),
    estadocoobrigadocasado character varying(255),
    cepcoobrigadocasado character varying(255),
    emailcoobrigadocasado character varying(255),
    dtnascimentocoobrigado timestamp without time zone,
    telresidencialcoobrigado character varying(255),
    telcelularcoobrigado character varying(255),
    sexocoobrigado character varying(255),
    estadocivilcoobrigado character varying(255)
);


ALTER TABLE cobranca.pagadorrecebedor OWNER TO webnowbr;

--
-- Name: responsavel; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE responsavel (
    id bigint NOT NULL,
    codigo character varying(255),
    nome character varying(255) NOT NULL,
    endereco character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    cidade character varying(255),
    estado character varying(255),
    contato character varying(255),
    telresidencial character varying(255),
    telcelular character varying(255),
    email character varying(255),
    dtnascimento timestamp without time zone,
    observacao character varying(255),
    cpf character varying(255),
    cnpj character varying(255),
    rg character varying(255),
    cep character varying(255)
);


ALTER TABLE cobranca.responsavel OWNER TO webnowbr;

--
-- Name: saqueiugu; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE saqueiugu (
    id bigint NOT NULL,
    idsaque character varying(255),
    idaccountiugu character varying(255),
    status character varying(255),
    amount character varying(255),
    created_at timestamp without time zone
);


ALTER TABLE cobranca.saqueiugu OWNER TO webnowbr;

--
-- Name: transferenciasobservacoesiugu; Type: TABLE; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

CREATE TABLE transferenciasobservacoesiugu (
    id bigint NOT NULL,
    idtransferencia character varying(255),
    observacao character varying(255)
);


ALTER TABLE cobranca.transferenciasobservacoesiugu OWNER TO webnowbr;

SET search_path = infra, pg_catalog;

--
-- Name: groupadm; Type: TABLE; Schema: infra; Owner: webnowbr; Tablespace: 
--

CREATE TABLE groupadm (
    id bigint NOT NULL,
    acronym character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    addinfo character varying(255),
    degree integer,
    enabled boolean
);


ALTER TABLE infra.groupadm OWNER TO webnowbr;

--
-- Name: parametros; Type: TABLE; Schema: infra; Owner: webnowbr; Tablespace: 
--

CREATE TABLE parametros (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    valorstring character varying(255),
    valorint integer,
    valorlong bigint,
    valorbigdecimal numeric(19,2),
    valorboolean boolean
);


ALTER TABLE infra.parametros OWNER TO webnowbr;

--
-- Name: user_group; Type: TABLE; Schema: infra; Owner: webnowbr; Tablespace: 
--

CREATE TABLE user_group (
    user_id bigint NOT NULL,
    group_id bigint NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE infra.user_group OWNER TO webnowbr;

--
-- Name: users; Type: TABLE; Schema: infra; Owner: webnowbr; Tablespace: 
--

CREATE TABLE users (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    levelusr integer NOT NULL,
    login character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    addinfo character varying(255),
    path character varying(255),
    administrador boolean,
    userposto boolean,
    userlocacao boolean,
    usercobranca boolean,
    usercobrancaedita boolean,
    usercobrancabaixa boolean,
    usercobrancaiugu boolean,
    userprecontrato boolean,
    useriuguposto boolean,
    codigoresponsavel character varying(255),
    ip character varying(255),
    ultimoacesso timestamp without time zone,
    horainiciopermissaoacesso timestamp without time zone,
    horafimpermissaoacesso timestamp without time zone
);


ALTER TABLE infra.users OWNER TO webnowbr;

--
-- Name: usuario_dias_semana_autorizados; Type: TABLE; Schema: infra; Owner: webnowbr; Tablespace: 
--

CREATE TABLE usuario_dias_semana_autorizados (
    diassemana_id bigint NOT NULL,
    diasemana character varying(255),
    seq integer NOT NULL
);


ALTER TABLE infra.usuario_dias_semana_autorizados OWNER TO webnowbr;

SET search_path = posto, pg_catalog;

--
-- Name: controleestoque; Type: TABLE; Schema: posto; Owner: webnowbr; Tablespace: 
--

CREATE TABLE controleestoque (
    id bigint NOT NULL,
    dataregistro timestamp without time zone,
    posto bigint,
    abertura numeric(19,2),
    comprou numeric(19,2),
    vendeu numeric(19,2),
    fechamento numeric(19,2),
    observacao character varying(255)
);


ALTER TABLE posto.controleestoque OWNER TO webnowbr;

--
-- Name: controleestoqueunificado; Type: TABLE; Schema: posto; Owner: webnowbr; Tablespace: 
--

CREATE TABLE controleestoqueunificado (
    id bigint NOT NULL,
    dataregistro timestamp without time zone,
    postounificado bigint,
    abertura_etanol numeric(19,2),
    abertura_etanol_2 numeric(19,2),
    comprou_etanol numeric(19,2),
    vendeu_etanol numeric(19,2),
    fechamento_etanol numeric(19,2),
    fechamento_etanol_2 numeric(19,2),
    saldo_etanol numeric(19,2),
    conferencia_etanol numeric(19,2),
    abertura_gasolina numeric(19,2),
    comprou_gasolina numeric(19,2),
    vendeu_gasolina numeric(19,2),
    fechamento_gasolina numeric(19,2),
    saldo_gasolina numeric(19,2),
    conferencia_gasolina numeric(19,2),
    abertura_gasolina_aditivada numeric(19,2),
    comprou_gasolina_aditivada numeric(19,2),
    vendeu_gasolina_aditivada numeric(19,2),
    fechamento_gasolina_aditivada numeric(19,2),
    saldo_gasolina_aditivada numeric(19,2),
    conferencia_gasolina_aditivada numeric(19,2),
    observacao character varying(255),
    observacao_etanol character varying(255),
    observacao_gasolina character varying(255),
    observacao_gasolina_aditivada character varying(255)
);


ALTER TABLE posto.controleestoqueunificado OWNER TO webnowbr;

--
-- Name: posto; Type: TABLE; Schema: posto; Owner: webnowbr; Tablespace: 
--

CREATE TABLE posto (
    id bigint NOT NULL,
    nome character varying(255),
    combustivel character varying(255),
    observacao character varying(255)
);


ALTER TABLE posto.posto OWNER TO webnowbr;

--
-- Name: postounificado; Type: TABLE; Schema: posto; Owner: webnowbr; Tablespace: 
--

CREATE TABLE postounificado (
    id bigint NOT NULL,
    nome character varying(255),
    observacao character varying(255)
);


ALTER TABLE posto.postounificado OWNER TO webnowbr;

SET search_path = reservarimovel, pg_catalog;

--
-- Name: imovel; Type: TABLE; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

CREATE TABLE imovel (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    endereco character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    cidade character varying(255),
    estado character varying(255),
    telresidencial character varying(255),
    observacao character varying(255),
    cep character varying(255),
    excluido boolean
);


ALTER TABLE reservarimovel.imovel OWNER TO webnowbr;

--
-- Name: locador; Type: TABLE; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

CREATE TABLE locador (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    endereco character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    cidade character varying(255),
    estado character varying(255),
    telresidencial character varying(255),
    telcelular character varying(255),
    email character varying(255),
    dtnascimento timestamp without time zone,
    observacao character varying(255),
    cpf character varying(255),
    rg character varying(255),
    cep character varying(255)
);


ALTER TABLE reservarimovel.locador OWNER TO webnowbr;

--
-- Name: reservarimovel; Type: TABLE; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

CREATE TABLE reservarimovel (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    endereco character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    cidade character varying(255),
    estado character varying(255),
    telresidencial character varying(255),
    telcelular character varying(255),
    email character varying(255),
    origem character varying(255),
    dtnascimento timestamp without time zone,
    observacao character varying(255),
    cpf character varying(255),
    rg character varying(255),
    dataentrada timestamp without time zone,
    datasaida timestamp without time zone,
    contratogerado boolean,
    imovel bigint,
    locador bigint,
    datacontrato timestamp without time zone,
    formapagamento character varying(255),
    valortotal numeric(19,2),
    parcelado smallint,
    pathcontrato character varying(255),
    nomecontrato character varying(255),
    hospedeadulto smallint,
    hospedecrianca smallint,
    taxa1 numeric(19,2),
    taxa2 numeric(19,2),
    data1 timestamp without time zone,
    data2 timestamp without time zone,
    data3 timestamp without time zone,
    data4 timestamp without time zone,
    data5 timestamp without time zone,
    data6 timestamp without time zone,
    data7 timestamp without time zone,
    data8 timestamp without time zone,
    data9 timestamp without time zone,
    data10 timestamp without time zone,
    valor1 numeric(19,2),
    valor2 numeric(19,2),
    valor3 numeric(19,2),
    valor4 numeric(19,2),
    valor5 numeric(19,2),
    valor6 numeric(19,2),
    valor7 numeric(19,2),
    valor8 numeric(19,2),
    valor9 numeric(19,2),
    valor10 numeric(19,2),
    cep character varying(255),
    parcelabaixada1 boolean,
    parcelabaixada2 boolean,
    parcelabaixada3 boolean,
    parcelabaixada4 boolean,
    parcelabaixada5 boolean,
    parcelabaixada6 boolean,
    parcelabaixada7 boolean,
    parcelabaixada8 boolean,
    parcelabaixada9 boolean,
    parcelabaixada10 boolean,
    numerocontrato character varying(255),
    urlfaturaiugu1 character varying(255),
    urlfaturaiugu2 character varying(255),
    urlfaturaiugu3 character varying(255),
    urlfaturaiugu4 character varying(255),
    urlfaturaiugu5 character varying(255),
    urlfaturaiugu6 character varying(255),
    urlfaturaiugu7 character varying(255),
    urlfaturaiugu8 character varying(255),
    urlfaturaiugu9 character varying(255),
    urlfaturaiugu10 character varying(255)
);


ALTER TABLE reservarimovel.reservarimovel OWNER TO webnowbr;

SET search_path = cobranca, pg_catalog;

--
-- Data for Name: boletosremessa; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: calculos; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: calculos_detalhes_join; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: calculosdetalhes; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: cobranca_detalhes_parcial_join; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobranca; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobranca_detalhes_join; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobranca_favorecidos_join; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobranca_observacoes_join; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobrancadetalhes; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobrancadetalhes_observacoes_join; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobrancadetalhesobservacoes; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobrancadetalhesparcial; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobrancafavorecidos; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: contratocobrancaobservacoes; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: empresacobranca; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: filainvestidores; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: gruposfavorecidos; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: grupospagadores; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: imovelcobranca; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: pagadorrecebedor; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: responsavel; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: saqueiugu; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



--
-- Data for Name: transferenciasobservacoesiugu; Type: TABLE DATA; Schema: cobranca; Owner: webnowbr
--



SET search_path = infra, pg_catalog;

--
-- Data for Name: groupadm; Type: TABLE DATA; Schema: infra; Owner: webnowbr
--

INSERT INTO groupadm VALUES (1, 'ROOT', 'Administration', 'Executa ações de gerenciamento dos operadores do sistema', -1, true);
INSERT INTO groupadm VALUES (2, 'POSTO', 'Administration', 'Executa ações de gerenciamento de posto', 0, true);
INSERT INTO groupadm VALUES (3, 'LOCACAO', 'Administration', 'Executa ações de gerenciamento de locação de imóveis', 0, true);
INSERT INTO groupadm VALUES (4, 'COBRANCA', 'Administration', 'Executa ações de gerenciamento de cobrança.', 0, true);
INSERT INTO groupadm VALUES (5, 'COBRANCA_BAIXA', 'Administration', 'Executa ações de gerenciamento de cobrança', 0, true);
INSERT INTO groupadm VALUES (6, 'COBRANCA_EDITA', 'Administration', 'Executa ações de gerenciamento de cobrança', 0, true);
INSERT INTO groupadm VALUES (7, 'PRECOBRANCA', 'Administration', 'Executa ações de gerenciamento de cobrança', 0, true);
INSERT INTO groupadm VALUES (8, 'IUGU_POSTO', 'Administration', 'Executa ações de gerenciamento de cobrança', 0, true);
INSERT INTO groupadm VALUES (9, 'COBRANCA_IUGU', 'Administration', 'Executa ações de gerenciamento de cobrança	
', 0, true);


--
-- Data for Name: parametros; Type: TABLE DATA; Schema: infra; Owner: webnowbr
--

INSERT INTO parametros VALUES (2, 'POSTO_DIAS_POSTERIOR_APONTAMENTO', '', 20, 0, 0.00, false);
INSERT INTO parametros VALUES (5, 'LOCACAO_HR_CHECKIN', '12:00:00', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (6, 'LOCACAO_HR_CHECKOUT', '08:00:00', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (1, 'POSTO_DIAS_ANTERIOR_APONTAMENTO', '', 40, 0, 0.00, false);
INSERT INTO parametros VALUES (7, 'COBRANCA_TX_ADMINISTRACAO', '', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (9, 'COBRANCA_REC_TX_JUROS', '', 0, 0, 10.00, false);
INSERT INTO parametros VALUES (10, 'COBRANCA_REC_MULTA', '', 0, 0, 2.00, false);
INSERT INTO parametros VALUES (11, 'COBRANCA_DOCUMENTOS', '/home/webnowbr/Siscoat/GalleriaFinancas/DocumentosCobranca/', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (4, 'LOCACAO_PATH_CONTRATO', '/home/webnowbr/Siscoat/GalleriaFinancas/ContratosLocacao/', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (8, 'LOCACAO_PATH_COBRANCA', '/home/webnowbr/Siscoat/GalleriaFinancas/ContratosCobranca/', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (12, 'LOCACAO_PATH_BOLETO', '/home/webnowbr/Siscoat/GalleriaFinancas/BoletosLocacao/', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (13, 'COBRANCA_PATH_BOLETO', '/home/webnowbr/Siscoat/GalleriaFinancas/BoletosCobranca/', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (14, 'BOLETO_REMESSAS', '/home/webnowbr/Siscoat/GalleriaFinancas/BoletosRemessas/', 0, 0, 0.00, false);
INSERT INTO parametros VALUES (15, 'RECIBOS_IUGU', '/home/webnowbr/Siscoat/GalleriaFinancas/RecibosIUGU/	', 0, 0, 0.00, false);


--
-- Data for Name: user_group; Type: TABLE DATA; Schema: infra; Owner: webnowbr
--

INSERT INTO user_group VALUES (1, 1, 0);
INSERT INTO user_group VALUES (2, 1, 0);
INSERT INTO user_group VALUES (1, 2, 1);
INSERT INTO user_group VALUES (1, 3, 2);
INSERT INTO user_group VALUES (1, 4, 3);
INSERT INTO user_group VALUES (1, 6, 4);
INSERT INTO user_group VALUES (1, 5, 5);
INSERT INTO user_group VALUES (2, 2, 1);
INSERT INTO user_group VALUES (2, 3, 2);
INSERT INTO user_group VALUES (2, 4, 3);
INSERT INTO user_group VALUES (2, 6, 4);
INSERT INTO user_group VALUES (2, 5, 5);
INSERT INTO user_group VALUES (2, 7, 6);
INSERT INTO user_group VALUES (1, 7, 6);


--
-- Data for Name: users; Type: TABLE DATA; Schema: infra; Owner: webnowbr
--

INSERT INTO users VALUES (1, 'Administrador', 0, 'sandro', 'qLRIWtRVFEKzqbWVqWpNP7zhaej8GZUV', 'Master', NULL, true, true, true, true, true, true, false, true, false, '', '', '2019-10-07 19:24:08.255', NULL, NULL);
INSERT INTO users VALUES (2, 'Hermes', 0, 'hermes', 'cEQ08Kp7YcjxdSvO0U3GElsnplBnXd6d', NULL, NULL, true, true, true, true, true, true, false, true, false, 'HVJ', NULL, '2019-10-08 18:26:13.057', NULL, NULL);


--
-- Data for Name: usuario_dias_semana_autorizados; Type: TABLE DATA; Schema: infra; Owner: webnowbr
--



SET search_path = posto, pg_catalog;

--
-- Data for Name: controleestoque; Type: TABLE DATA; Schema: posto; Owner: webnowbr
--



--
-- Data for Name: controleestoqueunificado; Type: TABLE DATA; Schema: posto; Owner: webnowbr
--



--
-- Data for Name: posto; Type: TABLE DATA; Schema: posto; Owner: webnowbr
--



--
-- Data for Name: postounificado; Type: TABLE DATA; Schema: posto; Owner: webnowbr
--



SET search_path = reservarimovel, pg_catalog;

--
-- Data for Name: imovel; Type: TABLE DATA; Schema: reservarimovel; Owner: webnowbr
--



--
-- Data for Name: locador; Type: TABLE DATA; Schema: reservarimovel; Owner: webnowbr
--



--
-- Data for Name: reservarimovel; Type: TABLE DATA; Schema: reservarimovel; Owner: webnowbr
--



SET search_path = cobranca, pg_catalog;

--
-- Name: boletosremessa_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY boletosremessa
    ADD CONSTRAINT boletosremessa_pkey PRIMARY KEY (id);


--
-- Name: calculos_detalhes_join_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY calculos_detalhes_join
    ADD CONSTRAINT calculos_detalhes_join_pkey PRIMARY KEY (idcalculos, idx);


--
-- Name: calculos_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY calculos
    ADD CONSTRAINT calculos_pkey PRIMARY KEY (id);


--
-- Name: calculosdetalhes_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY calculosdetalhes
    ADD CONSTRAINT calculosdetalhes_pkey PRIMARY KEY (id);


--
-- Name: cobranca_detalhes_parcial_join_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY cobranca_detalhes_parcial_join
    ADD CONSTRAINT cobranca_detalhes_parcial_join_pkey PRIMARY KEY (idcontratocobrancadetalhes, idx);


--
-- Name: contratocobranca_detalhes_join_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobranca_detalhes_join
    ADD CONSTRAINT contratocobranca_detalhes_join_pkey PRIMARY KEY (idcontratocobranca, idx);


--
-- Name: contratocobranca_favorecidos_join_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobranca_favorecidos_join
    ADD CONSTRAINT contratocobranca_favorecidos_join_pkey PRIMARY KEY (idcontratocobrancadetalhes, idx);


--
-- Name: contratocobranca_observacoes_join_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobranca_observacoes_join
    ADD CONSTRAINT contratocobranca_observacoes_join_pkey PRIMARY KEY (idcontratocobranca, idx);


--
-- Name: contratocobranca_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT contratocobranca_pkey PRIMARY KEY (id);


--
-- Name: contratocobrancadetalhes_observacoes_join_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobrancadetalhes_observacoes_join
    ADD CONSTRAINT contratocobrancadetalhes_observacoes_join_pkey PRIMARY KEY (idcontratocobrancadetalhes, idx);


--
-- Name: contratocobrancadetalhes_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobrancadetalhes
    ADD CONSTRAINT contratocobrancadetalhes_pkey PRIMARY KEY (id);


--
-- Name: contratocobrancadetalhesobservacoes_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobrancadetalhesobservacoes
    ADD CONSTRAINT contratocobrancadetalhesobservacoes_pkey PRIMARY KEY (id);


--
-- Name: contratocobrancadetalhesparcial_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobrancadetalhesparcial
    ADD CONSTRAINT contratocobrancadetalhesparcial_pkey PRIMARY KEY (id);


--
-- Name: contratocobrancafavorecidos_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobrancafavorecidos
    ADD CONSTRAINT contratocobrancafavorecidos_pkey PRIMARY KEY (id);


--
-- Name: contratocobrancaobservacoes_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY contratocobrancaobservacoes
    ADD CONSTRAINT contratocobrancaobservacoes_pkey PRIMARY KEY (id);


--
-- Name: empresacobranca_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY empresacobranca
    ADD CONSTRAINT empresacobranca_pkey PRIMARY KEY (id);


--
-- Name: filainvestidores_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY filainvestidores
    ADD CONSTRAINT filainvestidores_pkey PRIMARY KEY (id);


--
-- Name: gruposfavorecidos_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT gruposfavorecidos_pkey PRIMARY KEY (id);


--
-- Name: grupospagadores_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT grupospagadores_pkey PRIMARY KEY (id);


--
-- Name: imovelcobranca_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY imovelcobranca
    ADD CONSTRAINT imovelcobranca_pkey PRIMARY KEY (id);


--
-- Name: pagadorrecebedor_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY pagadorrecebedor
    ADD CONSTRAINT pagadorrecebedor_pkey PRIMARY KEY (id);


--
-- Name: responsavel_nome_key; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY responsavel
    ADD CONSTRAINT responsavel_nome_key UNIQUE (nome);


--
-- Name: responsavel_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY responsavel
    ADD CONSTRAINT responsavel_pkey PRIMARY KEY (id);


--
-- Name: saqueiugu_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY saqueiugu
    ADD CONSTRAINT saqueiugu_pkey PRIMARY KEY (id);


--
-- Name: transferenciasobservacoesiugu_pkey; Type: CONSTRAINT; Schema: cobranca; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY transferenciasobservacoesiugu
    ADD CONSTRAINT transferenciasobservacoesiugu_pkey PRIMARY KEY (id);


SET search_path = infra, pg_catalog;

--
-- Name: groupadm_acronym_key; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY groupadm
    ADD CONSTRAINT groupadm_acronym_key UNIQUE (acronym);


--
-- Name: groupadm_pkey; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY groupadm
    ADD CONSTRAINT groupadm_pkey PRIMARY KEY (id);


--
-- Name: parametros_nome_key; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY parametros
    ADD CONSTRAINT parametros_nome_key UNIQUE (nome);


--
-- Name: parametros_pkey; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY parametros
    ADD CONSTRAINT parametros_pkey PRIMARY KEY (id);


--
-- Name: user_group_pkey; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY user_group
    ADD CONSTRAINT user_group_pkey PRIMARY KEY (user_id, idx);


--
-- Name: users_login_key; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_login_key UNIQUE (login);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: usuario_dias_semana_autorizados_pkey; Type: CONSTRAINT; Schema: infra; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY usuario_dias_semana_autorizados
    ADD CONSTRAINT usuario_dias_semana_autorizados_pkey PRIMARY KEY (diassemana_id, seq);


SET search_path = posto, pg_catalog;

--
-- Name: controleestoque_pkey; Type: CONSTRAINT; Schema: posto; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY controleestoque
    ADD CONSTRAINT controleestoque_pkey PRIMARY KEY (id);


--
-- Name: controleestoqueunificado_pkey; Type: CONSTRAINT; Schema: posto; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY controleestoqueunificado
    ADD CONSTRAINT controleestoqueunificado_pkey PRIMARY KEY (id);


--
-- Name: posto_pkey; Type: CONSTRAINT; Schema: posto; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY posto
    ADD CONSTRAINT posto_pkey PRIMARY KEY (id);


--
-- Name: postounificado_pkey; Type: CONSTRAINT; Schema: posto; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY postounificado
    ADD CONSTRAINT postounificado_pkey PRIMARY KEY (id);


SET search_path = reservarimovel, pg_catalog;

--
-- Name: imovel_nome_key; Type: CONSTRAINT; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY imovel
    ADD CONSTRAINT imovel_nome_key UNIQUE (nome);


--
-- Name: imovel_pkey; Type: CONSTRAINT; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY imovel
    ADD CONSTRAINT imovel_pkey PRIMARY KEY (id);


--
-- Name: locador_nome_key; Type: CONSTRAINT; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY locador
    ADD CONSTRAINT locador_nome_key UNIQUE (nome);


--
-- Name: locador_pkey; Type: CONSTRAINT; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY locador
    ADD CONSTRAINT locador_pkey PRIMARY KEY (id);


--
-- Name: reservarimovel_pkey; Type: CONSTRAINT; Schema: reservarimovel; Owner: webnowbr; Tablespace: 
--

ALTER TABLE ONLY reservarimovel
    ADD CONSTRAINT reservarimovel_pkey PRIMARY KEY (id);


SET search_path = cobranca, pg_catalog;

--
-- Name: fk203575ea1a09f2c0; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca_favorecidos_join
    ADD CONSTRAINT fk203575ea1a09f2c0 FOREIGN KEY (idcontratocobrancadetalhes) REFERENCES contratocobrancadetalhes(id);


--
-- Name: fk203575eafaf94426; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca_favorecidos_join
    ADD CONSTRAINT fk203575eafaf94426 FOREIGN KEY (idcontratocobrancafavorecidos) REFERENCES contratocobrancafavorecidos(id);


--
-- Name: fk23ec0a90f34f4d1; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY calculos
    ADD CONSTRAINT fk23ec0a90f34f4d1 FOREIGN KEY (recebedor) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611d93e5b30; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611d93e5b30 FOREIGN KEY (recebedor10) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e5854004; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e5854004 FOREIGN KEY (recebedor1) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e5854005; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e5854005 FOREIGN KEY (recebedor2) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e5854006; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e5854006 FOREIGN KEY (recebedor3) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e5854007; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e5854007 FOREIGN KEY (recebedor4) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e5854008; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e5854008 FOREIGN KEY (recebedor5) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e5854009; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e5854009 FOREIGN KEY (recebedor6) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e585400a; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e585400a FOREIGN KEY (recebedor7) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e585400b; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e585400b FOREIGN KEY (recebedor8) REFERENCES pagadorrecebedor(id);


--
-- Name: fk29ff0611e585400c; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY gruposfavorecidos
    ADD CONSTRAINT fk29ff0611e585400c FOREIGN KEY (recebedor9) REFERENCES pagadorrecebedor(id);


--
-- Name: fk582b0eb3f33238f0; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca_observacoes_join
    ADD CONSTRAINT fk582b0eb3f33238f0 FOREIGN KEY (idcontratocobranca) REFERENCES contratocobranca(id);


--
-- Name: fk582b0eb3fe35df54; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca_observacoes_join
    ADD CONSTRAINT fk582b0eb3fe35df54 FOREIGN KEY (idcontratocobrancaobservacoes) REFERENCES contratocobrancaobservacoes(id);


--
-- Name: fk627a89bb1a09f2c0; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobrancadetalhes_observacoes_join
    ADD CONSTRAINT fk627a89bb1a09f2c0 FOREIGN KEY (idcontratocobrancadetalhes) REFERENCES contratocobrancadetalhes(id);


--
-- Name: fk627a89bb9bd6e384; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobrancadetalhes_observacoes_join
    ADD CONSTRAINT fk627a89bb9bd6e384 FOREIGN KEY (idcontratocobrancadetalhesobservacoes) REFERENCES contratocobrancadetalhesobservacoes(id);


--
-- Name: fk7f6107fb488b3898; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobrancadetalhes
    ADD CONSTRAINT fk7f6107fb488b3898 FOREIGN KEY (cedenteiugu) REFERENCES pagadorrecebedor(id);


--
-- Name: fk9f00b2b7f34f4d1; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobrancadetalhesparcial
    ADD CONSTRAINT fk9f00b2b7f34f4d1 FOREIGN KEY (recebedor) REFERENCES pagadorrecebedor(id);


--
-- Name: fk9f5de3261a09f2c0; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY cobranca_detalhes_parcial_join
    ADD CONSTRAINT fk9f5de3261a09f2c0 FOREIGN KEY (idcontratocobrancadetalhes) REFERENCES contratocobrancadetalhes(id);


--
-- Name: fk9f5de3263c62b84; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY cobranca_detalhes_parcial_join
    ADD CONSTRAINT fk9f5de3263c62b84 FOREIGN KEY (idcontratocobrancadetalhesparcial) REFERENCES contratocobrancadetalhesparcial(id);


--
-- Name: fkb072b1958e66115; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY filainvestidores
    ADD CONSTRAINT fkb072b1958e66115 FOREIGN KEY (investidor) REFERENCES pagadorrecebedor(id);


--
-- Name: fkcd1297551a09f2c0; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca_detalhes_join
    ADD CONSTRAINT fkcd1297551a09f2c0 FOREIGN KEY (idcontratocobrancadetalhes) REFERENCES contratocobrancadetalhes(id);


--
-- Name: fkcd129755f33238f0; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca_detalhes_join
    ADD CONSTRAINT fkcd129755f33238f0 FOREIGN KEY (idcontratocobranca) REFERENCES contratocobranca(id);


--
-- Name: fkd884a280140dd55d; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a280140dd55d FOREIGN KEY (pagador10) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c37; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c37 FOREIGN KEY (pagador1) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c38; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c38 FOREIGN KEY (pagador2) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c39; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c39 FOREIGN KEY (pagador3) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c3a; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c3a FOREIGN KEY (pagador4) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c3b; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c3b FOREIGN KEY (pagador5) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c3c; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c3c FOREIGN KEY (pagador6) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c3d; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c3d FOREIGN KEY (pagador7) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c3e; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c3e FOREIGN KEY (pagador8) REFERENCES pagadorrecebedor(id);


--
-- Name: fkd884a28018f74c3f; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY grupospagadores
    ADD CONSTRAINT fkd884a28018f74c3f FOREIGN KEY (pagador9) REFERENCES pagadorrecebedor(id);


--
-- Name: fkebb57ae22b0363aa; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY calculos_detalhes_join
    ADD CONSTRAINT fkebb57ae22b0363aa FOREIGN KEY (idcalculos) REFERENCES calculos(id);


--
-- Name: fkebb57ae2e4c9c37a; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY calculos_detalhes_join
    ADD CONSTRAINT fkebb57ae2e4c9c37a FOREIGN KEY (idcalculosdetalhes) REFERENCES calculosdetalhes(id);


--
-- Name: fkfee1482353869346; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee1482353869346 FOREIGN KEY (recebedorparcelafinal1) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee1482353869347; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee1482353869347 FOREIGN KEY (recebedorparcelafinal2) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee1482353869348; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee1482353869348 FOREIGN KEY (recebedorparcelafinal3) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee1482353869349; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee1482353869349 FOREIGN KEY (recebedorparcelafinal4) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee148235386934a; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee148235386934a FOREIGN KEY (recebedorparcelafinal5) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823ad4cd03a; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823ad4cd03a FOREIGN KEY (imovel) REFERENCES imovelcobranca(id);


--
-- Name: fkfee14823b607163e; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823b607163e FOREIGN KEY (pagador) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823d93e5b30; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823d93e5b30 FOREIGN KEY (recebedor10) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e5854005; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e5854005 FOREIGN KEY (recebedor2) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e5854006; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e5854006 FOREIGN KEY (recebedor3) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e5854007; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e5854007 FOREIGN KEY (recebedor4) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e5854008; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e5854008 FOREIGN KEY (recebedor5) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e5854009; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e5854009 FOREIGN KEY (recebedor6) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e585400a; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e585400a FOREIGN KEY (recebedor7) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e585400b; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e585400b FOREIGN KEY (recebedor8) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823e585400c; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823e585400c FOREIGN KEY (recebedor9) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823f34f4d1; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823f34f4d1 FOREIGN KEY (recebedor) REFERENCES pagadorrecebedor(id);


--
-- Name: fkfee14823f46a65b1; Type: FK CONSTRAINT; Schema: cobranca; Owner: webnowbr
--

ALTER TABLE ONLY contratocobranca
    ADD CONSTRAINT fkfee14823f46a65b1 FOREIGN KEY (responsavel) REFERENCES responsavel(id);


SET search_path = infra, pg_catalog;

--
-- Name: fk7aa9686cf204b8b; Type: FK CONSTRAINT; Schema: infra; Owner: webnowbr
--

ALTER TABLE ONLY usuario_dias_semana_autorizados
    ADD CONSTRAINT fk7aa9686cf204b8b FOREIGN KEY (diassemana_id) REFERENCES users(id);


--
-- Name: fkc62e00ebc34ccbb0; Type: FK CONSTRAINT; Schema: infra; Owner: webnowbr
--

ALTER TABLE ONLY user_group
    ADD CONSTRAINT fkc62e00ebc34ccbb0 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fkc62e00ebdb1e935c; Type: FK CONSTRAINT; Schema: infra; Owner: webnowbr
--

ALTER TABLE ONLY user_group
    ADD CONSTRAINT fkc62e00ebdb1e935c FOREIGN KEY (group_id) REFERENCES groupadm(id);


SET search_path = posto, pg_catalog;

--
-- Name: fk56768ab0d20dd8a9; Type: FK CONSTRAINT; Schema: posto; Owner: webnowbr
--

ALTER TABLE ONLY controleestoque
    ADD CONSTRAINT fk56768ab0d20dd8a9 FOREIGN KEY (posto) REFERENCES posto(id);


--
-- Name: fk8a5548cc78e8708f; Type: FK CONSTRAINT; Schema: posto; Owner: webnowbr
--

ALTER TABLE ONLY controleestoqueunificado
    ADD CONSTRAINT fk8a5548cc78e8708f FOREIGN KEY (postounificado) REFERENCES postounificado(id);


SET search_path = reservarimovel, pg_catalog;

--
-- Name: fk23fb132c149d61f6; Type: FK CONSTRAINT; Schema: reservarimovel; Owner: webnowbr
--

ALTER TABLE ONLY reservarimovel
    ADD CONSTRAINT fk23fb132c149d61f6 FOREIGN KEY (locador) REFERENCES locador(id);


--
-- Name: fk23fb132c721f49ba; Type: FK CONSTRAINT; Schema: reservarimovel; Owner: webnowbr
--

ALTER TABLE ONLY reservarimovel
    ADD CONSTRAINT fk23fb132c721f49ba FOREIGN KEY (imovel) REFERENCES imovel(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

