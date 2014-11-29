package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.MongoProducer;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.UserInfo;
import hu.tilos.radio.backend.data.types.UserDetailed;
import org.dozer.DozerBeanMapper;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class})
@ActivatedAlternatives(FongoCreator.class)
public class UserControllerTest {

    @Inject
    FongoRule fongoRule;

    @Inject
    Session session;

    @Inject
    UserController controller;

    @Inject
    DozerBeanMapper mapper;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    @InRequestScope
    public void me() {
        //given
        String authorId = loadTo(fongoRule, "author", "author-author1.json");
        loadTo(fongoRule, "user", "user-1.json", authorId);
        session.setCurrentUser(mapper.map(fongoRule.getDB().getCollection("user").findOne(),UserDetailed.class));


        //when
        UserInfo me = controller.me();

        //then
        assertThat(me.getUsername(), equalTo("bela"));

    }


}