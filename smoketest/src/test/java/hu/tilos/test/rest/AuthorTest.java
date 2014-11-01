package hu.tilos.test.rest;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

public class AuthorTest extends RestBase {

    @Test
    public void get() {
        RestAssured.get("/v0/author/pero").then().body("name", Matchers.equalTo("Pereszlényi Erika"));
    }
}
