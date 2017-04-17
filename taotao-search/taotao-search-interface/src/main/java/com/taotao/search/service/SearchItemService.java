package com.taotao.search.service;

import com.taotao.common.pojo.TaotaoResult;
public interface SearchItemService {
	
	TaotaoResult improtItemsToIndex();
	public TaotaoResult addDoument(long itemId) throws Exception;
}
