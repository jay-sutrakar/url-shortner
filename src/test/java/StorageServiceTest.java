import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.example.url_shortner.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class StorageServiceTest {
//    private StorageService service;
//    @BeforeEach
//    void initialSetup(Vertx vertx, VertxTestContext testContext) {
//        service = new StorageService(vertx, new JsonObject());
//        testContext.completeNow();
//    }
//    @Test
//    void insertData(Vertx vertx, VertxTestContext testContext) {
//        String query = "SELECT * FROM test;";
//        service.executeFetchQuery(query).onSuccess(result -> {
//            System.out.println(result);
//            testContext.completeNow();
//        }).onFailure( error -> {
//            System.out.println(error.getMessage());
//            testContext.failNow(error);
//        });
//    }
}
