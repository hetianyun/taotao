package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容类别service
 * @author hty
 *
 */

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
			//根据parentId查询子节点列表
			TbContentCategoryExample example=new TbContentCategoryExample();
			//设置查询条件
			Criteria criteria=example.createCriteria();
			criteria.andParentIdEqualTo(parentId);
			//执行查询
			List<TbContentCategory> list=contentCategoryMapper.selectByExample(example);
			//类型转换
			List<EasyUITreeNode> result=new ArrayList<>();
			for(TbContentCategory category:list){
				EasyUITreeNode node=new EasyUITreeNode();
				node.setId(category.getId());
				node.setText(category.getName());
				node.setState(category.getIsParent()?"closed":"open");
				result.add(node);
			}
		return result;
	}

	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		//创建一个pojo对象
		TbContentCategory category=new TbContentCategory();
		//补全对象的属性
		category.setParentId(parentId);
		category.setName(name);
		//状态 1-正常 2- 删除
		category.setStatus(1);
		//排序，默认是1
		category.setSortOrder(1);
		category.setIsParent(false);
		category.setCreated(new Date());
		category.setUpdated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(category);
		//判断父节点的状态
		TbContentCategory parent=contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			//如果父节点之前不是父节点，那么更新为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		return TaotaoResult.ok(category);
	}

	@Override
	public TaotaoResult updateContentCategory(Long id, String name) {
		TbContentCategory category=contentCategoryMapper.selectByPrimaryKey(id);
		category.setName(name);
		contentCategoryMapper.updateByPrimaryKey(category);
		return TaotaoResult.ok(category);
	}

	@Override
	public TaotaoResult delContentCategory(Long id) {
		TbContentCategory category=new TbContentCategory();
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria=example.createCriteria();
		criteria.andParentIdEqualTo(id);
		List<TbContentCategory> list=contentCategoryMapper.selectByExample(example);
		if(list!=null){
			for(TbContentCategory category2:list){
				Long id2=category2.getId();
				delContentCategory(id2);
			}
		}
		contentCategoryMapper.deleteByPrimaryKey(id);
		return TaotaoResult.ok();
	}
	
	
	
}
