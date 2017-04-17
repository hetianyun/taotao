package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

public interface ContentCategoryService {
	
	List<EasyUITreeNode>  getContentCategoryList(long parentId);
	TaotaoResult addContentCategory(Long parentId,String name);
	TaotaoResult updateContentCategory(Long id,String name);
	TaotaoResult delContentCategory(Long id);
}
