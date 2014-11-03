package hu.tilos.radio.backend;

import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.UserInfo;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class UserControllerTest {

    @Inject
    EntityManager entityManager;

    @Inject
    Session session;

    @Inject
    UserController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    @InRequestScope
    public void me() {
        //given
        User user = entityManager.find(User.class, 1);
        entityManager.detach(user);
        session.setCurrentUser(user);

        //when
        UserInfo me = controller.me();

        //then
        assertThat(me.getUsername(), equalTo("bela"));

    }


}