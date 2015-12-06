package hu.tilos.radio.backend.converter;

import hu.tilos.radio.backend.converters.HTMLSanitizer;
import hu.tilos.radio.backend.tag.TagUtil;
import org.dozer.ConfigurableCustomConverter;

import javax.inject.Inject;

public class ContentCleaner implements ConfigurableCustomConverter {

    @Inject
    HTMLSanitizer sanitizer;

    @Inject
    TagUtil tagUtil;

    @Override
    public void setParameter(String parameter) {

    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        return tagUtil.replaceToHtml(sanitizer.clean((String) sourceFieldValue));
    }
}
