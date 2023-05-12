package com.webnowbr.siscoat.cobranca.service;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.infra.mb.UsuarioMB;

public class UserService {
	
	private final UserDao userDao = new UserDao();
	
	//retorna usuario Administrador
	public User userSistema() {
		
		User userSistema = userDao.findById(-1l);
	
		return userSistema;			
		
	}
	

}
