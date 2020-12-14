package com.mitocode.pagination;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
//https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/
// ver page y pageable
public class PageSupport<T> {
	public static final String 	FIRST_PAGE_NUM = "0";
	public static final String 	DEFAULT_PAGE_SIZE = "20";
	
	private List<T> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	
	public PageSupport() {
		
		
	}

	public PageSupport(List<T> content, int pageNumber, int pageSize, long totalElements) {
		super();
		this.content = content;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
	}
	
	@JsonProperty
	public long totalPages() {
		return pageSize > 0 ? (totalElements - 1) / pageSize + 1 : 0; 
	}
	
	@JsonProperty
	public boolean first() {
		return pageSize == Integer.parseInt(FIRST_PAGE_NUM); 
	}
	
	@JsonProperty
	public boolean last() {
		return (pageNumber + 1) * pageSize >=  totalElements;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public static String getFirstPageNum() {
		return FIRST_PAGE_NUM;
	}

	public static String getDefaultPageSize() {
		return DEFAULT_PAGE_SIZE;
	}
	
	
}
