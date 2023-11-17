package org.example.url_shortner.service;

import io.netty.util.internal.StringUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.example.url_shortner.exception.InvalidRequestException;
import org.example.url_shortner.exception.NotFoundException;
import org.example.url_shortner.models.UrlInfo;
import org.example.url_shortner.util.Utility;

import java.util.UUID;
import java.util.regex.Pattern;

import static org.example.url_shortner.constants.Constants.*;
@Slf4j
public class UrlShortnerService {
    String URL_REGEX = "((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)";
    private final StorageService storageService;
    private JsonObject appProperties;

    public UrlShortnerService(Vertx vertx, JsonObject appProperties) {
        this.appProperties = appProperties;
        this.storageService = new StorageService(vertx, appProperties);
    }

    public Future<String> getRedirectionUrl(String hashCode) {
        Promise<String> promise = Promise.promise();
        String query = Utility.getSelectQueryForUrlSearch(hashCode, appProperties.getString(TABLE_NAME));
        storageService.executeFetchQuery(query)
                .onSuccess(result -> {
                    if (result != null && result.getJsonArray(ENTRIES) != null && result.getJsonArray(ENTRIES).size() > 0) {
                        UrlInfo urlInfo = result.getJsonArray(ENTRIES).getJsonObject(0).mapTo(UrlInfo.class);
                        if (urlInfo.getUrl() != null) {
                            promise.complete(urlInfo.getUrl());
                            return;
                        }
                    }
                    promise.fail(new InvalidRequestException("Redirection url doesn't exists for given short url."));
                }).onFailure(error -> {
                    log.error("logType=error | exceptionClass={} | method=getRedirectionUrl | errorMessage={}", error.getClass().getName(), error.getMessage());
                    promise.fail(error);
                });
        return promise.future();
    }

    public Future<String> createHashCode(String url) {
        Promise<String> promise = Promise.promise();
        if (!isUrlValid(url)) {
            promise.fail(new InvalidRequestException("Invalid url passed in request."));
            return promise.future();
        }
        var hashCode = Utility.getEncodedString(url);
        /**
         * there is a possibility that we might get same encoded value for different url
         * in that case we will append url with uuid and then generate hashcode again
         */
        storageService.entryExist(HASH_CODE, hashCode)
                .compose(rs -> {
                    Promise<String> pr = Promise.promise();
                    var newUrl = url + UUID.randomUUID();
                    var newHashCode = Utility.getEncodedString(newUrl);
                    insertIntoDB(url, newHashCode, pr);
                    return pr.future();
                })
                .onSuccess(promise::complete)
                .onFailure(error -> {
                    if (error instanceof NotFoundException) {
                        insertIntoDB(url, hashCode, promise);
                    } else {
                        log.error("logType=error | exceptionClass={} | method=createHashCode | errorMessage={}", error.getClass().getName(), error.getMessage());
                        promise.fail(error);
                    }
                });
        return promise.future();
    }

    private void insertIntoDB(String url, String hashCode, Promise<String> promise) {
        var query = Utility.getInsertQueryForUrlEntry(url, hashCode, appProperties.getString(TABLE_NAME));
        storageService.executeInsertQuery(query)
                .onSuccess(r -> {
                    log.info("logType=tracking | description=\"Successfully added entry in db\"");
                    promise.complete(hashCode);
                })
                .onFailure(promise::fail);
    }


    private boolean isUrlValid(String url) {
        if (StringUtil.isNullOrEmpty(url)) {
            return false;
        }
        Pattern pattern = Pattern.compile(URL_REGEX);
        return pattern.matcher(url).matches();
    }

}
