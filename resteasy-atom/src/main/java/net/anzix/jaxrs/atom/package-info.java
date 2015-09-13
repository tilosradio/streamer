@XmlSchema(namespace = "http://www.w3.org/2005/Atom",
//        attributeFormDefault = XmlNsForm.QUALIFIED, 
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(namespaceURI = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes"),
                @XmlNs(namespaceURI = "http://www.w3.org/2005/Atom", prefix = "")


        }
)
@XmlJavaTypeAdapters(
        {
                @XmlJavaTypeAdapter(type = URI.class, value = UriAdapter.class),
                @XmlJavaTypeAdapter(type = net.anzix.jaxrs.atom.MediaType.class, value = MediaTypeAdapter.class)
        }) package net.anzix.jaxrs.atom;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.net.URI;