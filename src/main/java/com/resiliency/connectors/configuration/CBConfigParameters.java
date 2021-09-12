package com.resiliency.connectors.configuration;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

public class CBConfigParameters {
	@Parameter
	@Optional
	@DisplayName("Members IP Address")
	@Summary("Comma separated IP address of members who would join the cluster")
	private String membersIPs;
	@Parameter
	@Optional(defaultValue="127.0.0.1")
	@DisplayName("Host IP Address")
	@Summary("IP address of the system that will host this connector")
	private String hostIP;
	@Parameter
	@Optional(defaultValue="5701")
	@DisplayName("Cluster Port")
	@Summary("Port that will be used by Hazelcast")
	private String clusterPort;
	public String getMembersIPs() {
		return membersIPs;
	}
	public void setMembersIPs(String membersIPs) {
		this.membersIPs = membersIPs;
	}
	public String getHostIP() {
		return hostIP;
	}
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
	public String getClusterPort() {
		return clusterPort;
	}
	public void setClusterPort(String clusterPort) {
		this.clusterPort = clusterPort;
	}
	
	

}
