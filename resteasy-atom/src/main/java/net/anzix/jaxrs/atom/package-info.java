@XmlSchema(namespace = "http://www.w3.org/2005/Atom",
//        attributeFormDefault = XmlNsForm.QUALIFIED, 
        elementFormDefault = XmlNsForm.QUALIFIED
)
@XmlJavaTypeAdapters(
        {
                @XmlJavaTypeAdapter(type = URI.class, value = UriAdapter.class),
                @XmlJavaTypeAdapter(type = net.anzix.jaxrs.atom.MediaType.class, value = MediaTypeAdapter.class)
        }) package net.anzix.jaxrs.atom;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.net.URI;