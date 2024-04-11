package com.webnowbr.siscoat.infra.mb;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.webnowbr.siscoat.common.GsonUtil;
import com.webnowbr.siscoat.infra.db.model.UserVO;

@FacesConverter("usuarioConverter")
public class UserConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// Implemente a lógica para converter a String para um objeto Usuario
		// Exemplo: Consultar o usuário com o ID fornecido do banco de dados
		// e retornar o objeto Usuario correspondente
		value = value.replace("&quot;", "\"");
		return  GsonUtil.fromJson(value, UserVO.class);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		// Implemente a lógica para converter o objeto Usuario para uma String
		// Exemplo: Retornar o ID do usuário como uma String
		return  GsonUtil.toJson( ((UserVO) value));
	}
	/*
	 * public static UserVO fromString(String userString) { UserVO user = new
	 * UserVO();
	 * 
	 * int idStartIndex = userString.indexOf("id=") + 3; int idEndIndex =
	 * userString.indexOf(",", idStartIndex);
	 * user.setId(Long.parseLong(userString.substring(idStartIndex,
	 * idEndIndex).trim()));
	 * 
	 * int nameStartIndex = userString.indexOf("name=") + 5; int nameEndIndex =
	 * userString.indexOf(",", nameStartIndex);
	 * user.setName(userString.substring(nameStartIndex, nameEndIndex).trim());
	 * 
	 * return user; }
	 */

}
