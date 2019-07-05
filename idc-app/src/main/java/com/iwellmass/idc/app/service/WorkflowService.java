package com.iwellmass.idc.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.WorkflowQueryParam;
import com.iwellmass.idc.app.vo.WorkflowVO;
import com.iwellmass.idc.app.vo.graph.EdgeVO;
import com.iwellmass.idc.app.vo.graph.GraphVO;
import com.iwellmass.idc.app.vo.graph.NodeVO;
import com.iwellmass.idc.app.vo.graph.SourceVO;
import com.iwellmass.idc.app.vo.graph.TargetVO;
import com.iwellmass.idc.scheduler.model.NodeTask;
import com.iwellmass.idc.scheduler.model.Workflow;
import com.iwellmass.idc.scheduler.model.WorkflowEdge;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;

@Service
public class WorkflowService {

	@Resource
	WorkflowRepository workflowRepository;
	
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
	
	public GraphVO getGraph(String id) {
		Workflow workflow = get(id);
		
		// 设置前端 VO
		GraphVO gvo = new GraphVO();
		List<NodeTask> nodes = workflow.getTaskNodes();
		if (nodes != null) {
			List<NodeVO> nvos = nodes.stream().map(n -> {
				NodeVO nvo = new NodeVO();
				BeanUtils.copyProperties(n, nvo);
				return nvo;
			}).collect(Collectors.toList());
			gvo.setNodes(nvos);
		}
		List<WorkflowEdge> edges = workflow.getEdges();
		if (edges != null) {
			List<EdgeVO> nvos = edges.stream().map(n -> {
				EdgeVO nvo = new EdgeVO();
				nvo.setId(n.getId());
				nvo.setSource(new SourceVO(n.getSource()));
				nvo.setTarget(new TargetVO(n.getTarget()));
				return nvo;
			}).collect(Collectors.toList());
			gvo.setEdges(nvos);
		}
		return gvo;
	}
	
	public void saveGraph(String id, GraphVO gvo) {
		// 格式化graph
		DirectedAcyclicGraph<String, EdgeVO> workflowGraph = new DirectedAcyclicGraph<>(EdgeVO.class);

		Map<String, NodeVO> nodeMap = gvo.getNodes().stream()
				.collect(Collectors.toMap(NodeVO::getId, Function.identity()));
		nodeMap.keySet().forEach(workflowGraph::addVertex);
		for (EdgeVO evo : gvo.getEdges()) {
			workflowGraph.addEdge(evo.getSource().getId(), evo.getTarget().getId(), evo);
		}

		// required
		List<String> sysNodes = Arrays.asList(NodeTask.START, NodeTask.END);
		
		sysNodes.forEach(requiredVertex -> {
			Assert.isTrue(workflowGraph.containsVertex(requiredVertex), "未找到 " + requiredVertex + "节点");
		});

		// 校验graph是否正确,检查孤立点
		workflowGraph.vertexSet().forEach(tk -> {
			// 开始节点
			if (NodeTask.START.equals(tk)) {
				if (workflowGraph.inDegreeOf(tk) > 0) {
					throw new AppException("开始节点不能作为下游节点");
				}
			}
			// 结束节点
			else if (NodeTask.END.equals(tk)) {
				if (workflowGraph.outDegreeOf(tk) > 0) {
					throw new AppException("结束节点不能作为上游节点");
				}
			}
			// 其他节点
			else {
				if (workflowGraph.inDegreeOf(tk) == 0 || workflowGraph.outDegreeOf(tk) == 0) {
					throw new AppException("节点" + tk + "依赖配置错误");
				}
			}
		});

		// nodes
		List<NodeTask> nodes = nodeMap.values().stream().map(node -> {
			NodeTask tk = new NodeTask();
			tk.setPid(id);
			tk.setId(node.getId());
			tk.setTaskId(Objects.requireNonNull(node.getTaskId(), "数据格式错误"));
			if (sysNodes.contains(node.getId())) {
				tk.setDomain("idc");
			} else {
				tk.setDomain(Objects.requireNonNull(node.getDomain(), "数据格式错误"));
			}
			tk.setTaskType(node.getTaskType());
			return tk;
		}).collect(Collectors.toList());

		// edges
		List<WorkflowEdge> edges = gvo.getEdges().stream().map(evo -> {
			WorkflowEdge we = new WorkflowEdge();
			we.setPid(id);
			we.setId(evo.getId());
			we.setSource(evo.getSource().getId());
			we.setTarget(evo.getTarget().getId());
			return we;
		}).collect(Collectors.toList());

		Workflow workflow = getModel(id);
		workflow.setTaskNodes(nodes);
		workflow.setEdges(edges);
		workflowRepository.save(workflow);
	}

	@Transactional
	public void save(WorkflowVO vo) {
		
		if (workflowRepository.existsById(vo.getId())) {
			throw new AppException("任务已存在");
		}
		// 格式化graph
		Workflow workflow = new Workflow();
		BeanUtils.copyProperties(vo, workflow);
		// nodes
		workflowRepository.save(workflow);
	}
	
	@Transactional
	public void update(WorkflowVO vo) {
		Workflow workflow = getModel(vo.getId());
		BeanUtils.copyProperties(vo, workflow);
		// nodes
		workflowRepository.save(workflow);
	}
	
	Workflow get(String id) {
		return workflowRepository.findById(id).orElseThrow(() -> new AppException("工作流不存在"));
	}
	
	public WorkflowVO getWorkflow(String workflowId) {

		Workflow workflow = get(workflowId);
		WorkflowVO vo = new WorkflowVO();
		
		BeanUtils.copyProperties(workflow, vo);
		return vo;
	}
	
	
	Workflow getModel(String id) {
		return workflowRepository.findById(id).orElseThrow(() -> new AppException("工作流不存在"));
	}

	@Transactional
	public void delete(String id) {
		workflowRepository.deleteById(id);
	}
}
