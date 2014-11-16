package hu.tilos.radio.backend;

import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.types.AuthorDetailed;
import hu.tilos.radio.backend.data.types.AuthorListElement;
import hu.tilos.radio.backend.data.types.AuthorSimple;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class AuthorControllerTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    @Inject
    AuthorController controller;


    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }


    @Test
    public void list() throws Exception {
        //given

        //when
        List<AuthorListElement> authors = controller.list();

        //then
        assertThat(authors.size(), equalTo(3));


    }

    @Test
    public void get() throws Exception {
        //given

        //when
        AuthorDetailed author = controller.get("author1");

        //then
        assertThat(author.getName(), equalTo("AUTHOR1"));
        assertThat(author.getAvatar(), equalTo("https://tilos.hu/upload/avatar/asd.jpg"));
        assertThat(author.getUrls().size(), equalTo(1));
        assertThat(author.getUrls().get(0).getAddress(), equalTo("http://szabi.hu"));


    }
}