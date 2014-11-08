package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.data.ErrorResponse;
import hu.tilos.radio.backend.data.LoginData;
import hu.tilos.radio.backend.data.RegisterData;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.util.JWTEncoder;
import hu.tilos.radio.backend.util.RecaptchaValidator;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;


/**
 * Generate atom feed for the shows.
 */
@Path("/api/v1/auth")
public class AuthController {


    @Inject
    private EntityManager entityManager;

    @Inject
    private JWTEncoder jwtEncoder;

    @Inject
    private Validator validator;

    @Inject
    RecaptchaValidator catpchaValidator;

    /**
     * @exclude
     */
    @Path("/login")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @POST
    public Response login(LoginData loginData) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username=:username");
        query.setParameter("username", loginData.getUsername());
        try {
            User user = (User) query.getSingleResult();
            if (toSHA1(loginData.getPassword() + user.getSalt()).equals(user.getPassword())) {
                try {
                    return Response.ok(createToken(loginData.getUsername(), user.getRole())).build();
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
//    @Path("/register")
//    @Produces("application/json")
//    @Security(role = Role.GUEST)
//    @Transactional
//    @POST
    public Response register(RegisterData registerData) {
        if (!checkCaptcha(registerData.getCaptchaChallenge(), registerData.getCaptchaResponse())) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("A captcha megadása hibás")).build();
        }

        Set<ConstraintViolation<RegisterData>> validationErrors = validator.validate(registerData);
        if (validationErrors.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<RegisterData> validationError : validationErrors) {
                builder.append(validationError.getPropertyPath() + " " + validationError.getMessage() + "\n");
            }
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("Hibás felhasználói adatok: " + builder.toString())).build();
        }
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username=:username");
        query.setParameter("username", registerData.getUsername());

        try {
            query.getSingleResult();
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("A felhasználónév már foglalt")).build();
        } catch (NoResultException ex) {
            //NOOP
        }

        //everything is ok
        User newUser = new User();
        newUser.setEmail(registerData.getEmail());
        newUser.setPassword(registerData.getPassword());
        newUser.setUsername(registerData.getUsername());
        newUser.setRole(Role.USER);
        newUser.setSalt(generateSalt());

        entityManager.persist(newUser);

        return Response.ok(createToken(newUser.getUsername(), newUser.getRole())).build();
    }

    private boolean checkCaptcha(String challenge, String solution) {
        return catpchaValidator.validate("http://tilos.hu", challenge, solution);
    }

    public String generateSalt() {
        return null;
    }

    public static String toSHA1(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return byteArrayToHexString(md.digest(data.getBytes()));
    }


    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}
