package org.example.url_shortner.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.url_shortner.exception.InvalidRequestException;
import org.example.url_shortner.models.ErrorMessage;
import org.example.url_shortner.service.UrlShortnerService;
import org.example.url_shortner.util.Utility;

import static org.example.url_shortner.constants.Constants.HTTP_PORT;
import static org.example.url_shortner.constants.Constants.URL;
@Slf4j
public class RouterVerticle extends AbstractVerticle {

    private UrlShortnerService urlShortnerService;
    @Override
    public void start(Promise<Void> startPromise) {
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig().onSuccess(result -> {
            urlShortnerService = new UrlShortnerService(vertx, result);
            Router router = Router.router(vertx);
            router.route().handler(CorsHandler.create(".*."));
            router.get("/api/v1/data/shorturl")
                    .handler(this::createShortUrlHandler);
            router.get("/api/v1/:urlhashcode")
                    .handler(this::getUrlHandler);
            vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(result.getInteger(HTTP_PORT))
                    .onSuccess(rs -> {
                        log.info("Server started on port {}", result.getInteger(HTTP_PORT));
                    })
                    .onFailure(error -> {
                        log.error("Server failed to start.", error);
                    });
            startPromise.complete();
        }).onFailure(startPromise::fail);

    }

    private void createShortUrlHandler(RoutingContext routingContext) {
        try {
            var url = routingContext.queryParams().get(URL);
            urlShortnerService.createHashCode(url).onSuccess(hashCode -> {
                var shortUrl = Utility.getShortUrlFromHashCode(hashCode);
                log.info("logType=tracking | description=\"short url created successfully.\" | shorturl={}", shortUrl);
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("content-type", "application/json")
                        .end(new JsonObject().put("short_url", shortUrl).toBuffer());
            }).onFailure(error -> {
                this.handleErrorMessage(error, routingContext);
            });
        } catch (Exception e) {
           this.handleErrorMessage(e, routingContext);
        }
    }

    private void getUrlHandler(RoutingContext routingContext) {
            var hashcode = routingContext.pathParam("urlhashcode");
            urlShortnerService.getRedirectionUrl(hashcode)
                    .onSuccess(redirectedUrl -> {
                        log.info("logType=tracking | description=\"Successfully fetched mapped url for {} hashcode.\" | url={}", hashcode, redirectedUrl);
                        routingContext.response()
                                .putHeader("location", redirectedUrl)
                                .setStatusCode(302)
                                .end();
                    }). onFailure(error -> {
                        this.handleErrorMessage(error, routingContext);
                    });

    }

    private void handleErrorMessage(Throwable e, RoutingContext routingContext) {
        int statusCode = 500;
        if (e instanceof InvalidRequestException) {
            statusCode = 400;
        }
        log.error("logType=error | errorMessage={}", e.getMessage(), e);
        ErrorMessage errorMessage = new ErrorMessage(statusCode, e.getMessage());
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json")
                .end(errorMessage.toJsonObject().toBuffer());
    }
}
