package com.resiliency.connectors.internal;

import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;

@Alias("CBListener")
public class CBListener extends Source<CBListenerEvent,Void>{
	
	private static final Logger logger = getLogger(CBListener.class);
	

	@Override
	public void onStart(SourceCallback<CBListenerEvent, Void> sourceCallback) throws MuleException {
		// TODO Auto-generated method stub
		logger.debug("Starting circuit breaker listener");
		CBClusterService.getServiceInstance().getCbStateMap().addEntryListener(
				new EntryAdapter<String, CBState>(){
					@Override
					public void entryAdded(EntryEvent<String,CBState> event) {
						logger.debug("Entry added in replicated map");
						handleStateChange(sourceCallback, event);
					}
					@Override
                    public void entryUpdated(EntryEvent<String,CBState> event) {
						logger.debug("Entry updated in replicated map");
						handleStateChange(sourceCallback, event);
					}
					
					
				}
				);
		
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		logger.info("CBListener stopped");
		
	}
	
	private void handleStateChange(SourceCallback<CBListenerEvent, Void> sourceCallback, EntryEvent<String, CBState> event) {
		final CBState value = event.getValue();
		sourceCallback.handle(Result.<CBListenerEvent,Void>builder().output(new CBListenerEvent(event.getKey(), event.getOldValue(), value)).build());
		
	}

}
