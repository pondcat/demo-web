package com.github.pondcat.webdemo.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import pondcat.commons.combine.BasePage;

import java.util.regex.Pattern;

/**
 * @author gejian at 6/13/2018 9:31 PM
 */
public class Paging<T> extends BasePage<T> {
	private static final Pattern SORT_BY_REGEX = Pattern.compile("^\\S+$");

	private String sortBy;

	private boolean isAsc = true;

	public Page<T> toPage(boolean count) {
		Page<T> tPage = new Page<>(getPageNumber(), getPageSize(), sortBy, isAsc);
		tPage.setSearchCount(count);
		return tPage;
	}

	public Paging fromPage(Page<T> page) {
		this.setTotal(page.getTotal());
		this.setContent(page.getRecords());
		return this;
	}

	/**
	 * 设置排序列, 排序列不能含有空白字符, 如空格、制表符、换页符等
	 * @param sortBy 排序列, 不能含有空白字符, 如空格、制表符、换页符等
	 */
	public final void setSortBy(String sortBy) {
		if (StringUtils.isEmpty(sortBy)) {
			return;
		}
		if (SORT_BY_REGEX.matcher(sortBy).matches()) {
			this.sortBy = sortBy;
		}
	}

	/**
	 * 设置排序顺序ASC或DESC
	 * @param sortOrder 以a或A开头, 排序ASC; 以d或D开头, 排序DESC
	 */
	public final void setSortOrder(String sortOrder) {
		if (StringUtils.isEmpty(sortOrder)) {
			return;
		}
		char c0 = sortOrder.charAt(0);
		if (c0 == 'a' || c0 == 'A') {
			isAsc = true;
		}
		else if (c0 == 'd' || c0 == 'D') {
			isAsc = false;
		}
	}

	/**
	 * 获取SQL排序, 如: id DESC
	 * @return 可能为null, 不会为空
	 */
	@JSONField(serialize = false)
	@JsonIgnore
	public final String getOrderBy() {
		if (sortBy != null) {
			return isAsc ? sortBy : sortBy + " DESC";
		}
		return null;
	}

	@Override
	public String toString() {
		return "Paging{" +
				"sortBy='" + sortBy + '\'' +
				", isAsc=" + isAsc +
				"} " + super.toString();
	}
}
