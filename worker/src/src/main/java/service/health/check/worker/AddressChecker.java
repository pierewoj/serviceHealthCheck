package service.health.check.worker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.apache.http.HttpStatus.SC_OK;
import service.health.check.messages.AddressToCheck;
import service.health.check.messages.CheckedAddress;
import static service.health.check.worker.util.AddressCheckerUtil.extractURI;
import static service.health.check.worker.util.AddressCheckerUtil.getRequestConfig;
import static service.health.check.worker.util.AddressCheckerUtil.instantiateHttpClient;

@Slf4j
@RequiredArgsConstructor
public class AddressChecker {

    private final RestTemplate restTemplate;

    public CheckedAddress checkAddress(AddressToCheck addressToCheck) {
        return new CheckedAddress(addressToCheck.getHost(), addressToCheck.getPort(),
                                  isAddressHealthy(addressToCheck));
    }

    private boolean isAddressHealthy(AddressToCheck address) {
        HttpClient httpClient = instantiateHttpClient();
        try {
            URI uri = extractURI(address);
            HttpHead httpHead = new HttpHead(uri);
            httpHead.setConfig(getRequestConfig(restTemplate));
            HttpResponse execute = httpClient.execute(httpHead);
            int statusCode = execute.getStatusLine().getStatusCode();
            log.debug(String.valueOf(statusCode));
            return statusCode == SC_OK;
        } catch (IOException | URISyntaxException e) {
            log.warn(e.getMessage(), e);
        }
        return false;
    }
}
