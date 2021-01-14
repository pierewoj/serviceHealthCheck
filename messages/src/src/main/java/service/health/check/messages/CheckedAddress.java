package service.health.check.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckedAddress {

	@JsonProperty("host")
	private String host;

	@JsonProperty("port")
	private String port;

	@JsonProperty("healthy")
	private boolean healthy;

	public CheckedAddress() {

	}

	public CheckedAddress(String host, String port, boolean healthy) {
		this.host = host;
		this.port = port;
		this.healthy = healthy;
	}

	public boolean getHealthy() {
		return healthy;
	}

	public void setHealthy(boolean healthy) {
		this.healthy = healthy;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
