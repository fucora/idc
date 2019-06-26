package com.iwellmass.idc.app.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.TriggerKey;

public class IDCUtils {
	
	public static final String GROUP_DEFAULT = "idc.default";
	
	
	
	public static final String REDO_GROUP = "idc";
	
//	public static final List<WorkflowEdge> parseWorkflowEdge(String graph) {
//        // 格式化graph
//		DirectedAcyclicGraph<TaskKey, WorkflowEdge> workflowGraph = parseGraph(graph);
//		
//		// required
//		Arrays.asList(WorkflowEdge.START, WorkflowEdge.END).forEach( rtk -> {
//			Assert.isTrue(workflowGraph.containsVertex(rtk), "未找到 " + rtk + "节点");
//		});
//		
//        // 校验graph是否正确,检查孤立点
//        workflowGraph.vertexSet().forEach(tk -> {
//        	// 开始节点
//        	if (WorkflowEdge.START.equals(tk)) {
//        		if (workflowGraph.inDegreeOf(tk) > 0) {
//        			throw new AppException("开始节点不能作为下游节点");
//        		}
//        	}
//        	// 结束节点
//        	else if (WorkflowEdge.END.equals(tk)) {
//        		if (workflowGraph.outDegreeOf(tk) > 0) {
//        			throw new AppException("结束节点不能作为上游节点");
//        		}
//        	}
//        	// 其他节点
//        	else {
//        		if(workflowGraph.inDegreeOf(tk) == 0 || workflowGraph.outDegreeOf(tk) == 0) {
//        			throw new AppException("节点" + tk + "依赖配置错误");
//        		}
//        	}
//        });
//        
//        return new ArrayList<>(workflowGraph.edgeSet());
//        
//	}
	
    // 格式化graph
   // public static DirectedAcyclicGraph<TaskKey, WorkflowEdge>  parseGraph(String graph) {
//        JSONObject jsonObject = JSON.parseObject(graph);
//        DirectedAcyclicGraph<TaskKey, WorkflowEdge> directedAcyclicGraph = new DirectedAcyclicGraph<>(WorkflowEdge.class);
//
//        // add vertex
//        for (JSONObject node : jsonObject.getJSONArray("nodes").toJavaList(JSONObject.class)) {
//        	String taskId = Objects.requireNonNull(node.getString("taskId"), "数据格式错误");
//        	String taskGroup = Objects.requireNonNull(node.getString("taskGroup"), "数据格式错误");
//        	TaskKey tk = new TaskKey(taskId, taskGroup);
//        	directedAcyclicGraph.addVertex(tk);
//        }
//        
//        // add edge
//        for (JSONObject graphJsonObject : jsonObject.getJSONArray("edges").toJavaList(JSONObject.class)) {
//        	
//            JSONObject sourceJsonObject = Objects.requireNonNull((JSONObject) graphJsonObject.get("source"), "数据格式错误");
//            JSONObject targetJsonObject = Objects.requireNonNull((JSONObject) graphJsonObject.get("target"), "数据格式错误");
//            
//        	TaskKey srcTaskKey = new TaskKey(sourceJsonObject.getString("taskId"),sourceJsonObject.getString("taskGroup"));
//            TaskKey targetTaskKey = new TaskKey(targetJsonObject.getString("taskId"),targetJsonObject.getString("taskGroup"));
//            
//            WorkflowEdge we = new WorkflowEdge();
//            we.setSrcTaskKey(srcTaskKey);
//            we.setTaskKey(targetTaskKey);
//            directedAcyclicGraph.addEdge(srcTaskKey, targetTaskKey, we);
//        }
//        return null;
//    }
	
	public static final LocalDateTime toLocalDateTime(Long mill) {
		if (mill == null) {
			return null;
		}
		return Instant.ofEpochMilli(mill).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static final Date toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		long mill = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return new Date(mill);
	}
	
	public static Long toEpochMilli(LocalDateTime loadDate) {
		if (loadDate == null) {
			return -1L;
		}
		return loadDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static final LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		long mill = date.getTime();
		return Instant.ofEpochMilli(mill).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static boolean isBlockingGroup(TriggerKey key) {
		return GROUP_DEFAULT.equals(key.getGroup());
	}
	
}
