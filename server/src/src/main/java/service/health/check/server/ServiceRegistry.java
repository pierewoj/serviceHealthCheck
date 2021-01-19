package service.health.check.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.models.Server;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class ServiceRegistry {
    private static final Server currentServer = createCurrentServer();

    private final Database db;

    public void sendHeartbeat() {
        currentServer.setLastHeartbeat(Instant.now());
        db.saveServer(currentServer);
    }

    public List<Server> getActiveServers() {
        Instant maxServerAge = Instant.now().minusSeconds(Config.ACTIVE_SERVER_TIMEOUT_SECONDS);
        List<Server> servers = db.getAll(Server.class);
        log.info("Got {} servers from the db", servers.size());
        return servers.stream()
                .filter(s -> s.getLastHeartbeat().isAfter(maxServerAge))
                .collect(Collectors.toList());
    }

    public String getCurrentServerId() {
        return currentServer.getId();
    }

    private static Server createCurrentServer() {
        // Each server should get a different ID; if a duplicate occurs (which is extremely unlikely)
        // both servers will be doing the same work. This will result in some of the targets getting
        // checked twice as often until one of the servers is restarted.
        Server server = new Server();
        server.setId(UUID.randomUUID().toString());
        return server;
    }
}
