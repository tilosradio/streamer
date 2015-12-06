package net.anzix.jaxrs.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Summary {


    private String type;


    private String content;

    public Summary(String value) {
        this.content = value;
    }

    public Summary(String type, String value) {
        this.type = type;
        this.content = value;
    }

    public Summary() {
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlValue
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
