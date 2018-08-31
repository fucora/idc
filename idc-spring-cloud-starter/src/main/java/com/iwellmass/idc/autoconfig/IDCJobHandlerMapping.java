package com.iwellmass.idc.autoconfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class IDCJobHandlerMapping extends RequestMappingHandlerMapping {

	private Map<String, IDCJobEndpoint> idcJobMap;

	@Override
	protected void initHandlerMethods() {
		if (idcJobMap == null || idcJobMap.isEmpty()) {
			return;
		}
		idcJobMap.forEach(this::detectHandlerMethods);
		handlerMethodsInitialized(getHandlerMethods());
	}

	private void detectHandlerMethods(String name, IDCJobEndpoint handler) {
		Class<?> handlerType = handler.getClass();
		final Class<?> userType = ClassUtils.getUserClass(handlerType);
		Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
				new MethodIntrospector.MetadataLookup<RequestMappingInfo>() {
					@Override
					public RequestMappingInfo inspect(Method method) {
						try {
							return getMappingForMethod(method, userType);
						} catch (Throwable ex) {
							throw new IllegalStateException(
									"Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
						}
					}
				});

		if (logger.isDebugEnabled()) {
			logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
		}
		for (Map.Entry<Method, RequestMappingInfo> entry : methods.entrySet()) {
			Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
			RequestMappingInfo mapping = entry.getValue();

			mapping = createRequestMappingInfo(newRequesMapping(new String[] { name }), null).combine(mapping);

			registerHandlerMethod(handler, invocableMethod, mapping);
		}
	}

	public Map<String, IDCJobEndpoint> getDynamicControllerMap() {
		return idcJobMap;
	}

	public void setDynamicControllerMap(Map<String, IDCJobEndpoint> idcJobMap) {
		this.idcJobMap = idcJobMap;
	}

	private RequestMapping newRequesMapping(String[] paths) {
		RequestMapping annotation = new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return RequestMapping.class;
			}

			@Override
			public String[] value() {
				return paths;
			}

			@Override
			public String[] produces() {
				return new String[] { MediaType.APPLICATION_JSON_VALUE };
			}

			@Override
			public String[] path() {
				return paths;
			}

			@Override
			public String[] params() {
				return null;
			}

			@Override
			public String name() {
				return null;
			}

			@Override
			public RequestMethod[] method() {
				return null;
			}

			@Override
			public String[] headers() {
				return null;
			}

			@Override
			public String[] consumes() {
				return new String[] { MediaType.APPLICATION_JSON_VALUE };
			}
		};
		return annotation;
	}

}
