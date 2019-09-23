package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.xml.soap.Node;

import com.google.common.collect.Lists;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.app.vo.CloneWorkflowVO;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.*;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@Service
public class WorkflowService {

    @Resource
    WorkflowRepository workflowRepository;
    @Resource
    JobRepository jobRepository;
    @Resource
    NodeJobRepository nodeJobRepository;
    @Resource
    TaskRepository taskRepository;

    public PageData<WorkflowVO> query(WorkflowQueryParam qm) {
        return QueryUtils.doJpaQuery(qm, pageable -> {
            Specification<Workflow> spec = SpecificationBuilder.toSpecification(qm);
            return workflowRepository.findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "updatetime")).map(model -> {
                WorkflowVO vo = new WorkflowVO();
                BeanUtils.copyProperties(model, vo);
                vo.setCanModify(canModify(vo.getId()));
                vo.setCanDelete(canDelete(vo.getId()));
                return vo;
            });
        });
    }

    public List<WorkflowVO> queryAll() {
        return workflowRepository.findAll(null, Sort.by(Sort.Direction.DESC, "updatetime")).stream().map(model -> {
            WorkflowVO vo = new WorkflowVO();
            BeanUtils.copyProperties(model, vo);
            vo.setCanModify(canModify(vo.getId()));
            vo.setCanDelete(canDelete(vo.getId()));
            return vo;
        }).collect(Collectors.toList());
    }

    public GraphVO getGraph(String workflowId) {
        Workflow workflow = get(workflowId);

        // 设置前端 VO
        GraphVO gvo = new GraphVO();
        List<NodeTask> nodes = workflow.getNodeTasks();
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

    // must empty all task,job,nodeJob associated with this workflow.
    @Transactional
    public void saveGraph(String id, GraphVO gvo) {
        // empty task.job.nodeJob associated with this workflow.
        if (workflowRepository.findById(id).get().getNodeTasks().size() != 0) {
            // modify operation
            List<Task> tasks = taskRepository.findAllByWorkflowId(id);
            List<Job> jobs = jobRepository.findAllByTaskNameIn(tasks.stream().map(Task::getTaskName).collect(Collectors.toList()));
            List<NodeJob> nodeJobs = nodeJobRepository.findAllByContainerIn(jobs.stream().map(Job::getId).collect(Collectors.toList()));
            // nodeJob
            nodeJobRepository.deleteAll(nodeJobs);
            // job
            jobRepository.deleteAll(jobs);
            // task
            taskRepository.deleteAll(tasks);
        }

        // save new graph
        // format graph
        DirectedAcyclicGraph<String, EdgeVO> workflowGraph = new DirectedAcyclicGraph<>(EdgeVO.class);

        Map<String, NodeVO> nodeMap = gvo.getNodes().stream()
                .collect(Collectors.toMap(NodeVO::getId, Function.identity()));
        nodeMap.keySet().forEach(workflowGraph::addVertex);
        for (EdgeVO evo : gvo.getEdges()) {
            workflowGraph.addEdge(evo.getSource().getId(), evo.getTarget().getId(), evo);
        }


        List<String> necessaryNode = Arrays.asList(NodeTask.START, NodeTask.END);    // required
        List<String> systemNode = Arrays.asList(NodeTask.START.toLowerCase(), NodeTask.CONTROL.toLowerCase(), NodeTask.END.toLowerCase());
        necessaryNode.forEach(requiredVertex ->
                Assert.isTrue(workflowGraph.containsVertex(requiredVertex), "未找到 " + requiredVertex + "节点"));
        Assert.isTrue(gvo.getNodes().stream().anyMatch(nvo -> !systemNode.contains(nvo.getTaskId())), "未配置任何任务节点");

        // validate graph whether is legal and contain isolated node
        workflowGraph.vertexSet().forEach(tk -> {
            // start
            if (NodeTask.START.equalsIgnoreCase(tk)) {
                if (workflowGraph.inDegreeOf(tk) > 0) {
                    throw new AppException("开始节点不能作为下游节点");
                }
            }
            // end
            else if (NodeTask.END.equalsIgnoreCase(tk)) {
                if (workflowGraph.outDegreeOf(tk) > 0) {
                    throw new AppException("结束节点不能作为上游节点");
                }
            }
            // other
            else {
                if (workflowGraph.inDegreeOf(tk) == 0 || workflowGraph.outDegreeOf(tk) == 0) {
                    throw new AppException("节点" + tk + "依赖配置错误");
                }
            }
        });

        // nodes
        List<NodeTask> nodes = nodeMap.values().stream().map(node -> {
            NodeTask tk = new NodeTask();
            tk.setWorkflowId(id);
            tk.setId(node.getId());
            tk.setTaskName(node.getTaskName());
            tk.setContentType(node.getContentType());
            tk.setTaskType(TaskType.SIMPLE);
            tk.setTaskId(Objects.requireNonNull(node.getTaskId(), "数据格式错误"));
            if (systemNode.contains(node.getTaskId())) {
                tk.setDomain("idc");
            } else {
                tk.setDomain(Objects.requireNonNull(node.getDomain(), "数据格式错误"));
            }
            return tk;
        }).collect(Collectors.toList());

        // edges
        List<WorkflowEdge> edges = gvo.getEdges().stream().map(evo -> {
            WorkflowEdge we = new WorkflowEdge();
            we.setWorkflowId(id);
            we.setId(evo.getId());
            we.setSource(evo.getSource().getId());
            we.setTarget(evo.getTarget().getId());
            return we;
        }).collect(Collectors.toList());

        Workflow workflow = getModel(id);
        workflow.setUpdatetime(LocalDateTime.now());
        workflow.getEdges().clear();
        workflow.getNodeTasks().clear();
        workflow.getEdges().addAll(edges);
        workflow.getNodeTasks().addAll(nodes);
        workflowRepository.save(workflow);

    }

    @Transactional
    public void save(WorkflowVO vo) {

        if (workflowRepository.existsById(vo.getId())) {
            throw new AppException("任务已存在");
        }
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(vo, workflow);
        // nodes
        workflowRepository.save(workflow);
    }

    @Transactional
    public void update(WorkflowVO vo) {
        Workflow workflow = getModel(vo.getId());
        BeanUtils.copyProperties(vo, workflow);
        workflow.setUpdatetime(LocalDateTime.now());
        // nodes
        workflowRepository.save(workflow);
    }

    Workflow get(String id) {
        return workflowRepository.findById(id).orElseThrow(() -> new AppException("工作流:" + id + "不存在"));
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
        // todo 判断该工作流是否正在被使用中
        if (canDelete(id)) {
            List<Task> tasks = taskRepository.findAllByWorkflowId(id);
            List<Job> jobs = jobRepository.findAllByTaskNameIn(tasks.stream().map(Task::getTaskName).collect(Collectors.toList()));
            List<NodeJob> nodeJobs = nodeJobRepository.findAllByContainerIn(jobs.stream().map(Job::getId).collect(Collectors.toList()));
            // nodeJob
            nodeJobRepository.deleteAll(nodeJobs);
            // job
            jobRepository.deleteAll(jobs);
            // task
            taskRepository.deleteAll(tasks);
            // workflow
            workflowRepository.deleteById(id);
        } else {
            throw new AppException("该工作流存未完成的实例,不可删除");
        }
    }

    // judge the workflow whether can be deleted. when the all nodeJobs of job of task of workflow is complete ,the workflow can be deleted.
    public boolean canDelete(String wfId) {
        // todo 考虑工作流嵌套情况
        return nodeJobRepository.findAllByContainerIn(
                jobRepository.findAllByTaskNameIn(
                        taskRepository.findAllByWorkflowId(wfId).stream().map(Task::getTaskName).collect(Collectors.toList())
                ).stream().map(Job::getId).collect(Collectors.toList())
        ).stream().filter(nj -> !nj.isSystemNode()).allMatch(n -> n.getState() == JobState.FINISHED);
    }

    // a workflow only can be inited once.judge a workflow whether can be inited with this workflow whether exist task.
    public boolean canModify(String wfId) {
        return taskRepository.findAllByWorkflowId(wfId).size() == 0;
    }

    public List<WorkflowVO> queryAvailableWorkflow() {
        return workflowRepository.findAll(null, Sort.by(Sort.Direction.DESC, "updatetime"))
                .stream()
                .filter(workflow -> !Utils.isNullOrEmpty(workflow.getEdges()))
                .map(model -> {
                    WorkflowVO vo = new WorkflowVO();
                    BeanUtils.copyProperties(model, vo);
//                    vo.setCanModify(canDelete(vo.getId()));
                    return vo;
                })
                .collect(Collectors.toList());

    }

    // clone a workflow.contain clone the basic info of workflow and edge dependency and nodetaskInfo
    @Transactional
    public void clone(CloneWorkflowVO vo) {
        if (workflowRepository.findById(vo.getNewWorkflowId()).isPresent()) {
            throw new AppException("工作流已存在,id:" + vo.getNewWorkflowId() + ",请修改后再尝试");
        }
        Workflow oldWorkflow = get(vo.getOldWorkflowId());
        Workflow newWorkflow = new Workflow(vo.getNewWorkflowId(), vo.getWorkflowName(), vo.getDescription());
        List<String> systemNode = Arrays.asList(NodeTask.START, NodeTask.CONTROL, NodeTask.END);
        List<NodeTask> nodeTasks = oldWorkflow.getNodeTasks().stream().map(nt -> {
            NodeTask nodeTask = new NodeTask();
            nodeTask.setWorkflowId(vo.getNewWorkflowId());
            nodeTask.setId(nt.getId());
            nodeTask.setTaskName(nt.getTaskName());
            nodeTask.setContentType(nt.getContentType());
            nodeTask.setTaskId(Objects.requireNonNull(nt.getTaskId(), "数据格式错误"));
            if (systemNode.contains(nt.getId())) {
                nodeTask.setDomain("idc");
            } else {
                nodeTask.setDomain(Objects.requireNonNull(nt.getDomain(), "数据格式错误"));
            }
            return nodeTask;
        }).collect(Collectors.toList());

        // edges
        List<WorkflowEdge> workflowEdges = oldWorkflow.getEdges().stream().map(edge -> {
            WorkflowEdge we = new WorkflowEdge();
            we.setWorkflowId(vo.getNewWorkflowId());
            we.setId(edge.getId());
            we.setSource(edge.getSource());
            we.setTarget(edge.getTarget());
            return we;
        }).collect(Collectors.toList());

        newWorkflow.setUpdatetime(LocalDateTime.now());
        newWorkflow.setEdges(workflowEdges);
        newWorkflow.setNodeTasks(nodeTasks);
        workflowRepository.save(newWorkflow);

    }

}
