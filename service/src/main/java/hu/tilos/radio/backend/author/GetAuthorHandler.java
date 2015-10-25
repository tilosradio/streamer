package hu.tilos.radio.backend.author;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.bus.Handler;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.dozer.DozerBeanMapper;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

public class GetAuthorHandler implements Handler<GetAuthorCommand> {


    @Inject
    private DB db;

    @Inject
    AvatarLocator avatarLocator;

    @Inject
    private DozerBeanMapper mapper;


    @Override
    public Try handle(GetAuthorCommand command) {
        Session session = null;
        String alias = command.getIdOrAlias();

        DBObject one = findAuthor(alias);

        if (one == null) {
            return new Failure<NotFoundException>(new NotFoundException("No such author"));
        }

        AuthorDetailed author = mapper.map(one, AuthorDetailed.class);
        avatarLocator.locateAvatar(author);
        if (session != null && session.getCurrentUser() != null && (session.getCurrentUser().getRole() == Role.ADMIN || session.getCurrentUser().getRole() == Role.AUTHOR)) {
            author.setEmail((String) one.get("email"));
        }
        return new Success(author);
    }


    private DBObject findAuthor(String alias) {
        BasicDBObject query = aliasOrId(alias);
        return db.getCollection("author").findOne(query);
    }

}
