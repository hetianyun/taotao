package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.service.UserService;
import com.taotao.utils.CookieUtils;

public class LoginInterceptor implements HandlerInterceptor {
	
	
	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	@Value("${SSO_URL}")
	private String SSO_URL;
	@Autowired
	private UserService userService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//执行Handler之前执行此方法
		//a)从cookie中取token
		String token=CookieUtils.getCookieValue(request, TT_TOKEN);
		if(StringUtils.isBlank(token)){
		//如果没有token
			//取当前请求的url
			String url=request.getRequestURL().toString();
			//跳转到登陆界面
			response.sendRedirect(SSO_URL+"?redirectUrl="+url);
			//拦截
			return false;
		}
		//如果有token，调用sso系统的服务，根据token查询用户信息
		TaotaoResult result=userService.getUserByToken(token);
		if(result.getStatus()!=200){
			//如果没有查询到用户信息，用户登陆已经过期，跳转到登陆界面
			//取当前请求的url
			String url=request.getRequestURL().toString();
			//跳转到登陆界面
			response.sendRedirect(SSO_URL+"?redirectUrl="+url);
			//拦截
			return false;
		}
		//放行
		return true;
	}

}
