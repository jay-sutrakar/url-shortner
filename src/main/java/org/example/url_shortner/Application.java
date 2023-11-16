package org.example.url_shortner;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import org.example.url_shortner.verticles.RouterVerticle;

public class Application extends Launcher {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RouterVerticle());
    }
}
