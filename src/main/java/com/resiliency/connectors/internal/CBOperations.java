package com.resiliency.connectors.internal;

import static org.slf4j.LoggerFactory.getLogger;

import javax.inject.Inject;

import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.parameter.ParameterResolver;
import org.mule.runtime.extension.api.runtime.process.RouterCompletionCallback;
import org.slf4j.Logger;

import com.resiliency.connectors.routes.FallbackRoute;
import com.resiliency.connectors.routes.MainRoute;
import static com.resiliency.connectors.utility.CBUtility.getCoreEvent;
import static com.resiliency.connectors.utility.CBUtility.isError;
import org.mule.runtime.core.api.event.CoreEvent;
public class CBOperations {
	private static final Logger logger = getLogger(CBOperations.class);
	@Inject
	private ExpressionManager expressionManager;
	
	@DisplayName("Circuit Breaker")
	public void circuitBreaker(
			@Optional(defaultValue="#[app.name]")
			@Summary("Unique Id for the circuit breaker, should be unique for every application to unexpected behaviour")
			@DisplayName("Circuit Breaker Id")
			String circuitBreakerId,
			@Optional(defaultValue="100")
			@Summary("Threshold percentage of errors after which the circuit will trip")
			@DisplayName("Threshold Percentage")
			String thresholdPercentage,
			@Optional(defaultValue="PT1M")
			@Summary("ISO-8601 Duration, it determines the duration for which the errors should be monitored")
			@DisplayName("Threshold Period")
			String thresholdPeriod,
			@Optional(defaultValue="PT1M")
			@Summary("ISO-8601 Duration, it determines the duration for which the circuit remains open")
			@DisplayName("Open Duration")
			String openDuration,
			@Optional(defaultValue="#[output application/java --- error != null]")
			@Summary("DW expression to specify the error, the expression must evaluate to true to consider an error to trip the cb")
			@DisplayName("Error Expression")
			ParameterResolver<Object> errorExpression,
			@DisplayName("Main Route")
			MainRoute mainRoute,
			@DisplayName("Fallback Route")
			FallbackRoute fallbackRoute,
			RouterCompletionCallback callback) {
			CBProcessor cbProcessor = new CBProcessor(circuitBreakerId,thresholdPercentage,thresholdPeriod,openDuration);
			
			switch(cbProcessor.getCbState()) {
				case CLOSED:
					mainRoute.getChain().process(result -> {
						CoreEvent event = getCoreEvent(result, "event");
						if(isError(errorExpression, event, expressionManager)) {
							cbProcessor.closedStateErrorHandler();
						}
						callback.success(result);
						
					}, (error, previous) -> {
						CoreEvent event = getCoreEvent(error, "event");
						if(isError(errorExpression, event, expressionManager)) {
							cbProcessor.closedStateErrorHandler();
						}
						callback.error(error);
					});
					break;
					
				case OPEN:
					fallbackRoute.getChain().process(result -> {
						callback.success(result);
					}, (error,previous) -> {
						callback.error(error);
					});
					break;
				
				case ERROR:
					mainRoute.getChain().process(result -> {
						CoreEvent event = getCoreEvent(result, "event");
						if(isError(errorExpression, event, expressionManager)) {
							cbProcessor.errorStateErrorHandler();
						}
						callback.success(result);
						
					}, (error, previous) -> {
						CoreEvent event = getCoreEvent(error, "event");
						if(isError(errorExpression, event, expressionManager)) {
							cbProcessor.errorStateErrorHandler();
						}
						callback.error(error);
					});
					logger.info("Error State");
					break;
					
			   default:
				   logger.info("Unexpected circuit state");
			
			}
			
		
	}

}
