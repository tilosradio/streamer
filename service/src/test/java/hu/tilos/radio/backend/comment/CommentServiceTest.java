package hu.tilos.radio.backend.comment;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.user.UserInfo;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommentServiceTest {

    CommentService controller;


    FongoRule fongoRule = new FongoRule();

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void list() {
        //given
        loadTo(fongoRule, "comment", "comment-list-comment1.json");

        Session session = new Session();
        UserInfo info = new UserInfo();
        info.setId("asd");
        session.setCurrentUser(info);
        DozerFactory factory = new DozerFactory();
        factory.init();

        controller = new CommentService();
        controller.db = fongoRule.getDB();
        controller.modelMapper = factory.mapperFactory();

        //when
        List<CommentData> list = controller.list(CommentType.EPISODE, "1", session);

        //then
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getAuthor(), Matchers.notNullValue());
        assertThat(list.get(0).getComment(), equalTo("mi ez a fos zene"));
    }

    @Test
    public void listWithoutUser() {
        //given
        loadTo(fongoRule, "comment", "comment-list-comment1.json");

        Session session = new Session();
        DozerFactory factory = new DozerFactory();
        factory.init();

        controller = new CommentService();
        controller.db = fongoRule.getDB();
        controller.modelMapper = factory.mapperFactory();

        //when
        List<CommentData> list = controller.list(CommentType.EPISODE, "1", session);

        //then
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getAuthor(), Matchers.notNullValue());
        assertThat(list.get(0).getComment(), equalTo("mi ez a fos zene"));
    }


}