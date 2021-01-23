package service.health.check.worker.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.messages.AddressToCheck;

import org.springframework.web.client.RestTemplate;
import service.health.check.models.Config;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressCheckerUtil {

	// constants
	public static final int TIMEOUT = 1000;

	public static final String REST_CONFIG_URL = "http://front:8090/config/";

	public static RequestConfig getRequestConfig(RestTemplate restTemplate) {
		return RequestConfig.custom()
				.setConnectTimeout(Integer.parseInt(Objects.requireNonNull(
						restTemplate.getForObject(
								REST_CONFIG_URL +
										Config.ConfigName.WORKER_CONNECTION_TIMEOUT,
								Config.class)).getValue()))
				.setSocketTimeout(Integer.parseInt(Objects.requireNonNull(
						restTemplate.getForObject(
								REST_CONFIG_URL +
										Config.ConfigName.WORKER_SOCKET_TIMEOUT,
								Config.class)).getValue()))
				.setConnectionRequestTimeout(
						Integer.parseInt(Objects.requireNonNull(
								restTemplate.getForObject(
										REST_CONFIG_URL +
												Config.ConfigName.WORKER_CONNECTION_REQUEST_TIMEOUT,
										Config.class))
								.getValue()))
				.build();
	}

    public static HttpClient instantiateHttpClient() {
        return HttpClients.createDefault();
    }

    public static URI extractURI(AddressToCheck address) throws URISyntaxException {
        URI uri = new URI(address.getHost());
        Integer port = parsePort(address.getPort());
        if (port == null) {
            return uri;
        }
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(),
                       uri.getQuery(), uri.getFragment());
    }

    private static Integer parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            log.warn(String.format("Port isn't int: '%s'", value), e);
            return null;
        }
    }
}
