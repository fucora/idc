package com.iwellmass.idc.app.service;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwellmass.idc.app.vo.WorkflowVO;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class WorkflowServiceTests {
	
	@Resource
	WorkflowRepository repository;
	
	String str = "{\n" + 
			"    \"id\": \"my-test\",\n" + 
			"    \"taskName\": \"aa\",\n" + 
			"    \"description\": \"bb\",\n" + 
			"    \"graph\": {\n" + 
			"      \"nodes\": [\n" + 
			"        {\n" + 
			"          \"id\": \"835-data-factory\",\n" + 
			"          \"taskId\": \"835\",\n" + 
			"          \"taskType\": \"SIMPLE\",\n" + 
			"          \"domain\": \"data-factory\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"id\": \"END\",\n" + 
			"          \"taskId\": \"end\",\n" + 
			"          \"taskType\": null,\n" + 
			"          \"domain\": \"idc\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"id\": \"START\",\n" + 
			"          \"taskId\": \"start\",\n" + 
			"          \"taskType\": null,\n" + 
			"          \"domain\": \"idc\"\n" + 
			"        }\n" + 
			"      ],\n" + 
			"      \"edges\": [\n" + 
			"        {\n" + 
			"          \"id\": \"835-data-factory-END\",\n" + 
			"          \"source\": {\n" + 
			"            \"id\": \"835-data-factory\"\n" + 
			"          },\n" + 
			"          \"target\": {\n" + 
			"            \"id\": \"END\"\n" + 
			"          }\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"id\": \"START-835-data-factory\",\n" + 
			"          \"source\": {\n" + 
			"            \"id\": \"START\"\n" + 
			"          },\n" + 
			"          \"target\": {\n" + 
			"            \"id\": \"835-data-factory\"\n" + 
			"          }\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    }\n" + 
			"  }";
	
	
	@Test
	@Rollback(false)
	public void save() throws IOException {
		WorkflowService service = new WorkflowService();
		service.workflowRepository = repository;

		
		ObjectMapper mapper = new ObjectMapper();
		WorkflowVO vo = mapper.readerFor(WorkflowVO.class).readValue(str);
		
		service.save(vo);
	}
	
}
