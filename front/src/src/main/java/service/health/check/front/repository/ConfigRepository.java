package service.health.check.front.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.health.check.models.Config;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Config.ConfigName> {
}
