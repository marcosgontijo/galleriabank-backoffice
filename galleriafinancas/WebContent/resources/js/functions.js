/*
 * @Version: 0.1
 */
function SomenteNumero(e){	var tecla=(window.event)?event.keyCode:e.which;   	if((tecla>47 && tecla<58)) return true;	else{		if (tecla==8 || tecla==0) return true;		else  return false;	}}function mascaraTelefone( campo ) {	function trata( valor,  isOnBlur ) {		if( valor.length >= 15 ) {			return valor;		}		valor = valor.replace(/\D/g,"");             					valor = valor.replace(/^(\d{2})(\d)/g,"($1) $2"); 				if( isOnBlur ) {			valor = valor.replace(/(\d)(\d{4})$/,"$1-$2");   		} else {			valor = valor.replace(/(\d)(\d{3})$/,"$1-$2"); 		}		return valor;	}	campo.onkeypress = function (evt) {		var code = (window.event)? window.event.keyCode : evt.which;			var valor = this.value;		if(code > 57 || (code < 48 && code != 8 ))  {			return false;		} else {			this.value = trata(valor, false);		}	};	campo.onblur = function() {		var valor = this.value;		if( valor.length < 14 ) {			this.value = "";		}else {					this.value = trata( this.value, true );		}	};	campo.maxLength = 15;}function formatarCampoPosto(campo) {	if(!campo.value)		return campo.value 		let valor = parseFloat(campo.value)		if (campo.value.indexOf('.') == -1) 			campo.value = valor.toLocaleString('pt-BR');}function adicionarPonto(campo) {	if(!campo.value)		return campo.value      		campo.value = Number(RemoverPonto(campo)).toLocaleString('pt-BR')}function RemoverPonto(campo){		campo.value = campo.value.split(".").join("");	return campo.value;}
/***************************************************************  
 * Rotinas utizada para formatar campos monetarios              *  
 ***************************************************************/   
function Limpar(valor, validos) {   
	var result = "";   
	var aux;   
	for (var i=0; i < valor.length; i++) {   
		aux = validos.indexOf(valor.substring(i, i+1));   
		if (aux>=0) {   
			result += aux;   
		}   
	}   
	return result;   
}

/*******************************************************************************  
 *   Formata número tipo moeda usando o evento onKeyDown  
 *   A formatação permite apagar um numero e retornar uma casa decimal.  
 *******************************************************************************/   
