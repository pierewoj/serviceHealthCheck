package service.health.check.front.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import service.health.check.front.repository.ConfigRepository;
import service.health.check.models.Config;

import java.util.Optional;

@Component
public class ConfigInitBean {

	@Autowired
	private ConfigRepository configRepository;

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		for(Config.ConfigName configName : Config.ConfigName.values()) {
			Optional<Config> config = configRepository.findById(configName);
			if (!config.isPresent()) {
				Config defaultConfig = new Config();
				defaultConfig.setName(configName);
				defaultConfig.setValue(Config.defaultConfigsValues.get(configName));
				configRepository.save(defaultConfig);
			}
		}
	}
}
