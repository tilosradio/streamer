package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import hu.radio.tilos.model.type.CommentStatus;
import hu.radio.tilos.model.type.CommentType;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.input.CommentToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.types.CommentData;
import org.hamcrest.Matchers;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class})
@ActivatedAlternatives(FongoCreator.class)
public class CommentControllerTest {

    @Inject
    CommentController controller;

    @Inject
    Session session;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void list() {
        //given
        loadTo(fongoRule,"comment","comment-list-comment1.json");

        //when
        List<CommentData> list = controller.list(CommentType.EPISODE, "1");

        //then
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getAuthor(), Matchers.notNullValue());
        assertThat(list.get(0).getComment(), equalTo("mi ez a fos zene"));
    }
//
//    @Test
//    @InRequestScope
//    public void listAll() {
//        //given
//
//
//        //when
//        List<CommentData> list = controller.listAll(CommentStatus.NEW.toString());
//
//        //then
//        assertThat(list.size(), equalTo(1));
//
//    }
//
//    @Test
//    @InRequestScope
//    public void approve() {
//        //given
//
//
//        //when
//        controller.approve(3);
//
//        //then
////        Comment comment = controller.getEntityManager().find(Comment.class, 3);
////        assertThat(comment.getStatus(), equalTo(CommentStatus.ACCEPTED));
//    }
//
//    @Test
//    @InRequestScope
//    public void create() {
//        //given
//        CommentToSave newComment = new CommentToSave();
//        newComment.setComment("Ahoj poplacsek");
//        //todo set user
//
//        //when
//        CreateResponse createResponse = controller.create(CommentType.EPISODE, 1, newComment);
//
//        //then
////        Comment comment = controller.getEntityManager().find(Comment.class, createResponse.getId());
//
////        assertThat(comment, notNullValue());
////        assertThat(comment.getComment(), equalTo("Ahoj poplacsek"));
//    }


}