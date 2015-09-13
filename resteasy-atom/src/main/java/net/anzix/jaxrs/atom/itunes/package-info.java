@XmlSchema(namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd",
//        attributeFormDefault = XmlNsForm.QUALIFIED, 
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(namespaceURI = "http://www.w3.org/2005/Atom", prefix = ""),
                @XmlNs(namespaceURI = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes")
        }
) package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;