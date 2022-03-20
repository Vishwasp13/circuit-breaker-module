package com.resiliency.connectors.internal;

public class CBListenerEvent {
	
	private String circuitBreakerId;
	private CBState oldState;
	private CBState updatedState;
	
	public CBListenerEvent(String circuitBreakerId, CBState oldState, CBState updatedState) {
		this.circuitBreakerId = circuitBreakerId;
		this.oldState = oldState;
		this.updatedState = updatedState;
	}
	public String getCircuitBreakerId() {
		return circuitBreakerId;
	}
	public void setCircuitBreakerId(String circuitBreakerId) {
		this.circuitBreakerId = circuitBreakerId;
	}
	public CBState getOldState() {
		return oldState;
	}
	public void setOldState(CBState oldState) {
		this.oldState = oldState;
	}
	public CBState getUpdatedState() {
		return updatedState;
	}
	public void setUpdatedState(CBState updatedState) {
		this.updatedState = updatedState;
	}
	

}
