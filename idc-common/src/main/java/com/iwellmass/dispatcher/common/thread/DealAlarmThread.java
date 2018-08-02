package com.iwellmass.dispatcher.common.thread;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iwellmass.dispatcher.common.context.JVMContext;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcAlarmHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcUserMapper;
import com.iwellmass.dispatcher.common.model.DdcAlarmHistory;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExample;

/**
 * 提供监控预警相关功能
 * @author duheng
 *
 */
public class DealAlarmThread implements Runnable {

	private Integer taskId;
	private String alarmKey;
	private String remarks;
	
	public DealAlarmThread(Integer taskId, String alarmKey, String remarks) {
		this.taskId = taskId;
		this.alarmKey = alarmKey;
		this.remarks = remarks;
	}
	
	@Override
	public void run() {
		DdcUserMapper ddcUserMapper = SpringContext.getApplicationContext().getBean(DdcUserMapper.class);
		DdcTaskMapper ddcTaskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
		DdcAlarmHistoryMapper ddcAlarmHistoryMapper = SpringContext.getApplicationContext().getBean(DdcAlarmHistoryMapper.class);
		JVMContext jvmContext = SpringContext.getApplicationContext().getBean(JVMContext.class);

		DdcTaskExample ddcTaskExample = new DdcTaskExample();
		DdcTaskExample.Criteria ddcTaskCriteria = ddcTaskExample.createCriteria();
		ddcTaskCriteria.andTaskIdEqualTo(taskId);
		List<DdcTask> ddcTask = ddcTaskMapper.selectByExample(ddcTaskExample);
		String receivers = "";
		//TODO:发送邮件
		/*if(ddcTask.size() > 0) {
			DdcUserAlarm ddcUserAlarm = new DdcUserAlarm();
			ddcUserAlarm.setAppId(ddcTask.get(0).getAppId());
			ddcUserAlarm.setTaskId(taskId);
			//获取满足条件配置的接收用户列表
			List<DdcUser> ddcUsers = ddcUserMapper.selectByUserAlarm(ddcUserAlarm);
			if(ddcUsers != null && ddcUsers.size() > 0) {
				List<UserInfo> userInfos = new ArrayList<UserInfo>();
				for(DdcUser ddcUser : ddcUsers) {
					UserInfo userInfo = new UserInfo();
					userInfo.setUserId(ddcUser.getUserId());
					userInfo.setUserName(ddcUser.getLoginName());
					receivers += ddcUser.getLoginName() + ",";
					userInfo.setPhone(ddcUser.getUserPhone());
					userInfo.setEmail(ddcUser.getUserEmail());
					userInfos.add(userInfo);
				}
				//发给指定的人员
				Monitor.alarm(jvmContext.getAlarmKeys().get(alarmKey), remarks, userInfos);
			} else {
				//发给当前组下面的人员
				Monitor.alarm(jvmContext.getAlarmKeys().get(alarmKey), remarks);
			}
		} else {
			//发给当前组下面的人员
			Monitor.alarm(jvmContext.getAlarmKeys().get(alarmKey), remarks);
		}*/
		//记录本次预警历史记录
		DdcAlarmHistory record = new DdcAlarmHistory();
		record.setAppId(ddcTask.size() > 0 ? ddcTask.get(0).getAppId() : 0);
		record.setTaskId(taskId);
		record.setAlarmKey(jvmContext.getAlarmKeys().get(alarmKey));
		record.setContent(remarks);
		record.setReceivers(StringUtils.isNotEmpty(receivers) ? receivers : null);
		record.setAlarmDate(new Date());
		ddcAlarmHistoryMapper.insert(record);
	}

}
