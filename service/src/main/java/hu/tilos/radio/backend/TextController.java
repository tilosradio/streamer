package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.TextContent;
import hu.radio.tilos.model.TextContent_;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.UpdateResponse;
import hu.tilos.radio.backend.data.input.TextToSave;
import hu.tilos.radio.backend.data.types.TextData;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/text")
public class TextController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ModelMapper mapper;

    @GET
    @Path("/{type}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public List<TextData> list(@PathParam("type") String type) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TextContent> query = criteriaBuilder.createQuery(TextContent.class);
        Root<TextContent> fromTextContent = query.from(TextContent.class);
        CriteriaQuery<TextContent> select = query.select(fromTextContent);
        select.where(criteriaBuilder.equal(fromTextContent.get(TextContent_.type), type));

        List<TextContent> contents = entityManager.createQuery(query).getResultList();
        List<TextData> result = new ArrayList<>();
        for (TextContent content : contents) {
            result.add(mapper.map(content, TextData.class));
        }
        return result;
    }

    @GET
    @Path("/{type}/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public TextData get(@PathParam("id") String alias, @PathParam("type") String type) {
        TextContent content = selectTextContent(alias);
        return mapper.map(content, TextData.class);
    }


    @Produces("application/json")
    @Path("/{type}/{id}")
    @Security(role = Role.ADMIN)
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("type") String type, @PathParam("id") String id, TextToSave textContentToSave) {
        TextContent content = selectTextContent(id);
        mapper.map(textContentToSave, content);
        entityManager.persist(content);
        entityManager.flush();
        return new UpdateResponse(true);
    }

    @Produces("application/json")
    @Path("/{type}")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(@PathParam("type") String type, TextToSave textContentToSave) {
        TextContent textContent = mapper.map(textContentToSave, TextContent.class);
        textContent.setType(type);
        textContent.setFormat("default");
        entityManager.persist(textContent);
        entityManager.flush();
        return new CreateResponse(textContent.getId());
    }

    public TextContent selectTextContent(String alias) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TextContent> query = criteriaBuilder.createQuery(TextContent.class);
        Root<TextContent> fromTextContent = query.from(TextContent.class);
        CriteriaQuery<TextContent> select = query.select(fromTextContent);
        if (alias.matches("\\d+")) {
            select.where(criteriaBuilder.equal(fromTextContent.get("id"), Integer.parseInt(alias)));
        } else {
            select.where(criteriaBuilder.equal(fromTextContent.get("alias"), alias));
        }
        return entityManager.createQuery(query).getSingleResult();

    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
