package com.iwellmass.dispatcher.admin.dao;

import com.iwellmass.common.util.Pager;

/**
 * Created by xkwu on 2016/5/11.
 */

/**
 * @author pan.wei
 * @date 2011-12-1 上午11:36:12
 */
public class IDCPager extends Pager {

	public IDCPager() {}

	/**
	 * 构造函数
	 *
	 * @param begin
	 * @param length
	 */
	public IDCPager(int begin, int length) {
		super(begin, length);
	}

	/**
	 * @param begin
	 * @param length
	 * @param count
	 */
	public IDCPager(int begin, int length, int count) {
		this(begin, length);
	}

	/**
	 * @return the begin
	 */
	public int getBegin() {
		return getPage() * getLimit() + 1;
	}

	
	@Override
	public int getFrom() {
		// TODO Auto-generated method stub
		return super.getFrom();
	}
	
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return this.getLimit();
	}

}