function FormataValor(campo,tammax,teclapres,decimal) { 

	var tecla = teclapres.keyCode;   
	vr = Limpar(campo.value,"0123456789");   
	tam = vr.length;   
	dec=decimal;

	if (tam < tammax && tecla != 8){
		tam = vr.length + 1;
	}

	if (tecla == 8 ){
		//tam = tam; //substituir para tam = tam -1; para apagar de 2 em 2 digitos
	}   

	if ( tecla == 8 || tecla >= 48 && tecla <= 57 || tecla >= 44){
		if ( tam <= dec ){
			campo.value = vr;
		}   

		if ( (tam > dec) && (tam <= 5) ){   
			campo.value = vr.substring( 0, tam - 2 ) + "," + vr.substring( tam - dec, tam );
		}   
		if ( (tam >= 6) && (tam <= 8) ){   
			campo.value = vr.substring( 0, tam - 5 ) + "." + vr.substring( tam - 5, (tam - 5) + 3 ) + "," + vr.substring( tam - dec, tam );   
		}
		if ( (tam >= 9) && (tam <= 11) ){   
			campo.value = vr.substring( 0, tam - 8 ) + "." + vr.substring( tam - 8, (tam - 8) + 3 ) + "." + vr.substring( tam - 5, (tam - 5) + 3 ) + "," + vr.substring( tam - dec, tam );
		}
		if ( (tam >= 12) && (tam <= 14) ){   
			campo.value = vr.substring( 0, tam - 11 ) + "." + vr.substring( tam - 11, (tam - 11) + 3) + "." + vr.substring( tam - 8, (tam - 8) + 3 ) + "." + vr.substring( tam - 5, (tam - 5) + 3 ) + "," + vr.substring( tam - dec, tam );
		}
		if ( (tam >= 19) && (tam <= 22) ){   
			campo.value = vr.substring( 0, tam - 18 ) + "." + vr.substring( tam - 14, 4 ) + "." + vr.substring( tam - 11, 4 ) + "." + vr.substring( tam - 8, 4 ) + "." + vr.substring( tam - 5, 4 ) + "," + vr.substring( tam - 2, tam );
		}
	}
}  
function FormataValorSemPonto(campo,tammax,teclapres,decimal) { 	var tecla = teclapres.keyCode;   	vr = Limpar(campo.value,"0123456789");   	tam = vr.length;   	dec=decimal;	if (tam < tammax && tecla != 8){		tam = vr.length + 1;	}	if (tecla == 8 ){		//tam = tam; //substituir para tam = tam -1; para apagar de 2 em 2 digitos	}   	if ( tecla == 8 || tecla >= 48 && tecla <= 57 || tecla >= 44){		if ( tam <= dec ){			campo.value = vr;		}   		if ( (tam > dec) && (tam <= 5) ){   			campo.value = vr.substring( 0, tam - 2 ) + "," + vr.substring( tam - dec, tam );		}   		if ( (tam >= 6) && (tam <= 8) ){   			campo.value = vr.substring( 0, tam - 5 ) + vr.substring( tam - 5, (tam - 5) + 3 ) + "," + vr.substring( tam - dec, tam );   		}		if ( (tam >= 9) && (tam <= 11) ){   			campo.value = vr.substring( 0, tam - 8 ) + vr.substring( tam - 8, (tam - 8) + 3 ) + "." + vr.substring( tam - 5, (tam - 5) + 3 ) + "," + vr.substring( tam - dec, tam );		}		if ( (tam >= 12) && (tam <= 14) ){   			campo.value = vr.substring( 0, tam - 11 ) + vr.substring( tam - 11, (tam - 11) + 3) + "." + vr.substring( tam - 8, (tam - 8) + 3 ) + "." + vr.substring( tam - 5, (tam - 5) + 3 ) + "," + vr.substring( tam - dec, tam );		}		if ( (tam >= 19) && (tam <= 22) ){   			campo.value = vr.substring( 0, tam - 18 ) + vr.substring( tam - 14, 4 ) + "." + vr.substring( tam - 11, 4 ) + "." + vr.substring( tam - 8, 4 ) + "." + vr.substring( tam - 5, 4 ) + "," + vr.substring( tam - 2, tam );		}	}} 
function isNumberKey(event){

	try{
		var key = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;

		if (key == 9 || key == 8 || key >= 48 && key <= 57){
			return true;
		}
	}catch(er){

	}

	return false;

}

function maskMoney(componente){
	jQuery(function($) {
		$(componente).unmaskMoney();
		$(componente).maskMoney({
			symbol : "",
			showSymbol : true,
			thousands : "",
			decimal : ".",
			symbolStay : true
		});
	});
}
(function($) {
	$.fn.maskMoney = function(settings) {
		settings = $.extend({
			symbol: "US$",
			decimal: ".",
			thousands: "",
			showSymbol:true
		}, settings);

		settings.symbol=settings.symbol+" ";

		return this.each(function(){
			var input=$(this);
			function money(e){
				e=e||window.event;
				var k=e.charCode||e.keyCode||e.which;
				if (k == 9){
					return true;
				}
				if (k == 8) {
					preventDefault(e);
					var x = input.val().substring(0,input.val().length-1);
					input.val(maskValue(x));
					return false;
				}
				if((k < 48 || k > 57)){
					preventDefault(e);
					return true;
				}
				var key = String.fromCharCode(k); // Valor para o c�digo da Chave
				preventDefault(e);
				input.val(maskValue(input.val()+key));
			}

			function preventDefault(e){
				if (e.preventDefault){ //standart browsers
					e.preventDefault()
				}else{ // internet explorer
					e.returnValue = false
				}
			}

			function maskValue(v){
				v = v.replace(settings.symbol,"");
				var a = "";
				var strCheck = "0123456789";
				var len = v.length;
				var t = "";
				if (len== 0){
					t = "0.00";
				}
				for(var i = 0; i < len; i++)
					if ((v.charAt(i) != "0") && (v.charAt(i) != settings.decimal)) break;

				for(; i < len; i++){
					if (strCheck.indexOf(v.charAt(i))!=-1) a+= v.charAt(i);
				}
				if(a.length==0){t = "0.00";}
				else if (a.length==1){t = "0.0" + a;}
				else if (a.length==2){t = "0." +a;}
				else{
					var part1 = a.substring(0,a.length-2);
					var part2 = a.substring(a.length-2);
					t = part1 + "." + part2;
				}
				var p, d = (t=t.split("."))[1].substr(0, 2);
				for(p = (t=t[0]).length; (p-=3) >= 1;) {
					t = t.substr(0,p) + settings.thousands + t.substr(p);
				}
				return setSymbol(t+settings.decimal+d+Array(3-d.length).join(0));
			}

			function focusEvent(){
				if(input.val()==""){
					input.val(setSymbol("0"+settings.decimal+"00"));
				}
				else{
					input.val(setSymbol(input.val()));
				}
			}

			function blurEvent(){
				input.val(input.val().replace(settings.symbol,""))
			}

			function setSymbol(v){
				if(settings.showSymbol){
					return settings.symbol+v;
				}
				return v;
			}

			input.bind("keypress",money);
			input.bind("blur",blurEvent);
			input.bind("focus",focusEvent);

			input.one("unmaskMoney",function(){
				input.unbind("focus",focusEvent);
				input.unbind("blur",blurEvent);
				input.unbind("keypress",money);
				if ($.browser.msie)
					this.onpaste= null;
				else if ($.browser.mozilla)
					this.removeEventListener("input",blurEvent,false);
			});
		});
	}

	$.fn.unmaskMoney=function(){
		return this.trigger("unmaskMoney");
	};
})(jQuery);function mascara_num(obj){	valida_num(obj)	if (obj.value.match("-")){		mod = "-";	}else{		mod = "";	}	valor = obj.value.replace("-","");	valor = valor.replace(",","");	if (valor.length >= 3){		valor = poe_ponto_num(valor.substring(0,valor.length-2))+","+valor.substring(valor.length-2, valor.length);	}	obj.value = mod+valor;}function poe_ponto_num(valor){	valor = valor.replace(/\./g,"");	if (valor.length > 3){		valores = "";		while (valor.length > 3){			valores = "."+valor.substring(valor.length-3,valor.length)+""+valores;			valor = valor.substring(0,valor.length-3);		}		return valor+""+valores;	}else{		return valor;	}}function valida_num(obj){	numeros = new RegExp("[0-9]");	while (!obj.value.charAt(obj.value.length-1).match(numeros)){		if(obj.value.length == 1 && obj.value == "-"){			return true;		}		if(obj.value.length >= 1){			obj.value = obj.value.substring(0,obj.value.length-1)		}else{			return false;		}	}}function changeText(id) {  alert('aaa');}