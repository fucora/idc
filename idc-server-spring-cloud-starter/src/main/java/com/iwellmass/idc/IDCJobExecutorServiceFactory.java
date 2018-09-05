package com.iwellmass.idc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.service.RestIDCJobExecutor;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;

@Component
@Import(FeignClientsConfiguration.class)
public class IDCJobExecutorServiceFactory {

	@Inject
	private Decoder decoder;
	
	@Inject
	private Encoder encoder;
	
	@Inject
	private Client client;
	
	@Inject
	private Contract contract;

	private Map<String, IDCJobExecutorService> registryMap = new ConcurrentHashMap<>();

	public IDCJobExecutorService getExecutor(String domain, String jobName) {
		IDCJobExecutorService service = registryMap.computeIfAbsent(domain, (key) -> {
			return newFeignClient(domain, jobName);
		});
		return service;
	}

	private IDCJobExecutorService newFeignClient(String name, String jobName) {
		String path = "http://" + name + MessageFormatter.arrayFormat(IDCJobExecutorService.RESOURCE_URI_TEMPLATE, new Object[] {
				jobName
		}).getMessage();
		
		RestIDCJobExecutor feginClient = Feign.builder()
			.client(client)
			.encoder(encoder)
			.decoder(decoder)
			.contract(contract)
			.target(RestIDCJobExecutor.class, path);
		return feginClient;
	}
}
