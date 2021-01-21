package service.health.check.worker;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import service.health.check.messages.AddressToCheck;
import service.health.check.messages.CheckedAddress;
import service.health.check.worker.util.AddressCheckerUtil;

public class AddressCheckerTest {

    // constants
    private static final Integer PORT = 8089;
    private static final String PORT_TEXT = PORT.toString();
    private static final String HOST = "http://localhost";

    // dependencies
    private AddressChecker addressChecker;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setup() {
        this.addressChecker = new AddressChecker();
    }

    @Test
    public void should_return_healthy_for_ok_response() {
        // given
        String path = "/request/ok";
        String testUrl = HOST + path;
        stubFor(head(urlEqualTo(path)).willReturn(aResponse().withStatus(SC_OK)));
        AddressToCheck addressToCheck = new AddressToCheck(testUrl, PORT_TEXT);

        // when
        CheckedAddress checkedAddress = addressChecker.checkAddress(addressToCheck);

        // then
        assertThat(checkedAddress.getHost()).isEqualTo(testUrl);
        assertThat(checkedAddress.getPort()).isEqualTo(PORT_TEXT);
        assertThat(checkedAddress.getHealthy()).isTrue();
    }

    @Test
    public void should_return_unhealthy_for_server_error() {
        // given
        String path = "/request/bad";
        String testUrl = HOST + path;
        stubFor(head(urlEqualTo(path)).willReturn(aResponse().withStatus(SC_INTERNAL_SERVER_ERROR)));
        AddressToCheck addressToCheck = new AddressToCheck(testUrl, PORT_TEXT);

        // when
        CheckedAddress checkedAddress = addressChecker.checkAddress(addressToCheck);

        // then
        assertThat(checkedAddress.getHost()).isEqualTo(testUrl);
        assertThat(checkedAddress.getPort()).isEqualTo(PORT_TEXT);
        assertThat(checkedAddress.getHealthy()).isFalse();
    }

    @Test
    public void should_return_unhealthy_for_timeout() {
        // given
        String path = "/request/timeout";
        String testUrl = HOST + path;
        stubFor(head(urlEqualTo(path))
                        .willReturn(aResponse().withStatus(SC_OK).withFixedDelay(3 * AddressCheckerUtil.TIMEOUT)));
        AddressToCheck addressToCheck = new AddressToCheck(testUrl, PORT_TEXT);

        // when
        CheckedAddress checkedAddress = addressChecker.checkAddress(addressToCheck);

        // then
        assertThat(checkedAddress.getHost()).isEqualTo(testUrl);
        assertThat(checkedAddress.getPort()).isEqualTo(PORT_TEXT);
        assertThat(checkedAddress.getHealthy()).isFalse();
    }
}