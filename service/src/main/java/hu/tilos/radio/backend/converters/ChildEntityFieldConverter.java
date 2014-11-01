package hu.tilos.radio.backend.converters;

import hu.radio.tilos.model.Show;
import hu.tilos.radio.backend.data.EntitySelector;
import org.dozer.CustomConverter;
import org.dozer.CustomFieldMapper;
import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ChildEntityFieldConverter implements CustomConverter {
    public static final String ID = "entityChild";
    private EntityManager entityManager;

    public ChildEntityFieldConverter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        if (sourceFieldValue != null) {
            if (sourceFieldValue instanceof EntitySelector) {
                EntitySelector s = (EntitySelector) sourceFieldValue;
                Integer id = s.getId();
                Object entity = (entityManager.find(destinationClass, id));
                return entity;
            } else {
                throw new RuntimeException("EntitySelector is required");
            }
        }
        return null;

    }
}
