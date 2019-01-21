package com.iwellmass.idc.quartz;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

public class JobCheck {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobCheck.class);
	
	@Test
	public void shouldMarkFailedResult() {
		
		boolean t = shouldMarkFailed();
		
		System.out.println(t);
	}
	

	private boolean shouldMarkFailed() {

		//
		String graph = "{\"nodes\":[{\"id\":\"start-idc\",\"taskId\":\"start\",\"taskGroup\":\"idc\",\"name\":\"起点\",\"taskName\":\"起点\",\"len\":40,\"type\":\"start\",\"class\":{\"start\":true}},{\"id\":\"end-idc\",\"taskId\":\"end\",\"taskGroup\":\"idc\",\"name\":\"终点\",\"taskName\":\"终点\",\"len\":40,\"type\":\"end\",\"class\":{\"end\":true}},{\"taskId\":\"871\",\"taskGroup\":\"data-factory\",\"taskName\":\"01\",\"updatetime\":\"2019-01-20T13:43:04\",\"description\":null,\"taskType\":\"NODE_TASK\",\"contentType\":\"SCALA\",\"workflowId\":null,\"graph\":null,\"parameter\":null,\"id\":\"871-data-factory\",\"name\":\"01\",\"type\":\"SCALA\"},{\"taskId\":\"872\",\"taskGroup\":\"data-factory\",\"taskName\":\"02\",\"updatetime\":\"2019-01-20T13:43:43\",\"description\":null,\"taskType\":\"NODE_TASK\",\"contentType\":\"SCALA\",\"workflowId\":null,\"graph\":null,\"parameter\":null,\"id\":\"872-data-factory\",\"name\":\"02\",\"type\":\"SCALA\"}],\"edges\":[{\"id\":\"871-data-factory-872-data-factory\",\"source\":{\"labelType\":\"svg\",\"label\":{},\"rx\":5,\"ry\":5,\"height\":16,\"taskId\":\"871\",\"taskGroup\":\"data-factory\",\"taskName\":\"01\",\"updatetime\":\"2019-01-20T13:43:04\",\"description\":null,\"taskType\":\"NODE_TASK\",\"contentType\":\"SCALA\",\"workflowId\":null,\"graph\":null,\"parameter\":null,\"id\":\"871-data-factory\",\"name\":\"01\",\"type\":\"SCALA\",\"class\":\"scala\",\"paddingLeft\":10,\"paddingRight\":10,\"paddingTop\":10,\"paddingBottom\":10,\"shape\":\"rect\",\"elem\":{\"__data__\":\"871-data-factory\",\"__on\":[{\"type\":\"mousedown\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"mouseup\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"click\",\"name\":\"\",\"capture\":false}]},\"x\":266.140625,\"y\":18},\"target\":{\"labelType\":\"svg\",\"label\":{},\"rx\":5,\"ry\":5,\"height\":16,\"taskId\":\"872\",\"taskGroup\":\"data-factory\",\"taskName\":\"02\",\"updatetime\":\"2019-01-20T13:43:43\",\"description\":null,\"taskType\":\"NODE_TASK\",\"contentType\":\"SCALA\",\"workflowId\":null,\"graph\":null,\"parameter\":null,\"id\":\"872-data-factory\",\"name\":\"02\",\"type\":\"SCALA\",\"class\":\"scala\",\"paddingLeft\":10,\"paddingRight\":10,\"paddingTop\":10,\"paddingBottom\":10,\"shape\":\"rect\",\"elem\":{\"__data__\":\"872-data-factory\",\"__on\":[{\"type\":\"mousedown\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"mouseup\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"click\",\"name\":\"\",\"capture\":false}]},\"x\":372.6015625,\"y\":18}},{\"id\":\"start-idc-871-data-factory\",\"source\":{\"labelType\":\"svg\",\"label\":{},\"rx\":5,\"ry\":5,\"height\":16,\"id\":\"start-idc\",\"taskId\":\"start\",\"taskGroup\":\"idc\",\"name\":\"起点\",\"taskName\":\"起点\",\"len\":40,\"class\":\"start def\",\"paddingLeft\":10,\"paddingRight\":10,\"paddingTop\":10,\"paddingBottom\":10,\"shape\":\"rect\",\"elem\":{\"__data__\":\"start-idc\",\"__on\":[{\"type\":\"mousedown\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"mouseup\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"click\",\"name\":\"\",\"capture\":false}]},\"x\":34.5,\"y\":18},\"target\":{\"labelType\":\"svg\",\"label\":{},\"rx\":5,\"ry\":5,\"height\":16,\"taskId\":\"871\",\"taskGroup\":\"data-factory\",\"taskName\":\"01\",\"updatetime\":\"2019-01-20T13:43:04\",\"description\":null,\"taskType\":\"NODE_TASK\",\"contentType\":\"SCALA\",\"workflowId\":null,\"graph\":null,\"parameter\":null,\"id\":\"871-data-factory\",\"name\":\"01\",\"type\":\"SCALA\",\"class\":\"scala\",\"paddingLeft\":10,\"paddingRight\":10,\"paddingTop\":10,\"paddingBottom\":10,\"shape\":\"rect\",\"elem\":{\"__data__\":\"871-data-factory\",\"__on\":[{\"type\":\"mousedown\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"mouseup\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"click\",\"name\":\"\",\"capture\":false}]},\"x\":266.140625,\"y\":18}},{\"id\":\"872-data-factory-end-idc\",\"source\":{\"labelType\":\"svg\",\"label\":{},\"rx\":5,\"ry\":5,\"height\":16,\"taskId\":\"872\",\"taskGroup\":\"data-factory\",\"taskName\":\"02\",\"updatetime\":\"2019-01-20T13:43:43\",\"description\":null,\"taskType\":\"NODE_TASK\",\"contentType\":\"SCALA\",\"workflowId\":null,\"graph\":null,\"parameter\":null,\"id\":\"872-data-factory\",\"name\":\"02\",\"type\":\"SCALA\",\"class\":\"scala\",\"paddingLeft\":10,\"paddingRight\":10,\"paddingTop\":10,\"paddingBottom\":10,\"shape\":\"rect\",\"elem\":{\"__data__\":\"872-data-factory\",\"__on\":[{\"type\":\"mousedown\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"mouseup\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"click\",\"name\":\"\",\"capture\":false}]},\"x\":34.5,\"y\":190},\"target\":{\"labelType\":\"svg\",\"label\":{},\"rx\":5,\"ry\":5,\"height\":16,\"id\":\"end-idc\",\"taskId\":\"end\",\"taskGroup\":\"idc\",\"name\":\"终点\",\"taskName\":\"终点\",\"len\":40,\"class\":\"end def\",\"paddingLeft\":10,\"paddingRight\":10,\"paddingTop\":10,\"paddingBottom\":10,\"shape\":\"rect\",\"elem\":{\"__data__\":\"end-idc\",\"__on\":[{\"type\":\"mousedown\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"mouseup\",\"name\":\"dragLine\",\"capture\":false},{\"type\":\"click\",\"name\":\"\",\"capture\":false}]},\"x\":153.5,\"y\":18}}]}";
		
		JobInstance _871 = new JobInstance();
		_871.setStatus(JobInstanceStatus.SKIPPED);
		_871.setTaskId("871");
		_871.setTaskGroup("data-factory");
		
		JobInstance _872 = new JobInstance();
		_872.setStatus(JobInstanceStatus.FAILED);
		_872.setTaskId("872");
		_872.setTaskGroup("data-factory");
		
		
		List<JobInstance> subInsList = Arrays.asList(_871, _872);
		
		// 选取所有候选的 edges
		List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(graph);
		
		if (LOGGER.isDebugEnabled()) {
			edges.forEach(edge -> {
				String msg = String.format("%20s -> %s", edge.getSrcTaskKey(), edge.getTaskKey());
				LOGGER.debug(msg);
			});
		}
		
		// 如果有 running 状态的 instance 直接返回
		Map<TaskKey, JobInstance> subInsMap = new HashMap<>();
		for (JobInstance subIns : subInsList) {
			if (!subIns.getStatus().isComplete()) {
				LOGGER.debug("子任务 {} 仍在运行 {}", subIns.getTaskKey(), subIns.getStatus());
				return false;
			}
			subInsMap.put(subIns.getTaskKey(), subIns);
		}

		int pendingTask = 0;
		Map<TaskKey, List<JobInstance>> checkListMap = new HashMap<>();
		
		for (WorkflowEdge edge : edges) {
			TaskKey targetTaskKey = edge.getTaskKey();

			// END TASK 由 barrier 管理
			if (WorkflowEdge.END.equals(targetTaskKey)) {
				continue;
			}
			
			// 已经被激活过了
			JobInstance targetSubIns = subInsMap.get(targetTaskKey);
			if (targetSubIns != null) {
				continue;
			} else {
				pendingTask++;
				LOGGER.debug("子任务 {} 未运行, pendingTask++", targetTaskKey);
			}
			
			List<JobInstance> checkList = checkListMap.get(targetTaskKey);
			
			if (checkList == null) {
				checkListMap.put(targetTaskKey, checkList = new LinkedList<>());
			}
			
			TaskKey srcTaskKey = edge.getSrcTaskKey();
			
			checkList.add(subInsMap.get(srcTaskKey));
			
			if (WorkflowEdge.START.equals(srcTaskKey)) {
				LOGGER.debug("idc.start -> {} can active...", targetTaskKey);
				return false;
			} else {
				JobInstance srcSubIns = subInsMap.get(srcTaskKey);
				checkList.add(srcSubIns);
			}
		}
		
		entryLoop : for (Entry<TaskKey, List<JobInstance>> checkEntry : checkListMap.entrySet()) {
			List<JobInstance> checkList = checkEntry.getValue();
			
			for (JobInstance check : checkList) {
				if (check == null || check.getStatus().isFailure()) {
					continue entryLoop;
				}
			}
			LOGGER.debug("task {} all predecessors successed, can active...", checkEntry.getKey());
			// 有可以被激活的点
			return false;
		}
		
		if (pendingTask > 0) {
			LOGGER.debug("none subtask can active, {} task still pending", pendingTask);
			return true;
		} else {
			LOGGER.debug("all subtask finished");
			return false;
		}
	}
}