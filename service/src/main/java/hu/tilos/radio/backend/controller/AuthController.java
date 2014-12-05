package hu.tilos.radio.backend.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.data.input.PasswordReset;
import hu.tilos.radio.backend.data.input.RegisterData;
import hu.tilos.radio.backend.data.output.LoginData;
import hu.tilos.radio.backend.data.response.ErrorResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.types.UserDetailed;
import hu.tilos.radio.backend.util.JWTEncoder;
import hu.tilos.radio.backend.util.RecaptchaValidator;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Date;


/**
 * Generate atom feed for the shows.
 */
@Path("/api/v1/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Inject
    Session session;

    @Inject
    AuthUtil authUtil;

    @Inject
    EmailSender sender;

    @Inject
    DB db;

    @Inject
    DozerBeanMapper mapper;

    @Inject
    @Configuration(name = "server.url")
    private String serverUrl;

    @Inject
    private JWTEncoder jwtEncoder;

    @Inject
    private ObjectValidator validator;

    @Inject
    private RecaptchaValidator catpchaValidator;

    @Inject
    private EmailSender emailSender;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @Transactional
    @POST
    @Path("/password_reset")
    public Response passwordReset(PasswordReset passwordReset) {
        if (null == passwordReset.getToken() || "".equals(passwordReset.getToken())) {
            return generateToken(passwordReset);
        } else {
            return changePassword(passwordReset);
        }
    }

    private Response changePassword(PasswordReset passwordReset) {
        validator.validate(passwordReset);
        BasicDBObject query = new BasicDBObject("email", passwordReset.getEmail());
        query.put("passwordChangeToken", passwordReset.getToken());
        DBObject userRaw = db.getCollection("user").findOne(query);
        UserDetailed user = mapper.map(userRaw, UserDetailed.class);
        if (user == null) {
            throw new RuntimeException("Invalid user or token");
        }


        if (new Date().getTime() - user.getPasswordChangeTokenCreated().getTime() > 1000 * 60 * 60) {
            throw new IllegalArgumentException("A jelszóemlékeztetőt egy órán belül fel kell használni. Kérj új jelszóemlékeztetőt");
        }

        //change the password
        String salt = authUtil.generateSalt();
        userRaw.put("salt", salt);
        userRaw.put("password", authUtil.encode(passwordReset.getPassword(), salt));
        //delete existing password change tokens
        userRaw.removeField("passwordChangeToken");
        userRaw.removeField("passwordChangeTokenCreated");
        db.getCollection("user").update(new BasicDBObject("username", user.getUsername()), userRaw);


        return Response.ok().entity(new OkResponse("Password has been changed")).build();

    }

    private Response generateToken(PasswordReset passwordReset) {


        DBObject user = db.getCollection("user").findOne(new BasicDBObject("email", passwordReset.getEmail()));


        //create new token

        String token = authUtil.generateSalt();
        user.put("passwordChangeTokenCreated", new Date());
        user.put("passwordChangeToken", token);
        db.getCollection("user").update(new BasicDBObject("username", user.get("username")), user);

        //send mail
        sendMail(user, token);

        return Response.ok().entity(new OkResponse("Password reminder has been sent")).build();
    }

    protected void sendMail(DBObject user, String token) {
        Email email = new Email();
        email.setFrom("webmester@tilos.hu");
        email.setTo((String) user.get("email"));
        email.setSubject("[tilos.hu] Jelszo emlekezteto");
        email.setBody(createBody(user, token));
        emailSender.send(email);
    }

    private String createBody(DBObject user, String token) {
        String body = "Valaki jelszoemlekeztetot kert erre a cimre. \n\n A jelszo megvaltoztatasahoz kattints a  " +
                serverUrl + "/password_reset?token=" + token +
                "&email=" + ((String) user.get("email")).replaceAll("@", "%40") + " cimre";
        LOG.debug("Creating mail: " + body);
        return body;
    }

    /**
     * @exclude
     */
    @Path("/login")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @POST
    public Response login(LoginData loginData) {

        try {
            DBObject user = db.getCollection("user").findOne(new BasicDBObject("username", loginData.getUsername()));
            if (user == null) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            if (authUtil.encode(loginData.getPassword(), (String) user.get("salt")).equals((String) user.get("password"))) {
                try {
                    return Response.ok(createToken(loginData.getUsername(), Role.values()[(int) user.get("role_id")])).build();
                } catch (Exception e) {
                    throw new RuntimeException("Can't encode the token", e);
                }
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } catch (NoResultException ex) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private String createToken(String username, Role role) {
        Token jwtToken = new Token();
        jwtToken.setUsername(username);
        jwtToken.setRole(role);
        return jwtEncoder.encode(jwtToken);
    }


    /**
     * @exclude
     */
    @Path("/register")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @Transactional
    @POST
    public Response register(RegisterData registerData) {
        if (!checkCaptcha(registerData.getCaptchaChallenge(), registerData.getCaptchaResponse())) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("A captcha megadása hibás")).build();
        }

        validator.validate(registerData);

        DBObject existingUser = db.getCollection("user").findOne(new BasicDBObject("username", registerData.getUsername()));
        if (existingUser != null) {
            throw new IllegalArgumentException("A felhasznalonev mar foglalat");

        }
        //everything is ok
        DBObject user = new BasicDBObject();
        user.put("email", registerData.getEmail());
        user.put("username", registerData.getUsername());
        user.put("role_id", Role.USER.ordinal());
        user.put("salt", authUtil.generateSalt());
        user.put("password", authUtil.encode(registerData.getPassword(), (String) user.get("salt")));

        db.getCollection("user").insert(user);

        return Response.ok(createToken(registerData.getUsername(), Role.USER)).build();
    }

    private boolean checkCaptcha(String challenge, String solution) {
        return catpchaValidator.validate("http://tilos.hu", challenge, solution);
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
