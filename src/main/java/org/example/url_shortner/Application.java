package org.example.url_shortner;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import org.example.url_shortner.verticles.RouterVerticle;

public class Application extends Launcher {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(3);
        vertx.deployVerticle("org.example.url_shortner.verticles.RouterVerticle", deploymentOptions);
    }
}
