package org.example.url_shortner.service;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
import lombok.extern.slf4j.Slf4j;
import org.example.url_shortner.exception.NotFoundException;

import java.util.ArrayList;

import static org.example.url_shortner.constants.Constants.*;
@Slf4j
public class StorageService {
    private PgPool pool;

    public StorageService(Vertx vertx, JsonObject appProperties) {
        var connectOptions = new PgConnectOptions()
                .setPassword(appProperties.getString(POSTGRES_PASSWORD))
                .setUser(appProperties.getString(POSTGRES_USERNAME))
                .setDatabase(appProperties.getString(POSTGRES_DB))
                .setHost(appProperties.getString(POSTGRES_HOST))
                .setPort(appProperties.getInteger(POSTGRES_PORT));
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        pool = PgPool.pool(vertx, connectOptions, poolOptions);
    }

    public Future<Void> executeInsertQuery(String query) {
        Promise<Void> promise = Promise.promise();
        pool.getConnection()
                .compose(connection -> connection.query(query).execute())
                .onSuccess(result -> promise.complete())
                .onFailure(promise::fail);
        return promise.future();
    }

    public Future<JsonObject> executeFetchQuery(String query) {
        return pool.getConnection()
                .compose(connection -> connection.query(query).execute())
                .compose(this::transform);
    }

    private Future<JsonObject> transform(RowSet<Row> rows) {
        try {
            var jsonObject = new JsonObject();
            var entries = new ArrayList<>();
            rows.forEach(entry -> entries.add(entry.toJson()));
            jsonObject.put(ENTRIES, entries);
            return Future.succeededFuture(jsonObject);
        } catch (Exception e) {
            log.error("logTyp=error | method=transform | errorMessage={}", e.getMessage());
            return Future.failedFuture(e);
        }
    }

    public Future<Void> entryExist(String key, String value) {
        Promise<Void> pr = Promise.promise();
        var query = String.format("select exists(select 1 from contact where %s='%s')", key, value);
        pool.getConnection()
                .compose(conn -> conn.query(query).execute())
                .onSuccess(rs -> pr.complete())
                .onFailure(error -> pr.fail(new NotFoundException("Unable to find document.")));
        return pr.future();
    }
}
