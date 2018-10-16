package com.transform.jdbc;

import java.util.List;

public class PageData {
	//数据
	@SuppressWarnings("rawtypes")
	private List data;
	//页码
	private int page;
	//每页大小
	private int size;
	//记录总数
	private int total;

	public PageData(int page, int size){
		super();
		this.page = page;
		this.size = size;
		this.total = 0;
	}
	@SuppressWarnings("rawtypes")
	public PageData(List data, int page, int size, int total) {
		super();
		this.data = data;
		this.page = page;
		this.size = size;
		this.total = total;
	}
	@SuppressWarnings("rawtypes")
	public List getData() {
		return data;
	}
	@SuppressWarnings("rawtypes")
	public void setData(List data) {
		this.data = data;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public boolean isEmpty() {
		return total == 0 && (data==null || data.isEmpty());
	}
}

