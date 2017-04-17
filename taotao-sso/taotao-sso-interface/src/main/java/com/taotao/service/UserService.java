package com.taotao.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserService {
	TaotaoResult checkData(String param,int type);
	TaotaoResult register(TbUser tbUser);
	TaotaoResult login(String username,String password);
	TaotaoResult getUserByToken(String token);
	TaotaoResult removeToken(String token);
}
