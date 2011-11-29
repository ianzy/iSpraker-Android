package com.iSpraker.android.dos;

public class Paging {
	private int current_page;
	private int per_page;
	private int total_count;
	private int num_pages;
	public int getCurrentPage() {
		return current_page;
	}
	public void setCurrentPage(int current_page) {
		this.current_page = current_page;
	}
	public int getPerPage() {
		return per_page;
	}
	public void setPerPage(int per_page) {
		this.per_page = per_page;
	}
	public int getTotalCount() {
		return total_count;
	}
	public void setTotalCount(int total_count) {
		this.total_count = total_count;
	}
	public int getNumPages() {
		return num_pages;
	}
	public void setNumPages(int num_pages) {
		this.num_pages = num_pages;
	}
}
