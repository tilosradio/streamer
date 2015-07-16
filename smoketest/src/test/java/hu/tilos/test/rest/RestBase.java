package hu.tilos.test.rest;

import com.jayway.restassured.RestAssured;
import org.junit.BeforeClass;

public class RestBase {

    @BeforeClass
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "https://tilos.hu/api/";
        RestAssured.port = 443;
    }

}
