package com.iwellmass.idc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.Job;
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

	public IDCJobExecutorService getExecutor(Job job) {
		IDCJobExecutorService service = registryMap.computeIfAbsent(job.getGroupId(), (key) -> {
			return newFeignClient(job.getGroupId(), job.getContentType());
		});
		return service;
	}

	private IDCJobExecutorService newFeignClient(String domain, String contentType) {
		String path = "http://" + domain + IDCJobExecutorService.toURI(contentType);
		RestIDCJobExecutor feginClient = Feign.builder().client(client).encoder(encoder).decoder(decoder)
				.contract(contract).target(RestIDCJobExecutor.class, path);
		return feginClient;
	}

}
