package webclientblock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(value = SpringRunner.class)
@TestPropertySource(properties = { "logging.level.root=WARN" })
public class WebClientBlockTest {
    @LocalServerPort
    private int port;

    private int loops = 25_000;

    @Test
    @Ignore("this works fine")
    public void testLocalServiceGet() {
        WebClient client = WebClient.create();

        for (int i = 0; i < loops; i++) {
            Mono<Map> answer = client.get().uri("http://localhost:" + port + "/hello").retrieve().bodyToMono(Map.class);

            assertEquals(0, answer.block(Duration.of(1, ChronoUnit.SECONDS)).size());
        }
    }

    @Test
    public void testWiremockServiceGetJsonDecode() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();
        long failures = 0;
        try {
            WireMock.reset();

            stubFor(get(urlPathEqualTo("/hello")) //
                    .willReturn(okJson("{}")));

            WebClient client = WebClient.create();

            for (int i = 0; i < loops; i++) {
                Mono<Map> answer = client.get().uri("http://localhost:" + 8080 + "/hello").retrieve()
                        .bodyToMono(Map.class);

                try {
                    answer.block(Duration.of(1, ChronoUnit.SECONDS));
                } catch (RuntimeException re) {
                    System.out.println(
                            "encountered a failure at loop count " + i + " " + re.getClass() + " " + re.getMessage());
                    failures++;
                }
            }
        } finally {
            wireMockServer.stop();
        }
        assertEquals(0, failures);
    }

    @Test
    public void testWiremockServiceGetJsonDecodeRestTemplate() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();
        try {
            WireMock.reset();

            stubFor(get(urlPathEqualTo("/hello")) //
                    .willReturn(okJson("{}")));

            RestTemplate restTemplate = new RestTemplate();

            for (int i = 0; i < loops; i++) {
                Map answer = restTemplate.getForObject("http://localhost:" + 8080 + "/hello", Map.class);

                assertEquals(0, answer.size());
            }
        } finally {
            wireMockServer.stop();
        }
    }

    @Test
    public void testBlockWiremockServiceGetString() throws Exception {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();
        try {

            WireMock.reset();

            stubFor(get(urlPathEqualTo("/hello")) //
                    .willReturn(okJson("{}")));

            WebClient client = WebClient.create();

            for (int i = 0; i < loops; i++) {
                Mono<String> answer = client.get().uri("http://localhost:" + 8080 + "/hello").retrieve()
                        .bodyToMono(String.class);

                assertEquals("{}", answer.block(Duration.of(1, ChronoUnit.SECONDS)));
            }
        } finally {
            wireMockServer.stop();
        }
    }

    @Test
    @Ignore("test reading an empty json document from a local webserver - works fine")
    public void testLocalNginxGet() {
        WebClient client = WebClient.create();

        for (int i = 0; i < loops; i++) {
            Mono<Map> answer = client.get().uri("http://localhost/empty.json").retrieve().bodyToMono(Map.class);

            assertEquals(0, answer.block(Duration.of(1, ChronoUnit.SECONDS)).size());
        }
    }
}
