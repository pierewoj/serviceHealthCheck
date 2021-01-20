package service.health.check.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name="configs")
public class Config implements Serializable {

	private static final long serialVersionUID = -2906374005396033669L;

	public enum ConfigName {
		/** Worker connection timeout in milliseconds  */
		WORKER_CONNECTION_TIMEOUT,
		/** Worker socket timeout in milliseconds */
		WORKER_SOCKET_TIMEOUT,
		/** Worker connection request timeout in milliseconds */
		WORKER_CONNECTION_REQUEST_TIMEOUT,
		/** Maximum time in seconds after which server is considered as inactive */
		SERVER_MAX_INACTIVE_TIME
	}

	public static final Map<ConfigName, String> defaultConfigsValues;

	static {
		defaultConfigsValues = new HashMap<>(ConfigName.values().length);
		defaultConfigsValues.put(ConfigName.WORKER_CONNECTION_TIMEOUT, "1000");
		defaultConfigsValues.put(ConfigName.WORKER_SOCKET_TIMEOUT, "1000");
		defaultConfigsValues.put(ConfigName.WORKER_CONNECTION_REQUEST_TIMEOUT, "1000");
		defaultConfigsValues.put(ConfigName.SERVER_MAX_INACTIVE_TIME, "10");
	}

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "name", length = 256, unique = true, nullable = false)
	private ConfigName name;

	@Column(name="value", length = 256, nullable = false)
	private String value;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Config that = (Config) obj;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public ConfigName getName() {
		return name;
	}

	public void setName(ConfigName name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
