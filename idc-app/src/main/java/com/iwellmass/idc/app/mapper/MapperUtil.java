package com.iwellmass.idc.app.mapper;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;

public class MapperUtil {
	public static <T> PageData<T> doQuery(Pager pager, ISelect select) {
		PageInfo<T> pi = PageHelper.startPage(pager.getPage()+1,pager.getLimit()).doSelectPageInfo(select);
		return new PageData<>((int)pi.getTotal(), pi.getList());
	}
}
