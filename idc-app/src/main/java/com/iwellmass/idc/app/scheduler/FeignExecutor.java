package com.iwellmass.idc.app.scheduler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.iwellmass.auth.config.ResourceServerConfig;
import com.iwellmass.idc.ExecuteRequest;
import com.iwellmass.idc.executor.IDCJobExecutorService;
import feign.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.scheduler.service.IDCJobExecutor;

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
    public void execute(ExecuteRequest request) {

//		IDCJob idcJob = registryMap.computeIfAbsent(request.getDomain(),request.getDomain(),this::newFeignClient);

        String _key = request.getContentType();
        IDCJob idcJob = registryMap.computeIfAbsent(_key, (key) -> newFeignClient(request.getContentType()));
        idcJob.execute(request);
    }

    private IDCJob newFeignClientold() {
        String path = String.format("http://data-factory-executor/idc-job/execute");
        LOGGER.info("Create fegin-base IDCJob: {}", path);
        IDCJob feginClient = Feign.builder().client(client).encoder(encoder).decoder(decoder).contract(contract)
                .target(new CustomerTarget<>(IDCJob.class, path, getToken()));
        return feginClient;
    }

//	public IDCJobExecutorService getExecutor(NodeJob nodeTask) {
//		// 域 + contentType
//		String _key = nodeTask.getTaskGroup() + nodeTask.getContentType();
//		IDCJobExecutorService service = registryMap.computeIfAbsent(_key, (key) -> {
//			return newFeignClient(nodeTask.getTaskGroup(), nodeTask.getContentType());
//		});
//		return service;
//	}

    private IDCJob newFeignClient(String contentType) {
        String path = "http://data-factory-executor" + IDCJobExecutorService.toURI(contentType);
        LOGGER.info("create fegin client: {}", path);
        IDCJob feginClient = Feign.builder().client(client).encoder(encoder).decoder(decoder)
                .contract(contract).target(new CustomerTarget<>(IDCJob.class, path, getToken()));
        return feginClient;
    }

    public String getToken() {
        return ResourceServerConfig.CUSTOM_TOKEN;
    }

    public static class CustomerTarget<T> extends Target.HardCodedTarget<T> {

        String token;

        public CustomerTarget(Class<T> type, String url, String token) {
            super(type, url);
            this.token = token;
        }

        @Override
        public Request apply(RequestTemplate input) {
            if (input.url().indexOf("http") != 0) {
                input.insert(0, url());
            }
            input.header("Authorization", token);
            return input.request();
        }
    }


}
