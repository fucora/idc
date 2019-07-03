package com.iwellmass.idc.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.WorkflowQueryParam;
import com.iwellmass.idc.app.vo.WorkflowVO;
import com.iwellmass.idc.scheduler.model.NodeTask;
import com.iwellmass.idc.scheduler.model.Workflow;
import com.iwellmass.idc.scheduler.model.WorkflowEdge;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;

@Service
public class WorkflowService {

	@Resource
    private WorkflowRepository workflowRepository;

    public PageData<WorkflowVO> query(WorkflowQueryParam qm) {
    	return QueryUtils.doJpaQuery(qm, pageable -> {
    		Specification<Workflow> spec = SpecificationBuilder.toSpecification(qm);
    		return workflowRepository.findAll(spec, pageable).map(model -> {
    			WorkflowVO vo = new WorkflowVO();
    			BeanUtils.copyProperties(model, vo);
    			return vo;
    		});
    		
    	});
    }

    public void save(WorkflowVO vo) {
    	
    	Workflow workflow = new Workflow();
    	
    	BeanUtils.copyProperties(vo, workflow);
    	
    	// TODO 设置边关系
    	
    	// TODO 设置点关系
    	workflowRepository.save(workflow);
    	
    	// validate
//    	// List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(workflow.getGraph());
//    	
//    	// autoId
//    	String autoId = Hashing.md5().hashString(workflow.getGraph(), Charsets.UTF_8).toString();
//    	workflow.setWorkflowId(autoId);
//    	
//    	Workflow check = workflowRepository.findById(autoId).get();
//    	
//    	if (check == null) {
//    		// 没有找到这个工作流
//    		workflow.setWorkflowId(autoId);
//    		edges.forEach(we -> {
//    			// we.setParentTaskKey(new TaskKey(workflow.getTaskId(),workflow.getTaskGroup()));
//    		});
//    		workflowRepository.save(workflow);
//    		// 刷新 edges
//    		// workflowEdgeRepository.deleteByParentTaskIdAndParentTaskGroup(workflow.getTaskId(),workflow.getTaskGroup());
//    		workflowEdgeRepository.saveAll(edges);
//    	}
    }

	public WorkflowVO getWorkflow(String workflowId) {
		return null;
	}

	
	private  List<WorkflowEdge> parseWorkflowEdge(String id, Map<String, Object> graph) {
      // 格式化graph
		DirectedAcyclicGraph<NodeTask, WorkflowEdge> workflowGraph = parseGraph(id, graph);
		
//		// required
//		Arrays.asList(NodeTask.START, NodeTask.END).forEach( rtk -> {
//			Assert.isTrue(workflowGraph.containsVertex(rtk), "未找到 " + rtk + "节点");
//		});
//		
//      // 校验graph是否正确,检查孤立点
//      workflowGraph.vertexSet().forEach(tk -> {
//      	// 开始节点
//      	if (WorkflowEdge.START.equals(tk)) {
//      		if (workflowGraph.inDegreeOf(tk) > 0) {
//      			throw new AppException("开始节点不能作为下游节点");
//      		}
//      	}
//      	// 结束节点
//      	else if (WorkflowEdge.END.equals(tk)) {
//      		if (workflowGraph.outDegreeOf(tk) > 0) {
//      			throw new AppException("结束节点不能作为上游节点");
//      		}
//      	}
//      	// 其他节点
//      	else {
//      		if(workflowGraph.inDegreeOf(tk) == 0 || workflowGraph.outDegreeOf(tk) == 0) {
//      			throw new AppException("节点" + tk + "依赖配置错误");
//      		}
//      	}
//      });
      
      return new ArrayList<>(workflowGraph.edgeSet());
      
	}
	
	// 格式化graph
	private DirectedAcyclicGraph<NodeTask, WorkflowEdge>  parseGraph(String id, Map<String, Object> graph) {
	        DirectedAcyclicGraph<NodeTask, WorkflowEdge> directedAcyclicGraph = new DirectedAcyclicGraph<>(WorkflowEdge.class);
	
	        // add vertex
//	        for (JSONObject node : jsonObject.getJSONArray("nodes").toJavaList(JSONObject.class)) {
//	        	String taskId = Objects.requireNonNull(node.getString("taskId"), "数据格式错误");
//	        	String taskGroup = Objects.requireNonNull(node.getString("taskGroup"), "数据格式错误");
//	        	NodeTask tk = new NodeTask();
//	        	tk.setId(id);
//	        	tk.setPid(pid);
//	        	tk.setDomain(domain);
//	        	tk.setScheduleType(scheduleType);
//	        	tk.setTaskId(taskId);
//	        	tk.setTaskType(taskType);
//	        	directedAcyclicGraph.addVertex(tk);
//	        }
//	        
//	        // add edge
//	        for (JSONObject graphJsonObject : jsonObject.getJSONArray("edges").toJavaList(JSONObject.class)) {
//	        	
//	            JSONObject sourceJsonObject = Objects.requireNonNull((JSONObject) graphJsonObject.get("source"), "数据格式错误");
//	            JSONObject targetJsonObject = Objects.requireNonNull((JSONObject) graphJsonObject.get("target"), "数据格式错误");
//	            
//	        	String srcTaskKey = new TaskKey(sourceJsonObject.getString("taskId"),sourceJsonObject.getString("taskGroup"));
//	            String targetTaskKey = new TaskKey(targetJsonObject.getString("taskId"),targetJsonObject.getString("taskGroup"));
//	            
//	            WorkflowEdge we = new WorkflowEdge();
//	            we.setPid(id);
//	            we.setSource(source);
//	            we.setTarget(target);
//	            directedAcyclicGraph.addEdge(srcTaskKey, targetTaskKey, we);
//	        }
//	        return directedAcyclicGraph;
	        return null;
	    }
}
