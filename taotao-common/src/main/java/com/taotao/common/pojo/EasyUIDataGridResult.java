package com.taotao.common.pojo;

import java.io.Serializable;
import java.util.List;

public class EasyUIDataGridResult implements Serializable {
	private long total;
	private List rows;
	
	public EasyUIDataGridResult(long total, List rows) {
		this.total = total;
		this.rows = rows;
	}
	public EasyUIDataGridResult() {
		// TODO Auto-generated constructor stub
		super();
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
}
