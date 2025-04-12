package com.resiliency.connectors.internal;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.stream.Collectors;


import org.apache.commons.lang3.StringUtils;
import java.time.Duration;
import org.slf4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.replicatedmap.ReplicatedMap;
import com.resiliency.connectors.configuration.CBConfigParameters;
import com.resiliency.connectors.utility.CBConstants;


public class CBClusterService {
	private static CBClusterService serviceInstance = null;
	private static HazelcastInstance hazelcastInstance = null;
	private ReplicatedMap<String,CBState> cbStateMap = null;
	private Map<String,PNCounter> errorCounter = new HashMap<>();
	private Map<String,PNCounter> requestCounter = new HashMap<>();
	
	private static final Logger logger = getLogger(CBClusterService.class);
	
	private CBClusterService(CBConfigParameters configParameters) {
		logger.debug("Intializing Hazelcast cluster");
		Config hazelCastConfig = new Config();
		hazelCastConfig.getNetworkConfig().setPublicAddress(configParameters.getHostIP()).setPort(Integer.parseInt(configParameters.getClusterPort()));
		JoinConfig join = hazelCastConfig.getNetworkConfig().getJoin();
		
		if(!StringUtils.isBlank(configParameters.getMembersIPs())) {
			List<String> membersIpList = Arrays.stream(configParameters.getMembersIPs().split(",")).map(ip -> ip + ":" + configParameters.getClusterPort()).collect(Collectors.toList());
			join.getTcpIpConfig().setEnabled(true).setMembers(membersIpList);
		}
		hazelcastInstance = Hazelcast.newHazelcastInstance(hazelCastConfig);
		cbStateMap = hazelcastInstance.getReplicatedMap(CBConstants.REPLCATED_MAP_NAME);
		logger.debug("Hazelcast instance initialized successfully.");
		
	}
	
	public static synchronized CBClusterService setServiceInstance(CBConfigParameters cbConfigParam){
		if(serviceInstance == null) {
			serviceInstance = new CBClusterService(cbConfigParam);
			return serviceInstance;
		}
		throw new IllegalStateException("Hazelcast instance is already initialized, only one circuit breaker config per application is allowed");
	}
	
	public static synchronized CBClusterService getServiceInstance() {
		if(serviceInstance != null) {
			return serviceInstance;
		}
		throw new IllegalStateException("Hazelcast instance not yet initialized, please add a circuit breaker config");
	}
	
	public static void destroyInstances() {
		hazelcastInstance.shutdown();
		serviceInstance = null;
	}
	
	public static void scheduleTasks(Runnable runnable, String delay) {
		hazelcastInstance.getScheduledExecutorService(CBConstants.REPLCATED_MAP_NAME).schedule(runnable, Duration.parse(delay).toMillis(), TimeUnit.MILLISECONDS);
	}
	
	public static HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}

	public Map<String,PNCounter> getErrorCounter() {
		return errorCounter;
	}

	public void setErrorCounter(Map<String,PNCounter> errorCounter) {
		this.errorCounter = errorCounter;
	}

	public Map<String,PNCounter> getRequestCounter() {
		return requestCounter;
	}

	public void setRequestCounter(Map<String,PNCounter> requestCounter) {
		this.requestCounter = requestCounter;
	}
	
	
	public void upsertCbStateMapEntries(String circuitBreakerId, CBState state) {
		logger.debug("Upserting state for {} state as {}", circuitBreakerId,state);
		cbStateMap.put(circuitBreakerId, state);
	}
	public ReplicatedMap<String,CBState> getCbStateMap() {
		return cbStateMap;
	}

}
