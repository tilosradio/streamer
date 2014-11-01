package hu.tilos.radio.backend;

import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.converters.MappingFactory;
import org.junit.Assert;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;


@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class UserControllerTest {

    @Inject
    EntityManager entityManager;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void testRelation() {
        //given

        //when
        User user = entityManager.find(User.class,1);

        //then
        Assert.assertNotNull(user.getRole());

    }
}