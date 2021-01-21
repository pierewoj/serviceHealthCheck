package service.health.check.front.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import service.health.check.front.exception.ResourceNotFoundException;
import service.health.check.front.repository.ConfigRepository;
import service.health.check.models.Config;

@RestController
public class ConfigController {

	@Autowired
	private ConfigRepository configRepository;

	@GetMapping("/config/{configName}")
	public Config getConfig(@PathVariable Config.ConfigName configName) {
		return configRepository.findById(configName)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Config not found with name " + configName));
	}
}
