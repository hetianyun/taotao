package com.taotao.service.impl;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import com.taotao.utils.JsonUtils;
/**
 * 商品管理Service
 * @author hty
 *
 */
@Service
public class ItemServiceImpl implements  ItemService {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	@Value("${ITEM_INFO_PRE}")
	private String ITEM_INFO_PRE;
	@Value("${ITEM_EXPIRE}")
	private Integer ITEM_EXPIRE;
	
	@Override
	public TbItem getItemById(Long itemId) {
		
		//查询缓存
		String json=jedisClient.get(ITEM_INFO_PRE+":"+itemId+":BASE");
		//判断缓存是否命中
		if(StringUtils.isNotBlank(json)){
			//转换为java对象
			TbItem item=JsonUtils.jsonToPojo(json, TbItem.class);
			return item;
		}
		TbItem item=itemMapper.selectByPrimaryKey(itemId);
		//把数据保存到缓存中
		jedisClient.set(ITEM_INFO_PRE+":"+itemId+":BASE", JsonUtils.objectToJson(item));
		//设置缓存有效期
		jedisClient.expire(ITEM_INFO_PRE+":"+itemId+":BASE", ITEM_EXPIRE);
		
		return  item;
	}


	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example=new TbItemExample();
		List<TbItem> list=itemMapper.selectByExample(example);
		//取查询结果
		PageInfo<TbItem> pageInfo=new PageInfo<>(list);
		
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}


	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		//生成商品id
		final long itemId=IDUtils.genItemId();
		//不全item属性
		item.setId(itemId);
		//商品状态1-正常 2-下架 3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//像商品表中插入数据
		itemMapper.insert(item);
		//创建一个商品描述表对应的pojo
		TbItemDesc itemDesc=new TbItemDesc();
		//像商品描述表中插入数据
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		itemDesc.setCreated(new Date());
		itemDescMapper.insert(itemDesc);
		
		//发送一个商品添加消息
		jmsTemplate.send(topicDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage=session.createTextMessage(itemId+"");
				return textMessage;
			}
		});
		//返回结果
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult deleteItem(List ids) {
		//处理缓存，待完善
		
		
		for(Object k:ids){
			itemMapper.deleteByPrimaryKey(Long.parseLong((String) k));
		}
		
		
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult reshelfItem(List ids) {
		for(Object k:ids){
		TbItem item=	itemMapper.selectByPrimaryKey(Long.parseLong((String) k));
		item.setStatus((byte) 1);
		itemMapper.updateByPrimaryKey(item);
		}
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult instockItem(List ids) {
		for(Object k:ids){
			TbItem item=	itemMapper.selectByPrimaryKey(Long.parseLong((String) k));
				item.setStatus((byte) 2);
			 itemMapper.updateByPrimaryKey(item);
			}
			return TaotaoResult.ok();
	}


	@Override
	public TbItemDesc getItemDescById(long itemId) {
		
		String json=jedisClient.get(ITEM_INFO_PRE+":"+itemId+":DESC");
		//判断缓存是否命中
		if(StringUtils.isNotBlank(json)){
			//转化为java对象
			TbItemDesc  itemDesc=JsonUtils.jsonToPojo(json, TbItemDesc.class);
			return itemDesc;
		}
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		
		//将数据添加到缓存中
		jedisClient.set(ITEM_INFO_PRE+":"+itemId+":DESC", JsonUtils.objectToJson(itemDesc));
		//设置过期时间
		jedisClient.expire(ITEM_INFO_PRE+":"+itemId+":DESC", ITEM_EXPIRE);
		//返回结果
		return itemDesc;
	}




}
