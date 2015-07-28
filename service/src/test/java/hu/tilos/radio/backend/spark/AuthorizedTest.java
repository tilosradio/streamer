package hu.tilos.radio.backend.spark;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;

public class AuthorizedTest {

    @Test
    public void resolvePath() throws Exception {
        //given
        AuthorizedRoute route = Mockito.mock(AuthorizedRoute.class);
        Authorized authorized = new Authorized("/show/{alias}", route);

        Request req = Mockito.mock(Request.class);
        Mockito.when(req.params("alias")).thenReturn("lichthof");

        //when
        String result = authorized.resolvePath("/show/{alias}", req);


        //then
        Assert.assertEquals("/show/lichthof", result);
    }
}