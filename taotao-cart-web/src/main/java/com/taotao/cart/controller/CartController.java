package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import com.taotao.utils.CookieUtils;
import com.taotao.utils.JsonUtils;

/**
 * 购物车处理Controller
 * @author hty
 *
 */
@Controller
public class CartController {
	@Autowired
	private ItemService itemService;
	
	@Value("${CART_KEY}")
	private String CART_KEY;
	@Value("${CART_EXPIER}")
	private Integer CART_EXPIER;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId,Integer num,
			HttpServletRequest request,HttpServletResponse response){
		//从cookie中查询商品列表
		List<TbItem> cartList=getCartList(request);
		//判断商品在商品列表中是否存在
		boolean flag=false;
		for(TbItem tbItem:cartList){
			if(tbItem.getId()==itemId.longValue()){
				//如果存在，商品数量相加
				tbItem.setNum(tbItem.getNum()+num);
				flag=true;
				break;
			}
		}
		//不存在，根据id查询商品信息
		if(!flag){
			//需要调用服务取商品信息
			TbItem tbItem=itemService.getItemById(itemId);
			//设置购买商品数量
			tbItem.setNum(num);
			//取一张图片
			String image=tbItem.getImage();
			if(StringUtils.isNotBlank(image)){
				String[] images=image.split(",");
				tbItem.setImage(images[0]);
			}
			//把商品添加到购物车列表
			cartList.add(tbItem);
		}
		//把购物车列表写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList),CART_EXPIER,true);
		return "cartSuccess";
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
	
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateItemNum(@PathVariable Long itemId,
			@PathVariable Integer num,HttpServletRequest request,HttpServletResponse response){
		//从cookie中取购物车列表
		List<TbItem> cartList=getCartList(request);
		//查询到对应的商品
		for(TbItem tbItem:cartList){
			if(tbItem.getId()==itemId.longValue()){
				//更新商品数量
				tbItem.setNum(num);
				break;
			}
		}
		//把购物车列表写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList), CART_EXPIER, true);
		return TaotaoResult.ok();
	}
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response){
		//从cookie中获取购物车列表
		List<TbItem> cartList=getCartList(request);
		//找到对应的商品
		for(TbItem tbItem:cartList){
			if(tbItem.getId()==itemId.longValue()){
				//删除商品
				cartList.remove(tbItem);
				break;
			}
		}
		//把购物车写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList),CART_EXPIER, true);
		//重定向到购物车列表页面
		return "redirect:/cart/cart.html";
	}	
}
