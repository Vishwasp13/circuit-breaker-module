package com.resiliency.connectors.internal;

import org.slf4j.Logger;

import com.resiliency.connectors.tasks.TripCircuitTask;
import com.resiliency.connectors.utility.CBConstants;

import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigDecimal;

public class CBProcessor {
	
	private String circuitBreakerId;
	private String thresholdPercentage;
	private String thresholdPeriod;
	private String openDuration;
	private CBState cbState;
	
	private static Logger logger = getLogger(CBProcessor.class);
	
	public CBProcessor(String circuitBreakerId, String thresholdPercentage, String thresholdPeriod,  String openDuration) {
		this.circuitBreakerId = circuitBreakerId;
		this.thresholdPercentage = thresholdPercentage;
		this.thresholdPeriod = thresholdPeriod;
		this.openDuration = openDuration;
		
		if(CBClusterService.getServiceInstance().getCbStateMap().get(circuitBreakerId) == null) {
			cbState = CBState.CLOSED;
			CBClusterService.getServiceInstance().upsertCbStateMapEntries(circuitBreakerId, CBState.CLOSED);
		}
		else {
			cbState = CBClusterService.getServiceInstance().getCbStateMap().get(circuitBreakerId);
		}
		
	}
	
	public CBState getCbState() {
		return cbState;
	}
	
	public synchronized void closedStateErrorHandler() {
		try {
			logger.debug("Error detected, error counter will be increased and state will be changed to ERROR, Current state is CLOSED");
			long errorCount = incrementErrors();
			logger.debug("Updated Error Count {}", errorCount);
			long requestCount = incrementRequests();
			logger.debug("Updated Request Count {}", requestCount);
			CBClusterService.getServiceInstance().upsertCbStateMapEntries(circuitBreakerId, CBState.ERROR);
			TripCircuitTask tripTask = new TripCircuitTask(circuitBreakerId, new BigDecimal(thresholdPercentage), getRequests(), getErrors(), openDuration);
			CBClusterService.scheduleTasks(tripTask, thresholdPeriod);
			logger.debug("Task scheduled for error check");
		}
		catch(Throwable throwable) {
			logger.error("Failure occurred while handling error in closed state {} {}", throwable.getMessage(), throwable);
		}
	}
	public synchronized void errorStateErrorHandler() {
		try {
			logger.debug("Errors detected, error counder will be increased, current state is ERROR");
			long errorCount = incrementErrors();
			logger.debug("Updated Error Count {}", errorCount);
			long requestCount = incrementRequests();
			logger.debug("Updated Request Count {}", requestCount);
		}
		catch(Throwable throwable) {
			logger.error("Failure occurred while handling error in error state {} {}", throwable.getMessage(), throwable);
		}
	}
	
	public long incrementErrors() {
		return CBClusterService.getServiceInstance().getErrorCounter().computeIfAbsent(circuitBreakerId, cbId -> CBClusterService.getHazelcastInstance().getPNCounter(CBConstants.ERROR + cbId)).incrementAndGet();
	}
	
	public long incrementRequests() {
		return CBClusterService.getServiceInstance().getRequestCounter().computeIfAbsent(circuitBreakerId, cbId -> CBClusterService.getHazelcastInstance().getPNCounter(CBConstants.REQUESTS + cbId)).incrementAndGet();
	}
	
	public long getErrors() {
		return CBClusterService.getHazelcastInstance().getPNCounter(CBConstants.ERROR + circuitBreakerId).get();
	}
	
	public long getRequests() {
		return CBClusterService.getHazelcastInstance().getPNCounter(CBConstants.REQUESTS + circuitBreakerId).get();
	}
	
	

}
