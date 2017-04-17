package com.taotao.service;

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
	TbItem getItemById(Long Itemid);
	EasyUIDataGridResult getItemList(int page,int rows);
	TaotaoResult addItem(TbItem item,String desc);
	TaotaoResult deleteItem(List ids);
	TaotaoResult reshelfItem(List ids);
	TaotaoResult instockItem(List ids);
	TbItemDesc getItemDescById(long itemId);
}
