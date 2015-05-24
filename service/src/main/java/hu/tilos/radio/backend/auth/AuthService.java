package hu.tilos.radio.backend.auth;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.Email;
import hu.tilos.radio.backend.EmailSender;
import hu.tilos.radio.backend.ObjectValidator;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.data.error.AccessDeniedException;
import hu.tilos.radio.backend.data.error.InternalErrorException;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.types.UserDetailed;
import hu.tilos.radio.backend.util.JWTEncoder;
import hu.tilos.radio.backend.util.RecaptchaValidator;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

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


    public OkResponse passwordReset(PasswordReset passwordReset) {
        if (null == passwordReset.getToken() || "".equals(passwordReset.getToken())) {
            return generateToken(passwordReset);
        } else {
            return changePassword(passwordReset);
        }
    }

    private OkResponse changePassword(PasswordReset passwordReset) {
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


        return new OkResponse("Password has been changed");

    }

    private OkResponse generateToken(PasswordReset passwordReset) {

        DBObject user = db.getCollection("user").findOne(new BasicDBObject("email", passwordReset.getEmail()));
        if (user == null) {
            user = createUserAtFirstTime(passwordReset.getEmail());
        }


        //create new token
        String token = authUtil.generateSalt();
        user.put("passwordChangeTokenCreated", new Date());
        user.put("passwordChangeToken", token);
        db.getCollection("user").update(new BasicDBObject("username", user.get("username")), user);

        try {
            //send mail
            sendMail(user, token);
        } catch (Exception ex) {
            throw new InternalErrorException("Nem sikerült az emailt kiküldeni a levelező szerveren keresztül.", ex);
        }

        return new OkResponse("A jelszóemlékeztetőt kiküldtük a megadott e-mail címre.");
    }

    private DBObject createUserAtFirstTime(String email) {
        DBObject author = db.getCollection("author").findOne(new BasicDBObject("email", email));
        if (author == null) {
            throw new NotFoundException("Se felhasználó, se műsorkészítő nincs ilyen e-mail címmel.");
        }
        DBObject user = new BasicDBObject();
        user.put("username", author.get("alias"));
        user.put("role", Role.AUTHOR.ordinal());
        user.put("role_id", Role.AUTHOR.ordinal());
        user.put("email", author.get("email"));
        user.put("salt", null);
        user.put("password", null);
        user.put("author", new DBRef(db, "author", author.get("_id")));
        db.getCollection("user").insert(user);
        return user;
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
                "&email=" + ((String) user.get("email")).replaceAll("@", "%40") + " cimre.\n\n(Felhasznallo: " + user.get("username") + ")";
        LOG.debug("Creating mail: " + body);
        return body;
    }


    public Map<String, String> login(LoginData loginData) {
        String username = loginData.getUsername();
        String sudo = "";
        if (username.contains(":")) {
            String[] parts = username.split(":");
            username = parts[0];
            sudo = parts[1];
        }

        DBObject user = db.getCollection("user").findOne(new BasicDBObject("username", username));
        if (user == null) {
            throw new AccessDeniedException("Access denied");
        }
        if (authUtil.encode(loginData.getPassword(), (String) user.get("salt")).equals((String) user.get("password"))) {
            try {
                if (!"".equals(sudo) && user.get("role_id").equals(Role.ADMIN.ordinal())) {
                    user = db.getCollection("user").findOne(new BasicDBObject("username", sudo));
                    if (user == null) {
                        throw new NotFoundException("User is missing");
                    }
                }
                Map<String, String> result = new HashMap<>();
                result.put("access_token", createToken((String) user.get("username"), Role.values()[(int) user.get("role_id")]));
                return result;

            } catch (Exception e) {
                throw new RuntimeException("Can't encode the token", e);
            }
        } else {
            throw new AccessDeniedException("Access denied");
        }

    }

    private String createToken(String username, Role role) {
        Token jwtToken = new Token();
        jwtToken.setUsername(username);
        jwtToken.setRole(role);
        return jwtEncoder.encode(jwtToken);
    }


    public String register(RegisterData registerData) {
        if (!checkCaptcha(registerData.getCaptchaChallenge(), registerData.getCaptchaResponse())) {
            throw new AccessDeniedException("A captcha megadása hibás");
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
        user.put("role", Role.USER.ordinal());
        user.put("salt", authUtil.generateSalt());
        user.put("password", authUtil.encode(registerData.getPassword(), (String) user.get("salt")));

        db.getCollection("user").insert(user);

        return createToken(registerData.getUsername(), Role.USER);
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
