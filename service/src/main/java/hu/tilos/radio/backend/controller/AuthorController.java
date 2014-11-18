package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.*;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.input.AuthorToSave;
import hu.tilos.radio.backend.data.types.AuthorDetailed;
import hu.tilos.radio.backend.data.types.AuthorListElement;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/author")
public class AuthorController {

    private static Logger LOG = LoggerFactory.getLogger(AuthorController.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private ModelMapper modelMapper;

    @Inject
    Session session;


    @Produces("application/json")
    @Path("/")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public List<AuthorListElement> list() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Author> query = criteriaBuilder.createQuery(Author.class);
        query.select(query.from(Author.class));
        List<Author> selectedAuthors = entityManager.createQuery(query).getResultList();

        List<AuthorListElement> mappedAuthors = new ArrayList<>();
        for (Author author : selectedAuthors) {
            mappedAuthors.add(modelMapper.map(author, AuthorListElement.class));
        }
        return mappedAuthors;

    }

    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public AuthorDetailed get(@PathParam("alias") String alias) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Author> query = criteriaBuilder.createQuery(Author.class);
        Root<Author> fromAuthor = query.from(Author.class);
        CriteriaQuery<Author> select = query.select(fromAuthor);
        if (alias.matches("\\d+")) {
            select.where(criteriaBuilder.equal(fromAuthor.get("id"), Integer.parseInt(alias)));
        } else {
            select.where(criteriaBuilder.equal(fromAuthor.get("alias"), alias));
        }

        Author author = entityManager.createQuery(query).getSingleResult();
        return modelMapper.map(author, AuthorDetailed.class);

    }

    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.AUTHOR)
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("alias") String alias, AuthorToSave authorToSave) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Author> query = criteriaBuilder.createQuery(Author.class);
        Root<Author> fromAuthor = query.from(Author.class);
        CriteriaQuery<Author> select = query.select(fromAuthor);
        if (alias.matches("\\d+")) {
            select.where(criteriaBuilder.equal(fromAuthor.get("id"), Integer.parseInt(alias)));
        } else {
            select.where(criteriaBuilder.equal(fromAuthor.get("alias"), alias));
        }

        Author author = entityManager.createQuery(query).getSingleResult();
        checkPermission(author, session.getCurrentUser());
        modelMapper.map(authorToSave, author);
        entityManager.persist(author);
        return new UpdateResponse(true);

    }

    protected void checkPermission(Author author, User currentUser) {
        if (author.getUser().getId() == currentUser.getId()) {
            return;
        }
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        throw new IllegalArgumentException("No permission to modify");
    }


    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(@PathParam("alias") String alias, AuthorToSave authorToSave) {
        Author author = modelMapper.map(authorToSave, Author.class);
        entityManager.persist(author);
        entityManager.flush();
        return new CreateResponse(author.getId());

    }

}
