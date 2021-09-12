package com.resiliency.connectors.extension;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

import com.resiliency.connectors.configuration.CBConfigurationService;

@Xml(prefix="cb")
@Extension(name="Circuit Breaker")
@Configurations(CBConfigurationService.class)
public class CBExtension {

}