package hu.tilos.radio.backend;

import hu.radio.tilos.model.Comment;
import hu.radio.tilos.model.User;
import hu.radio.tilos.model.type.CommentStatus;
import hu.radio.tilos.model.type.CommentType;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.CommentData;
import hu.tilos.radio.backend.data.input.CommentToSave;
import hu.tilos.radio.backend.data.CreateResponse;
import org.hamcrest.Matchers;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class CommentControllerTest {

    @Inject
    CommentController controller;

    @Inject
    EntityManager entityManager;

    @Inject
    Session session;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    @InRequestScope
    public void list() {
        //given
        session.setCurrentUser(entityManager.find(User.class, 1));

        //when
        List<CommentData> list = controller.list(CommentType.EPISODE, 1);

        //then
        assertThat(list.size(), equalTo(2));
        assertThat(list.get(0).getAuthor(), Matchers.notNullValue());
        assertThat(list.get(0).getComment(), equalTo("mi ez a fos zene"));
    }

    @Test
    @InRequestScope
    public void listAll() {
        //given


        //when
        List<CommentData> list = controller.listAll(CommentStatus.NEW.toString());

        //then
        assertThat(list.size(), equalTo(1));

    }

    @Test
    @InRequestScope
    public void approve() {
        //given


        //when
        controller.getEntityManager().getTransaction().begin();
        controller.approve(3);
        controller.getEntityManager().getTransaction().commit();

        //then
        Comment comment = controller.getEntityManager().find(Comment.class, 3);
        assertThat(comment.getStatus(), equalTo(CommentStatus.ACCEPTED));
    }

    @Test
    @InRequestScope
    public void create() {
        //given
        CommentToSave newComment = new CommentToSave();
        newComment.setComment("Ahoj poplacsek");
        session.setCurrentUser(entityManager.find(User.class, 1));

        //when
        controller.getEntityManager().getTransaction().begin();
        CreateResponse createResponse = controller.create(CommentType.EPISODE, 1, newComment);
        controller.getEntityManager().getTransaction().commit();

        //then
        Comment comment = controller.getEntityManager().find(Comment.class, createResponse.getId());

        assertThat(comment, notNullValue());
        assertThat(comment.getComment(), equalTo("Ahoj poplacsek"));
    }


}