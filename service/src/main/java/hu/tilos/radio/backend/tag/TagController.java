package hu.tilos.radio.backend.tag;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class TagController {

    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Inject
    TagService tagService;

    public TaggedElementList get(String tag) {
        return tagService.get(tag);
    }

    public TagCloud list(Integer limit) {
        return tagService.list(limit);
    }

}
