package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;



/**
 * 商品管理Controller	
 * @author hty
 *
 */
@Controller
public class ItemController {
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId){
		TbItem item=itemService.getItemById(itemId);
		return item;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows){
		return itemService.getItemList(page, rows);
	}
	
	@RequestMapping(value="/item/save", method=RequestMethod.POST)
	@ResponseBody
	public  TaotaoResult addItem(TbItem item,String desc){
		TaotaoResult result=itemService.addItem(item, desc);
		return result;
	}
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public TaotaoResult deleteItem(@RequestParam("ids") List ids){
	TaotaoResult result=	itemService.deleteItem(ids);
		return result;
	}
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public TaotaoResult reshelfItem(@RequestParam("ids") List ids){
			TaotaoResult result=	itemService.reshelfItem(ids);
				return result;
	}
	
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public TaotaoResult instockItem(@RequestParam("ids") List ids){
			TaotaoResult result=	itemService.instockItem(ids);
				return result;
	}
}
