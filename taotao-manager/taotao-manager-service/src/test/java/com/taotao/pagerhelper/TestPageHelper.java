package com.taotao.pagerhelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;

public class TestPageHelper {
	
	@Test
	public void TestPageHelper() throws Exception{
	//1现在mybatis的配置文件中配置分页插件

	//2在执行查询之前配置分页条件。使用PageHelper的静态方法
	PageHelper.startPage(1, 10);
	// 3执行查询
	ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
	TbItemMapper itemMapper=applicationContext.getBean(TbItemMapper.class);
		//创建example对象
	TbItemExample example=new TbItemExample();
//	可以设置查询条件
//	Criteria criteria=example.createCriteria();
	
	List<TbItem> list=itemMapper.selectByExample(example);
	//4取分页信息，使用PageInfo对象取
	PageInfo<TbItem> info=new PageInfo<>(list);
	System.out.println("总记录数："+ info.getTotal());
	System.out.println("总页数："+ info.getPages());
	System.out.println("返回的记录数："+ list.size());
	
	}
	
}
