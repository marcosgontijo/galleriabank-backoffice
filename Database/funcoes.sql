-- Function: cobranca.calculocontratoantecipado(numeric, timestamp without time zone, numeric, integer, numeric)

-- DROP FUNCTION cobranca.calculocontratoantecipado(numeric, timestamp without time zone, numeric, integer, numeric);

CREATE OR REPLACE FUNCTION cobranca.calculocontratoantecipado(
    vlrparcela numeric,
    datavencimento timestamp without time zone,
    vlrparcelafinal numeric,
    qtdmeses integer,
    taxajuros numeric)
  RETURNS numeric AS
$BODY$
DECLARE 
       coeficiente numeric;
       iParcela integer  = 0;
       valoratual numeric(19,2) = 0;
BEGIN
	coeficiente = DATE_PART('day',  date_trunc('day',datavencimento ) - date_trunc('day',now()) )   / 30.0;	
	IF ( qtdmeses > 0 ) THEN
		LOOP
			valoratual = valoratual + (vlrparcela/(1.0+ taxaJuros/100)^( iParcela + coeficiente));
			iParcela = iParcela + 1;
			IF iParcela = qtdmeses THEN
				EXIT;  -- exit loop
			END IF;
		END LOOP;
	END IF;
	valoratual = valoratual + (vlrparcelafinal/(1.0+ taxaJuros/100)^( iParcela + coeficiente));
	
	return valoratual;	
END;


$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cobranca.calculocontratoantecipado(numeric, timestamp without time zone, numeric, integer, numeric)
  OWNER TO postgres;

-- Function: cobranca.calculocontratofaltavender(bigint, numeric, timestamp without time zone, numeric, integer, numeric)

-- DROP FUNCTION cobranca.calculocontratofaltavender(bigint, numeric, timestamp without time zone, numeric, integer, numeric);

CREATE OR REPLACE FUNCTION cobranca.calculocontratofaltavender(
    idcontratocobranca bigint,
    vlrparcela numeric,
    datavencimento timestamp without time zone,
    vlrparcelafinal numeric,
    qtdmeses integer,
    taxajuros numeric)
  RETURNS numeric AS
$BODY$
DECLARE 
       coeficiente numeric;
       iParcela integer  = 0;
       valoratual numeric = 0;
       valorvendidomes numeric = 0;
       dataloop timestamp;
BEGIN
	dataloop =   date_trunc('month', datavencimento);
	coeficiente = 1; --DATE_PART('day',  date_trunc('day',datavencimento ) - date_trunc('day',now()) )   / 30.0;	
	IF ( qtdmeses > 0 ) THEN
		LOOP
			select cobranca.valorVendidoMes( idcontratocobranca,  recebedor2, 2, true,   dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor3, 3, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor4, 4, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor5, 5, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor6, 6, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor7, 7, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor8, 8, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor9, 9, true, dataloop)
			+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor10,10, true,dataloop)
			into valorvendidomes
			from  cobranca.contratocobranca 
			where id = idcontratocobranca;
			
			valoratual = valoratual +  ( (vlrparcela - valorvendidomes)/(1.0+ taxaJuros/100)^( iParcela + coeficiente));
			iParcela = iParcela + 1;
			dataloop = dataloop + interval '1' month;
			IF iParcela = qtdmeses THEN
				EXIT;  -- exit loop
			END IF;
		END LOOP;
	END IF;

	select cobranca.valorVendidoMes( idcontratocobranca,  recebedor2, 2, true,   dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor3, 3, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor4, 4, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor5, 5, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor6, 6, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor7, 7, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor8, 8, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor9, 9, true, dataloop)
		+  cobranca.valorVendidoMes( idcontratocobranca,  recebedor10,10, true,dataloop)
		into valorvendidomes
		from  cobranca.contratocobranca 
		where id = idcontratocobranca;

	valoratual = valoratual + ((vlrparcelafinal - valorvendidomes)/(1.0+ taxaJuros/100)^( iParcela + coeficiente));	
	return valoratual::numeric(19,2);	
END;


$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cobranca.calculocontratofaltavender(bigint, numeric, timestamp without time zone, numeric, integer, numeric)
  OWNER TO postgres;

-- Function: cobranca.contratoemdia(bigint)

-- DROP FUNCTION cobranca.contratoemdia(bigint);

CREATE OR REPLACE FUNCTION cobranca.contratoemdia(idcontratocobranca bigint)
  RETURNS boolean AS
$BODY$
BEGIN
	IF  ( select count(*)
		from cobranca.contratocobranca_detalhes_join cbdj
		inner join cobranca.contratocobrancadetalhes cbd on cbdj.idcontratocobrancadetalhes = cbd.id
		where  cbdj.idcontratocobranca =  452 and parcelapaga = false and datavencimento < now()) > 0 THEN					
		return false;
	ELSE
		return true;
	END IF;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cobranca.contratoemdia(bigint)
  OWNER TO postgres;

-- Function: cobranca.valorvendidomes(bigint, bigint, integer, boolean, timestamp without time zone)

-- DROP FUNCTION cobranca.valorvendidomes(bigint, bigint, integer, boolean, timestamp without time zone);

CREATE OR REPLACE FUNCTION cobranca.valorvendidomes(
    idcontratocobranca bigint,
    idinvestidor bigint,
    seqinvestidor integer,
    nulliszero boolean,
    datavencimento timestamp without time zone)
  RETURNS numeric AS
$BODY$
DECLARE
	retorno numeric(19,2);
	strSql varchar(1000);
BEGIN
	IF idinvestidor IS NULL THEN					
		retorno = null;
	ELSE
	EXECUTE 'select parcelamensal
	  from  cobranca.contratocobrancaparcelasinvestidor  ccpi
	  inner join cobranca.contratocobranca_parcelas_investidor_join_' || seqInvestidor  || ' cpij on ccpi.id = cpij.idcontratocobrancaparcelasinvestidor
	  where   investidor  = $2 and  date_trunc(''month'',datavencimento) =   date_trunc(''month'',$4)
	  and  cpij.idcontratocobrancaparcelasinvestidor' || seqInvestidor  || ' = $1 ;'  USING idcontratocobranca, idinvestidor , seqInvestidor, datavencimento INTO retorno;	  
	
	END IF;

	if ( nullIsZero ) then
		return COALESCE( retorno , 0);
	else			
		return retorno;
	end if;
	

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cobranca.valorvendidomes(bigint, bigint, integer, boolean, timestamp without time zone)
  OWNER TO postgres;
