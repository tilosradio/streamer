package hu.tilos.test.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class EpisodeTest extends RestBase {

    @Test
    public void list() throws ParseException {
        Date start = DateFormatUtil.YYYY_MM_DD_HHMM.parse("2015-07-15 80:00");
        Date end = DateFormatUtil.YYYY_MM_DD_HHMM.parse("2015-07-15 10:00");
        ValidatableResponse result = RestAssured.get("/v1/episode?start=" + start.getTime() + "&end=" + end.getTime()).then();
        result.statusCode(Matchers.equalTo(200));
        System.out.println(result);

    }


}
