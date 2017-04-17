package com.taotao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面展示Controller
 * @author hty
 *
 */
@Controller
public class PageControlller {
		@RequestMapping("/")
		public String showIndex(){
			return "index";
		}
		@RequestMapping("/{page}")
		public String showPage(@PathVariable	String page){
			return page;
		}
		
		
	
}
