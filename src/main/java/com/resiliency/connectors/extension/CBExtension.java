package com.resiliency.connectors.extension;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.meta.JavaVersion;
import com.resiliency.connectors.configuration.CBConfigurationService;

@Xml(prefix="cb")
@Extension(name="Circuit Breaker")
@Configurations(CBConfigurationService.class)
@JavaVersionSupport({JavaVersion.JAVA_8, JavaVersion.JAVA_11, JavaVersion.JAVA_17})
public class CBExtension {

}