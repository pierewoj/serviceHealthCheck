package service.health.check.worker.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import lombok.extern.slf4j.Slf4j;
import service.health.check.messages.AddressToCheck;

@Slf4j
public class AddressCheckerUtil {

    // constants
    public static final int TIMEOUT = 1000;

    public AddressCheckerUtil() {
        throw new UnsupportedOperationException("Cannot instantiate Util class!");
    }

    public static RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                            .setConnectTimeout(TIMEOUT)
                            .setSocketTimeout(TIMEOUT)
                            .setConnectionRequestTimeout(TIMEOUT)
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
            log.warn("Port isn't int: " + value, e);
            return null;
        }
    }
}
