package com.resiliency.connectors.configuration;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import com.resiliency.connectors.internal.CBClusterService;
import com.resiliency.connectors.internal.CBListener;
import com.resiliency.connectors.internal.CBOperations;
@Operations(CBOperations.class)
@Sources(CBListener.class)
public class CBConfigurationService implements Startable, Stoppable{
	private static final Logger logger = getLogger(CBConfigurationService.class);
	
	@ParameterGroup(name="Cluster Config")
	private CBConfigParameters config;

	@Override
	public void stop() throws MuleException {
		CBClusterService.destroyInstances();
		logger.info("Hazelcast instance destroyed");
	}

	@Override
	public void start() throws MuleException {
		
		CBClusterService.setServiceInstance(config);
		logger.info("Hazelcast instance initialzed");
		
	}
	
	


}