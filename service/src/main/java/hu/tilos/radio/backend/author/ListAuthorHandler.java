package hu.tilos.radio.backend.author;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.bus.Handler;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.dozer.DozerBeanMapper;
import scala.util.Success;
import scala.util.Try;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ListAuthorHandler implements Handler<ListAuthorCommand> {


    @Inject
    private DB db;

    @Inject
    AvatarLocator avatarLocator;

    @Inject
    private DozerBeanMapper mapper;

    @Override
    public Try handle(ListAuthorCommand command) {


        DBCursor selectedAuthors = db.getCollection("author").find();

        List<AuthorListElement> mappedAuthors = new ArrayList<>();

        for (DBObject author : selectedAuthors) {
            mappedAuthors.add(mapper.map(author, AuthorListElement.class));
        }
        return new Success(mappedAuthors);

    }

}

