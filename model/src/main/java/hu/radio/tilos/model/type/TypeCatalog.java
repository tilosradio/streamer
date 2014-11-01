package hu.radio.tilos.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Iterable catalog of all model enums.
 */
public class TypeCatalog {

    public Map<Class<? extends DescriptiveType>, DescriptiveType[]> getTypes() {
        Map<Class<? extends DescriptiveType>, DescriptiveType[]> result = new HashMap<Class<? extends DescriptiveType>, DescriptiveType[]>();
        result.put(MixType.class, MixType.values());
        return result;
    }
}
