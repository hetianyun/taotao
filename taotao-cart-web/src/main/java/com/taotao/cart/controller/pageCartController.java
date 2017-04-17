package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.pojo.TbItem;
import com.taotao.utils.CookieUtils;
import com.taotao.utils.JsonUtils;

/**
 * 购物车页面跳转控制Controller
 * @author hty
 *
 */

@Controller
public class pageCartController {
	
	
	
	@Value("${CART_KEY}")
	private String CART_KEY;
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request,Model model){
		//从cookie中获取购物车列表
		List<TbItem> cartList=getCartList(request); 
		//传递给页面
		model.addAttribute("cartList", cartList);
		return "cart";
		
		
		
	}
	private List<TbItem> getCartList(HttpServletRequest request){
		//从cookie中取购物车列表
		String json=CookieUtils.getCookieValue(request, CART_KEY,true);
		if(StringUtils.isBlank(json)){
			//如果没有内容，返回一个空的列表
			return new ArrayList<>();
		}
		List<TbItem> list=JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
}
