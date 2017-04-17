package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

/**
 * 
 * 内容管理Controller
 * 
 * @author hty
 *
 */
@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;

	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getContentList(Integer page, Integer rows) {
		return contentService.getContentList(page, rows);
	}

	@RequestMapping("/content/save")
	@ResponseBody
	public TaotaoResult addContent(TbContent content){
		TaotaoResult result=contentService.addContent(content);
		return result;
	}
	
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public TaotaoResult editContent(TbContent content){
		TaotaoResult result=contentService.editContent(content);
		return result;
	}
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaotaoResult deleteContent(@RequestParam("ids") List ids){
		TaotaoResult result=contentService.deleteContent(ids);
		return result;
	}
}
