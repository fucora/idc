package com.iwellmass.dispatcher.admin.dao;

/**
 * Created by xkwu on 2016/5/11.
 */

/**
 * @author pan.wei
 * @date 2011-12-1 上午11:36:12
 */
public class Page extends IDCPager{

	// 分页查询开始记录位置
	private int begin;

	// 每页显示记录数
	private int length;

	// 查询结果总记录数
	private int count;

	public Page() {
	}

	/**
	 * 构造函数
	 *
	 * @param begin
	 * @param length
	 */
	public Page(int begin, int length) {
		this.begin = begin;
		this.length = length;
	}

	/**
	 * @param begin
	 * @param length
	 * @param count
	 */
	public Page(int begin, int length, int count) {
		this(begin, length);
		this.count = count;
	}

	/**
	 * @return the begin
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return this.begin + this.length;
	}

	/**
	 * @param begin the begin to set
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
