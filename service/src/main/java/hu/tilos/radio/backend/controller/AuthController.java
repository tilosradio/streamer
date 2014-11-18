package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.ChangePassword;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.data.input.PasswordReset;
import hu.tilos.radio.backend.data.input.RegisterData;
import hu.tilos.radio.backend.data.output.LoginData;
import hu.tilos.radio.backend.data.response.ErrorResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.util.JWTEncoder;
import hu.tilos.radio.backend.util.RecaptchaValidator;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Set;


/**
 * Generate atom feed for the shows.
 */
@Path("/api/v1/auth")
public class AuthController {

    @Inject
    Session session;

    @Inject
    AuthUtil authUtil;

    @Inject
    EmailSender sender;

    @Inject
    private EntityManager entityManager;

    @Inject
    private JWTEncoder jwtEncoder;

    @Inject
    private Validator validator;

    @Inject
    private RecaptchaValidator catpchaValidator;

    @Inject
    private EmailSender emailSender;

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
        User user = (User) entityManager.createNamedQuery("user.byEmail").setParameter("email", passwordReset.getEmail()).getSingleResult();

        ChangePassword changePassword = (ChangePassword) entityManager.createQuery("SELECT cp FROM ChangePassword cp WHERE cp.user = :user AND token = :token").
                setParameter("user", user).
                setParameter("token", passwordReset.getToken()).getSingleResult();

        user.setSalt(authUtil.generateSalt());
        user.setPassword(authUtil.encode(passwordReset.getPassword(), user.getSalt()));
        entityManager.persist(user);

        return Response.ok().entity(new OkResponse("Password has been changed")).build();

    }

    private Response generateToken(PasswordReset passwordReset) {
        User user = (User) entityManager.createNamedQuery("user.byEmail").setParameter("email", passwordReset.getEmail()).getSingleResult();

        //delete old tokens
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<ChangePassword> deleteQuery = cb.createCriteriaDelete(ChangePassword.class);
        Root<ChangePassword> r = deleteQuery.from(ChangePassword.class);
        deleteQuery.where(cb.equal(r.get("user"), user));
        entityManager.createQuery(deleteQuery).executeUpdate();

        //create new token
        ChangePassword password = new ChangePassword();
        password.setUser(user);
        password.setCreated(new Date());
        password.setToken(authUtil.generateSalt());
        entityManager.persist(password);

        //send mail
        sendMail(user, password);

        return Response.ok().entity(new OkResponse("Password reminder has been sent")).build();
    }

    protected void sendMail(User user, ChangePassword password) {
        Email email = new Email();
        email.setFrom("test@tilos.hu");
        email.setTo(user.getEmail());
        email.setSubject("[tilos.hu] Jelszó emlékeztető");
        email.setBody("Valaki jelszóemlékeztetőt kért erre a címre. \n\n A jelszó megváltoztatásához kattints a " +
                "http://tilosadmin/password_reminder?token=" + password.getToken() + "&email=" + user.getEmail() + " címre");
        emailSender.send(email);
    }

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
            if (authUtil.encode(loginData.getPassword(), user.getSalt()).equals(user.getPassword())) {
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
    @Path("/register")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @Transactional
    @POST
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
        newUser.setUsername(registerData.getUsername());
        newUser.setRole(Role.USER);
        newUser.setSalt(authUtil.generateSalt());
        newUser.setPassword(authUtil.encode(registerData.getPassword(), newUser.getSalt()));

        entityManager.persist(newUser);

        return Response.ok(createToken(newUser.getUsername(), newUser.getRole())).build();
    }

    private boolean checkCaptcha(String challenge, String solution) {
        return catpchaValidator.validate("http://tilos.hu", challenge, solution);
    }


}
