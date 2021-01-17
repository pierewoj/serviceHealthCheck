package service.health.check.models;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name="servers")
public class Server implements Serializable {

	private static final long serialVersionUID = -6789189734956800614L;

	@Id
	@Column(name="id", length = 256, unique = true, nullable = false)
	private String id;

	@Column(name = "last_heartbeat", nullable = false)
	private Instant lastHeartbeat;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Server that = (Server) obj;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Instant getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void setLastHeartbeat(Instant host) {
		this.lastHeartbeat = host;
	}
}
