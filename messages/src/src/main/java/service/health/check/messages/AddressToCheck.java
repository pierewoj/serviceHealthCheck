package service.health.check.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressToCheck {

	@JsonProperty("host")
	private String host;

	@JsonProperty("port")
	private String port;

	public AddressToCheck() {

	}

	public AddressToCheck(String host, String port) {
		this.host = host;
		this.port = port;
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
