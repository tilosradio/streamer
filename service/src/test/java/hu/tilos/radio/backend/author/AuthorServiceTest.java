package hu.tilos.radio.backend.author;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.types.AuthorListElement;
import hu.tilos.radio.backend.user.UserInfo;
import org.dozer.DozerBeanMapper;

import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;


public class AuthorServiceTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    AuthorController controller;

    @Inject
    FongoRule fongoRule;

    @Inject
    private Session session;

    @Inject
    private DozerBeanMapper mapper;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void list() throws Exception {
        //given
        String showId = loadTo(fongoRule, "show", "show-vendeglo.json");
        loadTo(fongoRule, "author", "author-author1.json");
        loadTo(fongoRule, "author", "author-author2.json");
        loadTo(fongoRule, "author", "author-author3.json", showId);

        //when
        List<AuthorListElement> authors = controller.list();

        //then
        assertThat(authors.size(), equalTo(3));
        assertThat(authors.get(0).getAlias(), equalTo("author1"));
        assertThat(authors.get(2).getContributions().size(), equalTo(1));
        assertThat(authors.get(2).getContributions().get(0).getShow().getId(), equalTo(showId));


    }

    @Test
    public void get() throws Exception {
        //given
        loadTo(fongoRule, "author", "author-author1.json");
        session.setCurrentUser(null);

        //when
        AuthorDetailed author = controller.get("author1");

        //then
        assertThat(author.getName(), equalTo("AUTHOR1"));
        //TODO
        //assertThat(author.getAvatar(), equalTo("https://tilos.hu/upload/avatar/author1.jpg"));
        assertThat(author.getUrls().size(), equalTo(1));
        assertThat(author.getUrls().get(0).getAddress(), equalTo("http://szabi.hu"));


    }

    @Test
    public void update() throws Exception {
        //given
        String authorId = loadTo(fongoRule, "author", "author-author1.json");
        loadTo(fongoRule, "user", "user-1.json", authorId);
        session.setCurrentUser(mapper.map(fongoRule.getDB().getCollection("user").findOne(), UserInfo.class));


        AuthorToSave save = new AuthorToSave();
        save.setName("asd");

        //when
        controller.update("author1", save);

        //then
        DBObject one = fongoRule.getDB().getCollection("author").findOne();
        assertThat((String) one.get("name"), equalTo("asd"));
    }

    @Test
    public void create() throws Exception {
        //given
        AuthorToSave save = new AuthorToSave();
        save.setName("asd");
        save.setAlias("aliasx");

        //when
        controller.create(save);

        //then
        DBObject one = fongoRule.getDB().getCollection("author").findOne();
        assertThat((String) one.get("alias"), equalTo("aliasx"));
    }
}