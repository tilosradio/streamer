package hu.tilos.test.rest;

import com.jayway.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ShowTest extends RestBase {

    @Test
    public void get() {
        RestAssured.get("/v1/show/3-utas").then().body("name", Matchers.equalTo("3. utas"));
    }


}
