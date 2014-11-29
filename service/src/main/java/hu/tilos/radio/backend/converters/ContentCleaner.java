package hu.tilos.radio.backend.converters;

import org.dozer.ConfigurableCustomConverter;
import org.dozer.DozerConverter;

import javax.inject.Inject;

public class ContentCleaner  implements ConfigurableCustomConverter {

    @Inject
    StrictHTMLSanitizer sanitizer;

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
