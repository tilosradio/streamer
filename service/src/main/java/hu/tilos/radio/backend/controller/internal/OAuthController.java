package hu.tilos.radio.backend.controller.internal;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.util.JWTEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


@Path("/api/int/oauth")
public class OAuthController {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthController.class);

    @Inject
    Session session;

    @Inject
    DB db;

    @Inject
    private JWTEncoder jwtEncoder;

    @Inject
    @Configuration(name = "facebook.key")
    private String clientId;

    @Inject
    @Configuration(name = "facebook.secret")
    private String clientKey;

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @Transactional
    @POST
    @Path("/facebook")
    public Response facebook(FacebookRequest request) {
        try {

            String accessToken = getAccessToken(request.code);

            DBObject userResponse = saveOrGetUser(accessToken);

            Map result = createToken(userResponse);

            return Response.ok(result).build();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Map createToken(DBObject userResponse) {
        Map result = new HashMap<>();
        Token jwtToken = new Token();
        jwtToken.setUsername((String) userResponse.get("username"));
        jwtToken.setRole(Role.USER);
        result.put("token", jwtEncoder.encode(jwtToken));
        return result;
    }

    private DBObject saveOrGetUser(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);

        User me = facebookClient.fetchObject("me", User.class);

        DBObject userResponse = db.getCollection("user").findOne(new BasicDBObject("facebook", me.getId()));

        if (userResponse == null) {
            BasicDBObject newUser = new BasicDBObject()
                    .append("facebook", me.getId())
                    .append("username", me.getEmail())
                    .append("email", me.getEmail())
                    .append("role_id", Role.USER.ordinal());
            db.getCollection("user").insert(newUser);
            userResponse = db.getCollection("user").findOne(new BasicDBObject("facebook", me.getId()));
        }
        return userResponse;
    }

    private String getAccessToken(String code) throws IOException {
        //change to an access token
        String url = String.format("https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s", clientId, clientKey, code, "http://tiloslocal.hu/");
        URLConnection uc = new URL(url).openConnection();
        String response = new Scanner(uc.getInputStream()).useDelimiter("\\Z").next();

        String[] parts = response.split("&");
        return parts[0].split("=")[1];
    }

    public static class FacebookRequest {

        private String code;

        private String clientId;

        private String redirectUri;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }


}