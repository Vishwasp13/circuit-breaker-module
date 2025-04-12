package com.resiliency.connectors.tasks;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;

import org.slf4j.Logger;

import com.resiliency.connectors.internal.CBClusterService;
import com.resiliency.connectors.internal.CBState;
import com.resiliency.connectors.utility.CBUtility;

public class RestoreCircuitTask implements Runnable, Serializable{

	private static final long serialVersionUID = 9074678505096626158L;
	private static final Logger logger = getLogger(RestoreCircuitTask.class);
	private String circuitBreakerId;
	
	public RestoreCircuitTask(String circuitBreakerId) {
		this.circuitBreakerId = circuitBreakerId;
	}

	@Override
	public void run() {
		logger.debug("Trip duration completed, restoring the circuit");
		CBClusterService.getServiceInstance().upsertCbStateMapEntries(circuitBreakerId, CBState.CLOSED);
		logger.debug("Circuit restored to CLOSED state");
		CBUtility.resetPNCounter(CBClusterService.getServiceInstance().getErrorCounter().remove(circuitBreakerId));
		CBUtility.resetPNCounter(CBClusterService.getServiceInstance().getRequestCounter().remove(circuitBreakerId));
		
		
	}
}
