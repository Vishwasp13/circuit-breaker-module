package com.resiliency.connectors.utility;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;

import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.extension.api.runtime.parameter.ParameterResolver;
import org.slf4j.Logger;

import com.hazelcast.crdt.pncounter.PNCounter;


public class CBUtility {
	private static final Logger logger = getLogger(CBUtility.class);
	
	public static CoreEvent getCoreEvent(Object result, String methodName) {
		CoreEvent event = null;
		try {
			Method getEventMethod = result.getClass().getMethod(methodName);
			event = (CoreEvent) getEventMethod.invoke(result);	
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error("Error occurred in reflection utility {}", e.getCause());
			throw new RuntimeException("Error occurred in reflection");
		}
		
		return event;
	}
	
	
	public static boolean isError(ParameterResolver<Object> resolver, CoreEvent event, ExpressionManager expressionManager) {
		Boolean isError = null;
		if(resolver.getClass().getName().contains("ExpressionBasedParameterResolver")) {
			     isError =  resolvePrimitiveBoolean(expressionManager.evaluate(resolver.getExpression().get(), event).getValue());
		}
		else {
			isError = resolvePrimitiveBoolean(resolver.getExpression().get());
		}
		
		return isError;
	}
	
	public static boolean resolvePrimitiveBoolean(Object object) {
		Boolean resolvedBoolean = null;
		if(object instanceof Boolean) {
			resolvedBoolean = (Boolean) object;
		}
		else if(object instanceof String && "true".equalsIgnoreCase((String) object)) {
			resolvedBoolean = true;
		}
		else
		{
			resolvedBoolean = false;
		}
		
		return resolvedBoolean;
		
	}
	
	public static void resetPNCounter(PNCounter pnCounter) {
		if(pnCounter != null) {
			logger.debug("Destroying PN Counter");
			pnCounter.destroy();
			logger.debug("PN Counter destroyed");
		}
	}

}
