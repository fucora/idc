package com.iwellmass.idc.app.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.scheduler.model.AbstractJob;
import com.iwellmass.idc.scheduler.model.AbstractTask;
import com.iwellmass.idc.scheduler.service.IDCJobExecutor;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;

@Component
@Import(FeignClientsConfiguration.class)
public class FeignExecutor implements IDCJobExecutor {

	static final Logger LOGGER = LoggerFactory.getLogger(FeignExecutor.class);

	private static final Map<String, IDCJob> registryMap = new ConcurrentHashMap<>();

	@Resource
	Decoder decoder;

	@Resource
	Encoder encoder;

	@Resource
	Client client;

	@Resource
	Contract contract;

	@Override
	public void execute(AbstractJob job) {

		AbstractTask task = job.getTask();

		IDCJob idcJob = registryMap.computeIfAbsent(task.getDomain(), this::newFeignClient);

		idcJob.execute(null);

		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LOGGER.info("Go >> {}", i);
		}
	}

	private IDCJob newFeignClient(String domain) {
		String path = String.format("http://%s/idc-job/execute", domain);
		LOGGER.info("Create fegin-base IDCJob: {}", path);
		IDCJob feginClient = Feign.builder().client(client).encoder(encoder).decoder(decoder).contract(contract)
				.target(IDCJob.class, path);
		return feginClient;
	}
}
