package com.taotao.content.service.impl;

import java.util.Date; 
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.utils.JsonUtils;

/**
 * 内容管理Service
 * 
 * @author hty
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT}")
	private String INDEX_CONTENT;

	@Override
	public EasyUIDataGridResult getContentList(int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbContentExample example = new TbContentExample();
		List<TbContent> list = contentMapper.selectByExample(example);
		// 取查询结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);

		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public TaotaoResult addContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);
		// 同步缓存
		// 删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult editContent(TbContent content) {
		content.setUpdated(new Date());
		contentMapper.updateByPrimaryKey(content);
		// 同步缓存
		// 删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
		return TaotaoResult.ok(content);
	}

	@Override
	public TaotaoResult deleteContent(List ids) {
		for (Object k : ids) {
			contentMapper.deleteByPrimaryKey(Long.parseLong((String) k));
			// 同步缓存
			// 删除对应的缓存信息
			jedisClient.hdel(INDEX_CONTENT, k.toString());
		}
		
		return TaotaoResult.ok();
	}

	@Override
	public List<TbContent> getContentById(Long cid) {
		// 先查询缓存
		// 添加缓存不能影响正常逻辑
		try {
			// 查询缓存
			String json = jedisClient.hget(INDEX_CONTENT, cid + "");
			// 查询到：
			if (StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
			// 查询不到,执行下面结果

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 缓存中没有命中，查询数据库
		TbContentExample contentExample = new TbContentExample();
		// 添加查询条件
		Criteria criteria = contentExample.createCriteria();
		criteria.andCategoryIdEqualTo(cid);

		List<TbContent> list = contentMapper.selectByExample(contentExample);
		// 将查询到的结果添加到缓存
		// 添加缓存不能影响正常逻辑
		try {
			jedisClient.hset(INDEX_CONTENT, cid + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回结果
		return list;
	}

}
