import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.example.url_shortner.verticles.RouterVerticle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class RouterVerticleTest {

    @BeforeEach
    void startServer(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new RouterVerticle()).onSuccess(rs -> {
            testContext.succeedingThenComplete();
        }).onFailure(testContext::failNow);
    }

    @DisplayName("the get endpoint should  redirect the request mapped url")
    @Test
    void testcase1(final Vertx vertx, VertxTestContext testContext) {
        String tinyUrl = "http://localhost:8080/testurl1";
        WebClient client = WebClient.create(vertx);
        client.get(8080, "localhost", "/testurl")
                .send()
                .onSuccess(httpResponse -> {
                    //TODO need to write a logic to validate redirected request response
                    testContext.completeNow();
                })
                .onFailure(testContext::failNow);
    }

    @DisplayName("the get endpoint should return the tiny url")
    @Test
    void testcase2(Vertx vertx, VertxTestContext testContext) {
        String url = "https://www.google.com/";
        WebClient client = WebClient.create(vertx);
        client.get(8080, "localhost", "/shorturl")
                .addQueryParam("url", url)
                .send()
                .onSuccess(httpResponse -> {
                    testContext.verify(() -> {
                        Assertions.assertEquals(200, httpResponse.statusCode());
                        Assertions.assertNotNull(httpResponse.bodyAsJsonObject().getString("short_url"));
                        testContext.completeNow();
                    });
                });
    }

}
