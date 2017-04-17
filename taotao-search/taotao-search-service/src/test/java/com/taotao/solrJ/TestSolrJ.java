package com.taotao.solrJ;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

public class TestSolrJ {
	
	@Test
	public void testAddDocument() throws SolrServerException, IOException{
		//创建一个SolrServer对象。创建一个HttpSolrServer对象
		//需要指定solr服务的url
		SolrServer solrServer=new HttpSolrServer("http://127.0.1:8080/solr/collection1");
		//创建一个文档对象SolrInputDocument
		SolrInputDocument document=new SolrInputDocument();
		//向文档中添加域，必须有id域，域的名称必须在scheme.xml中定义
		document.addField("id", "test001");
		document.addField("item_title", "测试商品1");
		document.addField("item_price", 1000);
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	@Test
	public void deleteDocumentById() throws Exception{
		SolrServer server=new HttpSolrServer("http://127.0.1:8080/solr/collection1");
		server.deleteById("test001");
		server.commit();
	}
	
	
	@Test 
	public void deleteDocumentByQuery() throws Exception{
		SolrServer server=new HttpSolrServer("http://127.0.1:8080/solr/collection1");
		server.deleteByQuery("id:test001");
		server.commit();
	}
	
	
	
	@Test
	public void searchDocument()throws Exception{
		//创建一个SolrServer对象
		SolrServer server=new HttpSolrServer("http://127.0.1:8080/solr/collection1");
		//创建一个SolrQuery对象
		SolrQuery query=new SolrQuery();
		//设置查询条件	·过滤条件·分页条件·排序条件
			//query.set("q", "手机");
		query.setQuery("手机");
			//设置分页条件
		query.setStart(0);
		query.setRows(20);
			//设置搜索域
		 query.set("df", "item_keywords");
		 	//设置高亮
		 query.setHighlight(true);
		 	//设置高亮显示的域
		 query.addHighlightField("item_title");
		 query.setHighlightSimplePre("<em>");
		 query.setHighlightSimplePost("</em>");
		 //执行查询，得到一个Response对象
		 QueryResponse queryResponse=server.query(query);
		 //取查询结果
		 SolrDocumentList solrDocumentList=queryResponse.getResults();
		 //取查询结果总记录数
		 System.out.println("查询结果的总记录数："+solrDocumentList.getNumFound());
		 for(SolrDocument document:solrDocumentList){
			 System.out.println(document.get("id"));
			 //取高亮显示
			 java.util.Map<String, java.util.Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
			 List<String > list=highlighting.get(document.get("id")).get("item_title");
			 String itemTitle="";
			 if(list!=null&&list.size()!=0){
				 itemTitle=list.get(0);
			 }else{
				 itemTitle=(String) document.get("item_title");
			 }
			 
			 System.out.println(document.get("item_title"));
			 System.out.println("-------");
		 }
		 
		
		
		
		
	}
	
}
