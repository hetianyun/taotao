package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;

/**
 * 内容分类管理Controller
 * @author hty
 *
 */

@Controller
public class ContentCategoryController {

		@Autowired
		private ContentCategoryService contentCategoryService;
		
		
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContenCategoryList(@RequestParam(value="id",defaultValue="0") Long parentId){
		List<EasyUITreeNode> list=contentCategoryService.getContentCategoryList(parentId);
		return list;
	}
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public TaotaoResult addContentcategory(Long parentId,String name){
		TaotaoResult result=contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	
	@RequestMapping("/content/category/update")
	@ResponseBody
	public TaotaoResult updateContentCategory(Long id,String name){
		TaotaoResult result=contentCategoryService.updateContentCategory(id, name);
		return result;
	}
	
	@RequestMapping("/content/category/delete")
	@ResponseBody
	public TaotaoResult delContentCategory(Long id){
		TaotaoResult result=contentCategoryService.delContentCategory(id);
		return result;
	}
	
	
}