package com.resiliency.connectors.tasks;

import java.io.Serializable;
import java.math.BigDecimal;

import org.slf4j.Logger;

import com.resiliency.connectors.internal.CBClusterService;
import com.resiliency.connectors.internal.CBState;

import com.resiliency.connectors.utility.CBUtility;

import static org.slf4j.LoggerFactory.getLogger;
public class TripCircuitTask implements Runnable, Serializable{
	private static final long serialVersionUID = 1746122135737967021L;
	private static final Logger logger = getLogger(TripCircuitTask.class);
	private String circuitBreakerId;
	private BigDecimal thresholdPercentage;
	private String openDuration;
	private Long requests;
	private Long errors;
	
	public TripCircuitTask(String circuitBreakerId, BigDecimal thresholdPercentage, Long requests,Long errors,String openDuration) {
		this.circuitBreakerId = circuitBreakerId;
		this.thresholdPercentage = thresholdPercentage;
		this.requests = requests;
		this.errors = errors;
		this.openDuration = openDuration;
	}
	
	@Override
	public void run() {
	    BigDecimal ratio = new BigDecimal(errors).divide(new BigDecimal(requests));
	    logger.info("Error Count {}", errors);
	    logger.info("Request {}", requests);
	    logger.info("ratio {}", ratio);
	    if(ratio.compareTo(thresholdPercentage.divide(new BigDecimal(100))) >= 1) {
	    	logger.info("Error ratio is less than the defined threshold, state will be changed back to CLOSED");
	    	CBClusterService.getServiceInstance().upsertCbStateMapEntries(circuitBreakerId, CBState.CLOSED);
	    	logger.info("Circuit state changed to CLOSED");
	    	logger.info("Resetting errors and requests count.");
	    	CBUtility.resetPNCounter(CBClusterService.getServiceInstance().getErrorCounter().remove(circuitBreakerId));
			CBUtility.resetPNCounter(CBClusterService.getServiceInstance().getRequestCounter().remove(circuitBreakerId));
	    	logger.info("Reset completed");
	    }
	    else {
	    	logger.info("Error exceeded the defined threshold, circuit will be opened");
	    	CBClusterService.getServiceInstance().upsertCbStateMapEntries(circuitBreakerId, CBState.OPEN);
	    	logger.info("Circuit State changed to OPEN");
	    	CBClusterService.scheduleTasks(new RestoreCircuitTask(circuitBreakerId), openDuration);
	    	
	    }
	    
		
	}

}
