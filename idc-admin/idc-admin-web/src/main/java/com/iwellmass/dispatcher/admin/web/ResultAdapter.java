package com.iwellmass.dispatcher.admin.web;

import java.util.List;

import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;
import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.service.domain.DataResult.STATUS_CODE;

public class ResultAdapter {

	
	public static final TableDataResult asTableDataResult(PageData<?> data) {
		TableDataResult tdr = new TableDataResult();
		tdr.setDataList(data.getData());
		tdr.setStatusCode(STATUS_CODE.SUCCESS);
		Page page = new Page();
		page.setCount(data.getCount());
		return tdr;
	}
	public static final TableDataResult asTableDataResult(List<?> data) {
		return null;
	}
}
