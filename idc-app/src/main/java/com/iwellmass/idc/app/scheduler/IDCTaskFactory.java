package com.iwellmass.idc.app.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.scheduler.model.Job;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;

@Component
@Import(FeignClientsConfiguration.class)
public class IDCTaskFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTaskFactory.class);

	@Inject
	private Decoder decoder;

	@Inject
	private Encoder encoder;

	@Inject
	private Client client;

	@Inject
	private Contract contract;

	private Map<String, IDCJob> registryMap = new ConcurrentHashMap<>();

	public IDCJob getExecutor(Job task) {
		// 域 + contentType
//		String _key = task.getTaskGroup() + task.getContentType();
//		return registryMap.computeIfAbsent(_key, (key) -> {
//			return newFeignClient(task.getTaskGroup(), task.getContentType());
//		});
		return null;
	}

	private IDCJob newFeignClient(String domain, String contentType) {
		String path = "http://" + domain + IDCJobExecutorService.toURI(contentType);
		LOGGER.info("create fegin client: {}", path);
		IDCJob feginClient = Feign.builder().client(client).encoder(encoder).decoder(decoder)
				.contract(contract).target(IDCJob.class, path);
		return feginClient;
	}

}
